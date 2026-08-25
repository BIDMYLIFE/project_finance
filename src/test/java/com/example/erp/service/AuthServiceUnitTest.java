package com.example.erp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.erp.config.SecurityProperties;
import com.example.erp.exception.AuthenticationFailureException;
import com.example.erp.exception.InvalidSessionException;
import com.example.erp.entity.AuthSession;
import com.example.erp.entity.User;
import com.example.erp.repository.AuthSessionRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.security.JwtService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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

    @Test
    void refreshRotatesSessionAndRejectsReuse() {
        UserRepository users = mock(UserRepository.class);
        AuthSessionRepository sessions = mock(AuthSessionRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        SecurityProperties properties = properties();
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        AuthSession session = new AuthSession(UUID.randomUUID(), userId, organizationId, new byte[32], Instant.now(), Instant.now().plusSeconds(3600));
        when(sessions.findByRefreshTokenHash(any(byte[].class))).thenReturn(Optional.of(session), Optional.empty());
        when(users.findByIdAndOrganizationId(userId, organizationId)).thenReturn(Optional.of(new User(userId, organizationId, "admin@example.invalid", "hash", Instant.now())));
        AuthService service = new AuthService(users, sessions, encoder, new JwtService(properties), properties);

        AuthService.LoginResult result = service.refresh("refresh-token");

        verify(sessions).save(session);
        assertThatThrownBy(() -> service.refresh("refresh-token")).isExactlyInstanceOf(InvalidSessionException.class);
        org.assertj.core.api.Assertions.assertThat(result.refreshToken()).isNotEqualTo("refresh-token");
    }

    @Test
    void expiredOrRevokedRefreshIsRejectedAndLogoutIsIdempotent() {
        UserRepository users = mock(UserRepository.class);
        AuthSessionRepository sessions = mock(AuthSessionRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        SecurityProperties properties = properties();
        AuthSession expired = new AuthSession(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new byte[32], Instant.now().minusSeconds(10), Instant.now().minusSeconds(1));
        when(sessions.findByRefreshTokenHash(any(byte[].class))).thenReturn(Optional.of(expired));
        when(sessions.findById(expired.getId())).thenReturn(Optional.of(expired));
        AuthService service = new AuthService(users, sessions, encoder, new JwtService(properties), properties);

        assertThatThrownBy(() -> service.refresh("expired-token")).isExactlyInstanceOf(InvalidSessionException.class);
        service.logout(expired.getId());
        service.logout(UUID.randomUUID());
        verify(sessions).save(expired);
    }

    private SecurityProperties properties() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setSecret("01234567890123456789012345678901");
        return properties;
    }
}