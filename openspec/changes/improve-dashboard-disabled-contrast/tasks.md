## 1. Disabled 導覽樣式

- [ ] 1.1 檢查 Dashboard 導覽與 Bootstrap 5.3 的 disabled cascade，為未提供 capability 的項目名稱及 coming-soon 提示選定並套用符合 WCAG 2.1 AA 4.5:1 的本地樣式
- [ ] 1.2 明確處理 default、hover、disabled 與小螢幕換行狀態，確保自訂規則不被 Bootstrap 壓暗且文字不截斷或重疊
- [ ] 1.3 確認 disabled span、`aria-disabled`、狀態提示、不可啟動行為與可用連結的 focus/keyboard 行為維持不變

## 2. 自動化資源與可及性測試

- [ ] 2.1 在既有 Dashboard navigation test 附近加入 CSS/HTML 資源契約檢查，驗證 disabled 狀態樣式、coming-soon 提示、非互動語意及不存在未定義路由
- [ ] 2.2 加入或更新可取得 computed style 的測試，驗證 disabled 項目名稱與提示文字相對導覽背景達到 4.5:1，並涵蓋 hover/disabled 狀態
- [ ] 2.3 驗證鍵盤 tab 順序、可用連結 focus、輔助技術可辨識名稱/狀態，確認狀態訊息不只依賴顏色

## 3. 桌面、手機與離線驗證

- [ ] 3.1 以桌面 viewport 開啟並展開 Dashboard 導覽，驗證文字可讀性、對比、focus 與無重疊
- [ ] 3.2 以手機 viewport 展開折疊導覽，驗證 disabled/coming-soon 文字可讀、不截斷、不與相鄰項目或折疊控制重疊
- [ ] 3.3 在阻擋外部網路的條件下驗證 Dashboard 使用本地資產完成渲染，且不新增 CDN、外部字型、圖示或 runtime 網路依賴
- [ ] 3.4 執行專案既有測試、OpenSpec validate 與必要的建置檢查，記錄對比、可及性、響應式及離線驗證結果