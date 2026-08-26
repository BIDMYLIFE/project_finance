package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.erp.dto.BankAccountRequest;
import com.example.erp.entity.BankAccount;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.BankAccountRepository;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankAccountServiceUnitTest {
    private BankAccountRepository accounts;
    private OrganizationContext context;
    private BankAccountService service;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        accounts = mock(BankAccountRepository.class);
        context = mock(OrganizationContext.class);
        organizationId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(organizationId);
        service = new BankAccountService(accounts, context);
    }

    @Test
    void duplicateNamesAreRejectedWithinOrganization() {
        when(accounts.existsByOrganizationIdAndAccountNameIgnoreCase(organizationId, "Operating")).thenReturn(true);
        assertThatThrownBy(() -> service.create(new BankAccountRequest(" Operating ", "TWD", BigDecimal.ZERO)))
                .isExactlyInstanceOf(BusinessRuleException.class);
        verify(accounts, never()).save(any());
    }

    @Test
    void foreignAccountCannotBeUpdated() {
        UUID id = UUID.randomUUID();
        when(accounts.findByIdAndOrganizationId(id, organizationId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, new BankAccountRequest("Operating", "TWD", BigDecimal.ZERO)))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
        verify(accounts).findByIdAndOrganizationId(eq(id), eq(organizationId));
    }

    @Test
    void deactivationIsIdempotentAndPreservesEntity() {
        BankAccount account = new BankAccount(UUID.randomUUID(), organizationId, "Operating", "TWD", BigDecimal.ZERO, Instant.now());
        account.deactivate();
        when(accounts.findByIdAndOrganizationId(account.getId(), organizationId)).thenReturn(Optional.of(account));
        service.deactivate(account.getId());
        verify(accounts, never()).delete(any());
        verify(accounts).findByIdAndOrganizationId(account.getId(), organizationId);
    }
}
