package com.example.erp.dto;

import java.util.List;

public record PaymentFromInvoicesResponse(PaymentResponse payment, List<PaymentAllocationResponse> allocations) {}
