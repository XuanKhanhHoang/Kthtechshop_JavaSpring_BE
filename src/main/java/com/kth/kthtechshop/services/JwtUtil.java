package com.kth.kthtechshop.services;

import com.kth.kthtechshop.dto.auth.login.JWTToken;
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
    @Value("${jwt.refresh.secret}")
    private String refreshTokenKey;

    private SecretKey SECRET_KEY;
    private SecretKey REFRESH_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        REFRESH_KEY = Keys.hmacShaKeyFor(refreshTokenKey.getBytes());
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

    public JWTToken generateToken(long userId, int livingTime, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, userId, livingTime, true);
    }

    public JWTToken generateToken(long userId, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, userId);
    }

    private JWTToken createToken(Map<String, Object> claims, long userId, int livingTime, boolean isAccessToken) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (long) livingTime * 60 * 1000))
                .signWith(isAccessToken ? SECRET_KEY : REFRESH_KEY)
                .compact();
        return new JWTToken(token, System.currentTimeMillis(), System.currentTimeMillis() + (long) livingTime * 60 * 60 * 1000);
    }

    private JWTToken createToken(Map<String, Object> claims, long userId) {
        int defaultLivingTime = 2 * 60; //2h
        return createToken(claims, userId, defaultLivingTime, true);
    }

    public long validateToken(String token) {
        if (isTokenExpired(token)) throw new RuntimeException("Token is expired");
        return extractUserId(token);
    }

    public Set<Role> getRoles(String token) {
        return extractRoles(token);
    }

    //REFRESH
    public JWTToken generateRefreshToken(long userId, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        int livingTime = 3 * 24 * 60; //3days
        claims.put("roles", roles);
        return createToken(claims, userId, livingTime, false);
    }

    public Integer extractUserIdRf(String token) {
        final Claims claims = extractAllClaimsFr(token);
        return Integer.parseInt(claims.getSubject());
    }

    public Date extractExpirationRf(String token) {
        final Claims claims = extractAllClaimsFr(token);
        return claims.getExpiration();
    }

    public Set<Role> extractRolesRf(String token) {
        return (Set<Role>) extractAllClaimsFr(token).get("roles");
    }

    private Claims extractAllClaimsFr(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(REFRESH_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshTokenValid(String token) {
        return true;
    }
}
