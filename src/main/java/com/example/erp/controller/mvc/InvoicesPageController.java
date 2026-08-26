package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InvoicesPageController {
    private static final String[] MESSAGE_KEYS = {"invoices.title", "invoices.eyebrow", "invoices.heading", "invoices.description", "invoices.nav.dashboard", "invoices.nav.logout", "invoices.nav.logging_out", "invoices.action.add", "invoices.action.search", "invoices.action.clear", "invoices.action.edit", "invoices.action.issue", "invoices.action.cancel", "invoices.action.detail", "invoices.action.retry", "invoices.filter.keyword", "invoices.filter.status", "invoices.filter.all", "invoices.filter.from", "invoices.filter.to", "invoices.table.number", "invoices.table.customer", "invoices.table.date", "invoices.table.due_date", "invoices.table.total", "invoices.table.balance", "invoices.table.status", "invoices.table.actions", "invoices.status.draft", "invoices.status.issued", "invoices.status.partially_paid", "invoices.status.paid", "invoices.status.overdue", "invoices.status.cancelled", "invoices.loading", "invoices.empty", "invoices.error.load", "invoices.error.save", "invoices.error.lifecycle", "invoices.error.validation", "invoices.form.create_title", "invoices.form.edit_title", "invoices.form.customer", "invoices.form.currency", "invoices.form.invoice_date", "invoices.form.due_date", "invoices.form.product", "invoices.form.quantity", "invoices.form.discount", "invoices.form.add_line", "invoices.form.remove_line", "invoices.form.subtotal", "invoices.form.tax_total", "invoices.form.grand_total", "invoices.form.required", "invoices.form.invalid", "invoices.form.save", "invoices.form.cancel", "invoices.confirm.issue_title", "invoices.confirm.cancel_title", "invoices.confirm.text", "invoices.confirm.confirm", "invoices.confirm.cancel", "invoices.success.created", "invoices.success.updated", "invoices.success.issued", "invoices.success.cancelled", "invoices.pagination.previous", "invoices.pagination.next", "invoices.pagination.of", "common.error.network", "common.error.timeout", "common.error.request_failed"};
    private final MessageSource messageSource;
    public InvoicesPageController(MessageSource messageSource) { this.messageSource = messageSource; }
    @GetMapping("/invoices")
    public String invoices(Model model, Locale locale) { Map<String, String> messages = new LinkedHashMap<>(); for (String key : MESSAGE_KEYS) messages.put(key, messageSource.getMessage(key, null, locale)); model.addAttribute("messages", messages); return "invoices/list"; }
}
