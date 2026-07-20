# 单元测试环境配置说明

## 两种测试模式

本项目支持两种测试模式：

### 1. 单元测试模式（Unit Test）- 使用内存数据库
- **配置文件**: `application-unit-test.yml`
- **数据库**: H2 内存数据库
- **Redis**: jedis-mock 嵌入式 Redis
- **基类**: `BaseDbUnitTest`, `BaseDbAndRedisUnitTest`, `BaseRedisUnitTest`
- **适用场景**: 快速单元测试，不需要外部依赖

### 2. 集成测试模式（Integration Test）- 使用真实服务
- **配置文件**: `application-integration-test.yml`
- **数据库**: 真实 MySQL 数据库
- **Redis**: 真实 Redis 服务
- **基类**: `BaseIntegrationTest`
- **适用场景**: 集成测试，需要验证真实环境

## 如何切换到真实环境

### 方式1：使用集成测试基类（推荐）

1. **配置真实环境连接信息**
   
   编辑 `src/test/resources/application-integration-test.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/your_test_db
       username: your_username
       password: your_password
     redis:
       host: 127.0.0.1
       port: 6379
       password: your_redis_password  # 如果需要
   ```

2. **测试类继承 BaseIntegrationTest**
   ```java
   @DisplayName("集成测试示例")
   public class YourIntegrationTest extends BaseIntegrationTest {
       @Autowired
       private YourMapper yourMapper;
       
       @Test
       void testSomething() {
           // 测试代码
       }
   }
   ```

### 方式2：修改现有单元测试配置

如果你想让现有的 `BaseDbAndRedisUnitTest` 也使用真实环境：

#### 步骤1：修改 application-unit-test.yml

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/fastbee_test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
    # ... 其他配置
```

#### 步骤2：修改 BaseDbAndRedisUnitTest.java

移除嵌入式组件配置：

```java
@Import({
        // DB 配置类
        DataSourceConfig.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class,
        SqlInitializationTestConfiguration.class,
        
        // MyBatis 配置类
        MybatisPlusAutoConfiguration.class,
        MybatisPlusJoinAutoConfiguration.class,

        // Redis 配置类
        // 移除: RedisTestConfiguration.class,  ← 删除这行
        RedisConfig.class,
        RedisAutoConfiguration.class,
        RedissonAutoConfiguration.class,

        SpringUtil.class
})
```

**关键变化**：
- ❌ 删除 `RedisTestConfiguration.class`（禁用嵌入式 Redis）
- ✅ 保留其他配置不变

## 关键配置对比

| 配置项 | 单元测试 | 集成测试 |
|--------|----------|----------|
| 数据库驱动 | `org.h2.Driver` | `com.mysql.cj.jdbc.Driver` |
| 数据库URL | `jdbc:h2:mem:testdb` | `jdbc:mysql://localhost:3306/db` |
| Redis | jedis-mock (自动启动) | 真实 Redis (需手动启动) |
| RedisTestConfiguration | ✅ 包含 | ❌ 排除 |
| SQL初始化 | ✅ 自动执行 | ❌ 通常禁用 |
| 数据清理 | 每个测试后自动清理 | 需要手动管理 |

## 禁用嵌入式组件的方法

### 方法1：不导入配置类（推荐）

在测试基类的 `@Import` 中移除：
```java
// 删除这行
RedisTestConfiguration.class,
```

### 方法2：使用 @Exclude

```java
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    // 排除嵌入式 Redis 配置
})
```

### 方法3：Profile 控制

```java
@Configuration
@Profile("!integration-test")  // 仅在非集成测试时启用
public class RedisTestConfiguration {
    // ...
}
```

## 运行测试

### 运行单元测试（内存环境）
```bash
mvn test -Dtest=*UnitTestExample
```

### 运行集成测试（真实环境）
```bash
mvn test -Dtest=*IntegrationTestExample
```

### 运行所有测试
```bash
mvn test
```

## 注意事项

### 集成测试注意事项

1. **环境准备**
   - 确保 MySQL 和 Redis 服务已启动
   - 创建测试数据库
   - 配置正确的连接信息

2. **数据管理**
   - 集成测试会产生真实数据
   - 测试前备份重要数据
   - 测试后清理测试数据
   - 使用事务回滚或 `@Sql` 脚本管理数据

3. **测试隔离**
   - 使用独立的测试数据库
   - 避免使用生产环境
   - 使用时间戳或 UUID 作为唯一标识

4. **性能考虑**
   - 集成测试速度较慢
   - 建议只在关键路径使用
   - 可以考虑使用 Testcontainers

### 单元测试注意事项

1. **H2 兼容性**
   - H2 不完全兼容 MySQL
   - 复杂 SQL 可能在真实环境失败
   - 建议同时运行两种测试

2. **嵌入式 Redis 限制**
   - jedis-mock 不支持所有 Redis 命令
   - 某些高级功能可能不可用

## 故障排查

### 连接失败

1. **检查服务是否启动**
   ```bash
   # 检查 MySQL
   mysql -u root -p
   
   # 检查 Redis
   redis-cli ping
   ```

2. **检查配置文件**
   - 确认 URL、用户名、密码正确
   - 确认防火墙设置

3. **查看日志**
   ```yaml
   logging:
     level:
       com.zaxxer.hikari: DEBUG
       io.lettuce.core: DEBUG
   ```

### Bean 冲突

如果出现 Bean 定义冲突：
```yaml
spring:
  main:
    allow-bean-definition-overriding: true
```

## 最佳实践

1. **日常开发**：使用单元测试（快速）
2. **提交前**：运行集成测试（确保兼容）
3. **CI/CD**：两种测试都运行
4. **数据库迁移**：先在集成测试验证
5. **复杂查询**：必须在集成测试验证
