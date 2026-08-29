CREATE INDEX ix_reports_invoices_org_date_status ON SOUTHWND.invoices(organization_id, invoice_date, status, currency_code, customer_id);
CREATE INDEX ix_reports_invoices_org_due_status ON SOUTHWND.invoices(organization_id, due_date, status, currency_code, customer_id);
CREATE INDEX ix_reports_invoice_lines_invoice_tax ON SOUTHWND.invoice_lines(invoice_id, tax_rate);
CREATE INDEX ix_reports_payments_org_received ON SOUTHWND.payments(organization_id, received_at, status, currency_code, category_id, customer_id);
CREATE INDEX ix_reports_expenses_org_date ON SOUTHWND.expenses(organization_id, expense_date, status, currency_code, category_id, bank_account_id);
CREATE INDEX ix_reports_bank_transactions_org_date ON SOUTHWND.bank_transactions(organization_id, transaction_date, status, currency_code, bank_account_id, direction);
