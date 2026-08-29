## Context

參照 proposal 的 Why。目前專案已有 `SOUTHWND.expenses` 預留表、`ExpenseStatus`、銀行帳戶/交易模型及 `expense_categories` API，但尚無 Expense Entity、Service、頁面或 Dashboard capability。既有前端採 Vue 3 JavaScript、local WebJars、API module 與頁面 module；後端遵循 `Controller -> Service -> Repository`。

## Goals / Non-Goals

**Goals:**

- 建立完整的 Expense draft/confirm/void workflow 與 organization-scoped API。
- 確認費用與銀行 DEBIT 以同一 Service transaction 原子完成。
- 提供可在桌面及手機使用的 Expense 頁面，並把入口接到 Dashboard。
- 重用已完成的 expense category、bank account、organization context 及 centralized error contract。

**Non-Goals:**

- 不實作完整複式簿記、供應商主檔、稅務申報或外部銀行同步。
- 不建立階層式費用分類或跨幣別匯兌。
- 不提供 Expense hard delete。

## Decisions

1. **沿用現有 expenses 表並以新 migration 擴充。** 保留既有 `description`、`amount`、`currency_code`、`expense_date` 欄位，新增 `category_id`、`actor_id`、`payee_name`、`note`、狀態時間欄位與必要 constraints；如此可避免重新建立已存在的資料表。替代方案是新建 expense ledger 表，但會與現有 financial-erp-core schema 分裂。

2. **Expense API 採 `/api/v1/expenses`，頁面採 `/expenses`。** 頁面 MVC Controller 只回傳模板，Web API Controller 只處理 JSON；所有寫入由 Expense Service 協調分類、帳戶及 bank transaction repositories。

3. **確認是唯一產生 DEBIT 的事件。** DRAFT 不影響銀行餘額；confirm 在同一 transaction 驗證 active category/account、建立 `BankTransaction(DEBIT, sourceType=EXPENSE, sourceId=expenseId)` 後更新狀態。替代方案是先建費用再由背景工作入帳，但會造成畫面狀態與現金流不同步。

4. **作廢採不可破壞模式。** DRAFT 只改費用狀態；CONFIRMED 由 Service 建立 reversal/void 鏈，保留原 DEBIT，不透過 repository delete。沿用既有 bank transaction 的 `reversal_of_id` 與 source reference 契約。

5. **前端集中 API 與狀態管理。** 新增 expense API module 與 page module，分類和帳戶在初始化時並行載入；表單只保存可編輯欄位，確認/作廢以 SweetAlert2 二次確認，避免直接操作 DOM。

6. **Dashboard registry 只在頁面可用時標記 available。** 新增 `expenses` capability、`/expenses` route、owner 及中英文訊息，並由 navigation tests 固定入口存在與 mobile navigation 不重疊。

## Risks / Trade-offs

- [Risk] 既有 expenses 資料可能缺少新欄位或狀態值 → [Mitigation] migration 先處理 default/backfill，再加入限制；正式部署前對既有資料執行 dry-run 檢查。
- [Risk] confirm/void 的銀行 reversal 若重複送出可能重複扣款或沖銷 → [Mitigation] Service 僅允許合法狀態轉移，並以來源與 reversal reference 做 idempotency/duplicate guard。
- [Risk] 分類或帳戶在頁面載入後被其他操作停用 → [Mitigation] 後端 confirm 再次查驗 active、scope、currency，前端將 server error 顯示為可修正提示。
- [Risk] 費用備註過長造成 mobile layout 問題 → [Mitigation] 使用 bounded 欄位與 responsive Bootstrap layout，列表顯示摘要，詳情/編輯顯示完整文字。

## Migration Plan

1. 先部署 expense schema extension，建立 `expenses.category_id` 外鍵、必要欄位、狀態 constraints、索引及與銀行來源契約相容的欄位。
2. 部署後端 Entity、Repository、Service、DTO/API、MVC route 及測試。
3. 部署本地前端 API/page assets、Dashboard registry/message/template 更新。
4. 回滾應用程式版本時保留已建立的費用與銀行交易；資料庫欄位移除只可在確認沒有新版本資料依賴並完成備份後執行。
