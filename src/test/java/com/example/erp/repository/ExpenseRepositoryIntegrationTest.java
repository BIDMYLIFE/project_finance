package com.example.erp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.entity.Expense;
import com.example.erp.entity.ExpenseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:expenses;MODE=MSSQLServer;DB_CLOSE_DELAY=0",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/expense-repository-schema.sql")
class ExpenseRepositoryIntegrationTest {
    @Autowired private ExpenseRepository repository;
    private UUID organizationId;
    private UUID otherOrganizationId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        organizationId = UUID.randomUUID();
        otherOrganizationId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        repository.save(expense(organizationId, "Office rent", ExpenseStatus.DRAFT, LocalDate.of(2026, 8, 1)));
        repository.save(expense(organizationId, "Taxi to client", ExpenseStatus.CONFIRMED, LocalDate.of(2026, 8, 2)));
        repository.save(expense(otherOrganizationId, "Office rent", ExpenseStatus.DRAFT, LocalDate.of(2026, 8, 3)));
    }

    @Test
    void searchesWithinOrganizationByStatusKeywordAndDate() {
        var page = repository.search(organizationId, ExpenseStatus.DRAFT, categoryId, null,
                "office", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 1), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getDescription()).isEqualTo("Office rent");
    }

    @Test
    void neverReturnsAnotherOrganizationsExpenses() {
        var page = repository.search(organizationId, null, null, null, null, null, null, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(repository.findByIdAndOrganizationId(UUID.randomUUID(), otherOrganizationId)).isEmpty();
    }

    private Expense expense(UUID organization, String description, ExpenseStatus status, LocalDate date) {
        Expense expense = new Expense(UUID.randomUUID(), organization, categoryId, null, UUID.randomUUID(),
                "Vendor", description, null, new BigDecimal("100.0000"), "TWD", date, java.time.Instant.now());
        if (status == ExpenseStatus.CONFIRMED) {
            expense.confirm(UUID.randomUUID(), java.time.Instant.now());
        }
        return expense;
    }
}
