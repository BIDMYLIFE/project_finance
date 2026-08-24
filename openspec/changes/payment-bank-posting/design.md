## Context

目前 workspace 只有探索文件與身份驗證規劃，沒有財務交易程式碼。實作必須遵守 `Controller -> Service -> Repository` 分層，MVC Controller 與 Web API Controller 分離，並沿用 `auth-jwt-admin-bootstrap` 提供的 authenticated `ADMIN` principal 與 organization context。

本 change 的收款與銀行入帳是跨 aggregate 的流程：收款確認會涉及收據序列、發票分配與可選的銀行 `CREDIT`；待入帳處理會同時改變收款狀態與建立銀行交易。SQL Server 是目標資料庫，前端則必須使用本地 Vue 3 JavaScript、Bootstrap 5.3、Axios 與 SweetAlert2 資源。

## Goals / Non-Goals

**Goals:**

- 以可重試、可回滾的 Service transaction 完成收款確認與待入帳。
- 以 organization 與幣別條件保護每個查詢、異動、來源關聯與序列。
- 保留收款快照、收據重印紀錄、銀行交易來源與更正鏈，讓結果可稽核與重算。
- 提供一致的 DTO/API 錯誤契約與可在桌面、手機及列印預覽使用的離線頁面。

**Non-Goals:**

- 不連接外部銀行、第三方支付或自動對帳服務。
- 不引入完整複式簿記、費用領域、ERP 報表或 XLSX/PDF 報表。
- 不改寫身份驗證、JWT、refresh session 或發票生命週期；這些由前置 change 或其他 capability 負責。

## Decisions

### 1. 以 application service 協調收款與入帳

建立收款確認、待入帳、換帳戶、作廢與銀行更正等明確的 application service。API Controller 只負責 DTO 綁定、Bean Validation、呼叫 service 與組裝回應；Repository 只負責 organization-scoped 的資料存取。這比讓 Controller 直接更新收款與銀行 entity 更能保證跨資料異動的 transaction 邊界，也避免把業務規則散落在前端。

替代方案是讓 payment entity 直接建立 bank transaction，但會把銀行狀態機、序列鎖定與跨 aggregate rollback 綁在 entity lifecycle，難以測試，故不採用。

### 2. 收款事實與銀行現金流分開建模

Payment 保存收款快照、收據編號、分配與 `PENDING_DEPOSIT`/`POSTED`/`VOIDED` 狀態；BankTransaction 保存帳戶、`CREDIT`/`DEBIT`、日期、金額、來源與更正關聯。兩者以來源識別關聯，但不以單一可空欄位互相取代。

確認收款時先驗證 active 分類、金額、日期、幣別與帳戶。無帳戶時只建立 `PENDING_DEPOSIT`；有相容帳戶時在相同 transaction 建立唯一收據與來源 `CREDIT`。待入帳則鎖定待處理 payment 和目標 account，成功後同時建立 `CREDIT` 與轉換狀態。這保證任何中途錯誤都不會留下只有收款或只有銀行交易的半成品。

### 3. 收據與年度序列由資料庫保護

收據序列以 organization/年度作為範圍，在確認收款的 transaction 中取得下一個值，並以唯一條件保護 receipt number。序列值即使交易作廢也不回收；重印只建立 print record，讀取原始收款快照，不重新產生收據。

替代方案是使用應用程式時間戳或 UUID 作為收據號碼。UUID 可避免碰撞但不符合紙本年度序列閱讀需求；時間戳也無法在並行請求下提供連續且明確的唯一性，因此採資料庫序列記錄加唯一條件。

### 4. 銀行交易採 append-only 更正鏈

已確認的 BankTransaction 不提供一般 update/delete。換帳戶或更正會建立原交易的 reversal/void 關聯與新的有效交易，報表與餘額查詢只納入有效結果；每個結果保存原因、actor 與時間。這保持原始現金流事實，並讓日後追查能沿 source/reversal reference 還原操作歷史。

本 change 中的 `DEBIT` 是可追溯的銀行現金流調整，不建立 expense entity 或 expense workflow。日後若加入費用模組，費用 Service 仍須以相同 BankTransaction contract 建立來源關聯，不能繞過 append-only 規則。

### 5. organization scope 在 context、Repository 與資料庫三層防守

