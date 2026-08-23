# 專案開發規範

本文件是本專案的共同開發規範。新增或修改程式碼時，應優先遵守本文件；若需求與本文件衝突，必須先確認並更新規範或取得明確同意。

## 技術基準

### 後端

- 使用 Spring Boot 建置應用程式。
- Web 應用採用 Spring MVC。
- 後端採用分層架構，至少區分以下四層：
  - `Repository`：資料存取與查詢。
  - `Service`：業務規則、交易邏輯與跨資料來源協調。
  - `MVC Controller`：處理頁面請求並回傳 View。
  - `Web API Controller`：提供 REST API 並回傳 JSON。
- Controller 不得直接存取資料庫或實作業務規則；Controller 只能負責請求解析、輸入驗證、呼叫 Service 與回應組裝。
- Service 不得依賴 Controller；資料庫操作必須透過 Repository 進行。
- Repository 不得放置業務判斷；複雜查詢應集中在 Repository 或對應的查詢物件中。
- MVC Controller 與 Web API Controller 必須分開，不得以同一個 Controller 同時承擔頁面與 API 責任。
- API 的輸入與輸出優先使用 DTO，不直接暴露 Entity。
- API 回應格式、HTTP 狀態碼、錯誤訊息與分頁欄位須保持一致。
- 跨多筆資料異動時，交易邊界由 Service 定義，並使用適當的交易管理機制。
- 例外處理應集中管理，避免在每個 Controller 重複撰寫相同的錯誤處理程式。
- 設定值、連線資訊與密碼不得硬編碼在原始碼中，應使用設定檔、環境變數或秘密管理機制。

### 前端

- 前端必須採用離線架構，所有執行時需要的 JavaScript、CSS、字型與圖片資源都必須放在專案內或由建置產物提供。
- 禁止使用 CDN，包括但不限於 Bootstrap、Vue、Axios、SweetAlert2、字型與圖示 CDN。
- RWD 使用 Bootstrap `5.3.x`，且 Bootstrap 套件必須以本地依賴或本地靜態檔案提供。
- AJAX 使用 Axios，且 Axios 必須以本地依賴或本地靜態檔案提供。
- MVVM 使用 Vue `3.x` JavaScript；除非需求明確指定，前端不使用 TypeScript 取代 JavaScript。
- 對話盒、確認訊息與操作結果提示使用 SweetAlert2，禁止自行重複實作相同用途的對話盒。
- Vue 元件應以資料與事件驅動畫面，避免直接操作 DOM；API 呼叫集中於明確的 service 或 API 模組。
- 前端應統一處理 loading、成功、失敗、空資料與網路錯誤狀態。
- API URL、逾時時間與其他環境差異設定不得散落在元件中，應集中管理。

## 建議專案結構

後端套件應依責任分層，名稱可依實際業務模組調整：

```text
src/main/java/<base-package>/
├── repository/
├── service/
├── controller/
│   ├── mvc/
│   └── api/
├── dto/
├── entity/
├── exception/
└── config/
```

前端靜態資源應集中管理，並確保建置與執行時不需要外部網路：

```text
src/main/resources/
├── static/
│   ├── css/
│   ├── js/
│   │   ├── api/
│   │   ├── components/
│   │   └── pages/
│   ├── vendor/
│   │   ├── bootstrap/
│   │   ├── axios/
│   │   ├── vue/
│   │   └── sweetalert2/
│   └── assets/
└── templates/
```

## MVC Controller 規格

- MVC Controller 負責頁面路由與 View Model 組裝，回傳模板名稱，不回傳 API JSON。
- View 所需的初始資料應由明確的 View Model 或 DTO 提供。
- 頁面表單提交應有明確的成功、驗證失敗與例外處理流程。
- 不得把 Repository 或 Entity 直接傳入 View；頁面資料應經過適當轉換。

## Web API Controller 規格

- API 路徑使用一致的資源導向命名，例如 `/api/v1/users`。
- HTTP 方法應符合語意：`GET` 查詢、`POST` 建立、`PUT` 或 `PATCH` 更新、`DELETE` 刪除。
- 成功與失敗都應使用一致的 JSON 結構；錯誤回應至少包含錯誤代碼、訊息與必要的欄位錯誤資訊。
- 使用正確的 HTTP 狀態碼，例如 `200`、`201`、`204`、`400`、`401`、`403`、`404` 與 `500`。
- 請求參數必須進行 Bean Validation；驗證錯誤不可直接暴露例外堆疊資訊。
- 清單 API 應支援一致的分頁、排序與篩選參數，並避免一次回傳不受限制的大量資料。
- 不得在回應中暴露密碼、Token、內部例外訊息或不必要的資料庫欄位。

## 離線資源規格

- 所有第三方前端套件必須鎖定版本並納入專案依賴管理或版本控制的靜態資源。
- HTML、JavaScript 與 CSS 中不得出現 `https://` 或 `http://` 的第三方資源引用。
- 不得使用 `@import` 載入外部 CSS 或字型。
- 建置後應能在無網路環境啟動並完成主要頁面操作；若功能需要後端，僅允許連線到專案定義的後端服務。
- 新增套件時，必須確認授權、版本、離線可用性與是否會引入外部資源。

## 古典風格 UI

- 網頁採用古典、穩重、易讀的視覺方向，優先使用襯線字體、對比清楚的排版、細緻邊框與節制的裝飾。
- 色彩以象牙白、深墨色、酒紅、古銅或深綠等低飽和色為主，確保文字與背景符合可讀性及無障礙對比要求。
- 保持一致的間距、標題層級、按鈕狀態與表單樣式，不以裝飾取代資訊層級。
- 古典風格不得犧牲 RWD、鍵盤操作、表單可用性或行動裝置閱讀體驗。
- 所有互動元件需提供 hover、focus、disabled 與 loading 等必要狀態。

## 品質與提交前檢查

- 新增功能應同步補充對應的單元測試、整合測試或 API 測試。
- 驗證後端分層依賴方向：`Controller -> Service -> Repository`，不得反向依賴或跨層繞接。
- 驗證前端在無網路環境下不會嘗試載入 CDN 資源。
- 驗證桌面與手機尺寸的 RWD 版面，並確認文字、表單與對話盒不會互相遮蔽。
- 提交前執行專案既有的格式化、靜態檢查、測試與建置指令。
