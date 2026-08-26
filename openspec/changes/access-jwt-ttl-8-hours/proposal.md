## Why

目前 Access JWT 預設僅維持 15 分鐘，使用者在一般工作時段容易被迫重新刷新驗證。將存取權杖延長至 8 小時，可降低 ERP 日常操作中的登入中斷，同時保留 Refresh Token 的獨立生命週期。

## What Changes

- 將 Access JWT 預設 TTL 從 15 分鐘調整為 8 小時（`PT8H`）。
- 保留 Refresh JWT 預設 TTL 為 30 天（`P30D`）。
- 保留 `JWT_ACCESS_TTL` 環境變數覆寫能力。
- 更新 JWT 設定測試，確認 token 的 `exp` 與 cookie `Max-Age` 都反映 8 小時。

## Capabilities

### New Capabilities

- `auth-session`: 定義 Access JWT 與 Refresh Token 的有效期限及可設定性。

### Modified Capabilities

<!-- No existing main capability spec is present; this change introduces the
     authentication session contract for the configured token lifetimes. -->

## Impact

- `src/main/resources/application.yml` 的安全設定。
- JWT service、Authentication Cookie 與登入／刷新流程測試。
- 不改變 Refresh Token 期限、登入 API 契約、Cookie 名稱或授權規則。
