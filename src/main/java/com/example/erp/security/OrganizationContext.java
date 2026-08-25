package com.example.erp.security;

import java.util.UUID;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class OrganizationContext {
    public UUID requiredOrganizationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal))
            throw new AuthenticationCredentialsNotFoundException("Authenticated organization is required");
        return principal.organizationId();
    }
    public UUID requiredActorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal))
            throw new AuthenticationCredentialsNotFoundException("Authenticated actor is required");
        return principal.userId();
    }
}