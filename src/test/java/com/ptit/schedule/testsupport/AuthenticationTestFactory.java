package com.ptit.schedule.testsupport;

import com.ptit.schedule.entity.Role;
import com.ptit.schedule.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.atomic.AtomicLong;

public final class AuthenticationTestFactory {

    private static final AtomicLong USER_SEQUENCE = new AtomicLong(10_000L);

    private AuthenticationTestFactory() {
    }

    public static User createUser(String suffix) {
        long userId = USER_SEQUENCE.incrementAndGet();
        String normalizedSuffix = suffix == null || suffix.isBlank() ? "test" + userId : suffix;

        return User.builder()
                .id(userId)
                .username("test_user_" + normalizedSuffix)
                .email("test_" + normalizedSuffix + "@example.com")
                .password("encoded-password")
                .fullName("Test User " + normalizedSuffix)
                .role(Role.ADMIN)
                .enabled(true)
                .build();
    }

    public static Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    public static void setAuthentication(User user) {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(user));
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
