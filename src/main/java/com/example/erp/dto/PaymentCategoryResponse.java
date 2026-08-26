package com.example.erp.dto;

import com.example.erp.entity.PaymentCategory;
import java.time.Instant;
import java.util.UUID;

public record PaymentCategoryResponse(UUID id, String name, boolean active, Instant createdAt) {
    public static PaymentCategoryResponse from(PaymentCategory value) { return new PaymentCategoryResponse(value.getId(), value.getName(), value.isActive(), value.getCreatedAt()); }
}
