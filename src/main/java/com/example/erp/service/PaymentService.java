package com.example.erp.service;

import com.example.erp.dto.*;
import com.example.erp.entity.*;
import com.example.erp.exception.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private static final Set<String> CURRENCIES = Set.of("TWD", "USD", "EUR", "JPY");
    private final PaymentRepository payments; private final PaymentCategoryRepository categories; private final PaymentAllocationRepository paymentAllocations; private final InvoiceRepository invoices; private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions; private final ReceiptPrintRepository prints; private final DocumentSequenceRepository sequences; private final CustomerRepository customers; private final OrganizationContext context;
    public PaymentService(PaymentRepository payments, PaymentCategoryRepository categories, PaymentAllocationRepository paymentAllocations, InvoiceRepository invoices, BankAccountRepository accounts, BankTransactionRepository transactions, ReceiptPrintRepository prints, DocumentSequenceRepository sequences, CustomerRepository customers, OrganizationContext context) { this.payments=payments; this.categories=categories; this.paymentAllocations=paymentAllocations; this.invoices=invoices; this.accounts=accounts; this.transactions=transactions; this.prints=prints; this.sequences=sequences; this.customers=customers; this.context=context; }

    @Transactional(readOnly=true)
    public PageResponse<PaymentResponse> list(String keyword, PaymentStatus status, PageQuery query) { var page=payments.search(context.requiredOrganizationId(), keyword==null?"":keyword.trim(), status, pageable(query)); return PageResponse.of(page.getContent().stream().map(PaymentResponse::from).toList(), page.getNumber(), page.getSize(), page.getTotalElements()); }
    @Transactional(readOnly=true)
    public PaymentResponse detail(UUID id) { return PaymentResponse.from(find(id)); }
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        UUID org=context.requiredOrganizationId(); PaymentCategory category=categories.findByIdAndOrganizationId(request.categoryId(),org).orElseThrow(ResourceNotFoundException::new); if(!category.isActive()) throw new BusinessRuleException("Payment category is inactive");
        String currency=request.currencyCode().toUpperCase(Locale.ROOT); if(!CURRENCIES.contains(currency)) throw new BusinessRuleException("Currency is not supported");
        if(request.customerId()!=null) customers.findByIdAndOrganizationId(request.customerId(),org).orElseThrow(ResourceNotFoundException::new);
        BankAccount account=null; PaymentStatus status=PaymentStatus.PENDING_DEPOSIT;
        if(request.bankAccountId()!=null){ account=accounts.findByIdAndOrganizationId(request.bankAccountId(),org).orElseThrow(ResourceNotFoundException::new); if(!account.isActive()||!currency.equals(account.getCurrencyCode())) throw new BusinessRuleException("Bank account is inactive or currency does not match"); status=PaymentStatus.POSTED; }
        Payment payment=new Payment(UUID.randomUUID(),org,category.getId(),account==null?null:account.getId(),context.requiredActorId(),nextReceipt(org,request.receivedAt().getYear()),request.payerName().trim(),request.reason().trim(),request.note(),request.amount(),currency,request.paymentMethod(),request.receivedAt(),status,Instant.now()); payment.assignCustomer(request.customerId()); payments.save(payment);
        if(account!=null) transactions.save(new BankTransaction(UUID.randomUUID(),org,account.getId(),BankTransactionDirection.CREDIT,request.amount(),currency,request.receivedAt(),"PAYMENT",payment.getId(),null,BankTransactionStatus.POSTED,Instant.now()));
        return PaymentResponse.from(payment);
    }
    @Transactional
    public PaymentFromInvoicesResponse createFromInvoices(PaymentFromInvoicesRequest request) {
        UUID org = context.requiredOrganizationId();
        List<UUID> ids = request.invoiceIds().stream().distinct().sorted().toList();
        if (ids.isEmpty()) throw new BusinessRuleException("At least one invoice is required");
        PaymentCategory category = categories.findByIdAndOrganizationId(request.categoryId(), org).orElseThrow(ResourceNotFoundException::new);
        if (!category.isActive()) throw new BusinessRuleException("Payment category is inactive");
        List<Invoice> locked = ids.stream().map(id -> invoices.findWithLockByIdAndOrganizationId(id, org).orElseThrow(ResourceNotFoundException::new)).toList();
        Invoice first = locked.get(0);
        String currency = first.getCurrencyCode();
        UUID customerId = first.getCustomerId();
        BigDecimal available = BigDecimal.ZERO;
        for (Invoice invoice : locked) {
            if (!(invoice.getStatus() == InvoiceStatus.ISSUED || invoice.getStatus() == InvoiceStatus.PARTIALLY_PAID) || invoice.getBalanceDue().signum() <= 0) throw new BusinessRuleException("Invoice is not open for payment");
            if (!customerId.equals(invoice.getCustomerId())) throw new BusinessRuleException("Invoices must belong to the same customer");
            if (!currency.equals(invoice.getCurrencyCode())) throw new BusinessRuleException("Invoices must use the same currency");
            available = available.add(invoice.getBalanceDue());
        }
        if (request.amount().compareTo(available) > 0) throw new BusinessRuleException("Payment amount exceeds invoice balance");
        BankAccount account = accounts.findByIdAndOrganizationId(request.bankAccountId(), org).orElseThrow(ResourceNotFoundException::new);
        if (!account.isActive() || !currency.equals(account.getCurrencyCode())) throw new BusinessRuleException("Bank account is inactive or currency does not match");
        Customer customer = customers.findByIdAndOrganizationId(customerId, org).orElseThrow(ResourceNotFoundException::new);
        String reason = request.reason() == null || request.reason().isBlank() ? "Invoice payment" : request.reason().trim();
        Payment payment = new Payment(UUID.randomUUID(), org, category.getId(), account.getId(), context.requiredActorId(), nextReceipt(org, request.receivedAt().getYear()), customer.getName(), reason, request.note(), request.amount(), currency, request.paymentMethod(), request.receivedAt(), PaymentStatus.POSTED, Instant.now());
        payment.assignCustomer(customer.getId());
        payments.save(payment);
        BigDecimal remaining = request.amount();
        List<PaymentAllocationResponse> allocations = new ArrayList<>();
        for (Invoice invoice : locked.stream().sorted(Comparator.comparing(Invoice::getInvoiceDate).thenComparing(Invoice::getId)).toList()) {
            if (remaining.signum() == 0) break;
            BigDecimal amount = remaining.min(invoice.getBalanceDue());
            PaymentAllocation allocation = paymentAllocations.save(new PaymentAllocation(UUID.randomUUID(), org, payment.getId(), invoice.getId(), amount));
            invoice.applyPayment(amount);
            allocations.add(PaymentAllocationResponse.from(allocation));
            remaining = remaining.subtract(amount);
        }
        if (remaining.signum() != 0) throw new BusinessRuleException("Payment amount could not be allocated");
        transactions.save(new BankTransaction(UUID.randomUUID(), org, account.getId(), BankTransactionDirection.CREDIT, request.amount(), currency, request.receivedAt(), "PAYMENT", payment.getId(), null, BankTransactionStatus.POSTED, Instant.now()));
        return new PaymentFromInvoicesResponse(PaymentResponse.from(payment), allocations);
    }
    @Transactional public PaymentResponse voidPayment(UUID id){ Payment payment=find(id); if(payment.getStatus()==PaymentStatus.VOIDED) throw new BusinessRuleException("Payment is already voided"); payment.voidPayment(); return PaymentResponse.from(payment); }
    @Transactional public PaymentResponse print(UUID id){ Payment payment=find(id); if(payment.getStatus()==PaymentStatus.VOIDED) throw new BusinessRuleException("Voided payment cannot be printed"); prints.save(new ReceiptPrint(UUID.randomUUID(),context.requiredOrganizationId(),id,Instant.now(),context.requiredActorId())); return PaymentResponse.from(payment); }
    @Transactional public PaymentResponse post(UUID id, UUID bankAccountId){ Payment payment=find(id); if(payment.getStatus()!=PaymentStatus.PENDING_DEPOSIT) throw new BusinessRuleException("Only pending payments can be posted"); UUID org=context.requiredOrganizationId(); BankAccount account=accounts.findByIdAndOrganizationId(bankAccountId,org).orElseThrow(ResourceNotFoundException::new); if(!account.isActive()||!account.getCurrencyCode().equals(payment.getCurrencyCode())) throw new BusinessRuleException("Bank account is inactive or currency does not match"); payment.changeBankAccount(account.getId()); payment.post(); transactions.save(new BankTransaction(UUID.randomUUID(),org,account.getId(),BankTransactionDirection.CREDIT,payment.getAmount(),payment.getCurrencyCode(),payment.getReceivedAt(),"PAYMENT",payment.getId(),null,BankTransactionStatus.POSTED,Instant.now())); return PaymentResponse.from(payment); }
    @Transactional public java.util.List<PaymentAllocationResponse> allocate(UUID id, java.util.List<PaymentAllocationRequest> requests){ Payment payment=find(id); if(payment.getStatus()==PaymentStatus.VOIDED) throw new BusinessRuleException("Voided payment cannot be allocated"); UUID org=context.requiredOrganizationId(); java.math.BigDecimal used=paymentAllocations.sumAmountByPayment(org,id); java.math.BigDecimal total=java.math.BigDecimal.ZERO; java.util.List<PaymentAllocationResponse> result=new java.util.ArrayList<>(); for(PaymentAllocationRequest request:requests){ com.example.erp.entity.Invoice invoice=invoices.findByIdAndOrganizationId(request.invoiceId(),org).orElseThrow(ResourceNotFoundException::new); if(!(invoice.getStatus()==InvoiceStatus.ISSUED||invoice.getStatus()==InvoiceStatus.PARTIALLY_PAID)) throw new BusinessRuleException("Invoice is not open"); if(!invoice.getCurrencyCode().equals(payment.getCurrencyCode())||request.amount().compareTo(invoice.getBalanceDue())>0) throw new BusinessRuleException("Allocation exceeds invoice balance or currency does not match"); total=total.add(request.amount()); if(used.add(total).compareTo(payment.getAmount())>0) throw new BusinessRuleException("Allocation exceeds payment amount"); PaymentAllocation allocation=paymentAllocations.save(new PaymentAllocation(UUID.randomUUID(),org,id,invoice.getId(),request.amount())); invoice.applyPayment(request.amount()); result.add(PaymentAllocationResponse.from(allocation)); } return result; }
    private Payment find(UUID id){return payments.findByIdAndOrganizationId(id,context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new);}
    private String nextReceipt(UUID org,int year){var key=new DocumentSequence.Key(org,year,"RECEIPT"); var seq=sequences.findById(key).orElseGet(()->sequences.save(new DocumentSequence(key))); int value=seq.reserveValue(); sequences.save(seq); return "RCT-"+year+"-"+String.format("%04d",value);}
    private PageRequest pageable(PageQuery q){return PageRequest.of(q.page(),Math.min(q.size(),100),Sort.by(Sort.Direction.fromString(q.safeDirection()),q.sort()));}
}
