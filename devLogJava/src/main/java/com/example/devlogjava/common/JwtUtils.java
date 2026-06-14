package com.example.devlogjava.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key key;

    @PostConstruct
    public void init() {
        // 使用配置的密钥生成签名 Key
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 Token
     * @param userId 用户ID
     * @param phone  手机号
     * @return token 字符串
     */
    public String createToken(String userId, String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     * @param token token 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 校验 Token 是否过期
     * @param token token 字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
