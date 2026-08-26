## Purpose

提供組織內管理員建立、編輯、開立與追蹤發票的完整銷售文件入口，讓發票金額、明細快照、狀態與應收餘額能被安全且一致地維護。

## ADDED Requirements

### Requirement: Invoice drafts contain validated customer, dates, currency, and line snapshots

系統 SHALL 允許已驗證的管理員建立發票草稿。草稿 MUST 指定目前 organization 內的 active 客戶、幣別、發票日期、到期日與至少一筆明細；每筆明細 MUST 保存產品名稱、說明、數量、單價、折扣、稅率與計算後金額快照。草稿資料不得接受其他 organization 的客戶或產品。

#### Scenario: Create a valid invoice draft

- **WHEN** 管理員提交同 organization 的 active 客戶、有效日期、支援幣別與至少一筆有效明細
- **THEN** 系統建立 `DRAFT` 發票，保存明細快照與一致的金額結果，並回傳安全的發票 DTO

#### Scenario: Reject invalid draft references or dates

- **WHEN** 請求包含 foreign/inactive 客戶或產品、空明細、無效日期、負數數量/折扣或幣別不相容
- **THEN** 系統回傳標準驗證或業務錯誤，且不保存部分發票或明細

### Requirement: Invoice totals use fixed precision and preserved snapshots

系統 SHALL 依數量、快照單價、折扣與稅率計算每筆明細小計、文件小計、稅額、總額、已付金額與應收餘額。計算 MUST 使用固定四位小數與一致的四捨五入規則；建立或更新發票後，產品主檔的後續變更不得改寫既有發票明細快照。

#### Scenario: Calculate invoice totals

- **WHEN** 發票包含有效數量、單價、折扣與稅率明細
- **THEN** 回應中的總額等於明細小計加稅額，初始已付金額為零且應收餘額等於總額

#### Scenario: Reject inconsistent or negative totals

- **WHEN** 折扣超過明細金額、數量或稅率為負數，或計算結果不符合固定精度規則
- **THEN** 系統拒絕整份請求，不建立或更新發票

### Requirement: Drafts support controlled editing and invoice lifecycle

系統 SHALL 支援 `DRAFT`、`ISSUED`、`PARTIALLY_PAID`、`PAID`、`OVERDUE` 與 `CANCELLED` 狀態。只有 `DRAFT` 可編輯；草稿可被開立或取消，已開立文件不得被 hard delete 或任意改寫。開立後系統 MUST 產生 organization/年度內唯一且不可重用的發票編號。

#### Scenario: Update a draft

- **WHEN** 管理員修改仍為 `DRAFT` 的發票且提交有效內容
- **THEN** 系統更新發票與明細快照並重新計算金額

#### Scenario: Issue a draft with a unique number

- **WHEN** 管理員確認有效的發票草稿
- **THEN** 系統在同一 transaction 內產生唯一發票編號、保存文件快照並轉為 `ISSUED`

#### Scenario: Reject editing or deleting issued history

- **WHEN** 使用者嘗試編輯、hard delete 或重複開立已發出或已取消的發票
- **THEN** 系統拒絕操作並保留原文件與編號

### Requirement: Invoice queries expose receivable status and organization isolation

系統 SHALL 提供目前 organization 的分頁發票清單與單筆明細查詢，支援關鍵字、狀態與日期篩選，並回傳客戶、發票編號、日期、幣別、總額、已付金額、應收餘額與有效狀態。已發出且仍有餘額、目前日期晚於到期日的發票 MUST 呈現為 `OVERDUE`；已付款或取消者不得標為逾期。

#### Scenario: List and inspect invoices

- **WHEN** 管理員以分頁與篩選條件查詢發票
- **THEN** 系統只回傳目前 organization 內符合條件的安全資料，並可查詢單筆明細與明細快照

#### Scenario: Calculate overdue display status

- **WHEN** 已開立發票有應收餘額且目前日期晚於到期日
- **THEN** 清單與明細呈現 `OVERDUE`；已付款或取消發票維持其有效狀態

### Requirement: Invoice management has a responsive offline UI and Dashboard entry point

系統 SHALL 提供 `/invoices` 響應式管理頁面，包含草稿建立/編輯、清單、明細、開立、取消、loading、empty、validation、network error、成功提示與鍵盤可操作控制。Dashboard SHALL 將 invoices capability 設為可用並連結至 `/invoices`。頁面只能使用專案內或 WebJar 打包的本地資源。

#### Scenario: Open invoices from Dashboard

- **WHEN** 已驗證使用者從 Dashboard 選擇 Invoices
- **THEN** 瀏覽器導向受保護的 `/invoices` 頁面，而非未定義路由

#### Scenario: Use invoice form on mobile

- **WHEN** 管理員在窄版 viewport 建立或編輯發票
- **THEN** 客戶、日期、明細、金額、操作按鈕與錯誤訊息保持可讀、可聚焦，且不產生水平溢位

#### Scenario: Render without external network assets

- **WHEN** 外部網路被封鎖而使用者開啟發票頁面
- **THEN** 頁面與其 API 操作仍使用本地資源及專案後端正常呈現
