/*
 Navicat Premium Data Transfer

 当前项目的 OAuth2/OIDC 与 Sa-Token SSO 模式三客户端配置使用 oauth_client_details。
 oauth2_registered_client、oauth2_authorization、user_credentials 属于 Spring Authorization Server/WebAuthn 风格表，
 当前 fastbee-oauth-server 代码没有读取这些表，Sa-Token 的 code/token/ticket/session 数据由 Sa-Token DAO
 通过 Redis/Caffeine 管理，因此不在本脚本中创建。
*/

DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
    `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
    `resource_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端所能访问的资源id集合,多个资源时用逗号(,)分隔',
    `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端秘钥',
    `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限范围,多个权限范围用逗号(,)分隔，SSO/OIDC建议包含openid,userinfo',
    `authorized_grant_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权模式，多个grant_type用逗号(,)分隔，常用authorization_code,refresh_token,client_credentials,password',
    `web_server_redirect_uri` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调地址白名单，多个地址用逗号(,)分隔；SSO模式三也复用该字段校验redirect',
    `authorities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
    `access_token_validity` int(11) NULL DEFAULT NULL COMMENT 'access_token有效时间，单位秒',
    `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT 'refresh_token有效时间，单位秒',
    `additional_information` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '预留字段，可填写JSON格式扩展信息',
    `autoapprove` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'false' COMMENT '是否自动授权确认，可选true,false或指定scope',
    `type` tinyint(1) NULL DEFAULT NULL COMMENT '业务类型，历史字段：1=小度(DuerOS),2=天猫精灵(Aligenie),3=小米小爱；通用SSO客户端可为空',
    `status` tinyint(2) NULL DEFAULT 0 COMMENT '启用状态：0=启用，1=停用',
    `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
    `cloud_skill_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '云技能id',
    `tenant_id` bigint(20) NOT NULL COMMENT '租户id',
    `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称',
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_oauth_client_details_client_id` (`client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC COMMENT = 'OAuth2/OIDC/SSO客户端配置表';

-- ----------------------------
-- 示例数据：OAuth2 / OIDC 标准授权码模式
-- 访问示例：
--   /oauth2/authorize?response_type=code&client_id=oidc-demo-client&redirect_uri=http%3A%2F%2F127.0.0.1%3A18080%2Fcallback&scope=openid%20userinfo&state=demo-state
-- 注意：生产环境请替换 client_secret、redirect_uri、tenant_id、tenant_name。
-- ----------------------------
INSERT INTO `oauth_client_details` (
    `client_id`,
    `resource_ids`,
    `client_secret`,
    `scope`,
    `authorized_grant_types`,
    `web_server_redirect_uri`,
    `authorities`,
    `access_token_validity`,
    `refresh_token_validity`,
    `additional_information`,
    `autoapprove`,
    `type`,
    `status`,
    `icon`,
    `cloud_skill_id`,
    `tenant_id`,
    `tenant_name`,
    `create_by`
) VALUES (
    'oidc-demo-client',
    'fastbee-api',
    'oidc-demo-secret',
    'openid,userinfo',
    'authorization_code,refresh_token',
    'http://127.0.0.1:18080/callback',
    'ROLE_CLIENT',
    7200,
    604800,
    '{"usage":"oauth2-oidc","remark":"OAuth2/OIDC授权码模式示例"}',
    'true',
    NULL,
    0,
    NULL,
    NULL,
    1,
    '默认租户',
    'admin'
);

-- ----------------------------
-- 示例数据：Sa-Token SSO 模式三
-- 访问示例：
--   /sso/auth?client=sso-demo-client&redirect=http%3A%2F%2F127.0.0.1%3A18080%2Fsso-callback&state=sso-demo-state&mode=ticket
-- 说明：SSO模式三主要复用 client_id 与 web_server_redirect_uri 做客户端识别和回调白名单校验。
-- 注意：生产环境请替换 client_secret、redirect_uri、tenant_id、tenant_name。
-- ----------------------------
INSERT INTO `oauth_client_details` (
    `client_id`,
    `resource_ids`,
    `client_secret`,
    `scope`,
    `authorized_grant_types`,
    `web_server_redirect_uri`,
    `authorities`,
    `access_token_validity`,
    `refresh_token_validity`,
    `additional_information`,
    `autoapprove`,
    `type`,
    `status`,
    `icon`,
    `cloud_skill_id`,
    `tenant_id`,
    `tenant_name`,
    `create_by`
) VALUES (
    'sso-demo-client',
    'fastbee-sso',
    'sso-demo-secret',
    'openid,userinfo',
    'authorization_code,refresh_token',
    'http://127.0.0.1:18080/sso-callback,http://127.0.0.1:18080/logout-success',
    'ROLE_CLIENT',
    7200,
    604800,
    '{"usage":"sa-token-sso-mode3","remark":"Sa-Token SSO模式三示例"}',
    'true',
    NULL,
    0,
    NULL,
    NULL,
    1,
    '默认租户',
    'admin'
);


INSERT INTO oauth_client_details (
    client_id,
    resource_ids,
    client_secret,
    scope,
    authorized_grant_types,
    web_server_redirect_uri,
    authorities,
    access_token_validity,
    refresh_token_validity,
    additional_information,
    autoapprove,
    type,
    status,
    icon,
    cloud_skill_id,
    tenant_id,
    tenant_name,
    create_by
) VALUES (
             'dueros-test-client',
             'speaker-service',
             'dueros-test-secret',
             'read,write,userinfo',
             'authorization_code,refresh_token',
             'https://xiaodu.baidu.com/saiya/auth/oauth/callback,http://127.0.0.1:18080/dueros/callback',
             'ROLE_CLIENT',
             7200,
             604800,
             '{"platform":"dueros","remark":"DuerOS授权认证兼容测试"}',
             'true',
             1,
             0,
             NULL,
             'dueros-test-skill',
             1,
             '默认租户',
             'admin'
         );
