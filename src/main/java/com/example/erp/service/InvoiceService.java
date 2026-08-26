package com.example.erp.service;

import com.example.erp.dto.*;
import com.example.erp.entity.*;
import com.example.erp.exception.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {
    private static final int SCALE = 4;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final Set<String> CURRENCIES = Set.of("TWD", "USD", "EUR", "JPY");
    private final InvoiceRepository invoices;
    private final InvoiceLineRepository lines;
    private final DocumentSequenceRepository sequences;
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrganizationContext context;

    public InvoiceService(InvoiceRepository invoices, InvoiceLineRepository lines, DocumentSequenceRepository sequences,
                          CustomerRepository customers, ProductRepository products, OrganizationContext context) {
        this.invoices = invoices; this.lines = lines; this.sequences = sequences; this.customers = customers; this.products = products; this.context = context;
    }

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        UUID org = context.requiredOrganizationId();
        Customer customer = activeCustomer(request.customerId(), org);
        Calculation calculation = calculate(request, org);
        Invoice invoice = new Invoice(UUID.randomUUID(), org, customer.getId(), null, currency(request.currencyCode()), request.invoiceDate(), request.dueDate(), calculation.subtotal, calculation.taxTotal, calculation.grandTotal, Instant.now());
        invoices.save(invoice); lines.saveAll(calculation.toEntities(invoice.getId()));
        return response(invoice, customer);
    }

    @Transactional
    public InvoiceResponse update(UUID id, InvoiceRequest request) {
        UUID org = context.requiredOrganizationId(); Invoice invoice = find(id, org);
        if (invoice.getStatus() != InvoiceStatus.DRAFT) throw new BusinessRuleException("Only draft invoices can be edited");
        Customer customer = activeCustomer(request.customerId(), org); Calculation calculation = calculate(request, org);
        invoice.updateDraft(customer.getId(), currency(request.currencyCode()), request.invoiceDate(), request.dueDate(), calculation.subtotal, calculation.taxTotal, calculation.grandTotal);
        lines.deleteByInvoiceId(id); lines.saveAll(calculation.toEntities(id)); return response(invoice, customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> list(String keyword, InvoiceStatus status, LocalDate fromDate, LocalDate toDate, PageQuery query) {
        UUID org = context.requiredOrganizationId();
        String filter = keyword == null ? "" : keyword.trim();
        var page = status == InvoiceStatus.OVERDUE
                ? invoices.searchOverdue(org, filter, fromDate, toDate, pageable(query))
                : invoices.search(org, filter, status, fromDate, toDate, pageable(query));
        var items = page.getContent().stream().map(invoice -> { Customer c = customers.findByIdAndOrganizationId(invoice.getCustomerId(), org).orElse(null); return response(invoice, c); }).toList();
        return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse detail(UUID id) { Invoice invoice = find(id, context.requiredOrganizationId()); Customer c = customers.findByIdAndOrganizationId(invoice.getCustomerId(), invoice.getOrganizationId()).orElse(null); return response(invoice, c); }

    @Transactional(readOnly = true)
    public InvoiceResponse receipt(UUID id) {
        Invoice invoice = find(id, context.requiredOrganizationId());
        if (invoice.getStatus() != InvoiceStatus.ISSUED) throw new BusinessRuleException("Only issued invoices can be printed");
        Customer c = customers.findByIdAndOrganizationId(invoice.getCustomerId(), invoice.getOrganizationId()).orElse(null);
        return response(invoice, c);
    }

    @Transactional
    public InvoiceResponse issue(UUID id) {
        UUID org = context.requiredOrganizationId(); Invoice invoice = find(id, org);
        if (invoice.getStatus() != InvoiceStatus.DRAFT) throw new BusinessRuleException("Only draft invoices can be issued");
        String number = nextNumber(org, invoice.getInvoiceDate().getYear()); invoice.issue(number); Customer c = customers.findByIdAndOrganizationId(invoice.getCustomerId(), org).orElse(null); return response(invoice, c);
    }

    @Transactional
    public InvoiceResponse cancel(UUID id) {
        UUID org = context.requiredOrganizationId(); Invoice invoice = find(id, org);
        if (!(invoice.getStatus() == InvoiceStatus.DRAFT || invoice.getStatus() == InvoiceStatus.ISSUED || invoice.getStatus() == InvoiceStatus.OVERDUE)) throw new BusinessRuleException("Invoice cannot be cancelled");
        invoice.cancel(); Customer c = customers.findByIdAndOrganizationId(invoice.getCustomerId(), org).orElse(null); return response(invoice, c);
    }

    private String nextNumber(UUID org, int year) {
        DocumentSequence.Key key = new DocumentSequence.Key(org, year, "INVOICE");
        DocumentSequence sequence = sequences.findById(key).orElseGet(() -> sequences.save(new DocumentSequence(key)));
        int value = sequence.reserveValue(); sequences.save(sequence); return "INV-" + year + "-" + String.format("%04d", value);
    }
    private Customer activeCustomer(UUID id, UUID org) { Customer c = customers.findByIdAndOrganizationId(id, org).orElseThrow(ResourceNotFoundException::new); if (!c.isActive()) throw new BusinessRuleException("Customer is inactive"); return c; }
    private Product activeProduct(UUID id, UUID org) { Product p = products.findByIdAndOrganizationId(id, org).orElseThrow(ResourceNotFoundException::new); if (!p.isActive()) throw new BusinessRuleException("Product is inactive"); return p; }
    private Invoice find(UUID id, UUID org) { return invoices.findByIdAndOrganizationId(id, org).orElseThrow(ResourceNotFoundException::new); }
    private String currency(String value) { String result = value.toUpperCase(Locale.ROOT); if (!CURRENCIES.contains(result)) throw new BusinessRuleException("Currency is not supported"); return result; }
    private PageRequest pageable(PageQuery query) { return PageRequest.of(query.page(), Math.min(query.size(), 100), Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort())); }

    private Calculation calculate(InvoiceRequest request, UUID org) {
        if (request.dueDate().isBefore(request.invoiceDate())) throw new BusinessRuleException("Due date cannot precede invoice date");
        String invoiceCurrency = currency(request.currencyCode()); BigDecimal subtotal = BigDecimal.ZERO; BigDecimal tax = BigDecimal.ZERO; List<CalculatedLine> calculated = new ArrayList<>();
        for (InvoiceLineRequest line : request.lines()) {
            Product product = activeProduct(line.productId(), org); if (!invoiceCurrency.equals(product.getCurrencyCode())) throw new BusinessRuleException("Currency does not match product");
            BigDecimal quantity = line.quantity().setScale(SCALE, RoundingMode.HALF_UP); BigDecimal discount = line.discount().setScale(SCALE, RoundingMode.HALF_UP);
            BigDecimal base = quantity.multiply(product.getUnitPrice()).subtract(discount).setScale(SCALE, RoundingMode.HALF_UP); if (base.signum() < 0) throw new BusinessRuleException("Discount cannot exceed line amount");
            BigDecimal lineTax = base.multiply(product.getTaxRate()).divide(HUNDRED, SCALE, RoundingMode.HALF_UP); BigDecimal total = base.add(lineTax).setScale(SCALE, RoundingMode.HALF_UP); subtotal = subtotal.add(base); tax = tax.add(lineTax); calculated.add(new CalculatedLine(product, quantity, discount, lineTax, total));
        }
        subtotal = subtotal.setScale(SCALE, RoundingMode.HALF_UP); tax = tax.setScale(SCALE, RoundingMode.HALF_UP); return new Calculation(subtotal, tax, subtotal.add(tax).setScale(SCALE, RoundingMode.HALF_UP), calculated);
    }
    private InvoiceResponse response(Invoice invoice, Customer customer) { return InvoiceResponse.from(invoice, customer == null ? "" : customer.getName(), lines.findByInvoiceId(invoice.getId()), effectiveStatus(invoice)); }
    private InvoiceStatus effectiveStatus(Invoice invoice) { if (invoice.getStatus() == InvoiceStatus.ISSUED && invoice.getBalanceDue().signum() > 0 && invoice.getDueDate().isBefore(LocalDate.now())) return InvoiceStatus.OVERDUE; return invoice.getStatus(); }
    private record CalculatedLine(Product product, BigDecimal quantity, BigDecimal discount, BigDecimal tax, BigDecimal total) {}
    private record Calculation(BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal, List<CalculatedLine> lines) { List<InvoiceLine> toEntities(UUID invoiceId) { return lines.stream().map(l -> new InvoiceLine(UUID.randomUUID(), invoiceId, l.product.getId(), l.product.getName(), l.product.getDescription(), l.quantity, l.product.getUnitPrice(), l.discount, l.product.getTaxRate(), l.total)).toList(); } }
}
