package com.kafka.producer.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${app.jwt.secret}") String secret) {

        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(secret)
        );
    }

    public Claims validateAndExtractClaims(String token) {

        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public List<String> extractRoles(Claims claims) {
        return claims.get("roles", List.class);
    }
}
