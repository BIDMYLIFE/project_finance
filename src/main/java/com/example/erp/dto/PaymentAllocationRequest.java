package com.example.erp.dto;
import jakarta.validation.constraints.DecimalMin; import jakarta.validation.constraints.Digits; import jakarta.validation.constraints.NotNull; import java.math.BigDecimal; import java.util.UUID;
public record PaymentAllocationRequest(@NotNull UUID invoiceId,@NotNull @DecimalMin("0.0001") @Digits(integer=15,fraction=4) BigDecimal amount) {}
