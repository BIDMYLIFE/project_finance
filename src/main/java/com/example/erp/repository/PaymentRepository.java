package com.example.erp.repository;

import com.example.erp.entity.Payment;
import com.example.erp.entity.PaymentStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Optional<Payment> findByIdAndOrganizationIdAndStatus(UUID id, UUID organizationId, PaymentStatus status);
    Page<Payment> findByOrganizationIdAndStatus(UUID organizationId, PaymentStatus status, Pageable pageable);
    @Query("select p from Payment p where p.organizationId = :organizationId and (:status is null or p.status = :status) and (:keyword = '' or lower(p.payerName) like lower(concat('%', :keyword, '%')) or lower(p.receiptNumber) like lower(concat('%', :keyword, '%')) or lower(p.reason) like lower(concat('%', :keyword, '%')))")
    Page<Payment> search(@Param("organizationId") UUID organizationId, @Param("keyword") String keyword, @Param("status") PaymentStatus status, Pageable pageable);
}
