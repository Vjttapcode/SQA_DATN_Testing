package com.ptit.schedule.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB054
 * File Under Test: GlobalExceptionHandler.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for GlobalExceptionHandler
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB054 - GlobalExceptionHandler Tests")
public class GlobalExceptionHandlerTest {

    // =============================================================================
    // OBJECT UNDER TEST
    // =============================================================================
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create mock MethodArgumentNotValidException
     */
    private MethodArgumentNotValidException createMockValidationException() {
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
        
        Map<String, String> errors = new HashMap<>();
        errors.put("subjectId", "Subject ID is required");
        errors.put("classNumber", "Class number must be positive");
        
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(new java.util.ArrayList<>());
        
        org.springframework.validation.BindingResult bindingResultWithErrors = mock(org.springframework.validation.BindingResult.class);
        java.util.List<org.springframework.validation.ObjectError> errorList = new java.util.ArrayList<>();
        errorList.add(new FieldError("object", "subjectId", "Subject ID is required"));
        errorList.add(new FieldError("object", "classNumber", "Class number must be positive"));
        when(bindingResultWithErrors.getAllErrors()).thenReturn(errorList);
        
        when(mockException.getBindingResult()).thenReturn(bindingResultWithErrors);
        
        return mockException;
    }

    // =============================================================================
    // TEST CASES: MethodArgumentNotValidException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB054
     * Purpose: Verify handleValidationExceptions returns 400 with field errors
     * Input: MethodArgumentNotValidException with field errors
     * Expected Output: Response with status 400, field-specific error messages
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB054: Validation errors returns 400 with field errors")
    public void test_handleValidationExceptions_returns400WithFieldErrors() {
        // Arrange: Create validation exception with errors
        MethodArgumentNotValidException exception = createMockValidationException();

        // Act: Handle validation exception
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(exception);

        // Assert: Verify 400 response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"), "Status should be 400");
        assertEquals("Dữ liệu không hợp lệ", body.get("error"), "Error message should match");
        assertNotNull(body.get("timestamp"), "Timestamp should be present");
        assertNotNull(body.get("errors"), "Field errors should be present");
    }

