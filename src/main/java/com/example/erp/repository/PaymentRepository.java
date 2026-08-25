package com.example.erp.repository;

import com.example.erp.entity.Payment;
import com.example.erp.entity.PaymentStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Optional<Payment> findByIdAndOrganizationIdAndStatus(UUID id, UUID organizationId, PaymentStatus status);
    Page<Payment> findByOrganizationIdAndStatus(UUID organizationId, PaymentStatus status, Pageable pageable);
}