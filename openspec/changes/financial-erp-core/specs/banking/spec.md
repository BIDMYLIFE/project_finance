## Purpose

提供多銀行帳戶的現金流記錄與待入帳處理，讓每筆 Credit/Debit、帳戶餘額、轉帳與更正都具備可追溯的歷史。

## ADDED Requirements

### Requirement: Bank accounts are organization-scoped and currency-aware

系統 SHALL 允許 `ADMIN` 維護所屬 organization 的多個銀行帳戶，至少保存帳戶名稱、幣別、期初餘額與 active 狀態。停用帳戶不得接收新的收款或費用入帳，但既有交易 MUST 保留可查詢。

#### Scenario: Use an active bank account

- **WHEN** 使用者選擇目前 organization 內幣別相容的 active 銀行帳戶進行入帳
- **THEN** 系統允許建立相應銀行交易並保留帳戶關聯

#### Scenario: Inactive or foreign account is rejected

- **WHEN** 使用者以停用帳戶或幣別不相容帳戶作為新的入帳帳戶
- **THEN** 系統拒絕操作，不建立銀行交易

### Requirement: Credit and Debit transactions update balances

系統 SHALL 以現金流語意記錄 `CREDIT` 與 `DEBIT`：`CREDIT` 增加銀行餘額，`DEBIT` 減少銀行餘額。帳戶餘額 MUST 可由期初餘額加 Credit 總額減 Debit 總額重算，並提供交易日期、金額、來源與狀態。

#### Scenario: Calculate a bank balance

- **WHEN** 使用者查詢包含期初餘額、Credit 與 Debit 交易的銀行帳戶
- **THEN** 系統回傳 `opening_balance + credit_total - debit_total` 的期末餘額與可追溯明細

#### Scenario: Reject non-positive transaction amount

- **WHEN** 使用者建立金額為零或負數的 Credit/Debit 交易
- **THEN** 系統回傳欄位驗證錯誤，且不改變銀行帳戶餘額

### Requirement: Payments and expenses post as auditable bank transactions

已確認且指定有效銀行帳戶的收款 SHALL 建立一筆 `CREDIT`；具備有效付款帳戶的費用 SHALL 建立一筆 `DEBIT`。每筆銀行交易 MUST 能追溯至來源收款或費用，且跨資料異動需整體成功或回滾。

#### Scenario: Post a pending payment

- **WHEN** 使用者為 `PENDING_DEPOSIT` 收款指定同 organization 且幣別相容的 active 銀行帳戶
- **THEN** 系統在同一操作中建立 `CREDIT`、將收款轉為 `POSTED`，並從待入帳清單移除

#### Scenario: Post an expense as debit

- **WHEN** 使用者確認有效費用並指定 active 銀行帳戶
- **THEN** 系統建立對應 `DEBIT`，扣減帳戶可重算餘額，並保留費用與交易的雙向來源關聯

### Requirement: Transfers and account changes preserve history

帳戶轉帳 SHALL 以來源帳戶 `DEBIT` 與目的帳戶 `CREDIT` 成對記錄。已入帳收款更換銀行帳戶時，系統 MUST 保留原交易並建立可追溯的反向/沖銷與新帳戶 `CREDIT`，不得直接覆寫或刪除歷史交易。

#### Scenario: Transfer between accounts

- **WHEN** 使用者在同 organization 且幣別相容的兩個 active 帳戶間轉帳
- **THEN** 系統原子建立一筆來源 DEBIT 與一筆目的 CREDIT，兩筆交易共享轉帳參考並可互相追溯

#### Scenario: Move a posted payment to another account

- **WHEN** 使用者將已入帳收款改指定至另一個有效帳戶
- **THEN** 系統保留原收款與原交易，建立反向/沖銷歷史與新帳戶 CREDIT，且不改變收款金額、分類、事由、備註、日期或收據編號

### Requirement: Financial corrections are non-destructive

系統 SHALL 以反向交易、沖銷狀態或等效不可破壞方式處理銀行交易更正；已產生的財務交易不得由一般刪除操作移除。銀行報表 MUST 能排除已沖銷交易並保留原交易鏈。

#### Scenario: Correct a bank transaction

- **WHEN** `ADMIN` 更正已確認的銀行交易
- **THEN** 系統保留原交易，建立帶有原交易參考的反向或沖銷紀錄，並使有效餘額只計入未沖銷結果
