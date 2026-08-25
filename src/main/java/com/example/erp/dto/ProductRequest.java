package com.example.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(@NotBlank @Size(max = 80) String productCode, @NotBlank @Size(max = 200) String name,
                             @Size(max = 1000) String description, @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 4) BigDecimal unitPrice,
                             @NotBlank @Size(min = 3, max = 3) String currencyCode, @NotNull @DecimalMin("0.0") @Digits(integer = 5, fraction = 4) BigDecimal taxRate) {}