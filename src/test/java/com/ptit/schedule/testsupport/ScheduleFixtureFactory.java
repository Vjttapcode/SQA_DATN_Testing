package com.ptit.schedule.testsupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.schedule.entity.Faculty;
import com.ptit.schedule.entity.Major;
import com.ptit.schedule.entity.Room;
import com.ptit.schedule.entity.RoomStatus;
import com.ptit.schedule.entity.RoomType;
import com.ptit.schedule.entity.Schedule;
import com.ptit.schedule.entity.Semester;
import com.ptit.schedule.entity.Subject;
import com.ptit.schedule.entity.TKBTemplate;
import com.ptit.schedule.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class ScheduleFixtureFactory {

    private static final AtomicLong SEQUENCE = new AtomicLong(1_000L);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ScheduleFixtureFactory() {
    }

    public static String nextSuffix(String prefix) {
        return prefix + "_" + SEQUENCE.incrementAndGet();
    }

    public static Faculty createFaculty(String suffix) {
        Faculty faculty = new Faculty();
        faculty.setId("FAC_" + suffix);
        faculty.setFacultyName("Faculty " + suffix);
        return faculty;
    }

    public static Semester createSemester(String suffix) {
        return Semester.builder()
                .semesterName("HK" + (SEQUENCE.get() % 2 == 0 ? "1" : "2") + "_" + suffix)
                .academicYear("2099-2100")
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 6, 1))
                .isActive(false)
                .description("Semester " + suffix)
                .build();
    }

    public static Major createMajor(String suffix, Faculty faculty) {
        return Major.builder()
                .majorCode("MAJ_" + suffix)
                .classYear("K" + suffix)
                .majorName("Major " + suffix)
                .numberOfStudents(120)
                .faculty(faculty)
                .build();
    }

    public static Subject createSubject(String suffix, Major major, Semester semester) {
        return Subject.builder()
                .subjectCode("SUB_" + suffix)
                .subjectName("Subject " + suffix)
                .studentsPerClass(60)
                .numberOfClasses(2)
                .credits(3)
                .theoryHours(30)
                .exerciseHours(0)
                .projectHours(0)
                .labHours(0)
                .selfStudyHours(0)
                .department("Department " + suffix)
                .programType("CQ")
                .major(major)
                .semester(semester)
                .isCommon(false)
                .build();
    }

    public static TKBTemplate createTemplate(String suffix, Semester semester, int totalPeriods) {
        return TKBTemplate.builder()
                .templateId("TPL_" + suffix)
                .totalPeriods(totalPeriods)
                .dayOfWeek(2)
                .kip(1)
                .startPeriod(1)
                .periodLength(totalPeriods == 60 ? 3 : 2)
                .weekSchedule(toWeekScheduleJson(List.of(1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0)))
                .totalUsed(totalPeriods == 60 ? 21 : 14)
                .semester(semester)
                .rowOrder(1)
                .build();
    }

    public static Room createRoom(String suffix) {
        return Room.builder()
                .name("R" + suffix)
                .building("A" + suffix.substring(Math.max(0, suffix.length() - 1)))
                .capacity(80)
                .type(RoomType.GENERAL)
                .status(RoomStatus.AVAILABLE)
                .note("Room " + suffix)
                .build();
    }

    public static Schedule createSchedule(
            Subject subject,
            User user,
            TKBTemplate template,
            Room room,
            int classNumber,
            String majorCode) {
        return Schedule.builder()
                .subject(subject)
                .classNumber(classNumber)
                .studentYear("D" + classNumber)
                .major(majorCode)
                .specialSystem("CQ")
                .siSoMotLop(60)
                .room(room)
                .user(user)
                .tkbTemplate(template)
                .build();
    }

    private static String toWeekScheduleJson(List<Integer> weeks) {
        try {
            return OBJECT_MAPPER.writeValueAsString(weeks);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot build week schedule JSON", exception);
        }
    }
}
