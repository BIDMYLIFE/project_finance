## Purpose

提供 organization-scoped、口徑一致且可追溯的 ERP 報表，讓管理者能從收款、發票、銀行交易、費用與稅務來源取得可分頁的明細、可重算的摘要與一致的 CSV 匯出。

## ADDED Requirements

### Requirement: Reports provide bounded filters, pagination, and consistent response data

所有報表 SHALL 支援適用的 organization、日期區間、客戶、收款分類、銀行帳戶、幣別與狀態篩選，並支援明確的排序、分頁、總筆數、明細 rows、summary、applied filters 與使用中的 date basis。未指定日期時，系統 MUST 使用明確且有限制的預設期間或要求使用者提供日期範圍；不得執行無界的大量查詢。

#### Scenario: Query a filtered report

- **WHEN** `ADMIN` 以有效日期區間、幣別、狀態與分頁條件查詢報表
- **THEN** 系統只回傳目前 organization 且符合所有條件的 rows，並以一致格式回傳 summary、pagination、applied filters 與 date basis

#### Scenario: Reject an invalid or unbounded query

- **WHEN** 使用者提交反向日期區間、超過上限的 page size、無效排序欄位，或未提供必要日期且沒有可用預設期間
- **THEN** 系統回傳可辨識的驗證錯誤，不執行無界查詢

#### Scenario: Return a usable empty result

- **WHEN** 有效查詢條件沒有符合資料
- **THEN** 系統回傳成功的空 rows、零值 summary、完整 pagination 與可供前端顯示的 empty state，不回傳錯誤

### Requirement: Pending-deposit and payment-category reports use receipt data

待入帳報表 SHALL 以 `received_at` 作為日期基準，顯示收據編號、付款人、分類、事由、備註、金額、幣別、付款方式與收款日期。收款分類報表 SHALL 依期間、客戶、分類與幣別提供有效收款筆數、金額與可追溯明細；`VOIDED` 收款 MUST 排除有效總計，但可透過狀態篩選追溯。

#### Scenario: Show pending deposits

- **WHEN** `ADMIN` 查詢指定收款日期範圍的待入帳報表
- **THEN** 系統只列出目前 organization 的 `PENDING_DEPOSIT` 收款，並包含原收據與收款欄位

#### Scenario: Group valid payments by category

- **WHEN** `ADMIN` 查詢指定期間、客戶、分類與幣別的收款分類報表
- **THEN** 系統依分類回傳有效收款筆數與金額，且明細合計可重算相同 summary

#### Scenario: Exclude voided payments from effective totals

- **WHEN** 查詢收款分類報表的預設有效結果，且來源包含已作廢收款
- **THEN** 作廢收款不計入有效筆數與金額，但在明確要求 `VOIDED` 狀態時仍可被追溯

### Requirement: Bank, invoice, receivable, expense, tax, and cash-flow reports use defined source dates

系統 SHALL 提供銀行餘額、發票狀態、應收帳款帳齡、費用、稅務與 ERP 收支摘要報表。銀行報表 SHALL 以 `transaction_date` 提供期初餘額、有效 Credit、有效 Debit、淨變動、期末餘額與明細；發票狀態 SHALL 以 `issue_date` 或明確指定狀態日期統計；帳齡 SHALL 以 `due_date` 分為未到期、1-30、31-60、61 天以上；費用 SHALL 以 `expense_date` 統計；稅務 SHALL 以文件日期統計；ERP 收支摘要 SHALL 依已入帳收款 Credit 與費用 Debit 計算收入、支出與淨額。

#### Scenario: Calculate a bank balance report

- **WHEN** `ADMIN` 以帳戶、幣別與交易日期區間查詢銀行餘額報表
- **THEN** 系統回傳期初餘額、有效 Credit 總額、有效 Debit 總額、淨變動、期末餘額與可追溯的交易 rows

#### Scenario: Produce invoice status and aging buckets

- **WHEN** `ADMIN` 以指定基準日查詢發票狀態或應收帳款帳齡
- **THEN** 系統依發票狀態、到期日與有效未結清餘額分組，回傳未到期、1-30、31-60、61 天以上的筆數與金額，且已付款、取消或作廢資料不重複計入

