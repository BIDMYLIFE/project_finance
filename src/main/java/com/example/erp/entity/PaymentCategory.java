package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_categories", schema = "SOUTHWND")
public class PaymentCategory {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected PaymentCategory() {}
    public PaymentCategory(UUID id, UUID organizationId, String name, Instant createdAt) { this.id = id; this.organizationId = organizationId; this.name = name; this.active = true; this.createdAt = createdAt; }
    public void rename(String name) { this.name = name; }
    public void deactivate() { active = false; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
