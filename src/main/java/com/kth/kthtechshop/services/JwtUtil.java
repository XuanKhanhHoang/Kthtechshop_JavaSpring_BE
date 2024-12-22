package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.auth.login.AccessToken;
import com.kth.kthtechshop.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public Integer extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return Integer.parseInt(claims.getSubject());
    }

    public Date extractExpiration(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private Set<Role> extractRoles(String token) {
        return (Set<Role>) extractAllClaims(token).get("roles");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public AccessToken generateToken(int userId, int livingTime) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId, livingTime);
    }

    public AccessToken generateToken(long userId, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, userId);
    }

    private AccessToken createToken(Map<String, Object> claims, long userId, int livingTime) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (long) livingTime * 60 * 60 * 1000))
                .signWith(SECRET_KEY)
                .compact();
        return new AccessToken(token, System.currentTimeMillis(), System.currentTimeMillis() + (long) livingTime * 60 * 60 * 1000);
    }

    private AccessToken createToken(Map<String, Object> claims, long userId) {
        int defaultLivingTime = 2 * 60 * 60; // 2 giờ tính bằng giây
        return createToken(claims, userId, defaultLivingTime);
    }

    public long validateToken(String token) {
        if (isTokenExpired(token)) throw new RuntimeException("Token is expired");
        return extractUserId(token);
    }

    public Set<Role> getRoles(String token) {
        return extractRoles(token);
    }
}
