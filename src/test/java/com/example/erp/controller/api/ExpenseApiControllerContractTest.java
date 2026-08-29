package com.example.erp.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.erp.dto.*;
import com.example.erp.entity.ExpenseStatus;
import com.example.erp.service.ExpenseService;
import java.math.BigDecimal; import java.time.*; import java.util.*;
import org.junit.jupiter.api.*; import org.springframework.http.MediaType; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.test.web.servlet.MockMvc; import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ExpenseApiControllerContractTest {
    private ExpenseService service; private MockMvc mvc;
    @BeforeEach void setUp() { service = mock(ExpenseService.class); mvc = MockMvcBuilders.standaloneSetup(new ExpenseApiController(service)).build(); }
    @Test void routesUseExpectedMethodsAndStatuses() throws Exception {
        UUID id = UUID.randomUUID(); ExpenseResponse response = new ExpenseResponse(id, UUID.randomUUID(), null, UUID.randomUUID(), "Payee", "Desc", null, BigDecimal.TEN, "TWD", LocalDate.now(), ExpenseStatus.DRAFT, Instant.now(), Instant.now(), null, null);
        when(service.list(eq("Pay"), eq(null), eq(null), eq(null), eq(null), eq(null), any())).thenReturn(new PageResponse<>(List.of(response), 0, 20, 1, 1)); when(service.create(any())).thenReturn(response); when(service.update(eq(id), any())).thenReturn(response); when(service.confirm(eq(id), any())).thenReturn(response); when(service.voidExpense(id)).thenReturn(response);
        mvc.perform(get("/api/v1/expenses").param("keyword", "Pay").param("page", "0").param("size", "20")).andExpect(status().isOk()).andExpect(jsonPath("$.items[0].id").value(id.toString()));
        mvc.perform(post("/api/v1/expenses").contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":\"" + response.categoryId() + "\",\"payeeName\":\"Payee\",\"description\":\"Desc\",\"amount\":10,\"currencyCode\":\"TWD\",\"expenseDate\":\"2026-08-29\"}")).andExpect(status().isCreated());
        mvc.perform(put("/api/v1/expenses/{id}", id).contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":\"" + response.categoryId() + "\",\"payeeName\":\"Payee\",\"description\":\"Desc\",\"amount\":10,\"currencyCode\":\"TWD\",\"expenseDate\":\"2026-08-29\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/expenses/{id}/confirm", id)).andExpect(status().isOk()); mvc.perform(delete("/api/v1/expenses/{id}", id)).andExpect(status().isOk());
        verify(service).voidExpense(id); assertThat(ExpenseApiController.class.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('ADMIN')");
    }
    @Test void invalidCreateIsRejected() throws Exception { mvc.perform(post("/api/v1/expenses").contentType(MediaType.APPLICATION_JSON).content("{\"payeeName\":\"\"}" )).andExpect(status().isBadRequest()); }
}
