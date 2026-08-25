package com.example.erp.dto;

import com.example.erp.entity.Customer;
import java.util.UUID;

public record CustomerResponse(UUID id, String customerCode, String name, String email, String phone, boolean active) {
    public static CustomerResponse from(Customer value) { return new CustomerResponse(value.getId(), value.getCustomerCode(), value.getName(), value.getEmail(), value.getPhone(), value.isActive()); }
}