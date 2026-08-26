## Why

目前 Dashboard 雖然已註冊報價能力與中文 label，但報價 capability 仍是 unavailable，沒有 `/quotes` 頁面、報價 CRUD API 或可供業務人員維護的操作流程。既有 customers/products 頁面已建立登入後、離線、本地化與 RWD 的 UI 模式，報價需要沿用這些模式並接上既有 `quotes`/`quote_lines` 資料模型。

## What Changes

- 新增登入後 `/quotes` MVC 頁面與報價清單 UI。
- 新增報價草稿建立、查詢/分頁與草稿編輯 API；以 active customer/product 建立明細快照並計算小計、稅額與總額。
- 新增報價生命週期操作：送出、接受、拒絕、取消，並依目前狀態拒絕非法轉換；過期報價在查詢時呈現正確狀態。
- 報價編輯限制於 `DRAFT`，取消取代 hard delete，不刪除已保存的財務文件。
- `/quotes` 表單支援客戶、商品明細、數量、折扣、幣別與有效期限，並顯示計算結果與狀態操作。
- 將 Dashboard capability registry 的 quotes route 設為 `/quotes` 並標記 available。
- 使用專案既有本地 Vue、Axios、Bootstrap、SweetAlert2、i18n 與 API error patterns，支援 loading、validation、network、empty、disabled 與 retry 狀態。
- 補充中英文訊息、後端/API/UI 測試與 Dashboard route 測試。

## Capabilities

### New Capabilities

- `quotes-ui`: 登入後報價清單、篩選、分頁、草稿 CRUD 與生命週期操作頁面。

### Modified Capabilities

- `financial-erp-core/sales-documents`: 實作報價草稿、明細快照、金額計算與受控狀態轉換所需的 API 契約。
- Dashboard capability navigation: quotes 由 unavailable capability 變為可用的 `/quotes` 連結。

## Impact

- 後端：新增 Quote/QuoteLine entity mapping、repository、service、DTO、API controller，以及 `/quotes` MVC page controller；遵守 `Controller -> Service -> Repository` 與 organization scope。
- 前端：新增 `templates/quotes/list.html`、quotes page JavaScript/API module、CSS 與本地化訊息；不引入 CDN 或外部 runtime 資源。
- 資料庫：重用既有 `quotes` 與 `quote_lines` migration，不新增破壞性 schema migration；若現有 mapping 缺少必要欄位，僅補相容的 migration 或 mapping。
- Dashboard：更新 capability registry，並以頁面/導航測試確認 route 與 available flag。
- 範圍外：本 change 不實作接受報價轉發票、發票 UI、付款分配、列印、報表或報價 hard delete。
- 授權：沿用既有登入與 ADMIN organization context，不在頁面或 API 重新實作認證。
