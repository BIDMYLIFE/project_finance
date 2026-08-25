package com.example.erp.repository;

import com.example.erp.entity.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByIdAndOrganizationId(UUID id, UUID organizationId);
    boolean existsByOrganizationIdAndProductCode(UUID organizationId, String productCode);
    Page<Product> findByOrganizationIdAndActiveAndNameContainingIgnoreCase(UUID organizationId, boolean active, String name, Pageable pageable);
}