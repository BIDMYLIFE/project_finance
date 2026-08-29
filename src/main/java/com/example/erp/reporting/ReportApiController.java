package com.example.erp.reporting;

import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import org.openpdf.text.DocumentException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import com.example.erp.entity.*;
import com.example.erp.repository.*;
import com.example.erp.security.OrganizationContext;

@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportApiController {
    private final ReportQueryService service;
    private final ReportExportService exports;
    private final OrganizationContext context;
    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final ExpenseRepository expenses;
    private final BankTransactionRepository transactions;
    public ReportApiController(ReportQueryService service, ReportExportService exports, OrganizationContext context,
            InvoiceRepository invoices, PaymentRepository payments, ExpenseRepository expenses, BankTransactionRepository transactions) {
        this.service = service; this.exports = exports; this.context = context; this.invoices = invoices; this.payments = payments; this.expenses = expenses; this.transactions = transactions;
    }

    @GetMapping("/{type}")
    public ReportResponse report(@PathVariable String type, @Valid @ModelAttribute ReportFilterRequest request) {
        return service.query(parse(type), request);
    }

    @GetMapping(value = "/{type}/export", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportCsv(@PathVariable String type, @Valid @ModelAttribute ReportFilterRequest request) {
        return new String(exports.csv(parse(type), request), StandardCharsets.UTF_8);
    }

    @GetMapping("/summary")
    public ReportDashboardSummary dashboardSummary(@RequestParam LocalDate from, @RequestParam LocalDate to,
                                                    @RequestParam(required = false) String currencyCode) {
        ReportFilterRequest request = new ReportFilterRequest(from, to, null, null, null, currencyCode, null, "date", "DESC", 0, 100);
        Map<String, ReportSummary> summaries = new LinkedHashMap<>();
        summaries.put("sales", service.summary(ReportType.INVOICE_STATUS, request));
        summaries.put("payments", service.summary(ReportType.CASH_FLOW, request));
        summaries.put("expenses", service.summary(ReportType.EXPENSES, request));
        summaries.put("receivables", service.summary(ReportType.RECEIVABLE_AGING, request));
        summaries.put("pendingDeposits", service.summary(ReportType.PENDING_DEPOSITS, request));
        return new ReportDashboardSummary(from, to, currencyCode == null ? null : currencyCode.toUpperCase(Locale.ROOT), summaries);
    }

    @GetMapping("/source/{sourceType}/{id}")
    public Map<String, Object> source(@PathVariable String sourceType, @PathVariable java.util.UUID id) {
        java.util.UUID org = context.requiredOrganizationId();
        Object value = switch (sourceType.toUpperCase(Locale.ROOT)) {
            case "INVOICE" -> invoices.findByIdAndOrganizationId(id, org).orElseThrow(com.example.erp.exception.ResourceNotFoundException::new);
            case "PAYMENT" -> payments.findByIdAndOrganizationId(id, org).orElseThrow(com.example.erp.exception.ResourceNotFoundException::new);
            case "EXPENSE" -> expenses.findByIdAndOrganizationId(id, org).orElseThrow(com.example.erp.exception.ResourceNotFoundException::new);
            case "BANK_TRANSACTION" -> transactions.findByIdAndOrganizationId(id, org).orElseThrow(com.example.erp.exception.ResourceNotFoundException::new);
            default -> throw new IllegalArgumentException("Unsupported report source type");
        };
        return Map.of("sourceType", sourceType.toUpperCase(Locale.ROOT), "sourceId", id, "found", value != null);
    }

    @GetMapping(value = "/{type}/export/{format}")
    public ResponseEntity<byte[]> export(@PathVariable String type, @PathVariable String format, @Valid @ModelAttribute ReportFilterRequest request) throws IOException, DocumentException {
        ReportType reportType = parse(type);
        byte[] data = switch (format.toLowerCase(Locale.ROOT)) {
            case "csv" -> exports.csv(reportType, request);
            case "pdf" -> exports.pdf(reportType, request);
            case "xlsx" -> exports.xlsx(reportType, request);
            default -> throw new IllegalArgumentException("Unsupported report export format");
        };
        String contentType = switch (format.toLowerCase(Locale.ROOT)) { case "pdf" -> "application/pdf"; case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; default -> "text/csv; charset=UTF-8"; };
        return ResponseEntity.ok().header("Content-Type", contentType).header("Content-Disposition", "attachment; filename=report." + format.toLowerCase(Locale.ROOT)).body(data);
    }

    private ReportType parse(String value) { return ReportType.valueOf(value.trim().toUpperCase(Locale.ROOT).replace('-', '_')); }
}
