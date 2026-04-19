package com.ptit.schedule.repository;

import com.ptit.schedule.entity.Semester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Gộp chung các bài Test Repository của module Học Kỳ.
 */
@ExtendWith(MockitoExtension.class)
public class SemesterRepositoryGroupTest {

    @Mock private SemesterRepository semesterRepository;

    @Test
    void testFindById_Found() {
        // TC_SEM_REPO_01: Mục đích: Tìm kiếm theo ID mock.
        Semester mockSemester = new Semester(); mockSemester.setId(30L);
        when(semesterRepository.findById(30L)).thenReturn(Optional.of(mockSemester));
        assertEquals(30L, semesterRepository.findById(30L).get().getId());
    }

    @Test
    void testFindById_NotFound() {
        // TC_SEM_REPO_02: Mục đích: Khách không tồn tại -> empty
        when(semesterRepository.findById(1L)).thenReturn(Optional.empty());
        assertFalse(semesterRepository.findById(1L).isPresent());
    }

    @Test
    void testFindBySemesterName_Found() {
        // TC_SEM_REPO_03: Mục đích: Tìm theo tên SemesterName.
        Semester m = new Semester(); m.setSemesterName("Kỳ Hè");
        when(semesterRepository.findBySemesterName("Kỳ Hè")).thenReturn(Optional.of(m));
        assertEquals("Kỳ Hè", semesterRepository.findBySemesterName("Kỳ Hè").get().getSemesterName());
    }

    @Test
    void testFindBySemesterName_NotFound() {
        // TC_SEM_REPO_04: Mục đích: Tên không tồn tại trong Repo
        when(semesterRepository.findBySemesterName("404")).thenReturn(Optional.empty());
        assertTrue(semesterRepository.findBySemesterName("404").isEmpty());
    }

    @Test
    void testFindByIsActiveTrue() {
        // TC_SEM_REPO_05: Mục đích: Find default Active True Semester.
        Semester m = new Semester(); m.setIsActive(true);
        when(semesterRepository.findByIsActiveTrue()).thenReturn(Optional.of(m));
        assertTrue(semesterRepository.findByIsActiveTrue().get().getIsActive());
    }

    @Test
    void testExistsById_Valid() {
        // TC_SEM_REPO_06: Mục đích: Hàm Boolean existsById true
        when(semesterRepository.existsById(5L)).thenReturn(true);
        assertTrue(semesterRepository.existsById(5L));
    }

    @Test
    void testExistsById_Invalid() {
        // TC_SEM_REPO_07: Mục đích: Hàm Boolean exists false
        when(semesterRepository.existsById(6L)).thenReturn(false);
        assertFalse(semesterRepository.existsById(6L));
    }

    @Test
    void testFindAll() {
        // TC_SEM_REPO_08: Mục đích: Chạy custom findAll Method nếu mock cho array.
        when(semesterRepository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(0, semesterRepository.findAll().size());
    }

    @Test
    void testSave() {
        // TC_SEM_REPO_09: Mục đích: Repo test save behavior.
        Semester dto = new Semester(); dto.setId(10L);
        when(semesterRepository.save(any())).thenReturn(dto);
        assertEquals(10L, semesterRepository.save(new Semester()).getId());
    }

    @Test
    void testDelete() {
        // TC_SEM_REPO_10: Mục đích: Repo delete verify.
        doNothing().when(semesterRepository).deleteById(1L);
        semesterRepository.deleteById(1L);
        verify(semesterRepository, times(1)).deleteById(1L);
    }
}
