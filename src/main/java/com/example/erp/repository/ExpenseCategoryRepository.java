package com.example.erp.repository;

import com.example.erp.entity.ExpenseCategory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {
    java.util.List<ExpenseCategory> findByOrganizationId(UUID organizationId);
    Optional<ExpenseCategory> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<ExpenseCategory> findByOrganizationIdAndActiveAndNameContainingIgnoreCase(
            UUID organizationId, boolean active, String name, Pageable pageable);
    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);
    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id);
}
