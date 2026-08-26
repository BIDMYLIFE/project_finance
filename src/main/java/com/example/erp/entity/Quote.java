package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "quotes", schema = "SOUTHWND")
public class Quote {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "customer_id", nullable = false) private UUID customerId;
    @Column(name = "quote_number", length = 40) private String quoteNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private QuoteStatus status;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal subtotal;
    @Column(name = "tax_total", nullable = false, precision = 19, scale = 4) private BigDecimal taxTotal;
    @Column(name = "grand_total", nullable = false, precision = 19, scale = 4) private BigDecimal grandTotal;
    @Column(name = "valid_until", nullable = false) private LocalDate validUntil;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected Quote() {}

    public Quote(UUID id, UUID organizationId, UUID customerId, String quoteNumber, String currencyCode,
            BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal, LocalDate validUntil, Instant createdAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.customerId = customerId;
        this.quoteNumber = quoteNumber;
        this.status = QuoteStatus.DRAFT;
        this.currencyCode = currencyCode;
        this.subtotal = subtotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
        this.validUntil = validUntil;
        this.createdAt = createdAt;
    }

    public void update(UUID customerId, String currencyCode, BigDecimal subtotal, BigDecimal taxTotal,
            BigDecimal grandTotal, LocalDate validUntil) {
        this.customerId = customerId;
        this.currencyCode = currencyCode;
        this.subtotal = subtotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
        this.validUntil = validUntil;
    }

    public void transitionTo(QuoteStatus nextStatus) { this.status = nextStatus; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getCustomerId() { return customerId; }
    public String getQuoteNumber() { return quoteNumber; }
    public QuoteStatus getStatus() { return status; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxTotal() { return taxTotal; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public LocalDate getValidUntil() { return validUntil; }
    public Instant getCreatedAt() { return createdAt; }
}
