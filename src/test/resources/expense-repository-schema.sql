CREATE SCHEMA IF NOT EXISTS SOUTHWND;
DROP TABLE IF EXISTS SOUTHWND.expenses;
CREATE TABLE SOUTHWND.expenses (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    category_id UUID NOT NULL,
    bank_account_id UUID,
    actor_id UUID NOT NULL,
    payee_name VARCHAR(200) NOT NULL,
    description VARCHAR(500) NOT NULL,
    note VARCHAR(1000),
    amount DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    expense_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    voided_at TIMESTAMP WITH TIME ZONE
);
