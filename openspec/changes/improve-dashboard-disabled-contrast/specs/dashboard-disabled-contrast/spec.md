## Purpose

讓 Dashboard 導覽中的尚未提供 capability 清楚、可辨識且符合可及性要求，同時保留其不可啟動的 disabled/coming-soon 語意與既有離線、響應式使用限制。

## ADDED Requirements

### Requirement: Disabled navigation has readable status contrast

Dashboard 導覽中尚未提供的 capability SHALL 以足以辨識的項目名稱與 coming-soon 提示呈現；文字與其深色導覽背景的對比 SHALL 符合 WCAG 2.1 AA 一般文字至少 4.5:1 的要求，且不得因一般、hover、disabled 或其他 Bootstrap 狀態而降低可讀性。項目名稱與尚未可用提示 SHALL 在桌面與手機 viewport 保持清楚、不截斷且不互相重疊。

#### Scenario: Unavailable capability is legible in the default state

- **WHEN** 已登入使用者檢視尚未提供的導覽 capability
- **THEN** 項目名稱與 coming-soon 提示均能在導覽背景上清楚辨識，且文字對比達到 WCAG 2.1 AA 一般文字 4.5:1 以上

#### Scenario: State styles do not make the status unreadable

- **WHEN** 尚未提供的導覽項目處於一般、hover 或其他套用 disabled 狀態
- **THEN** 項目名稱與 coming-soon 提示維持可讀對比，不被 Bootstrap 或自訂狀態樣式以低對比顏色覆蓋

#### Scenario: Status remains usable on a narrow viewport

- **WHEN** 使用者在手機寬度展開 Dashboard 導覽
- **THEN** disabled/coming-soon 項目的文字完整可讀，且不與相鄰導覽項目、折疊控制或其他內容重疊

### Requirement: Unavailable navigation preserves disabled semantics

尚未提供 capability SHALL 保持非互動的 disabled/coming-soon 導覽語意，不得提供不存在或未受保護的路由；其狀態 SHALL 以可被輔助技術理解的名稱與狀態呈現。可用 capability 的導覽連結 SHALL 維持正常鍵盤操作與可見 focus，且 disabled 項目不得因改善顏色而被誤認為可啟動控制項。

#### Scenario: Disabled item cannot be activated as a route

- **WHEN** 使用者以滑鼠、鍵盤或觸控嘗試操作尚未提供的 capability
- **THEN** 項目不會導向路由、不會觸發不存在的請求，並保留不可啟動的 disabled 行為

#### Scenario: Assistive technology receives the unavailable state

- **WHEN** 輔助技術讀取 Dashboard 導覽中的尚未提供項目
- **THEN** 可辨識 capability 名稱、尚未可用提示與 disabled/不可操作狀態，且狀態不只依賴顏色傳達

#### Scenario: Keyboard navigation remains predictable

- **WHEN** 使用者只使用鍵盤巡覽 Dashboard 導覽
- **THEN** 可用連結與導覽控制項具有可見 focus 和合理 tab 順序，尚未提供項目不會被誤納入可啟動的鍵盤操作流程

### Requirement: Disabled contrast change remains offline and locally verifiable

Dashboard disabled/coming-soon 導覽的對比改善 SHALL 使用現有本地資產與頁面資源，不新增外部字型、圖示、CDN 或 runtime 網路依賴；其樣式與資源檢查 SHALL 可由自動化測試驗證，並涵蓋桌面與手機 viewport 的呈現結果。

#### Scenario: Dashboard loads the disabled state without external assets

- **WHEN** 瀏覽器阻擋外部網路並開啟 Dashboard
- **THEN** disabled/coming-soon 導覽仍使用本地資源完成渲染，且不嘗試載入第三方外部資產

#### Scenario: Automated checks cover contrast and accessibility behavior

- **WHEN** 執行 Dashboard 導覽的資產或瀏覽器測試
- **THEN** 測試會驗證 disabled/coming-soon 文字的對比與狀態語意、可用項目的鍵盤 focus，以及桌面/手機 viewport 無截斷或重疊