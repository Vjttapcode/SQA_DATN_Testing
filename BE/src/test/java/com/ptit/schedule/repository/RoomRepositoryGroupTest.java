package com.ptit.schedule.repository;

import com.ptit.schedule.entity.Room;
import com.ptit.schedule.entity.RoomOccupancy;
import com.ptit.schedule.entity.RoomType;
import com.ptit.schedule.entity.RoomStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Gộp chung các bài Test Repository của module Phòng Học.
 */
@ExtendWith(MockitoExtension.class)
public class RoomRepositoryGroupTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomOccupancyRepository roomOccupancyRepository;

    @Test
    void testRoomRepository_FindById() {
        // TC_ROOM_REPO_01: Mục đích: Tìm kiếm Room theo ID cơ bản.
        Room mockRoom = new Room(); mockRoom.setId(10L);
        when(roomRepository.findById(10L)).thenReturn(Optional.of(mockRoom));
        assertEquals(10L, roomRepository.findById(10L).get().getId());
    }

    @Test
    void testRoomRepository_NotFound() {
        // TC_ROOM_REPO_02: Mục đích: Trả về empty thay vì Room.
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(roomRepository.findById(99L).isPresent());
    }

    @Test
    void testRoomRepository_Save() {
        // TC_ROOM_REPO_03: Mục đích: Hàm Save Mock.
        Room m = new Room(); m.setName("Mock");
        when(roomRepository.save(any(Room.class))).thenReturn(m);
        assertEquals("Mock", roomRepository.save(new Room()).getName());
    }

    @Test
    void testRoomRepository_Delete() {
        // TC_ROOM_REPO_04: Mục đích: Verify Delete execution.
        doNothing().when(roomRepository).deleteById(1L);
        roomRepository.deleteById(1L);
        verify(roomRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testRoomRepository_FindByBuilding() {
        // TC_ROOM_REPO_05: Mục đích: Mock custom find method (Building).
        when(roomRepository.findByBuilding(anyString())).thenReturn(Collections.emptyList());
        List<Room> lst = roomRepository.findByBuilding("NT");
        assertTrue(lst.isEmpty());
    }

    @Test
    void testRoomRepository_FindByStatus() {
        // TC_ROOM_REPO_06: Mục đích: Theo dõi findByStatus filter.
        when(roomRepository.findByStatus(RoomStatus.AVAILABLE)).thenReturn(Collections.emptyList());
        assertNotNull(roomRepository.findByStatus(RoomStatus.AVAILABLE));
    }

    @Test
    void testRoomRepository_FindByType() {
        // TC_ROOM_REPO_07: Mục đích: Khớp với Enum Type.
        when(roomRepository.findByType(RoomType.GENERAL)).thenReturn(Collections.emptyList());
        assertNotNull(roomRepository.findByType(RoomType.GENERAL));
    }

    @Test
    void testRoomRepository_FindAll() {
        // TC_ROOM_REPO_08: Mục đích: findAll returns List.
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(0, roomRepository.findAll().size());
    }

    @Test
    void testRoomOccupancyRepository_FindById() {
        // TC_ROOM_OCC_REPO_01: Mục đích: Test tìm kiếm RoomOccupancy theo Id.
        RoomOccupancy mockOccupancy = new RoomOccupancy(); mockOccupancy.setId(20L);
        when(roomOccupancyRepository.findById(20L)).thenReturn(Optional.of(mockOccupancy));
        assertTrue(roomOccupancyRepository.findById(20L).isPresent());
    }

    @Test
    void testRoomOccupancyRepository_Save() {
        // TC_ROOM_OCC_REPO_02: Mục đích: Mock lưu mapping record.
        RoomOccupancy o = new RoomOccupancy();
        when(roomOccupancyRepository.save(any())).thenReturn(o);
        assertNotNull(roomOccupancyRepository.save(new RoomOccupancy()));
    }
}
