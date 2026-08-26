package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomersPageController {
    private static final String[] MESSAGE_KEYS = {
            "customers.title", "customers.eyebrow", "customers.heading", "customers.description",
            "customers.nav.dashboard", "customers.nav.logout", "customers.nav.logging_out",
            "customers.action.add", "customers.action.search", "customers.action.clear",
            "customers.action.edit", "customers.action.deactivate", "customers.action.retry",
            "customers.filter.keyword", "customers.filter.status", "customers.filter.active",
            "customers.filter.inactive", "customers.table.code", "customers.table.name",
            "customers.table.email", "customers.table.phone", "customers.table.status",
            "customers.table.actions", "customers.status.active", "customers.status.inactive",
            "customers.loading", "customers.empty", "customers.error.load", "customers.error.save",
            "customers.error.deactivate", "customers.error.validation", "customers.form.create_title",
            "customers.form.edit_title", "customers.form.code", "customers.form.name",
            "customers.form.email", "customers.form.phone", "customers.form.required",
            "customers.form.invalid_email", "customers.form.invalid_customerCode", "customers.form.invalid_name",
            "customers.form.invalid_phone", "customers.form.save", "customers.form.cancel",
            "customers.confirm.title", "customers.confirm.text", "customers.confirm.confirm",
            "customers.confirm.cancel", "customers.success.created", "customers.success.updated",
            "customers.success.deactivated", "customers.pagination.previous", "customers.pagination.next",
            "customers.pagination.of", "common.error.network", "common.error.timeout", "common.error.request_failed"
    };
    private final MessageSource messageSource;
    CustomersPageController(MessageSource messageSource) { this.messageSource = messageSource; }
    @GetMapping("/customers")
    public String customers(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : MESSAGE_KEYS) messages.put(key, messageSource.getMessage(key, null, locale));
        model.addAttribute("messages", messages);
        return "customers/list";
    }
}