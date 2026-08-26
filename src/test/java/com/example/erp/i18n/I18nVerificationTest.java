package com.example.erp.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class I18nVerificationTest {

    private static final Set<String> TEMPLATE_PATHS = Set.of(
            "templates/dashboard.html",
            "templates/auth/login.html",
            "templates/auth/bootstrap.html",
            "templates/customers/list.html"
    );

    private static final Set<String> JS_PATHS = Set.of(
            "static/js/api-client.js",
            "static/js/dashboard.js",
            "static/js/auth-login.js",
            "static/js/auth-bootstrap.js",
            "static/js/capability-registry.js",
            "static/js/erp-state.js",
            "static/js/api/customers-api.js",
            "static/js/pages/customers.js"
    );

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fff]");

    @Test
    void templatesHaveNoHardcodedChineseInHtmlContent() throws IOException {
        for (String path : TEMPLATE_PATHS) {
            String content = readResource(path);
            String cleaned = content
                    .replaceAll("th:text=\"[^\"]*\"", "")
                    .replaceAll("th:aria-label=\"[^\"]*\"", "")
                    .replaceAll("aria-label=\"[^\"]*\"", "")
                    .replaceAll(">\\s*[^<]*</", "><")
                    .replaceAll(">\\s*[^<]*/>", "/>");
            Matcher m = CHINESE_PATTERN.matcher(cleaned);
            StringBuilder chinese = new StringBuilder();
            while (m.find()) {
                chinese.append(m.group()).append(" at offset ").append(m.start()).append(", ");
            }
            assertTrue(chinese.isEmpty(), path + " contains hardcoded Chinese: " + chinese);
        }
    }

    @Test
    void jsFilesHaveNoHardcodedChinese() throws IOException {
        for (String path : JS_PATHS) {
            String content = readResource(path);
            Matcher m = CHINESE_PATTERN.matcher(content);
            StringBuilder chinese = new StringBuilder();
            while (m.find()) {
                chinese.append(m.group()).append(" at offset ").append(m.start()).append(", ");
            }
            assertTrue(chinese.isEmpty(), path + " contains hardcoded Chinese: " + chinese);
        }
    }

    @Test
    void messagesPropertiesFilesContainMatchingKeys() throws IOException {
        Properties defaultProps = loadProperties("i18n/messages.properties");
        Properties zhTwProps = loadProperties("i18n/messages_zh_TW.properties");

        Set<String> defaultKeys = new HashSet<>(defaultProps.stringPropertyNames());
        Set<String> zhTwKeys = new HashSet<>(zhTwProps.stringPropertyNames());

        Set<String> missingInZhTw = new HashSet<>(defaultKeys);
        missingInZhTw.removeAll(zhTwKeys);
        assertTrue(missingInZhTw.isEmpty(),
                "Keys in messages.properties missing from messages_zh_TW.properties: " + missingInZhTw);

        Set<String> missingInDefault = new HashSet<>(zhTwKeys);
        missingInDefault.removeAll(defaultKeys);
        assertTrue(missingInDefault.isEmpty(),
                "Keys in messages_zh_TW.properties missing from messages.properties: " + missingInDefault);

        assertFalse(defaultKeys.isEmpty(), "messages.properties should define at least one key");
    }

    @Test
    void allVueTemplatesHaveEmbeddedMessagesJson() throws IOException {
        for (String path : TEMPLATE_PATHS) {
            String content = readResource(path);
            assertTrue(content.contains("window.__ERP_MESSAGES__"), path + " missing window.__ERP_MESSAGES__ embed");
            assertTrue(content.contains("th:inline=\"javascript\""), path + " missing th:inline=\"javascript\"");
        }
    }

    @Test
    void templatesHaveThymeleafNamespace() throws IOException {
        for (String path : TEMPLATE_PATHS) {
            String content = readResource(path);
            assertTrue(content.contains("xmlns:th=\"http://www.thymeleaf.org\""), path + " missing xmlns:th");
        }
    }

    @Test
    void dashboardPageHasRequiredMessageKeys() throws IOException {
        String content = readResource("templates/dashboard.html");
        assertTrue(content.contains("#{dashboard.title}"), "Missing #{dashboard.title}");
        assertTrue(content.contains("#{dashboard.welcome}"), "Missing #{dashboard.welcome}");
        assertTrue(content.contains("#{dashboard.identity.title}"), "Missing #{dashboard.identity.title}");
    }

    @Test
    void loginPageHasRequiredMessageKeys() throws IOException {
        String content = readResource("templates/auth/login.html");
        assertTrue(content.contains("#{login.heading}"), "Missing #{login.heading}");
        assertTrue(content.contains("#{login.label.email}"), "Missing #{login.label.email}");
        assertTrue(content.contains("#{login.button.submit}"), "Missing #{login.button.submit}");
    }

    @Test
    void bootstrapPageHasRequiredMessageKeys() throws IOException {
        String content = readResource("templates/auth/bootstrap.html");
        assertTrue(content.contains("#{bootstrap.heading}"), "Missing #{bootstrap.heading}");
        assertTrue(content.contains("#{bootstrap.label.org_name}"), "Missing #{bootstrap.label.org_name}");
        assertTrue(content.contains("#{bootstrap.button.submit}"), "Missing #{bootstrap.button.submit}");
    }

    @Test
    void jsFilesUseMsgFunction() throws IOException {
        String[] filesUsingMsg = {
                "static/js/erp-state.js",
                "static/js/api-client.js",
                "static/js/dashboard.js",
                "static/js/auth-login.js",
                "static/js/auth-bootstrap.js",
                "static/js/capability-registry.js"
        };
        for (String path : filesUsingMsg) {
            String content = readResource(path);
            assertTrue(content.contains("MSG(") || content.contains("window.MSG"), path + " should use MSG function");
        }
    }

    private String readResource(String path) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing classpath resource: " + path);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Properties loadProperties(String path) throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertTrue(input != null, "Missing classpath resource: " + path);
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        return props;
    }
}
