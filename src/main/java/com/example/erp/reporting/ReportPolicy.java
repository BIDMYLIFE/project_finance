package com.example.erp.reporting;

import java.time.ZoneId;

public final class ReportPolicy {
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MAX_EXPORT_ROWS = 100;
    public static final int MAX_DATE_RANGE_MONTHS = 12;
    public static final ZoneId REPORT_ZONE = ZoneId.of("UTC");
    private ReportPolicy() {}
}
