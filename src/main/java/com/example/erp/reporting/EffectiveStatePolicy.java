package com.example.erp.reporting;

import com.example.erp.entity.BankTransactionStatus;
import com.example.erp.entity.PaymentStatus;

public final class EffectiveStatePolicy {
    private EffectiveStatePolicy() {}
    public static boolean paymentCounts(PaymentStatus status) { return status != null && status != PaymentStatus.VOIDED; }
    public static boolean bankTransactionCounts(BankTransactionStatus status) { return status == BankTransactionStatus.POSTED; }
}