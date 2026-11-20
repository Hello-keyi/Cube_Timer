package com.example.backend.util;

import com.example.backend.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey getKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String createJWT(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)                                    // payload
                .issuedAt(new Date())                              // 签发时间
                .expiration(new Date(System.currentTimeMillis()
                        + jwtProperties.getTtl()))                 // 过期时间
                .signWith(getKey())                                // 用密钥签名
                .compact();
    }
    public Claims parseJWT(String token) {
        return Jwts.parser()
                .verifyWith(getKey())                              // 用同一个密钥验签
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
