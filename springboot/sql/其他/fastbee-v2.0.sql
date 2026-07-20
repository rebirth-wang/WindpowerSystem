/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云数据库
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : kerwincui.mysql.rds.aliyuncs.com:3306
 Source Schema         : fastbee

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 16/04/2023 14:22:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for iot_alert
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert`;
CREATE TABLE `iot_alert`  (
  `alert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警ID',
  `alert_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警名称',
  `alert_level` tinyint(11) NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `actions` json NOT NULL COMMENT '执行动作',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '告警状态（1-启动，2-停止）',
  `message_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息通知类型[1,2,3]，1=设备告警，2=短信通知，3=移动端推送',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`alert_id`) USING BTREE,
  INDEX `iot_alert_index_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_alert
-- ----------------------------

-- ----------------------------
-- Table structure for iot_alert_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_log`;
CREATE TABLE `iot_alert_log` (
`alert_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警ID',
`alert_name` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '告警名称',
`alert_level` tinyint(11) NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
`status` tinyint(11) NOT NULL COMMENT '处理状态(1=不需要处理,2=未处理,3=已处理)',
`serial_number` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '设备编号',
`product_id` bigint(20) NOT NULL COMMENT '产品ID',
`detail` json DEFAULT NULL COMMENT '告警详情（对应物模型）',
`create_by` varchar(64) CHARACTER SET utf8 DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8 DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
`device_name` varchar(255) DEFAULT NULL COMMENT '设备名称',
PRIMARY KEY (`alert_log_id`) USING BTREE,
KEY `iot_alert_log_index_serial_number` (`serial_number`) USING BTREE,
KEY `iot_alert_log_index_product_id` (`product_id`) USING BTREE,
KEY `iot_alert_log_index_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备告警日志';
-- ----------------------------
-- Records of iot_alert_log
-- ----------------------------

-- ----------------------------
-- Table structure for iot_alert_trigger
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_trigger`;
CREATE TABLE `iot_alert_trigger`  (
  `alert_trigger_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警触发器ID',
  `alert_id` bigint(20) NOT NULL COMMENT '场景ID',
  `status` tinyint(1) NOT NULL COMMENT '告警状态（1-启动，2-停止）',
  `source` tinyint(1) NOT NULL COMMENT '触发源（1=设备触发，2=定时触发）',
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型标识符',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型值',
  `operator` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作符',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `job_id` bigint(20) NULL DEFAULT NULL COMMENT '任务ID',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `is_advance` tinyint(1) NULL DEFAULT NULL COMMENT '是否详细corn表达式（1=是，0=否）',
  PRIMARY KEY (`alert_trigger_id`) USING BTREE,
  INDEX `iot_alert_trigger_index_alert_id`(`alert_id`) USING BTREE,
  INDEX `iot_alert_trigger_index_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 196 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '告警触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_alert_trigger
-- ----------------------------

-- ----------------------------
-- Table structure for iot_category
-- ----------------------------
DROP TABLE IF EXISTS `iot_category`;
CREATE TABLE `iot_category`  (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品分类ID',
  `category_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品分类名称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `is_sys` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父级ID',
  `order_num` int(4) NULL DEFAULT NULL COMMENT '显示顺序',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `iot_category_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_category_index_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_category
-- ----------------------------
INSERT INTO `iot_category` VALUES (1, '电工照明', 1, 'admin', 1, 0, 1, '0', '', '2022-03-01 11:44:37', '', '2023-04-10 01:12:48', '例如：通断器、开关、插座、窗帘、灯');
INSERT INTO `iot_category` VALUES (2, '家居安防', 1, 'admin', 1, 0, 2, '0', '', '2021-12-18 14:46:52', '', '2021-12-18 14:49:48', '例如：智能门锁、摄像头、智能窗帘');
INSERT INTO `iot_category` VALUES (3, '环境电器', 1, 'admin', 1, 0, 3, '0', '', '2021-12-18 14:50:24', '', '2023-04-10 01:12:53', '例如：加湿器、风扇、扫地机器人');
INSERT INTO `iot_category` VALUES (4, '大家电', 1, 'admin', 1, 0, 4, '0', '', '2021-12-18 14:50:58', '', '2021-12-18 14:52:30', '例如：冰箱、热水器、电视');
INSERT INTO `iot_category` VALUES (5, '厨房电器', 1, 'admin', 1, 0, 5, '0', '', '2021-12-18 14:51:42', '', '2021-12-18 14:52:35', '例如：油烟机、烤箱、电饭煲');
INSERT INTO `iot_category` VALUES (6, '个护健康', 1, 'admin', 1, 0, 6, '0', '', '2021-12-18 14:52:15', '', '2021-12-18 14:52:40', '例如：洗衣机、按摩椅');
INSERT INTO `iot_category` VALUES (7, '其他', 1, 'admin', 1, 0, 7, '0', '', '2021-12-18 14:52:54', '', '2021-12-20 15:04:33', '其他');

-- ----------------------------
-- Table structure for iot_device
-- ----------------------------
DROP TABLE IF EXISTS `iot_device`;
CREATE TABLE `iot_device`  (
  `device_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `gw_dev_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子设备网关编号',
  `firmware_version` float(11, 2) NOT NULL COMMENT '固件版本',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
  `rssi` tinyint(11) NULL DEFAULT NULL COMMENT '信号强度（\r\n信号极好4格[-55— 0]，\r\n信号好3格[-70— -55]，\r\n信号一般2格[-85— -70]，\r\n信号差1格[-100— -85]）',
  `is_shadow` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用设备影子(0=禁用，1=启用)',
  `location_way` tinyint(1) NULL DEFAULT NULL COMMENT '定位方式(1=ip自动定位，2=设备定位，3=自定义)',
  `things_model_value` json NULL COMMENT '物模型值',
  `network_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备所在地址',
  `network_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备入网IP',
  `longitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备经度',
  `latitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备纬度',
  `active_time` datetime(0) NULL DEFAULT NULL COMMENT '激活时间',
  `summary` json NULL COMMENT '设备摘要，格式[{\"name\":\"device\"},{\"chip\":\"esp8266\"}]',
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_simulate` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '是否是模拟设备',
  `slave_id` int(10) NULL DEFAULT NULL COMMENT '从机id',
  PRIMARY KEY (`device_id`) USING BTREE,
  UNIQUE INDEX `iot_device_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_device_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_device_index_tanant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_device_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_index_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 203 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device
-- ----------------------------
INSERT INTO `iot_device` VALUES (108, '★温湿度开关', 41, '智能开关', 1, 'admin', 1, 'admin', 'D1ELV3A5TOJS', NULL, 1.00, 4, -51, 1, 1, NULL, '广东省佛山市顺德区 电信', '61.145.97.26', 113.128512, 23.027759, '2023-02-26 01:11:17', '{\"chip\": \"esp8266\", \"name\": \"wumei-smart\", \"author\": \"kerwincui\", \"create\": \"2022-06-06\", \"version\": 1.6}', NULL, '0', '', '2025-02-25 23:15:56', '', '2023-04-10 14:58:48', NULL, NULL, NULL);
INSERT INTO `iot_device` VALUES (109, '★网关设备', 96, '网关产品', 1, 'admin', 1, 'admin', 'D1PGLPG58KZ2', NULL, 1.00, 3, -46, 1, 3, '[{\"id\": \"temperature\", \"name\": \"空气温度\", \"value\": \"18.05\", \"shadow\": \"18.05\", \"isMonitor\": 1}, {\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"value\": \"3913,3012,4964,5098,3275\", \"shadow\": \"3913,3012,4964,5098,3275\", \"isMonitor\": 1}, {\"id\": \"category_switch\", \"name\": \"设备开关\", \"value\": \"1\", \"shadow\": \"1\", \"isMonitor\": 0}, {\"id\": \"category_temperature\", \"name\": \"空气温度-只读\", \"value\": \"20\", \"shadow\": \"20\", \"isMonitor\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"value\": \" 100, 50, 80\", \"shadow\": \" 100, 50, 80\", \"isMonitor\": 0}, {\"id\": \"category_report_monitor\", \"name\": \"上报监测数据\", \"value\": \"5\", \"shadow\": \"5\", \"isMonitor\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"value\": \"1\", \"shadow\": \"1\", \"isMonitor\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"value\": \" 5, 3, 3, 5, 5\", \"shadow\": \" 5, 3, 3, 5, 5\", \"isMonitor\": 0}, {\"id\": \"category_humidity\", \"name\": \"空气湿度\", \"value\": \"32\", \"shadow\": \"32\", \"isMonitor\": 1}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"value\": \"1, ,1, , \", \"shadow\": \"1, ,1, , \", \"isMonitor\": 0}, {\"id\": \"category_light\", \"name\": \"光照\", \"value\": \"64\", \"shadow\": \"64\", \"isMonitor\": 1}, {\"id\": \"category_gear\", \"name\": \"运行档位\", \"value\": \"1\", \"shadow\": \"1\", \"isMonitor\": 0}, {\"id\": \"report_monitor\", \"name\": \"上报监测数据\", \"value\": \"3\", \"shadow\": \"3\", \"isMonitor\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"value\": \"3\", \"shadow\": \"3\", \"isMonitor\": 0}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"value\": \"1,0,0, ,1\", \"shadow\": \"1,0,0,1,1\", \"isMonitor\": 0}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"value\": \"56,68,33,26,27\", \"shadow\": \"56,68,33,26,27\", \"isMonitor\": 0}, {\"id\": \"brightness\", \"name\": \"室内亮度\", \"value\": \"5430\", \"shadow\": \"5430\", \"isMonitor\": 1}, {\"id\": \"reset\", \"name\": \"设备重启\", \"value\": \"restart\", \"shadow\": \"restart\", \"isMonitor\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"value\": \"FastBee\", \"shadow\": \"FastBee\", \"isMonitor\": 0}]', '云南省曲靖市 移通', '183.225.206.92', 104.802435, 26.496407, '2023-02-26 00:00:00', '{\"chip\": \"esp8266\", \"name\": \"wumei-smart\", \"author\": \"kerwincui\", \"create\": \"2022-06-06\", \"version\": 1.6}', NULL, '0', '', '2025-02-25 23:17:31', '', '2023-04-16 02:39:45', NULL, NULL, NULL);
INSERT INTO `iot_device` VALUES (118, '★MODBUS网关设备', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C', NULL, 1.00, 4, 0, 0, 1, '[]', '云南省曲靖市 移通', '39.130.41.42', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2025-02-28 16:49:17', '', '2023-04-10 13:59:06', NULL, NULL, 1);
INSERT INTO `iot_device` VALUES (119, '漏水器', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_1', 'D4AD203F3A1C', 1.00, 4, 0, 0, 1, NULL, '云南省曲靖市 移通', '39.130.41.42', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-04-10 13:59:06', NULL, NULL, 1);
INSERT INTO `iot_device` VALUES (120, '温湿度计', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_2', 'D4AD203F3A1C', 1.00, 3, 0, 0, 1, NULL, '云南省曲靖市 移通', '39.130.41.42', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-04-10 13:59:06', NULL, NULL, 2);
INSERT INTO `iot_device` VALUES (121, '电量仪', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_11', 'D4AD203F3A1C', 1.00, 3, 0, 0, 1, NULL, '云南省曲靖市 移通', '39.130.41.42', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-04-10 13:59:06', NULL, NULL, 11);
INSERT INTO `iot_device` VALUES (135, '★MODBUS模拟设备', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D1WP03K5A675', NULL, 1.00, 4, 0, 0, 1, NULL, '山东省济南市 联通ADSL', '221.0.13.221', 117.126399, 36.656554, NULL, NULL, NULL, '0', '', '2023-04-07 20:08:24', '', '2023-04-12 17:28:19', NULL, 1, NULL);
INSERT INTO `iot_device` VALUES (136, '漏水器', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D1WP03K5A675_1', 'D1WP03K5A675', 1.00, 4, 0, 0, 1, NULL, '山东省济南市 联通ADSL', '221.0.13.221', 117.126399, 36.656554, NULL, NULL, NULL, '0', '', '2023-04-07 20:08:24', '', NULL, NULL, 1, 1);
INSERT INTO `iot_device` VALUES (137, '温湿度计', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D1WP03K5A675_2', 'D1WP03K5A675', 1.00, 4, 0, 0, 1, NULL, '山东省济南市 联通ADSL', '221.0.13.221', 117.126399, 36.656554, NULL, NULL, NULL, '0', '', '2023-04-07 20:08:24', '', NULL, NULL, 1, 2);
INSERT INTO `iot_device` VALUES (138, '电量仪', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D1WP03K5A675_11', 'D1WP03K5A675', 1.00, 4, 0, 0, 1, NULL, '山东省济南市 联通ADSL', '221.0.13.221', 117.126399, 36.656554, NULL, NULL, NULL, '0', '', '2023-04-07 20:08:24', '', NULL, NULL, 1, 11);
INSERT INTO `iot_device` VALUES (140, '★视频监控', 118, 'iDS-2DE2402IX-D3/W/XM', 1, 'admin', 1, 'admin', '11010200001320000001', NULL, 1.00, 4, 0, 0, 1, NULL, ' 局域网', '192.168.2.119', NULL, NULL, '2023-04-11 21:14:16', '{\"port\": 5060, \"firmware\": \"V5.7.4\", \"transport\": \"UDP\", \"streammode\": \"UDP\", \"hostaddress\": \"192.168.2.119:5060\", \"manufacturer\": \"Hikvision\"}', NULL, '0', '', '2023-04-11 21:12:35', '', '2023-04-11 22:11:01', NULL, 0, NULL);

-- ----------------------------
-- Table structure for iot_device_group
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_group`;
CREATE TABLE `iot_device_group`  (
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `group_id` bigint(20) NOT NULL COMMENT '分组ID',
  PRIMARY KEY (`device_id`, `group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device_group
-- ----------------------------
INSERT INTO `iot_device_group` VALUES (108, 2);
INSERT INTO `iot_device_group` VALUES (108, 6);
INSERT INTO `iot_device_group` VALUES (109, 2);
INSERT INTO `iot_device_group` VALUES (109, 6);
INSERT INTO `iot_device_group` VALUES (118, 2);
INSERT INTO `iot_device_group` VALUES (118, 5);

-- ----------------------------
-- Table structure for iot_device_job
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_job`;
CREATE TABLE `iot_device_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `device_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `is_advance` tinyint(1) NULL DEFAULT NULL COMMENT '是否详细corn表达式（1=是，0=否）',
  `actions` json NULL COMMENT '执行的动作集合',
  `job_type` tinyint(1) NULL DEFAULT NULL COMMENT '任务类型（1=设备定时，2=设备告警，3=场景联动）',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `scene_id` bigint(20) NULL DEFAULT NULL COMMENT '场景联动ID',
  `alert_id` bigint(20) NULL DEFAULT NULL COMMENT '告警ID',
  `alert_trigger` json NULL COMMENT '定时告警触发器',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE,
  INDEX `iot_device_job_index_device_id`(`device_id`) USING BTREE,
  INDEX `iot_device_job_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_device_job_index_scene_id`(`scene_id`) USING BTREE,
  INDEX `iot_device_job_index_alert_id`(`alert_id`) USING BTREE,
  INDEX `iot_device_job_index_serial_number`(`serial_number`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备定时' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device_job
-- ----------------------------
INSERT INTO `iot_device_job` VALUES (3, '场景联动定时触发', 'DEFAULT', '0 02 11 ? * 1,2,3,4,5,6,7', '2', '1', '0', '', '2023-04-15 11:04:36', '', NULL, '', NULL, NULL, '场景联动定时触发', 0, '[{\"id\": \"irc\", \"name\": \"射频遥控\", \"type\": 2, \"value\": \"FFXX04\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"value\": \"1\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"value\": \"str\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"report_monitor\", \"name\": \"上报数据\", \"type\": 2, \"value\": \"2\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"value\": \"restart\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}]', 3, NULL, NULL, 72, NULL, NULL);
INSERT INTO `iot_device_job` VALUES (4, 'P', 'DEFAULT', '0 08 11 ? * 1,2,3,4,5,6,7', '2', '1', '0', 'admin', '2023-04-15 11:08:37', '', NULL, '', 108, 'D1ELV3A5TOJS', '★温湿度开关', 0, '[{\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"value\": \"2\", \"deviceId\": 108, \"deviceName\": \"★温湿度开关\"}]', 1, 41, '★智能开关产品', NULL, NULL, NULL);
INSERT INTO `iot_device_job` VALUES (5, '告警定时触发', 'DEFAULT', '0 13 11 ? * 1,2,3,4,5,6,7', '2', '1', '0', '', '2023-04-15 11:14:06', '', NULL, '', NULL, NULL, '告警定时触发', 0, '[{\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"value\": \"1\", \"productId\": 96, \"productName\": \"★网关产品\"}]', 2, 96, '★网关产品', NULL, 50, '{\"id\": \"temperature\", \"name\": \"空气温度\", \"type\": 1, \"jobId\": 0, \"value\": \"1\", \"params\": {}, \"source\": 2, \"status\": 1, \"alertId\": 50, \"operator\": \"=\", \"isAdvance\": 0, \"productId\": 96, \"productName\": \"★网关产品\", \"cronExpression\": \"0 13 11 ? * 1,2,3,4,5,6,7\"}');

-- ----------------------------
-- Table structure for iot_device_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_log`;
CREATE TABLE `iot_device_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备日志ID',
  `identity` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标识符',
  `model_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `log_type` tinyint(1) NOT NULL COMMENT '类型（1=属性上报，2=调用功能，3=事件上报，4=设备升级，5=设备上线，6=设备离线）',
  `log_value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志值',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `is_monitor` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否监测数据（1=是，0=否）',
  `mode` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '模式(1=影子模式，2=在线模式，3=其他)',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户昵称',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '租户名称',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `iot_device_log_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_device_log_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_device_log_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_log_index_device_id`(`device_id`) USING BTREE,
  INDEX `index_serialNumber_createTime`(`serial_number`, `create_time`) USING BTREE,
  INDEX `index_isMonitor_serialNumber_createTime`(`serial_number`, `is_monitor`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 617765 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device_log
-- ----------------------------

-- ----------------------------
-- Table structure for iot_device_template
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_template`;
CREATE TABLE `iot_device_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品id',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '采集点模板id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备采集点模板关联对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device_template
-- ----------------------------
INSERT INTO `iot_device_template` VALUES (2, 112, 1);
INSERT INTO `iot_device_template` VALUES (3, 118, 4);
INSERT INTO `iot_device_template` VALUES (4, 120, 6);
INSERT INTO `iot_device_template` VALUES (5, 121, 1);
INSERT INTO `iot_device_template` VALUES (6, 122, 1);
INSERT INTO `iot_device_template` VALUES (7, 123, 11);
INSERT INTO `iot_device_template` VALUES (8, 125, 1);
INSERT INTO `iot_device_template` VALUES (9, 126, 12);
INSERT INTO `iot_device_template` VALUES (10, 127, 12);
INSERT INTO `iot_device_template` VALUES (11, 128, 12);
INSERT INTO `iot_device_template` VALUES (12, 129, 12);
INSERT INTO `iot_device_template` VALUES (13, 130, 12);
INSERT INTO `iot_device_template` VALUES (14, 131, 12);
INSERT INTO `iot_device_template` VALUES (15, 132, 12);
INSERT INTO `iot_device_template` VALUES (16, 133, 12);
INSERT INTO `iot_device_template` VALUES (17, 134, 12);
INSERT INTO `iot_device_template` VALUES (18, 135, 12);
INSERT INTO `iot_device_template` VALUES (19, 136, 12);
INSERT INTO `iot_device_template` VALUES (20, 137, 12);
INSERT INTO `iot_device_template` VALUES (21, 138, 12);
INSERT INTO `iot_device_template` VALUES (22, 139, 12);
INSERT INTO `iot_device_template` VALUES (23, 140, 12);
INSERT INTO `iot_device_template` VALUES (24, 141, 12);
INSERT INTO `iot_device_template` VALUES (25, 142, 12);
INSERT INTO `iot_device_template` VALUES (26, 143, 12);
INSERT INTO `iot_device_template` VALUES (27, 144, 12);
INSERT INTO `iot_device_template` VALUES (28, 145, 12);
INSERT INTO `iot_device_template` VALUES (29, 147, 12);
INSERT INTO `iot_device_template` VALUES (30, 148, 13);
INSERT INTO `iot_device_template` VALUES (31, 149, 14);
INSERT INTO `iot_device_template` VALUES (32, 150, 15);

-- ----------------------------
-- Table structure for iot_device_user
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_user`;
CREATE TABLE `iot_device_user`  (
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
  `phonenumber` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `is_owner` tinyint(11) NOT NULL COMMENT '是否为设备所有者（0=否，1=是）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`device_id`, `user_id`) USING BTREE,
  INDEX `iot_device_user_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_user_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_device_user
-- ----------------------------
INSERT INTO `iot_device_user` VALUES (108, 1, 1, 'admin', '温湿度开关', '15888888888', 'admin', 1, '0', '', '2023-02-25 23:15:57', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (109, 1, 1, 'admin', '网关设备', '15888888888', 'admin', 1, '0', '', '2023-02-25 23:17:32', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (118, 1, 1, 'admin', 'modbus测试-emq-0228', '15888888888', 'admin', 1, '0', '', '2023-02-28 16:49:18', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (135, 1, 1, 'admin', 'modbus模拟设备', '15888888888', 'admin', 1, '0', '', '2023-04-07 20:08:25', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (140, 1, 1, 'admin', '监控测试', '15888888888', 'admin', 1, '0', '', '2023-04-11 21:12:37', '', NULL, NULL);

-- ----------------------------
-- Table structure for iot_event_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_event_log`;
CREATE TABLE `iot_event_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备日志ID',
  `identity` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标识符',
  `model_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `log_type` tinyint(1) NOT NULL COMMENT '类型（3=事件上报，5=设备上线，6=设备离线）',
  `log_value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志值',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `is_monitor` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否监测数据（1=是，0=否）',
  `mode` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '模式(1=影子模式，2=在线模式，3=其他)',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户昵称',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '租户名称',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 610428 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_event_log
-- ----------------------------
INSERT INTO `iot_event_log` VALUES (524157, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 17:26:29', '设备上线');
INSERT INTO `iot_event_log` VALUES (524158, 'offline', NULL, 6, '0', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 17:28:23', '设备离线');
INSERT INTO `iot_event_log` VALUES (524159, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 22:01:30', '设备上线');
INSERT INTO `iot_event_log` VALUES (524160, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 22:16:29', '设备上线');
INSERT INTO `iot_event_log` VALUES (524161, 'height_temperature', NULL, 3, '40', 109, '★网关设备', 'D1PGLPG58KZ2', 0, 2, 1, 'admin', 1, 'admin', '', '2023-03-28 22:27:55', '温度过高警告');
INSERT INTO `iot_event_log` VALUES (524162, 'exception', NULL, 3, '异常消息，消息内容XXXXXXXX', 109, '★网关设备', 'D1PGLPG58KZ2', 0, 2, 1, 'admin', 1, 'admin', '', '2023-03-28 22:27:55', '设备发生错误');
INSERT INTO `iot_event_log` VALUES (524163, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 22:31:28', '设备上线');
INSERT INTO `iot_event_log` VALUES (524164, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 22:46:29', '设备上线');
INSERT INTO `iot_event_log` VALUES (524165, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 23:01:28', '设备上线');
INSERT INTO `iot_event_log` VALUES (524166, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-28 23:48:31', '设备上线');
INSERT INTO `iot_event_log` VALUES (524167, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 00:03:33', '设备上线');
INSERT INTO `iot_event_log` VALUES (524168, 'online', NULL, 5, '1', 125, '监控测试', '12010100001320000003', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 00:18:07', '设备上线');
INSERT INTO `iot_event_log` VALUES (524169, 'height_temperature', NULL, 3, '40', 109, '★网关设备', 'D1PGLPG58KZ2', 0, 2, 1, 'admin', 1, 'admin', '', '2023-03-29 00:27:55', '温度过高警告');
INSERT INTO `iot_event_log` VALUES (524170, 'exception', NULL, 3, '异常消息，消息内容XXXXXXXX', 109, '★网关设备', 'D1PGLPG58KZ2', 0, 2, 1, 'admin', 1, 'admin', '', '2023-03-29 00:27:55', '设备发生错误');
INSERT INTO `iot_event_log` VALUES (524177, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 16:45:54', '设备上线');
INSERT INTO `iot_event_log` VALUES (524178, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 16:54:10', '设备上线');
INSERT INTO `iot_event_log` VALUES (524179, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:23:23', '设备上线');
INSERT INTO `iot_event_log` VALUES (524180, 'offline', NULL, 6, '0', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:23:26', '设备离线');
INSERT INTO `iot_event_log` VALUES (524181, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:24:59', '设备上线');
INSERT INTO `iot_event_log` VALUES (524182, 'offline', NULL, 6, '0', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:26:30', '设备离线');
INSERT INTO `iot_event_log` VALUES (524183, 'online', NULL, 5, '1', 115, '温湿度开关0228-2', 'D1ELV3A523RG', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:29:11', '设备上线');
INSERT INTO `iot_event_log` VALUES (524184, 'online', NULL, 5, '1', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:45:58', '设备上线');
INSERT INTO `iot_event_log` VALUES (524185, 'online', NULL, 5, '1', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:51:29', '设备上线');
INSERT INTO `iot_event_log` VALUES (524186, 'offline', NULL, 6, '0', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:53:00', '设备离线');
INSERT INTO `iot_event_log` VALUES (524187, 'online', NULL, 5, '1', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 17:55:11', '设备上线');
INSERT INTO `iot_event_log` VALUES (524188, 'online', NULL, 5, '1', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 18:43:55', '设备上线');
INSERT INTO `iot_event_log` VALUES (524189, 'online', NULL, 5, '1', 118, 'modbus网关设备', 'D4AD203F3A1C', 0, 3, 1, 'admin', 1, 'admin', '', '2023-03-29 18:45:15', '设备上线');
INSERT INTO `iot_event_log` VALUES (610427, 'exception', NULL, 3, '异常消息，消息内容XXXXXXXX', 109, '★网关设备', 'D1PGLPG58KZ2', 0, 2, 1, 'admin', 1, 'admin', '', '2023-04-16 02:50:10', '设备发生错误');

-- ----------------------------
-- Table structure for iot_firmware
-- ----------------------------
DROP TABLE IF EXISTS `iot_firmware`;
CREATE TABLE `iot_firmware`  (
  `firmware_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '固件ID',
  `firmware_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '固件名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `is_sys` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
  `is_latest` tinyint(1) NOT NULL COMMENT '是否最新版本（0-否，1-是）',
  `version` float(11, 2) NOT NULL DEFAULT 0.10 COMMENT '固件版本',
  `file_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '文件路径',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`firmware_id`) USING BTREE,
  INDEX `iot_firmware_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_firmware_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品固件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_firmware
-- ----------------------------
INSERT INTO `iot_firmware` VALUES (1, '智能开关固件', 41, '★智能开关', 6, 'wumei', 1, 1, 3.00, '/profile/iot/1/2023-0226-143855.bin', '0', '', '2023-02-24 00:38:26', '', '2023-02-28 10:21:25', '测试固件');

-- ----------------------------
-- Table structure for iot_firmware_task
-- ----------------------------
DROP TABLE IF EXISTS `iot_firmware_task`;
CREATE TABLE `iot_firmware_task`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `firmware_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联固件ID',
  `upgrade_type` int(11) NOT NULL DEFAULT 1 COMMENT '1:指定设备 2:产品级别',
  `task_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `device_amount` int(11) NOT NULL DEFAULT 0 COMMENT '选中的设备总数',
  `del_flag` int(11) NOT NULL DEFAULT 0,
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `book_time` timestamp(0) NULL DEFAULT NULL COMMENT '预定时间升级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '固件升级任务对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_firmware_task
-- ----------------------------
INSERT INTO `iot_firmware_task` VALUES (1, '测试任务0227', 1, 2, '测试', 1, 0, '2023-02-27 16:23:21', '2023-02-27 16:23:21', '2023-02-27 16:23:18');
INSERT INTO `iot_firmware_task` VALUES (2, '测试任务227', 1, 2, '1', 1, 0, '2023-02-28 10:04:33', '2023-02-27 16:58:04', '2023-02-27 16:58:01');

-- ----------------------------
-- Table structure for iot_firmware_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `iot_firmware_task_detail`;
CREATE TABLE `iot_firmware_task_detail`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `serial_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设备编码',
  `upgrade_status` int(11) NOT NULL DEFAULT 0 COMMENT '0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败',
  `detail_msg` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `message_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '消息ID',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '固件升级任务详细对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_firmware_task_detail
-- ----------------------------
INSERT INTO `iot_firmware_task_detail` VALUES (1, 1, 'D1ELV3A5TOJS', 0, '等待升级-未推送固件到设备', '2023-02-27 16:24:15', '', '2023-02-28 10:34:33');
INSERT INTO `iot_firmware_task_detail` VALUES (2, 2, 'D1ELV3A5TOJH', 3, '升级成功', '2023-02-27 16:58:17', '1677550503', '2023-02-28 10:34:51');
INSERT INTO `iot_firmware_task_detail` VALUES (3, 3, 'D1ELV3A5TOJH', 1, '已发送', '2023-02-28 10:09:49', '1677550501', '2023-02-28 10:34:38');
INSERT INTO `iot_firmware_task_detail` VALUES (4, 3, 'D1ELV3A5TORG', 2, '设备OTA升级中', '2023-02-28 10:09:56', '1677550502', '2023-02-28 10:34:40');
INSERT INTO `iot_firmware_task_detail` VALUES (5, 4, 'D1ELV3A5TOJS', 3, '升级成功', '2023-02-28 10:11:30', '1677550504', '2023-02-28 10:34:48');
INSERT INTO `iot_firmware_task_detail` VALUES (6, 4, 'D1ELV3A523RG', 0, '等待升级-未推送固件到设备', '2023-02-28 10:11:34', '', '2023-02-28 10:34:31');
INSERT INTO `iot_firmware_task_detail` VALUES (7, 4, 'D1ELV3A576RG', 2, '设备OTA升级中', '2023-02-28 10:11:40', '1677550505', '2023-02-28 10:34:06');
INSERT INTO `iot_firmware_task_detail` VALUES (11, 5, 'D1ELV3A562RG', 4, '失败', '2023-02-28 10:12:14', '1677550509', '2023-02-28 10:33:53');
INSERT INTO `iot_firmware_task_detail` VALUES (12, 9, 'D1ELV3A576RG', 1, '已发送', '2023-02-28 10:28:33', NULL, '2023-02-28 10:28:33');
INSERT INTO `iot_firmware_task_detail` VALUES (17, 9, 'D1ELV3A5TORG', 5, '停止', '2023-02-28 10:28:33', NULL, '2023-02-28 10:33:49');
INSERT INTO `iot_firmware_task_detail` VALUES (18, 10, 'D1ELV3A562RG', 0, '等待升级', '2023-03-29 00:29:23', NULL, '2023-03-29 00:30:23');

-- ----------------------------
-- Table structure for iot_function_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_function_log`;
CREATE TABLE `iot_function_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `identify` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '标识符',
  `fun_type` int(2) NOT NULL COMMENT '1==服务下发，2=属性获取，3.OTA升级',
  `fun_value` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '日志值',
  `message_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '消息id',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '设备编号',
  `mode` int(2) NULL DEFAULT NULL COMMENT '模式(1=影子模式，2=在线模式，3=其他)',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `result_msg` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '下发结果描述',
  `result_code` int(3) NULL DEFAULT NULL COMMENT '下发结果代码',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `show_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '显示值',
  `model_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `reply_time` datetime(0) NULL DEFAULT NULL COMMENT '设备回复时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `iot_function_log_id_uindex`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13577 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备服务下发日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_function_log
-- ----------------------------
INSERT INTO `iot_function_log` VALUES (1, '起始地址:0', 2, '设备编号:2', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:13', NULL, NULL, '读取个数:2', NULL);
INSERT INTO `iot_function_log` VALUES (2, '起始地址:0', 2, '设备编号:1', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:13', NULL, NULL, '读取个数:1', NULL);
INSERT INTO `iot_function_log` VALUES (3, '起始地址:0', 2, '设备编号:11', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:13', NULL, NULL, '读取个数:20', NULL);
INSERT INTO `iot_function_log` VALUES (4, '起始地址:20', 2, '设备编号:11', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:16', NULL, NULL, '读取个数:20', NULL);
INSERT INTO `iot_function_log` VALUES (5, '起始地址:40', 2, '设备编号:11', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:19', NULL, NULL, '读取个数:20', NULL);
INSERT INTO `iot_function_log` VALUES (6, '起始地址:768', 2, '设备编号:11', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:22', NULL, NULL, '读取个数:20', NULL);
INSERT INTO `iot_function_log` VALUES (7, '起始地址:789', 2, '设备编号:11', NULL, NULL, 'D4AD203F3A1C', NULL, NULL, '服务下发成功', 203, NULL, '2023-03-29 00:32:25', NULL, NULL, '读取个数:20', NULL);
INSERT INTO `iot_function_log` VALUES (13573, 'irc', 1, 'FFXX01', NULL, NULL, 'D1ELV3A5TOJS', NULL, NULL, NULL, NULL, NULL, '2023-04-15 11:16:58', NULL, 'FFXX01', '射频遥控', NULL);
INSERT INTO `iot_function_log` VALUES (13574, 'switch', 1, '1', NULL, NULL, 'D1PGLPG58KZ2', NULL, NULL, NULL, NULL, NULL, '2023-04-16 02:39:46', NULL, '1', '设备开关', NULL);
INSERT INTO `iot_function_log` VALUES (13575, 'switch', 1, '0', NULL, NULL, 'D1PGLPG58KZ2', NULL, NULL, NULL, NULL, NULL, '2023-04-16 02:40:14', NULL, '0', '设备开关', NULL);
INSERT INTO `iot_function_log` VALUES (13576, 'switch', 1, '1', NULL, NULL, 'D1PGLPG58KZ2', NULL, NULL, NULL, NULL, NULL, '2023-04-16 02:40:15', NULL, '1', '设备开关', NULL);

-- ----------------------------
-- Table structure for iot_goview_project
-- ----------------------------
DROP TABLE IF EXISTS `iot_goview_project`;
CREATE TABLE `iot_goview_project`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `state` int(1) NOT NULL DEFAULT 0 COMMENT '项目状态[0未发布,1发布]',
  `index_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首页图片',
  `del_flag` int(11) NOT NULL DEFAULT 0 COMMENT '删除状态[1删除,-1未删除]',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目介绍',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_goview_project
-- ----------------------------
INSERT INTO `iot_goview_project` VALUES ('114ab733f2b94d44898919a96abe583b', '4rmddu4osas000', 1, '/profile/goview/1/114ab733f2b94d44898919a96abe583b_index_preview.png', 0, '2023-02-26 14:07:36', '6', '2023-02-26 23:11:41', NULL);
INSERT INTO `iot_goview_project` VALUES ('f112e56eee4a4a0fae3e9ec9b3083eab', '3oa3qdszoug000', 0, NULL, 1, '2023-02-26 12:12:34', '6', '2023-02-26 12:12:34', NULL);

-- ----------------------------
-- Table structure for iot_goview_project_data
-- ----------------------------
DROP TABLE IF EXISTS `iot_goview_project_data`;
CREATE TABLE `iot_goview_project_data`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目id',
  `content` longblob NULL COMMENT '存储数据',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目数据关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_goview_project_data
-- ----------------------------
INSERT INTO `iot_goview_project_data` VALUES ('a2a1cbd6a1944aa4a6f332b58650dc5c', '114ab733f2b94d44898919a96abe583b', 0x7B226564697443616E766173436F6E666967223A7B227769647468223A313932302C22686569676874223A313038302C2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C2273656C656374436F6C6F72223A747275652C2263686172745468656D65436F6C6F72223A226461726B222C2263686172745468656D6553657474696E67223A7B227469746C65223A7B2273686F77223A747275652C22746578745374796C65223A7B22636F6C6F72223A2223424642464246222C22666F6E7453697A65223A31387D2C22737562746578745374796C65223A7B22636F6C6F72223A2223413241324132222C22666F6E7453697A65223A31347D7D2C227841786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A22626F74746F6D222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A66616C73652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D7D2C227941786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A226C656674222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D7D2C226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D7D2C2267726964223A7B2273686F77223A66616C73652C226C656674223A22313025222C22746F70223A223630222C227269676874223A22313025222C22626F74746F6D223A223630227D2C2264617461736574223A6E756C6C7D2C22707265766965775363616C6554797065223A22666974227D2C22636F6D706F6E656E744C697374223A5B7B226964223A2231643673376D35746B6170733030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A36302C2279223A36312C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A22426172436F6D6D6F6E222C226368617274436F6E666967223A7B226B6579223A22426172436F6D6D6F6E222C2263686172744B6579223A2256426172436F6D6D6F6E222C22636F6E4B6579223A225643426172436F6D6D6F6E222C227469746C65223A22E69FB1E78AB6E59BBE222C2263617465676F7279223A2242617273222C2263617465676F72794E616D65223A22E69FB1E78AB6E59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A2265636861727473222C22696D616765223A222E2F7374617469632F706E672F6261725F782D36656332386536392E706E67227D2C226F7074696F6E223A7B226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D7D2C227841786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A22626F74746F6D222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A66616C73652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2263617465676F7279227D2C227941786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A226C656674222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2276616C7565227D2C2267726964223A7B2273686F77223A66616C73652C226C656674223A22313025222C22746F70223A223630222C227269676874223A22313025222C22626F74746F6D223A223630227D2C22746F6F6C746970223A7B2273686F77223A747275652C2274726967676572223A2261786973222C2261786973506F696E746572223A7B2273686F77223A747275652C2274797065223A22736861646F77227D7D2C2264617461736574223A7B2264696D656E73696F6E73223A5B2270726F64756374222C226461746131222C226461746132225D2C22736F75726365223A5B7B2270726F64756374223A224D6F6E222C226461746131223A3132302C226461746132223A3133307D2C7B2270726F64756374223A22547565222C226461746131223A3230302C226461746132223A3133307D2C7B2270726F64756374223A22576564222C226461746131223A3135302C226461746132223A3331327D2C7B2270726F64756374223A22546875222C226461746131223A38302C226461746132223A3236387D2C7B2270726F64756374223A22467269222C226461746131223A37302C226461746132223A3135357D2C7B2270726F64756374223A22536174222C226461746131223A3131302C226461746132223A3131377D2C7B2270726F64756374223A2253756E222C226461746131223A3133302C226461746132223A3136307D5D7D2C22736572696573223A5B7B2274797065223A22626172222C226261725769647468223A31352C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A22746F70222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A327D7D2C7B2274797065223A22626172222C226261725769647468223A31352C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A22746F70222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A327D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A22346266626A376A73346F77303030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A3537352E352C2279223A2D3336332C2277223A3830362C2268223A313538332C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A224D617042617365222C226368617274436F6E666967223A7B226B6579223A224D617042617365222C2263686172744B6579223A22564D617042617365222C22636F6E4B6579223A2256434D617042617365222C227469746C65223A22E59CB0E59BBE28E58FAFE98089E79C81E4BBBD29222C2263617465676F7279223A224D617073222C2263617465676F72794E616D65223A22E59CB0E59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A22636F6D6D6F6E222C22696D616765223A222E2F7374617469632F706E672F6D61702D66353464636430652E706E67227D2C226F7074696F6E223A7B2264617461736574223A7B22706F696E74223A5B7B226E616D65223A22E58C97E4BAAC222C2276616C7565223A5B3131362E3430353238352C33392E3930343938392C3230305D7D2C7B226E616D65223A22E98391E5B79E222C2276616C7565223A5B3131332E3636353431322C33342E3735373937352C3838385D7D2C7B226E616D65223A22E99D92E6B5B7222C2276616C7565223A5B3130312E3737383931362C33362E3632333137382C3636365D7D2C7B226E616D65223A22E5AE81E5A48FE59B9EE6978FE887AAE6B2BBE58CBA222C2276616C7565223A5B3130362E3237383137392C33382E34363633372C36365D7D2C7B226E616D65223A22E59388E5B094E6BBA8E5B882222C2276616C7565223A5B3132362E3634323436342C34352E3735363936372C3130315D7D5D2C226D6170223A5B7B226E616D65223A22E58C97E4BAACE5B882222C2276616C7565223A3636367D2C7B226E616D65223A22E6B2B3E58C97E79C81222C2276616C7565223A39387D2C7B226E616D65223A22E6B19FE88B8FE79C81222C2276616C7565223A3330307D2C7B226E616D65223A22E7A68FE5BBBAE79C81222C2276616C7565223A313139397D2C7B226E616D65223A22E5B1B1E4B89CE79C81222C2276616C7565223A38367D2C7B226E616D65223A22E6B2B3E58D97E79C81222C2276616C7565223A3835307D2C7B226E616D65223A22E6B996E58C97E79C81222C2276616C7565223A38347D2C7B226E616D65223A22E5B9BFE8A5BFE5A3AEE6978FE887AAE6B2BBE58CBA222C2276616C7565223A38317D2C7B226E616D65223A22E6B5B7E58D97E79C81222C2276616C7565223A3930307D2C7B226E616D65223A22E99D92E6B5B7E79C81222C2276616C7565223A3830307D2C7B226E616D65223A22E696B0E79686E7BBB4E590BEE5B094E887AAE6B2BBE58CBA222C2276616C7565223A377D5D2C22706965636573223A5B7B22677465223A313030302C226C6162656C223A223E31303030227D2C7B22677465223A3630302C226C7465223A3939392C226C6162656C223A223630302D393939227D2C7B22677465223A3230302C226C7465223A3539392C226C6162656C223A223230302D353939227D2C7B22677465223A35302C226C7465223A3139392C226C6162656C223A2234392D313939227D2C7B22677465223A31302C226C7465223A34392C226C6162656C223A2231302D3439227D2C7B226C7465223A392C226C6162656C223A223C39227D5D7D2C226D6170526567696F6E223A7B226164636F6465223A226368696E61222C2273686F774861696E616E49734C616E6473223A747275657D2C22746F6F6C746970223A7B2273686F77223A747275652C2274726967676572223A226974656D227D2C2276697375616C4D6170223A7B2273686F77223A747275652C226F7269656E74223A22766572746963616C222C22706965636573223A5B7B22677465223A313030302C226C6162656C223A223E31303030227D2C7B22677465223A3630302C226C7465223A3939392C226C6162656C223A223630302D393939227D2C7B22677465223A3230302C226C7465223A3539392C226C6162656C223A223230302D353939227D2C7B22677465223A35302C226C7465223A3139392C226C6162656C223A2234392D313939227D2C7B22677465223A31302C226C7465223A34392C226C6162656C223A2231302D3439227D2C7B226C7465223A392C226C6162656C223A223C39227D5D2C22696E52616E6765223A7B22636F6C6F72223A5B2223633364376466222C2223356362336363222C2223386162636431222C2223363661396339222C2223326639306239222C2223313738316235225D7D2C22746578745374796C65223A7B22636F6C6F72223A2223666666227D7D2C2267656F223A7B2273686F77223A66616C73652C2274797065223A226D6170222C22726F616D223A66616C73652C226D6170223A226368696E61222C2273656C65637465644D6F6465223A66616C73652C227A6F6F6D223A317D2C22736572696573223A5B7B226E616D65223A22222C2274797065223A2265666665637453636174746572222C22636F6F7264696E61746553797374656D223A2267656F222C2273796D626F6C53697A65223A342C226C6567656E64486F7665724C696E6B223A747275652C2273686F774566666563744F6E223A2272656E646572222C22726970706C65456666656374223A7B227363616C65223A362C22636F6C6F72223A2223464646464646222C22627275736854797065223A2266696C6C227D2C22746F6F6C746970223A7B2273686F77223A747275652C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C2E3629222C22626F72646572436F6C6F72223A2272676261283134372C203233352C203234382C202E3829222C22746578745374796C65223A7B22636F6C6F72223A2223464646227D7D2C226C6162656C223A7B22666F726D6174746572223A227B627D222C22666F6E7453697A65223A31312C226F6666736574223A5B302C325D2C22706F736974696F6E223A22626F74746F6D222C2274657874426F72646572436F6C6F72223A2223666666222C2274657874536861646F77436F6C6F72223A2223303030222C2274657874536861646F77426C7572223A31302C2274657874426F726465725769647468223A302C22636F6C6F72223A2223464646464646222C2273686F77223A747275657D2C226974656D5374796C65223A7B22636F6C6F72223A2223464646464646222C22626F72646572436F6C6F72223A2272676261283232352C3235352C3235352C3229222C22626F726465725769647468223A342C22736861646F77436F6C6F72223A2223453146464646222C22736861646F77426C7572223A31307D2C2264617461223A5B7B226E616D65223A22E58C97E4BAAC222C2276616C7565223A5B3131362E3430353238352C33392E3930343938392C3230305D7D2C7B226E616D65223A22E98391E5B79E222C2276616C7565223A5B3131332E3636353431322C33342E3735373937352C3838385D7D2C7B226E616D65223A22E99D92E6B5B7222C2276616C7565223A5B3130312E3737383931362C33362E3632333137382C3636365D7D2C7B226E616D65223A22E5AE81E5A48FE59B9EE6978FE887AAE6B2BBE58CBA222C2276616C7565223A5B3130362E3237383137392C33382E34363633372C36365D7D2C7B226E616D65223A22E59388E5B094E6BBA8E5B882222C2276616C7565223A5B3132362E3634323436342C34352E3735363936372C3130315D7D5D7D2C7B226E616D65223A22E58CBAE59F9F222C2274797065223A226D6170222C226D6170223A226368696E61222C2264617461223A5B7B226E616D65223A22E58C97E4BAACE5B882222C2276616C7565223A3636367D2C7B226E616D65223A22E6B2B3E58C97E79C81222C2276616C7565223A39387D2C7B226E616D65223A22E6B19FE88B8FE79C81222C2276616C7565223A3330307D2C7B226E616D65223A22E7A68FE5BBBAE79C81222C2276616C7565223A313139397D2C7B226E616D65223A22E5B1B1E4B89CE79C81222C2276616C7565223A38367D2C7B226E616D65223A22E6B2B3E58D97E79C81222C2276616C7565223A3835307D2C7B226E616D65223A22E6B996E58C97E79C81222C2276616C7565223A38347D2C7B226E616D65223A22E5B9BFE8A5BFE5A3AEE6978FE887AAE6B2BBE58CBA222C2276616C7565223A38317D2C7B226E616D65223A22E6B5B7E58D97E79C81222C2276616C7565223A3930307D2C7B226E616D65223A22E99D92E6B5B7E79C81222C2276616C7565223A3830307D2C7B226E616D65223A22E696B0E79686E7BBB4E590BEE5B094E887AAE6B2BBE58CBA222C2276616C7565223A377D5D2C2273656C65637465644D6F6465223A66616C73652C227A6F6F6D223A312C2267656F496E646578223A312C22746F6F6C746970223A7B2273686F77223A747275652C226261636B67726F756E64436F6C6F72223A22233030303030303630222C22626F72646572436F6C6F72223A2272676261283134372C203233352C203234382C20302E3829222C22746578745374796C65223A7B22636F6C6F72223A2223464646464646222C22666F6E7453697A65223A31327D7D2C226C6162656C223A7B2273686F77223A66616C73652C22636F6C6F72223A2223464646464646222C22666F6E7453697A65223A31327D2C22656D706861736973223A7B2264697361626C6564223A66616C73652C226C6162656C223A7B22636F6C6F72223A2223464646464646222C22666F6E7453697A65223A31327D2C226974656D5374796C65223A7B2261726561436F6C6F72223A2223333839424237222C22736861646F77436F6C6F72223A2223333839424237222C22626F726465725769647468223A317D7D2C226974656D5374796C65223A7B22626F72646572436F6C6F72223A2223393345424638222C22626F726465725769647468223A312C2261726561436F6C6F72223A7B2274797065223A2272616469616C222C2278223A302E352C2279223A302E352C2272223A302E382C22636F6C6F7253746F7073223A5B7B226F6666736574223A302C22636F6C6F72223A22233933656266383030227D2C7B226F6666736574223A312C22636F6C6F72223A22233933656266383230227D5D2C22676C6F62616C436F6F7264223A66616C73657D2C22736861646F77436F6C6F72223A22233830443946383432222C22736861646F774F666673657458223A2D322C22736861646F774F666673657459223A322C22736861646F77426C7572223A31307D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A2234666963326C6F77337238303030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A36302C2279223A3431352C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A2242617243726F737372616E6765222C226368617274436F6E666967223A7B226B6579223A2242617243726F737372616E6765222C2263686172744B6579223A225642617243726F737372616E6765222C22636F6E4B6579223A22564342617243726F737372616E6765222C227469746C65223A22E6A8AAE59091E69FB1E78AB6E59BBE222C2263617465676F7279223A2242617273222C2263617465676F72794E616D65223A22E69FB1E78AB6E59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A2265636861727473222C22696D616765223A222E2F7374617469632F706E672F6261725F792D30353036373136392E706E67227D2C226F7074696F6E223A7B226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D7D2C227841786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A22626F74746F6D222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A66616C73652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2276616C7565227D2C227941786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A226C656674222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2263617465676F7279227D2C2267726964223A7B2273686F77223A66616C73652C226C656674223A22313025222C22746F70223A223630222C227269676874223A22313025222C22626F74746F6D223A223630227D2C22746F6F6C746970223A7B2273686F77223A747275652C2274726967676572223A2261786973222C2261786973506F696E746572223A7B2273686F77223A747275652C2274797065223A22736861646F77227D7D2C2264617461736574223A7B2264696D656E73696F6E73223A5B2270726F64756374222C226461746131222C226461746132225D2C22736F75726365223A5B7B2270726F64756374223A224D6F6E222C226461746131223A3132302C226461746132223A3133307D2C7B2270726F64756374223A22547565222C226461746131223A3230302C226461746132223A3133307D2C7B2270726F64756374223A22576564222C226461746131223A3135302C226461746132223A3331327D2C7B2270726F64756374223A22546875222C226461746131223A38302C226461746132223A3236387D2C7B2270726F64756374223A22467269222C226461746131223A37302C226461746132223A3135357D2C7B2270726F64756374223A22536174222C226461746131223A3131302C226461746132223A3131377D2C7B2270726F64756374223A2253756E222C226461746131223A3133302C226461746132223A3136307D5D7D2C22736572696573223A5B7B2274797065223A22626172222C226261725769647468223A6E756C6C2C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A227269676874222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A307D7D2C7B2274797065223A22626172222C226261725769647468223A6E756C6C2C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A227269676874222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A307D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A22323037636675696C6A6766343030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A36302C2279223A3734392C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A2243617073756C654368617274222C226368617274436F6E666967223A7B226B6579223A2243617073756C654368617274222C2263686172744B6579223A225643617073756C654368617274222C22636F6E4B6579223A22564343617073756C654368617274222C227469746C65223A22E883B6E59B8AE69FB1E59BBE222C2263617465676F7279223A2242617273222C2263617465676F72794E616D65223A22E69FB1E78AB6E59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A22636F6D6D6F6E222C22696D616765223A222E2F7374617469632F706E672F63617073756C652D63356432663532312E706E67227D2C226F7074696F6E223A7B2264617461736574223A7B2264696D656E73696F6E73223A5B226E616D65222C2276616C7565225D2C22736F75726365223A5B7B226E616D65223A22E58EA6E997A8222C2276616C7565223A32307D2C7B226E616D65223A22E58D97E998B3222C2276616C7565223A34307D2C7B226E616D65223A22E58C97E4BAAC222C2276616C7565223A36307D2C7B226E616D65223A22E4B88AE6B5B7222C2276616C7565223A38307D2C7B226E616D65223A22E696B0E79686222C2276616C7565223A3130307D5D7D2C22636F6C6F7273223A5B2223633465626164222C2223366265366331222C2223613061376536222C2223393664656538222C2223336662316533225D2C22756E6974223A22222C226974656D486569676874223A31302C2276616C7565466F6E7453697A65223A31362C2270616464696E675269676874223A35302C2270616464696E674C656674223A35302C2273686F7756616C7565223A747275657D7D2C7B226964223A22316764753078716C73736B673030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A313430352C2279223A35322C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A224C696E65436F6D6D6F6E222C226368617274436F6E666967223A7B226B6579223A224C696E65436F6D6D6F6E222C2263686172744B6579223A22564C696E65436F6D6D6F6E222C22636F6E4B6579223A2256434C696E65436F6D6D6F6E222C227469746C65223A22E68A98E7BABFE59BBE222C2263617465676F7279223A224C696E6573222C2263617465676F72794E616D65223A22E68A98E7BABFE59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A2265636861727473222C22696D616765223A222E2F7374617469632F706E672F6C696E652D65373134626337342E706E67227D2C226F7074696F6E223A7B226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D7D2C227841786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A22626F74746F6D222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A66616C73652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2263617465676F7279227D2C227941786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A226C656674222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22736F6C6964227D7D2C2274797065223A2276616C7565227D2C2267726964223A7B2273686F77223A66616C73652C226C656674223A22313025222C22746F70223A223630222C227269676874223A22313025222C22626F74746F6D223A223630227D2C22746F6F6C746970223A7B2273686F77223A747275652C2274726967676572223A2261786973222C2261786973506F696E746572223A7B2274797065223A226C696E65227D7D2C2264617461736574223A7B2264696D656E73696F6E73223A5B2270726F64756374222C226461746131222C226461746132225D2C22736F75726365223A5B7B2270726F64756374223A224D6F6E222C226461746131223A3132302C226461746132223A3133307D2C7B2270726F64756374223A22547565222C226461746131223A3230302C226461746132223A3133307D2C7B2270726F64756374223A22576564222C226461746131223A3135302C226461746132223A3331327D2C7B2270726F64756374223A22546875222C226461746131223A38302C226461746132223A3236387D2C7B2270726F64756374223A22467269222C226461746131223A37302C226461746132223A3135357D2C7B2270726F64756374223A22536174222C226461746131223A3131302C226461746132223A3131377D2C7B2270726F64756374223A2253756E222C226461746131223A3133302C226461746132223A3136307D5D7D2C22736572696573223A5B7B2274797065223A226C696E65222C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A22746F70222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C2273796D626F6C53697A65223A352C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A307D2C226C696E655374796C65223A7B2274797065223A22736F6C6964222C227769647468223A332C22636F6C6F72223A6E756C6C7D7D2C7B2274797065223A226C696E65222C226C6162656C223A7B2273686F77223A747275652C22706F736974696F6E223A22746F70222C22636F6C6F72223A2223666666222C22666F6E7453697A65223A31327D2C2273796D626F6C53697A65223A352C226974656D5374796C65223A7B22636F6C6F72223A6E756C6C2C22626F72646572526164697573223A307D2C226C696E655374796C65223A7B2274797065223A22736F6C6964222C227769647468223A332C22636F6C6F72223A6E756C6C7D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A22347A73376E6E36756A6463303030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A313338352C2279223A3336312C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A22506965436F6D6D6F6E222C226368617274436F6E666967223A7B226B6579223A22506965436F6D6D6F6E222C2263686172744B6579223A2256506965436F6D6D6F6E222C22636F6E4B6579223A225643506965436F6D6D6F6E222C227469746C65223A22E9A5BCE59BBE222C2263617465676F7279223A2250696573222C2263617465676F72794E616D65223A22E9A5BCE59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A2265636861727473222C22696D616765223A222E2F7374617469632F706E672F7069652D39363230663139312E706E67227D2C226F7074696F6E223A7B226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D7D2C2274797065223A2272696E67222C22746F6F6C746970223A7B2273686F77223A747275652C2274726967676572223A226974656D227D2C2264617461736574223A7B2264696D656E73696F6E73223A5B2270726F64756374222C226461746131225D2C22736F75726365223A5B7B2270726F64756374223A224D6F6E222C226461746131223A3132307D2C7B2270726F64756374223A22547565222C226461746131223A3230307D2C7B2270726F64756374223A22576564222C226461746131223A3135307D2C7B2270726F64756374223A22546875222C226461746131223A38307D2C7B2270726F64756374223A22467269222C226461746131223A37307D2C7B2270726F64756374223A22536174222C226461746131223A3131307D2C7B2270726F64756374223A2253756E222C226461746131223A3133307D5D7D2C22736572696573223A5B7B2274797065223A22706965222C22726164697573223A5B22343025222C22363525225D2C2263656E746572223A5B22353025222C22363025225D2C22726F736554797065223A66616C73652C2261766F69644C6162656C4F7665726C6170223A66616C73652C226974656D5374796C65223A7B2273686F77223A747275652C22626F72646572526164697573223A31302C22626F72646572436F6C6F72223A2223666666222C22626F726465725769647468223A327D2C226C6162656C223A7B2273686F77223A66616C73652C22706F736974696F6E223A2263656E746572227D2C22656D706861736973223A7B226C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A223430222C22666F6E74576569676874223A22626F6C64227D7D2C226C6162656C4C696E65223A7B2273686F77223A66616C73657D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A22353669656D706379696230303030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A313338352C2279223A3732322C2277223A3530302C2268223A3330302C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A22536361747465724C6F6761726974686D696352656772657373696F6E222C226368617274436F6E666967223A7B226B6579223A22536361747465724C6F6761726974686D696352656772657373696F6E222C2263686172744B6579223A2256536361747465724C6F6761726974686D696352656772657373696F6E222C22636F6E4B6579223A225643536361747465724C6F6761726974686D696352656772657373696F6E222C227469746C65223A22E5AFB9E695B0E59B9EE5BD92E695A3E782B9E59BBE222C2263617465676F7279223A225363617474657273222C2263617465676F72794E616D65223A22E695A3E782B9E59BBE222C227061636B616765223A22436861727473222C2263686172744672616D65223A2265636861727473222C22696D616765223A222E2F7374617469632F706E672F736361747465722D6C6F6761726974686D69632D72656772657373696F6E2D38376665366564312E706E67227D2C226F7074696F6E223A7B226C6567656E64223A7B2273686F77223A747275652C22746F70223A223525222C22746578745374796C65223A7B22636F6C6F72223A2223423942384345227D2C2264617461223A5B2231393930222C2232303135225D7D2C227841786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A22626F74746F6D222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A66616C73652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22646173686564227D7D2C2274797065223A2276616C7565227D2C227941786973223A7B2273686F77223A747275652C226E616D65223A22222C226E616D65476170223A31352C226E616D65546578745374796C65223A7B22636F6C6F72223A2223423942384345222C22666F6E7453697A65223A31327D2C22696E7665727365223A66616C73652C22617869734C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31322C22636F6C6F72223A2223423942384345222C22726F74617465223A307D2C22706F736974696F6E223A226C656674222C22617869734C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223423942384345222C227769647468223A317D2C226F6E5A65726F223A747275657D2C22617869735469636B223A7B2273686F77223A747275652C226C656E677468223A357D2C2273706C69744C696E65223A7B2273686F77223A747275652C226C696E655374796C65223A7B22636F6C6F72223A2223343834373533222C227769647468223A312C2274797065223A22646173686564227D7D2C2274797065223A2276616C7565227D2C2267726964223A7B2273686F77223A66616C73652C226C656674223A22313025222C22746F70223A223630222C227269676874223A22313025222C22626F74746F6D223A223630227D2C2264617461736574223A5B7B22736F75726365223A5B5B32383630342C37372C31373039363836392C224175737472616C6961222C313939305D2C5B33313136332C37372E342C32373636323434302C2243616E616461222C313939305D2C5B313531362C36382C313135343630353737332C224368696E61222C313939305D2C5B31333637302C37342E372C31303538323038322C2243756261222C313939305D2C5B32383539392C37352C343938363730352C2246696E6C616E64222C313939305D2C5B32393437362C37372E312C35363934333239392C224672616E6365222C313939305D2C5B33313437362C37352E342C37383935383233372C224765726D616E79222C313939305D2C5B32383636362C37382E312C3235343833302C224963656C616E64222C313939305D2C5B313737372C35372E372C3837303630313737362C22496E646961222C313939305D2C5B32393535302C37392E312C3132323234393238352C224A6170616E222C313939305D2C5B323037362C36372E392C32303139343335342C224E6F727468204B6F726561222C313939305D2C5B31323038372C37322C34323937323235342C22536F757468204B6F726561222C313939305D2C5B32343032312C37352E342C333339373533342C224E6577205A65616C616E64222C313939305D2C5B34333239362C37362E382C343234303337352C224E6F72776179222C313939305D2C5B31303038382C37302E382C33383139353235382C22506F6C616E64222C313939305D2C5B31393334392C36392E362C3134373536383535322C22527573736961222C313939305D2C5B31303637302C36372E332C35333939343630352C225475726B6579222C313939305D2C5B32363432342C37352E372C35373131303131372C22556E69746564204B696E67646F6D222C313939305D2C5B33373036322C37352E342C3235323834373831302C22556E6974656420537461746573222C313939305D2C5B34343035362C38312E382C32333936383937332C224175737472616C6961222C323031355D2C5B34333239342C38312E372C33353933393932372C2243616E616461222C323031355D2C5B31333333342C37362E392C313337363034383934332C224368696E61222C323031355D2C5B32313239312C37382E352C31313338393536322C2243756261222C323031355D2C5B33383932332C38302E382C353530333435372C2246696E6C616E64222C323031355D2C5B33373539392C38312E392C36343339353334352C224672616E6365222C323031355D2C5B34343035332C38312E312C38303638383534352C224765726D616E79222C323031355D2C5B34323138322C38322E382C3332393432352C224963656C616E64222C323031355D2C5B353930332C36362E382C313331313035303532372C22496E646961222C323031355D2C5B33363136322C38332E352C3132363537333438312C224A6170616E222C323031355D2C5B313339302C37312E342C32353135353331372C224E6F727468204B6F726561222C323031355D2C5B33343634342C38302E372C35303239333433392C22536F757468204B6F726561222C323031355D2C5B33343138362C38302E362C343532383532362C224E6577205A65616C616E64222C323031355D2C5B36343330342C38312E362C353231303936372C224E6F72776179222C323031355D2C5B32343738372C37372E332C33383631313739342C22506F6C616E64222C323031355D2C5B32333033382C37332E31332C3134333435363931382C22527573736961222C323031355D2C5B31393336302C37362E352C37383636353833302C225475726B6579222C323031355D2C5B33383232352C38312E342C36343731353831302C22556E69746564204B696E67646F6D222C323031355D2C5B35333335342C37392E312C3332313737333633312C22556E6974656420537461746573222C323031355D5D7D2C7B227472616E73666F726D223A7B2274797065223A2266696C746572222C22636F6E666967223A7B2264696D656E73696F6E223A342C226571223A313939307D7D7D2C7B227472616E73666F726D223A7B2274797065223A2266696C746572222C22636F6E666967223A7B2264696D656E73696F6E223A342C226571223A323031357D7D7D2C7B227472616E73666F726D223A7B2274797065223A226563537461743A72656772657373696F6E222C22636F6E666967223A7B226D6574686F64223A226C6F6761726974686D6963227D7D7D5D2C22746F6F6C746970223A7B2273686F7744656C6179223A302C2261786973506F696E746572223A7B2273686F77223A747275652C2274797065223A2263726F7373222C226C696E655374796C65223A7B2274797065223A22646173686564222C227769647468223A317D7D7D2C2276697375616C4D6170223A7B2273686F77223A66616C73652C2264696D656E73696F6E223A322C226D696E223A32303030302C226D6178223A313530303030303030302C22736572696573496E646578223A5B302C315D2C22696E52616E6765223A7B2273796D626F6C53697A65223A5B31302C37305D7D7D2C22736572696573223A5B7B2274797065223A2273636174746572222C2264617461736574496E646578223A317D2C7B2274797065223A2273636174746572222C2264617461736574496E646578223A327D2C7B2274797065223A226C696E65222C22736D6F6F7468223A747275652C2264617461736574496E646578223A332C2273796D626F6C53697A65223A302E312C2273796D626F6C223A22636972636C65222C226C6162656C223A7B2273686F77223A747275652C22666F6E7453697A65223A31367D2C226C6162656C4C61796F7574223A7B226478223A2D32307D2C22656E636F6465223A7B226C6162656C223A322C22746F6F6C746970223A317D7D5D2C226261636B67726F756E64436F6C6F72223A227267626128302C302C302C3029227D7D2C7B226964223A223263636A6B376177737234303030222C22697347726F7570223A66616C73652C2261747472223A7B2278223A3630392E352C2279223A3736352C2277223A3730392C2268223A3236312C226F666673657458223A302C226F666673657459223A302C227A496E646578223A2D317D2C227374796C6573223A7B2266696C74657253686F77223A66616C73652C22687565526F74617465223A302C227361747572617465223A312C22636F6E7472617374223A312C226272696768746E657373223A312C226F706163697479223A312C22726F746174655A223A302C22726F7461746558223A302C22726F7461746559223A302C22736B657758223A302C22736B657759223A302C22626C656E644D6F6465223A226E6F726D616C222C22616E696D6174696F6E73223A5B5D7D2C22737461747573223A7B226C6F636B223A66616C73652C2268696465223A66616C73657D2C2272657175657374223A7B22726571756573744461746154797065223A302C22726571756573744874747054797065223A22676574222C227265717565737455726C223A22222C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374436F6E74656E7454797065223A302C2272657175657374506172616D73426F647954797065223A226E6F6E65222C227265717565737453514C436F6E74656E74223A7B2273716C223A2273656C656374202A2066726F6D20207768657265227D2C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D2C226B6579223A225461626C655363726F6C6C426F617264222C226368617274436F6E666967223A7B226B6579223A225461626C655363726F6C6C426F617264222C2263686172744B6579223A22565461626C655363726F6C6C426F617264222C22636F6E4B6579223A2256435461626C655363726F6C6C426F617264222C227469746C65223A22E8BDAEE692ADE58897E8A1A8222C2263617465676F7279223A225461626C6573222C2263617465676F72794E616D65223A22E8A1A8E6A0BC222C227061636B616765223A225461626C6573222C2263686172744672616D65223A22636F6D6D6F6E222C22696D616765223A222E2F7374617469632F706E672F7461626C655F7363726F6C6C626F6172642D66623634326537382E706E67227D2C226F7074696F6E223A7B22686561646572223A5B22E5889731222C22E5889732222C22E5889733225D2C2264617461736574223A5B5B22E8A18C31E5889731222C22E8A18C31E5889732222C22E8A18C31E5889733225D2C5B22E8A18C32E5889731222C22E8A18C32E5889732222C22E8A18C32E5889733225D2C5B22E8A18C33E5889731222C22E8A18C33E5889732222C22E8A18C33E5889733225D2C5B22E8A18C34E5889731222C22E8A18C34E5889732222C22E8A18C34E5889733225D2C5B22E8A18C35E5889731222C22E8A18C35E5889732222C22E8A18C35E5889733225D2C5B22E8A18C36E5889731222C22E8A18C36E5889732222C22E8A18C36E5889733225D2C5B22E8A18C37E5889731222C22E8A18C37E5889732222C22E8A18C37E5889733225D2C5B22E8A18C38E5889731222C22E8A18C38E5889732222C22E8A18C38E5889733225D2C5B22E8A18C39E5889731222C22E8A18C39E5889732222C22E8A18C39E5889733225D2C5B22E8A18C3130E5889731222C22E8A18C3130E5889732222C22E8A18C3130E5889733225D5D2C22696E646578223A747275652C22636F6C756D6E5769647468223A5B33302C3130302C3130305D2C22616C69676E223A5B2263656E746572222C227269676874222C227269676874222C227269676874225D2C22726F774E756D223A352C227761697454696D65223A322C22686561646572486569676874223A33352C226361726F7573656C223A2273696E676C65222C22686561646572424743223A2223303042414646222C226F6464526F77424743223A2223303033423531222C226576656E526F77424743223A2223304132373332227D7D5D2C2272657175657374476C6F62616C436F6E666967223A7B22726571756573744F726967696E55726C223A22222C2272657175657374496E74657276616C223A33302C2272657175657374496E74657276616C556E6974223A227365636F6E64222C2272657175657374506172616D73223A7B22426F6479223A7B22666F726D2D64617461223A7B7D2C22782D7777772D666F726D2D75726C656E636F646564223A7B7D2C226A736F6E223A22222C22786D6C223A22227D2C22486561646572223A7B7D2C22506172616D73223A7B7D7D7D7D, '2023-02-26 23:08:40', '1', '2023-02-26 23:11:41');

-- ----------------------------
-- Table structure for iot_group
-- ----------------------------
DROP TABLE IF EXISTS `iot_group`;
CREATE TABLE `iot_group`  (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `group_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分组名称',
  `group_order` tinyint(11) NOT NULL DEFAULT 0 COMMENT '分组排序',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`group_id`) USING BTREE,
  INDEX `iot_group_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_group
-- ----------------------------
INSERT INTO `iot_group` VALUES (2, '卧室', 2, 1, 'admin', '0', '', '2021-12-29 13:12:42', '', '2023-04-09 22:37:06', '卧室设备');
INSERT INTO `iot_group` VALUES (3, '厨房', 3, 1, 'admin', '0', '', '2021-12-29 13:12:59', '', '2021-12-29 13:13:48', '厨房设备');
INSERT INTO `iot_group` VALUES (4, '书房', 4, 1, 'admin', '0', '', '2021-12-29 13:13:10', '', '2021-12-29 13:13:54', '书房设备');
INSERT INTO `iot_group` VALUES (5, '卫生间', 5, 1, 'admin', '0', '', '2021-12-29 13:13:18', '', '2021-12-29 13:14:03', '卫生间设备');
INSERT INTO `iot_group` VALUES (6, '走道', 6, 1, 'admin', '0', '', '2021-12-29 13:13:26', '', '2021-12-29 13:14:11', '走道设备');

-- ----------------------------
-- Table structure for iot_product
-- ----------------------------
DROP TABLE IF EXISTS `iot_product`;
CREATE TABLE `iot_product`  (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `protocol_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '协议编号',
  `category_id` bigint(20) NOT NULL COMMENT '产品分类ID',
  `category_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品分类名称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `is_sys` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
  `is_authorize` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用授权码（0-否，1-是）',
  `mqtt_account` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mqtt账号',
  `mqtt_password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mqtt密码',
  `mqtt_secret` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品秘钥',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态（1-未发布，2-已发布）',
  `things_models_json` json NULL COMMENT '物模型JSON（属性、功能、事件）',
  `device_type` tinyint(1) NULL DEFAULT 1 COMMENT '设备类型（1-直连设备、2-网关设备、3-监控设备）',
  `network_method` tinyint(1) NULL DEFAULT 1 COMMENT '联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）',
  `vertificate_method` tinyint(1) NULL DEFAULT 1 COMMENT '认证方式（1-简单认证、2-加密认证、3-简单+加密）',
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `transport` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品支持的传输协议',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `iot_product_index_category_id`(`category_id`) USING BTREE,
  INDEX `iot_product_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 151 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_product
-- ----------------------------
INSERT INTO `iot_product` VALUES (41, '★智能开关产品', '0', 1, '电工照明', 1, 'admin', 1, 0, 'FastBee', 'P47T6OD5IPFWHUM6', 'KX3TSH4Q4OS835DO', 2, '{\"events\": [{\"id\": \"exception\", \"name\": \"设备发生异常\", \"type\": 3, \"order\": 0, \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"height_temperature\", \"name\": \"环境温度过高\", \"type\": 3, \"order\": 0, \"isChart\": 0, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"functions\": [{\"id\": \"report_monitor\", \"name\": \"上报数据\", \"type\": 2, \"order\": 10, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"order\": 7, \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"type\": 2, \"order\": 5, \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"order\": 9, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"irc\", \"name\": \"射频遥控\", \"type\": 2, \"order\": 11, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"order\": 8, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"status\", \"name\": \"上报状态\", \"type\": 2, \"order\": 12, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}], \"properties\": [{\"id\": \"co2\", \"name\": \"二氧化碳\", \"type\": 1, \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"brightness\", \"name\": \"室内亮度\", \"type\": 1, \"order\": 4, \"isChart\": 1, \"datatype\": {\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"temperature\", \"name\": \"空气温度\", \"type\": 1, \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"humidity\", \"name\": \"空气湿度\", \"type\": 1, \"order\": 3, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}]}', 1, 1, 3, NULL, '0', '', '2025-08-14 00:06:33', '', '2023-02-26 01:08:18', NULL, NULL);
INSERT INTO `iot_product` VALUES (96, '★网关产品', 'JSON', 1, '电工照明', 1, 'admin', 1, 0, 'FastBee', 'P467433O1MT8MXS2', 'KWF32S3H95LH14LO', 2, '{\"events\": [{\"id\": \"exception\", \"name\": \"设备发生异常\", \"type\": 3, \"order\": 0, \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"height_temperature\", \"name\": \"环境温度过高\", \"type\": 3, \"order\": 0, \"isChart\": 0, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"functions\": [{\"id\": \"report_monitor\", \"name\": \"上报监测数据\", \"type\": 2, \"order\": 11, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"order\": 0, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"order\": 0, \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"type\": 2, \"order\": 0, \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"order\": 7, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"order\": 8, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"properties\": [{\"id\": \"brightness\", \"name\": \"室内亮度\", \"type\": 1, \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"temperature\", \"name\": \"空气温度\", \"type\": 1, \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"device\", \"name\": \"子设备\", \"type\": 1, \"order\": 10, \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_irc\", \"name\": \"射频遥控\", \"order\": 1, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"arrayType\": \"object\", \"arrayCount\": 5}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group\", \"name\": \"功能分组\", \"type\": 1, \"order\": 9, \"isChart\": 0, \"datatype\": {\"type\": \"object\", \"params\": [{\"id\": \"group_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"group_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_gear\", \"name\": \"运行档位\", \"order\": 5, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_switch\", \"name\": \"设备开关\", \"order\": 4, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_irc\", \"name\": \"射频遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控配对\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}]}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}]}', 2, 1, 3, NULL, '0', '', '2025-02-25 22:51:39', '', '2023-02-26 00:35:53', NULL, NULL);
INSERT INTO `iot_product` VALUES (112, '★MODBUS协议产品', 'MODBUS-RTU', 1, '电工照明', 1, 'admin', 1, 0, 'FastBee', 'P6JPN3V4PRZA91W8', 'KJVHI708452TMIS0', 2, '{\"properties\": [{\"id\": \"31\", \"name\": \"三相视在功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"789\", \"name\": \"缺相电压值\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"2\", \"name\": \"A相电流 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*CT*0.001\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"35\", \"name\": \"负向有功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"54\", \"name\": \"正向无功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"29\", \"name\": \"三相总功率因数 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"772\", \"name\": \"波特率\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"1200\", \"value\": \"0\"}, {\"text\": \"2400\", \"value\": \"1\"}, {\"text\": \"4800\", \"value\": \"2\"}, {\"text\": \"9600\", \"value\": \"3\"}, {\"text\": \"19200\", \"value\": \"4\"}]}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"50\", \"name\": \"正向有功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"12\", \"name\": \"B相有功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"15\", \"name\": \"B相视在功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"6\", \"name\": \"A相无功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 1, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"22\", \"name\": \"C相无功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"38\", \"name\": \"正向无功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"19\", \"name\": \"零地电压\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"0\", \"name\": \"漏水值\", \"type\": 1, \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 1}, {\"id\": \"0\", \"name\": \"温度\", \"type\": 1, \"order\": 0, \"formula\": \"%s/10\", \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"°C\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 2}, {\"id\": \"32\", \"name\": \"零序电流 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*CT*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"1\", \"name\": \"线电压Uca\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"53\", \"name\": \"正向无功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"36\", \"name\": \"负向有功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"28\", \"name\": \"三相有功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"773\", \"name\": \"电压范围\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"150\", \"value\": \"0\"}, {\"text\": \"600\", \"value\": \"1\"}]}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"49\", \"name\": \"正向有功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"11\", \"name\": \"D0开出状态检测 \", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 1, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"断开\", \"value\": \"0\"}, {\"text\": \"闭合\", \"value\": \"1\"}]}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"9\", \"name\": \"线电压Uab\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"18\", \"name\": \"C相电流\", \"type\": 1, \"order\": 0, \"formula\": \"%s*CT*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"21\", \"name\": \"C相功率因数\", \"type\": 1, \"order\": 0, \"formula\": \"%s*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"39\", \"name\": \"负向无功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"5\", \"name\": \"A相功率因数\", \"type\": 1, \"order\": 0, \"formula\": \"%s*0.001\", \"isChart\": 1, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"777\", \"name\": \"CT\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"10000\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"25\", \"name\": \"三相平均线电压 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"56\", \"name\": \"负向无功电能累加值低高字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"52\", \"name\": \"负向有功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"27\", \"name\": \"频率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001Hz\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"0\", \"name\": \"相电压Ua\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"33\", \"name\": \"正向有功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"768\", \"name\": \"本机地址\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"247\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"14\", \"name\": \"B相无功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"10\", \"name\": \"B相电流 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*CT*0.001\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"17\", \"name\": \"线电压Ubc\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"8\", \"name\": \"相电压Ub\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"20\", \"name\": \"C相有功功率\", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"4\", \"name\": \"A相有功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 1, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"24\", \"name\": \"三相平均相电压 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"1\", \"name\": \"湿度\", \"type\": 1, \"order\": 0, \"formula\": \"%s/10\", \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"°C\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 2}, {\"id\": \"55\", \"name\": \"负向无功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"30\", \"name\": \"三相无功功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"769\", \"name\": \"被测系统负载接线方式\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"三相四线\", \"value\": \"0\"}, {\"text\": \"一相二线\", \"value\": \"1\"}, {\"text\": \"三相三线\", \"value\": \"2\"}, {\"text\": \"三相三线平衡\", \"value\": \"3\"}, {\"text\": \"一相三线\", \"value\": \"4\"}, {\"text\": \"三相四线平衡\", \"value\": \"5\"}]}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"34\", \"name\": \"正向有功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"51\", \"name\": \"负向有功电能累加值高位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"26\", \"name\": \"三相平均相电流 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*CT*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"3\", \"name\": \"D1开入状态检测 \", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 1, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"断开\", \"value\": \"0\"}, {\"text\": \"闭合\", \"value\": \"1\"}]}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"771\", \"name\": \"校验位\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"N\", \"value\": \"0\"}, {\"text\": \"O\", \"value\": \"1\"}, {\"text\": \"E\", \"value\": \"2\"}]}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"13\", \"name\": \"B相功率因数\", \"type\": 1, \"order\": 0, \"formula\": \"%s*0.001\", \"isChart\": 0, \"datatype\": {\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"775\", \"name\": \"PT\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"10000\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"40\", \"name\": \"负向无功电能累加值低高字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"7\", \"name\": \"A相视在功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 1, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"16\", \"name\": \"相电压Uc \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*0.1\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}, {\"id\": \"37\", \"name\": \"正向无功电能累加值低位字\", \"type\": 1, \"order\": 0, \"formula\": \"\", \"isChart\": 0, \"datatype\": {\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1, \"tempSlaveId\": 11}, {\"id\": \"23\", \"name\": \"C相视在功率 \", \"type\": 1, \"order\": 0, \"formula\": \"%s*PT*CT\", \"isChart\": 0, \"datatype\": {\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 0, \"tempSlaveId\": 11}]}', 2, 1, 3, NULL, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 'MQTT');
INSERT INTO `iot_product` VALUES (118, '★视频监控产品', NULL, 2, '家居安防', 1, 'admin', 1, 0, 'FastBee', 'P0IB9M8A7J4R056V', 'K69914VL8175ZY21', 2, '{"events": [{"id": "devAlarm", "name": "设备报警", "type": 3, "order": 0, "regId": "devAlarm", "isChart": 0, "datatype": {"type": "enum", "showWay": "select", "enumList": [{"text": "视频丢失报警", "value": "1"}, {"text": "设备防拆报警", "value": "2"}, {"text": "存储设备磁盘满报警", "value": "3"}, {"text": "设备高温报警", "value": "4"}, {"text": "设备低温报警", "value": "5"}]}, "isHistory": 1, "isMonitor": 0, "isReadonly": 1}], "functions": [{"id": "audio_broadcast", "name": "语音广播", "type": 2, "order": 0, "regId": "audio_broadcast", "isChart": 0, "datatype": {"type": "string", "maxLength": 1024}, "isHistory": 1, "isMonitor": 0, "isReadonly": 0}, {"id": "video_push", "name": "设备推流", "type": 2, "order": 0, "regId": "video_push", "isChart": 0, "datatype": {"type": "string", "maxLength": 1024}, "isHistory": 1, "isMonitor": 0, "isReadonly": 0}]}', 3, 1, 3, NULL, '0', '', '2023-04-11 21:11:54', '', NULL, NULL, 'GB28181');

-- ----------------------------
-- Table structure for iot_product_authorize
-- ----------------------------
DROP TABLE IF EXISTS `iot_product_authorize`;
CREATE TABLE `iot_product_authorize`  (
  `authorize_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '授权码ID',
  `authorize_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '授权码',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `serial_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态（1-未使用，2-使用中）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`authorize_id`) USING BTREE,
  INDEX `iot_product_authorize_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_product_authorize_index_device_id`(`device_id`) USING BTREE,
  INDEX `iot_product_authorize_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_product_authorize_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品授权码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_product_authorize
-- ----------------------------
INSERT INTO `iot_product_authorize` VALUES (1, '1D6144F2D1A54FE097630A8B8F9ED17E', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-04-08 18:10:58', '', NULL, NULL);

-- ----------------------------
-- Table structure for iot_protocol
-- ----------------------------
DROP TABLE IF EXISTS `iot_protocol`;
CREATE TABLE `iot_protocol`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `protocol_code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议编码',
  `protocol_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议名称',
  `protocol_file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议jar包,js包，c程序上传地址',
  `protocol_type` int(11) NOT NULL DEFAULT 0 COMMENT '协议类型 0:未知 1:jar，2.js,3.c',
  `jar_sign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议文件摘要(文件的md5)',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `protocol_status` int(11) NOT NULL DEFAULT 0 COMMENT '0:草稿 1:启用 2:停用',
  `del_flag` int(11) NOT NULL DEFAULT 0 COMMENT '0:正常 1:删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UNIQUE_CODE`(`protocol_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '协议表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_protocol
-- ----------------------------
INSERT INTO `iot_protocol` VALUES (1, 'JSON', 'JSON解析协议', '/', 0, '系统内置JSON解析协议', '2023-02-28 13:46:43', '2023-04-09 22:42:12', 1, 0);
INSERT INTO `iot_protocol` VALUES (2, 'MODBUS-RTU', 'ModbusRtu协议', '/', 0, '系统内置ModbusRtu解析协议', '2023-02-28 13:52:33', '2023-04-08 23:58:59', 1, 0);
INSERT INTO `iot_protocol` VALUES (3, 'MODBUS-RTU-PAK', '包装Modbus-rtu协议', '/', 0, '系统内置包装后的modbus-rtu协议', '2023-02-28 13:53:10', '2023-02-28 16:10:47', 1, 0);

-- ----------------------------
-- Table structure for iot_scene
-- ----------------------------
DROP TABLE IF EXISTS `iot_scene`;
CREATE TABLE `iot_scene`  (
  `scene_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '场景ID',
  `scene_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '场景名称',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名称',
  `actions` json NOT NULL COMMENT '执行动作',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '场景状态（1-启动，2-停止）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scene_id`) USING BTREE,
  INDEX `iot_scene_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场景联动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_scene
-- ----------------------------
INSERT INTO `iot_scene` VALUES (72, 'T1', 1, 'admin', '[{\"id\": \"irc\", \"name\": \"射频遥控\", \"type\": 2, \"value\": \"FFXX04\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"value\": \"1\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"value\": \"str\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"report_monitor\", \"name\": \"上报数据\", \"type\": 2, \"value\": \"2\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"value\": \"restart\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}]', 1, '', '2023-04-15 11:02:50', '', '2023-04-15 11:04:36', NULL);
INSERT INTO `iot_scene` VALUES (73, 'T2', 1, 'admin', '[{\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"value\": \"0\", \"deviceId\": 108, \"productId\": 41, \"deviceName\": \"★温湿度开关\", \"productName\": \"智能开关\", \"serialNumber\": \"D1ELV3A5TOJS\"}]', 1, '', '2023-04-15 11:06:52', '', NULL, NULL);

-- ----------------------------
-- Table structure for iot_scene_trigger
-- ----------------------------
DROP TABLE IF EXISTS `iot_scene_trigger`;
CREATE TABLE `iot_scene_trigger`  (
  `scene_trigger_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '场景触发器ID',
  `scene_id` bigint(20) NOT NULL COMMENT '所属场景ID',
  `status` tinyint(1) NOT NULL COMMENT '告警状态（1-启动，2-停止）',
  `source` tinyint(1) NOT NULL COMMENT '触发源（1=设备触发，2=定时触发）',
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型标识符',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型值',
  `operator` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作符',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备编号',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `job_id` bigint(20) NULL DEFAULT NULL COMMENT '任务ID',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `is_advance` tinyint(1) NULL DEFAULT NULL COMMENT '是否详细corn表达式（1=是，0=否）',
  PRIMARY KEY (`scene_trigger_id`) USING BTREE,
  INDEX `iot_scene_trigger_index_scene_id`(`scene_id`) USING BTREE,
  INDEX `iot_scene_trigger_index_device_id`(`device_id`) USING BTREE,
  INDEX `iot_scene_trigger_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_scene_trigger_index_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '场景联动触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_scene_trigger
-- ----------------------------
INSERT INTO `iot_scene_trigger` VALUES (229, 72, 1, 2, '', '', '', '', 1, 0, '', '', 0, '', 0, '0 02 11 ? * 1,2,3,4,5,6,7', 0);
INSERT INTO `iot_scene_trigger` VALUES (230, 73, 1, 1, 'temperature', '空气温度', '99', '=', 1, 109, '★网关设备', 'D1PGLPG58KZ2', 96, '网关产品', 0, '', 0);

-- ----------------------------
-- Table structure for iot_simulate_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_simulate_log`;
CREATE TABLE `iot_simulate_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `send_data` json NULL COMMENT '云端发送指令',
  `callback_data` json NULL COMMENT '设备回复',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 584464 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '模拟设备日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_simulate_log
-- ----------------------------

-- ----------------------------
-- Table structure for iot_social_platform
-- ----------------------------
DROP TABLE IF EXISTS `iot_social_platform`;
CREATE TABLE `iot_social_platform`  (
  `social_platform_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '第三方登录平台主键',
  `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方登录平台',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT ' 0:启用 ,1:禁用',
  `client_id` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台申请Id',
  `secret_key` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台密钥',
  `redirect_uri` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户认证后跳转地址',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记位(0代表存在，1代表删除)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `bind_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定注册登录uri,http://localhost/login?bindId=',
  `redirect_login_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '跳转登录uri,http://localhost/login?loginId=',
  `error_msg_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '错误提示uri,http://localhost/login?errorId=',
  PRIMARY KEY (`social_platform_id`) USING BTREE,
  UNIQUE INDEX `iot_social_platform_platform_uindex`(`platform`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方登录平台控制' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_social_platform
-- ----------------------------
INSERT INTO `iot_social_platform` VALUES (1, 'QQ', '0', '102005066', 'PhkaBYgZ99999', 'https://iot.wumei.live/auth/callback/qq', '0', 'admin', '2022-04-18 11:21:28', '2022-04-20 16:29:23', 'admin', NULL, 'http://localhost/login?bindId=', 'http://localhost/login?loginId=', 'http://localhost/login?errorId=');

-- ----------------------------
-- Table structure for iot_social_user
-- ----------------------------
DROP TABLE IF EXISTS `iot_social_user`;
CREATE TABLE `iot_social_user`  (
  `social_user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '第三方系统用户表主键',
  `uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方系统的唯一ID',
  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方用户来源',
  `access_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户的授权令牌',
  `expire_in` int(11) NULL DEFAULT NULL COMMENT '第三方用户的授权令牌的有效期（部分平台可能没有）',
  `refresh_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '刷新令牌(部分平台可能没有)',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 open id（部分平台可能没有）',
  `uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 ID(部分平台可能没有)',
  `access_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个别平台的授权信息（部分平台可能没有）',
  `union_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 union id(部分平台可能没有)',
  `scope` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户授予的权限(部分平台可能没有)',
  `token_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个别平台的授权信息（部分平台可能没有）',
  `id_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'id token（部分平台可能没有）',
  `mac_algorithm` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性（部分平台可能没有）',
  `mac_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性(部分平台可能没有)',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的授权code（部分平台可能没有）',
  `oauth_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性(部分平台可能没有)',
  `oauth_token_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性(部分平台可能没有)',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记位(0代表存在,2代表删除)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '绑定状态(0:未绑定,1:绑定)',
  `sys_user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '用户性别',
  UNIQUE INDEX `iot_social_user_pk`(`social_user_id`) USING BTREE,
  UNIQUE INDEX `iot_social_user_unique_key`(`uuid`, `source`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方登录用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_social_user
-- ----------------------------

-- ----------------------------
-- Table structure for iot_things_model
-- ----------------------------
DROP TABLE IF EXISTS `iot_things_model`;
CREATE TABLE `iot_things_model`  (
  `model_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物模型ID',
  `model_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物模型名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `identifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标识符，产品下唯一',
  `type` tinyint(1) NOT NULL COMMENT '模型类别（1-属性，2-功能，3-事件）',
  `datatype` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据类型（integer、decimal、string、bool、array、enum）',
  `specs` json NULL COMMENT '数据定义',
  `is_chart` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否图表展示（0-否，1-是）',
  `is_monitor` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否实时监测（0-否，1-是）',
  `is_history` tinyint(1) NULL DEFAULT 0 COMMENT '是否历史存储（0-否，1-是）',
  `is_readonly` tinyint(1) NULL DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)',
  `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `temp_slave_id` bigint(20) NULL DEFAULT NULL COMMENT '从机id',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式',
  `reverse_formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '控制公式',
  `reg_addr` int(255) NULL DEFAULT NULL COMMENT '寄存器地址值',
  `bit_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位定义选项',
  `value_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '解析类型 1.数值 2.选项',
  `is_params` int(1) NULL DEFAULT NULL COMMENT '是否是计算参数',
  PRIMARY KEY (`model_id`) USING BTREE,
  INDEX `iot_things_model_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_things_model_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_things_model_index_model_order`(`model_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 440 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_things_model
-- ----------------------------
INSERT INTO `iot_things_model` VALUES (113, '设备开关', 41, '温湿度智能开关', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 0, 0, 1, 0, 9, '0', NULL, '2022-08-14 00:06:53', '', '2023-03-31 23:43:43', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (125, '空气温度', 41, '温湿度智能开关', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 1, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (126, '空气湿度', 41, '温湿度智能开关', 1, 'admin', 'humidity', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}', 1, 1, 1, 1, 3, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:12', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (127, '二氧化碳', 41, '温湿度智能开关', 1, 'admin', 'co2', 1, 'integer', '{\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}', 1, 1, 1, 1, 2, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:17', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (128, '室内亮度', 41, '温湿度智能开关', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 4, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:08', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (129, '运行档位', 41, '温湿度智能开关', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 0, 0, 1, 0, 8, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:49', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (130, '灯光色值', 41, '温湿度智能开关', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 0, 0, 1, 0, 5, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:03', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (131, '屏显消息', 41, '温湿度智能开关', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 7, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:54', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (132, '上报数据', 41, '温湿度智能开关', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 0, 0, 0, 0, 10, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (134, '环境温度过高', 41, '温湿度智能开关', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 0, 0, 1, 0, 0, '0', NULL, '2022-11-05 23:56:29', '', '2023-03-31 23:44:25', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (135, '设备发生异常', 41, '温湿度智能开关', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, '0', NULL, '2022-11-05 23:56:29', '', '2023-03-31 23:44:29', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (161, '子设备', 96, '网关产品', 1, 'admin', 'device', 1, 'array', '{\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_irc\", \"name\": \"射频遥控\", \"order\": 1, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"arrayType\": \"object\", \"arrayCount\": 5}', 0, 0, 0, 0, 10, '0', NULL, '2023-02-25 22:51:53', '', '2023-04-03 22:48:14', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (162, '功能分组', 96, '网关产品', 1, 'admin', 'group', 1, 'object', '{\"type\": \"object\", \"params\": [{\"id\": \"group_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"group_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_gear\", \"name\": \"运行档位\", \"order\": 5, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_switch\", \"name\": \"设备开关\", \"order\": 4, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_irc\", \"name\": \"射频遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控配对\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}]}', 0, 0, 0, 0, 9, '0', NULL, '2023-02-25 22:51:53', '', '2023-04-03 22:48:20', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (163, '空气温度', 96, '网关产品', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:03', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (164, '设备开关', 96, '网关产品', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 0, 0, 1, 0, 8, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:07:20', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (165, '运行档位', 96, '网关产品', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 0, 0, 1, 0, 7, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:45', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (166, '灯光色值', 96, '网关产品', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 0, 0, 1, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (167, '上报监测数据', 96, '网关产品', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 0, 0, 0, 0, 11, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:05:41', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (168, '环境温度过高', 96, '网关产品', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 0, 0, 1, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:15', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (169, '设备发生异常', 96, '网关产品', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:20', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (170, '屏显消息', 96, '网关产品', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, '0', NULL, '2023-02-25 22:52:35', '', '2023-03-31 16:07:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (171, '设备重启', 96, '网关产品', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 0, 0, 1, 0, 0, '0', NULL, '2023-02-25 22:52:35', '', '2023-04-01 23:41:39', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (174, '室内亮度', 96, '网关产品', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 0, '0', NULL, '2023-02-26 00:56:39', '', '2023-03-31 16:07:45', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (175, '设备重启', 41, '智能开关', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 0, 0, 1, 0, 6, '0', NULL, '2023-02-26 02:20:40', '', '2023-04-01 23:40:05', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (237, '漏水值', 112, 'modbus协议组产品', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 0, 0, '0', 'admin', '2023-02-28 16:40:13', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (238, '温度', 112, 'modbus协议组产品', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"°C\"}', 1, 1, 1, 0, 0, '0', 'admin', '2023-02-28 16:40:13', '', NULL, NULL, 2, '%s/10', NULL, 0, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (239, '湿度', 112, 'modbus协议组产品', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"°C\"}', 1, 1, 1, 0, 0, '0', 'admin', '2023-02-28 16:40:13', '', NULL, NULL, 2, '%s/10', NULL, 1, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (240, '相电压Ua', 112, 'modbus协议组产品', 1, 'admin', '0', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 0, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (241, '线电压Uca', 112, 'modbus协议组产品', 1, 'admin', '1', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 1, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (242, 'A相电流 ', 112, 'modbus协议组产品', 1, 'admin', '2', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*CT*0.001', '', 2, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (243, 'D1开入状态检测 ', 112, 'modbus协议组产品', 1, 'admin', '3', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"断开\", \"value\": \"0\"}, {\"text\": \"闭合\", \"value\": \"1\"}]}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 3, '0:断开/1:闭合', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (244, 'A相有功功率 ', 112, 'modbus协议组产品', 1, 'admin', '4', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 4, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (245, 'A相功率因数', 112, 'modbus协议组产品', 1, 'admin', '5', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*0.001', '', 5, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (246, 'A相无功功率 ', 112, 'modbus协议组产品', 1, 'admin', '6', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 6, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (247, 'A相视在功率 ', 112, 'modbus协议组产品', 1, 'admin', '7', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 7, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (248, '相电压Ub', 112, 'modbus协议组产品', 1, 'admin', '8', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 8, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (249, '线电压Uab', 112, 'modbus协议组产品', 1, 'admin', '9', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 9, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (250, 'B相电流 ', 112, 'modbus协议组产品', 1, 'admin', '10', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*CT*0.001', '', 10, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (251, 'D0开出状态检测 ', 112, 'modbus协议组产品', 1, 'admin', '11', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"断开\", \"value\": \"0\"}, {\"text\": \"闭合\", \"value\": \"1\"}]}', 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 11, '0:断开/1:闭合', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (252, 'B相有功功率 ', 112, 'modbus协议组产品', 1, 'admin', '12', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 12, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (253, 'B相功率因数', 112, 'modbus协议组产品', 1, 'admin', '13', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*0.001', '', 13, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (254, 'B相无功功率 ', 112, 'modbus协议组产品', 1, 'admin', '14', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 14, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (255, 'B相视在功率 ', 112, 'modbus协议组产品', 1, 'admin', '15', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 15, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (256, '相电压Uc ', 112, 'modbus协议组产品', 1, 'admin', '16', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 16, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (257, '线电压Ubc', 112, 'modbus协议组产品', 1, 'admin', '17', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 17, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (258, 'C相电流', 112, 'modbus协议组产品', 1, 'admin', '18', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*CT*0.001', '', 18, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (259, '零地电压', 112, 'modbus协议组产品', 1, 'admin', '19', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 19, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (260, 'C相有功功率', 112, 'modbus协议组产品', 1, 'admin', '20', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 20, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (261, 'C相功率因数', 112, 'modbus协议组产品', 1, 'admin', '21', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*0.001', '', 21, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (262, 'C相无功功率 ', 112, 'modbus协议组产品', 1, 'admin', '22', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 22, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (263, 'C相视在功率 ', 112, 'modbus协议组产品', 1, 'admin', '23', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}', 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 23, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (264, '三相平均相电压 ', 112, 'modbus协议组产品', 1, 'admin', '24', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 24, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (265, '三相平均线电压 ', 112, 'modbus协议组产品', 1, 'admin', '25', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.1V\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*0.1', '', 25, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (266, '三相平均相电流 ', 112, 'modbus协议组产品', 1, 'admin', '26', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*CT*0.001', '', 26, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (267, '频率 ', 112, 'modbus协议组产品', 1, 'admin', '27', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001Hz\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*0.001', '', 27, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (268, '三相有功功率 ', 112, 'modbus协议组产品', 1, 'admin', '28', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"W\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 28, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (269, '三相总功率因数 ', 112, 'modbus协议组产品', 1, 'admin', '29', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"0.001\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*0.001', '', 29, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (270, '三相无功功率 ', 112, 'modbus协议组产品', 1, 'admin', '30', 1, 'integer', '{\"max\": \"32768\", \"min\": \"-32768\", \"step\": 0, \"type\": \"integer\", \"unit\": \"var\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 30, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (271, '三相视在功率 ', 112, 'modbus协议组产品', 1, 'admin', '31', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"VA\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*PT*CT', '', 31, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (272, '零序电流 ', 112, 'modbus协议组产品', 1, 'admin', '32', 1, 'integer', '{\"max\": \"65535\", \"min\": \"0\", \"step\": 0, \"type\": \"integer\", \"unit\": \"mA\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '%s*CT*0.001', '', 32, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (273, '正向有功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '33', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 33, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (274, '正向有功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '34', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 34, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (275, '负向有功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '35', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 35, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (276, '负向有功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '36', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 36, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (277, '正向无功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '37', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 37, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (278, '正向无功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '38', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 38, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (279, '负向无功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '39', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 39, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (280, '负向无功电能累加值低高字', 112, 'modbus协议组产品', 1, 'admin', '40', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 40, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (281, '正向有功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '49', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 49, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (282, '正向有功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '50', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 50, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (283, '负向有功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '51', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 51, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (284, '负向有功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '52', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 52, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (285, '正向无功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '53', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 53, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (286, '正向无功电能累加值低位字', 112, 'modbus协议组产品', 1, 'admin', '54', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 54, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (287, '负向无功电能累加值高位字', 112, 'modbus协议组产品', 1, 'admin', '55', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 55, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (288, '负向无功电能累加值低高字', 112, 'modbus协议组产品', 1, 'admin', '56', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 56, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (289, '本机地址', 112, 'modbus协议组产品', 1, 'admin', '768', 1, 'integer', '{\"max\": \"247\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 768, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (290, '被测系统负载接线方式', 112, 'modbus协议组产品', 1, 'admin', '769', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"三相四线\", \"value\": \"0\"}, {\"text\": \"一相二线\", \"value\": \"1\"}, {\"text\": \"三相三线\", \"value\": \"2\"}, {\"text\": \"三相三线平衡\", \"value\": \"3\"}, {\"text\": \"一相三线\", \"value\": \"4\"}, {\"text\": \"三相四线平衡\", \"value\": \"5\"}]}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 769, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (291, '校验位', 112, 'modbus协议组产品', 1, 'admin', '771', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"N\", \"value\": \"0\"}, {\"text\": \"O\", \"value\": \"1\"}, {\"text\": \"E\", \"value\": \"2\"}]}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 771, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (292, '波特率', 112, 'modbus协议组产品', 1, 'admin', '772', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"1200\", \"value\": \"0\"}, {\"text\": \"2400\", \"value\": \"1\"}, {\"text\": \"4800\", \"value\": \"2\"}, {\"text\": \"9600\", \"value\": \"3\"}, {\"text\": \"19200\", \"value\": \"4\"}]}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 772, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (293, '电压范围', 112, 'modbus协议组产品', 1, 'admin', '773', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"150\", \"value\": \"0\"}, {\"text\": \"600\", \"value\": \"1\"}]}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 773, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (294, 'PT', 112, 'modbus协议组产品', 1, 'admin', '775', 1, 'integer', '{\"max\": \"10000\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 775, '', NULL, 1);
INSERT INTO `iot_things_model` VALUES (295, 'CT', 112, 'modbus协议组产品', 1, 'admin', '777', 1, 'integer', '{\"max\": \"10000\", \"min\": \"1\", \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 777, '', NULL, 1);
INSERT INTO `iot_things_model` VALUES (296, '缺相电压值', 112, 'modbus协议组产品', 1, 'admin', '789', 1, 'integer', '{\"max\": \"100\", \"min\": \"1\", \"step\": 1, \"type\": \"integer\", \"unit\": \"无\"}', 0, 1, 0, 1, 0, '0', '', '2023-02-28 16:40:13', '', NULL, NULL, 11, '', '', 789, '', NULL, NULL);
INSERT INTO `iot_things_model` VALUES (385, '射频遥控', 41, '★智能开关', 1, 'admin', 'irc', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}', 0, 0, 1, 0, 11, '0', 'admin', '2023-03-31 23:46:36', '', '2023-04-13 01:38:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (439, '上报状态', 41, '★智能开关产品', 1, 'admin', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 0, 0, 0, 0, 12, '0', 'admin', '2023-04-13 01:39:31', '', '2023-04-13 01:39:42', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for iot_things_model_template
-- ----------------------------
DROP TABLE IF EXISTS `iot_things_model_template`;
CREATE TABLE `iot_things_model_template`  (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物模型ID',
  `template_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物模型名称',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `identifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标识符，产品下唯一',
  `type` tinyint(1) NOT NULL COMMENT '模型类别（1-属性，2-功能，3-事件）',
  `datatype` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据类型（integer、decimal、string、bool、array、enum）',
  `specs` json NULL COMMENT '数据定义',
  `is_sys` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）',
  `is_chart` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否图表展示（0-否，1-是）',
  `is_monitor` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否实时监测（0-否，1-是）',
  `is_history` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否历史存储 (0-否，1-是）',
  `is_readonly` tinyint(1) NULL DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)',
  `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `temp_slave_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '从机id',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式',
  `reverse_formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '控制公式',
  `reg_addr` int(255) NULL DEFAULT NULL COMMENT '寄存器地址值',
  `bit_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位定义选项',
  `value_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '解析类型 1.数值 2.选项',
  `is_params` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '是否是计算参数,默认否 0=否，1=是',
  PRIMARY KEY (`template_id`) USING BTREE,
  INDEX `iot_things_model_template_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_things_model_template_index_model_order`(`model_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 324 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_things_model_template
-- ----------------------------
INSERT INTO `iot_things_model_template` VALUES (1, '空气温度', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 1, 4, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:12:06', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (2, '空气湿度', 1, 'admin', 'humidity', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}', 1, 1, 1, 1, 1, 3, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:12:02', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (3, '二氧化碳', 1, 'admin', 'co2', 1, 'integer', '{\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:57', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (4, '室内亮度', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:53', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (5, '设备开关', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 1, 0, 0, 1, 0, 5, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (6, '运行档位', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 1, 0, 0, 1, 0, 6, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:43', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (7, '灯光色值', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 1, 0, 0, 1, 0, 2, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (8, '屏显消息', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 1, 0, 1, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:32', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (9, '上报监测数据', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 1, 0, 0, 0, 0, 9, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:25', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (10, '环境温度过高', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 0, 0, 1, 0, 8, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:19', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (11, '设备发生异常', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 1, 0, 7, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:16', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (12, '光照', 1, 'admin', 'light', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 0, 1, 1, 1, 1, 0, '0', 'wumei', '2022-05-07 09:41:17', 'admin', '2023-04-10 01:11:12', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (13, '压力', 1, 'admin', 'pressure', 1, 'decimal', '{\"max\": 200, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"帕斯卡\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 22:39:18', 'admin', '2023-04-10 01:11:05', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (14, '设备重启', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 1, 0, 0, 1, 0, 0, '0', 'admin', '2023-02-20 23:15:25', 'admin', '2023-04-10 01:11:08', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (15, '电池电压', 1, 'admin', 'voltage', 1, 'decimal', '{\"max\": 5, \"min\": 0, \"step\": 0.001, \"type\": \"decimal\", \"unit\": \"V\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:17:43', 'admin', '2023-04-10 01:10:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (16, '饮水量', 1, 'admin', 'shuiliang', 1, 'integer', '{\"max\": 500, \"min\": 80, \"step\": 1, \"type\": \"integer\", \"unit\": \"ML\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:18:39', 'admin', '2023-04-10 01:10:52', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (17, '灯光', 1, 'admin', 'light', 1, 'integer', '{\"max\": 1000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"Lux\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:19:23', 'admin', '2023-04-10 01:10:49', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (18, '长度', 1, 'admin', 'length', 1, 'integer', '{\"max\": 2000, \"min\": 1, \"step\": 5, \"type\": \"integer\", \"unit\": \"M\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:20:03', 'admin', '2023-04-10 01:10:44', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (19, '心率', 1, 'admin', 'heart_rate', 1, 'integer', '{\"max\": 250, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:21:46', 'admin', '2023-04-10 01:12:40', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (20, '光照强度', 1, 'admin', 'light_level', 1, 'integer', '{\"max\": 89.2, \"min\": 2.5, \"step\": 0.1, \"type\": \"integer\", \"unit\": \"L/g\"}', 1, 1, 1, 1, 1, 0, '0', 'admin', '2023-02-20 23:24:36', 'admin', '2023-04-10 01:10:35', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (21, '状态灯色', 1, 'admin', 'color', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"红色\", \"value\": \"0\"}, {\"text\": \"绿色\", \"value\": \"1\"}, {\"text\": \"蓝色\", \"value\": \"2\"}, {\"text\": \"黄色\", \"value\": \"3\"}]}', 1, 0, 0, 1, 0, 0, '0', 'admin', '2023-02-20 23:26:24', 'admin', '2023-04-10 01:10:32', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (22, '子设备', 1, 'admin', 'device', 2, 'array', '{\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"datatype\": {\"type\": \"bool\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"trueText\": \"打开\", \"arrayType\": \"int\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}], \"arrayType\": \"object\", \"arrayCount\": 5}', 1, 0, 0, 0, 0, 10, '0', 'admin', '2023-02-24 01:10:43', 'admin', '2023-04-13 01:33:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (23, '功能分组', 1, 'admin', 'group', 2, 'object', '{\"type\": \"object\", \"params\": [{\"id\": \"group_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"group_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_gear\", \"name\": \"运行档位\", \"order\": 5, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_switch\", \"name\": \"设备开关\", \"order\": 4, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_irc\", \"name\": \"红外遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}]}', 1, 0, 0, 0, 0, 11, '0', 'admin', '2023-02-25 22:41:43', 'admin', '2023-04-10 01:10:19', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (201, '频率 ', 1, 'admin', 'frequency', 2, 'integer', '{\"max\": 65535, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"0.001Hz\"}', 1, 0, 0, 1, 0, 0, '0', '', '2023-02-28 16:08:06', 'admin', '2023-04-10 03:37:11', NULL, '3#3', '%s*0.001', '', 27, '', NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (225, '校验位', 1, 'admin', 'check', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"N\", \"value\": \"0\"}, {\"text\": \"O\", \"value\": \"1\"}, {\"text\": \"E\", \"value\": \"2\"}]}', 1, 0, 0, 1, 1, 0, '0', '', '2023-02-28 16:08:08', 'admin', '2023-04-10 21:36:01', NULL, '3#3', '', '', 771, '', NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (226, '波特率', 1, 'admin', 'baud', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"1200\", \"value\": \"0\"}, {\"text\": \"2400\", \"value\": \"1\"}, {\"text\": \"4800\", \"value\": \"2\"}, {\"text\": \"9600\", \"value\": \"3\"}, {\"text\": \"19200\", \"value\": \"4\"}]}', 1, 0, 0, 1, 1, 0, '0', '', '2023-02-28 16:08:09', 'admin', '2023-04-10 03:37:32', NULL, '3#3', '', '', 772, '', NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (227, '电压', 1, 'admin', 'voltage', 1, 'integer', '{\"max\": 6, \"min\": 0.1, \"step\": 0.1, \"type\": \"integer\", \"unit\": \"v\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-02-28 16:08:09', 'admin', '2023-04-10 03:37:16', NULL, '3#3', '', '', 773, '', NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (236, '射频遥控', 1, 'admin', 'irc', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}', 1, 0, 0, 1, 0, 0, '0', 'admin', '2023-03-31 23:46:20', 'admin', '2023-04-10 01:09:46', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (250, '漏水值', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"度\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-04-11 22:35:36', '', NULL, NULL, '1#1', NULL, NULL, 0, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (251, '温度', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"°\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-04-11 22:36:10', '', NULL, NULL, '1#2', NULL, NULL, 0, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (252, '电量', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-04-11 22:36:27', '', NULL, NULL, '1#11', NULL, NULL, 1, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (323, '上报状态', 1, 'admin', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 1, 0, 0, 0, 0, 0, '0', 'admin', '2023-04-13 01:35:42', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for iot_var_temp
-- ----------------------------
DROP TABLE IF EXISTS `iot_var_temp`;
CREATE TABLE `iot_var_temp`  (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `template_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '模板名称',
  `type` tinyint(20) NULL DEFAULT NULL,
  `polling_method` tinyint(20) NOT NULL COMMENT '采集方式 1.云端轮询 2.云端边缘计算',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '模板所属用户',
  `slave_total` bigint(20) NULL DEFAULT NULL COMMENT '从机总数',
  `point_total` bigint(20) NULL DEFAULT NULL COMMENT '总采集点数',
  `share` tinyint(20) NULL DEFAULT NULL COMMENT '是否分享',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '更新用户',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备采集变量模板对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_var_temp
-- ----------------------------
INSERT INTO `iot_var_temp` VALUES (1, 'modbus从机组', NULL, 0, NULL, NULL, NULL, NULL, '2023-02-28 14:20:29', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for iot_var_temp_salve
-- ----------------------------
DROP TABLE IF EXISTS `iot_var_temp_salve`;
CREATE TABLE `iot_var_temp_salve`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `device_temp_id` bigint(20) NOT NULL COMMENT '关联的模板id',
  `slave_addr` int(20) NOT NULL COMMENT '从机编号',
  `slave_index` int(20) NULL DEFAULT NULL,
  `slave_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '从机ip地址',
  `slave_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '从机名称',
  `slave_port` int(20) NULL DEFAULT NULL COMMENT '从机端口',
  `addr_start` bigint(20) NOT NULL COMMENT '寄存器起始地址(10进制)',
  `addr_end` bigint(20) NOT NULL COMMENT '寄存器结束地址(10进制)',
  `packet_length` int(20) NOT NULL DEFAULT 32 COMMENT '寄存器批量读取个数',
  `timer` bigint(20) NOT NULL COMMENT '批量获取轮询时间(默认5分钟)',
  `status` tinyint(20) NOT NULL COMMENT '状态 0-启动 1-失效',
  `code` int(20) NULL DEFAULT NULL COMMENT '功能编码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '更新用户',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '变量模板设备从机对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_var_temp_salve
-- ----------------------------
INSERT INTO `iot_var_temp_salve` VALUES (1, 1, 1, NULL, NULL, '漏水器', NULL, 0, 0, 1, 300, 0, 4, '2023-02-28 14:43:24', NULL, '2023-04-11 19:02:06', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (2, 1, 2, NULL, NULL, '温湿度计', NULL, 0, 1, 2, 300, 0, 4, '2023-02-28 15:25:00', NULL, '2023-04-11 19:02:04', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (3, 1, 11, NULL, NULL, '电量仪', NULL, 0, 789, 20, 300, 0, 3, '2023-02-28 15:32:36', NULL, '2023-04-11 19:02:00', NULL, NULL);

-- ----------------------------
-- Table structure for media_server
-- ----------------------------
DROP TABLE IF EXISTS `media_server`;
CREATE TABLE `media_server`  (
 `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '流媒体配置ID',
 `server_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器标识',
 `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
 `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
 `enabled` tinyint(1) NULL DEFAULT NULL COMMENT '使能开关',
 `protocol` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '默认播放协议',
 `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器ip',
 `domain` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器域名',
 `hookurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '回调服务器地址',
 `secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体密钥',
 `port_http` int(11) NOT NULL DEFAULT 0 COMMENT 'http端口',
 `port_https` int(11) NOT NULL DEFAULT 0 COMMENT 'https端口',
 `port_rtmp` int(11) NOT NULL DEFAULT 0 COMMENT 'rtmp端口',
 `port_rtsp` int(11) NOT NULL DEFAULT 0 COMMENT 'rtsp端口',
 `rtp_proxy_port` int(11) NOT NULL DEFAULT 0 COMMENT 'RTP收流端口',
 `rtp_enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否使用多端口模式',
 `rtp_port_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'rtp端口范围',
 `record_port` int(11) NOT NULL DEFAULT 0 COMMENT '录像服务端口',
 `auto_config` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否自动同步配置ZLM',
 `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态',
 `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
 `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
 `create_time` datetime(0) NOT NULL COMMENT '创建时间',
 `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
 `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
 `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流媒体服务器配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of media_server
-- ----------------------------
INSERT INTO `media_server` VALUES (1,'test888', 1, 'admin', 1, 'http', '1.1.1.1', 'fastbee', 'java:8080','035c73f7-bb6b-4889-a715-d9eb2d192xxx', 8082, 8443, 1935, 554, 0, 1,'30000,30100', 18081, 1, 1,'0', '', '2021-12-29 13:12:42', '', '2023-02-26 22:35:09', 'admin');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `news_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `img_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '封面',
  `is_top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶(0-置顶 1-置顶)',
  `is_banner` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否banner(0-是banner 1-不是banner)',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `category_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '新闻状态（0-未发布，1-已发布）',
  `author` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`news_id`) USING BTREE,
  INDEX `news_index_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '新闻资讯' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (1, '广告图一', '<p>请后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-215805.png', 0, 1, 2, '相关产品', 1, '物美智能', '0', '', '2022-05-12 12:13:40', '', '2022-05-12 12:13:40', '物美智能');
INSERT INTO `news` VALUES (2, '广告图二', '<p>请后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-215852.png', 0, 1, 1, '新闻资讯', 1, '物美智能', '0', '', '2022-05-12 12:13:42', '', '2022-05-12 12:13:42', '物美智能');
INSERT INTO `news` VALUES (3, '广告图三', '<p>后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-224553.png', 0, 1, 2, '相关产品', 1, '物美智能', '0', '', '2022-05-12 12:13:44', '', '2022-05-12 12:13:44', '物美智能');
INSERT INTO `news` VALUES (4, '物美智能-快速搭建物联网和智能家居平台', '<p class=\"ql-align-justify\">物美智能 wumei-smart 是一个简单易用的生活物联网平台。可用于搭建物联网平台以及二次开发和学习。设备接入使用EMQX消息服务器，加密认证；后端采用Spring boot；前端采用Vue；移动端采用Uniapp；数据库采用Mysql和Redis；设备端支持ESP32、ESP8266、树莓派等；</p><p class=\"ql-align-justify\"><img src=\"/prod-api/profile/upload/2022/05/11/5f479f25-b85d-4c9f-b6a7-deadd2cdec76.png\"></p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\"><strong>系统功能介绍</strong></p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.权限管理： 用户管理、部门管理、岗位管理、菜单管理、角色管理、字典和参数管理等</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.系统监控： 操作日志、登录日志、系统日志、在线用户、服务监控、连接池监控、缓存监控等</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.产品管理： 产品、产品物模型、产品分类、产品固件、授权码等</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.设备管理： 控制、分组、定时、日志、统计、定位、OTA升级、影子模式、实时监测、加密认证等</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">5.EMQ管理： Mqtt客户端、监听器、消息主题、消息订阅、插件管理、规则引擎、资源</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">6.硬件 SDK： 支持WIFI和MQTT连接、物模型响应、实时监测、定时上报监测数据、AES加密、NTP时间等</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">7.物模型管理： 属性（设备状态和监测数据），功能（执行特定任务），事件（设备主动上报给云端）</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">8.其他（开发中）：第三方登录，设备分享、设备告警、场景联动（进度50%），智能音箱、多租户、APP界面自定义（进度40%），时序数据库、分布式集群部署、Granfa监控（进度30%），视频流处理、桌面端模拟器/监控、安卓端模拟器/监控（进度20%）</p><p class=\"ql-align-justify\"><strong>﻿</strong></p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\"><strong>硬件设备接入流程</strong></p><p><br></p><p>1.设备认证：加密认证、简单认证和emqx支持的多种认证方式。</p><p class=\"ql-align-justify\">2.设备交互：发布和订阅物模型、设备信息、设备升级和时钟同步等mqtt主题</p>', '/iot/tool/download?fileName=/profile/iot/1/2022-0508-133031.png', 1, 0, 2, '相关产品', 1, '物美智能', '0', '', '2022-05-12 12:13:46', '', '2022-05-12 12:13:46', '物美智能');
INSERT INTO `news` VALUES (5, '2022年中国物联网全景图产业链上中下游市场及企业剖析', '<p>后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-224151.png', 1, 0, 1, '新闻资讯', 1, '物美智能', '0', '', '2022-05-12 12:13:48', '', '2022-05-12 12:13:48', '物美智能');
INSERT INTO `news` VALUES (6, 'Arm打造物联网全面解决方案 携手合作伙伴共探智能未来', '<p>后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-224352.png', 1, 0, 1, '新闻资讯', 1, '物美智能', '0', '', '2022-05-12 12:13:50', '', '2022-05-12 12:13:50', '物美智能');
INSERT INTO `news` VALUES (7, '使用ESP32开发板，快速学习物联网开发', '<p>请后台添加内容</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0428-130824.jpg', 1, 0, 2, '相关产品', 1, '物美智能', '0', '', '2022-05-12 12:13:53', '', '2022-05-12 12:13:53', '物美智能');
INSERT INTO `news` VALUES (8, '物联网赛道观察之无源物联网', '<p>无源物联网，即终端无外接能量源，采用获取环境能量的方式进行供能的物联网技术。在当前物联网技术发展条件下，终端覆盖率是一个亟待解决的问题，而无源物联网凭借其极低的部署和维护成本、灵活多变的应用场景成为解决更广范围内终端供能需求问题、实现“千亿级互联”愿景的关键。</p><p><br></p><p>无源物联网技术的发展最终有赖于环境能量采集、低功耗计算与反向散射等低功耗通讯技术的进步。目前无源物联网应用较为成熟的路线主要包括射频识别技术（RFID）与近场通信技术（NFC）两类，覆盖仓储物流、智能制造、智慧零售、资产管理、物业服务等多元应用场景。未来，随着物联网行业的碎片化整合以及以Bluetooth、5G、LoRa等为媒介进行能量采集与信息传输的技术路线的逐渐成熟，当前困扰行业的诸多问题将会逐步得到解决，随之而来的是更包罗多样的无源终端需求与极具潜力的应用场景。</p>', '/iot/tool/download?fileName=/profile/iot/118/2022-0424-215643.png', 1, 0, 1, '新闻资讯', 1, '物美智能', '0', '', '2022-05-12 12:13:55', '', '2022-05-12 12:13:55', '物美智能');

-- ----------------------------
-- Table structure for news_category
-- ----------------------------
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category`  (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `order_num` int(2) NOT NULL COMMENT '显示顺序',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '新闻分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news_category
-- ----------------------------
INSERT INTO `news_category` VALUES (1, '新闻资讯', 3, '0', '', '2022-04-11 20:53:55', '', '2022-04-13 15:30:22', '新闻资讯信息');
INSERT INTO `news_category` VALUES (2, '相关产品', 2, '0', '', '2022-04-11 20:54:16', '', '2022-04-13 15:30:15', '相关产品推荐');

-- ----------------------------
-- Table structure for oauth_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`  (
  `token_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authentication` blob NULL,
  `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_access_token
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_approvals
-- ----------------------------
DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals`  (
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `clientId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expiresAt` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `lastModifiedAt` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_approvals
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID',
  `resource_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端所能访问的资源id集合,多个资源时用逗号(,)分隔',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端秘钥',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔',
  `authorized_grant_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权模式，可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔',
  `web_server_redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调地址',
  `authorities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) NULL DEFAULT NULL COMMENT '设定客户端的access_token的有效时间值(单位:秒)',
  `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT '设定客户端的refresh_token的有效时间值(单位:秒)',
  `additional_information` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据',
  `autoapprove` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设置用户是否自动Approval操作, 默认值为 \'false\', 可选值包括 \'true\',\'false\', \'read\',\'write\'. \n该字段只适用于grant_type=\"authorization_code\"的情况,当用户登录成功后,若该值为\'true\'或支持的scope值,则会跳过用户Approve的页面, 直接授权. ',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '1=小度(DuerOS),2=天猫精灵(ALiGenie),3=小米小爱',
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('speaker', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'client_credentials,password,authorization_code,implicit,refresh_token', 'https://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'false', 1);
INSERT INTO `oauth_client_details` VALUES ('tianmao', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'authorization_code,refresh_token', '\r\nhttps://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'true', 2);
INSERT INTO `oauth_client_details` VALUES ('xiaoai', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'authorization_code,refresh_token', 'https://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'true', 3);
INSERT INTO `oauth_client_details` VALUES ('xiaoyi', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'authorization_code,refresh_token', 'https://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'false', 4);

-- ----------------------------
-- Table structure for oauth_client_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token`  (
  `token_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client_token
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_code
-- ----------------------------
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`  (
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authentication` blob NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_code
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`  (
  `token_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication` blob NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_refresh_token
-- ----------------------------

-- ----------------------------
-- Table structure for oss_config
-- ----------------------------
DROP TABLE IF EXISTS `oss_config`;
CREATE TABLE `oss_config`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `enable` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '使能标志',
  `enable_access` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '访问使能标志',
  `platform_type` int(10) NOT NULL DEFAULT 0 COMMENT '存储平台类型',
  `platform` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储平台标识',
  `access_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '访问密钥',
  `secret_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密钥',
  `region` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存仓库所在地域(end-point)',
  `bucket_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '桶名',
  `domain` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '访问域名，注意“/”结尾',
  `base_path` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '基础路径',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件存储配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oss_config
-- ----------------------------
INSERT INTO `oss_config` VALUES (1, '1', '1', 1, 'test', 'test', 'test', 'test', 'test', 'test', 'test', '0', '', '2023-04-05 22:56:37', '', NULL, NULL);
INSERT INTO `oss_config` VALUES (2, '1', '1', 2, 'test', 'test', 'test', 'test', 'test', 'test', 'test', '0', '', '2023-04-05 22:57:36', '', NULL, NULL);

-- ----------------------------
-- Table structure for oss_detail
-- ----------------------------
DROP TABLE IF EXISTS `oss_detail`;
CREATE TABLE `oss_detail`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件id',
  `url` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件访问地址',
  `size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小，单位字节',
  `filename` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `original_filename` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `base_path` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '基础存储路径',
  `path` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储路径',
  `ext` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `platform` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储平台',
  `th_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图访问路径',
  `th_filename` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图名称',
  `th_size` bigint(20) NULL DEFAULT NULL COMMENT '缩略图大小，单位字节',
  `object_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所属对象id',
  `object_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件所属对象类型，例如用户头像，评价图片',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oss_detail
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------
INSERT INTO `qrtz_cron_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME1', 'DEFAULT', '0/10 * * * * ?', 'Asia/Shanghai');
INSERT INTO `qrtz_cron_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME2', 'DEFAULT', '0/15 * * * * ?', 'Asia/Shanghai');
INSERT INTO `qrtz_cron_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME3', 'DEFAULT', '0/20 * * * * ?', 'Asia/Shanghai');
INSERT INTO `qrtz_cron_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME4', 'SYSTEM', '0 0/4 * * * ? ', 'Asia/Shanghai');
INSERT INTO `qrtz_cron_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME5', 'SYSTEM', '0 0/1 * * * ? ', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '已触发的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------
INSERT INTO `qrtz_job_details` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME1', 'DEFAULT', NULL, 'com.fastbee.quartz.util.QuartzDisallowConcurrentExecution', '0', '1', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000F5441534B5F50524F5045525449455373720020636F6D2E666173746265652E71756172747A2E646F6D61696E2E5379734A6F6200000000000000010200084C000A636F6E63757272656E747400124C6A6176612F6C616E672F537472696E673B4C000E63726F6E45787072657373696F6E71007E00094C000C696E766F6B6554617267657471007E00094C00086A6F6247726F757071007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C00076A6F624E616D6571007E00094C000D6D697366697265506F6C69637971007E00094C000673746174757371007E000978720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E00094C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C0006706172616D7371007E00034C000672656D61726B71007E00094C000B73656172636856616C756571007E00094C0008757064617465427971007E00094C000A75706461746554696D6571007E000C787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED85078707400007070707400013174000E302F3130202A202A202A202A203F74001172795461736B2E72794E6F506172616D7374000744454641554C547372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000001740018E7B3BBE7BB9FE9BB98E8AEA4EFBC88E697A0E58F82EFBC8974000133740001307800);
INSERT INTO `qrtz_job_details` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME2', 'DEFAULT', NULL, 'com.fastbee.quartz.util.QuartzDisallowConcurrentExecution', '0', '1', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000F5441534B5F50524F5045525449455373720020636F6D2E666173746265652E71756172747A2E646F6D61696E2E5379734A6F6200000000000000010200084C000A636F6E63757272656E747400124C6A6176612F6C616E672F537472696E673B4C000E63726F6E45787072657373696F6E71007E00094C000C696E766F6B6554617267657471007E00094C00086A6F6247726F757071007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C00076A6F624E616D6571007E00094C000D6D697366697265506F6C69637971007E00094C000673746174757371007E000978720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E00094C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C0006706172616D7371007E00034C000672656D61726B71007E00094C000B73656172636856616C756571007E00094C0008757064617465427971007E00094C000A75706461746554696D6571007E000C787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED85078707400007070707400013174000E302F3135202A202A202A202A203F74001572795461736B2E7279506172616D7328277279272974000744454641554C547372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000002740018E7B3BBE7BB9FE9BB98E8AEA4EFBC88E69C89E58F82EFBC8974000133740001317800);
INSERT INTO `qrtz_job_details` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME3', 'DEFAULT', NULL, 'com.fastbee.quartz.util.QuartzDisallowConcurrentExecution', '0', '1', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000F5441534B5F50524F5045525449455373720020636F6D2E666173746265652E71756172747A2E646F6D61696E2E5379734A6F6200000000000000010200084C000A636F6E63757272656E747400124C6A6176612F6C616E672F537472696E673B4C000E63726F6E45787072657373696F6E71007E00094C000C696E766F6B6554617267657471007E00094C00086A6F6247726F757071007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C00076A6F624E616D6571007E00094C000D6D697366697265506F6C69637971007E00094C000673746174757371007E000978720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E00094C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C0006706172616D7371007E00034C000672656D61726B71007E00094C000B73656172636856616C756571007E00094C0008757064617465427971007E00094C000A75706461746554696D6571007E000C787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED85078707400007070707400013174000E302F3230202A202A202A202A203F74003872795461736B2E72794D756C7469706C65506172616D7328277279272C20747275652C20323030304C2C203331362E3530442C203130302974000744454641554C547372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000003740018E7B3BBE7BB9FE9BB98E8AEA4EFBC88E5A49AE58F82EFBC8974000133740001317800);
INSERT INTO `qrtz_job_details` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME4', 'SYSTEM', NULL, 'com.fastbee.quartz.util.QuartzJobExecution', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000F5441534B5F50524F5045525449455373720020636F6D2E666173746265652E71756172747A2E646F6D61696E2E5379734A6F6200000000000000010200084C000A636F6E63757272656E747400124C6A6176612F6C616E672F537472696E673B4C000E63726F6E45787072657373696F6E71007E00094C000C696E766F6B6554617267657471007E00094C00086A6F6247726F757071007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C00076A6F624E616D6571007E00094C000D6D697366697265506F6C69637971007E00094C000673746174757371007E000978720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E00094C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C0006706172616D7371007E00034C000672656D61726B71007E00094C000B73656172636856616C756571007E00094C0008757064617465427971007E00094C000A75706461746554696D6571007E000C787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B59741903000078707708000001869759B0B878707400007070707400013074000E3020302F34202A202A202A203F2074002070726F7047657453657276696365496D706C2E666574636850726F706572747974000653595354454D7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000047400126D6F64627573E4BA91E7ABAFE8BDAEE8AFA274000131740001307800);
INSERT INTO `qrtz_job_details` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME5', 'SYSTEM', NULL, 'com.fastbee.quartz.util.QuartzDisallowConcurrentExecution', '0', '1', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000F5441534B5F50524F5045525449455373720020636F6D2E666173746265652E71756172747A2E646F6D61696E2E5379734A6F6200000000000000010200084C000A636F6E63757272656E747400124C6A6176612F6C616E672F537472696E673B4C000E63726F6E45787072657373696F6E71007E00094C000C696E766F6B6554617267657471007E00094C00086A6F6247726F757071007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C00076A6F624E616D6571007E00094C000D6D697366697265506F6C69637971007E00094C000673746174757371007E000978720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E00094C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C0006706172616D7371007E00034C000672656D61726B71007E00094C000B73656172636856616C756571007E00094C0008757064617465427971007E00094C000A75706461746554696D6571007E000C787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B5974190300007870770800000187118D07E078707400007070707400013174000E3020302F31202A202A202A203F207400286465766963654A6F622E74696D696E6755706461746544657669636553746174757353746174757374000653595354454D7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000000000005740012E8AEBEE5A487E5AE9AE697B6E4BBBBE58AA174000131740001307800);

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
INSERT INTO `qrtz_locks` VALUES ('RuoyiScheduler', 'STATE_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('RuoyiScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
INSERT INTO `qrtz_scheduler_state` VALUES ('RuoyiScheduler', 'beecue1680023933011', 1680023961720, 15000);

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name`, `job_name`, `job_group`) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '触发器详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------
INSERT INTO `qrtz_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME1', 'DEFAULT', 'TASK_CLASS_NAME1', 'DEFAULT', NULL, 1680023970000, 1680023960000, 5, 'WAITING', 'CRON', 1680023946000, 0, NULL, 2, '');
INSERT INTO `qrtz_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME2', 'DEFAULT', 'TASK_CLASS_NAME2', 'DEFAULT', NULL, 1680023955000, -1, 5, 'PAUSED', 'CRON', 1680023947000, 0, NULL, 2, '');
INSERT INTO `qrtz_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME3', 'DEFAULT', 'TASK_CLASS_NAME3', 'DEFAULT', NULL, 1680023960000, -1, 5, 'PAUSED', 'CRON', 1680023948000, 0, NULL, 2, '');
INSERT INTO `qrtz_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME4', 'SYSTEM', 'TASK_CLASS_NAME4', 'SYSTEM', NULL, 1680024000000, -1, 5, 'WAITING', 'CRON', 1680023950000, 0, NULL, -1, '');
INSERT INTO `qrtz_triggers` VALUES ('RuoyiScheduler', 'TASK_CLASS_NAME5', 'SYSTEM', 'TASK_CLASS_NAME5', 'SYSTEM', NULL, 1680024000000, -1, 5, 'WAITING', 'CRON', 1680023951000, 0, NULL, -1, '');

-- ----------------------------
-- Table structure for sip_config
-- ----------------------------
DROP TABLE IF EXISTS `sip_config`;
CREATE TABLE `sip_config`  (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `enabled` tinyint(1) NULL DEFAULT NULL COMMENT '使能开关',
  `isdefault` tinyint(1) NULL DEFAULT NULL COMMENT '系统默认配置',
  `seniorSdp` tinyint(1) NULL DEFAULT NULL COMMENT '拓展sdp',
  `domain` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器域',
  `server_sipid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器sipid',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'sip认证密码',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sip接入IP',
  `port` bigint(10) NULL DEFAULT NULL COMMENT 'sip接入端口号',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'sip系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sip_config
-- ----------------------------
INSERT INTO `sip_config` VALUES (38, 1, 'admin', 117, '', 1, 1, NULL, '3402000000', '34020000002000000001', '12345678', '192.168.2.6', 5061, '0', '', '2023-03-16 21:26:18', '', '2023-03-16 21:26:24', NULL);
INSERT INTO `sip_config` VALUES (39, 1, 'admin', 118, '', 1, 1, NULL, '3402000000', '34020000002000000001', '12345678', '192.168.2.6', 5061, '0', '', '2023-04-11 21:11:54', '', NULL, NULL);

-- ----------------------------
-- Table structure for sip_device
-- ----------------------------
DROP TABLE IF EXISTS `sip_device`;
CREATE TABLE `sip_device`  (
  `device_id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `device_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备SipID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '设备名称',
  `manufacturer` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '厂商名称',
  `model` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品型号',
  `firmware` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '固件版本',
  `transport` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'UDP' COMMENT '传输模式',
  `streamMode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'UDP' COMMENT '流模式',
  `online` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '在线状态',
  `registerTime` datetime(0) NOT NULL COMMENT '注册时间',
  `lastConnectTime` datetime(0) NULL DEFAULT NULL COMMENT '最后上线时间',
  `active_time` datetime(0) NULL DEFAULT NULL COMMENT '激活时间',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备入网IP',
  `port` bigint(10) NULL DEFAULT NULL COMMENT '设备接入端口号',
  `hostAddress` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备地址',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sip_device
-- ----------------------------
INSERT INTO `sip_device` VALUES (9, 0, '', '13030300001170000008', '8E085C3RAJE156F', 'Dahua', 'DH-3H3205-ADW', '2.810.0000027.0.R,2022-08-26', 'UDP', 'UDP', '', '2023-02-27 12:07:35', '2023-02-26 23:36:45', NULL, '177.7.0.1', 35332, '177.7.0.1:35332', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (12, 0, '', '11010100001320000001', 'IP DOME', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2023-03-03 00:12:18', '2023-03-03 00:15:18', NULL, '223.73.5.216', 12038, '223.73.5.216:12038', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (13, 0, '', '11010200001320000017', '', '', '', '', 'UDP', 'UDP', '', '2023-03-16 21:41:45', '2023-03-16 21:52:50', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (14, 0, '', '11010100001320000003', 'IP DOME', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2023-03-27 22:24:59', '2023-03-27 22:38:03', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (16, 0, '', '12010100001320000003', 'IP DOME', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2023-04-11 21:08:07', '2023-04-11 21:13:16', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (18, 0, '', '13030100001320000001', '', 'ABCD', 'TEST001', 'V1.0', 'UDP', 'UDP', '', '2023-03-28 16:06:45', '2023-03-28 16:09:52', NULL, '192.168.205.250', 5063, '192.168.205.250:5063', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (19, 0, '', '11010200001320000001', 'IP DOME', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2023-04-11 22:00:05', '2023-04-11 22:12:04', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);

-- ----------------------------
-- Table structure for sip_device_channel
-- ----------------------------
DROP TABLE IF EXISTS `sip_device_channel`;
CREATE TABLE `sip_device_channel`  (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `device_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备SipID',
  `channel_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '通道SipID',
  `channel_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道名称',
  `register_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `device_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '设备类型',
  `channel_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '通道类型',
  `cityCode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '城市编码',
  `civilCode` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '行政区域',
  `manufacture` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '厂商名称',
  `model` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品型号',
  `owner` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '设备归属',
  `block` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '警区',
  `address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '安装地址',
  `parentId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '父级id',
  `ipAddress` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '设备入网IP',
  `port` bigint(10) NULL DEFAULT 0 COMMENT '设备接入端口号',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `PTZType` bigint(20) NOT NULL DEFAULT 0 COMMENT 'PTZ类型',
  `PTZTypeText` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'PTZ类型描述字符串',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
  `longitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备经度',
  `latitude` double(11, 6) NULL DEFAULT NULL COMMENT '设备纬度',
  `streamId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体ID',
  `subCount` bigint(20) NOT NULL DEFAULT 0 COMMENT '子设备数',
  `parental` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有子设备（1-有, 0-没有）',
  `hasAudio` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否含有音频（1-有, 0-没有）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`, `device_sip_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备通道信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sip_device_channel
-- ----------------------------
INSERT INTO `sip_device_channel` VALUES (77, 1, 'admin', 117, '测试', 1, 'admin', '11010100001320000003', '11010100001320000003', 'IPdome', '2023-03-16 21:53:50', '132', '132', '北京市/市辖区/东城区', '3402000000', 'Hikvision', 'IP Camera', 'Owner', '', 'Address', '34020000002000000001', '', 0, '', 0, '', 3, 0.000000, 0.000000, 'gb_play_11010100001320000003_11010100001320000003', 0, 0, 1, '0', '', '2023-03-16 21:50:06', '', NULL, NULL);
INSERT INTO `sip_device_channel` VALUES (80, 1, 'admin', 117, '测试', 1, 'admin', '12010100001320000003', '12010100001320000003', 'IPdome', '2023-03-27 22:39:10', '132', '132', '天津市/市辖区/和平区', '3402000000', 'Hikvision', 'IP Camera', 'Owner', '', 'Address', '34020000002000000001', '', 0, '', 0, '', 3, 0.000000, 0.000000, 'gb_play_12010100001320000003_12010100001320000003', 0, 0, 1, '0', '', '2023-03-27 22:36:54', '', NULL, NULL);
INSERT INTO `sip_device_channel` VALUES (83, 1, 'admin', 117, '测试', 1, 'admin', '13030100001320000001', '13030100001320000001', 'IPC', '2023-03-28 16:06:45', '132', '132', '河北省/秦皇岛市/市辖区', 'CivilCode1', 'ABCD', 'TEST001', 'Owner1', 'Block1', 'Address1', '13030100001320000001', '192.168.205.250', 5063, 'Password1', 0, '', 3, 0.000000, 0.000000, 'gb_play_13030100001320000001_13030100001320000001', 0, 0, 1, '0', '', '2023-03-28 16:06:29', '', NULL, NULL);
INSERT INTO `sip_device_channel` VALUES (84, 1, 'admin', 118, '监控设备', 1, 'admin', '11010200001320000001', '11010200001320000001', 'IPdome', '2023-04-11 21:14:18', '132', '132', '北京市/市辖区/西城区', '3402000000', 'Hikvision', 'IP Camera', 'Owner', '', 'Address', '34020000002000000001', '', 0, '', 0, '', 3, 0.000000, 0.000000, 'gb_play_11010200001320000001_11010200001320000001', 0, 0, 1, '0', '', '2023-04-11 21:12:33', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_user`;
CREATE TABLE `sys_auth_user`  (
  `auth_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '授权ID',
  `uuid` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台用户唯一ID',
  `user_id` bigint(20) NOT NULL COMMENT '系统用户ID',
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户来源',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`auth_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方授权表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_auth_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2021-12-15 21:36:18', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2021-12-15 21:36:18', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2021-12-15 21:36:18', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', '2021-12-15 21:36:18', 'admin', '2021-12-24 22:43:33', '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2023-03-10 23:29:21', '', NULL, '是否开启验证码功能（true开启，false关闭）');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '蜂信物联', 0, 'FastBee', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-02-26 23:06:24');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '北京总公司', 1, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2022-03-09 16:49:53');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '深圳分公司', 2, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-02-26 23:06:07');
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '物美', '15888888888', '164770707@qq.com', '1', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2022-02-01 23:12:40');
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '物美', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 241 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (100, 1, '属性', '1', 'iot_things_type', '', 'primary', 'Y', '0', 'admin', '2021-12-12 16:41:15', 'admin', '2021-12-15 22:49:37', '');
INSERT INTO `sys_dict_data` VALUES (101, 2, '功能', '2', 'iot_things_type', '', 'success', 'Y', '0', 'admin', '2021-12-12 16:43:33', 'admin', '2021-12-14 16:33:11', '');
INSERT INTO `sys_dict_data` VALUES (102, 3, '事件', '3', 'iot_things_type', NULL, 'warning', 'Y', '0', 'admin', '2021-12-12 16:46:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (103, 1, '整数', 'integer', 'iot_data_type', '', '', 'Y', '0', 'admin', '2021-12-12 20:20:46', 'admin', '2021-12-14 16:09:56', '');
INSERT INTO `sys_dict_data` VALUES (104, 2, '小数', 'decimal', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:21:21', 'admin', '2021-12-15 22:51:07', NULL);
INSERT INTO `sys_dict_data` VALUES (105, 3, '布尔', 'bool', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:12', 'admin', '2021-12-15 22:51:02', NULL);
INSERT INTO `sys_dict_data` VALUES (106, 4, '枚举', 'enum', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:37', 'admin', '2021-12-15 22:50:57', NULL);
INSERT INTO `sys_dict_data` VALUES (107, 5, '字符串', 'string', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:54', 'admin', '2021-12-15 22:50:52', NULL);
INSERT INTO `sys_dict_data` VALUES (108, 1, '是', '1', 'iot_yes_no', '', 'default', 'Y', '0', 'admin', '2021-12-12 20:25:14', 'admin', '2022-01-02 13:39:09', '');
INSERT INTO `sys_dict_data` VALUES (109, 2, '否', '0', 'iot_yes_no', '', 'default', 'Y', '0', 'admin', '2021-12-12 20:25:25', 'admin', '2022-01-02 13:39:15', '');
INSERT INTO `sys_dict_data` VALUES (110, 6, '数组', 'array', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-13 18:18:04', 'admin', '2021-12-15 22:50:42', NULL);
INSERT INTO `sys_dict_data` VALUES (111, 1, '未发布', '1', 'iot_product_status', NULL, 'info', 'N', '0', 'admin', '2021-12-19 15:01:18', 'admin', '2021-12-19 15:01:55', NULL);
INSERT INTO `sys_dict_data` VALUES (112, 2, '已发布', '2', 'iot_product_status', NULL, 'success', 'N', '0', 'admin', '2021-12-19 15:01:43', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (113, 1, '直连设备', '1', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:03:49', 'admin', '2021-12-19 15:10:13', NULL);
INSERT INTO `sys_dict_data` VALUES (114, 2, '网关设备', '2', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:04:28', 'admin', '2023-02-09 16:25:46', NULL);
INSERT INTO `sys_dict_data` VALUES (116, 1, 'WIFI', '1', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:07:35', 'admin', '2021-12-22 00:11:19', NULL);
INSERT INTO `sys_dict_data` VALUES (117, 2, '蜂窝(2G/3G/4G/5G)', '2', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:08:30', 'admin', '2022-01-14 02:12:27', NULL);
INSERT INTO `sys_dict_data` VALUES (118, 3, '以太网', '3', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:09:08', 'admin', '2022-01-14 02:12:39', NULL);
INSERT INTO `sys_dict_data` VALUES (119, 1, '简单认证', '1', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:13:16', 'admin', '2022-06-05 00:14:48', NULL);
INSERT INTO `sys_dict_data` VALUES (120, 2, '加密认证', '2', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:13:26', 'admin', '2022-06-05 00:14:57', NULL);
INSERT INTO `sys_dict_data` VALUES (122, 1, 'ESP8266/Arduino', '1', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:54:52', 'admin', '2021-12-24 16:07:31', NULL);
INSERT INTO `sys_dict_data` VALUES (123, 3, 'ESP32/Arduino', '2', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:55:04', 'admin', '2021-12-24 16:07:26', NULL);
INSERT INTO `sys_dict_data` VALUES (124, 2, 'ESP8266/RTOS', '3', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:56:08', 'admin', '2021-12-24 16:07:17', NULL);
INSERT INTO `sys_dict_data` VALUES (127, 4, 'ESP32/ESP-IDF', '4', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 16:07:54', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (128, 5, '树莓派/Python', '5', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 16:08:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (129, 0, '未激活', '1', 'iot_device_status', NULL, 'warning', 'N', '0', 'admin', '2021-12-27 22:21:04', 'admin', '2021-12-27 22:22:09', NULL);
INSERT INTO `sys_dict_data` VALUES (130, 0, '禁用', '2', 'iot_device_status', NULL, 'danger', 'N', '0', 'admin', '2021-12-27 22:21:22', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (131, 0, '在线', '3', 'iot_device_status', NULL, 'success', 'N', '0', 'admin', '2021-12-27 22:21:42', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (132, 0, '离线', '4', 'iot_device_status', NULL, 'info', 'N', '0', 'admin', '2021-12-27 22:22:01', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (133, 0, '启用', '1', 'iot_is_enable', NULL, 'success', 'N', '0', 'admin', '2022-01-12 23:25:08', 'admin', '2022-01-12 23:25:30', NULL);
INSERT INTO `sys_dict_data` VALUES (134, 0, '禁用', '0', 'iot_is_enable', NULL, 'info', 'N', '0', 'admin', '2022-01-12 23:25:19', 'admin', '2022-01-12 23:25:38', NULL);
INSERT INTO `sys_dict_data` VALUES (135, 0, '提醒通知', '1', 'iot_alert_level', NULL, 'success', 'N', '0', 'admin', '2022-01-13 14:58:10', 'admin', '2022-01-13 14:58:31', NULL);
INSERT INTO `sys_dict_data` VALUES (136, 0, '轻微问题', '2', 'iot_alert_level', NULL, 'warning', 'N', '0', 'admin', '2022-01-13 14:59:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (137, 0, '严重警告', '3', 'iot_alert_level', NULL, 'danger', 'N', '0', 'admin', '2022-01-13 14:59:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (138, 0, '不需要处理', '1', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:03', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (139, 0, '未处理', '2', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:14', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (140, 0, '已处理', '3', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (141, 1, '属性上报', '1', 'iot_device_log_type', NULL, 'primary', 'N', '0', 'admin', '2022-01-13 15:10:32', 'admin', '2022-03-13 00:20:25', NULL);
INSERT INTO `sys_dict_data` VALUES (142, 3, '事件上报', '3', 'iot_device_log_type', NULL, 'danger', 'N', '0', 'admin', '2022-01-13 15:10:43', 'admin', '2022-03-13 00:21:00', NULL);
INSERT INTO `sys_dict_data` VALUES (143, 2, '功能调用', '2', 'iot_device_log_type', NULL, 'warning', 'N', '0', 'admin', '2022-01-13 15:10:55', 'admin', '2022-03-13 00:20:32', NULL);
INSERT INTO `sys_dict_data` VALUES (144, 4, '设备升级', '4', 'iot_device_log_type', NULL, 'success', 'N', '0', 'admin', '2022-01-13 15:11:08', 'admin', '2022-03-13 00:21:06', NULL);
INSERT INTO `sys_dict_data` VALUES (145, 5, '设备上线', '5', 'iot_device_log_type', NULL, 'success', 'N', '0', 'admin', '2022-01-13 15:11:23', 'admin', '2022-03-13 00:21:26', NULL);
INSERT INTO `sys_dict_data` VALUES (146, 6, '设备离线', '6', 'iot_device_log_type', NULL, 'info', 'N', '0', 'admin', '2022-01-13 15:11:32', 'admin', '2022-03-13 00:21:13', NULL);
INSERT INTO `sys_dict_data` VALUES (147, 4, '其他', '4', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2022-01-14 02:12:49', 'admin', '2022-01-14 02:13:03', NULL);
INSERT INTO `sys_dict_data` VALUES (148, 6, '安卓/Android', '6', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2022-01-16 12:39:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (149, 7, '其他', '7', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2022-01-16 12:39:55', 'admin', '2022-01-16 12:40:13', NULL);
INSERT INTO `sys_dict_data` VALUES (150, 1, '小度平台', '1', 'oauth_platform', NULL, 'primary', 'N', '0', 'admin', '2022-02-07 20:29:23', 'admin', '2022-02-07 22:24:28', NULL);
INSERT INTO `sys_dict_data` VALUES (151, 2, '天猫精灵', '2', 'oauth_platform', NULL, 'danger', 'N', '0', 'admin', '2022-02-07 20:29:41', 'admin', '2022-02-07 22:23:14', NULL);
INSERT INTO `sys_dict_data` VALUES (152, 3, '小米小爱', '3', 'oauth_platform', NULL, 'success', 'N', '0', 'admin', '2022-02-07 20:30:07', 'admin', '2022-02-07 22:23:24', NULL);
INSERT INTO `sys_dict_data` VALUES (153, 4, '其他平台', '4', 'oauth_platform', NULL, 'warning', 'N', '0', 'admin', '2022-02-07 22:23:52', 'admin', '2022-02-07 22:24:02', NULL);
INSERT INTO `sys_dict_data` VALUES (154, 1, '微信登录', 'WECHAT', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-04-20 16:41:33', 'admin', '2022-05-12 17:49:45', NULL);
INSERT INTO `sys_dict_data` VALUES (155, 2, 'QQ登录', 'QQ', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-04-20 16:42:46', 'admin', '2022-05-12 17:49:54', NULL);
INSERT INTO `sys_dict_data` VALUES (156, 0, '启用', '0', 'iot_social_platform_status', NULL, 'success', 'N', '0', 'admin', '2022-04-20 17:02:48', 'admin', '2022-05-12 17:39:40', '启用');
INSERT INTO `sys_dict_data` VALUES (157, 1, '未启用', '1', 'iot_social_platform_status', NULL, 'info', 'N', '0', 'admin', '2022-04-20 17:03:15', 'admin', '2022-05-21 13:44:13', '禁用');
INSERT INTO `sys_dict_data` VALUES (158, 3, '支付宝', 'ALIPAY', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-05-12 17:49:24', 'admin', '2022-05-12 17:50:21', NULL);
INSERT INTO `sys_dict_data` VALUES (159, 1, '自动定位', '1', 'iot_location_way', NULL, 'success', 'N', '0', 'admin', '2022-05-21 13:46:51', 'admin', '2022-05-21 13:53:23', 'IP定位，精确到城市');
INSERT INTO `sys_dict_data` VALUES (160, 2, '设备定位', '2', 'iot_location_way', NULL, 'warning', 'N', '0', 'admin', '2022-05-21 13:46:51', 'admin', '2022-05-21 13:49:21', '最精确定位');
INSERT INTO `sys_dict_data` VALUES (161, 3, '自定义位置', '3', 'iot_location_way', NULL, 'primary', 'N', '0', 'admin', '2022-05-21 13:48:50', 'admin', '2022-05-21 13:55:45', '位置自定义');
INSERT INTO `sys_dict_data` VALUES (162, 3, '简单+加密', '3', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2022-06-05 00:15:46', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (163, 1, '未使用', '1', 'iot_auth_status', NULL, 'info', 'N', '0', 'admin', '2022-06-07 17:39:22', 'admin', '2022-06-07 17:40:10', NULL);
INSERT INTO `sys_dict_data` VALUES (164, 2, '已使用', '2', 'iot_auth_status', NULL, 'success', 'N', '0', 'admin', '2022-06-07 17:40:01', 'admin', '2022-06-07 23:21:49', NULL);
INSERT INTO `sys_dict_data` VALUES (165, 7, '对象', 'object', 'iot_data_type', NULL, 'default', 'N', '0', 'admin', '2023-02-09 16:20:57', 'admin', '2023-02-09 16:21:08', NULL);
INSERT INTO `sys_dict_data` VALUES (166, 3, '监控设备', '3', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2023-02-09 16:26:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (167, 1, '未使用', '1', 'sip_gen_status', NULL, 'info', 'N', '0', 'admin', '2023-02-19 15:49:04', 'admin', '2023-02-19 15:50:03', NULL);
INSERT INTO `sys_dict_data` VALUES (168, 2, '在线', '2', 'sip_gen_status', NULL, 'success', 'N', '0', 'admin', '2023-02-19 15:49:24', 'admin', '2023-02-24 21:36:29', NULL);
INSERT INTO `sys_dict_data` VALUES (169, 0, 'DVR', '111', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:07:06', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (170, 1, 'NVR', '118', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:07:59', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (171, 2, '报警控制器', '117', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:08:13', 'admin', '2023-02-22 01:08:35', NULL);
INSERT INTO `sys_dict_data` VALUES (172, 4, '摄像机', '131', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:08:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (173, 5, 'IPC', '132', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (174, 6, '显示器', '133', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:30', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (175, 7, '报警输入设备', '134', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (176, 8, '报警输出设备', '135', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (177, 9, '语音输入设备', '136', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:29', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (178, 10, '语音输出设备', '137', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:46', 'admin', '2023-02-22 01:10:51', NULL);
INSERT INTO `sys_dict_data` VALUES (179, 11, '移动传输设备', '138', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:11:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (180, 0, '报警控制器', '117', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (181, 1, '摄像机', '131', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (182, 2, 'IPC', '132', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (183, 3, '显示器', '133', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:57', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (184, 5, '报警输入设备', '134', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:13:14', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (185, 6, '报警输出设备', '135', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:13:29', 'admin', '2023-02-22 01:13:49', NULL);
INSERT INTO `sys_dict_data` VALUES (186, 7, '语音输入设备', '136', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:14:14', 'admin', '2023-02-22 01:14:24', NULL);
INSERT INTO `sys_dict_data` VALUES (187, 8, '语音输出设备', '137', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:14:50', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (188, 9, '移动传输设备', '138', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:15:11', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (189, 3, '离线', '3', 'sip_gen_status', NULL, 'warning', 'N', '0', 'admin', '2023-02-24 21:36:53', 'admin', '2023-02-24 21:37:11', NULL);
INSERT INTO `sys_dict_data` VALUES (190, 4, '禁用', '4', 'sip_gen_status', NULL, 'danger', 'N', '0', 'admin', '2023-02-24 21:37:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (191, 0, '云端轮询', '0', 'data_collect_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 13:56:16', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (192, 0, '边缘采集', '1', 'data_collect_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 13:56:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (193, 0, '1分钟', '60', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:07', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (194, 0, '2分钟', '120', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:20', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (195, 0, '5分钟', '300', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:29', 'admin', '2023-02-28 14:39:35', NULL);
INSERT INTO `sys_dict_data` VALUES (196, 1, '03(读保持寄存器', '3', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-02-28 15:19:46', 'admin', '2023-04-10 17:49:12', NULL);
INSERT INTO `sys_dict_data` VALUES (197, 0, '01(读线圈)', '1', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-02-28 15:20:06', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (198, 0, 'MQTT', 'MQTT', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:35:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (199, 1, 'TCP', 'TCP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:35:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (200, 2, 'COAP', 'COAP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:36:00', 'admin', '2023-02-28 16:36:22', NULL);
INSERT INTO `sys_dict_data` VALUES (201, 3, 'UDP', 'UDP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:36:15', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (230, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-03-10 23:28:32', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (231, 0, '事件上报', '3', 'iot_event_type', NULL, 'danger', 'N', '0', 'admin', '2023-03-29 00:25:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (232, 0, '设备上线', '5', 'iot_event_type', NULL, 'success', 'N', '0', 'admin', '2023-03-29 00:25:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (233, 0, '设备离线', '6', 'iot_event_type', NULL, 'info', 'N', '0', 'admin', '2023-03-29 00:26:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (234, 0, '服务下发', '1', 'iot_function_type', NULL, 'primary', 'N', '0', 'admin', '2023-03-29 00:38:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (235, 0, '属性获取', '2', 'iot_function_type', NULL, 'success', 'N', '0', 'admin', '2023-03-29 00:38:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (236, 0, 'OTA升级', '3', 'iot_function_type', NULL, 'warning', 'N', '0', 'admin', '2023-03-29 00:39:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (237, 0, '读写', '0', 'iot_data_read_write', NULL, 'primary', 'N', '0', 'admin', '2023-04-09 02:12:05', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (238, 0, '只读', '1', 'iot_data_read_write', NULL, 'info', 'N', '0', 'admin', '2023-04-09 02:12:19', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (239, 0, '全部设备', '1', 'oat_update_limit', NULL, 'default', 'N', '0', 'admin', '2023-04-09 23:57:06', 'admin', '2023-04-11 11:53:57', NULL);
INSERT INTO `sys_dict_data` VALUES (240, 1, '指定设备', '2', 'oat_update_limit', NULL, 'default', 'N', '0', 'admin', '2023-04-11 11:53:28', 'admin', '2023-04-11 11:53:52', NULL);
INSERT INTO `sys_dict_data` VALUES (241, 4, 'GB28181', 'GB28181', 'iot_transport_type', null, 'primary', 'N', '0', 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', null);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '物模型类别', 'iot_things_type', '0', 'admin', '2021-12-12 16:39:47', 'admin', '2021-12-15 22:49:19', '属性、动作、事件');
INSERT INTO `sys_dict_type` VALUES (101, '数据类型', 'iot_data_type', '0', 'admin', '2021-12-12 20:16:48', 'admin', '2021-12-12 20:17:54', 'integer、decimal、bool、string、enum');
INSERT INTO `sys_dict_type` VALUES (102, '是否', 'iot_yes_no', '0', 'admin', '2021-12-12 20:24:51', 'admin', '2021-12-19 15:12:35', '是、否');
INSERT INTO `sys_dict_type` VALUES (103, '产品状态', 'iot_product_status', '0', 'admin', '2021-12-19 15:00:13', '', NULL, '未发布、已发布（不能修改）');
INSERT INTO `sys_dict_type` VALUES (104, '设备类型', 'iot_device_type', '0', 'admin', '2021-12-19 15:03:06', '', NULL, '直连设备、网关子设备、网关设备');
INSERT INTO `sys_dict_type` VALUES (105, '联网方式', 'iot_network_method', '0', 'admin', '2021-12-19 15:07:12', 'admin', '2022-01-14 02:11:58', 'wifi、蜂窝(2G/3G/4G/5G)、以太网、其他');
INSERT INTO `sys_dict_type` VALUES (106, '认证方式', 'iot_vertificate_method', '0', 'admin', '2021-12-19 15:11:48', 'admin', '2022-06-05 12:57:02', '1=简单认证、2=加密认证、3=简单+加密');
INSERT INTO `sys_dict_type` VALUES (107, '设备芯片', 'iot_device_chip', '0', 'admin', '2021-12-24 15:53:27', 'admin', '2022-01-22 00:14:23', 'ESP8266、ESP32、树莓派');
INSERT INTO `sys_dict_type` VALUES (109, '设备状态', 'iot_device_status', '0', 'admin', '2021-12-27 22:19:55', 'admin', '2021-12-27 22:20:13', '未激活、禁用、在线、离线');
INSERT INTO `sys_dict_type` VALUES (110, '是否启用', 'iot_is_enable', '0', 'admin', '2022-01-12 23:24:01', 'admin', '2022-01-12 23:24:15', '启用、禁用');
INSERT INTO `sys_dict_type` VALUES (111, '告警类型', 'iot_alert_level', '0', 'admin', '2022-01-13 14:56:44', 'admin', '2022-01-13 15:04:46', '1=提醒通知，2=轻微问题，3=严重警告');
INSERT INTO `sys_dict_type` VALUES (112, '处理状态', 'iot_process_status', '0', 'admin', '2022-01-13 15:04:06', 'admin', '2022-01-13 15:06:39', '1=不需要处理,2=未处理,3=已处理');
INSERT INTO `sys_dict_type` VALUES (113, '设备日志类型', 'iot_device_log_type', '0', 'admin', '2022-01-13 15:09:49', 'admin', '2022-03-13 00:22:43', '1=属性上报，2=调用功能,3=事件上报，4=设备升级，5=设备上线，6=设备离线');
INSERT INTO `sys_dict_type` VALUES (114, 'Oauth开放平台', 'oauth_platform', '0', 'admin', '2022-02-07 20:27:48', 'admin', '2022-05-21 13:44:50', '1=小度，2=天猫精灵，3=小爱，4=其他');
INSERT INTO `sys_dict_type` VALUES (115, '第三方登录平台', 'iot_social_platform', '0', 'admin', '2022-04-12 15:28:13', 'admin', '2022-04-12 15:37:48', 'Wechat、QQ、');
INSERT INTO `sys_dict_type` VALUES (116, '第三方登录平台状态', 'iot_social_platform_status', '0', 'admin', '2022-04-20 17:02:13', 'admin', '2022-04-20 17:02:23', '第三方登录平台状态');
INSERT INTO `sys_dict_type` VALUES (117, '设备定位方式', 'iot_location_way', '0', 'admin', '2022-05-21 13:45:16', 'admin', '2022-05-21 13:46:06', '1=IP自动定位，2=设备定位，3=自定义');
INSERT INTO `sys_dict_type` VALUES (118, '授权码状态', 'iot_auth_status', '0', 'admin', '2022-06-07 17:38:56', '', NULL, '1=未分配，2=使用中');
INSERT INTO `sys_dict_type` VALUES (119, 'SipID状态', 'sip_gen_status', '0', 'admin', '2023-02-19 15:43:36', 'admin', '2023-02-19 15:45:54', '1=未使用，2=使用中');
INSERT INTO `sys_dict_type` VALUES (120, '监控设备类型', 'video_type', '0', 'admin', '2023-02-22 01:06:38', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (121, '通道类型', 'channel_type', '0', 'admin', '2023-02-22 01:11:51', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (122, '轮询方式', 'data_collect_type', '0', 'admin', '2023-02-28 13:55:45', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (123, '批量采集时间', 'iot_modbus_poll_time', '0', 'admin', '2023-02-28 14:38:21', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (124, '寄存器功能码', 'iot_modbus_status_code', '0', 'admin', '2023-02-28 15:19:02', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (125, '传输协议类型', 'iot_transport_type', '0', 'admin', '2023-02-28 16:35:20', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (126, '设备事件类型', 'iot_event_type', '0', 'admin', '2023-03-29 00:24:51', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (127, '指令下发类型', 'iot_function_type', '0', 'admin', '2023-03-29 00:37:51', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (128, '读写类型', 'iot_data_read_write', '0', 'admin', '2023-04-09 02:11:14', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (129, '升级范围', 'oat_update_limit', '0', 'admin', '2023-04-09 23:51:45', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (130, '云存储平台类型', 'oss_platform_type', '0', 'admin', '2023-04-12 00:26:09', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_job` VALUES (4, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', '0 0/1 * * * ? ', '1', '0', '0', 'admin', '2023-02-28 17:28:03', 'admin', '2023-04-08 22:07:42', '');
INSERT INTO `sys_job` VALUES (5, '设备定时任务', 'SYSTEM', 'deviceJob.timingUpdateDeviceStatusStatus', '0 0/1 * * * ? ', '1', '1', '1', 'admin', '2023-03-24 10:57:48', 'admin', '2023-03-31 21:14:55', '');
INSERT INTO `sys_job` VALUES (6, '监控在线状态更新', 'SYSTEM', 'deviceJob.updateSipDeviceOnlineStatus(90)', '0 0/1 * * * ?', '1', '0', '0', 'admin', '2023-04-14 16:18:54', '', '2023-04-15 09:37:27', '');
-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2077 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (1, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：1毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:38:00');
INSERT INTO `sys_job_log` VALUES (2, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：1毫秒', '0', '', '2023-04-15 09:38:00');
INSERT INTO `sys_job_log` VALUES (3, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:39:00');
INSERT INTO `sys_job_log` VALUES (4, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:39:00');
INSERT INTO `sys_job_log` VALUES (5, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:40:00');
INSERT INTO `sys_job_log` VALUES (6, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:40:00');
INSERT INTO `sys_job_log` VALUES (7, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:41:00');
INSERT INTO `sys_job_log` VALUES (8, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:41:00');
INSERT INTO `sys_job_log` VALUES (9, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:42:00');
INSERT INTO `sys_job_log` VALUES (10, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:42:00');
INSERT INTO `sys_job_log` VALUES (11, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:43:00');
INSERT INTO `sys_job_log` VALUES (12, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:43:00');
INSERT INTO `sys_job_log` VALUES (13, 'test', 'DEFAULT', 'deviceJob.updateSipDeviceOnlineStatus(90)', 'test 总共耗时：0毫秒', '1', 'java.lang.NoSuchMethodException: com.fastbee.data.service.impl.DeviceJob.updateSipDeviceOnlineStatus(java.lang.Integer)\n	at java.lang.Class.getMethod(Class.java:1786)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:55)\n	at com.fastbee.quartz.util.JobInvokeUtil.invokeMethod(JobInvokeUtil.java:33)\n	at com.fastbee.quartz.util.QuartzDisallowConcurrentExecution.doExecute(QuartzDisallowConcurrentExecution.java:19)\n	at com.fastbee.quartz.util.AbstractQuartzJob.execute(AbstractQuartzJob.java:43)\n	at org.quartz.core.JobRunShell.run(JobRunShell.java:202)\n	at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)\n', '2023-04-15 09:44:00');
INSERT INTO `sys_job_log` VALUES (14, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：2毫秒', '0', '', '2023-04-15 09:44:00');
INSERT INTO `sys_job_log` VALUES (2067, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：31毫秒', '0', '', '2023-04-16 14:13:00');
INSERT INTO `sys_job_log` VALUES (2068, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:14:00');
INSERT INTO `sys_job_log` VALUES (2069, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:15:00');
INSERT INTO `sys_job_log` VALUES (2070, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:16:00');
INSERT INTO `sys_job_log` VALUES (2071, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:17:00');
INSERT INTO `sys_job_log` VALUES (2072, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：8毫秒', '0', '', '2023-04-16 14:18:00');
INSERT INTO `sys_job_log` VALUES (2073, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：8毫秒', '0', '', '2023-04-16 14:19:00');
INSERT INTO `sys_job_log` VALUES (2074, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：8毫秒', '0', '', '2023-04-16 14:20:00');
INSERT INTO `sys_job_log` VALUES (2075, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:21:00');
INSERT INTO `sys_job_log` VALUES (2076, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：9毫秒', '0', '', '2023-04-16 14:22:00');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3046 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 2, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-12-15 21:36:18', 'admin', '2022-02-26 00:46:20', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 3, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-12-15 21:36:18', 'admin', '2022-02-26 00:43:05', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 4, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2021-12-15 21:36:18', 'admin', '2022-02-26 00:42:59', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '蜂信物联', 0, 7, 'http://fastbee.cn', NULL, '', 0, 0, 'M', '0', '0', '', 'guide', 'admin', '2021-12-15 21:36:18', 'admin', '2023-02-22 07:26:09', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-12-15 21:36:18', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2021-12-15 21:36:18', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2021-12-15 21:36:18', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2021-12-15 21:36:18', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2021-12-15 21:36:18', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2021-12-15 21:36:18', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2021-12-15 21:36:18', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2021-12-15 21:36:18', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2021-12-15 21:36:18', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2021-12-15 21:36:18', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2021-12-15 21:36:18', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2021-12-15 21:36:18', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2021-12-15 21:36:18', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2021-12-15 21:36:18', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2021-12-15 21:36:18', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2021-12-15 21:36:18', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2021-12-15 21:36:18', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (124, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2023-03-10 23:22:42', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2021-12-15 21:36:18', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2021-12-15 21:36:18', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 7, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1065, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2023-03-10 23:23:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '设备管理', 0, 0, 'iot', NULL, NULL, 1, 0, 'M', '0', '0', '', 'iot', 'admin', '2021-12-15 23:57:06', 'admin', '2021-12-26 23:55:54', '');
INSERT INTO `sys_menu` VALUES (2001, '产品分类', 2000, 2, 'category', 'iot/category/index', NULL, 1, 0, 'C', '0', '0', 'iot:category:list', 'category', 'admin', '2021-12-16 00:40:02', 'admin', '2021-12-26 23:56:20', '产品分类菜单');
INSERT INTO `sys_menu` VALUES (2002, '产品分类查询', 2001, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:category:query', '#', 'admin', '2021-12-16 00:40:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2003, '产品分类新增', 2001, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:category:add', '#', 'admin', '2021-12-16 00:40:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2004, '产品分类修改', 2001, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:category:edit', '#', 'admin', '2021-12-16 00:40:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2005, '产品分类删除', 2001, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:category:remove', '#', 'admin', '2021-12-16 00:40:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2006, '产品分类导出', 2001, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:category:export', '#', 'admin', '2021-12-16 00:40:02', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2007, '设备管理', 2000, 6, 'device', 'iot/device/index', NULL, 1, 0, 'C', '0', '0', 'iot:device:list', 'device', 'admin', '2021-12-16 00:40:12', 'admin', '2022-01-08 15:47:14', '设备菜单');
INSERT INTO `sys_menu` VALUES (2008, '设备查询', 2007, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:device:query', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2009, '设备新增', 2007, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:device:add', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2010, '设备修改', 2007, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:device:edit', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, '设备删除', 2007, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:device:remove', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2012, '设备导出', 2007, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:device:export', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2013, '产品固件', 3000, 4, 'firmware', 'iot/firmware/index', NULL, 1, 0, 'C', '0', '0', 'iot:firmware:list', 'firmware', 'admin', '2021-12-16 00:40:20', 'admin', '2021-12-26 23:56:42', '产品固件菜单');
INSERT INTO `sys_menu` VALUES (2014, '产品固件查询', 2013, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:firmware:query', '#', 'admin', '2021-12-16 00:40:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2015, '产品固件新增', 2013, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:firmware:add', '#', 'admin', '2021-12-16 00:40:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2016, '产品固件修改', 2013, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:firmware:edit', '#', 'admin', '2021-12-16 00:40:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2017, '产品固件删除', 2013, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:firmware:remove', '#', 'admin', '2021-12-16 00:40:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2018, '产品固件导出', 2013, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:firmware:export', '#', 'admin', '2021-12-16 00:40:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2019, '设备分组', 2000, 5, 'group', 'iot/group/index', NULL, 1, 0, 'C', '0', '0', 'iot:group:list', 'group', 'admin', '2021-12-16 00:40:31', 'admin', '2021-12-26 23:56:54', '设备分组菜单');
INSERT INTO `sys_menu` VALUES (2020, '设备分组查询', 2019, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:group:query', '#', 'admin', '2021-12-16 00:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2021, '设备分组新增', 2019, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:group:add', '#', 'admin', '2021-12-16 00:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2022, '设备分组修改', 2019, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:group:edit', '#', 'admin', '2021-12-16 00:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2023, '设备分组删除', 2019, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:group:remove', '#', 'admin', '2021-12-16 00:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2024, '设备分组导出', 2019, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:group:export', '#', 'admin', '2021-12-16 00:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2043, '产品管理', 2000, 3, 'product', 'iot/product/index', NULL, 1, 0, 'C', '0', '0', 'iot:product:list', 'product', 'admin', '2021-12-16 00:41:18', 'admin', '2021-12-26 23:58:44', '产品菜单');
INSERT INTO `sys_menu` VALUES (2044, '产品查询', 2043, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:product:query', '#', 'admin', '2021-12-16 00:41:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2045, '产品新增', 2043, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:product:add', '#', 'admin', '2021-12-16 00:41:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2046, '产品修改', 2043, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:product:edit', '#', 'admin', '2021-12-16 00:41:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2047, '产品删除', 2043, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:product:remove', '#', 'admin', '2021-12-16 00:41:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2048, '产品导出', 2043, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:product:export', '#', 'admin', '2021-12-16 00:41:18', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2049, '通用物模型', 2000, 1, 'template', 'iot/template/index', NULL, 1, 0, 'C', '0', '0', 'iot:template:list', 'model', 'admin', '2021-12-16 00:41:28', 'admin', '2021-12-26 23:56:09', '通用物模型菜单');
INSERT INTO `sys_menu` VALUES (2050, '通用物模型查询', 2049, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:template:query', '#', 'admin', '2021-12-16 00:41:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2051, '通用物模型新增', 2049, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:template:add', '#', 'admin', '2021-12-16 00:41:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2052, '通用物模型修改', 2049, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:template:edit', '#', 'admin', '2021-12-16 00:41:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2053, '通用物模型删除', 2049, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:template:remove', '#', 'admin', '2021-12-16 00:41:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2054, '通用物模型导出', 2049, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:template:export', '#', 'admin', '2021-12-16 00:41:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2067, '设备告警', 2000, 7, 'alertLog', 'iot/alertLog/index', NULL, 1, 0, 'C', '0', '0', 'iot:alert:list', 'alert', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 01:11:47', '设备告警菜单');
INSERT INTO `sys_menu` VALUES (2068, '设备告警查询', 2067, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:query', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:10', '');
INSERT INTO `sys_menu` VALUES (2069, '设备告警新增', 2067, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:add', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:20', '');
INSERT INTO `sys_menu` VALUES (2070, '设备告警修改', 2067, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:edit', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:29', '');
INSERT INTO `sys_menu` VALUES (2071, '设备告警删除', 2067, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:remove', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:38', '');
INSERT INTO `sys_menu` VALUES (2072, '设备告警导出', 2067, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:export', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:46', '');
INSERT INTO `sys_menu` VALUES (2085, '场景联动', 2000, 8, 'scene', 'iot/scene/index', NULL, 1, 0, 'C', '0', '0', 'iot:scene:list', 'scene', 'admin', '2022-01-13 17:16:45', 'admin', '2022-11-02 22:01:32', '场景联动菜单');
INSERT INTO `sys_menu` VALUES (2086, '场景联动查询', 2085, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:query', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2087, '场景联动新增', 2085, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:add', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2088, '场景联动修改', 2085, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:edit', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2089, '场景联动删除', 2085, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:remove', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2090, '场景联动导出', 2085, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:export', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2098, '云云对接', 2000, 9, 'clientDetails', 'iot/clientDetails/index', NULL, 1, 0, 'C', '1', '0', 'iot:clientDetails:list', 'cloud', 'admin', '2022-02-07 22:08:58', 'admin', '2023-02-09 16:10:22', '云云对接菜单');
INSERT INTO `sys_menu` VALUES (2099, '云云对接查询', 2098, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:query', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2100, '云云对接新增', 2098, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:add', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2101, '云云对接修改', 2098, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:edit', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2102, '云云对接删除', 2098, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:remove', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2103, '云云对接导出', 2098, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:export', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2104, 'EMQ管理', 0, 2, 'emqx', NULL, NULL, 1, 0, 'M', '1', '0', '', 'mq', 'admin', '2022-02-26 00:42:12', 'admin', '2023-04-16 14:12:41', '');
INSERT INTO `sys_menu` VALUES (2105, '客户端', 2104, 1, 'client', 'iot/emqx/client', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:client', 'client', 'admin', '2022-02-26 00:45:39', 'admin', '2022-06-03 14:11:22', '');
INSERT INTO `sys_menu` VALUES (2106, '消息主题', 2104, 3, 'topic', 'iot/emqx/topic', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:topic', 'topic', 'admin', '2022-02-27 16:31:17', 'admin', '2022-06-03 14:11:59', '');
INSERT INTO `sys_menu` VALUES (2107, '消息订阅', 2104, 4, 'subscribe', 'iot/emqx/subscribe', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:subscribe', 'subscribe', 'admin', '2022-02-27 16:32:21', 'admin', '2022-06-03 14:12:11', '');
INSERT INTO `sys_menu` VALUES (2108, '插件管理', 2104, 5, 'plugin', 'iot/emqx/plugin', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:plugin', 'plugin', 'admin', '2022-02-27 19:10:40', 'admin', '2022-06-03 14:12:23', '');
INSERT INTO `sys_menu` VALUES (2109, '监听器', 2104, 2, 'listener', 'iot/emqx/listener', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:listener', 'listener', 'admin', '2022-02-27 19:52:08', 'admin', '2022-06-03 14:11:45', '');
INSERT INTO `sys_menu` VALUES (2111, '规则资源', 2104, 6, 'resource', 'iot/emqx/resource', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:resource', 'build', 'admin', '2022-04-07 14:16:53', 'admin', '2022-06-03 14:12:35', '');
INSERT INTO `sys_menu` VALUES (2112, '规则引擎', 2104, 7, 'rule', 'iot/emqx/rule', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:rule', 'build', 'admin', '2022-04-07 14:19:37', 'admin', '2022-06-03 14:12:48', '');
INSERT INTO `sys_menu` VALUES (2123, '新闻分类', 1, 10, 'newsCategory', 'iot/newsCategory/index', NULL, 1, 0, 'C', '0', '0', 'iot:newsCategory:list', 'category', 'admin', '2022-04-11 16:47:27', 'admin', '2022-05-12 17:20:51', '新闻分类菜单');
INSERT INTO `sys_menu` VALUES (2124, '新闻分类查询', 2123, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:newsCategory:query', '#', 'admin', '2022-04-11 16:47:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2125, '新闻分类新增', 2123, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:newsCategory:add', '#', 'admin', '2022-04-11 16:47:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2126, '新闻分类修改', 2123, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:newsCategory:edit', '#', 'admin', '2022-04-11 16:47:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2127, '新闻分类删除', 2123, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:newsCategory:remove', '#', 'admin', '2022-04-11 16:47:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2128, '新闻分类导出', 2123, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:newsCategory:export', '#', 'admin', '2022-04-11 16:47:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2129, '新闻资讯', 1, 11, 'news', 'iot/news/index', NULL, 1, 0, 'C', '0', '0', 'iot:news:list', 'documentation', 'admin', '2022-04-11 16:47:46', 'admin', '2022-05-12 17:20:58', '新闻资讯菜单');
INSERT INTO `sys_menu` VALUES (2130, '新闻资讯查询', 2129, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:news:query', '#', 'admin', '2022-04-11 16:47:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2131, '新闻资讯新增', 2129, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:news:add', '#', 'admin', '2022-04-11 16:47:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2132, '新闻资讯修改', 2129, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:news:edit', '#', 'admin', '2022-04-11 16:47:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2133, '新闻资讯删除', 2129, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:news:remove', '#', 'admin', '2022-04-11 16:47:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2134, '新闻资讯导出', 2129, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:news:export', '#', 'admin', '2022-04-11 16:47:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2136, '产品授权码查询', 2043, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:authorize:query', '#', 'admin', '2022-04-11 17:17:53', 'admin', '2022-06-04 21:21:40', '');
INSERT INTO `sys_menu` VALUES (2137, '产品授权码新增', 2043, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:authorize:add', '#', 'admin', '2022-04-11 17:17:53', 'admin', '2022-06-04 21:21:59', '');
INSERT INTO `sys_menu` VALUES (2138, '产品授权码修改', 2043, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:authorize:edit', '#', 'admin', '2022-04-11 17:17:53', 'admin', '2022-06-04 21:22:08', '');
INSERT INTO `sys_menu` VALUES (2139, '产品授权码删除', 2043, 9, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:authorize:remove', '#', 'admin', '2022-04-11 17:17:53', 'admin', '2022-06-04 21:22:26', '');
INSERT INTO `sys_menu` VALUES (2140, '产品授权码导出', 2043, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:authorize:export', '#', 'admin', '2022-04-11 17:17:53', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2141, '三方登录', 1, 12, 'platform', 'iot/platform/index', NULL, 1, 1, 'C', '1', '0', 'iot:platform:list', 'cloud', 'admin', '2022-04-11 18:55:34', 'admin', '2023-02-09 16:11:21', '');
INSERT INTO `sys_menu` VALUES (2142, '平台查询', 2142, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:query', '#', 'admin', '2022-04-11 19:10:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2143, 'EMQ查询', 2104, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:query', '#', 'admin', '2022-03-09 16:58:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2144, 'EMQ新增', 2104, 9, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:add', '#', 'admin', '2022-03-09 16:58:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2145, 'EMQ编辑', 2104, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:edit', '#', 'admin', '2022-03-09 16:58:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2146, 'EMQ删除', 2104, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:remove', '#', 'admin', '2022-03-09 16:58:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2147, '设备分享', 2007, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:share', '#', 'admin', '2022-06-10 01:08:40', 'admin', '2022-06-10 01:10:46', '');
INSERT INTO `sys_menu` VALUES (2148, '设备定时', 2007, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer', '#', 'admin', '2022-06-10 01:10:30', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2149, '大屏展示', 0, 5, 'https://iot.fastbee.cn/bigScreen', NULL, NULL, 0, 0, 'M', '0', '0', '', 'monitor-a', 'admin', '2022-08-13 22:32:11', 'admin', '2023-02-26 23:05:42', '');
INSERT INTO `sys_menu` VALUES (2167, '可视化平台', 0, 6, 'https://iot.fastbee.cn/smartView/#/project/items', NULL, NULL, 0, 0, 'C', '0', '0', '', 'eye-open', 'admin', '2022-11-06 21:44:50', 'admin', '2023-02-26 23:05:29', '');
INSERT INTO `sys_menu` VALUES (2168, '视频配置', 2000, 10, 'sip', 'iot/sip/index', NULL, 1, 0, 'C', '0', '0', 'iot:video:list', 'live', 'admin', '2023-02-21 00:21:39', 'admin', '2023-02-22 07:54:06', '');
INSERT INTO `sys_menu` VALUES (2169, '视频配置查询', 2168, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:query', '#', 'admin', '2023-02-22 07:55:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2170, '视频配置新增', 2168, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', ' iot:video:add', '#', 'admin', '2023-02-22 07:56:41', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '视频配置修改', 2168, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:edit', '#', 'admin', '2023-02-22 07:57:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '视频配置删除', 2168, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:remove', '#', 'admin', '2023-02-22 07:58:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2173, '固件任务', 3, 1, 'task', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:list', '#', 'admin', '2023-02-28 01:17:55', '', NULL, '【请填写功能名称】菜单');
INSERT INTO `sys_menu` VALUES (2174, '固件任务查询', 2173, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:query', '#', 'admin', '2023-02-28 01:18:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2175, '固件任务新增', 2173, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:add', '#', 'admin', '2023-02-28 01:18:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2176, '固件任务修改', 2173, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:edit', '#', 'admin', '2023-02-28 01:18:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2177, '固件任务删除', 2173, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:remove', '#', 'admin', '2023-02-28 01:18:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2178, '固件任务导出', 2173, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:export', '#', 'admin', '2023-02-28 01:18:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2179, '固件详情', 3000, 1, 'detail', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:list', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '固件详情菜单');
INSERT INTO `sys_menu` VALUES (2180, '固件详情查询', 2179, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:query', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2181, '固件详情新增', 2179, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:add', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2182, '固件详情修改', 2179, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:edit', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2183, '固件详情删除', 2179, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:remove', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2184, '固件详情导出', 2179, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:detail:export', '#', 'admin', '2023-02-28 01:20:13', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3000, '运维管理', 0, 1, 'template', NULL, NULL, 1, 0, 'M', '0', '0', '', 'tree', 'admin', '2021-12-15 23:57:06', 'admin', '2023-02-28 11:51:36', '');
INSERT INTO `sys_menu` VALUES (3001, '设备服务下发日志', 2000, 1, 'log', 'iot/log/index', NULL, 1, 0, 'F', '0', '0', 'iot:log:list', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '设备服务下发日志菜单');
INSERT INTO `sys_menu` VALUES (3002, '设备服务下发日志查询', 3001, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:log:query', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3003, '设备服务下发日志新增', 3001, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:log:add', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3004, '设备服务下发日志修改', 3001, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:log:edit', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3005, '设备服务下发日志删除', 3001, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:log:remove', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3006, '设备服务下发日志导出', 3001, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:log:export', '#', 'admin', '2023-02-28 11:22:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3007, '协议管理', 3000, 1, 'protocol', 'iot/protocol/index', NULL, 1, 0, 'C', '0', '0', 'iot:protocol:list', 'connect', 'admin', '2023-02-28 11:26:54', 'admin', '2023-04-12 22:02:14', '协议菜单');
INSERT INTO `sys_menu` VALUES (3008, '协议查询', 3007, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:protocol:query', '#', 'admin', '2023-02-28 11:26:54', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3009, '协议新增', 3007, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:protocol:add', '#', 'admin', '2023-02-28 11:26:54', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3010, '协议修改', 3007, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:protocol:edit', '#', 'admin', '2023-02-28 11:26:54', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3011, '协议删除', 3007, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:protocol:remove', '#', 'admin', '2023-02-28 11:26:54', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3012, '协议导出', 3007, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:protocol:export', '#', 'admin', '2023-02-28 11:26:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3013, '变量模板设备从机', 3000, 1, 'salve', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:list', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '变量模板设备从机菜单');
INSERT INTO `sys_menu` VALUES (3014, '变量模板设备从机查询', 3013, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:query', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3015, '变量模板设备从机新增', 3013, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:add', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3016, '变量模板设备从机修改', 3013, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:edit', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3017, '变量模板设备从机删除', 3013, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:remove', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3018, '变量模板设备从机导出', 3013, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:salve:export', '#', 'admin', '2023-02-28 11:28:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3019, '变量模板从机采集点', 3000, 1, 'point', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:list', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '变量模板从机采集点菜单');
INSERT INTO `sys_menu` VALUES (3020, '变量模板从机采集点查询', 3019, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:query', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3021, '变量模板从机采集点新增', 3019, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:add', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3022, '变量模板从机采集点修改', 3019, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:edit', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3023, '变量模板从机采集点删除', 3019, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:remove', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3024, '变量模板从机采集点导出', 3019, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:point:export', '#', 'admin', '2023-02-28 11:30:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3025, '采集点模板', 3000, 2, 'template', 'iot/varTemp/temp', NULL, 1, 0, 'C', '0', '0', 'iot:temp:list', 'theme', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:02:24', '设备采集点模板关联菜单');
INSERT INTO `sys_menu` VALUES (3026, '设备采集点模板关联查询', 3025, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:temp:query', '#', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:10:35', '');
INSERT INTO `sys_menu` VALUES (3027, '设备采集点模板关联新增', 3025, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:temp:add', '#', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:10:44', '');
INSERT INTO `sys_menu` VALUES (3028, '设备采集点模板关联修改', 3025, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:temp:edit', '#', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:10:58', '');
INSERT INTO `sys_menu` VALUES (3029, '设备采集点模板关联删除', 3025, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:temp:remove', '#', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:11:06', '');
INSERT INTO `sys_menu` VALUES (3030, '设备采集点模板关联导出', 3025, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:temp:export', '#', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:11:14', '');
INSERT INTO `sys_menu` VALUES (3031, 'Netty管理', 0, 2, 'netty', NULL, NULL, 1, 0, 'M', '0', '0', '', 'mq', 'admin', '2022-02-26 00:42:12', 'admin', '2023-04-16 14:12:41', '');
INSERT INTO `sys_menu` VALUES (3032, '客户端', 3031, 1, 'client', 'iot/netty/clients', NULL, 1, 0, 'C', '0', '0', 'iot:emqx:client', 'client', 'admin', '2022-02-26 00:45:39', 'admin', '2022-06-03 14:11:22', '');
INSERT INTO `sys_menu` VALUES (3033, '事件日志', 2000, 1, 'log', 'iot/log/index', NULL, 1, 0, 'F', '0', '0', 'iot:event:list', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '事件日志菜单');
INSERT INTO `sys_menu` VALUES (3034, '事件日志查询', 3033, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:event:query', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3035, '事件日志新增', 3033, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:event:add', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3036, '事件日志修改', 3033, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:event:edit', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3037, '事件日志删除', 3033, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:event:remove', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3038, '事件日志导出', 3033, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:event:export', '#', 'admin', '2023-03-28 14:23:52', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3039, '设备模拟', 3000, 3, 'simulate', 'iot/simulate/index', NULL, 1, 0, 'C', '0', '0', 'iot:simulate:list', 'bug', 'admin', '2023-02-28 11:31:16', 'admin', '2023-04-12 22:02:33', '设备模拟');
INSERT INTO `sys_menu` VALUES (3040, '模拟设备日志查询', 3039, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:simulate:query', '#', 'admin', '2023-04-06 17:20:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3041, '模拟设备日志新增', 3039, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:simulate:add', '#', 'admin', '2023-04-06 17:20:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3042, '模拟设备日志修改', 3039, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:simulate:edit', '#', 'admin', '2023-04-06 17:20:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3043, '模拟设备日志删除', 3039, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:simulate:remove', '#', 'admin', '2023-04-06 17:20:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3044, '视频中心', 0, 1, 'video', null, null, 1, 0, 'M', '0', '0', '', 'build', 'admin', '2023-05-16 22:05:11', 'admin', '2023-05-16 22:09:56', '');
INSERT INTO `sys_menu` VALUES (3045, '分屏显示', 3044, 0, 'splitview', 'iot/sip/splitview', null, 1, 0, 'C', '0', '0', '', 'build', 'admin', '2023-05-16 22:08:33', 'admin', '2023-05-20 15:31:06', '');
INSERT INTO `sys_menu` VALUES (3046, '配置管理', 3044, 1, 'mediaServer', 'iot/sip/mediaServer', null, 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2023-05-16 22:09:10', 'admin', '2023-05-30 21:52:18', '');
INSERT INTO `sys_menu` VALUES (3048, '数据流转', 0, 1, 'ruleengine', null, null, 1, 0, 'M', '0', '0', null, 'scene', 'admin', '2023-07-03 21:22:19', '', null, '');
INSERT INTO `sys_menu` VALUES (3049, '数据推送', 3048, 2, 'datapush', 'iot/ruleengine/datapush', null, 1, 0, 'C', '0', '0', 'iot/ruleengine/datapush', 'mq', 'admin', '2023-07-03 21:25:30', 'admin', '2023-07-06 21:05:37', '');
INSERT INTO `sys_menu` VALUES (3050, '规则引擎', 3048, 1, 'chain', 'iot/ruleengine/chain', null, 1, 0, 'C', '0', '0', 'iot/ruleengine/chain', 'message', 'admin', '2023-07-03 21:28:21', 'admin', '2023-07-06 21:06:07', '');
INSERT INTO `sys_menu` VALUES (3051, '三方应用管理', 3048, 4, 'apiManage', null, null, 1, 0, 'C', '0', '0', null, 'example', 'admin', '2023-07-03 21:31:28', '', null, '');
INSERT INTO `sys_menu` VALUES (3052, '规则脚本', 3048, 1, 'script', 'iot/ruleengine/script', null, 1, 0, 'C', '0', '0', 'iot/ruleengine/script', 'build', 'admin', '2023-07-06 21:03:14', 'admin', '2023-07-06 21:05:01', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '物美智能V1.2版本发布', '2', 0x3C703EE8BF99E698AFE6B58BE8AF95E58685E5AEB9EFBC8CE696B0E78988E69CACE58A9FE883BDEFBC9A3C2F703E3C6F6C3E3C6C693EE694AFE68C81E5A49AE7A79FE688B73C2F6C693E3C6C693EE694AFE68C81E8AEBEE5A487E58886E4BAAB3C2F6C693E3C6C693EE694AFE68C81E697B6E5BA8FE695B0E68DAEE5BA933C2F6C693E3C6C693EE7AE80E58D95E8AEA4E8AF81E5928CE58AA0E5AF86E8AEA4E8AF81E7BB9FE4B8803C2F6C693E3C2F6F6C3E, '0', 'admin', '2021-12-15 21:36:18', 'admin', '2022-06-04 17:07:25', '管理员');
INSERT INTO `sys_notice` VALUES (2, '物美智能sdk支持树莓派', '1', 0x3C703EE8BF99E698AFE6B58BE8AF95E58685E5AEB9EFBC8CE79BAEE5898D73646BE694AFE68C81E79A84E78988E69CAC3A3C2F703E3C703E3C62723E3C2F703E3C6F6C3E3C6C693E41726475696E6F20657370383236363C2F6C693E3C6C693E41726475696E6F2065737033323C2F6C693E3C6C693E6573702D6964663C2F6C693E3C6C693E72617370626572727920E6A091E88E93E6B4BE3C2F6C693E3C2F6F6C3E, '0', 'admin', '2021-12-15 21:36:18', 'admin', '2022-06-04 17:07:36', '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1, '操作日志', 9, 'com.fastbee.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/operlog/clean', '39.130.41.42', '云南省 曲靖市', '{}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2023-04-16 14:21:47');
INSERT INTO `sys_oper_log` VALUES (2, '登录日志', 9, 'com.fastbee.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', NULL, '/monitor/logininfor/clean', '39.130.41.42', '云南省 曲靖市', '{}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2023-04-16 14:21:53');

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2021-12-15 21:36:18', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '设备租户', 'tenant', 2, '5', 1, 1, '0', '0', 'admin', '2021-12-16 16:41:30', 'admin', '2023-04-12 19:53:34', '管理产品和设备');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'general', 3, '5', 1, 1, '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-02-22 08:17:37', '设备的最终用户，只能管理设备和分组');
INSERT INTO `sys_role` VALUES (4, '游客', 'visitor', 4, '1', 1, 1, '0', '0', 'admin', '2021-12-16 16:44:30', 'admin', '2023-04-12 22:11:46', '只能查询和新增系统数据');
INSERT INTO `sys_role` VALUES (5, '管理员', 'manager', 5, '1', 1, 1, '0', '0', 'admin', '2022-06-10 13:54:29', 'admin', '2023-04-12 19:50:29', '普通管理员');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 1036);
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2001);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2003);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2005);
INSERT INTO `sys_role_menu` VALUES (2, 2006);
INSERT INTO `sys_role_menu` VALUES (2, 2007);
INSERT INTO `sys_role_menu` VALUES (2, 2008);
INSERT INTO `sys_role_menu` VALUES (2, 2009);
INSERT INTO `sys_role_menu` VALUES (2, 2010);
INSERT INTO `sys_role_menu` VALUES (2, 2011);
INSERT INTO `sys_role_menu` VALUES (2, 2012);
INSERT INTO `sys_role_menu` VALUES (2, 2013);
INSERT INTO `sys_role_menu` VALUES (2, 2014);
INSERT INTO `sys_role_menu` VALUES (2, 2015);
INSERT INTO `sys_role_menu` VALUES (2, 2016);
INSERT INTO `sys_role_menu` VALUES (2, 2017);
INSERT INTO `sys_role_menu` VALUES (2, 2018);
INSERT INTO `sys_role_menu` VALUES (2, 2019);
INSERT INTO `sys_role_menu` VALUES (2, 2020);
INSERT INTO `sys_role_menu` VALUES (2, 2021);
INSERT INTO `sys_role_menu` VALUES (2, 2022);
INSERT INTO `sys_role_menu` VALUES (2, 2023);
INSERT INTO `sys_role_menu` VALUES (2, 2024);
INSERT INTO `sys_role_menu` VALUES (2, 2043);
INSERT INTO `sys_role_menu` VALUES (2, 2044);
INSERT INTO `sys_role_menu` VALUES (2, 2045);
INSERT INTO `sys_role_menu` VALUES (2, 2046);
INSERT INTO `sys_role_menu` VALUES (2, 2047);
INSERT INTO `sys_role_menu` VALUES (2, 2048);
INSERT INTO `sys_role_menu` VALUES (2, 2049);
INSERT INTO `sys_role_menu` VALUES (2, 2050);
INSERT INTO `sys_role_menu` VALUES (2, 2051);
INSERT INTO `sys_role_menu` VALUES (2, 2052);
INSERT INTO `sys_role_menu` VALUES (2, 2053);
INSERT INTO `sys_role_menu` VALUES (2, 2054);
INSERT INTO `sys_role_menu` VALUES (2, 2067);
INSERT INTO `sys_role_menu` VALUES (2, 2068);
INSERT INTO `sys_role_menu` VALUES (2, 2069);
INSERT INTO `sys_role_menu` VALUES (2, 2070);
INSERT INTO `sys_role_menu` VALUES (2, 2071);
INSERT INTO `sys_role_menu` VALUES (2, 2072);
INSERT INTO `sys_role_menu` VALUES (2, 2085);
INSERT INTO `sys_role_menu` VALUES (2, 2086);
INSERT INTO `sys_role_menu` VALUES (2, 2087);
INSERT INTO `sys_role_menu` VALUES (2, 2088);
INSERT INTO `sys_role_menu` VALUES (2, 2089);
INSERT INTO `sys_role_menu` VALUES (2, 2090);
INSERT INTO `sys_role_menu` VALUES (2, 2098);
INSERT INTO `sys_role_menu` VALUES (2, 2099);
INSERT INTO `sys_role_menu` VALUES (2, 2100);
INSERT INTO `sys_role_menu` VALUES (2, 2101);
INSERT INTO `sys_role_menu` VALUES (2, 2102);
INSERT INTO `sys_role_menu` VALUES (2, 2103);
INSERT INTO `sys_role_menu` VALUES (2, 2129);
INSERT INTO `sys_role_menu` VALUES (2, 2130);
INSERT INTO `sys_role_menu` VALUES (2, 2136);
INSERT INTO `sys_role_menu` VALUES (2, 2137);
INSERT INTO `sys_role_menu` VALUES (2, 2138);
INSERT INTO `sys_role_menu` VALUES (2, 2139);
INSERT INTO `sys_role_menu` VALUES (2, 2140);
INSERT INTO `sys_role_menu` VALUES (2, 2147);
INSERT INTO `sys_role_menu` VALUES (2, 2148);
INSERT INTO `sys_role_menu` VALUES (2, 2168);
INSERT INTO `sys_role_menu` VALUES (2, 2169);
INSERT INTO `sys_role_menu` VALUES (2, 2170);
INSERT INTO `sys_role_menu` VALUES (2, 2171);
INSERT INTO `sys_role_menu` VALUES (2, 2172);
INSERT INTO `sys_role_menu` VALUES (2, 3000);
INSERT INTO `sys_role_menu` VALUES (3, 1);
INSERT INTO `sys_role_menu` VALUES (3, 4);
INSERT INTO `sys_role_menu` VALUES (3, 107);
INSERT INTO `sys_role_menu` VALUES (3, 1036);
INSERT INTO `sys_role_menu` VALUES (3, 2000);
INSERT INTO `sys_role_menu` VALUES (3, 2007);
INSERT INTO `sys_role_menu` VALUES (3, 2008);
INSERT INTO `sys_role_menu` VALUES (3, 2009);
INSERT INTO `sys_role_menu` VALUES (3, 2010);
INSERT INTO `sys_role_menu` VALUES (3, 2011);
INSERT INTO `sys_role_menu` VALUES (3, 2012);
INSERT INTO `sys_role_menu` VALUES (3, 2019);
INSERT INTO `sys_role_menu` VALUES (3, 2020);
INSERT INTO `sys_role_menu` VALUES (3, 2021);
INSERT INTO `sys_role_menu` VALUES (3, 2022);
INSERT INTO `sys_role_menu` VALUES (3, 2023);
INSERT INTO `sys_role_menu` VALUES (3, 2024);
INSERT INTO `sys_role_menu` VALUES (3, 2067);
INSERT INTO `sys_role_menu` VALUES (3, 2068);
INSERT INTO `sys_role_menu` VALUES (3, 2085);
INSERT INTO `sys_role_menu` VALUES (3, 2086);
INSERT INTO `sys_role_menu` VALUES (3, 2087);
INSERT INTO `sys_role_menu` VALUES (3, 2088);
INSERT INTO `sys_role_menu` VALUES (3, 2089);
INSERT INTO `sys_role_menu` VALUES (3, 2090);
INSERT INTO `sys_role_menu` VALUES (3, 2129);
INSERT INTO `sys_role_menu` VALUES (3, 2130);
INSERT INTO `sys_role_menu` VALUES (3, 2147);
INSERT INTO `sys_role_menu` VALUES (3, 2148);
INSERT INTO `sys_role_menu` VALUES (3, 2168);
INSERT INTO `sys_role_menu` VALUES (3, 2169);
INSERT INTO `sys_role_menu` VALUES (3, 2170);
INSERT INTO `sys_role_menu` VALUES (3, 2171);
INSERT INTO `sys_role_menu` VALUES (3, 2172);
INSERT INTO `sys_role_menu` VALUES (4, 1);
INSERT INTO `sys_role_menu` VALUES (4, 2);
INSERT INTO `sys_role_menu` VALUES (4, 3);
INSERT INTO `sys_role_menu` VALUES (4, 4);
INSERT INTO `sys_role_menu` VALUES (4, 100);
INSERT INTO `sys_role_menu` VALUES (4, 101);
INSERT INTO `sys_role_menu` VALUES (4, 102);
INSERT INTO `sys_role_menu` VALUES (4, 103);
INSERT INTO `sys_role_menu` VALUES (4, 104);
INSERT INTO `sys_role_menu` VALUES (4, 105);
INSERT INTO `sys_role_menu` VALUES (4, 106);
INSERT INTO `sys_role_menu` VALUES (4, 107);
INSERT INTO `sys_role_menu` VALUES (4, 108);
INSERT INTO `sys_role_menu` VALUES (4, 109);
INSERT INTO `sys_role_menu` VALUES (4, 110);
INSERT INTO `sys_role_menu` VALUES (4, 111);
INSERT INTO `sys_role_menu` VALUES (4, 112);
INSERT INTO `sys_role_menu` VALUES (4, 113);
INSERT INTO `sys_role_menu` VALUES (4, 114);
INSERT INTO `sys_role_menu` VALUES (4, 115);
INSERT INTO `sys_role_menu` VALUES (4, 116);
INSERT INTO `sys_role_menu` VALUES (4, 500);
INSERT INTO `sys_role_menu` VALUES (4, 501);
INSERT INTO `sys_role_menu` VALUES (4, 1001);
INSERT INTO `sys_role_menu` VALUES (4, 1008);
INSERT INTO `sys_role_menu` VALUES (4, 1013);
INSERT INTO `sys_role_menu` VALUES (4, 1017);
INSERT INTO `sys_role_menu` VALUES (4, 1021);
INSERT INTO `sys_role_menu` VALUES (4, 1026);
INSERT INTO `sys_role_menu` VALUES (4, 1031);
INSERT INTO `sys_role_menu` VALUES (4, 1036);
INSERT INTO `sys_role_menu` VALUES (4, 1040);
INSERT INTO `sys_role_menu` VALUES (4, 1043);
INSERT INTO `sys_role_menu` VALUES (4, 1046);
INSERT INTO `sys_role_menu` VALUES (4, 1049);
INSERT INTO `sys_role_menu` VALUES (4, 1055);
INSERT INTO `sys_role_menu` VALUES (4, 2000);
INSERT INTO `sys_role_menu` VALUES (4, 2001);
INSERT INTO `sys_role_menu` VALUES (4, 2002);
INSERT INTO `sys_role_menu` VALUES (4, 2003);
INSERT INTO `sys_role_menu` VALUES (4, 2007);
INSERT INTO `sys_role_menu` VALUES (4, 2008);
INSERT INTO `sys_role_menu` VALUES (4, 2009);
INSERT INTO `sys_role_menu` VALUES (4, 2013);
INSERT INTO `sys_role_menu` VALUES (4, 2014);
INSERT INTO `sys_role_menu` VALUES (4, 2015);
INSERT INTO `sys_role_menu` VALUES (4, 2019);
INSERT INTO `sys_role_menu` VALUES (4, 2020);
INSERT INTO `sys_role_menu` VALUES (4, 2021);
INSERT INTO `sys_role_menu` VALUES (4, 2043);
INSERT INTO `sys_role_menu` VALUES (4, 2044);
INSERT INTO `sys_role_menu` VALUES (4, 2045);
INSERT INTO `sys_role_menu` VALUES (4, 2049);
INSERT INTO `sys_role_menu` VALUES (4, 2050);
INSERT INTO `sys_role_menu` VALUES (4, 2051);
INSERT INTO `sys_role_menu` VALUES (4, 2067);
INSERT INTO `sys_role_menu` VALUES (4, 2068);
INSERT INTO `sys_role_menu` VALUES (4, 2069);
INSERT INTO `sys_role_menu` VALUES (4, 2085);
INSERT INTO `sys_role_menu` VALUES (4, 2086);
INSERT INTO `sys_role_menu` VALUES (4, 2087);
INSERT INTO `sys_role_menu` VALUES (4, 2098);
INSERT INTO `sys_role_menu` VALUES (4, 2099);
INSERT INTO `sys_role_menu` VALUES (4, 2100);
INSERT INTO `sys_role_menu` VALUES (4, 2104);
INSERT INTO `sys_role_menu` VALUES (4, 2105);
INSERT INTO `sys_role_menu` VALUES (4, 2106);
INSERT INTO `sys_role_menu` VALUES (4, 2107);
INSERT INTO `sys_role_menu` VALUES (4, 2108);
INSERT INTO `sys_role_menu` VALUES (4, 2109);
INSERT INTO `sys_role_menu` VALUES (4, 2111);
INSERT INTO `sys_role_menu` VALUES (4, 2112);
INSERT INTO `sys_role_menu` VALUES (4, 2123);
INSERT INTO `sys_role_menu` VALUES (4, 2124);
INSERT INTO `sys_role_menu` VALUES (4, 2125);
INSERT INTO `sys_role_menu` VALUES (4, 2129);
INSERT INTO `sys_role_menu` VALUES (4, 2130);
INSERT INTO `sys_role_menu` VALUES (4, 2131);
INSERT INTO `sys_role_menu` VALUES (4, 2136);
INSERT INTO `sys_role_menu` VALUES (4, 2137);
INSERT INTO `sys_role_menu` VALUES (4, 2141);
INSERT INTO `sys_role_menu` VALUES (4, 2143);
INSERT INTO `sys_role_menu` VALUES (4, 2144);
INSERT INTO `sys_role_menu` VALUES (4, 2147);
INSERT INTO `sys_role_menu` VALUES (4, 2148);
INSERT INTO `sys_role_menu` VALUES (4, 2149);
INSERT INTO `sys_role_menu` VALUES (4, 2167);
INSERT INTO `sys_role_menu` VALUES (4, 2168);
INSERT INTO `sys_role_menu` VALUES (4, 2169);
INSERT INTO `sys_role_menu` VALUES (4, 2170);
INSERT INTO `sys_role_menu` VALUES (4, 2173);
INSERT INTO `sys_role_menu` VALUES (4, 2174);
INSERT INTO `sys_role_menu` VALUES (4, 2175);
INSERT INTO `sys_role_menu` VALUES (4, 2179);
INSERT INTO `sys_role_menu` VALUES (4, 2180);
INSERT INTO `sys_role_menu` VALUES (4, 2181);
INSERT INTO `sys_role_menu` VALUES (4, 3000);
INSERT INTO `sys_role_menu` VALUES (4, 3001);
INSERT INTO `sys_role_menu` VALUES (4, 3002);
INSERT INTO `sys_role_menu` VALUES (4, 3003);
INSERT INTO `sys_role_menu` VALUES (4, 3007);
INSERT INTO `sys_role_menu` VALUES (4, 3008);
INSERT INTO `sys_role_menu` VALUES (4, 3009);
INSERT INTO `sys_role_menu` VALUES (4, 3013);
INSERT INTO `sys_role_menu` VALUES (4, 3014);
INSERT INTO `sys_role_menu` VALUES (4, 3015);
INSERT INTO `sys_role_menu` VALUES (4, 3019);
INSERT INTO `sys_role_menu` VALUES (4, 3020);
INSERT INTO `sys_role_menu` VALUES (4, 3021);
INSERT INTO `sys_role_menu` VALUES (4, 3025);
INSERT INTO `sys_role_menu` VALUES (4, 3026);
INSERT INTO `sys_role_menu` VALUES (4, 3027);
INSERT INTO `sys_role_menu` VALUES (4, 3031);
INSERT INTO `sys_role_menu` VALUES (4, 3032);
INSERT INTO `sys_role_menu` VALUES (4, 3033);
INSERT INTO `sys_role_menu` VALUES (4, 3034);
INSERT INTO `sys_role_menu` VALUES (4, 3035);
INSERT INTO `sys_role_menu` VALUES (4, 3039);
INSERT INTO `sys_role_menu` VALUES (4, 3040);
INSERT INTO `sys_role_menu` VALUES (4, 3041);
INSERT INTO `sys_role_menu` VALUES (5, 1);
INSERT INTO `sys_role_menu` VALUES (5, 2);
INSERT INTO `sys_role_menu` VALUES (5, 3);
INSERT INTO `sys_role_menu` VALUES (5, 4);
INSERT INTO `sys_role_menu` VALUES (5, 100);
INSERT INTO `sys_role_menu` VALUES (5, 101);
INSERT INTO `sys_role_menu` VALUES (5, 102);
INSERT INTO `sys_role_menu` VALUES (5, 103);
INSERT INTO `sys_role_menu` VALUES (5, 104);
INSERT INTO `sys_role_menu` VALUES (5, 105);
INSERT INTO `sys_role_menu` VALUES (5, 106);
INSERT INTO `sys_role_menu` VALUES (5, 107);
INSERT INTO `sys_role_menu` VALUES (5, 108);
INSERT INTO `sys_role_menu` VALUES (5, 109);
INSERT INTO `sys_role_menu` VALUES (5, 110);
INSERT INTO `sys_role_menu` VALUES (5, 111);
INSERT INTO `sys_role_menu` VALUES (5, 112);
INSERT INTO `sys_role_menu` VALUES (5, 113);
INSERT INTO `sys_role_menu` VALUES (5, 114);
INSERT INTO `sys_role_menu` VALUES (5, 115);
INSERT INTO `sys_role_menu` VALUES (5, 116);
INSERT INTO `sys_role_menu` VALUES (5, 124);
INSERT INTO `sys_role_menu` VALUES (5, 500);
INSERT INTO `sys_role_menu` VALUES (5, 501);
INSERT INTO `sys_role_menu` VALUES (5, 1001);
INSERT INTO `sys_role_menu` VALUES (5, 1002);
INSERT INTO `sys_role_menu` VALUES (5, 1003);
INSERT INTO `sys_role_menu` VALUES (5, 1004);
INSERT INTO `sys_role_menu` VALUES (5, 1005);
INSERT INTO `sys_role_menu` VALUES (5, 1006);
INSERT INTO `sys_role_menu` VALUES (5, 1007);
INSERT INTO `sys_role_menu` VALUES (5, 1008);
INSERT INTO `sys_role_menu` VALUES (5, 1009);
INSERT INTO `sys_role_menu` VALUES (5, 1010);
INSERT INTO `sys_role_menu` VALUES (5, 1011);
INSERT INTO `sys_role_menu` VALUES (5, 1012);
INSERT INTO `sys_role_menu` VALUES (5, 1013);
INSERT INTO `sys_role_menu` VALUES (5, 1014);
INSERT INTO `sys_role_menu` VALUES (5, 1015);
INSERT INTO `sys_role_menu` VALUES (5, 1016);
INSERT INTO `sys_role_menu` VALUES (5, 1017);
INSERT INTO `sys_role_menu` VALUES (5, 1018);
INSERT INTO `sys_role_menu` VALUES (5, 1019);
INSERT INTO `sys_role_menu` VALUES (5, 1020);
INSERT INTO `sys_role_menu` VALUES (5, 1021);
INSERT INTO `sys_role_menu` VALUES (5, 1022);
INSERT INTO `sys_role_menu` VALUES (5, 1023);
INSERT INTO `sys_role_menu` VALUES (5, 1024);
INSERT INTO `sys_role_menu` VALUES (5, 1025);
INSERT INTO `sys_role_menu` VALUES (5, 1026);
INSERT INTO `sys_role_menu` VALUES (5, 1027);
INSERT INTO `sys_role_menu` VALUES (5, 1028);
INSERT INTO `sys_role_menu` VALUES (5, 1029);
INSERT INTO `sys_role_menu` VALUES (5, 1030);
INSERT INTO `sys_role_menu` VALUES (5, 1031);
INSERT INTO `sys_role_menu` VALUES (5, 1032);
INSERT INTO `sys_role_menu` VALUES (5, 1033);
INSERT INTO `sys_role_menu` VALUES (5, 1034);
INSERT INTO `sys_role_menu` VALUES (5, 1035);
INSERT INTO `sys_role_menu` VALUES (5, 1036);
INSERT INTO `sys_role_menu` VALUES (5, 1037);
INSERT INTO `sys_role_menu` VALUES (5, 1038);
INSERT INTO `sys_role_menu` VALUES (5, 1039);
INSERT INTO `sys_role_menu` VALUES (5, 1040);
INSERT INTO `sys_role_menu` VALUES (5, 1041);
INSERT INTO `sys_role_menu` VALUES (5, 1042);
INSERT INTO `sys_role_menu` VALUES (5, 1043);
INSERT INTO `sys_role_menu` VALUES (5, 1044);
INSERT INTO `sys_role_menu` VALUES (5, 1045);
INSERT INTO `sys_role_menu` VALUES (5, 1046);
INSERT INTO `sys_role_menu` VALUES (5, 1047);
INSERT INTO `sys_role_menu` VALUES (5, 1048);
INSERT INTO `sys_role_menu` VALUES (5, 1049);
INSERT INTO `sys_role_menu` VALUES (5, 1050);
INSERT INTO `sys_role_menu` VALUES (5, 1051);
INSERT INTO `sys_role_menu` VALUES (5, 1052);
INSERT INTO `sys_role_menu` VALUES (5, 1053);
INSERT INTO `sys_role_menu` VALUES (5, 1054);
INSERT INTO `sys_role_menu` VALUES (5, 1055);
INSERT INTO `sys_role_menu` VALUES (5, 1056);
INSERT INTO `sys_role_menu` VALUES (5, 1057);
INSERT INTO `sys_role_menu` VALUES (5, 1058);
INSERT INTO `sys_role_menu` VALUES (5, 1059);
INSERT INTO `sys_role_menu` VALUES (5, 1060);
INSERT INTO `sys_role_menu` VALUES (5, 1065);
INSERT INTO `sys_role_menu` VALUES (5, 2000);
INSERT INTO `sys_role_menu` VALUES (5, 2001);
INSERT INTO `sys_role_menu` VALUES (5, 2002);
INSERT INTO `sys_role_menu` VALUES (5, 2003);
INSERT INTO `sys_role_menu` VALUES (5, 2004);
INSERT INTO `sys_role_menu` VALUES (5, 2005);
INSERT INTO `sys_role_menu` VALUES (5, 2006);
INSERT INTO `sys_role_menu` VALUES (5, 2007);
INSERT INTO `sys_role_menu` VALUES (5, 2008);
INSERT INTO `sys_role_menu` VALUES (5, 2009);
INSERT INTO `sys_role_menu` VALUES (5, 2010);
INSERT INTO `sys_role_menu` VALUES (5, 2011);
INSERT INTO `sys_role_menu` VALUES (5, 2012);
INSERT INTO `sys_role_menu` VALUES (5, 2013);
INSERT INTO `sys_role_menu` VALUES (5, 2014);
INSERT INTO `sys_role_menu` VALUES (5, 2015);
INSERT INTO `sys_role_menu` VALUES (5, 2016);
INSERT INTO `sys_role_menu` VALUES (5, 2017);
INSERT INTO `sys_role_menu` VALUES (5, 2018);
INSERT INTO `sys_role_menu` VALUES (5, 2019);
INSERT INTO `sys_role_menu` VALUES (5, 2020);
INSERT INTO `sys_role_menu` VALUES (5, 2021);
INSERT INTO `sys_role_menu` VALUES (5, 2022);
INSERT INTO `sys_role_menu` VALUES (5, 2023);
INSERT INTO `sys_role_menu` VALUES (5, 2024);
INSERT INTO `sys_role_menu` VALUES (5, 2043);
INSERT INTO `sys_role_menu` VALUES (5, 2044);
INSERT INTO `sys_role_menu` VALUES (5, 2045);
INSERT INTO `sys_role_menu` VALUES (5, 2046);
INSERT INTO `sys_role_menu` VALUES (5, 2047);
INSERT INTO `sys_role_menu` VALUES (5, 2048);
INSERT INTO `sys_role_menu` VALUES (5, 2049);
INSERT INTO `sys_role_menu` VALUES (5, 2050);
INSERT INTO `sys_role_menu` VALUES (5, 2051);
INSERT INTO `sys_role_menu` VALUES (5, 2052);
INSERT INTO `sys_role_menu` VALUES (5, 2053);
INSERT INTO `sys_role_menu` VALUES (5, 2054);
INSERT INTO `sys_role_menu` VALUES (5, 2067);
INSERT INTO `sys_role_menu` VALUES (5, 2068);
INSERT INTO `sys_role_menu` VALUES (5, 2069);
INSERT INTO `sys_role_menu` VALUES (5, 2070);
INSERT INTO `sys_role_menu` VALUES (5, 2071);
INSERT INTO `sys_role_menu` VALUES (5, 2072);
INSERT INTO `sys_role_menu` VALUES (5, 2085);
INSERT INTO `sys_role_menu` VALUES (5, 2086);
INSERT INTO `sys_role_menu` VALUES (5, 2087);
INSERT INTO `sys_role_menu` VALUES (5, 2088);
INSERT INTO `sys_role_menu` VALUES (5, 2089);
INSERT INTO `sys_role_menu` VALUES (5, 2090);
INSERT INTO `sys_role_menu` VALUES (5, 2098);
INSERT INTO `sys_role_menu` VALUES (5, 2099);
INSERT INTO `sys_role_menu` VALUES (5, 2100);
INSERT INTO `sys_role_menu` VALUES (5, 2101);
INSERT INTO `sys_role_menu` VALUES (5, 2102);
INSERT INTO `sys_role_menu` VALUES (5, 2103);
INSERT INTO `sys_role_menu` VALUES (5, 2104);
INSERT INTO `sys_role_menu` VALUES (5, 2105);
INSERT INTO `sys_role_menu` VALUES (5, 2106);
INSERT INTO `sys_role_menu` VALUES (5, 2107);
INSERT INTO `sys_role_menu` VALUES (5, 2108);
INSERT INTO `sys_role_menu` VALUES (5, 2109);
INSERT INTO `sys_role_menu` VALUES (5, 2111);
INSERT INTO `sys_role_menu` VALUES (5, 2112);
INSERT INTO `sys_role_menu` VALUES (5, 2123);
INSERT INTO `sys_role_menu` VALUES (5, 2124);
INSERT INTO `sys_role_menu` VALUES (5, 2125);
INSERT INTO `sys_role_menu` VALUES (5, 2126);
INSERT INTO `sys_role_menu` VALUES (5, 2127);
INSERT INTO `sys_role_menu` VALUES (5, 2128);
INSERT INTO `sys_role_menu` VALUES (5, 2129);
INSERT INTO `sys_role_menu` VALUES (5, 2130);
INSERT INTO `sys_role_menu` VALUES (5, 2131);
INSERT INTO `sys_role_menu` VALUES (5, 2132);
INSERT INTO `sys_role_menu` VALUES (5, 2133);
INSERT INTO `sys_role_menu` VALUES (5, 2134);
INSERT INTO `sys_role_menu` VALUES (5, 2136);
INSERT INTO `sys_role_menu` VALUES (5, 2137);
INSERT INTO `sys_role_menu` VALUES (5, 2138);
INSERT INTO `sys_role_menu` VALUES (5, 2139);
INSERT INTO `sys_role_menu` VALUES (5, 2140);
INSERT INTO `sys_role_menu` VALUES (5, 2141);
INSERT INTO `sys_role_menu` VALUES (5, 2143);
INSERT INTO `sys_role_menu` VALUES (5, 2144);
INSERT INTO `sys_role_menu` VALUES (5, 2145);
INSERT INTO `sys_role_menu` VALUES (5, 2146);
INSERT INTO `sys_role_menu` VALUES (5, 2147);
INSERT INTO `sys_role_menu` VALUES (5, 2148);
INSERT INTO `sys_role_menu` VALUES (5, 2149);
INSERT INTO `sys_role_menu` VALUES (5, 2167);
INSERT INTO `sys_role_menu` VALUES (5, 2168);
INSERT INTO `sys_role_menu` VALUES (5, 2169);
INSERT INTO `sys_role_menu` VALUES (5, 2170);
INSERT INTO `sys_role_menu` VALUES (5, 2171);
INSERT INTO `sys_role_menu` VALUES (5, 2172);
INSERT INTO `sys_role_menu` VALUES (5, 2179);
INSERT INTO `sys_role_menu` VALUES (5, 2180);
INSERT INTO `sys_role_menu` VALUES (5, 2181);
INSERT INTO `sys_role_menu` VALUES (5, 2182);
INSERT INTO `sys_role_menu` VALUES (5, 2183);
INSERT INTO `sys_role_menu` VALUES (5, 2184);
INSERT INTO `sys_role_menu` VALUES (5, 3000);
INSERT INTO `sys_role_menu` VALUES (5, 3001);
INSERT INTO `sys_role_menu` VALUES (5, 3002);
INSERT INTO `sys_role_menu` VALUES (5, 3003);
INSERT INTO `sys_role_menu` VALUES (5, 3004);
INSERT INTO `sys_role_menu` VALUES (5, 3005);
INSERT INTO `sys_role_menu` VALUES (5, 3006);
INSERT INTO `sys_role_menu` VALUES (5, 3007);
INSERT INTO `sys_role_menu` VALUES (5, 3008);
INSERT INTO `sys_role_menu` VALUES (5, 3009);
INSERT INTO `sys_role_menu` VALUES (5, 3010);
INSERT INTO `sys_role_menu` VALUES (5, 3011);
INSERT INTO `sys_role_menu` VALUES (5, 3012);
INSERT INTO `sys_role_menu` VALUES (5, 3013);
INSERT INTO `sys_role_menu` VALUES (5, 3014);
INSERT INTO `sys_role_menu` VALUES (5, 3015);
INSERT INTO `sys_role_menu` VALUES (5, 3016);
INSERT INTO `sys_role_menu` VALUES (5, 3017);
INSERT INTO `sys_role_menu` VALUES (5, 3018);
INSERT INTO `sys_role_menu` VALUES (5, 3019);
INSERT INTO `sys_role_menu` VALUES (5, 3020);
INSERT INTO `sys_role_menu` VALUES (5, 3021);
INSERT INTO `sys_role_menu` VALUES (5, 3022);
INSERT INTO `sys_role_menu` VALUES (5, 3023);
INSERT INTO `sys_role_menu` VALUES (5, 3024);
INSERT INTO `sys_role_menu` VALUES (5, 3025);
INSERT INTO `sys_role_menu` VALUES (5, 3026);
INSERT INTO `sys_role_menu` VALUES (5, 3027);
INSERT INTO `sys_role_menu` VALUES (5, 3028);
INSERT INTO `sys_role_menu` VALUES (5, 3029);
INSERT INTO `sys_role_menu` VALUES (5, 3030);
INSERT INTO `sys_role_menu` VALUES (5, 3031);
INSERT INTO `sys_role_menu` VALUES (5, 3032);
INSERT INTO `sys_role_menu` VALUES (5, 3033);
INSERT INTO `sys_role_menu` VALUES (5, 3034);
INSERT INTO `sys_role_menu` VALUES (5, 3035);
INSERT INTO `sys_role_menu` VALUES (5, 3036);
INSERT INTO `sys_role_menu` VALUES (5, 3037);
INSERT INTO `sys_role_menu` VALUES (5, 3038);
INSERT INTO `sys_role_menu` VALUES (5, 3039);
INSERT INTO `sys_role_menu` VALUES (5, 3040);
INSERT INTO `sys_role_menu` VALUES (5, 3041);
INSERT INTO `sys_role_menu` VALUES (5, 3042);
INSERT INTO `sys_role_menu` VALUES (5, 3043);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '蜂信管理员', '00', '164770707@qq.com', '15888888888', '0', '', '$2a$10$QAow7ybs74fkSWJDJkVTNeogF7mhnihF7STErt78PxDhHiNno4IUu', '0', '0', '39.130.41.42', '2023-04-16 14:19:25', 'admin', '2021-12-15 21:36:18', '', '2023-04-16 14:19:25', '管理员');
INSERT INTO `sys_user` VALUES (2, 100, 'fastbee-t1', '蜂信租户壹', '00', '', '15888888880', '0', '', '$2a$10$BAWId9C2Nrcwklzl1Ikoau4iqL8XRGvfRjq6Wl.PXWpzwAw0sXMdK', '0', '0', '127.0.0.1', '2023-04-12 22:25:10', 'admin', '2022-04-15 16:21:25', 'admin', '2023-04-12 22:25:11', NULL);
INSERT INTO `sys_user` VALUES (3, 100, 'fastbee-t2', '蜂信租户贰', '00', '', '15888888881', '0', '', '$2a$10$1zMlbW7hGpzA59gpzWGO/ObeASziQ296evjMjHrYdZnxKBLU4WUum', '0', '0', '127.0.0.1', '2022-06-12 00:54:28', 'admin', '2022-04-15 16:22:08', 'admin', '2022-06-12 00:54:30', NULL);
INSERT INTO `sys_user` VALUES (4, 100, 'fastbee-u1', '蜂信用户壹', '00', '', '15888888882', '0', '', '$2a$10$691RJMXZ9HM4sgNTExLPfO5Nw6J6cWgCvcoF9V.jKMnPk5o/8c9VS', '0', '0', '127.0.0.1', '2023-04-12 22:26:39', 'admin', '2022-04-15 16:22:37', 'admin', '2023-04-12 22:26:39', NULL);
INSERT INTO `sys_user` VALUES (5, 100, 'fastbee-u2', '蜂信用户贰', '00', '', '15888888883', '0', '', '$2a$10$x3rM39rewwbi7ayvriGMEOKUHoPCqcL2CYXPLTJRCWYPVvykFIYJq', '0', '0', '127.0.0.1', '2022-06-12 00:55:45', 'admin', '2022-04-15 16:23:13', 'admin', '2022-06-12 00:55:46', NULL);
INSERT INTO `sys_user` VALUES (6, 100, 'fastbee', '游客账号', '00', '', '15888888884', '0', '', '$2a$10$kKeZptrTnSlm0fencX4U2eq.QiaukDs.DckiUsMCwVTxh0IS2LRQ.', '0', '0', '211.20.157.166', '2023-04-14 12:29:53', 'admin', '2022-03-09 16:49:19', 'admin', '2023-04-14 12:29:53', NULL);
INSERT INTO `sys_user` VALUES (7, NULL, 'shenzehui', 'shenzehui', '00', '', '18257292958', '0', '', '$2a$10$UYKWiQF.VWfVvuksS/DMiO234Mwtz.niU7cM/noFgwLVRl7Jjt5pa', '0', '0', '39.189.61.11', '2023-04-16 14:18:09', '', '2023-04-16 14:17:59', '', '2023-04-16 14:18:08', NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (6, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 2);
INSERT INTO `sys_user_role` VALUES (4, 3);
INSERT INTO `sys_user_role` VALUES (5, 3);
INSERT INTO `sys_user_role` VALUES (6, 4);
INSERT INTO `sys_user_role` VALUES (7, 3);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 单独增加改动
-- ----------------------------

ALTER TABLE iot_things_model ADD quantity	INT(2) COMMENT '读取寄存器数量';
ALTER TABLE iot_things_model_template ADD quantity	INT(2) COMMENT '读取寄存器数量';

ALTER TABLE iot_things_model ADD code	varchar(255) COMMENT 'modbus功能码';
ALTER TABLE iot_things_model_template ADD code	varchar(255) COMMENT 'modbus功能码';
ALTER TABLE media_server ADD port_ws	int(11) COMMENT 'ws端口';

INSERT INTO `fastbee`.`sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( 1, '02(读离散量输入)', '2', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-07-03 10:16:48', 'admin', '2023-07-03 10:17:35', NULL);
INSERT INTO `fastbee`.`sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( 3, '04(读输入寄存器)', '4', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-07-03 10:17:18', 'admin', '2023-07-03 10:17:58', NULL);

alter table `iot_things_model_template` add  COLUMN `old_identifier` varchar(10) NULL  COMMENT '旧的标识符';
alter table `iot_things_model_template` add  COLUMN `old_temp_slave_id` varchar(10) NULL  COMMENT '旧的从机id';
