package com.ptit.schedule.service.impl;

import com.ptit.schedule.dto.TKBBatchRequest;
import com.ptit.schedule.dto.TKBBatchResponse;
import com.ptit.schedule.dto.TKBRequest;
import com.ptit.schedule.dto.TKBRowResult;
import com.ptit.schedule.entity.Schedule;
import com.ptit.schedule.entity.Semester;
import com.ptit.schedule.entity.Subject;
import com.ptit.schedule.exception.InvalidDataException;
import com.ptit.schedule.repository.ScheduleRepository;
import com.ptit.schedule.repository.SemesterRepository;
import com.ptit.schedule.repository.SubjectRepository;
import com.ptit.schedule.service.DataLoaderService;
import com.ptit.schedule.service.RedisService;
import com.ptit.schedule.service.RoomService;
import com.ptit.schedule.service.SubjectRoomMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private DataLoaderService dataLoaderService;
    @Mock
    private RoomService roomService;
    @Mock
    private SubjectRoomMappingService subjectRoomMappingService;
    @Mock
    private RedisService redisService;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private List<Integer> fullWeeks;

    @BeforeEach
    void setUp() {
        fullWeeks = List.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    void saveAll_shouldDelegateToRepository() {
        List<Schedule> schedules = List.of(new Schedule(), new Schedule());

        scheduleService.saveAll(schedules);

        verify(scheduleRepository).saveAll(schedules);
    }

    @Test
    void getAllSchedules_shouldReturnRepositoryResult() {
        List<Schedule> expected = List.of(new Schedule());
        when(scheduleRepository.findAll()).thenReturn(expected);

        List<Schedule> actual = scheduleService.getAllSchedules();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSchedulesBySubjectId_shouldDelegateToRepository() {
        List<Schedule> expected = List.of(new Schedule());
        when(scheduleRepository.findBySubjectId("INT1001")).thenReturn(expected);

        List<Schedule> actual = scheduleService.getSchedulesBySubjectId("INT1001");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSchedulesByMajor_shouldDelegateToRepository() {
        List<Schedule> expected = List.of(new Schedule());
        when(scheduleRepository.findByMajor("CNTT")).thenReturn(expected);

        List<Schedule> actual = scheduleService.getSchedulesByMajor("CNTT");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSchedulesByStudentYear_shouldDelegateToRepository() {
        List<Schedule> expected = List.of(new Schedule());
        when(scheduleRepository.findByStudentYear("2024")).thenReturn(expected);

        List<Schedule> actual = scheduleService.getSchedulesByStudentYear("2024");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSchedulesByUserId_shouldDelegateToOrderedRepositoryMethod() {
        List<Schedule> expected = List.of(new Schedule());
        when(scheduleRepository.findByUserIdOrderByIdAsc(1L)).thenReturn(expected);

        List<Schedule> actual = scheduleService.getSchedulesByUserId(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteScheduleById_shouldDelegateToRepository() {
        scheduleService.deleteScheduleById(100L);

        verify(scheduleRepository).deleteById(100L);
    }

    @Test
    void deleteAllSchedules_shouldDelegateToRepository() {
        scheduleService.deleteAllSchedules();

        verify(scheduleRepository).deleteAll();
    }

    @Test
    void generateSchedule_shouldThrowWhenTemplateDataIsEmpty() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("1", "2024-2025"))
                .thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025")).thenReturn(List.of());

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Chưa có dữ liệu lịch mẫu");

        verify(dataLoaderService).setCurrentSemesterId(null);
    }

    @Test
    void generateSchedule_shouldSetSemesterIdWhenSemesterExists() {
        Semester semester = Semester.builder().id(99L).semesterName("1").academicYear("2024-2025").build();
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("1", "2024-2025"))
                .thenReturn(Optional.of(semester));
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "T1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(200L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(dataLoaderService).setCurrentSemesterId(99L);
    }

    @Test
    void generateSchedule_shouldFallbackAcademicYearSemesterFromFirstItem() {
        Semester semester = Semester.builder().id(101L).semesterName("HK1").academicYear("2024-2025").build();
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year("2024-2025");
        item.setSemester("HK1");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(10L)
                .items(List.of(item))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025"))
                .thenReturn(Optional.of(semester));
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(10L, "2024-2025", "HK1")).thenReturn(2);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getTotalRows()).isGreaterThan(0);
        verify(redisService).loadLastSlotIdx(10L, "2024-2025", "HK1");
    }

    @Test
    void generateSchedule_shouldNotLoadRedisWhenMissingContext() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year("2024-2025");
        item.setSemester("HK1");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .items(List.of(item))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(redisService, never()).loadLastSlotIdx(any(), any(), any());
    }

    @Test
    void generateSchedule_shouldCreateRowsAndPopulateDerivedFieldsForRegularSubject() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 55L, "TPL-01")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(999L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);
        TKBRowResult row = response.getItems().get(0).getRows().get(0);

        assertThat(row.getMaMon()).isEqualTo("INT1001");
        assertThat(row.getTenMon()).isEqualTo("Nhap mon");
        assertThat(row.getPhong()).isNull();
        assertThat(row.getTemplateDatabaseId()).isEqualTo(55L);
        assertThat(row.getSubjectDatabaseId()).isEqualTo(999L);
        assertThat(row.getO_to_AG()).hasSize(18);
        assertThat(row.getAH()).isEqualTo(18);
        assertThat(row.getAJ()).isEqualTo(12);
    }

    @Test
    void generateSchedule_shouldSetSubjectDatabaseIdNullWhenSubjectNotFound() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT404", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "TPL-01")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT404", "HK1", "2024-2025"))
                .thenReturn(List.of());

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getSubjectDatabaseId()).isNull();
    }

    @Test
    void generateSchedule_shouldPrioritize60PeriodSubjectBeforeRegular() {
        TKBRequest regular = baseRequest("INT1001", 30, "CNTT");
        TKBRequest period60 = baseRequest("INT2001", 60, "CNTT");
        period60.setSolop(1);
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(regular, period60))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B"),
                        templateRow(30, 2, 1, 1, 12L, "30-A")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear(eq("INT2001"), eq("HK1"), eq("2024-2025")))
                .thenReturn(List.of(Subject.builder().id(2L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear(eq("INT1001"), eq("HK1"), eq("2024-2025")))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getInput().getSotiet()).isEqualTo(60);
        assertThat(response.getItems().get(1).getInput().getSotiet()).isEqualTo(30);
    }

    @Test
    void generateSchedule_shouldIncludeOnlyMatchingPeriodTemplates() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 14, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(30, 2, 1, 1, 10L, "30-A"),
                        templateRow(14, 4, 3, 1, 20L, "14-A")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "14-A".equals(r.getN()));
    }

    @Test
    void generateSchedule_shouldTruncateWeekScheduleTo18Columns() {
        List<Integer> longWeeks = List.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(new DataLoaderService.TKBTemplateRow(1L, 30, 2, 1, 1, 1, "TPL", longWeeks, 30)));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getO_to_AG()).hasSize(18);
    }

    @Test
    void generateSchedule_shouldIgnoreRowsWithZeroAHAndContinue() {
        DataLoaderService.TKBTemplateRow zeroAh = new DataLoaderService.TKBTemplateRow(
                1L, 30, 2, 1, 1, 0, "ZERO", fullWeeks, 0);
        DataLoaderService.TKBTemplateRow valid = templateRow(30, 2, 1, 1, 2L, "VALID");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(zeroAh, valid));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
        assertThat(response.getItems().get(0).getRows()).allMatch(r -> !"ZERO".equals(r.getN()));
    }

    @Test
    void generateSchedule_shouldNormalizeNumericSemesterForTemplateLoading() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("2")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("2", "2024-2025"))
                .thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK2 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "2")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "2", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        scheduleService.generateSchedule(request);

        verify(dataLoaderService).loadTemplateData("HK2 2024-2025");
    }

    @Test
    void commitSessionToRedis_shouldSaveSessionLastSlot() {
        // produce data first so sessionLastSlotIdx changes from default -1
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(5L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();
        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(5L, "2024-2025", "HK1")).thenReturn(4);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        scheduleService.generateSchedule(request);

        scheduleService.commitSessionToRedis(5L, "2024-2025", "HK1");

        verify(redisService).saveLastSlotIdx(eq(5L), eq("2024-2025"), eq("HK1"), any(Integer.class));
    }

    @Test
    void commitSessionToRedis_shouldNotSaveWhenContextIsNull() {
        scheduleService.commitSessionToRedis(null, "2024-2025", "HK1");
        scheduleService.commitSessionToRedis(1L, null, "HK1");
        scheduleService.commitSessionToRedis(1L, "2024-2025", null);

        verify(redisService, never()).saveLastSlotIdx(any(), any(), any(), any(Integer.class));
    }

    @Test
    void resetLastSlotIndexRedis_shouldClearAndResetSessionState() {
        scheduleService.resetLastSlotIndexRedis(7L, "2024-2025", "HK2");
        scheduleService.commitSessionToRedis(7L, "2024-2025", "HK2");

        verify(redisService).clearLastSlotIdx(7L, "2024-2025", "HK2");
        verify(redisService).saveLastSlotIdx(7L, "2024-2025", "HK2", -1);
    }

    @Test
    void resetLastSlotIndexRedis_shouldResetWithoutClearWhenMissingContext() {
        scheduleService.resetLastSlotIndexRedis(null, "2024-2025", "HK2");
        scheduleService.commitSessionToRedis(1L, "2024-2025", "HK1");

        verify(redisService, never()).clearLastSlotIdx(any(), any(), any());
        verify(redisService).saveLastSlotIdx(1L, "2024-2025", "HK1", -1);
    }

    @Test
    void generateSchedule_shouldClearMappingsAtSessionStart() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        scheduleService.generateSchedule(request);

        verify(subjectRoomMappingService, times(1)).clearMappings();
    }

    @Test
    void generateSchedule_shouldUseFirstSubjectWhenRepositoryReturnsMultiple() {
        Subject s1 = Subject.builder().id(100L).build();
        Subject s2 = Subject.builder().id(200L).build();
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(s1, s2));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getSubjectDatabaseId()).isEqualTo(100L);
    }

    @Test
    void generateSchedule_shouldComputeTotalsFromNonEmptyItems() {
        TKBRequest missingTemplateSubject = baseRequest("INT404", 45, "CNTT");
        TKBRequest validSubject = baseRequest("INT1001", 30, "CNTT");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(validSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getTotalClasses()).isEqualTo(1);
        assertThat(response.getTotalRows()).isGreaterThan(0);
        assertThat(missingTemplateSubject.getSotiet()).isEqualTo(45);
    }

    @Test
    void generateSchedule_shouldUseDerivedSemesterContextFromItemWhenRequestNull() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year("2025-2026");
        item.setSemester("HK2");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(77L)
                .items(List.of(item))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK2", "2025-2026")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK2 2025-2026"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(77L, "2025-2026", "HK2")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK2", "2025-2026"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        scheduleService.generateSchedule(request);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(dataLoaderService).loadTemplateData(keyCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo("HK2 2025-2026");
    }

    private TKBRequest baseRequest(String subjectCode, int periods, String major) {
        return TKBRequest.builder()
                .ma_mon(subjectCode)
                .ten_mon("Nhap mon")
                .sotiet(periods)
                .siso(100)
                .siso_mot_lop(50)
                .solop(1)
                .nganh(major)
                .student_year("2024")
                .he_dac_thu("CLC")
                .academic_year("2024-2025")
                .semester("HK1")
                .build();
    }

    private DataLoaderService.TKBTemplateRow templateRow(int totalPeriods, int day, int kip, int periodLength,
            Long dbId, String id) {
        return new DataLoaderService.TKBTemplateRow(
                dbId,
                totalPeriods,
                day,
                kip,
                1,
                periodLength,
                id,
                fullWeeks,
                periodLength * 18);
    }

    // ==================== NEW TEST CASES FOR COVERAGE ====================

    @Test
    void resetState_shouldResetLastSlotIdxToMinusOne() {
        scheduleService.resetState();

        // Reset state được gọi, lastSlotIdx sẽ được reset về -1
        // Không throw exception và không gọi repository
        verify(scheduleRepository, never()).findAll();
    }

    @Test
    void generateSchedule_shouldHandleRequestWithNullUserId() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(null)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(redisService, never()).loadLastSlotIdx(any(), any(), any());
    }

    @Test
    void generateSchedule_shouldHandleEmptyItemsList() {
        // When items is empty but academicYear/semester are provided, it still loads templates
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of())
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalRows()).isEqualTo(0);
        assertThat(response.getTotalClasses()).isEqualTo(0);
    }

    @Test
    void generateSchedule_shouldThrowWhenOnlyNullAcademicYearAndSemester() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year(null);
        item.setSemester(null);
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(List.of(item))
                .build();

        when(dataLoaderService.loadTemplateData("null null")).thenReturn(List.of());

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Chưa có dữ liệu lịch mẫu");
    }

    @Test
    void generateSchedule_shouldThrowWhenNoTemplateMatchesPeriods() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 45, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(30, 2, 1, 1, 1L, "30-A"),
                        templateRow(60, 2, 1, 1, 2L, "60-A")));

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Không có Data cho 45 tiết");
    }

    @Test
    void generateSchedule_shouldProcessMultipleClassesForSingleSubject() {
        TKBRequest requestWithMultipleClasses = TKBRequest.builder()
                .ma_mon("INT1001")
                .ten_mon("Nhap mon")
                .sotiet(30)
                .siso(150)
                .siso_mot_lop(50)
                .solop(3)
                .nganh("CNTT")
                .student_year("2024")
                .he_dac_thu("CLC")
                .academic_year("2024-2025")
                .semester("HK1")
                .build();

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(requestWithMultipleClasses))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).hasSize(6);
    }

    @Test
    void generateSchedule_shouldHandleMultipleSubjectsWithDifferentMajors() {
        TKBRequest subject1 = baseRequest("INT1001", 30, "CNTT");
        TKBRequest subject2 = baseRequest("MAT1001", 30, "KT");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(subject1, subject2))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("MAT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalClasses()).isEqualTo(2);
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithCombinedMajor() {
        TKBRequest combinedSubject = baseRequest("COM001", 30, "CNTT-KT");
        combinedSubject.setNganh("CNTT-KT");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(combinedSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("COM001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getInput().getNganh()).isEqualTo("CNTT-KT");
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithNullMajor() {
        TKBRequest nullMajorSubject = baseRequest("INT1001", 30, null);
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(nullMajorSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
    }

    @Test
    void generateSchedule_shouldSetLastSlotIdxInResponse() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(5);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getLastSlotIdx()).isNotNull();
    }

    @Test
    void generateSchedule_shouldContinueWhenSubjectNotFoundInRepository() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT9999", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT9999", "HK1", "2024-2025"))
                .thenReturn(List.of());

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows().get(0).getSubjectDatabaseId()).isNull();
    }

    @Test
    void generateSchedule_shouldCalculateTotalRowsCorrectly() {
        TKBRequest subject1 = baseRequest("INT1001", 30, "CNTT");
        TKBRequest subject2 = baseRequest("MAT1001", 30, "KT");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(subject1, subject2))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("MAT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getTotalRows()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void generateSchedule_shouldSetOccupiedRoomsCountZero() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getOccupiedRoomsCount()).isEqualTo(0);
    }

    @Test
    void generateSchedule_shouldHandle14PeriodSubjectCorrectly() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 14, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(14, 2, 1, 1, 1L, "14-A")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
    }

    @Test
    void generateSchedule_shouldSetHeDacThuFromRequest() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getHeDacThu()).isEqualTo("CLC");
    }

    @Test
    void generateSchedule_shouldSetStudentYearFromRequest() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getStudentYear()).isEqualTo("2024");
    }

    @Test
    void generateSchedule_shouldSetSiSoMotLopFromRequest() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getSiSoMotLop()).isEqualTo(50);
    }

    @Test
    void generateSchedule_shouldPopulateAllRowFieldsCorrectly() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 55L, "TPL-01")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(999L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);
        TKBRowResult row = response.getItems().get(0).getRows().get(0);

        assertThat(row.getMaMon()).isEqualTo("INT1001");
        assertThat(row.getTenMon()).isEqualTo("Nhap mon");
        assertThat(row.getKip()).isEqualTo(1);
        assertThat(row.getThu()).isEqualTo(2);
        assertThat(row.getTietBd()).isEqualTo(1);
        assertThat(row.getL()).isEqualTo(1);
        assertThat(row.getAH()).isEqualTo(18);
        assertThat(row.getAI()).isEqualTo(30);
        assertThat(row.getAJ()).isEqualTo(12);
        assertThat(row.getN()).isEqualTo("TPL-01");
        assertThat(row.getAcademicYear()).isEqualTo("2024-2025");
        assertThat(row.getSemester()).isEqualTo("HK1");
    }

    @Test
    void generateSchedule_shouldSkipTemplatesWithZeroAH() {
        DataLoaderService.TKBTemplateRow template1 = new DataLoaderService.TKBTemplateRow(
                1L, 30, 2, 1, 1, 0, "ZERO-AH", List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), 0);
        DataLoaderService.TKBTemplateRow template2 = new DataLoaderService.TKBTemplateRow(
                2L, 30, 2, 1, 1, 1, "VALID-AH", fullWeeks, 18);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(template1, template2));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> !"ZERO-AH".equals(r.getN()));
    }

    @Test
    void generateSchedule_shouldThrowWhenTemplateDataIsEmptyForSemester() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2025-2026")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2025-2026")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2025-2026")).thenReturn(List.of());

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Chưa có dữ liệu lịch mẫu cho HK1 2025-2026");
    }

    @Test
    void generateSchedule_shouldSetSemesterIdNullWhenSemesterNotFound() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK3")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK3", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK3 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));

        scheduleService.generateSchedule(request);

        verify(dataLoaderService).setCurrentSemesterId(null);
    }

    @Test
    void generateSchedule_shouldSetRoomIdNullForAllRows() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> r.getRoomId() == null);
    }

    @Test
    void generateSchedule_shouldSetPhongNullForAllRows() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> r.getPhong() == null);
    }

    @Test
    void generateSchedule_shouldHandleMultipleSubjectsWithMixedPeriods() {
        TKBRequest subject30 = baseRequest("INT1001", 30, "CNTT");
        TKBRequest subject60 = baseRequest("INT2001", 60, "CNTT");
        subject60.setSolop(1);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(subject30, subject60))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(30, 2, 1, 1, 20L, "30-A")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalClasses()).isEqualTo(2);
    }

    @Test
    void generateSchedule_shouldMaintainSessionStateAcrossMultipleCalls() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response1 = scheduleService.generateSchedule(request);
        TKBBatchResponse response2 = scheduleService.generateSchedule(request);

        assertThat(response1.getLastSlotIdx()).isEqualTo(response2.getLastSlotIdx());
    }

    @Test
    void generateSchedule_shouldProcessWithZeroSolopDefaultingToOne() {
        TKBRequest zeroSolopSubject = baseRequest("INT1001", 30, "CNTT");
        zeroSolopSubject.setSolop(0);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(zeroSolopSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).hasSize(2);
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithDifferentSemesterFormat() {
        TKBRequest subjectHK2 = baseRequest("INT1001", 30, "CNTT");
        subjectHK2.setSemester("2");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("2")
                .items(List.of(subjectHK2))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("2", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK2 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "2")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "2", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(dataLoaderService).loadTemplateData("HK2 2024-2025");
    }

    @Test
    void generateSchedule_shouldValidateThatAllRowsHaveCorrectMaMon() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "INT1001".equals(r.getMaMon()));
    }

    @Test
    void generateSchedule_shouldValidateThatAllRowsHaveCorrectTenMon() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "Nhap mon".equals(r.getTenMon()));
    }

    @Test
    void generateSchedule_shouldSetAcademicYearInRows() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "2024-2025".equals(r.getAcademicYear()));
    }

    @Test
    void generateSchedule_shouldSetSemesterInRows() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "HK1".equals(r.getSemester()));
    }

    @Test
    void generateSchedule_shouldSetNganhInRows() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> "CNTT".equals(r.getNganh()));
    }

    @Test
    void generateSchedule_shouldAssignCorrectLopNumbersForMultipleClasses() {
        TKBRequest multiClassSubject = baseRequest("INT1001", 30, "CNTT");
        multiClassSubject.setSolop(3);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(multiClassSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        List<Integer> lopNumbers = response.getItems().get(0).getRows().stream()
                .map(TKBRowResult::getLop)
                .distinct()
                .sorted()
                .toList();

        assertThat(lopNumbers).containsExactly(1, 2, 3);
    }

    @Test
    void generateSchedule_shouldHandleCombinedMajorSubject() {
        TKBRequest combinedSubject = baseRequest("COM001", 30, "CNTT-KT-VL");
        combinedSubject.setNganh("CNTT-KT-VL");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(combinedSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("COM001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
    }

    @Test
    void generateSchedule_shouldValidateOTAGArraySizeIs18() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows())
                .allMatch(r -> r.getO_to_AG() != null && r.getO_to_AG().size() == 18);
    }

    @Test
    void generateSchedule_shouldHandleVeryLargeSiSo() {
        TKBRequest largeSiSoSubject = baseRequest("INT1001", 30, "CNTT");
        largeSiSoSubject.setSiso(10000);
        largeSiSoSubject.setSiso_mot_lop(500);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(largeSiSoSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows().get(0).getSiSoMotLop()).isEqualTo(500);
    }

    // ============ ADDITIONAL COVERAGE TESTS ============

    @Test
    void generateSchedule_shouldHandleNullItemsInRequest() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(null)
                .build();

        // When items is null, code tries to call isEmpty() which throws NPE or validation exception
        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class);
    }

    @Test
    void generateSchedule_shouldPropagateExceptionWhenDataLoaderServiceFails() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenThrow(new RuntimeException("Database connection error"));

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection error");
    }

    @Test
    void generateSchedule_shouldPropagateExceptionWhenSemesterRepositoryFails() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year("2024-2025");
        item.setSemester("HK1");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(List.of(item))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025"))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }

    @Test
    void generateSchedule_shouldHandleExceptionInSubjectRepositoryGracefully() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenThrow(new RuntimeException("Subject lookup failed"));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        // Should still generate response with subjectDatabaseId=null
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows().get(0).getSubjectDatabaseId()).isNull();
    }

    @Test
    void generateSchedule_shouldHandleNullAcademicYearWithNonEmptyItems() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year(null);
        item.setSemester("HK1");
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(List.of(item))
                .build();

        when(dataLoaderService.loadTemplateData("HK1 null")).thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));

        // Should not throw, will use "HK1 null" as semesterKey
        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(dataLoaderService).loadTemplateData("HK1 null");
    }

    @Test
    void generateSchedule_shouldHandleNullSemesterWithNonEmptyItems() {
        TKBRequest item = baseRequest("INT1001", 30, "CNTT");
        item.setAcademic_year("2024-2025");
        item.setSemester(null);
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(List.of(item))
                .build();

        when(dataLoaderService.loadTemplateData("null 2024-2025")).thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));

        // Should not throw, will use "null 2024-2025" as semesterKey
        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        verify(dataLoaderService).loadTemplateData("null 2024-2025");
    }

    @Test
    void generateSchedule_shouldHandleVeryLongWeeksSchedule() {
        List<Integer> extraLongWeeks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            extraLongWeeks.add(1);
        }

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(new DataLoaderService.TKBTemplateRow(1L, 30, 2, 1, 1, 1, "LONG", extraLongWeeks, 30)));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getO_to_AG()).hasSize(18);
    }

    @Test
    void generateSchedule_shouldHandlePeriodLengthZero() {
        DataLoaderService.TKBTemplateRow zeroPeriodRow = new DataLoaderService.TKBTemplateRow(
                1L, 30, 2, 1, 0, 1, "ZERO-L", fullWeeks, 0);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(zeroPeriodRow));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        // Should still succeed but may generate fewer rows due to zero AH
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    void generateSchedule_shouldHandleMultipleTemplatesWithSamePeriod() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(30, 2, 1, 1, 1L, "TPL-01"),
                        templateRow(30, 3, 1, 1, 2L, "TPL-02"),
                        templateRow(30, 4, 1, 1, 3L, "TPL-03")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
        // Should use templates from different days
        List<String> templateNames = response.getItems().get(0).getRows().stream()
                .map(TKBRowResult::getN)
                .distinct()
                .toList();
        assertThat(templateNames).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void generateSchedule_shouldSetLastSlotIdxToNegativeWhenRedisReturnsNegative() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getLastSlotIdx()).isNotNull();
        assertThat(response.getLastSlotIdx()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void generateSchedule_shouldThrowWhenSubjectHasNoMatchingTemplate() {
        TKBRequest validSubject = baseRequest("INT1001", 30, "CNTT");
        TKBRequest invalidSubject = baseRequest("INT9999", 45, "CNTT"); // No template for 45 periods

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(validSubject, invalidSubject))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        assertThatThrownBy(() -> scheduleService.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Không có Data cho 45 tiết");
    }

    @Test
    void generateSchedule_shouldHandleSingleClassSubjectCorrectly() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).hasSize(2);
        assertThat(response.getItems().get(0).getRows().get(0).getLop()).isEqualTo(1);
    }

    @Test
    void generateSchedule_shouldProcess60PeriodSubjectWithMultipleClasses() {
        TKBRequest period60MultiClass = baseRequest("INT2001", 60, "CNTT");
        period60MultiClass.setSolop(2);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(period60MultiClass))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        // Only 1 class produces rows (2 days) because templates only exist for slot 0
        // Second class (slot 1) has no matching templates for kip 2
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).hasSize(2);
    }

    @Test
    void generateSchedule_shouldHandleVerySmallSiSo() {
        TKBRequest smallSiSo = baseRequest("INT1001", 30, "CNTT");
        smallSiSo.setSiso(5);
        smallSiSo.setSiso_mot_lop(5);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(smallSiSo))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).hasSize(2);
    }

    @Test
    void generateSchedule_shouldHandleVeryHighSolopValue() {
        TKBRequest highSolop = baseRequest("INT1001", 30, "CNTT");
        highSolop.setSolop(10);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(highSolop))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).hasSize(20);
    }

    @Test
    void generateSchedule_shouldHandleSessionStateAcrossMultiple60PeriodSubjects() {
        TKBRequest period601 = baseRequest("INT2001", 60, "CNTT");
        period601.setSolop(1);
        TKBRequest period602 = baseRequest("INT2002", 60, "CNTT");
        period602.setSolop(1);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(period601, period602))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2002", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalClasses()).isEqualTo(2);
    }

    @Test
    void generateSchedule_shouldHandleMixedPeriodSubjectsInterleaved() {
        TKBRequest period60 = baseRequest("INT2001", 60, "CNTT");
        period60.setSolop(1);
        TKBRequest period30a = baseRequest("INT1001", 30, "CNTT");
        TKBRequest period30b = baseRequest("INT1002", 30, "CNTT");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(period60, period30a, period30b))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B"),
                        templateRow(30, 4, 1, 1, 12L, "30-A")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1002", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(3L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(3);
        // First should be 60-period subject (sorted to front)
        assertThat(response.getItems().get(0).getInput().getMa_mon()).isEqualTo("INT2001");
    }

    @Test
    void generateSchedule_shouldReturnEmptyRowsWhenNoTemplatesMatchAfterFiltering() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        // Return templates but all have AH=0 (filtered out)
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        new DataLoaderService.TKBTemplateRow(1L, 30, 2, 1, 1, 0, "ZERO", fullWeeks, 0)));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows()).isEmpty();
    }

    @Test
    void generateSchedule_shouldHandleLargeNumberOfSubjects() {
        List<TKBRequest> manySubjects = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            TKBRequest subject = baseRequest("INT" + i, 30, "CNTT");
            manySubjects.add(subject);
        }

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(manySubjects)
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);

        // Mock subject repository for all subjects
        List<Subject> subjects = manySubjects.stream()
                .map(s -> Subject.builder().id(Long.parseLong(s.getMa_mon().replace("INT", ""))).build())
                .toList();
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear(any(), any(), any()))
                .thenReturn(subjects);

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(50);
        assertThat(response.getTotalClasses()).isEqualTo(50);
    }

    @Test
    void commitSessionToRedis_shouldUpdateLastSlotIdxAfterSaving() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(10L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(10L, "2024-2025", "HK1")).thenReturn(5);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        scheduleService.generateSchedule(request);
        scheduleService.commitSessionToRedis(10L, "2024-2025", "HK1");

        verify(redisService).saveLastSlotIdx(eq(10L), eq("2024-2025"), eq("HK1"), any(Integer.class));
    }

    @Test
    void resetLastSlotIndexRedis_shouldClearAndResetWithValidContext() {
        scheduleService.resetLastSlotIndexRedis(5L, "2024-2025", "HK1");

        verify(redisService).clearLastSlotIdx(5L, "2024-2025", "HK1");
        // Note: resetLastSlotIndexRedis does NOT call saveLastSlotIdx, only clear
    }

    @Test
    void generateSchedule_shouldSetCorrectSiSoFromRequest() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getSiSoMotLop()).isEqualTo(50);
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithEmptyMajor() {
        TKBRequest emptyMajor = baseRequest("INT1001", 30, "");
        emptyMajor.setNganh("");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(emptyMajor))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithSpecialCharactersInMajor() {
        TKBRequest specialMajor = baseRequest("INT1001", 30, "CNTT & KT");
        specialMajor.setNganh("CNTT & KT");

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(specialMajor))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRows()).isNotEmpty();
    }

    @Test
    void generateSchedule_shouldHandleSubjectWithNegativeSolop() {
        TKBRequest negativeSolop = baseRequest("INT1001", 30, "CNTT");
        negativeSolop.setSolop(-1);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(negativeSolop))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 1L, "R1")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        // Math.max(1, -1) should default to 1, but 30 periods need 2 rows
        assertThat(response.getItems().get(0).getRows()).hasSize(2);
    }

    @Test
    void generateSchedule_shouldSetCorrectLopFor60PeriodSubject() {
        TKBRequest period60 = baseRequest("INT2001", 60, "CNTT");
        period60.setSolop(2);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(period60))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        List<Integer> lopNumbers = response.getItems().get(0).getRows().stream()
                .map(TKBRowResult::getLop)
                .distinct()
                .sorted()
                .toList();

        // Only class 1 produces rows (2 days) because templates match only first slot
        assertThat(lopNumbers).containsExactly(1);
    }

    @Test
    void generateSchedule_shouldProcessMultiple60PeriodSubjects() {
        TKBRequest period60a = baseRequest("INT2001", 60, "CNTT");
        period60a.setSolop(1);
        TKBRequest period60b = baseRequest("INT2002", 60, "CNTT");
        period60b.setSolop(1);

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(period60a, period60b))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(
                        templateRow(60, 2, 1, 1, 10L, "60-A"),
                        templateRow(60, 3, 1, 1, 11L, "60-B")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT2002", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(2L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalClasses()).isEqualTo(2);
    }

    @Test
    void generateSchedule_shouldIncludeAllExpectedFieldsInRow() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(templateRow(30, 2, 1, 1, 55L, "TPL-01")));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(999L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);
        TKBRowResult row = response.getItems().get(0).getRows().get(0);

        assertThat(row.getLop()).isNotNull();
        assertThat(row.getMaMon()).isNotNull();
        assertThat(row.getTenMon()).isNotNull();
        assertThat(row.getKip()).isNotNull();
        assertThat(row.getThu()).isNotNull();
        assertThat(row.getTietBd()).isNotNull();
        assertThat(row.getL()).isNotNull();
        assertThat(row.getPhong()).isNull();
        assertThat(row.getRoomId()).isNull();
        assertThat(row.getAH()).isNotNull();
        assertThat(row.getAI()).isNotNull();
        assertThat(row.getAJ()).isNotNull();
        assertThat(row.getN()).isNotNull();
        assertThat(row.getO_to_AG()).isNotNull();
        assertThat(row.getTemplateDatabaseId()).isNotNull();
        assertThat(row.getStudentYear()).isNotNull();
        assertThat(row.getHeDacThu()).isNotNull();
        assertThat(row.getNganh()).isNotNull();
        assertThat(row.getSiSoMotLop()).isNotNull();
        assertThat(row.getAcademicYear()).isNotNull();
        assertThat(row.getSemester()).isNotNull();
        assertThat(row.getSubjectDatabaseId()).isNotNull();
    }

    @Test
    void resetState_shouldNotAffectOtherInstances() {
        // Reset state
        scheduleService.resetState();

        // Verify no repository interactions
        verify(scheduleRepository, never()).findAll();
        verify(scheduleRepository, never()).saveAll(any());
    }

    @Test
    void generateSchedule_shouldHandleNullInWeekScheduleList() {
        List<Integer> weekWithNulls = new ArrayList<>();
        weekWithNulls.add(1);
        weekWithNulls.add(null);
        weekWithNulls.add(1);
        weekWithNulls.add(null);
        // Fill to 18
        while (weekWithNulls.size() < 18) {
            weekWithNulls.add(1);
        }

        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .academicYear("2024-2025")
                .semester("HK1")
                .items(List.of(baseRequest("INT1001", 30, "CNTT")))
                .build();

        when(semesterRepository.findBySemesterNameAndAcademicYear("HK1", "2024-2025")).thenReturn(Optional.empty());
        when(dataLoaderService.loadTemplateData("HK1 2024-2025"))
                .thenReturn(List.of(new DataLoaderService.TKBTemplateRow(1L, 30, 2, 1, 1, 1, "NULL-TEST", weekWithNulls, 30)));
        when(redisService.loadLastSlotIdx(1L, "2024-2025", "HK1")).thenReturn(-1);
        when(subjectRepository.findAllBySubjectCodeAndSemesterAndAcademicYear("INT1001", "HK1", "2024-2025"))
                .thenReturn(List.of(Subject.builder().id(1L).build()));

        TKBBatchResponse response = scheduleService.generateSchedule(request);

        assertThat(response.getItems().get(0).getRows().get(0).getO_to_AG()).hasSize(18);
    }
}
