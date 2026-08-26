package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductsPageController {
    private static final String[] MESSAGE_KEYS = {
            "products.title", "products.eyebrow", "products.heading", "products.description",
            "products.nav.dashboard", "products.nav.logout", "products.nav.logging_out",
            "products.action.add", "products.action.search", "products.action.clear", "products.action.edit",
            "products.action.deactivate", "products.action.retry", "products.filter.keyword", "products.filter.status",
            "products.filter.active", "products.filter.inactive", "products.table.code", "products.table.name",
            "products.table.price", "products.table.currency", "products.table.tax", "products.table.status",
            "products.table.actions", "products.status.active", "products.status.inactive", "products.loading",
            "products.empty", "products.error.load", "products.error.save", "products.error.deactivate",
            "products.error.validation", "products.form.create_title", "products.form.edit_title", "products.form.code",
            "products.form.name", "products.form.description", "products.form.unit_price", "products.form.currency",
            "products.form.tax_rate", "products.form.active", "products.form.required", "products.form.invalid_code",
            "products.form.invalid_name", "products.form.invalid_description", "products.form.invalid_unit_price",
            "products.form.invalid_currency", "products.form.invalid_tax_rate", "products.form.save", "products.form.cancel",
            "products.confirm.title", "products.confirm.text", "products.confirm.confirm", "products.confirm.cancel",
            "products.success.created", "products.success.updated", "products.success.deactivated",
            "products.pagination.previous", "products.pagination.next", "products.pagination.of",
            "common.error.network", "common.error.timeout", "common.error.request_failed"
    };

    private final MessageSource messageSource;

    ProductsPageController(MessageSource messageSource) { this.messageSource = messageSource; }

    @GetMapping("/products")
    public String products(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : MESSAGE_KEYS) messages.put(key, messageSource.getMessage(key, null, locale));
        model.addAttribute("messages", messages);
        return "products/list";
    }
}