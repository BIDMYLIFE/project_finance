## Context

目前 workspace 只有財務探索與各領域的 OpenSpec 規劃，沒有可直接使用的報表程式碼。報表必須讀取收款、銀行、發票、費用與稅務來源，並承接 `auth-jwt-admin-bootstrap` 的 authenticated `ADMIN` principal、organization context，以及財務來源模組提供的狀態與金額契約。

本 change 是讀取與匯出 capability，不應改寫來源交易。實作需遵守 `Controller -> Service -> Repository`，Web API Controller 與 MVC page Controller 分離，目標資料庫為 SQL Server；前端需在無外部網路時以本地 Vue 3 JavaScript、Bootstrap 5.3、Axios 與 SweetAlert2 資源運作。

## Goals / Non-Goals

**Goals:**

- 建立一致的報表 query contract，涵蓋 filters、date basis、sorting、pagination、rows、summary 與 empty state。
- 讓每種報表從來源資料即時計算有效結果，並保留明細到來源文件/交易的追溯關係。
- 讓畫面查詢與 CSV 匯出共用相同的篩選、狀態排除、金額與日期基準規則。
- 在 query、API、匯出與前端呈現各層套用 organization isolation、ADMIN authorization、查詢上限與錯誤處理。

**Non-Goals:**

- 不保存獨立的報表總計作為財務事實，不建立或修改來源交易。
- 不實作外部銀行同步、自動對帳、完整總帳、XLSX/PDF 匯出或費用交易生命週期。
- 不以報表模組取代來源領域的 payment、bank、invoice 或 expense business rules。

## Decisions

### 1. 使用專用 report query service 與 projection

每類報表提供以 projection/row DTO 為輸出的 query service/repository：待入帳、收款分類、銀行餘額、發票狀態、應收帳款帳齡、費用、稅務與 ERP 收支摘要各自保有來源欄位與 summary 計算。查詢直接在資料庫投影必要欄位與聚合，避免 Controller 逐筆載入 entity 或讓大量資料進入記憶體。

替代方案是將所有來源 entity 載入後由共用 Java 程式計算。這較容易開始，但在資料量增加時會造成 N+1 查詢、不同報表口徑分歧與未授權資料短暫進入應用層，因此不採用。

### 2. 共同 filter contract 搭配每個報表的 date basis

共用 request model 定義 organization context、日期起訖、customer/category/account/currency/status、sort、page 與 size；organization id 不由 request body 決定。每個 report type 宣告自己的 date basis：收款使用 `received_at`、銀行使用 `transaction_date`、發票使用 `issue_date`/狀態日期、帳齡使用 `due_date`、費用使用 `expense_date`、稅務使用文件日期。回應會回傳正規化後的 applied filters 與 date basis，讓 UI 與 CSV 可被驗證。

日期區間、page size 與匯出筆數都設上限；無日期時使用明確的短預設期間或要求輸入。這比接受任意 SQL-like filter 或無限制 offset 更能控制查詢成本與 API 可預期性。

### 3. 有效資料與 reversal chain 由集中規則判定

建立共用 effective-state predicate，排除 cancelled、voided 與 reversed 的來源；銀行交易依 reversal chain 只計入有效結果，發票帳齡使用有效未結清餘額，收支摘要只納入已入帳收款 Credit 與有效費用 Debit。歷史狀態查詢可展示原始/反向關聯，但 summary 分開標示為歷史資料，不混入有效總計。

這些判定集中在 query specification，而不是分散於各 Controller 或前端。若未來來源模組新增狀態，先更新來源契約與 effective predicate，再同步報表測試。

### 4. 畫面與 CSV 共用查詢規格，匯出不另算一套口徑

報表 Web API 與 CSV service 都接收同一組已驗證的 query specification。畫面使用分頁 rows；CSV 使用相同 filters、sort、date basis 與有效狀態，輸出該查詢的受上限完整資料集與 summary，並在 metadata/header 保存查詢條件、產生時間與幣別。page/size 只控制畫面分頁，不讓匯出因目前頁面而遺漏其他符合條件的 rows。

