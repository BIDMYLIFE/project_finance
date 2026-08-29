package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportingPageController {
    private final MessageSource messages;
    ReportingPageController(MessageSource messages) { this.messages = messages; }

    @GetMapping("/reporting")
    public String reporting(Model model, Locale locale) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String key : new String[]{"reporting.title", "reporting.eyebrow", "reporting.heading", "reporting.description",
                "reporting.nav.dashboard", "reporting.nav.logout", "reporting.action.search", "reporting.action.export",
                "reporting.loading", "reporting.empty", "reporting.error.load", "reporting.error.export", "reporting.filter.type",
                "reporting.filter.from", "reporting.filter.to", "reporting.filter.currency", "reporting.filter.all",
                "reporting.table.source", "reporting.table.date", "reporting.table.currency", "reporting.table.amount",
                "reporting.summary.count", "reporting.summary.amount", "reporting.summary.credit", "reporting.summary.debit", "reporting.summary.net"})
            values.put(key, messages.getMessage(key, null, locale));
        model.addAttribute("messages", values);
        return "reporting/list";
    }
}
