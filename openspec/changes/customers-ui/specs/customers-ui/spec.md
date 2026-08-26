## Purpose

提供登入後的客戶資料維護工作區，讓具備存取權限的使用者能以可搜尋、可分頁且適合桌面與行動裝置的介面查看並維護客戶狀態。

## ADDED Requirements

### Requirement: Customer list can be queried

系統 SHALL 在登入後的 `/customers` 頁面提供客戶清單，並以名稱關鍵字、啟用狀態與頁碼查詢客戶資料。

#### Scenario: Load the default customer list

- **WHEN** 使用者進入 `/customers`
- **THEN** 系統以啟用客戶為預設條件載入第一頁，並顯示客戶欄位、目前頁碼與可用的分頁控制項

#### Scenario: Search customers by name

- **WHEN** 使用者輸入名稱關鍵字並送出查詢
- **THEN** 系統只顯示符合關鍵字的結果，且從第一頁開始呈現

#### Scenario: Filter by active status

- **WHEN** 使用者切換啟用或停用篩選
- **THEN** 系統以所選狀態重新查詢並顯示對應客戶，且保留與結果一致的分頁資訊

#### Scenario: Navigate server pages

- **WHEN** 使用者選擇下一頁、上一頁或指定頁碼
- **THEN** 系統向伺服器要求所選頁面並更新清單，不以目前頁面資料推算未載入的結果

#### Scenario: No matching customers

- **WHEN** 查詢成功但沒有任何符合條件的客戶
- **THEN** 系統顯示明確的空資料狀態，且不顯示誤導性的資料列

### Requirement: Customer records can be created and edited

系統 SHALL 讓使用者透過表單新增客戶或編輯既有客戶，並在送出前檢查必要欄位與格式。

#### Scenario: Create a valid customer

- **WHEN** 使用者填入通過驗證的客戶資料並送出新增表單
- **THEN** 系統建立客戶、顯示成功結果、關閉或重設表單，並重新整理目前查詢條件下的清單

#### Scenario: Edit a valid customer

- **WHEN** 使用者修改既有客戶資料並送出通過驗證的表單
- **THEN** 系統更新該客戶、顯示成功結果，並重新整理目前查詢條件下的清單

#### Scenario: Reject invalid form input

- **WHEN** 使用者送出缺少必要資料或格式不正確的表單
- **THEN** 系統在表單上指出錯誤、保留使用者輸入，且不送出建立或更新請求

### Requirement: Customer records can be soft-deactivated

系統 SHALL 提供停用操作，並在執行前要求使用者確認；停用不得從資料來源實體刪除客戶。

#### Scenario: Confirm deactivation

- **WHEN** 使用者選擇啟用客戶的停用操作並確認
- **THEN** 系統執行停用、顯示成功結果，並從啟用客戶清單移除或重新整理該資料

#### Scenario: Cancel deactivation

- **WHEN** 使用者選擇停用操作但取消確認
- **THEN** 系統不執行停用請求，客戶資料與清單保持不變

### Requirement: UI handles request states and access boundaries

系統 SHALL 對資料請求提供載入、失敗與重試狀態，並要求使用者先完成登入才能使用客戶管理頁面。

#### Scenario: Show loading state

- **WHEN** 客戶清單或表單請求尚未完成
- **THEN** 系統顯示載入狀態並避免重複提交或與過期資料互動

#### Scenario: Handle API failure

- **WHEN** 清單、新增、更新或停用請求失敗
- **THEN** 系統以不暴露內部例外細節的訊息告知使用者，恢復可操作狀態，並提供清單重新載入或表單修正的路徑

#### Scenario: Require authentication

- **WHEN** 未登入使用者直接存取 `/customers`
- **THEN** 系統依既有登入保護流程拒絕或導向登入，且不呈現客戶資料

### Requirement: Customer management is usable across locales and viewport sizes

系統 SHALL 提供中英文文字與錯誤訊息，並在桌面與行動裝置尺寸下維持可讀、可操作且不互相遮蔽的版面。

#### Scenario: Switch interface locale

- **WHEN** 使用者在支援的中英文語系環境中開啟客戶頁面
- **THEN** 頁面標題、欄位、按鈕、狀態、驗證、錯誤與確認文字使用目前語系，且不得以缺少鍵值的原始識別字取代

#### Scenario: Use the page on a narrow viewport

- **WHEN** 使用者以行動裝置寬度瀏覽或操作清單與表單
- **THEN** 內容可閱讀、控制項不重疊，且清單、分頁與表單仍可完成主要操作
