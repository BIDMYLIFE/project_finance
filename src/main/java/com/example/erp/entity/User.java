package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "SOUTHWND")
public class User {
    @Id
    private UUID id;
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    @Column(nullable = false, unique = true, length = 320)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 512)
    private String passwordHash;
    @Column(nullable = false)
    private boolean enabled;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected User() {}
    public User(UUID id, UUID organizationId, String email, String passwordHash, Instant createdAt) {
        this.id = id; this.organizationId = organizationId; this.email = email; this.passwordHash = passwordHash;
        this.enabled = true; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEnabled() { return enabled; }
}