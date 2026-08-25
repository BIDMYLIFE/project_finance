## Purpose

讓已完成身份驗證的使用者從登入頁進入安全且可用的 ERP 工作入口，透過一致的 Dashboard、主導航、登出與 API session 狀態處理銜接後續業務 capability，而不重複實作各財務詳細頁。

## ADDED Requirements

### Requirement: Successful login routes to the protected dashboard

系統 SHALL 在既有登入 API 成功且 authentication cookies 已設定後，將瀏覽器導向 `/`。`/` SHALL 是受保護的 Dashboard 頁面；沒有有效 authenticated session 的請求不得取得 Dashboard 的業務內容。

#### Scenario: Login success opens dashboard

- **WHEN** 使用者提交有效登入資料且登入 API 成功
- **THEN** 瀏覽器導向 `/`，並顯示 Dashboard 而非只顯示成功提示

#### Scenario: Unauthenticated dashboard request is rejected

- **WHEN** 沒有有效 access session 的瀏覽器請求 `/`
- **THEN** 系統不提供受保護 Dashboard 內容，並導向 `/auth/login`

### Requirement: Session failures return the user to login safely

前端 SHALL 將未授權、access session 過期、refresh session 失敗或 API 回傳未驗證錯誤視為 authentication failure，清除可由前端控制的身份狀態後導向 `/auth/login`。導向 SHALL 避免無限重導，並可保留非敏感的 return path 供登入後返回原本頁面。

#### Scenario: Expired access session redirects to login

- **WHEN** 受保護 Dashboard 或其他已接入頁面收到 access session 過期或 `401` 回應
- **THEN** 共用 API 狀態處理導向 `/auth/login`，且不把 token、cookie 值或錯誤堆疊顯示給使用者

#### Scenario: Refresh failure does not loop

- **WHEN** session refresh 已失敗或 refresh token 已撤銷
- **THEN** 前端停止重試該 session，導向 `/auth/login`，且不在登入頁再次觸發受保護 API 重導循環

#### Scenario: Authorization denial is not treated as data

- **WHEN** 使用者已登入但 API 回傳禁止操作
- **THEN** 系統顯示安全且一致的授權錯誤狀態；若回應表示 session 已無效則導向登入，否則不洩露受保護資料

### Requirement: Logout revokes session and returns to login

系統 SHALL 提供可由鍵盤操作的 logout command，呼叫既有 logout API/service，讓伺服器撤銷目前 refresh session 並清除 access/refresh authentication cookies；無論 session 已撤銷、缺失或重複 logout，完成後瀏覽器 SHALL 回到 `/auth/login`。前端 SHALL 不以 localStorage、sessionStorage 或自訂可讀 token 取代伺服器 cookie 契約。

#### Scenario: Valid session logs out

- **WHEN** 已登入使用者執行 logout
- **THEN** 系統撤銷目前 session、清除 authentication cookies、顯示短暫結果或直接完成，並導向 `/auth/login`

#### Scenario: Repeated logout remains safe

- **WHEN** 使用者對已撤銷、缺失或過期 session 執行 logout
- **THEN** 系統仍清除可清除的 authentication cookies、不得建立新 session，並導向 `/auth/login`

### Requirement: Dashboard identity comes from the server authenticated context

Dashboard 顯示的 user 與 organization 資訊 SHALL 來自伺服器驗證的 authenticated principal/organization context，或受保護的最小 current-identity API DTO。該來源 SHALL 不接受前端提交的 organization id、user id 或角色作為授權依據，且回應 SHALL 不包含密碼、token、secret、refresh session 值或不必要的個人資料。

#### Scenario: Dashboard displays authenticated identity

- **WHEN** 已登入使用者開啟 Dashboard
- **THEN** 畫面顯示伺服器解析出的非敏感使用者與 organization 摘要，且不依賴前端可偽造的身份欄位

#### Scenario: Forged organization data cannot change scope

- **WHEN** 前端嘗試以 request body、query、path 或本地狀態提供另一個 organization id
- **THEN** Dashboard identity 與後續 API scope 仍以 authenticated context 為準，不顯示或存取其他 organization 資料

#### Scenario: Identity source is unavailable

- **WHEN** 登入成功但 current-identity 資料取得失敗或回應格式不安全
- **THEN** Dashboard 不顯示猜測或快取的身份資料，依錯誤類型顯示安全錯誤或導向 `/auth/login`

### Requirement: Dashboard provides scoped navigation without duplicating detail pages

