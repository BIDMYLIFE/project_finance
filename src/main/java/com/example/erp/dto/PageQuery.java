package com.example.erp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;

public record PageQuery(@Min(0) int page, @Min(1) @Max(100) int size, String sort, String direction) {
    public PageQuery { if (size == 0) size = 20; if (sort == null || sort.isBlank()) sort = "createdAt"; }
    public Set<String> allowedDirections() { return Set.of("ASC", "DESC"); }
    public String safeDirection() { return allowedDirections().contains(direction == null ? "" : direction.toUpperCase()) ? direction.toUpperCase() : "DESC"; }
}