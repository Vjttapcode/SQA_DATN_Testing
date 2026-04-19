package com.ptit.schedule.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB041
 * File Under Test: SaveScheduleRequest.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for SaveScheduleRequest DTO class
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB041 - SaveScheduleRequest Tests")
public class SaveScheduleRequestTest {

    // =============================================================================
    // OBJECT MAPPER SETUP
    // =============================================================================
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create SaveScheduleRequest with all fields
     */
    private SaveScheduleRequest createFullRequest() {
        return SaveScheduleRequest.builder()
                .subjectId(1L)
                .classNumber(1)
                .studentYear("2024")
                .major("INT")
                .specialSystem("Chính quy")
                .siSoMotLop(50)
                .roomNumber("401-A2")
                .templateDatabaseId(10L)
                .build();
    }

    // =============================================================================
    // TEST CASES: CONSTRUCTOR AND BUILDER
    // =============================================================================

    /**
     * Test Case ID: TKB041
     * Purpose: Verify builder creates SaveScheduleRequest with all fields
     * Input: All fields populated
     * Expected Output: All fields accessible correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB041: Builder creates complete request")
    public void test_builder_createsCompleteRequest() {
        // Arrange & Act: Create complete request
        SaveScheduleRequest request = createFullRequest();

        // Assert: Verify all fields
        assertNotNull(request, "Request should not be null");
        assertEquals(1L, request.getSubjectId(), "Subject ID should match");
        assertEquals(1, request.getClassNumber(), "Class number should match");
        assertEquals("2024", request.getStudentYear(), "Student year should match");
        assertEquals("INT", request.getMajor(), "Major should match");
        assertEquals("Chính quy", request.getSpecialSystem(), "Special system should match");
        assertEquals(50, request.getSiSoMotLop(), "SiSoMotLop should match");
        assertEquals("401-A2", request.getRoomNumber(), "Room number should match");
        assertEquals(10L, request.getTemplateDatabaseId(), "Template database ID should match");
    }

    /**
     * Test Case ID: TKB042
     * Purpose: Verify default constructor creates empty request
     * Input: No fields
     * Expected Output: All fields null
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB042: Default constructor creates empty request")
    public void test_defaultConstructor_emptyFields() {
        // Act: Create request with default constructor
        SaveScheduleRequest request = new SaveScheduleRequest();

        // Assert: All fields should be null
        assertNull(request.getSubjectId(), "Subject ID should be null");
        assertNull(request.getClassNumber(), "Class number should be null");
        assertNull(request.getStudentYear(), "Student year should be null");
        assertNull(request.getMajor(), "Major should be null");
        assertNull(request.getSpecialSystem(), "Special system should be null");
        assertNull(request.getSiSoMotLop(), "SiSoMotLop should be null");
        assertNull(request.getRoomNumber(), "Room number should be null");
        assertNull(request.getTemplateDatabaseId(), "Template database ID should be null");
    }

    // =============================================================================
    // TEST CASES: JSON SERIALIZATION (FIELD NAMES)
    // =============================================================================

    /**
     * Test Case ID: TKB043
     * Purpose: Verify JSON serialization uses snake_case field names
     * Input: SaveScheduleRequest with data
     * Expected Output: JSON contains snake_case keys (subject_id, class_number, etc.)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB043: JSON uses snake_case field names")
    public void test_jsonSerialization_usesSnakeCase() throws Exception {
        // Arrange: Create request with data
        SaveScheduleRequest request = createFullRequest();

        // Act: Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Assert: Verify snake_case keys in JSON
        assertTrue(json.contains("\"subject_id\""), "JSON should contain subject_id");
        assertTrue(json.contains("\"class_number\""), "JSON should contain class_number");
        assertTrue(json.contains("\"student_year\""), "JSON should contain student_year");
        assertTrue(json.contains("\"special_system\""), "JSON should contain special_system");
        assertTrue(json.contains("\"si_so_mot_lop\""), "JSON should contain si_so_mot_lop");
        assertTrue(json.contains("\"room_number\""), "JSON should contain room_number");
        assertTrue(json.contains("\"template_database_id\""), "JSON should contain template_database_id");
    }

    /**
     * Test Case ID: TKB044
     * Purpose: Verify JSON deserialization maps snake_case to camelCase fields
     * Input: JSON with snake_case keys
     * Expected Output: Object fields populated correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB044: JSON deserialization maps snake_case correctly")
    public void test_jsonDeserialization_mapsSnakeCase() throws Exception {
        // Arrange: JSON with snake_case keys
        String json = "{\"subject_id\":5,\"class_number\":2,\"student_year\":\"2024\"," +
                "\"major\":\"INT\",\"special_system\":\"Chính quy\",\"si_so_mot_lop\":60," +
                "\"room_number\":\"402-A2\",\"template_database_id\":20}";

        // Act: Deserialize from JSON
        SaveScheduleRequest request = objectMapper.readValue(json, SaveScheduleRequest.class);

        // Assert: Verify fields mapped correctly
        assertEquals(5L, request.getSubjectId(), "Subject ID should be 5");
        assertEquals(2, request.getClassNumber(), "Class number should be 2");
        assertEquals("2024", request.getStudentYear(), "Student year should be 2024");
        assertEquals("INT", request.getMajor(), "Major should be INT");
        assertEquals("Chính quy", request.getSpecialSystem(), "Special system should match");
        assertEquals(60, request.getSiSoMotLop(), "SiSoMotLop should be 60");
        assertEquals("402-A2", request.getRoomNumber(), "Room number should be 402-A2");
        assertEquals(20L, request.getTemplateDatabaseId(), "Template ID should be 20");
    }

    // =============================================================================
    // TEST CASES: FIELD VALIDATION
    // =============================================================================

    /**
     * Test Case ID: TKB045
     * Purpose: Verify request with only required fields (subjectId, templateDatabaseId)
     * Input: Only required fields populated
     * Expected Output: Optional fields null
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB045: Request with only required fields")
    public void test_requiredFieldsOnly() {
        // Arrange: Only subjectId and templateDatabaseId
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(1L)
                .templateDatabaseId(10L)
                .build();

        // Assert: Required fields set, optional fields null
        assertEquals(1L, request.getSubjectId(), "Subject ID should be set");
        assertEquals(10L, request.getTemplateDatabaseId(), "Template ID should be set");
        assertNull(request.getClassNumber(), "Class number should be null");
        assertNull(request.getStudentYear(), "Student year should be null");
        assertNull(request.getMajor(), "Major should be null");
        assertNull(request.getRoomNumber(), "Room number should be null");
    }

    /**
     * Test Case ID: TKB046
     * Purpose: Verify request with all optional fields
     * Input: All fields including optional
     * Expected Output: All fields accessible
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB046: Request with all fields")
    public void test_allFields_populated() {
        // Arrange: All fields
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(100L)
                .classNumber(3)
                .studentYear("2023")
                .major("EPU")
                .specialSystem("Tại chức")
                .siSoMotLop(80)
                .roomNumber("501-B1")
                .templateDatabaseId(50L)
                .build();

        // Assert: Verify all fields
        assertEquals(100L, request.getSubjectId(), "Subject ID should be 100");
        assertEquals(3, request.getClassNumber(), "Class number should be 3");
        assertEquals("2023", request.getStudentYear(), "Student year should be 2023");
        assertEquals("EPU", request.getMajor(), "Major should be EPU");
        assertEquals("Tại chức", request.getSpecialSystem(), "Special system should be Tại chức");
        assertEquals(80, request.getSiSoMotLop(), "SiSoMotLop should be 80");
        assertEquals("501-B1", request.getRoomNumber(), "Room number should be 501-B1");
        assertEquals(50L, request.getTemplateDatabaseId(), "Template ID should be 50");
    }

    // =============================================================================
    // TEST CASES: EDGE CASES
    // =============================================================================

    /**
     * Test Case ID: TKB047
     * Purpose: Verify request with null optional fields
     * Input: Required fields only, optional fields null
     * Expected Output: Serialization handles nulls
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB047: Handles null optional fields")
    public void test_nullOptionalFields() {
        // Arrange: Only required fields
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(1L)
                .templateDatabaseId(1L)
                .build();

        // Act & Assert: Serialize should not crash
        assertDoesNotThrow(() -> {
            String json = objectMapper.writeValueAsString(request);
            assertNotNull(json, "JSON should not be null");
            assertTrue(json.contains("\"subject_id\":1"), "Should contain subject_id");
            assertTrue(json.contains("\"template_database_id\":1"), "Should contain template_database_id");
        });
    }

    /**
     * Test Case ID: TKB048
     * Purpose: Verify room number with hyphen format parsing
     * Input: Room number "401-A2" (name-building format)
     * Expected Output: Room number stored correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB048: Room number with hyphen format")
    public void test_roomNumber_hyphenFormat() {
        // Arrange: Room number in name-building format
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(1L)
                .templateDatabaseId(1L)
                .roomNumber("401-A2")
                .build();

        // Assert: Room number stored as-is (parsing done in controller/service)
        assertEquals("401-A2", request.getRoomNumber(), "Room number should be stored");
    }

    /**
     * Test Case ID: TKB049
     * Purpose: Verify large student count
     * Input: siSoMotLop=200 (large lecture hall)
     * Expected Output: Large value stored correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB049: Large student count")
    public void test_largeStudentCount() {
        // Arrange: Large student count for lecture hall
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(1L)
                .templateDatabaseId(1L)
                .siSoMotLop(200)
                .build();

        // Assert: Large value preserved
        assertEquals(200, request.getSiSoMotLop(), "Large student count preserved");
    }

    /**
     * Test Case ID: TKB050
     * Purpose: Verify special characters in major field
     * Input: Major with special characters
     * Expected Output: Special characters preserved
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB050: Special characters in major field")
    public void test_specialCharactersInMajor() {
        // Arrange: Major with spaces and special chars
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(1L)
                .templateDatabaseId(1L)
                .major("Viễn thông 1")  // Special characters
                .build();

        // Assert: Special characters preserved
        assertEquals("Viễn thông 1", request.getMajor(), "Special characters should be preserved");
    }

    // =============================================================================
    // TEST CASES: OBJECT MUTABILITY
    // =============================================================================

    /**
     * Test Case ID: TKB051
     * Purpose: Verify setters modify fields after creation
     * Input: Use setters to modify fields
     * Expected Output: Fields updated correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB051: Setters modify fields")
    public void test_setters_modifyFields() {
        // Arrange: Create empty request
        SaveScheduleRequest request = new SaveScheduleRequest();

        // Act: Use setters to populate
        request.setSubjectId(50L);
        request.setClassNumber(2);
        request.setStudentYear("2025");
        request.setMajor("MAT");
        request.setSpecialSystem("Cao đẳng");
        request.setSiSoMotLop(40);
        request.setRoomNumber("301-A1");
        request.setTemplateDatabaseId(25L);

        // Assert: All fields updated
        assertEquals(50L, request.getSubjectId(), "Subject ID should be 50");
        assertEquals(2, request.getClassNumber(), "Class number should be 2");
        assertEquals("2025", request.getStudentYear(), "Student year should be 2025");
        assertEquals("MAT", request.getMajor(), "Major should be MAT");
        assertEquals("Cao đẳng", request.getSpecialSystem(), "Special system should be Cao đẳng");
        assertEquals(40, request.getSiSoMotLop(), "SiSoMotLop should be 40");
        assertEquals("301-A1", request.getRoomNumber(), "Room number should be 301-A1");
        assertEquals(25L, request.getTemplateDatabaseId(), "Template ID should be 25");
    }

    /**
     * Test Case ID: TKB052
     * Purpose: Verify equals() and hashCode() work correctly
     * Input: Two requests with same data
     * Expected Output: equals() returns true, hashCode() equal
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB052: equals() and hashCode() work correctly")
    public void test_equalsAndHashCode() {
        // Arrange: Two requests with same data
        SaveScheduleRequest request1 = createFullRequest();
        SaveScheduleRequest request2 = SaveScheduleRequest.builder()
                .subjectId(1L)
                .classNumber(1)
                .studentYear("2024")
                .major("INT")
                .specialSystem("Chính quy")
                .siSoMotLop(50)
                .roomNumber("401-A2")
                .templateDatabaseId(10L)
                .build();

        // Assert: Equals and hashCode work
        assertEquals(request1, request2, "Requests with same data should be equal");
        assertEquals(request1.hashCode(), request2.hashCode(), "Hash codes should be equal");
    }

    /**
     * Test Case ID: TKB053
     * Purpose: Verify different requests are not equal
     * Input: Two requests with different data
     * Expected Output: equals() returns false
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB053: Different requests not equal")
    public void test_differentRequests_notEqual() {
        // Arrange: Two requests with different data
        SaveScheduleRequest request1 = createFullRequest();
        SaveScheduleRequest request2 = SaveScheduleRequest.builder()
                .subjectId(999L)  // Different
                .templateDatabaseId(10L)
                .build();

        // Assert: Requests not equal
        assertNotEquals(request1, request2, "Different requests should not be equal");
    }
}
