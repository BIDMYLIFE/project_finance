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
@Table(name = "bank_transactions", schema = "SOUTHWND")
public class BankTransaction {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "bank_account_id", nullable = false) private UUID bankAccountId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private BankTransactionDirection direction;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(name = "transaction_date", nullable = false) private LocalDate transactionDate;
    @Column(name = "source_type", nullable = false, length = 40) private String sourceType;
    @Column(name = "source_id") private UUID sourceId;
    @Column(name = "transfer_reference") private UUID transferReference;
    @Column(name = "reversal_of_id") private UUID reversalOfId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BankTransactionStatus status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected BankTransaction() {}
    public BankTransaction(UUID id, UUID organizationId, UUID bankAccountId, BankTransactionDirection direction, BigDecimal amount, String currencyCode, LocalDate transactionDate, String sourceType, UUID sourceId, UUID reversalOfId, BankTransactionStatus status, Instant createdAt) {
        this.id = id; this.organizationId = organizationId; this.bankAccountId = bankAccountId; this.direction = direction; this.amount = amount; this.currencyCode = currencyCode; this.transactionDate = transactionDate; this.sourceType = sourceType; this.sourceId = sourceId; this.reversalOfId = reversalOfId; this.status = status; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getBankAccountId() { return bankAccountId; }
    public BankTransactionDirection getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public String getSourceType() { return sourceType; }
    public UUID getSourceId() { return sourceId; }
    public UUID getReversalOfId() { return reversalOfId; }
    public BankTransactionStatus getStatus() { return status; }
}