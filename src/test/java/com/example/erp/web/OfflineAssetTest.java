package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OfflineAssetTest {

    private static final Set<String> TEMPLATE_PATHS = Set.of(
            "templates/dashboard.html",
            "templates/auth/login.html",
            "templates/auth/bootstrap.html"
    );

    private static final Set<String> JS_PATHS = Set.of(
            "static/js/api-client.js",
            "static/js/dashboard.js",
            "static/js/auth-login.js",
            "static/js/auth-bootstrap.js",
            "static/js/capability-registry.js",
            "static/js/erp-state.js"
    );

    private static final Set<String> CSS_PATHS = Set.of(
            "static/css/dashboard.css",
            "static/css/auth.css"
    );

    private static final String[] VENDOR_WEBJARS = {
            "META-INF/resources/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css",
            "META-INF/resources/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js",
            "META-INF/resources/webjars/vue/3.5.18/dist/vue.global.prod.js",
            "META-INF/resources/webjars/axios/1.11.0/dist/axios.min.js",
            "META-INF/resources/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js"
    };

    @Test
    void templatesContainNoExternalResourceUrls() throws IOException {
        for (String path : TEMPLATE_PATHS) {
            String content = readResource(path);
            String stripped = content.replaceAll("xmlns:[a-z]+=\"http://[^\"]*\"", "");
            assertFalse(stripped.contains("https://"), path + " contains https:// URL");
            assertFalse(stripped.contains("http://"), path + " contains http:// URL");
        }
    }

    @Test
    void javaScriptFilesContainNoExternalUrls() throws IOException {
        for (String path : JS_PATHS) {
            String content = readResource(path);
            assertFalse(content.contains("https://"), path + " contains https:// URL");
            assertFalse(content.contains("http://"), path + " contains http:// URL");
        }
    }

    @Test
    void cssFilesContainNoExternalUrls() throws IOException {
        for (String path : CSS_PATHS) {
            String content = readResource(path);
            assertFalse(content.contains("https://"), path + " contains https:// URL");
            assertFalse(content.contains("http://"), path + " contains http:// URL");
        }
    }

    @Test
    void templatesReferenceLocalWebjarsOnly() throws IOException {
        String dashboard = readResource("templates/dashboard.html");
        assertTrue(dashboard.contains("/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css"));
        assertTrue(dashboard.contains("/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js"));
        assertTrue(dashboard.contains("/webjars/vue/3.5.18/dist/vue.global.prod.js"));
        assertTrue(dashboard.contains("/webjars/axios/1.11.0/dist/axios.min.js"));
        assertTrue(dashboard.contains("/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js"));
    }

    @Test
    void allRequiredWebjarAssetsExistOnClasspath() {
        for (String webjar : VENDOR_WEBJARS) {
            assertTrue(resourceExists(webjar), "Missing classpath resource: " + webjar);
        }
    }

    @Test
    void templatesUseLocalCssReferences() throws IOException {
        for (String path : TEMPLATE_PATHS) {
            String content = readResource(path);
            assertFalse(content.contains("@import url("), path + " contains @import for external CSS");
        }
    }

    private String readResource(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing classpath resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private boolean resourceExists(String path) {
        return getClass().getClassLoader().getResource(path) != null;
    }
}
