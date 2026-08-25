package com.example.erp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class MoneyPolicy {
    public static final int DEFAULT_SCALE = 2;
    public BigDecimal amount(BigDecimal value) { return requireNonNegative(value).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP); }
    public BigDecimal quantity(BigDecimal value) {
        if (value == null || value.signum() <= 0) throw new IllegalArgumentException("Quantity must be positive");
        return value.setScale(4, RoundingMode.HALF_UP);
    }
    public BigDecimal tax(BigDecimal taxableAmount, BigDecimal taxRate) {
        if (taxRate == null || taxRate.signum() < 0) throw new IllegalArgumentException("Tax rate must be non-negative");
        return amount(requireNonNegative(taxableAmount).multiply(taxRate).divide(BigDecimal.valueOf(100), DEFAULT_SCALE, RoundingMode.HALF_UP));
    }
    public BigDecimal lineTotal(BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount) {
        BigDecimal subtotal = quantity(quantity).multiply(amount(unitPrice));
        BigDecimal safeDiscount = discount == null ? BigDecimal.ZERO : requireNonNegative(discount);
        return amount(subtotal.subtract(safeDiscount));
    }
    private BigDecimal requireNonNegative(BigDecimal value) {
        if (value == null || value.signum() < 0) throw new IllegalArgumentException("Amount must be non-negative");
        return value;
    }
}