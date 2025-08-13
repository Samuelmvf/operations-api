package br.com.astro.operations.service;

import java.util.Date;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_CACHE = "tokenBlacklist";

    @CachePut(value = BLACKLIST_CACHE, key = "#jti")
    public String blacklistToken(String jti, Date expiration) {
        log.info("Token blacklisted: {} expires at: {}", jti, expiration);
        return "BLACKLISTED";
    }

    @Cacheable(value = BLACKLIST_CACHE, key = "#jti", unless = "#result == null")
    public String isTokenBlacklisted(String jti) {
        return null;
    }

    public boolean isBlacklisted(String jti) {
        String result = isTokenBlacklisted(jti);
        boolean blacklisted = result != null;
        
        if (blacklisted) {
            log.debug("Token {} is blacklisted", jti);
        }
        
        return blacklisted;
    }

    public void logoutToken(String jti, Date expiration) {
        blacklistToken(jti, expiration);
        log.info("User logged out, token {} blacklisted until {}", jti, expiration);
    }
}
