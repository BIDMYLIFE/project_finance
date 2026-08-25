package com.example.erp.reporting;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record ReportFilterRequest(LocalDate from, LocalDate to, UUID customerId, UUID categoryId, UUID accountId,
                                  @Size(min = 3, max = 3) String currencyCode, String status, String sort,
                                  String direction, @Min(0) int page, @Min(1) @Max(100) int size) {
    public ReportFilterRequest {
        if (size == 0) size = 20;
        if (sort == null || sort.isBlank()) sort = "date";
        if (direction == null || direction.isBlank()) direction = "DESC";
    }
    public void validate(DateBasis basis, java.util.Set<String> allowedSorts) {
        if (from == null || to == null) throw new IllegalArgumentException("Report date range is required");
        if (to.isBefore(from)) throw new IllegalArgumentException("Report date range is invalid");
        if (from.plusMonths(12).isBefore(to)) throw new IllegalArgumentException("Report date range is too large");
        if (!allowedSorts.contains(sort)) throw new IllegalArgumentException("Report sort is invalid");
        if (!(direction.equalsIgnoreCase("ASC") || direction.equalsIgnoreCase("DESC"))) throw new IllegalArgumentException("Report direction is invalid");
        if (currencyCode != null && !currencyCode.matches("[A-Za-z]{3}")) throw new IllegalArgumentException("Report currency is invalid");
        if (basis == null) throw new IllegalArgumentException("Report date basis is required");
    }
}