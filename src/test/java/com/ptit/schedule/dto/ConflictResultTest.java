package com.ptit.schedule.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: HK01
 * File Under Test: ConflictResult.java
 * Module: Hậu Kiểm (Schedule Validation)
 * Description: Unit tests for ConflictResult DTO
 * ===============================================================================
 */
class ConflictResultTest {

    /**
     * Test Case ID: HK01
     * Purpose: Kiểm tra getTotalConflicts()=0 khi không có xung đột
     * Input: ConflictResult với danh sách rỗng
     * Expected Output: getTotalConflicts()==0
     */
    @Test
    void test_totalZero_noConflict() {
        ConflictResult result = ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(new ArrayList<>())
                .build();
        assertThat(result.getTotalConflicts()).isEqualTo(0);
    }

    /**
     * Test Case ID: HK02
     * Purpose: Kiểm tra getTotalConflicts() đếm đúng số room conflicts
     * Input: ConflictResult với 1 room conflict (2 tuần)
     * Expected Output: getTotalConflicts()==2
     */
    @Test
    void test_totalCount_onlyRoom() {
        List<ConflictResult.RoomConflict> roomConflicts = List.of(
                ConflictResult.RoomConflict.builder().room("401").build(),
                ConflictResult.RoomConflict.builder().room("402").build()
        );
        ConflictResult result = ConflictResult.builder()
                .roomConflicts(roomConflicts)
                .teacherConflicts(new ArrayList<>())
                .build();
        assertThat(result.getTotalConflicts()).isEqualTo(2);
    }

    /**
     * Test Case ID: HK03
     * Purpose: Kiểm tra getTotalConflicts() đếm đúng teacher conflicts
     * Input: ConflictResult với 1 teacher conflict
     * Expected Output: getTotalConflicts()==1
     */
    @Test
    void test_totalCount_onlyTeacher() {
        List<ConflictResult.TeacherConflict> teacherConflicts = List.of(
                ConflictResult.TeacherConflict.builder().teacherId("GV01").build()
        );
        ConflictResult result = ConflictResult.builder()
                .roomConflicts(new ArrayList<>())
                .teacherConflicts(teacherConflicts)
                .build();
        assertThat(result.getTotalConflicts()).isEqualTo(1);
    }

    /**
     * Test Case ID: HK04
     * Purpose: Kiểm tra getTotalConflicts() tổng hợp cả 2 loại conflict
     * Input: ConflictResult với roomConflicts + teacherConflicts
     * Expected Output: getTotalConflicts()==3 (Tổng=2+1)
     */
    @Test
    void test_totalCount_bothType() {
        List<ConflictResult.RoomConflict> roomConflicts = List.of(
                ConflictResult.RoomConflict.builder().room("401").build()
        );
        List<ConflictResult.TeacherConflict> teacherConflicts = List.of(
                ConflictResult.TeacherConflict.builder().teacherId("GV01").build(),
                ConflictResult.TeacherConflict.builder().teacherId("GV02").build()
        );
        ConflictResult result = ConflictResult.builder()
                .roomConflicts(roomConflicts)
                .teacherConflicts(teacherConflicts)
                .build();
        assertThat(result.getTotalConflicts()).isEqualTo(3);
    }

    /**
     * Test Case ID: HK05
     * Purpose: Kiểm tra getTotalConflicts() xử lý null an toàn
     * Input: ConflictResult với null lists
     * Expected Output: getTotalConflicts()==0, không throw NullPointerException
     */
    @Test
    void test_totalZero_nullLists() {
        ConflictResult result = ConflictResult.builder()
                .roomConflicts(null)
                .teacherConflicts(null)
                .build();
        assertThat(result.getTotalConflicts()).isEqualTo(0);
    }

