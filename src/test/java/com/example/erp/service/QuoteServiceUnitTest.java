package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.erp.dto.QuoteLineRequest;
import com.example.erp.dto.QuoteRequest;
import com.example.erp.entity.Customer;
import com.example.erp.entity.Product;
import com.example.erp.entity.Quote;
import com.example.erp.entity.QuoteStatus;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.QuoteLineRepository;
import com.example.erp.repository.QuoteRepository;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceUnitTest {
    private QuoteRepository quotes;
    private QuoteLineRepository lines;
    private CustomerRepository customers;
    private ProductRepository products;
    private OrganizationContext context;
    private QuoteService service;
    private UUID organizationId;
    private UUID customerId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        quotes = mock(QuoteRepository.class);
        lines = mock(QuoteLineRepository.class);
        customers = mock(CustomerRepository.class);
        products = mock(ProductRepository.class);
        context = mock(OrganizationContext.class);
        organizationId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(organizationId);
        when(lines.findByQuoteId(any())).thenReturn(List.of());
        when(customers.findByIdAndOrganizationId(customerId, organizationId)).thenReturn(Optional.of(
                new Customer(customerId, organizationId, "C-001", "Acme", null, null, Instant.now())));
        when(products.findByIdAndOrganizationId(productId, organizationId)).thenReturn(Optional.of(
                new Product(productId, organizationId, "P-001", "Service", "", new BigDecimal("100.00"), "TWD", new BigDecimal("5"), Instant.now())));
        service = new QuoteService(quotes, lines, customers, products, context);
    }

    @Test
    void createsDraftWithCalculatedTotalsAndSnapshots() {
        var response = service.create(request(new BigDecimal("2"), new BigDecimal("10")));

        assertThat(response.status()).isEqualTo(QuoteStatus.DRAFT);
        assertThat(response.subtotal()).isEqualByComparingTo("190.0000");
        assertThat(response.taxTotal()).isEqualByComparingTo("9.5000");
        assertThat(response.grandTotal()).isEqualByComparingTo("199.5000");
        verify(quotes).save(any(Quote.class));
        verify(lines).saveAll(any());
    }

    @Test
    void rejectsInactiveProductBeforeSaving() {
        Product inactive = new Product(productId, organizationId, "P-001", "Service", "", BigDecimal.TEN, "TWD", BigDecimal.ZERO, Instant.now());
        inactive.deactivate(Instant.now());
        when(products.findByIdAndOrganizationId(productId, organizationId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.create(request(BigDecimal.ONE, BigDecimal.ZERO)))
                .isExactlyInstanceOf(BusinessRuleException.class);
        verify(quotes, never()).save(any());
    }

    @Test
    void onlyDraftCanBeEditedAndSent() {
        Quote quote = new Quote(UUID.randomUUID(), organizationId, customerId, null, "TWD", BigDecimal.ONE,
                BigDecimal.ZERO, BigDecimal.ONE, LocalDate.now().plusDays(1), Instant.now());
        when(quotes.findByIdAndOrganizationId(quote.getId(), organizationId)).thenReturn(Optional.of(quote));

        assertThat(service.transition(quote.getId(), QuoteStatus.SENT).status()).isEqualTo(QuoteStatus.SENT);
        assertThatThrownBy(() -> service.update(quote.getId(), request(BigDecimal.ONE, BigDecimal.ZERO)))
                .isExactlyInstanceOf(BusinessRuleException.class);
    }

    private QuoteRequest request(BigDecimal quantity, BigDecimal discount) {
        return new QuoteRequest(customerId, "TWD", LocalDate.now().plusDays(10),
                List.of(new QuoteLineRequest(productId, quantity, discount)));
    }
}
