package com.example.erp.reporting;

import java.math.BigDecimal;

public record ReportSummary(long count, BigDecimal amount, BigDecimal credit, BigDecimal debit, BigDecimal net) {
    public static ReportSummary zero() { return new ReportSummary(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO); }
}