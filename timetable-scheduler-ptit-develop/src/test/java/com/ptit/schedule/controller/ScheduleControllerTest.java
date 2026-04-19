package com.ptit.schedule.controller;

import com.ptit.schedule.dto.*;
import com.ptit.schedule.entity.Schedule;
import com.ptit.schedule.entity.User;
import com.ptit.schedule.entity.Subject;
import com.ptit.schedule.entity.TKBTemplate;
import com.ptit.schedule.entity.Room;
import com.ptit.schedule.repository.SubjectRepository;
import com.ptit.schedule.repository.TKBTemplateRepository;
import com.ptit.schedule.repository.RoomRepository;
import com.ptit.schedule.service.ScheduleService;
import com.ptit.schedule.service.DataLoaderService;
import com.ptit.schedule.exception.InvalidDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private DataLoaderService dataLoaderService;

    @Mock
    private Authentication authentication;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TKBTemplateRepository tkbTemplateRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ScheduleController scheduleController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void saveSchedule_shouldReturnSuccessMessage() {
        setupSecurityContext();

        // Mock repository responses
        Subject subject = Subject.builder().id(100L).subjectCode("INT1001").subjectName("Nhap mon").build();
        TKBTemplate template = TKBTemplate.builder().id(200L).kip(30).build();
        Room room = Room.builder().id(1L).name("401").building("A1").build();

        when(subjectRepository.getReferenceById(100L)).thenReturn(subject);
        when(tkbTemplateRepository.getReferenceById(200L)).thenReturn(template);
        when(roomRepository.findByNameAndBuilding("401", "A1")).thenReturn(Optional.of(room));

        SaveScheduleRequest request1 = SaveScheduleRequest.builder()
                .subjectId(100L)
                .templateDatabaseId(200L)
                .roomNumber("401-A1")
                .classNumber(1)
                .studentYear("2024")
                .major("CNTT")
                .specialSystem("CLC")
                .siSoMotLop(50)
                .build();

        List<SaveScheduleRequest> requests = List.of(request1);

        ResponseEntity<String> response = scheduleController.saveSchedule(requests);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Đã lưu TKB vào database!");
        verify(scheduleService).saveAll(anyList());
    }

    @Test
    void saveSchedule_shouldThrowWhenRequestListIsEmpty() {
        List<SaveScheduleRequest> emptyList = Collections.emptyList();

        assertThatThrownBy(() -> scheduleController.saveSchedule(emptyList))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Danh sách lịch học không được rỗng");
    }

    @Test
    void saveSchedule_shouldThrowWhenSubjectIdIsNull() {
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(null)
                .templateDatabaseId(200L)
                .roomNumber("401-A1")
                .build();

        assertThatThrownBy(() -> scheduleController.saveSchedule(List.of(request)))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Subject ID không được rỗng");
    }

    @Test
    void saveSchedule_shouldThrowWhenTemplateIdIsNull() {
        SaveScheduleRequest request = SaveScheduleRequest.builder()
                .subjectId(100L)
                .templateDatabaseId(null)
                .roomNumber("401-A1")
                .build();

        assertThatThrownBy(() -> scheduleController.saveSchedule(List.of(request)))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Template ID không được rỗng");
    }

    @Test
    void getAllSchedules_shouldReturnUserSchedules() {
        setupSecurityContext();

        Schedule schedule = Schedule.builder().id(1L).build();
        List<Schedule> schedules = List.of(schedule);
        when(scheduleService.getSchedulesByUserId(1L)).thenReturn(schedules);

        ResponseEntity<List<Schedule>> response = scheduleController.getAllSchedules();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getSchedulesBySubject_shouldReturnSchedules() {
        Schedule schedule = Schedule.builder().id(1L).build();
        List<Schedule> schedules = List.of(schedule);
        when(scheduleService.getSchedulesBySubjectId("INT1001")).thenReturn(schedules);

        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesBySubject("INT1001");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getSchedulesByMajor_shouldReturnSchedules() {
        Schedule schedule = Schedule.builder().id(1L).build();
        List<Schedule> schedules = List.of(schedule);
        when(scheduleService.getSchedulesByMajor("CNTT")).thenReturn(schedules);

        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesByMajor("CNTT");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getSchedulesByStudentYear_shouldReturnSchedules() {
        Schedule schedule = Schedule.builder().id(1L).build();
        List<Schedule> schedules = List.of(schedule);
        when(scheduleService.getSchedulesByStudentYear("2024")).thenReturn(schedules);

        ResponseEntity<List<Schedule>> response = scheduleController.getSchedulesByStudentYear("2024");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void deleteSchedule_shouldReturnSuccessMessage() {
        doNothing().when(scheduleService).deleteScheduleById(100L);

        ResponseEntity<String> response = scheduleController.deleteSchedule(100L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Đã xóa lịch học!");
        verify(scheduleService).deleteScheduleById(100L);
    }

    @Test
    void deleteSchedule_shouldThrowWhenIdIsInvalid() {
        assertThatThrownBy(() -> scheduleController.deleteSchedule(0L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID lịch học không hợp lệ");

        assertThatThrownBy(() -> scheduleController.deleteSchedule(-1L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ID lịch học không hợp lệ");
    }

    @Test
    void deleteAllSchedules_shouldReturnSuccessMessage() {
        doNothing().when(scheduleService).deleteAllSchedules();

        ResponseEntity<String> response = scheduleController.deleteAllSchedules();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Đã xóa toàn bộ lịch học!");
        verify(scheduleService).deleteAllSchedules();
    }

    @Test
    void generateSchedule_shouldReturnBatchResponse() {
        TKBRequest item = TKBRequest.builder()
                .ma_mon("INT1001")
                .ten_mon("Nhap mon")
                .sotiet(30)
                .siso(100)
                .siso_mot_lop(50)
                .solop(1)
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
                .items(List.of(item))
                .build();

        TKBBatchResponse batchResponse = new TKBBatchResponse();
        batchResponse.setItems(List.of());
        batchResponse.setTotalClasses(1);
        batchResponse.setTotalRows(2);

        when(scheduleService.generateSchedule(any(TKBBatchRequest.class))).thenReturn(batchResponse);

        ResponseEntity<TKBBatchResponse> response = scheduleController.generateSchedule(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(batchResponse);
    }

    @Test
    void generateSchedule_shouldThrowWhenRequestIsNull() {
        assertThatThrownBy(() -> scheduleController.generateSchedule(null))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Danh sách môn học không được rỗng");
    }

    @Test
    void generateSchedule_shouldThrowWhenItemsIsNull() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(null)
                .build();

        assertThatThrownBy(() -> scheduleController.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Danh sách môn học không được rỗng");
    }

    @Test
    void generateSchedule_shouldThrowWhenItemsIsEmpty() {
        TKBBatchRequest request = TKBBatchRequest.builder()
                .userId(1L)
                .items(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> scheduleController.generateSchedule(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Danh sách môn học không được rỗng");
    }

    @Test
    void health_shouldReturnOk() {
        ResponseEntity<String> response = scheduleController.health();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Schedule Controller is OK");
    }

    @Test
    void testData_shouldReturnTemplateCount() {
        DataLoaderService.TKBTemplateRow row = new DataLoaderService.TKBTemplateRow(
                1L, 30, 2, 1, 1, 1, "TPL", List.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), 30);
        List<DataLoaderService.TKBTemplateRow> templateData = List.of(row);
        when(dataLoaderService.loadTemplateData()).thenReturn(templateData);

        ResponseEntity<Map<String, Object>> response = scheduleController.testData();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("template_rows_count")).isEqualTo(1);
        assertThat(body.get("status")).isEqualTo("success");
    }

    @Test
    void resetState_shouldCallServiceAndReturnSuccess() {
        doNothing().when(scheduleService).resetState();

        ResponseEntity<Map<String, Object>> response = scheduleController.resetState();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo("success");
        assertThat(body.get("message")).isEqualTo("TKB state reset successfully");
        verify(scheduleService).resetState();
    }

    @Test
    void debugCommonSubject_shouldReturnDebugInfo() {
        TKBRequest commonSubjectRequest = TKBRequest.builder()
                .ma_mon("SKD1102")
                .ten_mon("Kỹ năng làm việc nhóm")
                .sotiet(30)
                .siso(100)
                .siso_mot_lop(50)
                .solop(2)
                .nganh("Chung")
                .subject_type("general")
                .student_year("2024")
                .he_dac_thu("")
                .build();

        TKBBatchResponse batchResponse = new TKBBatchResponse();
        batchResponse.setItems(List.of());
        batchResponse.setTotalRows(2);
        batchResponse.setTotalClasses(1);

        when(scheduleService.generateSchedule(any(TKBBatchRequest.class))).thenReturn(batchResponse);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = scheduleController.debugCommonSubject();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse<Map<String, Object>> body = response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getMessage()).isEqualTo("Debug common subject completed");
    }

    @Test
    void saveLastSlotIdxToRedis_shouldCallService() {
        doNothing().when(scheduleService).commitSessionToRedis(10L, "2024-2025", "HK1");

        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                scheduleController.saveLastSlotIdxToRedis(10L, "2024-2025", "HK1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse<Map<String, Object>> body = response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getMessage()).isEqualTo("Lưu lastSlotIdx thành công");
        verify(scheduleService).commitSessionToRedis(10L, "2024-2025", "HK1");
    }

    @Test
    void resetLastSlotIdxRedis_shouldCallService() {
        doNothing().when(scheduleService).resetLastSlotIndexRedis(10L, "2024-2025", "HK1");

        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                scheduleController.resetLastSlotIdxRedis(10L, "2024-2025", "HK1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse<Map<String, Object>> body = response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getMessage()).isEqualTo("Reset lastSlotIdx thành công");
        verify(scheduleService).resetLastSlotIndexRedis(10L, "2024-2025", "HK1");
    }

}
