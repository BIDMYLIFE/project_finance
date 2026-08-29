package com.example.erp.dto;

import com.example.erp.entity.Expense;
import com.example.erp.entity.ExpenseStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(UUID id, UUID categoryId, UUID bankAccountId, UUID actorId, String payeeName,
                              String description, String note, BigDecimal amount, String currencyCode,
                              LocalDate expenseDate, ExpenseStatus status, Instant createdAt, Instant updatedAt,
                              Instant confirmedAt, Instant voidedAt) {
    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getCategoryId(), expense.getBankAccountId(), expense.getActorId(),
                expense.getPayeeName(), expense.getDescription(), expense.getNote(), expense.getAmount(), expense.getCurrencyCode(),
                expense.getExpenseDate(), expense.getStatus(), expense.getCreatedAt(), expense.getUpdatedAt(),
                expense.getConfirmedAt(), expense.getVoidedAt());
    }
}
