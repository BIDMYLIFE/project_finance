package com.example.erp.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public record InvoiceRequest(@NotNull UUID customerId, @NotNull @Pattern(regexp="TWD|USD|EUR|JPY") String currencyCode, @NotNull LocalDate invoiceDate, @NotNull @FutureOrPresent LocalDate dueDate, @NotEmpty @Size(max=100) List<@Valid InvoiceLineRequest> lines) {}
