package com.example.erp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.erp.entity.ExpenseCategory;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:expense_categories;MODE=MSSQLServer;DB_CLOSE_DELAY=0",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/expense-category-repository-schema.sql")
class ExpenseCategoryRepositoryIntegrationTest {
    @Autowired private ExpenseCategoryRepository repository;
    private UUID firstOrganization;
    private UUID otherOrganization;

    @BeforeEach
    void setUp() {
        firstOrganization = UUID.randomUUID();
        otherOrganization = UUID.randomUUID();
        repository.save(new ExpenseCategory(UUID.randomUUID(), firstOrganization, "Travel", Instant.now()));
        repository.save(new ExpenseCategory(UUID.randomUUID(), firstOrganization, "Office", Instant.now()));
        repository.save(new ExpenseCategory(UUID.randomUUID(), otherOrganization, "Travel", Instant.now()));
    }

    @Test
    void searchesByOrganizationActiveKeywordAndPage() {
        var page = repository.findByOrganizationIdAndActiveAndNameContainingIgnoreCase(
                firstOrganization, true, "rav", PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).extracting(ExpenseCategory::getName).containsExactly("Travel");
        assertThat(repository.findByIdAndOrganizationId(UUID.randomUUID(), otherOrganization)).isEmpty();
    }

    @Test
    void duplicateNamesAreScopedToOrganization() {
        assertThat(repository.existsByOrganizationIdAndNameIgnoreCase(firstOrganization, "travel")).isTrue();
        assertThat(repository.existsByOrganizationIdAndNameIgnoreCase(otherOrganization, "travel")).isTrue();
        assertThat(repository.existsByOrganizationIdAndNameIgnoreCase(UUID.randomUUID(), "travel")).isFalse();
    }
}
