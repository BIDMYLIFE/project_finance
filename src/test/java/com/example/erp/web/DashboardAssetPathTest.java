package com.example.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class DashboardAssetPathTest {

    private static final String BOOTSTRAP_CSS_PATH = "/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css";
    private static final String BOOTSTRAP_JS_PATH = "/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js";
    private static final String VUE_JS_PATH = "/webjars/vue/3.5.18/dist/vue.global.prod.js";
    private static final String AXIOS_JS_PATH = "/webjars/axios/1.11.0/dist/axios.min.js";
    private static final String SWEETALERT2_JS_PATH = "/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js";

    @Test
    void dashboardReferencesPackagedBootstrapAssetsWithoutExternalUrls() throws IOException {
        String dashboard = readResource("templates/dashboard.html");
        String stripped = dashboard.replaceAll("xmlns:[a-z]+=\"http://[^\"]*\"", "");

        assertTrue(dashboard.contains(BOOTSTRAP_CSS_PATH));
        assertTrue(dashboard.contains(BOOTSTRAP_JS_PATH));
        assertFalse(stripped.contains("https://"));
        assertFalse(stripped.contains("http://"));
        assertTrue(resourceExists("META-INF/resources/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css"));
        assertTrue(resourceExists("META-INF/resources/webjars/bootstrap/5.3.8/dist/js/bootstrap.bundle.min.js"));
    }

    @Test
    void dashboardReferencesVueAxiosSweetAlertFromLocalWebjars() throws IOException {
        String dashboard = readResource("templates/dashboard.html");

        assertTrue(dashboard.contains(VUE_JS_PATH), "Missing local Vue reference");
        assertTrue(dashboard.contains(AXIOS_JS_PATH), "Missing local Axios reference");
        assertTrue(dashboard.contains(SWEETALERT2_JS_PATH), "Missing local SweetAlert2 reference");
        assertTrue(resourceExists("META-INF/resources/webjars/vue/3.5.18/dist/vue.global.prod.js"));
        assertTrue(resourceExists("META-INF/resources/webjars/axios/1.11.0/dist/axios.min.js"));
        assertTrue(resourceExists("META-INF/resources/webjars/sweetalert2/11.23.0/dist/sweetalert2.all.min.js"));
    }

    @Test
    void dashboardDoesNotReferenceCdnOrExternalResources() throws IOException {
        String dashboard = readResource("templates/dashboard.html");
        String stripped = dashboard.replaceAll("xmlns:[a-z]+=\"http://[^\"]*\"", "");

        assertFalse(stripped.contains("cdn"), "Dashboard should not reference CDN");
        assertFalse(stripped.contains("cdnjs"), "Dashboard should not reference cdnjs");
        assertFalse(stripped.contains("unpkg"), "Dashboard should not reference unpkg");
        assertFalse(stripped.contains("jsdelivr"), "Dashboard should not reference jsdelivr");
        assertFalse(stripped.contains("fonts.googleapis"), "Dashboard should not reference Google Fonts");
    }

    @Test
    void loginPageDoesNotReferenceExternalResources() throws IOException {
        String login = readResource("templates/auth/login.html");
        String stripped = login.replaceAll("xmlns:[a-z]+=\"http://[^\"]*\"", "");
        assertFalse(stripped.contains("https://"), "login.html contains external https:// URL");
        assertFalse(stripped.contains("http://"), "login.html contains external http:// URL");
    }

    @Test
    void bootstrapPageReferencesOnlyLocalAssets() throws IOException {
        String bootstrap = readResource("templates/auth/bootstrap.html");
        String stripped = bootstrap.replaceAll("xmlns:[a-z]+=\"http://[^\"]*\"", "");
        assertFalse(stripped.contains("https://"), "bootstrap.html contains external https:// URL");
        assertFalse(stripped.contains("http://"), "bootstrap.html contains external http:// URL");
        assertTrue(bootstrap.contains("/webjars/bootstrap/5.3.8/dist/css/bootstrap.min.css"));
        assertTrue(bootstrap.contains("/webjars/vue/3.5.18/dist/vue.global.prod.js"));
    }

    @Test
    void allJsFilesHaveNoExternalUrls() throws IOException {
        String[] jsFiles = {
                "static/js/api-client.js",
                "static/js/dashboard.js",
                "static/js/auth-login.js",
                "static/js/auth-bootstrap.js",
                "static/js/capability-registry.js",
                "static/js/erp-state.js"
        };
        for (String path : jsFiles) {
            String content = readResource(path);
            assertFalse(content.contains("https://"), path + " contains https:// URL");
            assertFalse(content.contains("http://"), path + " contains http:// URL");
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
