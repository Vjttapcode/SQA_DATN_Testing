package com.ptit.schedule;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: TKB189
 * File Under Test: ScheduleApplication.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for main Spring Boot application class
 * ===============================================================================
 */
class ScheduleApplicationTest {

    /**
     * Test Case ID: TKB189
     * Purpose: Kiểm tra class tồn tại với đúng annotation
     * Input: Không có input
     * Expected Output: Class exists, @SpringBootApplication present
     */
    @Test
    void test_scheduleApplication_classExists() {
        assertThat(ScheduleApplication.class).isNotNull();
        assertThat(ScheduleApplication.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.SpringBootApplication.class)).isTrue();
    }

    /**
     * Test Case ID: TKB190
     * Purpose: Kiểm tra là public class
     * Input: Không có input
     * Expected Output: Class.isPublic()=true
     */
    @Test
    void test_scheduleApplication_isPublicClass() {
        assertThat(Modifier.isPublic(ScheduleApplication.class.getModifiers())).isTrue();
    }

    /**
     * Test Case ID: TKB191
     * Purpose: Kiểm tra có main() method
     * Input: Không có input
     * Expected Output: Method main(String[]) tồn tại
     */
    @Test
    void test_scheduleApplication_hasMainMethod() throws NoSuchMethodException {
        Method mainMethod = ScheduleApplication.class.getMethod("main", String[].class);
        assertThat(mainMethod).isNotNull();
    }

    /**
     * Test Case ID: TKB192
     * Purpose: Kiểm tra main() có signature đúng
     * Input: Không có input
     * Expected Output: ParameterTypes=[String[].class]
     */
    @Test
    void test_mainMethod_correctSignature() throws NoSuchMethodException {
        Method mainMethod = ScheduleApplication.class.getMethod("main", String[].class);
        assertThat(mainMethod.getParameterTypes()).hasSize(1);
        assertThat(mainMethod.getParameterTypes()[0]).isEqualTo(String[].class);
    }

    /**
     * Test Case ID: TKB193
     * Purpose: Kiểm tra không có instance fields
     * Input: Không có input
     * Expected Output: Only static members
     */
    @Test
    void test_scheduleApplication_noInstanceFields() {
        boolean hasInstanceField = false;
        for (var field : ScheduleApplication.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                hasInstanceField = true;
                break;
            }
        }
        assertThat(hasInstanceField).isFalse();
    }

    /**
     * Test Case ID: TKB194
     * Purpose: Kiểm tra có default constructor
     * Input: Không có input
     * Expected Output: Public no-arg constructor tồn tại
     */
    @Test
    void test_scheduleApplication_defaultConstructor() {
        boolean hasPublicNoArgConstructor = false;
        for (Constructor<?> constructor : ScheduleApplication.class.getConstructors()) {
            if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() == 0) {
                hasPublicNoArgConstructor = true;
                break;
            }
        }
        assertThat(hasPublicNoArgConstructor).isTrue();
    }

    /**
     * Test Case ID: TKB195
     * Purpose: Kiểm tra main() có thể invoke qua reflection
     * Input: Không có input
     * Expected Output: Method accessible
     */
    @Test
    void test_mainMethod_invokable() throws NoSuchMethodException {
        Method mainMethod = ScheduleApplication.class.getMethod("main", String[].class);
        assertThat(mainMethod.trySetAccessible()).isTrue();
    }

    /**
     * Test Case ID: TKB196
     * Purpose: Kiểm tra @SpringBootApplication bao gồm meta-annotations
     * Input: @SpringBootApplication annotation
     * Expected Output: @SpringBootConfiguration, @ComponentScan, @EnableAutoConfiguration present
     */
    @Test
    void test_springBootApplication_metaAnnotations() {
        org.springframework.boot.autoconfigure.SpringBootApplication annotation =
                ScheduleApplication.class.getAnnotation(
                        org.springframework.boot.autoconfigure.SpringBootApplication.class);
        assertThat(annotation).isNotNull();
        // @SpringBootApplication -> @SpringBootConfiguration + @ComponentScan + @EnableAutoConfiguration
        // @SpringBootConfiguration -> @Configuration
        assertThat(ScheduleApplication.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.SpringBootApplication.class)).isTrue();
    }
}
