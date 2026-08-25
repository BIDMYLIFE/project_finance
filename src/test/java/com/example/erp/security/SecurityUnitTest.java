package com.example.erp.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

class SecurityUnitTest {
    @Test
    void argon2idHashVerifiesWithoutStoringPlaintext() {
        var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        String password = "test-only-password";
        String hash = encoder.encode(password);
        assertThat(hash).startsWith("$argon2id$");
        assertThat(hash).doesNotContain(password);
        assertThat(encoder.matches(password, hash)).isTrue();
    }

    @Test
    void jwtContainsRequiredClaimsAndExpiry() {
        SecurityProperties properties = properties();
        JwtService service = new JwtService(properties);
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String token = service.createAccessToken(new AuthPrincipal(userId, organizationId, "test@example.invalid", sessionId), sessionId, now);
        Claims claims = service.parse(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("org", String.class)).isEqualTo(organizationId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("sid", String.class)).isEqualTo(sessionId.toString());
        assertThat(claims.getExpiration().toInstant()).isEqualTo(now.plus(Duration.ofMinutes(15)));
    }

    @Test
    void authenticationCookieIsHttpOnlySecureAndSameSite() {
        SecurityProperties properties = properties();
        MockHttpServletResponse response = new MockHttpServletResponse();
        new AuthenticationCookie(properties).set(response, "access-test", "refresh-test");
        assertThat(response.getHeaders("Set-Cookie")).allMatch(value -> value.contains("HttpOnly") && value.contains("Secure") && value.contains("SameSite=Strict"));
    }

    private SecurityProperties properties() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setSecret("01234567890123456789012345678901");
        return properties;
    }
}