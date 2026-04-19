package com.ptit.schedule.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===============================================================================
 * TEST CASE ID: TKB028
 * File Under Test: PagedResponse.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for PagedResponse DTO class
 * ===============================================================================
 */
@DisplayName("Test Case ID: TKB028 - PagedResponse Tests")
public class PagedResponseTest {

    // =============================================================================
    // HELPER METHODS
    // =============================================================================

    /**
     * Helper method to create PagedResponse with test data
     */
    private <T> PagedResponse<T> createPagedResponse(List<T> items, int page, int size, 
                                                     long totalElements, int totalPages) {
        return PagedResponse.<T>builder()
                .items(items)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    // =============================================================================
    // TEST CASES: FACTORY METHOD - of()
    // =============================================================================

    /**
     * Test Case ID: TKB028
     * Purpose: Verify of() factory method creates PagedResponse correctly
     * Input: items=[A,B,C], page=1, size=10, totalElements=25, totalPages=3
     * Expected Output: All fields accessible correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB028: of() factory method creates correct response")
    public void test_factoryMethod_of_createsCorrectResponse() {
        // Arrange: Prepare test data
        List<String> items = Arrays.asList("Schedule A", "Schedule B", "Schedule C");
        int page = 1;
        int size = 10;
        long totalElements = 25;
        int totalPages = 3;

        // Act: Create PagedResponse using factory method
        PagedResponse<String> response = PagedResponse.of(items, page, size, totalElements, totalPages);

        // Assert: Verify all fields
        assertNotNull(response, "PagedResponse should not be null");
        assertEquals(items, response.getItems(), "Items should match");
        assertEquals(page, response.getPage(), "Page should match");
        assertEquals(size, response.getSize(), "Size should match");
        assertEquals(totalElements, response.getTotalElements(), "Total elements should match");
        assertEquals(totalPages, response.getTotalPages(), "Total pages should match");
    }

    /**
     * Test Case ID: TKB029
     * Purpose: Verify of() with empty items list
     * Input: items=[], page=0, size=10, totalElements=0, totalPages=0
     * Expected Output: Empty items, zero totals
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB029: of() with empty items list")
    public void test_factoryMethod_of_emptyItems() {
        // Arrange: Empty items
        List<String> emptyItems = Collections.emptyList();

        // Act: Create PagedResponse with empty items
        PagedResponse<String> response = PagedResponse.of(emptyItems, 0, 10, 0, 0);

        // Assert: Verify empty state
        assertNotNull(response.getItems(), "Items should not be null");
        assertTrue(response.getItems().isEmpty(), "Items should be empty");
        assertEquals(0, response.getTotalElements(), "Total elements should be 0");
        assertEquals(0, response.getTotalPages(), "Total pages should be 0");
    }

    /**
     * Test Case ID: TKB030
     * Purpose: Verify of() with null items list
     * Input: items=null
     * Expected Output: Items field is null
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB030: of() with null items list")
    public void test_factoryMethod_of_nullItems() {
        // Act: Create PagedResponse with null items
        PagedResponse<String> response = PagedResponse.<String>of(null, 0, 10, 0, 0);

        // Assert: Verify null items handled
        assertNull(response.getItems(), "Items should be null");
    }

    // =============================================================================
    // TEST CASES: BUILDER PATTERN
    // =============================================================================

    /**
     * Test Case ID: TKB031
     * Purpose: Verify builder pattern creates complete PagedResponse
     * Input: All fields via builder
     * Expected Output: All fields accessible
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB031: Builder creates complete PagedResponse")
    public void test_builder_createsCompleteResponse() {
        // Arrange: Prepare complete data
        List<String> items = Arrays.asList("Item1", "Item2", "Item3");
        int page = 2;
        int size = 5;
        long totalElements = 100;
        int totalPages = 20;

        // Act: Build complete PagedResponse
        PagedResponse<String> response = PagedResponse.<String>builder()
                .items(items)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        // Assert: Verify all fields
        assertEquals(items, response.getItems(), "Items should match");
        assertEquals(page, response.getPage(), "Page should match");
        assertEquals(size, response.getSize(), "Size should match");
        assertEquals(totalElements, response.getTotalElements(), "Total elements should match");
        assertEquals(totalPages, response.getTotalPages(), "Total pages should match");
    }

    /**
     * Test Case ID: TKB032
     * Purpose: Verify builder with partial fields
     * Input: Only items and page
     * Expected Output: Other fields have default values
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB032: Builder with partial fields")
    public void test_builder_partialFields() {
        // Act: Build with only items and page
        PagedResponse<String> response = PagedResponse.<String>builder()
                .items(Collections.singletonList("Single Item"))
                .page(0)
                .build();

        // Assert: Verify partial fields
        assertNotNull(response.getItems(), "Items should not be null");
        assertEquals(0, response.getPage(), "Page should be 0");
        assertEquals(0, response.getSize(), "Size should be default (0)");
        assertEquals(0, response.getTotalElements(), "Total elements should be 0");
        assertEquals(0, response.getTotalPages(), "Total pages should be 0");
    }

    // =============================================================================
    // TEST CASES: PAGE CALCULATIONS
    // =============================================================================

    /**
     * Test Case ID: TKB033
     * Purpose: Verify totalPages calculation for exact division
     * Input: totalElements=100, size=10
     * Expected Output: totalPages=10
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB033: Total pages for exact division")
    public void test_totalPages_exactDivision() {
        // Arrange: Exact division parameters
        List<String> items = Arrays.asList("Item1", "Item2");
        int page = 9;
        int size = 10;
        long totalElements = 100;
        int totalPages = 10;

        // Act: Create PagedResponse
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify total pages
        assertEquals(totalPages, response.getTotalPages(), "Total pages should be 10");
        assertEquals(totalElements, response.getTotalElements(), "Total elements should be 100");
    }

    /**
     * Test Case ID: TKB034
     * Purpose: Verify totalPages calculation for partial last page
     * Input: totalElements=95, size=10
     * Expected Output: totalPages=10 (9 full + 1 partial)
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB034: Total pages for partial last page")
    public void test_totalPages_partialLastPage() {
        // Arrange: Partial last page parameters
        List<String> items = Collections.singletonList("Last Item");
        int page = 9;
        int size = 10;
        long totalElements = 95;
        int totalPages = 10;

        // Act: Create PagedResponse
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify total pages (10 pages: 9 full pages + 1 partial)
        assertEquals(totalPages, response.getTotalPages(), "Total pages should be 10");
        assertEquals(1, response.getItems().size(), "Last page should have 1 item");
    }

    /**
     * Test Case ID: TKB035
     * Purpose: Verify single page scenario
     * Input: totalElements=5, size=10
     * Expected Output: totalPages=1
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB035: Single page scenario")
    public void test_singlePage_scenario() {
        // Arrange: Single page parameters
        List<String> items = Arrays.asList("A", "B", "C", "D", "E");
        int page = 0;
        int size = 10;
        long totalElements = 5;
        int totalPages = 1;

        // Act: Create PagedResponse
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify single page
        assertEquals(totalPages, response.getTotalPages(), "Should have exactly 1 page");
        assertEquals(5, response.getItems().size(), "Items should equal total elements");
    }

    // =============================================================================
    // TEST CASES: EDGE CASES
    // =============================================================================

    /**
     * Test Case ID: TKB036
     * Purpose: Verify first page (page=0) with full items
     * Input: page=0, size=20, totalElements=100
     * Expected Output: First page correctly identified
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB036: First page (page=0)")
    public void test_firstPage_pageZero() {
        // Arrange: First page data
        List<String> items = Arrays.asList("Item1", "Item2");
        int page = 0;
        int size = 20;
        long totalElements = 100;
        int totalPages = 5;

        // Act: Create PagedResponse for first page
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify first page
        assertEquals(0, response.getPage(), "First page should be 0");
        assertEquals(size, response.getSize(), "Page size should match");
        assertTrue(response.getTotalElements() > 0, "Total elements should be greater than 0");
    }

    /**
     * Test Case ID: TKB037
     * Purpose: Verify last page detection
     * Input: page=totalPages-1
     * Expected Output: Last page correctly identified
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB037: Last page detection")
    public void test_lastPage_detection() {
        // Arrange: Last page data (0-indexed, so page 4 is 5th page)
        List<String> items = Collections.singletonList("Last Page Item");
        int page = 4;
        int size = 20;
        long totalElements = 95;
        int totalPages = 5;

        // Act: Create PagedResponse for last page
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify last page
        assertEquals(page, response.getPage(), "Last page should match");
        assertEquals(totalPages, response.getTotalPages(), "Total pages should match");
        assertEquals(1, response.getItems().size(), "Last page may have fewer items");
    }

    /**
     * Test Case ID: TKB038
     * Purpose: Verify large dataset handling
     * Input: totalElements=1_000_000, totalPages=50_000
     * Expected Output: Large values preserved correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB038: Large dataset handling")
    public void test_largeDataset_handledCorrectly() {
        // Arrange: Large dataset parameters
        List<String> items = Arrays.asList("Large1", "Large2");
        int page = 49999;
        int size = 20;
        long totalElements = 1_000_000L;
        int totalPages = 50000;

        // Act: Create PagedResponse with large values
        PagedResponse<String> response = createPagedResponse(items, page, size, totalElements, totalPages);

        // Assert: Verify large values preserved
        assertEquals(totalElements, response.getTotalElements(), "Large total elements preserved");
        assertEquals(totalPages, response.getTotalPages(), "Large total pages preserved");
    }

    /**
     * Test Case ID: TKB039
     * Purpose: Verify complex object content
     * Input: Items list of Map objects
     * Expected Output: Complex objects preserved correctly
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB039: Complex object content")
    public void test_complexObjectContent() {
        // Arrange: Complex objects as items
        List<java.util.Map<String, Object>> items = Arrays.asList(
                java.util.Map.of("id", 1, "name", "Schedule 1", "room", "401-A2"),
                java.util.Map.of("id", 2, "name", "Schedule 2", "room", "402-A2")
        );

        // Act: Create PagedResponse with complex items
        PagedResponse<java.util.Map<String, Object>> response = createPagedResponse(items, 0, 10, 2, 1);

        // Assert: Verify complex content
        assertEquals(2, response.getItems().size(), "Should have 2 complex items");
        assertEquals("Schedule 1", response.getItems().get(0).get("name"), "First item name should match");
        assertEquals("401-A2", response.getItems().get(0).get("room"), "First item room should match");
    }

    /**
     * Test Case ID: TKB040
     * Purpose: Verify PagedResponse has equivalent fields to PageResponse
     * Input: Similar data for both
     * Expected Output: PagedResponse.items equivalent to PageResponse.content
     * CheckDB: No database access required
     */
    @Test
    @DisplayName("TKB040: PagedResponse vs PageResponse field mapping")
    public void test_vsPageResponse_fieldMapping() {
        // Arrange: Same data for both response types
        List<String> data = Arrays.asList("A", "B", "C");
        
        // Act: Create both response types
        PagedResponse<String> pagedResponse = PagedResponse.of(data, 0, 10, 3, 1);
        PageResponse<String> pageResponse = PageResponse.<String>builder()
                .content(data)
                .pageNum(0)
                .pageSize(10)
                .total(3)
                .build();

        // Assert: Verify equivalent fields
        assertEquals(pageResponse.getContent().size(), pagedResponse.getItems().size(), 
                "Items and content should have same size");
        assertEquals(pageResponse.getPageNum(), pagedResponse.getPage(), 
                "PageNum and page should be equivalent");
        assertEquals(pageResponse.getPageSize(), pagedResponse.getSize(), 
                "PageSize and size should be equivalent");
        assertEquals(pageResponse.getTotal(), pagedResponse.getTotalElements(), 
                "Total and totalElements should be equivalent");
    }
}
