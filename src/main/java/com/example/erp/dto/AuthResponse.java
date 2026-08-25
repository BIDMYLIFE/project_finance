package com.example.erp.dto;

import java.util.UUID;

public record AuthResponse(String status, UUID userId, UUID organizationId) {
    public static AuthResponse success(UUID userId, UUID organizationId) { return new AuthResponse("ok", userId, organizationId); }
}