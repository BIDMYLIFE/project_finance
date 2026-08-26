package com.example.erp.dto;

import com.example.erp.security.AuthPrincipal;
import java.util.UUID;

public record CurrentIdentityResponse(UUID userId, String email, UUID organizationId, String organizationName, String roleName) {
    public static CurrentIdentityResponse from(AuthPrincipal principal) {
        return new CurrentIdentityResponse(principal.userId(), principal.email(), principal.organizationId(), null, null);
    }
}