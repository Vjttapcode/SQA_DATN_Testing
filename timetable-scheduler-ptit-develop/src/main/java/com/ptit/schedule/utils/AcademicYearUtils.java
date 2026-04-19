package com.ptit.schedule.utils;

import org.modelmapper.internal.Pair;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcademicYearUtils {

    public static String resolveAcademicYear(String academicYear) {
        if (academicYear != null && !academicYear.trim().isEmpty()) {
            return academicYear;
        }

        return resolveAcademicYearForDate(LocalDate.now());
    }

    public static String resolveAcademicYearForDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int startYear = (month >= 8) ? year : year - 1;

        return startYear + "-" + (startYear + 1);
    }

    public static Pair<String, String> splitSemesterAndYear(String input) {
        if (input == null) return null;

        // chuẩn hóa dấu gạch nối và trim
        String normalized = input.replace("–", "-").trim();

        // Regex: tách semester và năm học, xử lý nhiều khoảng trắng/dấu gạch
        Pattern pattern = Pattern.compile("^(.+?)\\s*-{1,3}\\s*(\\d{4}-\\d{4})$");
        Matcher matcher = pattern.matcher(normalized);

        if (matcher.matches()) {
            String semester = matcher.group(1).trim();
            String academicYear = matcher.group(2).trim();
            return Pair.of(semester, academicYear);
        }

        return null;
    }
}