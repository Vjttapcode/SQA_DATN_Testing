package com.ptit.schedule.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB001
 * File Under Test: ApiResponse.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for ApiResponse DTO class
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB001 - ApiResponse Tests")
public class ApiResponseTest {

    // =============================================================================
    // OBJECT MAPPER SETUP
    // =============================================================================
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to verify success response structure
     */
    private void verifySuccessResponse(ApiResponse<?> response, boolean expectedSuccess, int expectedStatus) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedSuccess, response.isSuccess(), "Success flag should match");
        assertEquals(expectedStatus, response.getStatus(), "Status code should match");
    }

    /**
     * Helper method to verify error response structure
     */
    private void verifyErrorResponse(ApiResponse<?> response, boolean expectedSuccess, int expectedStatus) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedSuccess, response.isSuccess(), "Success flag should be false");
        assertEquals(expectedStatus, response.getStatus(), "Status code should match");
        assertNotNull(response.getMessage(), "Error message should not be null");
    }

    // =============================================================================
    // TEST CASES: SUCCESS RESPONSES
    // =============================================================================

    /**
     * Test Case ID: TKB001
     * Purpose: Verify success(T data) creates correct response with status 200
     * Input: Map with key-value data
     * Expected Output: success=true, status=200, message="Success", data is present
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB001: success(T data) returns status 200 with data")
    public void test_success_withData_returnsStatus200() {
        // Arrange: Prepare test data
        Map<String, String> testData = new HashMap<>();
        testData.put("key", "value");
        testData.put("name", "Test Schedule");

        // Act: Call static factory method
        ApiResponse<Map<String, String>> response = ApiResponse.success(testData);

        // Assert: Verify response structure
        verifySuccessResponse(response, true, 200);
        assertEquals("Success", response.getMessage(), "Default message should be 'Success'");
        assertNotNull(response.getData(), "Data should not be null");
        assertEquals("value", response.getData().get("key"), "Data content should match");
        assertNull(response.getError(), "Error field should be null for success response");
    }

    /**
     * Test Case ID: TKB002
     * Purpose: Verify success(String message) creates response with custom message
     * Input: Custom message string
     * Expected Output: success=true, status=200, message as provided
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB002: success(String message) returns custom message")
    public void test_success_withMessage_returnsCustomMessage() {
        // Arrange: Prepare custom message
        String customMessage = "Schedule saved successfully";

        // Act: Call static factory method
        ApiResponse<Void> response = ApiResponse.success(customMessage);

        // Assert: Verify response with custom message
        verifySuccessResponse(response, true, 200);
        assertEquals(customMessage, response.getMessage(), "Message should match provided value");
        assertNull(response.getData(), "Data should be null when only message is provided");
    }

    /**
     * Test Case ID: TKB003
     * Purpose: Verify success(T data, String message) creates response with both data and message
     * Input: Data object and custom message
     * Expected Output: success=true, status=200, data and message present
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB003: success(T data, String message) returns both data and message")
    public void test_success_withDataAndMessage_returnsBothFields() {
        // Arrange: Prepare data and message
        String testData = "Test schedule data";
        String customMessage = "Data loaded successfully";

        // Act: Call static factory method
        ApiResponse<String> response = ApiResponse.success(testData, customMessage);

        // Assert: Verify both data and message are present
        verifySuccessResponse(response, true, 200);
        assertEquals(customMessage, response.getMessage(), "Message should match");
        assertEquals(testData, response.getData(), "Data should match");
    }

    /**
     * Test Case ID: TKB004
     * Purpose: Verify created(T data) creates response with status 201
     * Input: Data object
     * Expected Output: success=true, status=201, message="Created successfully"
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB004: created(T data) returns status 201")
    public void test_created_withData_returnsStatus201() {
        // Arrange: Prepare data for creation
        Long scheduleId = 123L;
        Map<String, Object> createdData = new HashMap<>();
        createdData.put("id", scheduleId);
        createdData.put("status", "created");

        // Act: Call created factory method
        ApiResponse<Map<String, Object>> response = ApiResponse.created(createdData);

        // Assert: Verify 201 Created response
        verifySuccessResponse(response, true, 201);
        assertEquals("Created successfully", response.getMessage(), "Default message for created");
        assertNotNull(response.getData(), "Data should be present");
        assertEquals(scheduleId, response.getData().get("id"), "Created ID should match");
    }

    /**
     * Test Case ID: TKB005
     * Purpose: Verify created(T data, String message) creates response with custom message
     * Input: Data object and custom message
     * Expected Output: success=true, status=201, custom message
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB005: created(T data, String message) returns custom message")
    public void test_created_withMessage_returnsCustomMessage() {
        // Arrange: Prepare data and message
        String testData = "New schedule";
        String customMessage = "Schedule successfully created at PTIT";

        // Act: Call created factory method with message
        ApiResponse<String> response = ApiResponse.created(testData, customMessage);

        // Assert: Verify 201 response with custom message
        verifySuccessResponse(response, true, 201);
        assertEquals(customMessage, response.getMessage(), "Custom message should match");
        assertEquals(testData, response.getData(), "Data should match");
    }

    // =============================================================================
    // TEST CASES: ERROR RESPONSES
    // =============================================================================

    /**
     * Test Case ID: TKB006
     * Purpose: Verify error(String message, int status) creates correct error response
     * Input: Error message and HTTP status code
     * Expected Output: success=false, status as provided, error field populated
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB006: error(String, int) returns correct error structure")
    public void test_error_withMessageAndStatus_returnsErrorResponse() {
        // Arrange: Prepare error details
        String errorMessage = "Invalid schedule data";
        int errorStatus = 400;

        // Act: Call error factory method
        ApiResponse<Void> response = ApiResponse.error(errorMessage, errorStatus);

        // Assert: Verify error response structure
        verifyErrorResponse(response, false, errorStatus);
        assertEquals(errorMessage, response.getMessage(), "Message should match");
        assertEquals(errorMessage, response.getError(), "Error field should equal message");
        assertNull(response.getData(), "Data should be null for error response");
    }

    /**
     * Test Case ID: TKB007
     * Purpose: Verify error(String, String, int) creates response with separate error type
     * Input: Message, error type, and status code
     * Expected Output: success=false, both message and error fields populated
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB007: error(String, String, int) returns message and error type")
    public void test_error_withMessageErrorAndStatus_returnsBothFields() {
        // Arrange: Prepare error details
        String message = "Schedule validation failed";
        String errorType = "VALIDATION_ERROR";
        int status = 422;

        // Act: Call error factory method with separate error type
        ApiResponse<Void> response = ApiResponse.error(message, errorType, status);

        // Assert: Verify error response with separate error type
        verifyErrorResponse(response, false, status);
        assertEquals(message, response.getMessage(), "Message should match");
        assertEquals(errorType, response.getError(), "Error type should match");
    }

    /**
     * Test Case ID: TKB008
     * Purpose: Verify notFound(String message) creates 404 response
     * Input: Resource not found message
     * Expected Output: success=false, status=404, error="Resource not found"
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB008: notFound(String) returns 404 status")
    public void test_notFound_returns404Status() {
        // Arrange: Prepare not found message
        String notFoundMessage = "Schedule with ID 999 not found";

        // Act: Call notFound factory method
        ApiResponse<Void> response = ApiResponse.notFound(notFoundMessage);

        // Assert: Verify 404 response
        verifyErrorResponse(response, false, 404);
        assertEquals(notFoundMessage, response.getMessage(), "Message should match");
        assertEquals("Resource not found", response.getError(), "Default error type should match");
    }

    /**
     * Test Case ID: TKB009
     * Purpose: Verify badRequest(String message) creates 400 response
     * Input: Bad request message
     * Expected Output: success=false, status=400, error="Bad request"
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB009: badRequest(String) returns 400 status")
    public void test_badRequest_returns400Status() {
        // Arrange: Prepare bad request message
        String badRequestMessage = "Invalid date range for schedule";

        // Act: Call badRequest factory method
        ApiResponse<Void> response = ApiResponse.badRequest(badRequestMessage);

        // Assert: Verify 400 response
        verifyErrorResponse(response, false, 400);
        assertEquals(badRequestMessage, response.getMessage(), "Message should match");
        assertEquals("Bad request", response.getError(), "Default error type should match");
    }

    // =============================================================================
    // TEST CASES: JSON SERIALIZATION
    // =============================================================================

    /**
     * Test Case ID: TKB010
     * Purpose: Verify ApiResponse serializes correctly to JSON
     * Input: Success response with nested data
     * Expected Output: JSON contains success, message, status fields; null fields excluded
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB010: JSON serialization excludes null fields")
    public void test_jsonSerialization_excludesNullFields() throws Exception {
        // Arrange: Create response with partial data
        ApiResponse<String> response = ApiResponse.success("Test Data", "Custom message");

        // Act: Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Assert: Verify JSON structure
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("\"success\":true"), "JSON should contain success=true");
        assertTrue(json.contains("\"message\":\"Custom message\""), "JSON should contain message");
        assertTrue(json.contains("\"status\":200"), "JSON should contain status");
        assertFalse(json.contains("\"error\":null"), "JSON should not contain null error");
        assertFalse(json.contains("\"error\":null"), "JSON should not contain null error");
    }

    /**
     * Test Case ID: TKB011
     * Purpose: Verify ApiResponse deserializes correctly from JSON
     * Input: JSON string with all fields
     * Expected Output: Deserialized object matches original values
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB011: JSON deserialization creates correct object")
    public void test_jsonDeserialization_createsCorrectObject() throws Exception {
        // Arrange: Create JSON with all fields
        String json = "{\"success\":true,\"message\":\"Test\",\"status\":200,\"data\":{\"id\":1}}";

        // Act: Deserialize from JSON
        ApiResponse<Map> response = objectMapper.readValue(json, ApiResponse.class);

        // Assert: Verify deserialized values
        assertTrue(response.isSuccess(), "Success should be true");
        assertEquals("Test", response.getMessage(), "Message should match");
        assertEquals(200, response.getStatus(), "Status should match");
        assertNotNull(response.getData(), "Data should not be null");
    }

    // =============================================================================
    // TEST CASES: BUILDER PATTERN
    // =============================================================================

    /**
     * Test Case ID: TKB012
     * Purpose: Verify builder pattern creates ApiResponse with all fields
     * Input: All fields populated via builder
     * Expected Output: All fields accessible correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB012: Builder creates ApiResponse with all fields")
    public void test_builder_withAllFields_createsCompleteResponse() {
        // Arrange & Act: Build complete response
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Complete response")
                .data("Test data content")
                .status(201)
                .build();

        // Assert: Verify all fields
        assertTrue(response.isSuccess(), "Success should be true");
        assertEquals("Complete response", response.getMessage(), "Message should match");
        assertEquals("Test data content", response.getData(), "Data should match");
        assertEquals(201, response.getStatus(), "Status should match");
    }

    /**
     * Test Case ID: TKB013
     * Purpose: Verify builder allows partial field population
     * Input: Only success and message fields
     * Expected Output: Other fields null
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB013: Builder with partial fields leaves others null")
    public void test_builder_withPartialFields_leavesOthersNull() {
        // Arrange & Act: Build response with only success and message
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Error occurred")
                .build();

        // Assert: Only specified fields are set
        assertFalse(response.isSuccess(), "Success should be false");
        assertEquals("Error occurred", response.getMessage(), "Message should match");
        assertNull(response.getData(), "Data should be null");
        assertEquals(0, response.getStatus(), "Status should be default (0)");
        assertNull(response.getError(), "Error should be null");
    }

    // =============================================================================
    // TEST CASES: NULL HANDLING
    // =============================================================================

    /**
     * Test Case ID: TKB014
     * Purpose: Verify success() with null data works correctly
     * Input: null data object
     * Expected Output: Response created with null data field
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB014: success(null) handles null data gracefully")
    public void test_success_withNullData_handlesGracefully() {
        // Act: Call success with null data
        ApiResponse<String> response = ApiResponse.success((String) null);

        // Assert: Verify response created with null data
        assertTrue(response.isSuccess(), "Success should be true");
        assertEquals("Success", response.getMessage(), "Default message should be set");
        assertNull(response.getData(), "Data should be null as provided");
    }

    /**
     * Test Case ID: TKB015
     * Purpose: Verify ApiResponse handles complex nested data structures
     * Input: Nested map with schedule data
     * Expected Output: Complex data serialized/deserialized correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB015: Handles complex nested data structures")
    public void test_complexNestedData_serializationWorks() throws Exception {
        // Arrange: Create complex nested data structure
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("id", 1L);
        scheduleData.put("subject", "INT1306");
        scheduleData.put("room", "401-A2");

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("schedule", scheduleData);
        nestedData.put("conflicts", Arrays.asList("room", "teacher"));

        ApiResponse<Map<String, Object>> response = ApiResponse.success(nestedData, "Complex data test");

        // Act & Assert: Verify serialization works
        String json = objectMapper.writeValueAsString(response);
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("\"schedule\""), "JSON should contain nested schedule");
        assertTrue(json.contains("\"conflicts\""), "JSON should contain conflicts array");
    }

    /**
     * Test Case ID: TKB016
     * Purpose: Verify ApiResponse with generic List type works correctly
     * Input: List of schedule strings
     * Expected Output: List data preserved in response
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB016: ApiResponse with List<T> generic type works")
    public void test_genericListType_preservesListData() {
        // Arrange: Create list of schedules
        var scheduleList = Arrays.asList("INT1306", "INT1402", "INT1503");

        // Act: Create response with list data
        ApiResponse<java.util.List<String>> response = ApiResponse.success(scheduleList);

        // Assert: Verify list preserved
        assertNotNull(response.getData(), "Data list should not be null");
        assertEquals(3, response.getData().size(), "List should have 3 items");
        assertEquals("INT1306", response.getData().get(0), "First item should match");
    }
}
