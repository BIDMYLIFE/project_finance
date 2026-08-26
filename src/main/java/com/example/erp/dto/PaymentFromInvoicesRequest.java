package com.example.erp.dto;

import com.example.erp.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PaymentFromInvoicesRequest(
        @NotEmpty @Size(max = 100) List<@NotNull UUID> invoiceIds,
        @NotNull UUID categoryId,
        @NotNull UUID bankAccountId,
        @NotNull @DecimalMin("0.0001") @Digits(integer = 15, fraction = 4) BigDecimal amount,
        @Size(max = 500) String reason,
        @Size(max = 1000) String note,
        @NotNull PaymentMethod paymentMethod,
        @NotNull LocalDate receivedAt) {}
