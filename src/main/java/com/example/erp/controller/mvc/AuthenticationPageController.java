package com.example.erp.controller.mvc;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationPageController {

    private final MessageSource messageSource;

    AuthenticationPageController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public String login(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        put(messages, "login.title", locale);
        put(messages, "login.heading", locale);
        put(messages, "login.label.email", locale);
        put(messages, "login.label.password", locale);
        put(messages, "login.button.submit", locale);
        put(messages, "login.button.logging_in", locale);
        put(messages, "login.error", locale);
        put(messages, "common.error.network", locale);
        put(messages, "common.error.timeout", locale);
        put(messages, "common.error.request_failed", locale);
        model.addAttribute("messages", messages);
        return "auth/login";
    }

    @GetMapping("/bootstrap")
    public String bootstrap(Model model, Locale locale) {
        Map<String, String> messages = new LinkedHashMap<>();
        put(messages, "bootstrap.title", locale);
        put(messages, "bootstrap.heading", locale);
        put(messages, "bootstrap.label.org_name", locale);
        put(messages, "bootstrap.label.admin_email", locale);
        put(messages, "bootstrap.label.admin_password", locale);
        put(messages, "bootstrap.button.submit", locale);
        put(messages, "bootstrap.button.processing", locale);
        put(messages, "bootstrap.success", locale);
        put(messages, "bootstrap.success_title", locale);
        put(messages, "bootstrap.success_text", locale);
        put(messages, "bootstrap.error", locale);
        put(messages, "common.error.network", locale);
        put(messages, "common.error.timeout", locale);
        put(messages, "common.error.request_failed", locale);
        model.addAttribute("messages", messages);
        return "auth/bootstrap";
    }

    private void put(Map<String, String> map, String key, Locale locale) {
        map.put(key, messageSource.getMessage(key, null, locale));
    }
}
