package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DashboardNavigationTest {

    @Test
    void capabilityRegistryDefinesAllRequiredCapabilities() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("'dashboard'"), "Missing dashboard capability");
        assertTrue(js.contains("'customers'"), "Missing customers capability");
        assertTrue(js.contains("'products'"), "Missing products capability");
        assertTrue(js.contains("'quotes'"), "Missing quotes capability");
        assertTrue(js.contains("'invoices'"), "Missing invoices capability");
        assertTrue(js.contains("'payments'"), "Missing payments capability");
        assertTrue(js.contains("'banking'"), "Missing banking capability");
        assertTrue(js.contains("'reporting'"), "Missing reporting capability");
    }

    @Test
    void capabilityRegistryHasAvailableFlag() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("available: true"), "Missing available: true flag");
        assertTrue(js.contains("available: false"), "Missing available: false flag");
    }

    @Test
    void capabilityRegistryHasOwnerField() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("owner:"), "Missing owner field");
        assertTrue(js.contains("dashboard-post-auth-routing"), "Missing dashboard owner");
        assertTrue(js.contains("financial-erp-core"), "Missing financial-erp-core owner");
    }

    @Test
    void customerManagementHasRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("route: '/customers'"), "Missing customer route");
    }

    @Test
    void productManagementHasAvailableRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("route: '/products'"), "Missing product route");
        assertTrue(js.contains("id: 'products', labelKey: 'capability.products', route: '/products', available: true"), "Products capability should be available");
    }

    @Test
    void unavailableCapabilitiesHaveNullRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("route: null"), "Unavailable capabilities should have null route");
    }

    @Test
    void dashboardHasAccessibleNavbarToggler() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("navbar-toggler"), "Missing navbar toggler");
        assertTrue(html.contains("aria-label"), "Missing aria-label on toggler");
        assertTrue(html.contains("aria-controls"), "Missing aria-controls on toggler");
        assertTrue(html.contains("aria-expanded"), "Missing aria-expanded on toggler");
    }

    @Test
    void dashboardHasLogoutButton() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("loggingOut"), "Missing logout button with loggingOut state");
    }

    @Test
    void dashboardHasLiveRegionForStatus() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("aria-live"), "Missing aria-live region");
    }

    @Test
    void dashboardCapabilityRegistryLoaded() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("capability-registry.js"), "Missing capability-registry.js script");
        assertTrue(html.contains("erp-state.js"), "Missing erp-state.js script");
    }

    @Test
    void dashboardHasResponsiveViewportMeta() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("viewport"), "Missing viewport meta tag");
        assertTrue(html.contains("width=device-width"), "Missing responsive viewport width");
    }

    @Test
    void dashboardDoesNotLinkToUndefinedRoutes() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertFalse(html.contains("href=\"/quotes\""), "Quotes should not have a direct link");
        assertFalse(html.contains("href=\"/invoices\""), "Invoices should not have a direct link");
        assertFalse(html.contains("href=\"/payments\""), "Payments should not have a direct link");
        assertFalse(html.contains("href=\"/banking\""), "Banking should not have a direct link");
        assertFalse(html.contains("href=\"/reporting\""), "Reporting should not have a direct link");
    }

    @Test
    void registryProvidesListAvailableUnavailableFindMethods() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("list:"), "Missing list method");
        assertTrue(js.contains("available:"), "Missing available method");
        assertTrue(js.contains("unavailable:"), "Missing unavailable method");
        assertTrue(js.contains("find:"), "Missing find method");
    }

    private String readResource(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing classpath resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
