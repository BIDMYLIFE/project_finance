package com.example.erp.dto;
import com.example.erp.entity.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
public record InvoiceResponse(UUID id, String invoiceNumber, InvoiceStatus status, UUID customerId, String customerName, UUID sourceQuoteId, String currencyCode, LocalDate invoiceDate, LocalDate dueDate, BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal, BigDecimal paidTotal, BigDecimal balanceDue, Instant createdAt, List<InvoiceLineResponse> lines) { public static InvoiceResponse from(Invoice i,String customerName,List<InvoiceLine> lines,InvoiceStatus status){return new InvoiceResponse(i.getId(),i.getInvoiceNumber(),status,i.getCustomerId(),customerName,i.getSourceQuoteId(),i.getCurrencyCode(),i.getInvoiceDate(),i.getDueDate(),i.getSubtotal(),i.getTaxTotal(),i.getGrandTotal(),i.getPaidTotal(),i.getBalanceDue(),i.getCreatedAt(),lines.stream().map(InvoiceLineResponse::from).toList());} }
