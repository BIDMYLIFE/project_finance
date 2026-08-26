package com.example.erp.repository;
import com.example.erp.entity.InvoiceLine;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine,UUID> { List<InvoiceLine> findByInvoiceId(UUID invoiceId); void deleteByInvoiceId(UUID invoiceId); }
