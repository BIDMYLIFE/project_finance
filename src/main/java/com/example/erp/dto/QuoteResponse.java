package com.example.erp.dto;

import com.example.erp.entity.Quote;
import com.example.erp.entity.QuoteLine;
import com.example.erp.entity.QuoteStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(UUID id, String quoteNumber, QuoteStatus status, UUID customerId, String customerName,
        String currencyCode, BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal,
        LocalDate validUntil, Instant createdAt, List<QuoteLineResponse> lines) {
    public static QuoteResponse from(Quote quote, String customerName, List<QuoteLine> lines, QuoteStatus status) {
        return new QuoteResponse(quote.getId(), quote.getQuoteNumber(), status, quote.getCustomerId(), customerName,
                quote.getCurrencyCode(), quote.getSubtotal(), quote.getTaxTotal(), quote.getGrandTotal(),
                quote.getValidUntil(), quote.getCreatedAt(), lines.stream().map(QuoteLineResponse::from).toList());
    }
}
