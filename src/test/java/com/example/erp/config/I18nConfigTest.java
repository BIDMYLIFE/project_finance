package com.example.erp.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

class I18nConfigTest {

    private final I18nConfig config = new I18nConfig();

    @Test
    void localeResolverIsSessionLocaleResolver() {
        assertThat(config.localeResolver()).isInstanceOf(SessionLocaleResolver.class);
    }

    @Test
    void localeChangeInterceptorParamNameIsLang() {
        LocaleChangeInterceptor interceptor = config.localeChangeInterceptor();
        assertThat(interceptor.getParamName()).isEqualTo("lang");
    }
}
