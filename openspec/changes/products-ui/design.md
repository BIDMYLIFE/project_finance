## Context

現有 Spring Boot ERP 已提供 Product entity、ProductRequest、ProductResponse、ProductRepository、MasterDataService 與 `MasterDataApiController`，並已有 `/api/v1/products` 的 GET/POST/PUT/DELETE API。產品與服務共用同一個 Product 模型，後端既有 organization scope 與 ADMIN 權限規則是本頁面必須沿用的邊界；本 change 只補登入後 `/products` 頁面，不改 API 或資料庫。

專案的 customers UI 已建立 Thymeleaf、Vue 3、Axios、Bootstrap 5.3、SweetAlert2 與本地 vendor/static 資源的整合模式。本設計沿用該架構與古典風格，但不修改 customers UI。所有頁面文字需透過既有中英文訊息資源提供，且前端不可依賴 CDN。

## Goals / Non-Goals

**Goals:**

- 新增專用 `/products` Thymeleaf MVC page controller，讓頁面路由與既有 JSON API controller 分離。
- 以既有 products API 實作產品/服務清單、名稱搜尋、active/inactive 篩選、伺服器分頁、新增、編輯與軟停用。
- 集中管理 Vue 頁面狀態、請求生命週期、表單驗證、錯誤映射與防重複請求。
- 使用本地 Vue/Axios/Bootstrap/SweetAlert2 資源，提供中英文、RWD 與鍵盤可用性。
- 以 controller/API contract、前端 workflow、授權、locale、資源引用與 build/test 驗證完成條件。

**Non-Goals:**

- 不修改 `MasterDataApiController`、Product entity、request/response DTO、Repository、Service 或既有 API 路徑、HTTP 方法與回應 contract。
- 不新增 database migration、Product `type` 欄位、產品/服務分類切換、批次匯入匯出、實體刪除、排序或進階篩選。
- 不重新實作 organization scope、ADMIN 權限或登入保護；不在前端繞過後端授權。
- 不處理 favicon 500；只列為非阻塞 follow-up risk。

## Decisions

### 使用專用 MVC page controller

新增 products page controller，只負責驗證後的頁面路由與 Thymeleaf view；瀏覽器載入頁面後由 Vue module 呼叫既有 `/api/v1/products`。這維持 `Controller -> Service -> Repository` 邊界及 MVC/API 分離，避免 page controller 直接查 Repository 或 API controller 回傳 view。

### 以既有 products API 作為唯一資料來源

清單查詢將名稱與 active 狀態轉成既有 API 支援的查詢參數，頁碼與 page size 交由伺服器處理；查詢條件變更時重設第一頁。新增使用 POST、編輯使用 PUT、軟停用使用 DELETE `{id}`，成功後重新整理目前查詢。前端不一次載入全部資料，也不自行重建伺服器未回傳的分頁結果。

### 產品與服務維持單一模型

頁面欄位直接對應既有 ProductRequest/ProductResponse：`productCode`、`name`、`description`、`unitPrice`、`currencyCode`、`taxRate` 與 `active`。不新增 type 欄位或分類控制；產品與服務的差異若存在，維持在既有資料內容與後端規則中。編輯模式將 productCode 設為唯讀。

### Vue page module 集中狀態與 API helper

沿用 customers UI 的 JavaScript 結構，將 API URL、逾時與環境設定集中於既有 API client/helper，頁面 module 管理 query、rows、pagination、form、mode、loading、error、retry 與 field errors。請求期間停用相關控制項並以 finally 恢復狀態，避免重複提交與過期回應覆蓋較新結果。

### 前端與後端驗證採雙層且錯誤安全

前端在送出前檢查必填、長度、非負單價、允許幣別與 0-100 稅率，錯誤顯示在欄位並保留輸入。API 回傳的 validation error 依既有錯誤 DTO/欄位名稱映射至本地化訊息；無法辨識的欄位錯誤與一般錯誤使用通用訊息。任何 stack trace、內部例外內容、token 或資料庫細節都不得進入畫面。

### 沿用本地資源與古典 RWD 版面

Thymeleaf template 僅引用專案內 webjar/static 的 Vue 3、Axios、Bootstrap 5.3、SweetAlert2、CSS 與必要圖示/字型，加入自動化檢查避免外部 URL。表格在窄版面維持可閱讀與可水平操作，表單與分頁採 Bootstrap responsive layout；互動元件提供 focus、disabled、loading、錯誤與鍵盤操作狀態。

### 以 workflow tests 驗證可交付條件

補充 MVC page route 與 API contract 測試，確認 API 未被修改；以頁面/JavaScript workflow tests 驗證查詢、搜尋、篩選、伺服器分頁、表單驗證、create/update、確認與取消停用、loading/error/retry/empty、防重複請求、locale 與 offline 資源。另執行既有格式化、靜態檢查、測試、build 及桌面/手機 viewport 檢查。

## Risks / Trade-offs

- [既有 products API 的查詢或分頁欄位名稱與 UI 預期不同] -> 以實際 Product DTO、PageResponse 與既有 API contract test 建立 mapping，不修改 API。
- [API validation error 欄位格式不一致] -> 在 API helper 集中容錯 mapping，未知錯誤回退到安全的表單訊息，並補不同錯誤形狀的測試。
- [登入 session/token 傳遞方式與頁面請求不同] -> 沿用既有 API client 與 Spring Security 流程，以未登入頁面與 API 401/403 測試確認不洩露資料。
- [中文或英文訊息鍵遺漏] -> 新增鍵值時同步更新兩份 locale 資源，並以 locale rendering/缺鍵檢查驗證。
- [窄視窗表格與控制項擁擠] -> 使用響應式 grid、表格容器與穩定控制項尺寸，在 desktop/mobile viewport 進行視覺檢查。
- [favicon 請求仍回傳 500] -> 明確列為非阻塞 follow-up risk，本 change 不修改 favicon handling。

## Migration Plan

1. 新增 products MVC controller、Thymeleaf template、Vue/Axios page module、CSS 與中英文訊息鍵；所有第三方資源維持本地引用。
2. 補上 controller/API contract、workflow、授權、錯誤、locale 與資源檢查，執行格式化、靜態檢查、測試與 build。
3. 在既有登入流程下驗證清單與維護操作，並以桌面與手機 viewport 檢查 RWD 和鍵盤操作；部署不需 migration 或 API 版本遷移。
4. 回滾時移除 products page 資源、controller 與測試；既有 Product API 與資料不受影響。

## Open Questions

無。API 重用範圍、共用 Product 模型、organization scope/ADMIN 規則、頁面互動、離線資源、i18n、測試與 favicon scope 已確定。