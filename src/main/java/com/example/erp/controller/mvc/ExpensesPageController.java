package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpensesPageController {
    private static final String[] KEYS = {
            "expenses.title", "expenses.eyebrow", "expenses.heading", "expenses.description",
            "expenses.nav.dashboard", "expenses.nav.logout", "expenses.nav.logging_out",
            "expenses.action.add", "expenses.action.search", "expenses.action.clear", "expenses.action.edit",
            "expenses.action.confirm", "expenses.action.void", "expenses.action.retry", "expenses.filter.keyword",
            "expenses.filter.status", "expenses.filter.all", "expenses.filter.draft", "expenses.filter.confirmed",
            "expenses.filter.voided", "expenses.table.payee", "expenses.table.description", "expenses.table.amount",
            "expenses.table.category", "expenses.table.account", "expenses.table.date", "expenses.table.status",
            "expenses.table.actions", "expenses.status.draft", "expenses.status.confirmed", "expenses.status.voided",
            "expenses.loading", "expenses.empty", "expenses.error.load", "expenses.error.save", "expenses.error.lifecycle",
            "expenses.error.validation", "expenses.form.create_title", "expenses.form.edit_title", "expenses.form.category",
            "expenses.form.bank_account", "expenses.form.payee", "expenses.form.description", "expenses.form.note",
            "expenses.form.amount", "expenses.form.currency", "expenses.form.date", "expenses.form.save",
            "expenses.form.cancel", "expenses.form.required", "expenses.confirm.title", "expenses.confirm.text",
            "expenses.confirm.confirm", "expenses.confirm.cancel", "expenses.success.created", "expenses.success.updated",
            "expenses.success.confirmed", "expenses.success.voided", "expenses.pagination.previous", "expenses.pagination.next",
            "expenses.pagination.of", "common.error.network", "common.error.timeout", "common.error.request_failed"
    };
    private final MessageSource messages;
    public ExpensesPageController(MessageSource messages) { this.messages = messages; }
    @GetMapping("/expenses")
    public String expenses(Model model, Locale locale) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String key : KEYS) values.put(key, messages.getMessage(key, null, locale));
        model.addAttribute("messages", values);
        return "expenses/list";
    }
}
