package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ProductsWorkflowAssetTest {
    @Test
    void pageUsesOnlyLocalRuntimeAssetsAndSharedState() throws IOException {
        String page = read("templates/products/list.html");
        assertTrue(page.contains("/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css"));
        assertTrue(page.contains("/webjars/vue/3.5.18/dist/vue.global.prod.js"));
        assertTrue(page.contains("/webjars/axios/1.11.0/dist/axios.min.js"));
        assertTrue(page.contains("/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js"));
        assertTrue(page.contains("/js/erp-state.js"));
        assertTrue(page.contains("/js/api-client.js"));
        assertFalse(page.replace("http://www.thymeleaf.org", "").contains("http://"));
        assertFalse(page.contains("https://"));
        assertFalse(page.contains("cdn"));
    }

    @Test
    void moduleCoversQueriesPaginationCrudValidationAndSafeFailures() throws IOException {
        String api = read("static/js/api/products-api.js");
        String page = read("static/js/pages/products.js");
        String template = read("templates/products/list.html");
        assertTrue(api.contains("active: query.active === 'true'") && api.contains("page: page"));
        assertTrue(api.contains("client.post('/products'") && api.contains("client.put('/products/'") && api.contains("client.delete('/products/'"));
        assertTrue(page.contains("requestSequence") && page.contains("totalPages") && page.contains("VALIDATION_ERROR") && page.contains("self.loading = false"));
        assertTrue(page.contains("Swal.fire") && page.contains("result.isConfirmed") && page.contains("Number(form.taxRate) > 100"));
        assertTrue(template.contains("products.loading") && template.contains("products.empty") && template.contains("products.form.description"));
        assertFalse(page.contains("error.response.data.message"));
        assertFalse(page.contains("type:"));
    }

    private String read(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}