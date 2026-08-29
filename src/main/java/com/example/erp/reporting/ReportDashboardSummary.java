package com.example.erp.reporting;

import java.time.LocalDate;
import java.util.Map;

public record ReportDashboardSummary(LocalDate from, LocalDate to, String currencyCode, Map<String, ReportSummary> summaries) {}
