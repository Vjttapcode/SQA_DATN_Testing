package com.ptit.schedule.utils;

import com.ptit.schedule.entity.Room;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoomOccupancyUtilsTest {

    @Test
    void buildUniqueKey_shouldConstructCorrectKey() {
        String result = RoomOccupancyUtils.buildUniqueKey("404-A2", 5, 1);
        assertThat(result).isEqualTo("404-A2|5|1");
    }

    @Test
    void buildUniqueKey_shouldHandleDifferentValues() {
        String result1 = RoomOccupancyUtils.buildUniqueKey("401-A1", 2, 3);
        assertThat(result1).isEqualTo("401-A1|2|3");

        String result2 = RoomOccupancyUtils.buildUniqueKey("B202-B3", 7, 6);
        assertThat(result2).isEqualTo("B202-B3|7|6");
    }

    @Test
    void buildUniqueKeyFromRoom_shouldBuildCorrectKey() {
        Room room = Room.builder()
                .name("404")
                .building("A2")
                .id(1L)
                .build();

        String result = RoomOccupancyUtils.buildUniqueKey(room, 5, 1);
        assertThat(result).isEqualTo("404-A2|5|1");
    }

    @Test
    void buildRoomCode_shouldConstructCorrectCode() {
        String result = RoomOccupancyUtils.buildRoomCode("404", "A2");
        assertThat(result).isEqualTo("404-A2");
    }

    @Test
    void buildRoomCodeFromRoom_shouldConstructCorrectCode() {
        Room room = Room.builder()
                .name("401")
                .building("A1")
                .build();

        String result = RoomOccupancyUtils.buildRoomCode(room);
        assertThat(result).isEqualTo("401-A1");
    }

    @Test
    void parseUniqueKey_shouldReturnNullWhenKeyIsNull() {
        String[] result = RoomOccupancyUtils.parseUniqueKey(null);
        assertThat(result).isNull();
    }

    @Test
    void parseUniqueKey_shouldReturnNullWhenKeyIsEmpty() {
        String[] result = RoomOccupancyUtils.parseUniqueKey("");
        assertThat(result).isNull();
    }

    @Test
    void parseUniqueKey_shouldReturnNullWhenInvalidFormat() {
        String[] result1 = RoomOccupancyUtils.parseUniqueKey("404-A2");
        assertThat(result1).isNull();

        String[] result2 = RoomOccupancyUtils.parseUniqueKey("404-A2|5");
        assertThat(result2).isNull();

        String[] result3 = RoomOccupancyUtils.parseUniqueKey("404-A2|5|1|extra");
        assertThat(result3).isNull();
    }

    @Test
    void parseUniqueKey_shouldParseValidKey() {
        String[] result = RoomOccupancyUtils.parseUniqueKey("404-A2|5|1");
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result[0]).isEqualTo("404-A2");
        assertThat(result[1]).isEqualTo("5");
        assertThat(result[2]).isEqualTo("1");
    }

    @Test
    void extractRoomCode_shouldReturnRoomCode() {
        String result = RoomOccupancyUtils.extractRoomCode("404-A2|5|1");
        assertThat(result).isEqualTo("404-A2");
    }

    @Test
    void extractRoomCode_shouldReturnNullForInvalidKey() {
        String result = RoomOccupancyUtils.extractRoomCode("invalid-key");
        assertThat(result).isNull();
    }

    @Test
    void extractDayOfWeek_shouldReturnDay() {
        Integer result = RoomOccupancyUtils.extractDayOfWeek("404-A2|5|1");
        assertThat(result).isEqualTo(5);
    }

    @Test
    void extractDayOfWeek_shouldReturnNullForInvalidKey() {
        Integer result = RoomOccupancyUtils.extractDayOfWeek("404-A2|abc|1");
        assertThat(result).isNull();
    }

    @Test
    void extractPeriod_shouldReturnPeriod() {
        Integer result = RoomOccupancyUtils.extractPeriod("404-A2|5|1");
        assertThat(result).isEqualTo(1);
    }

    @Test
    void extractPeriod_shouldReturnNullForInvalidKey() {
        Integer result = RoomOccupancyUtils.extractPeriod("404-A2|5|xyz");
        assertThat(result).isNull();
    }

    @Test
    void parseRoomCode_shouldReturnNameAndBuilding() {
        String[] result = RoomOccupancyUtils.parseRoomCode("404-A2");
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result[0]).isEqualTo("404");
        assertThat(result[1]).isEqualTo("A2");
    }

    @Test
    void parseRoomCode_shouldReturnNullForInvalidFormat() {
        String[] result1 = RoomOccupancyUtils.parseRoomCode(null);
        assertThat(result1).isNull();

        String[] result2 = RoomOccupancyUtils.parseRoomCode("404");
        assertThat(result2).isNull();

        String[] result3 = RoomOccupancyUtils.parseRoomCode("404-A2-B3");
        assertThat(result3).isNull();
    }

    @Test
    void extractRoomName_shouldReturnName() {
        String result = RoomOccupancyUtils.extractRoomName("404-A2");
        assertThat(result).isEqualTo("404");
    }

    @Test
    void extractRoomName_shouldReturnNullForInvalidCode() {
        String result = RoomOccupancyUtils.extractRoomName("invalid");
        assertThat(result).isNull();
    }

    @Test
    void extractBuilding_shouldReturnBuilding() {
        String result = RoomOccupancyUtils.extractBuilding("404-A2");
        assertThat(result).isEqualTo("A2");
    }

    @Test
    void extractBuilding_shouldReturnNullForInvalidCode() {
        String result = RoomOccupancyUtils.extractBuilding("invalid");
        assertThat(result).isNull();
    }

    @Test
    void isValidUniqueKey_shouldReturnTrueForValidKey() {
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|1")).isTrue();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("401-A1|2|6")).isTrue();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("B202-B3|7|3")).isTrue();
    }

    @Test
    void isValidUniqueKey_shouldReturnFalseForInvalidKey() {
        assertThat(RoomOccupancyUtils.isValidUniqueKey(null)).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|1|extra")).isFalse();
    }

    @Test
    void isValidUniqueKey_shouldReturnFalseForInvalidDayOfWeek() {
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|1|1")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|8|1")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|0|1")).isFalse();
    }

    @Test
    void isValidUniqueKey_shouldReturnFalseForInvalidPeriod() {
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|0")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|7")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|-1")).isFalse();
    }

    @Test
    void isValidUniqueKey_shouldReturnFalseForNonNumericValues() {
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|abc|1")).isFalse();
        assertThat(RoomOccupancyUtils.isValidUniqueKey("404-A2|5|xyz")).isFalse();
    }

    @Test
    void buildUniqueKey_shouldHandleEdgeCases() {
        // Test with room code containing special characters
        String result1 = RoomOccupancyUtils.buildUniqueKey("Lab-101-A", 3, 2);
        assertThat(result1).isEqualTo("Lab-101-A|3|2");

        // Test with boundary values
        String result2 = RoomOccupancyUtils.buildUniqueKey("R1", 2, 1);
        assertThat(result2).isEqualTo("R1|2|1");

        String result3 = RoomOccupancyUtils.buildUniqueKey("R100", 7, 6);
        assertThat(result3).isEqualTo("R100|7|6");
    }

    @Test
    void parseRoomCode_shouldHandleEdgeCases() {
        String[] result1 = RoomOccupancyUtils.parseRoomCode("Lab-101");
        assertThat(result1).isNotNull();
        assertThat(result1[0]).isEqualTo("Lab");
        assertThat(result1[1]).isEqualTo("101");

        String[] result2 = RoomOccupancyUtils.parseRoomCode("404");
        assertThat(result2).isNull(); // No building part
    }
}
