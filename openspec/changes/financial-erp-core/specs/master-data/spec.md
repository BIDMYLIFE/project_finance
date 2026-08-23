## Purpose

提供 organization-scoped 的客戶與產品/服務主檔，讓報價、發票、收款與報表使用一致且可追溯的基礎資料。

## ADDED Requirements

### Requirement: Customer records are organization-scoped

系統 SHALL 允許 `ADMIN` 建立、查詢、更新與停用所屬 organization 的客戶資料。客戶至少 MUST 包含客戶名稱、組織歸屬、啟用狀態與可追蹤的識別碼；同一 organization 內的客戶識別碼 MUST 唯一。

#### Scenario: Create a customer in the current organization

- **WHEN** 已驗證的 `ADMIN` 提交有效客戶資料
- **THEN** 系統建立歸屬目前 organization 的 active 客戶，並回傳不暴露內部敏感欄位的 DTO

#### Scenario: Cross-organization customer access is denied

- **WHEN** 使用者以路徑、query 或 request body 指定其他 organization 的客戶
- **THEN** 系統拒絕請求或回傳不存在結果，且不讀取、不修改該客戶

#### Scenario: Inactive customer cannot be used for new documents

- **WHEN** 使用者嘗試將已停用客戶指定為新的報價或發票客戶
- **THEN** 系統拒絕該操作並指出客戶不可用，既有文件仍保留原客戶關聯

### Requirement: Product and service records include pricing and tax settings

系統 SHALL 允許 `ADMIN` 維護所屬 organization 的產品或服務，至少保存名稱、識別碼、單位價格、幣別、稅務設定與啟用狀態。價格 MUST 為非負數，識別碼 MUST 在 organization 內唯一；停用品項不得加入新報價或發票。

#### Scenario: Create a priced product

- **WHEN** 使用者提交有效名稱、唯一識別碼、非負單價、幣別與稅務設定
- **THEN** 系統建立 active 產品並可在同 organization 的文件明細選取

#### Scenario: Invalid product price is rejected

- **WHEN** 使用者提交負單價或不支援的幣別
- **THEN** 系統回傳欄位驗證錯誤，且不建立或修改產品

### Requirement: Master-data lists support safe organization-scoped search

客戶與產品/服務清單 SHALL 支援 organization scope、啟用狀態、關鍵字、分頁與排序；未指定分頁時系統 MUST 使用有上限的預設頁面大小，不得一次回傳不受限制的資料量。

#### Scenario: Search active products

- **WHEN** 使用者以關鍵字查詢目前 organization 的 active 產品
- **THEN** 系統只回傳符合 organization、狀態與關鍵字條件的分頁結果，並提供一致的分頁欄位
