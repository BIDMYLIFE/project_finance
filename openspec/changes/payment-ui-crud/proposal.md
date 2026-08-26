## Why

目前專案已有付款、收款分類、付款分配與收據資料表及既有領域規格，但使用者尚無法透過網頁建立、查詢或確認收款。現在補上付款管理入口，可讓已完成的發票流程真正支援收款與應收帳款管理。

## What Changes

- 新增 organization-scoped 收款分類 CRUD，支援啟用與停用。
- 新增付款清單、詳細資料與建立／確認收款流程，保存付款人、日期、金額、幣別、付款方式、事由與備註。
- 支援付款分配至同 organization 的未結清發票，並驗證分配金額與幣別。
- 支援 `PENDING_DEPOSIT`、`POSTED` 與 `VOIDED` 等付款狀態及對應操作。
- 新增 REST API、MVC 頁面 Controller、Vue 3／Bootstrap／Axios／SweetAlert2 離線 UI 與一致的 loading、錯誤及空資料狀態。
- 提供收據檢視／列印入口，沿用既有收據編號與付款快照，不重複建立付款。
- 將 dashboard 的付款 capability 更新為可用，連結至 `/payments`。

## Capabilities

### New Capabilities

- `payment-receipts`: 收款分類、收款 CRUD、確認、發票分配、狀態管理與收據操作。

### Modified Capabilities

<!-- No existing main capability specs are being modified. The existing
     payment-bank-posting change supplies the broader posting and banking
     domain rules that this UI consumes. -->

## Impact

- 新增付款與收款分類的 DTO、Repository、Service、MVC/API Controller 及測試。
- 新增 `/payments` 頁面、API service、Vue page script、CSS 與繁體中文訊息。
- 更新 dashboard capability registry、付款頁面路由與導航測試。
- 沿用既有 `payments`、`payment_categories`、`payment_allocations`、`receipt_prints`、`invoices` 與銀行帳戶資料，不新增外部服務或 CDN。
- 付款入帳與銀行交易規則依賴既有 `payment-bank-posting` 領域設計；本 change 不實作外部支付或銀行同步。
