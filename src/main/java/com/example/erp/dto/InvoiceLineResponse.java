package com.example.erp.dto;
import com.example.erp.entity.InvoiceLine;
import java.math.BigDecimal;
import java.util.UUID;
public record InvoiceLineResponse(UUID id, UUID productId, String productName, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount, BigDecimal taxRate, BigDecimal lineTotal) { public static InvoiceLineResponse from(InvoiceLine l){return new InvoiceLineResponse(l.getId(),l.getProductId(),l.getProductName(),l.getDescription(),l.getQuantity(),l.getUnitPrice(),l.getDiscount(),l.getTaxRate(),l.getLineTotal());} }
