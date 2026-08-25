## Context

請參閱 proposal.md 的動機。本 change 建立在 `auth-jwt-admin-bootstrap` 的登入、refresh、logout、HttpOnly cookie、authenticated principal、ADMIN authorization 與 organization context 之上；現有登入流程成功後尚未提供工作區導向。`financial-erp-core` 與 `erp-reporting` 分別擁有客戶/產品/文件/收款/銀行/報表詳細功能，本 change 只負責入口與共用前端狀態。

實作需遵守 Spring MVC 的 MVC Controller 與 Web API Controller 分離、`Controller -> Service -> Repository` 依賴方向、DTO 與集中錯誤處理，以及專案的離線前端規範。需求沒有發現已存在的 dashboard summary API，因此設計採不含即時財務數字的 MVP。

## Goals / Non-Goals

**Goals:**

- 讓登入成功、受保護 `/`、session failure 與 logout 形成可預期的瀏覽器生命週期。
- 以伺服器 authenticated context 提供最小且安全的目前使用者/organization 顯示資料。
- 建立可擴充但不重複領域頁面的主導航與 capability availability model。
- 集中 Axios credentials、timeout、錯誤分類、refresh/redirect guard 與 Vue loading/error/empty/retry 狀態。
- 以本地 Bootstrap 5.3、Vue 3 JavaScript、Axios、SweetAlert2 與本地字型/圖示完成 RWD、keyboard 與 accessibility 驗證。

**Non-Goals:**

- 不在 Dashboard 建立客戶、產品、報價、發票、收款、銀行或報表的詳細畫面、交易規則或財務計算。
- 不新增沒有契約與來源的即時財務 summary；未來 summary 需另行定義 API 與 organization scope。
- 不改變 JWT 簽發、refresh rotation、cookie 安全屬性或 authentication domain 的既有契約；只接入其公開邊界。
- 不使用 localStorage/sessionStorage 保存 credential，也不引入 CDN 或外部 runtime 資源。

## Decisions

### 1. 以既有 login/logout 契約驅動導頁

登入頁在既有 login API 成功並收到 cookies 後使用 history navigation 導向 `/`；不把 API 成功提示當作終點。Dashboard MVC route 與 Dashboard 內容由 authentication boundary 保護，未通過身份驗證的 request 由 server redirect 或共用 API handler 導向 `/auth/login`。

替代方案是只在前端檢查 cookie 是否存在，但 HttpOnly cookie 不可安全讀取，且存在 cookie 不代表 token 有效，因此不採用前端 cookie presence 作為 authorization。

### 2. 以受保護 current-identity DTO 作為唯一顯示來源

若既有 authentication MVC model 無法安全提供 identity，新增受保護的最小 current-identity API，例如 `GET /api/v1/auth/me`，由 authenticated principal 與 server-side organization context 組裝 DTO。DTO 只包含畫面所需的非敏感 user display name/email（依現有資料可得性）與 organization display name/id 的必要顯示欄位，不接受 request 中的 organization/user/role 作為 scope，也不輸出 password、JWT、refresh 值、secret 或完整 entity。

替代方案是登入成功時把 response 中的 id/name 寫入前端 storage，會讓身份顯示可被竄改且形成第二個狀態來源，因此不採用。若 `/me` 失敗，Dashboard 不用登入頁或舊快取資料冒充身份；依錯誤分類執行安全錯誤或登入導向。

### 3. 將 session handling 放在共用 Axios/API service

建立單一共用 API client，集中 base URL、timeout、`withCredentials`、錯誤 DTO 轉換與 request state。response handling 對 `401` 採一次受控 refresh 嘗試；refresh endpoint 本身與 `/auth/login` 不再觸發 refresh interceptor，失敗後清理前端暫態狀態並 redirect guard 導向 `/auth/login`。`403` 只有在回應代表 session 仍有效時顯示授權錯誤，不把它當成可繼續讀取的資料。

替代方案是每個 Vue page 自己處理 status code，會導致 refresh、redirect loop 與錯誤訊息不一致，因此不採用。所有 operation component 仍透過資料/event 更新 UI，不直接操作 DOM；必要的 focus restoration 由共用可及性元件或 Vue lifecycle 邊界處理。

### 4. 導航採宣告式 capability registry

主導航使用集中設定的 capability registry，至少註冊 Dashboard 與 customer management，並保留 product、quote、invoice、payment、banking、reporting 的 label、route、availability 與 owner。只有 capability 明確標記可用且 route 已由其 owner 提供時才產生可啟動 link；否則產生有 accessible disabled/coming-soon 狀態的項目，不請求不存在的 endpoint。

