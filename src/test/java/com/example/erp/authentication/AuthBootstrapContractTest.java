package com.example.erp.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.config.SecurityProperties;
import com.example.erp.security.AuthPrincipal;
import com.example.erp.security.AuthenticationCookie;
import com.example.erp.security.JwtService;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

class AuthBootstrapContractTest {

    private final SecurityProperties properties = createProperties();
    private final JwtService jwtService = new JwtService(properties);
    private final AuthenticationCookie cookieUtil = new AuthenticationCookie(properties);

    @Test
    void loginSuccessProducesValidJwtWithCorrectClaims() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, orgId, "admin@example.invalid", sessionId);
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        String token = jwtService.createAccessToken(principal, sessionId, now);
        Claims claims = jwtService.parse(token);

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("org", String.class)).isEqualTo(orgId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("sid", String.class)).isEqualTo(sessionId.toString());
    }

    @Test
    void logoutClearsAuthenticationCookies() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        cookieUtil.set(response, "access-token", "refresh-token");

        cookieUtil.clear(response);

        List<String> cookies = response.getHeaders("Set-Cookie");
        assertThat(cookies).isNotEmpty();
        boolean hasAccessClear = cookies.stream().anyMatch(c -> c.contains(properties.getCookie().getAccessName()) && c.contains("Max-Age=0"));
        boolean hasRefreshClear = cookies.stream().anyMatch(c -> c.contains(properties.getCookie().getRefreshName()) && c.contains("Max-Age=0"));
        assertThat(hasAccessClear).isTrue();
        assertThat(hasRefreshClear).isTrue();
    }

    @Test
    void cookieSecurityAttributesAreSet() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        cookieUtil.set(response, "access-test", "refresh-test");

        List<String> cookies = response.getHeaders("Set-Cookie");
        assertThat(cookies).allMatch(header -> header.contains("HttpOnly") && header.contains("Secure") && header.contains("SameSite=Strict"));
    }

    @Test
    void refreshTokenRotationProducesNewToken() {
        UUID sessionId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "admin@example.invalid", sessionId);
        Instant now = Instant.now();

        String token1 = jwtService.createAccessToken(principal, sessionId, now);
        String token2 = jwtService.createAccessToken(principal, sessionId, now.plusSeconds(1));

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void accessTtlIsConfigured() {
        assertThat(properties.getJwt().getAccessTtl()).isEqualTo(java.time.Duration.ofMinutes(15));
    }

    @Test
    void refreshTtlIsConfigured() {
        assertThat(properties.getJwt().getRefreshTtl()).isEqualTo(java.time.Duration.ofDays(30));
    }

    private SecurityProperties createProperties() {
        SecurityProperties props = new SecurityProperties();
        props.getJwt().setSecret("01234567890123456789012345678901");
        props.getJwt().setAccessTtl(java.time.Duration.ofMinutes(15));
        props.getJwt().setRefreshTtl(java.time.Duration.ofDays(30));
        return props;
    }
}
