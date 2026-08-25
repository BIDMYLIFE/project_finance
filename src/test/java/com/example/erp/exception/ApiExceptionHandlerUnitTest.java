package com.example.erp.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiExceptionHandlerUnitTest {
    @Test
    void authenticationErrorDoesNotExposeInternals() {
        var response = new ApiExceptionHandler().authentication();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.code()).isEqualTo("AUTHENTICATION_FAILED");
        assertThat(body.message()).doesNotContain("stack", "token", "secret");
        assertThat(body.fields()).isEmpty();
    }
}