package com.example.erp.repository;

import com.example.erp.entity.BankTransaction;
import com.example.erp.entity.BankTransactionDirection;
import com.example.erp.entity.BankTransactionStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    java.util.Optional<BankTransaction> findByIdAndOrganizationId(UUID id, UUID organizationId);
    java.util.List<BankTransaction> findByOrganizationIdAndBankAccountIdAndStatus(UUID organizationId, UUID bankAccountId, BankTransactionStatus status);
    boolean existsByOrganizationIdAndSourceTypeAndSourceIdAndStatus(UUID organizationId, String sourceType, UUID sourceId, BankTransactionStatus status);
    java.util.Optional<BankTransaction> findByOrganizationIdAndSourceTypeAndSourceIdAndStatus(UUID organizationId, String sourceType, UUID sourceId, BankTransactionStatus status);
    @Query("select coalesce(sum(t.amount), 0) from BankTransaction t where t.organizationId = :organizationId and t.bankAccountId = :accountId and t.status = :status and t.direction = :direction")
    BigDecimal sumAmount(@Param("organizationId") UUID organizationId, @Param("accountId") UUID accountId, @Param("status") BankTransactionStatus status, @Param("direction") BankTransactionDirection direction);
}
