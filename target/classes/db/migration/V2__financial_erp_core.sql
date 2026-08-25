CREATE TABLE SOUTHWND.customers (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_customers PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    customer_code NVARCHAR(80) NOT NULL,
    name NVARCHAR(200) NOT NULL,
    email NVARCHAR(320) NULL,
    phone NVARCHAR(50) NULL,
    active BIT NOT NULL CONSTRAINT df_customers_active DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    updated_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_customers_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_customers_organization_code UNIQUE (organization_id, customer_code)
);
CREATE INDEX ix_customers_scope_active ON SOUTHWND.customers(organization_id, active, name);

CREATE TABLE SOUTHWND.products (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_products PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    product_code NVARCHAR(80) NOT NULL,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(1000) NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    tax_rate DECIMAL(9,4) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_products_active DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    updated_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_products_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_products_organization_code UNIQUE (organization_id, product_code),
    CONSTRAINT ck_products_price CHECK (unit_price >= 0),
    CONSTRAINT ck_products_tax_rate CHECK (tax_rate >= 0 AND tax_rate <= 100)
);
CREATE INDEX ix_products_scope_active ON SOUTHWND.products(organization_id, active, name);

CREATE TABLE SOUTHWND.payment_categories (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_payment_categories PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(100) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_payment_categories_active DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_payment_categories_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_payment_categories_name UNIQUE (organization_id, name)
);

CREATE TABLE SOUTHWND.bank_accounts (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_bank_accounts PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    account_name NVARCHAR(200) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    opening_balance DECIMAL(19,4) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_bank_accounts_active DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_bank_accounts_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_bank_accounts_name UNIQUE (organization_id, account_name)
);

CREATE TABLE SOUTHWND.document_sequences (
    organization_id UNIQUEIDENTIFIER NOT NULL,
    sequence_year INT NOT NULL,
    document_type NVARCHAR(30) NOT NULL,
    next_value INT NOT NULL,
    CONSTRAINT pk_document_sequences PRIMARY KEY (organization_id, sequence_year, document_type),
    CONSTRAINT fk_document_sequences_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT ck_document_sequences_next_value CHECK (next_value > 0)
);

CREATE TABLE SOUTHWND.audit_logs (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_audit_logs PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    actor_id UNIQUEIDENTIFIER NULL,
    action_name NVARCHAR(80) NOT NULL,
    entity_type NVARCHAR(80) NOT NULL,
    entity_id UNIQUEIDENTIFIER NULL,
    summary NVARCHAR(1000) NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_audit_logs_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_id) REFERENCES SOUTHWND.users(id)
);
CREATE INDEX ix_audit_logs_scope_time ON SOUTHWND.audit_logs(organization_id, created_at);

CREATE TABLE SOUTHWND.quotes (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_quotes PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    customer_id UNIQUEIDENTIFIER NOT NULL,
    quote_number NVARCHAR(40) NULL,
    status NVARCHAR(20) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    subtotal DECIMAL(19,4) NOT NULL,
    tax_total DECIMAL(19,4) NOT NULL,
    grand_total DECIMAL(19,4) NOT NULL,
    valid_until DATE NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_quotes_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_quotes_customer FOREIGN KEY (customer_id) REFERENCES SOUTHWND.customers(id),
    CONSTRAINT uq_quotes_number UNIQUE (organization_id, quote_number)
);
CREATE TABLE SOUTHWND.quote_lines (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_quote_lines PRIMARY KEY,
    quote_id UNIQUEIDENTIFIER NOT NULL,
    product_id UNIQUEIDENTIFIER NOT NULL,
    product_name NVARCHAR(200) NOT NULL,
    description NVARCHAR(1000) NULL,
    quantity DECIMAL(19,4) NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    discount DECIMAL(19,4) NOT NULL,
    tax_rate DECIMAL(9,4) NOT NULL,
    line_total DECIMAL(19,4) NOT NULL,
    CONSTRAINT fk_quote_lines_quote FOREIGN KEY (quote_id) REFERENCES SOUTHWND.quotes(id),
    CONSTRAINT fk_quote_lines_product FOREIGN KEY (product_id) REFERENCES SOUTHWND.products(id)
);

CREATE TABLE SOUTHWND.invoices (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_invoices PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    customer_id UNIQUEIDENTIFIER NOT NULL,
    source_quote_id UNIQUEIDENTIFIER NULL,
    invoice_number NVARCHAR(40) NULL,
    status NVARCHAR(20) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(19,4) NOT NULL,
    tax_total DECIMAL(19,4) NOT NULL,
    grand_total DECIMAL(19,4) NOT NULL,
    paid_total DECIMAL(19,4) NOT NULL,
    balance_due DECIMAL(19,4) NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_invoices_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES SOUTHWND.customers(id),
    CONSTRAINT fk_invoices_quote FOREIGN KEY (source_quote_id) REFERENCES SOUTHWND.quotes(id),
    CONSTRAINT uq_invoices_number UNIQUE (organization_id, invoice_number)
);
CREATE TABLE SOUTHWND.invoice_lines (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_invoice_lines PRIMARY KEY,
    invoice_id UNIQUEIDENTIFIER NOT NULL,
    product_id UNIQUEIDENTIFIER NULL,
    product_name NVARCHAR(200) NOT NULL,
    description NVARCHAR(1000) NULL,
    quantity DECIMAL(19,4) NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    discount DECIMAL(19,4) NOT NULL,
    tax_rate DECIMAL(9,4) NOT NULL,
    line_total DECIMAL(19,4) NOT NULL,
    CONSTRAINT fk_invoice_lines_invoice FOREIGN KEY (invoice_id) REFERENCES SOUTHWND.invoices(id),
    CONSTRAINT fk_invoice_lines_product FOREIGN KEY (product_id) REFERENCES SOUTHWND.products(id)
);

