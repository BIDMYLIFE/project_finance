package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuotesPageController {
    private static final String[] MESSAGE_KEYS = {
            "quotes.title", "quotes.eyebrow", "quotes.heading", "quotes.description",
            "quotes.nav.dashboard", "quotes.nav.logout", "quotes.nav.logging_out",
            "quotes.action.add", "quotes.action.search", "quotes.action.clear", "quotes.action.edit",
            "quotes.action.submit", "quotes.action.accept", "quotes.action.reject", "quotes.action.cancel",
            "quotes.action.retry", "quotes.filter.keyword", "quotes.filter.status", "quotes.filter.all",
            "quotes.table.number", "quotes.table.customer", "quotes.table.created", "quotes.table.valid_until",
            "quotes.table.currency", "quotes.table.total", "quotes.table.status", "quotes.table.actions",
            "quotes.status.draft", "quotes.status.sent", "quotes.status.accepted", "quotes.status.rejected",
            "quotes.status.expired", "quotes.status.cancelled", "quotes.loading", "quotes.empty",
            "quotes.error.load", "quotes.error.save", "quotes.error.lifecycle", "quotes.error.validation",
            "quotes.form.create_title", "quotes.form.edit_title", "quotes.form.customer", "quotes.form.currency",
            "quotes.form.valid_until", "quotes.form.product", "quotes.form.quantity", "quotes.form.discount",
            "quotes.form.line_total", "quotes.form.subtotal", "quotes.form.tax_total", "quotes.form.grand_total",
            "quotes.form.add_line", "quotes.form.remove_line", "quotes.form.required", "quotes.form.invalid",
            "quotes.form.save", "quotes.form.cancel", "quotes.confirm.submit_title", "quotes.confirm.cancel_title",
            "quotes.confirm.text", "quotes.confirm.confirm", "quotes.confirm.cancel", "quotes.success.created",
            "quotes.success.updated", "quotes.success.submitted", "quotes.success.accepted", "quotes.success.rejected",
            "quotes.success.cancelled", "quotes.pagination.previous", "quotes.pagination.next", "quotes.pagination.of",
            "common.error.network", "common.error.timeout", "common.error.request_failed"
    };
    private final MessageSource messageSource;
    QuotesPageController(MessageSource messageSource) { this.messageSource = messageSource; }

    @GetMapping("/quotes")
    public String quotes(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : MESSAGE_KEYS) messages.put(key, messageSource.getMessage(key, null, locale));
        model.addAttribute("messages", messages);
        return "quotes/list";
    }
}
