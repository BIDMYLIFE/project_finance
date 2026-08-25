package com.example.erp.reporting;

import java.util.List;

public record ReportResponse(ReportType reportType, DateBasis dateBasis, List<ReportRow> rows, ReportSummary summary,
                             AppliedFilters appliedFilters, int totalPages, long totalRows, boolean empty) {
    public static ReportResponse empty(ReportType type, DateBasis basis, AppliedFilters filters) {
        return new ReportResponse(type, basis, List.of(), ReportSummary.zero(), filters, 0, 0, true);
    }
}