-- 数据库版本升级脚本
-- 适用于fastbee2.5版本到fastbee2.5.2版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作

ALTER TABLE `iot_firmware`
    ADD COLUMN `firmware_type` tinyint(2) NULL DEFAULT NULL COMMENT '1,二进制包升级2.http升级' AFTER `firmware_name`,
    ADD COLUMN `byte_size` int(5) NULL DEFAULT NULL COMMENT '分包字节大小' AFTER `version`;

ALTER TABLE `gen_table`
    ADD COLUMN `data_name` varchar(200)  NULL DEFAULT '' COMMENT '数据源名称';

-- v2.5.2 修改的内容
ALTER TABLE `gen_table_column`
    ADD COLUMN `remark` varchar(500)  NULL DEFAULT '' COMMENT '备注';

-- 定时日志
ALTER TABLE `sys_job_log`
    MODIFY COLUMN `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用目标字符串';

-- 场景管理
ALTER TABLE `scene_model_data`
    ADD COLUMN `unit` varchar(50) NULL COMMENT '变量单位';

-- 修复国际化物模型表遗漏字段
ALTER TABLE `iot_things_model_translate`
    ADD COLUMN `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID';

ALTER TABLE `sys_menu`
    CHANGE COLUMN `query` `query_param` varchar(255)  NULL DEFAULT '' COMMENT '路由参数';

ALTER TABLE `iot_event_log`
    CHANGE COLUMN `identity` `identify` varchar(64)  NOT NULL COMMENT '标识符';

ALTER TABLE `iot_device_log`
    CHANGE COLUMN `identity` `identify` varchar(64)  NOT NULL COMMENT '标识符';

ALTER TABLE `bridge`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0-未连接，1-连接中）';

ALTER TABLE `iot_device_job`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0正常 1暂停）';

ALTER TABLE `iot_modbus_job`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0正常 1暂停）';

ALTER TABLE `iot_product_modbus_job`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）';

ALTER TABLE `iot_social_platform`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL DEFAULT '0' COMMENT ' 0:启用 ,1:禁用';

ALTER TABLE `iot_social_user`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL DEFAULT 0 COMMENT '绑定状态(0:未绑定,1:绑定)';

ALTER TABLE `oss_config`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL DEFAULT 1 COMMENT '是否默认（0=是,1=否）';

ALTER TABLE `sys_dept`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT '0' COMMENT '机构状态（0正常 1停用）';

ALTER TABLE `sys_dict_data`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT '0' COMMENT '状态（0正常 1停用）';

ALTER TABLE `sys_dict_type`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0正常 1停用）';

ALTER TABLE `sys_job`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0正常 1暂停）';

ALTER TABLE `sys_logininfor`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '登陆状态（0成功 1失败）';

ALTER TABLE `sys_job_log`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '状态（0正常 1失败）';

ALTER TABLE `sys_menu`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）';

ALTER TABLE `sys_notice`
    MODIFY COLUMN `status` tinyint(1)  NULL DEFAULT 0 COMMENT '公告状态（0正常 1关闭）';

ALTER TABLE `sys_post`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL COMMENT '状态（0正常 1停用）';

ALTER TABLE `sys_role`
    MODIFY COLUMN `status` tinyint(1)  NOT NULL COMMENT '角色状态（0正常 1停用）';

ALTER TABLE `sys_user`
    MODIFY COLUMN status tinyint(1) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
    MODIFY COLUMN del_flag tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）';

ALTER TABLE `media_server`
    CHANGE COLUMN `domain` `domain_alias` varchar(128)  NOT NULL DEFAULT '' COMMENT '服务器域名';

ALTER TABLE `oss_config`
    CHANGE COLUMN `domain` `domain_alias` varchar(255)  NOT NULL DEFAULT '' COMMENT '自定义域名';

ALTER TABLE `sip_config`
    CHANGE COLUMN `seniorSdp` `senior_sdp` tinyint(1) NULL DEFAULT NULL COMMENT '拓展sdp',
    CHANGE COLUMN `domain` `domain_alias` varchar(255)  NOT NULL DEFAULT '' COMMENT '服务器域';

ALTER TABLE `sip_device`
    CHANGE COLUMN `streamMode` `stream_mode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'UDP' COMMENT '流模式',
    CHANGE COLUMN `registerTime` `register_time` datetime NOT NULL COMMENT '注册时间',
    CHANGE COLUMN `lastConnectTime` `last_connect_time` datetime NULL DEFAULT NULL COMMENT '最后上线时间',
    CHANGE COLUMN `hostAddress` `host_address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备地址';

ALTER TABLE `sip_device_channel`
    CHANGE COLUMN `cityCode` `city_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '城市编码',
    CHANGE COLUMN `civilCode` `civil_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '行政区域',
    CHANGE COLUMN `parentId` `parent_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '父级id',
    CHANGE COLUMN `ipAddress` `ip_address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '设备入网IP',
    CHANGE COLUMN `PTZType` `ptz_type` bigint(20) NOT NULL DEFAULT 0 COMMENT 'PTZ类型',
    CHANGE COLUMN `PTZTypeText` `ptz_type_text` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'PTZ类型描述字符串',
    CHANGE COLUMN `streamId` `stream_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体ID',
    CHANGE COLUMN `subCount` `sub_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '子设备数',
    CHANGE COLUMN `hasAudio` `has_audio` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否含有音频（1-有, 0-没有）';

INSERT INTO `sys_menu` VALUES (3372, '固件升级推送', 2173, 6, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:task:upgrade', '#', 'admin', '2025-01-22 10:35:47', '', NULL, '');
INSERT INTO `sys_menu_translate` VALUES (3372, '固件升级推送', 'Firmware task upgrade');
