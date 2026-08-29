package com.example.erp.web;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ReportingWorkflowAssetTest {
    @Test
    void reportingPageUsesLocalAssetsAndAllExportFormats() throws IOException {
        String page = read("templates/reporting/list.html");
        assertThat(page).contains("/js/api/reports-api.js", "/js/pages/reporting.js", "exportFile('csv')", "exportFile('pdf')", "exportFile('xlsx')");
        assertThat(page.replace("http://www.thymeleaf.org", "")).doesNotContain("http://", "https://");
    }

    @Test
    void dashboardExposesReportingSummary() throws IOException {
        String page = read("templates/dashboard.html");
        assertThat(page).contains("report-summary-panel", "dashboard.reports.title", "/js/api/reports-api.js");
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertThat(input).as("Missing %s", path).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
