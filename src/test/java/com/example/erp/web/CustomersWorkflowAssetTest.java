package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CustomersWorkflowAssetTest {
    @Test
    void pageUsesOnlyLocalRuntimeAssets() throws IOException {
        String page = read("templates/customers/list.html");
        assertTrue(page.contains("/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css"));
        assertTrue(page.contains("/webjars/vue/3.5.18/dist/vue.global.prod.js"));
        assertTrue(page.contains("/webjars/axios/1.11.0/dist/axios.min.js"));
        assertTrue(page.contains("/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js"));
        assertTrue(page.contains("/js/erp-state.js"));
        assertFalse(page.replace("http://www.thymeleaf.org", "").contains("http://"));
        assertFalse(page.contains("https://"));
        assertFalse(page.contains("cdn"));
    }

    @Test
    void pageModuleCoversServerQueriesAndMaintenanceStates() throws IOException {
        String api = read("static/js/api/customers-api.js");
        String page = read("static/js/pages/customers.js");
        String template = read("templates/customers/list.html");
        assertTrue(api.contains("active: query.active === 'true'"), "active criteria");
        assertTrue(api.contains("page: page"), "server page");
        assertTrue(api.contains("client.post('/customers'"), "create route");
        assertTrue(api.contains("client.put('/customers/'"), "update route");
        assertTrue(api.contains("client.delete('/customers/'"), "deactivate route");
        assertTrue(page.contains("this.page = 0"), "reset page");
        assertTrue(page.contains("totalPages"), "pagination metadata");
        assertTrue(page.contains("VALIDATION_ERROR"), "validation response");
        assertTrue(page.contains("Swal.fire"), "confirmation and feedback");
        assertTrue(page.contains("result.isConfirmed"), "confirmation result");
        assertTrue(template.contains("customers.loading"), "loading state");
        assertTrue(template.contains("customers.empty"), "empty state");
        assertFalse(page.contains("error.response.data.message"));
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}