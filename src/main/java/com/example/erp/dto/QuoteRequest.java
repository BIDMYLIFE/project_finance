package com.example.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteRequest(
        @NotNull UUID customerId,
        @NotNull @Pattern(regexp = "TWD|USD|EUR|JPY") String currencyCode,
        @NotNull @FutureOrPresent LocalDate validUntil,
        @NotEmpty @Size(max = 100) List<@Valid QuoteLineRequest> lines) {
}
