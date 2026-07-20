package com.fastbee.ai.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;
import com.fastbee.common.exception.ServiceException;

/**
 * NL2SQL 运行时业务语义守卫回归测试。
 */
class AiNl2SqlGenerateServiceImplTest {

    private final AiNl2SqlGenerateServiceImpl service = new AiNl2SqlGenerateServiceImpl();

    @Test
    void shouldBuildDeterministicAlertTrendSql() throws Exception {
        Object result = invokePrivateMethod("buildAlertTrendSql",
                new Class[]{int.class, com.fastbee.common.mybatis.enums.DataBaseType.class},
                7, com.fastbee.common.mybatis.enums.DataBaseType.MY_SQL);

        String sql = String.valueOf(result);
        Assertions.assertTrue(sql.contains("FROM iot_alert_log"));
        Assertions.assertTrue(sql.contains("DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY)"));
        Assertions.assertTrue(sql.contains("GROUP BY DATE(create_time)"));
    }

    @Test
    void shouldBlockAlertTrendWhenModelDriftsToMaintenanceTable() {
        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setSql("SELECT DATE(create_time) AS day, COUNT(*) AS total FROM iot_device_maintenance GROUP BY DATE(create_time)");
        result.setTables(List.of("iot_device_maintenance"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> invokeValidateBusinessSemantic("统计近7天告警趋势", result));

        Assertions.assertTrue(exception.getMessage().contains("iot_alert_log"));
        Assertions.assertTrue(exception.getMessage().contains("告警趋势"));
    }

    @Test
    void shouldAllowAlertTrendWhenFactTableIsAlertLog() {
        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setSql("SELECT DATE(create_time) AS day, COUNT(*) AS total FROM iot_alert_log GROUP BY DATE(create_time)");
        result.setTables(List.of("iot_alert_log"));

        Assertions.assertDoesNotThrow(() -> invokeValidateBusinessSemantic("统计近7天告警趋势", result));
    }

    @Test
    void shouldBlockDeviceRuntimeMetricValueSqlWhenRuntimeSemanticMissed() {
        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setSql("SELECT device_id, product_name, things_model_value FROM iot_device WHERE product_name = '远程采集控制设备' LIMIT 1");
        result.setTables(List.of("iot_device"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> invokeValidateBusinessSemantic("远程采集控制设备的PM10数值是多少", result));

        Assertions.assertTrue(exception.getMessage().contains("设备运行时指标问数"));
        Assertions.assertTrue(exception.getMessage().contains("Redis"));
    }

    @Test
    void shouldBuildDeterministicPendingAlertCountSql() throws Exception {
        Object result = invokePrivateMethod("tryBuildDeterministicStructuredResult",
                new Class[]{String.class, com.fastbee.ai.model.vo.AiSemanticContextVO.class},
                "查询待处理告警数量", null);

        Assertions.assertTrue(result instanceof AiNl2SqlStructuredResultVO);
        AiNl2SqlStructuredResultVO structuredResult = (AiNl2SqlStructuredResultVO) result;
        Assertions.assertTrue(structuredResult.getSql().contains("FROM iot_alert_log"));
        Assertions.assertTrue(structuredResult.getSql().contains("status = 2"));
        Assertions.assertEquals(List.of("iot_alert_log"), structuredResult.getTables());

        Object colloquialResult = invokePrivateMethod("tryBuildDeterministicStructuredResult",
                new Class[]{String.class, com.fastbee.ai.model.vo.AiSemanticContextVO.class},
                "待处理告警有多少", null);
        Assertions.assertTrue(colloquialResult instanceof AiNl2SqlStructuredResultVO);
    }

    @Test
    void shouldBlockPendingAlertWhenModelDriftsToWorkOrderTable() {
        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setSql("SELECT COUNT(*) AS pending_count FROM iot_work_order WHERE status = 2");
        result.setTables(List.of("iot_work_order"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> invokeValidateBusinessSemantic("查询待处理告警数量", result));

        Assertions.assertTrue(exception.getMessage().contains("iot_alert_log"));
        Assertions.assertTrue(exception.getMessage().contains("待处理告警"));
    }

    private void invokeValidateBusinessSemantic(String question, AiNl2SqlStructuredResultVO result) throws Exception {
        invokePrivateMethod("validateBusinessSemantic",
                new Class[]{String.class, com.fastbee.ai.model.vo.AiSemanticContextVO.class, AiNl2SqlStructuredResultVO.class},
                question, null, result);
    }

    private Object invokePrivateMethod(String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = AiNl2SqlGenerateServiceImpl.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }
}
