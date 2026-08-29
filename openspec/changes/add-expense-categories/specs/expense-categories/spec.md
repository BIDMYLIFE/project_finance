## Purpose

提供可由每個 organization 獨立維護的費用分類主檔，讓費用輸入與報表能使用一致、可追溯且可停用的分類資料。

## ADDED Requirements

### Requirement: Organization-scoped expense categories

系統 SHALL 允許已驗證的 `ADMIN` 使用者管理所屬 organization 的費用分類。每個分類 MUST 具有唯一識別碼、名稱、active 狀態與建立時間；不同 organization 的資料 MUST 完全隔離。

#### Scenario: Create an expense category

- **WHEN** organization 內的 `ADMIN` 提交有效且未重複的分類名稱
- **THEN** 系統建立一筆啟用中的費用分類，並回傳其 DTO 資料

#### Scenario: Reject duplicate category name

- **WHEN** `ADMIN` 提交同一 organization 已存在的分類名稱（忽略前後空白後比較）
- **THEN** 系統拒絕建立或更新，回傳一致的欄位錯誤，且不改變既有資料

#### Scenario: Isolate categories between organizations

- **WHEN** 使用者查詢、修改或停用不屬於目前 organization 的費用分類 ID
- **THEN** 系統以資源不存在或等效的安全錯誤回應，且不洩露該分類資料

### Requirement: Validate and query expense categories

系統 SHALL 拒絕空白或超過 100 字元的分類名稱，並提供依 active 狀態及關鍵字查詢費用分類的能力。清單查詢 MUST 支援一致的排序與有上限的分頁，不得一次回傳不受限制的資料量。

#### Scenario: List active categories for expense entry

- **WHEN** `ADMIN` 查詢目前 organization 的費用分類並指定 active filter
- **THEN** 系統只回傳符合 organization scope、關鍵字、active filter、排序及分頁條件的 DTO 清單

#### Scenario: Reject invalid category name

- **WHEN** `ADMIN` 建立或更新分類時提交空白或超長名稱
- **THEN** 系統回傳驗證錯誤，且不建立或修改分類

### Requirement: Deactivate categories without destroying history

系統 SHALL 以停用取代刪除費用分類。停用分類 MUST 不得被新的費用選用，但既有費用仍 MUST 保留分類關聯並可查詢；一般 API 不得刪除分類資料。

#### Scenario: Deactivate an unused category

- **WHEN** `ADMIN` 停用目前 organization 的費用分類
- **THEN** 系統將分類標記為 inactive，後續新增或確認費用不得選用該分類

#### Scenario: Preserve category on historical expense

- **WHEN** 已有費用使用某分類後，`ADMIN` 停用該分類並查詢該費用
- **THEN** 系統保留費用的分類關聯與分類資料，且費用歷史仍可被查詢

### Requirement: Protect category management endpoints

系統 SHALL 要求費用分類管理 API 具備有效身份驗證與 `ADMIN` 角色。未登入或非 `ADMIN` 請求 MUST 被拒絕，且錯誤回應不得暴露內部例外資訊。

#### Scenario: Reject unauthenticated category management

- **WHEN** 未登入使用者呼叫費用分類管理 API
- **THEN** 系統回傳未授權錯誤且不存取或修改費用分類

#### Scenario: Reject non-admin category management

- **WHEN** 已登入但不具 `ADMIN` 角色的使用者呼叫費用分類管理 API
- **THEN** 系統回傳禁止存取錯誤且不存取或修改費用分類
