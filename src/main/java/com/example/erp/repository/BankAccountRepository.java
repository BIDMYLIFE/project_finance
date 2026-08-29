package com.example.erp.repository;

import com.example.erp.entity.BankAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    java.util.List<BankAccount> findByOrganizationId(UUID organizationId);
    Optional<BankAccount> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<BankAccount> findByOrganizationIdAndActiveAndAccountNameContainingIgnoreCase(UUID organizationId, boolean active, String accountName, Pageable pageable);
    boolean existsByOrganizationIdAndAccountNameIgnoreCase(UUID organizationId, String accountName);
    boolean existsByOrganizationIdAndAccountNameIgnoreCaseAndIdNot(UUID organizationId, String accountName, UUID id);
}
