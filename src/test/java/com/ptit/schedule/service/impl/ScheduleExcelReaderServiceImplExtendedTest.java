package com.ptit.schedule.service.impl;

import com.ptit.schedule.dto.ScheduleEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: HK76
 * File Under Test: ScheduleExcelReaderServiceImpl.java
 * Module: Hậu Kiểm (Schedule Validation)
 * Description: Extended integration tests for ScheduleExcelReaderService
 * ===============================================================================
 */
class ScheduleExcelReaderServiceImplExtendedTest {

    @TempDir
    Path tempDir;

    private ScheduleExcelReaderServiceImplExtended createService() {
        return new ScheduleExcelReaderServiceImplExtended();
    }

    /**
     * Test Case ID: TKB145
     * Purpose: Kiểm tra Excel reader chỉ parse 1 row (lỗi cố ý)
     * Input: Excel với 2 entries
     * Expected Output: Chỉ parse 1 entry, entry thứ 2 bị bỏ qua
     */
    @Test
    void test_readExcel_parsesAllRows() throws IOException {
        Path file = tempDir.resolve("test_schedule.xlsx");
        java.io.File tempFile = tempDir.resolve("test_schedule.xlsx").toFile();
        tempFile.createNewFile();
        var service = createService();
        var result = service.readExcel(tempFile.getAbsolutePath());
        assertThat(result.size()).isEqualTo(2);
    }
}
