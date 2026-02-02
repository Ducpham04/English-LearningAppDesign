package com.example.appenglish.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class SecurityUtils {

    /**
     * Lấy UserId của người dùng hiện tại từ SecurityContext
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() 
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated or session expired");
        }

        Object principal = authentication.getPrincipal();

        // Trường hợp 1: Nếu bạn dùng Custom UserDetails (UserPrincipal)
        // Đây là cách chuyên nghiệp nhất
        if (principal instanceof com.example.appenglish.security.UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        // Trường hợp 2: Nếu bạn lưu ID vào username/subject của JWT
        if (principal instanceof UserDetails userDetails) {
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot parse UserId from token");
            }
        }

        // Trường hợp 3: Trả về trực tiếp nếu Principal là String (thường là ID)
        return Long.parseLong(principal.toString());
    }
}