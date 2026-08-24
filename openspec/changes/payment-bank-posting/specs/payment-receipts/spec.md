## Purpose

提供 organization-scoped、可稽核的收款與收據流程，讓每筆收款能被正確分類、分配至發票、追蹤待入帳狀態，並以不可重用的收據編號輸出紙本憑證。

## ADDED Requirements

### Requirement: Payments require one active category and a reason

系統 SHALL 要求確認收款恰有一個目前 organization 的 active 收款分類與一個非空事由；備註 SHALL 為選填。收款 MUST 保存付款人、收款日期、正數金額、幣別、付款方式、分類、事由與備註的最終確認內容。

#### Scenario: Confirm a valid categorized payment

- **WHEN** `ADMIN` 提交有效付款人、日期、正數金額、幣別、付款方式、恰好一個 active 分類與非空事由，可選填備註
- **THEN** 系統保存收款及上述確認內容，並回傳收款識別、狀態與摘要

#### Scenario: Reject invalid category or reason

- **WHEN** 收款缺少事由、指定不存在/停用分類、指定多個分類、金額非正數，或分類不屬於目前 organization
- **THEN** 系統回傳可辨識的驗證或業務錯誤，不建立或確認收款

### Requirement: Confirmation creates a non-reusable receipt and deposit state

確認收款 SHALL 在同一 organization 與收據年度範圍內產生唯一且不可重用的收據編號。沒有指定銀行帳戶時，收款 MUST 進入 `PENDING_DEPOSIT`；指定同 organization、幣別相容且 active 的銀行帳戶時，收款 MUST 與銀行入帳結果一致地成為 `POSTED`。收據編號不得因作廢而回收。

#### Scenario: Confirm payment without an account

- **WHEN** `ADMIN` 確認有效收款且未指定銀行帳戶
- **THEN** 系統在同一交易建立收款與唯一收據編號，狀態為 `PENDING_DEPOSIT`，且不產生銀行入帳交易

#### Scenario: Confirm payment into an active account

- **WHEN** `ADMIN` 確認有效收款並指定相容的 active 銀行帳戶
- **THEN** 系統在同一交易建立收款、唯一收據編號與對應銀行 `CREDIT`，並回傳 `POSTED` 狀態

#### Scenario: Receipt sequence remains unique under concurrency

- **WHEN** 同一 organization 的多筆收款同時確認
- **THEN** 每筆成功收款取得不同收據編號，失敗交易不會留下重複收款或可再次使用的編號

### Requirement: Payments support bounded multi-invoice allocation

系統 SHALL 允許已確認收款分配至一張或多張同 organization 的未結清發票。所有分配金額總和 MUST 不超過收款金額或各發票當時未結清餘額；系統 MUST 保存每筆分配並可回傳已分配與未分配金額。

#### Scenario: Allocate one payment to multiple invoices

- **WHEN** `ADMIN` 將已確認收款分配至多張同 organization 且仍有餘額的發票，且總額不超過所有可用餘額
- **THEN** 系統保存每筆分配，回傳分配總額與未分配餘額，並更新發票收款狀態

#### Scenario: Reject an over-allocation atomically

- **WHEN** 任一分配使收款總分配超過付款金額、發票未結清餘額，或發票不屬於目前 organization
- **THEN** 系統拒絕整個操作，不留下部分分配，也不改變原收款與發票餘額

### Requirement: Reason and note suggestions are scoped and editable

事由與備註 SHALL 各自支援依目前 organization、欄位類型、active 狀態與關鍵字查詢的有限筆數建議。使用者選取建議後 MUST 能修改最終文字；自訂文字可直接保存，不得自動改寫成其他建議值或強制新增建議。

#### Scenario: Search and edit a suggestion

- **WHEN** `ADMIN` 查詢事由或備註建議、選取一筆後修改文字並確認收款
- **THEN** 系統只提供符合欄位類型與 organization 的 active 建議，並保存修改後的最終文字

#### Scenario: Hide another organization's suggestion

- **WHEN** 使用者以關鍵字查詢建議值
- **THEN** 回應不包含其他 organization 的建議、不接受用 request body 偽造 organization 來取得建議

### Requirement: Receipts print as one-page three-copy A4 output

系統 SHALL 提供已確認收款的 A4 portrait 一頁三聯收據輸出。存根聯、客戶收據聯與會計聯 MUST 共用收據編號、收款快照、分類、事由、備註、金額、幣別、付款方式與銀行帳戶資訊，且僅聯別標籤不同。重印 MUST 建立列印紀錄但不得建立新收款或新收據編號。

#### Scenario: Print a receipt with three consistent copies

- **WHEN** `ADMIN` 列印一筆已確認收款
- **THEN** 系統輸出固定 A4 portrait 的單頁三聯文件，三聯的收款資料一致並共用原收據編號

#### Scenario: Reprint keeps the original receipt

- **WHEN** `ADMIN` 再次要求列印同一筆收款
- **THEN** 系統新增可稽核的列印紀錄，輸出原收據快照與原編號，不新增付款或編號

#### Scenario: Long note does not create a second page

- **WHEN** 收款包含較長選填備註並要求列印
- **THEN** 系統依固定模板限制或換行規則輸出，三聯仍位於同一 A4 頁面

### Requirement: Voiding preserves payment and receipt history

系統 SHALL 允許依業務規則作廢已確認收款，但不得刪除收款、收據、分配或已建立的銀行交易，也不得回收收據編號。作廢 MUST 保留 actor、時間、原因與原始資料摘要，並使有效收款查詢排除該筆。

#### Scenario: Void a pending payment

- **WHEN** `ADMIN` 作廢一筆 `PENDING_DEPOSIT` 收款並提供作廢原因
- **THEN** 系統將收款標記為 `VOIDED`，保留原收據與稽核紀錄，且不建立銀行交易

#### Scenario: Void a posted payment

- **WHEN** `ADMIN` 作廢一筆 `POSTED` 收款並提供作廢原因
- **THEN** 系統保留收款、原收據與原銀行交易，依銀行更正流程建立可追溯的反向處理，且有效總計不再計入原收款
