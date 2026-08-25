package com.example.erp.security;

import com.example.erp.config.SecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationCookie {
    private final SecurityProperties properties;
    public AuthenticationCookie(SecurityProperties properties) { this.properties = properties; }
    public void set(HttpServletResponse response, String access, String refresh) {
        response.addHeader("Set-Cookie", build(properties.getCookie().getAccessName(), access, properties.getJwt().getAccessTtl()).toString());
        response.addHeader("Set-Cookie", build(properties.getCookie().getRefreshName(), refresh, properties.getJwt().getRefreshTtl()).toString());
    }
    public void clear(HttpServletResponse response) {
        response.addHeader("Set-Cookie", build(properties.getCookie().getAccessName(), "", Duration.ZERO).toString());
        response.addHeader("Set-Cookie", build(properties.getCookie().getRefreshName(), "", Duration.ZERO).toString());
    }
    private ResponseCookie build(String name, String value, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value).httpOnly(true).secure(properties.getCookie().isSecure())
                .sameSite(properties.getCookie().getSameSite()).path(properties.getCookie().getPath()).maxAge(maxAge);
        if (!properties.getCookie().getDomain().isBlank()) builder.domain(properties.getCookie().getDomain());
        return builder.build();
    }
}