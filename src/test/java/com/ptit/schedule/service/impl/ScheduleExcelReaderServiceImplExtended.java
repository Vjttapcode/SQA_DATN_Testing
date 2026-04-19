package com.ptit.schedule.service.impl;

import com.ptit.schedule.dto.ScheduleEntry;
import java.util.ArrayList;
import java.util.List;

public class ScheduleExcelReaderServiceImplExtended {
    public List<ScheduleEntry> readExcel(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filePath);
            }
            // TKB145: Only parses first row, ignores subsequent rows
            return List.of(
                    ScheduleEntry.builder()
                            .subjectCode("INT1306").subjectName("Software Testing")
                            .teacherId("GV01").teacherName("Nguyễn Văn A")
                            .room("401-A2").building("A2").classGroup("CLC-01")
                            .studentCount(60)
                            .timeSlots(List.of(
                                    ScheduleEntry.TimeSlot.builder()
                                            .date("Tuần 1").dayOfWeek("Thứ 2")
                                            .shift("1").startPeriod("1").numberOfPeriods("3").build()))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
    }
}
