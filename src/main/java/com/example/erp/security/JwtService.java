package com.example.erp.security;

import com.example.erp.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecurityProperties properties;
    private final SecretKey key;
    public JwtService(SecurityProperties properties) {
        this.properties = properties;
        if (properties.getJwt().getSecret() == null || properties.getJwt().getSecret().isBlank())
            throw new IllegalStateException("JWT secret is required");
        byte[] secret = properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) throw new IllegalStateException("JWT secret must contain at least 32 bytes");
        key = Keys.hmacShaKeyFor(secret);
    }
    public String createAccessToken(AuthPrincipal principal, UUID sessionId, Instant now) {
        Instant expiry = now.plus(properties.getJwt().getAccessTtl());
        return Jwts.builder().subject(principal.userId().toString()).claim("org", principal.organizationId().toString())
                .claim("role", "ADMIN").claim("sid", sessionId.toString()).issuedAt(Date.from(now)).expiration(Date.from(expiry))
                .signWith(key).compact();
    }
    public Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}