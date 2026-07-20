package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.fastbee.framework.core.ut.BaseDbAndRedisUnitTest;
import com.fastbee.framework.core.util.RandomUtils;
import com.fastbee.framework.test.core.ut.entity.TestUserDO;
import com.fastbee.framework.test.core.ut.mapper.TestUserMapper;

/**
 * {@link BaseDbAndRedisUnitTest} 使用示例
 * 演示如何同时使用 H2 内存数据库和 Redis 进行综合测试
 * 常见场景：数据库 + 缓存的综合测试
 */
@DisplayName("数据库+Redis 综合单元测试示例")
@Sql(scripts = "/sql/test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseDbAndRedisUnitTestExample extends BaseDbAndRedisUnitTest {

    @Autowired
    private TestUserMapper testUserMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("测试数据库查询 + Redis 缓存")
    public void testDatabaseQueryWithRedisCache() {
        Long userId = 1L;
        String cacheKey = "user:cache:" + userId;

        // 第一次查询：从数据库查询并缓存
        TestUserDO user = getUserFromDatabaseOrCache(userId, cacheKey);
        assertNotNull(user);
        assertEquals(1L, user.getId());

        // 验证数据已缓存到 Redis
        assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));

        // 第二次查询：应该从 Redis 缓存获取
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cachedUser);
    }

    @Test
    @DisplayName("测试数据库更新 + Redis 缓存失效")
    public void testDatabaseUpdateWithRedisCacheEvict() {
        Long userId = 1L;
        String cacheKey = "user:cache:" + userId;

        // 先查询并缓存
        TestUserDO user = getUserFromDatabaseOrCache(userId, cacheKey);
        assertNotNull(user);

        // 更新数据库
        String newUsername = RandomUtils.randomString();
        user.setUsername(newUsername);
        user.setUpdateTime(LocalDateTime.now());
        testUserMapper.updateById(user);

        // 清除缓存
        redisTemplate.delete(cacheKey);

        // 验证缓存已清除
        assertFalse(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));

        // 再次查询：应该从数据库重新查询并缓存
        TestUserDO updatedUser = getUserFromDatabaseOrCache(userId, cacheKey);
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    @DisplayName("测试数据库插入 + Redis 缓存预热")
    public void testDatabaseInsertWithRedisCacheWarmup() {
        // 准备测试数据
        TestUserDO user = new TestUserDO();
        user.setUsername(RandomUtils.randomString());
        user.setEmail(RandomUtils.randomEmail());
        user.setMobile(RandomUtils.randomMobile());
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入数据库
        int rows = testUserMapper.insert(user);
        assertEquals(1, rows);
        assertNotNull(user.getId());

        // 预热缓存
        String cacheKey = "user:cache:" + user.getId();
        redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);

        // 验证缓存已设置
        assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));

        // 从缓存获取
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cachedUser);
    }

    @Test
    @DisplayName("测试数据库删除 + Redis 缓存清理")
    public void testDatabaseDeleteWithRedisCacheCleanup() {
        // 先插入一条数据
        TestUserDO user = new TestUserDO();
        user.setUsername(RandomUtils.randomString());
        user.setEmail(RandomUtils.randomEmail());
        user.setMobile(RandomUtils.randomMobile());
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        testUserMapper.insert(user);
        Long userId = user.getId();

        // 添加到缓存
        String cacheKey = "user:cache:" + userId;
        redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));

        // 删除数据库记录
        int rows = testUserMapper.deleteById(userId);
        assertEquals(1, rows);

        // 清理缓存
        redisTemplate.delete(cacheKey);

        // 验证数据库和缓存都已清理
        TestUserDO deletedUser = testUserMapper.selectById(userId);
        assertNull(deletedUser);
        assertFalse(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));
    }

    @Test
    @DisplayName("测试批量查询 + Redis 批量缓存")
    public void testBatchQueryWithRedisBatchCache() {
        // 查询多个用户
        var userList = testUserMapper.selectList(null);
        assertNotNull(userList);
        assertTrue(userList.size() > 0);

        // 批量缓存到 Redis
        for (TestUserDO user : userList) {
            String cacheKey = "user:cache:" + user.getId();
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }

        // 验证缓存
        for (TestUserDO user : userList) {
            String cacheKey = "user:cache:" + user.getId();
            assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey)));
        }
    }

    @Test
    @DisplayName("测试 Redis 计数器 + 数据库操作")
    public void testRedisCounterWithDatabaseOperations() {
        String counterKey = "user:access:count";
        Long userId = 1L;

        // 模拟用户访问：Redis 计数器自增
        Long count = redisTemplate.opsForValue().increment(counterKey + ":" + userId, 1);
        assertNotNull(count);
        assertTrue(count > 0);

        // 从数据库查询用户信息
        TestUserDO user = testUserMapper.selectById(userId);
        assertNotNull(user);

        // 记录访问次数到数据库（示例）
        // 实际业务中可能会有访问日志表
    }

    @Test
    @DisplayName("测试缓存穿透防护")
    public void testCachePenetrationProtection() {
        Long nonExistentUserId = 99999L;
        String cacheKey = "user:cache:" + nonExistentUserId;

        // 查询不存在的用户
        TestUserDO user = getUserFromDatabaseOrCache(nonExistentUserId, cacheKey);
        assertNull(user);

        // 缓存空值，防止缓存穿透
        // 设置较短的过期时间
        redisTemplate.opsForValue().set(cacheKey, "NULL", 5, TimeUnit.MINUTES);

        // 再次查询：应该从缓存获取到空值标记
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        assertEquals("NULL", cachedValue);
    }

    @Test
    @DisplayName("测试缓存雪崩防护")
    public void testCacheAvalancheProtection() {
        // 批量查询用户
        for (long i = 1; i <= 5; i++) {
            String cacheKey = "user:cache:" + i;

            // 设置不同的过期时间，防止缓存雪崩
            long expireTime = 30 + (i % 10); // 30-39 分钟随机
            TestUserDO user = testUserMapper.selectById(i);
            if (user != null) {
                redisTemplate.opsForValue().set(cacheKey, user, expireTime, TimeUnit.MINUTES);
            }
        }

        // 验证缓存已设置
        for (long i = 1; i <= 5; i++) {
            String cacheKey = "user:cache:" + i;
            Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.MINUTES);
            if (testUserMapper.selectById(i) != null) {
                assertNotNull(ttl);
                assertTrue(ttl > 0);
            }
        }
    }

    /**
     * 从数据库或缓存中获取用户信息
     *
     * @param userId 用户ID
     * @param cacheKey 缓存键
     * @return 用户信息
     */
    private TestUserDO getUserFromDatabaseOrCache(Long userId, String cacheKey) {
        // 先从缓存获取
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null && !"NULL".equals(cachedUser)) {
            if (cachedUser instanceof TestUserDO) {
                return (TestUserDO) cachedUser;
            }
        }

        // 缓存未命中，从数据库查询
        TestUserDO user = testUserMapper.selectById(userId);

        // 查询到数据后缓存到 Redis
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }

        return user;
    }
}
