package com.fastbee.framework.test.core.ut;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.fastbee.framework.core.ut.BaseDbUnitTest;
import com.fastbee.framework.core.util.RandomUtils;
import com.fastbee.framework.test.core.ut.entity.TestUserDO;
import com.fastbee.framework.test.core.ut.mapper.TestUserMapper;

/**
 * {@link BaseDbUnitTest} 使用示例
 * 演示如何使用 H2 内存数据库进行数据库相关的单元测试
 */
@DisplayName("数据库单元测试示例")
@Sql(scripts = "/sql/test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseDbUnitTestExample extends BaseDbUnitTest {

    @Autowired
    private TestUserMapper testUserMapper;

    @Test
    @DisplayName("测试插入数据")
    public void testInsert() {
        // 准备测试数据
        TestUserDO user = new TestUserDO();
        user.setUsername(RandomUtils.randomString());
        user.setEmail(RandomUtils.randomEmail());
        user.setMobile(RandomUtils.randomMobile());
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 执行插入
        int rows = testUserMapper.insert(user);

        // 验证结果
        assertEquals(1, rows);
        assertNotNull(user.getId());
        assertTrue(user.getId() > 0);

        // 验证数据库中的数据
        TestUserDO dbUser = testUserMapper.selectById(user.getId());
        assertNotNull(dbUser);
        assertEquals(user.getUsername(), dbUser.getUsername());
        assertEquals(user.getEmail(), dbUser.getEmail());
        assertEquals(user.getMobile(), dbUser.getMobile());
    }

    @Test
    @DisplayName("测试查询数据")
    public void testSelect() {
        // 执行查询 - 查询所有数据
        LambdaQueryWrapper<TestUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestUserDO::getStatus, 0);

        var userList = testUserMapper.selectList(queryWrapper);

        // 验证结果
        assertNotNull(userList);
        assertTrue(userList.size() >= 2); // 根据 test_user.sql 中的数据，status=0 的应该有 2 条

        // 验证具体数据
        TestUserDO firstUser = userList.get(0);
        assertNotNull(firstUser.getId());
        assertNotNull(firstUser.getUsername());
    }

    @Test
    @DisplayName("测试更新数据")
    public void testUpdate() {
        // 先查询一条数据（不依赖固定 ID）
        LambdaQueryWrapper<TestUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.last("LIMIT 1");
        TestUserDO user = testUserMapper.selectOne(queryWrapper);
        assertNotNull(user);
        
        Long userId = user.getId();

        // 修改数据
        String newUsername = RandomUtils.randomString();
        user.setUsername(newUsername);
        user.setUpdateTime(LocalDateTime.now());

        // 执行更新
        int rows = testUserMapper.updateById(user);

        // 验证结果
        assertEquals(1, rows);

        // 验证更新后的数据
        TestUserDO updatedUser = testUserMapper.selectById(userId);
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
    }

    @Test
    @DisplayName("测试删除数据")
    public void testDelete() {
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

        // 执行删除
        int rows = testUserMapper.deleteById(userId);

        // 验证结果
        assertEquals(1, rows);

        // 验证数据已删除
        TestUserDO deletedUser = testUserMapper.selectById(userId);
        assertNull(deletedUser);
    }

    @Test
    @DisplayName("测试条件查询")
    public void testSelectByCondition() {
        // 先清理数据，确保测试环境干净
        testUserMapper.delete(null);
        
        // 插入一条明确的测试数据
        TestUserDO testUser = new TestUserDO();
        testUser.setUsername("test_user_1_specific");
        testUser.setEmail("test_specific@example.com");
        testUser.setMobile("13800138000");
        testUser.setStatus(0);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
        testUserMapper.insert(testUser);
        
        // 查询 username 包含 "test_user_1" 的用户
        LambdaQueryWrapper<TestUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(TestUserDO::getUsername, "test_user_1");

        var userList = testUserMapper.selectList(queryWrapper);

        // 验证结果
        assertNotNull(userList);
        assertEquals(1, userList.size());

        TestUserDO user = userList.get(0);
        assertTrue(user.getUsername().contains("test_user_1"));
    }

    @Test
    @DisplayName("测试批量操作")
    public void testBatchOperations() {
        // 批量插入
        for (int i = 0; i < 5; i++) {
            TestUserDO user = new TestUserDO();
            user.setUsername("batch_user_" + i);
            user.setEmail("batch" + i + "@example.com");
            user.setMobile("1380013800" + i);
            user.setStatus(0);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            testUserMapper.insert(user);
        }

        // 查询批量插入的数据
        LambdaQueryWrapper<TestUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(TestUserDO::getUsername, "batch_user_");

        var userList = testUserMapper.selectList(queryWrapper);

        // 验证结果
        assertNotNull(userList);
        assertEquals(5, userList.size());
    }

    @Test
    @DisplayName("测试事务回滚")
    public void testTransactionRollback() {
        // 这个测试演示了 Spring 的事务管理
        // 每个测试方法默认在一个事务中执行，测试结束后会回滚

        // 插入数据
        TestUserDO user = new TestUserDO();
        user.setUsername("transaction_test");
        user.setEmail("transaction@example.com");
        user.setMobile("13800138999");
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        testUserMapper.insert(user);
        Long userId = user.getId();

        // 验证数据存在
        TestUserDO dbUser = testUserMapper.selectById(userId);
        assertNotNull(dbUser);

        // 注意：由于测试结束后会回滚，所以这条数据不会真正保存到数据库
    }

    @Test
    @DisplayName("测试空结果处理")
    public void testEmptyResult() {
        // 查询不存在的数据
        TestUserDO user = testUserMapper.selectById(99999L);

        // 验证结果为空
        assertNull(user);
    }
}
