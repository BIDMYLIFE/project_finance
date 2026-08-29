package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardPageController {

    private final MessageSource messageSource;

    DashboardPageController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public String dashboard(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        put(messages, "dashboard.title", locale);
        put(messages, "dashboard.eyebrow", locale);
        put(messages, "dashboard.welcome", locale);
        put(messages, "dashboard.nav.logout", locale);
        put(messages, "dashboard.nav.logging_out", locale);
        put(messages, "dashboard.nav.not_yet_available", locale);
        put(messages, "dashboard.identity.title", locale);
        put(messages, "dashboard.identity.loading", locale);
        put(messages, "dashboard.identity.network_error", locale);
        put(messages, "dashboard.identity.retry", locale);
        put(messages, "dashboard.identity.email", locale);
        put(messages, "dashboard.identity.user_id", locale);
        put(messages, "dashboard.identity.organization_id", locale);
        put(messages, "dashboard.identity.no_data", locale);
        put(messages, "dashboard.reports.title", locale);
        put(messages, "dashboard.reports.loading", locale);
        put(messages, "dashboard.reports.error", locale);
        put(messages, "dashboard.reports.retry", locale);
        put(messages, "dashboard.reports.sales", locale);
        put(messages, "dashboard.reports.payments", locale);
        put(messages, "dashboard.reports.expenses", locale);
        put(messages, "dashboard.reports.receivables", locale);
        put(messages, "dashboard.reports.pending", locale);
        put(messages, "capability.dashboard", locale);
        put(messages, "capability.customers", locale);
        put(messages, "capability.products", locale);
        put(messages, "capability.quotes", locale);
        put(messages, "capability.invoices", locale);
        put(messages, "capability.payments", locale);
        put(messages, "capability.banking", locale);
        put(messages, "capability.expenses", locale);
        put(messages, "capability.reporting", locale);
        put(messages, "common.error.network", locale);
        put(messages, "common.error.timeout", locale);
        put(messages, "common.error.request_failed", locale);
        put(messages, "common.logout.title", locale);
        put(messages, "common.nav.logout", locale);
        put(messages, "common.nav.logging_out", locale);
        model.addAttribute("messages", messages);
        return "dashboard";
    }

    private void put(Map<String, String> map, String key, Locale locale) {
        map.put(key, messageSource.getMessage(key, null, locale));
    }
}
