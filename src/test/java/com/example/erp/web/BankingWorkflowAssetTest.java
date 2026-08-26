package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class BankingWorkflowAssetTest {
    @Test
    void bankingPageReferencesLocalWorkflowAssetsAndApi() throws IOException {
        String html = read("templates/banking/list.html");
        assertTrue(html.contains("/css/banking.css"));
        assertTrue(html.contains("/js/api/banking-api.js"));
        assertTrue(html.contains("/js/pages/banking.js"));
        assertTrue(read("static/js/api/banking-api.js").contains("/bank-accounts"));
        assertFalse(html.contains("cdn"));
        assertFalse(html.contains("https://"));
    }

    @Test
    void bankingPageContainsAccessibleCrudControls() throws IOException {
        String html = read("templates/banking/list.html");
        assertTrue(html.contains("/banking"));
        assertTrue(html.contains("aria-live"));
        assertTrue(html.contains("account-keyword"));
        assertTrue(html.contains("bank-account-name"));
        assertTrue(html.contains("bank-account-currency"));
        assertTrue(html.contains("bank-account-opening-balance"));
        assertTrue(html.contains("@click=\"openCreate\""));
        assertTrue(html.contains("@click=\"openEdit(account)\""));
        assertTrue(html.contains("@click=\"deactivate(account)\""));
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(input, "Missing resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
