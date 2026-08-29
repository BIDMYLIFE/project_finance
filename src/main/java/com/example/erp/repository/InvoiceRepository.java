package com.example.erp.repository;

import com.example.erp.entity.Invoice;
import com.example.erp.entity.InvoiceStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    java.util.List<Invoice> findByOrganizationId(UUID organizationId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Invoice> findWithLockByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Invoice> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("select i from Invoice i join Customer c on c.id=i.customerId where i.organizationId=:org and (:keyword='' or lower(coalesce(i.invoiceNumber,'')) like lower(concat('%',:keyword,'%')) or lower(c.name) like lower(concat('%',:keyword,'%'))) and (:status is null or i.status=:status) and (:fromDate is null or i.invoiceDate>=:fromDate) and (:toDate is null or i.invoiceDate<=:toDate)")
    Page<Invoice> search(@Param("org") UUID org, @Param("keyword") String keyword, @Param("status") InvoiceStatus status, @Param("fromDate") java.time.LocalDate fromDate, @Param("toDate") java.time.LocalDate toDate, Pageable pageable);

    @Query("select i from Invoice i join Customer c on c.id=i.customerId where i.organizationId=:org and i.status=com.example.erp.entity.InvoiceStatus.ISSUED and i.balanceDue>0 and i.dueDate< CURRENT_DATE and (:keyword='' or lower(coalesce(i.invoiceNumber,'')) like lower(concat('%',:keyword,'%')) or lower(c.name) like lower(concat('%',:keyword,'%'))) and (:fromDate is null or i.invoiceDate>=:fromDate) and (:toDate is null or i.invoiceDate<=:toDate)")
    Page<Invoice> searchOverdue(@Param("org") UUID org, @Param("keyword") String keyword, @Param("fromDate") java.time.LocalDate fromDate, @Param("toDate") java.time.LocalDate toDate, Pageable pageable);
}
