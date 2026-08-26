## Why

登入後目前缺少可供業務人員管理產品與服務資料的 `/products` 操作頁面，既有產品 CRUD API 因此無法被一般使用者直接使用。此變更補上沿用既有 ERP 規則的產品/服務管理 UI，讓清單查詢與基本維護能在既有驗證後流程中完成。

## What Changes

- 新增登入後 `/products` 頁面與專用 Thymeleaf MVC page controller，與既有 API controller 分離。
- 以 Vue 3、Axios、Bootstrap 5.3 與 SweetAlert2 的本地 webjar/static 資源建立離線產品/服務管理介面，不使用 CDN。
- 重用既有 `GET`、`POST`、`PUT`、`DELETE` `/api/v1/products`（含 `{id}`）API，支援名稱搜尋、active/inactive 篩選、伺服器分頁、上一頁/下一頁/頁碼導覽。
- 支援產品/服務清單、新增、編輯與軟停用確認，並處理載入、錯誤、重試、空狀態及防重複請求。
- 表單支援 productCode、name、description、unitPrice、currencyCode、taxRate 的前端驗證；編輯時 productCode 唯讀，API 驗證錯誤映射為安全且本地化的欄位/表單訊息。
- 加入中英文 i18n、RWD 與鍵盤可用性，並補充 MVC/API contract、前端資源與必要 workflow tests。
- Product 與 Service 共用同一個 Product 模型，不新增 type 欄位或產品/服務分類切換。
- 不新增 database migration 或 API contract；沿用既有 organization scope 與 ADMIN 權限後端規則。
- favicon 500 不納入本 change，僅記錄為非阻塞 follow-up risk。

## Capabilities

### New Capabilities

- `products-ui`: 登入後產品/服務清單、搜尋、狀態篩選、伺服器分頁、新增、編輯與軟停用管理頁面。

### Modified Capabilities

無。既有 `/api/v1/products` CRUD API 作為相容的整合介面重用，不變更其 API contract 或後端模型規則。

## Impact

- 後端：新增 `/products` 的 MVC page controller；不修改既有 Product entity、request/response DTO、Repository、Service 或 `MasterDataApiController` API contract。
- 前端：新增 `templates/products/` 頁面及對應 Vue/Axios/CSS 模組，使用專案內既有 vendor 資源。
- 國際化：新增產品/服務管理頁面所需的中英文訊息鍵值。
- 驗證與授權：沿用登入後路由、organization scope 與 ADMIN 權限，以及既有 API 驗證機制。
- 測試：新增頁面路由/API contract、資源離線載入、清單查詢、分頁、CRUD、錯誤映射、授權與 locale workflow tests。
- 非功能風險：favicon 500 可能仍存在，但不影響產品/服務管理主要流程，列為後續處理項目。