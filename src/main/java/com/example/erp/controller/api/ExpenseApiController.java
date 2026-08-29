package com.example.erp.controller.api;

import com.example.erp.dto.ExpenseRequest;
import com.example.erp.dto.ExpenseResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.entity.ExpenseStatus;
import com.example.erp.service.ExpenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/expenses")
@PreAuthorize("hasRole('ADMIN')")
public class ExpenseApiController {
    private final ExpenseService service;
    public ExpenseApiController(ExpenseService service) { this.service = service; }

    @GetMapping
    public PageResponse<ExpenseResponse> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) ExpenseStatus status,
                                              @RequestParam(required = false) UUID categoryId,
                                              @RequestParam(required = false) UUID bankAccountId,
                                              @RequestParam(required = false) LocalDate fromDate,
                                              @RequestParam(required = false) LocalDate toDate,
                                              @Valid PageQuery query) {
        return service.list(keyword, status, categoryId, bankAccountId, fromDate, toDate, query);
    }

    @GetMapping("/{id}")
    public ExpenseResponse detail(@PathVariable UUID id) { return service.detail(id); }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/confirm")
    public ExpenseResponse confirm(@PathVariable UUID id, @RequestParam(required = false) UUID bankAccountId) {
        return service.confirm(id, bankAccountId);
    }

    @DeleteMapping("/{id}")
    public ExpenseResponse voidExpense(@PathVariable UUID id) { return service.voidExpense(id); }
}
