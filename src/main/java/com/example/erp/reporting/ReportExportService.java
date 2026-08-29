package com.example.erp.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

@Service
public class ReportExportService {
    private static final int MAX_ROWS = ReportPolicy.MAX_EXPORT_ROWS;
    private final ReportQueryService reports;

    public ReportExportService(ReportQueryService reports) { this.reports = reports; }

    public byte[] csv(ReportType type, ReportFilterRequest request) {
        ReportResponse response = exportResponse(type, request);
        AppliedFilters filters = response.appliedFilters();
        StringBuilder out = new StringBuilder("# reportType=").append(response.reportType()).append("; dateBasis=").append(response.dateBasis())
                .append("; from=").append(filters.from()).append("; to=").append(filters.to()).append("; currency=").append(filters.currencyCode())
                .append("; status=").append(filters.status()).append("; sort=").append(filters.sort()).append("; direction=").append(filters.direction())
                .append("; generatedAt=").append(java.time.Instant.now()).append("\r\n")
                .append("reportType,dateBasis,sourceType,sourceId,displayNumber,status,date,currency,amount\r\n");
        response.rows().forEach(row -> out.append(response.reportType()).append(',').append(response.dateBasis()).append(',')
                .append(row.source().sourceType()).append(',').append(row.source().sourceId()).append(',')
                .append(csv(row.source().displayNumber())).append(',').append(row.source().status()).append(',')
                .append(row.date()).append(',').append(row.currencyCode()).append(',').append(row.amount()).append("\r\n"));
        out.append("summaryCount,,,,,,,amount\r\n").append(response.summary().count()).append(",,,,,,,").append(response.summary().amount()).append("\r\n");
        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] xlsx(ReportType type, ReportFilterRequest request) throws IOException {
        ReportResponse response = exportResponse(type, request);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet detail = workbook.createSheet("明細");
            Row header = detail.createRow(0);
            String[] headings = {"來源類型", "來源 ID", "顯示編號", "狀態", "日期", "幣別", "金額"};
            for (int i = 0; i < headings.length; i++) header.createCell(i).setCellValue(headings[i]);
            int index = 1;
            for (ReportRow row : response.rows()) {
                Row line = detail.createRow(index++);
                line.createCell(0).setCellValue(row.source().sourceType()); line.createCell(1).setCellValue(row.source().sourceId().toString());
                line.createCell(2).setCellValue(row.source().displayNumber()); line.createCell(3).setCellValue(row.source().status());
                line.createCell(4).setCellValue(row.date().toString()); line.createCell(5).setCellValue(row.currencyCode());
                line.createCell(6).setCellValue(row.amount().doubleValue());
            }
            Sheet summary = workbook.createSheet("總計");
            summary.createRow(0).createCell(0).setCellValue("報表"); summary.getRow(0).createCell(1).setCellValue(type.name());
            summary.createRow(1).createCell(0).setCellValue("日期基準"); summary.getRow(1).createCell(1).setCellValue(response.dateBasis().name());
            summary.createRow(2).createCell(0).setCellValue("查詢期間"); summary.getRow(2).createCell(1).setCellValue(response.appliedFilters().from() + " ~ " + response.appliedFilters().to());
            summary.createRow(3).createCell(0).setCellValue("筆數"); summary.getRow(3).createCell(1).setCellValue(response.summary().count());
            summary.createRow(4).createCell(0).setCellValue("金額"); summary.getRow(4).createCell(1).setCellValue(response.summary().amount().doubleValue());
            for (Sheet sheet : workbook) for (int i = 0; i < 7; i++) sheet.autoSizeColumn(i);
            workbook.write(out); return out.toByteArray();
        }
    }

    public byte[] pdf(ReportType type, ReportFilterRequest request) throws IOException, DocumentException {
        ReportResponse response = exportResponse(type, request);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, out); document.open();
            document.add(new Paragraph(type.name() + " Report"));
            document.add(new Paragraph("Date basis: " + response.dateBasis().name() + " | From: " + response.appliedFilters().from() + " | To: " + response.appliedFilters().to() + " | Generated: " + java.time.Instant.now()));
            document.add(new Paragraph("Rows: " + response.summary().count() + " | Amount: " + response.summary().amount()));
            PdfPTable table = new PdfPTable(7); table.setWidthPercentage(100);
            String[] headings = {"Source", "ID", "Number", "Status", "Date", "Currency", "Amount"};
            for (String heading : headings) table.addCell(heading);
            for (ReportRow row : response.rows()) {
                table.addCell(row.source().sourceType()); table.addCell(row.source().sourceId().toString()); table.addCell(row.source().displayNumber());
                table.addCell(row.source().status()); table.addCell(row.date().toString()); table.addCell(row.currencyCode()); table.addCell(row.amount().toPlainString());
            }
            document.add(table); document.close(); return out.toByteArray();
        }
    }

    private ReportResponse exportResponse(ReportType type, ReportFilterRequest request) {
        ReportFilterRequest exportRequest = new ReportFilterRequest(request.from(), request.to(), request.customerId(), request.categoryId(), request.accountId(), request.currencyCode(), request.status(), request.sort(), request.direction(), 0, ReportPolicy.MAX_EXPORT_ROWS);
        ReportResponse response = reports.query(type, exportRequest);
        if (response.totalRows() > MAX_ROWS || response.rows().size() < response.totalRows()) throw new IllegalArgumentException("Report export exceeds the row limit");
        return response;
    }
    private String csv(String value) { return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\""; }
}
