package com.example.erp.repository;

import com.example.erp.entity.QuoteLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteLineRepository extends JpaRepository<QuoteLine, UUID> {
    List<QuoteLine> findByQuoteId(UUID quoteId);
    void deleteByQuoteId(UUID quoteId);
}
