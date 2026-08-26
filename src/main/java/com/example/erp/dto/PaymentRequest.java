package com.example.erp.dto;

import com.example.erp.entity.PaymentMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentRequest(
        UUID customerId,
        @NotNull UUID categoryId,
        UUID bankAccountId,
        @NotBlank @Size(max = 200) String payerName,
        @NotBlank @Size(max = 500) String reason,
        @Size(max = 1000) String note,
        @NotNull @DecimalMin("0.0001") @Digits(integer = 15, fraction = 4) BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currencyCode,
        @NotNull PaymentMethod paymentMethod,
        @NotNull LocalDate receivedAt) {}
