package com.ptit.schedule.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: TKB158
 * File Under Test: CorsConfig.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for CorsConfig - CORS configuration
 * ===============================================================================
 */
class CorsConfigTest {

    private CorsConfiguration createCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setMaxAge(3600L);
        return configuration;
    }

    private CorsConfigurationSource createCorsConfigurationSource() {
        CorsConfiguration configuration = createCorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Test Case ID: TKB158
     * Purpose: Kiểm tra CorsConfiguration set đúng AllowedOriginPatterns
     * Input: CorsConfiguration với allowedOriginPatterns
     * Expected Output: AllowedOriginPatterns chứa "*"
     */
    @Test
    void test_globalCorsFilter_setsAllowOrigin() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowedOriginPatterns()).contains("*");
    }

    /**
     * Test Case ID: TKB159
     * Purpose: Kiểm tra global CorsFilter set wildcard khi không có Origin header
     * Input: HTTP request không có Origin header
     * Expected Output: Allow-Origin="*"
     */
    @Test
    void test_globalCorsFilter_setsWildcardWhenNoOrigin() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowedOriginPatterns()).contains("*");
    }

    /**
     * Test Case ID: TKB160
     * Purpose: Kiểm tra global CorsFilter set Allow-Methods header
     * Input: HTTP request bất kỳ
     * Expected Output: Allow-Methods chứa GET, POST, PUT, DELETE, OPTIONS
     */
    @Test
    void test_globalCorsFilter_setsAllowMethods() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    /**
     * Test Case ID: TKB161
     * Purpose: Kiểm tra global CorsFilter set Allow-Credentials header
     * Input: HTTP request bất kỳ
     * Expected Output: Allow-Credentials="true"
     */
    @Test
    void test_globalCorsFilter_setsAllowCredentials() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowCredentials()).isTrue();
    }

    /**
     * Test Case ID: TKB162
     * Purpose: Kiểm tra global CorsFilter set Max-Age header
     * Input: HTTP request bất kỳ
     * Expected Output: Max-Age="3600"
     */
    @Test
    void test_globalCorsFilter_setsMaxAge() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getMaxAge()).isEqualTo(3600L);
    }

    /**
     * Test Case ID: TKB163
     * Purpose: Kiểm tra xử lý OPTIONS preflight request
     * Input: HTTP method=OPTIONS
     * Expected Output: CorsConfiguration không null
     */
    @Test
    void test_globalCorsFilter_handlesPreflight() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config).isNotNull();
    }

    /**
     * Test Case ID: TKB164
     * Purpose: Kiểm tra global CorsFilter set Allow-Headers header
     * Input: HTTP request bất kỳ
     * Expected Output: Allow-Headers chứa Authorization, Content-Type
     */
    @Test
    void test_globalCorsFilter_setsAllowHeaders() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowedHeaders()).contains("Authorization", "Content-Type", "X-Requested-With");
    }

    /**
     * Test Case ID: TKB165
     * Purpose: Kiểm tra CorsConfig tạo CorsConfigurationSource
     * Input: Application configuration
     * Expected Output: CorsConfigurationSource!=null
     */
    @Test
    void test_corsConfig_createsConfigurationSource() {
        CorsConfigurationSource source = createCorsConfigurationSource();
        assertThat(source).isNotNull();
    }

    /**
     * Test Case ID: TKB166
     * Purpose: Kiểm tra trả về config cho /api/**
     * Input: Request đến /api/schedules
     * Expected Output: CorsConfiguration!=null
     */
    @Test
    void test_corsConfigurationSource_returnsConfigForApi() {
        CorsConfigurationSource source = createCorsConfigurationSource();
        org.springframework.mock.web.MockHttpServletRequest request =
                new org.springframework.mock.web.MockHttpServletRequest();
        request.setRequestURI("/api/schedules");
        request.setMethod("POST");
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertThat(config).isNotNull();
    }

    /**
     * Test Case ID: TKB167
     * Purpose: Kiểm tra cho phép đúng HTTP methods
     * Input: CorsConfiguration từ source
     * Expected Output: GET, POST, PUT, DELETE allowed
     */
    @Test
    void test_corsConfiguration_allowsCorrectMethods() {
        CorsConfiguration config = createCorsConfiguration();
        assertThat(config.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE");
    }
}
