## Purpose

提供登入後的產品與服務共用管理工作區，讓具備既有存取權限的使用者能以可搜尋、可分頁且適合桌面與行動裝置的介面查看、建立、編輯與軟停用 Product 資料。

## ADDED Requirements

### Requirement: Product and service records use the existing Product model

系統 SHALL 將產品與服務視為同一種 Product 資料，不新增 `type` 欄位、不提供產品/服務分類切換，並沿用既有 organization scope 與 ADMIN 權限規則。

#### Scenario: Render shared product and service records

- **WHEN** 使用者查詢產品清單
- **THEN** 系統以既有 Product response 欄位呈現產品或服務資料，且不要求或產生額外的分類欄位

#### Scenario: Preserve organization and authorization scope

- **WHEN** 使用者透過產品頁面執行查詢或維護操作
- **THEN** 系統沿用既有後端 organization scope 與 ADMIN 權限判斷，不在前端繞過或重新定義權限

### Requirement: Product list can be queried with server pagination

系統 SHALL 在登入後的 `/products` 頁面，以名稱搜尋、active/inactive 篩選與伺服器頁碼查詢產品/服務清單，並重用既有 `/api/v1/products` API。

#### Scenario: Load the default product list

- **WHEN** 使用者進入 `/products`
- **THEN** 系統以既有 API 的預設查詢條件載入第一頁，並顯示產品代碼、名稱、單價、幣別、稅率、active 狀態與分頁控制項

#### Scenario: Search products by name

- **WHEN** 使用者輸入名稱搜尋文字並送出查詢
- **THEN** 系統向既有 products API 要求符合名稱的結果，並從第一頁開始呈現

#### Scenario: Filter by active status

- **WHEN** 使用者切換 active 或 inactive 篩選
- **THEN** 系統向伺服器重新查詢所選狀態，重設到第一頁並呈現與結果一致的分頁資訊

#### Scenario: Navigate server pages

- **WHEN** 使用者選擇上一頁、下一頁或指定頁碼
- **THEN** 系統向伺服器要求所選頁面並更新清單，不以目前頁面資料推算未載入的結果

#### Scenario: Show no matching records

- **WHEN** 查詢成功但沒有任何符合條件的產品或服務
- **THEN** 系統顯示明確的空資料狀態，且不顯示誤導性的資料列

### Requirement: Product records can be created and edited

系統 SHALL 使用既有 `/api/v1/products` POST/PUT API 提供新增與編輯表單，並在送出前驗證 `productCode` 必填且最多 80 字、`name` 必填且最多 200 字、`description` 選填且最多 1000 字、`unitPrice` 必填且大於或等於 0、`currencyCode` 必填且為 TWD/USD/EUR/JPY、`taxRate` 必填且介於 0 到 100；`active` 狀態應依既有模型規則處理。

#### Scenario: Create a valid product or service

- **WHEN** 使用者填入通過驗證的代碼、名稱、描述、單價、幣別與稅率並送出新增表單
- **THEN** 系統呼叫既有 products POST API、顯示本地化成功結果、關閉或重設表單，並重新整理目前查詢條件下的清單

#### Scenario: Edit a valid product or service

- **WHEN** 使用者編輯既有資料並送出通過驗證的表單
- **THEN** 系統以既有 products PUT API 更新資料、保持 productCode 唯讀、顯示本地化成功結果，並重新整理目前查詢條件下的清單

#### Scenario: Reject invalid product form input

- **WHEN** 使用者送出缺少必要資料、超過長度、數值超界或不支援幣別的表單
- **THEN** 系統在對應欄位顯示驗證訊息、保留輸入內容，且不送出建立或更新請求

### Requirement: Product records can be soft-deactivated

系統 SHALL 重用既有 products DELETE `{id}` API 提供軟停用操作，執行前要求確認，且不得將資料實體刪除。

#### Scenario: Confirm product deactivation

- **WHEN** 使用者選擇 active 產品或服務的停用操作並確認本地化提示
- **THEN** 系統呼叫既有 DELETE `{id}` API、顯示成功結果，並重新整理清單使該筆資料依 active 篩選規則呈現

