package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class QuotesWorkflowAssetTest {
    @Test
    void quotePageUsesLocalAssetsAndQuoteApi() throws IOException {
        String page = read("templates/quotes/list.html");
        assertTrue(page.contains("/js/api/quotes-api.js"));
        assertTrue(page.contains("/js/pages/quotes.js"));
        assertTrue(page.contains("/css/quotes.css"));
        assertFalse(page.contains("https://"));
        assertFalse(page.contains("cdn"));
    }

    @Test
    void quoteModuleCoversQueriesCrudLifecycleAndSafeFailures() throws IOException {
        String api = read("static/js/api/quotes-api.js");
        String page = read("static/js/pages/quotes.js");
        String template = read("templates/quotes/list.html");
        assertTrue(api.contains("client.get('/quotes'") && api.contains("client.post('/quotes'") && api.contains("client.put('/quotes/'"));
        assertTrue(api.contains("/submit") || api.contains("transition"));
        assertTrue(page.contains("totalPages") && page.contains("VALIDATION_ERROR") && page.contains("requestSequence"));
        assertTrue(page.contains("Swal.fire") && page.contains("result.isConfirmed") && page.contains("quotes.error.lifecycle"));
        assertTrue(template.contains("quotes.loading") && template.contains("quotes.empty") && template.contains("form.lines"));
        assertTrue(template.contains("previewTotals.subtotal") && template.contains("previewTotals.taxTotal") && template.contains("previewTotals.grandTotal"));
        assertFalse(page.contains("error.response.data.message"));
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
