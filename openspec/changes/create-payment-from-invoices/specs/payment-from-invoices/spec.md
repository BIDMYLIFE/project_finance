## Purpose

提供一種由單筆或多筆符合條件的發票建立單筆付款的方式，確保付款、客戶、發票分配與銀行入帳資料一致。

## ADDED Requirements

### Requirement: Select eligible invoices

系統 MUST 允許使用者選取至少一筆狀態為「已開立」或「部分付款」且仍有未付款餘額的發票建立付款。

#### Scenario: Create from one invoice
- **WHEN** 使用者選取一筆符合條件的發票
- **THEN** 系統允許建立一筆付款，並建立該付款與發票的關聯

#### Scenario: Create from multiple invoices
- **WHEN** 使用者選取多筆符合條件的發票
- **THEN** 系統允許建立一筆付款，並為每筆發票建立付款分配

#### Scenario: Reject ineligible invoice
- **WHEN** 使用者選取已付款、作廢、草稿或無未付款餘額的發票
- **THEN** 系統拒絕建立付款並回傳可理解的驗證錯誤

### Requirement: Enforce customer and currency consistency

系統 MUST 禁止不同客戶或不同幣別的發票合併成同一筆付款。

#### Scenario: Mixed customers
- **WHEN** 選取的發票包含不同客戶
- **THEN** 系統拒絕建立付款

#### Scenario: Mixed currencies
- **WHEN** 選取的發票包含不同幣別
- **THEN** 系統拒絕建立付款

### Requirement: Auto-fill payer and amount

系統 MUST 將付款人與所選發票的客戶關聯，並將付款金額預設為所有所選發票未付款餘額的總和。付款金額 MAY 調低以支援部分付款，但 MUST 不得超過可分配餘額。

#### Scenario: Auto-fill customer and full balance
- **WHEN** 使用者選取同一客戶的發票
- **THEN** 系統自動帶入客戶作為付款人、設定客戶關聯，並帶入未付款餘額總和

#### Scenario: Partial payment
- **WHEN** 使用者輸入小於所選發票餘額總和的付款金額
- **THEN** 系統建立部分付款，依既定分配順序分配金額，並將未付足的發票保留為「部分付款」

#### Scenario: Amount exceeds balance
- **WHEN** 使用者輸入大於所選發票可付款餘額總和的金額
- **THEN** 系統拒絕建立付款

### Requirement: Post linked payment immediately

由發票建立付款時，系統 MUST 強制使用者選擇目前組織中啟用且幣別相符的銀行帳戶，並在建立成功後將付款標記為「已入帳」。

#### Scenario: Immediate posting
- **WHEN** 使用者選擇有效銀行帳戶並完成付款建立
- **THEN** 系統建立已入帳付款及對應的銀行收入交易

#### Scenario: Missing or invalid bank account
- **WHEN** 使用者未選銀行帳戶，或所選帳戶未啟用、非目前組織或幣別不符
- **THEN** 系統拒絕建立付款

### Requirement: Preserve manual payments

系統 MUST 保留原有手動建立未關聯發票付款的功能；新增關聯付款流程不得改變該流程的既有行為。

#### Scenario: Manual payment remains available
- **WHEN** 使用者使用原有新增收款入口且不選擇發票
- **THEN** 系統仍依原有規則建立付款
