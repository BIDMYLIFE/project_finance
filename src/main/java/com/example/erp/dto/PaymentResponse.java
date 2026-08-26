package com.example.erp.dto;

import com.example.erp.entity.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID customerId, UUID categoryId, UUID bankAccountId, String receiptNumber,
                              String payerName, String reason, String note, BigDecimal amount, String currencyCode,
                              PaymentMethod paymentMethod, LocalDate receivedAt, PaymentStatus status, Instant createdAt) {
    public static PaymentResponse from(Payment p) { return new PaymentResponse(p.getId(), p.getCustomerId(), p.getCategoryId(), p.getBankAccountId(), p.getReceiptNumber(), p.getPayerName(), p.getReason(), p.getNote(), p.getAmount(), p.getCurrencyCode(), p.getPaymentMethod(), p.getReceivedAt(), p.getStatus(), p.getCreatedAt()); }
}
