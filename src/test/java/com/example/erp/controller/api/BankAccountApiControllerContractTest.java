package com.example.erp.controller.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.erp.dto.BankAccountRequest;
import com.example.erp.dto.BankAccountResponse;
import com.example.erp.dto.PageResponse;
import com.example.erp.service.BankAccountService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BankAccountApiControllerContractTest {
    private BankAccountService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(BankAccountService.class);
        mvc = MockMvcBuilders.standaloneSetup(new BankAccountApiController(service)).build();
    }

    @Test
    void accountCrudRoutesUseExpectedMethodsAndStatuses() throws Exception {
        UUID id = UUID.randomUUID();
        BankAccountResponse response = new BankAccountResponse(id, "Operating", "TWD", BigDecimal.TEN, true, Instant.now());
        when(service.accounts(eq("Oper"), eq(true), any())).thenReturn(new PageResponse<>(List.of(response), 0, 20, 1, 1));
        when(service.create(any(BankAccountRequest.class))).thenReturn(response);
        when(service.update(eq(id), any(BankAccountRequest.class))).thenReturn(response);

        mvc.perform(get("/api/v1/bank-accounts").param("keyword", "Oper").param("active", "true").param("page", "0").param("size", "20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].accountName").value("Operating"));
        mvc.perform(post("/api/v1/bank-accounts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountName\":\"Operating\",\"currencyCode\":\"TWD\",\"openingBalance\":10}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.currencyCode").value("TWD"));
        mvc.perform(put("/api/v1/bank-accounts/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountName\":\"Operating\",\"currencyCode\":\"TWD\",\"openingBalance\":10}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/bank-accounts/{id}", id)).andExpect(status().isNoContent());
        verify(service).deactivate(id);
    }
}
