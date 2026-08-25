package com.example.erp.repository;

import com.example.erp.entity.ReceiptPrint;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptPrintRepository extends JpaRepository<ReceiptPrint, UUID> {
    List<ReceiptPrint> findByOrganizationIdAndPaymentIdOrderByPrintedAtAsc(UUID organizationId, UUID paymentId);
}