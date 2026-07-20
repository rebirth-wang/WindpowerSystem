package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.framework.core.util.RandomUtils;

/**
 * {@link BaseMockitoUnitTest} 使用示例
 * 演示如何使用纯 Mockito 进行单元测试
 */
@DisplayName("Mockito 单元测试示例")
public class BaseMockitoUnitTestExample extends BaseMockitoUnitTest {

    @Mock
    private UserService userService;

    @Spy
    private List<String> spyList = new java.util.ArrayList<>();

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("测试 Mock 对象 - 基本用法")
    public void testMockObject_basic() {
        // 准备测试数据
        String username = RandomUtils.randomString();
        User expectedUser = new User(1L, username, "test@example.com");

        // 配置 Mock 行为
        when(userService.getUserById(1L)).thenReturn(expectedUser);
        when(userService.getUserById(999L)).thenReturn(null);

        // 执行测试
        User result1 = userService.getUserById(1L);
        User result2 = userService.getUserById(999L);

        // 验证结果
        assertNotNull(result1);
        assertEquals(expectedUser.getId(), result1.getId());
        assertEquals(expectedUser.getUsername(), result1.getUsername());
        assertNull(result2);

        // 验证 Mock 方法被调用
        verify(userService).getUserById(1L);
        verify(userService).getUserById(999L);
    }

    @Test
    @DisplayName("测试 Mock 对象 - 参数匹配器")
    public void testMockObject_argumentMatchers() {
        // 配置 Mock 行为 - 使用参数匹配器
        when(userService.getUserByUsername(anyString())).thenReturn(new User(1L, "test", "test@example.com"));
        when(userService.createUser(argThat(user -> user.getUsername().length() > 3))).thenReturn(true);

        // 执行测试
        User result = userService.getUserByUsername("anyUsername");
        boolean created = userService.createUser(new User(null, "longUsername", "test@example.com"));

        // 验证结果
        assertNotNull(result);
        assertTrue(created);

        // 验证调用
        verify(userService).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("测试 Mock 对象 - 验证调用次数")
    public void testMockObject_verifyInvocations() {
        // 配置 Mock 行为
        when(userService.getUserById(anyLong())).thenReturn(new User(1L, "test", "test@example.com"));

        // 执行多次调用
        userService.getUserById(1L);
        userService.getUserById(2L);
        userService.getUserById(3L);

        // 验证调用次数
        verify(userService, times(3)).getUserById(anyLong());
        verify(userService, atLeast(2)).getUserById(anyLong());
        verify(userService, atMost(5)).getUserById(anyLong());
    }

    @Test
    @DisplayName("测试 Mock 对象 - 捕获参数")
    public void testMockObject_captureArguments() {
        // 配置 Mock 行为
        when(userService.createUser(any(User.class))).thenReturn(true);

        // 执行测试
        User newUser = new User(null, "captured", "captured@example.com");
        userService.createUser(newUser);

        // 捕获并验证参数
        verify(userService).createUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("captured", capturedUser.getUsername());
        assertEquals("captured@example.com", capturedUser.getEmail());
    }

    @Test
    @DisplayName("测试 Spy 对象 - 真实对象的部分 Mock")
    public void testSpyObject_partialMocking() {
        // Spy 对象会调用真实方法，除非特别配置
        spyList.add("one");
        spyList.add("two");

        // 配置 Mock 行为 - 覆盖特定方法
        when(spyList.size()).thenReturn(100);

        // 执行测试
        assertEquals(100, spyList.size()); // 返回 mock 的值
        assertEquals("one", spyList.get(0)); // 调用真实方法

        // 验证调用
        verify(spyList).add("one");
        verify(spyList).add("two");
    }

    @Test
    @DisplayName("测试异常抛出")
    public void testException() {
        // 配置 Mock 行为 - 抛出异常
        when(userService.getUserById(-1L)).thenThrow(new IllegalArgumentException("Invalid ID"));

        // 验证异常抛出
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(-1L);
        });

        assertEquals("Invalid ID", exception.getMessage());
    }

    @Test
    @DisplayName("测试 Void 方法")
    public void testVoidMethod() {
        // Void 方法的 Mock
        doNothing().when(userService).deleteUser(anyLong());

        // 执行测试
        userService.deleteUser(1L);

        // 验证调用
        verify(userService).deleteUser(1L);
    }

    /**
     * 测试用的 User 实体类
     */
    public static class User {
        private Long id;
        private String username;
        private String email;

        public User(Long id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * 测试用的 UserService 接口
     */
    public interface UserService {
        User getUserById(Long id);
        User getUserByUsername(String username);
        boolean createUser(User user);
        void deleteUser(Long id);
    }
}
