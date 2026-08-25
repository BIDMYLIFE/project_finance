package com.example.erp.reporting;

import java.util.UUID;

public record SourceReference(String sourceType, UUID sourceId, String displayNumber, String status) {}