package com.example.erp.reporting;

import com.example.erp.entity.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportQueryService {
    private final OrganizationContext context;
    private final PaymentRepository payments;
    private final InvoiceRepository invoices;
    private final ExpenseRepository expenses;
    private final BankTransactionRepository transactions;
    private final CustomerRepository customers;
    private final PaymentCategoryRepository paymentCategories;
    private final ExpenseCategoryRepository expenseCategories;
    private final BankAccountRepository accounts;
    private final InvoiceLineRepository invoiceLines;

    public ReportQueryService(OrganizationContext context, PaymentRepository payments, InvoiceRepository invoices,
            ExpenseRepository expenses, BankTransactionRepository transactions, CustomerRepository customers,
            PaymentCategoryRepository paymentCategories, ExpenseCategoryRepository expenseCategories,
            BankAccountRepository accounts, InvoiceLineRepository invoiceLines) {
        this.context = context; this.payments = payments; this.invoices = invoices; this.expenses = expenses;
        this.transactions = transactions; this.customers = customers; this.paymentCategories = paymentCategories;
        this.expenseCategories = expenseCategories; this.accounts = accounts; this.invoiceLines = invoiceLines;
    }

    @Transactional(readOnly = true)
    public ReportResponse query(ReportType type, ReportFilterRequest request) {
        DateBasis basis = basis(type);
        request.validate(basis, Set.of("date", "amount", "status", "currency"));
        UUID org = context.requiredOrganizationId();
        List<ReportRow> rows = switch (type) {
            case PENDING_DEPOSITS, PAYMENT_CATEGORIES -> paymentRows(org, request, type == ReportType.PENDING_DEPOSITS);
            case BANK_BALANCE, CASH_FLOW -> transactionRows(org, request, type == ReportType.CASH_FLOW);
            case INVOICE_STATUS, RECEIVABLE_AGING, TAX -> invoiceRows(org, request, type);
            case EXPENSES -> expenseRows(org, request);
        };
        rows = rows.stream().sorted(Comparator.comparing(ReportRow::date, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(r -> r.source().sourceId())).toList();
        if ("DESC".equalsIgnoreCase(request.direction())) rows = rows.reversed();
        ReportSummary summary = summary(rows);
        int from = Math.min(request.page() * request.size(), rows.size());
        int to = Math.min(from + request.size(), rows.size());
        List<ReportRow> page = rows.subList(from, to);
        return new ReportResponse(type, basis, page, summary, AppliedFilters.from(request),
                rows.isEmpty() ? 0 : (int) Math.ceil((double) rows.size() / request.size()), rows.size(), rows.isEmpty());
    }

    @Transactional(readOnly = true)
    public ReportSummary summary(ReportType type, ReportFilterRequest request) { return query(type, request).summary(); }

    private DateBasis basis(ReportType type) {
        return switch (type) {
            case PENDING_DEPOSITS, PAYMENT_CATEGORIES -> DateBasis.RECEIVED_AT;
            case BANK_BALANCE, CASH_FLOW -> DateBasis.TRANSACTION_DATE;
            case INVOICE_STATUS -> DateBasis.ISSUE_DATE;
            case RECEIVABLE_AGING -> DateBasis.DUE_DATE;
            case EXPENSES -> DateBasis.EXPENSE_DATE;
            case TAX -> DateBasis.DOCUMENT_DATE;
        };
    }

    private List<ReportRow> paymentRows(UUID org, ReportFilterRequest q, boolean pendingOnly) {
        Map<UUID, String> cats = paymentCategories.findByOrganizationId(org).stream().collect(Collectors.toMap(PaymentCategory::getId, PaymentCategory::getName));
        Map<UUID, String> cust = customers.findByOrganizationId(org).stream().collect(Collectors.toMap(Customer::getId, Customer::getName));
        Predicate<Payment> filter = p -> inRange(p.getReceivedAt(), q) && (pendingOnly ? p.getStatus() == PaymentStatus.PENDING_DEPOSIT : p.getStatus() != PaymentStatus.VOIDED)
                && match(q.currencyCode(), p.getCurrencyCode()) && match(q.customerId(), p.getCustomerId()) && match(q.categoryId(), p.getCategoryId());
        return payments.findByOrganizationId(org).stream().filter(filter).map(p -> row("PAYMENT", p.getId(), p.getReceiptNumber(), p.getStatus().name(), p.getReceivedAt(), p.getCurrencyCode(), p.getAmount(),
                Map.of("payerName", p.getPayerName(), "category", cats.getOrDefault(p.getCategoryId(), ""), "customer", cust.getOrDefault(p.getCustomerId(), ""), "reason", p.getReason(), "method", p.getPaymentMethod().name()))).toList();
    }

    private List<ReportRow> transactionRows(UUID org, ReportFilterRequest q, boolean cashFlow) {
        return transactions.findByOrganizationId(org).stream().filter(t -> inRange(t.getTransactionDate(), q) && t.getStatus() == BankTransactionStatus.POSTED
                && match(q.currencyCode(), t.getCurrencyCode()) && match(q.accountId(), t.getBankAccountId())
                && (!cashFlow || "PAYMENT".equals(t.getSourceType()) || "EXPENSE".equals(t.getSourceType())))
                .map(t -> row("BANK_TRANSACTION", t.getId(), t.getSourceId() == null ? "" : t.getSourceId().toString(), t.getStatus().name(), t.getTransactionDate(), t.getCurrencyCode(), t.getAmount(),
                        Map.of("direction", t.getDirection().name(), "sourceType", t.getSourceType(), "accountId", t.getBankAccountId().toString()))).toList();
    }

    private List<ReportRow> invoiceRows(UUID org, ReportFilterRequest q, ReportType type) {
        Map<UUID, String> cust = customers.findByOrganizationId(org).stream().collect(Collectors.toMap(Customer::getId, Customer::getName));
        return invoices.findByOrganizationId(org).stream().filter(i -> {
            LocalDate date = type == ReportType.RECEIVABLE_AGING ? i.getDueDate() : i.getInvoiceDate();
            boolean valid = i.getStatus() != InvoiceStatus.CANCELLED && (type != ReportType.RECEIVABLE_AGING || i.getBalanceDue().signum() > 0);
            return valid && inRange(date, q) && match(q.currencyCode(), i.getCurrencyCode()) && match(q.customerId(), i.getCustomerId()) && matchStatus(q.status(), i.getStatus().name());
        }).map(i -> {
            BigDecimal amount = type == ReportType.RECEIVABLE_AGING ? i.getBalanceDue() : type == ReportType.TAX ? i.getTaxTotal() : i.getGrandTotal();
            String taxRate = invoiceLines.findByInvoiceId(i.getId()).stream().map(line -> line.getTaxRate().toPlainString()).distinct().collect(Collectors.joining("/"));
            long daysLate = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(i.getDueDate(), q.to()));
            String agingBucket = i.getDueDate().isAfter(q.to()) ? "NOT_DUE" : daysLate <= 30 ? "1_30" : daysLate <= 60 ? "31_60" : "61_PLUS";
            return row("INVOICE", i.getId(), i.getInvoiceNumber(), i.getStatus().name(), type == ReportType.RECEIVABLE_AGING ? i.getDueDate() : i.getInvoiceDate(), i.getCurrencyCode(), amount,
                    Map.of("customer", cust.getOrDefault(i.getCustomerId(), ""), "subtotal", i.getSubtotal().toPlainString(), "taxTotal", i.getTaxTotal().toPlainString(), "taxRate", taxRate, "grandTotal", i.getGrandTotal().toPlainString(), "balanceDue", i.getBalanceDue().toPlainString(), "dueDate", i.getDueDate().toString(), "agingBucket", agingBucket));
        }).toList();
    }

    private List<ReportRow> expenseRows(UUID org, ReportFilterRequest q) {
        Map<UUID, String> cats = expenseCategories.findByOrganizationId(org).stream().collect(Collectors.toMap(ExpenseCategory::getId, ExpenseCategory::getName));
        Map<UUID, String> bankAccounts = accounts.findByOrganizationId(org).stream().collect(Collectors.toMap(BankAccount::getId, BankAccount::getAccountName));
        return expenses.findByOrganizationId(org).stream().filter(e -> e.getStatus() != ExpenseStatus.VOIDED && inRange(e.getExpenseDate(), q)
                && match(q.currencyCode(), e.getCurrencyCode()) && match(q.categoryId(), e.getCategoryId()) && match(q.accountId(), e.getBankAccountId()) && matchStatus(q.status(), e.getStatus().name()))
                .map(e -> row("EXPENSE", e.getId(), e.getId().toString(), e.getStatus().name(), e.getExpenseDate(), e.getCurrencyCode(), e.getAmount(),
                        Map.of("payeeName", e.getPayeeName(), "description", e.getDescription(), "category", cats.getOrDefault(e.getCategoryId(), ""), "account", bankAccounts.getOrDefault(e.getBankAccountId(), "")))).toList();
    }

    private ReportRow row(String sourceType, UUID id, String number, String status, LocalDate date, String currency, BigDecimal amount, Map<String,String> fields) {
        return new ReportRow(new SourceReference(sourceType, id, number, status), date, currency, amount, fields);
    }
    private ReportSummary summary(List<ReportRow> rows) {
        BigDecimal amount = rows.stream().map(ReportRow::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = rows.stream().filter(r -> "CREDIT".equals(r.fields().get("direction"))).map(ReportRow::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debit = rows.stream().filter(r -> "DEBIT".equals(r.fields().get("direction"))).map(ReportRow::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportSummary(rows.size(), amount, credit, debit, credit.subtract(debit));
    }
    private boolean inRange(LocalDate date, ReportFilterRequest q) { return date != null && !date.isBefore(q.from()) && !date.isAfter(q.to()); }
    private boolean match(String wanted, String actual) { return wanted == null || wanted.equalsIgnoreCase(actual); }
    private boolean match(UUID wanted, UUID actual) { return wanted == null || wanted.equals(actual); }
    private boolean matchStatus(String wanted, String actual) { return wanted == null || wanted.isBlank() || wanted.equalsIgnoreCase(actual); }
}