    /**
     * Test Case ID: TKB055
     * Purpose: Verify handleValidationExceptions with no field errors
     * Input: MethodArgumentNotValidException with empty errors
     * Expected Output: Response with empty errors map
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB055: Validation with empty errors")
    public void test_handleValidationExceptions_emptyErrors() {
        // Arrange: Create mock with no errors
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(new java.util.ArrayList<>());
        when(mockException.getBindingResult()).thenReturn(bindingResult);

        // Act: Handle validation exception
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(mockException);

        // Assert: Verify response with empty errors
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        Map<String, Object> body = response.getBody();
        assertNotNull(body.get("errors"), "Errors map should be present");
    }

    // =============================================================================
    // TEST CASES: HttpMessageNotReadableException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB056
     * Purpose: Verify handleHttpMessageNotReadableException returns 400
     * Input: HttpMessageNotReadableException
     * Expected Output: Response with status 400, error message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB056: Invalid JSON returns 400")
    public void test_handleHttpMessageNotReadable_returns400() {
        // Arrange: Create mock exception
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleHttpMessageNotReadableException(exception);

        // Assert: Verify 400 response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"), "Status should be 400");
        assertEquals("Dữ liệu không hợp lệ", body.get("error"), "Error message should match");
    }

    // =============================================================================
    // TEST CASES: ResourceNotFoundException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB057
     * Purpose: Verify handleResourceNotFoundException returns 404
     * Input: ResourceNotFoundException with message
     * Expected Output: Response with status 404, not found message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB057: Resource not found returns 404")
    public void test_handleResourceNotFoundException_returns404() {
        // Arrange: Create exception
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "Không tìm thấy lịch học với ID = '999'"
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);

        // Assert: Verify 404 response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"), "Status should be 404");
        assertEquals("Không tìm thấy dữ liệu", body.get("error"), "Error should match");
        assertEquals("Không tìm thấy lịch học với ID = '999'", body.get("message"), 
                "Message should contain exception message");
    }

    /**
     * Test Case ID: TKB058
     * Purpose: Verify handleResourceNotFoundException with resource details
     * Input: ResourceNotFoundException with resource name, field, value
     * Expected Output: Response with formatted message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB058: Resource not found with details")
    public void test_handleResourceNotFoundException_withDetails() {
        // Arrange: Create exception with resource details
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "Subject", "maMon", "INT9999"
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFoundException(exception);

        // Assert: Verify formatted message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should return 404");
        assertEquals("Không tìm thấy Subject với maMon = 'INT9999'", 
                response.getBody().get("message"), "Formatted message should match");
    }

    // =============================================================================
    // TEST CASES: DuplicateResourceException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB059
     * Purpose: Verify handleDuplicateResourceException returns 409
     * Input: DuplicateResourceException
     * Expected Output: Response with status 409, conflict message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB059: Duplicate resource returns 409")
    public void test_handleDuplicateResourceException_returns409() {
        // Arrange: Create exception
        DuplicateResourceException exception = new DuplicateResourceException(
                "Đã tồn tại môn học với mã = 'INT1306'"
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResourceException(exception);

        // Assert: Verify 409 response
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Should return 409");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.CONFLICT.value(), body.get("status"), "Status should be 409");
        assertEquals("Dữ liệu trùng lặp", body.get("error"), "Error should match");
    }

    /**
     * Test Case ID: TKB060
     * Purpose: Verify handleDuplicateResourceException with factory constructor
     * Input: DuplicateResourceException created with resource details
     * Expected Output: Response with formatted duplicate message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB060: Duplicate with factory constructor")
    public void test_handleDuplicateResourceException_factoryConstructor() {
        // Arrange: Create exception with factory constructor
        DuplicateResourceException exception = new DuplicateResourceException(
                "Schedule", "id", 123
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResourceException(exception);

        // Assert: Verify formatted message
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Should return 409");
        assertTrue(response.getBody().get("message").toString().contains("Schedule"), 
                "Message should contain resource name");
    }

    // =============================================================================
    // TEST CASES: InvalidDataException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB061
     * Purpose: Verify handleInvalidDataException returns 400
     * Input: InvalidDataException
     * Expected Output: Response with status 400, invalid data message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB061: Invalid data returns 400")
    public void test_handleInvalidDataException_returns400() {
        // Arrange: Create exception
        InvalidDataException exception = new InvalidDataException(
                "Dữ liệu lịch học không hợp lệ: ngày bắt đầu sau ngày kết thúc"
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidDataException(exception);

        // Assert: Verify 400 response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"), "Status should be 400");
        assertEquals("Dữ liệu không hợp lệ", body.get("error"), "Error should match");
    }

    // =============================================================================
    // TEST CASES: FileProcessingException Handler
    // =============================================================================

    /**
     * Test Case ID: TKB062
     * Purpose: Verify handleFileProcessingException returns 400
     * Input: FileProcessingException
     * Expected Output: Response with status 400, file error message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB062: File processing error returns 400")
    public void test_handleFileProcessingException_returns400() {
        // Arrange: Create exception
        FileProcessingException exception = new FileProcessingException(
                "Không thể đọc file Excel: file bị corrupt"
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleFileProcessingException(exception);

        // Assert: Verify 400 response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"), "Status should be 400");
        assertEquals("Lỗi xử lý file", body.get("error"), "Error should match");
    }

    /**
     * Test Case ID: TKB063
     * Purpose: Verify FileProcessingException with cause
     * Input: FileProcessingException with nested exception
     * Expected Output: Response with original message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB063: File processing error with cause")
    public void test_handleFileProcessingException_withCause() {
        // Arrange: Create exception with cause
        RuntimeException cause = new RuntimeException("Original IO error");
        FileProcessingException exception = new FileProcessingException(
                "Error reading schedule file", cause
        );

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleFileProcessingException(exception);

        // Assert: Verify response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should return 400");
        assertTrue(response.getBody().get("message").toString().contains("Error reading schedule file"), 
                "Message should contain exception message");
    }

    // =============================================================================
    // TEST CASES: Generic Exception Handlers
    // =============================================================================

    /**
     * Test Case ID: TKB064
     * Purpose: Verify handleRuntimeException returns 500
     * Input: RuntimeException
     * Expected Output: Response with status 500, system error message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB064: Runtime exception returns 500")
    public void test_handleRuntimeException_returns500() {
        // Arrange: Create runtime exception
        RuntimeException exception = new RuntimeException("Unexpected system error");

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(exception);

        // Assert: Verify 500 response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Should return 500");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"), "Status should be 500");
        assertEquals("Lỗi hệ thống", body.get("error"), "Error should match");
    }

    /**
     * Test Case ID: TKB065
     * Purpose: Verify handleGenericException returns 500 with generic message
     * Input: Generic Exception
     * Expected Output: Response with status 500, generic error message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB065: Generic exception returns 500 with generic message")
    public void test_handleGenericException_returns500WithGenericMessage() {
        // Arrange: Create generic exception
        Exception exception = new Exception("Any unexpected error");

        // Act: Handle exception
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Assert: Verify 500 response with generic message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Should return 500");
        
        Map<String, Object> body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"), "Status should be 500");
        assertEquals("Đã xảy ra lỗi không mong muốn. Vui lòng liên hệ quản trị viên.", 
                body.get("message"), "Generic message should match");
    }

    // =============================================================================
    // TEST CASES: Response Structure Verification
    // =============================================================================

    /**
     * Test Case ID: TKB066
     * Purpose: Verify all error responses contain timestamp
     * Input: Different exception types
     * Expected Output: All responses include timestamp
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB066: All responses contain timestamp")
    public void test_allResponses_containTimestamp() {
        // Test ResourceNotFoundException
        ResponseEntity<Map<String, Object>> response1 = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Test")
        );
        assertNotNull(response1.getBody().get("timestamp"), "ResourceNotFound response should have timestamp");
        
        // Test DuplicateResourceException
        ResponseEntity<Map<String, Object>> response2 = handler.handleDuplicateResourceException(
                new DuplicateResourceException("Test")
        );
        assertNotNull(response2.getBody().get("timestamp"), "DuplicateResource response should have timestamp");
        
        // Test InvalidDataException
        ResponseEntity<Map<String, Object>> response3 = handler.handleInvalidDataException(
                new InvalidDataException("Test")
        );
        assertNotNull(response3.getBody().get("timestamp"), "InvalidData response should have timestamp");
    }

    /**
     * Test Case ID: TKB067
     * Purpose: Verify error responses follow consistent structure
     * Input: Different exception types
     * Expected Output: All responses have consistent fields (status, error, message, timestamp)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB067: Error responses have consistent structure")
    public void test_errorResponses_consistentStructure() {
        // Test different exception types
        ResponseEntity<Map<String, Object>>[] responses = new ResponseEntity[] {
                handler.handleResourceNotFoundException(new ResourceNotFoundException("Test")),
                handler.handleDuplicateResourceException(new DuplicateResourceException("Test")),
                handler.handleInvalidDataException(new InvalidDataException("Test")),
                handler.handleFileProcessingException(new FileProcessingException("Test"))
        };

        // Assert: All responses have consistent structure
        for (ResponseEntity<Map<String, Object>> response : responses) {
            Map<String, Object> body = response.getBody();
            assertNotNull(body.get("status"), "Response should have status field");
            assertNotNull(body.get("error"), "Response should have error field");
            assertNotNull(body.get("message"), "Response should have message field");
            assertNotNull(body.get("timestamp"), "Response should have timestamp field");
        }
    }
}
