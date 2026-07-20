package com.fastbee.record;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
@EnableScheduling
@ComponentScan(basePackages = {"com.fastbee.common.core.redis","com.fastbee.record","com.fastbee.common.utils.spring"})
public class ZlmRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZlmRecordApplication.class, args);
    }

}
