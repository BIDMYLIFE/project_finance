## Purpose

定義 ERP 登入工作階段中 Access JWT 與 Refresh Token 的有效期限，讓使用者體驗與安全設定可被明確驗證及由環境設定覆寫。

## ADDED Requirements

### Requirement: Access tokens remain valid for the configured eight-hour session window

系統 SHALL 預設建立有效期限為 8 小時的 Access JWT，並使用相同期限設定其 HTTP-only access cookie。`JWT_ACCESS_TTL` 環境設定存在時 SHALL 覆寫預設值。

#### Scenario: Login issues an eight-hour access token
- **WHEN** 使用者以有效帳密登入且未設定 `JWT_ACCESS_TTL`
- **THEN** 回應的 Access JWT `exp` 與 `iat` 相差 8 小時，access cookie 的 Max-Age 亦為 8 小時

#### Scenario: Deployment overrides access token TTL
- **WHEN** 部署設定 `JWT_ACCESS_TTL` 為有效 ISO-8601 duration
- **THEN** 新建立的 Access JWT 與 access cookie 使用該 duration，而非 8 小時預設值

### Requirement: Refresh sessions retain a thirty-day renewable lifetime

系統 SHALL 預設讓 Refresh Token 及其伺服器端 session 有效 30 天；每次成功 refresh SHALL 輪替 Refresh Token 並重新計算 30 天期限，且不得因 Access TTL 調整而縮短。

#### Scenario: Refresh lifetime is independent
- **WHEN** 使用者登入或成功刷新 Access Token
- **THEN** Refresh Token cookie 與 server session 仍使用 30 天預設期限，並取得新的 Access Token
