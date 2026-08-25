package com.example.erp.repository;

import com.example.erp.entity.BankAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<BankAccount> findByOrganizationId(Pageable pageable, UUID organizationId);
    boolean existsByOrganizationIdAndAccountNameIgnoreCase(UUID organizationId, String accountName);
}