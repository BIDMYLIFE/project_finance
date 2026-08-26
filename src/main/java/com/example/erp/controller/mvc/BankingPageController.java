package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BankingPageController {
    private static final String[] MESSAGE_KEYS = {
            "banking.title", "banking.eyebrow", "banking.heading", "banking.description",
            "banking.nav.dashboard", "banking.nav.logout", "banking.nav.logging_out",
            "banking.action.add", "banking.action.search", "banking.action.clear", "banking.action.edit",
            "banking.action.deactivate", "banking.action.retry", "banking.filter.keyword", "banking.filter.status",
            "banking.filter.active", "banking.filter.inactive", "banking.table.name", "banking.table.currency",
            "banking.table.opening_balance", "banking.table.status", "banking.table.actions", "banking.status.active",
            "banking.status.inactive", "banking.loading", "banking.empty", "banking.error.load", "banking.error.save",
            "banking.error.deactivate", "banking.error.validation", "banking.form.create_title", "banking.form.edit_title",
            "banking.form.name", "banking.form.currency", "banking.form.opening_balance", "banking.form.required",
            "banking.form.invalid_name", "banking.form.invalid_currency", "banking.form.invalid_balance", "banking.form.save",
            "banking.form.cancel", "banking.confirm.title", "banking.confirm.text", "banking.confirm.confirm",
            "banking.confirm.cancel", "banking.success.created", "banking.success.updated", "banking.success.deactivated",
            "banking.pagination.previous", "banking.pagination.next", "banking.pagination.of",
            "common.error.network", "common.error.timeout", "common.error.request_failed"
    };

    private final MessageSource messageSource;
    public BankingPageController(MessageSource messageSource) { this.messageSource = messageSource; }

    @GetMapping("/banking")
    public String banking(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : MESSAGE_KEYS) messages.put(key, messageSource.getMessage(key, null, locale));
        model.addAttribute("messages", messages);
        return "banking/list";
    }
}
