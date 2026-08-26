package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts", schema = "SOUTHWND")
public class BankAccount {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "account_name", nullable = false, length = 200) private String accountName;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(name = "opening_balance", nullable = false, precision = 19, scale = 4) private BigDecimal openingBalance;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected BankAccount() {}
    public BankAccount(UUID id, UUID organizationId, String accountName, String currencyCode, BigDecimal openingBalance, Instant createdAt) { this.id = id; this.organizationId = organizationId; this.accountName = accountName; this.currencyCode = currencyCode; this.openingBalance = openingBalance; this.active = true; this.createdAt = createdAt; }
    public void update(String accountName, String currencyCode, BigDecimal openingBalance) { this.accountName = accountName; this.currencyCode = currencyCode; this.openingBalance = openingBalance; }
    public void deactivate() { active = false; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getAccountName() { return accountName; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
