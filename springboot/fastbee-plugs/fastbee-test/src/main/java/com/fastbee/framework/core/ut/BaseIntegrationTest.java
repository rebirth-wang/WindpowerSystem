package com.fastbee.framework.core.ut;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.fastbee.framework.config.DataSourceConfig;
import com.fastbee.framework.config.RedisConfig;
import com.fastbee.framework.config.SqlInitializationTestConfiguration;

/**
 * 集成测试基类 - 使用真实的数据库和 Redis
 * 
 * 与 {@link BaseDbAndRedisUnitTest} 的区别：
 * - 使用真实的 MySQL 数据库，而非 H2 内存数据库
 * - 使用真实的 Redis，而非 jedis-mock 嵌入式 Redis
 * - 不包含 RedisTestConfiguration（嵌入式 Redis 配置）
 * 
 * 使用方式：
 * 1. 确保本地或测试环境有可用的 MySQL 和 Redis
 * 2. 在 application-integration-test.yml 中配置连接信息
 * 3. 测试类继承此类即可
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseIntegrationTest.Application.class)
@ActiveProfiles("integration-test") // 设置使用 application-integration-test 配置文件
public class BaseIntegrationTest {

    @Import({
            // DB 配置类
            DataSourceConfig.class, // 自己的 DB 配置类
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            SqlInitializationTestConfiguration.class, // SQL 初始化
            
            // MyBatis 配置类
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类
            MybatisPlusJoinAutoConfiguration.class, // MyBatis 的Join配置类

            // Redis 配置类
            // 注意：不包含 RedisTestConfiguration，因为使用的是真实 Redis
            RedisConfig.class, // 自己的 Redis 配置类（带 JavaTimeModule 支持）
            RedisAutoConfiguration.class, // Spring Redis 自动配置类
            RedissonAutoConfiguration.class, // Redisson 自动配置类

            // 其它配置类
            SpringUtil.class
    })
    @MapperScan(basePackages = {"com.fastbee.framework.test.core.ut.mapper"}) // 根据需要修改包路径
    public static class Application {
    }

}
