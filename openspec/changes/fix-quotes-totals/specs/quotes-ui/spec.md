## MODIFIED Requirements

### Requirement: Quote UI handles operational states accessibly
報價頁面 SHALL 使用既有本地前端資源與本地化訊息，並呈現 loading、成功、欄位驗證錯誤、網路錯誤、空資料、disabled 與 retry 狀態。清單、明細表單、狀態操作與確認訊息 MUST 支援鍵盤操作與窄螢幕閱讀，不得依賴外部 CDN。報價明細表單中的小計、稅額與總額 MUST 在 Vue 管理的頁面內容中顯示計算結果，不得將模板插值語法暴露給使用者。

#### Scenario: Save quote with validation failure

- **WHEN** 使用者提交缺少必要欄位或無效明細的表單
- **THEN** 頁面標示對應欄位與通用錯誤，保留使用者輸入，且不送出無效保存結果

#### Scenario: Recover from a quote list network failure

- **WHEN** 報價清單請求發生網路錯誤
- **THEN** 頁面顯示本地化錯誤與重試操作，並在重試期間避免重複請求

#### Scenario: Render calculated quote totals inside the Vue page

- **WHEN** 使用者開啟報價建立或編輯表單
- **THEN** 頁面在 Vue mount root 內顯示小計、稅額與總額的計算值，且不顯示 `{{ previewTotals.* }}` 等未處理的插值文字
