package com.example.erp.service;

import com.example.erp.dto.CustomerRequest;
import com.example.erp.dto.CustomerResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.dto.ProductRequest;
import com.example.erp.dto.ProductResponse;
import com.example.erp.entity.Customer;
import com.example.erp.entity.Product;
import com.example.erp.exception.BusinessRuleException;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.security.OrganizationContext;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MasterDataService {
    private static final java.util.Set<String> CURRENCIES = java.util.Set.of("TWD", "USD", "EUR", "JPY");
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrganizationContext context;
    public MasterDataService(CustomerRepository customers, ProductRepository products, OrganizationContext context) { this.customers = customers; this.products = products; this.context = context; }
    @Transactional public CustomerResponse createCustomer(CustomerRequest request) {
        UUID organizationId = context.requiredOrganizationId();
        String code = request.customerCode().trim();
        if (customers.existsByOrganizationIdAndCustomerCode(organizationId, code)) throw new BusinessRuleException("Customer code already exists");
        return CustomerResponse.from(customers.save(new Customer(UUID.randomUUID(), organizationId, code, request.name().trim(), request.email(), request.phone(), Instant.now())));
    }
    @Transactional public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customers.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new);
        if (!customer.getCustomerCode().equals(request.customerCode().trim())) throw new BusinessRuleException("Customer code cannot change");
        customer.update(request.name().trim(), request.email(), request.phone(), Instant.now()); return CustomerResponse.from(customer);
    }
    @Transactional public void deactivateCustomer(UUID id) { customers.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new).deactivate(Instant.now()); }
    @Transactional(readOnly = true) public PageResponse<CustomerResponse> customers(String keyword, boolean active, PageQuery query) {
        var page = customers.findByOrganizationIdAndActiveAndNameContainingIgnoreCase(context.requiredOrganizationId(), active, keyword == null ? "" : keyword, pageable(query));
        return PageResponse.of(page.map(CustomerResponse::from).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
    @Transactional public ProductResponse createProduct(ProductRequest request) {
        UUID organizationId = context.requiredOrganizationId(); validateProduct(request);
        String code = request.productCode().trim(); if (products.existsByOrganizationIdAndProductCode(organizationId, code)) throw new BusinessRuleException("Product code already exists");
        return ProductResponse.from(products.save(new Product(UUID.randomUUID(), organizationId, code, request.name().trim(), request.description(), request.unitPrice(), request.currencyCode().toUpperCase(Locale.ROOT), request.taxRate(), Instant.now())));
    }
    @Transactional public ProductResponse updateProduct(UUID id, ProductRequest request) {
        validateProduct(request); Product product = products.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new);
        if (!product.getProductCode().equals(request.productCode().trim())) throw new BusinessRuleException("Product code cannot change");
        product.update(request.name().trim(), request.description(), request.unitPrice(), request.currencyCode().toUpperCase(Locale.ROOT), request.taxRate(), Instant.now()); return ProductResponse.from(product);
    }
    @Transactional public void deactivateProduct(UUID id) { products.findByIdAndOrganizationId(id, context.requiredOrganizationId()).orElseThrow(ResourceNotFoundException::new).deactivate(Instant.now()); }
    @Transactional(readOnly = true) public PageResponse<ProductResponse> products(String keyword, boolean active, PageQuery query) {
        var page = products.findByOrganizationIdAndActiveAndNameContainingIgnoreCase(context.requiredOrganizationId(), active, keyword == null ? "" : keyword, pageable(query));
        return PageResponse.of(page.map(ProductResponse::from).getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
    private void validateProduct(ProductRequest request) { if (!CURRENCIES.contains(request.currencyCode().toUpperCase(Locale.ROOT))) throw new BusinessRuleException("Currency is not supported"); if (request.unitPrice().signum() < 0) throw new BusinessRuleException("Unit price must be non-negative"); }
    private PageRequest pageable(PageQuery query) { return PageRequest.of(query.page(), Math.min(query.size(), 100), Sort.by(Sort.Direction.fromString(query.safeDirection()), query.sort())); }
}