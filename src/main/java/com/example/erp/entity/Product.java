package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "SOUTHWND")
public class Product {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "product_code", nullable = false, length = 80) private String productCode;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 1000) private String description;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4) private BigDecimal unitPrice;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(name = "tax_rate", nullable = false, precision = 9, scale = 4) private BigDecimal taxRate;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Product() {}
    public Product(UUID id, UUID organizationId, String productCode, String name, String description, BigDecimal unitPrice, String currencyCode, BigDecimal taxRate, Instant now) {
        this.id = id; this.organizationId = organizationId; this.productCode = productCode; this.name = name; this.description = description;
        this.unitPrice = unitPrice; this.currencyCode = currencyCode; this.taxRate = taxRate; this.active = true; this.createdAt = now; this.updatedAt = now;
    }
    public void update(String name, String description, BigDecimal unitPrice, String currencyCode, BigDecimal taxRate, Instant now) {
        this.name = name; this.description = description; this.unitPrice = unitPrice; this.currencyCode = currencyCode; this.taxRate = taxRate; this.updatedAt = now;
    }
    public void deactivate(Instant now) { active = false; updatedAt = now; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getProductCode() { return productCode; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getTaxRate() { return taxRate; }
    public boolean isActive() { return active; }
}