package com.ptit.schedule.utils;

import org.junit.jupiter.api.Test;
import org.modelmapper.internal.Pair;

import java.time.LocalDate;

import static com.ptit.schedule.utils.AcademicYearUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

class AcademicYearUtilsTest {

    @Test
    void resolveAcademicYear_shouldReturnProvidedYearWhenValid() {
        String result = resolveAcademicYear("2024-2025");
        assertThat(result).isEqualTo("2024-2025");
    }

    @Test
    void resolveAcademicYear_shouldReturnCurrentYearWhenNull() {
        String result = resolveAcademicYear(null);
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int startYear = (month >= 8) ? year : year - 1;
        String expected = startYear + "-" + (startYear + 1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void resolveAcademicYear_shouldReturnCurrentYearWhenEmpty() {
        String result = resolveAcademicYear("");
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int startYear = (month >= 8) ? year : year - 1;
        String expected = startYear + "-" + (startYear + 1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void resolveAcademicYear_shouldReturnCurrentYearWhenOnlyWhitespace() {
        String result = resolveAcademicYear("   ");
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int startYear = (month >= 8) ? year : year - 1;
        String expected = startYear + "-" + (startYear + 1);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void resolveAcademicYear_shouldStartFromAugust() {
        // Test for August (month 8) - should use current year
        LocalDate augustDate = LocalDate.of(2025, 8, 15);
        String result = AcademicYearUtils.resolveAcademicYearForDate(augustDate);
        assertThat(result).isEqualTo("2025-2026");
    }

    @Test
    void resolveAcademicYear_shouldStartFromJuly() {
        // Test for July (month 7) - should use previous year
        LocalDate julyDate = LocalDate.of(2025, 7, 15);
        String result = AcademicYearUtils.resolveAcademicYearForDate(julyDate);
        assertThat(result).isEqualTo("2024-2025");
    }

    @Test
    void splitSemesterAndYear_shouldReturnNullWhenInputIsNull() {
        Pair<String, String> result = splitSemesterAndYear(null);
        assertThat(result).isNull();
    }

    @Test
    void splitSemesterAndYear_shouldHandleEnDash() {
        Pair<String, String> result = splitSemesterAndYear("HK1–2024-2025");
        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isEqualTo("HK1");
        assertThat(result.getRight()).isEqualTo("2024-2025");
    }

    @Test
    void splitSemesterAndYear_shouldHandleRegularDash() {
        Pair<String, String> result = splitSemesterAndYear("HK1-2024-2025");
        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isEqualTo("HK1");
        assertThat(result.getRight()).isEqualTo("2024-2025");
    }

    @Test
    void splitSemesterAndYear_shouldHandleSpacesAroundDash() {
        Pair<String, String> result = splitSemesterAndYear("HK1  -  2024-2025");
        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isEqualTo("HK1");
        assertThat(result.getRight()).isEqualTo("2024-2025");
    }

    @Test
    void splitSemesterAndYear_shouldHandleHK2() {
        Pair<String, String> result = splitSemesterAndYear("HK2-2024-2025");
        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isEqualTo("HK2");
        assertThat(result.getRight()).isEqualTo("2024-2025");
    }

    @Test
    void splitSemesterAndYear_shouldReturnNullForInvalidFormat() {
        Pair<String, String> result = splitSemesterAndYear("invalid");
        assertThat(result).isNull();
    }

    @Test
    void splitSemesterAndYear_shouldTrimSemesterAndYear() {
        Pair<String, String> result = splitSemesterAndYear("  HK1  -  2024-2025  ");
        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isEqualTo("HK1");
        assertThat(result.getRight()).isEqualTo("2024-2025");
    }
}


