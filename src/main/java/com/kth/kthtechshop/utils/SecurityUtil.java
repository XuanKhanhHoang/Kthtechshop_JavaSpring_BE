package com.kth.kthtechshop.utils;

import com.kth.kthtechshop.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtil {

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return (Long) principal;
            }
        }
        return null;
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
            if (roles != null) {
                for (GrantedAuthority role : roles) {
                    if (role.getAuthority().equals(Role.Admin.toString())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
}

