-- ----------------------------
-- 升级SQL v2.8.0 -> v2.9.0
-- ----------------------------

-- ----------------------------
-- 1. 产品扩展参数表
-- ----------------------------
DROP TABLE IF EXISTS `iot_product_ext_param`;
CREATE TABLE `iot_product_ext_param` (
                                         `param_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '参数ID',
                                         `product_id` bigint(20) NOT NULL COMMENT '产品ID',
                                         `param_name` varchar(64) NOT NULL COMMENT '参数名称',
                                         `param_type` varchar(32) NOT NULL COMMENT '参数类型',
                                         `default_value` varchar(255) DEFAULT NULL COMMENT '默认值',
                                         `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用(0-否,1-是)',
                                         `is_app_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在APP显示(0-否,1-是)',
                                         `spec` varchar(255) DEFAULT NULL COMMENT '规格',
                                         `description` varchar(500) DEFAULT NULL COMMENT '描述',
                                         `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0-存在,1-删除)',
                                         `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`param_id`),
                                         KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品扩展参数表';

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数', '2043', '1', 'productExtParam', 'iot/productExtParam/index', 1, 0, 'C', '0', '0', 'iot:productExtParam:list', '#', 'admin', sysdate(), '', null, '产品扩展参数菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:productExtParam:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:productExtParam:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:productExtParam:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:productExtParam:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('产品扩展参数导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:productExtParam:export',       '#', 'admin', sysdate(), '', null, '');

-- 菜单翻译 SQL
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`)
SELECT `menu_id`,
       `menu_name`,
       CASE `perms`
           WHEN 'iot:productExtParam:list' THEN 'Product Extension Parameters'
           WHEN 'iot:productExtParam:query' THEN 'Product Extension Parameter Query'
           WHEN 'iot:productExtParam:add' THEN 'Product Extension Parameter Add'
           WHEN 'iot:productExtParam:edit' THEN 'Product Extension Parameter Edit'
           WHEN 'iot:productExtParam:remove' THEN 'Product Extension Parameter Remove'
           WHEN 'iot:productExtParam:export' THEN 'Product Extension Parameter Export'
           ELSE `menu_name`
       END
FROM `sys_menu`
WHERE (`menu_id` = @parentId OR `parent_id` = @parentId)
  AND `perms` IN (
      'iot:productExtParam:list',
      'iot:productExtParam:query',
      'iot:productExtParam:add',
      'iot:productExtParam:edit',
      'iot:productExtParam:remove',
      'iot:productExtParam:export'
  );

-- ----------------------------
-- 2. 设备扩展参数值表
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_ext_param_value`;
CREATE TABLE `iot_device_ext_param_value` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                              `device_id` bigint(20) NOT NULL COMMENT '设备ID',
                                              `param_id` bigint(20) NOT NULL COMMENT '参数ID',
                                              `param_value` varchar(500) DEFAULT NULL COMMENT '参数值',
                                              `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `idx_device_param` (`device_id`,`param_id`),
                                              KEY `idx_device_id` (`device_id`),
                                              KEY `idx_param_id` (`param_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备扩展参数值表';

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值', '2007', '1', 'deviceExtValue', 'iot/deviceExtValue/index', 1, 0, 'C', '0', '0', 'iot:deviceExtValue:list', '#', 'admin', sysdate(), '', null, '设备扩展参数值菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:deviceExtValue:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:deviceExtValue:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:deviceExtValue:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:deviceExtValue:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备扩展参数值导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:deviceExtValue:export',       '#', 'admin', sysdate(), '', null, '');

-- 菜单翻译 SQL
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`)
SELECT `menu_id`,
       `menu_name`,
       CASE `perms`
           WHEN 'iot:deviceExtValue:list' THEN 'Device Extension Parameter Values'
           WHEN 'iot:deviceExtValue:query' THEN 'Device Extension Parameter Value Query'
           WHEN 'iot:deviceExtValue:add' THEN 'Device Extension Parameter Value Add'
           WHEN 'iot:deviceExtValue:edit' THEN 'Device Extension Parameter Value Edit'
           WHEN 'iot:deviceExtValue:remove' THEN 'Device Extension Parameter Value Remove'
           WHEN 'iot:deviceExtValue:export' THEN 'Device Extension Parameter Value Export'
           ELSE `menu_name`
       END
FROM `sys_menu`
WHERE (`menu_id` = @parentId OR `parent_id` = @parentId)
  AND `perms` IN (
      'iot:deviceExtValue:list',
      'iot:deviceExtValue:query',
      'iot:deviceExtValue:add',
      'iot:deviceExtValue:edit',
      'iot:deviceExtValue:remove',
      'iot:deviceExtValue:export'
  );


