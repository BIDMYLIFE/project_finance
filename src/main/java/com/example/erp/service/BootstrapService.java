package com.example.erp.service;

import com.example.erp.config.SecurityProperties;
import com.example.erp.dto.BootstrapRequest;
import com.example.erp.entity.BootstrapState;
import com.example.erp.entity.Organization;
import com.example.erp.entity.User;
import com.example.erp.entity.UserRole;
import com.example.erp.exception.BootstrapConflictException;
import com.example.erp.repository.BootstrapStateRepository;
import com.example.erp.repository.OrganizationRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.UserRoleRepository;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootstrapService {
    private static final String STATE_KEY = "PRIMARY";
    private final BootstrapStateRepository states;
    private final OrganizationRepository organizations;
    private final UserRepository users;
    private final UserRoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final SecurityProperties properties;
    public BootstrapService(BootstrapStateRepository states, OrganizationRepository organizations, UserRepository users,
                            UserRoleRepository roles, PasswordEncoder passwordEncoder, SecurityProperties properties) {
        this.states = states; this.organizations = organizations; this.users = users; this.roles = roles;
        this.passwordEncoder = passwordEncoder; this.properties = properties;
    }
    @Transactional
    public User bootstrap(BootstrapRequest request) {
        if (!properties.getBootstrap().isEnabled()) throw new BootstrapConflictException();
        BootstrapState state = states.findByStateKey(STATE_KEY).orElseThrow(BootstrapConflictException::new);
        if (state.isInitialized() || users.existsByEmailIgnoreCase(request.email())) throw new BootstrapConflictException();
        Instant now = Instant.now();
        Organization organization = organizations.save(new Organization(UUID.randomUUID(), request.organizationName().trim(), now));
        User user = users.save(new User(UUID.randomUUID(), organization.getId(), request.email().trim().toLowerCase(Locale.ROOT),
                passwordEncoder.encode(request.password()), now));
        roles.save(new UserRole(user.getId(), "ADMIN"));
        state.initialize(now);
        states.save(state);
        return user;
    }
}