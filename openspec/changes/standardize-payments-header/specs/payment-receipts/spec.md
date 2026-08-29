## MODIFIED Requirements

### Requirement: Payment management has an offline responsive UI and dashboard entry
系統 SHALL 提供 `/payments` 頁面與 `/api/v1/payments` 資源導向 API。頁面 MUST 支援繁體中文、RWD、loading、成功、空資料、驗證錯誤與網路錯誤狀態，且所有 runtime 第三方資源 MUST 由專案本地提供。Dashboard SHALL 將 payments capability 標示為可用並連結至 `/payments`。付款管理頁面 MUST 使用與其他管理頁一致的 responsive 導覽，提供本地化的 Dashboard/工作台連結與登出操作；登出進行中按鈕 MUST 顯示 disabled/loading 狀態。

#### Scenario: Open payment management from dashboard

- **WHEN** 管理員在 dashboard 選取付款管理
- **THEN** 系統導向 `/payments` 並顯示目前 organization 的付款資料

#### Scenario: Use payment UI without external assets

- **WHEN** 使用者在無外部網路環境載入付款頁
- **THEN** 頁面仍能載入本地 UI 資源並呈現主要查詢與付款操作

#### Scenario: Navigate from the payment header

- **WHEN** 已登入管理員開啟 `/payments`
- **THEN** 頁面顯示可聚焦的本地化 Dashboard/工作台連結與登出操作，且登出處理期間控制項不可重複操作

#### Scenario: Use payment navigation on a narrow screen

- **WHEN** 管理員使用手機或窄螢幕開啟 `/payments`
- **THEN** header 導覽保持可讀、可操作，Dashboard 與 logout 控制項不重疊或溢出
