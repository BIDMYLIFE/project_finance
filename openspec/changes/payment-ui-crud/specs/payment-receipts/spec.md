## Purpose

提供組織管理員可操作的收款與收款分類流程，讓付款資料能安全連結發票、追蹤入帳狀態並產生可稽核的收據。

## ADDED Requirements

### Requirement: Payment categories are organization-scoped and manageable

系統 SHALL 提供目前 organization 的收款分類清單、新增、改名與停用功能。分類名稱 MUST 非空且在同一 organization 內唯一；停用分類不得再被新的收款使用，但既有收款仍須保留原分類。

#### Scenario: Create and deactivate a payment category
- **WHEN** 管理員提交有效且未重複的分類名稱
- **THEN** 系統建立目前 organization 的 active 分類，並可在後續停用該分類

#### Scenario: Reject cross-organization category access
- **WHEN** 管理員以另一 organization 的分類識別碼查詢或修改分類
- **THEN** 系統拒絕請求且不得洩露或修改該分類

### Requirement: Users can create and confirm categorized payments

系統 SHALL 支援建立與查詢 organization-scoped 收款，收款 MUST 保存付款人、收款日期、正數金額、幣別、付款方式、目前 organization 的 active 分類、非空事由及選填備註。確認收款時 MUST 產生 organization/年度內唯一且不可重用的收據編號。

#### Scenario: Confirm a valid payment
- **WHEN** 管理員提交有效付款資料、恰好一個 active 分類與非空事由
- **THEN** 系統建立已確認收款，保存確認時的資料快照並回傳收款摘要與收據編號

#### Scenario: Reject invalid payment input
- **WHEN** 金額非正數、幣別不支援、分類停用、事由空白或必要欄位缺漏
- **THEN** 系統回傳可理解的驗證錯誤，不建立收款或收據

### Requirement: Payments can be allocated to open invoices

系統 SHALL 支援一筆收款分配至多張目前 organization 的已開立且未結清發票。每筆分配 MUST 使用相同幣別、不得超過收款未分配金額或發票未結清餘額；任一筆分配失敗時整體操作 MUST 回滾。

#### Scenario: Allocate a payment across invoices
- **WHEN** 管理員提交多筆合法且總額未超限的發票分配
- **THEN** 系統保存全部分配並更新付款未分配摘要與各發票付款餘額

#### Scenario: Reject over-allocation or foreign organization invoice
- **WHEN** 任一分配超過付款或發票餘額、幣別不符，或發票不屬於目前 organization
- **THEN** 系統拒絕整個操作，不留下部分分配且不改變原餘額

### Requirement: Payment status and deposit actions preserve financial history

系統 SHALL 支援 `PENDING_DEPOSIT`、`POSTED` 與 `VOIDED` 狀態。沒有相容 active 銀行帳戶的確認收款 SHALL 進入待入帳；指定相容帳戶後建立可追溯的 CREDIT 銀行交易並轉為已入帳。作廢或換帳戶 MUST 保留原付款與收據歷史，不得 hard delete 或重用收據編號。

#### Scenario: Post a pending payment
- **WHEN** 管理員指定同 organization 且幣別相容的 active 銀行帳戶
- **THEN** 系統建立一筆可追溯的 CREDIT 交易並將收款轉為 `POSTED`

#### Scenario: Void a confirmed payment
- **WHEN** 管理員作廢一筆可作廢收款
- **THEN** 系統保留原收款、分配與收據紀錄，並將其標為 `VOIDED`，不產生第二個收據編號

### Requirement: Payments provide receipt viewing and reprinting

系統 SHALL 提供已確認收款的收據檢視與固定 A4 直式一頁三聯列印。三聯 MUST 共用原收據編號與付款快照；重印 MUST 建立列印紀錄但不得建立新收款或新收據編號。

#### Scenario: Print a payment receipt
- **WHEN** 管理員要求列印已確認收款
- **THEN** 系統輸出包含付款人、分類、事由、備註、金額、幣別、付款方式與銀行帳戶資訊的三聯收據

### Requirement: Payment management has an offline responsive UI and dashboard entry

系統 SHALL 提供 `/payments` 頁面與 `/api/v1/payments` 資源導向 API。頁面 MUST 支援繁體中文、RWD、loading、成功、空資料、驗證錯誤與網路錯誤狀態，且所有 runtime 第三方資源 MUST 由專案本地提供。Dashboard SHALL 將 payments capability 標示為可用並連結至 `/payments`。

#### Scenario: Open payment management from dashboard
- **WHEN** 管理員在 dashboard 選取付款管理
- **THEN** 系統導向 `/payments` 並顯示目前 organization 的付款資料

#### Scenario: Use payment UI without external assets
- **WHEN** 使用者在無外部網路環境載入付款頁
- **THEN** 頁面仍能載入本地 UI 資源並呈現主要查詢與付款操作
