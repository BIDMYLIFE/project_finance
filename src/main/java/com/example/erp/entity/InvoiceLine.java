package com.example.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name="invoice_lines", schema="SOUTHWND")
public class InvoiceLine {
    @Id private UUID id; @Column(name="invoice_id", nullable=false) private UUID invoiceId; @Column(name="product_id") private UUID productId; @Column(name="product_name", nullable=false, length=200) private String productName; @Column(length=1000) private String description; @Column(nullable=false, precision=19, scale=4) private BigDecimal quantity; @Column(name="unit_price", nullable=false, precision=19, scale=4) private BigDecimal unitPrice; @Column(nullable=false, precision=19, scale=4) private BigDecimal discount; @Column(name="tax_rate", nullable=false, precision=9, scale=4) private BigDecimal taxRate; @Column(name="line_total", nullable=false, precision=19, scale=4) private BigDecimal lineTotal;
    protected InvoiceLine() {}
    public InvoiceLine(UUID id, UUID invoiceId, UUID productId, String productName, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate, BigDecimal lineTotal) { this.id=id;this.invoiceId=invoiceId;this.productId=productId;this.productName=productName;this.description=description;this.quantity=quantity;this.unitPrice=unitPrice;this.discount=discount;this.taxRate=taxRate;this.lineTotal=lineTotal; }
    public UUID getId(){return id;} public UUID getInvoiceId(){return invoiceId;} public UUID getProductId(){return productId;} public String getProductName(){return productName;} public String getDescription(){return description;} public BigDecimal getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getDiscount(){return discount;} public BigDecimal getTaxRate(){return taxRate;} public BigDecimal getLineTotal(){return lineTotal;}
}
