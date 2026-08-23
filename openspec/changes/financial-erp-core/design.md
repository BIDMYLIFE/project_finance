## Context

目前 workspace 沒有財務應用程式實作，只有財務系統探索報告與獨立的 `auth-jwt-admin-bootstrap` 身份驗證 change。這個 change 建立在該身份驗證 change 之上，假設請求已能取得 authenticated principal、`ADMIN` authority 與 organization context；本 change 不重複建立登入、JWT 或 refresh session。

系統採 Spring Boot/Spring MVC，後端必須遵守 `Controller -> Service -> Repository`，MVC Controller 與 Web API Controller 分離，API 使用 DTO 與集中錯誤處理。前端採 Vue 3 JavaScript、Bootstrap 5.3、Axios 與 SweetAlert2，所有 runtime 資源必須離線可用。資料庫目標為 Microsoft SQL Server，所有財務資料以 `organization_id` 作為租戶隔離條件。

## Goals / Non-Goals

**Goals:**

- 建立客戶、產品/服務、報價單、發票、收款、收據、銀行帳務、費用與 ERP 報表的可追溯核心流程。
- 讓金額、稅額、幣別精度、發票應收餘額與銀行餘額由明確規則計算，避免 Controller 或前端自行重算。
- 以 Service transaction 管理收款確認、發票分配、待入帳、銀行入帳、帳戶異動與不可破壞更正。
- 將 organization isolation、`ADMIN` authorization 與 authenticated actor 套用到每一個查詢、異動與匯出流程。
- 提供能在桌面與手機使用的離線管理介面、固定 A4 三聯收據列印與 CSV 報表匯出。
- 以 migration、整合測試、稽核紀錄與來源追溯支撐後續 ERP 功能擴充。

**Non-Goals:**

- 不實作外部銀行、第三方支付、自動銀行對帳或即時金融機構同步。
- 不實作完整複式簿記、總帳、會計科目、期末結算或跨幣別匯兌。
- 不在本 change 增加 `OPERATOR`、`VIEWER` 等角色、多組織切換或跨組織管理。
- 不重新實作 MFA、社群登入、密碼重設或身份驗證流程；這些由身份驗證 change 或後續 capability 處理。
- 不把 XLSX/PDF 報表匯出列為 MVP；收據 PDF/列印模板與 CSV 是本 change 的輸出範圍。

## Decisions

### 1. 依責任分層並以 domain service 管理跨模組流程

每個 capability 以 entity、Repository、Service、DTO 與 API Controller 組成清楚邊界。Controller 只負責路由、DTO 綁定、Bean Validation、呼叫 Service 與回應組裝；Repository 只處理資料存取與查詢；Service 負責狀態轉換、跨 entity 規則、交易與稽核。

跨模組流程使用 application service 協調，而非讓 entity 或 Controller 直接互相寫入。例如 `ConfirmPaymentService` 負責收款確認、收據序列、發票分配與可選銀行入帳；`PostPendingDepositService` 負責待入帳到銀行 Credit；`MovePaymentBankAccountService` 負責原交易沖銷與新交易建立。每個流程的交易邊界由 service 定義。

### 2. 統一 organization scope 與 API 契約

從已驗證 principal 導出目前 organization id，建立共用 `OrganizationContext` 或等效 service。所有 Repository 查詢與更新都必須帶 organization 條件；service 不接受 request body 中的 organization id 作為授權依據。Controller 不把 entity 直接回傳，清單 API 使用一致的 page、size、sort、total、items 結構與有限制的預設 page size。

錯誤由集中 exception handler 轉成一致 JSON，至少區分 validation、not found、conflict、business rule、unauthorized 與 forbidden。錯誤回應不包含 SQL、stack trace、其他 organization 資料或內部識別資訊；跨 organization 的不存在與禁止策略依 endpoint 的洩漏風險採一致處理。

### 3. 以 SQL Server migration 與 decimal 金額模型保存財務資料

使用版本化 migration 建立 organizations、customers、products、quotes/quote_lines、invoices/invoice_lines、payment_categories、payments、payment_allocations、receipt_prints、bank_accounts、bank_transactions、expenses、audit_logs 與文件/收據序列所需的表。主鍵採應用程式可產生且不暴露順序的 UUID/uniqueidentifier；外鍵、organization 複合索引與 active/status 條件索引由 schema 固定。

