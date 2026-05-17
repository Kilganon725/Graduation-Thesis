package com.example.aifomo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expireMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-minutes}") long expireMinutes) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes.length >= 32 ? bytes : padTo32(bytes));
        this.expireMillis = expireMinutes * 60 * 1000L;
    }

    private static byte[] padTo32(byte[] source) {
        byte[] target = new byte[32];
        for (int i = 0; i < target.length; i++) {
            target[i] = i < source.length ? source[i] : '0';
        }
        return target;
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public Long getUserId(String token) {
        Object uid = parseToken(token).get("uid");
        return uid == null ? null : Long.valueOf(uid.toString());
    }

    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}
