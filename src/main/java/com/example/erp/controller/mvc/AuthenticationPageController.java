package com.example.erp.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationPageController {
    @GetMapping("/bootstrap") public String bootstrap() { return "auth/bootstrap"; }
    @GetMapping("/login") public String login() { return "auth/login"; }
}