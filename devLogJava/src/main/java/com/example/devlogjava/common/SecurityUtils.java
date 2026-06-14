package com.example.devlogjava.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 从当前登录态获取用户信息
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户的 ID
     */
    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return principal instanceof String ? (String) principal : null;
    }
}