    /**
     * Test Case ID: HK06
     * Purpose: Kiểm tra RoomConflict.getConflictKey() tạo đúng format
     * Input: RoomConflict với room="401", Thứ 2, kíp 1, tiết 1, 3 tiết
     * Expected Output: getConflictKey()=="401-Thứ 2-1-1-3"
     */
    @Test
    void test_roomConflictKey_format() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ConflictResult.RoomConflict conflict = ConflictResult.RoomConflict.builder()
                .room("401").timeSlot(timeSlot).build();
        assertThat(conflict.getConflictKey()).isEqualTo("401-Thứ 2-1-1-3");
    }

    /**
     * Test Case ID: HK07
     * Purpose: Kiểm tra RoomConflict.getConflictDescription() mô tả đúng các tuần
     * Input: RoomConflict với weeks=[1, 3, 5]
     * Expected Output: getConflictDescription() chứa "Tuần 1, 3, 5"
     */
    @Test
    void test_roomConflictDesc_withWeeks() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 5").shift("2").startPeriod("4").numberOfPeriods("2").build();
        ConflictResult.RoomConflict conflict = ConflictResult.RoomConflict.builder()
                .room("302").timeSlot(timeSlot).conflictWeeks(List.of("1", "3", "5")).build();
        String description = conflict.getConflictDescription();
        assertThat(description).contains("Phòng 302");
        assertThat(description).contains("1, 3, 5");
    }

    /**
     * Test Case ID: HK08
     * Purpose: Kiểm tra RoomConflict.getConflictDescription() khi danh sách tuần rỗng
     * Input: RoomConflict với weeks=[]
     * Expected Output: getConflictDescription() không chứa "các tuần:"
     */
    @Test
    void test_roomConflictDesc_emptyWeeks() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ConflictResult.RoomConflict conflict = ConflictResult.RoomConflict.builder()
                .room("401").timeSlot(timeSlot).conflictWeeks(new ArrayList<>()).build();
        String description = conflict.getConflictDescription();
        assertThat(description).contains("Phòng 401");
        assertThat(description).doesNotContain("các tuần:");
    }

    /**
     * Test Case ID: HK09
     * Purpose: Kiểm tra TeacherConflict.getConflictKey() tạo đúng format
     * Input: TeacherConflict với teacherId="GV01", Thứ 3
     * Expected Output: getConflictKey()=="GV01-Thứ 3-1-1-2"
     */
    @Test
    void test_teacherConflictKey_format() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 2").dayOfWeek("Thứ 3").shift("1").startPeriod("1").numberOfPeriods("2").build();
        ConflictResult.TeacherConflict conflict = ConflictResult.TeacherConflict.builder()
                .teacherId("GV01").timeSlot(timeSlot).build();
        assertThat(conflict.getConflictKey()).isEqualTo("GV01-Thứ 3-1-1-2");
    }

    /**
     * Test Case ID: HK10
     * Purpose: Kiểm tra TeacherConflict.getConflictDescription() hiển thị đúng thông tin
     * Input: TeacherConflict với weeks=[2, 4], teacherId="GV05"
     * Expected Output: getConflictDescription() chứa "2, 4" và "GV05"
     */
    @Test
    void test_teacherConflictDesc_withWeeks() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 6").shift("2").startPeriod("6").numberOfPeriods("3").build();
        ConflictResult.TeacherConflict conflict = ConflictResult.TeacherConflict.builder()
                .teacherId("GV05").teacherName("Nguyễn Văn A").timeSlot(timeSlot)
                .conflictWeeks(List.of("2", "4")).build();
        String description = conflict.getConflictDescription();
        assertThat(description).contains("Nguyễn Văn A");
        assertThat(description).contains("GV05");
        assertThat(description).contains("2, 4");
    }

    /**
     * Test Case ID: HK11
     * Purpose: Kiểm tra TeacherConflict.getConflictDescription() xử lý null weeks
     * Input: TeacherConflict với weeks=null
     * Expected Output: Không throw NullPointerException, mô tả mặc định
     */
    @Test
    void test_teacherConflictDesc_nullWeeks() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("2").build();
        ConflictResult.TeacherConflict conflict = ConflictResult.TeacherConflict.builder()
                .teacherId("GV01").teacherName("Test Teacher").timeSlot(timeSlot).conflictWeeks(null).build();
        String description = conflict.getConflictDescription();
        assertThat(description).contains("GV01");
        assertThat(description).doesNotContain("NullPointerException");
    }

    /**
     * Test Case ID: HK12
     * Purpose: Kiểm tra builder tạo ConflictResult với đầy đủ thuộc tính
     * Input: ConflictResult.builder() với đầy đủ data
     * Expected Output: Tất cả trường được set đúng
     */
    @Test
    void test_builder_allProperties() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ScheduleEntry entry = ScheduleEntry.builder()
                .subjectCode("INT1306").teacherId("GV01").room("401").build();
        List<ScheduleEntry> conflictingSchedules = List.of(entry);

        ConflictResult.RoomConflict roomConflict = ConflictResult.RoomConflict.builder()
                .room("401").timeSlot(timeSlot).conflictingSchedules(conflictingSchedules)
                .conflictWeeks(List.of("1", "2", "3")).build();
        ConflictResult.TeacherConflict teacherConflict = ConflictResult.TeacherConflict.builder()
                .teacherId("GV01").teacherName("Teacher One").timeSlot(timeSlot)
                .conflictingSchedules(conflictingSchedules).conflictWeeks(List.of("1", "2")).build();

        ConflictResult result = ConflictResult.builder()
                .roomConflicts(List.of(roomConflict)).teacherConflicts(List.of(teacherConflict)).build();

        assertThat(result.getRoomConflicts()).hasSize(1);
        assertThat(result.getTeacherConflicts()).hasSize(1);
        assertThat(result.getTotalConflicts()).isEqualTo(2);
    }
}
