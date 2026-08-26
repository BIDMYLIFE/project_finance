## 1. Backend contract and validation

- [x] 1.1 Add request and response DTOs for creating one payment from one or more invoice IDs.
- [x] 1.2 Add the payment API endpoint while preserving the existing manual payment endpoint.
- [x] 1.3 Add organization-scoped invoice lookup with locking for the association transaction.
- [x] 1.4 Validate at least one invoice, eligible status, positive balance, same customer, same currency, and valid payment amount.
- [x] 1.5 Validate the required active bank account belongs to the organization and matches the invoice currency.

## 2. Transactional payment association

- [x] 2.1 Implement a service transaction that creates one Payment linked to the invoice customer.
- [x] 2.2 Default payer, currency, and amount from the selected invoices while allowing a lower partial-payment amount.
- [x] 2.3 Create one PaymentAllocation per selected invoice and update invoice paid totals and statuses.
- [x] 2.4 Create the bank CREDIT transaction and mark the new payment as POSTED immediately.
- [x] 2.5 Ensure validation or persistence failures roll back payment, allocations, invoice updates, and bank transaction together.

## 3. Frontend workflow

- [x] 3.1 Add API modules for loading eligible invoices and creating an invoice-linked payment.
- [x] 3.2 Add a separate "由發票建立付款" action that keeps manual payment creation available.
- [x] 3.3 Add invoice multi-selection with same-customer and same-currency filtering.
- [x] 3.4 Auto-fill payer, customer context, currency, and outstanding-balance total; allow reducing the amount for partial payment.
- [x] 3.5 Require a matching bank account and show success, validation, loading, and failure states.

## 4. Verification and documentation

- [x] 4.1 Add service and API tests for one invoice, multiple invoices, mixed customer/currency rejection, partial payment, and missing bank account.
- [x] 4.2 Add tests confirming existing manual payments remain available and unchanged.
- [x] 4.3 Verify organization isolation, transaction rollback, and concurrent invoice updates.
- [x] 4.4 Run formatting, static checks, tests, frontend syntax checks, and OpenSpec validation.
