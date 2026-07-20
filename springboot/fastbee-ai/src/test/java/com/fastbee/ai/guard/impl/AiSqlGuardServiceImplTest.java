package com.fastbee.ai.guard.impl;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.common.exception.ServiceException;

class AiSqlGuardServiceImplTest {

    private AiSqlGuardServiceImpl guardService;

    @BeforeEach
    void setUp() throws Exception {
        guardService = new AiSqlGuardServiceImpl();
        FastBeeAiProperties properties = new FastBeeAiProperties();
        Field propertiesField = AiSqlGuardServiceImpl.class.getDeclaredField("properties");
        propertiesField.setAccessible(true);
        propertiesField.set(guardService, properties);
    }

    @Test
    void shouldAllowReadOnlyQueryWithOrderByDesc() {
        Assertions.assertDoesNotThrow(() -> guardService.guardReadOnlySelect(
                "SELECT p.product_id, p.product_name, COUNT(d.device_id) AS offline_device_count " +
                        "FROM iot_product p LEFT JOIN iot_device d ON p.product_id = d.product_id " +
                        "GROUP BY p.product_id, p.product_name ORDER BY offline_device_count DESC",
                200));
    }

    @Test
    void shouldBlockDangerousUnionKeyword() {
        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> guardService.guardReadOnlySelect("SELECT * FROM iot_device UNION SELECT * FROM sys_user", 200));

        Assertions.assertTrue(exception.getMessage().contains("高风险关键字"));
    }
}
