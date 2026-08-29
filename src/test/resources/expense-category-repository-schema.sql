CREATE SCHEMA IF NOT EXISTS SOUTHWND;
DROP TABLE IF EXISTS SOUTHWND.expense_categories;
CREATE TABLE SOUTHWND.expense_categories (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_expense_categories_name UNIQUE (organization_id, name)
);
