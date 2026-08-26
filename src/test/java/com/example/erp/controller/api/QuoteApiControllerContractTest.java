package com.example.erp.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.erp.dto.PageResponse;
import com.example.erp.dto.QuoteRequest;
import com.example.erp.dto.QuoteResponse;
import com.example.erp.entity.QuoteStatus;
import com.example.erp.service.QuoteService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class QuoteApiControllerContractTest {
    private QuoteService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(QuoteService.class);
        mvc = MockMvcBuilders.standaloneSetup(new QuoteApiController(service)).build();
    }

    @Test
    void listUsesKeywordStatusAndPageResponse() throws Exception {
        when(service.list(eq("Acme"), eq(QuoteStatus.SENT), any())).thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));

        mvc.perform(get("/api/v1/quotes").param("keyword", "Acme").param("status", "SENT").param("page", "0").param("size", "20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray()).andExpect(jsonPath("$.totalPages").value(0));
        verify(service).list(eq("Acme"), eq(QuoteStatus.SENT), any());
    }

    @Test
    void createUpdateAndLifecycleRoutesUseExpectedMethods() throws Exception {
        UUID id = UUID.randomUUID();
        QuoteResponse response = new QuoteResponse(id, null, QuoteStatus.DRAFT, UUID.randomUUID(), "Acme", "TWD",
                BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, LocalDate.now().plusDays(1), Instant.now(), List.of());
        when(service.create(any(QuoteRequest.class))).thenReturn(response);
        when(service.update(eq(id), any(QuoteRequest.class))).thenReturn(response);
        when(service.transition(eq(id), eq(QuoteStatus.SENT))).thenReturn(response);

        String body = "{\"customerId\":\"" + response.customerId() + "\",\"currencyCode\":\"TWD\",\"validUntil\":\"2099-01-01\",\"lines\":[{\"productId\":\"" + UUID.randomUUID() + "\",\"quantity\":1,\"discount\":0}]}";
        mvc.perform(post("/api/v1/quotes").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        mvc.perform(put("/api/v1/quotes/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        mvc.perform(post("/api/v1/quotes/{id}/submit", id)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DRAFT"));
        verify(service).transition(id, QuoteStatus.SENT);
    }
}
