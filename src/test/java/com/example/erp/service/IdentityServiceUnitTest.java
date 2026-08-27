package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.erp.entity.Organization;
import com.example.erp.entity.User;
import com.example.erp.repository.OrganizationRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.UserRoleRepository;
import com.example.erp.security.AuthPrincipal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IdentityServiceUnitTest {
    @Test
    void currentIdentityLoadsEmailFromUserRepositoryInsteadOfJwtPlaceholder() {
        UserRepository users = mock(UserRepository.class);
        OrganizationRepository organizations = mock(OrganizationRepository.class);
        UserRoleRepository userRoles = mock(UserRoleRepository.class);
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        User user = new User(userId, organizationId, "real@example.invalid", "hash", Instant.now());
        when(users.findByIdAndOrganizationId(userId, organizationId)).thenReturn(Optional.of(user));
        when(organizations.findById(organizationId)).thenReturn(Optional.of(new Organization(organizationId, "Southwnd", Instant.now())));
        when(userRoles.findByUserId(userId)).thenReturn(List.of());

        var response = new IdentityService(users, organizations, userRoles)
                .currentIdentity(new AuthPrincipal(userId, organizationId, "jwt", UUID.randomUUID()));

        assertThat(response.email()).isEqualTo("real@example.invalid");
    }
}
