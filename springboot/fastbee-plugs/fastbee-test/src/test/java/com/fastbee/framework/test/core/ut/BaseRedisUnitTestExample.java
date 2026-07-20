package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fastbee.framework.core.ut.BaseRedisUnitTest;
import com.fastbee.framework.core.util.RandomUtils;

/**
 * {@link BaseRedisUnitTest} 使用示例
 * 演示如何使用内嵌 Redis 进行 Redis 相关的单元测试
 */
@DisplayName("Redis 单元测试示例")
public class BaseRedisUnitTestExample extends BaseRedisUnitTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("测试 String 类型操作")
    public void testStringOperations() {
        String key = "test:string:" + RandomUtils.randomString();
        String value = "Hello Redis";

        // 设置值
        redisTemplate.opsForValue().set(key, value);

        // 获取值
        Object result = redisTemplate.opsForValue().get(key);
        assertEquals(value, result);

        // 设置带过期时间的值
        String expireKey = "test:string:expire:" + RandomUtils.randomString();
        redisTemplate.opsForValue().set(expireKey, "Expire Value", 10, TimeUnit.SECONDS);

        Object expireResult = redisTemplate.opsForValue().get(expireKey);
        assertEquals("Expire Value", expireResult);
    }

    @Test
    @DisplayName("测试 Integer 类型操作")
    public void testIntegerOperations() {
        String key = "test:integer:" + RandomUtils.randomString();
        Integer value = 12345;

        // 设置值
        redisTemplate.opsForValue().set(key, value);

        // 获取值
        Object result = redisTemplate.opsForValue().get(key);
        assertEquals(value, result);
    }

    @Test
    @DisplayName("测试对象类型操作")
    public void testObjectOperations() {
        String key = "test:object:" + RandomUtils.randomString();

        // 创建测试对象
        TestUser user = new TestUser();
        user.setId(1L);
        user.setUsername(RandomUtils.randomString());
        user.setEmail(RandomUtils.randomEmail());
        user.setCreateTime(LocalDateTime.now());

        // 设置对象
        redisTemplate.opsForValue().set(key, user);

        // 获取对象
        Object result = redisTemplate.opsForValue().get(key);
        assertNotNull(result);

        // 验证对象属性
        if (result instanceof TestUser) {
            TestUser redisUser = (TestUser) result;
            assertEquals(user.getId(), redisUser.getId());
            assertEquals(user.getUsername(), redisUser.getUsername());
            assertEquals(user.getEmail(), redisUser.getEmail());
        }
    }

    @Test
    @DisplayName("测试 Hash 类型操作")
    public void testHashOperations() {
        String key = "test:hash:" + RandomUtils.randomString();

        // 创建 Hash 数据
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("field1", "value1");
        hashMap.put("field2", "value2");
        hashMap.put("field3", 100);

        // 设置 Hash
        redisTemplate.opsForHash().putAll(key, hashMap);

        // 获取单个 field
        Object field1 = redisTemplate.opsForHash().get(key, "field1");
        assertEquals("value1", field1);

        // 获取所有 fields
        Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
        assertEquals(3, result.size());
        assertEquals("value1", result.get("field1"));
        assertEquals("value2", result.get("field2"));
        assertEquals(100, result.get("field3"));
    }

    @Test
    @DisplayName("测试 List 类型操作")
    public void testListOperations() {
        String key = "test:list:" + RandomUtils.randomString();

        // 从左侧推入数据
        redisTemplate.opsForList().leftPush(key, "item1");
        redisTemplate.opsForList().leftPush(key, "item2");
        redisTemplate.opsForList().leftPush(key, "item3");

        // 获取列表长度
        Long size = redisTemplate.opsForList().size(key);
        assertEquals(3, size);

        // 获取所有元素
        var list = redisTemplate.opsForList().range(key, 0, -1);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("item3", list.get(0)); // 因为是 leftPush，所以顺序相反
        assertEquals("item1", list.get(2));
    }

    @Test
    @DisplayName("测试 Set 类型操作")
    public void testSetOperations() {
        String key = "test:set:" + RandomUtils.randomString();

        // 添加元素
        redisTemplate.opsForSet().add(key, "member1", "member2", "member3");

        // 获取所有元素
        Set<Object> members = redisTemplate.opsForSet().members(key);
        assertNotNull(members);
        assertEquals(3, members.size());
        assertTrue(members.contains("member1"));
        assertTrue(members.contains("member2"));
        assertTrue(members.contains("member3"));

        // 判断元素是否存在
        assertTrue(redisTemplate.opsForSet().isMember(key, "member1"));
        assertFalse(redisTemplate.opsForSet().isMember(key, "member4"));
    }

    @Test
    @DisplayName("测试 ZSet 类型操作")
    public void testZSetOperations() {
        String key = "test:zset:" + RandomUtils.randomString();

        // 添加元素（带分数）
        redisTemplate.opsForZSet().add(key, "member1", 10.0);
        redisTemplate.opsForZSet().add(key, "member2", 20.0);
        redisTemplate.opsForZSet().add(key, "member3", 15.0);

        // 获取元素个数
        Long size = redisTemplate.opsForZSet().zCard(key);
        assertEquals(3, size);

        // 按分数范围查询
        Set<Object> range = redisTemplate.opsForZSet().rangeByScore(key, 10.0, 20.0);
        assertNotNull(range);
        assertEquals(3, range.size());

        // 获取元素的分数
        Double score = redisTemplate.opsForZSet().score(key, "member2");
        assertEquals(20.0, score);
    }

    @Test
    @DisplayName("测试删除键")
    public void testDelete() {
        String key = "test:delete:" + RandomUtils.randomString();
        redisTemplate.opsForValue().set(key, "value");

        // 验证键存在
        assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(key)));

        // 删除键
        Boolean deleted = redisTemplate.delete(key);
        assertTrue(deleted);

        // 验证键已删除
        assertFalse(Boolean.TRUE.equals(redisTemplate.hasKey(key)));
    }

    @Test
    @DisplayName("测试设置过期时间")
    public void testExpire() {
        String key = "test:expire:" + RandomUtils.randomString();
        redisTemplate.opsForValue().set(key, "value");

        // 设置过期时间
        Boolean expired = redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        assertTrue(expired);

        // 验证键存在
        assertTrue(Boolean.TRUE.equals(redisTemplate.hasKey(key)));

        // 获取剩余时间
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertTrue(ttl > 0 && ttl <= 5);
    }

    @Test
    @DisplayName("测试自增自减操作")
    public void testIncrement() {
        String key = "test:increment:" + RandomUtils.randomString();

        // 初始化值
        redisTemplate.opsForValue().set(key, 0);

        // 自增
        Long increment = redisTemplate.opsForValue().increment(key, 5);
        assertEquals(5, increment);

        increment = redisTemplate.opsForValue().increment(key, 3);
        assertEquals(8, increment);

        // 自减
        Long decrement = redisTemplate.opsForValue().decrement(key, 2);
        assertEquals(6, decrement);

        // 验证最终值
        Object result = redisTemplate.opsForValue().get(key);
        assertEquals(6, result);
    }

    @Test
    @DisplayName("测试空值处理")
    public void testNullValue() {
        String key = "test:null:" + RandomUtils.randomString();

        // 获取不存在的键
        Object result = redisTemplate.opsForValue().get(key);
        assertNull(result);
    }

    /**
     * 测试用的 User 实体类
     */
    public static class TestUser {
        private Long id;
        private String username;
        private String email;
        private LocalDateTime createTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }
}
