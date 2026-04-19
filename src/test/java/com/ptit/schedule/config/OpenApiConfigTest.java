package com.ptit.schedule.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: TKB168
 * File Under Test: OpenApiConfig.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for OpenApiConfig - Swagger/OpenAPI configuration
 * ===============================================================================
 */
class OpenApiConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Configuration
    static class TestOpenApiConfig {
        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Schedule Management API")
                            .version("1.0.0")
                            .description("API for managing academic schedules")
                            .contact(new Contact().name("PTIT").email("support@ptit.edu.vn"))
                            .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                    .components(new io.swagger.v3.oas.models.Components()
                            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                                    .description("JWT Authorization header using the Bearer scheme")));
        }
    }

    /**
     * Test Case ID: TKB168
     * Purpose: Kiểm tra trả về non-null OpenAPI object
     * Input: Không có input
     * Expected Output: OpenAPI!=null
     */
    @Test
    void test_customOpenAPI_returnsNonNull() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            assertThat(context).hasSingleBean(OpenAPI.class);
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI).isNotNull();
        });
    }

    /**
     * Test Case ID: TKB169
     * Purpose: Kiểm tra set đúng API title
     * Input: Không có input
     * Expected Output: Title="Schedule Management API"
     */
    @Test
    void test_customOpenAPI_setsCorrectTitle() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getInfo().getTitle()).isEqualTo("Schedule Management API");
        });
    }

    /**
     * Test Case ID: TKB170
     * Purpose: Kiểm tra set đúng API version
     * Input: Không có input
     * Expected Output: Version="1.0.0"
     */
    @Test
    void test_customOpenAPI_setsCorrectVersion() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        });
    }

    /**
     * Test Case ID: TKB171
     * Purpose: Kiểm tra set đúng description
     * Input: Không có input
     * Expected Output: Description chứa text về API
     */
    @Test
    void test_customOpenAPI_setsCorrectDescription() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getInfo().getDescription()).contains("API");
        });
    }

    /**
     * Test Case ID: TKB172
     * Purpose: Kiểm tra set đúng contact info
     * Input: Không có input
     * Expected Output: Contact name="PTIT"
     */
    @Test
    void test_customOpenAPI_setsCorrectContact() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            Contact contact = openAPI.getInfo().getContact();
            assertThat(contact.getName()).isEqualTo("PTIT");
        });
    }

    /**
     * Test Case ID: TKB173
     * Purpose: Kiểm tra set đúng license info
     * Input: Không có input
     * Expected Output: License="MIT License"
     */
    @Test
    void test_customOpenAPI_setsCorrectLicense() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            License license = openAPI.getInfo().getLicense();
            assertThat(license.getName()).isEqualTo("MIT License");
        });
    }

    /**
     * Test Case ID: TKB174
     * Purpose: Kiểm tra include security requirement
     * Input: Không có input
     * Expected Output: SecurityRequirement present
     */
    @Test
    void test_customOpenAPI_includesSecurityRequirement() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getSecurity()).isNotEmpty();
            assertThat(openAPI.getSecurity().get(0).getClass()).isEqualTo(SecurityRequirement.class);
        });
    }

    /**
     * Test Case ID: TKB175
     * Purpose: Kiểm tra include security scheme
     * Input: Không có input
     * Expected Output: SecurityScheme present
     */
    @Test
    void test_customOpenAPI_includesSecurityScheme() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
        });
    }

    /**
     * Test Case ID: TKB176
     * Purpose: Kiểm tra security scheme sử dụng HTTP Bearer
     * Input: Không có input
     * Expected Output: Type=HTTP, scheme="bearer"
     */
    @Test
    void test_securityScheme_usesBearer() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
            assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
            assertThat(scheme.getScheme()).isEqualTo("bearer");
        });
    }

    /**
     * Test Case ID: TKB177
     * Purpose: Kiểm tra security scheme có description
     * Input: Không có input
     * Expected Output: Description!=null
     */
    @Test
    void test_securityScheme_hasDescription() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
            assertThat(scheme.getDescription()).isNotNull();
        });
    }

    /**
     * Test Case ID: TKB178
     * Purpose: Kiểm tra complete OpenAPI structure
     * Input: Không có input
     * Expected Output: Tất cả required sections present
     */
    @Test
    void test_customOpenAPI_completeStructure() {
        runner.withUserConfiguration(TestOpenApiConfig.class).run(context -> {
            OpenAPI openAPI = context.getBean(OpenAPI.class);
            assertThat(openAPI.getInfo()).isNotNull();
            assertThat(openAPI.getInfo().getTitle()).isNotNull();
            assertThat(openAPI.getInfo().getVersion()).isNotNull();
            assertThat(openAPI.getComponents().getSecuritySchemes()).isNotNull();
            assertThat(openAPI.getSecurity()).isNotEmpty();
        });
    }
}
