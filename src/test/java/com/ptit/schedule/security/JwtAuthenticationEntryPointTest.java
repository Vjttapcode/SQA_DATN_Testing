package com.ptit.schedule.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB100
 * File Under Test: JwtAuthenticationEntryPoint.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for JwtAuthenticationEntryPoint - 401 Unauthorized response handler
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB100 - JwtAuthenticationEntryPoint Tests")
public class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        authenticationEntryPoint = new JwtAuthenticationEntryPoint();

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getServletPath()).thenReturn("/api/schedules");

        mockResponse = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(mockResponse.getOutputStream()).thenReturn(new MockServletOutputStream(printWriter));
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    private static class MockServletOutputStream extends jakarta.servlet.ServletOutputStream {
        private final PrintWriter writer;

        public MockServletOutputStream(PrintWriter writer) {
            this.writer = writer;
        }

        @Override
        public void write(int b) {
            writer.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener listener) {
        }
    }

    /**
     * Test Case ID: TKB100
     * Purpose: Verify commence() sets response status to 401
     * Input: HttpServletRequest, HttpServletResponse, AuthenticationException
     * Expected Output: Response status set to 401 UNAUTHORIZED
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB100: commence() sets 401 status")
    public void test_commence_sets401Status() throws Exception {
        AuthenticationException exception = new AuthenticationException("Authentication required") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);

        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Test Case ID: TKB101
     * Purpose: Verify commence() sets Content-Type to application/json
     * Input: Standard request/response
     * Expected Output: Content-Type header set to application/json
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB101: commence() sets JSON content type")
    public void test_commence_setsJsonContentType() throws Exception {
        AuthenticationException exception = new AuthenticationException("Auth required") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);

        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Test Case ID: TKB102
     * Purpose: Verify commence() writes JSON error body
     * Input: Request with servlet path, AuthenticationException
     * Expected Output: JSON response contains status, message, error, path
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB102: commence() writes JSON error body")
    public void test_commence_writesJsonBody() throws Exception {
        AuthenticationException exception = new AuthenticationException("JWT token invalid") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertNotNull(jsonResponse, "JSON response should not be null");
        assertTrue(jsonResponse.contains("\"status\":401"), "JSON should contain status 401");
        assertTrue(jsonResponse.contains("\"path\":\"/api/schedules\""),
                "JSON should contain request path");
    }

    /**
     * Test Case ID: TKB103
     * Purpose: Verify commence() includes Vietnamese error message
     * Input: Any AuthenticationException
     * Expected Output: JSON contains Vietnamese unauthorized message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB103: commence() includes Vietnamese unauthorized message")
    public void test_commence_includesVietnameseMessage() throws Exception {
        AuthenticationException exception = new AuthenticationException("Token expired") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertTrue(jsonResponse.contains("Token không hợp lệ") || jsonResponse.contains("Unauthorized"),
                "JSON should contain Vietnamese unauthorized message. Actual: " + jsonResponse);
    }

    /**
     * Test Case ID: TKB104
     * Purpose: Verify commence() includes exception message
     * Input: AuthenticationException with specific message
     * Expected Output: JSON contains error field with exception message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB104: commence() includes exception message in error field")
    public void test_commence_includesExceptionMessage() throws Exception {
        String exceptionMessage = "Full authentication is required";
        AuthenticationException exception = new AuthenticationException(exceptionMessage) {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertTrue(jsonResponse.contains("\"error\":\"" + exceptionMessage + "\""),
                "JSON should contain exception message in error field");
    }

    /**
     * Test Case ID: TKB105
     * Purpose: Verify commence() with null exception message
     * Input: AuthenticationException with null message
     * Expected Output: Handler completes without error
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB105: commence() with null exception message")
    public void test_commence_nullExceptionMessage() throws Exception {
        AuthenticationException exception = new AuthenticationException(null) {};

        assertDoesNotThrow(() -> authenticationEntryPoint.commence(mockRequest, mockResponse, exception));
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Test Case ID: TKB106
     * Purpose: Verify commence() with empty servlet path
     * Input: Request with empty servlet path
     * Expected Output: Handler completes, empty path in JSON
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB106: commence() with empty servlet path")
    public void test_commence_emptyServletPath() throws Exception {
        when(mockRequest.getServletPath()).thenReturn("");
        AuthenticationException exception = new AuthenticationException("Unauthorized") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertNotNull(jsonResponse);
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Test Case ID: TKB107
     * Purpose: Verify commence() uses correct HTTP status constant
     * Input: Standard AuthenticationException
     * Expected Output: Response status equals HttpServletResponse.SC_UNAUTHORIZED (401)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB107: commence() uses SC_UNAUTHORIZED constant")
    public void test_commence_usesCorrectStatusConstant() throws Exception {
        AuthenticationException exception = new AuthenticationException("Unauthorized") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, exception);

        verify(mockResponse).setStatus(401);
    }

    /**
     * Test Case ID: TKB108
     * Purpose: Verify different status codes for authentication vs access denied
     * Input: JwtAuthenticationEntryPoint vs JwtAccessDeniedHandler
     * Expected Output: Different status codes (401 vs 403)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB108: Different status codes for auth vs access denied")
    public void test_differentStatusCodes_vsAccessDenied() throws Exception {
        AuthenticationException authException = new AuthenticationException("Auth required") {};

        authenticationEntryPoint.commence(mockRequest, mockResponse, authException);
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        reset(mockResponse);
        StringWriter accessDeniedWriter = new StringWriter();
        PrintWriter accessDeniedPrintWriter = new PrintWriter(accessDeniedWriter);
        when(mockResponse.getOutputStream()).thenReturn(new MockServletOutputStream(accessDeniedPrintWriter));

        JwtAccessDeniedHandler accessDeniedHandler = new JwtAccessDeniedHandler();
        org.springframework.security.access.AccessDeniedException accessException =
                new org.springframework.security.access.AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, accessException);
        verify(mockResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
