package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.erp.dto.ExpenseCategoryRequest;
import com.example.erp.entity.ExpenseCategory;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.ExpenseCategoryRepository;
import com.example.erp.security.OrganizationContext;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseCategoryServiceUnitTest {
    private ExpenseCategoryRepository repository;
    private OrganizationContext context;
    private ExpenseCategoryService service;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        repository = mock(ExpenseCategoryRepository.class);
        context = mock(OrganizationContext.class);
        organizationId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(organizationId);
        service = new ExpenseCategoryService(repository, context);
    }

    @Test
    void createTrimsNameAndStartsActive() {
        when(repository.existsByOrganizationIdAndNameIgnoreCase(organizationId, "Travel")).thenReturn(false);
        when(repository.save(any(ExpenseCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(new ExpenseCategoryRequest("  Travel  "));

        assertThat(response.name()).isEqualTo("Travel");
        assertThat(response.active()).isTrue();
    }

    @Test
    void duplicateNameIsRejectedWithoutSaving() {
        when(repository.existsByOrganizationIdAndNameIgnoreCase(organizationId, "Travel")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new ExpenseCategoryRequest("Travel")))
                .isExactlyInstanceOf(BusinessRuleException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateChecksDuplicateExcludingCurrentCategory() {
        UUID id = UUID.randomUUID();
        ExpenseCategory category = new ExpenseCategory(id, organizationId, "Travel", Instant.now());
        when(repository.findByIdAndOrganizationId(id, organizationId)).thenReturn(Optional.of(category));
        when(repository.existsByOrganizationIdAndNameIgnoreCaseAndIdNot(organizationId, "Office", id)).thenReturn(false);

        assertThat(service.update(id, new ExpenseCategoryRequest("Office")).name()).isEqualTo("Office");
        verify(repository).existsByOrganizationIdAndNameIgnoreCaseAndIdNot(organizationId, "Office", id);
    }

    @Test
    void foreignOrInactiveCategoryCannotBeUsedAsActiveCategory() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndOrganizationId(id, organizationId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.requireActive(id)).isExactlyInstanceOf(ResourceNotFoundException.class);

        ExpenseCategory inactive = new ExpenseCategory(id, organizationId, "Travel", Instant.now());
        inactive.deactivate();
        when(repository.findByIdAndOrganizationId(id, organizationId)).thenReturn(Optional.of(inactive));
        assertThatThrownBy(() -> service.requireActive(id)).isExactlyInstanceOf(BusinessRuleException.class);
    }
}
