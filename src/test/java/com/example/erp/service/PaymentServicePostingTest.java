package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.erp.entity.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class PaymentServicePostingTest {
    private PaymentRepository payments; private BankAccountRepository accounts; private BankTransactionRepository transactions;
    private PaymentService service; private OrganizationContext context; private UUID org, paymentId, accountId;

    @BeforeEach void setUp() {
        payments = mock(PaymentRepository.class); accounts = mock(BankAccountRepository.class); transactions = mock(BankTransactionRepository.class);
        context = mock(OrganizationContext.class); org = UUID.randomUUID(); paymentId = UUID.randomUUID(); accountId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(org);
        service = new PaymentService(payments, mock(PaymentCategoryRepository.class), mock(PaymentAllocationRepository.class), mock(InvoiceRepository.class), accounts, transactions, mock(ReceiptPrintRepository.class), mock(DocumentSequenceRepository.class), mock(CustomerRepository.class), context);
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
