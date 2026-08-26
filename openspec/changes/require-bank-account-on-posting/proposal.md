## Why

付款從「待入帳」轉為「已入帳」時，使用者必須明確指定實際入帳的銀行帳戶，才能確保 `payments.bank_account_id` 與銀行 CREDIT 交易正確對應。目前若由介面自動挑選帳戶，容易造成款項入錯帳戶且難以稽核。

## What Changes

- 修改待入帳付款的入帳操作，要求使用者先選擇銀行帳戶。
- 選擇清單只提供目前 organization 內、啟用中且幣別相符的銀行帳戶。
- 未選擇銀行帳戶時不得執行入帳 API；取消選擇不改變付款狀態。
- 入帳成功後保存 `payments.bank_account_id`，並建立對應的銀行 CREDIT 交易。
- 保留待入帳付款可暫不指定帳戶的既有流程。

## Capabilities

### New Capabilities

- `payment-bank-selection`: 待入帳付款轉為已入帳時的銀行帳戶選擇與關聯要求。

### Modified Capabilities

<!-- No root capability spec exists in this repository; the focused behavior is captured as a new capability delta. -->

## Impact

- 付款頁面的 Vue 入帳互動、SweetAlert2 選擇視窗與本地化訊息。
- 付款 MVC 頁面的訊息傳遞與付款清單/入帳 UI。
- 既有 `PaymentService.post`、`payments.bank_account_id` 外鍵與 `bank_transactions` 建立流程；不新增資料表或外部依賴。
- 需補充付款入帳的 UI 資產驗證與 Service/API 測試，確認 organization、active 與幣別隔離。
