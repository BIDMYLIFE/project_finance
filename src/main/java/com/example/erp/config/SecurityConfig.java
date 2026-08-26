package com.example.erp.config;

import com.example.erp.security.JwtAuthenticationFilter;
import com.example.erp.security.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(SecurityProperties properties) {
        SecurityProperties.Password password = properties.getPassword();
        return new Argon2PasswordEncoder(password.getSaltLength(), password.getHashLength(), password.getParallelism(),
            password.getMemory(), password.getIterations());
    }
    @Bean JwtAuthenticationFilter jwtAuthenticationFilter(JwtService service, SecurityProperties properties) { return new JwtAuthenticationFilter(service, properties); }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**", "/auth/**", "/css/**", "/js/**", "/vendor/**", "/webjars/**", "/favicon.ico").permitAll().anyRequest().authenticated())
                .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) -> {
                    if ("/".equals(request.getRequestURI())) response.sendRedirect("/auth/login");
                    else writeError(response, 401, "UNAUTHENTICATED", "Authentication is required");
                })
                        .accessDeniedHandler((request, response, exception) -> writeError(response, 403, "FORBIDDEN", "Access is forbidden")))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
    }
    private static void writeError(HttpServletResponse response, int status, String code, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().printf("{\"code\":\"%s\",\"message\":\"%s\"}", code, message);
    }
}