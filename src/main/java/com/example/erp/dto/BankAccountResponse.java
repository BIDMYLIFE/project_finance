package com.example.erp.dto;

import com.example.erp.entity.BankAccount;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BankAccountResponse(UUID id, String accountName, String currencyCode,
                                  BigDecimal openingBalance, boolean active, Instant createdAt) {
    public static BankAccountResponse from(BankAccount value) {
        return new BankAccountResponse(value.getId(), value.getAccountName(), value.getCurrencyCode(),
                value.getOpeningBalance(), value.isActive(), value.getCreatedAt());
    }
}
