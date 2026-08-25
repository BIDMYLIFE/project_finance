package com.example.erp.service;

import com.example.erp.config.SecurityProperties;
import com.example.erp.entity.AuthSession;
import com.example.erp.entity.User;
import com.example.erp.exception.AuthenticationFailureException;
import com.example.erp.exception.InvalidSessionException;
import com.example.erp.repository.AuthSessionRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.security.AuthPrincipal;
import com.example.erp.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final AuthSessionRepository sessions;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityProperties properties;
    private final String dummyHash;
    private final SecureRandom random = new SecureRandom();
    public AuthService(UserRepository users, AuthSessionRepository sessions, PasswordEncoder passwordEncoder,
                       JwtService jwtService, SecurityProperties properties) {
        this.users = users; this.sessions = sessions; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; this.properties = properties;
        dummyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }
    @Transactional
    public LoginResult login(String email, String password) {
        User user = users.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (user == null) {
            passwordEncoder.matches(password, dummyHash);
            throw new AuthenticationFailureException();
        }
        if (!user.isEnabled() || !passwordEncoder.matches(password, user.getPasswordHash())) throw new AuthenticationFailureException();
        Instant now = Instant.now();
        UUID sessionId = UUID.randomUUID();
        String refresh = randomToken();
        AuthSession session = sessions.save(new AuthSession(sessionId, user.getId(), user.getOrganizationId(), hash(refresh), now, now.plus(properties.getJwt().getRefreshTtl())));
        AuthPrincipal principal = new AuthPrincipal(user.getId(), user.getOrganizationId(), user.getEmail(), session.getId());
        return new LoginResult(principal, jwtService.createAccessToken(principal, sessionId, now), refresh);
    }
    @Transactional
    public LoginResult refresh(String refreshToken) {
        AuthSession session = sessions.findByRefreshTokenHash(hash(refreshToken)).orElseThrow(InvalidSessionException::new);
        Instant now = Instant.now();
        if (session.getRevokedAt() != null || !session.getExpiresAt().isAfter(now)) throw new InvalidSessionException();
        User user = users.findByIdAndOrganizationId(session.getUserId(), session.getOrganizationId()).filter(User::isEnabled).orElseThrow(InvalidSessionException::new);
        String nextRefresh = randomToken();
        session.rotate(hash(nextRefresh), now.plus(properties.getJwt().getRefreshTtl()), now);
        sessions.save(session);
        AuthPrincipal principal = new AuthPrincipal(user.getId(), user.getOrganizationId(), user.getEmail(), session.getId());
        return new LoginResult(principal, jwtService.createAccessToken(principal, session.getId(), now), nextRefresh);
    }
    @Transactional
    public void logout(UUID sessionId) { if (sessionId != null) sessions.findById(sessionId).ifPresent(session -> { session.revoke(Instant.now()); sessions.save(session); }); }
    private String randomToken() { byte[] bytes = new byte[32]; random.nextBytes(bytes); return java.util.HexFormat.of().formatHex(bytes); }
    private byte[] hash(String value) { try { return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)); } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); } }
    public record LoginResult(AuthPrincipal principal, String accessToken, String refreshToken) {}
}