package com.example.erp.reporting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ReportRow(SourceReference source, LocalDate date, String currencyCode, BigDecimal amount, Map<String, String> fields) {}