## Purpose

確保待入帳付款轉為已入帳時，使用者明確選擇正確的銀行帳戶，讓付款資料與銀行 CREDIT 交易保持可追溯的一致關聯。

## ADDED Requirements

### Requirement: Posting requires an explicit compatible bank account

系統 SHALL 要求管理員在待入帳付款轉為已入帳前，明確選擇目前 organization 內啟用中且幣別與付款相同的銀行帳戶。未選擇帳戶、帳戶停用、帳戶不屬於目前 organization 或幣別不符時，系統 MUST 拒絕入帳且不得改變付款狀態。

#### Scenario: Select a bank account before posting
- **WHEN** 管理員選擇同 organization、啟用中且幣別相符的銀行帳戶並確認待入帳付款
- **THEN** 系統將 `bank_account_id` 設為所選帳戶，建立一筆可追溯的銀行 CREDIT 交易，並將付款狀態改為 `POSTED`

#### Scenario: Reject posting without a selection
- **WHEN** 管理員未選擇銀行帳戶或取消銀行帳戶選擇
- **THEN** 系統不得呼叫或完成入帳，付款仍維持 `PENDING_DEPOSIT`

#### Scenario: Reject an incompatible bank account
- **WHEN** 管理員提交停用、跨 organization 或幣別不相符的銀行帳戶
- **THEN** 系統回傳可理解的錯誤，付款仍維持 `PENDING_DEPOSIT`，且不建立銀行 CREDIT 交易

### Requirement: Bank account choices are limited to valid accounts

系統 SHALL 在入帳選擇介面只列出目前 organization 內啟用中且幣別相符的銀行帳戶，並顯示足以辨識帳戶的名稱與幣別。

#### Scenario: Filter bank account choices by currency and active state
- **WHEN** 管理員開啟待入帳付款的入帳操作
- **THEN** 系統只顯示符合付款幣別且 active 的銀行帳戶，不顯示停用或其他幣別帳戶
