package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "SOUTHWND")
public class Payment {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "customer_id") private UUID customerId;
    @Column(name = "category_id", nullable = false) private UUID categoryId;
    @Column(name = "bank_account_id") private UUID bankAccountId;
    @Column(name = "actor_id") private UUID actorId;
    @Column(name = "receipt_number", nullable = false, length = 40) private String receiptNumber;
    @Column(name = "payer_name", nullable = false, length = 200) private String payerName;
    @Column(nullable = false, length = 500) private String reason;
    @Column(length = 1000) private String note;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false, length = 30) private PaymentMethod paymentMethod;
    @Column(name = "received_at", nullable = false) private LocalDate receivedAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PaymentStatus status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected Payment() {}
    public Payment(UUID id, UUID organizationId, UUID categoryId, UUID bankAccountId, UUID actorId, String receiptNumber, String payerName, String reason, String note, BigDecimal amount, String currencyCode, PaymentMethod paymentMethod, LocalDate receivedAt, PaymentStatus status, Instant createdAt) {
        this.id = id; this.organizationId = organizationId; this.categoryId = categoryId; this.bankAccountId = bankAccountId; this.actorId = actorId; this.receiptNumber = receiptNumber; this.payerName = payerName; this.reason = reason; this.note = note; this.amount = amount; this.currencyCode = currencyCode; this.paymentMethod = paymentMethod; this.receivedAt = receivedAt; this.status = status; this.createdAt = createdAt;
    }
    public void post() { status = PaymentStatus.POSTED; }
    public void voidPayment() { status = PaymentStatus.VOIDED; }
    public void changeBankAccount(UUID bankAccountId) { this.bankAccountId = bankAccountId; }
    public void assignCustomer(UUID customerId) { this.customerId = customerId; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getCustomerId() { return customerId; }
    public UUID getCategoryId() { return categoryId; }
    public UUID getBankAccountId() { return bankAccountId; }
    public UUID getActorId() { return actorId; }
    public String getReceiptNumber() { return receiptNumber; }
    public String getPayerName() { return payerName; }
    public String getReason() { return reason; }
    public String getNote() { return note; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDate getReceivedAt() { return receivedAt; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
