package com.ptit.schedule.controller;

import com.ptit.schedule.dto.*;
import com.ptit.schedule.entity.*;
import com.ptit.schedule.exception.InvalidDataException;
import com.ptit.schedule.exception.ResourceNotFoundException;
import com.ptit.schedule.repository.SubjectRepository;
import com.ptit.schedule.repository.TKBTemplateRepository;
import com.ptit.schedule.repository.RoomRepository;
import com.ptit.schedule.service.DataLoaderService;
import com.ptit.schedule.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB137
 * File Under Test: ScheduleController.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for ScheduleController - Schedule management endpoints
 * ===============================================================================
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Test Case ID: TKB137 - ScheduleController Tests")
public class ScheduleControllerTest {

    // =============================================================================
    // MOCK DEPENDENCIES
    // =============================================================================
    @Mock
    private ScheduleService scheduleService;

    @Mock
    private TKBTemplateRepository tkbTemplateRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    // =============================================================================
    // OBJECT UNDER TEST
    // =============================================================================
    @InjectMocks
    private ScheduleController scheduleController;

    // =============================================================================
    // TEST DATA
    // =============================================================================
    private User testUser;
    private Subject testSubject;
    private TKBTemplate testTemplate;
    private Room testRoom;

    // =============================================================================
    // SETUP
    // =============================================================================

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@ptit.edu.vn")
                .fullName("Test User")
                .build();

        // Create test subject
        testSubject = Subject.builder()
                .id(1L)
                .subjectCode("INT1306")
                .subjectName("Nhập môn lập trình")
                .build();

        // Create test template
        testTemplate = TKBTemplate.builder()
                .id(10L)
                .templateId("TEMPLATE1")
                .totalPeriods(45)
                .build();

        // Create test room
        testRoom = Room.builder()
                .id(1L)
                .name("401")
                .building("A2")
                .capacity(50)
                .build();