客戶入口連至 `financial-erp-core` 的既定 customer route；其他財務詳細入口不在本 change 猜測路徑或複製頁面，待 owner change 提供 route contract 後更新 registry。這讓 Dashboard 不會偷偷承擔 domain implementation，也避免使用者被導到未受保護的 placeholder URL。

### 5. MVP 不建立 dashboard summary API

第一版 Dashboard 只載入 current identity（若採 `/me`）與 capability availability，不呼叫未定義的 summary endpoint，也不顯示客戶數、應收款、銀行餘額、收款或報表數字。若未來要顯示數字，必須由獨立 capability 提供 versioned DTO、organization-scoped authorization、loading/error/empty contract 與測試，再以相容方式接入；summary failure 不得阻塞基本導航。

替代方案是從各領域清單 API 即時計算數字，會重複財務規則、增加查詢成本並造成畫面與報表口徑不一致，因此不採用。

### 6. 離線與 UI 邊界沿用專案既有資源方式

Bootstrap 5.3、Vue 3、Axios、SweetAlert2、字型及圖示使用既有本地 vendor 或 build asset，資源版本由專案依賴管理鎖定。頁面採 Bootstrap responsive layout，主導航在小螢幕提供可鍵盤操作的折疊控制；所有按鈕、狀態提示與 disabled controls 有 focus/disabled/loading 樣式與 accessible name。SweetAlert2 只用於短暫操作結果，持久錯誤與 session 導向同時提供頁面內可讀狀態。

### 7. 以瀏覽器測試固定安全與可用流程

瀏覽器測試以 mock/stub 的 API 契約驗證 login success -> `/`、無 credential `/` -> login、`401` refresh success/failure、logout cookie-clearing response、`/me` safe DTO、navigation availability、無 summary request、network retry 與禁止重導循環。測試在桌面與手機 viewport 驗證 focus、tab order、折疊導航、文字不重疊與 SweetAlert/page alert 不遮蔽主要控制項；另外掃描載入資源，確認沒有外部 URL/CDN。

## Risks / Trade-offs

- [Risk] server redirect 與前端 interceptor 的責任邊界若重複，可能造成導向循環 → [Mitigation] 對 login、refresh、logout 與 login path 設 redirect guard，並以瀏覽器測試驗證最多一次受控 refresh。
- [Risk] current-identity endpoint 會增加 authentication change 的整合面 → [Mitigation] 使用最小 DTO、只從 authenticated principal/context 組裝，並將 endpoint 納入本 change 的 API/安全測試；若 MVC 已能提供等價安全資料則重用該來源。
- [Risk] 既有財務 capability 路由尚未穩定，導航可能暫時不可用 → [Mitigation] registry 以 availability gate 控制，未完成項目維持 disabled/coming-soon，不導向猜測路徑。
- [Risk] 不顯示摘要數字降低 Dashboard 的即時營運資訊 → [Mitigation] 明確標示 MVP 邊界，等正式 summary contract 完成後再增量接入，不從領域清單自行推算。
- [Risk] 小螢幕折疊導航與錯誤提示可能遮蔽內容 → [Mitigation] 使用穩定 Bootstrap layout、可見 focus、狀態區域與桌面/手機瀏覽器 no-overlap 測試。

## Migration Plan

1. 先確認 `auth-jwt-admin-bootstrap` 已可提供成功登入、受保護路由、refresh、冪等 logout、cookie 清除與一致 `401/403` error contract；確認 customer capability 的實際入口，其餘詳細頁先標為不可用。
2. 若沒有等價的安全 identity view model，新增最小受保護 current-identity DTO/API；不變更既有 token、cookie 或 organization authorization 規則。
3. 建立共用 Axios/API state service、redirect/refresh guard 與登入成功導向，再建立 Dashboard MVC route、Vue view、registry、logout 與客戶入口。
4. 接入本地 vendor/build assets，完成 loading/error/empty/retry、keyboard/accessibility 與 RWD 狀態；確認 Dashboard 不發送未定義 summary request。
5. 執行單元、API/security 與瀏覽器桌面/手機測試，包含 offline asset/network failure、cookie clear、session expiry、navigation availability 與 no-overlap。
6. 部署時先確保 authentication change 已完成，再啟用 Dashboard route。回滾可停用 `/` Dashboard route 或回退前端入口，不刪除 authentication session、使用者或任何財務資料；若 `/me` 新增 migration/contract 失敗，整體部署不啟用 Dashboard。

沒有需要資料轉換的既有 Dashboard 資料；MVP 也不建立 summary snapshot 或財務快照。

## Open Questions

無。會影響 acceptance 的選擇已決定：current identity 優先重用現有安全 MVC/API model，若不存在則由本 change 建立最小受保護 `/api/v1/auth/me`；summary 採無即時財務數字 MVP；未完成詳細 capability 一律 disabled/coming-soon。
