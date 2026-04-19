package com.ptit.schedule.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB068
 * File Under Test: Exception classes (ResourceNotFoundException, DuplicateResourceException, InvalidDataException, FileProcessingException)
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for custom exception classes
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB068 - Exception Classes Tests")
public class ExceptionClassesTest {

    // =============================================================================
    // TEST CASES: ResourceNotFoundException
    // =============================================================================

    /**
     * Test Case ID: TKB068
     * Purpose: Verify ResourceNotFoundException with simple message
     * Input: "Không tìm thấy lịch học"
     * Expected Output: Exception message equals input
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB068: ResourceNotFoundException with simple message")
    public void test_resourceNotFoundException_simpleMessage() {
        // Arrange
        String message = "Không tìm thấy lịch học";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertTrue(exception instanceof RuntimeException, "Should extend RuntimeException");
    }

    /**
     * Test Case ID: TKB069
     * Purpose: Verify ResourceNotFoundException with factory constructor
     * Input: resourceName="Subject", fieldName="maMon", fieldValue="INT1306"
     * Expected Output: Formatted message "Không tìm thấy Subject với maMon = 'INT1306'"
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB069: ResourceNotFoundException with factory constructor")
    public void test_resourceNotFoundException_factoryConstructor() {
        // Arrange
        String resourceName = "Subject";
        String fieldName = "maMon";
        String fieldValue = "INT1306";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(resourceName, fieldName, fieldValue);

        // Assert
        String expectedMessage = "Không tìm thấy Subject với maMon = 'INT1306'";
        assertEquals(expectedMessage, exception.getMessage(), "Formatted message should match");
    }

    /**
     * Test Case ID: TKB070
     * Purpose: Verify ResourceNotFoundException with numeric field value
     * Input: resourceName="Schedule", fieldName="id", fieldValue=123
     * Expected Output: Formatted message with numeric value
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB070: ResourceNotFoundException with numeric value")
    public void test_resourceNotFoundException_numericFieldValue() {
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException("Schedule", "id", 123);

        // Assert
        assertEquals("Không tìm thấy Schedule với id = '123'", exception.getMessage());
    }

    // =============================================================================
    // TEST CASES: DuplicateResourceException
    // =============================================================================

    /**
     * Test Case ID: TKB071
     * Purpose: Verify DuplicateResourceException with simple message
     * Input: "Đã tồn tại môn học"
     * Expected Output: Exception message equals input
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB071: DuplicateResourceException with simple message")
    public void test_duplicateResourceException_simpleMessage() {
        // Arrange
        String message = "Đã tồn tại môn học";

        // Act
        DuplicateResourceException exception = new DuplicateResourceException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test Case ID: TKB072
     * Purpose: Verify DuplicateResourceException with factory constructor
     * Input: resourceName="Schedule", fieldName="id", fieldValue="456"
     * Expected Output: Formatted message "Đã tồn tại Schedule với id = '456'"
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB072: DuplicateResourceException factory constructor")
    public void test_duplicateResourceException_factoryConstructor() {
        // Act
        DuplicateResourceException exception = new DuplicateResourceException("Schedule", "id", "456");

        // Assert
        String expectedMessage = "Đã tồn tại Schedule với id = '456'";
        assertEquals(expectedMessage, exception.getMessage());
    }

    // =============================================================================
    // TEST CASES: InvalidDataException
    // =============================================================================

    /**
     * Test Case ID: TKB073
     * Purpose: Verify InvalidDataException with message
     * Input: "Dữ liệu không hợp lệ"
     * Expected Output: Exception message equals input
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB073: InvalidDataException with message")
    public void test_invalidDataException_withMessage() {
        // Arrange
        String message = "Dữ liệu không hợp lệ";

        // Act
        InvalidDataException exception = new InvalidDataException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test Case ID: TKB074
     * Purpose: Verify InvalidDataException with empty message
     * Input: ""
     * Expected Output: Empty message string
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB074: InvalidDataException with empty message")
    public void test_invalidDataException_emptyMessage() {
        // Act
        InvalidDataException exception = new InvalidDataException("");

        // Assert
        assertEquals("", exception.getMessage());
    }

    // =============================================================================
    // TEST CASES: FileProcessingException
    // =============================================================================

    /**
     * Test Case ID: TKB075
     * Purpose: Verify FileProcessingException with simple message
     * Input: "Không thể đọc file"
     * Expected Output: Exception message equals input
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB075: FileProcessingException with message")
    public void test_fileProcessingException_withMessage() {
        // Arrange
        String message = "Không thể đọc file";

        // Act
        FileProcessingException exception = new FileProcessingException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test Case ID: TKB076
     * Purpose: Verify FileProcessingException with message and cause
     * Input: message="Error", cause=IOException
     * Expected Output: Exception with message and cause chain
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB076: FileProcessingException with cause")
    public void test_fileProcessingException_withCause() {
        // Arrange
        String message = "Error processing file";
        RuntimeException cause = new RuntimeException("Original error");

        // Act
        FileProcessingException exception = new FileProcessingException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /**
     * Test Case ID: TKB077
     * Purpose: Verify FileProcessingException cause is accessible via getCause()
     * Input: Exception with cause
     * Expected Output: getCause() returns original exception
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB077: FileProcessingException cause chain")
    public void test_fileProcessingException_causeChain() {
        // Arrange: Create nested cause chain
        RuntimeException rootCause = new RuntimeException("Root cause");
        RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
        FileProcessingException exception = new FileProcessingException("Top level error", intermediateCause);

        // Assert: Cause chain is preserved
        assertEquals("Intermediate", exception.getCause().getMessage());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    // =============================================================================
    // TEST CASES: Exception Inheritance
    // =============================================================================

    /**
     * Test Case ID: TKB078
     * Purpose: Verify all custom exceptions extend RuntimeException
     * Input: All exception classes
     * Expected Output: All are RuntimeException subclasses
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB078: All exceptions extend RuntimeException")
    public void test_allExceptions_extendRuntimeException() {
        // Assert: All custom exceptions extend RuntimeException
        assertTrue(new ResourceNotFoundException("test") instanceof RuntimeException);
        assertTrue(new DuplicateResourceException("test") instanceof RuntimeException);
        assertTrue(new InvalidDataException("test") instanceof RuntimeException);
        assertTrue(new FileProcessingException("test") instanceof RuntimeException);
    }

    /**
     * Test Case ID: TKB079
     * Purpose: Verify exceptions can be thrown and caught properly
     * Input: try-catch block
     * Expected Output: Exception caught correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB079: Exceptions can be thrown and caught")
    public void test_exceptions_canBeThrownAndCaught() {
        // Test ResourceNotFoundException
        try {
            throw new ResourceNotFoundException("Test");
        } catch (ResourceNotFoundException e) {
            assertEquals("Test", e.getMessage());
        }

        // Test DuplicateResourceException
        try {
            throw new DuplicateResourceException("Duplicate");
        } catch (DuplicateResourceException e) {
            assertEquals("Duplicate", e.getMessage());
        }

        // Test InvalidDataException
        try {
            throw new InvalidDataException("Invalid");
        } catch (InvalidDataException e) {
            assertEquals("Invalid", e.getMessage());
        }

        // Test FileProcessingException
        try {
            throw new FileProcessingException("File error");
        } catch (FileProcessingException e) {
            assertEquals("File error", e.getMessage());
        }
    }
}
