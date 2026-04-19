package com.ptit.schedule.QLND;

import com.ptit.schedule.entity.Role;
import com.ptit.schedule.entity.User;
import com.ptit.schedule.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Test Suite UserRepository - Kiểm thử Repository Người dùng")
class UserRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .fullName("Test User")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("TC001 - Lưu người dùng thành công")
    void testSaveUserSuccess() {
        // Arrange
        logger.info("TC001 - Input: username={}, email={}, password={}, role={}, enabled={}", 
            testUser.getUsername(), testUser.getEmail(), testUser.getPassword(), 
            testUser.getRole(), testUser.getEnabled());
        
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        logger.info("TC001 - Output: savedId={}, username={}, email={}", 
            savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("TC002 - Tìm người dùng theo username")
    void testFindByUsername() {
        // Arrange
        userRepository.save(testUser);
        logger.info("TC002 - Input: username={}", "testuser");

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Assert
        logger.info("TC002 - Output: found={}, email={}", 
            foundUser.isPresent(), foundUser.map(User::getEmail).orElse(null));
        
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @DisplayName("TC003 - Tìm người dùng theo email")
    void testFindByEmail() {
        // Arrange
        userRepository.save(testUser);
        logger.info("TC003 - Input: email={}", "testuser@example.com");

        // Act
        Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");

        // Assert
        logger.info("TC003 - Output: found={}, username={}", 
            foundUser.isPresent(), foundUser.map(User::getUsername).orElse(null));
        
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @DisplayName("TC004 - Kiểm tra username tồn tại")
    void testExistsByUsername() {
        // Arrange
        userRepository.save(testUser);
        logger.info("TC004 - Input: username={}", "testuser");

        // Act
        boolean exists = userRepository.existsByUsername("testuser");

        // Assert
        logger.info("TC004 - Output: exists={}", exists);
        
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("TC005 - Kiểm tra username không tồn tại")
    void testExistsByUsernameNotFound() {
        // Arrange
        logger.info("TC005 - Input: username={}", "nonexistent");

        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        logger.info("TC005 - Output: exists={}", exists);
        
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("TC006 - Tìm tất cả người dùng hoạt động")
    void testFindAllActiveUsers() {
        // Arrange
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password123")
                .fullName("User One")
                .role(Role.USER)
                .enabled(true)
                .build();
        
        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password123")
                .fullName("User Two")
                .role(Role.ADMIN)
                .enabled(false)
                .build();
        
        userRepository.save(user1);
        userRepository.save(user2);
        logger.info("TC006 - Input: findAllByEnabledTrue()");

        // Act
        List<User> activeUsers = userRepository.findAllByEnabledTrue();

        // Assert
        logger.info("TC006 - Output: resultCount={}, firstUsername={}", 
            activeUsers.size(), activeUsers.isEmpty() ? null : activeUsers.get(0).getUsername());
        
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("user1");
        assertThat(activeUsers.get(0).isEnabled()).isTrue();
    }

    @Test
    @DisplayName("TC007 - Cập nhật người dùng thành công")
    void testUpdateUserSuccess() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        logger.info("TC007 - Input: userId={}, newEmail={}", savedUser.getId(), "newemail@example.com");

        // Act
        savedUser.setEmail("newemail@example.com");
        savedUser.setFullName("Updated User");
        User updatedUser = userRepository.save(savedUser);

        // Assert
        logger.info("TC007 - Output: updateSuccess={}, email={}, fullName={}", 
            true, updatedUser.getEmail(), updatedUser.getFullName());
        
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
        assertThat(updatedUser.getFullName()).isEqualTo("Updated User");
    }

    @Test
    @DisplayName("TC008 - Xóa người dùng thành công")
    void testDeleteUserSuccess() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();
        logger.info("TC008 - Input: userId={}", userId);

        // Act
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Assert
        logger.info("TC008 - Output: deleteSuccess={}", deletedUser.isEmpty());
        
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("TC009 - Kiểm tra email không tồn tại")
    void testFindByEmailNotFound() {
        // Arrange
        logger.info("TC009 - Input: email={}", "nonexistent@example.com");

        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        logger.info("TC009 - Output: found={}", foundUser.isPresent());
        
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("TC010 - Lưu và lấy nhiều người dùng")
    void testSaveMultipleUsers() {
        // Arrange
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("pass123")
                .fullName("User One")
                .role(Role.USER)
                .enabled(true)
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("pass123")
                .fullName("User Two")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        logger.info("TC010 - Input: saving 2 users");

        // Act
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> allUsers = userRepository.findAll();

        // Assert
        logger.info("TC010 - Output: totalUsers={}", allUsers.size());
        
        assertThat(allUsers).hasSize(2);
    }
}

