package com.example.erp.controller.api;

import com.example.erp.dto.CustomerRequest;
import com.example.erp.dto.CustomerResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.dto.ProductRequest;
import com.example.erp.dto.ProductResponse;
import com.example.erp.service.MasterDataService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MasterDataApiController {
    private final MasterDataService service;
    public MasterDataApiController(MasterDataService service) { this.service = service; }
    @PostMapping("/customers") public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) { return ResponseEntity.status(201).body(service.createCustomer(request)); }
    @PutMapping("/customers/{id}") public CustomerResponse updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) { return service.updateCustomer(id, request); }
    @DeleteMapping("/customers/{id}") public ResponseEntity<Void> deactivateCustomer(@PathVariable UUID id) { service.deactivateCustomer(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/customers") public PageResponse<CustomerResponse> customers(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "true") boolean active, @Valid PageQuery query) { return service.customers(keyword, active, query); }
    @PostMapping("/products") public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) { return ResponseEntity.status(201).body(service.createProduct(request)); }
    @PutMapping("/products/{id}") public ProductResponse updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) { return service.updateProduct(id, request); }
    @DeleteMapping("/products/{id}") public ResponseEntity<Void> deactivateProduct(@PathVariable UUID id) { service.deactivateProduct(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/products") public PageResponse<ProductResponse> products(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "true") boolean active, @Valid PageQuery query) { return service.products(keyword, active, query); }
}