-- ----------------------------
-- 3. 字典类型 - 产品扩展参数类型
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (230, '产品扩展参数类型', 'product_ext_param_type', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` VALUES (230, '产品扩展参数类型', 'Product extension parameter type');

-- ----------------------------
-- 4. 字典数据 - 参数类型
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (970, 1, '文字', 'text', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (971, 2, '数值', 'number', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (972, 3, '开关', 'switch', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (973, 4, '选项', 'select', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (974, 5, '日期', 'date', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (975, 6, '时间', 'time', 'product_ext_param_type', NULL, 'default', 'N', 0, 'admin', NOW(), '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (976, 6, '推流设备', 'STREAM_PUSH', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data` VALUES (977, 7, '拉流代理', 'STREAM_PROXY', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data` VALUES (978, 8, 'ONVIF', 'ONVIF', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data` VALUES (980, 10, 'ISUP', 'ISUP', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data` VALUES (981, 11, '萤石云', 'EZVIZ', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data` VALUES (982, 12, '大华ICC', 'ICC_DAHUA', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);

INSERT INTO `sys_dict_data_translate` VALUES (970, '文字', 'text');
INSERT INTO `sys_dict_data_translate` VALUES (971, '数值', 'number');
INSERT INTO `sys_dict_data_translate` VALUES (972, '开关', 'switch');
INSERT INTO `sys_dict_data_translate` VALUES (973, '选项', 'select');
INSERT INTO `sys_dict_data_translate` VALUES (974, '日期', 'date');
INSERT INTO `sys_dict_data_translate` VALUES (975, '时间', 'time');
INSERT INTO `sys_dict_data_translate` VALUES (976, '推流设备', 'Stream push');
INSERT INTO `sys_dict_data_translate` VALUES (977, '拉流代理', 'Stream proxy');
INSERT INTO `sys_dict_data_translate` VALUES (978, 'ONVIF', 'ONVIF');
INSERT INTO `sys_dict_data_translate` VALUES (980, 'ISUP', 'ISUP');
INSERT INTO `sys_dict_data_translate` VALUES (981, '萤石云', 'EZVIZ');
INSERT INTO `sys_dict_data_translate` VALUES (982, '大华ICC', 'Dahua ICC');

ALTER TABLE `sip_device`
    ADD COLUMN `server_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '接入sip服务ID';

DROP TABLE IF EXISTS `common_channel`;
CREATE TABLE `common_channel`  (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `tenant_id` bigint(20) NULL DEFAULT 1,
                                   `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
                                   `device_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备ID',
                                   `channel_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '通道ID',
                                   `channel_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '通道名称',
                                   `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
                                   `device_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备类型',
                                   `channel_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '通道类型',
                                   `data_type` int NULL DEFAULT 0 COMMENT '流数据类型',
                                   `city_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '城市编码',
                                   `civil_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '行政区域',
                                   `manufacture` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '厂商名称',
                                   `model` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品型号',
                                   `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道归属',
                                   `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道口令',
                                   `parent_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '父级id',
                                   `ip_address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '设备入网IP',
                                   `port` bigint(10) NULL DEFAULT 0 COMMENT '设备接入端口号',
                                   `ptz_type` bigint(20) NULL DEFAULT 0 COMMENT 'PTZ类型',
                                   `ptz_type_text` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'PTZ类型描述字符串',
                                   `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
                                   `longitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备经度',
                                   `latitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备纬度',
                                   `stream_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体ID',
                                   `play_url` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道播放地址',
                                   `proxy_url` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流代理地址',
                                   `has_audio` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否含有音频（1-有, 0-没有）',
                                   `has_broadcast` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否支持对讲（1-支持, 0-不支持）',
                                   `ctrl_config` json NULL COMMENT '设备控制配置',
                                   `extend_info` json NULL COMMENT '拓展信息',
                                   `del_flag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                   `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                   `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX  idx_channel (device_id, channel_id, del_flag)
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控视频通道信息' ROW_FORMAT = DYNAMIC;

UPDATE `sys_menu`
SET
    `menu_name` = '固定式大屏',
    `path` = 'https://iot.fastbee.cn/fixedBigScreen'
WHERE `menu_id` = 2149;

UPDATE `sys_menu_translate`
SET
    `zh_cn` = '固定式大屏',
    `en_us` = 'Fixed Screen'
WHERE `id` = 2149;


UPDATE `sys_menu`
SET
    `path` = 'project',
    `component` = 'visualBigScreen/management/views/project/items/index',
    `is_frame` = 1,
    `perms` = 'goview:project:list'
WHERE `menu_id` = 2167;

UPDATE `sys_menu_translate`
SET
    `zh_cn` = '可视化管理',
    `en_us` = 'Visual Mgmt'
WHERE `id` = 2167;

UPDATE `sys_menu`
SET
    `path` = 'scadaModel'
WHERE `menu_id` = 3159;

-- 可视化新增菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化查询', 2167, 0, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:query', '#', 'admin', '2026-05-07 16:20:02', 'admin', '2026-05-07 16:22:02', '', 0);
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化新增', 2167, 1, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:add', '#', 'admin', '2026-05-07 16:23:07', '', NULL, '', 0);
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化修改', 2167, 2, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:edit', '#', 'admin', '2026-05-07 16:24:22', '', NULL, '', 0);
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化删除', 2167, 3, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:remove', '#', 'admin', '2026-05-07 16:25:19', '', NULL, '', 0);
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化预览', 2167, 4, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:preview', '#', 'admin', '2026-05-07 16:31:34', '', NULL, '', 0);
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES ('可视化发布', 2167, 5, '', NULL, NULL, 1, 0, 'F', '0', 0, 'goview:project:release', '#', 'admin', '2026-05-07 16:37:33', '', NULL, '', 0);

-- 菜单翻译 SQL
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`)
SELECT `menu_id`,
       `menu_name`,
       CASE `perms`
           WHEN 'goview:project:query' THEN 'Visualization Query'
           WHEN 'goview:project:add' THEN 'Visualization Add'
           WHEN 'goview:project:edit' THEN 'Visualization Edit'
           WHEN 'goview:project:remove' THEN 'Visualization Remove'
           WHEN 'goview:project:preview' THEN 'Visualization Preview'
           WHEN 'goview:project:release' THEN 'Visualization Release'
           ELSE `menu_name`
       END
FROM `sys_menu`
WHERE `parent_id` = 2167
  AND `perms` IN (
      'goview:project:query',
      'goview:project:add',
      'goview:project:edit',
      'goview:project:remove',
      'goview:project:preview',
      'goview:project:release'
  );

-- ----------------------------
-- AI 一期初始化脚本 v3.0.0
-- 适用数据库：MySQL
-- ----------------------------

-- ----------------------------
-- 1. AI 厂商管理表
-- ----------------------------
DROP TABLE IF EXISTS `ai_provider`;
CREATE TABLE `ai_provider` (
                               `provider_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '厂商ID',
                               `provider_code` varchar(64) NOT NULL COMMENT '厂商编码',
                               `provider_name` varchar(128) NOT NULL COMMENT '厂商名称',
                               `region_profile` varchar(32) NOT NULL DEFAULT 'CN' COMMENT '区域档位',
                               `api_base_url` varchar(255) DEFAULT '' COMMENT '接口基础地址',
                               `auth_type` varchar(32) NOT NULL DEFAULT 'API_KEY' COMMENT '鉴权方式',
                               `api_key_cipher` varchar(1024) DEFAULT '' COMMENT '加密后的密钥',
                               `extra_config` text COMMENT '扩展配置',
                               `sort_num` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
                               `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                               `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                               `tenant_name` varchar(30) DEFAULT '' COMMENT '租户名称',
                               `remark` varchar(500) DEFAULT '' COMMENT '备注',
                               `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                               PRIMARY KEY (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 厂商管理表';

-- ----------------------------
-- 2. AI 模型管理表
-- ----------------------------
DROP TABLE IF EXISTS `ai_model`;
CREATE TABLE `ai_model` (
                            `model_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模型ID',
                            `provider_id` bigint(20) NOT NULL COMMENT '厂商ID',
                            `model_code` varchar(128) NOT NULL COMMENT '模型编码',
                            `model_name` varchar(128) NOT NULL COMMENT '模型名称',
                            `model_type` varchar(32) NOT NULL DEFAULT 'CHAT' COMMENT '模型类型',
                            `context_length` int(11) DEFAULT 0 COMMENT '上下文长度',
                            `request_options` text COMMENT '默认推理参数',
                            `is_default` char(1) NOT NULL DEFAULT '0' COMMENT '是否默认(0否 1是)',
                            `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                            `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                            `tenant_name` varchar(30) DEFAULT '' COMMENT '租户名称',
                            `remark` varchar(500) DEFAULT '' COMMENT '备注',
                            `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                            PRIMARY KEY (`model_id`),
                            KEY `idx_ai_model_provider_id` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 模型管理表';

-- ----------------------------
-- 3. AI 会话表
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session` (
                                   `session_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
                                   `session_code` varchar(64) NOT NULL COMMENT '会话编码',
                                   `session_title` varchar(255) DEFAULT '' COMMENT '会话标题',
                                   `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
                                   `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                   `tenant_name` varchar(30) DEFAULT '' COMMENT '租户名称',
                                   `provider_code` varchar(64) DEFAULT '' COMMENT '厂商编码',
                                   `model_code` varchar(128) DEFAULT '' COMMENT '模型编码',
                                   `chat_mode` varchar(32) NOT NULL DEFAULT 'AUTO' COMMENT '兼容会话模式快照',
                                   `mode_policy` varchar(16) NOT NULL DEFAULT 'AUTO' COMMENT '会话策略(AUTO自动识别 PINNED锁定会话)',
                                   `pinned_mode` varchar(32) DEFAULT '' COMMENT '会话锁定模式',
                                   `last_effective_mode` varchar(32) DEFAULT '' COMMENT '最近实际能力',
                                   `is_archived` char(1) NOT NULL DEFAULT '0' COMMENT '是否归档(0否 1是)',
                                   `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                   `last_message_time` datetime DEFAULT NULL   COMMENT '最后消息时间',
                                   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                   PRIMARY KEY (`session_id`),
                                   UNIQUE KEY `uk_ai_chat_session_code` (`session_code`),
                                   KEY `idx_ai_chat_session_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 会话表';

-- ----------------------------
-- 4. AI 会话消息表
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message` (
                                   `message_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                                   `session_id` bigint(20) NOT NULL COMMENT '会话ID',
                                   `message_no` int(11) NOT NULL DEFAULT 1 COMMENT '消息序号',
                                   `role_type` varchar(32) NOT NULL COMMENT '消息角色',
                                   `message_content` longtext COMMENT '消息内容',
                                   `message_summary` varchar(1000) DEFAULT '' COMMENT '消息摘要',
                                   `tool_name` varchar(128) DEFAULT '' COMMENT '工具名称',
                                   `tool_result` longtext COMMENT '工具结果',
                                   `provider_code` varchar(64) DEFAULT '' COMMENT '厂商编码',
                                   `model_code` varchar(128) DEFAULT '' COMMENT '模型编码',
                                   `ability_type` varchar(64) DEFAULT '' COMMENT '能力类型',
                                   `route_mode` varchar(32) DEFAULT '' COMMENT '路由模式',
                                   `token_usage` int(11) DEFAULT 0 COMMENT 'Token 用量',
                                   `duration_ms` bigint(20) DEFAULT 0 COMMENT '耗时毫秒',
                                   `risk_confirmed` char(1) NOT NULL DEFAULT '0' COMMENT '是否已确认风险动作',
                                   `message_status` varchar(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '消息状态',
                                   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                   PRIMARY KEY (`message_id`),
                                   KEY `idx_ai_chat_message_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 会话消息表';

-- ----------------------------
-- 5. AI 知识库表
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_base`;
CREATE TABLE `ai_knowledge_base` (
                                     `knowledge_base_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
                                     `kb_code` varchar(64) NOT NULL COMMENT '知识库编码',
                                     `kb_name` varchar(128) NOT NULL COMMENT '知识库名称',
                                     `kb_type` varchar(32) NOT NULL DEFAULT 'PLATFORM_DOC' COMMENT '知识库类型',
                                     `vector_store_type` varchar(64) DEFAULT 'REDIS' COMMENT '向量存储类型',
                                     `publish_status` varchar(32) DEFAULT 'DRAFT' COMMENT '发布状态',
                                     `active_version_id` bigint(20) DEFAULT NULL COMMENT '当前激活版本ID',
                                     `extra_config` text COMMENT '扩展配置',
                                     `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                     `tenant_name` varchar(30) DEFAULT '' COMMENT '租户名称',
                                     `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                     `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                     `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                     PRIMARY KEY (`knowledge_base_id`),
                                     UNIQUE KEY `uk_ai_kb_code` (`kb_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 知识库表';

-- ----------------------------
-- 6. AI 知识文档表
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_document`;
CREATE TABLE `ai_knowledge_document` (
                                         `document_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文档ID',
                                         `knowledge_base_id` bigint(20) NOT NULL COMMENT '知识库ID',
                                         `file_name` varchar(255) NOT NULL COMMENT '文件名',
                                         `file_path` varchar(255) NOT NULL COMMENT '文件路径',
                                         `file_size` bigint(20) DEFAULT 0 COMMENT '文件大小',
                                         `checksum` varchar(128) DEFAULT '' COMMENT '文件校验码',
                                         `parse_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '解析状态',
                                         `chunk_count` int(11) DEFAULT 0 COMMENT '分片数量',
                                         `parsed_snapshot_path` varchar(255) DEFAULT '' COMMENT '解析快照路径',
                                         `parsed_summary` varchar(1000) DEFAULT '' COMMENT '解析摘要',
                                         `sort_num` int(11) DEFAULT 100 COMMENT '排序号',
                                         `source_origin` varchar(32) DEFAULT 'CUSTOM' COMMENT '来源类型',
                                         `app_version` varchar(64) DEFAULT '' COMMENT '平台版本',
                                         `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                         `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                         `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                         PRIMARY KEY (`document_id`),
                                         KEY `idx_ai_document_kb_id` (`knowledge_base_id`),
                                         KEY `idx_ai_document_kb_sort` (`knowledge_base_id`, `sort_num`),
                                         KEY `idx_ai_document_parse` (`knowledge_base_id`, `parse_status`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 知识文档表';

-- ----------------------------
-- 7. AI 知识库版本表
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_version`;
CREATE TABLE `ai_knowledge_version` (
                                        `version_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
                                        `knowledge_base_id` bigint(20) NOT NULL COMMENT '知识库ID',
                                        `version_no` varchar(64) NOT NULL COMMENT '版本号',
                                        `source_document_ids` varchar(1000) DEFAULT '' COMMENT '来源文档ID集合',
                                        `snapshot_path` varchar(255) DEFAULT '' COMMENT '结构化快照路径',
                                        `vector_store_type` varchar(64) DEFAULT 'REDIS' COMMENT '向量库类型',
                                        `publish_status` varchar(32) DEFAULT 'DRAFT' COMMENT '发布状态',
                                        `is_active` char(1) NOT NULL DEFAULT '0' COMMENT '是否激活(0否 1是)',
                                        `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
                                        `published_by` varchar(64) DEFAULT '' COMMENT '发布人',
                                        `rollback_from_version` varchar(64) DEFAULT '' COMMENT '回滚来源版本',
                                        `source_file_count` int(11) DEFAULT 0 COMMENT '参与文件数',
                                        `merged_item_count` int(11) DEFAULT 0 COMMENT '合并条目数',
                                        `override_count` int(11) DEFAULT 0 COMMENT '覆盖次数',
                                        `conflict_count` int(11) DEFAULT 0 COMMENT '冲突次数',
                                        `build_summary` varchar(1000) DEFAULT '' COMMENT '构建摘要',
                                        `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                        `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                        PRIMARY KEY (`version_id`),
                                        UNIQUE KEY `uk_ai_kb_version` (`knowledge_base_id`, `version_no`),
                                        KEY `idx_ai_version_kb_active` (`knowledge_base_id`, `is_active`),
                                        KEY `idx_ai_version_kb_status` (`knowledge_base_id`, `publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 知识库版本表';

-- ----------------------------
-- 8. AI 调用审计表
-- ----------------------------
DROP TABLE IF EXISTS `ai_invoke_log`;
CREATE TABLE `ai_invoke_log` (
                                 `invoke_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '调用日志ID',
                                 `request_id` varchar(64) NOT NULL COMMENT '请求ID',
                                 `session_id` bigint(20) DEFAULT NULL COMMENT '会话ID',
                                 `ability_type` varchar(64) DEFAULT '' COMMENT '能力类型',
                                 `provider_code` varchar(64) DEFAULT '' COMMENT '厂商编码',
                                 `model_code` varchar(128) DEFAULT '' COMMENT '模型编码',
                                 `request_content` longtext COMMENT '请求内容',
                                 `response_summary` varchar(2000) DEFAULT '' COMMENT '响应摘要',
                                 `token_usage` int(11) DEFAULT 0 COMMENT 'Token 用量',
                                 `duration_ms` bigint(20) DEFAULT 0 COMMENT '耗时毫秒',
                                 `invoke_status` varchar(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '调用状态',
                                 `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                 `dept_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
                                 `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`invoke_log_id`),
                                 KEY `idx_ai_invoke_log_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 调用审计表';

-- ----------------------------
-- 8. Tool 调用审计表
-- ----------------------------
DROP TABLE IF EXISTS `ai_tool_call_log`;
CREATE TABLE `ai_tool_call_log` (
                                    `tool_call_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工具调用日志ID',
                                    `session_id` bigint(20) DEFAULT NULL COMMENT '会话ID',
                                    `message_id` bigint(20) DEFAULT NULL COMMENT '消息ID',
                                    `tool_name` varchar(128) NOT NULL COMMENT '工具名称',
                                    `tool_args` longtext COMMENT '工具参数',
                                    `tool_result` longtext COMMENT '工具结果',
                                    `invoke_status` varchar(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '调用状态',
                                    `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                    `dept_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
                                    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`tool_call_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tool 调用审计表';

-- ----------------------------
-- 9. AI 文件资产表
-- ----------------------------
DROP TABLE IF EXISTS `ai_file_asset`;
CREATE TABLE `ai_file_asset` (
                                 `file_asset_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件资产ID',
                                 `biz_type` varchar(64) NOT NULL COMMENT '业务类型',
                                 `biz_id` bigint(20) DEFAULT NULL COMMENT '业务ID',
                                 `file_name` varchar(255) NOT NULL COMMENT '文件名',
                                 `file_path` varchar(255) NOT NULL COMMENT '文件路径',
                                 `file_size` bigint(20) DEFAULT 0 COMMENT '文件大小',
                                 `content_type` varchar(128) DEFAULT '' COMMENT '文件类型',
                                 `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                 `dept_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
                                 `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                 PRIMARY KEY (`file_asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 文件资产表';

-- ----------------------------
-- 10. AI 协议适配生产线表
-- ----------------------------
DROP TABLE IF EXISTS `ai_protocol_adaptation_task`;
CREATE TABLE `ai_protocol_adaptation_task` (
                                               `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
                                               `task_name` varchar(128) NOT NULL COMMENT '任务名称',
                                               `protocol_code` varchar(64) DEFAULT '' COMMENT '协议编码',
                                               `protocol_name` varchar(128) DEFAULT '' COMMENT '协议名称',
                                               `dsl_snapshot_path` varchar(255) DEFAULT '' COMMENT '当前DSL快照路径',
                                               `workbook_path` varchar(255) DEFAULT '' COMMENT '当前工作簿路径',
                                               `task_status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT '任务状态',
                                               `parse_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '解析状态',
                                               `validation_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '校验状态',
                                               `generation_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '生成状态',
                                               `risk_level` varchar(32) NOT NULL DEFAULT 'LOW' COMMENT '风险等级',
                                               `error_summary` varchar(1000) DEFAULT '' COMMENT '错误摘要',
                                               `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
                                               `tenant_name` varchar(30) DEFAULT '' COMMENT '租户名称',
                                               `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                               `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                               `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                               PRIMARY KEY (`task_id`),
                                               KEY `idx_ai_protocol_task_status` (`task_status`, `validation_status`, `generation_status`),
                                               KEY `idx_ai_protocol_task_protocol` (`protocol_code`, `protocol_name`),
                                               KEY `idx_ai_protocol_task_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 协议适配任务表';

DROP TABLE IF EXISTS `ai_protocol_adaptation_artifact`;
CREATE TABLE `ai_protocol_adaptation_artifact` (
                                                   `artifact_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产物ID',
                                                   `task_id` bigint(20) NOT NULL COMMENT '任务ID',
                                                   `artifact_type` varchar(32) NOT NULL COMMENT '产物类型',
                                                   `artifact_name` varchar(255) NOT NULL COMMENT '产物名称',
                                                   `file_path` varchar(255) DEFAULT '' COMMENT '文件路径',
                                                   `file_size` bigint(20) DEFAULT 0 COMMENT '文件大小',
                                                   `checksum` varchar(128) DEFAULT '' COMMENT '文件校验码',
                                                   `source_type` varchar(32) DEFAULT '' COMMENT '来源类型',
                                                   `artifact_status` varchar(32) NOT NULL DEFAULT 'READY' COMMENT '产物状态',
                                                   `summary` varchar(1000) DEFAULT '' COMMENT '摘要',
                                                   `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                                   `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                                   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                   `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                   `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                                   PRIMARY KEY (`artifact_id`),
                                                   KEY `idx_ai_protocol_artifact_task` (`task_id`, `artifact_type`),
                                                   KEY `idx_ai_protocol_artifact_status` (`artifact_status`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 协议适配产物表';

DROP TABLE IF EXISTS `ai_protocol_generation_record`;
CREATE TABLE `ai_protocol_generation_record` (
                                                 `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '生成记录ID',
                                                 `task_id` bigint(20) NOT NULL COMMENT '任务ID',
                                                 `dsl_snapshot_path` varchar(255) DEFAULT '' COMMENT '输入DSL快照路径',
                                                 `generation_strategy` varchar(1000) DEFAULT '' COMMENT '生成策略',
                                                 `code_package_path` varchar(255) DEFAULT '' COMMENT '代码包路径',
                                                 `file_manifest_path` varchar(255) DEFAULT '' COMMENT '文件清单路径',
                                                 `test_report_path` varchar(255) DEFAULT '' COMMENT '测试报告路径',
                                                 `compile_status` varchar(32) DEFAULT 'PENDING' COMMENT '编译状态',
                                                 `validation_error_count` int(11) DEFAULT 0 COMMENT '校验错误数',
                                                 `validation_warning_count` int(11) DEFAULT 0 COMMENT '校验告警数',
                                                 `confirm_by` varchar(64) DEFAULT '' COMMENT '确认人',
                                                 `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
                                                 `generation_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '生成状态',
                                                 `failure_reason` varchar(1000) DEFAULT '' COMMENT '失败原因',
                                                 `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
                                                 `remark` varchar(500) DEFAULT '' COMMENT '备注',
                                                 `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
                                                 PRIMARY KEY (`record_id`),
                                                 KEY `idx_ai_protocol_generation_task` (`task_id`, `generation_status`),
                                                 KEY `idx_ai_protocol_generation_compile` (`compile_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 协议代码生成记录表';

-- ----------------------------
-- 11. 菜单与权限初始化
-- ----------------------------
DELETE FROM `sys_menu_translate`
WHERE `id` IN (
    SELECT `menu_id`
    FROM (
             SELECT `menu_id`
             FROM `sys_menu`
             WHERE `path` IN ('ai', 'chat', 'chatRecord', 'provider', 'aiModel', 'knowledge')
                OR `component` IN ('ai/chat/index', 'ai/chatRecord/index', 'ai/provider/index', 'ai/model/index', 'ai/knowledge/index')
                OR `perms` LIKE 'ai:%'
         ) t
);

DELETE FROM `sys_menu`
WHERE `path` IN ('ai', 'chat', 'chatRecord', 'provider', 'aiModel', 'knowledge')
   OR `component` IN ('ai/chat/index', 'ai/chatRecord/index', 'ai/provider/index', 'ai/model/index', 'ai/knowledge/index')
   OR `perms` LIKE 'ai:%';

SELECT @menu_id_base := IFNULL(MAX(menu_id), 0) + 1 FROM sys_menu;
SET @ai_menu_parent := @menu_id_base;
SET @ai_chat_menu := @menu_id_base + 1;
SET @ai_chat_record_menu := @menu_id_base + 2;
SET @ai_provider_menu := @menu_id_base + 3;
SET @ai_model_menu := @menu_id_base + 4;
SET @ai_knowledge_menu := @menu_id_base + 5;
SET @ai_protocol_adaptation_menu := @menu_id_base + 6;

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`)
VALUES
    (@ai_menu_parent, 'AI赋能', 0, 3, 'ai', NULL, '', 1, 0, 'M', '0', 0, '', 'guide', 'admin', NOW(), '', NULL, 'AI赋能目录', 0),
    (@ai_chat_menu, 'AI会话', @ai_menu_parent, 1, 'chat', 'ai/chat/index', '', 1, 0, 'C', '0', 0, 'ai:chat:list', 'message', 'admin', NOW(), '', NULL, 'AI会话菜单', 0),
    (@ai_chat_record_menu, '会话记录', @ai_menu_parent, 2, 'chatRecord', 'ai/chatRecord/index', '', 1, 0, 'C', '0', 0, 'ai:chatRecord:list', 'form', 'admin', NOW(), '', NULL, 'AI会话记录菜单', 0),
    (@ai_provider_menu, '厂商管理', @ai_menu_parent, 3, 'provider', 'ai/provider/index', '', 1, 0, 'C', '0', 0, 'ai:provider:list', 'tree', 'admin', NOW(), '', NULL, 'AI厂商管理菜单', 0),
    (@ai_model_menu, '模型管理', @ai_menu_parent, 4, 'aiModel', 'ai/model/index', '', 1, 0, 'C', '0', 0, 'ai:model:list', 'edit', 'admin', NOW(), '', NULL, 'AI模型管理菜单', 0),
    (@ai_knowledge_menu, '知识库', @ai_menu_parent, 5, 'knowledge', 'ai/knowledge/index', '', 1, 0, 'C', '0', 0, 'ai:knowledge:list', 'dict', 'admin', NOW(), '', NULL, 'AI知识库菜单', 0),
    (@ai_protocol_adaptation_menu, '协议适配', @ai_menu_parent, 6, 'protocolAdaptation', 'ai/protocolAdaptation/index', '', 1, 0, 'C', '0', 0, 'ai:protocol:adaptation:list', 'zip', 'admin', NOW(), '', NULL, 'AI协议适配任务菜单', 0);

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES
                                                              (@ai_menu_parent, 'AI赋能', 'AI Enablement'),
                                                              (@ai_chat_menu, 'AI会话', 'AI Chat'),
                                                              (@ai_chat_record_menu, '会话记录', 'Chat Records'),
                                                              (@ai_provider_menu, '厂商管理', 'Provider Management'),
                                                              (@ai_model_menu, '模型管理', 'Model Management'),
                                                              (@ai_knowledge_menu, '知识库', 'Knowledge Base'),
                                                              (@ai_protocol_adaptation_menu, '协议适配', 'Protocol Adaptation');

SET @ai_button_id_base := @menu_id_base + 100;

INSERT INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`)
VALUES
    (@ai_button_id_base + 1, 'AI会话查询', @ai_chat_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chat:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 2, 'AI会话新增', @ai_chat_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chat:add', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 3, 'AI会话修改', @ai_chat_menu, 3, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chat:edit', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 4, 'AI会话删除', @ai_chat_menu, 4, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chat:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 5, '会话记录查询', @ai_chat_record_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chatRecord:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 6, '会话记录删除', @ai_chat_record_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:chatRecord:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 7, '厂商管理查询', @ai_provider_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:provider:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 8, '厂商管理新增', @ai_provider_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:provider:add', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 9, '厂商管理修改', @ai_provider_menu, 3, '#', '', '', 1, 0, 'F', '0', 0, 'ai:provider:edit', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 10, '厂商管理删除', @ai_provider_menu, 4, '#', '', '', 1, 0, 'F', '0', 0, 'ai:provider:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 11, '模型管理查询', @ai_model_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:model:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 12, '模型管理新增', @ai_model_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:model:add', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 13, '模型管理修改', @ai_model_menu, 3, '#', '', '', 1, 0, 'F', '0', 0, 'ai:model:edit', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 14, '模型管理删除', @ai_model_menu, 4, '#', '', '', 1, 0, 'F', '0', 0, 'ai:model:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 15, '知识库查询', @ai_knowledge_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:knowledge:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 16, '知识库新增', @ai_knowledge_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:knowledge:add', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 17, '知识库修改', @ai_knowledge_menu, 3, '#', '', '', 1, 0, 'F', '0', 0, 'ai:knowledge:edit', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 18, '知识库删除', @ai_knowledge_menu, 4, '#', '', '', 1, 0, 'F', '0', 0, 'ai:knowledge:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 19, '协议适配查询', @ai_protocol_adaptation_menu, 1, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:query', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 20, '协议适配新增', @ai_protocol_adaptation_menu, 2, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:add', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 21, '协议适配修改', @ai_protocol_adaptation_menu, 3, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:edit', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 22, '协议适配删除', @ai_protocol_adaptation_menu, 4, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:remove', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 23, '协议代码生成', @ai_protocol_adaptation_menu, 5, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:generate', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 24, '协议质量校验', @ai_protocol_adaptation_menu, 6, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:validate', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 25, '协议工作簿导出', @ai_protocol_adaptation_menu, 7, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:workbook:export', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 26, '协议工作簿导入', @ai_protocol_adaptation_menu, 8, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:workbook:import', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 27, '协议代码验证', @ai_protocol_adaptation_menu, 9, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:verify', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 28, '协议代码确认', @ai_protocol_adaptation_menu, 10, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:confirm', '#', 'admin', NOW(), '', NULL, '', 0),
    (@ai_button_id_base + 29, '协议自动生成', @ai_protocol_adaptation_menu, 11, '#', '', '', 1, 0, 'F', '0', 0, 'ai:protocol:adaptation:auto-run', '#', 'admin', NOW(), '', NULL, '', 0);

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES
                                                              (@ai_button_id_base + 1, 'AI会话查询', 'AI Chat Query'),
                                                              (@ai_button_id_base + 2, 'AI会话新增', 'AI Chat Add'),
                                                              (@ai_button_id_base + 3, 'AI会话修改', 'AI Chat Edit'),
                                                              (@ai_button_id_base + 4, 'AI会话删除', 'AI Chat Remove'),
                                                              (@ai_button_id_base + 5, '会话记录查询', 'Chat Record Query'),
                                                              (@ai_button_id_base + 6, '会话记录删除', 'Chat Record Remove'),
                                                              (@ai_button_id_base + 7, '厂商管理查询', 'Provider Query'),
                                                              (@ai_button_id_base + 8, '厂商管理新增', 'Provider Add'),
                                                              (@ai_button_id_base + 9, '厂商管理修改', 'Provider Edit'),
                                                              (@ai_button_id_base + 10, '厂商管理删除', 'Provider Remove'),
                                                              (@ai_button_id_base + 11, '模型管理查询', 'Model Query'),
                                                              (@ai_button_id_base + 12, '模型管理新增', 'Model Add'),
                                                              (@ai_button_id_base + 13, '模型管理修改', 'Model Edit'),
                                                              (@ai_button_id_base + 14, '模型管理删除', 'Model Remove'),
                                                              (@ai_button_id_base + 15, '知识库查询', 'Knowledge Query'),
                                                              (@ai_button_id_base + 16, '知识库新增', 'Knowledge Add'),
                                                              (@ai_button_id_base + 17, '知识库修改', 'Knowledge Edit'),
                                                              (@ai_button_id_base + 18, '知识库删除', 'Knowledge Remove'),
                                                              (@ai_button_id_base + 19, '协议适配查询', 'Protocol Adaptation Query'),
                                                              (@ai_button_id_base + 20, '协议适配新增', 'Protocol Adaptation Add'),
                                                              (@ai_button_id_base + 21, '协议适配修改', 'Protocol Adaptation Edit'),
                                                              (@ai_button_id_base + 22, '协议适配删除', 'Protocol Adaptation Remove'),
                                                              (@ai_button_id_base + 23, '协议代码生成', 'Protocol Code Generate'),
                                                              (@ai_button_id_base + 24, '协议质量校验', 'Protocol Quality Gate'),
                                                              (@ai_button_id_base + 25, '协议工作簿导出', 'Protocol Workbook Export'),
                                                              (@ai_button_id_base + 26, '协议工作簿导入', 'Protocol Workbook Import'),
                                                              (@ai_button_id_base + 27, '协议代码验证', 'Protocol Code Verify'),
                                                              (@ai_button_id_base + 28, '协议代码确认', 'Protocol Code Confirm'),
                                                              (@ai_button_id_base + 29, '协议自动生成', 'Protocol Auto Generate');

-- ----------------------------
-- 12. AI 厂商管理字典
-- ----------------------------
SET @ai_dict_type_base := (SELECT IFNULL(MAX(dict_id), 0) FROM `sys_dict_type`);
SET @ai_dict_data_base := (SELECT IFNULL(MAX(dict_code), 0) FROM `sys_dict_data`);

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 1, 'AI厂商区域档位', 'ai_provider_region_profile', 0, 'admin', NOW(), 'admin', NOW(), 'AI厂商区域档位'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_provider_region_profile');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 2, 'AI厂商鉴权方式', 'ai_provider_auth_type', 0, 'admin', NOW(), 'admin', NOW(), 'AI厂商鉴权方式'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_provider_auth_type');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 3, 'AI厂商状态', 'ai_provider_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI厂商状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_provider_status');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 4, 'AI厂商编码', 'ai_provider_code', 0, 'admin', NOW(), 'admin', NOW(), 'AI厂商编码'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_provider_code');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 5, 'AI模型类型', 'ai_model_type', 0, 'admin', NOW(), 'admin', NOW(), 'AI模型类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_model_type');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 6, 'AI模型推理参数Key', 'ai_model_param_key', 0, 'admin', NOW(), 'admin', NOW(), 'AI模型推理参数Key'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_model_param_key');

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI厂商区域档位', 'AI Provider Region Profile'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_provider_region_profile'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI厂商鉴权方式', 'AI Provider Auth Type'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_provider_auth_type'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI厂商状态', 'AI Provider Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_provider_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI厂商编码', 'AI Provider Code'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_provider_code'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI模型类型', 'AI Model Type'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_model_type'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI模型推理参数Key', 'AI Model Param Key'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_model_param_key'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, '国内', 'CN', 'ai_provider_region_profile', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'CN');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, '海外', 'GLOBAL', 'ai_provider_region_profile', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'GLOBAL');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 3, '本地', 'LOCAL', 'ai_provider_region_profile', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'LOCAL');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, 'API Key', 'API_KEY', 'ai_provider_auth_type', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_auth_type' AND `dict_value` = 'API_KEY');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, '免鉴权', 'NONE', 'ai_provider_auth_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_auth_type' AND `dict_value` = 'NONE');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, '正常', '0', 'ai_provider_status', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_status' AND `dict_value` = '0');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, '停用', '1', 'ai_provider_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_status' AND `dict_value` = '1');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, '对话模型', 'CHAT', 'ai_model_type', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'CHAT');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, '推理模型', 'REASONING', 'ai_model_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'REASONING');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 3, '文本模型', 'TEXT', 'ai_model_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'TEXT');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 4, '多模态模型', 'MULTIMODAL', 'ai_model_type', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'MULTIMODAL');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 5, '视觉模型', 'VISION', 'ai_model_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'VISION');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 6, '语音模型', 'AUDIO', 'ai_model_type', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'AUDIO');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 7, '向量模型', 'EMBEDDING', 'ai_model_type', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'EMBEDDING');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 8, '重排模型', 'RERANK', 'ai_model_type', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'RERANK');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 9, '自定义模型', 'CUSTOM', 'ai_model_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'CUSTOM');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, '温度系数', 'temperature', 'ai_model_param_key', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), '控制输出随机性，通常取值 0 到 2'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'temperature');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, 'Top P', 'topP', 'ai_model_param_key', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), '控制候选概率截断，通常与 temperature 二选一主调'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'topP');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 3, '最大输出Token', 'maxTokens', 'ai_model_param_key', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), '限制单次响应输出长度'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'maxTokens');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 4, '存在惩罚', 'presencePenalty', 'ai_model_param_key', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), '提高话题发散度，减少重复旧话题'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'presencePenalty');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 5, '频率惩罚', 'frequencyPenalty', 'ai_model_param_key', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), '降低重复词或重复表达'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'frequencyPenalty');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 6, '停止词', 'stop', 'ai_model_param_key', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), '可填写单个词，或在值中输入 JSON 数组'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'stop');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 7, '随机种子', 'seed', 'ai_model_param_key', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), '用于提高可复现性'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'seed');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 8, '用户标识', 'user', 'ai_model_param_key', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), '用于透传调用侧用户标识'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'user');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 1, '阿里百炼', 'qwen', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"阿里百炼","regionProfile":"CN","authType":"API_KEY","apiBaseUrl":"https://dashscope.aliyuncs.com/compatible-mode/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'qwen');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 2, 'DeepSeek', 'deepseek', 'ai_provider_code', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"DeepSeek","regionProfile":"CN","authType":"API_KEY","apiBaseUrl":"https://api.deepseek.com/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'deepseek');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 3, '智谱AI', 'zhipu', 'ai_provider_code', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"智谱AI","regionProfile":"CN","authType":"API_KEY","apiBaseUrl":"https://open.bigmodel.cn/api/paas/v4/"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'zhipu');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 4, '月之暗面', 'moonshot', 'ai_provider_code', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"月之暗面","regionProfile":"CN","authType":"API_KEY","apiBaseUrl":"https://api.moonshot.cn/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'moonshot');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 5, 'OpenAI', 'openai', 'ai_provider_code', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"OpenAI","regionProfile":"GLOBAL","authType":"API_KEY","apiBaseUrl":"https://api.openai.com/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'openai');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 6, 'Azure OpenAI', 'azure_openai', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"Azure OpenAI","regionProfile":"GLOBAL","authType":"API_KEY","apiBaseUrl":""}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'azure_openai');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 7, 'OpenRouter', 'openrouter', 'ai_provider_code', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"OpenRouter","regionProfile":"GLOBAL","authType":"API_KEY","apiBaseUrl":"https://openrouter.ai/api/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'openrouter');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 8, 'Ollama / 本地模型', 'ollama', 'ai_provider_code', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"Ollama / 本地模型","regionProfile":"LOCAL","authType":"NONE","apiBaseUrl":"http://127.0.0.1:11434/v1"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'ollama');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 9, 'Google / Gemini', 'google', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"Google / Gemini","regionProfile":"GLOBAL","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'google');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 10, 'Anthropic / Claude', 'anthropic', 'ai_provider_code', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"Anthropic / Claude","regionProfile":"GLOBAL","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'anthropic');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 11, 'Meta / Llama', 'meta', 'ai_provider_code', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"Meta / Llama","regionProfile":"GLOBAL","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'meta');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 12, 'NVIDIA / NeMo', 'nvidia', 'ai_provider_code', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"NVIDIA / NeMo","regionProfile":"GLOBAL","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'nvidia');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 13, '百度 / 文心', 'baidu', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"百度 / 文心","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'baidu');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 14, '华为 / 盘古', 'huawei', 'ai_provider_code', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"华为 / 盘古","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'huawei');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 15, '腾讯 / 混元', 'tencent', 'ai_provider_code', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"腾讯 / 混元","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'tencent');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 16, '小米 / MiMo', 'xiaomi', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"小米 / MiMo","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'xiaomi');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 17, '科大讯飞 / 星火', 'xfyun', 'ai_provider_code', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"科大讯飞 / 星火","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'xfyun');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 18, '商汤 / SenseNova', 'sensetime', 'ai_provider_code', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"商汤 / SenseNova","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'sensetime');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 19, '字节跳动 / 豆包', 'bytedance', 'ai_provider_code', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"字节跳动 / 豆包","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'bytedance');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 20, 'MiniMax / 海螺', 'minimax', 'ai_provider_code', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"MiniMax / 海螺","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'minimax');

INSERT INTO `sys_dict_data`
(`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_data_base := @ai_dict_data_base + 1, 21, '澜舟科技 / 孟子', 'lanzhou', 'ai_provider_code', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(),
       '{"providerName":"澜舟科技 / 孟子","regionProfile":"CN","authType":"API_KEY"}'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'lanzhou');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '国内', 'Domestic'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'CN'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '海外', 'Global'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'GLOBAL'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '本地', 'Local'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_region_profile' AND `dict_value` = 'LOCAL'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'API Key', 'API Key'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_auth_type' AND `dict_value` = 'API_KEY'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '免鉴权', 'No Auth'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_auth_type' AND `dict_value` = 'NONE'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '正常', 'Enabled'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_status' AND `dict_value` = '0'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '停用', 'Disabled'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_status' AND `dict_value` = '1'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '对话模型', 'Chat Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'CHAT'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '推理模型', 'Reasoning Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'REASONING'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '文本模型', 'Text Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'TEXT'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '多模态模型', 'Multimodal Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'MULTIMODAL'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '视觉模型', 'Vision Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'VISION'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '语音模型', 'Audio Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'AUDIO'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '向量模型', 'Embedding Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'EMBEDDING'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '重排模型', 'Rerank Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'RERANK'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '自定义模型', 'Custom Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_type' AND `dict_value` = 'CUSTOM'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '温度系数', 'Temperature'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'temperature'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Top P', 'Top P'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'topP'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '最大输出Token', 'Max Tokens'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'maxTokens'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '存在惩罚', 'Presence Penalty'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'presencePenalty'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '频率惩罚', 'Frequency Penalty'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'frequencyPenalty'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '停止词', 'Stop'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'stop'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '随机种子', 'Seed'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'seed'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '用户标识', 'User'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_model_param_key' AND `dict_value` = 'user'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '阿里百炼', 'Qwen'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'qwen'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'DeepSeek', 'DeepSeek'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'deepseek'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '智谱AI', 'Zhipu AI'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'zhipu'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '月之暗面', 'Moonshot AI'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'moonshot'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'OpenAI', 'OpenAI'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'openai'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Azure OpenAI', 'Azure OpenAI'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'azure_openai'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'OpenRouter', 'OpenRouter'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'openrouter'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Ollama / 本地模型', 'Ollama / Local Model'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'ollama'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Google / Gemini', 'Google / Gemini'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'google'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Anthropic / Claude', 'Anthropic / Claude'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'anthropic'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'Meta / Llama', 'Meta / Llama'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'meta'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'NVIDIA / NeMo', 'NVIDIA / NeMo'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'nvidia'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '百度 / 文心', 'Baidu / ERNIE'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'baidu'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '华为 / 盘古', 'Huawei / Pangu'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'huawei'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '腾讯 / 混元', 'Tencent / Hunyuan'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'tencent'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '小米 / MiMo', 'Xiaomi / MiMo'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'xiaomi'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '科大讯飞 / 星火', 'iFlytek / Spark'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'xfyun'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '商汤 / SenseNova', 'SenseTime / SenseNova'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'sensetime'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '字节跳动 / 豆包', 'ByteDance / Doubao'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'bytedance'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, 'MiniMax / 海螺', 'MiniMax / Hailuo'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'minimax'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, '澜舟科技 / 孟子', 'LanZhou / Mengzi'
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_provider_code' AND `dict_value` = 'lanzhou'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

-- ----------------------------
-- 13. AI 会话技能字典
-- ----------------------------
INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 11, 'AI会话技能', 'ai_chat_skill', 0, 'admin', NOW(), 'admin', NOW(), 'AI会话技能'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_chat_skill');

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI会话技能', 'AI Chat Skill'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_chat_skill'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '自动识别', 'AUTO', 'ai_chat_skill', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), '系统自动识别平台助手、通用对话、智能问数、设备控制、协议解析、物模型生成或需求评估'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'AUTO');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '平台助手', 'PLATFORM_ASSISTANT', 'ai_chat_skill', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), '基于平台知识库回答菜单路径、配置步骤、操作说明和平台使用问题'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'PLATFORM_ASSISTANT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '通用对话', 'GENERAL', 'ai_chat_skill', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), '直接与当前模型进行普通对话，不主动注入平台知识库'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'GENERAL');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '智能问数', 'NL2SQL', 'ai_chat_skill', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), '自然语言转结构化问数计划并执行受控查询'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'NL2SQL');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 5, '设备控制', 'DEVICE_CONTROL', 'ai_chat_skill', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), '通过受控链路执行设备服务、属性控制、指令生成或场景执行'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'DEVICE_CONTROL');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 6, '协议解析', 'PROTOCOL_PARSE', 'ai_chat_skill', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), '基于协议知识和模型能力解析协议文档、报文和寄存器'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'PROTOCOL_PARSE');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 7, '物模型生成', 'THING_MODEL_GENERATE', 'ai_chat_skill', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), '解析客户上传文件，提取设备属性、功能或事件并生成物模型导入 Excel'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'THING_MODEL_GENERATE');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 8, '需求评估', 'REQUIREMENT_EVALUATION', 'ai_chat_skill', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), '解析客户上传需求文件，结合平台知识库输出初步能力匹配、差距建议、风险和待确认问题'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_chat_skill' AND `dict_value` = 'REQUIREMENT_EVALUATION');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`,
       CASE `dict_value`
           WHEN 'AUTO' THEN '自动识别'
           WHEN 'PLATFORM_ASSISTANT' THEN '平台助手'
           WHEN 'GENERAL' THEN '通用对话'
           WHEN 'NL2SQL' THEN '智能问数'
           WHEN 'DEVICE_CONTROL' THEN '设备控制'
           WHEN 'PROTOCOL_PARSE' THEN '协议解析'
           WHEN 'THING_MODEL_GENERATE' THEN '物模型生成'
           WHEN 'REQUIREMENT_EVALUATION' THEN '需求评估'
           ELSE `dict_label`
           END,
       CASE `dict_value`
           WHEN 'AUTO' THEN 'Auto Routing'
           WHEN 'PLATFORM_ASSISTANT' THEN 'Platform Assistant'
           WHEN 'GENERAL' THEN 'General Chat'
           WHEN 'NL2SQL' THEN 'NL2SQL'
           WHEN 'DEVICE_CONTROL' THEN 'Device Control'
           WHEN 'PROTOCOL_PARSE' THEN 'Protocol Parse'
           WHEN 'THING_MODEL_GENERATE' THEN 'Thing Model Generate'
           WHEN 'REQUIREMENT_EVALUATION' THEN 'Requirement Evaluation'
           ELSE `dict_label`
           END
FROM `sys_dict_data`
WHERE `dict_type` = 'ai_chat_skill'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

-- ----------------------------
-- 14. AI 知识库管理字典
-- ----------------------------
INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 7, 'AI知识库类型', 'ai_knowledge_type', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识库类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_type');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 8, 'AI知识库向量库类型', 'ai_knowledge_vector_store_type', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识库向量库类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_vector_store_type');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 9, 'AI知识库发布状态', 'ai_knowledge_publish_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识库发布状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_publish_status');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 10, 'AI知识库状态', 'ai_knowledge_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识库状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_status');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 12, 'AI知识文档解析状态', 'ai_knowledge_parse_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识文档解析状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_parse_status');

INSERT INTO `sys_dict_type`
(`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT @ai_dict_type_base + 13, 'AI知识文档来源类型', 'ai_knowledge_source_origin', 0, 'admin', NOW(), 'admin', NOW(), 'AI知识文档来源类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_knowledge_source_origin');

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识库类型', 'AI Knowledge Type'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_type'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识库向量库类型', 'AI Knowledge Vector Store Type'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_vector_store_type'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识库发布状态', 'AI Knowledge Publish Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_publish_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识库状态', 'AI Knowledge Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识文档解析状态', 'AI Knowledge Parse Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_parse_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI知识文档来源类型', 'AI Knowledge Source Origin'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_knowledge_source_origin'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '问数语义库', 'NL2SQL_SEMANTIC', 'ai_knowledge_type', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_type' AND `dict_value` = 'NL2SQL_SEMANTIC');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '协议知识库', 'PROTOCOL_SPEC', 'ai_knowledge_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_type' AND `dict_value` = 'PROTOCOL_SPEC');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '平台知识库', 'PLATFORM_DOC', 'ai_knowledge_type', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_type' AND `dict_value` = 'PLATFORM_DOC');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '源码导航库', 'CODEBASE_GUIDE', 'ai_knowledge_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), '源码导航安全摘要知识库'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_type' AND `dict_value` = 'CODEBASE_GUIDE');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, 'Redis', 'REDIS', 'ai_knowledge_vector_store_type', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'REDIS');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, 'Redis Stack', 'REDIS_STACK', 'ai_knowledge_vector_store_type', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'REDIS_STACK');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, 'Milvus', 'MILVUS', 'ai_knowledge_vector_store_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'MILVUS');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, 'PGVector', 'PGVECTOR', 'ai_knowledge_vector_store_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'PGVECTOR');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 5, 'Elasticsearch', 'ELASTICSEARCH', 'ai_knowledge_vector_store_type', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'ELASTICSEARCH');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 6, 'Qdrant', 'QDRANT', 'ai_knowledge_vector_store_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'QDRANT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 7, 'Memory', 'MEMORY', 'ai_knowledge_vector_store_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), '仅建议开发调试使用'
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_vector_store_type' AND `dict_value` = 'MEMORY');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '草稿', 'DRAFT', 'ai_knowledge_publish_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_publish_status' AND `dict_value` = 'DRAFT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '已发布', 'PUBLISHED', 'ai_knowledge_publish_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_publish_status' AND `dict_value` = 'PUBLISHED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '已归档', 'ARCHIVED', 'ai_knowledge_publish_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_publish_status' AND `dict_value` = 'ARCHIVED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '启用', '0', 'ai_knowledge_status', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_status' AND `dict_value` = '0');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '停用', '1', 'ai_knowledge_status', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_status' AND `dict_value` = '1');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '待解析', 'PENDING', 'ai_knowledge_parse_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_parse_status' AND `dict_value` = 'PENDING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '解析成功', 'SUCCESS', 'ai_knowledge_parse_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_parse_status' AND `dict_value` = 'SUCCESS');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '解析失败', 'FAILED', 'ai_knowledge_parse_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_parse_status' AND `dict_value` = 'FAILED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '官方文件', 'OFFICIAL', 'ai_knowledge_source_origin', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_source_origin' AND `dict_value` = 'OFFICIAL');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '自定义文件', 'CUSTOM', 'ai_knowledge_source_origin', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_knowledge_source_origin' AND `dict_value` = 'CUSTOM');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`, `dict_label`,
       CASE `dict_value`
           WHEN 'NL2SQL_SEMANTIC' THEN 'NL2SQL Semantic Knowledge'
           WHEN 'PROTOCOL_SPEC' THEN 'Protocol Specification Knowledge'
           WHEN 'PLATFORM_DOC' THEN 'Platform Documentation Knowledge'
           WHEN 'CODEBASE_GUIDE' THEN 'Codebase Guide Knowledge'
           WHEN 'REDIS' THEN 'Redis'
           WHEN 'REDIS_STACK' THEN 'Redis Stack'
           WHEN 'MILVUS' THEN 'Milvus'
           WHEN 'PGVECTOR' THEN 'PGVector'
           WHEN 'ELASTICSEARCH' THEN 'Elasticsearch'
           WHEN 'QDRANT' THEN 'Qdrant'
           WHEN 'MEMORY' THEN 'Memory'
           WHEN 'DRAFT' THEN 'Draft'
           WHEN 'PUBLISHED' THEN 'Published'
           WHEN 'ARCHIVED' THEN 'Archived'
           WHEN '0' THEN 'Enabled'
           WHEN '1' THEN 'Disabled'
           WHEN 'PENDING' THEN 'Pending'
           WHEN 'SUCCESS' THEN 'Success'
           WHEN 'FAILED' THEN 'Failed'
           WHEN 'OFFICIAL' THEN 'Official'
           WHEN 'CUSTOM' THEN 'Custom'
           ELSE `dict_label`
           END
FROM `sys_dict_data`
WHERE `dict_type` IN (
                      'ai_knowledge_type',
                      'ai_knowledge_vector_store_type',
                      'ai_knowledge_publish_status',
                      'ai_knowledge_status',
                      'ai_knowledge_parse_status',
                      'ai_knowledge_source_origin'
    )
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);

-- ----------------------------
-- 15. AI 协议适配状态字典
-- ----------------------------
INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配任务状态', 'ai_protocol_adaptation_task_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配任务状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_task_status');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配解析状态', 'ai_protocol_adaptation_parse_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配解析状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_parse_status');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配校验状态', 'ai_protocol_adaptation_validation_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配校验状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_validation_status');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配生成状态', 'ai_protocol_adaptation_generation_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配生成状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_generation_status');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配风险等级', 'ai_protocol_adaptation_risk_level', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配风险等级'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_risk_level');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配产物类型', 'ai_protocol_adaptation_artifact_type', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配产物类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type');

INSERT INTO `sys_dict_type`
(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 'AI协议适配产物状态', 'ai_protocol_adaptation_artifact_status', 0, 'admin', NOW(), 'admin', NOW(), 'AI协议适配产物状态'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_type` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_status');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '草稿', 'DRAFT', 'ai_protocol_adaptation_task_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'DRAFT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '已上传', 'UPLOADED', 'ai_protocol_adaptation_task_status', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'UPLOADED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, 'AI已解析', 'AI_PARSED', 'ai_protocol_adaptation_task_status', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'AI_PARSED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '工作簿已导出', 'WORKBOOK_EXPORTED', 'ai_protocol_adaptation_task_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'WORKBOOK_EXPORTED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 5, '已导入校对', 'REVIEW_IMPORTED', 'ai_protocol_adaptation_task_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'REVIEW_IMPORTED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 6, '已校验', 'VALIDATED', 'ai_protocol_adaptation_task_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'VALIDATED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 7, '已生成', 'GENERATED', 'ai_protocol_adaptation_task_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'GENERATED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 8, '已确认', 'CONFIRMED', 'ai_protocol_adaptation_task_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'CONFIRMED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 9, '已归档', 'ARCHIVED', 'ai_protocol_adaptation_task_status', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'ARCHIVED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 10, '失败', 'FAILED', 'ai_protocol_adaptation_task_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_task_status' AND `dict_value` = 'FAILED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '待处理', 'PENDING', 'ai_protocol_adaptation_parse_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_parse_status' AND `dict_value` = 'PENDING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '解析中', 'PARSING', 'ai_protocol_adaptation_parse_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_parse_status' AND `dict_value` = 'PARSING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '成功', 'SUCCESS', 'ai_protocol_adaptation_parse_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_parse_status' AND `dict_value` = 'SUCCESS');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '失败', 'FAILED', 'ai_protocol_adaptation_parse_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_parse_status' AND `dict_value` = 'FAILED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '待校验', 'PENDING', 'ai_protocol_adaptation_validation_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_validation_status' AND `dict_value` = 'PENDING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '通过', 'PASSED', 'ai_protocol_adaptation_validation_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_validation_status' AND `dict_value` = 'PASSED');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '有告警', 'WARNING', 'ai_protocol_adaptation_validation_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_validation_status' AND `dict_value` = 'WARNING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '已阻断', 'BLOCKED', 'ai_protocol_adaptation_validation_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_validation_status' AND `dict_value` = 'BLOCKED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '待生成', 'PENDING', 'ai_protocol_adaptation_generation_status', '', 'info', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_generation_status' AND `dict_value` = 'PENDING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '生成中', 'GENERATING', 'ai_protocol_adaptation_generation_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_generation_status' AND `dict_value` = 'GENERATING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '成功', 'SUCCESS', 'ai_protocol_adaptation_generation_status', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_generation_status' AND `dict_value` = 'SUCCESS');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '失败', 'FAILED', 'ai_protocol_adaptation_generation_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_generation_status' AND `dict_value` = 'FAILED');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '低', 'LOW', 'ai_protocol_adaptation_risk_level', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_risk_level' AND `dict_value` = 'LOW');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '中', 'MEDIUM', 'ai_protocol_adaptation_risk_level', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_risk_level' AND `dict_value` = 'MEDIUM');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '高', 'HIGH', 'ai_protocol_adaptation_risk_level', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_risk_level' AND `dict_value` = 'HIGH');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '阻断', 'BLOCKER', 'ai_protocol_adaptation_risk_level', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_risk_level' AND `dict_value` = 'BLOCKER');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '原始协议文档', 'SOURCE_DOCUMENT', 'ai_protocol_adaptation_artifact_type', '', 'primary', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'SOURCE_DOCUMENT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '文本抽取结果', 'EXTRACTED_TEXT', 'ai_protocol_adaptation_artifact_type', '', 'info', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'EXTRACTED_TEXT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, 'AI DSL草稿', 'AI_DSL_DRAFT', 'ai_protocol_adaptation_artifact_type', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'AI_DSL_DRAFT');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 4, '企业工作簿', 'ENTERPRISE_WORKBOOK', 'ai_protocol_adaptation_artifact_type', '', 'primary', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'ENTERPRISE_WORKBOOK');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 5, '人工回填工作簿', 'REVIEW_WORKBOOK', 'ai_protocol_adaptation_artifact_type', '', 'success', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'REVIEW_WORKBOOK');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 6, '校验报告', 'VALIDATION_REPORT', 'ai_protocol_adaptation_artifact_type', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type' AND `dict_value` = 'VALIDATION_REPORT');

INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 1, '已就绪', 'READY', 'ai_protocol_adaptation_artifact_status', '', 'success', 'Y', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_status' AND `dict_value` = 'READY');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 2, '生成中', 'GENERATING', 'ai_protocol_adaptation_artifact_status', '', 'warning', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_status' AND `dict_value` = 'GENERATING');
INSERT INTO `sys_dict_data`
(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT 3, '失败', 'FAILED', 'ai_protocol_adaptation_artifact_status', '', 'danger', 'N', 0, 'admin', NOW(), 'admin', NOW(), ''
    WHERE NOT EXISTS (SELECT 1 FROM `sys_dict_data` WHERE `dict_type` = 'ai_protocol_adaptation_artifact_status' AND `dict_value` = 'FAILED');

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配任务状态', 'AI Protocol Adaptation Task Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_task_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配解析状态', 'AI Protocol Adaptation Parse Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_parse_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配校验状态', 'AI Protocol Adaptation Validation Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_validation_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配生成状态', 'AI Protocol Adaptation Generation Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_generation_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配风险等级', 'AI Protocol Adaptation Risk Level'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_risk_level'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配产物类型', 'AI Protocol Adaptation Artifact Type'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_artifact_type'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_id`, 'AI协议适配产物状态', 'AI Protocol Adaptation Artifact Status'
FROM `sys_dict_type`
WHERE `dict_type` = 'ai_protocol_adaptation_artifact_status'
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_type_translate` WHERE `id` = `dict_id`);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`)
SELECT `dict_code`,
       `dict_label`,
       CASE
           WHEN `dict_type` = 'ai_protocol_adaptation_validation_status' AND `dict_value` = 'PENDING' THEN 'Pending Validation'
           WHEN `dict_type` = 'ai_protocol_adaptation_generation_status' AND `dict_value` = 'PENDING' THEN 'Pending Generation'
           WHEN `dict_value` = 'DRAFT' THEN 'Draft'
           WHEN `dict_value` = 'UPLOADED' THEN 'Uploaded'
           WHEN `dict_value` = 'AI_PARSED' THEN 'AI Parsed'
           WHEN `dict_value` = 'WORKBOOK_EXPORTED' THEN 'Workbook Exported'
           WHEN `dict_value` = 'REVIEW_IMPORTED' THEN 'Review Imported'
           WHEN `dict_value` = 'VALIDATED' THEN 'Validated'
           WHEN `dict_value` = 'GENERATED' THEN 'Generated'
           WHEN `dict_value` = 'CONFIRMED' THEN 'Confirmed'
           WHEN `dict_value` = 'ARCHIVED' THEN 'Archived'
           WHEN `dict_value` = 'FAILED' THEN 'Failed'
           WHEN `dict_value` = 'PENDING' THEN 'Pending'
           WHEN `dict_value` = 'PARSING' THEN 'Parsing'
           WHEN `dict_value` = 'SUCCESS' THEN 'Success'
           WHEN `dict_value` = 'PASSED' THEN 'Passed'
           WHEN `dict_value` = 'WARNING' THEN 'Warning'
           WHEN `dict_value` = 'BLOCKED' THEN 'Blocked'
           WHEN `dict_value` = 'GENERATING' THEN 'Generating'
           WHEN `dict_value` = 'LOW' THEN 'Low'
           WHEN `dict_value` = 'MEDIUM' THEN 'Medium'
           WHEN `dict_value` = 'HIGH' THEN 'High'
           WHEN `dict_value` = 'BLOCKER' THEN 'Blocker'
           WHEN `dict_value` = 'SOURCE_DOCUMENT' THEN 'Source Document'
           WHEN `dict_value` = 'EXTRACTED_TEXT' THEN 'Extracted Text'
           WHEN `dict_value` = 'AI_DSL_DRAFT' THEN 'AI DSL Draft'
           WHEN `dict_value` = 'ENTERPRISE_WORKBOOK' THEN 'Enterprise Workbook'
           WHEN `dict_value` = 'REVIEW_WORKBOOK' THEN 'Review Workbook'
           WHEN `dict_value` = 'VALIDATION_REPORT' THEN 'Validation Report'
           WHEN `dict_value` = 'READY' THEN 'Ready'
           ELSE `dict_label`
           END
FROM `sys_dict_data`
WHERE `dict_type` IN (
                      'ai_protocol_adaptation_task_status',
                      'ai_protocol_adaptation_parse_status',
                      'ai_protocol_adaptation_validation_status',
                      'ai_protocol_adaptation_generation_status',
                      'ai_protocol_adaptation_risk_level',
                      'ai_protocol_adaptation_artifact_type',
                      'ai_protocol_adaptation_artifact_status'
    )
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_data_translate` WHERE `id` = `dict_code`);


