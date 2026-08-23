## Purpose

提供可篩選、可分頁、可追溯的財務與 ERP 報表，讓管理者能從收款、發票、費用與銀行交易取得一致的營運摘要與匯出資料。

## ADDED Requirements

### Requirement: Reports provide consistent filters and pagination

所有 ERP 報表 SHALL 支援 organization、日期區間、客戶、收款分類、銀行帳戶、幣別與狀態等適用篩選，並支援排序、分頁、總筆數與空資料狀態。未指定日期時，系統 MUST 使用明確且有限制的預設期間或要求使用者提供日期範圍。

#### Scenario: Filter a report by organization and period

- **WHEN** `ADMIN` 查詢指定期間與條件的報表
- **THEN** 系統只回傳目前 organization 且符合所有條件的結果，並以一致格式提供分頁與總計欄位

#### Scenario: Empty report returns a usable state

- **WHEN** 查詢條件沒有符合資料
- **THEN** 系統回傳成功的空資料結果、零總計與可供前端顯示的空狀態，不回傳錯誤或未限制的大查詢

### Requirement: Pending-deposit and payment reports reflect receipt data

待入帳報表 SHALL 顯示收據編號、付款人、分類、事由、備註、金額、幣別、付款方式與收款日期；收款分類報表 SHALL 依期間、客戶、分類與幣別提供筆數及金額統計。作廢收款 MUST 排除於有效總計之外，但仍可透過狀態篩選追溯。

#### Scenario: Show pending deposits

- **WHEN** `ADMIN` 查詢指定收款日期範圍的待入帳報表
- **THEN** 系統只列出目前 organization 的 `PENDING_DEPOSIT` 收款，並包含原收據與收款欄位

#### Scenario: Group payments by category

- **WHEN** `ADMIN` 查詢收款分類報表
- **THEN** 系統按分類提供有效收款筆數與金額，並遵守日期、幣別與 organization 篩選

### Requirement: Financial reports calculate receivable and bank values from source records

系統 SHALL 提供銀行餘額、發票狀態、應收帳款帳齡、費用、稅務與 ERP 收支摘要報表。銀行餘額 MUST 依有效 Credit/Debit 交易計算；應收帳款 MUST 依發票到期日與有效應收餘額分為未到期、1-30、31-60、61 天以上；ERP 收支摘要 MUST 以已入帳收款 Credit 與費用 Debit 為基礎。

#### Scenario: Calculate receivable aging

- **WHEN** `ADMIN` 以指定基準日查詢應收帳款帳齡
- **THEN** 系統依發票到期日與未結清餘額分組，且已付款、取消或作廢資料不重複計入

#### Scenario: Summarize bank cash flow

- **WHEN** `ADMIN` 查詢指定期間的 ERP 收支摘要
- **THEN** 系統回傳有效收款 Credit、費用 Debit、淨額與來源追溯資訊，並排除沖銷交易的重複計算

### Requirement: Report rows are traceable to source documents

報表每一筆明細 SHALL 能追溯至來源發票、收款、費用或銀行交易；彙總結果 MUST 可由明細在相同篩選條件下重算。報表不得洩漏其他 organization 的資料。

#### Scenario: Open a source record from a report row

- **WHEN** 使用者從報表明細要求查看來源
- **THEN** 系統只開啟同 organization 的對應來源文件或交易，且來源不存在或不屬於目前 organization 時拒絕存取

### Requirement: CSV export preserves report meaning

系統 SHALL 支援報表 CSV 匯出，匯出內容 MUST 保留套用的篩選條件、日期基準、幣別、欄位標題、明細/總計與產生時間；匯出失敗時不得產生不完整且看似成功的檔案。

#### Scenario: Export a filtered report

- **WHEN** `ADMIN` 匯出已套用期間、狀態與幣別篩選的報表
- **THEN** 系統產生可下載的 CSV，其資料集與畫面查詢一致，並包含查詢條件與總計資訊

#### Scenario: Export does not include another organization

- **WHEN** 使用者匯出報表
- **THEN** CSV 只包含目前 organization 的資料，且不含其他 organization 的識別或明細
