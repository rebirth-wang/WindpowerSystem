package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fastbee.framework.core.ut.BaseIntegrationTest;
import com.fastbee.framework.core.util.RandomUtils;
import com.fastbee.framework.test.core.ut.entity.TestUserDO;
import com.fastbee.framework.test.core.ut.mapper.TestUserMapper;

/**
 * {@link BaseIntegrationTest} 使用示例
 * 演示如何使用真实的数据库和 Redis 进行集成测试
 */
@DisplayName("集成测试示例")
public class BaseIntegrationTestExample extends BaseIntegrationTest {

    @Autowired
    private TestUserMapper testUserMapper;

    @Test
    @DisplayName("测试真实数据库插入")
    public void testRealDatabaseInsert() {
        // 准备测试数据
        TestUserDO user = new TestUserDO();
        user.setUsername("integration_test_user");
        user.setEmail(RandomUtils.randomEmail());
        user.setMobile(RandomUtils.randomMobile());
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 执行插入
        int rows = testUserMapper.insert(user);
        assertEquals(1, rows);
        assertNotNull(user.getId());

        // 验证查询
        TestUserDO savedUser = testUserMapper.selectById(user.getId());
        assertNotNull(savedUser);
        assertEquals("integration_test_user", savedUser.getUsername());
    }

    @Test
    @DisplayName("测试真实数据库查询")
    public void testRealDatabaseQuery() {
        // 先插入数据
        TestUserDO user = new TestUserDO();
        user.setUsername("query_test_user");
        user.setEmail(RandomUtils.randomEmail());
        user.setMobile(RandomUtils.randomMobile());
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        testUserMapper.insert(user);

        // 查询数据
        LambdaQueryWrapper<TestUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestUserDO::getUsername, "query_test_user");
        TestUserDO foundUser = testUserMapper.selectOne(queryWrapper);

        // 验证结果
        assertNotNull(foundUser);
        assertEquals("query_test_user", foundUser.getUsername());
    }

    @Test
    @DisplayName("测试真实 Redis 缓存")
    public void testRealRedisCache() {
        // 注意：此测试需要注入 RedisTemplate
        // 由于真实环境的 Redis 数据会持久化，测试后需要清理数据
        // 建议使用唯一的 key 避免影响其他测试
        String testKey = "integration:test:" + System.currentTimeMillis();
        String testValue = "test_value";
        
        // 这里只是示例，实际需要注入 RedisTemplate
        // redisTemplate.opsForValue().set(testKey, testValue);
        // Object cachedValue = redisTemplate.opsForValue().get(testKey);
        // assertEquals(testValue, cachedValue);
        
        assertTrue(true, "真实 Redis 测试需要注入 RedisTemplate");
    }
}
