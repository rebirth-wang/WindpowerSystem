# FastBee-Test 模块测试用例说明

## 概述

`fastbee-test` 模块提供了完整的测试基础设施，包括4个测试基类和配套的工具类，用于支持不同类型的单元测试场景。

## 测试基类

### 1. BaseMockitoUnitTest - 纯 Mockito 单元测试

**用途**：适用于不需要 Spring 容器的纯业务逻辑测试，使用 Mock 对象进行隔离测试。

**示例文件**：`BaseMockitoUnitTestExample.java`

**演示的测试场景**：
- Mock 对象的基本用法
- 参数匹配器（anyString, anyLong, argThat等）
- 验证方法调用次数（times, atLeast, atMost）
- 参数捕获（ArgumentCaptor）
- Spy 对象的部分 Mock
- 异常抛出测试
- Void 方法测试

**使用示例**：
```java
public class MyServiceTest extends BaseMockitoUnitTest {
    @Mock
    private MyMapper myMapper;
    
    @Test
    public void testMyMethod() {
        when(myMapper.selectById(1L)).thenReturn(mockData);
        // 测试逻辑
    }
}
```

### 2. BaseDbUnitTest - 数据库单元测试

**用途**：使用 H2 内存数据库测试数据库操作（MyBatis Mapper 层测试）。

**示例文件**：`BaseDbUnitTestExample.java`

**演示的测试场景**：
- 插入数据（insert）
- 查询数据（select）
- 更新数据（update）
- 删除数据（delete）
- 条件查询（LambdaQueryWrapper）
- 批量操作
- 事务回滚测试
- 空结果处理

**依赖配置**：
- H2 内存数据库（自动启动）
- MyBatis Plus
- Druid 数据源

**使用示例**：
```java
@Sql(scripts = "/sql/test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MyMapperTest extends BaseDbUnitTest {
    @Autowired
    private MyMapper myMapper;
    
    @Test
    public void testInsert() {
        // 测试数据库插入
    }
}
```

### 3. BaseRedisUnitTest - Redis 单元测试

**用途**：使用内嵌 Redis（jedis-mock）测试 Redis 操作。

**示例文件**：`BaseRedisUnitTestExample.java`

**演示的测试场景**：
- String 类型操作
- Integer 类型操作
- 对象类型操作（JSON 序列化）
- Hash 类型操作
- List 类型操作
- Set 类型操作
- ZSet 类型操作
- 删除键
- 设置过期时间
- 自增自减操作
- 空值处理

**依赖配置**：
- jedis-mock（内嵌 Redis 服务器）
- RedisTemplate
- Redisson

**使用示例**：
```java
public class MyRedisTest extends BaseRedisUnitTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    public void testRedisOperations() {
        redisTemplate.opsForValue().set("key", "value");
        assertEquals("value", redisTemplate.opsForValue().get("key"));
    }
}
```

### 4. BaseDbAndRedisUnitTest - 数据库 + Redis 综合测试

**用途**：同时使用 H2 数据库和 Redis 的综合测试，适用于测试缓存策略、数据库与缓存一致性等场景。

**示例文件**：`BaseDbAndRedisUnitTestExample.java`

**演示的测试场景**：
- 数据库查询 + Redis 缓存（Cache-Aside 模式）
- 数据库更新 + Redis 缓存失效
- 数据库插入 + Redis 缓存预热
- 数据库删除 + Redis 缓存清理
- 批量查询 + 批量缓存
- Redis 计数器 + 数据库操作
- 缓存穿透防护（缓存空值）
- 缓存雪崩防护（随机过期时间）

**使用示例**：
```java
@Sql(scripts = "/sql/test_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MyServiceWithCacheTest extends BaseDbAndRedisUnitTest {
    @Autowired
    private MyMapper myMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    public void testCacheStrategy() {
        // 测试缓存逻辑
    }
}
```

## 工具类

### 1. RandomUtils - 随机数据生成工具

提供随机数据生成能力，基于 Podam 框架：

```java
// 生成随机字符串
String str = RandomUtils.randomString();

// 生成随机 Long ID
Long id = RandomUtils.randomLongId();

// 生成随机邮箱
String email = RandomUtils.randomEmail();

// 生成随机手机号
String mobile = RandomUtils.randomMobile();

// 生成随机 LocalDateTime
LocalDateTime time = RandomUtils.randomLocalDateTime();

// 生成随机 POJO 对象
User user = RandomUtils.randomPojo(User.class);

// 生成随机 POJO 列表
List<User> users = RandomUtils.randomPojoList(User.class, 10);
```

### 2. AssertUtils - 断言工具类

提供增强的断言功能：

