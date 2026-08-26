CREATE SCHEMA IF NOT EXISTS SOUTHWND;
DROP TABLE IF EXISTS SOUTHWND.quote_lines;
DROP TABLE IF EXISTS SOUTHWND.quotes;
DROP TABLE IF EXISTS SOUTHWND.products;
DROP TABLE IF EXISTS SOUTHWND.customers;
CREATE TABLE SOUTHWND.customers (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    customer_code VARCHAR(80) NOT NULL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(320),
    phone VARCHAR(50),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE SOUTHWND.products (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    product_code VARCHAR(80) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    unit_price DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    tax_rate DECIMAL(9,4) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE SOUTHWND.quotes (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    quote_number VARCHAR(40),
    status VARCHAR(20) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    subtotal DECIMAL(19,4) NOT NULL,
    tax_total DECIMAL(19,4) NOT NULL,
    grand_total DECIMAL(19,4) NOT NULL,
    valid_until DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE SOUTHWND.quote_lines (
    id UUID PRIMARY KEY,
    quote_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    quantity DECIMAL(19,4) NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    discount DECIMAL(19,4) NOT NULL,
    tax_rate DECIMAL(9,4) NOT NULL,
    line_total DECIMAL(19,4) NOT NULL
);
