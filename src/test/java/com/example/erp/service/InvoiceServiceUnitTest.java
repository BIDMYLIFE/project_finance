package com.example.erp.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.erp.dto.*;
import com.example.erp.entity.*;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class InvoiceServiceUnitTest {
    private InvoiceRepository invoices; private InvoiceLineRepository lines; private DocumentSequenceRepository sequences; private CustomerRepository customers; private ProductRepository products; private OrganizationContext context; private InvoiceService service; private UUID org, customerId, productId;
    @BeforeEach void setUp() { invoices=mock(InvoiceRepository.class); lines=mock(InvoiceLineRepository.class); sequences=mock(DocumentSequenceRepository.class); customers=mock(CustomerRepository.class); products=mock(ProductRepository.class); context=mock(OrganizationContext.class); org=UUID.randomUUID(); customerId=UUID.randomUUID(); productId=UUID.randomUUID(); when(context.requiredOrganizationId()).thenReturn(org); service=new InvoiceService(invoices,lines,sequences,customers,products,context); }
    @Test void calculatesTotalsAndPreservesProductSnapshot() { Customer c=new Customer(customerId,org,"C-1","Acme",null,null,Instant.now()); Product p=new Product(productId,org,"P-1","Consulting","Fixed",new BigDecimal("100"),"TWD",new BigDecimal("5"),Instant.now()); when(customers.findByIdAndOrganizationId(customerId,org)).thenReturn(Optional.of(c)); when(products.findByIdAndOrganizationId(productId,org)).thenReturn(Optional.of(p)); when(invoices.save(any())).thenAnswer(i->i.getArgument(0)); InvoiceResponse response=service.create(new InvoiceRequest(customerId,"TWD",LocalDate.now(),LocalDate.now().plusDays(30),List.of(new InvoiceLineRequest(productId,new BigDecimal("2"),new BigDecimal("10"))))); assertThat(response.subtotal()).isEqualByComparingTo("190.0000"); assertThat(response.taxTotal()).isEqualByComparingTo("9.5000"); assertThat(response.grandTotal()).isEqualByComparingTo("199.5000"); assertThat(response.balanceDue()).isEqualByComparingTo("199.5000"); verify(lines).saveAll(anyList()); }
    @Test void rejectsForeignOrInactiveProduct() { Customer c=new Customer(customerId,org,"C-1","Acme",null,null,Instant.now()); when(customers.findByIdAndOrganizationId(customerId,org)).thenReturn(Optional.of(c)); when(products.findByIdAndOrganizationId(productId,org)).thenReturn(Optional.empty()); assertThatThrownBy(()->service.create(new InvoiceRequest(customerId,"TWD",LocalDate.now(),LocalDate.now().plusDays(1),List.of(new InvoiceLineRequest(productId,BigDecimal.ONE,BigDecimal.ZERO))))).isExactlyInstanceOf(com.example.erp.exception.ResourceNotFoundException.class); verify(invoices,never()).save(any()); }
    @Test void rejectsNonDraftIssue() { Invoice invoice=mock(Invoice.class); when(invoice.getStatus()).thenReturn(InvoiceStatus.ISSUED); when(invoices.findByIdAndOrganizationId(any(),eq(org))).thenReturn(Optional.of(invoice)); assertThatThrownBy(()->service.issue(UUID.randomUUID())).isExactlyInstanceOf(BusinessRuleException.class); }
}
