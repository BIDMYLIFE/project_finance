## Why

目前 ERP 尚未定義使用者登入、工作階段撤銷或第一位管理者的建立流程，導致後續財務資料無法可靠地限制在正確的組織與操作人員下。現在先建立單一 `ADMIN` 角色的身份驗證基礎，可在實作客戶、產品與財務功能前確立安全邊界，並避免以不安全的密碼保存或不可撤銷的 token 開始累積系統風險。

## What Changes

- 新增以 email 作為登入識別的使用者身份模型，使用 Argon2id 保存密碼雜湊，不保存明文密碼。
- 新增登入流程，核發短效 access JWT 與可撤銷的 refresh token；瀏覽器端透過 HttpOnly、Secure、SameSite cookie 傳送 token。
- 新增登出流程，撤銷對應的 refresh session、使後續 refresh 失效並清除身份驗證 cookie。
- 新增僅有 `ADMIN` 的角色授權基礎，並將已驗證使用者關聯至其 organization。
- 新增首次啟動的 ADMIN 初始化流程：以一次性入口建立 organization 與第一位 `ADMIN`，完成後關閉初始化入口，且整體建立必須具備一致的交易與並行防護。
- 新增身份驗證設定與安全邊界，JWT secret 維持由 `security.jwt.secret=${JWT_SECRET}` 注入，不將實際 secret、資料庫密碼或 token 寫入程式碼、文件與 log。
- 新增身份驗證失敗、初始化已完成、token 撤銷與授權拒絕等情境的一致 API 錯誤回應與稽核所需資料。

## Capabilities

### New Capabilities

- `authentication`: 登入、Argon2id 密碼驗證、access/refresh JWT、登出撤銷、`ADMIN` 授權、首次 organization 與 ADMIN 初始化，以及身份驗證錯誤處理。

### Modified Capabilities

<!-- No existing capabilities are present; this change introduces the first authentication contract. -->

## Impact

- Spring Boot 的 security configuration、authentication controller/API、service、repository、DTO、exception handling 與 organization/user/session domain model。
- 資料庫 migration：使用者、organization、角色關聯、refresh session hash、期限、撤銷時間與必要稽核欄位。
- 登入、refresh、logout、初始化與目前受保護 API 的路由及其 HTTP 狀態碼、cookie 行為與錯誤回應格式。
- 應用程式設定與部署環境，尤其是 `JWT_SECRET`、token TTL、cookie flags 與首次初始化開關；秘密僅由環境或秘密管理機制提供。
- 單元測試、整合測試與安全測試，涵蓋錯誤密碼、過期/撤銷 token、cookie 清除、初始化競態、organization isolation 與未授權存取。