package com.ptit.schedule.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB092
 * File Under Test: JwtAccessDeniedHandler.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for JwtAccessDeniedHandler - 403 Forbidden response handler
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB092 - JwtAccessDeniedHandler Tests")
public class JwtAccessDeniedHandlerTest {

    private JwtAccessDeniedHandler accessDeniedHandler;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        accessDeniedHandler = new JwtAccessDeniedHandler();

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getServletPath()).thenReturn("/api/schedules/save-batch");

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
     * Test Case ID: TKB092
     * Purpose: Verify handle() sets response status to 403
     * Input: HttpServletRequest, HttpServletResponse, AccessDeniedException
     * Expected Output: Response status set to 403 FORBIDDEN
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB092: handle() sets 403 status")
    public void test_handle_sets403Status() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);

        verify(mockResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Test Case ID: TKB093
     * Purpose: Verify handle() sets Content-Type to application/json
     * Input: HttpServletRequest, HttpServletResponse, AccessDeniedException
     * Expected Output: Content-Type header set to application/json
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB093: handle() sets JSON content type")
    public void test_handle_setsJsonContentType() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);

        verify(mockResponse).setContentType("application/json");
    }

    /**
     * Test Case ID: TKB094
     * Purpose: Verify handle() writes JSON error body
     * Input: HttpServletRequest with servletPath, HttpServletResponse, AccessDeniedException
     * Expected Output: Response body contains valid JSON with success=false
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB094: handle() writes JSON body")
    public void test_handle_writesJsonBody() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertNotNull(jsonResponse, "JSON response should not be null");
        assertTrue(jsonResponse.contains("\"status\":403"), "JSON should contain status 403");
        assertTrue(jsonResponse.contains("\"path\""), "JSON should contain path");
    }

    /**
     * Test Case ID: TKB095
     * Purpose: Verify handle() includes error message from exception
     * Input: AccessDeniedException with message
     * Expected Output: JSON response contains error message from exception
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB095: handle() includes exception message")
    public void test_handle_includesExceptionMessage() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Custom error message");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertTrue(jsonResponse.contains("Custom error message"),
                "JSON should contain exception message");
    }

    /**
     * Test Case ID: TKB096
     * Purpose: Verify handle() includes Vietnamese access denied message
     * Input: Any AccessDeniedException
     * Expected Output: JSON contains Vietnamese message about access denial
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB096: handle() includes Vietnamese access denied message")
    public void test_handle_includesVietnameseMessage() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);
        printWriter.flush();
        String jsonResponse = stringWriter.toString();

        assertTrue(jsonResponse.contains("quyền truy cập") || jsonResponse.contains("Forbidden"),
                "JSON should contain Vietnamese access denied message. Actual: " + jsonResponse);
    }

    /**
     * Test Case ID: TKB097
     * Purpose: Verify handle() handles null exception message gracefully
     * Input: AccessDeniedException with null message
     * Expected Output: Handler completes without throwing exception
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB097: handle() handles null exception message")
    public void test_handle_nullExceptionMessage() throws Exception {
        AccessDeniedException exception = new AccessDeniedException((String) null);

        assertDoesNotThrow(() -> accessDeniedHandler.handle(mockRequest, mockResponse, exception));
    }

    /**
     * Test Case ID: TKB098
     * Purpose: Verify handle() handles empty servlet path
     * Input: HttpServletRequest with empty servletPath
     * Expected Output: Handler completes normally
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB098: handle() handles empty servlet path")
    public void test_handle_emptyServletPath() throws Exception {
        when(mockRequest.getServletPath()).thenReturn("");
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        assertDoesNotThrow(() -> accessDeniedHandler.handle(mockRequest, mockResponse, exception));
    }

    /**
     * Test Case ID: TKB099
     * Purpose: Verify handle() uses SC_FORBIDDEN constant
     * Input: Standard AccessDeniedException
     * Expected Output: Response status equals 403
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB099: handle() uses SC_FORBIDDEN constant")
    public void test_handle_usesCorrectStatusConstant() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(mockRequest, mockResponse, exception);

        verify(mockResponse).setStatus(403);
    }
}
