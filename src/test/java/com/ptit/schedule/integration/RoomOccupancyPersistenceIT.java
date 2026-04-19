package com.ptit.schedule.integration;

import com.ptit.schedule.dto.BulkCreateRoomOccupancyRequest;
import com.ptit.schedule.dto.RoomOccupancyResponse;
import com.ptit.schedule.entity.Room;
import com.ptit.schedule.entity.RoomOccupancy;
import com.ptit.schedule.entity.Semester;
import com.ptit.schedule.repository.RoomOccupancyRepository;
import com.ptit.schedule.repository.RoomRepository;
import com.ptit.schedule.repository.SemesterRepository;
import com.ptit.schedule.service.RoomOccupancyService;
import com.ptit.schedule.testsupport.RoomOccupancyFixtureFactory;
import com.ptit.schedule.testsupport.ScheduleFixtureFactory;
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
class RoomOccupancyPersistenceIT {

    @Autowired
    private RoomOccupancyService roomOccupancyService;

    @Autowired
    private RoomOccupancyRepository roomOccupancyRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void bulkCreateOccupancies_shouldPersistRowsInDatabaseWithinTransaction() {
        // Test Case ID: RO-DB-001
        String suffix = ScheduleFixtureFactory.nextSuffix("occupancy_db");
        Semester semester = semesterRepository.save(ScheduleFixtureFactory.createSemester(suffix));
        Room room = roomRepository.save(ScheduleFixtureFactory.createRoom(suffix));
        long baselineCount = roomOccupancyRepository.findBySemesterId(semester.getId()).size();

        List<RoomOccupancyResponse> created = roomOccupancyService.bulkCreateOccupancies(
                BulkCreateRoomOccupancyRequest.builder()
                        .semesterId(semester.getId())
                        .items(List.of(BulkCreateRoomOccupancyRequest.OccupancyItem.builder()
                                .roomId(room.getId())
                                .dayOfWeek(2)
                                .kip(1)
                                .subjectCode("INT1306")
                                .subjectName("Software Testing")
                                .build()))
                        .build());

        List<RoomOccupancy> storedOccupancies = roomOccupancyRepository.findBySemesterId(semester.getId());

        assertThat(created).hasSize(1);
        assertThat(storedOccupancies).hasSize((int) baselineCount + 1);
        assertThat(storedOccupancies.get(0).getUniqueKey()).isEqualTo(room.getName() + "-" + room.getBuilding() + "|2|1");
    }

    @Test
    void deleteOccupanciesBySemester_shouldRemoveSemesterRowsWithinTransaction() {
        // Test Case ID: RO-DB-002
        String suffix = ScheduleFixtureFactory.nextSuffix("occupancy_delete");
        Semester semester = semesterRepository.save(ScheduleFixtureFactory.createSemester(suffix));
        Room room = roomRepository.save(ScheduleFixtureFactory.createRoom(suffix));
        roomOccupancyRepository.save(RoomOccupancyFixtureFactory.createOccupancy(room, semester, 2, 1, "INT1306"));
        roomOccupancyRepository.save(RoomOccupancyFixtureFactory.createOccupancy(room, semester, 2, 2, "INT1307"));

        assertThat(roomOccupancyRepository.findBySemesterId(semester.getId())).hasSize(2);

        roomOccupancyService.deleteOccupanciesBySemester(semester.getId());

        assertThat(roomOccupancyRepository.findBySemesterId(semester.getId())).isEmpty();
    }
}
