CREATE TABLE SOUTHWND.expense_categories (
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_expense_categories PRIMARY KEY,
    organization_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(100) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_expense_categories_active DEFAULT 1,
    created_at DATETIME2(6) NOT NULL,
    CONSTRAINT fk_expense_categories_organization FOREIGN KEY (organization_id) REFERENCES SOUTHWND.organizations(id),
    CONSTRAINT uq_expense_categories_name UNIQUE (organization_id, name)
);
CREATE INDEX ix_expense_categories_scope_active_name
    ON SOUTHWND.expense_categories(organization_id, active, name);
