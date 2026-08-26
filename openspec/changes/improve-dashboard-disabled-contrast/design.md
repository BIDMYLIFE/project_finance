## Context

目前 Dashboard 導覽以非互動的 disabled span 呈現尚未提供 capability，並透過深色導覽背景、Bootstrap disabled 規則與專案自訂狀態樣式共同決定文字顏色。現有樣式將 disabled 文字設為低亮度灰色，造成項目名稱與 coming-soon 提示難以辨識。此 change 只處理既有導覽的視覺與驗證邊界，不改變 capability registry、路由或 API。

## Goals / Non-Goals

**Goals:**

- 建立在 Bootstrap 載入後穩定生效的 disabled 導覽樣式，讓項目名稱與提示文字達到 spec 要求的對比。
- 讓 default、hover 與 Bootstrap disabled 狀態具有一致的可讀顏色，且不以顏色作為唯一狀態訊息。
- 保留非互動元素、`aria-disabled`、狀態公告、合理 tab 順序與可用連結的可見 focus。
- 以既有 Java/JUnit 資源檢查及可用的瀏覽器驗證，覆蓋色彩規則、本地資產、桌面/手機呈現與 no-overlap。

**Non-Goals:**

- 不啟用任何尚未提供的 capability，不新增或猜測導覽路由。
- 不改動登入、API、capability registry、Bootstrap vendor 資產或後端。
- 不以 tooltip、JavaScript click handler 或額外圖示取代既有 coming-soon 文字語意。

## Decisions

### 以專案 disabled 規則作為最後的視覺覆蓋邊界

在現有 Dashboard stylesheet 中針對 disabled 導覽文字及其提示文字定義明確的高對比顏色，並同時約束 hover/disabled 組合狀態，使 Bootstrap 的通用 `.disabled` 規則不會把顏色再次壓暗。顏色選擇以導覽背景為基準，使用對比計算或瀏覽器 accessibility audit 驗證至少 4.5:1。

替代方案是移除自訂樣式、完全依賴 Bootstrap；這無法保證專案古典深色導覽背景上的 AA 對比，因此不採用。改用更亮背景也會影響整體導覽視覺層級，暫不採用。

### 保持既有非互動 markup 與狀態資訊

沿用現有不可用項目的非互動元素、`aria-disabled` 與 coming-soon 文字，不新增 tabindex 或可觸發事件。測試直接確認尚未提供項目沒有 href/不存在路由，並確認可用連結仍有可見 focus；如此顏色改善不會改變操作模型。

替代方案是把 disabled span 改成按鈕以取得原生 disabled 行為，但會擴大 markup、鍵盤與輔助技術契約變更，且不符合目前導覽結構，因此不採用。

### 以資源測試加 viewport 驗證形成最小防回歸組合

在既有 Dashboard navigation test 附近加入可檢查的 CSS/HTML 資源契約；瀏覽器驗證則在桌面與手機 viewport 檢查 computed color、focus、狀態文字可見性、無截斷/重疊與離線資產載入。這同時捕捉規則遺失與實際 Bootstrap cascade 造成的回歸。

替代方案是只做字串測試，執行快速但無法驗證實際對比或響應式布局，因此不採用。

## Risks / Trade-offs

- [Risk] 未來 Bootstrap 升級可能改變 disabled specificity 或變數 → [Mitigation] 保留明確的狀態選擇器與 computed-style/對比測試，升級時重新執行桌面與手機驗證。
- [Risk] 提高灰階亮度可能降低 disabled 與可用連結的視覺差異 → [Mitigation] 以顏色以外的非互動元素與 coming-soon 文本維持狀態區分，並確認可用項目 focus/hover 不被覆蓋。
- [Risk] 手機折疊導覽寬度不足造成提示換行或重疊 → [Mitigation] 使用可換行且穩定的布局規則，在小螢幕展開導覽並檢查文字邊界。

## Migration Plan

1. 先更新 Dashboard disabled/coming-soon 的自訂樣式與必要的資源測試，確認不涉及 capability availability 或路由。
2. 在既有本地資產環境執行單元/資源測試，再以桌面與手機 viewport 驗證對比、focus、狀態語意、無重疊與離線載入。
3. 部署後若視覺回歸，可回退本 change 的 CSS/測試變更；不需資料轉換、資料庫 migration 或 session migration。

## Open Questions

無。對比門檻、非互動語意、驗證 viewport 與離線限制已由 proposal 與 capability spec 定義。