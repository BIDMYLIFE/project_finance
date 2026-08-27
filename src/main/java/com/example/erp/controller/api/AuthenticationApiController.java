package com.example.erp.controller.api;

import com.example.erp.dto.AuthResponse;
import com.example.erp.dto.BootstrapRequest;
import com.example.erp.dto.CurrentIdentityResponse;
import com.example.erp.dto.LoginRequest;
import com.example.erp.entity.User;
import com.example.erp.security.AuthenticationCookie;
import com.example.erp.security.AuthPrincipal;
import com.example.erp.service.AuthService;
import com.example.erp.service.BootstrapService;
import com.example.erp.service.IdentityService;
import com.example.erp.config.SecurityProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationApiController {
    private final BootstrapService bootstrapService;
    private final AuthService authService;
    private final AuthenticationCookie cookies;
    private final SecurityProperties properties;
    private final IdentityService identityService;
    public AuthenticationApiController(BootstrapService bootstrapService, AuthService authService, AuthenticationCookie cookies, SecurityProperties properties,
                                        IdentityService identityService) {
        this.bootstrapService = bootstrapService; this.authService = authService; this.cookies = cookies; this.properties = properties;
        this.identityService = identityService;
    }
    @PostMapping("/bootstrap")
    public ResponseEntity<AuthResponse> bootstrap(@Valid @RequestBody BootstrapRequest request) {
        User user = bootstrapService.bootstrap(request);
        return ResponseEntity.status(201).body(AuthResponse.success(user.getId(), user.getOrganizationId()));
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request.email(), request.password());
        cookies.set(response, result.accessToken(), result.refreshToken());
        return AuthResponse.success(result.principal().userId(), result.principal().organizationId());
    }
    @org.springframework.web.bind.annotation.GetMapping("/me")
    public CurrentIdentityResponse currentIdentity(@AuthenticationPrincipal AuthPrincipal principal) {
        return identityService.currentIdentity(principal);
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.refresh(cookie(request, properties.getCookie().getRefreshName()));
        cookies.set(response, result.accessToken(), result.refreshToken());
        return AuthResponse.success(result.principal().userId(), result.principal().organizationId());
    }
    @PostMapping("/logout")
    public AuthResponse logout(@AuthenticationPrincipal AuthPrincipal principal, HttpServletResponse response) {
        authService.logout(principal == null ? null : principal.sessionId());
        cookies.clear(response);
        return new AuthResponse("ok", null, null);
    }
    private String cookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) throw new com.example.erp.exception.InvalidSessionException();
        for (Cookie cookie : request.getCookies()) if (name.equals(cookie.getName())) return cookie.getValue();
        throw new com.example.erp.exception.InvalidSessionException();
    }
}
