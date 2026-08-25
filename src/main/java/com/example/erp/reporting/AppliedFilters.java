package com.example.erp.reporting;

import java.time.LocalDate;
import java.util.UUID;

public record AppliedFilters(LocalDate from, LocalDate to, UUID customerId, UUID categoryId, UUID accountId,
                             String currencyCode, String status, String sort, String direction, int page, int size) {
    public static AppliedFilters from(ReportFilterRequest request) {
        return new AppliedFilters(request.from(), request.to(), request.customerId(), request.categoryId(), request.accountId(),
                request.currencyCode() == null ? null : request.currencyCode().toUpperCase(java.util.Locale.ROOT), request.status(),
                request.sort(), request.direction().toUpperCase(java.util.Locale.ROOT), request.page(), request.size());
    }
}