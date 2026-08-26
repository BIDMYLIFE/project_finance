## Context

目前 `payments.bank_account_id` 已透過外鍵連結 `bank_accounts.id`，`PaymentService` 也會在入帳時驗證 organization、active 狀態與幣別，並建立對應的銀行 CREDIT 交易。前端目前需要將帳戶選擇明確化，避免自動選取第一個帳戶。

## Goals / Non-Goals

**Goals:**

- 讓入帳操作明確取得管理員選定的銀行帳戶。
- 讓前端選項與後端可接受的帳戶條件一致。
- 保持既有待入帳、銀行交易與 organization isolation 行為。

**Non-Goals:**

- 不改變資料表結構或將銀行帳戶欄位改為建立付款時的必填欄位。
- 不新增銀行同步、對帳或自動分配帳戶功能。

## Decisions

- 使用現有銀行帳戶清單 API 載入啟用帳戶，前端依付款幣別過濾選項；選擇視窗使用既有 SweetAlert2，不新增 UI 套件。
- 入帳 API 仍由後端接受 `bankAccountId` 並執行最終驗證；前端驗證只改善操作體驗，不能取代安全邊界。
- 選擇成功後沿用現有 `PaymentService.post` transaction，由 service 更新付款關聯並建立 CREDIT 交易；取消或驗證失敗時不寫入任何變更。
- 保留建立付款時未指定帳戶的 `PENDING_DEPOSIT` 狀態，因為待入帳本身代表尚未決定實際銀行帳戶。

## Risks / Trade-offs

- [Risk] 帳戶在選擇後、API 執行前被停用 → [Mitigation] 後端再次查詢並驗證 active、organization 與幣別，拒絕無效請求。
- [Risk] 使用者有多個同幣別帳戶而誤選 → [Mitigation] 選單顯示帳戶名稱與幣別，並要求確認後才執行入帳。
- [Risk] 沒有相容帳戶時無法入帳 → [Mitigation] 顯示明確錯誤，維持付款為待入帳，讓使用者先建立或啟用相容帳戶。

## Migration Plan

不需資料庫 migration。部署前端互動與既有付款 API 相容的變更，部署後驗證待入帳付款的選帳、取消、錯誤與成功入帳流程；回滾時恢復原前端互動，不刪除既有付款或銀行交易。
