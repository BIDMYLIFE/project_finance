package com.example.erp.controller.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CustomersPageControllerTest {
    @Test
    void customersRouteReturnsOnlyCustomersViewAndEmbeddedMessages() throws Exception {
        MessageSource messages = new MessageSource() {
            @Override public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) { return "translated:" + code; }
            @Override public String getMessage(String code, Object[] args, Locale locale) { return "translated:" + code; }
            @Override public String getMessage(MessageSourceResolvable resolvable, Locale locale) { return "translated:" + resolvable.getCodes()[0]; }
        };
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new CustomersPageController(messages)).build();

        var result = mvc.perform(get("/customers").locale(Locale.ENGLISH))
                .andExpect(status().isOk()).andExpect(view().name("customers/list")).andReturn();

        assertThat(result.getModelAndView().getModel().get("messages")).asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
                .containsEntry("customers.heading", "translated:customers.heading");
    }
}