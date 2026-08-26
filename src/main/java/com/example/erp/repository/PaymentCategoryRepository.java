package com.example.erp.repository;

import com.example.erp.entity.PaymentCategory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, UUID> {
    Optional<PaymentCategory> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<PaymentCategory> findByOrganizationIdAndActive(UUID organizationId, boolean active, Pageable pageable);
    Page<PaymentCategory> findByOrganizationId(UUID organizationId, Pageable pageable);
    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);
}
