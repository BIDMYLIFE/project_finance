package com.example.erp.service;

import com.example.erp.dto.ExpenseCategoryRequest;
import com.example.erp.dto.ExpenseCategoryResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.entity.ExpenseCategory;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.ExpenseCategoryRepository;
import com.example.erp.security.OrganizationContext;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository repository;
    private final OrganizationContext context;

    public ExpenseCategoryService(ExpenseCategoryRepository repository, OrganizationContext context) {
        this.repository = repository;
        this.context = context;
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseCategoryResponse> categories(String keyword, boolean active, PageQuery query) {
        var page = repository.findByOrganizationIdAndActiveAndNameContainingIgnoreCase(
                context.requiredOrganizationId(), active, keyword == null ? "" : keyword.trim(), pageable(query));
        return PageResponse.of(page.map(ExpenseCategoryResponse::from).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional
    public ExpenseCategoryResponse create(ExpenseCategoryRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        String name = normalizedName(request);
        ensureNameAvailable(organizationId, name, null);
        return ExpenseCategoryResponse.from(repository.save(
                new ExpenseCategory(UUID.randomUUID(), organizationId, name, Instant.now())));
    }

    @Transactional
    public ExpenseCategoryResponse update(UUID id, ExpenseCategoryRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        ExpenseCategory category = repository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(ResourceNotFoundException::new);
        String name = normalizedName(request);
        ensureNameAvailable(organizationId, name, id);
        category.rename(name);
        return ExpenseCategoryResponse.from(category);
    }

    @Transactional
    public void deactivate(UUID id) {
        ExpenseCategory category = repository.findByIdAndOrganizationId(id, context.requiredOrganizationId())
                .orElseThrow(ResourceNotFoundException::new);
        if (category.isActive()) category.deactivate();
    }

    @Transactional(readOnly = true)
    public ExpenseCategory requireActive(UUID id) {
        ExpenseCategory category = repository.findByIdAndOrganizationId(id, context.requiredOrganizationId())
                .orElseThrow(ResourceNotFoundException::new);
        if (!category.isActive()) throw new BusinessRuleException("Expense category is inactive");
        return category;
    }

    private String normalizedName(ExpenseCategoryRequest request) {
        return request.name().trim();
    }

    private void ensureNameAvailable(UUID organizationId, String name, UUID currentId) {
        boolean duplicate = currentId == null
                ? repository.existsByOrganizationIdAndNameIgnoreCase(organizationId, name)
                : repository.existsByOrganizationIdAndNameIgnoreCaseAndIdNot(organizationId, name, currentId);
        if (duplicate) throw new BusinessRuleException("Expense category name already exists");
    }

    private PageRequest pageable(PageQuery query) {
        return PageRequest.of(query.page(), Math.min(query.size(), 100),
                Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort()));
    }
}
