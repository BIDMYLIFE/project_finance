## Context

目前 workspace 尚未有應用程式身份驗證實作，也沒有可沿用的 user、organization 或 session schema。ERP 後續功能需要在請求進入 Controller 前完成身份與角色判斷，並在 Service 層持續使用使用者的 organization context，避免只依賴前端傳入的 organization id。

此 change 必須符合既有的 Spring Boot 分層規範、API Controller 與 MVC Controller 分離、DTO 優先、集中例外處理，以及離線前端資源要求。JWT secret、資料庫密碼與其他 credentials 只能來自部署環境或秘密管理機制；實際值不會進入原始碼、OpenSpec artifacts、前端或 log。

## Goals / Non-Goals

**Goals:**

- 建立 email/password 登入、短效 access JWT、可撤銷 refresh session 與 logout 的完整生命週期。
- 使用 Argon2id 驗證密碼，並以一致的錯誤回應避免洩漏帳號存在與內部安全資訊。
- 以 `ADMIN` 作為目前唯一角色，將 authenticated principal 綁定到 organization。
- 以一次性 bootstrap 建立第一個 organization 與 ADMIN，支援重複請求與並行請求的安全處理。
- 讓受保護資源使用共用的 security context 與 organization scope，便於後續財務模組接入。
- 提供可測試的設定、migration、服務與 API 邊界。

**Non-Goals:**

- 不在本 change 建立完整的多角色 RBAC、權限矩陣、邀請流程或自助註冊。
- 不在本 change 實作客戶、產品、報價、發票、收款或銀行帳務功能。
- 不建立 access token 的伺服器端逐次撤銷清單；access token 以短 TTL 限制風險，logout 主要撤銷 refresh session。
- 不把實際部署 secret、資料庫連線資訊或初始化帳號密碼寫入 repository。

## Decisions

### 1. 使用 Spring Security 建立 request authentication boundary

以 Spring Security filter chain 驗證 access JWT，成功後建立包含 user id、organization id 與 `ADMIN` authority 的 authenticated principal。Web API Controller 只接收已完成身份驗證的請求；Controller 不直接查詢 user/session repository，業務層透過 service 與 organization context 執行規則。

登入、refresh、logout 與 bootstrap 端點明確標記為公開或半公開入口，其餘 ERP API 預設需要 authenticated。MVC 頁面路由與 REST API 路由分離；API 錯誤由共用 exception handler 轉成一致 DTO，避免 Spring 預設 HTML 錯誤頁混入 API。

### 2. Access JWT 短效，refresh token 以資料庫 session 管理

登入成功時產生短效 access JWT（預設 TTL 可配置，例如 15 分鐘），claims 只包含必要的 subject、organization、role、issued-at、expiry 與 session identifier，不放入密碼或其他敏感資料。JWT 的簽章 secret 由 `security.jwt.secret=${JWT_SECRET}` 提供，啟動時缺少必要 secret 應使安全設定失敗，而不是使用內建預設值。

Refresh token 使用高熵隨機值，對外只在 HttpOnly、Secure、SameSite cookie 中傳送；資料庫的 `auth_sessions` 只保存 refresh token hash、user/organization 關聯、建立與到期時間、最近使用時間及 revoked_at。refresh 先驗證 hash、期限與撤銷狀態，再以交易原子化地撤銷舊 token 並建立新 token，避免同一 refresh token 被重複使用。

### 3. Cookie 與 logout 行為集中管理

Cookie 名稱、path、domain、Secure、SameSite 與 TTL 由集中設定提供，不散落在 Controller。登入與 refresh 設定新的 credentials；logout 不論目前 session 是否已撤銷，都清除同名 cookie，並在可識別 session 時寫入 revoked_at。這使 logout 冪等，也讓 refresh repository 成為撤銷狀態的單一來源。

### 4. Argon2id 僅保存不可逆密碼雜湊

使用 Spring Security 的 password encoder abstraction，實際 encoder 配置為 Argon2id，參數集中於安全設定並可依部署效能調整。user entity 只保存 `password_hash`；DTO、log、例外與 audit payload 不攜帶原始密碼。登入對不存在 email 與錯誤密碼使用相同的外部錯誤，不以不同查詢結果或訊息暴露帳號存在。

### 5. Bootstrap 使用交易與資料庫層級的一次性保護

