package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PaymentsWorkflowAssetTest {
    @Test void paymentsPageProvidesExplicitBankAccountPostingSelection() throws IOException {
        String html = read("templates/payments/list.html");
        String script = read("static/js/pages/payments.js");
        assertTrue(html.contains("payment-bank-account"));
        assertTrue(html.contains("payment.table.bank_account") || html.contains("payments.table.bank_account"));
        assertTrue(script.contains("payments.confirm.post_title"));
        assertTrue(script.contains("payments.confirm.post_confirm"));
        assertTrue(script.contains("inputValidator"));
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(input, "Missing resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
