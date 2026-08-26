## Why

登入後目前缺少可供業務人員管理客戶資料的 `/customers` 操作頁面，既有客戶 CRUD API 因此無法被一般使用者直接使用。此變更補上符合 ERP 工作流程的客戶管理 UI，讓清單查詢與基本維護能在既有驗證後流程中完成。

## What Changes

- 新增登入後 `/customers` 頁面與 Thymeleaf page controller。
- 以 Vue 3、Axios、Bootstrap 5.3 與 SweetAlert2 的本地資源建立客戶管理介面，保持離線可執行。
- 重用既有 `MasterDataApiController` 提供的 `/api/v1/customers` CRUD API，支援客戶清單、名稱關鍵字搜尋、啟用/停用篩選與伺服器分頁。
- 支援新增、編輯、軟停用確認、表單驗證，以及載入、錯誤與空資料狀態。
- 支援中英文 i18n、RWD 與鍵盤可用的互動狀態。
- 不擴大 favicon 500 的處理範圍；僅在風險或後續工作中記錄，不納入本次主要功能。

## Capabilities

### New Capabilities

- `customers-ui`: 登入後客戶清單、篩選、分頁、新增、編輯與軟停用管理頁面。

### Modified Capabilities

無。既有 `/api/v1/customers` CRUD API 作為相容的整合介面重用，不變更其規格。

## Impact

- 後端：新增 customers 頁面的 MVC controller；不修改既有 API、Service 或 Repository 契約。
- 前端：新增 `templates/customers/` 頁面及對應的 Vue/Axios/CSS 模組，使用專案內既有 vendor 資源。
- 國際化：新增客戶管理頁面所需的中英文訊息鍵值。
- 驗證與授權：頁面沿用登入後路由與既有 API 驗證機制。
- 非功能風險：favicon 500 可能仍存在，但不影響客戶管理主要流程，列為後續處理項目。