CREATE TABLE SOUTHWND.payments (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_payments PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    customer_id UNIQUEIDENTIFIER NULL,
    category_id UNIQUEIDENTIFIER NOT NULL,
    bank_account_id UNIQUEIDENTIFIER NULL,
    actor_id UNIQUEIDENTIFIER NULL,
    receipt_number NVARCHAR(40) NOT NULL,
    payer_name NVARCHAR(200) NOT NULL,
    reason NVARCHAR(500) NOT NULL,
    note NVARCHAR(1000) NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    payment_method NVARCHAR(30) NOT NULL,
    received_at DATE NOT NULL,
    status NVARCHAR(20) NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_payments_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_payments_customer FOREIGN KEY (customer_id) REFERENCES SOUTHWND.customers(id),
    CONSTRAINT fk_payments_category FOREIGN KEY (category_id) REFERENCES SOUTHWND.payment_categories(id),
    CONSTRAINT fk_payments_bank_account FOREIGN KEY (bank_account_id) REFERENCES SOUTHWND.bank_accounts(id),
    CONSTRAINT fk_payments_actor FOREIGN KEY (actor_id) REFERENCES SOUTHWND.users(id),
    CONSTRAINT uq_payments_receipt UNIQUE (organization_id, receipt_number),
    CONSTRAINT ck_payments_amount CHECK (amount > 0),
    CONSTRAINT ck_payments_status CHECK (status IN ('PENDING_DEPOSIT', 'POSTED', 'VOIDED')),
    CONSTRAINT ck_payments_method CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'CARD', 'OTHER'))
);
CREATE INDEX ix_payments_scope_status_date ON SOUTHWND.payments(organization_id, status, received_at, category_id);
CREATE TABLE SOUTHWND.payment_allocations (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_payment_allocations PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    payment_id UNIQUEIDENTIFIER NOT NULL,
    invoice_id UNIQUEIDENTIFIER NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    CONSTRAINT fk_payment_allocations_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_payment_allocations_payment FOREIGN KEY (payment_id) REFERENCES SOUTHWND.payments(id),
    CONSTRAINT fk_payment_allocations_invoice FOREIGN KEY (invoice_id) REFERENCES SOUTHWND.invoices(id),
    CONSTRAINT ck_payment_allocations_amount CHECK (amount > 0)
);
CREATE TABLE SOUTHWND.receipt_prints (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_receipt_prints PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    payment_id UNIQUEIDENTIFIER NOT NULL,
    printed_at DATETIME2(6) NOT NULL,
    actor_id UNIQUEIDENTIFIER NULL,
    CONSTRAINT fk_receipt_prints_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_receipt_prints_payment FOREIGN KEY (payment_id) REFERENCES SOUTHWND.payments(id),
    CONSTRAINT fk_receipt_prints_actor FOREIGN KEY (actor_id) REFERENCES SOUTHWND.users(id)
);

CREATE TABLE SOUTHWND.expenses (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_expenses PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    bank_account_id UNIQUEIDENTIFIER NULL,
    description NVARCHAR(500) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_expenses_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_expenses_bank_account FOREIGN KEY (bank_account_id) REFERENCES SOUTHWND.bank_accounts(id),
    CONSTRAINT ck_expenses_amount CHECK (amount > 0)
);
CREATE TABLE SOUTHWND.bank_transactions (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_bank_transactions PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    bank_account_id UNIQUEIDENTIFIER NOT NULL,
    direction NVARCHAR(10) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    transaction_date DATE NOT NULL,
    source_type NVARCHAR(40) NOT NULL,
    source_id UNIQUEIDENTIFIER NULL,
    transfer_reference UNIQUEIDENTIFIER NULL,
    reversal_of_id UNIQUEIDENTIFIER NULL,
    status NVARCHAR(20) NOT NULL,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_bank_transactions_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT fk_bank_transactions_account FOREIGN KEY (bank_account_id) REFERENCES SOUTHWND.bank_accounts(id),
    CONSTRAINT fk_bank_transactions_reversal FOREIGN KEY (reversal_of_id) REFERENCES SOUTHWND.bank_transactions(id),
    CONSTRAINT ck_bank_transactions_direction CHECK (direction IN ('CREDIT', 'DEBIT')),
    CONSTRAINT ck_bank_transactions_amount CHECK (amount > 0),
    CONSTRAINT ck_bank_transactions_status CHECK (status IN ('POSTED', 'REVERSED', 'VOIDED'))
);
CREATE INDEX ix_bank_transactions_balance ON SOUTHWND.bank_transactions(organization_id, bank_account_id, transaction_date, status);
CREATE INDEX ix_payments_report ON SOUTHWND.payments(organization_id, received_at, status, currency_code);
CREATE INDEX ix_invoices_report ON SOUTHWND.invoices(organization_id, invoice_date, due_date, status);