package com.ptit.schedule.controller;

import com.ptit.schedule.dto.ConflictResult;
import com.ptit.schedule.dto.ScheduleEntry;
import com.ptit.schedule.security.JwtTokenProvider;
import com.ptit.schedule.service.ScheduleConflictDetectionService;
import com.ptit.schedule.service.ScheduleExcelReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===============================================================================
 * TEST CASE ID: HK39
 * File Under Test: ScheduleValidationController.java
 * Module: Hậu Kiểm (Schedule Validation)
 * Description: Integration tests for ScheduleValidationController
 * ===============================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
class ScheduleValidationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ScheduleExcelReaderService excelReaderService;

    @MockBean
    private ScheduleConflictDetectionService conflictDetectionService;

    private String validToken;

    private UserDetails mockUser() {
        return User.builder()
                .username("admin@ptit.edu.vn")
                .password("password")
                .roles("ADMIN")
                .build();
    }

    @BeforeEach
    void setUp() {
        validToken = "valid.jwt.token";
        when(jwtTokenProvider.validationToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(validToken)).thenReturn("admin@ptit.edu.vn");
        when(userDetailsService.loadUserByUsername("admin@ptit.edu.vn")).thenReturn(mockUser());
    }

    private MockMultipartFile makeFile() {
        return new MockMultipartFile("file", "schedule.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "fake excel content".getBytes());
    }

    private List<ScheduleEntry> makeEntries() {
        return List.of(
                ScheduleEntry.builder()
                        .subjectCode("INT1306").subjectName("Software Testing")
                        .teacherId("GV01").teacherName("Teacher A")
                        .room("401").timeSlots(new ArrayList<>()).build()
        );
    }

    private ConflictResult noConflicts() {
        return ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(new ArrayList<>())
                .build();
    }

    private ConflictResult withRoomConflict() {
        ScheduleEntry.TimeSlot slot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        return ConflictResult.builder()
                .roomConflicts(List.of(
                        ConflictResult.RoomConflict.builder()
                                .room("401").timeSlot(slot)
                                .conflictingSchedules(new ArrayList<>()).conflictWeeks(new ArrayList<>()).build()
                ))
                .teacherConflicts(new ArrayList<>())
                .build();
    }

    private ConflictResult withTeacherConflict() {
        ScheduleEntry.TimeSlot slot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        return ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(List.of(
                        ConflictResult.TeacherConflict.builder()
                                .teacherId("GV01").timeSlot(slot)
                                .conflictingSchedules(new ArrayList<>()).conflictWeeks(new ArrayList<>()).build()
                ))
                .build();
    }

    /**
     * Test Case ID: HK39
     * Purpose: Kiểm tra validate không có conflicts trả về 200
     * Input: Valid schedule entries
     * Expected Output: status=200, hasConflicts=false
     */
    @Test
    void test_validateSchedule_noConflicts_returns200() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(0));
    }

    /**
     * Test Case ID: HK40
     * Purpose: Kiểm tra validate có room conflict vẫn trả về 200
     * Input: ScheduleEntries với room conflict
     * Expected Output: status=200, hasConflicts=true
     */
    @Test
    void test_validateSchedule_roomConflict_returns200() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(withRoomConflict());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(1));
    }

    /**
     * Test Case ID: HK41
     * Purpose: Kiểm tra validate có teacher conflict trả về 200
     * Input: ScheduleEntries với teacher conflict
     * Expected Output: status=200, hasConflicts=true
     */
    @Test
    void test_validateSchedule_teacherConflict_returns200() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(withTeacherConflict());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(1));
    }

    /**
     * Test Case ID: HK42
     * Purpose: Kiểm tra validate có cả 2 loại conflicts
     * Input: ScheduleEntries với cả 2 loại
     * Expected Output: status=200, hasConflicts=true
     */
    @Test
    void test_validateSchedule_bothConflicts_returns200() throws Exception {
        ScheduleEntry.TimeSlot slot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ConflictResult both = ConflictResult.builder()
                .roomConflicts(List.of(
                        ConflictResult.RoomConflict.builder()
                                .room("401").timeSlot(slot)
                                .conflictingSchedules(new ArrayList<>()).conflictWeeks(new ArrayList<>()).build()
                ))
                .teacherConflicts(List.of(
                        ConflictResult.TeacherConflict.builder()
                                .teacherId("GV01").timeSlot(slot)
                                .conflictingSchedules(new ArrayList<>()).conflictWeeks(new ArrayList<>()).build()
                ))
                .build();
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(both);

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(2));
    }

    /**
     * Test Case ID: HK43
     * Purpose: Kiểm tra validate empty list trả về 200
     * Input: Empty scheduleEntries
     * Expected Output: status=200, totalEntries=0
     */
    @Test
    void test_validateSchedule_emptyList_returns200() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(new ArrayList<>());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Test Case ID: HK44
     * Purpose: Kiểm tra validate single entry trả về 200
     * Input: Single ScheduleEntry
     * Expected Output: status=200, totalEntries=1
     */
    @Test
    void test_validateSchedule_singleEntry_returns200() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(List.of(makeEntries().get(0)));
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Test Case ID: HK45
     * Purpose: Kiểm tra response chứa schedule entries
     * Input: Multiple entries
     * Expected Output: Response chứa entries list
     */
    @Test
    void test_validateSchedule_responseContainsEntries() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.scheduleEntries").isArray());
    }

    /**
     * Test Case ID: HK46
     * Purpose: Kiểm tra response chứa conflict results
     * Input: Entries với conflicts
     * Expected Output: Response chứa conflictResult
     */
    @Test
    void test_validateSchedule_responseContainsConflicts() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(withRoomConflict());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.conflictResult").exists());
    }

    /**
     * Test Case ID: HK47
     * Purpose: Kiểm tra response chứa file info
     * Input: Request với file info
     * Expected Output: Response chứa fileName, fileSize
     */
    @Test
    void test_validateSchedule_responseContainsFileInfo() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileName").value("schedule.xlsx"))
                .andExpect(jsonPath("$.data.fileSize").exists());
    }

    /**
     * Test Case ID: HK48
     * Purpose: Kiểm tra hasConflicts phản ánh thực tế
     * Input: Entries không conflict
     * Expected Output: hasConflicts=false
     */
    @Test
    void test_validateSchedule_hasConflictsReflectsReality() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(0));
    }

    /**
     * Test Case ID: HK49
     * Purpose: Kiểm tra conflict count chính xác
     * Input: Known number of conflicts
     * Expected Output: Conflict count đúng
     */
    @Test
    void test_validateSchedule_conflictCountAccurate() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(withRoomConflict());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(1));
    }

    /**
     * Test Case ID: HK50
     * Purpose: Kiểm tra không auth trả về 401
     * Input: No Authorization header
     * Expected Output: status=401
     */
    @Test
    void test_validateSchedule_noAuth_returns401() throws Exception {
        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test Case ID: HK51
     * Purpose: Kiểm tra invalid token trả về 403
     * Input: Invalid JWT token
     * Expected Output: status=401 (token rejected → security returns 401)
     */
    @Test
    void test_validateSchedule_invalidToken_returns401() throws Exception {
        when(jwtTokenProvider.validationToken("invalid.token")).thenReturn(false);

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test Case ID: HK52
     * Purpose: Kiểm tra response structure đầy đủ
     * Input: Valid request
     * Expected Output: Response có đầy đủ fields
     */
    @Test
    void test_validateSchedule_responseStructure() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * Test Case ID: HK53
     * Purpose: Kiểm tra complete validation report được trả về
     * Input: Valid request
     * Expected Output: Report chứa tất cả thông tin cần thiết
     */
    @Test
    void test_validateSchedule_completeValidationReport() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEntries").exists())
                .andExpect(jsonPath("$.data.scheduleEntries").isArray())
                .andExpect(jsonPath("$.data.conflictResult").exists());
    }

    /**
     * Test Case ID: HK54
     * Purpose: Kiểm tra entries được map đúng
     * Input: Request entries vs response
     * Expected Output: Entries mapped correctly
     */
    @Test
    void test_validateSchedule_entriesMappedCorrectly() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(noConflicts());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.scheduleEntries[0].subjectCode").value("INT1306"));
    }

    /**
     * Test Case ID: HK55
     * Purpose: Kiểm tra conflicts được map đúng
     * Input: Conflict entries vs response
     * Expected Output: Conflicts mapped correctly
     */
    @Test
    void test_validateSchedule_conflictsMappedCorrectly() throws Exception {
        when(excelReaderService.validateScheduleExcelFormat(any())).thenReturn(true);
        when(excelReaderService.readScheduleFromExcel(any())).thenReturn(makeEntries());
        when(conflictDetectionService.detectConflicts(any())).thenReturn(withRoomConflict());

        mockMvc.perform(multipart("/api/schedule-validation/analyze")
                        .file(makeFile())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.conflictResult.totalConflicts").value(1));
    }
}
