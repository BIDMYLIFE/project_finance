## Why

目前財務交易資料即使完成收款、銀行、發票與費用流程，管理者仍缺少一致的 ERP 查詢與匯出方式，難以快速掌握待入帳、應收帳款、銀行現金流與營運結果。現在獨立建立報表 capability，可讓所有摘要都從來源資料重算並保留追溯，避免畫面、匯出與交易資料各自產生不同口徑。

## What Changes

- 新增 organization-scoped 報表查詢，統一支援日期區間、客戶、收款分類、銀行帳戶、幣別、狀態、排序與有上限的分頁。
- 新增待入帳與收款分類報表，顯示收據、付款人、分類、事由、備註、金額、幣別與收款日期，並提供有效筆數與金額統計。
- 新增銀行餘額、發票狀態、應收帳款帳齡、費用、稅務與 ERP 收支摘要報表，依各自定義的日期基準與有效狀態計算。
- 報表列必須追溯至來源發票、收款、費用或銀行交易；取消、作廢與沖銷資料不得重複計入有效總計。
- 新增一致的 rows、summary、pagination、applied filters 與 empty-state API 回應，以及報表明細來源查詢。
- 新增 CSV、PDF 與 XLSX 匯出，確保匯出資料集與畫面相同，並保留篩選條件、日期基準、幣別、欄位標題、總計與產生時間。
- 新增 Vue 3、Bootstrap 5.3、Axios 與 SweetAlert2 的離線報表頁面，提供 RWD、鍵盤操作、載入/錯誤/空資料與匯出狀態。
- 將報表整合至 Dashboard，提供報表入口與核心財務摘要卡片，並可從摘要導向對應報表。

本 change 僅提供讀取、彙總與匯出，不建立、修改或刪除收款、銀行、發票、費用及其他財務來源資料；不包含外部銀行同步、自動對帳或完整複式簿記。

## Capabilities

### New Capabilities

- `erp-reporting`: 統一 ERP 報表查詢、財務摘要、來源追溯、分頁篩選、多格式匯出與 Dashboard 整合。

### Modified Capabilities

<!-- No existing main specs are present; this change introduces the reporting capability. -->

## Impact

- Spring Boot report query Service/Repository、DTO、Web API Controller、分離的 MVC page Controller 與集中錯誤處理。
- 報表查詢需要讀取 organization、customer、invoice、payment、payment allocation、payment category、bank account、bank transaction、expense 與稅務來源資料，但不改變其交易生命週期。
- API 需沿用已驗證 `ADMIN` principal、organization context、分頁契約與錯誤格式，並防止任何 organization id 參數繞過隔離。
- Vue 3 JavaScript、Bootstrap 5.3、Axios、SweetAlert2 與本地字型/圖示資源；不得新增 CDN 或外部 runtime 依賴。PDF/XLSX 產生依賴必須由後端建置提供，且不可依賴執行時外部服務。
- 依賴 `auth-jwt-admin-bootstrap`、財務主檔/銷售文件、`payment-bank-posting` 及後續費用資料契約；不在本 change 重做這些來源模組。
