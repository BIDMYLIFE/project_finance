## Context

目前資料庫只有 `payment_categories`，`expenses` 雖已預留但尚未有分類主檔、Entity 或服務流程。現有專案使用 SQL Server migration、UUID 主鍵、organization scope、DTO API 與 `Controller -> Service -> Repository` 分層；本設計延續這些既有約束。

## Goals / Non-Goals

**Goals:**

- 建立獨立的 `SOUTHWND.expense_categories` 主檔與 organization-scoped CRUD/停用流程。
- 讓 Expense workflow 可以只選用 active 且同 organization 的分類。
- 保留歷史費用的分類參照，並提供 bounded、可篩選的查詢。

**Non-Goals:**

- 不在本 change 建立完整 `Expense` entity 或費用確認/作廢流程。
- 不處理銀行 `DEBIT` 入帳、稅務分類、階層式分類或跨 organization 共用分類。

## Decisions

1. **使用獨立 expense_categories 表。** 收款分類與費用分類的報表語意不同；共用 `payment_categories` 會造成名稱、active 狀態及未來權限互相耦合。替代方案是增加 category type 欄位，但會改動既有收款契約，故不採用。
2. **只停用、不刪除。** 分類可能被歷史費用引用，使用 `active` 保留來源與報表追溯能力。API 提供 deactivate，不提供一般 delete。
3. **以 organization + name 建立唯一性。** 名稱在寫入前 trim，資料庫以 `(organization_id, name)` unique constraint 作最後保護；服務層將 unique violation 轉成一致的欄位錯誤。名稱大小寫策略沿用 SQL Server database collation，不另造 normalized name 欄位。
4. **分類關聯先以 ID 保留。** `expenses.category_id` 應在後續 expense migration 加上外鍵；停用不影響既有費用，而建立/確認費用由 Expense Service 驗證分類仍 active。分類 API 不直接依賴 Expense entity，避免反向跨層。
5. **查詢沿用共用 DTO、分頁與 organization context。** Controller 只解析輸入並呼叫 Service；Repository 負責 scope、active、keyword、排序與 page query；回應沿用專案既有成功/錯誤與 pagination contract。

## Risks / Trade-offs

- [Risk] 既有 database collation 對大小寫與重音的比較規則可能與使用者預期不同 → [Mitigation] 先沿用資料庫一致性規則，並以 integration test 固定實際行為；若日後需要嚴格規則再引入 normalized key migration。
- [Risk] 分類停用後，歷史清單若只查 active 可能顯示無法辨識的資料 → [Mitigation] Expense detail/report 保留分類名稱快照或允許讀取 inactive reference；本 change 先保留分類 row，不做 hard delete。
- [Risk] 只有名稱沒有 code，日後整合或匯入時穩定識別能力較弱 → [Mitigation] MVP 先以 UUID 作內部識別；若需要會計科目或外部匯入，再另立 category code 規格，不在本 change 偷加欄位。

## Migration Plan

1. 部署新增 `expense_categories` migration 與唯一索引。
2. 部署分類 Entity、Repository、Service、DTO/API 及測試。
3. 後續 Expense migration 將 `expenses.category_id` 連至分類表；既有資料若存在，先完成資料回填再加 NOT NULL/foreign key。
4. 回滾時先停止使用分類 API，再移除應用程式版本；資料庫 migration 是否回退依部署備份策略處理，不刪除已被財務資料引用的分類。
