package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.erp.entity.*;
import com.example.erp.dto.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class PaymentServicePostingTest {
    private PaymentRepository payments; private BankAccountRepository accounts; private BankTransactionRepository transactions; private PaymentCategoryRepository categories; private InvoiceRepository invoices; private CustomerRepository customers; private PaymentAllocationRepository allocations; private DocumentSequenceRepository sequences;
    private PaymentService service; private OrganizationContext context; private UUID org, paymentId, accountId;

    @BeforeEach void setUp() {
        payments = mock(PaymentRepository.class); accounts = mock(BankAccountRepository.class); transactions = mock(BankTransactionRepository.class); categories = mock(PaymentCategoryRepository.class); invoices = mock(InvoiceRepository.class); customers = mock(CustomerRepository.class); allocations = mock(PaymentAllocationRepository.class); sequences = mock(DocumentSequenceRepository.class);
        context = mock(OrganizationContext.class); org = UUID.randomUUID(); paymentId = UUID.randomUUID(); accountId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(org);
        service = new PaymentService(payments, categories, allocations, invoices, accounts, transactions, mock(ReceiptPrintRepository.class), sequences, customers, context);
    }

    @Test void createsPostedPaymentAndAllocationsForOneInvoiceWithPartialAmount() {
        UUID customerId = UUID.randomUUID(), categoryId = UUID.randomUUID(), invoiceId = UUID.randomUUID();
        Customer customer = new Customer(customerId, org, "C-1", "ACME", null, null, Instant.now());
        PaymentCategory category = new PaymentCategory(categoryId, org, "Sales", Instant.now());
        Invoice invoice = new Invoice(invoiceId, org, customerId, null, "TWD", LocalDate.now(), LocalDate.now().plusDays(30), new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("100"), Instant.now());
        invoice.issue("INV-1");
        BankAccount account = new BankAccount(accountId, org, "Operating", "TWD", BigDecimal.ZERO, Instant.now());
        when(categories.findByIdAndOrganizationId(categoryId, org)).thenReturn(Optional.of(category));
        when(invoices.findWithLockByIdAndOrganizationId(invoiceId, org)).thenReturn(Optional.of(invoice));
        when(accounts.findByIdAndOrganizationId(accountId, org)).thenReturn(Optional.of(account));
        when(customers.findByIdAndOrganizationId(customerId, org)).thenReturn(Optional.of(customer));
        when(sequences.findById(any())).thenReturn(Optional.empty());
        when(sequences.save(any(DocumentSequence.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(allocations.save(any(PaymentAllocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaymentFromInvoicesRequest request = new PaymentFromInvoicesRequest(List.of(invoiceId), categoryId, accountId, new BigDecimal("40"), null, null, PaymentMethod.BANK_TRANSFER, LocalDate.now());
        PaymentFromInvoicesResponse response = service.createFromInvoices(request);
        assertThat(response.allocations()).hasSize(1);
        assertThat(response.payment().status()).isEqualTo(PaymentStatus.POSTED);
        assertThat(response.payment().customerId()).isEqualTo(customerId);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PARTIALLY_PAID);
        assertThat(invoice.getBalanceDue()).isEqualByComparingTo("60");
        verify(transactions).save(any(BankTransaction.class));
    }

    @Test void rejectsInvoicesFromDifferentCustomers() {
        UUID categoryId = UUID.randomUUID(), firstId = UUID.randomUUID(), secondId = UUID.randomUUID();
        PaymentCategory category = new PaymentCategory(categoryId, org, "Sales", Instant.now());
        Invoice first = new Invoice(firstId, org, UUID.randomUUID(), null, "TWD", LocalDate.now(), LocalDate.now(), BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, Instant.now());
        Invoice second = new Invoice(secondId, org, UUID.randomUUID(), null, "TWD", LocalDate.now(), LocalDate.now(), BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, Instant.now());
        first.issue("INV-1"); second.issue("INV-2");
        when(categories.findByIdAndOrganizationId(categoryId, org)).thenReturn(Optional.of(category));
        when(invoices.findWithLockByIdAndOrganizationId(firstId, org)).thenReturn(Optional.of(first));
        when(invoices.findWithLockByIdAndOrganizationId(secondId, org)).thenReturn(Optional.of(second));
        PaymentFromInvoicesRequest request = new PaymentFromInvoicesRequest(List.of(firstId, secondId), categoryId, accountId, BigDecimal.TEN, null, null, PaymentMethod.BANK_TRANSFER, LocalDate.now());
        assertThatThrownBy(() -> service.createFromInvoices(request)).isInstanceOf(com.example.erp.exception.BusinessRuleException.class);
        verify(payments, never()).save(any());
    }

    @Test void postingRequiresAValidSelectedBankAccount() {
        Payment payment = new Payment(paymentId, org, UUID.randomUUID(), null, UUID.randomUUID(), "RCT-1", "Payer", "Reason", null, BigDecimal.TEN, "TWD", PaymentMethod.BANK_TRANSFER, LocalDate.now(), PaymentStatus.PENDING_DEPOSIT, Instant.now());
        when(payments.findByIdAndOrganizationId(paymentId, org)).thenReturn(Optional.of(payment));
        when(accounts.findByIdAndOrganizationId(accountId, org)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.post(paymentId, accountId)).isInstanceOf(com.example.erp.exception.ResourceNotFoundException.class);
        verify(transactions, never()).save(any());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING_DEPOSIT);
    }

    @Test void postingLinksPaymentAndCreatesCreditTransaction() {
        Payment payment = new Payment(paymentId, org, UUID.randomUUID(), null, UUID.randomUUID(), "RCT-1", "Payer", "Reason", null, BigDecimal.TEN, "TWD", PaymentMethod.BANK_TRANSFER, LocalDate.now(), PaymentStatus.PENDING_DEPOSIT, Instant.now());
        BankAccount account = new BankAccount(accountId, org, "Operating", "TWD", BigDecimal.ZERO, Instant.now());
        when(payments.findByIdAndOrganizationId(paymentId, org)).thenReturn(Optional.of(payment));
        when(accounts.findByIdAndOrganizationId(accountId, org)).thenReturn(Optional.of(account));
        service.post(paymentId, accountId);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.POSTED);
        assertThat(payment.getBankAccountId()).isEqualTo(accountId);
        verify(transactions).save(argThat(transaction -> transaction.getBankAccountId().equals(accountId) && transaction.getDirection() == BankTransactionDirection.CREDIT && transaction.getSourceId().equals(paymentId)));
    }
}
