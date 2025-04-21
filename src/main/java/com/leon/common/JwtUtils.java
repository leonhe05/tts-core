package com.leon.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private static String base64Secret;
    private static long expirationTimeMs;
    private static SecretKey secretKey;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Secret);
            secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
            log.info("JWT Secret Key initialized successfully.");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid JWT secret key configured", e);
        }
    }

    public static String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public static String getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            Claims body = claimsJws.getPayload();

            return body.getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", token);
            return null;
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("Error parsing JWT token: {}, Error: {}", token, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT token: {}", token, e);
            return null;
        }
    }

    @Value("${jwt.expiration-ms:604800000}")
    public void setExpirationTimeMs(long expirationTimeMs) {
        JwtUtils.expirationTimeMs = expirationTimeMs;
    }

    @Value("${jwt.secret}")
    public void setBase64Secret(String base64Secret) {
        JwtUtils.base64Secret = base64Secret;
    }
}