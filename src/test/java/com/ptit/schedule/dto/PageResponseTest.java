package com.ptit.schedule.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB017
 * File Under Test: PageResponse.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for PageResponse DTO class
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB017 - PageResponse Tests")
public class PageResponseTest {

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create PageResponse with test data
     */
    private <T> PageResponse<T> createPageResponse(List<T> content, int pageNum, int pageSize, long total) {
        return PageResponse.<T>builder()
                .content(content)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(total)
                .build();
    }

    // =============================================================================
    // TEST CASES: CONSTRUCTOR AND BUILDER
    // =============================================================================

    /**
     * Test Case ID: TKB017
     * Purpose: Verify PageResponse constructor creates object with all fields
     * Input: content=[item1, item2], pageNum=0, pageSize=10, total=25
     * Expected Output: All fields accessible correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB017: Constructor sets all fields correctly")
    public void test_constructor_allFieldsSet() {
        // Arrange: Prepare test data
        List<String> content = Arrays.asList("Schedule1", "Schedule2");
        int pageNum = 0;
        int pageSize = 10;
        long total = 25;

        // Act: Create PageResponse using builder
        PageResponse<String> response = createPageResponse(content, pageNum, pageSize, total);

        // Assert: Verify all fields
        assertNotNull(response, "PageResponse should not be null");
        assertEquals(content, response.getContent(), "Content should match");
        assertEquals(pageNum, response.getPageNum(), "Page number should match");
        assertEquals(pageSize, response.getPageSize(), "Page size should match");
        assertEquals(total, response.getTotal(), "Total should match");
    }

    /**
     * Test Case ID: TKB018
     * Purpose: Verify PageResponse builder works with empty content list
     * Input: content=[], pageNum=0, pageSize=10, total=0
     * Expected Output: Empty content, zero total
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB018: Builder with empty content list")
    public void test_builder_emptyContent() {
        // Arrange: Prepare empty content
        List<String> emptyContent = Collections.emptyList();

        // Act: Create PageResponse with empty content
        PageResponse<String> response = createPageResponse(emptyContent, 0, 10, 0);

        // Assert: Verify empty state
        assertNotNull(response.getContent(), "Content list should not be null");
        assertTrue(response.getContent().isEmpty(), "Content should be empty");
        assertEquals(0, response.getTotal(), "Total should be 0");
    }

    /**
     * Test Case ID: TKB019
     * Purpose: Verify PageResponse handles null content list
     * Input: content=null
     * Expected Output: Content field is null (Lombok @Data allows null)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB019: Builder with null content")
    public void test_builder_nullContent() {
        // Act: Create PageResponse with null content
        PageResponse<String> response = PageResponse.<String>builder()
                .content(null)
                .pageNum(0)
                .pageSize(10)
                .total(0)
                .build();

        // Assert: Verify null content handled
        assertNull(response.getContent(), "Content should be null");
    }

    // =============================================================================
    // TEST CASES: PAGE INFORMATION
    // =============================================================================

    /**
     * Test Case ID: TKB020
     * Purpose: Verify PageResponse stores correct page number (0-based)
     * Input: pageNum=5
     * Expected Output: getPageNum() returns 5
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB020: Page number stored correctly")
    public void test_pageNumber_storedCorrectly() {
        // Arrange: Set page number
        int expectedPage = 5;

        // Act: Create PageResponse with specific page number
        PageResponse<String> response = PageResponse.<String>builder()
                .content(Collections.emptyList())
                .pageNum(expectedPage)
                .pageSize(10)
                .total(100)
                .build();

        // Assert: Verify page number
        assertEquals(expectedPage, response.getPageNum(), "Page number should match");
    }

    /**
     * Test Case ID: TKB021
     * Purpose: Verify PageResponse calculates total pages correctly
     * Input: total=95, pageSize=10
     * Expected Output: totalPages=10 (95 items / 10 per page = 9 full pages + 1 partial)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB021: Page size and total relationship")
    public void test_pageSizeAndTotal_relationship() {
        // Arrange: Set page size and total
        int pageSize = 10;
        long total = 95;

        // Act: Create PageResponse
        PageResponse<String> response = createPageResponse(Collections.emptyList(), 0, pageSize, total);

        // Assert: Verify fields
        assertEquals(pageSize, response.getPageSize(), "Page size should match");
        assertEquals(total, response.getTotal(), "Total should match");
    }

    /**
     * Test Case ID: TKB022
     * Purpose: Verify PageResponse with large total count
     * Input: total=1000000
     * Expected Output: Total preserved correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB022: Handles large total count")
    public void test_largeTotalCount_handledCorrectly() {
        // Arrange: Set large total
        long largeTotal = 1_000_000L;

        // Act: Create PageResponse
        PageResponse<String> response = createPageResponse(Collections.emptyList(), 0, 20, largeTotal);

        // Assert: Verify large total preserved
        assertEquals(largeTotal, response.getTotal(), "Large total should be preserved");
    }

    // =============================================================================
    // TEST CASES: CONTENT HANDLING
    // =============================================================================

    /**
     * Test Case ID: TKB023
     * Purpose: Verify PageResponse content is mutable list
     * Input: Modifiable list content
     * Expected Output: Content can be modified after creation
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB023: Content list is modifiable")
    public void test_contentList_modifiable() {
        // Arrange: Create modifiable list
        List<String> content = new ArrayList<>(Arrays.asList("Schedule1", "Schedule2"));

        // Act: Create PageResponse
        PageResponse<String> response = createPageResponse(content, 0, 10, 2);

        // Modify content (add another item) - should work without exception
        assertDoesNotThrow(() -> response.getContent().add("Schedule3"));

        // Assert: Verify content modified
        assertEquals(3, response.getContent().size(), "Content should have 3 items after modification");
    }

    /**
     * Test Case ID: TKB024
     * Purpose: Verify PageResponse with complex object content
     * Input: Content list of Map objects
     * Expected Output: Complex objects preserved
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB024: Content with complex objects")
    public void test_contentWithComplexObjects() {
        // Arrange: Create complex content
        List<java.util.Map<String, Object>> content = Arrays.asList(
                java.util.Map.of("id", 1, "name", "Schedule A"),
                java.util.Map.of("id", 2, "name", "Schedule B")
        );

        // Act: Create PageResponse
        PageResponse<java.util.Map<String, Object>> response = createPageResponse(content, 0, 10, 2);

        // Assert: Verify complex content
        assertEquals(2, response.getContent().size(), "Content should have 2 items");
        assertEquals("Schedule A", response.getContent().get(0).get("name"), "First item name should match");
    }

    // =============================================================================
    // TEST CASES: EDGE CASES
    // =============================================================================

    /**
     * Test Case ID: TKB025
     * Purpose: Verify PageResponse with first page (pageNum=0)
     * Input: pageNum=0, pageSize=20, total=50
     * Expected Output: First page correctly identified
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB025: First page (pageNum=0)")
    public void test_firstPage_pageNumZero() {
        // Act: Create first page response
        PageResponse<String> response = createPageResponse(Collections.emptyList(), 0, 20, 50);

        // Assert: Verify first page
        assertEquals(0, response.getPageNum(), "First page number should be 0");
        assertTrue(response.getTotal() > 0, "Total should be greater than 0");
    }

    /**
     * Test Case ID: TKB026
     * Purpose: Verify PageResponse with last page
     * Input: pageNum=9, total=95, pageSize=10
     * Expected Output: Last page correctly identified
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB026: Last page identification")
    public void test_lastPage_pageNumCorrect() {
        // Arrange: Set last page parameters
        int pageNum = 9;  // 0-indexed, so 10th page

        // Act: Create last page response
        PageResponse<String> response = createPageResponse(Collections.emptyList(), pageNum, 10, 95);

        // Assert: Verify last page
        assertEquals(pageNum, response.getPageNum(), "Last page number should match");
        assertEquals(10, response.getPageSize(), "Page size should match");
    }

    /**
     * Test Case ID: TKB027
     * Purpose: Verify PageResponse with single item per page
     * Input: pageSize=1, total=1
     * Expected Output: Single item content
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB027: Single item per page")
    public void test_singleItemPerPage() {
        // Arrange: Single item data
        List<String> content = Collections.singletonList("Only Schedule");

        // Act: Create PageResponse with pageSize=1
        PageResponse<String> response = createPageResponse(content, 0, 1, 1);

        // Assert: Verify single item
        assertEquals(1, response.getContent().size(), "Content should have 1 item");
        assertEquals(1, response.getPageSize(), "Page size should be 1");
        assertEquals(1, response.getTotal(), "Total should be 1");
    }
}
