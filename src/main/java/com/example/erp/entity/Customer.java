package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers", schema = "SOUTHWND")
public class Customer {
    @Id private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    @Column(name = "customer_code", nullable = false, length = 80) private String customerCode;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 320) private String email;
    @Column(length = 50) private String phone;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Customer() {}
    public Customer(UUID id, UUID organizationId, String customerCode, String name, String email, String phone, Instant now) {
        this.id = id; this.organizationId = organizationId; this.customerCode = customerCode; this.name = name;
        this.email = email; this.phone = phone; this.active = true; this.createdAt = now; this.updatedAt = now;
    }
    public void update(String name, String email, String phone, Instant now) { this.name = name; this.email = email; this.phone = phone; this.updatedAt = now; }
    public void deactivate(Instant now) { active = false; updatedAt = now; }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getCustomerCode() { return customerCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
}