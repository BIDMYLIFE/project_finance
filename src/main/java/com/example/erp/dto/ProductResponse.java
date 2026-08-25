package com.example.erp.dto;

import com.example.erp.entity.Product;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String productCode, String name, String description, BigDecimal unitPrice, String currencyCode, BigDecimal taxRate, boolean active) {
    public static ProductResponse from(Product value) { return new ProductResponse(value.getId(), value.getProductCode(), value.getName(), value.getDescription(), value.getUnitPrice(), value.getCurrencyCode(), value.getTaxRate(), value.isActive()); }
}