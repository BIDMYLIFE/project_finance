package com.example.erp.dto;

import com.example.erp.entity.ExpenseCategory;
import java.time.Instant;
import java.util.UUID;

public record ExpenseCategoryResponse(UUID id, String name, boolean active, Instant createdAt) {
    public static ExpenseCategoryResponse from(ExpenseCategory value) {
        return new ExpenseCategoryResponse(value.getId(), value.getName(), value.isActive(), value.getCreatedAt());
    }
}
