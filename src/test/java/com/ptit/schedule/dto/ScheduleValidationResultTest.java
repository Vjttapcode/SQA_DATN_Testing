package com.ptit.schedule.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: HK23
 * File Under Test: ScheduleValidationResult.java
 * Module: Hậu Kiểm (Schedule Validation)
 * Description: Unit tests for ScheduleValidationResult DTO
 * ===============================================================================
 */
class ScheduleValidationResultTest {

    /**
     * Test Case ID: HK23
     * Purpose: Kiểm tra hasConflicts()=false khi conflictResult null
     * Input: null conflictResult
     * Expected Output: false
     */
    @Test
    void test_hasConflicts_nullResult() {
        ScheduleValidationResult result = ScheduleValidationResult.builder()
                .scheduleEntries(List.of()).fileName("test.xlsx").totalEntries(0).fileSize(1024).build();
        assertThat(result.hasConflicts()).isFalse();
    }

    /**
     * Test Case ID: HK24
     * Purpose: Kiểm tra hasConflicts()=false khi không có conflict
     * Input: conflictResult có totalConflicts=0
     * Expected Output: false
     */
    @Test
    void test_hasConflicts_noConflict() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(new ArrayList<>()).teacherConflicts(new ArrayList<>()).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder()
                .conflictResult(conflictResult).build();
        assertThat(result.hasConflicts()).isFalse();
    }

    /**
     * Test Case ID: HK25
     * Purpose: Kiểm tra hasConflicts()=true khi có room conflict
     * Input: 1 RoomConflict
     * Expected Output: true
     */
    @Test
    void test_hasConflicts_roomConflict() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(List.of(ConflictResult.RoomConflict.builder().room("401").build()))
                .teacherConflicts(new ArrayList<>()).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder()
                .conflictResult(conflictResult).fileName("test.xlsx").totalEntries(1).fileSize(2048).build();
        assertThat(result.hasConflicts()).isTrue();
    }

    /**
     * Test Case ID: HK26
     * Purpose: Kiểm tra hasConflicts()=true khi có teacher conflict
     * Input: 1 TeacherConflict
     * Expected Output: true
     */
    @Test
    void test_hasConflicts_teacherConflict() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(List.of(ConflictResult.TeacherConflict.builder().teacherId("GV01").build())).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder().conflictResult(conflictResult).build();
        assertThat(result.hasConflicts()).isTrue();
    }

    /**
     * Test Case ID: HK27
     * Purpose: Kiểm tra getRoomConflictCount()=0 khi list null
     * Input: null
     * Expected Output: 0
     */
    @Test
    void test_roomConflictCount_nullList() {
        ConflictResult conflictResult = ConflictResult.builder().roomConflicts(null).teacherConflicts(new ArrayList<>()).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder().conflictResult(conflictResult).build();
        assertThat(result.getRoomConflictCount()).isEqualTo(0);
    }

    /**
     * Test Case ID: HK28
     * Purpose: Kiểm tra getRoomConflictCount() đếm đúng số lượng
     * Input: 3 RoomConflict entries
     * Expected Output: getRoomConflictCount()==3
     */
    @Test
    void test_roomConflictCount_correct() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(List.of(
                        ConflictResult.RoomConflict.builder().room("401").build(),
                        ConflictResult.RoomConflict.builder().room("402").build(),
                        ConflictResult.RoomConflict.builder().room("403").build()))
                .teacherConflicts(new ArrayList<>()).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder().conflictResult(conflictResult).build();
        assertThat(result.getRoomConflictCount()).isEqualTo(3);
    }

    /**
     * Test Case ID: HK29
     * Purpose: Kiểm tra getTeacherConflictCount()=0 khi list null
     * Input: null
     * Expected Output: 0
     */
    @Test
    void test_teacherConflictCount_nullList() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(new ArrayList<>()).teacherConflicts(null).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder().conflictResult(conflictResult).build();
        assertThat(result.getTeacherConflictCount()).isEqualTo(0);
    }

    /**
     * Test Case ID: HK30
     * Purpose: Kiểm tra getTeacherConflictCount() đếm đúng số lượng
     * Input: 2 TeacherConflict entries
     * Expected Output: getTeacherConflictCount()==2
     */
    @Test
    void test_teacherConflictCount_correct() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(List.of(
                        ConflictResult.TeacherConflict.builder().teacherId("GV01").build(),
                        ConflictResult.TeacherConflict.builder().teacherId("GV02").build()))
                .build();
        ScheduleValidationResult result = ScheduleValidationResult.builder().conflictResult(conflictResult).build();
        assertThat(result.getTeacherConflictCount()).isEqualTo(2);
    }

    /**
     * Test Case ID: HK31
     * Purpose: Kiểm tra getFormattedFileSize() với 0 bytes
     * Input: 0
     * Expected Output: "0 Bytes"
     */
    @Test
    void test_fileSize_zero() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(0).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("0 Bytes");
    }

    /**
     * Test Case ID: HK32
     * Purpose: Kiểm tra getFormattedFileSize() format Bytes
     * Input: 500
     * Expected Output: "500.00 Bytes"
     */
    @Test
    void test_fileSize_bytes() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(500).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("500.00 Bytes");
    }

    /**
     * Test Case ID: HK33
     * Purpose: Kiểm tra getFormattedFileSize() format KB
     * Input: 1024
     * Expected Output: "1.00 KB"
     */
    @Test
    void test_fileSize_kilobytes() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(1024).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("1.00 KB");
    }

    /**
     * Test Case ID: HK34
     * Purpose: Kiểm tra getFormattedFileSize() format MB
     * Input: 1024*1024
     * Expected Output: "1.00 MB"
     */
    @Test
    void test_fileSize_megabytes() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(1024L * 1024).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("1.00 MB");
    }

    /**
     * Test Case ID: HK35
     * Purpose: Kiểm tra getFormattedFileSize() format GB
     * Input: 1024^3
     * Expected Output: "1.00 GB"
     */
    @Test
    void test_fileSize_gigabytes() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(1024L * 1024 * 1024).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("1.00 GB");
    }

    /**
     * Test Case ID: HK36
     * Purpose: Kiểm tra getFormattedFileSize() xử lý giá trị > 1KB
     * Input: 2048
     * Expected Output: "2.00 KB"
     */
    @Test
    void test_fileSize_overKB() {
        ScheduleValidationResult result = ScheduleValidationResult.builder().fileSize(2048).build();
        assertThat(result.getFormattedFileSize()).isEqualTo("2.00 KB");
    }

    /**
     * Test Case ID: HK37
     * Purpose: Kiểm tra builder với đầy đủ thuộc tính
     * Input: ScheduleValidationResult với đầy đủ fields
     * Expected Output: Tất cả thuộc tính được set đúng
     */
    @Test
    void test_builder_allProperties() {
        ScheduleEntry entry = ScheduleEntry.builder().subjectCode("INT1306").subjectName("Testing").build();
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(new ArrayList<>()).teacherConflicts(new ArrayList<>()).build();
        ScheduleValidationResult result = ScheduleValidationResult.builder()
                .conflictResult(conflictResult).scheduleEntries(List.of(entry))
                .fileName("schedule.xlsx").totalEntries(1).fileSize(2048).build();
        assertThat(result.getScheduleEntries()).hasSize(1);
        assertThat(result.getFileName()).isEqualTo("schedule.xlsx");
    }

    /**
     * Test Case ID: HK38
     * Purpose: Kiểm tra tích hợp hasConflicts() và các method đếm
     * Input: 1 room + 2 teacher conflicts
     * Expected Output: hasConflicts()=true, room=1, teacher=2
     */
    @Test
    void test_integration_conflictsAndCounts() {
        ConflictResult conflictResult = ConflictResult.builder()
                .roomConflicts(List.of(ConflictResult.RoomConflict.builder().room("401").build()))
                .teacherConflicts(List.of(
                        ConflictResult.TeacherConflict.builder().teacherId("GV01").build(),
                        ConflictResult.TeacherConflict.builder().teacherId("GV02").build()))
                .build();
        ScheduleValidationResult result = ScheduleValidationResult.builder()
                .conflictResult(conflictResult).totalEntries(10).fileSize(4096).build();
        assertThat(result.hasConflicts()).isTrue();
        assertThat(result.getRoomConflictCount()).isEqualTo(1);
        assertThat(result.getTeacherConflictCount()).isEqualTo(2);
    }
}
