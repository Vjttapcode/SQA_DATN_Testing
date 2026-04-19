package com.ptit.schedule.testsupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public final class ScheduleWorkbookFactory {

    private static final int SCHEDULE_TOTAL_COLUMNS = 44;
    private static final int TEMPLATE_TOTAL_COLUMNS = 24;

    private ScheduleWorkbookFactory() {
    }

    public static MockMultipartFile createScheduleValidationWorkbook(
            String fileName,
            List<ScheduleValidationRowSpec> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("validation");

            for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
                Row headerRow = sheet.createRow(rowIndex);
                for (int columnIndex = 0; columnIndex < SCHEDULE_TOTAL_COLUMNS; columnIndex++) {
                    headerRow.createCell(columnIndex).setCellValue("H" + rowIndex + "-" + columnIndex);
                }
            }

            int dataRowIndex = 3;
            for (ScheduleValidationRowSpec spec : rows) {
                Row row = sheet.createRow(dataRowIndex++);
                fillScheduleValidationRow(row, spec);
            }

            workbook.write(outputStream);
            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static MockMultipartFile createWorkbookWithColumnCount(String fileName, int columnCount) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("invalid");
            Row row = sheet.createRow(0);
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                row.createCell(columnIndex).setCellValue("Column " + columnIndex);
            }

            workbook.write(outputStream);
            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static MockMultipartFile createTemplateImportWorkbook(String fileName, List<TemplateRowSpec> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("template");
            Row headerRow = sheet.createRow(0);
            for (int columnIndex = 0; columnIndex < TEMPLATE_TOTAL_COLUMNS; columnIndex++) {
                headerRow.createCell(columnIndex).setCellValue("T" + columnIndex);
            }

            int rowIndex = 1;
            for (TemplateRowSpec spec : rows) {
                Row row = sheet.createRow(rowIndex++);
                fillTemplateRow(row, spec);
            }

            workbook.write(outputStream);
            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void fillScheduleValidationRow(Row row, ScheduleValidationRowSpec spec) {
        setCellValue(row, 1, spec.subjectCode());
        setCellValue(row, 2, spec.subjectName());
        setCellValue(row, 3, spec.classGroup());
        setCellValue(row, 6, spec.dayOfWeek());
        setCellValue(row, 7, spec.shift());
        setCellValue(row, 8, spec.startPeriod());
        setCellValue(row, 9, spec.numberOfPeriods());
        setCellValue(row, 10, spec.room());
        setCellValue(row, 11, spec.building());
        setCellValue(row, 19, spec.studentCount());
        setCellValue(row, 21, spec.teacherId());
        setCellValue(row, 22, spec.teacherName());

        for (Integer weekNumber : spec.activeWeeks()) {
            int weekColumnIndex = 27 + (weekNumber - 1);
            setCellValue(row, weekColumnIndex, "x");
        }
    }

    private static void fillTemplateRow(Row row, TemplateRowSpec spec) {
        setCellValue(row, 0, spec.totalPeriods());
        setCellValue(row, 1, spec.dayOfWeek());
        setCellValue(row, 2, spec.kip());
        setCellValue(row, 3, spec.startPeriod());
        setCellValue(row, 4, spec.periodLength());
        setCellValue(row, 5, spec.templateId());

        for (Integer activeWeek : spec.activeWeeks()) {
            int columnIndex = 5 + activeWeek;
            setCellValue(row, columnIndex, "x");
        }
    }

    private static void setCellValue(Row row, int columnIndex, Object value) {
        if (value == null) {
            return;
        }

        Cell cell = row.createCell(columnIndex);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
            return;
        }

        cell.setCellValue(value.toString());
    }

    public record ScheduleValidationRowSpec(
            String subjectCode,
            String subjectName,
            String classGroup,
            String dayOfWeek,
            String shift,
            String startPeriod,
            String numberOfPeriods,
            String room,
            String building,
            String teacherId,
            String teacherName,
            int studentCount,
            List<Integer> activeWeeks) {
    }

    public record TemplateRowSpec(
            int totalPeriods,
            int dayOfWeek,
            int kip,
            int startPeriod,
            int periodLength,
            String templateId,
            List<Integer> activeWeeks) {
    }
}
