package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ExpensesWorkflowAssetTest {
    @Test void dashboardExposesExpensesCapability() throws IOException {
        String js = read("static/js/capability-registry.js");
        assertTrue(js.contains("id: 'expenses'") && js.contains("route: '/expenses'") && js.contains("available: true"));
        assertTrue(js.contains("owner: 'expense-ui-crud'"));
    }
    @Test void expensePageUsesLocalAssetsAndAccessibleControls() throws IOException {
        String html = read("templates/expenses/list.html");
        assertTrue(html.contains("id=\"expenses-app\"") && html.contains("viewport"));
        assertTrue(html.contains("/js/api/expenses-api.js") && html.contains("/js/pages/expenses.js"));
        assertTrue(html.contains("aria-live") && html.contains("for=\"expense-category\"") && html.contains("for=\"expense-amount\""));
        assertTrue(!html.contains("http://cdn.") && !html.contains("https://cdn."));
    }
    @Test void expenseScriptSupportsAllLifecycleAndOperationalStates() throws IOException {
        String js = read("static/js/pages/expenses.js");
        assertTrue(js.contains("loadOptions") && js.contains("loadExpenses") && js.contains("openEdit"));
        assertTrue(js.contains("confirmExpense") && js.contains("voidExpense") && js.contains("Swal.fire"));
        assertTrue(js.contains("loading") && js.contains("loadError") && js.contains("saving"));
    }
    private String read(String path) throws IOException { try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) { assertTrue(input != null, "Missing resource: " + path); return new String(input.readAllBytes(), StandardCharsets.UTF_8); } }
}
