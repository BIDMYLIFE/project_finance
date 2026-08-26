package com.example.erp.web;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*; import java.nio.charset.StandardCharsets; import org.junit.jupiter.api.Test;
class InvoicesWorkflowAssetTest {
 @Test void invoicePageUsesLocalAssetsAndRoutes() throws IOException { String html=read("templates/invoices/list.html"); String js=read("static/js/pages/invoices.js"); String api=read("static/js/api/invoices-api.js"); assertTrue(html.contains("/css/invoices.css")); assertTrue(html.contains("/js/api/invoices-api.js")); assertTrue(html.contains("/js/pages/invoices.js")); assertTrue(html.contains("/invoices")); assertTrue(js.contains("invoicesApi.issue")); assertTrue(api.contains("/invoices")); assertFalse(html.contains("cdn")); assertFalse(html.contains("https://")); }
 @Test void invoicePageContainsAccessibleResponsiveWorkflow() throws IOException { String html=read("templates/invoices/list.html"); assertTrue(html.contains("aria-live")); assertTrue(html.contains("invoice-customer")); assertTrue(html.contains("invoice-date")); assertTrue(html.contains("invoice-due-date")); assertTrue(html.contains("@click=\"addLine\"")); assertTrue(html.contains("@click=\"openDetail(invoice)\"")); assertTrue(html.contains("@click=\"lifecycle(invoice, 'issue')\"")); assertTrue(html.contains("@click=\"lifecycle(invoice, 'cancel')\"")); }
 private String read(String path)throws IOException{try(InputStream in=getClass().getClassLoader().getResourceAsStream(path)){assertNotNull(in,path);return new String(in.readAllBytes(),StandardCharsets.UTF_8);}}
}
