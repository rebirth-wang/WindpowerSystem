package com.fastbee.ai.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.service.IAiNl2SqlScopeSemanticResolver;

/**
 * NL2SQL 数据范围路由回归测试。
 */
class AiNl2SqlQueryServiceImplTest {

    private final AiNl2SqlQueryServiceImpl service = new AiNl2SqlQueryServiceImpl();

    @Test
    void shouldTreatDeviceMaintenanceAsTenantScopeTable() throws Exception {
        Object scopeMode = invokeResolveScopeMode(List.of("iot_device_maintenance"));
        Assertions.assertEquals("TENANT", String.valueOf(scopeMode));
    }

    @Test
    void shouldTreatAiGovernanceTablesAsTenantScopeTables() throws Exception {
        Assertions.assertEquals("TENANT", String.valueOf(invokeResolveScopeMode(List.of("ai_provider"))));
        Assertions.assertEquals("TENANT", String.valueOf(invokeResolveScopeMode(List.of("ai_model"))));
        Assertions.assertEquals("TENANT", String.valueOf(invokeResolveScopeMode(List.of("ai_chat_session"))));
        Assertions.assertEquals("TENANT", String.valueOf(invokeResolveScopeMode(List.of("ai_knowledge_base"))));
        Assertions.assertEquals("TENANT", String.valueOf(invokeResolveScopeMode(List.of("ai_protocol_adaptation_task"))));
    }

    @Test
    void shouldTreatAiProviderAndModelJoinAsTenantScopeQuery() throws Exception {
        Object scopeMode = invokeResolveScopeMode(List.of("ai_provider", "ai_model"));
        Assertions.assertEquals("TENANT", String.valueOf(scopeMode));
    }

    @Test
    void shouldCorrectDanglingSingleTableColumnQualifier() throws Exception {
        String correctedSql = invokeCorrectDanglingColumnQualifiersIfNecessary(
                "SELECT COUNT(DISTINCT d.product_id) FROM iot_device WHERE d.del_flag = 0",
                List.of("iot_device"));

        Assertions.assertEquals(
                "SELECT COUNT(DISTINCT iot_device.product_id) FROM iot_device WHERE iot_device.del_flag = 0",
                correctedSql);
    }

    @Test
    void shouldKeepDeclaredSingleTableAliasQualifier() throws Exception {
        String sql = "SELECT COUNT(DISTINCT d.product_id) FROM iot_device d WHERE d.del_flag = 0";

        String correctedSql = invokeCorrectDanglingColumnQualifiersIfNecessary(sql, List.of("iot_device"));

        Assertions.assertEquals(sql, correctedSql);
    }

//    @Test
//    void shouldResolveTenantScopeFromSemanticLibraryBeforeBuiltinFallback() throws Exception {
//        setSemanticResolver(tables -> {
//            Map<String, SemanticScopeMode> scopeModes = new HashMap<>();
//            scopeModes.put("customer_ext_order", SemanticScopeMode.TENANT);
//            return scopeModes;
//        });
//
//        Object scopeMode = invokeResolveScopeMode(List.of("customer_ext_order"));
//
//        Assertions.assertEquals("TENANT", String.valueOf(scopeMode));
//    }
//
//    @Test
//    void shouldResolveUserScopeFromSemanticLibraryBeforeBuiltinFallback() throws Exception {
//        setSemanticResolver(tables -> {
//            Map<String, SemanticScopeMode> scopeModes = new HashMap<>();
//            scopeModes.put("customer_ext_log", SemanticScopeMode.USER);
//            return scopeModes;
//        });
//
//        Object scopeMode = invokeResolveScopeMode(List.of("customer_ext_log"));
//
//        Assertions.assertEquals("USER", String.valueOf(scopeMode));
//    }

    private void setSemanticResolver(IAiNl2SqlScopeSemanticResolver resolver) throws Exception {
        Field field = AiNl2SqlQueryServiceImpl.class.getDeclaredField("aiNl2SqlScopeSemanticResolver");
        field.setAccessible(true);
        field.set(service, resolver);
    }

    private Object invokeResolveScopeMode(List<String> tables) throws Exception {
        Method method = AiNl2SqlQueryServiceImpl.class.getDeclaredMethod("resolveScopeMode", List.class);
        method.setAccessible(true);
        try {
            return method.invoke(service, tables);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }

    private String invokeCorrectDanglingColumnQualifiersIfNecessary(String sql, List<String> tables) throws Exception {
        Method method = AiNl2SqlQueryServiceImpl.class.getDeclaredMethod(
                "correctDanglingColumnQualifiersIfNecessary", String.class, List.class);
        method.setAccessible(true);
        return String.valueOf(method.invoke(service, sql, tables));
    }
}
