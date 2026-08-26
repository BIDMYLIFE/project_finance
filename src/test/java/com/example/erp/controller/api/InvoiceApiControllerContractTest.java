package com.example.erp.controller.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.erp.dto.*;
import com.example.erp.service.InvoiceService;
import java.math.BigDecimal; import java.time.*; import java.util.*;
import org.junit.jupiter.api.*; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.*; import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class InvoiceApiControllerContractTest {
    private InvoiceService service; private MockMvc mvc;
    @BeforeEach void setUp(){service=mock(InvoiceService.class);mvc=MockMvcBuilders.standaloneSetup(new InvoiceApiController(service)).build();}
    @Test void invoiceRoutesUseExpectedMethodsAndStatuses() throws Exception { UUID id=UUID.randomUUID(); InvoiceResponse response=new InvoiceResponse(id,"INV-2026-0001",com.example.erp.entity.InvoiceStatus.DRAFT,UUID.randomUUID(),"Acme",null,"TWD",LocalDate.now(),LocalDate.now().plusDays(30),BigDecimal.TEN,BigDecimal.ONE,new BigDecimal("11"),BigDecimal.ZERO,new BigDecimal("11"),Instant.now(),List.of()); when(service.list(isNull(),isNull(),isNull(),isNull(),any())).thenReturn(new PageResponse<>(List.of(response),0,20,1,1)); when(service.create(any())).thenReturn(response); when(service.update(eq(id),any())).thenReturn(response); mvc.perform(get("/api/v1/invoices").param("page","0").param("size","20")).andExpect(status().isOk()).andExpect(jsonPath("$.items[0].invoiceNumber").value("INV-2026-0001")); mvc.perform(post("/api/v1/invoices").contentType(MediaType.APPLICATION_JSON).content("{\"customerId\":\""+response.customerId()+"\",\"currencyCode\":\"TWD\",\"invoiceDate\":\"2026-08-26\",\"dueDate\":\"2026-09-25\",\"lines\":[{\"productId\":\""+UUID.randomUUID()+"\",\"quantity\":1,\"discount\":0}]}")).andExpect(status().isCreated()); mvc.perform(put("/api/v1/invoices/{id}",id).contentType(MediaType.APPLICATION_JSON).content("{\"customerId\":\""+response.customerId()+"\",\"currencyCode\":\"TWD\",\"invoiceDate\":\"2026-08-26\",\"dueDate\":\"2026-09-25\",\"lines\":[{\"productId\":\""+UUID.randomUUID()+"\",\"quantity\":1,\"discount\":0}]}")).andExpect(status().isOk()); mvc.perform(post("/api/v1/invoices/{id}/issue",id)).andExpect(status().isOk()); mvc.perform(post("/api/v1/invoices/{id}/cancel",id)).andExpect(status().isOk()); verify(service).issue(id); verify(service).cancel(id); }
}
