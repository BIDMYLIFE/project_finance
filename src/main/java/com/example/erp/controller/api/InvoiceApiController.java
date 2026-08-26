package com.example.erp.controller.api;
import com.example.erp.dto.*;
import com.example.erp.entity.InvoiceStatus;
import com.example.erp.service.InvoiceService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/invoices")
public class InvoiceApiController {
    private final InvoiceService service; public InvoiceApiController(InvoiceService service){this.service=service;}
    @GetMapping public PageResponse<InvoiceResponse> list(@RequestParam(required=false) String keyword,@RequestParam(required=false) InvoiceStatus status,@RequestParam(required=false) LocalDate fromDate,@RequestParam(required=false) LocalDate toDate,@Valid PageQuery query){return service.list(keyword,status,fromDate,toDate,query);}
    @GetMapping("/{id}") public InvoiceResponse detail(@PathVariable UUID id){return service.detail(id);}
    @PostMapping public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request){return ResponseEntity.status(201).body(service.create(request));}
    @PutMapping("/{id}") public InvoiceResponse update(@PathVariable UUID id,@Valid @RequestBody InvoiceRequest request){return service.update(id,request);}
    @PostMapping("/{id}/issue") public InvoiceResponse issue(@PathVariable UUID id){return service.issue(id);}
    @PostMapping("/{id}/cancel") public InvoiceResponse cancel(@PathVariable UUID id){return service.cancel(id);}
}
