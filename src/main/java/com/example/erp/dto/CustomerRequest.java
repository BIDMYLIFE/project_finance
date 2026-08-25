package com.example.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(@NotBlank @Size(max = 80) String customerCode, @NotBlank @Size(max = 200) String name,
                              @Email @Size(max = 320) String email, @Size(max = 50) String phone) {}