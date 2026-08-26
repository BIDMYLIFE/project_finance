package com.example.erp.controller.api;

import com.example.erp.dto.BankAccountRequest;
import com.example.erp.dto.BankAccountResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.service.BankAccountService;
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
@RequestMapping("/api/v1/bank-accounts")
public class BankAccountApiController {
    private final BankAccountService service;

    public BankAccountApiController(BankAccountService service) { this.service = service; }

    @GetMapping
    public PageResponse<BankAccountResponse> accounts(@RequestParam(required = false) String keyword,
                                                       @RequestParam(defaultValue = "true") boolean active,
                                                       @Valid PageQuery query) {
        return service.accounts(keyword, active, query);
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> create(@Valid @RequestBody BankAccountRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public BankAccountResponse update(@PathVariable UUID id, @Valid @RequestBody BankAccountRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