```java
// 比对两个对象的属性是否一致
AssertUtils.assertPojoEquals(expected, actual, "ignoreField1", "ignoreField2");

// 判断两个对象是否一致
boolean equals = AssertUtils.isPojoEquals(expected, actual);

// 验证 Service 异常
AssertUtils.assertServiceException(
    () -> service.doSomething(),
    new ErrorCode(1001, "错误信息")
);
```

## 配置文件

### application-unit-test.yml

单元测试专用配置文件，位于 `src/test/resources/application-unit-test.yml`：

```yaml
spring:
  # H2 内存数据库配置
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL
    username: sa
    password: 
  
  # Redis 配置
  redis:
    host: 127.0.0.1
    port: 6379
  
  # SQL 初始化配置
  sql:
    init:
      mode: always

# MyBatis Plus 配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

## SQL 脚本

### 初始化脚本

位置：`src/test/resources/sql/test_user.sql`

```sql
-- 创建测试表
CREATE TABLE IF NOT EXISTS test_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    mobile VARCHAR(20),
    status INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO test_user (username, email, mobile, status) VALUES
('test_user_1', 'test1@example.com', '13800138000', 0),
('test_user_2', 'test2@example.com', '13800138001', 1);
```

### 清理脚本

位置：`src/test/resources/sql/clean.sql`

```sql
-- 清理测试数据
DELETE FROM test_user;
```

## 运行测试

### 运行所有测试

```bash
cd fastbee-plugs/fastbee-test
mvn test -DskipTests=false
```

### 运行特定测试类

```bash
# 运行 Mockito 测试
mvn test -DskipTests=false -Dtest=BaseMockitoUnitTestExample

# 运行数据库测试
mvn test -DskipTests=false -Dtest=BaseDbUnitTestExample

# 运行 Redis 测试
mvn test -DskipTests=false -Dtest=BaseRedisUnitTestExample

# 运行综合测试
mvn test -DskipTests=false -Dtest=BaseDbAndRedisUnitTestExample
```

### 运行特定测试方法

```bash
mvn test -DskipTests=false -Dtest=BaseDbUnitTestExample#testInsert
```

## 测试验证清单

### ✅ H2 内存数据库验证
- [x] 数据库连接正常
- [x] 表创建成功
- [x] 数据插入成功
- [x] 数据查询成功
- [x] 数据更新成功
- [x] 数据删除成功
- [x] 事务回滚正常

### ✅ Redis 缓存验证
- [x] Redis 连接正常
- [x] String 操作正常
- [x] Hash 操作正常
- [x] List 操作正常
- [x] Set 操作正常
- [x] ZSet 操作正常
- [x] 过期时间设置正常
- [x] 对象序列化/反序列化正常

### ✅ 综合场景验证
- [x] 数据库 + Redis 缓存策略
- [x] 缓存穿透防护
- [x] 缓存雪崩防护
- [x] 缓存一致性

## 最佳实践

### 1. 选择合适的测试基类

- **纯业务逻辑**：使用 `BaseMockitoUnitTest`
- **仅数据库操作**：使用 `BaseDbUnitTest`
- **仅 Redis 操作**：使用 `BaseRedisUnitTest`
- **数据库 + Redis**：使用 `BaseDbAndRedisUnitTest`

### 2. 测试数据准备

- 使用 `@Sql` 注解在测试前加载 SQL 脚本
- 使用 `RandomUtils` 生成随机测试数据
- 每个测试方法应该独立，不依赖其他测试

### 3. 测试清理

- 基类已配置 `@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)`
- 每个测试结束后自动清理数据
- Redis 测试使用内嵌服务器，自动清理

### 4. 测试命名规范

- 测试类命名：`XxxTest` 或 `XxxExample`
- 测试方法命名：`test + 测试场景`
- 使用 `@DisplayName` 注解提供可读的测试描述

## 常见问题

### Q1: 测试没有被执行？

确保添加了 `-DskipTests=false` 参数，因为父 POM 默认跳过测试。

### Q2: H2 数据库报错？

检查 SQL 脚本语法是否符合 H2 数据库规范，使用 `MODE=MYSQL` 兼容模式。

### Q3: Redis 连接失败？

检查 `application-unit-test.yml` 中的 Redis 端口配置，确保与 jedis-mock 启动端口一致。

### Q4: 测试数据冲突？

确保每个测试方法使用独立的数据，或使用 `@Sql` 注解清理数据。

## 总结

`fastbee-test` 模块提供了完整的测试基础设施，支持：
- ✅ 纯 Mockito 单元测试
- ✅ H2 内存数据库测试
- ✅ 内嵌 Redis 测试
- ✅ 数据库 + Redis 综合测试
- ✅ 随机数据生成工具
- ✅ 增强的断言工具

所有测试用例已经创建完成，可以直接参考使用。
