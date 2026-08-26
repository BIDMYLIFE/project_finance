package com.example.erp.service;

import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.dto.QuoteLineRequest;
import com.example.erp.dto.QuoteRequest;
import com.example.erp.dto.QuoteResponse;
import com.example.erp.entity.Customer;
import com.example.erp.entity.Product;
import com.example.erp.entity.Quote;
import com.example.erp.entity.QuoteLine;
import com.example.erp.entity.QuoteStatus;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.QuoteLineRepository;
import com.example.erp.repository.QuoteRepository;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuoteService {
    private static final int SCALE = 4;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final java.util.Set<String> CURRENCIES = java.util.Set.of("TWD", "USD", "EUR", "JPY");
    private final QuoteRepository quotes;
    private final QuoteLineRepository lines;
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrganizationContext context;

    public QuoteService(QuoteRepository quotes, QuoteLineRepository lines, CustomerRepository customers,
            ProductRepository products, OrganizationContext context) {
        this.quotes = quotes;
        this.lines = lines;
        this.customers = customers;
        this.products = products;
        this.context = context;
    }

    @Transactional
    public QuoteResponse create(QuoteRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        Customer customer = activeCustomer(request.customerId(), organizationId);
        Calculation calculation = calculate(request, organizationId);
        UUID id = UUID.randomUUID();
        Quote quote = new Quote(id, organizationId, customer.getId(), "Q-" + id.toString().substring(0, 8),
                normalizedCurrency(request.currencyCode()), calculation.subtotal(), calculation.taxTotal(),
                calculation.grandTotal(), request.validUntil(), Instant.now());
        quotes.save(quote);
        lines.saveAll(calculation.lines(quote.getId()));
        return response(quote, customer, QuoteStatus.DRAFT);
    }

    @Transactional
    public QuoteResponse update(UUID id, QuoteRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        Quote quote = find(id, organizationId);
        ensureStatus(quote, QuoteStatus.DRAFT);
        Customer customer = activeCustomer(request.customerId(), organizationId);
        Calculation calculation = calculate(request, organizationId);
        quote.update(customer.getId(), normalizedCurrency(request.currencyCode()), calculation.subtotal(),
                calculation.taxTotal(), calculation.grandTotal(), request.validUntil());
        lines.deleteByQuoteId(quote.getId());
        lines.saveAll(calculation.lines(quote.getId()));
        return response(quote, customer, QuoteStatus.DRAFT);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuoteResponse> list(String keyword, QuoteStatus status, PageQuery query) {
        UUID organizationId = context.requiredOrganizationId();
        var page = quotes.search(organizationId, keyword == null ? "" : keyword.trim(), status, pageable(query));
        List<QuoteResponse> items = page.getContent().stream().map(quote -> {
            Customer customer = customers.findByIdAndOrganizationId(quote.getCustomerId(), organizationId).orElse(null);
            return response(quote, customer, effectiveStatus(quote));
        }).toList();
        return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public QuoteResponse detail(UUID id) {
        UUID organizationId = context.requiredOrganizationId();
        Quote quote = find(id, organizationId);
        Customer customer = customers.findByIdAndOrganizationId(quote.getCustomerId(), organizationId).orElse(null);
        return response(quote, customer, effectiveStatus(quote));
    }

    @Transactional
    public QuoteResponse transition(UUID id, QuoteStatus target) {
        UUID organizationId = context.requiredOrganizationId();
        Quote quote = find(id, organizationId);
        QuoteStatus current = effectiveStatus(quote);
        if (current == QuoteStatus.EXPIRED && quote.getStatus() != QuoteStatus.EXPIRED) quote.transitionTo(QuoteStatus.EXPIRED);
        if (!allowedTransitions().getOrDefault(current, java.util.Set.of()).contains(target))
            throw new BusinessRuleException("Quote status transition is not allowed");
        quote.transitionTo(target);
        Customer customer = customers.findByIdAndOrganizationId(quote.getCustomerId(), organizationId).orElse(null);
        return response(quote, customer, target);
    }

    private Customer activeCustomer(UUID id, UUID organizationId) {
        Customer customer = customers.findByIdAndOrganizationId(id, organizationId).orElseThrow(ResourceNotFoundException::new);
        if (!customer.isActive()) throw new BusinessRuleException("Customer is inactive");
        return customer;
    }

    private Calculation calculate(QuoteRequest request, UUID organizationId) {
        String currency = normalizedCurrency(request.currencyCode());
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        java.util.ArrayList<CalculatedLine> calculated = new java.util.ArrayList<>();
        for (QuoteLineRequest requestLine : request.lines()) {
            Product product = products.findByIdAndOrganizationId(requestLine.productId(), organizationId)
                    .orElseThrow(ResourceNotFoundException::new);
            if (!product.isActive()) throw new BusinessRuleException("Product is inactive");
            if (!currency.equals(product.getCurrencyCode())) throw new BusinessRuleException("Currency does not match product");
            BigDecimal discount = requestLine.discount().setScale(SCALE, RoundingMode.HALF_UP);
            BigDecimal base = requestLine.quantity().multiply(product.getUnitPrice()).subtract(discount)
                    .setScale(SCALE, RoundingMode.HALF_UP);
            if (base.signum() < 0) throw new BusinessRuleException("Discount cannot exceed line amount");
            BigDecimal tax = base.multiply(product.getTaxRate()).divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);
            BigDecimal total = base.add(tax).setScale(SCALE, RoundingMode.HALF_UP);
            subtotal = subtotal.add(base);
            taxTotal = taxTotal.add(tax);
            calculated.add(new CalculatedLine(product, requestLine.quantity(), discount, tax, total));
        }
        return new Calculation(subtotal.setScale(SCALE, RoundingMode.HALF_UP), taxTotal.setScale(SCALE, RoundingMode.HALF_UP),
                subtotal.add(taxTotal).setScale(SCALE, RoundingMode.HALF_UP), calculated);
    }

    private QuoteResponse response(Quote quote, Customer customer, QuoteStatus status) {
        return QuoteResponse.from(quote, customer == null ? "" : customer.getName(), lines.findByQuoteId(quote.getId()), status);
    }

    private Quote find(UUID id, UUID organizationId) {
        return quotes.findByIdAndOrganizationId(id, organizationId).orElseThrow(ResourceNotFoundException::new);
    }

    private QuoteStatus effectiveStatus(Quote quote) {
        return quote.getStatus() == QuoteStatus.SENT && quote.getValidUntil().isBefore(LocalDate.now())
                ? QuoteStatus.EXPIRED : quote.getStatus();
    }

    private void ensureStatus(Quote quote, QuoteStatus expected) {
        if (effectiveStatus(quote) != expected) throw new BusinessRuleException("Quote is not editable");
    }

    private String normalizedCurrency(String value) {
        String currency = value.toUpperCase(Locale.ROOT);
        if (!CURRENCIES.contains(currency)) throw new BusinessRuleException("Currency is not supported");
        return currency;
    }

    private PageRequest pageable(PageQuery query) {
        return PageRequest.of(query.page(), Math.min(query.size(), 100),
                Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort()));
    }

    private Map<QuoteStatus, java.util.Set<QuoteStatus>> allowedTransitions() {
        return Map.of(QuoteStatus.DRAFT, java.util.Set.of(QuoteStatus.SENT, QuoteStatus.CANCELLED),
                QuoteStatus.SENT, java.util.Set.of(QuoteStatus.ACCEPTED, QuoteStatus.REJECTED, QuoteStatus.EXPIRED, QuoteStatus.CANCELLED));
    }

    private record CalculatedLine(Product product, BigDecimal quantity, BigDecimal discount, BigDecimal tax, BigDecimal total) {}
    private record Calculation(BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal, List<CalculatedLine> calculated) {
        List<QuoteLine> lines(UUID quoteId) {
            return calculated.stream().map(line -> new QuoteLine(UUID.randomUUID(), quoteId, line.product().getId(),
                    line.product().getName(), line.product().getDescription(), line.quantity(), line.product().getUnitPrice(),
                    line.discount(), line.product().getTaxRate(), line.total())).toList();
        }
    }
}
