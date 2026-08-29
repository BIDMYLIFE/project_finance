package com.example.erp.repository;

import com.example.erp.entity.Expense;
import com.example.erp.entity.ExpenseStatus;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Optional<Expense> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Optional<Expense> findByIdAndOrganizationIdAndStatus(UUID id, UUID organizationId, ExpenseStatus status);
    @Query("select e from Expense e where e.organizationId = :organizationId and (:status is null or e.status = :status) and (:categoryId is null or e.categoryId = :categoryId) and (:bankAccountId is null or e.bankAccountId = :bankAccountId) and (:keyword is null or :keyword = '' or lower(e.payeeName) like lower(concat('%', :keyword, '%')) or lower(e.description) like lower(concat('%', :keyword, '%'))) and (:fromDate is null or e.expenseDate >= :fromDate) and (:toDate is null or e.expenseDate <= :toDate)")
    Page<Expense> search(@Param("organizationId") UUID organizationId, @Param("status") ExpenseStatus status,
                          @Param("categoryId") UUID categoryId, @Param("bankAccountId") UUID bankAccountId,
                          @Param("keyword") String keyword, @Param("fromDate") LocalDate fromDate,
                          @Param("toDate") LocalDate toDate, Pageable pageable);
}
