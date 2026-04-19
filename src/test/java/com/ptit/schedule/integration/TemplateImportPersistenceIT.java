package com.ptit.schedule.integration;

import com.ptit.schedule.entity.Semester;
import com.ptit.schedule.entity.TKBTemplate;
import com.ptit.schedule.repository.SemesterRepository;
import com.ptit.schedule.repository.TKBTemplateRepository;
import com.ptit.schedule.service.DataLoaderService;
import com.ptit.schedule.testsupport.ScheduleFixtureFactory;
import com.ptit.schedule.testsupport.ScheduleWorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TemplateImportPersistenceIT {

    @Autowired
    private DataLoaderService dataLoaderService;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private TKBTemplateRepository tkbTemplateRepository;

    @Test
    void importDataFromExcel_shouldReplaceTemplatesForTargetSemesterWithinTransaction() {
        // Test Case ID: TKB-DB-003
        String suffix = ScheduleFixtureFactory.nextSuffix("template_db");
        Semester semester = semesterRepository.save(ScheduleFixtureFactory.createSemester(suffix));
        TKBTemplate oldTemplate = tkbTemplateRepository.save(TKBTemplate.builder()
                .templateId("OLD_" + suffix)
                .totalPeriods(14)
                .dayOfWeek(2)
                .kip(1)
                .startPeriod(1)
                .periodLength(2)
                .weekSchedule("[1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]")
                .totalUsed(4)
                .semester(semester)
                .rowOrder(0)
                .build());

        String semesterLabel = semester.getSemesterName() + " " + semester.getAcademicYear();

        String importedLabel = dataLoaderService.importDataFromExcel(
                ScheduleWorkbookFactory.createTemplateImportWorkbook(
                        "template.xlsx",
                        List.of(
                                new ScheduleWorkbookFactory.TemplateRowSpec(14, 2, 1, 1, 2, "TPL_A", List.of(1, 2)),
                                new ScheduleWorkbookFactory.TemplateRowSpec(30, 3, 2, 1, 3, "TPL_B", List.of(1, 3, 5)))),
                semesterLabel);

        List<TKBTemplate> savedTemplates = tkbTemplateRepository.findBySemesterOrderByRowOrderAsc(semester);

        assertThat(importedLabel).isEqualTo(semesterLabel);
        assertThat(savedTemplates).hasSize(2);
        assertThat(savedTemplates).extracting(TKBTemplate::getTemplateId).containsExactly("TPL_A", "TPL_B");
        assertThat(savedTemplates).extracting(TKBTemplate::getId).doesNotContain(oldTemplate.getId());
    }
}
