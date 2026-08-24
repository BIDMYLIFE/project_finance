## Purpose

提供 organization-scoped、幣別一致且可追溯的銀行現金流模型，讓收款能安全入帳並能在待入帳、帳戶異動與更正過程中保留完整歷史。

## ADDED Requirements

### Requirement: Bank accounts are isolated and currency-aware

系統 SHALL 允許 `ADMIN` 維護目前 organization 的多個銀行帳戶，每個帳戶至少包含名稱、幣別、期初餘額與 active 狀態。停用帳戶不得接收新的入帳交易，但既有交易 MUST 保留可查詢；銀行帳戶與交易不得跨 organization 存取。

#### Scenario: Create and use an active account

- **WHEN** `ADMIN` 建立目前 organization 的 active 銀行帳戶，並以相同幣別收款入帳
- **THEN** 系統保存帳戶並允許該帳戶接收對應的 `CREDIT`

#### Scenario: Reject an inactive or foreign account

- **WHEN** 使用者以停用帳戶、其他 organization 帳戶或幣別不相容帳戶建立新的入帳
- **THEN** 系統拒絕操作，不建立銀行交易且不改變任何帳戶餘額

### Requirement: Credit and debit transactions produce a reproducible balance

系統 SHALL 只接受正數的 `CREDIT` 或 `DEBIT` 銀行交易；`CREDIT` 增加餘額，`DEBIT` 減少餘額。帳戶有效餘額 MUST 可由 `opening_balance + valid credits - valid debits` 重算，並保存交易日期、幣別、金額、來源、狀態與操作人。

#### Scenario: Recalculate an account balance

- **WHEN** `ADMIN` 查詢包含期初餘額與有效 Credit/Debit 交易的帳戶
- **THEN** 系統回傳可由來源交易重算的期末餘額、Credit 總額、Debit 總額與明細

#### Scenario: Reject an invalid transaction amount or direction

- **WHEN** 使用者提交零或負數金額，或非 `CREDIT`/`DEBIT` 的交易方向
- **THEN** 系統回傳欄位驗證錯誤，且不改變交易清單或帳戶餘額

### Requirement: Pending deposits post atomically

系統 SHALL 提供將 `PENDING_DEPOSIT` 收款指定至銀行帳戶的操作。該操作 MUST 在同一交易中驗證 organization、幣別與 active 狀態、建立一筆來源可追溯的 `CREDIT`，並將收款轉為 `POSTED`；任一部分失敗時，收款與銀行交易 MUST 一起回滾。

#### Scenario: Post a pending deposit

- **WHEN** `ADMIN` 為待入帳收款指定同 organization、幣別相容且 active 的銀行帳戶
- **THEN** 系統建立一筆 `CREDIT`、保留收款來源關聯、將收款改為 `POSTED`，並從待入帳結果移除

#### Scenario: Roll back a failed posting

- **WHEN** 待入帳收款不存在、已被其他操作處理、帳戶停用/不相容，或建立銀行交易失敗
- **THEN** 系統不建立部分 `CREDIT`，收款仍維持原狀，且回傳可重試或可修正的錯誤

### Requirement: Financial corrections are append-only and traceable

系統 SHALL 以反向交易、沖銷狀態或等效不可破壞方式處理已建立的銀行交易；一般操作不得直接更新或刪除已確認交易。更正後的有效餘額 MUST 排除原交易的重複效果，並能沿交易鏈追溯原交易、反向交易、原因、actor 與時間。

#### Scenario: Move a posted payment to another account

- **WHEN** `ADMIN` 將已入帳收款改指定至另一個有效且幣別相容的帳戶
- **THEN** 系統保留原收款、原 `CREDIT` 與原收據，建立可追溯的原交易反向/沖銷紀錄及新帳戶 `CREDIT`，且收款金額與收據內容不變

#### Scenario: Correct a bank transaction without deletion

- **WHEN** `ADMIN` 更正一筆已確認銀行交易並提供原因
- **THEN** 系統保留原交易，建立帶有原交易參考的更正結果，使有效餘額只計算有效交易，且拒絕一般刪除或直接改寫歷史

### Requirement: Banking APIs enforce organization and administrative access

銀行帳戶、銀行交易、待入帳與更正操作 SHALL 只允許已驗證且具 `ADMIN` 權限的使用者。請求中的 organization id、帳戶 id、收款 id 或來源 id 不得覆蓋 authenticated organization context；跨 organization 或不存在的資源 MUST 不洩漏其他 organization 的資料。

#### Scenario: Reject an unauthenticated or non-admin request

- **WHEN** 未登入使用者或沒有 `ADMIN` 權限的使用者呼叫銀行查詢或異動 API
- **THEN** 系統回傳一致的身份驗證或授權錯誤，不執行資料庫異動

#### Scenario: Prevent cross-organization posting

- **WHEN** `ADMIN` 嘗試以 request body 或路徑參數指定另一 organization 的帳戶或待入帳收款
- **THEN** 系統拒絕請求，不建立交易、不改變收款，且回應不包含被指定 organization 的資料
