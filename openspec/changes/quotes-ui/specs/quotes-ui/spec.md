## Purpose

提供登入後可操作的報價管理流程，讓使用者能在 organization 範圍內建立、查詢、編輯與推進報價文件，並以清楚的金額、有效期限與狀態資訊支援日常銷售作業。

## ADDED Requirements

### Requirement: Quote list is available from authenticated navigation

登入後 Dashboard SHALL 將報價 capability 顯示為可用，並提供指向 `/quotes` 的連結。未登入使用者不得透過頁面或 API 繞過既有認證與 organization scope。

#### Scenario: Open quotes from dashboard

- **WHEN** 已登入使用者在 Dashboard 選擇報價
- **THEN** 系統導向 `/quotes`，並顯示報價管理頁面而非 unavailable 或 disabled 狀態

### Requirement: Users can query organization-scoped quotes

報價頁面 SHALL 支援依報價編號或客戶名稱關鍵字、報價狀態與分頁查詢。結果 MUST 僅包含目前 organization 的報價，並提供客戶、報價編號、建立日期、有效期限、幣別、總額與狀態。

#### Scenario: Filter quote list

- **WHEN** 使用者輸入關鍵字或選擇狀態後執行搜尋
- **THEN** 系統回傳符合條件且屬於目前 organization 的報價，並保留可操作的分頁資訊

#### Scenario: Quote list has no matches

- **WHEN** 查詢條件沒有符合的報價
- **THEN** 頁面顯示明確的空資料狀態，不顯示過期或上一個查詢的資料

### Requirement: Users can create valid quote drafts

使用者 SHALL 能建立報價草稿，且請求 MUST 包含 active customer、至少一筆 active product 明細、正數數量、非負折扣、有效幣別、有效稅率與有效期限。系統 MUST 保存商品名稱、描述、單價與稅率快照，並計算每筆明細與文件的小計、稅額及總額。

#### Scenario: Create a valid draft

- **WHEN** 使用者提交含 active 客戶、有效商品明細與未過期有效期限的報價
- **THEN** 系統建立 `DRAFT` 報價，保存明細快照，並回傳可供清單顯示的計算結果

#### Scenario: Reject invalid draft input

- **WHEN** 請求缺少客戶、明細，或含 inactive/跨 organization 資料、非正數數量、負折扣、無效稅率或無效期限
- **THEN** 系統回傳驗證或業務錯誤，不保存報價或部分明細

### Requirement: Draft quotes can be edited without changing their identity

系統 SHALL 允許編輯 `DRAFT` 報價的客戶、有效期限、幣別與明細，並重新計算金額。報價編號與 organization identity MUST 保持不變；非草稿狀態不得修改報價內容。

#### Scenario: Edit a draft quote

- **WHEN** 使用者提交有效內容更新既有 `DRAFT` 報價
- **THEN** 系統以同一報價識別資料保存新內容、重新計算金額，並回傳更新後的明細與總額

#### Scenario: Reject editing a submitted quote

- **WHEN** 使用者嘗試修改 `SENT`、`ACCEPTED`、`REJECTED`、`EXPIRED` 或 `CANCELLED` 報價
- **THEN** 系統拒絕操作，且原內容、金額與狀態保持不變

### Requirement: Quote lifecycle actions follow controlled transitions

系統 SHALL 支援 `DRAFT`、`SENT`、`ACCEPTED`、`REJECTED`、`EXPIRED` 與 `CANCELLED` 狀態，並只允許合法的狀態轉換。頁面 SHALL 依目前狀態顯示可用操作；取消 MUST 是狀態變更，不得 hard delete。

#### Scenario: Submit a draft quote

- **WHEN** 使用者對有效 `DRAFT` 報價執行送出
- **THEN** 系統將狀態轉為 `SENT`，並禁止再編輯其內容

#### Scenario: Accept or reject a sent quote

- **WHEN** 使用者對 `SENT` 報價執行接受或拒絕
- **THEN** 系統分別將狀態轉為 `ACCEPTED` 或 `REJECTED`，並不修改保存的金額快照

#### Scenario: Cancel an eligible quote

- **WHEN** 使用者對仍可取消的報價確認取消
- **THEN** 系統將狀態轉為 `CANCELLED`，報價仍可查詢但不再出現在可繼續處理的清單中

#### Scenario: Reject an invalid lifecycle transition

- **WHEN** 使用者對已接受、已拒絕、已過期或已取消的報價執行不允許的狀態操作
- **THEN** 系統回傳一致的業務錯誤，且不改變報價狀態或內容

### Requirement: Quote UI handles operational states accessibly

報價頁面 SHALL 使用既有本地前端資源與本地化訊息，並呈現 loading、成功、欄位驗證錯誤、網路錯誤、空資料、disabled 與 retry 狀態。清單、明細表單、狀態操作與確認訊息 MUST 支援鍵盤操作與窄螢幕閱讀，不得依賴外部 CDN。

#### Scenario: Save quote with validation failure

- **WHEN** 使用者提交缺少必要欄位或無效明細的表單
- **THEN** 頁面標示對應欄位與通用錯誤，保留使用者輸入，且不送出無效保存結果

#### Scenario: Recover from a quote list network failure

- **WHEN** 報價清單請求發生網路錯誤
- **THEN** 頁面顯示本地化錯誤與重試操作，並在重試期間避免重複請求