金額使用固定精度的 `DECIMAL`，不使用浮點或 SQL Server money 型別作為業務計算基礎。數量、單價、折扣、稅率、稅額、總額與餘額的 precision/scale 由 organization currency policy 決定；計算服務在明細或文件層級依已決定的 rounding 規則產生結果，保存結果快照供日後重算與稽核。

### 4. 主檔採停用而非刪除

Customer、Product、PaymentCategory 與 BankAccount 的一般管理操作採 active/inactive 狀態。停用資料不得被新的報價、發票、收款或銀行交易選用，但既有文件與交易仍可查詢。需要更正的財務資料不提供一般 hard delete；所有異動記錄 actor、時間、來源 entity、action 與必要的 before/after 摘要，且 audit payload 不保存敏感 credentials。

Autocomplete 建議表以 organization、field type、active 與 normalized keyword 建索引，可先使用 Spring Cache + Caffeine。建議值只是選項，不是收款的資料來源；收款保存使用者最後確認的 reason/note，建議新增或停用時清除對應 cache。

### 5. 文件流程使用明確狀態機與不可重用序列

Quote 與 Invoice 的狀態轉換集中在狀態服務，非法轉換以 business rule error 拒絕。接受的 Quote 只能建立保留來源關聯的 Invoice draft；Issue Invoice 時在 organization/年度序列上取得鎖並以 unique constraint 保護唯一 invoice number。序列產生與文件狀態更新位於同一 transaction，衝突時整體回滾。

Invoice line 保存當時的產品名稱、描述、數量、單價、折扣與稅率快照，不依賴日後產品主檔變更重算已發出文件。服務統一計算 line subtotal、document subtotal、tax total、grand total、paid total 與 balance due；付款分配以獨立 `payment_allocations` 保存，並在同一 transaction 驗證不得超過付款或發票未結餘額。

### 6. 收款、收據與銀行入帳分離

Payment confirmation 是收款事實，BankTransaction 是銀行現金流事實，兩者不可用單一 nullable foreign key 取代。確認收款時鎖定 organization/年度 receipt sequence，產生一次性的 receipt number；沒有 bank account 時建立 `PENDING_DEPOSIT`，有相容 active account 時建立 `CREDIT` 並成為 `POSTED`。Payment、receipt number、allocation 與初次 bank posting 的一致性由同一 application service transaction 維護。

每筆 confirmed payment 強制一個 active category 與非空 reason，note 可為空。作廢只改狀態、建立 audit 與必要銀行反向處理，不刪除 payment、不回收 receipt number。Receipt print service 從保存的 payment snapshot 組出固定 A4 portrait 三聯模板，三聯共用資料，只改聯別標籤；重印只新增 print record。

### 7. 銀行帳務採 append-only 現金流模型

BankTransaction 的 direction 僅允許 `CREDIT` 或 `DEBIT`，有效餘額依 `opening_balance + credits - debits` 計算。來源收款建立 Credit，費用建立 Debit，轉帳原子建立來源 Debit 與目的 Credit，並用 transfer reference 互相追蹤。每筆交易保存 transaction date、currency、amount、source type/id、status、reversal reference 與 actor。

已入帳收款換帳戶或更正時，不直接改寫原 transaction。服務建立原交易的 reversal/void chain，再以新帳戶建立有效 Credit；報表只計入有效結果。資料庫與 service 同時限制來源 organization、帳戶 active 狀態與幣別相容性，避免跨 organization 或跨幣別誤入帳。

### 8. 報表以 source query service 重算並保留追溯

報表讀取由專用 query service/repository 負責，不透過逐筆載入 entity 重新組合大型清單。每種報表定義自己的日期基準：待入帳/收款使用 `received_at`、銀行使用 `transaction_date`、發票狀態可用 issue/due date、費用使用 `expense_date`。所有 query 先套用 organization scope，再套用狀態、日期、客戶、分類、帳戶、幣別條件。

報表結果同時回傳 rows、summary、pagination 與 applied filters。有效總計排除 voided、cancelled、reversed 的重複結果，但可透過狀態篩選追溯。CSV exporter 重用相同 query specification 與 summary，將篩選條件、日期基準、幣別、產生時間寫入 metadata/header，避免畫面與匯出使用不同計算邏輯。

### 9. 前端維持離線、RWD 與列印邊界