替代方案是前端把目前表格轉成 CSV，會遺失未載入頁面、summary 與 date basis，也可能造成畫面與下載不一致，因此由後端重用 query specification 產生 CSV。

### 5. 來源追溯使用 typed source reference

每個 row 回傳 source type、source id、顯示編號與 status；API 的 source detail route 依 source type 導向對應 read service，再次驗證 authenticated organization scope。report query 不回傳不必要的敏感欄位，CSV 也不得包含其他 organization 的識別資訊或 credentials。

這比只返回可讀文字或直接暴露 entity 更能支援前端導覽與稽核，同時保留來源 API 的授權邊界。

### 6. 以專用 Web API 與離線報表頁隔離責任

Web API Controller 負責 binding、Bean Validation、authorization 與 DTO response；MVC Controller 只回傳報表頁面。Vue feature service 集中 API URL、timeout、下載回應與錯誤轉換；頁面以 state/event 呈現 loading、empty、validation、network、disabled、retry 與成功狀態。Bootstrap responsive table/filter layout 與可見 focus state 確保手機及鍵盤可用，所有 runtime 資源來自本地 vendor/build asset。

### 7. 先以索引與 bounded query 支撐效能，再評估 materialized summary

對 organization、報表 date basis、status、customer、category、account、currency 與 source/reversal reference 建立合適索引；聚合使用 projection query，並限制日期範圍、page size 與 CSV 筆數。第一版不建立報表快照或 materialized table，避免來源交易更新後出現同步問題；若實際查詢量證明需要，再以不改變 API contract 的快取/彙總讀模型改善。

## Risks / Trade-offs

- [Risk] 多來源 join 與帳齡聚合在長期間查詢可能變慢 → [Mitigation] 強制日期/筆數上限、使用 projection、索引與查詢計畫檢查，避免逐筆 entity 載入。
- [Risk] source modules 的狀態或金額契約尚未完成會阻塞報表 → [Mitigation] 將 authentication、sales document、payment-bank-posting 與 expense contracts 列為部署前置條件，以 fixture contract tests 先固定欄位與有效狀態。
- [Risk] CSV 大量輸出可能占用記憶體或逾時 → [Mitigation] 設定 bounded export limit，使用串流/分批寫出；超限或失敗時回傳錯誤且不提供部分成功檔案。
- [Risk] 不同報表的日期基準容易被誤用 → [Mitigation] query response 與 CSV metadata 明確回傳 date basis，並為每種 report type 建立日期邊界與跨日測試。
- [Risk] organization filter 遺漏可能造成資料外洩 → [Mitigation] context、Repository query、source detail 與 API integration tests 多層驗證，並以兩個 organization 的資料做負向測試。

## Migration Plan

1. 先確認 `auth-jwt-admin-bootstrap`、財務主檔/銷售文件、`payment-bank-posting` 與費用資料契約已提供報表所需的 organization、狀態、金額、日期與 source reference。
2. 建立共用 report DTO、filter validation、date-basis registry、effective-state predicate、pagination limit 與 organization scope，再建立各報表的 read projection/query。
3. 依序實作待入帳/收款分類、銀行/發票/帳齡、費用/稅務/ERP 收支摘要，加入 source detail、summary 重算與 empty-state API。
4. 以相同 query specification 實作 CSV metadata、明細與 summary 輸出，加入逾時、超限、查詢失敗時不產生部分檔案的處理。
5. 建立本地前端資源與 Vue 報表頁，完成桌面/手機、鍵盤 focus、loading/error/empty/retry 與 CSV download 流程。
6. 以兩個 organization 與假資料執行 migration/整合/API/瀏覽器測試，確認有效狀態、帳齡邊界、summary 可由 rows 重算、CSV 與畫面條件一致。
7. 部署時先執行來源模組 migration，再部署報表 API/UI；若回滾，停止報表入口即可，不需刪除來源資料或報表交易，因本 change 不寫入財務來源。

## Open Questions

- 費用與稅務來源資料的最終欄位命名需在其來源 capability 契約完成時對照；不改變本設計的 report type、date basis、organization scope 或匯出契約。