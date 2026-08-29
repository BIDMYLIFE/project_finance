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
@Table(name = "expenses", schema = "SOUTHWND")
public class Expense {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "category_id", nullable = false) private UUID categoryId;
    @Column(name = "bank_account_id") private UUID bankAccountId;
    @Column(name = "actor_id") private UUID actorId;
    @Column(name = "payee_name", nullable = false, length = 200) private String payeeName;
    @Column(nullable = false, length = 500) private String description;
    @Column(length = 1000) private String note;
    @Column(nullable = false, precision = 19, scale = 4) private BigDecimal amount;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(name = "expense_date", nullable = false) private LocalDate expenseDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private ExpenseStatus status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Column(name = "confirmed_at") private Instant confirmedAt;
    @Column(name = "voided_at") private Instant voidedAt;

    protected Expense() {}
    public Expense(UUID id, UUID organizationId, UUID categoryId, UUID bankAccountId, UUID actorId, String payeeName,
                   String description, String note, BigDecimal amount, String currencyCode, LocalDate expenseDate, Instant now) {
        this.id = id; this.organizationId = organizationId; this.categoryId = categoryId; this.bankAccountId = bankAccountId;
        this.actorId = actorId; this.payeeName = payeeName; this.description = description; this.note = note; this.amount = amount;
        this.currencyCode = currencyCode; this.expenseDate = expenseDate; this.status = ExpenseStatus.DRAFT;
        this.createdAt = now; this.updatedAt = now;
    }
    public void update(UUID categoryId, UUID bankAccountId, String payeeName, String description, String note,
                       BigDecimal amount, String currencyCode, LocalDate expenseDate, Instant now) {
        this.categoryId = categoryId; this.bankAccountId = bankAccountId; this.payeeName = payeeName; this.description = description;
        this.note = note; this.amount = amount; this.currencyCode = currencyCode; this.expenseDate = expenseDate; this.updatedAt = now;
    }
    public void confirm(UUID bankAccountId, Instant now) { this.bankAccountId = bankAccountId; this.status = ExpenseStatus.CONFIRMED; this.confirmedAt = now; this.updatedAt = now; }
    public void voidExpense(Instant now) { this.status = ExpenseStatus.VOIDED; this.voidedAt = now; this.updatedAt = now; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getCategoryId() { return categoryId; }
    public UUID getBankAccountId() { return bankAccountId; }
    public UUID getActorId() { return actorId; }
    public String getPayeeName() { return payeeName; }
    public String getDescription() { return description; }
    public String getNote() { return note; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public ExpenseStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getConfirmedAt() { return confirmedAt; }
    public Instant getVoidedAt() { return voidedAt; }
}
