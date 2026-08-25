package com.example.erp.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.erp.entity.BankTransactionStatus;
import com.example.erp.entity.PaymentStatus;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReportingFoundationTest {
    @Test
    void validatesBoundedDateRangeAndSort() {
        ReportFilterRequest request = new ReportFilterRequest(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1), null, null, null, "twd", null, "date", "asc", 0, 0);

        request.validate(DateBasis.RECEIVED_AT, Set.of("date", "amount"));

        assertThat(AppliedFilters.from(request).currencyCode()).isEqualTo("TWD");
        assertThat(request.size()).isEqualTo(20);
    }

    @Test
    void rejectsReverseLongOrUnapprovedQueries() {
        ReportFilterRequest reverse = new ReportFilterRequest(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1), null, null, null, "TWD", null, "date", "DESC", 0, 20);
        ReportFilterRequest longRange = new ReportFilterRequest(LocalDate.of(2024, 1, 1), LocalDate.of(2026, 1, 1), null, null, null, "TWD", null, "date", "DESC", 0, 20);
        ReportFilterRequest badSort = new ReportFilterRequest(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), null, null, null, "TWD", null, "secret", "DESC", 0, 20);

        assertThatThrownBy(() -> reverse.validate(DateBasis.RECEIVED_AT, Set.of("date"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> longRange.validate(DateBasis.RECEIVED_AT, Set.of("date"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> badSort.validate(DateBasis.RECEIVED_AT, Set.of("date"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyResponseHasZeroSummary() {
        AppliedFilters filters = AppliedFilters.from(new ReportFilterRequest(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), null, null, null, null, null, null, null, 0, 20));

        ReportResponse response = ReportResponse.empty(ReportType.PENDING_DEPOSITS, DateBasis.RECEIVED_AT, filters);

        assertThat(response.empty()).isTrue();
        assertThat(response.rows()).isEmpty();
        assertThat(response.summary().count()).isZero();
    }

    @Test
    void effectivePolicyExcludesVoidAndReversalStates() {
        assertThat(EffectiveStatePolicy.paymentCounts(PaymentStatus.POSTED)).isTrue();
        assertThat(EffectiveStatePolicy.paymentCounts(PaymentStatus.VOIDED)).isFalse();
        assertThat(EffectiveStatePolicy.bankTransactionCounts(BankTransactionStatus.POSTED)).isTrue();
        assertThat(EffectiveStatePolicy.bankTransactionCounts(BankTransactionStatus.REVERSED)).isFalse();
    }
}