## 1. Backend contract and transaction behavior

- [x] 1.1 Verify the posting API requires `bankAccountId` and preserves organization, active-account, and currency validation.
- [x] 1.2 Verify successful posting persists `payments.bank_account_id` and creates exactly one traceable bank CREDIT transaction.
- [x] 1.3 Add service/API tests for missing, cross-organization, inactive, and currency-mismatched bank accounts, including unchanged pending state.

## 2. Frontend bank-account selection

- [x] 2.1 Load active bank accounts for the current organization and filter choices by payment currency.
- [x] 2.2 Replace implicit first-account selection with an explicit required bank-account selection dialog.
- [x] 2.3 Keep cancel, loading, success, empty-compatible-account, and error states consistent with existing payment UI patterns.
- [x] 2.4 Display the selected bank account in the payment list/detail view where applicable.

## 3. Verification and documentation

- [x] 3.1 Add or update offline asset/UI contract tests for the bank-account selection flow.
- [x] 3.2 Run formatting, JavaScript syntax checks, Maven tests, and build verification.
- [x] 3.3 Document that creating a payment may remain pending, while posting requires an explicit compatible bank account.
