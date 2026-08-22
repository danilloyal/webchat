package com.danilloyal.webchat.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.danilloyal.webchat.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access.expiration-time}")
    private Long acessExpiration;

    @Value("${jwt.refresh.expiration-time}")
    private Long refreshExpiration;

    public String generateAcessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + acessExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + refreshExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean isTokenValid(String token, User user){
        String username = extracUsernameFromToken(token);

        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    public String extractTypeFromToken(String token){
        return getAllClaimsFromToken(token).get("type", String.class);
    }

    public boolean isAccessToken(String token){
        return "access".equals(extractTypeFromToken(token));
    }

    public boolean isRefreshToken(String token){
        return "refresh".equals(extractTypeFromToken(token));
    }

    public String extracUsernameFromToken(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token)
                .getExpiration()
                .before(new Date());
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
