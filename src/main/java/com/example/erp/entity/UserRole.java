package com.example.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_roles", schema = "SOUTHWND")
@IdClass(UserRole.Key.class)
public class UserRole {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Id
    @Column(name = "role_name")
    private String roleName;

    protected UserRole() {}
    public UserRole(UUID userId, String roleName) { this.userId = userId; this.roleName = roleName; }
    public static final class Key implements Serializable {
        private UUID userId;
        private String roleName;
        public Key() {}
        public Key(UUID userId, String roleName) { this.userId = userId; this.roleName = roleName; }
        public UUID getUserId() { return userId; }
        public String getRoleName() { return roleName; }
        @Override public boolean equals(Object value) { return value instanceof Key other && userId.equals(other.userId) && roleName.equals(other.roleName); }
        @Override public int hashCode() { return 31 * userId.hashCode() + roleName.hashCode(); }
    }
}