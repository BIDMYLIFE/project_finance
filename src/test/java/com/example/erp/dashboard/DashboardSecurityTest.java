package com.example.erp.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.dto.CurrentIdentityResponse;
import com.example.erp.dto.ErrorResponse;
import com.example.erp.security.AuthPrincipal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DashboardSecurityTest {

    @Test
    void identityResponseExcludesSensitiveFields() {
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "user@example.invalid", UUID.randomUUID());
        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.userId()).isNotNull();
        assertThat(response.email()).isNotNull();
        assertThat(response.organizationId()).isNotNull();

        assertThat(response.getClass().getRecordComponents()).extracting("name")
                .doesNotContain("password", "token", "secret", "sessionId", "accessToken", "refreshToken");
    }

    @Test
    void identityResponseDoesNotContainPassword() {
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "user@example.invalid", UUID.randomUUID());
        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.toString()).doesNotContain("password");
    }

    @Test
    void errorResponseFormatDoesNotExposeInternals() {
        ErrorResponse error = new ErrorResponse("AUTHENTICATION_FAILED", "Authentication failed", java.util.Map.of(), Instant.now());
        assertThat(error.code()).isEqualTo("AUTHENTICATION_FAILED");
        assertThat(error.message()).doesNotContain("token", "secret", "password", "hash", "key");
        assertThat(error.fields()).isEmpty();
    }

    @Test
    void sessionErrorResponseFormatDoesNotExposeInternals() {
        ErrorResponse error = new ErrorResponse("INVALID_SESSION", "Session is invalid", java.util.Map.of(), Instant.now());
        assertThat(error.code()).isEqualTo("INVALID_SESSION");
        assertThat(error.message()).doesNotContain("token", "secret", "password", "hash", "key");
    }

    @Test
    void internalErrorResponseFormatDoesNotExposeStackTraces() {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "The request could not be completed", java.util.Map.of(), Instant.now());
        assertThat(error.code()).isEqualTo("INTERNAL_ERROR");
        assertThat(error.message()).doesNotContain("Exception", "stack", "trace");
    }

    @Test
    void errorResponseTimestampIsPresent() {
        ErrorResponse error = new ErrorResponse("AUTHENTICATION_FAILED", "Authentication failed", java.util.Map.of(), Instant.now());
        assertThat(error.timestamp()).isBeforeOrEqualTo(Instant.now());
    }
}
