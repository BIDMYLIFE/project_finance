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
        assertTrue(js.contains("id: 'reporting', labelKey: 'capability.reporting', route: '/reporting', available: true"), "Reporting capability should be available");
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
    void reportingCapabilityHasLiveRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("route: '/reporting'"), "Reporting should have a live route");
    }

    @Test
    void quoteManagementHasAvailableRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("id: 'quotes', labelKey: 'capability.quotes', route: '/quotes', available: true"), "Quotes capability should be available");
    }

    @Test
    void invoiceManagementHasAvailableRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("id: 'invoices', labelKey: 'capability.invoices', route: '/invoices', available: true"));
        assertTrue(js.contains("owner: 'invoice-ui-crud'"));
    }

    @Test
    void bankingManagementHasAvailableRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("id: 'banking', labelKey: 'capability.banking', route: '/banking', available: true"), "Banking capability should be available");
        assertTrue(js.contains("owner: 'bank-account-ui-crud'"), "Banking capability owner is missing");
    }

    @Test
    void dashboardHasAccessibleNavbarToggler() throws IOException {
        String html = readResource("templates/dashboard.html");
        assertTrue(html.contains("navbar-toggler"), "Missing navbar toggler");
        assertTrue(html.contains("dashboard-menu-toggle"), "Missing Dashboard toggler style hook");
        assertTrue(html.contains("aria-label"), "Missing aria-label on toggler");
        assertTrue(html.contains("aria-controls"), "Missing aria-controls on toggler");
        assertTrue(html.contains("aria-expanded"), "Missing aria-expanded on toggler");
        assertTrue(html.contains("data-bs-toggle=\"collapse\""), "Toggler must retain Bootstrap collapse behavior");
        assertTrue(html.contains("data-bs-target=\"#main-navigation\""), "Toggler must retain its navigation target");
    }

    @Test
    void dashboardTogglerHasExplicitHighContrastAndResponsiveStyles() throws IOException {
        String css = readResource("static/css/dashboard.css");
        assertTrue(css.contains(".dashboard-nav .dashboard-menu-toggle"), "Missing Dashboard toggler styles");
        assertTrue(css.contains("border: 2px solid #fffdf8"), "Toggler needs a high-contrast boundary");
        assertTrue(css.contains(".dashboard-menu-toggle::before"), "Toggler needs a local CSS icon");
        assertTrue(css.contains("[aria-expanded=\"true\"]"), "Expanded toggler state must be explicit");
        assertTrue(css.contains(".dashboard-menu-toggle:focus-visible"), "Toggler needs visible keyboard focus");
        assertTrue(css.contains("@media (max-width: 991.98px)"), "Missing mobile navigation layout rules");
        assertTrue(css.contains("overflow-wrap: anywhere"), "Mobile navigation labels must be able to wrap");
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
        assertFalse(html.contains("href=\"/invoices\""), "Invoices should not have a direct link");
        assertFalse(html.contains("href=\"/banking\""), "Banking should not have a direct link");
        assertTrue(html.contains("href=\"/reporting?type=invoice-status\""), "Reporting should have a summary link");
    }

    @Test
    void paymentCapabilityIsAvailableAtPaymentsRoute() throws IOException {
        String js = readResource("static/js/capability-registry.js");
        assertTrue(js.contains("id: 'payments'") && js.contains("route: '/payments'") && js.contains("available: true"),
                "Payments capability should link to the live payments page");
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
