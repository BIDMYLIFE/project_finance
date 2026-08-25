package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions", schema = "SOUTHWND")
public class AuthSession {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "refresh_token_hash", nullable = false, unique = true) private byte[] refreshTokenHash;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "last_used_at") private Instant lastUsedAt;
    @Column(name = "revoked_at") private Instant revokedAt;

    protected AuthSession() {}
    public AuthSession(UUID id, UUID userId, UUID organizationId, byte[] hash, Instant createdAt, Instant expiresAt) {
        this.id = id; this.userId = userId; this.organizationId = organizationId; this.refreshTokenHash = hash;
        this.createdAt = createdAt; this.expiresAt = expiresAt;
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getOrganizationId() { return organizationId; }
    public byte[] getRefreshTokenHash() { return refreshTokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void rotate(byte[] hash, Instant expiry, Instant usedAt) { this.refreshTokenHash = hash; this.expiresAt = expiry; this.lastUsedAt = usedAt; }
    public void revoke(Instant at) { if (revokedAt == null) revokedAt = at; }
}