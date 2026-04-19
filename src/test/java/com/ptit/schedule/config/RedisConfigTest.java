package com.ptit.schedule.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * TEST CASE ID: TKB179
 * File Under Test: RedisConfig.java
 * Module: Quản lý Thời Khóa Biểu (Schedule Management)
 * Description: Unit tests for RedisConfig - Redis configuration
 * ===============================================================================
 */
class RedisConfigTest {

    @Configuration
    static class TestRedisConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
            return new LettuceConnectionFactory(config);
        }

        @Bean
        @Primary
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.afterPropertiesSet();
            return template;
        }
    }

    /**
     * Test Case ID: TKB179
     * Purpose: Kiểm tra tạo non-null RedisTemplate
     * Input: RedisConnectionFactory
     * Expected Output: redisTemplate!=null
     */
    @Test
    void test_redisTemplate_createsNonNullTemplate() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template).isNotNull();
    }

    /**
     * Test Case ID: TKB180
     * Purpose: Kiểm tra sử dụng String serializer cho keys
     * Input: RedisConnectionFactory
     * Expected Output: Key serializer is StringRedis
     */
    @Test
    void test_redisTemplate_usesStringKeySerializer() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
    }

    /**
     * Test Case ID: TKB181
     * Purpose: Kiểm tra sử dụng String serializer cho hash keys
     * Input: RedisConnectionFactory
     * Expected Output: Hash key serializer is StringRedis
     */
    @Test
    void test_redisTemplate_usesStringHashKeySerializer() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getHashKeySerializer()).isInstanceOf(StringRedisSerializer.class);
    }

    /**
     * Test Case ID: TKB182
     * Purpose: Kiểm tra sử dụng JSON serializer cho values
     * Input: RedisConnectionFactory
     * Expected Output: Value serializer is JSON
     */
    @Test
    void test_redisTemplate_usesJsonValueSerializer() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }

    /**
     * Test Case ID: TKB183
     * Purpose: Kiểm tra sử dụng JSON serializer cho hash values
     * Input: RedisConnectionFactory
     * Expected Output: Hash value serializer is JSON
     */
    @Test
    void test_redisTemplate_usesJsonHashValueSerializer() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getHashValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }

    /**
     * Test Case ID: TKB184
     * Purpose: Kiểm tra sử dụng provided connection factory
     * Input: RedisConnectionFactory
     * Expected Output: Uses provided factory
     */
    @Test
    void test_redisTemplate_usesConnectionFactory() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getConnectionFactory()).isEqualTo(factory);
    }

    /**
     * Test Case ID: TKB185
     * Purpose: Kiểm tra initializes đúng cách
     * Input: Valid RedisConnectionFactory
     * Expected Output: All serializers set
     */
    @Test
    void test_redisTemplate_initializesProperly() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getKeySerializer()).isNotNull();
        assertThat(template.getValueSerializer()).isNotNull();
        assertThat(template.getHashKeySerializer()).isNotNull();
        assertThat(template.getHashValueSerializer()).isNotNull();
    }

    /**
     * Test Case ID: TKB186
     * Purpose: Kiểm tra JSON serializer xử lý Object type
     * Input: GenericJackson2JsonRedisSerializer
     * Expected Output: Serializer is GenericJackson2JsonRedisSerializer
     */
    @Test
    void test_jsonSerializer_handlesObjectType() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getValueSerializer())
                .isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }

    /**
     * Test Case ID: TKB187
     * Purpose: Kiểm tra String serializers cho keys
     * Input: RedisTemplate instances
     * Expected Output: Both key serializers are String
     */
    @Test
    void test_stringSerializers_forKeys() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getKeySerializer() instanceof StringRedisSerializer).isTrue();
        assertThat(template.getHashKeySerializer() instanceof StringRedisSerializer).isTrue();
    }

    /**
     * Test Case ID: TKB188
     * Purpose: Kiểm tra JSON serializers cho values
     * Input: RedisTemplate instances
     * Expected Output: Both value serializers are JSON
     */
    @Test
    void test_jsonSerializers_forValues() {
        TestRedisConfig config = new TestRedisConfig();
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer).isTrue();
        assertThat(template.getHashValueSerializer() instanceof GenericJackson2JsonRedisSerializer).isTrue();
    }
}
