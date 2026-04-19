package com.ptit.schedule.controller;

import com.ptit.schedule.dto.ApiResponse;
import com.ptit.schedule.dto.RoomRequest;
import com.ptit.schedule.dto.RoomResponse;
import com.ptit.schedule.dto.RoomStatusUpdateRequest;
import com.ptit.schedule.dto.RoomBulkStatusUpdateRequest;
import com.ptit.schedule.entity.RoomStatus;
import com.ptit.schedule.entity.RoomType;
import com.ptit.schedule.service.RoomOccupancyService;
import com.ptit.schedule.service.RoomService;
import com.ptit.schedule.service.ScheduleService;
import com.ptit.schedule.service.SubjectRoomMappingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Controller test của module Phòng Học.
 */
@ExtendWith(MockitoExtension.class)
public class RoomControllerGroupTest {

    @Mock private RoomService roomService;
    @Mock private ScheduleService scheduleService;
    @Mock private SubjectRoomMappingService mappingService;
    @Mock private RoomOccupancyService occupancyService;

    @InjectMocks private RoomController roomController;
    @InjectMocks private RoomOccupancyController occupancyController;

    @Test
    void testGetAllRooms() {
        // TC_ROOM_CTRL_01: Lấy tất cả phòng.
        when(roomService.getAllRooms()).thenReturn(Collections.singletonList(new RoomResponse()));
        ResponseEntity<ApiResponse<List<RoomResponse>>> resp = roomController.getAllRooms();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().isSuccess());
    }

    @Test
    void testGetRoomById() {
        // TC_ROOM_CTRL_02: Lấy phòng theo ID.
        when(roomService.getRoomById(1L)).thenReturn(new RoomResponse());
        ResponseEntity<ApiResponse<RoomResponse>> resp = roomController.getRoomById(1L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testCreateRoom() {
        // TC_ROOM_CTRL_03: Tạo phòng.
        when(roomService.createRoom(any())).thenReturn(new RoomResponse());
        ResponseEntity<ApiResponse<RoomResponse>> resp = roomController.createRoom(new RoomRequest());
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void testUpdateRoom() {
        // TC_ROOM_CTRL_04: Cập nhật phòng.
        when(roomService.updateRoom(anyLong(), any())).thenReturn(new RoomResponse());
        ResponseEntity<ApiResponse<RoomResponse>> resp = roomController.updateRoom(2L, new RoomRequest());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testDeleteRoom() {
        // TC_ROOM_CTRL_05: Xóa phòng.
        doNothing().when(roomService).deleteRoom(3L);
        ResponseEntity<ApiResponse<Void>> resp = roomController.deleteRoom(3L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(roomService, times(1)).deleteRoom(3L);
    }

    @Test
    void testUpdateRoomStatus() {
        // TC_ROOM_CTRL_06: Đổi trạng thái 1 phòng.
        when(roomService.updateRoomStatus(anyLong(), any())).thenReturn(new RoomResponse());
        ResponseEntity<ApiResponse<RoomResponse>> resp = roomController.updateRoomStatus(1L, new RoomStatusUpdateRequest());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testBulkUpdateRoomStatus() {
        // TC_ROOM_CTRL_07: Đổi trạng thái hàng loạt.
        when(roomService.bulkUpdateRoomStatus(any())).thenReturn(Collections.emptyList());
        ResponseEntity<ApiResponse<List<RoomResponse>>> resp = roomController.bulkUpdateRoomStatus(new RoomBulkStatusUpdateRequest());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testGetRoomsByBuilding() {
        // TC_ROOM_CTRL_08: Lọc theo building.
        when(roomService.getRoomsByBuilding("A1")).thenReturn(Collections.emptyList());
        ResponseEntity<ApiResponse<List<RoomResponse>>> resp = roomController.getRoomsByBuilding("A1");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testGetRoomsByStatus() {
        // TC_ROOM_CTRL_09: Lọc theo status.
        when(roomService.getRoomsByStatus(RoomStatus.AVAILABLE)).thenReturn(Collections.emptyList());
        ResponseEntity<ApiResponse<List<RoomResponse>>> resp = roomController.getRoomsByStatus(RoomStatus.AVAILABLE);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testGetRoomsByType() {
        // TC_ROOM_CTRL_10: Lọc theo Type.
        when(roomService.getRoomsByType(RoomType.GENERAL)).thenReturn(Collections.emptyList());
        ResponseEntity<ApiResponse<List<RoomResponse>>> resp = roomController.getRoomsByType(RoomType.GENERAL);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testOccupancyController() {
        // TC_ROOM_OCC_CTRL_01: Đảm bảo RoomOccupancyController tạo được bởi mock.
        assertNotNull(occupancyController);
    }
}
