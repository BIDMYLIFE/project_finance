package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.erp.dto.CustomerRequest;
import com.example.erp.dto.ProductRequest;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MasterDataServiceUnitTest {
    private CustomerRepository customers;
    private ProductRepository products;
    private OrganizationContext context;
    private MasterDataService service;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        customers = mock(CustomerRepository.class);
        products = mock(ProductRepository.class);
        context = mock(OrganizationContext.class);
        organizationId = UUID.randomUUID();
        when(context.requiredOrganizationId()).thenReturn(organizationId);
        service = new MasterDataService(customers, products, context);
    }

    @Test
    void duplicateCustomerCodeIsRejectedWithinCurrentOrganization() {
        when(customers.existsByOrganizationIdAndCustomerCode(organizationId, "C-001")).thenReturn(true);

        assertThatThrownBy(() -> service.createCustomer(new CustomerRequest(" C-001 ", "Customer", null, null)))
                .isExactlyInstanceOf(BusinessRuleException.class);
        verify(customers, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void unsupportedCurrencyAndNegativePriceAreRejected() {
        ProductRequest unsupported = new ProductRequest("P-001", "Service", null, BigDecimal.TEN, "GBP", BigDecimal.ZERO);
        ProductRequest negative = new ProductRequest("P-002", "Service", null, BigDecimal.ONE.negate(), "TWD", BigDecimal.ZERO);

        assertThatThrownBy(() -> service.createProduct(unsupported)).isExactlyInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> service.createProduct(negative)).isExactlyInstanceOf(BusinessRuleException.class);
        verify(products, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void customerLookupUsesAuthenticatedOrganizationAndHidesForeignRecord() {
        UUID customerId = UUID.randomUUID();
        when(customers.findByIdAndOrganizationId(customerId, organizationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCustomer(customerId, new CustomerRequest("C-001", "Changed", null, null)))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
        verify(customers).findByIdAndOrganizationId(eq(customerId), eq(organizationId));
    }
}