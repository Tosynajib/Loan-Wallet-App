package com.tosin.koins.common.security;

import com.tosin.koins.common.exception.UnauthorizedException;
import com.tosin.koins.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provides the currently authenticated user.
 *
 * Why this exists:
 * - Avoids repeating SecurityContextHolder logic everywhere.
 * - Keeps service classes clean.
 */
@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new UnauthorizedException("Invalid authenticated user");
        }

        return user;
    }
}