Organization id 由 authenticated principal/context 取得，不信任 request body 的 organization id。Service 對帳戶、分類、收款、發票與來源關聯做同 organization 驗證；Repository 查詢及更新固定帶 organization 條件；資料庫外鍵、複合唯一條件與索引再提供最後防線。找不到或不屬於目前 organization 的資源使用一致錯誤策略，不回傳其他 organization 的內容。

### 6. API 與前端維持明確的離線契約

Web API 以 `/api/v1/` 資源路徑提供收款、收據、待入帳、銀行帳戶與交易操作，輸入/輸出使用 DTO。成功回應包含 resource id、status、amount/currency、source references 與必要摘要；錯誤統一區分 validation、not-found、conflict、business-rule、unauthorized 與 forbidden。

MVC Controller 只回傳 Vue page view。Vue feature service 集中 Axios URL、timeout 與錯誤轉換，畫面統一呈現 loading、成功、空資料、驗證失敗、網路錯誤與 disabled 狀態。收據頁以 print stylesheet 固定 A4 portrait、三份 copy 與 page break，所有 Vue、Bootstrap、Axios、SweetAlert2、字型與圖示使用本地 vendor/build asset。

### 7. 以 source query 與整合測試固定一致性

餘額、未分配金額與待入帳清單由專用 query/projection 查詢來源資料，不在 Controller 逐筆重算。整合測試使用 SQL Server 相容測試資料庫，驗證 transaction rollback、並行收據序列、重複待入帳、反向交易、active/currency 規則與至少兩個 organization 的 isolation。瀏覽器測試覆蓋收款確認、待入帳、重印與 A4 單頁預覽的桌面/手機尺寸。

## Risks / Trade-offs

- [Risk] 序列 row lock 會降低同一 organization 高並行確認的吞吐 → [Mitigation] 只在短 transaction 內鎖定序列，使用 unique constraint 與有限次 retry，避免長時間持有 payment/account lock。
- [Risk] append-only reversal chain 讓餘額查詢與更正比直接 update 複雜 → [Mitigation] 建立有效狀態、source/reversal 索引與 projection query，並以交易鏈測試固定有效結果。
- [Risk] 發票分配與收款確認的跨 aggregate 鎖定可能造成競爭 → [Mitigation] 依固定順序鎖定 payment、invoice 與 allocation 查詢，發生 conflict 時整體回滾並回傳可重試錯誤。
- [Risk] 三聯收據的長備註可能造成瀏覽器跨頁 → [Mitigation] 在 DTO/模板定義長度限制或換行規則，並把 print preview 的單頁驗證列為 release gate。
- [Risk] 前置身份驗證或發票契約尚未實作會阻塞整合 → [Mitigation] 將 `auth-jwt-admin-bootstrap` 與 sales document contract 列為部署前置條件，拒絕在本 change 以繞過方式替代。

## Migration Plan

1. 先確認身份驗證 change 已提供 `ADMIN` principal、organization context、cookie/API error contract；確認發票資料契約可供 allocation 使用。
2. 建立 organizations 關聯下的 payment categories、payments、payment allocations、receipt sequences、receipt print records、bank accounts 與 bank transactions migrations，加入 UUID、外鍵、唯一條件、狀態條件與 organization 索引。
3. 先部署共享 context、DTO validation、error handler、money/currency policy 與序列 service，再依序部署 payment confirmation、allocation、receipt、bank posting、reversal 與 API。
4. 部署離線 vendor/build asset、收款/銀行頁面與 A4 print stylesheet，確認無外部 runtime URL。
5. 以乾淨 SQL Server 執行 migration，使用兩個 organization 驗證 API、交易 rollback、序列並行、待入帳重複提交、換帳戶與列印流程。
6. 回滾時停止新的異動入口並保留已產生的 append-only 歷史；應用程式版本可回退，但不得以 destructive migration 刪除 payment、receipt 或 bank transaction。migration 失敗須由資料庫交易回滾。

## Open Questions

- 收據年度採收款日期、組織時區與會計年度哪一種規則，需在 implementation 前由業務確認；不改變唯一性與不可回收原則。
- 實際收據的品牌欄位、語系、紙張邊界與三聯文案需由使用者提供；可在模板設定完成，不改變 payment 或 bank transaction contract。