## Why

目前財務系統已完成收款與銀行入帳的探索，但尚未有可執行、可稽核的交易流程。先交付這段核心流程，才能讓收款從確認、開立收據、待入帳到銀行入帳形成一致的財務事實，並支援後續發票分配與報表查詢。

## What Changes

- 新增 organization-scoped 的收款分類、收款建立與確認流程，確認時必須保存付款人、日期、金額、幣別、付款方式、事由與備註快照。
- 確認收款時產生 organization/年度內唯一且不可重用的收據編號；支援收據重印與固定 A4 直式一頁三聯輸出。
- 支援收款分配至多張同 organization 的未結清發票，並追蹤已分配與未分配金額。
- 沒有銀行帳戶的確認收款進入 `PENDING_DEPOSIT`；指定相容的 active 帳戶後建立 `CREDIT` 並轉為 `POSTED`。
- 新增 organization-scoped 銀行帳戶與 append-only `CREDIT`/`DEBIT` 交易，提供餘額重算、來源追溯與待入帳處理。
- 支援已入帳收款換帳戶、銀行交易更正與收款作廢；以反向/沖銷交易保留歷史，不刪除財務事實或回收收據編號。
- 提供收款、收據、待入帳與銀行入帳所需的 Spring MVC/Web API 分層、DTO 驗證、交易邊界、稽核紀錄與離線 Vue 操作頁面。

## Capabilities

### New Capabilities

- `payment-receipts`: 收款分類、確認收款、發票分配、待入帳、唯一收據、重印與 A4 三聯收據。
- `banking`: organization-scoped 銀行帳戶、Credit/Debit 入帳、餘額、待入帳轉正式入帳與不可破壞更正。

### Modified Capabilities

<!-- No existing main specs are present; this change introduces the focused capabilities. -->

## Impact

- Spring Boot entity、DTO、Repository、Service、MVC Controller、Web API Controller、集中例外處理、organization context 與 audit service。
- Microsoft SQL Server migrations、收款/收據/分配/銀行帳戶/銀行交易/反向交易資料表、序列、外鍵、唯一條件與索引。
- 收款、收據、待入帳、銀行帳戶、銀行交易、換帳戶與作廢 API；所有查詢與異動須受已驗證的 `ADMIN` organization scope 保護。
- Vue 3 JavaScript、Bootstrap 5.3、Axios、SweetAlert2 與收據列印頁；所有 runtime 資源必須離線可用，不新增 CDN。
- 依賴 `auth-jwt-admin-bootstrap` 提供登入、JWT、`ADMIN` 授權與 organization context；發票資料沿用既有 sales document 契約，不在本 change 重做發票流程。

本 change 不包含外部銀行或第三方支付同步、銀行自動對帳、完整複式簿記、費用模組、ERP 報表或 XLSX/PDF 報表匯出。