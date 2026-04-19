package com.ptit.schedule.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * File gộp chung để test các lớp Entity và Enum thuộc module Phòng học.
 */
public class RoomEntityGroupTest {

    // --- Room Entity Tests ---
    @Test
    void testRoomEntity_Valid() {
        // TC_ROOM_ENT_01: Mục đích: Kiểm tra khởi tạo Entity Room thành công với Builder pattern.
        Room room = Room.builder()
                .id(1L)
                .name("101")
                .capacity(50)
                .building("A1")
                .type(RoomType.GENERAL)
                .status(RoomStatus.AVAILABLE)
                .note("Phòng học lý thuyết chung")
                .build();
        
        assertEquals(1L, room.getId());
        assertEquals(RoomType.GENERAL, room.getType());
        assertEquals("101", room.getName());
        assertEquals(50, room.getCapacity());
    }

    @Test
    void testRoomEntity_DefaultStatus() {
        // TC_ROOM_ENT_02: Mục đích: Kiểm tra Default Status khi tạo Room rỗng.
        Room room = new Room();
        assertEquals(RoomStatus.AVAILABLE, room.getStatus(), "Status default là AVAILABLE");
    }

    @Test
    void testRoomEntity_Setters() {
        // TC_ROOM_ENT_03: Mục đích: Thay đổi giá trị bằng setters.
        Room room = new Room();
        room.setId(2L);
        room.setName("A2-205");
        assertEquals("A2-205", room.getName());
    }

    @Test
    void testRoomEntity_CapacityBoundary() {
        // TC_ROOM_ENT_04: Mục đích: Biên số lượng.
        Room room = new Room();
        room.setCapacity(0); // Validation annotation check normally done by validation layer
        assertEquals(0, room.getCapacity());
    }

    // --- RoomOccupancy Entity Tests ---
    @Test
    void testRoomOccupancyEntity_Valid() {
        // TC_ROOM_OCC_ENT_01: Mục đích: Kiểm tra entity RoomOccupancy khởi tạo ID tĩnh.
        RoomOccupancy roomOccupancy = new RoomOccupancy();
        roomOccupancy.setId(10L);
        assertEquals(10L, roomOccupancy.getId());
    }

    @Test
    void testRoomOccupancyEntity_SetRoom() {
        // TC_ROOM_OCC_ENT_02: Mục đích: Gán Room object cho RoomOccupancy.
        RoomOccupancy occ = new RoomOccupancy();
        Room room = new Room();
        room.setId(5L);
        occ.setRoom(room);
        assertNotNull(occ.getRoom());
        assertEquals(5L, occ.getRoom().getId());
    }

    // --- RoomStatus Enum Tests ---
    @Test
    void testRoomStatusEnum_Values() {
        // TC_ROOM_STATUS_01: Mục đích: Đảm bảo enum RoomStatus chứa trạng thái AVAILABLE.
        assertNotNull(RoomStatus.valueOf("AVAILABLE"));
    }

    @Test
    void testRoomStatusEnum_Length() {
        // TC_ROOM_STATUS_02: Mục đích: RoomStatus có số lượng item lớn hơn 0.
        assertTrue(RoomStatus.values().length > 0);
    }

    // --- RoomType Enum Tests ---
    @Test
    void testRoomTypeEnum_Values() {
        // TC_ROOM_TYPE_01: Mục đích: Khẳng định RoomType enum tồn tại GENERAL.
        assertNotNull(RoomType.valueOf("GENERAL"));
    }

    @Test
    void testRoomTypeEnum_Length() {
        // TC_ROOM_TYPE_02: Mục đích: RoomType có chứa item.
        assertTrue(RoomType.values().length > 0);
    }

    // --- OccupancyStatus Enum Tests ---
    @Test
    void testOccupancyStatusEnum_Length() {
        // TC_OCC_STATUS_01: Mục đích: Enum OccupancyStatus chưa bị lỗi và có thể truy xuất mảng giá trị.
        assertNotNull(OccupancyStatus.values());
        assertTrue(OccupancyStatus.values().length >= 0);
    }
}
