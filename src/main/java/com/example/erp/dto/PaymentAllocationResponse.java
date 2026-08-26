package com.example.erp.dto;
import com.example.erp.entity.PaymentAllocation; import java.math.BigDecimal; import java.util.UUID;
public record PaymentAllocationResponse(UUID id,UUID invoiceId,BigDecimal amount){public static PaymentAllocationResponse from(PaymentAllocation a){return new PaymentAllocationResponse(a.getId(),a.getInvoiceId(),a.getAmount());}}
