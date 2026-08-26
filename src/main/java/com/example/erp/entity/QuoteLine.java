package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quote_lines", schema = "SOUTHWND")
public class QuoteLine {
    @Id private UUID id;
    @Column(name = "quote_id", nullable = false) private UUID quoteId;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(name = "product_name", nullable = false, length = 200) private String productName;
    @Column(length = 1000) private String description;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4) private BigDecimal unitPrice;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal discount;
    @Column(name = "tax_rate", nullable = false, precision = 9, scale = 4) private BigDecimal taxRate;
    @Column(name = "line_total", nullable = false, precision = 19, scale = 4) private BigDecimal lineTotal;
    protected QuoteLine() {}

    public QuoteLine(UUID id, UUID quoteId, UUID productId, String productName, String description,
            BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate, BigDecimal lineTotal) {
        this.id = id;
        this.quoteId = quoteId;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.taxRate = taxRate;
        this.lineTotal = lineTotal;
    }

    public UUID getId() { return id; }
    public UUID getQuoteId() { return quoteId; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
