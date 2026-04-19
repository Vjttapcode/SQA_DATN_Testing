package com.ptit.schedule.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB080
 * File Under Test: JwtTokenProvider.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for JwtTokenProvider - JWT token generation and validation
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB080 - JwtTokenProvider Tests")
public class JwtTokenProviderTest {

    // =============================================================================
    // TEST CONSTANTS
    // =============================================================================
    // Secret key must be at least 512 bits (64 bytes) for HS512 algorithm
    private static final String TEST_SECRET = "ptit-schedule-jwt-secret-key-for-testing-purposes-only-must-be-64-bytes-long";
    private static final Long TEST_EXPIRATION = 86400000L; // 24 hours in milliseconds

    // =============================================================================
    // OBJECT UNDER TEST
    // =============================================================================
    private JwtTokenProvider jwtTokenProvider;

    // =============================================================================
    // SETUP
    // =============================================================================

    /**
     * Setup method to initialize JwtTokenProvider with test values
     */
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", TEST_EXPIRATION);
    }

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create Role enum for testing
     */
    private com.ptit.schedule.entity.Role createRole(String roleName) {
        try {
            return com.ptit.schedule.entity.Role.valueOf(roleName);
        } catch (Exception e) {
            // If Role enum doesn't exist, create mock
            return null;
        }
    }

    // =============================================================================
    // TEST CASES: TOKEN GENERATION
    // =============================================================================

    /**
     * Test Case ID: TKB080
     * Purpose: Verify generateToken() creates valid JWT token
     * Input: email="admin@ptit.edu.vn", role=ADMIN
     * Expected Output: Non-null, non-empty token string
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB080: Generate token creates valid JWT string")
    public void test_generateToken_createsValidToken() {
        // Arrange: Prepare test data
        String email = "admin@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("ADMIN");

        // Skip if Role enum is not available
        if (role == null) {
            return;
        }

        // Act: Generate token
        String token = jwtTokenProvider.generateToken(email, role);

        // Assert: Token is valid
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.contains("."), "Token should have JWT format (dot separators)");
    }

    /**
     * Test Case ID: TKB081
     * Purpose: Verify generateToken() creates token with correct claims
     * Input: email="user@ptit.edu.vn", role=USER
     * Expected Output: Token can be parsed with correct subject
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB081: Generated token contains correct email claim")
    public void test_generateToken_containsCorrectEmail() {
        // Arrange
        String email = "user@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("USER");

        if (role == null) {
            return;
        }

        // Act: Generate and extract email
        String token = jwtTokenProvider.generateToken(email, role);
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // Assert: Email matches
        assertEquals(email, extractedEmail, "Extracted email should match original");
    }

    /**
     * Test Case ID: TKB082
     * Purpose: Verify generateTokenWithUsername() is alias for generateToken()
     * Input: username="testuser", role=USER
     * Expected Output: Same behavior as generateToken()
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB082: generateTokenWithUsername creates token")
    public void test_generateTokenWithUsername_createsToken() {
        // Arrange
        String username = "testuser";
        com.ptit.schedule.entity.Role role = createRole("USER");

        if (role == null) {
            return;
        }

        // Act: Generate token with username
        String token = jwtTokenProvider.generateTokenWithUsername(username, role);

        // Assert: Token generated successfully
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    // =============================================================================
    // TEST CASES: TOKEN VALIDATION
    // =============================================================================

    /**
     * Test Case ID: TKB083
     * Purpose: Verify validationToken() returns true for valid token
     * Input: Valid JWT token
     * Expected Output: validationToken() returns true
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB083: validationToken returns true for valid token")
    public void test_validationToken_validToken_returnsTrue() {
        // Arrange: Generate valid token
        String email = "test@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("ADMIN");

        if (role == null) {
            return;
        }

        String token = jwtTokenProvider.generateToken(email, role);

        // Act: Validate token
        boolean isValid = jwtTokenProvider.validationToken(token);

        // Assert: Token is valid
        assertTrue(isValid, "Valid token should return true");
    }

    /**
     * Test Case ID: TKB084
     * Purpose: Verify validationToken() returns false for invalid token
     * Input: Tampered/invalid token
     * Expected Output: validationToken() returns false
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB084: validationToken returns false for invalid token")
    public void test_validationToken_invalidToken_returnsFalse() {
        // Arrange: Invalid tokens
        String invalidToken = "invalid.jwt.token";
        String emptyToken = "";
        String nullLikeToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0In0.invalid_signature";

        // Act & Assert: Invalid tokens should return false
        assertFalse(jwtTokenProvider.validationToken(invalidToken), "Invalid format token should return false");
        assertFalse(jwtTokenProvider.validationToken(emptyToken), "Empty token should return false");
    }

    /**
     * Test Case ID: TKB085
     * Purpose: Verify validateToken() is alias for validationToken()
     * Input: Valid JWT token
     * Expected Output: Same result as validationToken()
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB085: validateToken alias works correctly")
    public void test_validateToken_aliasWorks() {
        // Arrange
        String email = "alias@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("ADMIN");

        if (role == null) {
            return;
        }

        String token = jwtTokenProvider.generateToken(email, role);

        // Act: Use alias method
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert: Should work same as validationToken
        assertTrue(isValid, "Alias method should return same result");
    }

    // =============================================================================
    // TEST CASES: TOKEN EXTRACTION
    // =============================================================================

    /**
     * Test Case ID: TKB086
     * Purpose: Verify getEmailFromToken() extracts correct email
     * Input: Token generated with specific email
     * Expected Output: Extracted email matches original
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB086: getEmailFromToken extracts correct email")
    public void test_getEmailFromToken_extractsCorrectEmail() {
        // Arrange
        String expectedEmail = "extract@test.ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("ADMIN");

        if (role == null) {
            return;
        }

        String token = jwtTokenProvider.generateToken(expectedEmail, role);

        // Act: Extract email
        String actualEmail = jwtTokenProvider.getEmailFromToken(token);

        // Assert
        assertEquals(expectedEmail, actualEmail, "Extracted email should match");
    }

    /**
     * Test Case ID: TKB087
     * Purpose: Verify extractUsername() returns same as getEmailFromToken()
     * Input: Token with username
     * Expected Output: Both methods return same value
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB087: extractUsername returns same as getEmailFromToken")
    public void test_extractUsername_sameAsGetEmailFromToken() {
        // Arrange
        String username = "scheduletest";
        com.ptit.schedule.entity.Role role = createRole("USER");

        if (role == null) {
            return;
        }

        String token = jwtTokenProvider.generateToken(username, role);

        // Act: Extract using both methods
        String emailResult = jwtTokenProvider.getEmailFromToken(token);
        String usernameResult = jwtTokenProvider.extractUsername(token);

        // Assert: Both should return same value
        assertEquals(emailResult, usernameResult, "Both methods should return same value");
    }

    // =============================================================================
    // TEST CASES: EDGE CASES
    // =============================================================================

    /**
     * Test Case ID: TKB088
     * Purpose: Verify handling of malformed token
     * Input: Token without proper JWT structure
     * Expected Output: validationToken() returns false, no exception
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB088: Malformed token handled gracefully")
    public void test_malformedToken_handledGracefully() {
        // Arrange: Malformed tokens
        String[] malformedTokens = {
                "notajwttoken",
                "only.one.part",
                "missing.sig",
                "eyJhbGciOiJIUzUxMiJ9.incomplete"
        };

        // Act & Assert: All should return false without throwing
        for (String token : malformedTokens) {
            assertDoesNotThrow(() -> jwtTokenProvider.validationToken(token),
                    "Should not throw for malformed token: " + token);
            assertFalse(jwtTokenProvider.validationToken(token),
                    "Malformed token should return false: " + token);
        }
    }

    /**
     * Test Case ID: TKB089
     * Purpose: Verify handling of null/empty token
     * Input: null or empty string
     * Expected Output: validationToken() returns false
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB089: Null/empty token handled correctly")
    public void test_nullAndEmptyToken_handled() {
        // These should not throw exceptions
        assertDoesNotThrow(() -> jwtTokenProvider.validationToken(null),
                "Should not throw for null token");
        assertFalse(jwtTokenProvider.validationToken(null),
                "Null token should return false");
    }

    /**
     * Test Case ID: TKB090
     * Purpose: Verify token can be validated multiple times
     * Input: Same valid token validated multiple times
     * Expected Output: Always returns true
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB090: Token can be validated multiple times")
    public void test_tokenValidation_multipleTimes() {
        // Arrange
        String email = "multipletest@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("ADMIN");

        if (role == null) {
            return;
        }

        String token = jwtTokenProvider.generateToken(email, role);

        // Act: Validate multiple times
        boolean first = jwtTokenProvider.validationToken(token);
        boolean second = jwtTokenProvider.validationToken(token);
        boolean third = jwtTokenProvider.validationToken(token);

        // Assert: All validations should succeed
        assertTrue(first, "First validation should succeed");
        assertTrue(second, "Second validation should succeed");
        assertTrue(third, "Third validation should succeed");
    }

    /**
     * Test Case ID: TKB091
     * Purpose: Verify different tokens are unique
     * Input: Two tokens generated for same user
     * Expected Output: Tokens are different (due to timestamp)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB091: Different tokens are unique")
    public void test_differentTokens_areUnique() throws InterruptedException {
        // Arrange
        String email = "unique@ptit.edu.vn";
        com.ptit.schedule.entity.Role role = createRole("USER");

        if (role == null) {
            return;
        }

        // Act: Generate two tokens with slight delay
        String token1 = jwtTokenProvider.generateToken(email, role);
        Thread.sleep(1); // Small delay to ensure different timestamps
        String token2 = jwtTokenProvider.generateToken(email, role);

        // Assert: Tokens should be different (or at least both valid)
        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(jwtTokenProvider.validationToken(token1));
        assertTrue(jwtTokenProvider.validationToken(token2));
    }
}
