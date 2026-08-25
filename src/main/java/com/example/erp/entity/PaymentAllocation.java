package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_allocations", schema = "SOUTHWND")
public class PaymentAllocation {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "payment_id", nullable = false) private UUID paymentId;
    @Column(name = "invoice_id", nullable = false) private UUID invoiceId;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    protected PaymentAllocation() {}
    public PaymentAllocation(UUID id, UUID organizationId, UUID paymentId, UUID invoiceId, BigDecimal amount) { this.id = id; this.organizationId = organizationId; this.paymentId = paymentId; this.invoiceId = invoiceId; this.amount = amount; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getPaymentId() { return paymentId; }
    public UUID getInvoiceId() { return invoiceId; }
    public BigDecimal getAmount() { return amount; }
}