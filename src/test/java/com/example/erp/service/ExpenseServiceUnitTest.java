package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.erp.dto.ExpenseRequest;
import com.example.erp.dto.ExpenseResponse;
import com.example.erp.entity.*;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseServiceUnitTest {
    private ExpenseRepository expenses; private ExpenseCategoryService categories; private BankAccountRepository accounts;
    private BankTransactionRepository transactions; private OrganizationContext context; private ExpenseService service; private UUID org;

    @BeforeEach void setUp() {
        expenses = mock(ExpenseRepository.class); categories = mock(ExpenseCategoryService.class); accounts = mock(BankAccountRepository.class);
        transactions = mock(BankTransactionRepository.class); context = mock(OrganizationContext.class); org = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(org); when(context.requiredActorId()).thenReturn(UUID.randomUUID());
        service = new ExpenseService(expenses, categories, accounts, transactions, context);
    }

    private ExpenseRequest request(UUID category, UUID account) { return new ExpenseRequest(category, account, " Payee ", " Office supplies ", null, new BigDecimal("10.0000"), "TWD", LocalDate.of(2026, 8, 29)); }
    private BankAccount account(UUID id) { return new BankAccount(id, org, "Operating", "TWD", BigDecimal.ZERO, Instant.now()); }

    @Test void createStoresDraftWithoutPostingTransaction() {
        UUID category = UUID.randomUUID(); when(expenses.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));
        ExpenseResponse response = service.create(request(category, null));
        assertThat(response.status()).isEqualTo(ExpenseStatus.DRAFT); assertThat(response.payeeName()).isEqualTo("Payee");
        verify(categories).requireActive(category); verify(transactions, never()).save(any());
    }

    @Test void confirmationPostsOneDebitAndChangesState() {
        UUID id = UUID.randomUUID(), accountId = UUID.randomUUID(); Expense expense = new Expense(id, org, UUID.randomUUID(), null, UUID.randomUUID(), "Payee", "Desc", null, BigDecimal.TEN, "TWD", LocalDate.now(), Instant.now());
        when(expenses.findByIdAndOrganizationId(id, org)).thenReturn(Optional.of(expense)); when(accounts.findByIdAndOrganizationId(accountId, org)).thenReturn(Optional.of(account(accountId)));
        when(transactions.existsByOrganizationIdAndSourceTypeAndSourceIdAndStatus(org, "EXPENSE", id, BankTransactionStatus.POSTED)).thenReturn(false);
        ExpenseResponse response = service.confirm(id, accountId);
        assertThat(response.status()).isEqualTo(ExpenseStatus.CONFIRMED); assertThat(response.bankAccountId()).isEqualTo(accountId);
        verify(transactions).save(argThat(t -> t.getDirection() == BankTransactionDirection.DEBIT && "EXPENSE".equals(t.getSourceType()) && id.equals(t.getSourceId())));
    }

    @Test void inactiveOrForeignExpenseCannotBeUpdated() {
        UUID id = UUID.randomUUID(); when(expenses.findByIdAndOrganizationId(id, org)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, request(UUID.randomUUID(), null))).isExactlyInstanceOf(ResourceNotFoundException.class);
    }

    @Test void confirmedVoidReversesOriginalDebitAndCannotRepeat() {
        UUID id = UUID.randomUUID(), accountId = UUID.randomUUID(); Expense expense = new Expense(id, org, UUID.randomUUID(), accountId, UUID.randomUUID(), "Payee", "Desc", null, BigDecimal.TEN, "TWD", LocalDate.now(), Instant.now());
        expense.confirm(accountId, Instant.now()); BankTransaction original = new BankTransaction(UUID.randomUUID(), org, accountId, BankTransactionDirection.DEBIT, BigDecimal.TEN, "TWD", LocalDate.now(), "EXPENSE", id, null, BankTransactionStatus.POSTED, Instant.now());
        when(expenses.findByIdAndOrganizationId(id, org)).thenReturn(Optional.of(expense)); when(transactions.findByOrganizationIdAndSourceTypeAndSourceIdAndStatus(org, "EXPENSE", id, BankTransactionStatus.POSTED)).thenReturn(Optional.of(original));
        assertThat(service.voidExpense(id).status()).isEqualTo(ExpenseStatus.VOIDED); assertThat(original.getStatus()).isEqualTo(BankTransactionStatus.REVERSED);
        verify(transactions).save(argThat(t -> t.getDirection() == BankTransactionDirection.CREDIT && t.getReversalOfId().equals(original.getId())));
        assertThatThrownBy(() -> service.voidExpense(id)).isExactlyInstanceOf(BusinessRuleException.class);
    }
}
