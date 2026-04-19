package com.ptit.schedule.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * File gộp chung để test các lớp DTO thuộc module Học kỳ.
 */
public class SemesterDtoTest {

    // --- SemesterRequest ---
    @Test
    void testSemesterRequest_ValidData() {
        // TC_SEM_REQ_01: Mục đích: Khởi tạo và kiểm chứng data mapping của SemesterRequest.
        SemesterRequest request = new SemesterRequest();
        request.setSemesterName("Kỳ 1");
        assertEquals("Kỳ 1", request.getSemesterName());
    }

    @Test
    void testSemesterRequest_NullName() {
        // TC_SEM_REQ_02: Mục đích: Xử lý tên học kỳ bị null.
        SemesterRequest request = new SemesterRequest();
        request.setSemesterName(null);
        assertNull(request.getSemesterName());
    }

    @Test
    void testSemesterRequest_EmptyName() {
        // TC_SEM_REQ_03: Mục đích: Tên học kỳ chuỗi rỗng.
        SemesterRequest request = new SemesterRequest();
        request.setSemesterName("");
        assertEquals("", request.getSemesterName());
    }

    @Test
    void testSemesterRequest_SetYear() {
        // TC_SEM_REQ_04: Mục đích: Cài đặt và lấy academicYear.
        SemesterRequest request = new SemesterRequest();
        request.setAcademicYear("2024-2025");
        assertEquals("2024-2025", request.getAcademicYear());
    }

    @Test
    void testSemesterRequest_SetActive() {
        // TC_SEM_REQ_05: Mục đích: Cài flag active.
        SemesterRequest request = new SemesterRequest();
        request.setIsActive(true);
        assertTrue(request.getIsActive());
    }

    // --- SemesterResponse ---
    @Test
    void testSemesterResponse_ValidData() {
        // TC_SEM_RES_01: Mục đích: Đảm bảo class Response hoạt động và lấy đúng boolean flag.
        SemesterResponse response = new SemesterResponse();
        response.setId(1L);
        response.setIsActive(true);
        assertEquals(1L, response.getId());
        assertTrue(response.getIsActive());
    }

    @Test
    void testSemesterResponse_NullActive() {
        // TC_SEM_RES_02: Mục đích: Flag isActive có cho qua null không (Boolean wrapper).
        SemesterResponse response = new SemesterResponse();
        response.setIsActive(null);
        assertNull(response.getIsActive());
    }

    @Test
    void testSemesterResponse_SetDescription() {
        // TC_SEM_RES_03: Mục đích: Kiểm tra Setter cho description.
        SemesterResponse response = new SemesterResponse();
        response.setDescription("Test Desc");
        assertEquals("Test Desc", response.getDescription());
    }

    @Test
    void testSemesterResponse_SetYear() {
        // TC_SEM_RES_04: Mục đích: Kiểm tra Setter năm học.
        SemesterResponse response = new SemesterResponse();
        response.setAcademicYear("2020-2021");
        assertEquals("2020-2021", response.getAcademicYear());
    }

    @Test
    void testSemesterResponse_SetName() {
        // TC_SEM_RES_05: Mục đích: Đặt và lấy SemesterName.
        SemesterResponse response = new SemesterResponse();
        response.setSemesterName("Kỳ Hè");
        assertEquals("Kỳ Hè", response.getSemesterName());
    }
}
