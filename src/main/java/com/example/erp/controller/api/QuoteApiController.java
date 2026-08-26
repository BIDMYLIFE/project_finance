package com.example.erp.controller.api;

import com.example.erp.dto.PageQuery;
import com.example.erp.dto.PageResponse;
import com.example.erp.dto.QuoteRequest;
import com.example.erp.dto.QuoteResponse;
import com.example.erp.entity.QuoteStatus;
import com.example.erp.service.QuoteService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteApiController {
    private final QuoteService service;
    public QuoteApiController(QuoteService service) { this.service = service; }

    @GetMapping
    public PageResponse<QuoteResponse> list(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) QuoteStatus status, @Valid PageQuery query) {
        return service.list(keyword, status, query);
    }

    @GetMapping("/{id}")
    public QuoteResponse detail(@PathVariable UUID id) { return service.detail(id); }

    @PostMapping
    public ResponseEntity<QuoteResponse> create(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PutMapping("/{id}")
    public QuoteResponse update(@PathVariable UUID id, @Valid @RequestBody QuoteRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/submit")
    public QuoteResponse submit(@PathVariable UUID id) { return service.transition(id, QuoteStatus.SENT); }

    @PostMapping("/{id}/accept")
    public QuoteResponse accept(@PathVariable UUID id) { return service.transition(id, QuoteStatus.ACCEPTED); }

    @PostMapping("/{id}/reject")
    public QuoteResponse reject(@PathVariable UUID id) { return service.transition(id, QuoteStatus.REJECTED); }

    @PostMapping("/{id}/cancel")
    public QuoteResponse cancel(@PathVariable UUID id) { return service.transition(id, QuoteStatus.CANCELLED); }
}
