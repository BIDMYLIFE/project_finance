package com.example.erp.controller.api;
import com.example.erp.dto.*; import com.example.erp.entity.PaymentStatus; import com.example.erp.service.*; import jakarta.validation.Valid; import java.util.UUID; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/payments") public class PaymentApiController { private final PaymentService payments; private final PaymentCategoryService categories; public PaymentApiController(PaymentService payments,PaymentCategoryService categories){this.payments=payments;this.categories=categories;}
 @GetMapping public PageResponse<PaymentResponse> list(@RequestParam(required=false)String keyword,@RequestParam(required=false)PaymentStatus status,@Valid PageQuery q){return payments.list(keyword,status,q);}
 @GetMapping("/{id}") public PaymentResponse detail(@PathVariable UUID id){return payments.detail(id);}
 @PostMapping public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(payments.create(r));}
 @PostMapping("/{id}/post") public PaymentResponse post(@PathVariable UUID id,@RequestParam UUID bankAccountId){return payments.post(id,bankAccountId);}
 @PostMapping("/{id}/void") public PaymentResponse voidPayment(@PathVariable UUID id){return payments.voidPayment(id);}
 @PostMapping("/{id}/allocations") public java.util.List<PaymentAllocationResponse> allocate(@PathVariable UUID id,@Valid @RequestBody java.util.List<PaymentAllocationRequest> r){return payments.allocate(id,r);}
 @PostMapping("/{id}/print") public PaymentResponse print(@PathVariable UUID id){return payments.print(id);}
 @GetMapping("/categories") public PageResponse<PaymentCategoryResponse> categories(@Valid PageQuery q){return categories.list(q);}
 @PostMapping("/categories") public ResponseEntity<PaymentCategoryResponse> createCategory(@Valid @RequestBody PaymentCategoryRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(categories.create(r));}
 @PutMapping("/categories/{id}") public PaymentCategoryResponse renameCategory(@PathVariable UUID id,@Valid @RequestBody PaymentCategoryRequest r){return categories.rename(id,r);}
 @DeleteMapping("/categories/{id}") public ResponseEntity<Void> deactivateCategory(@PathVariable UUID id){categories.deactivate(id);return ResponseEntity.noContent().build();}
}