Dashboard SHALL 提供一致的主導航，至少包含 Dashboard 與客戶管理入口，並為產品、報價、發票、收款、銀行及報表保留與其既有 capability 對應的入口。客戶/產品/報價/發票/收款/銀行/報表的詳細頁、交易規則與報表數字 SHALL 由各既有 change 負責，本 capability 不得重複實作。

#### Scenario: Customer management entry is available

- **WHEN** 已登入使用者從 Dashboard 開啟客戶管理
- **THEN** 導航進入客戶管理 capability 的既定入口，並保留 authenticated organization scope

#### Scenario: Detail capability is available

- **WHEN** 某個既有業務 capability 已提供可用頁面與路由
- **THEN** Dashboard 導航以一致名稱連到該 capability 的既定入口，不複製其詳細畫面或 API

#### Scenario: Detail capability is not yet available

- **WHEN** 某個導航目標尚未由既有 change 提供
- **THEN** 導航以不可誤觸的 disabled/coming-soon 狀態呈現，明確表示尚未可用，且不導向不存在或未受保護的路由

### Requirement: MVP dashboard does not fabricate live financial summaries

在沒有現成且已驗證的 dashboard summary API 時，Dashboard MVP SHALL 不顯示即時客戶數、應收款、銀行餘額、收款或報表數字，也不得以靜態假資料冒充即時結果。MVP SHALL 仍提供身份摘要、功能入口與各 capability 可用狀態；未來若新增 summary API，必須另有明確 API contract 與 organization scope。

#### Scenario: No summary API exists

- **WHEN** Dashboard 載入且系統沒有既有 summary API
- **THEN** 畫面只顯示安全身份/organization 摘要、導航與可用性狀態，不發出未定義的 summary request，也不顯示虛構財務數字

#### Scenario: Summary API is added later

- **WHEN** 後續 capability 提供經授權、可驗證且有明確錯誤契約的 summary API
- **THEN** Dashboard 只有在契約與 organization scope 完成整合後才可顯示其資料，且 API 失敗時呈現 loading/error/empty 狀態而不阻塞基本導航

### Requirement: Shared API state handling is consistent and accessible

共用 Axios/API layer SHALL 集中管理 API base URL、timeout、credentials、authentication failure、validation error、network error 與重試邏輯。Vue 頁面 SHALL 以資料與事件驅動畫面，並對 loading、success、validation error、network error、empty、disabled、retry 與 logout 狀態提供可理解且可操作的呈現；操作結果提示 SHALL 使用本地 SweetAlert2 或一致的可及性提示機制。

#### Scenario: API request shows loading and completion states

- **WHEN** Dashboard 或導航頁面發出 API request
- **THEN** 控制項在 request 期間呈現 loading/disabled，成功、空資料或錯誤完成後恢復可操作狀態並顯示對應結果

#### Scenario: Network failure can be retried

- **WHEN** API 因網路錯誤失敗且 session 仍有效
- **THEN** 畫面顯示不洩露內部資訊的 network error 與可操作 retry，retry 不會重複提交 logout 或造成無限請求

#### Scenario: Keyboard and assistive technology can operate navigation

- **WHEN** 使用者只使用鍵盤或輔助技術操作 Dashboard
- **THEN** 主導航、客戶入口、可用性狀態、logout、錯誤提示與 retry 均有可見 focus、可理解 accessible name、合理 tab 順序與必要的狀態公告

### Requirement: Runtime assets remain offline and responsive

Dashboard 與共用前端 SHALL 在無外部網路時運作；Bootstrap 5.3、Vue 3、Axios、SweetAlert2、字型、圖示與圖片 SHALL 由專案本地 vendor 或建置產物提供，不得依賴 CDN、外部 font、外部 icon 或 runtime `http://`/`https://` 資源。介面 SHALL 支援桌面與手機 viewport，並確保文字、導航、錯誤提示與互動控制不重疊或被截斷。

#### Scenario: Offline assets load locally

- **WHEN** 瀏覽器阻擋外部網路並開啟 Dashboard
- **THEN** 頁面使用本地 runtime assets 完成渲染，且不嘗試載入第三方 CDN 或外部字型/圖示

#### Scenario: Responsive layout remains usable

- **WHEN** 使用者在支援的桌面或手機 viewport 開啟 Dashboard、展開導航並觸發錯誤提示
- **THEN** 內容可讀、控制項不重疊、文字不被裁切，且可透過鍵盤與觸控完成主要操作
