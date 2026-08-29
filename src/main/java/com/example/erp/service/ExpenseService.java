package com.example.erp.service;

import com.example.erp.dto.ExpenseRequest;
import com.example.erp.dto.ExpenseResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.entity.BankAccount;
import com.example.erp.entity.BankTransaction;
import com.example.erp.entity.BankTransactionDirection;
import com.example.erp.entity.BankTransactionStatus;
import com.example.erp.entity.Expense;
import com.example.erp.entity.ExpenseStatus;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.BankAccountRepository;
import com.example.erp.repository.BankTransactionRepository;
import com.example.erp.repository.ExpenseRepository;
import com.example.erp.security.OrganizationContext;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {
    private static final java.util.Set<String> CURRENCIES = java.util.Set.of("TWD", "USD", "EUR", "JPY");
    private final ExpenseRepository expenses;
    private final ExpenseCategoryService categories;
    private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions;
    private final OrganizationContext context;

    public ExpenseService(ExpenseRepository expenses, ExpenseCategoryService categories, BankAccountRepository accounts,
                          BankTransactionRepository transactions, OrganizationContext context) {
        this.expenses = expenses; this.categories = categories; this.accounts = accounts;
        this.transactions = transactions; this.context = context;
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> list(String keyword, ExpenseStatus status, UUID categoryId, UUID bankAccountId,
                                               java.time.LocalDate fromDate, java.time.LocalDate toDate, PageQuery query) {
        var page = expenses.search(context.requiredOrganizationId(), status, categoryId, bankAccountId,
                keyword == null ? "" : keyword.trim(), fromDate, toDate, pageable(query));
        return PageResponse.of(page.map(ExpenseResponse::from).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ExpenseResponse detail(UUID id) { return ExpenseResponse.from(find(id)); }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        String currency = normalizeCurrency(request.currencyCode());
        validateCommon(request, currency);
        categories.requireActive(request.categoryId());
        if (request.bankAccountId() != null) validateAccount(request.bankAccountId(), currency);
        Instant now = Instant.now();
        Expense expense = new Expense(UUID.randomUUID(), organizationId, request.categoryId(), request.bankAccountId(),
                context.requiredActorId(), request.payeeName().trim(), request.description().trim(), request.note(),
                request.amount(), currency, request.expenseDate(), now);
        return ExpenseResponse.from(expenses.save(expense));
    }

    @Transactional
    public ExpenseResponse update(UUID id, ExpenseRequest request) {
        Expense expense = find(id);
        if (expense.getStatus() != ExpenseStatus.DRAFT) throw new BusinessRuleException("Only draft expenses can be edited");
        String currency = normalizeCurrency(request.currencyCode());
        validateCommon(request, currency);
        categories.requireActive(request.categoryId());
        if (request.bankAccountId() != null) validateAccount(request.bankAccountId(), currency);
        expense.update(request.categoryId(), request.bankAccountId(), request.payeeName().trim(), request.description().trim(),
                request.note(), request.amount(), currency, request.expenseDate(), Instant.now());
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse confirm(UUID id, UUID requestedAccountId) {
        Expense expense = find(id);
        if (expense.getStatus() != ExpenseStatus.DRAFT) throw new BusinessRuleException("Only draft expenses can be confirmed");
        categories.requireActive(expense.getCategoryId());
        UUID accountId = requestedAccountId == null ? expense.getBankAccountId() : requestedAccountId;
        if (accountId == null) throw new BusinessRuleException("Bank account is required to confirm an expense");
        BankAccount account = validateAccount(accountId, expense.getCurrencyCode());
        UUID organizationId = context.requiredOrganizationId();
        if (transactions.existsByOrganizationIdAndSourceTypeAndSourceIdAndStatus(organizationId, "EXPENSE", expense.getId(), BankTransactionStatus.POSTED))
            throw new BusinessRuleException("Expense debit already exists");
        Instant now = Instant.now();
        expense.confirm(account.getId(), now);
        transactions.save(new BankTransaction(UUID.randomUUID(), organizationId, account.getId(), BankTransactionDirection.DEBIT,
                expense.getAmount(), expense.getCurrencyCode(), expense.getExpenseDate(), "EXPENSE", expense.getId(), null,
                BankTransactionStatus.POSTED, now));
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse voidExpense(UUID id) {
        Expense expense = find(id);
        if (expense.getStatus() == ExpenseStatus.VOIDED) throw new BusinessRuleException("Expense is already voided");
        UUID organizationId = context.requiredOrganizationId();
        if (expense.getStatus() == ExpenseStatus.CONFIRMED) {
            BankTransaction original = transactions.findByOrganizationIdAndSourceTypeAndSourceIdAndStatus(
                    organizationId, "EXPENSE", expense.getId(), BankTransactionStatus.POSTED)
                    .orElseThrow(() -> new BusinessRuleException("Expense debit is missing"));
            original.reverse();
            transactions.save(new BankTransaction(UUID.randomUUID(), organizationId, original.getBankAccountId(),
                    BankTransactionDirection.CREDIT, original.getAmount(), original.getCurrencyCode(), original.getTransactionDate(),
                    "EXPENSE", expense.getId(), original.getId(), BankTransactionStatus.POSTED, Instant.now()));
        }
        expense.voidExpense(Instant.now());
        return ExpenseResponse.from(expense);
    }

    private Expense find(UUID id) { return expenses.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new); }
    private String normalizeCurrency(String value) { String currency = value.toUpperCase(Locale.ROOT); if (!CURRENCIES.contains(currency)) throw new BusinessRuleException("Currency is not supported"); return currency; }
    private void validateCommon(ExpenseRequest request, String currency) { if (request.amount().signum() <= 0) throw new BusinessRuleException("Amount must be positive"); }
    private BankAccount validateAccount(UUID id, String currency) { BankAccount account = accounts.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new); if (!account.isActive() || !currency.equals(account.getCurrencyCode())) throw new BusinessRuleException("Bank account is inactive or currency does not match"); return account; }
    private PageRequest pageable(PageQuery query) { return PageRequest.of(query.page(), Math.min(query.size(), 100), Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort())); }
}
