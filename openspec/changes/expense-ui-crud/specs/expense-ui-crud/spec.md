## Purpose

讓管理者能從 Dashboard 進入費用作業，建立並管理 organization-scoped 費用，使用有效分類與銀行帳戶，並以可追溯的狀態與銀行出帳結果呈現完整流程。

## ADDED Requirements

### Requirement: Dashboard provides an Expenses entry

系統 SHALL 在已登入的 Dashboard 導覽中提供可用的 Expenses 入口，連至費用頁面。入口 MUST 使用現有 capability registry、雙語訊息及本地資源，不得依賴外部 CDN。

#### Scenario: Open expenses from dashboard

- **WHEN** 已登入的 ADMIN 在 Dashboard 選擇 Expenses
- **THEN** 系統導向 `/expenses` 費用頁面

#### Scenario: Expense entry is available on mobile navigation

- **WHEN** ADMIN 使用桌面或行動裝置開啟 Dashboard 導覽
- **THEN** Expenses 入口在既有 responsive navigation 中可見、可聚焦且不與其他入口重疊

### Requirement: Create and manage organization-scoped expenses

系統 SHALL 允許已驗證的 `ADMIN` 建立、查詢及更新所屬 organization 的費用。費用 MUST 保存分類、說明、付款對象、金額、幣別、費用日期、付款銀行帳戶及狀態；所有資源查詢 MUST 套用目前 organization scope。

#### Scenario: Create a draft expense

- **WHEN** ADMIN 提交有效分類、說明、付款對象、正數金額、支援幣別與費用日期
- **THEN** 系統建立 `DRAFT` 費用並回傳費用 DTO，且不建立銀行交易

#### Scenario: Reject invalid expense input

- **WHEN** ADMIN 提交空白必要欄位、非正數金額、不支援幣別或無效日期
- **THEN** 系統回傳一致的 validation error，且不建立或修改費用

#### Scenario: Prevent cross-organization expense access

- **WHEN** ADMIN 以目前 organization 以外的費用 ID 查詢、更新、確認或作廢
- **THEN** 系統回傳資源不存在或等效安全錯誤，且不洩露外部費用資料

### Requirement: Expense confirmation posts an auditable debit

系統 SHALL 允許只有 `DRAFT` 費用被確認。確認時分類 MUST 為 active，銀行帳戶 MUST 屬於目前 organization、active 且幣別相容；系統 MUST 在同一交易中將費用改為 `CONFIRMED` 並建立一筆 `DEBIT` bank transaction，來源指向該費用。

#### Scenario: Confirm expense and post debit

- **WHEN** ADMIN 確認有效 DRAFT 費用並指定 active、幣別相容銀行帳戶
- **THEN** 系統原子地建立 `DEBIT`、將費用改為 `CONFIRMED`，並回傳可追溯的費用資料

#### Scenario: Reject invalid confirmation account or category

- **WHEN** ADMIN 確認費用時使用停用、外部 organization 或幣別不相容的分類/銀行帳戶
- **THEN** 系統拒絕確認，不建立銀行交易，費用仍維持原狀態

#### Scenario: Roll back failed confirmation

- **WHEN** 費用更新或 DEBIT 建立任一步驟失敗
- **THEN** 系統回滾整個確認操作，不留下部分更新或孤立銀行交易

### Requirement: Void expenses without deleting history

系統 SHALL 以作廢取代刪除費用。DRAFT 或 CONFIRMED 費用可作廢；CONFIRMED 費用作廢時 MUST 保留原費用與原 DEBIT，並建立可追溯的 reversal/void 結果；一般 Expense API 不得 hard delete。

#### Scenario: Void a draft expense

- **WHEN** ADMIN 作廢 DRAFT 費用
- **THEN** 系統將狀態改為 `VOIDED`，不建立銀行交易，且費用仍可查詢

#### Scenario: Void a confirmed expense

- **WHEN** ADMIN 作廢已確認費用
- **THEN** 系統保留原 DEBIT 與費用資料，建立符合銀行更正規則的沖銷鏈，並將費用標記為 `VOIDED`

### Requirement: Expense page handles operational states

費用頁面 SHALL 顯示可分頁的費用清單、分類與帳戶選項，並統一處理 loading、empty、validation error、network error、success、disabled 與 retry 狀態。確認與作廢等具破壞性或不可逆語意的操作 MUST 使用 SweetAlert2 確認訊息。

#### Scenario: Load expense page data

- **WHEN** ADMIN 開啟或重試費用頁面
- **THEN** 系統顯示 loading 狀態，成功後顯示費用清單與 active 分類/帳戶選項，無資料時顯示 empty state

#### Scenario: Handle expense request failure

- **WHEN** 清單或儲存 API 發生 validation、network 或 server error
- **THEN** 系統顯示不暴露內部資訊的錯誤提示，恢復可重試或可修正的操作狀態

### Requirement: Protect expense operations

費用頁面及其 API SHALL 僅允許已驗證且具 `ADMIN` 角色的使用者操作；未登入或非 ADMIN 請求 MUST 被拒絕，錯誤回應不得暴露 token、密碼、資料庫資訊或 stack trace。

#### Scenario: Reject unauthenticated expense operation

- **WHEN** 未登入使用者存取 `/expenses` 或 Expense API
- **THEN** 頁面導向登入或 API 回傳未授權錯誤，且不讀寫費用資料

#### Scenario: Reject non-admin expense operation

- **WHEN** 非 ADMIN 使用者呼叫 Expense API
- **THEN** 系統回傳禁止存取錯誤，且不讀寫費用資料
