DROP TABLE IF EXISTS `iot_rule_trigger`;
CREATE TABLE `iot_rule_trigger` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
    `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
    `trigger_type` tinyint(4) NOT NULL COMMENT '条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义',
    `serial_number` varchar(100) DEFAULT NULL COMMENT '设备编号(设备触发时使用)',
    `product_id` bigint(20) DEFAULT NULL COMMENT '产品ID(产品触发时使用)',
    `model_id` varchar(100) DEFAULT NULL COMMENT '物模型标识',
    `operator` varchar(10) DEFAULT NULL COMMENT '操作符: >, <, =, >=, <=, !=',
    `value` varchar(100) DEFAULT NULL COMMENT '比较值',
    `cron_expression` varchar(100) DEFAULT NULL COMMENT '定时表达式(定时触发时使用)',
    `custom_expression` text COMMENT '自定义表达式(自定义触发时使用)',
    `order_num` int(11) DEFAULT '0' COMMENT '排序号',
    `device_status` tinyint(4) DEFAULT NULL COMMENT '设备状态: 1-在线, 0-离线',
    `trigger_params` json NULL COMMENT '触发参数',
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_rule_id` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='规则触发条件表';

DROP TABLE IF EXISTS `iot_rule_log`;
CREATE TABLE `iot_rule_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
    `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
    `el_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'EL表达式ID',
    `status` tinyint(4) NOT NULL COMMENT '执行状态: 0-失败, 1-成功',
    `trigger_type` tinyint(4) NOT NULL COMMENT '触发类型',
    `rule_params` json COMMENT '规则入参',
    `step_msg` json DEFAULT NULL COMMENT '组件步骤信息',
    `result_msg` text DEFAULT NULL COMMENT '执行结果信息',
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_rule_id` (`rule_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则执行记录表';

ALTER TABLE rule_el RENAME TO iot_rule_el;

ALTER TABLE bridge RENAME TO iot_bridge;

ALTER TABLE `iot_rule_el`
    ADD COLUMN `rule_params` json NULL COMMENT '规则入参';

ALTER TABLE `sys_client`
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id';

ALTER TABLE `scene_model_tag`
    ADD COLUMN `is_scene_attribute` tinyint(2) DEFAULT 0 COMMENT '是否配置为场景属性(0-否，1-是)';

ALTER TABLE `iot_event_log`
    MODIFY COLUMN  `is_monitor` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否监测数据（1=是，0=否）',
    MODIFY COLUMN  `mode` tinyint(1) NOT NULL DEFAULT 0 COMMENT '模式(1=影子模式，2=在线模式，3=其他)';

INSERT INTO `sys_config` VALUES (7, '规则脚本-引入包白名单', 'sys.rule.allowPackages', 'import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.HexUtil;', 'Y', 'admin', '2025-11-06 23:29:21', '', NULL, '');

-- 物联网卡平台
INSERT INTO `sys_dict_type` VALUES (177, '物联卡平台', 'iot_card_platform', 0, 'admin', '2025-11-11 14:31:24', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` VALUES (177, '物联卡平台', 'IoT card platform');

INSERT INTO `sys_dict_data` VALUES (787, 1, '中国移动', 'mobile', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-11-11 14:41:28', 'admin', '2025-12-20 11:29:05', '{\"apiBaseUrl\":\"http://111.10.45.200:7000\",\"appid\":\"\",\"password\":\"\",\"transid\":\"\"}');
INSERT INTO `sys_dict_data` VALUES (788, 2, '中国电信5GCMP', 'telecom', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-11-11 14:42:58', 'admin', '2025-12-20 11:29:29', '{\"apiBaseUrl\":\"https://cmp-api.ctwing.cn:20164\",\"appKey\":\"\",\"secretKey\":\"\"}');
INSERT INTO `sys_dict_data` VALUES (789, 3, '中国联通', 'unicom', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-11-11 14:45:44', 'admin', '2025-12-20 11:29:16', '{\"apiBaseUrl\":\"https://gwapi.10646.cn/api\",\"appid\":\"\",\"appSecret\":\"\",\"version\":\"\",\"openId\":\"\"}');
INSERT INTO `sys_dict_data` VALUES (790, 5, '握手物联卡', 'handshake', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-11-11 14:49:29', 'admin', '2025-12-19 15:01:41', '{\"apiBaseUrl\":\"http://tools.wosoiot.com:8080\",\"account\":\"\",\"password\":\"\"}');

INSERT INTO `sys_dict_data_translate` VALUES (787, '中国移动', 'China Mobile');
INSERT INTO `sys_dict_data_translate` VALUES (788, '中国电信5GCMP', 'China Telecom 5GCMP');
INSERT INTO `sys_dict_data_translate` VALUES (789, '中国联通', 'China Unicom');
INSERT INTO `sys_dict_data_translate` VALUES (790, '握手物联卡', 'Handshake IoT card');

INSERT INTO `sys_menu` VALUES (3421, '物联卡管理', 0, 8, 'card', NULL, NULL, 1, 0, 'M', '0', 0, NULL, 'card_manage', 'admin', '2025-11-11 11:11:52', '', NULL, '');
INSERT INTO `sys_menu_translate` VALUES (3421, '物联卡管理', 'IoT card management');

CREATE TABLE `iot_card_platform` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',
                                     `platform` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '平台',
                                     `config_content` varchar(1024) COLLATE utf8_unicode_ci NOT NULL COMMENT '配置内容',
                                     `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                                     `tenant_name` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                                     `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='物联卡平台';

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('卡平台', '3421', '0', 'platform', 'iot/card/platform/index', 1, 0, 'C', '0', '0', 'iot:cardPlatform:list', 'card_platfrom', 'admin', sysdate(), '', null, '物联卡平台菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联卡平台查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:cardPlatform:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联卡平台新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:cardPlatform:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联卡平台修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:cardPlatform:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联卡平台删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:cardPlatform:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联卡平台导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:cardPlatform:export',       '#', 'admin', sysdate(), '', null, '');

INSERT INTO `sys_menu_translate` VALUES (3422, '物联卡平台', 'IoT card platform');
INSERT INTO `sys_menu_translate` VALUES (3423, '物联卡平台查询', 'Query on IoT card platform');
INSERT INTO `sys_menu_translate` VALUES (3424, '物联卡平台新增', 'New addition to the IoT card platform');
INSERT INTO `sys_menu_translate` VALUES (3425, '物联卡平台修改', 'Modification of IoT card platform');
INSERT INTO `sys_menu_translate` VALUES (3426, '物联卡平台删除', 'Delete IoT card platform');
INSERT INTO `sys_menu_translate` VALUES (3427, '物联卡平台导出', 'Export from IoT card platform');

-- 物联网卡
INSERT INTO `sys_dict_type` VALUES (178, '物联卡运营商', 'iot_card_operator', 0, 'admin', '2025-11-12 15:16:14', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` VALUES (178, '物联卡运营商', 'IoT SIM card operator');

INSERT INTO `sys_dict_data` VALUES (791, 1, '中国移动', 'CMCC', 'iot_card_operator', NULL, 'default', 'N', 0, 'admin', '2025-11-12 15:16:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (792, 2, '中国电信', 'CTCC', 'iot_card_operator', NULL, 'default', 'N', 0, 'admin', '2025-11-12 15:17:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (793, 3, '中国联通', 'CUCC', 'iot_card_operator', NULL, 'default', 'N', 0, 'admin', '2025-11-12 15:17:21', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` VALUES (791, '中国移动', 'China Mobile');
INSERT INTO `sys_dict_data_translate` VALUES (792, '中国电信', 'China Telecom');
INSERT INTO `sys_dict_data_translate` VALUES (793, '中国联通', 'China Unicom');

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('SIM卡', '3421', '2', 'sim', 'iot/card/sim/index', 1, 0, 'C', '0', '0', 'iot:card:list', 'sim_card', 'admin', sysdate(), '', null, '物联网卡菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联网卡查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:card:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联网卡新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:card:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联网卡修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:card:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联网卡删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:card:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('物联网卡导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:card:export',       '#', 'admin', sysdate(), '', null, '');

INSERT INTO `sys_menu_translate` VALUES (3428, 'SIM卡', 'SIM card');
INSERT INTO `sys_menu_translate` VALUES (3429, '物联网卡查询', 'IoT SIM card inquiry');
INSERT INTO `sys_menu_translate` VALUES (3430, '物联网卡新增', 'New IoT SIM Card');
INSERT INTO `sys_menu_translate` VALUES (3431, '物联网卡修改', 'IoT SIM card modification');
INSERT INTO `sys_menu_translate` VALUES (3432, '物联网卡删除', 'IoT card deletion');
INSERT INTO `sys_menu_translate` VALUES (3433, '物联网卡导出', 'IoT SIM Card Export');

CREATE TABLE `iot_card` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `access_number` varchar(64) DEFAULT NULL COMMENT '接入号码',
                            `iccid` varchar(64) NOT NULL COMMENT 'ICCID号',
                            `imsi` varchar(64) DEFAULT NULL COMMENT 'IMSI号',
                            `msisdn` varchar(64) DEFAULT NULL COMMENT '卡号（手机号）',
                            `imei` varchar(64) DEFAULT NULL COMMENT 'IMEI号',
                            `operator` varchar(10) NOT NULL COMMENT '运营商',
                            `card_status` tinyint(4) DEFAULT NULL COMMENT '卡状态',
                            `total_data` decimal(12,2) DEFAULT NULL COMMENT '总流量(MB)',
                            `data_used` decimal(12,2) DEFAULT NULL COMMENT '已用流量(MB)',
                            `data_remaining` decimal(12,2) DEFAULT NULL COMMENT '剩余流量(MB)',
                            `open_date` datetime DEFAULT NULL COMMENT '开卡日期',
                            `activate_time` datetime DEFAULT NULL COMMENT '激活时间',
                            `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
                            `down_time` datetime DEFAULT NULL COMMENT '停机时间',
                            `device_id` bigint(20) DEFAULT NULL COMMENT '设备ID',
                            `card_platform_id` bigint(20) DEFAULT NULL COMMENT '卡平台id',
                            `data_plan` varchar(200) DEFAULT NULL COMMENT '流量套餐',
                            `data_alert_threshold` decimal(5,2) DEFAULT NULL COMMENT '流量告警阈值(%)',
                            `notify_users` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '通知用户',
                            `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                            `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                            `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_iccid` (`iccid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='物联网卡表';


ALTER TABLE `sip_device_channel`
    MODIFY COLUMN `proxy_url` varchar(1000) NOT NULL DEFAULT '' COMMENT '流代理地址';

update sys_menu set menu_name = '产品组态' where menu_id = 3347;
update sys_menu_translate set zh_cn = '产品组态' where id = 3347;

INSERT INTO `sys_job` VALUES (8, '物联网卡定时同步', 'SYSTEM', 'syncCardJob.batchSyncTraffic', '0 0/10 9-22 * * ?', '1', '1', 0, 'admin', NULL, '', NULL, '');

INSERT INTO `sys_dict_type` VALUES (179, '物联卡状态', 'iot_card_status', 0, 'admin', '2025-12-09 09:29:30', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` VALUES (179, '物联卡状态', 'IoT card status');

INSERT INTO `sys_dict_data` VALUES (794, 0, '正常', '0', 'iot_card_status', NULL, 'success', 'N', 0, 'admin', '2025-12-09 09:30:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (795, 1, '待激活', '1', 'iot_card_status', NULL, 'info', 'N', 0, 'admin', '2025-12-09 09:31:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (796, 2, '停机', '2', 'iot_card_status', NULL, 'danger', 'N', 0, 'admin', '2025-12-09 09:32:06', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (797, 3, '销号', '3', 'iot_card_status', NULL, 'danger', 'N', 0, 'admin', '2025-12-09 09:32:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (798, 99, '未知', '99', 'iot_card_status', NULL, 'warning', 'N', 0, 'admin', '2025-12-09 09:32:44', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` VALUES (794, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` VALUES (795, '待激活', 'Pending activation');
INSERT INTO `sys_dict_data_translate` VALUES (796, '停机', 'shutdown');
INSERT INTO `sys_dict_data_translate` VALUES (797, '销号', 'account termination');
INSERT INTO `sys_dict_data_translate` VALUES (798, '未知', 'unknown');

INSERT INTO `sys_dict_data` VALUES (799, 6, '物联卡通知', 'card', 'notify_service_code', NULL, 'default', 'N', 0, 'admin', '2025-12-09 16:29:06', 'admin', '2025-12-09 16:29:12', NULL);
INSERT INTO `sys_dict_data_translate` VALUES (799, '物联卡通知', 'IoT Card Notification');

INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (25, '物联卡流量告警QQ邮箱通知', 'card', 5, 'email', 'qq', '{\"sendAccount\":\"\",\"title\":\"物联卡告警\",\"attachment\":\"\",\"content\":\"<p>卡号：#{msisdn}，ICCID：#{iccid}，总流量：#{totalData}，已用流量：#{dataUsed}，剩余流量：#{dataRemaining}</p>\"}', 1, 'admin', '2025-12-09 17:35:26', NULL, '2025-12-09 17:35:30', 0, 1, '蜂信物联');

ALTER TABLE `sys_menu`
    ADD COLUMN `target` int(1) DEFAULT '0' COMMENT '打开方式（0页签 1新窗口）';

update `sys_menu` set target = 0 where is_frame = 1;
update `sys_menu` set target = 1 where is_frame = 0;

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`)
VALUES (3434, '卡概览', 3421, 1, 'overview', 'iot/card/overview/index', NULL, 1, 0, 'C', '0', 0, 'iot:card:overview', 'card_view', 'admin', '2025-12-11 10:40:06', '', NULL, '', 0);

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3434, '卡概览', 'Card Overview');

INSERT INTO `sys_dict_data` VALUES (800, 4, '有人云物联卡', 'usr', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-12-17 11:29:42', 'admin', '2025-12-20 11:29:41', '{\"apiBaseUrl\":\"https://sim-api.usr.cn\",\"appKey\":\"\",\"appSecret\":\"\"}');
INSERT INTO `sys_dict_data` VALUES (801, 6, '智宇物联卡', 'zhiyu', 'iot_card_platform', NULL, 'default', 'N', 0, 'admin', '2025-12-17 11:33:44', 'admin', '2025-12-18 10:53:21', '{\"apiBaseUrl\":\"https://api.wl1688.net/iotc/getway\",\"appid\":\"\",\"appSecret\":\"\"}');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (800, '有人云物联卡', 'Someone Cloud IoT Card');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (801, '智宇物联卡', 'Zhiyu IoT Card');

-- 消息通知公众号更改为服务号
update sys_dict_data
set dict_label = '微信服务号',
    dict_value = 'service_account'
where dict_type = 'notify_channel_wechat_provider'
  and   dict_value = 'public_account';

update notify_channel
set provider = 'service_account'
where provider = 'public_account';

update notify_template
set provider = 'service_account'
where provider = 'public_account';

-- 设备维保
CREATE TABLE `iot_device_maintenance` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                          `name` varchar(200) NOT NULL COMMENT '维保名称',
                                          `device_id` bigint(20) NOT NULL COMMENT '设备id',
                                          `device_name` varchar(200) NOT NULL COMMENT '设备名称',
                                          `maintenance_time` int(11) NOT NULL COMMENT '保内维保时间',
                                          `maintenance_time_unit` tinyint(2) NOT NULL COMMENT '保内维保时间单位',
                                          `maintenance_period` int(11) NOT NULL COMMENT '维保周期',
                                          `maintenance_period_unit` tinyint(2) NOT NULL COMMENT '维保周期单位',
                                          `start_maintenance_time` datetime NOT NULL COMMENT '开始维保时间',
                                          `next_maintenance_time` datetime DEFAULT NULL COMMENT '下次维保时间',
                                          `expire_stop_flag` tinyint(2) NOT NULL COMMENT '保内维保到期停止执行',
                                          `pre_work_time_type` tinyint(2) NOT NULL COMMENT '提前派工单时间',
                                          `status` tinyint(2) NOT NULL COMMENT '状态',
                                          `maintenance_type` tinyint(2) NOT NULL COMMENT '维保类型',
                                          `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                                          `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                                          `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='设备维保';

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保', '2000', '8', 'maintenance', 'iot/maintenance/index', 1, 0, 'C', '0', '0', 'iot:maintenance:list', 'maintenance', 'admin', sysdate(), '', null, '设备维保菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:maintenance:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:maintenance:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:maintenance:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:maintenance:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('设备维保导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:maintenance:export',       '#', 'admin', sysdate(), '', null, '');

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3435, '设备维保', 'Device Maintenance');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3436, '设备维保查询', 'Device Maintenance Query');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3437, '设备维保新增', 'Device Maintenance Add');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3438, '设备维保修改', 'Device Maintenance Update');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3439, '设备维保删除', 'Device Maintenance Delete');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3440, '设备维保导出', 'Device Maintenance Report');


INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (182, '维保时间单位', 'maintenance_unit', 0, 'admin', '2025-12-26 17:16:10', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (183, '维保提前时间类型', 'maintenance_pretime_type', 0, 'admin', '2025-12-26 17:18:50', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (182, '维保时间单位', 'Maintenance time unit');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (183, '维保提前时间类型', 'Maintenance advance time type');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (807, 1, '天', '1', 'maintenance_unit', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:16:29', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (808, 2, '周', '2', 'maintenance_unit', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:16:43', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (809, 3, '月', '3', 'maintenance_unit', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:16:56', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (810, 4, '年', '4', 'maintenance_unit', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:17:10', '', NULL, NULL);

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (811, 0, '不提前', '0', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:19:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (812, 1, '一天', '1', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:20:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (813, 2, '两天', '2', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:20:21', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (814, 3, '三天', '3', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:20:46', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (815, 4, '一周', '4', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:21:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (816, 5, '两周', '5', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:21:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (817, 6, '一个月', '6', 'maintenance_pretime_type', NULL, 'default', 'N', 0, 'admin', '2025-12-26 17:21:43', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (807, '天', 'Day');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (808, '周', 'Week');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (809, '月', 'Month');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (810, '年', 'Year');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (811, '不提前', 'Not in advance');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (812, '一天', 'One day');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (813, '两天', 'Two days');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (814, '三天', 'Three days');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (815, '一周', 'One week');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (816, '两周', 'Two weeks');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (817, '一个月', 'One month');

INSERT INTO `sys_job` (`job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备维保自动生成工单', 'DEFAULT', 'deviceMaintenanceJob.batchGenerateWorkOrder()', '0 0 0 * * ?', '1', '1', 0, 'admin', NULL, '', NULL, '');

ALTER TABLE `iot_work_order`
    ADD COLUMN `device_maintenance_id` bigint(20) NULL COMMENT '设备维保id';

-- 后面更新的
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (184, '系统类型状态', 'system_type_status', 0, 'admin', '2026-01-13 15:52:11', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (184, '系统类型状态', 'System Type Status');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (819, 0, '公有', '1', 'system_type_status', NULL, 'success', 'N', 0, 'admin', '2026-01-13 15:52:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (820, 0, '私有', '0', 'system_type_status', NULL, 'danger', 'N', 0, 'admin', '2026-01-13 15:53:05', '', NULL, NULL);
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (819, '公有', 'Public');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (820, '私有', 'Private');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (185, '产品指令功能码', 'product_command_function_code', 0, 'admin', '2026-01-16 09:35:38', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (186, 'modbus读写配置', 'modbus_read_config', 0, 'admin', '2026-01-16 10:10:03', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (187, '物模型数据类型', 'model_data_type', 0, 'admin', '2026-01-16 10:13:35', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (188, '物模型数组类型', 'model_array_type', 0, 'admin', '2026-01-16 10:22:14', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (189, '桥接状态', 'bridge_status', 0, 'admin', '2026-01-16 10:26:03', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (190, '桥接类型', 'bridge_type', 0, 'admin', '2026-01-16 10:28:13', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (191, '桥接方向', 'bridging_direction', 0, 'admin', '2026-01-16 10:31:34', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (192, '是否告警', 'is_alert', 0, 'admin', '2026-01-16 10:45:54', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (193, '触发条件', 'trigger_condition', 0, 'admin', '2026-01-16 10:47:37', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (194, '执行方式', 'execution_method', 0, 'admin', '2026-01-16 10:50:21', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (195, '告警处理', 'alarm_handling', 0, 'admin', '2026-01-16 10:52:36', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (197, '设备影子状态', 'device_shadow', 0, 'admin', '2026-01-16 10:59:00', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (198, '设备分享用户类型', 'device_sharing_user_type', 0, 'admin', '2026-01-16 11:05:47', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (199, '设备排序', 'device_sort', 0, 'admin', '2026-01-16 14:04:08', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200, '授权码启用', 'authorization_code_active', 0, 'admin', '2026-01-16 14:11:19', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (201, '固件升级状态', 'firmware_upgrade_status', 0, 'admin', '2026-01-16 14:14:05', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (202, '客户端连接状态', 'client_connection_status', 0, 'admin', '2026-01-16 14:23:43', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (203, '是否并发', 'is_concurrency', 0, 'admin', '2026-01-16 14:30:51', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (204, '执行策略', 'execution_strategy', 0, 'admin', '2026-01-16 14:33:22', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (205, '执行状态', 'execution_status', 0, 'admin', '2026-01-16 14:36:02', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (206, '规则可视化时间类型', 'rule_visualization_time_type', 0, 'admin', '2026-01-16 14:39:03', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (207, '触发器类别', 'trigger_type', 0, 'admin', '2026-01-16 14:43:33', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (208, '桥接入口', 'bridge_entry', 0, 'admin', '2026-01-16 14:46:01', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (209, '规则可视化触发状态', 'rule_visualization_triggering_status', 0, 'admin', '2026-01-16 15:28:09', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (210, '规则可视化触发条件', 'rule_visualization_triggering_conditions', 0, 'admin', '2026-01-16 15:31:46', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (211, '国际化配置模板', 'international_configuration_template', 0, 'admin', '2026-01-16 15:36:08', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (212, '数据权限', 'data_scope', 0, 'admin', '2026-01-16 15:39:31', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (213, '用户类型', 'user_type', 0, 'admin', '2026-01-16 15:49:09', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (185, '产品指令功能码', 'Product command function code');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (186, 'modbus读写配置', 'modbus configuration');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (187, '物模型数据类型', 'model data type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (188, '物模型数组类型', 'model array type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (189, '桥接状态', 'bridge status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (190, '桥接类型', 'briage type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (191, '桥接方向', 'bridging direction');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (192, '是否告警', 'Is alert');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (193, '触发条件', 'trigger condition');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (194, '执行方式', 'Execution method');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (195, '告警处理', 'alarm handling');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (196, '设备分配类型', 'Device allocation type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (197, '设备影子状态', 'Device Shadow');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (198, '设备分享用户类型', 'Device sharing user type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (199, '设备排序', 'Device sorting');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (200, '授权码启用', 'Authorization code active');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (201, '固件升级状态', 'Firmware upgrade status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (202, '客户端连接状态', 'Client connection status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (203, '是否并发', 'is concurrency');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (204, '执行策略', 'execution strategy');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (205, '执行状态', 'Execution status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (206, '规则可视化时间类型', 'Rule visualization time type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (207, '触发器类别', 'Trigger type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (208, '桥接入口', 'bridge entry');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (209, '规则可视化触发状态', 'Rule visualization triggering status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (210, '规则可视化触发条件', 'Rule visualization triggering conditions');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (211, '国际化配置模板', 'International configuration template');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (212, '数据权限', 'data scope');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (213, '用户类型', 'User Type');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (821, 0, '01 读取线圈状态', '01', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:38:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (822, 0, '02 读取输入状态', '02', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:39:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (823, 0, '03 读取保存寄存器', '03', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:40:36', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (824, 0, '04 读取输入寄存器', '04', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:41:18', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (825, 0, '05 写入单个线圈寄存器', '05', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:42:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (826, 0, '06 写入单个保存寄存器', '06', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 09:43:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (827, 0, '15 写入多个线圈状态', '15', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:05:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (828, 0, '16 写入多个保存寄存器', '16', 'product_command_function_code', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:06:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (829, 0, '只读 04', '1', 'modbus_read_config', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:11:19', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (830, 0, '读写 03/06', '0', 'modbus_read_config', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:12:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (831, 0, '整数', 'integer', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:19:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (832, 0, '小数', 'decimal', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:19:55', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (833, 0, '布尔', 'bool', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:20:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (834, 0, '枚举', 'enum', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:20:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (835, 0, '字符串', 'string', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:20:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (836, 0, '数组', 'array', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:21:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (837, 0, '对象', 'object', 'model_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:21:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (838, 0, '整数', 'integer', 'model_array_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:23:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (839, 0, '小数', 'decimal', 'model_array_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:23:37', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (840, 0, '字符串', 'string', 'model_array_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:23:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (841, 0, '对象', 'object', 'model_array_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:24:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (842, 0, '未连接', '0', 'bridge_status', NULL, 'warning', 'N', 0, 'admin', '2026-01-16 10:26:56', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (843, 0, '连接中', '1', 'bridge_status', NULL, 'success', 'N', 0, 'admin', '2026-01-16 10:27:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (844, 0, 'Http推送', '3', 'bridge_type', NULL, 'warning', 'N', 0, 'admin', '2026-01-16 10:29:21', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (845, 0, 'Mqtt桥接', '4', 'bridge_type', NULL, 'success', 'N', 0, 'admin', '2026-01-16 10:29:48', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (846, 0, '数据库存储', '5', 'bridge_type', NULL, 'info', 'N', 0, 'admin', '2026-01-16 10:30:23', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (847, 0, '输入', '1', 'bridging_direction', NULL, 'success', 'N', 0, 'admin', '2026-01-16 10:32:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (848, 0, '输出', '2', 'bridging_direction', NULL, 'warning', 'N', 0, 'admin', '2026-01-16 10:33:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (849, 0, '是', '1', 'is_alert', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:46:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (850, 0, '否', '2', 'is_alert', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:46:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (851, 0, '任意条件', '1', 'trigger_condition', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:48:21', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (852, 0, '所有条件', '2', 'trigger_condition', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:48:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (853, 0, '不满足条件', '3', 'trigger_condition', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:49:47', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (854, 0, '串行', '1', 'execution_method', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:51:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (855, 0, '并行', '2', 'execution_method', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:51:30', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (856, 0, '无需处理', '1', 'alarm_handling', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:53:29', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (857, 0, '已处理', '3', 'alarm_handling', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:54:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (861, 0, '开启', '1', 'device_shadow', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:59:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (862, 0, '关闭', '0', 'device_shadow', NULL, 'default', 'N', 0, 'admin', '2026-01-16 10:59:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (863, 0, '拥有者', '1', 'device_sharing_user_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 11:06:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (864, 0, '分享', '2', 'device_sharing_user_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 11:06:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (865, 0, '名称降序', '1', 'device_sort', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:04:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (866, 0, '名称升序', '2', 'device_sort', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:08:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (867, 0, '时间降序', '3', 'device_sort', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:09:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (868, 0, '时间升序', '4', 'device_sort', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:10:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (869, 0, '启用', '1', 'authorization_code_active', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:12:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (870, 0, '未启用', '0', 'authorization_code_active', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:12:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (871, 0, '待推送', '0', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:15:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (872, 0, '升级中', '2', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:15:30', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (873, 0, '升级成功', '3', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:16:07', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (874, 0, '升级失败', '4', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:16:25', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (875, 0, '停止', '5', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:16:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (876, 0, '未知', '404', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:22:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (877, 0, '发送中', '1', 'firmware_upgrade_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:22:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (878, 0, '已连接', 'true', 'client_connection_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:27:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (879, 0, '已断开', 'false', 'client_connection_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:27:37', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (880, 0, '允许', '0', 'is_concurrency', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:31:19', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (881, 0, '禁止', '1', 'is_concurrency', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:32:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (882, 0, '默认策略', '0', 'execution_strategy', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:33:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (883, 0, '立即执行', '1', 'execution_strategy', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:34:18', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (884, 0, '执行一次', '2', 'execution_strategy', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:34:41', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (885, 0, '放弃执行', '3', 'execution_strategy', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:35:14', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (886, 0, '正常', '0', 'execution_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:36:33', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (887, 0, '失败', '1', 'execution_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:37:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (888, 0, '毫秒', '1', 'rule_visualization_time_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:39:37', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (889, 0, '秒', '2', 'rule_visualization_time_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:40:02', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (890, 0, '分', '3', 'rule_visualization_time_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:40:36', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (891, 0, '小时', '4', 'rule_visualization_time_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:40:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (892, 0, '天', '5', 'rule_visualization_time_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:41:03', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (893, 0, '属性', '1', 'trigger_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:44:02', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (894, 0, '功能', '2', 'trigger_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:44:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (895, 0, '事件', '3', 'trigger_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:44:32', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (896, 0, '设备上线', '5', 'trigger_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:44:55', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (897, 0, '设备下线', '6', 'trigger_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:45:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (898, 0, 'Http桥接GET', '/bridge/get', 'bridge_entry', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:47:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (899, 0, 'Http桥接PUT', '/bridge/put', 'bridge_entry', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:48:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (900, 0, 'Http桥接POST', '/bridge/post', 'bridge_entry', NULL, 'default', 'N', 0, 'admin', '2026-01-16 14:48:46', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (901, 0, '成功', 'success', 'rule_visualization_triggering_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:29:33', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (902, 0, '警告', 'warning', 'rule_visualization_triggering_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:29:55', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (903, 0, '错误', 'error', 'rule_visualization_triggering_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:30:10', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (904, 0, '运行中', 'running', 'rule_visualization_triggering_status', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:30:46', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (905, 0, '任意条件', '1', 'rule_visualization_triggering_conditions', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:32:32', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (906, 0, '所有条件', '2', 'rule_visualization_triggering_conditions', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:32:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (907, 0, '菜单', 'menu', 'international_configuration_template', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:36:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (908, 0, '字典数据', 'dict_data', 'international_configuration_template', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:36:58', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (909, 0, '字典类型', 'dict_type', 'international_configuration_template', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:37:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (910, 0, '物模型', 'things_model', 'international_configuration_template', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:37:59', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (911, 0, '物模型模板', 'things_model_template', 'international_configuration_template', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:38:35', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (912, 0, '本机构数据权限', '3', 'data_scope', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:41:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (913, 0, '本机构及以下机构数据权限', '4', 'data_scope', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:42:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (914, 0, '仅本人数据权限', '5', 'data_scope', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:47:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (915, 0, '终端', 'true', 'user_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:50:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (916, 0, '机构', 'false', 'user_type', NULL, 'default', 'N', 0, 'admin', '2026-01-16 15:50:56', '', NULL, NULL);


INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (821, '01 读取线圈状态', 'Read coil status');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (822, '02 读取输入状态', 'Read input status');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (823, '03 读取保存寄存器', 'Read save register');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (824, '04 读取输入寄存器', 'Read input register');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (825, '05 写入单个线圈寄存器', 'Write to single coil register');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (826, '06 写入单个保存寄存器', 'Write to single save register');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (827, '15 写入多个线圈状态', 'Write multiple coil states');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (828, '16 写入多个保存寄存器', 'Write to multiple save registers');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (829, '只读 04', 'read only');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (830, '读写 03/06', 'read and write');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (831, '整数', 'integer');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (832, '小数', 'decimal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (833, '布尔', 'bool');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (834, '枚举', 'enum');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (835, '字符串', 'string');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (836, '数组', 'array');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (837, '对象', 'object');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (838, '整数', 'integer');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (839, '小数', 'decimal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (840, '字符串', 'string');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (841, '对象', 'object');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (842, '未连接', 'Not connected');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (843, '连接中', 'Connecting');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (844, 'Http推送', 'HTTP push');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (845, 'Mqtt桥接', 'Mqtt bridge');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (846, '数据库存储', 'database storage');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (847, '输入', 'input');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (848, '输出', 'output');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (849, '是', 'yes');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (850, '否', 'no');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (851, '任意条件', 'Any condition');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (852, '所有条件', 'All condition');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (853, '不满足条件', 'Does not meet the conditions');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (854, '串行', 'serial');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (855, '并行', 'parallel');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (856, '无需处理', 'No processing required');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (857, '已处理', 'Processed');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (858, '选择分配', 'Assignment Selection');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (859, '导入分配', 'import assignment');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (860, '用户分配', 'User assignment ');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (861, '开启', 'open');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (862, '关闭', 'close');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (863, '拥有者', 'owner');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (864, '分享', 'share');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (865, '名称降序', 'device name DESC');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (866, '名称升序', 'device name ASC');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (867, '时间降序', 'create time DESC');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (868, '时间升序', 'create time ASC');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (869, '启用', 'enable');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (870, '未启用', 'not enable');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (871, '待推送', 'Awaiting push');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (872, '升级中', 'Upgrading');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (873, '升级成功', 'Upgrade successful');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (874, '升级失败', 'Upgrade failed');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (875, '停止', 'Upgrade stop');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (876, '未知', 'unknown');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (877, '发送中', 'Sending');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (878, '已连接', 'Connected');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (879, '已断开', 'Disconnected');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (880, '允许', 'allow');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (881, '禁止', 'Prohibit');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (882, '默认策略', '默认策略');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (883, '立即执行', 'Execute immediately');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (884, '执行一次', 'Execute once');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (885, '放弃执行', 'Abandon execution');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (886, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (887, '失败', 'failure');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (888, '毫秒', 'millisecond');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (889, '秒', 'second');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (890, '分', 'minute');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (891, '小时', 'hour');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (892, '天', 'day');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (893, '属性', 'property');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (894, '功能', 'function');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (895, '事件', 'event');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (896, '设备上线', 'device online');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (897, '设备下线', 'device offline');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (898, 'Http桥接GET', 'Http bridge GET');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (899, 'Http桥接PUT', 'Http bridge PUT');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (900, 'Http桥接POST', 'Http bridge POST');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (901, '成功', 'success');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (902, '警告', 'warning');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (903, '错误', 'error');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (904, '运行中', 'running');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (905, '任意条件', 'Any condition');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (906, '所有条件', 'All condition');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (907, '菜单', 'menu');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (908, '字典数据', 'dict data');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (909, '字典类型', 'dict type');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (910, '物模型', 'things model');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (911, '物模型模板', 'things model template');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (912, '本机构数据权限', 'Data access rights of this dept');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (913, '本机构及以下机构数据权限', 'data access this dept and the following dept');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (914, '仅本人数据权限', 'Only my data permissions');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (915, '终端', 'terminal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (916, '机构', 'dept');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (214, '关联场景状态', 'scene_status', 0, 'admin', '2026-01-20 15:08:58', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (215, '场景触发器源', 'scene_trigger_source', 0, 'admin', '2026-01-20 15:11:49', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (216, '场景操作符', 'scene_operator', 0, 'admin', '2026-01-20 15:15:25', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (217, '操作符', 'operator', 0, 'admin', '2026-01-20 15:19:33', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (218, '报表聚合单位', 'report_aggregation_unit', 0, 'admin', '2026-01-20 15:31:20', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (219, '报表时间周期', 'report_time_period', 0, 'admin', '2026-01-20 15:35:19', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (220, '报表数据类型', 'report_data_type', 0, 'admin', '2026-01-20 15:38:00', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (221, '报表生成周期', 'report_generation_cycle', 0, 'admin', '2026-01-20 15:40:22', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (222, '报表状态', 'report_status', 0, 'admin', '2026-01-20 15:42:16', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (223, '数据库类型', 'database_type', 0, 'admin', '2026-01-20 15:46:18', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (225, '设备模式', 'device_mode', 0, 'admin', '2026-01-20 15:53:29', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (226, '菜单类型', 'menu_type', 0, 'admin', '2026-01-20 15:58:50', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (214, '关联场景状态', 'scene status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (215, '场景触发器源', 'scene trigger source');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (216, '场景操作符', 'scene operator');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (217, '操作符', 'operator');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (218, '报表聚合单位', 'Report aggregation unit');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (219, '报表时间周期', 'Report time period');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (220, '报表数据类型', 'Report data type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (221, '报表生成周期', 'Report generation cycle');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (222, '报表状态', 'report status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (223, '数据库类型', 'database type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (225, '设备模式', 'device mode');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (226, '菜单类型', 'menu type');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (917, 0, '启用', '1', 'scene_status', NULL, 'success', 'N', 0, 'admin', '2026-01-20 15:09:53', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (918, 0, '暂停', '2', 'scene_status', NULL, 'danger', 'N', 0, 'admin', '2026-01-20 15:10:12', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (919, 0, '设备触发', '1', 'scene_trigger_source', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:12:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (920, 0, '定时触发', '2', 'scene_trigger_source', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:12:59', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (921, 0, '产品触发', '3', 'scene_trigger_source', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:13:21', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (922, 0, '自定义触发', '4', 'scene_trigger_source', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:14:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (923, 0, '一键触发', '10', 'scene_trigger_source', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:14:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (924, 0, '设备执行', '1', 'scene_operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:16:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (925, 0, '产品执行', '3', 'scene_operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:17:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (926, 0, '告警执行', '4', 'scene_operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:17:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (927, 0, '告警恢复', '5', 'scene_operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:17:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (928, 0, '等于（=）', '=', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:21:07', 'admin', '2026-01-20 15:23:56', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (929, 0, '不等于（!=）', '!=', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:21:45', 'admin', '2026-01-20 15:24:20', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (930, 0, '大于（>）', '>', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:23:42', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (931, 0, '小于（<）', '<', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:25:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (932, 0, '大于等于（>=）', '>=', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:26:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (933, 0, '小于等于（<=）', '<=', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:27:02', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (934, 0, '在...之间（between）', 'between', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:28:21', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (935, 0, '不在...之间（notBetween）', 'notBetween', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:29:07', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (936, 0, '包含（contain）', 'contain', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:29:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (937, 0, '不包含（notContain）', 'notContain', 'operator', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:30:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (938, 0, '分钟', '1', 'report_aggregation_unit', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:33:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (939, 0, '小时', '2', 'report_aggregation_unit', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:33:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (940, 0, '天', '3', 'report_aggregation_unit', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:33:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (941, 0, '月', '4', 'report_aggregation_unit', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:34:06', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (942, 0, '周期计算', '1', 'report_time_period', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:36:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (943, 0, '固定时间', '3', 'report_time_period', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:37:02', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (944, 0, '历史数据', '1', 'report_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:38:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (945, 0, '聚合数据', '2', 'report_data_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:39:17', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (946, 0, '天', 'day', 'report_generation_cycle', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:41:01', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (947, 0, '周', 'week', 'report_generation_cycle', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:41:20', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (948, 0, '月', 'month', 'report_generation_cycle', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:41:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (949, 0, '正常', '0', 'report_status', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:43:01', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (950, 0, '异常', '1', 'report_status', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:43:37', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (951, 0, '关系型数据库', '1', 'database_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:48:36', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (952, 0, '敬请期待', '2', 'database_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:49:33', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (956, 0, '影子模式', '1', 'device_mode', NULL, 'primary', 'N', 0, 'admin', '2026-01-20 15:54:37', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (957, 0, '在线模式', '2', 'device_mode', NULL, 'success', 'N', 0, 'admin', '2026-01-20 15:55:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (958, 0, '其他信息', '3', 'device_mode', NULL, 'info', 'N', 0, 'admin', '2026-01-20 15:58:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (959, 0, '目录', 'M', 'menu_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 15:59:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (960, 0, '菜单', 'C', 'menu_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 16:00:07', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (961, 0, '按钮', 'F', 'menu_type', NULL, 'default', 'N', 0, 'admin', '2026-01-20 16:00:26', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (917, '启用', 'enable');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (918, '暂停', 'pause');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (919, '设备触发', 'device trigger');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (920, '定时触发', 'timed trigger');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (921, '产品触发', 'product trigger');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (922, '自定义触发', 'custom trigger');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (923, '一键触发', 'one-click trigger');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (924, '设备执行', 'device operation');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (925, '产品执行', 'product operation');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (926, '告警执行', 'alert operation');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (927, '告警恢复', 'alert recover');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (928, '等于（=）', 'equal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (929, '不等于（!=）', 'not equal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (930, '大于（>）', 'greater than');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (931, '小于（<）', 'less than');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (932, '大于等于（>=）', 'greater than or equal ');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (933, '小于等于（<=）', 'less than or equal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (934, '在...之间（between）', 'between');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (935, '不在...之间（notBetween）', 'notBetween');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (936, '包含（contain）', 'contain');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (937, '不包含（notContain）', 'notContain');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (938, '分钟', 'minute');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (939, '小时', 'hour');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (940, '天', 'day');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (941, '月', 'month');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (942, '周期计算', 'Cycle calculation');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (943, '固定时间', 'fixed time');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (944, '历史数据', 'history data');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (945, '聚合数据', 'combined data');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (946, '天', 'day');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (947, '周', 'week');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (948, '月', 'month');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (949, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (950, '异常', 'abnormal');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (951, '关系型数据库', 'Relational database');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (952, '敬请期待', 'Stay tuned');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (956, '影子模式', 'shadow mode');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (957, '在线模式', 'online mode');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (958, '其他信息', 'else info');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (959, '目录', 'directory');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (960, '菜单', 'menu');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (961, '按钮', 'button');


INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (962, 0, '视频（MP4）', '5', 'databoard_card_type', NULL, 'default', 'N', 0, 'admin', '2026-01-22 10:03:01', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (963, 0, '设备', '1', 'data_dimension', NULL, 'default', 'N', 0, 'admin', '2026-01-22 10:12:22', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (964, 0, '场景', '2', 'data_dimension', NULL, 'default', 'N', 0, 'admin', '2026-01-22 10:12:33', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (962, '视频（MP4）', 'video');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (963, '设备', 'device');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (964, '场景', 'scene');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (227, '数据维度', 'data_dimension', 0, 'admin', '2026-01-22 10:11:36', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (227, '数据维度', 'data dimension');

update sys_menu set icon = 'monitor_solid' where menu_id = 2;
update sys_menu set icon = 'tree_table' where menu_id = 102;
update sys_menu set icon = 'time' where menu_id = 110;
update sys_menu set icon = 'log' where menu_id = 111;
update sys_menu set icon = 'notify_log' where menu_id = 113;
update sys_menu set icon = 'list' where menu_id = 124;
update sys_menu set icon = 'monitor_solid' where menu_id = 2149;
update sys_menu set icon = 'split_screen' where menu_id = 3047;
update sys_menu set icon = 'server' where menu_id = 3049;
update sys_menu set icon = 'monitor_outline' where menu_id = 3052;
update sys_menu set icon = 'monitor_solid' where menu_id = 3100;
update sys_menu set icon = 'scada' where menu_id = 3160;
update sys_menu set icon = 'bar_chart' where menu_id = 3166;
update sys_menu set icon = '3d' where menu_id = 3178;
update sys_menu set icon = 'coverage' where menu_id = 3184;
update sys_menu set icon = 'tree_table' where menu_id = 3255;
update sys_menu set icon = 'terminal_user' where menu_id = 3256;
update sys_menu set icon = 'list' where menu_id = 3317;
update sys_menu set icon = 'bar_chart' where menu_id = 3343;
update sys_menu set icon = 'custom' where menu_id = 3344;
update sys_menu set icon = 'event' where menu_id = 3374;
update sys_menu set icon = 'subscribe' where menu_id = 3384;
update sys_menu set icon = 'version' where menu_id = 3412;
update sys_menu set icon = 'edit' where menu_id = 3418;

update iot_rule_el set enable = 2 where enable = 0;

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (228, '通知HTTP服务商', 'notify_channel_http_provider', 0, 'admin', '2026-02-03 10:08:38', 'admin', '2026-02-03 10:09:27', NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (228, '通知HTTP服务商', 'Notify the HTTP service provider');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (965, 7, 'HTTP', 'http', 'notify_channel_type', NULL, 'default', 'N', 0, 'admin', '2026-02-03 10:09:12', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (966, 0, '公共HTTP', 'http', 'notify_channel_http_provider', NULL, 'default', 'N', 0, 'admin', '2026-02-03 10:10:08', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (965, 'HTTP', 'http');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (966, '公共HTTP', 'Public HTTP');

ALTER TABLE `sys_client`
    MODIFY COLUMN `remark` varchar(3096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注';

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (180, '设备维保类型', 'iot_device_maintenance_type', 0, 'admin', '2025-12-25 17:07:11', 'admin', '2025-12-25 17:09:23', NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (181, '设备维保状态', 'iot_device_maintenance_status', 0, 'admin', '2025-12-25 17:09:12', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (180, '设备维保类型', 'Equipment maintenance type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (181, '设备维保状态', 'Equipment maintenance status');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (802, 0, '保内维保', '1', 'iot_device_maintenance_type', NULL, 'default', 'N', 0, 'admin', '2025-12-25 17:07:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (803, 1, '保外维保', '2', 'iot_device_maintenance_type', NULL, 'default', 'N', 0, 'admin', '2025-12-25 17:08:19', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (804, 0, '未开始', '0', 'iot_device_maintenance_status', NULL, 'default', 'N', 0, 'admin', '2025-12-25 17:10:22', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (805, 1, '维保中', '1', 'iot_device_maintenance_status', NULL, 'default', 'N', 0, 'admin', '2025-12-25 17:12:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (806, 2, '已停止', '2', 'iot_device_maintenance_status', NULL, 'default', 'N', 0, 'admin', '2025-12-25 17:13:03', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (802, '保内维保', 'Maintenance within the warranty');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (803, '保外维保', 'Out of warranty maintenance');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (804, '未开始', 'Not started');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (805, '维保中', 'Under maintenance');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (806, '已停止', 'Stopped');

ALTER TABLE `sys_config`
    ADD COLUMN `is_encryption` tinyint(4) NULL DEFAULT 0 COMMENT '是否加密：0-不加密，1-加密';

INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `is_encryption`) VALUES (8, '百度地图KEY', 'sys.env.baidumap.key', '', 'Y', 'admin', NULL, 'admin', NULL, NULL, 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `is_encryption`) VALUES (9, '天地图KEY', 'sys.env.tianmap.key', '', 'Y', 'admin', NULL, 'admin', NULL, NULL, 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `is_encryption`) VALUES (10, '谷歌地图KEY', 'sys.env.googlemap.key', '', 'Y', 'admin', NULL, 'admin', NULL, NULL, 0);
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `is_encryption`) VALUES (11, '心知天气KEY', 'sys.env.weather.key', '', 'Y', 'admin', NULL, 'admin', NULL, NULL, 0);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `target`) VALUES (3441, '引入包白名单', 3055, 6, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:script:allowpackage', '#', 'admin', '2026-03-25 14:09:39', '', NULL, '', 0);
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3441, '引入包白名单', 'package whitelist');

ALTER TABLE `sip_device_channel`
    MODIFY COLUMN `play_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道播放地址';

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (229, '菜单打开方式', 'sys_menu_target', 0, 'admin', '2026-04-03 10:06:39', '', NULL, NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (229, '菜单打开方式', 'MenutTarget');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (967, 0, '未处理', '2', 'alarm_handling', NULL, 'default', 'N', 0, 'admin', '2026-02-05 10:18:42', 'admin', '2026-02-05 10:18:49', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (968, 0, '内签', '0', 'sys_menu_target', NULL, 'default', 'N', 0, 'admin', '2026-04-03 10:07:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (969, 0, '新窗口', '1', 'sys_menu_target', NULL, 'default', 'N', 0, 'admin', '2026-04-03 10:07:33', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (967, '未处理', 'Unprocessed');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (968, '内签', 'Internal Signature');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (969, '新窗口', 'New Window');
