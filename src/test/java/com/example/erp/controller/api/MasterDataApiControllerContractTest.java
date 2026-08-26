package com.example.erp.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.erp.dto.CustomerRequest;
import com.example.erp.dto.CustomerResponse;
import com.example.erp.dto.PageResponse;
import com.example.erp.service.MasterDataService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MasterDataApiControllerContractTest {
    private MasterDataService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(MasterDataService.class);
        mvc = MockMvcBuilders.standaloneSetup(new MasterDataApiController(service)).build();
    }

    @Test
    void listUsesExistingCriteriaAndPageResponse() throws Exception {
        when(service.customers(eq("Acme"), eq(false), any())).thenReturn(new PageResponse<>(List.of(), 1, 20, 0, 0));

        mvc.perform(get("/api/v1/customers").param("keyword", "Acme").param("active", "false")
                        .param("page", "1").param("size", "20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.page").value(1)).andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.total").value(0)).andExpect(jsonPath("$.totalPages").value(0));
        verify(service).customers(eq("Acme"), eq(false), any());
    }

    @Test
    void customerCrudRoutesKeepExistingMethodsAndStatuses() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerResponse response = new CustomerResponse(id, "C-001", "Acme", "a@example.invalid", "123", true);
        when(service.createCustomer(any(CustomerRequest.class))).thenReturn(response);
        when(service.updateCustomer(eq(id), any(CustomerRequest.class))).thenReturn(response);

        mvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerCode\":\"C-001\",\"name\":\"Acme\",\"email\":\"a@example.invalid\",\"phone\":\"123\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.customerCode").value("C-001"));
        mvc.perform(put("/api/v1/customers/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerCode\":\"C-001\",\"name\":\"Acme\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id.toString()));
        mvc.perform(delete("/api/v1/customers/{id}", id)).andExpect(status().isNoContent());
        verify(service).deactivateCustomer(id);
    }
}