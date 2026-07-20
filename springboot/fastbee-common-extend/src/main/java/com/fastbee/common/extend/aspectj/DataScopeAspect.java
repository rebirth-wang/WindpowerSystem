package com.fastbee.common.extend.aspectj;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.core.text.Convert;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.mybatis.enums.DataBaseType;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.framework.security.context.PermissionContextHolder;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    /**
     * 租户字段为userId时注解字段
     */
    public static final String DATA_SCOPE_FILED_ALIAS_USER_ID = "userId";

    /**
     * 查机构时注解字段
     */
    public static final String DATA_SCOPE_FILED_ALIAS_DEPT_ID = "deptId";

    /**
     * 自己数据权限只查自己
     */
    public static final String DATA_SCOPE_FILED_SELECT_OWNER = "selectOwner";
    public static final String DATA_SCOPE_FILED_SELECT_SIP = "sip";

    private static final DynamicRoutingDataSource DS = SpringUtils.getBean(DynamicRoutingDataSource.class);
    public static final String DEFAULT_DATASOURCE_NAME = "master";

    /**
     * 获取当前数据库类型
     */
    public static DataBaseType getDataBaseType(String dataName) {
        DataSource dataSource = DS.getDataSources().get(dataName);
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            return DataBaseType.find(databaseProductName);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext());
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
                        controllerDataScope.userAlias(), permission, controllerDataScope.fieldAlias());
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias, String permission, String fieldAlias)
    {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (!DATA_SCOPE_CUSTOM.equals(dataScope) && conditions.contains(dataScope))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && StringUtils.isNotEmpty(role.getPermissions())
                    && !StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                break;
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                        role.getRoleId()));
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope))
            {
                if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_USER_ID.equals(fieldAlias)) {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        sqlString.append(StringUtils.format(" OR {}.user_id = {} ", deptAlias, user.getDept().getDeptUserId()));
                    } else {
                        sqlString.append(StringUtils.format(" OR user_id = {} ", user.getDept().getDeptUserId()));
                    }
                } else if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_DEPT_ID.equals(fieldAlias)) {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDept().getDeptId()));
                    } else {
                        sqlString.append(StringUtils.format(" OR dept_id = {} ", user.getDept().getDeptId()));
                    }
                } else {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        sqlString.append(StringUtils.format(" OR {}.tenant_id = {} ", deptAlias, user.getDept().getDeptUserId()));
                    } else {
                        sqlString.append(StringUtils.format(" OR tenant_id = {} ", user.getDept().getDeptUserId()));
                    }
                }
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                DataBaseType dataBaseType = getDataBaseType(DEFAULT_DATASOURCE_NAME);
                if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_USER_ID.equals(fieldAlias)) {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} OR {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR {}.user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    } else {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        user.getDeptId(),
                                        user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR user_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    }
                } else if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_DEPT_ID.equals(fieldAlias)) {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} OR {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    } else {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        user.getDeptId(),
                                        user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    }
                } else {
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR {}.tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        deptAlias, user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    } else {
                        switch (dataBaseType) {
                            case SQL_SERVER:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or CHARINDEX( CAST( {} AS VARCHAR(100) ), ',' + ancestors + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case ORACLE:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' + CAST( {} AS VARCHAR(100) ) + ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case POSTGRE_SQL:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or {} = ANY(string_to_array(ancestors, ',')::integer[]) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case DM:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} OR INSTR(','||ancestors||',', ',%s,') > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            case KINGBASE:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or instr( ',' + ancestors + ',', ',' || CAST( {} AS VARCHAR(100) ) || ',' ) > 0 )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                            default:
                                sqlString.append(StringUtils.format(
                                        " OR tenant_id IN ( SELECT dept_user_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                                        user.getDeptId(), user.getDeptId()));
                                break;
                        }
                    }
                }
            }

            else if (DATA_SCOPE_SELF.equals(dataScope))
            {
                if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_SELECT_SIP.equals(fieldAlias)) {
                    continue;
                }

                // 严格的本人数据权限：必须同时满足创建者条件和当前租户范围
                StringBuilder selfCondition = new StringBuilder();

                // 1. 创建者条件
                if (StringUtils.isNotBlank(userAlias)) {
                    selfCondition.append(StringUtils.format("{}.create_by = '{}'", userAlias, user.getUserName()));
                } else {
                    selfCondition.append(StringUtils.format("create_by = '{}'", user.getUserName()));
                }

                // 2. 当前租户范围条件（必须属于用户当前所在租户）
                selfCondition.append(" AND ");
                if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_USER_ID.equals(fieldAlias)) {
                    Long scopeUserId = user.getDeptId() == null ? user.getUserId() : user.getDept().getDeptUserId();
                    // 用户ID字段别名处理
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        selfCondition.append(StringUtils.format("{}.user_id = {}", deptAlias, scopeUserId));
                    } else {
                        selfCondition.append(StringUtils.format("user_id = {}", scopeUserId));
                    }
                } else if (StringUtils.isNotEmpty(fieldAlias) && DATA_SCOPE_FILED_ALIAS_DEPT_ID.equals(fieldAlias)) {
                    // 部门ID字段别名处理
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        selfCondition.append(StringUtils.format("{}.dept_id = {}", deptAlias, user.getDeptId()));
                    } else {
                        selfCondition.append(StringUtils.format("dept_id = {}", user.getDeptId()));
                    }
                } else {
                    // 默认租户字段条件
                    if (StringUtils.isNotEmpty(deptAlias)) {
                        selfCondition.append(StringUtils.format("{}.tenant_id = {}", deptAlias, user.getDept().getDeptUserId()));
                    } else {
                        selfCondition.append(StringUtils.format("tenant_id = {}", user.getDept().getDeptUserId()));
                    }
                }

                sqlString.append(StringUtils.format(" OR ({}) ", selfCondition.toString()));
                conditions.add(dataScope);
            }
        }

        if (StringUtils.isNotBlank(sqlString.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, sqlString.substring(4));
            } else if (StringUtils.isNotNull(params) && params instanceof PageEntity) {
                PageEntity pageEntity = (PageEntity) params;
                pageEntity.getParams().put(DATA_SCOPE, sqlString.substring(4));
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
        if (StringUtils.isNotNull(params) && params instanceof PageEntity)
        {
            PageEntity pageEntity = (PageEntity) params;
            pageEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
