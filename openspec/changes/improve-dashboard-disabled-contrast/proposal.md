## Why

Dashboard 導覽中的尚未提供項目目前以 disabled/coming-soon 文字呈現，但自訂灰色與 Bootstrap disabled 狀態疊加後，文字和深色導覽背景的對比不足，使用者難以辨識項目名稱與尚未可用提示。需要改善可讀性，同時保留這些項目不可啟動的語意與既有鍵盤、輔助技術行為。

## What Changes

- 提升 Dashboard disabled/coming-soon 導覽項目及其提示文字在深色導覽背景上的文字對比與辨識度。
- 明確定義自訂 disabled 樣式與 Bootstrap 狀態的優先順序，避免 disabled/hover/focus 狀態產生過暗或不一致的顏色。
- 保留未提供 capability 使用非互動 disabled span、`aria-disabled` 與狀態提示的語意，不將尚未提供項目變成可啟動連結或猜測路由。
- 補充 CSS/資產層級檢查與桌面、手機 viewport 的瀏覽器驗證，涵蓋文字對比、提示可讀性、鍵盤焦點與無重疊顯示。

## Capabilities

### New Capabilities

- `dashboard-disabled-contrast`: Dashboard 導覽未提供 capability 的 disabled/coming-soon 視覺對比、狀態樣式與可及性驗證，涵蓋既有 Dashboard 導覽的改善契約。

### Modified Capabilities

<!-- No main spec exists yet for dashboard-post-auth-routing; the new capability above records this focused improvement without creating a delta that lacks a baseline. -->

## Impact

- 影響 Dashboard 導覽的 CSS 狀態樣式，以及對應的資源/瀏覽器測試；預期不需修改 capability registry、路由、API 或後端資料。
- 需要依賴 Bootstrap 5.3 disabled 樣式行為與目前 `dashboard.html` 的 disabled span 結構，確認自訂規則在實際載入順序下穩定生效。
- 驗證範圍包含本地離線資產載入、桌面與手機尺寸、文字及 coming-soon 提示對比、鍵盤操作和輔助技術可辨識性。