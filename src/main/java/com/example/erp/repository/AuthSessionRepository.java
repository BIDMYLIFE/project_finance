package com.example.erp.repository;

import com.example.erp.entity.AuthSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {
    Optional<AuthSession> findByRefreshTokenHash(byte[] hash);
}