Vue page 將 API 呼叫集中至 feature service，元件以 state/event 驅動畫面，不直接操作資料庫或以 DOM hack 完成業務流程。所有列表與表單統一呈現 loading、成功、validation error、network error、空資料與 disabled 狀態；Bootstrap 5.3、Axios、SweetAlert2、字型、圖示與圖片由本地 vendor 或建置產物提供。

收款與文件表單使用 keyboard-accessible autocomplete、明確 focus state 與 responsive layout。收據列印使用專用 print stylesheet/模板，固定 A4 portrait、三聯高度與 page-break 規則，長備註應依既定模板規則換行或限制長度，並以瀏覽器列印預覽測試不產生第二頁。

### 10. 以整合測試固定交易與資料隔離

除單元測試外，使用 SQL Server 相容的整合測試資料庫驗證 migration、unique constraint、transaction rollback、row locking 與 report query。測試建立至少兩個 organization，所有 endpoint 以 authenticated ADMIN principal 呼叫，驗證 path/query/body/token 任何一種 organization id 都不能繞過 scope。

並行測試專注於 invoice/receipt sequence、同一付款重複確認、同一付款雙重入帳、refresh 後續整合與 bootstrap change 的 auth boundary。測試 fixture 一律使用假資料，不使用任何實際 database password、JWT secret 或 production customer data。

## Risks / Trade-offs

- 使用 append-only bank transaction 與 reversal chain 可保留完整歷史，但查詢與更正流程比直接 update 複雜；以有效狀態與 source/reversal index 降低報表成本。
- 文件與收據 sequence 需要資料庫鎖，並行建立會降低尖峰吞吐；唯一 constraint 與 retry policy 可把衝突控制在序列操作，不讓整個 API 長時間持鎖。
- 將發出文件保存明細快照能保證歷史一致，但產品主檔更新不會回溯已發文件；這是財務可追溯性優先的取捨。
- 報表即時計算能避免日結資料漂移，但大量期間查詢可能昂貴；先以索引、bounded date range、分頁與 query projection 控制，後續再評估 materialized summary。
- 固定一頁三聯收據能符合紙本流程，但長文字需設計明確截斷/換行規則；列印驗證列為 release gate，不能只依螢幕畫面判定。
- 本 change 與身份驗證 change 分開可降低安全與財務領域耦合，但 apply 順序必須在部署文件與整合測試中明確標記為 authentication first。

## Migration Plan

1. 確認 `auth-jwt-admin-bootstrap` 已提供可用的 `ADMIN` principal、organization context 與一致 API error contract；若未完成，先完成其 apply，不在本 change 臨時繞過安全邊界。
2. 建立 SQL Server migration 基礎表、organization foreign keys、UUID primary keys、status/active 欄位、currency policy、audit log 與 document/receipt sequences。
3. 建立 customer/product/payment-category/bank-account master data 與 organization-scoped repositories，再加入 quote、invoice、line、payment、allocation、expense 與 bank transaction domain。
4. 實作共用 organization scope、DTO/validation、pagination、error handler、money/tax calculation、state transition 與序列服務。
5. 依交易依賴順序實作 quote/invoice、payment/receipt、pending deposit/banking、expense/transfer/reversal 與 report query services。
6. 建立 API Controller 與分離的 MVC/Vue pages，加入 autocomplete、收據 A4 三聯列印、CSV exporter、本地 vendor 資源與 RWD/keyboard states。
7. 以兩個以上 organization 執行 migration、單元、整合、併發、security boundary、report totals、CSV、列印與瀏覽器桌面/手機驗證。
8. 空資料庫以 migration 建立後，先由 authentication change 完成 organization/ADMIN bootstrap，再由 ADMIN 建立主檔與第一筆測試文件；任何 migration 或 service failure 都不得留下部分財務資料。

本 change 沒有既有財務資料需要轉換，因此不提供 legacy backfill。正式部署前必須以備份與 migration dry run 驗證 SQL Server schema；不把實際連線字串、密碼或 secret 放入 repository。

## Open Questions

- 發票是否要支援特定地區的統一發票/稅籍欄位與列印格式，需在正式稅務上線前由業務確認；MVP 先以 organization tax settings 與一般 invoice number 為準。
- 收據與發票的品牌 logo、紙張邊界、語系與欄位文案需由使用者提供，架構保留模板設定，不改變交易資料模型。
- 後續若需要 XLSX/PDF 報表、外部銀行匯入或完整複式簿記，應新增 capability，不在本 change 以隱藏欄位預留未定義行為。