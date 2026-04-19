package com.ptit.schedule.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB109
 * File Under Test: JwtAuthenticationFilter.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for JwtAuthenticationFilter - JWT authentication filter
 * ===============================================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Test Case ID: TKB109 - JwtAuthenticationFilter Tests")
public class JwtAuthenticationFilterTest {

    // =============================================================================
    // TEST CONSTANTS
    // =============================================================================
    private static final String TEST_SECRET = "ptit-schedule-jwt-secret-key-for-testing-purposes-only-must-be-64-bytes-long";
    private static final Long TEST_EXPIRATION = 86400000L;

    // =============================================================================
    // MOCK OBJECTS
    // =============================================================================
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    // =============================================================================
    // OBJECT UNDER TEST
    // =============================================================================
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // =============================================================================
    // SETUP
    // =============================================================================

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create test user details
     */
    private UserDetails createTestUserDetails() {
        return User.builder()
                .username("testuser@ptit.edu.vn")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    /**
     * Helper method to create valid Authorization header
     */
    private String createValidAuthHeader(String token) {
        return "Bearer " + token;
    }

    // =============================================================================
    // TEST CASES: NO AUTH HEADER
    // =============================================================================

    /**
     * Test Case ID: TKB109
     * Purpose: Verify filter passes through when no Authorization header
     * Input: Request without Authorization header
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB109: No auth header - passes through filter chain")
    public void test_noAuthHeader_passesThrough() throws Exception {
        // Arrange: No Authorization header
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert: FilterChain called, no authentication set
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "No authentication should be set");
    }

    /**
     * Test Case ID: TKB110
     * Purpose: Verify filter passes through when Authorization header doesn't start with "Bearer "
     * Input: Authorization header with different prefix
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB110: Non-Bearer auth header - passes through")
    public void test_nonBearerAuthHeader_passesThrough() throws Exception {
        // Arrange: Basic auth header
        when(mockRequest.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // =============================================================================
    // TEST CASES: INVALID TOKEN
    // =============================================================================

    /**
     * Test Case ID: TKB111
     * Purpose: Verify filter passes through when token validation fails
     * Input: Invalid JWT token
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB111: Invalid token - passes through filter chain")
    public void test_invalidToken_passesThrough() throws Exception {
        // Arrange: Invalid token
        String invalidToken = "invalid.jwt.token";
        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(invalidToken));
        when(jwtTokenProvider.validationToken(invalidToken)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Test Case ID: TKB112
     * Purpose: Verify filter handles null from getEmailFromToken
     * Input: Token that returns null email
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB112: Null email from token - passes through")
    public void test_nullEmailFromToken_passesThrough() throws Exception {
        // Arrange: Valid token format but null email
        String token = "valid.format.token";
        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(token));
        when(jwtTokenProvider.validationToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // =============================================================================
    // TEST CASES: VALID TOKEN
    // =============================================================================

    /**
     * Test Case ID: TKB113
     * Purpose: Verify filter authenticates user with valid token
     * Input: Valid JWT token with existing user
     * Expected Output: SecurityContext set with authenticated user
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB113: Valid token - sets authentication")
    public void test_validToken_setsAuthentication() throws Exception {
        // Arrange: Valid token and user
        String validToken = "valid.jwt.token";
        String username = "testuser@ptit.edu.vn";
        UserDetails userDetails = createTestUserDetails();

        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(validToken));
        when(jwtTokenProvider.validationToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert: Authentication is set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be set");
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName(),
                "Username should match");

        // FilterChain should still be called
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    /**
     * Test Case ID: TKB114
     * Purpose: Verify filter doesn't overwrite existing authentication
     * Input: Already authenticated request
     * Expected Output: Existing authentication preserved
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB114: Existing authentication - preserved")
    public void test_existingAuthentication_preserved() throws Exception {
        // Arrange: Existing authentication
        UserDetails existingUser = createTestUserDetails();
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser, null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        String validToken = "valid.jwt.token";
        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(validToken));
        when(jwtTokenProvider.validationToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn("testuser@ptit.edu.vn");

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert: Original authentication preserved
        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
    }

    // =============================================================================
    // TEST CASES: EXCEPTION HANDLING
    // =============================================================================

    /**
     * Test Case ID: TKB115
     * Purpose: Verify filter handles exception during token processing
     * Input: Exception thrown by token provider
     * Expected Output: FilterChain proceeds, error logged, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB115: Exception during processing - filter chain continues")
    public void test_exceptionDuringProcessing_continues() throws Exception {
        // Arrange: Token that causes exception
        String token = "token.that.causes.exception";
        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(token));
        when(jwtTokenProvider.validationToken(token)).thenThrow(new RuntimeException("Token error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert: FilterChain should still be called
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Test Case ID: TKB116
     * Purpose: Verify filter handles UserDetailsService exception
     * Input: UserDetailsService throws exception
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB116: UserDetailsService exception - continues")
    public void test_userDetailsServiceException_continues() throws Exception {
        // Arrange
        String token = "valid.token";
        String username = "unknown@ptit.edu.vn";
        
        when(mockRequest.getHeader("Authorization")).thenReturn(createValidAuthHeader(token));
        when(jwtTokenProvider.validationToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new RuntimeException("User not found"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // =============================================================================
    // TEST CASES: EDGE CASES
    // =============================================================================

    /**
     * Test Case ID: TKB117
     * Purpose: Verify filter handles empty Bearer token
     * Input: "Bearer " with no token
     * Expected Output: FilterChain proceeds, no authentication set
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB117: Empty Bearer token - passes through")
    public void test_emptyBearerToken_passesThrough() throws Exception {
        // Arrange: Bearer with no token
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    /**
     * Test Case ID: TKB118
     * Purpose: Verify filter handles Authorization header with only spaces
     * Input: "Bearer   " (only spaces after Bearer)
     * Expected Output: FilterChain proceeds
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB118: Bearer with only spaces - passes through")
    public void test_bearerOnlySpaces_passesThrough() throws Exception {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer    ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }
}
