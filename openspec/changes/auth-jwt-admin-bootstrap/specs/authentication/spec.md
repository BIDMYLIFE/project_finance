## Purpose

為 ERP 提供可撤銷、可稽核且與 organization 關聯的身份驗證邊界，讓第一位管理者能安全完成系統初始化，並讓後續財務 API 只接受有效身份與授權。

## ADDED Requirements

### Requirement: Bootstrap creates the first organization administrator

系統 SHALL 提供一次性初始化流程，讓尚未完成初始化的部署建立一個 organization 與其第一位 `ADMIN` 使用者。初始化請求 MUST 包含 organization 名稱、ADMIN email 與密碼；成功後系統 MUST 將部署標記為已初始化，且不得再透過相同入口建立另一位初始 ADMIN。

#### Scenario: First bootstrap succeeds

- **WHEN** 部署尚未完成初始化，且提交有效的 organization 名稱、未使用的 email 與符合密碼政策的密碼
- **THEN** 系統建立 organization 與 `ADMIN` 使用者，將使用者關聯至該 organization，回傳成功結果，並關閉後續初始化入口

#### Scenario: Bootstrap is unavailable after completion

- **WHEN** 部署已完成初始化，且再次提交初始化請求
- **THEN** 系統拒絕請求並回傳一致的衝突或禁止操作錯誤，且不新增 organization 或使用者

#### Scenario: Concurrent bootstrap permits one winner

- **WHEN** 多個初始化請求同時在尚未完成初始化的部署送出
- **THEN** 只有一個請求成功建立 organization 與 ADMIN，其餘請求失敗，且不得留下部分建立的資料

### Requirement: Passwords are protected with Argon2id

系統 SHALL 以 Argon2id 雜湊保存使用者密碼，且任何 API 回應、前端資料、一般應用程式 log 或資料庫欄位 SHALL NOT 包含明文密碼。登入驗證 MUST 使用保存的雜湊驗證密碼，而非比較明文或可逆加密值。

#### Scenario: Stored password is not recoverable plaintext

- **WHEN** 初始化或建立使用者完成
- **THEN** 使用者資料只保存 Argon2id 密碼雜湊，並不保存原始密碼

#### Scenario: Incorrect password is rejected

- **WHEN** 使用者以正確 email 與錯誤密碼登入
- **THEN** 系統拒絕登入並回傳不揭露帳號是否存在的一致身份驗證錯誤

### Requirement: Login issues short-lived access and refresh credentials

系統 SHALL 提供以 email 與密碼登入的流程；成功時 MUST 核發短效 access JWT 與具期限的 refresh token，並將 refresh token 綁定至使用者、organization 與可撤銷的登入 session。瀏覽器模式 MUST 以 HttpOnly、Secure、SameSite cookie 傳送 credentials，且回應 SHALL NOT 暴露密碼或 token secret。

#### Scenario: Valid credentials login

- **WHEN** 已存在且可登入的使用者提交正確 email 與密碼
- **THEN** 系統建立登入 session，設定 access 與 refresh cookie，並回傳不包含敏感 credential 內容的登入成功結果

#### Scenario: Unknown email does not reveal account existence

- **WHEN** 不存在的 email 提交登入
- **THEN** 系統以與錯誤密碼相同的一致身份驗證錯誤回應，不揭露該 email 是否存在

#### Scenario: Expired access credential is not accepted

- **WHEN** 請求只帶有已過期的 access JWT
- **THEN** 系統拒絕受保護資源請求並回傳未驗證錯誤

### Requirement: Refresh credentials are rotated and revocable

系統 SHALL 只接受未過期且未撤銷的 refresh token 取得新的 access JWT；成功 refresh MUST 使原 refresh token 失效並核發新的 refresh token。refresh token 被重複使用、撤銷或過期時，系統 MUST 拒絕 refresh，且不得延長原登入 session。

#### Scenario: Valid refresh rotates credentials

- **WHEN** 使用者提交尚未過期且尚未使用的 refresh token
- **THEN** 系統核發新的 access JWT 與 refresh token，使舊 refresh token 不能再次使用，並更新登入 session 的期限或狀態

#### Scenario: Revoked refresh is rejected

- **WHEN** 使用者提交已被撤銷的 refresh token
- **THEN** 系統拒絕 refresh 並回傳未驗證錯誤，不核發任何新 credential

### Requirement: Logout revokes the current session

系統 SHALL 提供登出流程，撤銷目前登入 session 的 refresh token，並清除 access 與 refresh cookie。登出完成後，原 session 的 refresh token MUST 不能再取得新的 access JWT；重複登出 SHALL 保持冪等且不得造成錯誤資料。

#### Scenario: Logout invalidates refresh

- **WHEN** 使用者以有效登入 session 執行登出
- **THEN** 系統標記該 session 為已撤銷、清除身份驗證 cookie，並回傳成功結果

#### Scenario: Repeated logout is idempotent

- **WHEN** 使用者對已撤銷或不存在的 session 再次執行登出
- **THEN** 系統仍清除 cookie 並回傳可接受的成功結果，且不建立新 session

### Requirement: Protected resources enforce ADMIN authorization and organization scope

系統 SHALL 驗證受保護請求的 access JWT、使用者狀態與 organization 關聯；目前唯一有效角色為 `ADMIN`。未驗證請求 MUST 被拒絕，已驗證但不具所需 `ADMIN` 權限的請求 MUST 被禁止，且業務資料查詢與異動 SHALL 只允許目前使用者所屬 organization 的範圍。

#### Scenario: Missing credential is rejected

- **WHEN** 未帶有效 access JWT 的請求存取受保護資源
- **THEN** 系統回傳未驗證錯誤，且不執行任何業務操作

#### Scenario: ADMIN accesses own organization data

- **WHEN** 有效 `ADMIN` 存取其 organization 的受保護資源
- **THEN** 系統允許請求，並將資料範圍限制在該 organization

#### Scenario: Cross-organization access is denied

- **WHEN** 有效 `ADMIN` 嘗試以請求參數或 token 內容存取其他 organization 的資料
- **THEN** 系統拒絕請求或回傳不存在結果，且不洩露其他 organization 的資料

### Requirement: Authentication failures use consistent safe responses

身份驗證與授權 API SHALL 對輸入驗證失敗、credential 無效、session 過期或撤銷、未驗證與禁止操作回傳一致的 JSON 錯誤結構與適當 HTTP 狀態碼；錯誤內容 SHALL NOT 暴露密碼、token、JWT secret、資料庫憑證或內部例外堆疊。

#### Scenario: Invalid bootstrap input is reported safely

- **WHEN** 初始化請求缺少必要欄位、email 格式錯誤或密碼不符合政策
- **THEN** 系統回傳欄位層級的驗證錯誤，且不建立任何資料或記錄敏感值

#### Scenario: Authorization failure does not leak internals

- **WHEN** 請求因未驗證、權限不足或 session 失效而被拒絕
- **THEN** 系統回傳一致錯誤代碼與訊息，不回傳 stack trace、token 內容或秘密設定