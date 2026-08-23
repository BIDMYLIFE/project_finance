## Purpose

提供從報價到發票的銷售文件流程，確保明細、稅額、應收餘額、狀態與組織內編號可以被一致計算並追蹤。

## ADDED Requirements

### Requirement: Quotes support a controlled lifecycle

系統 SHALL 支援報價單草稿、送出、接受、拒絕、過期與取消狀態，並只允許符合目前狀態的轉換。報價單 MUST 屬於一個 organization 與客戶，並保存明細、幣別、稅額、總額、建立時間與有效期限。

#### Scenario: Submit a valid draft quote

- **WHEN** 使用者提交包含 active 客戶、至少一筆有效明細、有效期限與可計算金額的草稿報價單
- **THEN** 系統保存報價單並將狀態由 `DRAFT` 轉為 `SENT`

#### Scenario: Invalid quote transition is rejected

- **WHEN** 使用者嘗試將已接受或已取消的報價單重新送出
- **THEN** 系統拒絕狀態轉換，且不修改原報價內容或狀態

#### Scenario: Accepted quote can create an invoice

- **WHEN** 使用者對 organization 內已接受的報價單要求建立發票
- **THEN** 系統建立保留客戶、明細、幣別與稅務結果的發票草稿，並保留來源報價關聯

### Requirement: Invoices have organization-unique numbers and lifecycle states

系統 SHALL 支援發票草稿、已發出、部分付款、已付款、逾期與取消狀態。每張發票 MUST 有 organization 內唯一且不可重用的發票編號、客戶、文件日期、到期日、明細、稅額、總額、已付金額與應收餘額。

#### Scenario: Issue an invoice with a unique number

- **WHEN** 使用者確認有效的發票草稿
- **THEN** 系統在同一 organization 內產生唯一發票編號，將狀態轉為 `ISSUED`，並保存當時的明細與金額快照

#### Scenario: Duplicate invoice number is never accepted

- **WHEN** 建立或匯入發票時產生已存在於同 organization 的發票編號
- **THEN** 系統拒絕該操作，不覆寫既有發票，也不產生第二張相同編號的發票

#### Scenario: Overdue status follows due date

- **WHEN** 發票已發出、仍有應收餘額且目前日期晚於到期日
- **THEN** 系統在查詢或狀態更新時呈現 `OVERDUE`，已付款或取消的發票不得被標為逾期

### Requirement: Document totals use consistent tax and rounding rules

報價單與發票 SHALL 依明細數量、單價、折扣與稅務設定計算小計、稅額、總額與應收餘額。系統 MUST 使用 organization 設定的幣別精度與固定 rounding 規則，並在保存與回應中提供可重算的一致結果。

#### Scenario: Calculate a taxed invoice

- **WHEN** 發票包含多筆有效數量、單價與稅率明細
- **THEN** 系統回傳每筆明細小計、文件小計、稅額、總額與初始應收餘額，且總額等於小計加稅額再依規則四捨五入

#### Scenario: Negative or inconsistent totals are rejected

- **WHEN** 請求明細造成負數數量、無效稅率或客戶應收餘額超過文件總額
- **THEN** 系統回傳驗證或業務規則錯誤，且不保存不一致的文件

### Requirement: Invoice payment allocation updates receivable status

系統 SHALL 允許已確認收款分配至同 organization 的一張或多張發票；所有分配金額總和 MUST 不超過收款金額與各發票剩餘應收額。分配後系統 MUST 更新發票已付金額、應收餘額與 `PARTIALLY_PAID`/`PAID` 狀態。

#### Scenario: Allocate one payment across invoices

- **WHEN** 使用者以有效收款將金額分配至同 organization 的多張未結清發票，且每筆分配都不超過剩餘額
- **THEN** 系統保存分配，更新每張發票的已付金額與應收餘額，並使收款與發票可互相追溯

#### Scenario: Allocation beyond outstanding balance is rejected

- **WHEN** 分配金額超過收款金額或任一發票的剩餘應收額
- **THEN** 系統拒絕整個分配交易，不留下部分分配或錯誤的發票狀態
