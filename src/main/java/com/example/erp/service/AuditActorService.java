package com.example.erp.service;

import com.example.erp.security.OrganizationContext;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditActorService {
    private final OrganizationContext organizationContext;
    public AuditActorService(OrganizationContext organizationContext) { this.organizationContext = organizationContext; }
    public UUID currentActorId() { return organizationContext.requiredActorId(); }
    public UUID currentOrganizationId() { return organizationContext.requiredOrganizationId(); }
}