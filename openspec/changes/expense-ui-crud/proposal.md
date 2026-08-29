## Why

Dashboard 目前沒有 Expense 入口，使用者無法從系統工作台進入費用作業；雖然 `expense_categories` API 已完成，但尚未有可供使用者建立、查詢或維護費用的頁面。現在補上完整 Expense UI 與對應流程，才能讓費用資料真正進入日常操作及後續報表。

## What Changes

- 在 Dashboard capability registry 新增可用的 Expenses 入口，連至 `/expenses`。
- 新增 Expense MVC page、Vue 頁面及本地離線資源整合。
- 提供 organization-scoped 費用清單、建立、編輯、確認與作廢操作。
- 費用表單使用 active `expense_categories`，並支援銀行帳戶、幣別、金額、日期、付款對象、說明與備註。
- 確認費用時驗證 active 分類與 active、幣別相容的銀行帳戶，並建立可追溯的銀行 `DEBIT`。
- 顯示 loading、空資料、驗證失敗、網路錯誤、成功及 disabled/loading 操作狀態。
- 保留已確認或已作廢費用的歷史資料，不提供 hard delete。

## Capabilities

### New Capabilities

- `expense-ui-crud`: Expense 頁面、費用交易操作、分類/銀行帳戶選擇及 Dashboard 入口。

### Modified Capabilities

- 無

## Impact

- 新增 Expense entity、repository、DTO、service、API controller、MVC controller、Vue page、API module、模板與樣式。
- 修改 Dashboard capability registry、Dashboard page message 注入、雙語訊息與導航測試。
- 延伸現有 `SOUTHWND.expenses` schema，使其可關聯 `expense_categories`，並與 `bank_transactions` 建立來源追溯。
- 依賴既有 authentication、organization context、bank account 與 expense-category API；不新增外部套件或 CDN 資源。
