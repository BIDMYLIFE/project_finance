## Why

費用目前沒有獨立的分類主檔，無法穩定支援費用輸入、依分類篩選及費用報表。現在先建立 organization-scoped 的 `expense_categories`，可讓後續 Expense workflow 使用一致且可停用的分類資料。

## What Changes

- 新增費用分類主檔，保存分類名稱、啟用狀態與建立時間。
- 限制同一 organization 內的費用分類名稱不可重複；不同 organization 可使用相同名稱。
- 提供 ADMIN 建立、查詢、更新及停用費用分類的服務與 Web API。
- 費用分類查詢支援 organization scope、active 篩選、關鍵字、排序與 bounded pagination。
- 停用分類不得被新的費用選用，但既有費用仍保留其分類關聯並可查詢。
- 不在本 change 實作完整費用交易、銀行 DEBIT 入帳或費用報表；這些功能只整合分類的讀取契約。

## Capabilities

### New Capabilities

- `expense-categories`: 管理 organization-scoped 費用分類主檔，並提供給費用流程及報表使用。

### Modified Capabilities

- 無

## Impact

- 新增 `SOUTHWND.expense_categories` migration、Entity、Repository、Service、DTO 與 Web API。
- 後續 `expenses.category_id` 將參照費用分類主檔，並在建立或確認費用時驗證 organization、active 狀態。
- 新增 organization isolation、重複名稱、停用後不可新用及分頁查詢測試。
- 不新增外部依賴；沿用現有 Spring Boot、Spring MVC、DTO validation 與 centralized API error contract。
