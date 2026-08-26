package com.example.erp.repository;

import com.example.erp.entity.Quote;
import com.example.erp.entity.QuoteStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    Optional<Quote> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("select q from Quote q join Customer c on c.id = q.customerId "
            + "where q.organizationId = :organizationId "
            + "and (:keyword = '' or lower(coalesce(q.quoteNumber, '')) like lower(concat('%', :keyword, '%')) "
            + "or lower(c.name) like lower(concat('%', :keyword, '%'))) "
            + "and (:status is null or q.status = :status)")
    Page<Quote> search(@Param("organizationId") UUID organizationId, @Param("keyword") String keyword,
            @Param("status") QuoteStatus status, Pageable pageable);
}
