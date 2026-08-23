## Why

目前專案只有財務系統探索文件，尚未有可執行的客戶、產品、報價、發票、收款、銀行帳務與報表流程。現在建立 organization 隔離的財務核心，能讓 ERP 從報價一路追蹤至發票、收款、銀行入帳與管理報表，並將收據與待入帳等實際作業納入同一套可稽核模型。

## What Changes

- 新增 organization-scoped 的客戶與產品/服務主檔，支援幣別、單價、稅務設定、啟用狀態與基本搜尋。
- 新增報價單與發票流程，包含明細、稅額、總額、應收餘額、組織內唯一編號，以及草稿、送出、接受、拒絕、過期、已發出、部分付款、已付款、逾期與取消等狀態。
- 新增收款流程：每筆確認收款必須有且只有一個分類，可保存可修改的事由與選填備註，並支援一筆收款分配至多張發票。
- 新增收款待入帳狀態；確認收款時產生不可重用的唯一收據編號，重印沿用原編號，並提供固定 A4 直式一頁三聯收據。
- 新增事由與備註的 organization-scoped Autocomplete 建議，保留使用者最後確認的文字，不強制把自訂文字變成建議值。
- 新增多銀行帳戶與現金流 Credit/Debit 交易，支援收款入帳、費用出帳、帳戶轉帳、待入帳轉正式入帳與換帳戶時保留反向/新交易歷史。
- 新增待入帳、收款分類、銀行餘額、發票狀態、應收帳款帳齡、費用、稅務與 ERP 收支摘要報表，支援組織、日期、客戶、分類、帳戶、幣別與狀態篩選及 CSV 匯出。
- 新增 Vue 3、Bootstrap 5.3、Axios 與 SweetAlert2 的離線操作頁面，統一處理 loading、成功、失敗、空資料、網路錯誤、RWD 與鍵盤操作狀態。
- 所有跨資料異動由 Service transaction 管理，保留收款、銀行異動、收據重印與文件狀態變更所需的稽核軌跡。
- 本 change 以前一個 `auth-jwt-admin-bootstrap` change 提供的登入、JWT、`ADMIN` 授權與 organization context 為前置安全邊界，不重複改寫身份驗證契約。

## Capabilities

### New Capabilities

- `master-data`: organization-scoped 客戶與產品/服務維護、稅務設定、啟用狀態與查詢。
- `sales-documents`: 報價單與發票的明細、編號、金額計算、狀態流程與應收餘額。
- `payment-receipts`: 收款分類、事由、備註、Autocomplete、發票分配、待入帳、唯一收據與 A4 三聯列印。
- `banking`: 銀行帳戶、Credit/Debit 交易、收款入帳、費用出帳、轉帳、沖銷與帳戶異動。
- `erp-reporting`: 財務報表、共同篩選/排序/分頁、來源追溯、總計與 CSV 匯出。

### Modified Capabilities

<!-- No existing main specs are present; this change introduces the financial capabilities. -->

## Impact

- Spring Boot 的 entity、DTO、Repository、Service、MVC Controller、Web API Controller、集中例外處理與 organization scope enforcement。
- Microsoft SQL Server migration、foreign keys、unique constraints、索引、金額/幣別欄位、文件序列、收據序列、稽核紀錄與銀行交易歷史。
- 客戶、產品、報價、發票、收款、收據、銀行、費用與報表 API 契約；所有資料須由已驗證的 `ADMIN` organization context 限制範圍。
- Vue 3 離線靜態資源、Bootstrap 5.3 RWD 表單與列表、收據列印模板及 CSV 匯出流程；不得新增 CDN 或外部 runtime 資源。
- 與 `auth-jwt-admin-bootstrap` 的整合：受保護路由、organization context、錯誤回應格式及稽核 actor；兩個 change 不共享或提交任何實際 JWT secret、資料庫密碼或其他 credentials。
- 測試範圍包含金額/稅額計算、文件狀態、收款分配、待入帳、收據唯一性、銀行餘額與反向交易、報表總計、organization isolation、API 與瀏覽器 RWD/列印驗證。