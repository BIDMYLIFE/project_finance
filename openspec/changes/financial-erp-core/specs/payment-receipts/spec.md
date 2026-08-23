## Purpose

提供可稽核的收款、發票分配、待入帳與三聯收據流程，完整保存收款分類、事由、備註、金額與後續銀行入帳關係。

## ADDED Requirements

### Requirement: Confirmed payments require classification and reason

系統 SHALL 要求每筆確認收款恰有一個目前 organization 的 active 收款分類與一個非空事由；備註 SHALL 為選填。收款 MUST 保存付款人、收款日期、金額、幣別、付款方式、分類、事由與備註的最終確認內容。

#### Scenario: Confirm a categorized payment

- **WHEN** 使用者提交有效金額、日期、付款方式、恰好一個 active 分類與非空事由，可選擇性提供備註
- **THEN** 系統建立收款並保存使用者最後確認的分類、事由與備註

#### Scenario: Missing reason or duplicate category is rejected

- **WHEN** 收款缺少事由、指定不存在/停用分類，或同時指定多個分類
- **THEN** 系統回傳驗證或業務規則錯誤，且不確認收款

### Requirement: Confirming a payment creates an immutable receipt number

確認收款 SHALL 在同一 organization 內產生唯一且不可重用的收據編號，即使收款日後作廢也不得回收。重印收據 MUST 沿用原收據編號，不得建立第二筆收款或第二個編號。

#### Scenario: Receipt number is created on confirmation

- **WHEN** 使用者確認有效收款
- **THEN** 系統在保存收款的同一交易中產生唯一收據編號並回傳收據摘要

#### Scenario: Reprint uses the original receipt

- **WHEN** 使用者要求重印已確認收款的收據
- **THEN** 系統建立列印紀錄並輸出原收據資料與原編號，不新增收款

### Requirement: Payments support pending deposit and invoice allocation

收款確認時若未指定銀行帳戶，系統 SHALL 將狀態設為 `PENDING_DEPOSIT`；若已指定有效且同 organization、幣別相容的 active 銀行帳戶，則可進入 `POSTED`。收款 SHALL 支援分配至一張或多張同 organization 發票，未分配餘額 MUST 可被待入帳與收款報表正確反映。

#### Scenario: Confirm payment without a bank account

- **WHEN** 使用者確認收款但沒有指定銀行帳戶
- **THEN** 系統產生收據並將收款列入 `PENDING_DEPOSIT`，不建立銀行入帳交易

#### Scenario: Allocate payment to multiple invoices

- **WHEN** 使用者將已確認收款分配至多張同 organization 的未結清發票，且總分配額不超過收款與發票餘額
- **THEN** 系統保存所有分配並更新發票應收狀態，未分配部分仍可在收款資料中追蹤

### Requirement: Autocomplete suggestions are organization-scoped and editable

事由與備註 SHALL 各自支援依 organization、欄位類型、啟用狀態與關鍵字查詢的建議值。使用者選取建議後 MUST 能修改最終文字；自訂文字可直接保存，不得強制新增建議值。

#### Scenario: Search and edit a reason suggestion

- **WHEN** 使用者在收款表單輸入事由關鍵字並選取建議後修改文字
- **THEN** 系統只顯示目前 organization 的 active 事由建議，並保存修改後的文字而非未修改的建議值

#### Scenario: Other organization suggestions are hidden

- **WHEN** 使用者查詢事由或備註建議
- **THEN** 系統不回傳其他 organization 的建議資料

### Requirement: Receipt output is a fixed one-page three-copy A4 document

系統 SHALL 提供 A4 portrait 一頁三聯收據輸出；存根聯、客戶收據聯與會計聯 MUST 共用同一收據編號，且分類、事由、備註、金額、幣別、付款方式與銀行帳戶資訊一致。備註為空時可隱藏備註欄，但不得造成跨頁。

#### Scenario: Print a three-copy receipt

- **WHEN** 使用者列印已確認收款
- **THEN** 系統輸出固定 A4 portrait 的三聯文件，三聯內容一致且僅聯別標籤不同

#### Scenario: Long optional note stays on one page

- **WHEN** 收款包含較長選填備註並要求列印
- **THEN** 系統依模板限制或適當截斷/換行，使三聯仍在同一 A4 頁面，不產生第二頁

### Requirement: Payment voiding preserves financial history

系統 SHALL 允許依規則將已確認收款作廢，但不得刪除收款、重用收據編號或直接刪除已產生的銀行交易；作廢結果 MUST 可被報表排除並保留稽核紀錄。

#### Scenario: Void a posted payment

- **WHEN** `ADMIN` 作廢已入帳收款
- **THEN** 系統將收款標記為 `VOIDED`，保留原收據與稽核資料，並由銀行流程建立可追溯的反向處理
