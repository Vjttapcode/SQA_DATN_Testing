package com.ptit.schedule.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: HK13
 * File Under Test: ScheduleEntry.java
 * Module: Hậu Kiểm (Schedule Validation)
 * Description: Unit tests for ScheduleEntry DTO
 * ===============================================================================
 */
class ScheduleEntryTest {

    /**
     * Test Case ID: HK13
     * Purpose: Kiểm tra getDisplayInfo() format "mã - tên (GV)"
     * Input: ScheduleEntry với code="INT1306", name, teacher
     * Expected Output: getDisplayInfo()=="INT1306 - ..."
     */
    @Test
    void test_displayInfo_format() {
        ScheduleEntry entry = ScheduleEntry.builder()
                .subjectCode("INT1306").subjectName("Kiểm thử phần mềm").teacherName("Nguyễn Văn A").build();
        assertThat(entry.getDisplayInfo()).isEqualTo("INT1306 - Kiểm thử phần mềm (Nguyễn Văn A)");
    }

    /**
     * Test Case ID: HK14
     * Purpose: Kiểm tra builder với đầy đủ thuộc tính
     * Input: ScheduleEntry với đầy đủ thuộc tính
     * Expected Output: Tất cả thuộc tính được set đúng
     */
    @Test
    void test_builder_allProperties() {
        List<ScheduleEntry.TimeSlot> timeSlots = List.of(
                ScheduleEntry.TimeSlot.builder()
                        .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build()
        );
        ScheduleEntry entry = ScheduleEntry.builder()
                .subjectCode("INT1306").subjectName("Software Testing").teacherId("GV01")
                .teacherName("Teacher One").room("401 - A1").building("A1")
                .classGroup("CLC-01").studentCount(60).timeSlots(timeSlots).build();
        assertThat(entry.getSubjectCode()).isEqualTo("INT1306");
        assertThat(entry.getStudentCount()).isEqualTo(60);
        assertThat(entry.getTimeSlots()).hasSize(1);
    }

    /**
     * Test Case ID: HK15
     * Purpose: Kiểm tra builder cho phép null các trường optional
     * Input: ScheduleEntry với chỉ code, name
     * Expected Output: null cho optional
     */
    @Test
    void test_builder_nullValues() {
        ScheduleEntry entry = ScheduleEntry.builder()
                .subjectCode("INT1306").subjectName("Software Testing").build();
        assertThat(entry.getTeacherId()).isNull();
        assertThat(entry.getRoom()).isNull();
    }

    /**
     * Test Case ID: HK16
     * Purpose: Kiểm tra TimeSlot.getSlotKey() format
     * Input: TimeSlot(week=1, Thứ 2, kíp 1, tiết 1, 3 tiết)
     * Expected Output: getSlotKey()=="Tuần 1-Thứ 2-1-1-3"
     */
    @Test
    void test_slotKey_format() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        assertThat(timeSlot.getSlotKey()).isEqualTo("Tuần 1-Thứ 2-1-1-3");
    }

    /**
     * Test Case ID: HK17
     * Purpose: Kiểm tra TimeSlot.getDisplayInfo() format dễ đọc
     * Input: TimeSlot(week=5, Thứ 6, kíp 2, tiết 4, 2 tiết)
     * Expected Output: getDisplayInfo()=="Tuần 5 (Thứ 6) - ..."
     */
    @Test
    void test_slotDisplayInfo_format() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 5").dayOfWeek("Thứ 6").shift("2").startPeriod("4").numberOfPeriods("2").build();
        assertThat(timeSlot.getDisplayInfo()).isEqualTo("Tuần 5 (Thứ 6) - Kíp 2 - Tiết 4 (2 tiết)");
    }

    /**
     * Test Case ID: HK18
     * Purpose: Kiểm tra getSlotKey() phân biệt các slot khác nhau
     * Input: TimeSlot1(Thứ 2) vs TimeSlot2(Thứ 3)
     * Expected Output: slotKey1!=slotKey2
     */
    @Test
    void test_slotKey_different() {
        ScheduleEntry.TimeSlot slot1 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ScheduleEntry.TimeSlot slot2 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 3").shift("1").startPeriod("1").numberOfPeriods("3").build();
        assertThat(slot1.getSlotKey()).isNotEqualTo(slot2.getSlotKey());
    }

    /**
     * Test Case ID: HK19
     * Purpose: Kiểm tra getSlotKey() tạo key giống nhau cho slot cùng thời gian
     * Input: 2 TimeSlot cùng thông số
     * Expected Output: slotKey1==slotKey2
     */
    @Test
    void test_slotKey_same() {
        ScheduleEntry.TimeSlot slot1 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ScheduleEntry.TimeSlot slot2 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        assertThat(slot1.getSlotKey()).isEqualTo(slot2.getSlotKey());
    }

    /**
     * Test Case ID: HK20
     * Purpose: Kiểm tra TimeSlot xử lý đúng các giá trị kíp, tiết khác nhau
     * Input: TimeSlot(shift=3, tiết 7, 4 tiết)
     * Expected Output: getShift()=="3", getStartPeriod()=="7"
     */
    @Test
    void test_timeSlot_differentValues() {
        ScheduleEntry.TimeSlot timeSlot = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 10").dayOfWeek("Thứ 7").shift("3").startPeriod("7").numberOfPeriods("4").build();
        assertThat(timeSlot.getShift()).isEqualTo("3");
        assertThat(timeSlot.getStartPeriod()).isEqualTo("7");
        assertThat(timeSlot.getDisplayInfo()).contains("Kíp 3");
    }

    /**
     * Test Case ID: HK21
     * Purpose: Kiểm tra ScheduleEntry chứa nhiều TimeSlot
     * Input: ScheduleEntry với 5 timeSlots
     * Expected Output: getTimeSlots().size()==5
     */
    @Test
    void test_entry_multipleTimeSlots() {
        List<ScheduleEntry.TimeSlot> timeSlots = new ArrayList<>();
        for (int week = 1; week <= 5; week++) {
            timeSlots.add(ScheduleEntry.TimeSlot.builder()
                    .date("Tuần " + week).dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build());
        }
        ScheduleEntry entry = ScheduleEntry.builder().subjectCode("INT1306").subjectName("Testing").timeSlots(timeSlots).build();
        assertThat(entry.getTimeSlots()).hasSize(5);
    }

    /**
     * Test Case ID: HK22
     * Purpose: Kiểm tra TimeSlot equals và hashCode hoạt động đúng
     * Input: 2 TimeSlot cùng thông số
     * Expected Output: equals()==true, hashCode() bằng nhau
     */
    @Test
    void test_timeSlot_equals() {
        ScheduleEntry.TimeSlot slot1 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        ScheduleEntry.TimeSlot slot2 = ScheduleEntry.TimeSlot.builder()
                .date("Tuần 1").dayOfWeek("Thứ 2").shift("1").startPeriod("1").numberOfPeriods("3").build();
        assertThat(slot1).isEqualTo(slot2);
        assertThat(slot1.hashCode()).isEqualTo(slot2.hashCode());
    }
}
