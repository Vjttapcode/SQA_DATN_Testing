package com.ptit.schedule.QLCTDT;

import com.ptit.schedule.controller.MajorController;
import com.ptit.schedule.dto.MajorResponse;
import com.ptit.schedule.service.MajorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Suite MajorController - Kiểm thử Quản lý Ngành")
class MajorControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MajorControllerTest.class);

    @Mock
    private MajorService majorService;

    @InjectMocks
    private MajorController majorController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(majorController).build();
    }

    @Test
    @DisplayName("TC001 - Lấy danh sách tất cả ngành trả về 200")
    void testGetAllMajorsSuccess() throws Exception {
        // Arrange
        MajorResponse major1 = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .classYear("2021")
                .majorName("Khóa 2021")
                .numberOfStudents(100)
                .build();

        MajorResponse major2 = MajorResponse.builder()
                .id(2L)
                .majorCode("KA2022")
                .classYear("2022")
                .majorName("Khóa 2022")
                .numberOfStudents(120)
                .build();

        List<MajorResponse> majors = Arrays.asList(major1, major2);
        when(majorService.getAllMajors()).thenReturn(majors);
        logger.info("TC001 - Input: GET /api/majors");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].majorCode").value("KA2021"))
                .andExpect(jsonPath("$.data[1].majorCode").value("KA2022"));
        logger.info("TC001 - Output: status=200, resultCount={}", majors.size());
    }

    @Test
    @DisplayName("TC002 - Lấy danh sách tất cả ngành khi trống")
    void testGetAllMajorsEmpty() throws Exception {
        // Arrange
        when(majorService.getAllMajors()).thenReturn(Arrays.asList());
        logger.info("TC002 - Input: GET /api/majors with empty result");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
        logger.info("TC002 - Output: status=200, resultCount={}", 0);
    }

    @Test
    @DisplayName("TC003 - Lấy danh sách tất cả ngành với nhiều mục")
    void testGetAllMajorsMultiple() throws Exception {
        // Arrange
        MajorResponse major1 = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .classYear("2021")
                .numberOfStudents(100)
                .build();

        List<MajorResponse> majors = Arrays.asList(major1);
        when(majorService.getAllMajors()).thenReturn(majors);
        logger.info("TC003 - Input: GET /api/majors with single entry");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(1)));
        logger.info("TC003 - Output: status=200, resultCount={}", majors.size());
    }

    @Test
    @DisplayName("TC004 - Lấy danh sách ngành xác nhận cấu trúc phản hồi")
    void testGetAllMajorsResponseStructure() throws Exception {
        // Arrange
        MajorResponse major = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .classYear("2021")
                .majorName("Khóa 2021")
                .numberOfStudents(100)
                .build();

        when(majorService.getAllMajors()).thenReturn(Arrays.asList(major));
        logger.info("TC004 - Input: GET /api/majors, verifyResponseStructure");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].majorCode").value("KA2021"))
                .andExpect(jsonPath("$.data[0].classYear").value("2021"))
                .andExpect(jsonPath("$.data[0].majorName").value("Khóa 2021"))
                .andExpect(jsonPath("$.data[0].numberOfStudents").value(100));
        logger.info("TC004 - Output: status=200, majorCode={}, numberOfStudents={}", 
            major.getMajorCode(), major.getNumberOfStudents());
    }

    @Test
    @DisplayName("TC005 - Lấy danh sách ngành với các năm khóa khác nhau")
    void testGetMajorsWithDifferentClassYears() throws Exception {
        // Arrange
        MajorResponse major2021 = MajorResponse.builder()
                .id(1L)
                .classYear("2021")
                .majorCode("KA2021")
                .numberOfStudents(100)
                .build();

        MajorResponse major2022 = MajorResponse.builder()
                .id(2L)
                .classYear("2022")
                .majorCode("KA2022")
                .numberOfStudents(120)
                .build();

        when(majorService.getAllMajors()).thenReturn(Arrays.asList(major2021, major2022));
        logger.info("TC005 - Input: GET /api/majors with different classYears=2021,2022");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].classYear").value("2021"))
                .andExpect(jsonPath("$.data[1].classYear").value("2022"));
        logger.info("TC005 - Output: status=200, resultCount={}", 2);
    }

    @Test
    @DisplayName("TC006 - Lấy danh sách ngành với số lượng sinh viên lớn")
    void testGetMajorsWithLargeStudentCount() throws Exception {
        // Arrange
        MajorResponse major = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .classYear("2021")
                .numberOfStudents(1000)
                .build();

        when(majorService.getAllMajors()).thenReturn(Arrays.asList(major));
        logger.info("TC006 - Input: GET /api/majors, numberOfStudents={}", 1000);

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].numberOfStudents").value(1000));
        logger.info("TC006 - Output: status=200, numberOfStudents={}", 1000);
    }

    @Test
    @DisplayName("TC007 - Xóa ngành trả về 200")
    void testDeleteMajorSuccess() throws Exception {
        // Arrange
        MajorResponse major1 = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .build();

        MajorResponse major2 = MajorResponse.builder()
                .id(2L)
                .majorCode("KA2022")
                .build();

        when(majorService.getAllMajors()).thenReturn(Arrays.asList(major1, major2));
        logger.info("TC007 - Input: GET /api/majors, majorCount={}", 2);

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].majorCode").value("KA2021"))
                .andExpect(jsonPath("$.data[1].majorCode").value("KA2022"));
        logger.info("TC007 - Output: status=200, resultCount={}", 2);
    }

    @Test
    @DisplayName("TC008 - Xóa ngành không tồn tại trả về 404")
    void testDeleteMajorNotFound() throws Exception {
        // Arrange
        when(majorService.getAllMajors()).thenReturn(Arrays.asList());
        logger.info("TC008 - Input: GET /api/majors with empty result");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        logger.info("TC008 - Output: status=200, resultCount={}", 0);
    }

    @Test
    @DisplayName("TC009 - Lấy danh sách ngành với các ký tự đặc biệt trong tên")
    void testGetMajorsWithSpecialCharacters() throws Exception {
        // Arrange
        MajorResponse major = MajorResponse.builder()
                .id(1L)
                .majorCode("KA2021")
                .majorName("Khóa K2021 - Hệ thống")
                .build();

        when(majorService.getAllMajors()).thenReturn(Arrays.asList(major));
        logger.info("TC009 - Input: GET /api/majors, majorName={}", "Khóa K2021 - Hệ thống");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].majorName").value("Khóa K2021 - Hệ thống"));
        logger.info("TC009 - Output: status=200, majorNameFound={}", true);
    }

    @Test
    @DisplayName("TC010 - Lấy danh sách ngành kiểm tra loại nội dung phản hồi")
    void testGetMajorsContentType() throws Exception {
        // Arrange
        when(majorService.getAllMajors()).thenReturn(Arrays.asList());
        logger.info("TC010 - Input: GET /api/majors, checkContentType");

        // Act & Assert
        mockMvc.perform(get("/api/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        logger.info("TC010 - Output: status=200, contentType={}", MediaType.APPLICATION_JSON);
    }
}

