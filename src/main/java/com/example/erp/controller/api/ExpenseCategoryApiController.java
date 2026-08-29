package com.example.erp.controller.api;

import com.example.erp.dto.ExpenseCategoryRequest;
import com.example.erp.dto.ExpenseCategoryResponse;
import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.service.ExpenseCategoryService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/expense-categories")
@PreAuthorize("hasRole('ADMIN')")
public class ExpenseCategoryApiController {
    private final ExpenseCategoryService service;

    public ExpenseCategoryApiController(ExpenseCategoryService service) { this.service = service; }

    @GetMapping
    public PageResponse<ExpenseCategoryResponse> categories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "true") boolean active,
            @Valid PageQuery query) {
        return service.categories(keyword, active, query);
    }

    @PostMapping
    public ResponseEntity<ExpenseCategoryResponse> create(@Valid @RequestBody ExpenseCategoryRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ExpenseCategoryResponse update(@PathVariable UUID id, @Valid @RequestBody ExpenseCategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
