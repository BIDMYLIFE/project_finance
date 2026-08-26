package com.example.erp.service;

import com.example.erp.dto.BankAccountRequest;
import com.example.erp.dto.BankAccountResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.entity.BankAccount;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.BankAccountRepository;
import com.example.erp.security.OrganizationContext;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAccountService {
    private final BankAccountRepository accounts;
    private final OrganizationContext context;

    public BankAccountService(BankAccountRepository accounts, OrganizationContext context) {
        this.accounts = accounts;
        this.context = context;
    }

    @Transactional(readOnly = true)
    public PageResponse<BankAccountResponse> accounts(String keyword, boolean active, PageQuery query) {
        var page = accounts.findByOrganizationIdAndActiveAndAccountNameContainingIgnoreCase(
                context.requiredOrganizationId(), active, keyword == null ? "" : keyword.trim(), pageable(query));
        return PageResponse.of(page.map(BankAccountResponse::from).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional
    public BankAccountResponse create(BankAccountRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        String name = request.accountName().trim();
        validateName(organizationId, name, null);
        String currency = request.currencyCode().toUpperCase(Locale.ROOT);
        return BankAccountResponse.from(accounts.save(new BankAccount(UUID.randomUUID(), organizationId, name, currency,
                request.openingBalance(), Instant.now())));
    }

    @Transactional
    public BankAccountResponse update(UUID id, BankAccountRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        BankAccount account = accounts.findByIdAndOrganizationId(id, organizationId).orElseThrow(ResourceNotFoundException::new);
        String name = request.accountName().trim();
        validateName(organizationId, name, id);
        account.update(name, request.currencyCode().toUpperCase(Locale.ROOT), request.openingBalance());
        return BankAccountResponse.from(account);
    }

    @Transactional
    public void deactivate(UUID id) {
        BankAccount account = accounts.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new);
        if (account.isActive()) account.deactivate();
    }

    private void validateName(UUID organizationId, String name, UUID currentId) {
        boolean duplicate = currentId == null
                ? accounts.existsByOrganizationIdAndAccountNameIgnoreCase(organizationId, name)
                : accounts.existsByOrganizationIdAndAccountNameIgnoreCaseAndIdNot(organizationId, name, currentId);
        if (duplicate) throw new BusinessRuleException("Bank account name already exists");
    }

    private PageRequest pageable(PageQuery query) {
        return PageRequest.of(query.page(), Math.min(query.size(), 100),
                Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort()));
    }
}
