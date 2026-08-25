package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.erp.config.SecurityProperties;
import com.example.erp.exception.AuthenticationFailureException;
import com.example.erp.repository.AuthSessionRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceUnitTest {
    @Test
    void unknownEmailAndWrongPasswordUseTheSameFailure() {
        UserRepository users = mock(UserRepository.class);
        AuthSessionRepository sessions = mock(AuthSessionRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).thenReturn("dummy-hash");
        when(users.findByEmailIgnoreCase("missing@example.invalid")).thenReturn(java.util.Optional.empty());
        AuthService service = new AuthService(users, sessions, encoder, new JwtService(properties()), properties());

        assertThatThrownBy(() -> service.login("missing@example.invalid", "wrong-password"))
                .isExactlyInstanceOf(AuthenticationFailureException.class).hasMessage("Authentication failed");
        verify(encoder).matches("wrong-password", "dummy-hash");
    }

    private SecurityProperties properties() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setSecret("01234567890123456789012345678901");
        return properties;
    }
}