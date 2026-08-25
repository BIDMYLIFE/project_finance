## Why

登入成功後目前只顯示成功提示，使用者仍停留在登入頁，無法進入受保護的 ERP 工作區。現在補上登入後 Dashboard、導覽、登出與共用前端狀態邊界，可讓身份驗證 change 與後續客戶、財務功能形成可用的入口，同時避免 Dashboard 提前重做各領域詳細頁。

## What Changes

- 登入成功後導向 `/`，並提供受保護的 Dashboard 首頁。
- 未授權、access session 過期或 refresh 失敗時，統一清理前端可見狀態並導向 `/auth/login`。
- 提供登出操作：呼叫既有 logout 契約、清除 authentication cookies，完成後回到登入頁。
- 提供主導航與客戶管理入口；產品、報價、發票、收款、銀行與報表連結依既有 change 的可用狀態導向，尚未可用時以一致的 disabled/coming-soon 行為處理，不建立詳細頁。
- 建立共用 Axios/API 狀態處理，統一 loading、成功、驗證錯誤、網路錯誤、未授權、session 過期、空資料與 retry 行為。
- 以伺服器驗證的 authenticated identity/organization context 提供 Dashboard 顯示資訊；不信任前端自行保存或提交的 organization/user 資料。
- Dashboard MVP 不提供即時財務數字；在沒有既有 summary API 的前提下，不新增虛構的財務統計，僅呈現安全身份摘要、功能入口與明確的可用狀態。
- 維持本地離線資源、Bootstrap 5.3、Vue 3 JavaScript、Axios、SweetAlert2、RWD、鍵盤操作與 accessibility，並補充瀏覽器測試。

## Capabilities

### New Capabilities

- `dashboard-post-auth-routing`: 登入後導向、受保護 Dashboard、主導航、登出、共用 API/session 狀態處理與安全身份摘要。

### Modified Capabilities

<!-- No existing main capability is modified; authentication and financial details remain owned by their existing changes. -->

## Impact

- 依賴 `auth-jwt-admin-bootstrap` 提供 login/logout、HttpOnly cookies、authenticated principal、organization scope 與一致錯誤回應。
- 新增受保護的 `/` MVC page route、Dashboard Vue page、主導航與共用 Axios/API service；不修改客戶、產品、報價、發票、收款、銀行或報表詳細頁的責任範圍。
- 需要明確的安全 current-user/organization 顯示來源；若現有 authentication API 沒有可用 endpoint，需在本 change 增加受保護的最小 identity endpoint，回傳非敏感 DTO，不回傳 token、密碼或可被前端偽造的 organization scope。
- 所有 Vue、Bootstrap 5.3、Axios、SweetAlert2、字型與圖示資源由專案本地 vendor 或建置產物提供，不新增 CDN 或外部 runtime 依賴。
- 驗證包含 API/session 邊界、logout cookie 清除、未授權導向、導航可用性、離線資源、桌面與手機瀏覽器流程。
