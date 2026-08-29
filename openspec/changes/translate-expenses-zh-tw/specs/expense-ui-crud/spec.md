## MODIFIED Requirements

### Requirement: Expense page handles operational states
費用頁面 SHALL 顯示可分頁的費用清單、分類與帳戶選項，並統一處理 loading、empty、validation error、network error、success、disabled 與 retry 狀態。確認與作廢等具破壞性或不可逆語意的操作 MUST 使用 SweetAlert2 確認訊息。當目前語系為 `zh-TW` 時，頁面所有 user-facing Expense 訊息 MUST 使用繁體中文，包括頁面標題、欄位、按鈕、狀態、錯誤、成功與確認訊息。

#### Scenario: Load expense page data

- **WHEN** ADMIN 開啟或重試費用頁面
- **THEN** 系統顯示 loading 狀態，成功後顯示費用清單與 active 分類/帳戶選項，無資料時顯示 empty state

#### Scenario: Handle expense request failure

- **WHEN** 清單或儲存 API 發生 validation、network 或 server error
- **THEN** 系統顯示不暴露內部資訊的錯誤提示，恢復可重試或可修正的操作狀態

#### Scenario: Render Traditional Chinese Expense text

- **WHEN** ADMIN 以 `zh-TW` 語系開啟 `/expenses` 或執行費用操作
- **THEN** 頁面顯示繁體中文的標題、操作、狀態、表單、錯誤、成功與確認訊息，不顯示英文 Expense UI 文案
