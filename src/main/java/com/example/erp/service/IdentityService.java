package com.example.erp.service;

import com.example.erp.dto.CurrentIdentityResponse;
import com.example.erp.entity.User;
import com.example.erp.exception.InvalidSessionException;
import com.example.erp.repository.OrganizationRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.UserRoleRepository;
import com.example.erp.security.AuthPrincipal;
import org.springframework.stereotype.Service;

@Service
public class IdentityService {
    private final UserRepository users;
    private final OrganizationRepository organizations;
    private final UserRoleRepository userRoles;

    public IdentityService(UserRepository users, OrganizationRepository organizations, UserRoleRepository userRoles) {
        this.users = users;
        this.organizations = organizations;
        this.userRoles = userRoles;
    }

    public CurrentIdentityResponse currentIdentity(AuthPrincipal principal) {
        User user = users.findByIdAndOrganizationId(principal.userId(), principal.organizationId())
                .filter(User::isEnabled)
                .orElseThrow(InvalidSessionException::new);
        var organization = organizations.findById(principal.organizationId()).orElse(null);
        var roles = userRoles.findByUserId(user.getId());
        return new CurrentIdentityResponse(
                user.getId(),
                user.getEmail(),
                user.getOrganizationId(),
                organization == null ? null : organization.getName(),
                roles.isEmpty() ? null : roles.get(0).getRoleName()
        );
    }
}
