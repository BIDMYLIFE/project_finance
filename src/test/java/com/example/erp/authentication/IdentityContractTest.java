package com.example.erp.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.dto.CurrentIdentityResponse;
import com.example.erp.security.AuthPrincipal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IdentityContractTest {

    @Test
    void currentIdentityResponseUsesServerPrincipalValues() {
        UUID serverOrgId = UUID.randomUUID();
        UUID clientForgedOrgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), serverOrgId, "admin@example.invalid", UUID.randomUUID());

        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.organizationId()).isEqualTo(serverOrgId);
        assertThat(response.organizationId()).isNotEqualTo(clientForgedOrgId);
    }

    @Test
    void currentIdentityResponseDoesNotExposeSessionId() {
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "admin@example.invalid", UUID.randomUUID());
        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.getClass().getRecordComponents()).extracting("name")
                .doesNotContain("sessionId");
    }

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
    void identityDoesNotAcceptClientSuppliedOrganizationId() {
        UUID serverOrgId = UUID.randomUUID();
        UUID clientForgedOrgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), serverOrgId, "admin@example.invalid", UUID.randomUUID());

        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.organizationId()).isEqualTo(serverOrgId);
        assertThat(response.organizationId()).isNotEqualTo(clientForgedOrgId);
    }

    @Test
    void identityDoesNotAcceptClientSuppliedUserId() {
        UUID serverUserId = UUID.randomUUID();
        UUID clientForgedUserId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(serverUserId, UUID.randomUUID(), "admin@example.invalid", UUID.randomUUID());

        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.userId()).isEqualTo(serverUserId);
        assertThat(response.userId()).isNotEqualTo(clientForgedUserId);
    }

    @Test
    void identityEmailComesFromPrincipalNotRequest() {
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "real@example.invalid", UUID.randomUUID());
        CurrentIdentityResponse response = CurrentIdentityResponse.from(principal);

        assertThat(response.email()).isEqualTo("real@example.invalid");
    }
}
