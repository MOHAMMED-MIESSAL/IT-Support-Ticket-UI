package com.projets.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;

public class JwtUtil {

    private static final String SECRET = "YWJjMTIzIT8kKiYoKS1fQGFiY2RlZmdoaWprbG1ub3A=";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));


    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        JwtUtil.token = token;
    }

    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public static String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }
}
