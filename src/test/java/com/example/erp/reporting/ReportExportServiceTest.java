package com.example.erp.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ReportExportServiceTest {
    @Test
    void exportsCsvPdfAndXlsxFromTheSameReportResponse() throws Exception {
        ReportQueryService query = Mockito.mock(ReportQueryService.class);
        ReportFilterRequest request = new ReportFilterRequest(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, "TWD", null, "date", "DESC", 0, 20);
        ReportRow row = new ReportRow(new SourceReference("EXPENSE", java.util.UUID.randomUUID(), "EXP-1", "CONFIRMED"), LocalDate.of(2026, 1, 2), "TWD", BigDecimal.TEN, java.util.Map.of());
        ReportResponse response = new ReportResponse(ReportType.EXPENSES, DateBasis.EXPENSE_DATE, List.of(row), new ReportSummary(1, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN), AppliedFilters.from(request), 1, 1, false);
        when(query.query(eq(ReportType.EXPENSES), any(ReportFilterRequest.class))).thenReturn(response);

        ReportExportService service = new ReportExportService(query);
        assertThat(new String(service.csv(ReportType.EXPENSES, request))).contains("EXPENSES", "summaryCount", "10");
        assertThat(service.pdf(ReportType.EXPENSES, request)).startsWith("%PDF".getBytes());
        assertThat(service.xlsx(ReportType.EXPENSES, request)).startsWith(new byte[]{0x50, 0x4b});
    }
}
