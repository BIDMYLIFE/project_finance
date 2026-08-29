package com.example.erp.repository;

import com.example.erp.entity.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    java.util.List<Customer> findByOrganizationId(UUID organizationId);
    Optional<Customer> findByIdAndOrganizationId(UUID id, UUID organizationId);
    boolean existsByOrganizationIdAndCustomerCode(UUID organizationId, String customerCode);
    Page<Customer> findByOrganizationIdAndActiveAndNameContainingIgnoreCase(UUID organizationId, boolean active, String name, Pageable pageable);
}
