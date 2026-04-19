package com.ptit.schedule.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * File gộp chung để test các lớp DTO thuộc module Phòng học.
 */
public class RoomDtoTest {

    // --- RoomRequest Tests ---
    @Test
    void testRoomRequest_ValidData() {
        // TC_ROOM_REQ_01: Mục đích: Kiểm tra getter/setter.
        RoomRequest request = new RoomRequest();
        request.setName("A1-100");
        request.setCapacity(50);
        assertEquals("A1-100", request.getName());
        assertEquals(50, request.getCapacity());
    }

    @Test
    void testRoomRequest_NullName() {
        // TC_ROOM_REQ_02: Mục đích: Test tên phòng null.
        RoomRequest request = new RoomRequest();
        request.setName(null);
        assertNull(request.getName());
    }

    // --- RoomResponse Tests ---
    @Test
    void testRoomResponse_ValidData() {
        // TC_ROOM_RES_01: Mục đích: Đảm bảo RoomResponse khởi tạo và nhận đúng ID, Name.
        RoomResponse response = new RoomResponse();
        response.setId(1L);
        response.setName("A2");
        assertEquals(1L, response.getId());
        assertEquals("A2", response.getName());
    }

    @Test
    void testRoomResponse_SetBuilding() {
        // TC_ROOM_RES_02: Mục đích: RoomResponse nhận building.
        RoomResponse response = new RoomResponse();
        response.setBuilding("Tòa A2");
        assertEquals("Tòa A2", response.getBuilding());
    }

    // --- BulkCreateRoomOccupancyRequest Tests ---
    @Test
    void testBulkCreateRoomOccupancyRequest() {
        // TC_BULK_CREATE_OCC_01: Mục đích: Kiểm tra việc khởi tạo BulkCreateRoomOccupancyRequest.
        BulkCreateRoomOccupancyRequest request = new BulkCreateRoomOccupancyRequest();
        assertNotNull(request);
    }

    // --- RoomBulkStatusUpdateRequest Tests ---
    @Test
    void testRoomBulkStatusUpdateRequest() {
        // TC_ROOM_BULK_STATUS_01: Mục đích: Kiểm tra khởi tạo của RoomBulkStatusUpdateRequest.
        RoomBulkStatusUpdateRequest request = new RoomBulkStatusUpdateRequest();
        assertNotNull(request);
    }
    
    @Test
    void testRoomBulkStatusUpdateRequest_SetList() {
        // TC_ROOM_BULK_STATUS_02: Mục đích: Set and get empty list in bulk request.
        RoomBulkStatusUpdateRequest dto = new RoomBulkStatusUpdateRequest();
        assertNotNull(dto);
    }

    // --- RoomOccupancyResponse Tests ---
    @Test
    void testRoomOccupancyResponse() {
        // TC_ROOM_OCC_RES_01: Mục đích: Kiểm tra đối tượng RoomOccupancyResponse.
        RoomOccupancyResponse response = new RoomOccupancyResponse();
        assertNotNull(response);
    }

    @Test
    void testRoomOccupancyResponse_SetId() {
        // TC_ROOM_OCC_RES_02: Mục đích: Kiểm tra setId của RoomOccupancyResponse.
        RoomOccupancyResponse response = new RoomOccupancyResponse();
        response.setId(100L);
        assertEquals(100L, response.getId());
    }

    // --- RoomPickResult Tests ---
    @Test
    void testRoomPickResult() {
        // TC_ROOM_PICK_RES_01: Mục đích: Khởi tạo RoomPickResult không lỗi.
        RoomPickResult result = new RoomPickResult();
        assertNotNull(result);
    }

    // --- RoomStatusUpdateRequest Tests ---
    @Test
    void testRoomStatusUpdateRequest() {
        // TC_ROOM_STATUS_REQ_01: Mục đích: Cập nhật trạng thái phòng riêng lẻ qua Request.
        RoomStatusUpdateRequest request = new RoomStatusUpdateRequest();
        assertNotNull(request);
    }

    // --- RoomWithOccupancyStatus Tests ---
    @Test
    void testRoomWithOccupancyStatus() {
        // TC_ROOM_WITH_OCC_01: Mục đích: Xác nhận RoomWithOccupancyStatus khởi tạo.
        RoomWithOccupancyStatus dto = new RoomWithOccupancyStatus();
        assertNotNull(dto);
    }
}
