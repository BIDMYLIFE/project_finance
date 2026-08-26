package com.example.erp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.entity.Customer;
import com.example.erp.entity.Quote;
import com.example.erp.entity.QuoteLine;
import com.example.erp.entity.QuoteStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:quotes;MODE=MSSQLServer;DB_CLOSE_DELAY=0",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/quote-repository-schema.sql")
class QuoteRepositoryIntegrationTest {
    @Autowired private QuoteRepository quotes;
    @Autowired private QuoteLineRepository lines;
    @Autowired private CustomerRepository customers;

    private UUID firstOrganization;
    private UUID otherOrganization;
    private UUID customerId;
    private UUID quoteId;

    @BeforeEach
    void setUp() {
        firstOrganization = UUID.randomUUID();
        otherOrganization = UUID.randomUUID();
        customerId = UUID.randomUUID();
        quoteId = UUID.randomUUID();
        Instant now = Instant.now();
        customers.save(new Customer(customerId, firstOrganization, "C-001", "Acme Taiwan", null, null, now));
        customers.save(new Customer(UUID.randomUUID(), otherOrganization, "C-001", "Acme Other", null, null, now));
        quotes.save(new Quote(quoteId, firstOrganization, customerId, "Q-001", "TWD",
                new BigDecimal("100.0000"), new BigDecimal("5.0000"), new BigDecimal("105.0000"),
                LocalDate.now().plusDays(10), now));
    }

    @Test
    void searchesOnlyWithinOrganizationAndSupportsKeywordAndStatus() {
        assertThat(quotes.search(firstOrganization, "acme", QuoteStatus.DRAFT, org.springframework.data.domain.PageRequest.of(0, 20)))
                .hasSize(1).first().extracting(Quote::getId).isEqualTo(quoteId);
        assertThat(quotes.search(otherOrganization, "acme", null, org.springframework.data.domain.PageRequest.of(0, 20))).isEmpty();
        assertThat(quotes.findByIdAndOrganizationId(quoteId, otherOrganization)).isEmpty();
    }

    @Test
    void twoQuotesInSameOrganizationHaveUniqueNumbers() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String number1 = "Q-" + id1.toString().substring(0, 8);
        String number2 = "Q-" + id2.toString().substring(0, 8);
        Instant now = Instant.now();

        quotes.save(new Quote(id1, firstOrganization, customerId, number1, "TWD",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                LocalDate.now().plusDays(10), now));
        quotes.save(new Quote(id2, firstOrganization, customerId, number2, "TWD",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                LocalDate.now().plusDays(10), now));
        quotes.flush();

        assertThat(quotes.search(firstOrganization, "", null, org.springframework.data.domain.PageRequest.of(0, 20)))
                .hasSize(3);
    }

    @Test
    void persistsAndDeletesQuoteLinesByQuoteId() {
        QuoteLine line = new QuoteLine(UUID.randomUUID(), quoteId, UUID.randomUUID(), "Service", "Description",
                new BigDecimal("2.0000"), new BigDecimal("50.0000"), BigDecimal.ZERO,
                new BigDecimal("5.0000"), new BigDecimal("105.0000"));
        lines.saveAndFlush(line);

        assertThat(lines.findByQuoteId(quoteId)).hasSize(1);
        lines.deleteByQuoteId(quoteId);
        lines.flush();
        assertThat(lines.findByQuoteId(quoteId)).isEmpty();
    }
}