建立 organization、ADMIN user、role 關聯與 initialized marker 必須由 bootstrap service 在同一 transaction 完成。初始化狀態由專用設定/狀態資料表保存，並以唯一鍵或等效資料庫條件保證只有一個 initialized marker；service 在建立前取得適當的 row lock，遇到競態時回滾失敗交易。bootstrap endpoint 只在尚未初始化時可用，成功後由 service 的狀態判斷永久關閉，而不是依賴前端隱藏按鈕。

初始化輸入使用 request DTO 與 Bean Validation。organization 名稱、email、密碼政策與唯一性檢查在 service 中協調，repository 只負責資料存取與必要的唯一查詢。organization 與 ADMIN 建立成功後，後續 login 才能建立正常 auth session。

### 6. Organization context 由已驗證身份導出

不信任 request body、query parameter 或前端自行傳入的 organization id 作為授權依據。security principal 的 organization id 由簽章有效且尚未過期的 access JWT 取得，Service 在資料存取條件中強制套用此 organization scope；若未來需要跨 organization 管理，必須另增明確的權限需求，不以目前 `ADMIN` 預設放寬。

### 7. 以 migration 與測試固定安全契約

資料庫 migration 新增 organization、user、role 關聯、auth_sessions 與 bootstrap state 所需的 schema、foreign key、unique constraint、index 與時間欄位。密碼雜湊與 refresh token hash 欄位以足夠長度保存演算法輸出；refresh token 原文永不落庫。

測試分層涵蓋：Argon2id encoder 與登入 service 單元測試、JWT/filter 與錯誤回應整合測試、refresh rotation/reuse/revoke 測試、logout 冪等測試、bootstrap transaction/競態測試，以及 organization isolation API 測試。測試 fixtures 使用專用假值，不使用任何真實 credentials。

## Risks / Trade-offs

- JWT access token 在 logout 後仍可能存活到短 TTL 結束；縮短 TTL 會增加 refresh 流量，因此以短效 access 加上 refresh revoke 取得可接受平衡。需要立即撤銷時，後續可增加 session version 或 access deny-list。
- refresh rotation 需要交易與併發控制；若資料庫隔離級別或唯一 constraint 設計不足，可能出現重放競態，因此必須以整合測試驗證同一 token 的雙重 refresh 只有一個成功。
- HttpOnly cookie 降低 JavaScript 讀取 token 的風險，但跨站請求仍需正確設定 SameSite、CORS 與後續 CSRF 策略；若部署情境需要跨站前端，必須在實作前明確配置允許來源。
- Argon2id 會消耗 CPU 與記憶體；過高參數可能造成登入尖峰延遲，過低則降低抗暴力破解能力，因此參數需可設定並以部署環境基準測試。
- 單一 `ADMIN` 角色能快速建立基礎，但無法表達細緻職責；後續導入更多角色時必須新增明確 capability 與 migration，不直接把目前 ADMIN 判斷複製成散落字串。

## Migration Plan

1. 建立 organization、user、role 關聯、auth_sessions 與 bootstrap state 的 migration，並加入必要的 unique constraint、foreign key 與 index。
2. 建立 security properties 與部署範例，只描述 `JWT_SECRET` 等環境變數名稱與格式要求，不提供可用 secret。
3. 實作 domain/repository、Argon2id encoder、bootstrap service、login/refresh/logout service，再接上 API Controller 與 Spring Security filter chain。
4. 在空資料庫啟動時導向一次性 bootstrap；成功完成後確認 bootstrap endpoint 以 API 與直接 URL 都不可再次建立資料。
5. 執行單元、整合、安全與 organization isolation 測試，再接入後續受保護 ERP API。

本 change 沒有既有使用者或明文密碼需要轉換。若部署在未初始化資料庫上，migration 後必須先完成 bootstrap 才能使用受保護的 ERP 功能；migration 失敗或 bootstrap transaction 回滾時，不應留下可登入的部分資料。

## Open Questions

- ADMIN 後續新增其他使用者的管理 API 與邀請/停用政策，將在使用者管理 capability 中另行定義。
- Cookie domain、反向代理 TLS termination、前端與 API 是否同源，以及跨站部署所需的 CORS/CSRF 策略，需在部署拓撲確認後定案。
- Access/refresh cookie 的實際名稱、TTL、Argon2id 參數與 refresh session 保留期限，需依環境安全基準與效能測試設定，不能使用未審核的預設 secret。