#### Scenario: Cancel product deactivation

- **WHEN** 使用者選擇停用操作但取消確認
- **THEN** 系統不發出 DELETE 請求，資料與清單保持不變

### Requirement: Product page separates MVC and API responsibilities

系統 SHALL 提供專用 Thymeleaf MVC page controller 處理 `/products` 頁面，並保持其與既有 `MasterDataApiController` 的 `/api/v1/products` JSON API controller 分離；既有 API 路徑、HTTP 方法、request/response contract 與 Product 模型不得因本 change 修改。

#### Scenario: Open the product page

- **WHEN** 已登入使用者存取 `/products`
- **THEN** 專用 MVC controller 回傳 products Thymeleaf view，頁面再由瀏覽器呼叫既有 products API 取得資料

#### Scenario: Preserve existing products API contract

- **WHEN** 既有 API client 呼叫 GET/POST/PUT/DELETE `/api/v1/products`（含 `{id}`）
- **THEN** API contract 維持相容，本 change 不新增 migration、不新增第二套 API，也不要求既有 client 改用新路徑

### Requirement: Product UI handles request states and safe API errors

系統 SHALL 顯示載入、失敗、重試與空資料狀態，防止清單或表單重複請求，並將 API 驗證錯誤映射到安全且本地化的欄位/表單訊息；不得向使用者暴露 stack trace、內部錯誤內容或 token。

#### Scenario: Prevent duplicate requests

- **WHEN** 清單、建立、更新或停用請求尚未完成
- **THEN** 系統顯示載入狀態並停用會造成重複請求的控制項，直到請求完成或失敗

#### Scenario: Retry a failed list request

- **WHEN** 產品清單請求失敗
- **THEN** 系統顯示本地化的一般錯誤訊息與重試入口，且重試可重新取得目前查詢條件的資料

#### Scenario: Map API validation errors safely

- **WHEN** 建立或更新 API 回傳欄位驗證錯誤
- **THEN** 系統將可辨識的錯誤映射到對應欄位或表單訊息，其他錯誤使用通用本地化訊息，且不呈現 stack trace、內部錯誤或 token

### Requirement: Product UI is offline, localized, responsive, and keyboard usable

系統 SHALL 使用專案內本地 webjar/static 的 Vue 3、Axios、Bootstrap 5.3 與 SweetAlert2 資源，不引用 CDN，並提供中英文 i18n、RWD、清楚的 focus/disabled/loading 狀態與鍵盤可操作的清單、表單、確認及分頁控制項。

#### Scenario: Load page without third-party network access

- **WHEN** 使用者在無外部網路環境開啟 `/products`
- **THEN** 頁面所需 JavaScript、CSS、字型與圖示資源均由專案本地資源提供，不嘗試載入 CDN

#### Scenario: Render supported locale

- **WHEN** 使用者以中文或英文環境開啟產品頁面
- **THEN** 標題、欄位、操作、狀態、驗證、錯誤、重試與確認文字使用對應語系，不顯示缺少的原始訊息鍵

#### Scenario: Use the page on a narrow viewport and keyboard

- **WHEN** 使用者以行動裝置寬度或鍵盤操作清單與表單
- **THEN** 內容可閱讀、控制項不重疊，所有主要操作具可見 focus 狀態且不需滑鼠即可完成

### Requirement: Product UI completion is verified by workflow tests

系統 SHALL 以必要的自動化測試驗證 MVC/API contract、授權邊界、清單搜尋與分頁、新增、編輯、軟停用確認/取消、前端驗證、錯誤狀態、locale 與本地資源引用，並確認不新增 database migration 或 API contract。

#### Scenario: Verify the implementation before completion

- **WHEN** products-ui 實作準備完成
- **THEN** 專案格式化、靜態檢查、單元/整合/workflow tests 與 build 通過，並在桌面與手機 viewport 驗證 RWD；favicon 500 僅記錄為非阻塞 follow-up risk