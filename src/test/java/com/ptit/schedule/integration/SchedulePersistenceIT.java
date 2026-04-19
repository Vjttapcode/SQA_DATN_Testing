package com.ptit.schedule.integration;

import com.ptit.schedule.controller.ScheduleController;
import com.ptit.schedule.dto.SaveScheduleRequest;
import com.ptit.schedule.entity.Faculty;
import com.ptit.schedule.entity.Major;
import com.ptit.schedule.entity.Room;
import com.ptit.schedule.entity.Schedule;
import com.ptit.schedule.entity.Semester;
import com.ptit.schedule.entity.Subject;
import com.ptit.schedule.entity.TKBTemplate;
import com.ptit.schedule.entity.User;
import com.ptit.schedule.repository.FacultyRepository;
import com.ptit.schedule.repository.MajorRepository;
import com.ptit.schedule.repository.RoomRepository;
import com.ptit.schedule.repository.ScheduleRepository;
import com.ptit.schedule.repository.SemesterRepository;
import com.ptit.schedule.repository.SubjectRepository;
import com.ptit.schedule.repository.TKBTemplateRepository;
import com.ptit.schedule.repository.UserRepository;
import com.ptit.schedule.service.RedisService;
import com.ptit.schedule.testsupport.AuthenticationTestFactory;
import com.ptit.schedule.testsupport.ScheduleFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SchedulePersistenceIT {

    @Autowired
    private ScheduleController scheduleController;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TKBTemplateRepository tkbTemplateRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private MajorRepository majorRepository;

    @MockBean
    private RedisService redisService;

    @AfterEach
    void clearSecurityContext() {
        AuthenticationTestFactory.clearAuthentication();
    }

    @Test
    void saveBatch_shouldPersistScheduleInDatabaseWithinTransaction() {
        // Test Case ID: TKB-DB-001
        String suffix = ScheduleFixtureFactory.nextSuffix("schedule_db");

        Semester semester = semesterRepository.save(ScheduleFixtureFactory.createSemester(suffix));
        Faculty faculty = facultyRepository.save(ScheduleFixtureFactory.createFaculty(suffix));
        Major major = majorRepository.save(ScheduleFixtureFactory.createMajor(suffix, faculty));
        Subject subject = subjectRepository.save(ScheduleFixtureFactory.createSubject(suffix, major, semester));
        TKBTemplate template = tkbTemplateRepository.save(ScheduleFixtureFactory.createTemplate(suffix, semester, 14));
        Room room = roomRepository.save(ScheduleFixtureFactory.createRoom(suffix));
        User user = userRepository.save(AuthenticationTestFactory.createUser(suffix));

        long baselineCount = scheduleRepository.count();
        AuthenticationTestFactory.setAuthentication(user);

        ResponseEntity<String> response = scheduleController.saveSchedule(List.of(SaveScheduleRequest.builder()
                .subjectId(subject.getId())
                .templateDatabaseId(template.getId())
                .classNumber(1)
                .studentYear("D21")
                .major(major.getMajorCode())
                .specialSystem("CQ")
                .siSoMotLop(60)
                .roomNumber(room.getName() + "-" + room.getBuilding())
                .build()));

        List<Schedule> savedSchedules = scheduleRepository.findByUserIdOrderByIdAsc(user.getId());

        assertThat(response.getBody()).contains("Đã lưu TKB");
        assertThat(scheduleRepository.count()).isEqualTo(baselineCount + 1);
        assertThat(savedSchedules).hasSize(1);
        assertThat(savedSchedules.get(0).getSubject().getId()).isEqualTo(subject.getId());
        assertThat(savedSchedules.get(0).getTkbTemplate().getId()).isEqualTo(template.getId());
        assertThat(savedSchedules.get(0).getRoom().getId()).isEqualTo(room.getId());
        verify(redisService).saveLastSlotIdx(user.getId(), semester.getAcademicYear(), semester.getSemesterName(), -1);
    }

    @Test
    void deleteSchedule_shouldRemoveOnlyTargetRecordWithinTransaction() {
        // Test Case ID: TKB-DB-002
        String suffix = ScheduleFixtureFactory.nextSuffix("schedule_delete");

        Semester semester = semesterRepository.save(ScheduleFixtureFactory.createSemester(suffix));
        Faculty faculty = facultyRepository.save(ScheduleFixtureFactory.createFaculty(suffix));
        Major major = majorRepository.save(ScheduleFixtureFactory.createMajor(suffix, faculty));
        Subject subject = subjectRepository.save(ScheduleFixtureFactory.createSubject(suffix, major, semester));
        TKBTemplate template = tkbTemplateRepository.save(ScheduleFixtureFactory.createTemplate(suffix, semester, 14));
        Room room = roomRepository.save(ScheduleFixtureFactory.createRoom(suffix));
        User user = userRepository.save(AuthenticationTestFactory.createUser(suffix));

        Schedule schedule = scheduleRepository.save(ScheduleFixtureFactory.createSchedule(
                subject,
                user,
                template,
                room,
                1,
                major.getMajorCode()));

        long baselineCount = scheduleRepository.count();

        ResponseEntity<String> response = scheduleController.deleteSchedule(schedule.getId());

        assertThat(response.getBody()).contains("Đã xóa lịch học");
        assertThat(scheduleRepository.count()).isEqualTo(baselineCount - 1);
        assertThat(scheduleRepository.findById(schedule.getId())).isEmpty();
    }
}
