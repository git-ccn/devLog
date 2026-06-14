package com.example.devlogjava.config;

import com.example.devlogjava.common.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7).trim();
                if (!token.isEmpty() && !jwtUtils.isTokenExpired(token)) {
                    try {
                        Claims claims = jwtUtils.parseToken(token);
                        String userId = claims.get("userId", String.class);
                        // fallback: 兼容旧 token（subject 是 phone）
                        if (userId == null || userId.isBlank()) {
                            String oldPhone = claims.get("phone", String.class);
                            if (oldPhone == null || oldPhone.isBlank()) {
                                oldPhone = claims.getSubject();
                            }
                            userId = oldPhone;
                        }
                        if (userId != null && !userId.isBlank()) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

