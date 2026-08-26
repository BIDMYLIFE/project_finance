## Context

目前 `application.yml` 以 `JWT_ACCESS_TTL` 的 `PT15M` fallback 設定 Access JWT，`SecurityProperties` 綁定 duration，`JwtService` 寫入 JWT `exp`，`AuthenticationCookie` 同時設定 access cookie Max-Age。Refresh Token 使用獨立的 `JWT_REFRESH_TTL` 與資料庫 session expiry。

## Goals / Non-Goals

**Goals:**

- 將單一 Access TTL fallback 改為 `PT8H`，讓 JWT 與 cookie 共用同一設定。
- 以測試保護 JWT claims、cookie duration 與環境覆寫行為。

**Non-Goals:**

- 不修改 refresh token、登入／登出 API、Cookie 安全屬性或授權角色。
- 不新增依賴或資料庫 migration。

## Decisions

- 只修改 `application.yml` 的 fallback 值，保留既有 `SecurityProperties`、`JwtService` 與 `AuthenticationCookie` 的單一設定流。這比在 Java 程式中加入第二個常數更不易造成 JWT 與 cookie 不一致。
- 以 duration 計算測試差值，不依賴固定時區或字串化 token 內容；refresh 測試另行確認其 30 天設定未受影響。
- 保留環境變數優先順序，讓不同部署環境可在不改版的情況下縮短或延長 TTL。

## Risks / Trade-offs

- [Risk] Access token 遺失後可被使用更久 → [Mitigation] 保留 HTTP-only、Secure、SameSite cookie 與 refresh session 撤銷機制，並允許部署覆寫 TTL。
- [Risk] 只改 fallback 可能讓部署環境既有 `JWT_ACCESS_TTL` 維持舊值 → [Mitigation] 在部署設定清單與測試中明確記錄環境變數優先於 fallback。