        // Setup security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create SaveScheduleRequest
     */
    private SaveScheduleRequest createSaveScheduleRequest() {
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
    // TEST CASES: saveSchedule (save-batch endpoint)
    // =============================================================================

    /**
     * Test Case ID: TKB137
     * Purpose: Verify saveSchedule() saves schedule successfully
     * Input: Valid SaveScheduleRequest list
     * Expected Output: ResponseEntity with success message, HTTP 200
     * CheckDB: Database write operation (requires transaction rollback)
     */
    @Test
    @DisplayName("TKB137: saveSchedule() saves successfully")
    public void test_saveSchedule_savesSuccessfully() {
        // Arrange: Setup mocks
        List<SaveScheduleRequest> requests = Collections.singletonList(createSaveScheduleRequest());
        
        when(subjectRepository.getReferenceById(1L)).thenReturn(testSubject);
        when(tkbTemplateRepository.getReferenceById(10L)).thenReturn(testTemplate);
        when(roomRepository.findByNameAndBuilding("401", "A2")).thenReturn(Optional.of(testRoom));
        doNothing().when(scheduleService).saveAll(anyList());
        
        // Mock semester for Redis commit
        Schedule mockSchedule = Schedule.builder().tkbTemplate(null).build();
        
        // Act
        ResponseEntity<String> response = scheduleController.saveSchedule(requests);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return 200 OK");
        assertEquals("Đã lưu TKB vào database!", response.getBody(), "Success message should match");
        verify(scheduleService).saveAll(anyList());
    }

    /**
     * Test Case ID: TKB138
     * Purpose: Verify saveSchedule() throws exception for null/empty list
     * Input: Empty list
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB138: saveSchedule() throws for empty list")
    public void test_saveSchedule_emptyList_throwsException() {
        // Arrange
        List<SaveScheduleRequest> emptyRequests = Collections.emptyList();

        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.saveSchedule(emptyRequests);
        }, "Empty list should throw InvalidDataException");
    }

    /**
     * Test Case ID: TKB139
     * Purpose: Verify saveSchedule() throws exception for null request
     * Input: null
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB139: saveSchedule() throws for null request")
    public void test_saveSchedule_nullRequest_throwsException() {
        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.saveSchedule(null);
        }, "Null request should throw InvalidDataException");
    }

    /**
     * Test Case ID: TKB140
     * Purpose: Verify saveSchedule() throws exception for missing subjectId
     * Input: Request with null subjectId
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB140: saveSchedule() throws for missing subjectId")
    public void test_saveSchedule_missingSubjectId_throwsException() {
        // Arrange: Request without subjectId
        SaveScheduleRequest invalidRequest = SaveScheduleRequest.builder()
                .templateDatabaseId(10L)
                .build();
        List<SaveScheduleRequest> requests = Collections.singletonList(invalidRequest);

        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.saveSchedule(requests);
        }, "Missing subjectId should throw InvalidDataException");
    }

    /**
     * Test Case ID: TKB141
     * Purpose: Verify saveSchedule() throws exception for missing templateId
     * Input: Request with null templateDatabaseId
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB141: saveSchedule() throws for missing templateId")
    public void test_saveSchedule_missingTemplateId_throwsException() {
        // Arrange: Request without templateId
        SaveScheduleRequest invalidRequest = SaveScheduleRequest.builder()
                .subjectId(1L)
                .build();
        List<SaveScheduleRequest> requests = Collections.singletonList(invalidRequest);

        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.saveSchedule(requests);
        }, "Missing templateId should throw InvalidDataException");
    }

    /**
     * Test Case ID: TKB142
     * Purpose: Verify saveSchedule() handles null user
     * Input: Valid request but no authenticated user
     * Expected Output: ResourceNotFoundException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB142: saveSchedule() throws for null user")
    public void test_saveSchedule_nullUser_throwsException() {
        // Arrange: No authenticated user
        when(authentication.getPrincipal()).thenReturn(null);
        
        List<SaveScheduleRequest> requests = Collections.singletonList(createSaveScheduleRequest());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            scheduleController.saveSchedule(requests);
        }, "Null user should throw ResourceNotFoundException");
    }

    // =============================================================================
    // TEST CASES: getAllSchedules
    // =============================================================================

    /**
     * Test Case ID: TKB143
     * Purpose: Verify getAllSchedules() returns user's schedules
     * Input: Authenticated user
     * Expected Output: ResponseEntity with list of user's schedules
     * CheckDB: Database read (user's schedules)
     */
    @Test
    @DisplayName("TKB143: getAllSchedules() returns user schedules")
    public void test_getAllSchedules_returnsSchedules() {
        // Arrange: Setup mock schedules
        List<Schedule> mockSchedules = Arrays.asList(
                Schedule.builder().id(1L).subject(testSubject).build(),
                Schedule.builder().id(2L).subject(testSubject).build()
        );
        when(scheduleService.getSchedulesByUserId(1L)).thenReturn(mockSchedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.getAllSchedules();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should return 200 OK");
        assertNotNull(response.getBody(), "Body should not be null");
        assertEquals(2, response.getBody().size(), "Should return 2 schedules");
        verify(scheduleService).getSchedulesByUserId(1L);
    }

    // =============================================================================
    // TEST CASES: getSchedulesBySubject
    // =============================================================================

    /**
     * Test Case ID: TKB144
     * Purpose: Verify getSchedulesBySubject() returns schedules for subject
     * Input: subjectId="INT1306"
     * Expected Output: ResponseEntity with schedules for subject
     * CheckDB: Database read
     */
    @Test
    @DisplayName("TKB144: getSchedulesBySubject() returns subject schedules")
    public void test_getSchedulesBySubject_returnsSchedules() {
        // Arrange
        List<Schedule> mockSchedules = Collections.singletonList(
                Schedule.builder().id(1L).subject(testSubject).build()
        );
        when(scheduleService.getSchedulesBySubjectId("INT1306")).thenReturn(mockSchedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesBySubject("INT1306");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // =============================================================================
    // TEST CASES: getSchedulesByMajor
    // =============================================================================

    /**
     * Test Case ID: TKB145
     * Purpose: Verify getSchedulesByMajor() returns schedules for major
     * Input: major="INT"
     * Expected Output: ResponseEntity with schedules for major
     * CheckDB: Database read
     */
    @Test
    @DisplayName("TKB145: getSchedulesByMajor() returns major schedules")
    public void test_getSchedulesByMajor_returnsSchedules() {
        // Arrange
        List<Schedule> mockSchedules = Collections.singletonList(
                Schedule.builder().id(1L).major("INT").build()
        );
        when(scheduleService.getSchedulesByMajor("INT")).thenReturn(mockSchedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesByMajor("INT");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // =============================================================================
    // TEST CASES: getSchedulesByStudentYear
    // =============================================================================

    /**
     * Test Case ID: TKB146
     * Purpose: Verify getSchedulesByStudentYear() returns schedules for year
     * Input: studentYear="2024"
     * Expected Output: ResponseEntity with schedules for year
     * CheckDB: Database read
     */
    @Test
    @DisplayName("TKB146: getSchedulesByStudentYear() returns year schedules")
    public void test_getSchedulesByStudentYear_returnsSchedules() {
        // Arrange
        List<Schedule> mockSchedules = Collections.singletonList(
                Schedule.builder().id(1L).studentYear("2024").build()
        );
        when(scheduleService.getSchedulesByStudentYear("2024")).thenReturn(mockSchedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesByStudentYear("2024");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // =============================================================================
    // TEST CASES: deleteSchedule
    // =============================================================================

    /**
     * Test Case ID: TKB147
     * Purpose: Verify deleteSchedule() deletes schedule by ID
     * Input: id=1
     * Expected Output: ResponseEntity with success message
     * CheckDB: Database delete (requires transaction rollback)
     */
    @Test
    @DisplayName("TKB147: deleteSchedule() deletes successfully")
    public void test_deleteSchedule_deletesSuccessfully() {
        // Arrange
        doNothing().when(scheduleService).deleteScheduleById(1L);

        // Act
        ResponseEntity<String> response = scheduleController.deleteSchedule(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đã xóa lịch học!", response.getBody());
        verify(scheduleService).deleteScheduleById(1L);
    }

    /**
     * Test Case ID: TKB148
     * Purpose: Verify deleteSchedule() throws for invalid ID
     * Input: id=0
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB148: deleteSchedule() throws for invalid ID")
    public void test_deleteSchedule_invalidId_throwsException() {
        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.deleteSchedule(0L);
        }, "Invalid ID should throw InvalidDataException");
    }

    /**
     * Test Case ID: TKB149
     * Purpose: Verify deleteSchedule() throws for null ID
     * Input: id=null
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB149: deleteSchedule() throws for null ID")
    public void test_deleteSchedule_nullId_throwsException() {
        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.deleteSchedule(null);
        }, "Null ID should throw InvalidDataException");
    }

    // =============================================================================
    // TEST CASES: deleteAllSchedules
    // =============================================================================

    /**
     * Test Case ID: TKB150
     * Purpose: Verify deleteAllSchedules() deletes all schedules
     * Input: None
     * Expected Output: ResponseEntity with success message
     * CheckDB: Database delete all (requires transaction rollback)
     */
    @Test
    @DisplayName("TKB150: deleteAllSchedules() deletes all")
    public void test_deleteAllSchedules_deletesAll() {
        // Arrange
        doNothing().when(scheduleService).deleteAllSchedules();

        // Act
        ResponseEntity<String> response = scheduleController.deleteAllSchedules();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đã xóa toàn bộ lịch học!", response.getBody());
        verify(scheduleService).deleteAllSchedules();
    }

    // =============================================================================
    // TEST CASES: generateSchedule
    // =============================================================================

    /**
     * Test Case ID: TKB151
     * Purpose: Verify generateSchedule() generates TKB batch
     * Input: Valid TKBBatchRequest
     * Expected Output: ResponseEntity with TKBBatchResponse
     * CheckDB: No database write
     */
    @Test
    @DisplayName("TKB151: generateSchedule() generates batch successfully")
    public void test_generateSchedule_generatesSuccessfully() {
        // Arrange: Setup mock request and response
        TKBRequest request = TKBRequest.builder()
                .ma_mon("INT1306")
                .ten_mon("Nhập môn lập trình")
                .sotiet(30)
                .siso(100)
                .build();
        TKBBatchRequest batchRequest = TKBBatchRequest.builder()
                .items(Collections.singletonList(request))
                .build();
        
        TKBBatchResponse mockResponse = TKBBatchResponse.builder()
                .totalRows(1)
                .totalClasses(1)
                .note("Generated successfully")
                .build();
        
        when(scheduleService.generateSchedule(any(TKBBatchRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<TKBBatchResponse> response = scheduleController.generateSchedule(batchRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalRows());
    }

    /**
     * Test Case ID: TKB152
     * Purpose: Verify generateSchedule() throws for null/empty request
     * Input: null TKBBatchRequest
     * Expected Output: InvalidDataException thrown
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB152: generateSchedule() throws for empty request")
    public void test_generateSchedule_emptyRequest_throwsException() {
        // Arrange: Empty items
        TKBBatchRequest emptyRequest = TKBBatchRequest.builder()
                .items(Collections.emptyList())
                .build();

        // Act & Assert
        assertThrows(InvalidDataException.class, () -> {
            scheduleController.generateSchedule(emptyRequest);
        }, "Empty items should throw InvalidDataException");
    }

    // =============================================================================
    // TEST CASES: health check
    // =============================================================================

    /**
     * Test Case ID: TKB153
     * Purpose: Verify health() returns OK status
     * Input: None
     * Expected Output: ResponseEntity with "Schedule Controller is OK"
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB153: health() returns OK status")
    public void test_health_returnsOk() {
        // Act
        ResponseEntity<String> response = scheduleController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Schedule Controller is OK", response.getBody());
    }

    // =============================================================================
    // TEST CASES: testData
    // =============================================================================

    /**
     * Test Case ID: TKB154
     * Purpose: Verify testData() returns template data info
     * Input: None
     * Expected Output: ResponseEntity with template data count
     * CheckDB: No database write
     */
    @Test
    @DisplayName("TKB154: testData() returns template data info")
    public void test_testData_returnsTemplateInfo() {
        // Arrange
        List<DataLoaderService.TKBTemplateRow> mockData = Arrays.asList(
                mock(DataLoaderService.TKBTemplateRow.class),
                mock(DataLoaderService.TKBTemplateRow.class)
        );
        when(dataLoaderService.loadTemplateData()).thenReturn(mockData);

        // Act
        ResponseEntity<Map<String, Object>> response = scheduleController.testData();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(2, response.getBody().get("template_rows_count"));
    }

    // =============================================================================
    // TEST CASES: resetState
    // =============================================================================

    /**
     * Test Case ID: TKB155
     * Purpose: Verify resetState() resets scheduling state
     * Input: None
     * Expected Output: ResponseEntity with success status
     * CheckDB: No database access
     */
    @Test
    @DisplayName("TKB155: resetState() resets successfully")
    public void test_resetState_resetsSuccessfully() {
        // Arrange
        doNothing().when(scheduleService).resetState();

        // Act
        ResponseEntity<Map<String, Object>> response = scheduleController.resetState();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        verify(scheduleService).resetState();
    }

    // =============================================================================
    // TEST CASES: saveLastSlotIdxToRedis
    // =============================================================================

    /**
     * Test Case ID: TKB156
     * Purpose: Verify saveLastSlotIdxToRedis() saves slot index
     * Input: userId, academicYear, semester
     * Expected Output: ResponseEntity with success
     * CheckDB: Redis write (requires cleanup after test)
     */
    @Test
    @DisplayName("TKB156: saveLastSlotIdxToRedis() saves successfully")
    public void test_saveLastSlotIdxToRedis_savesSuccessfully() {
        // Arrange
        doNothing().when(scheduleService).commitSessionToRedis(anyLong(), anyString(), anyString());

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> response = 
                scheduleController.saveLastSlotIdxToRedis(1L, "2024-2025", "Học kỳ 1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(scheduleService).commitSessionToRedis(1L, "2024-2025", "Học kỳ 1");
    }

    // =============================================================================
    // TEST CASES: resetLastSlotIdxRedis
    // =============================================================================

    /**
     * Test Case ID: TKB157
     * Purpose: Verify resetLastSlotIdxRedis() resets slot index
     * Input: userId, academicYear, semester
     * Expected Output: ResponseEntity with success
     * CheckDB: Redis delete (requires verification after test)
     */
    @Test
    @DisplayName("TKB157: resetLastSlotIdxRedis() resets successfully")
    public void test_resetLastSlotIdxRedis_resetsSuccessfully() {
        // Arrange
        doNothing().when(scheduleService).resetLastSlotIndexRedis(anyLong(), anyString(), anyString());

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> response = 
                scheduleController.resetLastSlotIdxRedis(1L, "2024-2025", "Học kỳ 1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(scheduleService).resetLastSlotIndexRedis(1L, "2024-2025", "Học kỳ 1");
    }
}
