## Context

本變更承接 proposal 中的登入後客戶管理需求。現有 `MasterDataApiController` 已提供 `/api/v1/customers` 的建立、更新、軟停用與查詢 API；查詢接受名稱關鍵字、啟用狀態與 `PageQuery`，頁面路由 `/customers` 尚未有專用 MVC controller。專案已有 Thymeleaf、Vue 3、Axios、Bootstrap 5.3、SweetAlert2 與中英文訊息資源，且前端必須離線運作。

## Goals / Non-Goals

**Goals:**

- 建立登入後 `/customers` 的頁面入口，並將頁面呈現與 JSON API 責任分離。
- 以單一頁面狀態管理查詢條件、伺服器分頁、表單模式、請求狀態與錯誤狀態。
- 重用既有客戶 API 契約，不新增第二套客戶資料存取流程。
- 透過既有本地 vendor 資源與 i18n 機制完成可離線、可 RWD 的 UI。

**Non-Goals:**

- 不修改客戶 API 的路徑、HTTP 方法、DTO、分頁格式或後端業務規則。
- 不新增客戶批次匯入、匯出、刪除、排序或進階篩選功能。
- 不處理 favicon 500；將其保留為已知風險與後續工作。

## Decisions

### 使用專用 Thymeleaf page controller

新增 `/customers` 的 MVC controller，只回傳頁面模板；客戶資料仍由瀏覽器呼叫 `/api/v1/customers`。這能維持 `Controller -> Service -> Repository` 的後端邊界，也避免頁面路由與 API controller 混責。替代方案是讓 API controller 回傳頁面或在頁面 controller 直接查 Repository，兩者都會破壞現有分層契約，因此不採用。

### 使用既有 API 作為唯一資料來源

清單查詢以 `keyword`、`active` 與頁碼參數組成請求，新增使用 POST、編輯使用 PUT、軟停用使用 DELETE；搜尋或狀態切換時將頁碼重設為第一頁。這能讓 UI 的分頁結果與伺服器資料一致。替代方案是在前端一次載入全部客戶後本地篩選，但會失去伺服器分頁並造成資料量與權限風險，因此不採用。

### 頁面狀態集中在 Vue page module

頁面 module 管理清單、查詢表單、分頁 metadata、編輯中的客戶、表單驗證、loading、空資料與 API 錯誤，API 呼叫則集中在明確的 service/helper。這可避免散落的 DOM 操作與重複請求，並讓 SweetAlert2 只負責確認與結果提示。替代方案是以 inline script 分散處理事件，會難以維持狀態一致，因此不採用。

### 沿用專案本地資源與 i18n 訊息鍵

Thymeleaf 頁面只引用專案內的 Vue、Axios、Bootstrap 與 SweetAlert2 靜態資源，文字與錯誤訊息透過既有中英文訊息鍵提供。Bootstrap grid、表格可水平捲動區與可縮排的表單布局支援窄視窗；控制項在請求期間停用以避免重複提交。替代方案是 CDN 或頁面內硬編碼文字，但會違反離線與 i18n 約束，因此不採用。

### API 錯誤採用安全的使用者訊息

前端將驗證錯誤映射到欄位或表單訊息，其他錯誤顯示通用本地化訊息並保留重試或修正入口，不把例外堆疊、token 或內部欄位直接呈現。停用前使用 SweetAlert2 確認，取消時不發出 DELETE 請求。

## Risks / Trade-offs

- [現有 API 的回應欄位或分頁欄位若與 UI 假設不一致] -> 以既有 DTO/PageResponse 實際契約建立 mapping，並補 API/controller 與頁面測試。
- [瀏覽器直接載入頁面時 session/token 保護方式不同] -> 沿用既有登入後路由與 API client 的認證處理，未登入時交由既有安全流程導向登入。
- [中英文訊息鍵遺漏導致 UI 顯示原始鍵名] -> 新增訊息時同步檢查兩份 locale 資源，並以缺鍵檢查或頁面測試驗證。
- [favicon 請求仍回傳 500] -> 本次不處理，記錄為非阻塞風險，另行建立後續變更。

## Migration Plan

1. 先新增 page controller、頁面模板、客戶頁面 JavaScript/CSS 與中英文訊息，並確認所有資源引用為本地路徑。
2. 執行既有單元、整合與建置檢查，另以登入、查詢、CRUD、錯誤與窄視窗情境驗證頁面。
3. 部署時不需資料庫 migration 或 API 版本遷移；回滾時移除新增頁面資源與 controller 即可，既有客戶 API 不受影響。

## Open Questions

無。API 路徑、主要互動、離線依賴、i18n 與 favicon scope 已在規格與本設計中決定。
