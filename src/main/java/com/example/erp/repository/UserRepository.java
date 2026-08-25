package com.example.erp.repository;

import com.example.erp.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByIdAndOrganizationId(UUID id, UUID organizationId);
    boolean existsByEmailIgnoreCase(String email);
}