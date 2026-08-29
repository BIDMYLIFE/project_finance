package com.example.erp.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.example.erp.dto.ExpenseCategoryResponse;
import com.example.erp.dto.PageResponse;
import com.example.erp.service.ExpenseCategoryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ExpenseCategoryApiControllerContractTest {
    private ExpenseCategoryService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(ExpenseCategoryService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ExpenseCategoryApiController(service)).build();
    }

    @Test
    void categoryCrudRoutesUseExpectedContract() throws Exception {
        UUID id = UUID.randomUUID();
        ExpenseCategoryResponse response = new ExpenseCategoryResponse(id, "Travel", true, java.time.Instant.now());
        when(service.categories(eq("trav"), eq(true), any())).thenReturn(new PageResponse<>(List.of(response), 0, 20, 1, 1));
        when(service.create(any())).thenReturn(response);
        when(service.update(eq(id), any())).thenReturn(response);

        mvc.perform(get("/api/v1/expense-categories").param("keyword", "trav").param("active", "true")
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].name").value("Travel"));
        mvc.perform(post("/api/v1/expense-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Travel\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.active").value(true));
        mvc.perform(put("/api/v1/expense-categories/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Office\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id.toString()));
        mvc.perform(delete("/api/v1/expense-categories/{id}", id)).andExpect(status().isNoContent());

        verify(service).categories(eq("trav"), eq(true), any());
        verify(service).deactivate(id);
    }

    @Test
    void invalidNameIsRejectedByBeanValidation() throws Exception {
        mvc.perform(post("/api/v1/expense-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void controllerRequiresAdminRole() {
        PreAuthorize authorization = ExpenseCategoryApiController.class.getAnnotation(PreAuthorize.class);
        assertThat(authorization).isNotNull();
        assertThat(authorization.value()).isEqualTo("hasRole('ADMIN')");
    }
}
