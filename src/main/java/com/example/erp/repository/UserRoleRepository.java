package com.example.erp.repository;

import com.example.erp.entity.UserRole;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.Key> {
    List<UserRole> findByUserId(UUID userId);
}