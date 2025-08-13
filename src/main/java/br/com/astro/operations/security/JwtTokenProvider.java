package br.com.astro.operations.security;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.astro.operations.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long validityInMilliseconds;
    private final long refreshValidityInMilliseconds;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtTokenProvider(
        @Value("${app.jwt.secret:mySecretKey}") String secret,
        @Value("${app.jwt.expiration:86400000}") long validityInMilliseconds,
        @Value("${app.jwt.refresh-expiration:604800000}") long refreshValidityInMilliseconds,
        @Autowired TokenBlacklistService tokenBlacklistService
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
        this.refreshValidityInMilliseconds = refreshValidityInMilliseconds;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String generateToken(String username) {
        return generateToken(username, validityInMilliseconds, "ACCESS");
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshValidityInMilliseconds, "REFRESH");
    }

    private String generateToken(String username, long validity, String type) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiration)
            .id(jti)
            .claim("type", type)
            .signWith(secretKey)
            .compact();

        log.debug("Generated {} token for user: {} with JTI: {} expires at: {}", 
                 type, username, jti, expiration);
        return token;
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }

    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public String getTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            String jti = claims.getId();
            
            // Verificar se o token está na blacklist
            if (tokenBlacklistService.isBlacklisted(jti)) {
                log.warn("Token {} is blacklisted", jti);
                return false;
            }
            
            log.debug("Token {} validated successfully", jti);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public void blacklistToken(String token) {
        try {
            Claims claims = getClaims(token);
            String jti = claims.getId();
            Date expiration = claims.getExpiration();
            
            tokenBlacklistService.blacklistToken(jti, expiration);
            log.info("Token {} blacklisted until {}", jti, expiration);
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }
}
