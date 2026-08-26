package com.example.erp.dto;

import com.example.erp.entity.QuoteLine;
import java.math.BigDecimal;
import java.util.UUID;

public record QuoteLineResponse(UUID id, UUID productId, String productName, String description,
        BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate, BigDecimal lineTotal) {
    public static QuoteLineResponse from(QuoteLine line) {
        return new QuoteLineResponse(line.getId(), line.getProductId(), line.getProductName(), line.getDescription(),
                line.getQuantity(), line.getUnitPrice(), line.getDiscount(), line.getTaxRate(), line.getLineTotal());
    }
}
