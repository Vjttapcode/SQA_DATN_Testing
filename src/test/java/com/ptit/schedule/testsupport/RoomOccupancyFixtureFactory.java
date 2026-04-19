package com.ptit.schedule.testsupport;

import com.ptit.schedule.entity.Room;
import com.ptit.schedule.entity.RoomOccupancy;
import com.ptit.schedule.entity.Semester;

public final class RoomOccupancyFixtureFactory {

    private RoomOccupancyFixtureFactory() {
    }

    public static RoomOccupancy createOccupancy(
            Room room,
            Semester semester,
            int dayOfWeek,
            int period,
            String note) {
        return RoomOccupancy.builder()
                .room(room)
                .semester(semester)
                .dayOfWeek(dayOfWeek)
                .period(period)
                .uniqueKey(room.getName() + "-" + room.getBuilding() + "|" + dayOfWeek + "|" + period)
                .note(note)
                .build();
    }
}
