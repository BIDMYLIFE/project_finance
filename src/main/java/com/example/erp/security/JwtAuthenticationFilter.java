package com.example.erp.security;

import com.example.erp.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityProperties properties;
    public JwtAuthenticationFilter(JwtService jwtService, SecurityProperties properties) { this.jwtService = jwtService; this.properties = properties; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = token(request);
        if (token != null) {
            try {
                Claims claims = jwtService.parse(token);
                AuthPrincipal principal = new AuthPrincipal(UUID.fromString(claims.getSubject()), UUID.fromString(claims.get("org", String.class)), "jwt", UUID.fromString(claims.get("sid", String.class)));
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
    private String token(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) if (properties.getCookie().getAccessName().equals(cookie.getName())) return cookie.getValue();
        return null;
    }
}