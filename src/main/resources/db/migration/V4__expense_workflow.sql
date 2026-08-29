ALTER TABLE SOUTHWND.expenses ADD
    category_id UNIQUEIDENTIFIER NULL,
    actor_id UNIQUEIDENTIFIER NULL,
    payee_name NVARCHAR(200) NULL,
    note NVARCHAR(1000) NULL,
    updated_at DATETIME2(6) NULL,
    confirmed_at DATETIME2(6) NULL,
    voided_at DATETIME2(6) NULL;
GO

UPDATE SOUTHWND.expenses SET payee_name = '', updated_at = created_at WHERE payee_name IS NULL;
GO

ALTER TABLE SOUTHWND.expenses ALTER COLUMN category_id UNIQUEIDENTIFIER NOT NULL;
ALTER TABLE SOUTHWND.expenses ALTER COLUMN payee_name NVARCHAR(200) NOT NULL;
ALTER TABLE SOUTHWND.expenses ALTER COLUMN updated_at DATETIME2(6) NOT NULL;
ALTER TABLE SOUTHWND.expenses ADD CONSTRAINT fk_expenses_category FOREIGN KEY (category_id) REFERENCES SOUTHWND.expense_categories(id);
ALTER TABLE SOUTHWND.expenses ADD CONSTRAINT fk_expenses_actor FOREIGN KEY (actor_id) REFERENCES SOUTHWND.users(id);
ALTER TABLE SOUTHWND.expenses ADD CONSTRAINT ck_expenses_status CHECK (status IN ('DRAFT', 'CONFIRMED', 'VOIDED'));
ALTER TABLE SOUTHWND.expenses ADD CONSTRAINT ck_expenses_amount_positive CHECK (amount > 0);
CREATE INDEX ix_expenses_report ON SOUTHWND.expenses(organization_id, expense_date, status, category_id, currency_code);
