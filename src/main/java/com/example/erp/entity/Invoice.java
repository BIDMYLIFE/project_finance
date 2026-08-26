package com.example.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "invoices", schema = "SOUTHWND")
public class Invoice {
    @Id private UUID id;
    @Column(name="organization_id", nullable=false) private UUID organizationId;
    @Column(name="customer_id", nullable=false) private UUID customerId;
    @Column(name="source_quote_id") private UUID sourceQuoteId;
    @Column(name="invoice_number", length=40) private String invoiceNumber;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private InvoiceStatus status;
    @Column(name="currency_code", nullable=false, length=3) private String currencyCode;
    @Column(name="invoice_date", nullable=false) private LocalDate invoiceDate;
    @Column(name="due_date", nullable=false) private LocalDate dueDate;
    @Column(nullable=false, precision=19, scale=4) private BigDecimal subtotal;
    @Column(name="tax_total", nullable=false, precision=19, scale=4) private BigDecimal taxTotal;
    @Column(name="grand_total", nullable=false, precision=19, scale=4) private BigDecimal grandTotal;
    @Column(name="paid_total", nullable=false, precision=19, scale=4) private BigDecimal paidTotal;
    @Column(name="balance_due", nullable=false, precision=19, scale=4) private BigDecimal balanceDue;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    protected Invoice() {}
    public Invoice(UUID id, UUID organizationId, UUID customerId, UUID sourceQuoteId, String currencyCode, LocalDate invoiceDate, LocalDate dueDate, BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal, Instant createdAt) { this.id=id; this.organizationId=organizationId; this.customerId=customerId; this.sourceQuoteId=sourceQuoteId; this.invoiceNumber=UUID.randomUUID().toString(); this.status=InvoiceStatus.DRAFT; this.currencyCode=currencyCode; this.invoiceDate=invoiceDate; this.dueDate=dueDate; this.subtotal=subtotal; this.taxTotal=taxTotal; this.grandTotal=grandTotal; this.paidTotal=BigDecimal.ZERO; this.balanceDue=grandTotal; this.createdAt=createdAt; }
    public void updateDraft(UUID customerId, String currencyCode, LocalDate invoiceDate, LocalDate dueDate, BigDecimal subtotal, BigDecimal taxTotal, BigDecimal grandTotal) { this.customerId=customerId; this.currencyCode=currencyCode; this.invoiceDate=invoiceDate; this.dueDate=dueDate; this.subtotal=subtotal; this.taxTotal=taxTotal; this.grandTotal=grandTotal; this.balanceDue=grandTotal.subtract(paidTotal); }
    public void issue(String number) { this.invoiceNumber=number; this.status=InvoiceStatus.ISSUED; }
    public void cancel() { this.status=InvoiceStatus.CANCELLED; }
    public void applyPayment(BigDecimal amount) { this.paidTotal = paidTotal.add(amount); this.balanceDue = grandTotal.subtract(paidTotal).max(BigDecimal.ZERO); this.status = balanceDue.signum() == 0 ? InvoiceStatus.PAID : InvoiceStatus.PARTIALLY_PAID; }
    public UUID getId(){return id;} public UUID getOrganizationId(){return organizationId;} public UUID getCustomerId(){return customerId;} public UUID getSourceQuoteId(){return sourceQuoteId;} public String getInvoiceNumber(){return invoiceNumber;} public InvoiceStatus getStatus(){return status;} public String getCurrencyCode(){return currencyCode;} public LocalDate getInvoiceDate(){return invoiceDate;} public LocalDate getDueDate(){return dueDate;} public BigDecimal getSubtotal(){return subtotal;} public BigDecimal getTaxTotal(){return taxTotal;} public BigDecimal getGrandTotal(){return grandTotal;} public BigDecimal getPaidTotal(){return paidTotal;} public BigDecimal getBalanceDue(){return balanceDue;} public Instant getCreatedAt(){return createdAt;}
}
