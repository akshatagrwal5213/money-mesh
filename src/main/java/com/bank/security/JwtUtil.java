package com.bank.security;

import com.bank.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Autowired
    private JwtProperties jwtProperties;

    private Key key() {
        return Keys.hmacShaKeyFor(this.jwtProperties.getSecret().getBytes());
    }

    // Generate access token with role
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("type", "access");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(this.key())
                .compact();
    }

    // Generate access token without role (backward compatibility)
    public String generateToken(String username) {
        return generateToken(username, "ROLE_CUSTOMER");
    }

    // Generate refresh token (longer expiration)
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtProperties.getExpirationMs() * 24))) // 24 hours
                .signWith(this.key())
                .compact();
    }

    // Parse and validate token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("role");
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Check if token is a refresh token
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
}
