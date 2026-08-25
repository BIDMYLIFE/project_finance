package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "receipt_prints", schema = "SOUTHWND")
public class ReceiptPrint {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "payment_id", nullable = false) private UUID paymentId;
    @Column(name = "printed_at", nullable = false) private Instant printedAt;
    @Column(name = "actor_id") private UUID actorId;
    protected ReceiptPrint() {}
    public ReceiptPrint(UUID id, UUID organizationId, UUID paymentId, Instant printedAt, UUID actorId) { this.id = id; this.organizationId = organizationId; this.paymentId = paymentId; this.printedAt = printedAt; this.actorId = actorId; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getPaymentId() { return paymentId; }
    public Instant getPrintedAt() { return printedAt; }
    public UUID getActorId() { return actorId; }
}