package com.example.erp.repository;

import com.example.erp.entity.PaymentAllocation;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {
    List<PaymentAllocation> findByOrganizationIdAndPaymentId(UUID organizationId, UUID paymentId);
    @Query("select coalesce(sum(a.amount), 0) from PaymentAllocation a where a.organizationId = :organizationId and a.paymentId = :paymentId")
    BigDecimal sumAmountByPayment(@Param("organizationId") UUID organizationId, @Param("paymentId") UUID paymentId);
    @Query("select coalesce(sum(a.amount), 0) from PaymentAllocation a where a.organizationId = :organizationId and a.invoiceId = :invoiceId")
    BigDecimal sumAmountByInvoice(@Param("organizationId") UUID organizationId, @Param("invoiceId") UUID invoiceId);
}