#### Scenario: Summarize expenses, tax, and ERP cash flow

- **WHEN** `ADMIN` 查詢指定文件/費用期間的費用、稅務或 ERP 收支摘要
- **THEN** 系統依對應日期基準與有效狀態回傳分類金額、稅率/稅額或已入帳 Credit/Debit 與淨額，並提供來源 rows

### Requirement: Effective totals exclude cancellation, void, and reversal duplicates

報表的預設有效結果 MUST 排除取消、作廢與已沖銷來源，且反向交易不得與原交易重複計入。若使用者以狀態條件要求查看歷史，系統 SHALL 顯示其狀態與原始/反向關聯，不得將歷史追溯結果誤當成有效總計。

#### Scenario: Exclude a reversed bank transaction

- **WHEN** 銀行餘額或 ERP 收支摘要包含原始交易及其反向交易
- **THEN** 系統依有效交易鏈計算一次正確結果，不重複增加或扣減餘額

#### Scenario: Trace a cancelled or voided source

- **WHEN** `ADMIN` 以歷史狀態篩選查詢取消發票或作廢收款
- **THEN** 系統回傳該來源的狀態、原因與關聯摘要，但有效 summary 不包含該來源

### Requirement: Report rows trace to same-organization source records

每一筆報表明細 SHALL 能追溯至來源發票、收款、費用或銀行交易，summary MUST 能在相同 filters、date basis 與有效狀態下由 rows 重算。來源開啟或詳情查詢 SHALL 再次套用 authenticated organization context，不得以 request 中的 organization id 取代安全範圍。

#### Scenario: Open a report source

- **WHEN** `ADMIN` 從報表明細要求查看來源資料
- **THEN** 系統只開啟同 organization 的對應發票、收款、費用或銀行交易，並保留來源識別與狀態

#### Scenario: Prevent cross-organization report leakage

- **WHEN** 使用者嘗試以 query、path 或 body 的 organization id 查詢、匯出或開啟另一 organization 的報表來源
- **THEN** 系統拒絕或回傳不洩漏資料的資源錯誤，不在 rows、summary、filters 或來源詳情中包含其他 organization 內容

### Requirement: CSV exports preserve the visible report meaning

系統 SHALL 支援報表 CSV 匯出，匯出資料集 MUST 與相同查詢條件下的畫面 rows、summary 與有效狀態一致。CSV MUST 包含欄位標題、明細、總計、套用的篩選條件、date basis、幣別與產生時間；匯出失敗時不得產生不完整且看似成功的檔案。

#### Scenario: Export a filtered report

- **WHEN** `ADMIN` 匯出已套用期間、狀態、幣別與分頁/排序條件的報表
- **THEN** 系統產生可下載 CSV，內容與相同查詢的報表資料一致，並保留查詢 metadata 與 summary

#### Scenario: Fail an export without a partial file

- **WHEN** 報表查詢或 CSV 產生過程失敗
- **THEN** 系統回傳一致的錯誤，不提供內容不完整的成功檔案或下載連結

### Requirement: Report pages are usable offline and responsively

報表頁面 SHALL 透過專案提供的本地資源運作，不得依賴 CDN 或外部 runtime 資源；桌面與手機畫面 MUST 支援鍵盤操作、明確 focus、loading、成功、驗證錯誤、網路錯誤、空資料、disabled 與重試狀態。報表表格與篩選控制不得在支援的 viewport 互相遮蔽或造成不可讀內容。

#### Scenario: Use a report page without external resources

- **WHEN** `ADMIN` 在無外部網路的環境開啟報表頁並查詢或匯出
- **THEN** 頁面使用本地資源完成主要操作，並能顯示 loading、結果或錯誤狀態，不嘗試載入 CDN

#### Scenario: Navigate report controls by keyboard on mobile

- **WHEN** 使用者在手機尺寸以鍵盤或輔助操作方式瀏覽篩選、表格明細與匯出控制
- **THEN** 所有可操作控制具可見 focus 與可理解名稱，內容維持可讀且不互相重疊
