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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_alert
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert`;
CREATE TABLE `iot_alert`  (
  `alert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警ID',
  `alert_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警名称',
  `alert_level` tinyint(11) NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
  `status` tinyint(1) NOT NULL COMMENT '告警状态（1-启动，2-停止）',
  `notify` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知方式[1,2,3]',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`alert_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_alert
-- ----------------------------
INSERT INTO `iot_alert` VALUES (1, '温度告警', 2, 1, '1', '', '2023-08-31 09:10:16', '', '2024-01-28 20:05:19', NULL);
INSERT INTO `iot_alert` VALUES (2, '湿度告警', 3, 1, '1', '', '2023-08-31 09:10:44', '', '2024-01-28 20:05:12', NULL);
INSERT INTO `iot_alert` VALUES (52, '电子围栏', 1, 1, '1', '', '2023-11-10 09:39:23', '', '2024-02-07 11:54:01', NULL);

-- ----------------------------
-- Table structure for iot_alert_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_log`;
CREATE TABLE `iot_alert_log`  (
  `alert_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警日志ID',
  `alert_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警名称',
  `alert_level` tinyint(11) NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
  `status` tinyint(11) NOT NULL COMMENT '处理状态(1=不需要处理,2=未处理,3=已处理)',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `detail` json NULL COMMENT '告警详情（对应物模型）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  PRIMARY KEY (`alert_log_id`) USING BTREE,
  INDEX `iot_alert_log_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_alert_log_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_alert_log_index_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警日志' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_alert_notify_template
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_notify_template`;
CREATE TABLE `iot_alert_notify_template`  (
  `alert_id` bigint(20) NOT NULL COMMENT '告警id',
  `notify_template_id` bigint(20) NOT NULL COMMENT '通知模版id',
  PRIMARY KEY (`alert_id`, `notify_template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '告警通知模版关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of iot_alert_notify_template
-- ----------------------------
INSERT INTO `iot_alert_notify_template` VALUES (52, 4);

-- ----------------------------
-- Table structure for iot_alert_scene
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_scene`;
CREATE TABLE `iot_alert_scene`  (
  `alert_id` bigint(20) NOT NULL COMMENT '告警ID',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  PRIMARY KEY (`alert_id`, `scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '告警场景表' ROW_FORMAT = Dynamic;


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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `iot_category_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_category_index_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品分类' ROW_FORMAT = DYNAMIC;

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
  `active_time` datetime NULL DEFAULT NULL COMMENT '激活时间',
  `summary` json NULL COMMENT '设备摘要，格式[{\"name\":\"device\"},{\"chip\":\"esp8266\"}]',
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_simulate` tinyint(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '是否是模拟设备',
  `slave_id` int(10) NULL DEFAULT NULL COMMENT '从机id',
  PRIMARY KEY (`device_id`) USING BTREE,
  UNIQUE INDEX `iot_device_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_device_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_device_index_tanant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_device_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_index_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 229 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_device
-- ----------------------------
INSERT INTO `iot_device` VALUES (108, '温湿度开关', 41, '智能开关', 1, 'admin', 1, 'admin', 'D1ELV3A5TOJS', NULL, 1.00, 4, -51, 1, 1, '[{\"id\": \"irc\", \"name\": \"射频遥控\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"report_monitor\", \"name\": \"上报数据\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"humidity\", \"name\": \"空气湿度\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"co2\", \"name\": \"二氧化碳\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"value\": \" , , , \", \"shadow\": \" , , , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"status\", \"name\": \"上报状态\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"temperature\", \"name\": \"空气温度\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"brightness\", \"name\": \"室内亮度\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}]', '内网IP', '127.0.0.1', 113.128512, 23.027759, '2023-02-26 00:00:00', '{\"chip\": \"esp8266\", \"name\": \"wumei-smart\", \"author\": \"kerwincui\", \"create\": \"2022-06-06\", \"version\": 1.6}', NULL, '0', '', '2025-02-25 23:15:56', '', '2024-02-07 02:12:46', NULL, NULL, NULL);
INSERT INTO `iot_device` VALUES (109, '网关设备', 96, '网关产品', 1, 'admin', 1, 'admin', 'D1PGLPG58KZ2', NULL, 1.00, 3, -53, 1, 3, '[{\"id\": \"category_gear\", \"name\": \"运行档位\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_switch\", \"name\": \"设备开关\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_light\", \"ts\": \"2023-09-25 17:56:08.848\", \"name\": \"光照\", \"value\": \"68\", \"shadow\": \"68\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"value\": \" , , , , , , \", \"shadow\": \" , , , , , , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"value\": \" , , , \", \"shadow\": \" , , , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"temperature\", \"ts\": \"2023-09-25 17:56:08.582\", \"name\": \"空气温度\", \"value\": \"23.69\", \"shadow\": \"23.69\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_irc\", \"name\": \"射频遥控\", \"value\": \" , , , , , , \", \"shadow\": \" , , , , , , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"brightness\", \"ts\": \"2023-09-25 17:56:08.671\", \"name\": \"室内亮度\", \"value\": \"5387\", \"shadow\": \"5387\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"report_monitor\", \"name\": \"上报监测数据\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_switch\", \"ts\": \"2023-09-25 17:56:26.188\", \"name\": \"设备开关\", \"value\": \"1,1,1, ,1,1, \", \"shadow\": \"1,1,1, ,1,1, \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_temperature\", \"ts\": \"2023-09-25 17:56:09.203\", \"name\": \"空气温度-只读\", \"value\": \"95\", \"shadow\": \"95\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_co2\", \"ts\": \"2023-09-25 17:56:11.229\", \"name\": \"二氧化碳\", \"value\": \"3780,2612,2145,3988,5697, , \", \"shadow\": \"3780,2612,2145,3988,5697, , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_gear\", \"ts\": \"2023-09-25 17:56:28.066\", \"name\": \"运行档位\", \"value\": \"0,0,0, ,0,0, \", \"shadow\": \"0,0,0, ,0,0, \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_humidity\", \"ts\": \"2023-09-25 17:56:09.025\", \"name\": \"空气湿度\", \"value\": \"90\", \"shadow\": \"90\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_report_monitor\", \"name\": \"上报监测数据\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"category_irc\", \"name\": \"射频遥控\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_temperature\", \"ts\": \"2023-09-25 17:56:11.45\", \"name\": \"空气温度-只读\", \"value\": \"86,39,4,80,52, , \", \"shadow\": \"86,39,4,80,52, , \", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}]', '云南省曲靖市 移通', '183.225.206.92', 104.802435, 26.496407, '2023-02-26 00:00:00', '{\"chip\": \"esp8266\", \"name\": \"wumei-smart\", \"author\": \"kerwincui\", \"create\": \"2022-06-06\", \"version\": 1.6}', NULL, '0', '', '2025-02-25 23:17:31', '', '2024-02-07 01:47:36', NULL, NULL, NULL);
INSERT INTO `iot_device` VALUES (118, 'MODBUS网关设备', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C', NULL, 1.00, 4, 0, 0, 1, '[{\"id\": \"6#11\", \"name\": \"A相无功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"31#11\", \"name\": \"三相视在功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"53#11\", \"name\": \"正向无功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"0#11\", \"name\": \"相电压Ua\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"55#11\", \"name\": \"负向无功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"789#11\", \"name\": \"缺相电压值\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"37#11\", \"name\": \"正向无功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"17#11\", \"name\": \"线电压Ubc\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"12#11\", \"name\": \"B相有功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"769#11\", \"name\": \"被测系统负载接线方式\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"40#11\", \"name\": \"负向无功电能累加值低高字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"50#11\", \"name\": \"正向有功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"26#11\", \"name\": \"三相平均相电流 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"771#11\", \"name\": \"校验位\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"16#11\", \"name\": \"相电压Uc \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"34#11\", \"name\": \"正向有功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"0#1\", \"name\": \"漏水值\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"15#11\", \"name\": \"B相视在功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"33#11\", \"name\": \"正向有功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"24#11\", \"name\": \"三相平均相电压 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"0#2\", \"name\": \"温度\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"2#11\", \"name\": \"A相电流 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"23#11\", \"name\": \"C相视在功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"1#2\", \"name\": \"湿度\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"29#11\", \"name\": \"三相总功率因数 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"9#11\", \"name\": \"线电压Uab\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"13#11\", \"name\": \"B相功率因数\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"11#11\", \"name\": \"D0开出状态检测 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"56#11\", \"name\": \"负向无功电能累加值低高字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"775#11\", \"name\": \"PT\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"20#11\", \"name\": \"C相有功功率\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"38#11\", \"name\": \"正向无功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"22#11\", \"name\": \"C相无功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"35#11\", \"name\": \"负向有功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"18#11\", \"name\": \"C相电流\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"51#11\", \"name\": \"负向有功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"3#11\", \"name\": \"D1开入状态检测 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"54#11\", \"name\": \"正向无功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"21#11\", \"name\": \"C相功率因数\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"19#11\", \"name\": \"零地电压\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"49#11\", \"name\": \"正向有功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"10#11\", \"name\": \"B相电流 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"7#11\", \"name\": \"A相视在功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"14#11\", \"name\": \"B相无功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"4#11\", \"name\": \"A相有功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"52#11\", \"name\": \"负向有功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"8#11\", \"name\": \"相电压Ub\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"28#11\", \"name\": \"三相有功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"32#11\", \"name\": \"零序电流 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"36#11\", \"name\": \"负向有功电能累加值高位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"772#11\", \"name\": \"波特率\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"39#11\", \"name\": \"负向无功电能累加值低位字\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"25#11\", \"name\": \"三相平均线电压 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"5#11\", \"name\": \"A相功率因数\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"27#11\", \"name\": \"频率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"773#11\", \"name\": \"电压范围\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"777#11\", \"name\": \"CT\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"30#11\", \"name\": \"三相无功功率 \", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"1#11\", \"name\": \"线电压Uca\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"768#11\", \"name\": \"本机地址\", \"value\": \"\", \"shadow\": \"\", \"isChart\": 0, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}]', ' 本机地址', '127.0.0.1', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2025-02-28 16:49:17', '', '2023-09-25 16:19:30', NULL, NULL, 1);
INSERT INTO `iot_device` VALUES (119, '漏水器', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_1', 'D4AD203F3A1C', 1.00, 4, 0, 0, 1, NULL, ' 本机地址', '127.0.0.1', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-09-25 16:19:30', NULL, NULL, 1);
INSERT INTO `iot_device` VALUES (120, '温湿度计', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_2', 'D4AD203F3A1C', 1.00, 4, 0, 0, 1, NULL, ' 本机地址', '127.0.0.1', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-09-25 16:19:30', NULL, NULL, 2);
INSERT INTO `iot_device` VALUES (121, '电量仪', 112, 'modbus协议组产品', 1, 'admin', 1, 'admin', 'D4AD203F3A1C_11', 'D4AD203F3A1C', 1.00, 4, 0, 0, 1, NULL, ' 本机地址', '127.0.0.1', 103.802435, 25.496407, '2023-03-23 00:00:00', NULL, NULL, '0', '', '2023-02-28 16:49:17', '', '2023-09-25 16:19:30', NULL, NULL, 11);
INSERT INTO `iot_device` VALUES (186, '视频监控', 135, '视频监控', 1, 'admin', 1, 'admin', '11010100001320000001', NULL, 1.00, 4, 0, 0, 1, NULL, '内网IP', '192.168.2.119', NULL, NULL, '2024-01-08 22:17:12', NULL, '', '0', '', '2024-01-08 22:15:58', '', NULL, NULL, 0, NULL);
INSERT INTO `iot_device` VALUES (228, '★网关产品10', 96, '★网关产品', 1, 'admin', 1, 'admin', 'D1PGLPG58K77', NULL, 1.00, 3, 0, 0, 1, NULL, '中国', '183.225.43.216', 115.918972, 29.213654, '2024-02-07 15:27:29', NULL, NULL, '0', '', '2024-02-07 15:27:29', '', NULL, NULL, 0, NULL);

-- ----------------------------
-- Table structure for iot_device_group
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_group`;
CREATE TABLE `iot_device_group`  (
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `group_id` bigint(20) NOT NULL COMMENT '分组ID',
  PRIMARY KEY (`device_id`, `group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备分组' ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备定时' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_device_job
-- ----------------------------
INSERT INTO `iot_device_job` VALUES (4, 'P', 'DEFAULT', '0 08 11 ? * 1,2,3,4,5,6,7', '2', '1', '0', 'admin', '2023-04-15 11:08:37', '', NULL, '', 108, 'D1ELV3A5TOJS', '★温湿度开关', 0, '[{\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"value\": \"2\", \"deviceId\": 108, \"deviceName\": \"★温湿度开关\"}]', 1, 41, '★智能开关产品', NULL, NULL, NULL);
-- ----------------------------
-- Table structure for iot_device_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_log`;
CREATE TABLE `iot_device_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备监测信息ID',
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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `iot_device_log_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_device_log_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_device_log_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_log_index_device_id`(`device_id`) USING BTREE,
  INDEX `index_serialNumber_createTime`(`serial_number`, `create_time`) USING BTREE,
  INDEX `index_isMonitor_serialNumber_createTime`(`serial_number`, `is_monitor`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备日志' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_device_template
-- ----------------------------
DROP TABLE IF EXISTS `iot_device_template`;
CREATE TABLE `iot_device_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品id',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '采集点模板id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备采集点模板关联对象' ROW_FORMAT = DYNAMIC;


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
  `perms` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户物模型权限，多个以英文逗号分隔',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`device_id`, `user_id`) USING BTREE,
  INDEX `iot_device_user_index_user_id`(`user_id`) USING BTREE,
  INDEX `iot_device_user_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_device_user
-- ----------------------------
INSERT INTO `iot_device_user` VALUES (108, 1, 1, 'admin', '温湿度开关', '15888888888', 'admin', 1, NULL, '0', '', '2023-02-25 23:15:57', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (109, 1, 1, 'admin', '网关设备', '15888888888', 'admin', 1, NULL, '0', '', '2023-02-25 23:17:32', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (109, 3, 1, 'admin', '★网关设备', '15888888881', 'fastbee-t2', 0, 'ota,timer,log,monitor,statistic,reset,gear,switch', '0', '', '2023-09-03 01:17:03', '', '2023-09-03 11:05:06', NULL);
INSERT INTO `iot_device_user` VALUES (109, 7, 1, 'admin', '★网关设备', '18257292958', 'shenzehui', 0, NULL, '0', '', '2023-08-24 08:26:34', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (109, 8, 1, 'admin', '★网关设备', '15752221201', 'shadow', 0, NULL, '0', '', '2023-08-24 08:25:44', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (118, 1, 1, 'admin', 'modbus测试-emq-0228', '15888888888', 'admin', 1, NULL, '0', '', '2023-02-28 16:49:18', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (186, 1, 1, 'admin', '视频监控', '15888888888', 'admin', 1, NULL, '0', '', '2024-01-08 22:15:59', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (216, 1, 1, 'admin', 'testZ', '15888888888', 'admin', 1, NULL, '0', '', '2024-02-07 09:28:07', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (225, 1, 1, 'admin', '测试新增11', '15888888888', 'admin', 1, NULL, '0', '', '2024-02-07 10:31:58', '', NULL, NULL);
INSERT INTO `iot_device_user` VALUES (228, 1, 1, 'admin', '★网关产品10', '15888888888', 'admin', 1, NULL, '0', '', NULL, '', NULL, NULL);

-- ----------------------------
-- Table structure for iot_event_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_event_log`;
CREATE TABLE `iot_event_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备事件日志ID',
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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件日志' ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`firmware_id`) USING BTREE,
  INDEX `iot_firmware_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_firmware_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品固件' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_firmware_task
-- ----------------------------
DROP TABLE IF EXISTS `iot_firmware_task`;
CREATE TABLE `iot_firmware_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `firmware_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联固件ID',
  `upgrade_type` int(11) NOT NULL DEFAULT 1 COMMENT '1:指定设备 2:产品级别',
  `task_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `device_amount` int(11) NOT NULL DEFAULT 0 COMMENT '选中的设备总数',
  `del_flag` int(11) NOT NULL DEFAULT 0,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `book_time` timestamp NULL DEFAULT NULL COMMENT '预定时间升级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '固件升级任务对象' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_firmware_task
-- ----------------------------
INSERT INTO `iot_firmware_task` VALUES (1, '测试任务0227', 1, 2, '测试', 1, 0, '2023-02-28 08:23:21', '2023-02-28 08:23:21', '2023-02-28 08:23:18');
INSERT INTO `iot_firmware_task` VALUES (2, '测试任务227', 1, 2, '1', 1, 0, '2023-03-01 02:04:33', '2023-02-28 08:58:04', '2023-02-28 08:58:01');
INSERT INTO `iot_firmware_task` VALUES (3, '111', 1, 1, '', 1, 0, '2023-09-25 09:02:23', '2023-09-25 09:02:23', '2023-09-25 09:02:17');
INSERT INTO `iot_firmware_task` VALUES (4, '99888', 1, 1, '', 1, 0, '2023-09-25 09:02:45', '2023-09-25 09:02:45', '2023-09-29 09:02:17');

-- ----------------------------
-- Table structure for iot_firmware_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `iot_firmware_task_detail`;
CREATE TABLE `iot_firmware_task_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `serial_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设备编码',
  `upgrade_status` int(11) NOT NULL DEFAULT 0 COMMENT '0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败',
  `detail_msg` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '消息ID',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '固件升级任务详细对象' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_firmware_task_detail
-- ----------------------------
INSERT INTO `iot_firmware_task_detail` VALUES (1, 1, 'D1ELV3A5TOJS', 0, '等待升级-未推送固件到设备', '2023-02-28 08:24:15', '', '2023-03-01 02:34:33');
INSERT INTO `iot_firmware_task_detail` VALUES (2, 2, 'D1ELV3A5TOJH', 3, '升级成功', '2023-02-28 08:58:17', '1677550503', '2023-03-01 02:34:51');
INSERT INTO `iot_firmware_task_detail` VALUES (3, 3, 'D1ELV3A5TOJH', 1, '已发送', '2023-03-01 02:09:49', '1677550501', '2023-03-01 02:34:38');
INSERT INTO `iot_firmware_task_detail` VALUES (4, 3, 'D1ELV3A5TORG', 2, '设备OTA升级中', '2023-03-01 02:09:56', '1677550502', '2023-03-01 02:34:40');
INSERT INTO `iot_firmware_task_detail` VALUES (5, 4, 'D1ELV3A5TOJS', 3, '升级成功', '2023-03-01 02:11:30', '1677550504', '2023-03-01 02:34:48');
INSERT INTO `iot_firmware_task_detail` VALUES (6, 4, 'D1ELV3A523RG', 0, '等待升级-未推送固件到设备', '2023-03-01 02:11:34', '', '2023-03-01 02:34:31');
INSERT INTO `iot_firmware_task_detail` VALUES (7, 4, 'D1ELV3A576RG', 2, '设备OTA升级中', '2023-03-01 02:11:40', '1677550505', '2023-03-01 02:34:06');
INSERT INTO `iot_firmware_task_detail` VALUES (11, 5, 'D1ELV3A562RG', 4, '失败', '2023-03-01 02:12:14', '1677550509', '2023-03-01 02:33:53');
INSERT INTO `iot_firmware_task_detail` VALUES (12, 9, 'D1ELV3A576RG', 1, '已发送', '2023-03-01 02:28:33', NULL, '2023-03-01 02:28:33');
INSERT INTO `iot_firmware_task_detail` VALUES (17, 9, 'D1ELV3A5TORG', 5, '停止', '2023-03-01 02:28:33', NULL, '2023-03-01 02:33:49');
INSERT INTO `iot_firmware_task_detail` VALUES (18, 10, 'D1ELV3A562RG', 0, '等待升级', '2023-03-29 16:29:23', NULL, '2023-03-29 16:30:23');
INSERT INTO `iot_firmware_task_detail` VALUES (19, 3, 'D1ELV3A5TOJS', 0, '等待升级', '2023-09-25 09:02:23', NULL, '2023-09-25 09:02:23');
INSERT INTO `iot_firmware_task_detail` VALUES (20, 4, 'D1ELV3A5TOJS', 0, '等待升级', '2023-09-25 09:02:45', NULL, '2023-09-25 09:02:45');

-- ----------------------------
-- Table structure for iot_function_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_function_log`;
CREATE TABLE `iot_function_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备功能日志ID',
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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `show_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '显示值',
  `model_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '设备回复时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `iot_function_log_id_uindex`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备服务下发日志' ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目介绍',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_goview_project
-- ----------------------------
INSERT INTO `iot_goview_project` VALUES ('114ab733f2b94d44898919a96abe583b', '4rmddu4osas000', 1, '/profile/goview/1/114ab733f2b94d44898919a96abe583b_index_preview.png', 0, '2023-02-26 14:07:36', '6', '2023-02-26 23:11:41', NULL);
INSERT INTO `iot_goview_project` VALUES ('6c11d0d9c0fc493e8b92d1c799714807', '5hjup7rqg5g000', 0, '/profile/goview/1/6c11d0d9c0fc493e8b92d1c799714807_index_preview.png', 0, '2023-09-25 23:13:45', '1', '2023-09-25 23:24:06', NULL);
INSERT INTO `iot_goview_project` VALUES ('f112e56eee4a4a0fae3e9ec9b3083eab', '3oa3qdszoug000', 0, NULL, 1, '2023-02-26 12:12:34', '6', '2023-02-26 12:12:34', NULL);

-- ----------------------------
-- Table structure for iot_goview_project_data
-- ----------------------------
DROP TABLE IF EXISTS `iot_goview_project_data`;
CREATE TABLE `iot_goview_project_data`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目id',
  `content` longblob NULL COMMENT '存储数据',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目数据关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_goview_project_data
-- ----------------------------
INSERT INTO `iot_goview_project_data` VALUES ('0c148832f26f40ecb7ddddc482e65976', '6c11d0d9c0fc493e8b92d1c799714807', 0x7B0D0A2020226564697443616E766173436F6E666967223A207B0D0A202020202270726F6A6563744E616D65223A202235686A7570377271673567303030222C0D0A20202020227769647468223A20313932302C0D0A2020202022686569676874223A20313038302C0D0A202020202266696C74657253686F77223A2066616C73652C0D0A2020202022687565526F74617465223A20302C0D0A20202020227361747572617465223A20312C0D0A2020202022636F6E7472617374223A20312C0D0A20202020226272696768746E657373223A20312C0D0A20202020226F706163697479223A20312C0D0A2020202022726F746174655A223A20302C0D0A2020202022726F7461746558223A20302C0D0A2020202022726F7461746559223A20302C0D0A2020202022736B657758223A20302C0D0A2020202022736B657759223A20302C0D0A2020202022626C656E644D6F6465223A20226E6F726D616C222C0D0A20202020226261636B67726F756E64223A206E756C6C2C0D0A20202020226261636B67726F756E64496D616765223A206E756C6C2C0D0A202020202273656C656374436F6C6F72223A20747275652C0D0A202020202263686172745468656D65436F6C6F72223A20226461726B222C0D0A20202020226368617274437573746F6D5468656D65436F6C6F72496E666F223A206E756C6C2C0D0A202020202263686172745468656D6553657474696E67223A207B0D0A202020202020227469746C65223A207B0D0A20202020202020202273686F77223A20747275652C0D0A202020202020202022746578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223424642464246222C0D0A2020202020202020202022666F6E7453697A65223A2031380D0A20202020202020207D2C0D0A202020202020202022737562746578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223413241324132222C0D0A2020202020202020202022666F6E7453697A65223A2031340D0A20202020202020207D0D0A2020202020207D2C0D0A202020202020227841786973223A207B0D0A20202020202020202273686F77223A20747275652C0D0A2020202020202020226E616D65223A2022222C0D0A2020202020202020226E616D65476170223A2031352C0D0A2020202020202020226E616D65546578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223423942384345222C0D0A2020202020202020202022666F6E7453697A65223A2031320D0A20202020202020207D2C0D0A202020202020202022696E7665727365223A2066616C73652C0D0A202020202020202022617869734C6162656C223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A2020202020202020202022666F6E7453697A65223A2031322C0D0A2020202020202020202022636F6C6F72223A202223423942384345222C0D0A2020202020202020202022726F74617465223A20300D0A20202020202020207D2C0D0A202020202020202022706F736974696F6E223A2022626F74746F6D222C0D0A202020202020202022617869734C696E65223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A20202020202020202020226C696E655374796C65223A207B0D0A20202020202020202020202022636F6C6F72223A202223423942384345222C0D0A202020202020202020202020227769647468223A20310D0A202020202020202020207D2C0D0A20202020202020202020226F6E5A65726F223A20747275650D0A20202020202020207D2C0D0A202020202020202022617869735469636B223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A20202020202020202020226C656E677468223A20350D0A20202020202020207D2C0D0A20202020202020202273706C69744C696E65223A207B0D0A202020202020202020202273686F77223A2066616C73652C0D0A20202020202020202020226C696E655374796C65223A207B0D0A20202020202020202020202022636F6C6F72223A202223343834373533222C0D0A202020202020202020202020227769647468223A20312C0D0A2020202020202020202020202274797065223A2022736F6C6964220D0A202020202020202020207D0D0A20202020202020207D0D0A2020202020207D2C0D0A202020202020227941786973223A207B0D0A20202020202020202273686F77223A20747275652C0D0A2020202020202020226E616D65223A2022222C0D0A2020202020202020226E616D65476170223A2031352C0D0A2020202020202020226E616D65546578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223423942384345222C0D0A2020202020202020202022666F6E7453697A65223A2031320D0A20202020202020207D2C0D0A202020202020202022696E7665727365223A2066616C73652C0D0A202020202020202022617869734C6162656C223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A2020202020202020202022666F6E7453697A65223A2031322C0D0A2020202020202020202022636F6C6F72223A202223423942384345222C0D0A2020202020202020202022726F74617465223A20300D0A20202020202020207D2C0D0A202020202020202022706F736974696F6E223A20226C656674222C0D0A202020202020202022617869734C696E65223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A20202020202020202020226C696E655374796C65223A207B0D0A20202020202020202020202022636F6C6F72223A202223423942384345222C0D0A202020202020202020202020227769647468223A20310D0A202020202020202020207D2C0D0A20202020202020202020226F6E5A65726F223A20747275650D0A20202020202020207D2C0D0A202020202020202022617869735469636B223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A20202020202020202020226C656E677468223A20350D0A20202020202020207D2C0D0A20202020202020202273706C69744C696E65223A207B0D0A202020202020202020202273686F77223A20747275652C0D0A20202020202020202020226C696E655374796C65223A207B0D0A20202020202020202020202022636F6C6F72223A202223343834373533222C0D0A202020202020202020202020227769647468223A20312C0D0A2020202020202020202020202274797065223A2022736F6C6964220D0A202020202020202020207D0D0A20202020202020207D0D0A2020202020207D2C0D0A202020202020226C6567656E64223A207B0D0A20202020202020202273686F77223A20747275652C0D0A20202020202020202274797065223A20227363726F6C6C222C0D0A20202020202020202278223A202263656E746572222C0D0A20202020202020202279223A2022746F70222C0D0A20202020202020202269636F6E223A2022636972636C65222C0D0A2020202020202020226F7269656E74223A2022686F72697A6F6E74616C222C0D0A202020202020202022746578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223423942384345222C0D0A2020202020202020202022666F6E7453697A65223A2031380D0A20202020202020207D2C0D0A2020202020202020226974656D486569676874223A2031352C0D0A2020202020202020226974656D5769647468223A2031352C0D0A20202020202020202270616765546578745374796C65223A207B0D0A2020202020202020202022636F6C6F72223A202223423942384345220D0A20202020202020207D0D0A2020202020207D2C0D0A2020202020202267726964223A207B0D0A20202020202020202273686F77223A2066616C73652C0D0A2020202020202020226C656674223A2022313025222C0D0A202020202020202022746F70223A20223630222C0D0A2020202020202020227269676874223A2022313025222C0D0A202020202020202022626F74746F6D223A20223630220D0A2020202020207D2C0D0A2020202020202264617461736574223A206E756C6C2C0D0A2020202020202272656E6465726572223A2022737667220D0A202020207D2C0D0A2020202022707265766965775363616C6554797065223A2022666974220D0A20207D2C0D0A202022636F6D706F6E656E744C697374223A205B0D0A202020207B0D0A202020202020226964223A202235693562796773796E6E6B303030222C0D0A20202020202022697347726F7570223A2066616C73652C0D0A2020202020202261747472223A207B0D0A20202020202020202278223A203333392C0D0A20202020202020202279223A202D3133352C0D0A20202020202020202277223A20313030302C0D0A20202020202020202268223A203830302C0D0A2020202020202020226F666673657458223A20302C0D0A2020202020202020226F666673657459223A20302C0D0A2020202020202020227A496E646578223A202D310D0A2020202020207D2C0D0A202020202020227374796C6573223A207B0D0A20202020202020202266696C74657253686F77223A2066616C73652C0D0A202020202020202022687565526F74617465223A20302C0D0A2020202020202020227361747572617465223A20312C0D0A202020202020202022636F6E7472617374223A20312C0D0A2020202020202020226272696768746E657373223A20312C0D0A2020202020202020226F706163697479223A20312C0D0A202020202020202022726F746174655A223A20302C0D0A202020202020202022726F7461746558223A20302C0D0A202020202020202022726F7461746559223A20302C0D0A202020202020202022736B657758223A20302C0D0A202020202020202022736B657759223A20302C0D0A202020202020202022626C656E644D6F6465223A20226E6F726D616C222C0D0A202020202020202022616E696D6174696F6E73223A205B5D0D0A2020202020207D2C0D0A2020202020202270726576696577223A207B0D0A2020202020202020226F766572466C6F7748696464656E223A2066616C73650D0A2020202020207D2C0D0A20202020202022737461747573223A207B0D0A2020202020202020226C6F636B223A2066616C73652C0D0A20202020202020202268696465223A2066616C73650D0A2020202020207D2C0D0A2020202020202272657175657374223A207B0D0A202020202020202022726571756573744461746154797065223A20302C0D0A202020202020202022726571756573744874747054797065223A2022676574222C0D0A2020202020202020227265717565737455726C223A2022222C0D0A20202020202020202272657175657374496E74657276616C223A206E756C6C2C0D0A20202020202020202272657175657374496E74657276616C556E6974223A20227365636F6E64222C0D0A20202020202020202272657175657374436F6E74656E7454797065223A20302C0D0A20202020202020202272657175657374506172616D73426F647954797065223A20226E6F6E65222C0D0A2020202020202020227265717565737453514C436F6E74656E74223A207B0D0A202020202020202020202273716C223A202273656C656374202A2066726F6D20207768657265220D0A20202020202020207D2C0D0A20202020202020202272657175657374506172616D73223A207B0D0A2020202020202020202022426F6479223A207B0D0A20202020202020202020202022666F726D2D64617461223A207B7D2C0D0A20202020202020202020202022782D7777772D666F726D2D75726C656E636F646564223A207B7D2C0D0A202020202020202020202020226A736F6E223A2022222C0D0A20202020202020202020202022786D6C223A2022220D0A202020202020202020207D2C0D0A2020202020202020202022486561646572223A207B7D2C0D0A2020202020202020202022506172616D73223A207B7D0D0A20202020202020207D0D0A2020202020207D2C0D0A2020202020202266696C746572223A206E756C6C2C0D0A202020202020226576656E7473223A207B0D0A202020202020202022626173654576656E74223A207B0D0A2020202020202020202022636C69636B223A206E756C6C2C0D0A202020202020202020202264626C636C69636B223A206E756C6C2C0D0A20202020202020202020226D6F757365656E746572223A206E756C6C2C0D0A20202020202020202020226D6F7573656C65617665223A206E756C6C0D0A20202020202020207D2C0D0A202020202020202022616476616E6365644576656E7473223A207B0D0A2020202020202020202022766E6F64654D6F756E746564223A206E756C6C2C0D0A2020202020202020202022766E6F64654265666F72654D6F756E74223A206E756C6C0D0A20202020202020207D2C0D0A202020202020202022696E7465726163744576656E7473223A205B5D0D0A2020202020207D2C0D0A202020202020226B6579223A20224D6170416D6170222C0D0A202020202020226368617274436F6E666967223A207B0D0A2020202020202020226B6579223A20224D6170416D6170222C0D0A20202020202020202263686172744B6579223A2022564D6170416D6170222C0D0A202020202020202022636F6E4B6579223A202256434D6170416D6170222C0D0A2020202020202020227469746C65223A2022E9AB98E5BEB7E59CB0E59BBE222C0D0A20202020202020202263617465676F7279223A20224D617073222C0D0A20202020202020202263617465676F72794E616D65223A2022E59CB0E59BBE222C0D0A2020202020202020227061636B616765223A2022436861727473222C0D0A20202020202020202263686172744672616D65223A2022636F6D6D6F6E222C0D0A202020202020202022696D616765223A20226D61705F616D61702E706E67220D0A2020202020207D2C0D0A202020202020226F7074696F6E223A207B0D0A20202020202020202264617461736574223A207B0D0A20202020202020202020226D61726B657273223A205B0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B599423633394A36222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E31353734312C0D0A2020202020202020202020202020202033302E3034333835320D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C304A313535222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E3135373336342C0D0A2020202020202020202020202020202033302E3034333837350D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C3346383732222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E3135383731342C0D0A2020202020202020202020202020202033302E3034343437360D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C3544383337222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E31353734322C0D0A2020202020202020202020202020202033302E3034333830360D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C3837373255222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E31353734382C0D0A2020202020202020202020202020202033302E3034333836350D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C5232373336222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E3135373239352C0D0A2020202020202020202020202020202033302E3034333838340D0A20202020202020202020202020205D0D0A2020202020202020202020207D2C0D0A2020202020202020202020207B0D0A2020202020202020202020202020226E616D65223A2022E6B5994C5236303835222C0D0A20202020202020202020202020202276616C7565223A2031302C0D0A202020202020202020202020202022706F736974696F6E223A205B0D0A202020202020202020202020202020203132322E3135373432352C0D0A2020202020202020202020202020202033302E3034333736360D0A20202020202020202020202020205D0D0A2020202020202020202020207D0D0A202020202020202020205D0D0A20202020202020207D2C0D0A2020202020202020226D61704F7074696F6E73223A207B0D0A20202020202020202020227069746368223A2036302C0D0A2020202020202020202022736B79436F6C6F72223A202223353341394445222C0D0A2020202020202020202022616D61704B6579223A20226435663365313635383964626563616536346430356665393065326261346632222C0D0A2020202020202020202022616D61705374796C654B6579223A20226461726B222C0D0A2020202020202020202022616D61705374796C654B6579437573746F6D223A2022222C0D0A2020202020202020202022616D61704C6F6E223A203132322E322C0D0A2020202020202020202022616D61704C6174223A2033302C0D0A2020202020202020202022616D61705A696E646578223A2031302C0D0A20202020202020202020226D61726B6572223A207B0D0A2020202020202020202020202266696C6C436F6C6F72223A2022234539383938344646222C0D0A2020202020202020202020202266696C6C4F706163697479223A20302E352C0D0A202020202020202020202020227374726F6B65436F6C6F72223A20227768697465222C0D0A202020202020202020202020227374726F6B65576569676874223A20322C0D0A202020202020202020202020227374726F6B654F706163697479223A20302E352C0D0A202020202020202020202020227A496E646578223A2031302C0D0A20202020202020202020202022627562626C65223A20747275652C0D0A20202020202020202020202022637572736F72223A2022706F696E746572222C0D0A20202020202020202020202022636C69636B61626C65223A20747275650D0A202020202020202020207D2C0D0A20202020202020202020226D61704D61726B657254797065223A20224D61726B6572222C0D0A2020202020202020202022766965774D6F6465223A20223244222C0D0A20202020202020202020226C616E67223A20227A685F636E222C0D0A20202020202020202020226665617475726573223A205B0D0A202020202020202020202020226267222C0D0A20202020202020202020202022706F696E74222C0D0A20202020202020202020202022726F6164222C0D0A202020202020202020202020226275696C64696E67220D0A202020202020202020205D0D0A20202020202020207D0D0A2020202020207D0D0A202020207D0D0A20205D2C0D0A20202272657175657374476C6F62616C436F6E666967223A207B0D0A20202020227265717565737444617461506F6E64223A205B5D2C0D0A2020202022726571756573744F726967696E55726C223A2022222C0D0A202020202272657175657374496E74657276616C223A2033302C0D0A202020202272657175657374496E74657276616C556E6974223A20227365636F6E64222C0D0A202020202272657175657374506172616D73223A207B0D0A20202020202022426F6479223A207B0D0A202020202020202022666F726D2D64617461223A207B7D2C0D0A202020202020202022782D7777772D666F726D2D75726C656E636F646564223A207B7D2C0D0A2020202020202020226A736F6E223A2022222C0D0A202020202020202022786D6C223A2022220D0A2020202020207D2C0D0A20202020202022486561646572223A207B7D2C0D0A20202020202022506172616D73223A207B7D0D0A202020207D0D0A20207D0D0A7D, '2023-09-25 23:14:50', '1', '2023-09-25 23:24:06');
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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`group_id`) USING BTREE,
  INDEX `iot_group_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备分组' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `transport` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品支持的传输协议',
  `location_way` tinyint(1) NULL DEFAULT 1 COMMENT '定位方式(1=ip自动定位，2=设备定位，3=自定义)',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `iot_product_index_category_id`(`category_id`) USING BTREE,
  INDEX `iot_product_index_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_product
-- ----------------------------
INSERT INTO `iot_product` VALUES (41, '★智能开关产品', 'JSON', 1, '电工照明', 1, 'admin', 1, 0, 'FastBee', 'P47T6OD5IPFWHUM6', 'KX3TSH4Q4OS835DO', 2, '{\"events\": [{\"id\": \"exception\", \"name\": \"设备发生异常\", \"type\": 3, \"order\": 0, \"regId\": \"exception\", \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"height_temperature\", \"name\": \"环境温度过高\", \"type\": 3, \"order\": 0, \"regId\": \"height_temperature\", \"isChart\": 0, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"functions\": [{\"id\": \"report_monitor\", \"name\": \"上报数据\", \"type\": 2, \"order\": 10, \"regId\": \"report_monitor\", \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"order\": 7, \"regId\": \"message\", \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"type\": 2, \"order\": 5, \"regId\": \"light_color\", \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"order\": 9, \"regId\": \"switch\", \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"order\": 6, \"regId\": \"reset\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"irc\", \"name\": \"射频遥控\", \"type\": 2, \"order\": 11, \"regId\": \"irc\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"order\": 8, \"regId\": \"gear\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"status\", \"name\": \"上报状态\", \"type\": 2, \"order\": 12, \"regId\": \"status\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}], \"properties\": [{\"id\": \"co2\", \"name\": \"二氧化碳\", \"type\": 1, \"order\": 2, \"regId\": \"co2\", \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"brightness\", \"name\": \"室内亮度\", \"type\": 1, \"order\": 4, \"regId\": \"brightness\", \"isChart\": 1, \"datatype\": {\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"temperature\", \"name\": \"空气温度\", \"type\": 1, \"order\": 1, \"regId\": \"temperature\", \"isChart\": 1, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"humidity\", \"name\": \"空气湿度\", \"type\": 1, \"order\": 3, \"regId\": \"humidity\", \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}]}', 1, 1, 3, NULL, '0', '', '2025-08-14 00:06:33', '', '2023-09-25 22:58:17', NULL, 'MQTT', 1);
INSERT INTO `iot_product` VALUES (96, '★网关产品', 'JSON', 1, '电工照明', 1, 'admin', 1, 0, 'FastBee', 'P467433O1MT8MXS2', 'KWF32S3H95LH14LO', 2, '{\"events\": [{\"id\": \"exception\", \"name\": \"设备发生异常\", \"type\": 3, \"order\": 0, \"regId\": \"exception\", \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"height_temperature\", \"name\": \"环境温度过高\", \"type\": 3, \"order\": 0, \"regId\": \"height_temperature\", \"isChart\": 0, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"functions\": [{\"id\": \"report_monitor\", \"name\": \"上报监测数据\", \"type\": 2, \"order\": 11, \"regId\": \"report_monitor\", \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"reset\", \"name\": \"设备重启\", \"type\": 2, \"order\": 0, \"regId\": \"reset\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"message\", \"name\": \"屏显消息\", \"type\": 2, \"order\": 0, \"regId\": \"message\", \"isChart\": 0, \"datatype\": {\"type\": \"string\", \"maxLength\": 1024}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"light_color\", \"name\": \"灯光色值\", \"type\": 2, \"order\": 0, \"regId\": \"light_color\", \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"gear\", \"name\": \"运行档位\", \"type\": 2, \"order\": 7, \"regId\": \"gear\", \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"switch\", \"name\": \"设备开关\", \"type\": 2, \"order\": 8, \"regId\": \"switch\", \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}], \"properties\": [{\"id\": \"brightness\", \"name\": \"室内亮度\", \"type\": 1, \"order\": 0, \"regId\": \"brightness\", \"isChart\": 1, \"datatype\": {\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"temperature\", \"name\": \"空气温度\", \"type\": 1, \"order\": 0, \"regId\": \"temperature\", \"isChart\": 1, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"category\", \"name\": \"功能分组\", \"type\": 1, \"order\": 9, \"regId\": \"category\", \"isChart\": 0, \"datatype\": {\"type\": \"object\", \"params\": [{\"id\": \"category_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"category_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"category_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"category_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_gear\", \"name\": \"运行档位\", \"order\": 5, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_switch\", \"name\": \"设备开关\", \"order\": 4, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_irc\", \"name\": \"射频遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控配对\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}]}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device\", \"name\": \"子设备\", \"type\": 1, \"order\": 10, \"regId\": \"device\", \"isChart\": 0, \"datatype\": {\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1, \"isSharePerm\": 0}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_irc\", \"name\": \"射频遥控\", \"order\": 1, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}], \"arrayType\": \"object\", \"arrayCount\": \"5\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}]}', 2, 2, 3, NULL, '0', '', '2025-02-25 22:51:39', '', '2023-09-16 11:46:43', NULL, 'MQTT', 1);
INSERT INTO `iot_product` VALUES (135, '视频监控', 'JSON', 2, '家居安防', 1, 'admin', 1, 0, 'FastBee', 'P0JY568MA1P45JUT', 'K7B1377M8CVY5ZFX', 2, '{}', 3, 1, 3, '', '0', '', '2024-01-08 22:14:34', '', NULL, NULL, 'GB28181', 1);

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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`authorize_id`) USING BTREE,
  INDEX `iot_product_authorize_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_product_authorize_index_device_id`(`device_id`) USING BTREE,
  INDEX `iot_product_authorize_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_product_authorize_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品授权码表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_product_authorize
-- ----------------------------
INSERT INTO `iot_product_authorize` VALUES (11, 'FD42B9A208E54FC39511939385CE7D28', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (12, '2AD0EEF05A5D404EB23571E709641B77', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (13, '8ED19205FE7E469B97FE8540D065BD1B', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (14, 'EEDDE2452473445EAE02EC25B85B8DBA', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (15, '71B8676112B64605AA0236A1ED1A96E5', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (16, 'C941FA610E474186B0DFD3C4BBA51D83', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (17, '7B9CFC7FC3504004A52FDCCD92687652', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (18, '15A881D5CA4B4455BC125F1A0B098441', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (19, '55CA60FD59254548A2523D8B9E117C1F', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (20, '747168F3E7F24E9C8F95D8E6D3ABECD3', 135, NULL, NULL, NULL, NULL, 1, '0', '18926529123', '2023-11-10 09:38:30', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (31, 'A3B8B7D00F794604B2F8C6FD8CC7E6C1', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (32, '050D236FD0A447BB8D36A0DD39A03B4B', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (33, 'E2B55DBCBF5947C4AFB48F1DA978E5A0', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (34, 'D514624CAA3C42AE969B8FF27DBDDD90', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (35, '27BF166DABCF43C3923181AE4303ABFC', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (36, 'E13394F2E300432D91F4BA1B6C88378E', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (37, '79ED51A333A341AEB8AD78843406B058', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (38, '4C8740A967E34B64AFDBD34FBF57018F', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (39, '398E38DF7F9744B4BA9302EA2A984DCD', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);
INSERT INTO `iot_product_authorize` VALUES (40, '77991DC7F5FA4EEE8DFFCC33C50037EE', 41, NULL, NULL, NULL, NULL, 1, '0', 'admin', '2023-12-28 23:53:02', '', NULL, NULL);

-- ----------------------------
-- Table structure for iot_protocol
-- ----------------------------
DROP TABLE IF EXISTS `iot_protocol`;
CREATE TABLE `iot_protocol`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `protocol_code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议编码',
  `protocol_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议名称',
  `protocol_file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议jar包,js包，c程序上传地址',
  `protocol_type` int(11) NOT NULL DEFAULT 0 COMMENT '协议类型 0:未知 1:jar，2.js,3.c',
  `jar_sign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '协议文件摘要(文件的md5)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `protocol_status` int(11) NOT NULL DEFAULT 0 COMMENT '0:草稿 1:启用 2:停用',
  `del_flag` int(11) NOT NULL DEFAULT 0 COMMENT '0:正常 1:删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UNIQUE_CODE`(`protocol_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '协议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_protocol
-- ----------------------------
INSERT INTO `iot_protocol` VALUES (1, 'JSON', 'JSON解析协议', '/', 0, '系统内置JSON解析协议', '2023-03-01 13:46:43', '2023-04-10 22:42:12', 1, 0);
INSERT INTO `iot_protocol` VALUES (2, 'MODBUS-RTU', 'ModbusRtu协议', '/', 0, '系统内置ModbusRtu解析协议', '2023-03-01 13:52:33', '2023-04-09 23:58:59', 1, 0);
INSERT INTO `iot_protocol` VALUES (3, 'MODBUS-RTU-PAK', '包装Modbus-rtu协议', '/', 0, '系统内置包装后的modbus-rtu协议', '2023-03-01 13:53:10', '2023-03-01 16:10:47', 1, 0);
INSERT INTO `iot_protocol` VALUES (4, 'JSONOBJECT-CHENYI', 'JSON-Data解析协议', '', 0, '系统内置JSONObject解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (5, 'RJ45', 'RJ45解析协议', '', 0, '系统内置RJ45解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (6, 'FlowMeter', '流量计解析协议', '', 0, '流量计解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (7, 'JSONOBJECT', 'JSONObject解析协议', '', 0, '系统内置JSONObject解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (8, 'MODBUS-JSON', 'Modbus转Json解析协议', '', 0, 'modbus转json解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (9, 'YinErDa', 'YinErDa解析协议', '', 0, 'YinErDa解析协议', '2023-08-23 09:31:39', '2023-08-23 09:31:39', 1, 0);
INSERT INTO `iot_protocol` VALUES (10, 'MODBUS-JSON-FY', 'Modbus转Json解析协议-繁易', '', 0, 'modbus转json解析协议-繁易', '2023-08-23 09:33:03', '2023-08-23 09:33:03', 1, 0);
INSERT INTO `iot_protocol` VALUES (11, 'MODBUS-JSON-HP', 'Modbus转Json解析协议-华普物联', '', 0, 'modbus转json解析协议-华普物联', '2024-02-03 21:02:18', '2024-02-03 21:02:17', 1, 0);

-- ----------------------------
-- Table structure for iot_scene
-- ----------------------------
DROP TABLE IF EXISTS `iot_scene`;
CREATE TABLE `iot_scene`  (
  `scene_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '场景ID',
  `scene_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '场景名称',
  `chain_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规则名称',
  `enable` tinyint(1) NULL DEFAULT NULL COMMENT '场景状态（1-启动，2-停止）',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名称',
  `silent_period` int(20) NULL DEFAULT NULL COMMENT '静默周期（分钟）',
  `cond` tinyint(2) NULL DEFAULT NULL COMMENT '执行条件（1=或、任意条件，2=且、所有条件，3=非，不满足）',
  `execute_mode` tinyint(1) NULL DEFAULT NULL COMMENT '执行方式（1=串行，顺序执行，2=并行，同时执行）',
  `execute_delay` int(10) NULL DEFAULT NULL COMMENT '延时执行（秒钟）',
  `has_alert` tinyint(1) NULL DEFAULT 0 COMMENT '是否包含告警推送（1=包含，2=不包含）',
  `application_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '应用名称',
  `el_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规则数据',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`scene_id`) USING BTREE,
  INDEX `iot_scene_index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场景联动' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_scene_device
-- ----------------------------
DROP TABLE IF EXISTS `iot_scene_device`;
CREATE TABLE `iot_scene_device`  (
  `scene_device_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '场景设备ID',
  `serial_number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号（产品触发的没有设备编号）',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '产品名称',
  `source` tinyint(1) NOT NULL COMMENT '触发源（1=设备触发，3=产品触发）',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `script_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '场景脚本ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '类型（2=触发器，3=执行动作）',
  PRIMARY KEY (`scene_device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 803 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '场景设备表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_scene_script
-- ----------------------------
DROP TABLE IF EXISTS `iot_scene_script`;
CREATE TABLE `iot_scene_script`  (
  `script_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '脚本ID',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `source` tinyint(1) NOT NULL COMMENT '触发源（1=设备触发，2=定时触发，3=产品触发,4=告警执行）',
  `script_purpose` tinyint(1) NOT NULL COMMENT '脚本用途(1=数据流，2=触发器，3=执行动作)',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID（用于获取对应物模型）',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型标识符',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型名称',
  `value` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物模型值',
  `operator` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作符',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线）',
  `device_count` int(20) NULL DEFAULT NULL COMMENT '设备数量',
  `job_id` bigint(20) NULL DEFAULT NULL COMMENT '任务ID',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `is_advance` tinyint(1) NULL DEFAULT NULL COMMENT '是否详细corn表达式（1=是，0=否）',
  `parent_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父物模id',
  `parent_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父物模名称',
  `array_index` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数组索引',
  `array_index_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数组索引名称',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`script_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '场景脚本' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_script
-- ----------------------------
DROP TABLE IF EXISTS `iot_script`;
CREATE TABLE `iot_script`  (
  `script_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '脚本ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `scene_id` bigint(64) NOT NULL DEFAULT 0 COMMENT '关联场景ID',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `script_event` tinyint(1) NOT NULL COMMENT '脚本事件(1=设备上报，2=平台下发，3=设备上线，4=设备离线)',
  `script_action` tinyint(1) NOT NULL COMMENT '脚本动作(1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储)',
  `script_purpose` tinyint(1) NOT NULL COMMENT '脚本用途(1=数据流，2=触发器，3=执行动作)',
  `script_order` tinyint(1) NOT NULL DEFAULT 0 COMMENT '脚本执行顺序，值越大优先级越高',
  `application_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '应用名，后端、规则和脚本要统一',
  `script_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '脚本名',
  `script_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本数据',
  `script_type` char(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '脚本类型：\r\nscript=普通脚本，\r\nswitch_script=选择脚本，\r\nif_script=条件脚本，\r\nfor_script=数量循环脚本，\r\nwhile_script=条件循环，\r\nbreak_script=退出循环脚本',
  `script_language` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '脚本语言（groovy | qlexpress | js | python | lua | aviator | java）',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否生效（0-不生效，1-生效）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`script_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '规则引擎脚本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_script
-- ----------------------------
INSERT INTO `iot_script` VALUES ('D1751532166174609408', 1, 'admin', 0, 41, '★智能开关产品', 1, 1, 1, 1, 'fastbee', '消息转发', 'import cn.hutool.json.JSONArray;\nimport cn.hutool.json.JSONObject;\nimport cn.hutool.json.JSONUtil;\nimport cn.hutool.core.util.NumberUtil;\n\n//系统主题\nString sysTopic = \'\';\n//系统数据格式\nString sysPayload = \'\';\n\n// 1. 获取主题和内容(必要)\nString name = msgContext.getTopic();\nLong productId = msgContext.getProductId();\nString serialNumber = msgContext.getSerialNumber();\nString protocolCode = msgContext.getProtocolCode();\nString payload = msgContext.getPayload();\nSystem.out.println(\"产品id/协议编号：\" + productId + \" / \" + protocolCode);\n// 2. 转换为系统主题 /60/DEVICE555/property/post\nsysTopic = \"/\" + productId + \"/\" + serialNumber + \"/property/post\" \n\nif(protocolCode == \"JSON\"){   \n    // 3. 内容格式转换\n    JSONArray newArray = new JSONArray();\n    JSONObject jsonObject = JSONUtil.parseObj(payload);\n    jsonObject.keySet().forEach(key -> {\n        JSONObject newObject =new JSONObject();\n        newObject.put(\"id\" , key);\n        newObject.put(\"value\" , jsonObject.getStr(key));\n        newArray.add(newObject);\n    });\n    sysPayload = newArray.toString();\n}else{\n    //其他协议处理\n}\n\n\n// 4.打印\nSystem.out.println(\"新主题：\" + sysTopic);\nSystem.out.println(\"新内容：\" + sysPayload);\n\n// 5. 返回新的数据（必要）\nmsgContext.setTopic(sysTopic)\nmsgContext.setPayload(sysPayload);', 'script', 'groovy', 1, '0', '', '2024-01-28 17:06:25', '', '2024-02-04 17:06:53', NULL);
INSERT INTO `iot_script` VALUES ('D1753673875549458432', 1, 'admin', 0, 96, '★网关产品', 2, 1, 1, 1, 'fastbee', '平台下发转发', 'import cn.hutool.json.JSONArray;\nimport cn.hutool.json.JSONObject;\nimport cn.hutool.json.JSONUtil;\nimport cn.hutool.core.util.NumberUtil;\n\n\n//系统主题\nString tranTopic = \'\';\n//系统数据格式\nString tranPayload = \'\';\n\n\n// 1. 获取主题和内容(必要)\nString topic = msgContext.getTopic();\nString payload = msgContext.getPayload();\nLong productId = msgContext.getProductId();\nString serialNumber = msgContext.getSerialNumber();\nString protocolCode = msgContext.getProtocolCode();\n\n\n// 2. topic转换\ntranTopic = serialNumber+ \"/set\";\n\n\n//这里根据系统内置的协议类型来做数据转换\n\nif(protocolCode == \"JSON\"){ \n    \n    // 3. 转发的数据  {\"temperature\":26.5,\"humidity\":65.8}\n    JSONArray jsonArray = JSONUtil.parseArray(payload);\n    JSONObject resultObj = new JSONObject();\n    jsonArray.forEach(obj -> {\n    JSONObject jsonObject = (JSONObject)obj;\n    resultObj.put(jsonObject.getStr(\"id\"),jsonObject.getStr(\"value\"));\n    });\n    tranPayload = JSONUtil.toJsonStr(resultObj);  \n    \n}else{\n    //其他协议处理\n}\n\n// 4.打印\nSystem.out.println(\"转发主题：\" + tranTopic);\nSystem.out.println(\"转发内容：\" + tranPayload);\n\n\n// 3. 返回新的数据（必要）\nmsgContext.setTopic(tranTopic);\nmsgContext.setPayload(tranPayload);', 'script', 'groovy', 1, '0', '', '2024-02-03 14:56:48', '', '2024-02-07 15:24:29', NULL);

-- ----------------------------
-- Table structure for iot_simulate_log
-- ----------------------------
DROP TABLE IF EXISTS `iot_simulate_log`;
CREATE TABLE `iot_simulate_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备模拟日志ID',
  `send_data` json NULL COMMENT '云端发送指令',
  `callback_data` json NULL COMMENT '设备回复',
  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备ID',
  `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '模拟设备日志' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for iot_social_platform
-- ----------------------------
DROP TABLE IF EXISTS `iot_social_platform`;
CREATE TABLE `iot_social_platform`  (
  `social_platform_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '第三方登录平台主键',
  `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方登录平台',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT ' 0:启用 ,1:禁用',
  `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台申请Id',
  `secret_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台密钥',
  `redirect_uri` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户认证后跳转地址',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记位(0代表存在，1代表删除)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `bind_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定注册登录uri,http://localhost/login?bindId=',
  `redirect_login_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '跳转登录uri,http://localhost/login?loginId=',
  `error_msg_uri` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '错误提示uri,http://localhost/login?errorId=',
  PRIMARY KEY (`social_platform_id`) USING BTREE,
  UNIQUE INDEX `iot_social_platform_platform_uindex`(`platform`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方登录平台控制' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_social_platform
-- ----------------------------
INSERT INTO `iot_social_platform` VALUES (1, 'QQ', '0', '102005066', 'PhkaBYgZ99999', 'https://iot.wumei.live/auth/callback/qq', '0', 'admin', '2022-04-18 11:21:28', '2022-04-20 16:29:23', 'admin', NULL, 'http://localhost/login?bindId=', 'http://localhost/login?loginId=', 'http://localhost/login?errorId=');
INSERT INTO `iot_social_platform` VALUES (2, 'wechat_open_web', '0', 'wxd8e66af0d2ac0b9d', 'c1172c438d407d8ebce2ac0e314a18db', 'http://localhost:8080/auth/callback/wechat_open_web', '0', 'admin', '2023-08-23 11:41:37', '2023-10-09 15:54:25', 'admin', NULL, 'http://localhost/login?bindId=', 'http://localhost/login?loginId=', 'http://localhost/login?errorId=');
INSERT INTO `iot_social_platform` VALUES (3, 'wechat_open_mobile', '0', 'wx6be3f0d7bf7154e1', 'b6c1d0da60bd5250857d211cdc64fdc9', 'http://localhost', '0', 'admin', '2023-08-28 14:21:29', NULL, NULL, NULL, 'http://localhost', 'http://localhost', 'http://localhost');
INSERT INTO `iot_social_platform` VALUES (4, 'wechat_open_mini_program', '0', 'wx5bfbadf52adc17f3', '1faddfc3fa6ab2f9ce937f41fcfc7c52', 'http://localhost', '0', 'admin', '2023-09-12 15:39:48', NULL, NULL, NULL, 'http://localhost', 'http://localhost', 'http://localhost');
INSERT INTO `iot_social_platform` VALUES (5, 'wechat_open_web_bind', '0', 'wxd8e66af0d2ac0b9d', 'c1172c438d407d8ebce2ac0e314a18db', 'https://81.71.97.58/prod-api/wechat/wxBind/callback?wxBindId=', '0', 'admin', '2023-10-09 12:04:18', '2023-10-09 15:59:38', 'admin', NULL, 'http://localhost', 'https://81.71.97.58/user/profile?wxBindMsgId=', 'http://localhost');

-- ----------------------------
-- Table structure for iot_social_user
-- ----------------------------
DROP TABLE IF EXISTS `iot_social_user`;
CREATE TABLE `iot_social_user`  (
  `social_user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '第三方系统用户表主键',
  `uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方系统的唯一ID',
  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方用户来源',
  `access_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的授权令牌',
  `expire_in` int(11) NULL DEFAULT NULL COMMENT '第三方用户的授权令牌的有效期（部分平台可能没有）',
  `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '刷新令牌(部分平台可能没有)',
  `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 open id（部分平台可能没有）',
  `uid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 ID(部分平台可能没有)',
  `access_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个别平台的授权信息（部分平台可能没有）',
  `union_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户的 union id(部分平台可能没有)',
  `scope` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户授予的权限(部分平台可能没有)',
  `token_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个别平台的授权信息（部分平台可能没有）',
  `id_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'id token（部分平台可能没有）',
  `mac_algorithm` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性（部分平台可能没有）',
  `mac_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性(部分平台可能没有)',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的授权code（部分平台可能没有）',
  `oauth_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性(部分平台可能没有)',
  `oauth_token_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性(部分平台可能没有)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标记位(0代表存在,2代表删除)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '绑定状态(0:未绑定,1:绑定)',
  `sys_user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '用户性别',
  `source_client` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方用户来源客户端（web、app、小程序）',
  PRIMARY KEY (`social_user_id`) USING BTREE,
  UNIQUE INDEX `iot_social_user_pk`(`social_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 269 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方登录用户' ROW_FORMAT = DYNAMIC;

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
  `is_share_perm` tinyint(1) NULL DEFAULT 0 COMMENT '是否设备分享权限(0-否，1-是)',
  `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `temp_slave_id` bigint(20) NULL DEFAULT NULL COMMENT '从机id',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式',
  `reverse_formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '控制公式',
  `reg_addr` int(255) NULL DEFAULT NULL COMMENT '寄存器地址值',
  `bit_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位定义选项',
  `value_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '解析类型 1.数值 2.选项',
  `is_params` int(1) NULL DEFAULT NULL COMMENT '是否是计算参数',
  `quantity` int(2) NULL DEFAULT NULL COMMENT '读取寄存器数量',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'modbus功能码',
  `parse_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'modbus解析类型',
  PRIMARY KEY (`model_id`) USING BTREE,
  INDEX `iot_things_model_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_things_model_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_things_model_index_model_order`(`model_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 883 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_things_model
-- ----------------------------
INSERT INTO `iot_things_model` VALUES (113, '设备开关', 41, '温湿度智能开关', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 0, 0, 1, 0, 0, 9, '0', NULL, '2022-08-14 00:06:53', '', '2023-03-31 23:43:43', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (125, '空气温度', 41, '温湿度智能开关', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 0, 1, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (126, '空气湿度', 41, '温湿度智能开关', 1, 'admin', 'humidity', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}', 1, 1, 1, 1, 0, 3, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:12', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (127, '二氧化碳', 41, '温湿度智能开关', 1, 'admin', 'co2', 1, 'integer', '{\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}', 1, 1, 1, 1, 0, 2, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:17', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (128, '室内亮度', 41, '温湿度智能开关', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 0, 4, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:44:08', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (129, '运行档位', 41, '温湿度智能开关', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 0, 0, 1, 0, 0, 8, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:49', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (130, '灯光色值', 41, '温湿度智能开关', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 0, 0, 1, 0, 0, 5, '0', NULL, '2022-11-05 23:56:21', '', '2023-09-25 22:57:42', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (131, '屏显消息', 41, '温湿度智能开关', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, 7, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:54', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (132, '上报数据', 41, '温湿度智能开关', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 0, 0, 0, 0, 0, 10, '0', NULL, '2022-11-05 23:56:21', '', '2023-03-31 23:43:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (134, '环境温度过高', 41, '温湿度智能开关', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 0, 0, 1, 0, 0, 0, '0', NULL, '2022-11-05 23:56:29', '', '2023-03-31 23:44:25', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (135, '设备发生异常', 41, '温湿度智能开关', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, 0, '0', NULL, '2022-11-05 23:56:29', '', '2023-03-31 23:44:29', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (161, '子设备', 96, '网关产品', 1, 'admin', 'device', 1, 'array', '{\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1, \"isSharePerm\": 0}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"device_irc\", \"name\": \"射频遥控\", \"order\": 1, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}], \"arrayType\": \"object\", \"arrayCount\": \"5\"}', 0, 0, 0, 0, 1, 10, '0', NULL, '2023-02-25 22:51:53', '', '2023-09-25 23:13:21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (162, '功能分组', 96, '网关产品', 1, 'admin', 'category', 1, 'object', '{\"type\": \"object\", \"params\": [{\"id\": \"category_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"category_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isHistory\": 0, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"category_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"category_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"isChart\": 0, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_gear\", \"name\": \"运行档位\", \"order\": 5, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_switch\", \"name\": \"设备开关\", \"order\": 4, \"isChart\": 0, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}, {\"id\": \"category_irc\", \"name\": \"射频遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控配对\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0, \"isSharePerm\": 1}]}', 0, 0, 0, 0, 1, 9, '0', NULL, '2023-02-25 22:51:53', '', '2023-09-03 11:03:24', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (163, '空气温度', 96, '网关产品', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:03', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (164, '设备开关', 96, '网关产品', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 0, 0, 1, 0, 1, 8, '0', NULL, '2023-02-25 22:52:16', '', '2023-09-03 11:03:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (165, '运行档位', 96, '网关产品', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 0, 0, 1, 0, 1, 7, '0', NULL, '2023-02-25 22:52:16', '', '2023-09-03 11:03:41', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (166, '灯光色值', 96, '网关产品', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 0, 0, 1, 0, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (167, '上报监测数据', 96, '网关产品', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 0, 0, 1, 0, 1, 11, '0', NULL, '2023-02-25 22:52:16', '', '2023-09-03 11:03:11', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (168, '环境温度过高', 96, '网关产品', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 0, 0, 1, 0, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:15', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (169, '设备发生异常', 96, '网关产品', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 0, 0, '0', NULL, '2023-02-25 22:52:16', '', '2023-03-31 16:08:20', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (170, '屏显消息', 96, '网关产品', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 1, 0, 1, 0, '0', NULL, '2023-02-25 22:52:35', '', '2023-09-03 11:03:55', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (171, '设备重启', 96, '网关产品', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 0, 0, 1, 0, 1, 0, '0', NULL, '2023-02-25 22:52:35', '', '2023-09-03 11:03:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (174, '室内亮度', 96, '网关产品', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 0, 0, '0', NULL, '2023-02-26 00:56:39', '', '2023-09-03 10:40:55', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (175, '设备重启', 41, '智能开关', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 0, 0, 1, 0, 0, 6, '0', NULL, '2023-02-26 02:20:40', '', '2023-04-01 23:40:05', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (385, '射频遥控', 41, '★智能开关', 1, 'admin', 'irc', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}', 0, 0, 1, 0, 0, 11, '0', 'admin', '2023-03-31 23:46:36', '', '2023-04-13 01:38:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (439, '上报状态', 41, '★智能开关产品', 1, 'admin', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 0, 0, 0, 0, 0, 12, '0', 'admin', '2023-04-13 01:39:31', '', '2023-04-13 01:39:42', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (493, '漏水值', 112, '★MODBUS协议产品', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"度\"}', 0, 0, 1, 0, 0, 0, '0', '', '2023-09-13 23:33:19', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (494, '温度', 112, '★MODBUS协议产品', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"°\"}', 0, 0, 1, 0, 0, 0, '0', '', '2023-09-13 23:33:19', '', NULL, NULL, 2, NULL, NULL, 0, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (495, '电量', 112, '★MODBUS协议产品', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 1, 0, 0, 0, '0', '', '2023-09-13 23:33:19', '', NULL, NULL, 11, NULL, NULL, 1, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (499, '上报状态', 130, 'TCP测试设备', 1, 'admin', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 0, 0, 0, 0, 1, 0, '0', 'admin', '2023-09-19 11:22:55', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (836, 'DI', 156, 'IO088-12.11.03', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-12 10:33:29', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (837, 'DI', 157, 'IO088-12.11.04', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-12 10:33:30', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (838, 'DI', 158, 'IO088-12.11.05', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-12 10:33:30', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (839, 'DI', 159, 'IO088-12.12.01', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-12 14:11:28', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (840, '上报状态', 160, '智能中控xiaoyue', 31, '564601654', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 0, 0, 0, 0, 1, 0, '0', 'admin', '2023-12-14 23:33:23', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (841, '中控ECU', 160, '智能中控xiaoyue', 31, '564601654', 'ECU', 2, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 1, 0, 1, 0, '0', '18926529123', '2023-12-14 23:33:23', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (862, '123', 162, 'CAT-T200测试13.19', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (863, '54165', 162, 'CAT-T200测试13.19', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 1, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (864, '4651', 162, 'CAT-T200测试13.19', 1, 'admin', '2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 2, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (865, '7894', 162, 'CAT-T200测试13.19', 1, 'admin', '3', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 3, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (866, '54651', 162, 'CAT-T200测试13.19', 1, 'admin', '4', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 4, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (867, '564', 162, 'CAT-T200测试13.19', 1, 'admin', '5', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-19 18:51:04', '', NULL, NULL, 1, NULL, NULL, 5, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (868, '1', 163, 'CAT-T200 TCP测试', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (869, '2', 163, 'CAT-T200 TCP测试', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 1, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (870, '3', 163, 'CAT-T200 TCP测试', 1, 'admin', '2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 2, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (871, '4', 163, 'CAT-T200 TCP测试', 1, 'admin', '3', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 3, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (872, '5', 163, 'CAT-T200 TCP测试', 1, 'admin', '4', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 4, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (873, '6', 163, 'CAT-T200 TCP测试', 1, 'admin', '5', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 10:15:34', '', NULL, NULL, 1, NULL, NULL, 5, NULL, NULL, NULL, 1, '3', 'ushort');
INSERT INTO `iot_things_model` VALUES (875, '111', 165, 'gjz-chanpin', 1, 'admin', 'keystr-1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-12-20 15:47:48', '', '2023-12-20 16:55:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (876, 'DI', 166, 'IO088-12.20', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 16:01:29', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (877, 'DI', 167, '088-12.20', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 0, 0, 0, 1, 0, 0, '0', '', '2023-12-20 16:24:31', '', NULL, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, 8, '2', 'bit');
INSERT INTO `iot_things_model` VALUES (878, '222', 165, 'gjz-chanpin', 1, 'admin', 'keystr-2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-12-20 16:56:05', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (879, '111', 168, 'gjz-chanpin2', 1, 'admin', 'keystr-1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-12-20 17:57:55', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (880, '222', 168, 'gjz-chanpin2', 1, 'admin', 'keystr-2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, '0', '', '2023-12-20 17:58:02', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (881, '电源管理', 169, 'YK电源控制器', 1, 'admin', 'power', 1, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 0, 0, 0, 1, 0, 0, '0', 'admin', '2023-12-26 13:55:05', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model` VALUES (882, '1', 170, '照明开关', 1, 'admin', '1', 1, 'integer', '{\"max\": 1, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"1\"}', 1, 1, 1, 1, 1, 0, '0', '', '2024-01-04 14:55:34', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
  `is_share_perm` tinyint(1) NULL DEFAULT 0 COMMENT '是否设备分享权限(0-否，1-是)',
  `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `temp_slave_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '从机id',
  `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式',
  `reverse_formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '控制公式',
  `reg_addr` int(255) NULL DEFAULT NULL COMMENT '寄存器地址值',
  `bit_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位定义选项',
  `value_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '解析类型 1.数值 2.选项',
  `is_params` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '是否是计算参数,默认否 0=否，1=是',
  `quantity` int(2) NULL DEFAULT NULL COMMENT '读取寄存器数量',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'modbus功能码',
  `old_identifier` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '旧的标识符',
  `old_temp_slave_id` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '旧的从机id',
  `parse_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'modbus解析类型',
  PRIMARY KEY (`template_id`) USING BTREE,
  INDEX `iot_things_model_template_index_tenant_id`(`tenant_id`) USING BTREE,
  INDEX `iot_things_model_template_index_model_order`(`model_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 570 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_things_model_template
-- ----------------------------
INSERT INTO `iot_things_model_template` VALUES (1, '空气温度', 1, 'admin', 'temperature', 1, 'decimal', '{\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 1, 1, 1, 1, 0, 4, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:12:06', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (2, '空气湿度', 1, 'admin', 'humidity', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}', 1, 1, 1, 1, 1, 0, 3, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:12:02', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (3, '二氧化碳', 1, 'admin', 'co2', 1, 'integer', '{\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:57', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (4, '室内亮度', 1, 'admin', 'brightness', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"cd/m2\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:53', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (5, '设备开关', 1, 'admin', 'switch', 2, 'bool', '{\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}', 1, 0, 0, 1, 0, 0, 5, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:48', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (6, '运行档位', 1, 'admin', 'gear', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}', 1, 0, 0, 1, 0, 0, 6, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:43', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (7, '灯光色值', 1, 'admin', 'light_color', 2, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\", \"arrayCount\": \"3\"}', 1, 0, 0, 1, 0, 0, 2, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (8, '屏显消息', 1, 'admin', 'message', 2, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 1, 0, 0, 1, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:32', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (9, '上报监测数据', 1, 'admin', 'report_monitor', 2, 'integer', '{\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 1, 0, 0, 0, 0, 0, 9, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:25', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (10, '环境温度过高', 1, 'admin', 'height_temperature', 3, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}', 1, 0, 0, 1, 0, 0, 8, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:19', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (11, '设备发生异常', 1, 'admin', 'exception', 3, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 1, 0, 0, 7, '0', 'admin', '2022-03-09 17:41:49', 'admin', '2023-04-10 01:11:16', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (12, '光照', 1, 'admin', 'light', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 0, 1, 1, 1, 1, 0, 0, '0', 'wumei', '2022-05-07 09:41:17', 'admin', '2023-04-10 01:11:12', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (13, '压力', 1, 'admin', 'pressure', 1, 'decimal', '{\"max\": 200, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"帕斯卡\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 22:39:18', 'admin', '2023-04-10 01:11:05', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (14, '设备重启', 1, 'admin', 'reset', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"重启\", \"value\": \"restart\"}]}', 1, 0, 0, 1, 0, 0, 0, '0', 'admin', '2023-02-20 23:15:25', 'admin', '2023-04-10 01:11:08', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (15, '电池电压', 1, 'admin', 'voltage', 1, 'decimal', '{\"max\": 5, \"min\": 0, \"step\": 0.001, \"type\": \"decimal\", \"unit\": \"V\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:17:43', 'admin', '2023-04-10 01:10:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (16, '饮水量', 1, 'admin', 'shuiliang', 1, 'integer', '{\"max\": 500, \"min\": 80, \"step\": 1, \"type\": \"integer\", \"unit\": \"ML\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:18:39', 'admin', '2023-04-10 01:10:52', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (17, '灯光', 1, 'admin', 'light', 1, 'integer', '{\"max\": 1000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"Lux\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:19:23', 'admin', '2023-04-10 01:10:49', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (18, '长度', 1, 'admin', 'length', 1, 'integer', '{\"max\": 2000, \"min\": 1, \"step\": 5, \"type\": \"integer\", \"unit\": \"M\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:20:03', 'admin', '2023-04-10 01:10:44', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (19, '心率', 1, 'admin', 'heart_rate', 1, 'integer', '{\"max\": 250, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:21:46', 'admin', '2023-04-10 01:12:40', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (20, '光照强度', 1, 'admin', 'light_level', 1, 'integer', '{\"max\": 89.2, \"min\": 2.5, \"step\": 0.1, \"type\": \"integer\", \"unit\": \"L/g\"}', 1, 1, 1, 1, 1, 0, 0, '0', 'admin', '2023-02-20 23:24:36', 'admin', '2023-04-10 01:10:35', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (21, '状态灯色', 1, 'admin', 'color', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"红色\", \"value\": \"0\"}, {\"text\": \"绿色\", \"value\": \"1\"}, {\"text\": \"蓝色\", \"value\": \"2\"}, {\"text\": \"黄色\", \"value\": \"3\"}]}', 1, 0, 0, 1, 0, 0, 0, '0', 'admin', '2023-02-20 23:26:24', 'admin', '2023-04-10 01:10:32', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (22, '子设备', 1, 'admin', 'device', 2, 'array', '{\"type\": \"array\", \"params\": [{\"id\": \"device_co2\", \"name\": \"二氧化碳\", \"order\": 0, \"isChart\": 1, \"datatype\": {\"max\": 6000, \"min\": 100, \"step\": 1, \"type\": \"integer\", \"unit\": \"ppm\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"device_temperature\", \"name\": \"空气温度-只读\", \"order\": 4, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"device_gear\", \"name\": \"运行档位\", \"order\": 6, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_switch\", \"name\": \"设备开关\", \"order\": 5, \"datatype\": {\"type\": \"bool\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"trueText\": \"打开\", \"arrayType\": \"int\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"device_report_monitor\", \"name\": \"上报监测数据\", \"order\": 9, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\", \"enumList\": [{\"text\": \"\", \"value\": \"\"}], \"arrayType\": \"int\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}], \"arrayType\": \"object\", \"arrayCount\": 5}', 1, 0, 0, 0, 0, 0, 10, '0', 'admin', '2023-02-24 01:10:43', 'admin', '2023-04-13 01:33:38', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (23, '功能分组', 1, 'admin', 'group', 2, 'object', '{\"type\": \"object\", \"params\": [{\"id\": \"group_light\", \"name\": \"光照\", \"order\": 1, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}, \"isHistory\": 1, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_humidity\", \"name\": \"空气湿度\", \"order\": 2, \"isChart\": 1, \"datatype\": {\"max\": 100, \"min\": 0, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"%\"}, \"isMonitor\": 1, \"isReadonly\": 1}, {\"id\": \"group_temperature\", \"name\": \"空气温度-只读\", \"order\": 3, \"isChart\": 0, \"datatype\": {\"max\": 120, \"min\": -20, \"step\": 0.1, \"type\": \"decimal\", \"unit\": \"℃\"}, \"isMonitor\": 0, \"isReadonly\": 1}, {\"id\": \"group_report_monitor\", \"name\": \"上报监测数据\", \"order\": 7, \"datatype\": {\"max\": 10, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"次数\"}, \"isHistory\": 0, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_gear\", \"name\": \"运行档位\", \"order\": 5, \"datatype\": {\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"低速档位\", \"value\": \"0\"}, {\"text\": \"中速档位\", \"value\": \"1\"}, {\"text\": \"中高速档位\", \"value\": \"2\"}, {\"text\": \"高速档位\", \"value\": \"3\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_switch\", \"name\": \"设备开关\", \"order\": 4, \"datatype\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"}, \"isMonitor\": 0, \"isReadonly\": 0}, {\"id\": \"group_irc\", \"name\": \"红外遥控\", \"order\": 6, \"isChart\": 0, \"datatype\": {\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}, \"isHistory\": 1, \"isMonitor\": 0, \"isReadonly\": 0}]}', 1, 0, 0, 0, 0, 0, 11, '0', 'admin', '2023-02-25 22:41:43', 'admin', '2023-08-30 15:29:34', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (201, '频率 ', 1, 'admin', 'frequency', 2, 'integer', '{\"max\": 65535, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"0.001Hz\"}', 1, 0, 0, 1, 0, 0, 0, '0', '', '2023-02-28 16:08:06', 'admin', '2023-04-10 03:37:11', NULL, '3#3', '%s*0.001', '', 27, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (225, '校验位', 1, 'admin', 'check', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"N\", \"value\": \"0\"}, {\"text\": \"O\", \"value\": \"1\"}, {\"text\": \"E\", \"value\": \"2\"}]}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-02-28 16:08:08', 'admin', '2023-04-10 21:36:01', NULL, '3#3', '', '', 771, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (226, '波特率', 1, 'admin', 'baud', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"1200\", \"value\": \"0\"}, {\"text\": \"2400\", \"value\": \"1\"}, {\"text\": \"4800\", \"value\": \"2\"}, {\"text\": \"9600\", \"value\": \"3\"}, {\"text\": \"19200\", \"value\": \"4\"}]}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-02-28 16:08:09', 'admin', '2023-04-10 03:37:32', NULL, '3#3', '', '', 772, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (227, '电压', 1, 'admin', 'voltage', 1, 'integer', '{\"max\": 6, \"min\": 0.1, \"step\": 0.1, \"type\": \"integer\", \"unit\": \"v\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-02-28 16:08:09', 'admin', '2023-04-10 03:37:16', NULL, '3#3', '', '', 773, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (236, '射频遥控', 1, 'admin', 'irc', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"遥控学习\", \"value\": \"FFXX01\"}, {\"text\": \"遥控清码\", \"value\": \"FFXX02\"}, {\"text\": \"打开开关\", \"value\": \"FFXX03\"}, {\"text\": \"关闭开关\", \"value\": \"FFXX04\"}, {\"text\": \"暂停\", \"value\": \"FFXX05\"}, {\"text\": \"锁定\", \"value\": \"FFXX06\"}]}', 1, 0, 0, 1, 0, 0, 0, '0', 'admin', '2023-03-31 23:46:20', 'admin', '2023-04-10 01:09:46', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (250, '漏水值', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"度\"}', 1, 0, 0, 1, 0, 0, 0, '0', '', '2023-04-11 22:35:36', '', '2023-09-13 23:32:34', NULL, '1#1', NULL, NULL, 0, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (251, '温度', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"°\"}', 1, 0, 0, 1, 0, 0, 0, '0', '', '2023-04-11 22:36:10', '', '2023-09-13 23:32:51', NULL, '1#2', NULL, NULL, 0, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (252, '电量', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 1, 0, 0, 0, '0', '', '2023-04-11 22:36:27', '', '2023-09-13 23:33:11', NULL, '1#11', NULL, NULL, 1, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (323, '上报状态', 1, 'admin', 'status', 2, 'enum', '{\"type\": \"enum\", \"showWay\": \"button\", \"enumList\": [{\"text\": \"更新状态\", \"value\": \"update_status\"}]}', 1, 0, 0, 0, 0, 1, 0, '0', 'admin', '2023-04-13 01:35:42', 'admin', '2023-09-03 10:50:16', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (324, 'X位移', 1, 'admin', 'x-shift', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-08-26 19:36:58', '', NULL, NULL, '2#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (325, 'Y位移', 1, 'admin', 'y-shift', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-08-26 19:37:23', '', '2023-08-26 19:37:32', NULL, '2#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (326, 'X位移', 1, 'admin', 'x-shift', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-08-26 19:38:31', '', NULL, NULL, '2#2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (327, 'Y位移', 1, 'admin', 'y-shift', 1, 'decimal', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"decimal\", \"unit\": \"mm\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-08-26 19:38:51', '', NULL, NULL, '2#2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (328, '计件数量', 1, 'admin', '0', 1, 'integer', '{\"max\": 10000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-08-28 15:05:25', '', NULL, NULL, '3#1', NULL, NULL, 0, NULL, NULL, NULL, 1, '3', NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (329, '参数1', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-08-28 15:06:55', '', NULL, NULL, '3#2', NULL, NULL, 0, NULL, NULL, NULL, 1, '3', NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (330, '图片', 1, 'admin', 'image', 1, 'string', '{\"type\": \"string\", \"maxLength\": 10240}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-28 23:19:30', '', NULL, NULL, '2#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (331, '回路状态', 10, 'jamon', 'loop_status', 1, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\"}', 0, 0, 0, 0, 0, 0, 0, '0', '', '2023-08-29 18:21:38', '', NULL, NULL, '4#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (332, '回路状态', 10, 'jamon', 'loop_status', 1, 'array', '{\"type\": \"array\", \"arrayType\": \"integer\"}', 0, 0, 0, 0, 0, 0, 0, '0', '', '2023-08-29 18:23:08', '', NULL, NULL, '4#2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (335, '湿度', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-30 14:05:38', '', '2023-08-30 14:58:28', NULL, '6#1', '%s/10', NULL, 0, NULL, NULL, NULL, 1, '3', NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (336, '温度', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"°C\"}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-30 14:06:05', '', '2023-08-30 14:58:38', NULL, '6#1', '%s/10', NULL, 1, NULL, NULL, NULL, 1, '3', NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (341, '视频', 1, 'admin', 'video', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-30 23:08:51', '', '2023-08-30 23:25:15', NULL, '2#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (342, '图片', 1, 'admin', 'image', 1, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-30 23:21:48', '', '2023-08-30 23:25:22', NULL, '2#2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (343, '状态', 1, 'admin', 'status', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 1, 1, 0, 0, '0', '', '2023-08-30 23:28:00', '', '2023-08-30 23:28:17', NULL, '2#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (359, '相电压UA', 1, 'admin', '37', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 1, '0', '', '2023-12-06 16:32:11', '', '2023-12-07 18:10:53', NULL, '11#1', NULL, NULL, 37, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (360, '相电压UB', 1, 'admin', '38', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 2, '0', '', '2023-12-06 16:32:59', '', '2023-12-07 18:10:56', NULL, '11#1', NULL, NULL, 38, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (361, '相电压UC', 1, 'admin', '39', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 3, '0', '', '2023-12-06 16:34:20', '', '2023-12-07 18:11:00', NULL, '11#1', NULL, NULL, 39, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (362, '线电压UAB', 1, 'admin', '40', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 4, '0', '', '2023-12-06 16:34:46', '', '2023-12-07 18:11:05', NULL, '11#1', NULL, NULL, 40, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (363, '线电压UBC', 1, 'admin', '41', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 5, '0', '', '2023-12-06 16:35:10', '', '2023-12-07 18:11:08', NULL, '11#1', NULL, NULL, 41, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (364, '线电压UAC', 1, 'admin', '42', 1, 'integer', '{\"max\": 500, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 6, '0', '', '2023-12-06 16:35:33', '', '2023-12-07 18:11:10', NULL, '11#1', NULL, NULL, 42, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (365, '电流IA', 1, 'admin', '43', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 7, '0', '', '2023-12-06 16:36:14', '', '2023-12-07 18:11:13', NULL, '11#1', NULL, NULL, 43, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (366, '电流IB', 1, 'admin', '44', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 8, '0', '', '2023-12-06 16:36:44', '', '2023-12-07 18:11:20', NULL, '11#1', NULL, NULL, 44, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (367, '电流IC', 1, 'admin', '45', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 9, '0', '', '2023-12-06 16:37:07', '', '2023-12-07 18:11:24', NULL, '11#1', NULL, NULL, 45, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (368, 'A相有功功率', 1, 'admin', '46', 1, 'integer', '{\"max\": 1000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"W\"}', 1, 0, 0, 0, 1, 0, 10, '0', '', '2023-12-06 16:37:54', '', '2023-12-07 18:11:28', NULL, '11#1', NULL, NULL, 46, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (369, 'B相有功功率', 1, 'admin', '47', 1, 'integer', '{\"max\": 1000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"W\"}', 1, 0, 0, 0, 1, 0, 11, '0', '', '2023-12-06 16:38:18', '', '2023-12-07 18:11:32', NULL, '11#1', NULL, NULL, 47, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (370, 'C相有功功率', 1, 'admin', '48', 1, 'integer', '{\"max\": 1000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"W\"}', 1, 0, 0, 0, 1, 0, 12, '0', '', '2023-12-06 16:39:13', '', '2023-12-07 18:11:35', NULL, '11#1', NULL, NULL, 48, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (371, '总有功功率', 1, 'admin', '49', 1, 'integer', '{\"max\": 3000, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"W\"}', 1, 0, 0, 0, 1, 0, 13, '0', '', '2023-12-06 16:39:42', '', '2023-12-07 18:11:40', NULL, '11#1', NULL, NULL, 49, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (382, '电压', 1, 'admin', '37', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-12-07 17:39:46', '', NULL, NULL, '11#2', NULL, NULL, 37, NULL, NULL, NULL, 1, '3', NULL, NULL, 'ushort');
INSERT INTO `iot_things_model_template` VALUES (394, 'A相电压', 1, 'admin', '0', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '%s*10', '', 0, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (395, 'B相电压', 1, 'admin', '1', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '', '', 1, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (396, 'C相电压', 1, 'admin', '2', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '', '', 2, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (397, 'A相电流', 1, 'admin', '3', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '', '', 3, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (398, 'B相电流', 1, 'admin', '4', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '', '', 4, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (399, 'C相电流', 1, 'admin', '5', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:45:33', '', NULL, NULL, '3#2', '', '', 5, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (400, 'A相电压', 1, 'admin', '0', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '%s*10', '', 0, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (401, 'B相电压', 1, 'admin', '1', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '', '', 1, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (402, 'C相电压', 1, 'admin', '2', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '', '', 2, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (403, 'A相电流', 1, 'admin', '3', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '', '', 3, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (404, 'B相电流', 1, 'admin', '4', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '', '', 4, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (405, 'C相电流', 1, 'admin', '5', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-07 19:46:20', '', NULL, NULL, '3#1', '', '', 5, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (412, 'A相电压', 1, 'admin', '0', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '%s*10', '', 0, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (413, 'B相电压', 1, 'admin', '1', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '', '', 1, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (414, 'C相电压', 1, 'admin', '2', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '', '', 2, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (415, 'A相电流', 1, 'admin', '3', 1, 'integer', '{\"max\": 220, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '', '', 3, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (416, 'B相电流', 1, 'admin', '4', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '', '', 4, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (417, 'C相电流', 1, 'admin', '5', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 1, 0, '0', '', '2023-12-08 08:48:34', '', NULL, NULL, '11#2', '', '', 5, '', NULL, NULL, 1, NULL, NULL, NULL, '');
INSERT INTO `iot_things_model_template` VALUES (419, 'DI-1', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-12-11 10:39:15', '', '2023-12-11 11:07:07', NULL, '13#1', NULL, NULL, 0, NULL, NULL, NULL, 8, '2', NULL, '13#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (424, 'DI', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-12-11 13:31:14', '', '2023-12-12 09:34:33', NULL, '18#1', NULL, NULL, 0, NULL, NULL, NULL, 8, '2', NULL, '18#1', 'bit');
INSERT INTO `iot_things_model_template` VALUES (425, 'DI', 1, 'admin', '0', 1, 'enum', '{\"type\": \"enum\", \"showWay\": \"select\", \"enumList\": [{\"text\": \"0\", \"value\": \"DI-1\"}, {\"text\": \"1\", \"value\": \"DI-2\"}, {\"text\": \"2\", \"value\": \"DI-3\"}, {\"text\": \"3\", \"value\": \"DI-4\"}, {\"text\": \"4\", \"value\": \"DI-5\"}, {\"text\": \"5\", \"value\": \"DI-6\"}, {\"text\": \"6\", \"value\": \"DI-7\"}, {\"text\": \"7\", \"value\": \"DI-8\"}]}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2023-12-11 13:40:03', '', '2023-12-12 10:33:21', NULL, '19#1', NULL, NULL, 0, NULL, NULL, NULL, 8, '2', NULL, NULL, 'bit');
INSERT INTO `iot_things_model_template` VALUES (440, 'data-1', 1, 'admin', 'keystr-1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-12-20 15:36:05', '', '2023-12-20 15:36:32', NULL, '23#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (441, 'data-2', 1, 'admin', 'keystr-2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 1, 1, 1, 1, 0, 0, '0', '', '2023-12-20 15:36:25', '', NULL, NULL, '23#1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (442, '电源管理', 1, 'admin', 'power', 1, 'string', '{\"type\": \"string\", \"maxLength\": 1024}', 1, 0, 0, 0, 1, 0, 0, '0', 'admin', '2023-12-26 13:52:45', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (443, '密码', 1, 'admin', 'k0', 1, 'integer', '{\"max\": 9999, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (444, '地址', 1, 'admin', 'k1', 1, 'integer', '{\"max\": 247, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (445, '波特率', 1, 'admin', 'k2', 1, 'integer', '{\"max\": 38400, \"min\": 1920, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (446, '5A输入CT变比/10mA输入为一次电流', 1, 'admin', 'k3', 1, 'integer', '{\"max\": 9999, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (447, 'Pt变比', 1, 'admin', 'k5', 1, 'integer', '{\"max\": 9999, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (448, '额定电压', 1, 'admin', 'k6', 1, 'integer', '{\"max\": 220, \"min\": 100, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (449, '额定电流', 1, 'admin', 'k7', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (450, '三相回路-总功率因数', 1, 'admin', 'k13', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (451, 'A相电压', 1, 'admin', 'k17', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (452, 'B相电压', 1, 'admin', 'k18', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (453, 'C相电压', 1, 'admin', 'k19', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (454, 'A相电流', 1, 'admin', 'k20', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:42', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (455, 'B相电流', 1, 'admin', 'k21', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (456, 'C相电流', 1, 'admin', 'k22', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (457, 'U12线电压', 1, 'admin', 'k29', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (458, 'U23线电压', 1, 'admin', 'k30', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (459, 'U31线电压', 1, 'admin', 'k31', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (460, '频率', 1, 'admin', 'k32', 1, 'integer', '{\"max\": 99.99, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (461, '三相回路-总有功功率', 1, 'admin', 'k33', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (462, '三相回路-总无功功率', 1, 'admin', 'k36', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (463, 'A相正（市电）有功电能高字节', 1, 'admin', 'k39', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (464, 'A相正（市电）有功电能低字节', 1, 'admin', 'k40', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (465, 'B相正（市电）有功电能高字节', 1, 'admin', 'k41', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (466, 'B相正（市电）有功电能低字节', 1, 'admin', 'k42', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (467, 'C相正（市电）有功电能高字节', 1, 'admin', 'k43', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (468, 'C相正（市电）有功电能低字节', 1, 'admin', 'k44', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (469, 'A相有功功率', 1, 'admin', 'k57', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (470, 'B相有功功率', 1, 'admin', 'k58', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (471, 'C相有功功率', 1, 'admin', 'k59', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (472, 'A相无功功率', 1, 'admin', 'k66', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (473, 'B相无功功率', 1, 'admin', 'k67', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (474, 'C相无功功率', 1, 'admin', 'k68', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (475, 'A相视在功率', 1, 'admin', 'k69', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (476, 'B相视在功率', 1, 'admin', 'k70', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (477, 'C相视在功率', 1, 'admin', 'k71', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (478, '三相总视在功率', 1, 'admin', 'k72', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (479, 'A相功率因数', 1, 'admin', 'k75', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (480, 'B相功率因数', 1, 'admin', 'k76', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (481, 'C相功率因数', 1, 'admin', 'k77', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (482, 'A相正（市电）无功电能高字节', 1, 'admin', 'k84', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (483, 'A相正（市电）无功电能低字节', 1, 'admin', 'k85', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (484, 'B相正（市电）无功电能高字节', 1, 'admin', 'k86', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (485, 'B相正（市电）无功电能低字节', 1, 'admin', 'k87', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (486, 'C相正（市电）无功电能高字节', 1, 'admin', 'k88', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (487, 'C相正（市电）无功电能低字节', 1, 'admin', 'k89', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (488, '三相回路正(市电)总有功电能高字节', 1, 'admin', 'k112', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:43', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (489, '三相回路正(市电)总有功电能低字节', 1, 'admin', 'k113', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:44', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (490, '三相回路正(市电)总无功电能高字节', 1, 'admin', 'k118', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:44', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (491, '三相回路正(市电)总无功电能低字节', 1, 'admin', 'k119', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:34:44', '', NULL, NULL, '2#0', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (492, '密码', 1, 'admin', 'k0', 1, 'integer', '{\"max\": 9999, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (493, '地址', 1, 'admin', 'k1', 1, 'integer', '{\"max\": 247, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (494, '波特率', 1, 'admin', 'k2', 1, 'integer', '{\"max\": 38400, \"min\": 1920, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (495, '5A输入CT变比/10mA输入为一次电流', 1, 'admin', 'k3', 1, 'integer', '{\"max\": 9999, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (496, 'Pt变比', 1, 'admin', 'k5', 1, 'integer', '{\"max\": 9999, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (497, '额定电压', 1, 'admin', 'k6', 1, 'integer', '{\"max\": 220, \"min\": 100, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (498, '额定电流', 1, 'admin', 'k7', 1, 'integer', '{\"max\": 100, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (499, '三相回路-总功率因数', 1, 'admin', 'k13', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (500, 'A相电压', 1, 'admin', 'k17', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (501, 'B相电压', 1, 'admin', 'k18', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (502, 'C相电压', 1, 'admin', 'k19', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (503, 'A相电流', 1, 'admin', 'k20', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (504, 'B相电流', 1, 'admin', 'k21', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (505, 'C相电流', 1, 'admin', 'k22', 1, 'integer', '{\"max\": 200, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"A\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (506, 'U12线电压', 1, 'admin', 'k29', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (507, 'U23线电压', 1, 'admin', 'k30', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (508, 'U31线电压', 1, 'admin', 'k31', 1, 'integer', '{\"max\": 999.9, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"V\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (509, '频率', 1, 'admin', 'k32', 1, 'integer', '{\"max\": 99.99, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (510, '三相回路-总有功功率', 1, 'admin', 'k33', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (511, '三相回路-总无功功率', 1, 'admin', 'k36', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:16', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (512, 'A相正（市电）有功电能高字节', 1, 'admin', 'k39', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (513, 'A相正（市电）有功电能低字节', 1, 'admin', 'k40', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (514, 'B相正（市电）有功电能高字节', 1, 'admin', 'k41', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (515, 'B相正（市电）有功电能低字节', 1, 'admin', 'k42', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (516, 'C相正（市电）有功电能高字节', 1, 'admin', 'k43', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (517, 'C相正（市电）有功电能低字节', 1, 'admin', 'k44', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (518, 'A相有功功率', 1, 'admin', 'k57', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (519, 'B相有功功率', 1, 'admin', 'k58', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (520, 'C相有功功率', 1, 'admin', 'k59', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (521, 'A相无功功率', 1, 'admin', 'k66', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (522, 'B相无功功率', 1, 'admin', 'k67', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (523, 'C相无功功率', 1, 'admin', 'k68', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (524, 'A相视在功率', 1, 'admin', 'k69', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (525, 'B相视在功率', 1, 'admin', 'k70', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (526, 'C相视在功率', 1, 'admin', 'k71', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (527, '三相总视在功率', 1, 'admin', 'k72', 1, 'integer', '{\"max\": 44, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"kW\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (528, 'A相功率因数', 1, 'admin', 'k75', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (529, 'B相功率因数', 1, 'admin', 'k76', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (530, 'C相功率因数', 1, 'admin', 'k77', 1, 'integer', '{\"max\": 1, \"min\": -1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (531, 'A相正（市电）无功电能高字节', 1, 'admin', 'k84', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (532, 'A相正（市电）无功电能低字节', 1, 'admin', 'k85', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (533, 'B相正（市电）无功电能高字节', 1, 'admin', 'k86', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (534, 'B相正（市电）无功电能低字节', 1, 'admin', 'k87', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (535, 'C相正（市电）无功电能高字节', 1, 'admin', 'k88', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (536, 'C相正（市电）无功电能低字节', 1, 'admin', 'k89', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (537, '三相回路正(市电)总有功电能高字节', 1, 'admin', 'k112', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (538, '三相回路正(市电)总有功电能低字节', 1, 'admin', 'k113', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (539, '三相回路正(市电)总无功电能高字节', 1, 'admin', 'k118', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (540, '三相回路正(市电)总无功电能低字节', 1, 'admin', 'k119', 1, 'integer', '{\"max\": 1, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 15:46:17', '', NULL, NULL, '25#1', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `iot_things_model_template` VALUES (541, 'DS18B20温度数据  ', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"摄氏度\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '%s*0.01', '', 0, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (542, 'CO2二氧化碳数据  ', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 1, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (543, 'ADC1的AD值       ', 1, 'admin', '2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 2, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (544, '光照值高16位', 1, 'admin', '3', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 3, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (545, '光照值低16位', 1, 'admin', '4', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 4, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (546, '空气温度', 1, 'admin', '5', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '%s*0.01', '', 5, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (547, '空气湿度', 1, 'admin', '6', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '%s*0.01', '', 6, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (548, '485的地址        ', 1, 'admin', '7', 1, 'integer', '{\"max\": 254, \"min\": 1, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 7, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (549, 'RSSI             ', 1, 'admin', '8', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 8, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (550, 'CO2补偿          ', 1, 'admin', '9', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 9, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (551, 'PM25', 1, 'admin', '10', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 10, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (552, 'PM10', 1, 'admin', '11', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 11, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (553, '电池电压ADC值', 1, 'admin', '12', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 12, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (554, '输入电压ADC值', 1, 'admin', '13', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '%s*10', '', 13, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (555, '系统运行模式              ', 1, 'admin', '14', 1, 'enum', '{\"type\": \"enum\", \"enumList\": [{\"text\": \"一直运行\", \"value\": \"1\"}, {\"text\": \"定时运行\", \"value\": \"0\"}, {\"text\": \"立即进入低功耗定时运行\", \"value\": \"2\"}]}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 14, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (556, '定时运行时间秒 ', 1, 'admin', '15', 2, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"秒\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 15, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (557, '定时睡眠时间秒', 1, 'admin', '16', 2, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"秒\"}', 1, 0, 0, 0, 0, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 16, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (558, '低功耗模式中当前的运行时间', 1, 'admin', '17', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"秒\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 17, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (559, '低功耗模式中当前的睡眠时间', 1, 'admin', '18', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 0, \"type\": \"integer\", \"unit\": \"秒\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-08 17:03:03', '', NULL, NULL, '3#2', '', '', 18, '', NULL, 0, NULL, NULL, NULL, NULL, '数值');
INSERT INTO `iot_things_model_template` VALUES (560, '1', 1, 'admin', '0', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:03:57', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 0, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (561, '2', 1, 'admin', '1', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:03', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 1, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (562, '3', 1, 'admin', '2', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:07', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 2, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (563, '4', 1, 'admin', '3', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:13', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 3, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (564, '5', 1, 'admin', '4', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:16', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 4, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (565, '6', 1, 'admin', '5', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:20', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 5, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (566, '7', 1, 'admin', '6', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:24', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 6, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (567, '8', 1, 'admin', '7', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:27', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 7, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (568, '9', 1, 'admin', '8', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:31', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 8, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');
INSERT INTO `iot_things_model_template` VALUES (569, '10', 1, 'admin', '9', 1, 'integer', '{\"max\": 100, \"min\": 0, \"step\": 1, \"type\": \"integer\", \"unit\": \"\"}', 1, 0, 0, 0, 1, 0, 0, '0', '', '2024-01-09 10:04:35', '', '2024-01-09 10:44:21', NULL, '26#1', NULL, NULL, 9, NULL, NULL, NULL, 1, '3', NULL, '26#1', 'ushort');

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '更新用户',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '设备采集变量模板对象' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_var_temp
-- ----------------------------
INSERT INTO `iot_var_temp` VALUES (1, 'modbus从机组', NULL, 0, NULL, NULL, NULL, NULL, '2023-02-28 14:20:29', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (2, '边缘网关-MCU', NULL, 1, NULL, NULL, NULL, NULL, '2023-08-26 19:25:56', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (3, '工程数据采集模板', NULL, 0, NULL, NULL, NULL, NULL, '2023-08-28 14:20:21', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (4, '测试网关', NULL, 1, NULL, NULL, NULL, NULL, '2023-08-29 18:20:10', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (6, '温湿度模板', NULL, 0, NULL, NULL, NULL, NULL, '2023-08-30 14:04:56', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (7, '测试采集点', NULL, 0, NULL, NULL, NULL, NULL, '2023-10-13 16:46:24', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (11, 'T200测试12.6.1', NULL, 0, NULL, NULL, NULL, NULL, '2023-12-06 16:29:25', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (13, 'IO088测试12.11.01', NULL, 0, NULL, NULL, NULL, NULL, '2023-12-11 10:33:19', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (18, 'IO088-12.11.02', NULL, 0, NULL, NULL, NULL, NULL, '2023-12-11 13:30:30', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (19, 'IO088-12.11.03', NULL, 0, NULL, NULL, NULL, NULL, '2023-12-11 13:39:22', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (22, 'CAT-T200 TCP测试', NULL, 0, NULL, NULL, NULL, NULL, '2023-12-20 10:13:24', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (23, 'gjz-test', NULL, 1, NULL, NULL, NULL, NULL, '2023-12-20 10:15:06', NULL, '2023-12-20 10:18:01', NULL);
INSERT INTO `iot_var_temp` VALUES (25, '测试模板', NULL, 1, NULL, NULL, NULL, NULL, '2024-01-08 15:41:40', NULL, NULL, NULL);
INSERT INTO `iot_var_temp` VALUES (26, '1-9', NULL, 0, NULL, NULL, NULL, NULL, '2024-01-09 10:03:39', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for iot_var_temp_salve
-- ----------------------------
DROP TABLE IF EXISTS `iot_var_temp_salve`;
CREATE TABLE `iot_var_temp_salve`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `device_temp_id` bigint(20) NOT NULL COMMENT '关联的模板id',
  `slave_addr` int(20) NULL DEFAULT NULL COMMENT '从机编号',
  `slave_index` int(20) NULL DEFAULT NULL,
  `slave_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '从机ip地址',
  `slave_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '从机名称',
  `slave_port` int(20) NULL DEFAULT NULL COMMENT '从机端口',
  `addr_start` bigint(20) NULL DEFAULT NULL COMMENT '寄存器起始地址(10进制)',
  `addr_end` bigint(20) NULL DEFAULT NULL COMMENT '寄存器结束地址(10进制)',
  `packet_length` int(20) NULL DEFAULT 32 COMMENT '寄存器批量读取个数',
  `timer` bigint(20) NULL DEFAULT NULL COMMENT '批量获取轮询时间(默认5分钟)',
  `status` tinyint(20) NOT NULL COMMENT '状态 0-启动 1-失效',
  `code` int(20) NULL DEFAULT NULL COMMENT '功能编码',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建用户',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '更新用户',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '变量模板设备从机对象' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of iot_var_temp_salve
-- ----------------------------
INSERT INTO `iot_var_temp_salve` VALUES (1, 1, 1, NULL, NULL, '漏水器', NULL, 0, 0, 1, 300, 0, 4, '2023-02-28 14:43:24', NULL, '2023-04-11 19:02:06', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (2, 1, 2, NULL, NULL, '温湿度计', NULL, 0, 1, 2, 300, 0, 4, '2023-02-28 15:25:00', NULL, '2023-04-11 19:02:04', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (3, 1, 11, NULL, NULL, '电量仪', NULL, 0, 789, 20, 300, 0, 3, '2023-02-28 15:32:36', NULL, '2023-04-11 19:02:00', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (4, 2, 1, NULL, NULL, '标靶#01', NULL, NULL, NULL, 32, NULL, 0, NULL, '2023-08-26 19:35:09', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (5, 2, 2, NULL, NULL, '标靶#02', NULL, NULL, NULL, 32, NULL, 0, NULL, '2023-08-26 19:35:20', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (7, 3, 1, NULL, NULL, '计件传感器', NULL, 0, 10, 10, 60, 0, NULL, '2023-08-28 14:22:09', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (8, 3, 2, NULL, NULL, '机器参数传感器', NULL, 0, 10, 10, 60, 0, NULL, '2023-08-28 15:06:32', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (13, 6, 1, NULL, NULL, '温湿度传感器', NULL, 0, 1, 2, 60, 0, NULL, '2023-08-30 14:05:26', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (17, 2, 0, NULL, NULL, '采集设备', NULL, NULL, NULL, 32, NULL, 0, NULL, '2023-08-30 23:30:25', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (18, 7, 1, NULL, NULL, '电表', NULL, 0, 100, 64, 60, 0, NULL, '2023-10-13 16:46:58', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (21, 11, 1, NULL, NULL, '电表', NULL, 37, 49, 13, 60, 0, NULL, '2023-12-06 16:31:12', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (22, 11, 2, NULL, NULL, '电表2', NULL, 37, 49, 13, 60, 0, NULL, '2023-12-07 16:40:36', NULL, '2023-12-07 17:03:18', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (24, 13, 1, NULL, NULL, 'IO088.12.11.01', NULL, 0, 7, 8, 60, 0, NULL, '2023-12-11 10:34:16', NULL, '2023-12-11 10:53:58', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (28, 18, 1, NULL, NULL, 'IO088-12.11.02', NULL, 0, 8, 8, 60, 0, NULL, '2023-12-11 13:30:57', NULL, '2023-12-11 13:37:56', NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (29, 19, 1, NULL, NULL, 'IO088.12.11.03', NULL, 0, 8, 9, 60, 0, NULL, '2023-12-11 13:39:47', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (32, 22, 1, NULL, NULL, '测试', NULL, 0, 5, 6, 60, 0, NULL, '2023-12-20 10:13:37', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (34, 23, 1, NULL, NULL, 'json-test', NULL, NULL, NULL, 32, NULL, 0, NULL, '2023-12-20 15:35:16', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (35, 25, 1, NULL, NULL, '设备1', NULL, NULL, NULL, 32, NULL, 0, NULL, '2024-01-08 15:46:04', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (36, 2, 6, NULL, NULL, '设备1', NULL, NULL, NULL, 32, NULL, 0, NULL, '2024-01-08 17:02:04', NULL, NULL, NULL, NULL);
INSERT INTO `iot_var_temp_salve` VALUES (37, 26, 1, NULL, NULL, '1-9', NULL, 0, 9, 10, 60, 0, NULL, '2024-01-09 10:03:52', NULL, '2024-01-09 10:44:21', NULL, NULL);

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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `port_ws` int(11) NULL DEFAULT NULL COMMENT 'ws端口',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流媒体服务器配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media_server
-- ----------------------------
INSERT INTO `media_server` VALUES (7, 'fastbee', 1, 'admin', 1, 'http', '192.168.2.120', 'fastbee.com2', '192.168.2.15:8080', '035c73f7-bb6b-4889-a715-d9eb2d192xxx', 8082, 8443, 1935, 554, 0, 1, '30000,30103', 18081, 1, 0, '0', '', '2023-09-26 21:11:43', '', '2023-10-26 21:51:25', NULL, NULL);

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
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`news_id`) USING BTREE,
  INDEX `news_index_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '新闻资讯' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '新闻分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of news_category
-- ----------------------------
INSERT INTO `news_category` VALUES (1, '新闻资讯', 3, '0', '', '2022-04-11 20:53:55', '', '2022-04-13 15:30:22', '新闻资讯信息');
INSERT INTO `news_category` VALUES (2, '相关产品', 2, '0', '', '2022-04-11 20:54:16', '', '2022-04-13 15:30:15', '相关产品推荐');

-- ----------------------------
-- Table structure for notify_channel
-- ----------------------------
DROP TABLE IF EXISTS `notify_channel`;
CREATE TABLE `notify_channel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '通知名称',
  `channel_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道类型',
  `provider` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '服务商',
  `config_content` varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '配置内容',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '通知渠道' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notify_channel
-- ----------------------------
INSERT INTO `notify_channel` VALUES (1, '阿里云短信', 'sms', 'alibaba', '{\"accessKeyId\":\"LTAI5tFVCwhmWL5BE1RH1dtQ\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:24:24', NULL, '2024-02-06 10:02:24', 0);
INSERT INTO `notify_channel` VALUES (2, '阿里云语音', 'voice', 'alibaba', '{\"accessKeyId\":\"LTAI5tFVCwhmWL5BE1RH1dtQ\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:24:47', NULL, '2024-02-06 10:02:19', 0);
INSERT INTO `notify_channel` VALUES (3, '腾讯云短信', 'sms', 'tencent', '{\"accessKeyId\":\"AKIDhGe5Pmz3zkIeszuc6VkrBYqEYmvwyV1N\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:25:31', NULL, '2024-02-06 10:02:14', 0);
INSERT INTO `notify_channel` VALUES (4, '腾讯云语音', 'voice', 'tencent', '{\"accessKeyId\":\"AKIDhGe5Pmz3zkIeszuc6VkrBYqEYmvwyV1N\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:26:01', NULL, '2024-02-06 10:02:09', 0);
INSERT INTO `notify_channel` VALUES (5, 'QQ邮箱', 'email', 'qq', '{\"smtpServer\":\"smtp.qq.com\",\"port\":\"465\",\"username\":\"\",\"password\":\"\",\"sslEnable\":true,\"authEnable\":true,\"retryInterval\":\"5\",\"maxRetries\":\"1\"}', 1, 'admin', NULL, '2024-01-25 17:27:34', NULL, '2024-01-25 17:27:34', 0);
INSERT INTO `notify_channel` VALUES (6, '163邮箱', 'email', '163', '{\"smtpServer\":\"smtp.163.com\",\"port\":\"465\",\"username\":\"\",\"password\":\"\",\"sslEnable\":true,\"authEnable\":true,\"retryInterval\":\"5\",\"maxRetries\":\"1\"}', 1, 'admin', NULL, '2024-01-25 17:27:58', NULL, '2024-01-25 17:27:58', 0);
INSERT INTO `notify_channel` VALUES (7, '微信小程序', 'wechat', 'mini_program', '{\"appId\":\"wx5bfbadf52adc17f3\",\"appSecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:28:24', NULL, '2024-01-31 14:32:39', 0);
INSERT INTO `notify_channel` VALUES (8, '企业微信群机器人', 'wechat', 'wecom_robot', '{\"webHook\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:29:06', NULL, '2024-01-31 14:50:38', 0);
INSERT INTO `notify_channel` VALUES (9, '企业微信应用消息', 'wechat', 'wecom_apply', '{\"corpId\":\"ww4761023a5d81550f\",\"corpSecret\":\"\",\"agentId\":\"1000005\"}', 1, 'admin', NULL, '2024-01-25 17:30:47', NULL, '2024-02-06 10:01:58', 0);
INSERT INTO `notify_channel` VALUES (10, '钉钉消息通知', 'dingtalk', 'work', '{\"appKey\":\"dingpy8h6c7cft1knlwl\",\"appSecret\":\"\",\"agentId\":\"2862570717\"}', 1, 'admin', NULL, '2024-01-25 17:35:53', NULL, '2024-02-06 10:01:52', 0);
INSERT INTO `notify_channel` VALUES (11, '钉钉群机器人', 'dingtalk', 'group_robot', '{\"webHook\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:38:58', NULL, '2024-01-31 15:26:39', 0);

-- ----------------------------
-- Table structure for notify_log
-- ----------------------------
DROP TABLE IF EXISTS `notify_log`;
CREATE TABLE `notify_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知日志ID',
  `channel_id` bigint(20) NOT NULL COMMENT '渠道编号',
  `notify_template_id` bigint(20) NOT NULL COMMENT '通知模版编号',
  `msg_content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL COMMENT '消息内容',
  `send_account` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '发送账号',
  `send_status` tinyint(4) NOT NULL COMMENT '发送状态',
  `result_content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL COMMENT '返回内容',
  `service_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '业务编码(唯一启用)',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '通知日志' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for notify_template
-- ----------------------------
DROP TABLE IF EXISTS `notify_template`;
CREATE TABLE `notify_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道名称',
  `service_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '业务编码(唯一启用)',
  `channel_id` bigint(20) NULL DEFAULT NULL COMMENT '通知渠道账号',
  `channel_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道类型',
  `provider` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '服务商',
  `msg_params` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL COMMENT '模板配置参数',
  `status` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否启用 0-不启用 1-启用',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '通知模版' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notify_template
-- ----------------------------
INSERT INTO `notify_template` VALUES (1, '告警阿里云短信', 'alert', 1, 'sms', 'alibaba', '{\"sendAccount\":\"15752221201,15187816573\",\"templateId\":\"SMS_465165225\",\"signature\":\"蜂信物联\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\"}', 0, NULL, '2024-01-26 09:21:57', NULL, '2024-02-05 09:36:16', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (2, '验证码阿里云短信', 'captcha', 1, 'sms', 'alibaba', '{\"templateId\":\"SMS_464385158\",\"signature\":\"蜂信物联\",\"content\":\"验证码${code}，有效期5分钟\"}', 0, NULL, '2024-01-26 09:23:55', NULL, '2024-01-30 13:59:47', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (3, '验证码腾讯云短信', 'captcha', 3, 'sms', 'tencent', '{\"templateId\":\"2047168\",\"signature\":\"曲靖蜂信科技\",\"sdkAppId\":\"1400882003\",\"content\":\"验证码{1}，有效期5分钟\"}', 0, NULL, '2024-01-26 09:28:50', NULL, '2024-01-26 09:28:52', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (4, '告警腾讯云短信', 'alert', 3, 'sms', 'tencent', '{\"sendAccount\":\"15752221201,15187816573\",\"templateId\":\"2044927\",\"signature\":\"曲靖蜂信科技\",\"sdkAppId\":\"1400882003\",\"content\":\"您的设备:{1},设备编号:{2},在{3}发生{4}告警\"}', 0, NULL, '2024-01-26 09:37:18', NULL, '2024-02-01 09:30:49', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (5, '告警QQ邮箱', 'alert', 5, 'email', 'qq', '{\"sendAccount\":\"1667783625@qq.com,2698076424@qq.com\",\"title\":\"设备告警\",\"attachment\":\"http://81.71.97.58/prod-api/profile/upload/2024/02/07/1_20240207151859A044.png\",\"content\":\"<p>您的设备:#{name},设备编号:#{serialnumber},在#{address}发生#{alert}告警<img src=\\\"http://81.71.97.58/dev-api/profile/upload/2024/02/07/1_20240207151904A045.png\\\"></p>\"}', 1, NULL, '2024-01-26 09:43:26', NULL, '2024-02-07 15:31:32', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (6, '告警163邮箱', 'alert', 6, 'email', '163', '{\"sendAccount\":\"\",\"title\":\"设备告警\",\"attachment\":\"https://localhost/prod-api/profile/upload/2024/02/07/8233ffb6-8ad3-4ec6-8192-d8ae0b207771_20240207134906A027.jpg\",\"content\":\"<p>您的设备:#{name},设备编号:#{serialnumber},在#{address}发生#{alert}告警<img src=\\\"http://81.71.97.58:8080/profile/upload/2024/02/07/8233ffb6-8ad3-4ec6-8192-d8ae0b207771_20240207134859A026.jpg\\\"></p>\"}', 0, NULL, '2024-01-26 09:44:05', NULL, '2024-02-07 13:49:08', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (7, '告警阿里云语音', 'alert', 2, 'voice', 'alibaba', '{\"sendAccount\":\"15752221201，15187816573\",\"templateId\":\"TTS_287005231\",\"content\":\"您的设备:${name}，在${address}发生告警，请尽快处理\",\"playTimes\":\"1\",\"volume\":\"50\",\"speed\":\"0\"}', 0, NULL, '2024-01-26 09:49:23', NULL, '2024-02-05 09:36:17', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (8, '告警腾讯云语音', 'alert', 4, 'voice', 'tencent', '{\"sendAccount\":\"15752221201,15187816573\",\"sdkAppId\":\"1400821558\",\"templateId\":\"1480599\",\"content\":\"您的设备:{1},设备编号:{2},在{3}发生{4}告警\"}', 0, NULL, '2024-01-26 09:53:07', NULL, '2024-02-01 11:31:35', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (9, '告警微信小程序', 'alert', 7, 'wechat', 'mini_program', '{\"sendAccount\":\"24\",\"templateId\":\"M5OmcrDIGwg4sD_gMjBgDrtk2LM_x1N9mKtxTVYJjnA\",\"redirectUrl\":\"/pages/tabBar/alert/index\",\"content\":\"报警设备 {{thing1.DATA}}  设备编号 {{character_string7.DATA}}\"}', 0, NULL, '2024-01-26 10:22:42', NULL, '2024-02-05 10:46:49', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (10, '告警企业微信应用消息', 'alert', 9, 'wechat', 'wecom_apply', '{\"sendAccount\":\"shadow,oh\",\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"url\":\"https://iot.fastbee.cn/\",\"picUrl\":\"\",\"msgType\":\"news\"}', 0, NULL, '2024-01-26 10:25:58', NULL, '2024-02-05 09:36:18', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (11, '告警企业微信群机器人', 'alert', 8, 'wechat', 'wecom_robot', '{\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"url\":\"https://iot.fastbee.cn/\",\"picUrl\":\"http://81.71.97.58/prod-api/profile/upload/2024/02/07/1_20240207152000A047.png\",\"msgType\":\"news\"}', 1, NULL, '2024-01-26 10:26:57', NULL, '2024-02-07 15:20:01', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (12, '告警钉钉消息通知', 'alert', 10, 'dingtalk', 'work', '{\"deptId\":\"\",\"sendAllEnable\":true,\"sendAccount\":\"\",\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"messageUrl\":\" https://iot.fastbee.cn/  \",\"picUrl\":\"http://localhost:8080/profile/upload/2024/01/31/微信图片_20240119101536_20240131153350A001.png\",\"msgType\":\"link\"}', 0, NULL, '2024-01-26 10:27:50', NULL, '2024-02-05 09:36:19', 0, 1, 'admin');
INSERT INTO `notify_template` VALUES (13, '告警钉钉机器人', 'alert', 11, 'dingtalk', 'group_robot', '{\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"messageUrl\":\"https://iot.fastbee.cn\",\"picUrl\":\"http://81.71.97.58/prod-api/profile/upload/2024/02/07/1_20240207151952A046.png\",\"msgType\":\"link\"}', 1, NULL, '2024-01-26 10:28:34', NULL, '2024-02-07 15:19:54', 0, 1, 'admin');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth_access_token
-- ----------------------------
INSERT INTO `oauth_access_token` VALUES ('eb357bf9b62458a1a280e9435efdda34', 0xACED0005737200436F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E636F6D6D6F6E2E44656661756C744F4175746832416363657373546F6B656E0CB29E361B24FACE0200064C00156164646974696F6E616C496E666F726D6174696F6E74000F4C6A6176612F7574696C2F4D61703B4C000A65787069726174696F6E7400104C6A6176612F7574696C2F446174653B4C000C72656672657368546F6B656E74003F4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F636F6D6D6F6E2F4F417574683252656672657368546F6B656E3B4C000573636F706574000F4C6A6176612F7574696C2F5365743B4C0009746F6B656E547970657400124C6A6176612F6C616E672F537472696E673B4C000576616C756571007E000578707372001E6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794D6170593614855ADCE7D002000078707372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000018C440553ED7870737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C0001637400164C6A6176612F7574696C2F436F6C6C656374696F6E3B7870737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F400000000000027400047265616474000577726974657874000662656172657274001B2D764C39434439795253345151376E646E66545733674236494155, 'b708289f373582cdd4710d04f87b6f96', 'admin', 'fastbee-dueros', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E0011787074000E666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000A740004636F646574000638446552727074000A6772616E745F74797065740012617574686F72697A6174696F6E5F636F64657400076F70656E5F69647400406635643930643334333539363435396232636238343334306231646465363839353162616532653866323430376534646266323961383631396165636139643374000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F65326566636666663936383964633462366166363764373865313039363934647400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000D636C69656E745F7365637265747400205332456E65487864745E4D48684276384E23245E7479366E71244E5159324E6474000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696474000E666173746265652D647565726F7378737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0030770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6532656663666666393638396463346236616636376437386531303936393464707371007E0030770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0030770C000000103F4000000000000171007E0022787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A3170707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E00454C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E00454C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E004578707400094368726F6D652031317372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000677371007E00490000018C477BE8E17400093132372E302E302E31740008E58685E7BD9149507371007E00490000018C42558CE174000A57696E646F77732031307371007E0030770C000000023F400000000000017400052A3A2A3A2A7874002434613835666630662D323866392D343837372D396435652D3438373862663934386435377372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E00454C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E00455B0007726F6C6549647371007E00574C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E00454C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00564C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0056787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED850787371007E00173F400000000000007708000000100000000078740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F32322F626C6F625F3230323331313232313730383331413030332E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E00454C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E00454C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E005870707371007E00173F40000000000000770800000010000000007870707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004B74000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E004A000000017371007E0049000000000000006570707400013171007E004B7400103136343737303730374071712E636F6D7371007E005B77080000018C4254DCB8787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00575B00076D656E7549647371007E00574C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E00454C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00624C000673746174757371007E000F7871007E005870707371007E00173F4000000000000077080000001000000000787070707000000074000131707070707371007E0049000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E006A7400013078740001307400013071007E007874000561646D696E71007E0078, NULL);

-- ----------------------------
-- Table structure for oauth_approvals
-- ----------------------------
DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals`  (
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `clientId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expiresAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lastModifiedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth_approvals
-- ----------------------------
INSERT INTO `oauth_approvals` VALUES ('fastbee', 'csfastbee-dueros', 'read', 'APPROVED', '2023-12-22 15:17:37', '2023-11-22 15:17:37');
INSERT INTO `oauth_approvals` VALUES ('fastbee', 'csfastbee-dueros', 'write', 'APPROVED', '2023-12-22 15:17:37', '2023-11-22 15:17:37');
INSERT INTO `oauth_approvals` VALUES ('admin', 'csfastbee-dueros', 'read', 'APPROVED', '2023-12-22 15:28:28', '2023-11-22 15:28:28');
INSERT INTO `oauth_approvals` VALUES ('admin', 'csfastbee-dueros', 'write', 'APPROVED', '2023-12-22 15:28:28', '2023-11-22 15:28:28');
INSERT INTO `oauth_approvals` VALUES ('admin', 'fastbee-dueros', 'write', 'APPROVED', '2023-12-22 16:42:05', '2023-11-22 16:42:05');
INSERT INTO `oauth_approvals` VALUES ('admin', 'fastbee-dueros', 'read', 'APPROVED', '2023-12-22 16:42:05', '2023-11-22 16:42:05');

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
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
  `status` tinyint(2) NULL DEFAULT 0 COMMENT '启用状态',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `cloud_skill_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '云技能id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES (1, 'fastbee-dueros', 'speaker-service', 'S2EneHxdt^MHhBv8N#$^ty6nq$NQY2Nd', 'read,write', 'authorization_code,fresh_token', 'https://xiaodu.baidu.com/saiya/auth/e2efcfff9689dc4b6af67d78e109694d', 'ROLE_ADMIN', NULL, NULL, NULL, 'false', 1, 0, NULL, 'f7eb644d-0739-93ef-cd8e-d02400899750');
INSERT INTO `oauth_client_details` VALUES (2, 'tianmao', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'authorization_code,fresh_token', 'https://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'true', 2, 0, NULL, NULL);
INSERT INTO `oauth_client_details` VALUES (3, 'xiaoai', 'speaker-service', '$2a$10$jMEhxWXpc6KsMyFF0JJ3kuoVHOp.tEsTCvaJHnQqfGtYKo4.scv/m', 'read,write', 'authorization_code,fresh_token', 'https://xiaodu.baidu.com/saiya/auth/22c6bd1489c8396f00cc25bf2d9d0206', 'ROLE_ADMIN', 7200, 7200, NULL, 'true', 3, 0, NULL, NULL);
INSERT INTO `oauth_client_details` VALUES (8, 'other', 'speaker-service', 'MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEinh/CuUD0bhsLQRw65LAUDSFSE4q eLqucMk6G3vECZmKLDCSlDISaPt8SAzYWK0c', 'read,write', 'authorization_code,client_credentials', 'https://www.baidu.com/', NULL, 7200, 7200, '', NULL, 4, 1, NULL, '');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for oauth_code
-- ----------------------------
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`  (
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `authentication` blob NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth_code
-- ----------------------------
INSERT INTO `oauth_code` VALUES ('dz_RZa', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203931303735443637363134334337434632373038343233373030454646453844707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('2rKJPT', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203038454336303831353543333239434541333741424243343237434144454346707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000023F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED850787371007E00173F400000000000007708000000100000000078740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707371007E00173F40000000000000770800000010000000007870707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5DDF108787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00534C000673746174757371007E000F7871007E004970707371007E00173F4000000000000077080000001000000000787070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E005B7400013078740001307400013071007E006974000561646D696E71007E0069);
INSERT INTO `oauth_code` VALUES ('frzHx9', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203038454336303831353543333239434541333741424243343237434144454346707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000023F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED850787371007E00173F400000000000007708000000100000000078740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707371007E00173F40000000000000770800000010000000007870707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5DDF108787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00534C000673746174757371007E000F7871007E004970707371007E00173F4000000000000077080000001000000000787070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E005B7400013078740001307400013071007E006974000561646D696E71007E0069);
INSERT INTO `oauth_code` VALUES ('nBQYFA', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203630343036384244303736314345413145463241323333343237324345313144707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('rIglwz', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400204544453336353730424443434245373432313342333542313732353545384631707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('nWELM8', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400204544453336353730424443434245373432313342333542313732353545384631707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('rGWf3A', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203544333841323243364238353833393533353037413341444437433542303231707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('9LPsSG', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400204139363339324133353442413544384631333234373042314137334430313831707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('9jiJ_8', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203338303031344334354332364534444544373833394543453336303330344235707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('HKnawI', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203338303031344334354332364534444544373833394543453336303330344235707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('rr8TDw', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203338303031344334354332364534444544373833394543453336303330344235707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('uAJEqp', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203338303031344334354332364534444544373833394543453336303330344235707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006470707070707371007E0027770C000001003F4000000000006374001073797374656D3A706F73743A6C69737474000B696F743A6C6F673A6164647400126D6F6E69746F723A64727569643A6C697374740015696F743A6E65777343617465676F72793A6C69737474001173797374656D3A6D656E753A717565727974001073797374656D3A6D656E753A6C69737474000F696F743A7363656E653A717565727974000D696F743A73616C76653A61646474000E696F743A73616C76653A6C69737474001173797374656D3A757365723A7175657279740012696F743A63617465676F72793A7175657279740010696F743A73696D756C6174653A61646474000F696F743A6465766963653A6C69737474000C696F743A74656D703A616464740010696F743A70726F746F636F6C3A61646474001173797374656D3A646570743A717565727974000D696F743A67726F75703A61646474000F696F743A6576656E743A7175657279740013696F743A617574686F72697A653A717565727974001173797374656D3A646963743A7175657279740010696F743A6465766963653A74696D65727400146D6F6E69746F723A6F6E6C696E653A71756572797400136D6F6E69746F723A6F6E6C696E653A6C69737474000D696F743A6C6F673A71756572797400176D6F6E69746F723A6C6F67696E696E666F723A6C69737474000F696F743A64657461696C3A6C697374740011696F743A617574686F72697A653A616464740011696F743A706C6174666F726D3A6C69737474001273797374656D3A636F6E6669673A6C69737474000C696F743A7461736B3A616464740011696F743A73696D756C6174653A6C697374740012696F743A6669726D776172653A717565727974001373797374656D3A636F6E6669673A717565727974000D696F743A7363656E653A6164647400186D6F6E69746F723A6C6F67696E696E666F723A717565727974000F746F6F6C3A6275696C643A6C697374740011746F6F6C3A737761676765723A6C697374740010696F743A74656D706C6174653A61646474000D696F743A616C6572743A6C6F6774000E696F743A7461736B3A717565727974000E696F743A6E6577733A7175657279740011696F743A70726F746F636F6C3A6C69737474000D696F743A74656D703A6C69737474000D696F743A706F696E743A61646474000C696F743A6E6577733A61646474000F696F743A766964656F3A717565727974000D696F743A6576656E743A6164647400106D6F6E69746F723A6A6F623A6C697374740010696F743A63617465676F72793A61646474000F696F743A73616C76653A7175657279740011696F743A63617465676F72793A6C697374740010696F743A6465766963653A717565727974000E746F6F6C3A67656E3A7175657279740010696F743A64657461696C3A717565727974000F696F743A67726F75703A717565727974000D696F743A6E6577733A6C69737474001073797374656D3A646963743A6C6973747400116D6F6E69746F723A6A6F623A7175657279740012696F743A70726F746F636F6C3A717565727974000E696F743A64657461696C3A616464740010696F743A70726F647563743A6C69737474001273797374656D3A6E6F746963653A6C69737474001373797374656D3A6E6F746963653A717565727974000E696F743A766964656F3A6C69737474000E696F743A7363656E653A6C697374740014696F743A6E65777343617465676F72793A61646474000E696F743A706F696E743A6C69737474000D746F6F6C3A67656E3A6C697374740016696F743A6E65777343617465676F72793A7175657279740011696F743A6669726D776172653A6C69737474001173797374656D3A706F73743A7175657279740010696F743A6465766963653A736861726574000D696F743A7461736B3A6C69737474000D696F743A616C6572743A61646474000D696F743A766964656F3A61646474001073797374656D3A726F6C653A6C69737474000C696F743A6C6F673A6C69737474000F696F743A706F696E743A717565727974001073797374656D3A646570743A6C697374740016696F743A636C69656E7444657461696C733A6C697374740015696F743A636C69656E7444657461696C733A61646474000F696F743A616C6572743A71756572797400146D6F6E69746F723A6F7065726C6F673A6C69737474000E696F743A6576656E743A6C6973747400136D6F6E69746F723A7365727665723A6C697374740012696F743A73696D756C6174653A717565727974000E696F743A67726F75703A6C697374740010696F743A6669726D776172653A6164647400156D6F6E69746F723A6F7065726C6F673A7175657279740017696F743A636C69656E7444657461696C733A717565727974001073797374656D3A757365723A6C697374740011696F743A70726F647563743A7175657279740012696F743A74656D706C6174653A71756572797400126D6F6E69746F723A63616368653A6C69737474000E696F743A74656D703A717565727974000E696F743A6465766963653A61646474000F696F743A70726F647563743A61646474001173797374656D3A726F6C653A7175657279740011696F743A74656D706C6174653A6C69737478707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00AA4C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00A94C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E00A9787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017F6DDE4A9878707070707074003F2F70726F66696C652F6176617461722F323032332F31302F30382F313639363735373335313139315F3230323331303038313732393131413034302E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E00AB70707070707070740001307371007E000B00000000770400000000787071007E004274000CE89C82E4BFA1E789A9E881947074000746617374426565737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000007371007E0040000000000000000070707400013071007E004274000D3232313131324071712E636F6D7371007E00AE77080000018BAD7876B87874000D33392E3137302E36332E32313974000CE6B8B8E5AEA2E8B4A6E58FB774003C243261243130246B4B655A707472546E536C6D3066656E635834553265712E516961756B44732E44636B6955734D437756547868304953324C52512E74000B31353838383838383838347070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00AA5B00076D656E7549647371007E00AA4C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00B34C000673746174757371007E000F7871007E00AB7070707070707000000074000131707070707371007E0040000000000000000474000776697369746F72740006E6B8B8E5AEA27371007E00B900000004740001307874000130740001307371007E004000000000000000067400076661737462656571007E00CE);
INSERT INTO `oauth_code` VALUES ('3MbnzC', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E001178707400106373666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F62306666306532613864316462356439373366393832356663623731396135667400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6230666630653261386431646235643937336639383235666362373139613566707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203737323641454639443445383543364338324238324139374130454146334333707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000103F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED8507870740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707070707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5F11F30787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00524C000673746174757371007E000F7871007E00497070707070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E00597400013078740001307400013071007E006674000561646D696E71007E0066);
INSERT INTO `oauth_code` VALUES ('uobjde', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E0011787074000E666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F65326566636666663936383964633462366166363764373865313039363934647400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6532656663666666393638396463346236616636376437386531303936393464707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203339423734393141363034383435333434424535304632364235454231313946707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000103F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED8507870740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707070707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5F11F30787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00524C000673746174757371007E000F7871007E00497070707070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E00597400013078740001307400013071007E006674000561646D696E71007E0066);
INSERT INTO `oauth_code` VALUES ('R4Ayzu', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E0011787074000E666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F65326566636666663936383964633462366166363764373865313039363934647400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6532656663666666393638396463346236616636376437386531303936393464707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203032463331374338423130453042333938394135394141454135464244383936707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000103F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED8507870740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707070707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5F11F30787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00524C000673746174757371007E000F7871007E00497070707070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E00597400013078740001307400013071007E006674000561646D696E71007E0066);
INSERT INTO `oauth_code` VALUES ('dMaUGS', 0xACED0005737200416F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F417574683241757468656E7469636174696F6EBD400B02166252130200024C000D73746F7265645265717565737474003C4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F4F4175746832526571756573743B4C00127573657241757468656E7469636174696F6E7400324C6F72672F737072696E676672616D65776F726B2F73656375726974792F636F72652F41757468656E7469636174696F6E3B787200476F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E416273747261637441757468656E7469636174696F6E546F6B656ED3AA287E6E47640E0200035A000D61757468656E746963617465644C000B617574686F7269746965737400164C6A6176612F7574696C2F436F6C6C656374696F6E3B4C000764657461696C737400124C6A6176612F6C616E672F4F626A6563743B787000737200266A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654C697374FC0F2531B5EC8E100200014C00046C6973747400104C6A6176612F7574696C2F4C6973743B7872002C6A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65436F6C6C656374696F6E19420080CB5EF71E0200014C00016371007E00047870737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A657870000000007704000000007871007E000C707372003A6F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E4F41757468325265717565737400000000000000010200075A0008617070726F7665644C000B617574686F72697469657371007E00044C000A657874656E73696F6E7374000F4C6A6176612F7574696C2F4D61703B4C000B72656469726563745572697400124C6A6176612F6C616E672F537472696E673B4C00077265667265736874003B4C6F72672F737072696E676672616D65776F726B2F73656375726974792F6F61757468322F70726F76696465722F546F6B656E526571756573743B4C000B7265736F7572636549647374000F4C6A6176612F7574696C2F5365743B4C000D726573706F6E7365547970657371007E0011787200386F72672E737072696E676672616D65776F726B2E73656375726974792E6F61757468322E70726F76696465722E426173655265717565737436287A3EA37169BD0200034C0008636C69656E74496471007E000F4C001172657175657374506172616D657465727371007E000E4C000573636F706571007E0011787074000E666173746265652D647565726F73737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C654D6170F1A5A8FE74F507420200014C00016D71007E000E7870737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000674000573636F706574000A7265616420777269746574000D726573706F6E73655F74797065740004636F646574000C72656469726563745F75726974004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F65326566636666663936383964633462366166363764373865313039363934647400057374617465740020346635653763303462313731306262653836376535376431373434613534373874000A647565726F735F7569647400203466356537633034623137313062626538363765353764313734346135343738740009636C69656E745F696471007E001478737200256A6176612E7574696C2E436F6C6C656374696F6E7324556E6D6F6469666961626C65536574801D92D18F9B80550200007871007E0009737200176A6176612E7574696C2E4C696E6B656448617368536574D86CD75A95DD2A1E020000787200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000274000472656164740005777269746578017371007E0027770C000000103F40000000000001737200426F72672E737072696E676672616D65776F726B2E73656375726974792E636F72652E617574686F726974792E53696D706C654772616E746564417574686F7269747900000000000002260200014C0004726F6C6571007E000F787074000A524F4C455F41444D494E787371007E00173F40000000000000770800000010000000007874004468747470733A2F2F7869616F64752E62616964752E636F6D2F73616979612F617574682F6532656663666666393638396463346236616636376437386531303936393464707371007E0027770C000000103F4000000000000174000F737065616B65722D73657276696365787371007E0027770C000000103F4000000000000171007E001C787372004F6F72672E737072696E676672616D65776F726B2E73656375726974792E61757468656E7469636174696F6E2E557365726E616D6550617373776F726441757468656E7469636174696F6E546F6B656E00000000000002260200024C000B63726564656E7469616C7371007E00054C00097072696E636970616C71007E00057871007E0003017372001F6A6176612E7574696C2E436F6C6C656374696F6E7324456D7074794C6973747AB817B43CA79EDE0200007870737200486F72672E737072696E676672616D65776F726B2E73656375726974792E7765622E61757468656E7469636174696F6E2E57656241757468656E7469636174696F6E44657461696C7300000000000002260200024C000D72656D6F74654164647265737371007E000F4C000973657373696F6E496471007E000F787074000F303A303A303A303A303A303A303A317400203446384146453631364341394445304645443932413942453245383031344438707372002E636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E6D6F64656C2E4C6F67696E55736572000000000000000102000B4C000762726F7773657271007E000F4C00066465707449647400104C6A6176612F6C616E672F4C6F6E673B4C000A65787069726554696D6571007E003D4C000669706164647271007E000F4C000D6C6F67696E4C6F636174696F6E71007E000F4C00096C6F67696E54696D6571007E003D4C00026F7371007E000F4C000B7065726D697373696F6E7371007E00114C0005746F6B656E71007E000F4C00047573657274002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973557365723B4C000675736572496471007E003D7870707372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000006770707070707371007E0027770C000000103F400000000000017400052A3A2A3A2A78707372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E5379735573657200000000000000010200124C000661766174617271007E000F4C000764656C466C616771007E000F4C00046465707474002F4C636F6D2F666173746265652F636F6D6D6F6E2F636F72652F646F6D61696E2F656E746974792F537973446570743B4C000664657074496471007E003D4C0005656D61696C71007E000F4C00096C6F67696E446174657400104C6A6176612F7574696C2F446174653B4C00076C6F67696E497071007E000F4C00086E69636B4E616D6571007E000F4C000870617373776F726471007E000F4C000B70686F6E656E756D62657271007E000F5B0007706F73744964737400115B4C6A6176612F6C616E672F4C6F6E673B4C0006726F6C65496471007E003D5B0007726F6C6549647371007E00484C0005726F6C657371007E00084C000373657871007E000F4C000673746174757371007E000F4C000675736572496471007E003D4C0008757365724E616D6571007E000F78720029636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E42617365456E7469747900000000000000010200074C0008637265617465427971007E000F4C000A63726561746554696D6571007E00474C0006706172616D7371007E000E4C000672656D61726B71007E000F4C000B73656172636856616C756571007E000F4C0008757064617465427971007E000F4C000A75706461746554696D6571007E0047787074000561646D696E7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000017DBE4ED8507870740009E7AEA1E79086E591987070707400362F70726F66696C652F6176617461722F323032332F31312F31332F626C6F625F3230323331313133313230343135413030312E706E67740001307372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E53797344657074000000000000000102000C4C0009616E636573746F727371007E000F4C00086368696C6472656E71007E00084C000764656C466C616771007E000F4C000664657074496471007E003D4C0008646570744E616D6571007E000F4C0005656D61696C71007E000F4C00066C656164657271007E000F4C00086F726465724E756D7400134C6A6176612F6C616E672F496E74656765723B4C0008706172656E74496471007E003D4C000A706172656E744E616D6571007E000F4C000570686F6E6571007E000F4C000673746174757371007E000F7871007E004970707070707070740009302C3130302C3130317371007E000B00000000770400000000787071007E004274000CE7A094E58F91E983A8E997A870740006E789A9E7BE8E737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0041000000017371007E0040000000000000006570707400013171007E00427400103136343737303730374071712E636F6D7371007E004C77080000018BF5F11F30787400093132372E302E302E3174000FE89C82E4BFA1E7AEA1E79086E5919874003C2432612431302451416F77377962733734666B53574A444A6B56544E656F6746376D686E69684637535445727437385078446848694E6E6F3449557574000B31353838383838383838387070707371007E000B000000017704000000017372002D636F6D2E666173746265652E636F6D6D6F6E2E636F72652E646F6D61696E2E656E746974792E537973526F6C65000000000000000102000D5A001164657074436865636B5374726963746C795A0004666C61675A00116D656E75436865636B5374726963746C794C00096461746153636F706571007E000F4C000764656C466C616771007E000F5B00076465707449647371007E00485B00076D656E7549647371007E00484C000B7065726D697373696F6E7371007E00114C0006726F6C65496471007E003D4C0007726F6C654B657971007E000F4C0008726F6C654E616D6571007E000F4C0008726F6C65536F727471007E00524C000673746174757371007E000F7871007E00497070707070707000000074000131707070707371007E0040000000000000000174000561646D696E74000FE8B685E7BAA7E7AEA1E79086E5919871007E00597400013078740001307400013071007E006674000561646D696E71007E0066);

-- ----------------------------
-- Table structure for oauth_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`  (
  `token_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication` blob NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件存储配置表' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件记录表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日历信息表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '已触发的触发器表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务详细信息表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '暂停的触发器表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '调度器状态表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = DYNAMIC;


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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '触发器详细信息表' ROW_FORMAT = DYNAMIC;

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
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'sip系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sip_config
-- ----------------------------
INSERT INTO `sip_config` VALUES (38, 1, 'admin', 117, '', 1, 1, NULL, '3402000000', '34020000002000000001', '12345678', '177.7.0.13', 5061, '0', '', '2023-03-16 21:26:18', '', '2023-03-16 21:26:24', NULL);
INSERT INTO `sip_config` VALUES (39, 1, 'admin', 118, '', 1, 1, NULL, '3402000000', '34020000002000000001', '12345678', '177.7.0.13', 5061, '0', '', '2023-04-11 21:11:54', '', NULL, NULL);
INSERT INTO `sip_config` VALUES (41, 1, 'admin', 135, '', 1, 1, NULL, '3402000000', '34020000002000000001', '12345678', '177.7.0.13', 5061, '0', '', '2024-01-08 22:14:35', '', NULL, NULL);

-- ----------------------------
-- Table structure for sip_device
-- ----------------------------
DROP TABLE IF EXISTS `sip_device`;
CREATE TABLE `sip_device`  (
  `device_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
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
  `registerTime` datetime NOT NULL COMMENT '注册时间',
  `lastConnectTime` datetime NULL DEFAULT NULL COMMENT '最后上线时间',
  `active_time` datetime NULL DEFAULT NULL COMMENT '激活时间',
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备入网IP',
  `port` bigint(10) NULL DEFAULT NULL COMMENT '设备接入端口号',
  `hostAddress` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备地址',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sip_device
-- ----------------------------
INSERT INTO `sip_device` VALUES (9, 0, '', '13030300001170000008', '8E085C3RAJE156F', 'Dahua', 'DH-3H3205-ADW', '2.810.0000027.0.R,2022-08-26', 'UDP', 'UDP', '', '2023-02-27 12:07:35', '2023-02-26 23:36:45', NULL, '177.7.0.1', 35332, '177.7.0.1:35332', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (12, 0, '', '11010100001320000001', '海康威视摄像头', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2024-01-09 23:29:52', '2024-01-09 23:35:00', NULL, '192.168.2.119', 5065, '192.168.2.119:5065', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (13, 0, '', '11010200001320000017', '', '', '', '', 'UDP', 'UDP', '', '2023-03-16 21:41:45', '2023-03-16 21:52:50', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (16, 0, '', '12010100001320000003', 'IP DOME', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2023-04-11 21:08:07', '2023-04-11 21:13:16', NULL, '192.168.2.119', 5060, '192.168.2.119:5060', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (18, 0, '', '13030100001320000001', '', 'ABCD', 'TEST001', 'V1.0', 'UDP', 'UDP', '', '2023-03-28 16:06:45', '2023-03-28 16:09:52', NULL, '192.168.205.250', 5063, '192.168.205.250:5063', '0', '', NULL, '', NULL, NULL);
INSERT INTO `sip_device` VALUES (19, 0, '', '11010200001320000001', '海康威视摄像头', 'Hikvision', 'iDS-2DE2402IX-D3/W/XM', 'V5.7.4', 'UDP', 'UDP', '', '2024-01-08 22:08:27', '2024-01-08 22:16:32', NULL, '192.168.2.119', 5065, '192.168.2.119:5065', '0', '', NULL, '', NULL, NULL);

-- ----------------------------
-- Table structure for sip_device_channel
-- ----------------------------
DROP TABLE IF EXISTS `sip_device_channel`;
CREATE TABLE `sip_device_channel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
  `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
  `device_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备SipID',
  `channel_sip_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '通道SipID',
  `channel_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道名称',
  `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
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
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`, `device_sip_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备通道信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sip_device_channel
-- ----------------------------
INSERT INTO `sip_device_channel` VALUES (84, 1, 'admin', 118, '监控设备', 1, 'admin', '11010200001320000001', '11010200001320000001', 'IPdome', '2024-01-08 22:16:32', '132', '132', '北京市/市辖区/西城区', '3402000000', 'Hikvision', 'IP Camera', 'Owner', '', 'Address', '34020000002000000001', '', 0, '', 0, '', 3, 0.000000, 0.000000, 'gb_play_11010200001320000001_11010200001320000001', 0, 0, 0, '0', '', '2023-04-11 21:12:33', '', NULL, NULL);
INSERT INTO `sip_device_channel` VALUES (102, 1, 'admin', 135, '视频监控', 0, '', '11010100001320000001', '11010100001320000001', 'IPdome', '2024-01-09 23:35:00', '132', '132', '北京市/市辖区/东城区', '3402000000', 'Hikvision', 'IP Camera', 'Owner', '', 'Address', '34020000002000000001', '', 0, '', 0, '', 3, 0.000000, 0.000000, '', 0, 0, 0, '0', '', '2024-01-08 22:15:57', '', NULL, NULL);

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`auth_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方授权表' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 596 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别男'),
       (2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别女'),
       (3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '性别未知'),
       (1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '显示菜单'),
       (2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '隐藏菜单'),
       (1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态'),
       (2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态'),
       (1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态'),
       (2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态'),
       (1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '默认分组'),
       (2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统分组'),
       (1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统默认是'),
       (2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统默认否'),
       (1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知'),
       (2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '公告'),
       (1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态'),
       (2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '关闭状态'),
       (1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '新增操作'),
       (2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '修改操作'),
       (3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '删除操作'),
       (4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '授权操作'),
       (5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '导出操作'),
       (6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '导入操作'),
       (7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '强退操作'),
       (8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '生成操作'),
       (9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '清空操作'),
       (1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '正常状态'),
       (2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '停用状态'),
       (1, '属性', '1', 'iot_things_type', '', 'primary', 'Y', '0', 'admin', '2021-12-12 16:41:15', 'admin', '2021-12-15 22:49:37', ''),
       (2, '功能', '2', 'iot_things_type', '', 'success', 'Y', '0', 'admin', '2021-12-12 16:43:33', 'admin', '2021-12-14 16:33:11', ''),
       (3, '事件', '3', 'iot_things_type', NULL, 'warning', 'Y', '0', 'admin', '2021-12-12 16:46:04', '', NULL, NULL),
       (1, '整数', 'integer', 'iot_data_type', '', '', 'Y', '0', 'admin', '2021-12-12 20:20:46', 'admin', '2021-12-14 16:09:56', ''),
       (2, '小数', 'decimal', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:21:21', 'admin', '2021-12-15 22:51:07', NULL),
       (3, '布尔', 'bool', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:12', 'admin', '2021-12-15 22:51:02', NULL),
       (4, '枚举', 'enum', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:37', 'admin', '2021-12-15 22:50:57', NULL),
       (5, '字符串', 'string', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-12 20:22:54', 'admin', '2021-12-15 22:50:52', NULL),
       (1, '是', '1', 'iot_yes_no', '', 'success', 'Y', '0', 'admin', '2021-12-12 20:25:14', 'admin', '2023-12-15 16:40:49', ''),
       (2, '否', '0', 'iot_yes_no', '', 'danger', 'Y', '0', 'admin', '2021-12-12 20:25:25', 'admin', '2023-12-15 16:41:01', ''),
       (6, '数组', 'array', 'iot_data_type', NULL, 'default', 'Y', '0', 'admin', '2021-12-13 18:18:04', 'admin', '2021-12-15 22:50:42', NULL),
       (1, '未发布', '1', 'iot_product_status', NULL, 'info', 'N', '0', 'admin', '2021-12-19 15:01:18', 'admin', '2021-12-19 15:01:55', NULL),
       (2, '已发布', '2', 'iot_product_status', NULL, 'success', 'N', '0', 'admin', '2021-12-19 15:01:43', '', NULL, NULL),
       (1, '直连设备', '1', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:03:49', 'admin', '2021-12-19 15:10:13', NULL),
       (2, '网关设备', '2', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:04:28', 'admin', '2023-02-09 16:25:46', NULL),
       (1, 'WIFI', '1', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:07:35', 'admin', '2021-12-22 00:11:19', NULL),
       (2, '蜂窝(2G/3G/4G/5G)', '2', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:08:30', 'admin', '2022-01-14 02:12:27', NULL),
       (3, '以太网', '3', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:09:08', 'admin', '2022-01-14 02:12:39', NULL),
       (1, '简单认证', '1', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:13:16', 'admin', '2022-06-05 00:14:48', NULL),
       (2, '加密认证', '2', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2021-12-19 15:13:26', 'admin', '2022-06-05 00:14:57', NULL),
       (1, 'ESP8266/Arduino', '1', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:54:52', 'admin', '2021-12-24 16:07:31', NULL),
       (3, 'ESP32/Arduino', '2', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:55:04', 'admin', '2021-12-24 16:07:26', NULL),
       (2, 'ESP8266/RTOS', '3', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 15:56:08', 'admin', '2021-12-24 16:07:17', NULL),
       (4, 'ESP32/ESP-IDF', '4', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 16:07:54', '', NULL, NULL),
       (5, '树莓派/Python', '5', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2021-12-24 16:08:31', '', NULL, NULL),
       (0, '未激活', '1', 'iot_device_status', NULL, 'warning', 'N', '0', 'admin', '2021-12-27 22:21:04', 'admin', '2021-12-27 22:22:09', NULL),
       (0, '禁用', '2', 'iot_device_status', NULL, 'danger', 'N', '0', 'admin', '2021-12-27 22:21:22', '', NULL, NULL),
       (0, '在线', '3', 'iot_device_status', NULL, 'success', 'N', '0', 'admin', '2021-12-27 22:21:42', '', NULL, NULL),
       (0, '离线', '4', 'iot_device_status', NULL, 'info', 'N', '0', 'admin', '2021-12-27 22:22:01', '', NULL, NULL),
       (0, '启用', '1', 'iot_is_enable', NULL, 'success', 'N', '0', 'admin', '2022-01-12 23:25:08', 'admin', '2022-01-12 23:25:30', NULL),
       (0, '禁用', '0', 'iot_is_enable', NULL, 'info', 'N', '0', 'admin', '2022-01-12 23:25:19', 'admin', '2022-01-12 23:25:38', NULL),
       (0, '提醒通知', '1', 'iot_alert_level', NULL, 'success', 'N', '0', 'admin', '2022-01-13 14:58:10', 'admin', '2022-01-13 14:58:31', NULL),
       (0, '轻微问题', '2', 'iot_alert_level', NULL, 'warning', 'N', '0', 'admin', '2022-01-13 14:59:00', '', NULL, NULL),
       (0, '严重警告', '3', 'iot_alert_level', NULL, 'danger', 'N', '0', 'admin', '2022-01-13 14:59:16', '', NULL, NULL),
       (0, '不需要处理', '1', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:03', '', NULL, NULL),
       (0, '未处理', '2', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:14', '', NULL, NULL),
       (0, '已处理', '3', 'iot_process_status', NULL, 'default', 'N', '0', 'admin', '2022-01-13 15:06:24', '', NULL, NULL),
       (1, '属性上报', '1', 'iot_device_log_type', NULL, 'primary', 'N', '0', 'admin', '2022-01-13 15:10:32', 'admin', '2022-03-13 00:20:25', NULL),
       (3, '事件上报', '3', 'iot_device_log_type', NULL, 'danger', 'N', '0', 'admin', '2022-01-13 15:10:43', 'admin', '2022-03-13 00:21:00', NULL),
       (2, '功能调用', '2', 'iot_device_log_type', NULL, 'warning', 'N', '0', 'admin', '2022-01-13 15:10:55', 'admin', '2022-03-13 00:20:32', NULL),
       (4, '设备升级', '4', 'iot_device_log_type', NULL, 'success', 'N', '0', 'admin', '2022-01-13 15:11:08', 'admin', '2022-03-13 00:21:06', NULL),
       (5, '设备上线', '5', 'iot_device_log_type', NULL, 'success', 'N', '0', 'admin', '2022-01-13 15:11:23', 'admin', '2022-03-13 00:21:26', NULL),
       (6, '设备离线', '6', 'iot_device_log_type', NULL, 'info', 'N', '0', 'admin', '2022-01-13 15:11:32', 'admin', '2022-03-13 00:21:13', NULL),
       (4, '其他', '4', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2022-01-14 02:12:49', 'admin', '2022-01-14 02:13:03', NULL),
       (6, '安卓/Android', '6', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2022-01-16 12:39:27', '', NULL, NULL),
       (7, '其他', '7', 'iot_device_chip', NULL, 'default', 'N', '0', 'admin', '2022-01-16 12:39:55', 'admin', '2022-01-16 12:40:13', NULL),
       (1, '小度平台', '1', 'oauth_platform', NULL, 'primary', 'N', '0', 'admin', '2022-02-07 20:29:23', 'admin', '2022-02-07 22:24:28', NULL),
       (2, '天猫精灵', '2', 'oauth_platform', NULL, 'danger', 'N', '0', 'admin', '2022-02-07 20:29:41', 'admin', '2022-02-07 22:23:14', NULL),
       (3, '小米小爱', '3', 'oauth_platform', NULL, 'success', 'N', '0', 'admin', '2022-02-07 20:30:07', 'admin', '2022-02-07 22:23:24', NULL),
       (4, '其他平台', '4', 'oauth_platform', NULL, 'warning', 'N', '0', 'admin', '2022-02-07 22:23:52', 'admin', '2022-02-07 22:24:02', NULL),
       (1, '微信登录', 'WECHAT', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-04-20 16:41:33', 'admin', '2023-09-22 10:27:54', NULL),
       (2, 'QQ登录', 'QQ', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-04-20 16:42:46', 'admin', '2023-09-22 10:28:03', NULL),
       (0, '启用', '0', 'iot_social_platform_status', NULL, 'success', 'N', '0', 'admin', '2022-04-20 17:02:48', 'admin', '2022-05-12 17:39:40', '启用'),
       (1, '未启用', '1', 'iot_social_platform_status', NULL, 'info', 'N', '0', 'admin', '2022-04-20 17:03:15', 'admin', '2022-05-21 13:44:13', '禁用'),
       (3, '支付宝', 'ALIPAY', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2022-05-12 17:49:24', 'admin', '2022-05-12 17:50:21', NULL),
       (1, '自动定位', '1', 'iot_location_way', NULL, 'success', 'N', '0', 'admin', '2022-05-21 13:46:51', 'admin', '2022-05-21 13:53:23', 'IP定位，精确到城市'),
       (2, '设备定位', '2', 'iot_location_way', NULL, 'warning', 'N', '0', 'admin', '2022-05-21 13:46:51', 'admin', '2022-05-21 13:49:21', '最精确定位'),
       (3, '自定义位置', '3', 'iot_location_way', NULL, 'primary', 'N', '0', 'admin', '2022-05-21 13:48:50', 'admin', '2022-05-21 13:55:45', '位置自定义'),
       (3, '简单+加密', '3', 'iot_vertificate_method', NULL, 'default', 'N', '0', 'admin', '2022-06-05 00:15:46', '', NULL, NULL),
       (1, '未使用', '1', 'iot_auth_status', NULL, 'info', 'N', '0', 'admin', '2022-06-07 17:39:22', 'admin', '2022-06-07 17:40:10', NULL),
       (2, '已使用', '2', 'iot_auth_status', NULL, 'success', 'N', '0', 'admin', '2022-06-07 17:40:01', 'admin', '2022-06-07 23:21:49', NULL),
       (7, '对象', 'object', 'iot_data_type', NULL, 'default', 'N', '0', 'admin', '2023-02-09 16:20:57', 'admin', '2023-02-09 16:21:08', NULL),
       (3, '监控设备', '3', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2023-02-09 16:26:00', '', NULL, NULL),
       (1, '未使用', '1', 'sip_gen_status', NULL, 'info', 'N', '0', 'admin', '2023-02-19 15:49:04', 'admin', '2023-02-19 15:50:03', NULL),
       (2, '在线', '2', 'sip_gen_status', NULL, 'success', 'N', '0', 'admin', '2023-02-19 15:49:24', 'admin', '2023-02-24 21:36:29', NULL),
       (0, 'DVR', '111', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:07:06', '', NULL, NULL),
       (1, 'NVR', '118', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:07:59', '', NULL, NULL),
       (2, '报警控制器', '117', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:08:13', 'admin', '2023-02-22 01:08:35', NULL),
       (4, '摄像机', '131', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:08:52', '', NULL, NULL),
       (5, 'IPC', '132', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:11', '', NULL, NULL),
       (6, '显示器', '133', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:30', '', NULL, NULL),
       (7, '报警输入设备', '134', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:09:49', '', NULL, NULL),
       (8, '报警输出设备', '135', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:08', '', NULL, NULL),
       (9, '语音输入设备', '136', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:29', '', NULL, NULL),
       (10, '语音输出设备', '137', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:10:46', 'admin', '2023-02-22 01:10:51', NULL),
       (11, '移动传输设备', '138', 'video_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:11:09', '', NULL, NULL),
       (0, '报警控制器', '117', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:09', '', NULL, NULL),
       (1, '摄像机', '131', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:24', '', NULL, NULL),
       (2, 'IPC', '132', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:39', '', NULL, NULL),
       (3, '显示器', '133', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:12:57', '', NULL, NULL),
       (5, '报警输入设备', '134', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:13:14', '', NULL, NULL),
       (6, '报警输出设备', '135', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:13:29', 'admin', '2023-02-22 01:13:49', NULL),
       (7, '语音输入设备', '136', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:14:14', 'admin', '2023-02-22 01:14:24', NULL),
       (8, '语音输出设备', '137', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:14:50', '', NULL, NULL),
       (9, '移动传输设备', '138', 'channel_type', NULL, 'default', 'N', '0', 'admin', '2023-02-22 01:15:11', '', NULL, NULL),
       (3, '离线', '3', 'sip_gen_status', NULL, 'warning', 'N', '0', 'admin', '2023-02-24 21:36:53', 'admin', '2023-02-24 21:37:11', NULL),
       (4, '禁用', '4', 'sip_gen_status', NULL, 'danger', 'N', '0', 'admin', '2023-02-24 21:37:39', '', NULL, NULL),
       (0, '云端轮询', '0', 'data_collect_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 13:56:16', '', NULL, NULL),
       (0, '边缘采集', '1', 'data_collect_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 13:56:28', '', NULL, NULL),
       (0, '1分钟', '60', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:07', '', NULL, NULL),
       (0, '2分钟', '120', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:20', '', NULL, NULL),
       (0, '5分钟', '300', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2023-02-28 14:39:29', 'admin', '2023-02-28 14:39:35', NULL),
       (1, '03(读保持寄存器)', '3', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-02-28 15:19:46', 'admin', '2023-12-21 14:33:17', NULL),
       (0, '01(读线圈)', '1', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-02-28 15:20:06', '', NULL, NULL),
       (0, 'MQTT', 'MQTT', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:35:40', '', NULL, NULL),
       (1, 'TCP', 'TCP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:35:51', '', NULL, NULL),
       (2, 'COAP', 'COAP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:36:00', 'admin', '2023-02-28 16:36:22', NULL),
       (3, 'UDP', 'UDP', 'iot_transport_type', NULL, 'default', 'N', '0', 'admin', '2023-02-28 16:36:15', '', NULL, NULL),
       (99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-03-10 23:28:32', '', NULL, '其他操作'),
       (0, '事件上报', '3', 'iot_event_type', NULL, 'danger', 'N', '0', 'admin', '2023-03-29 00:25:28', '', NULL, NULL),
       (0, '设备上线', '5', 'iot_event_type', NULL, 'success', 'N', '0', 'admin', '2023-03-29 00:25:52', '', NULL, NULL),
       (0, '设备离线', '6', 'iot_event_type', NULL, 'info', 'N', '0', 'admin', '2023-03-29 00:26:09', '', NULL, NULL),
       (0, '服务下发', '1', 'iot_function_type', NULL, 'primary', 'N', '0', 'admin', '2023-03-29 00:38:26', '', NULL, NULL),
       (0, '属性获取', '2', 'iot_function_type', NULL, 'success', 'N', '0', 'admin', '2023-03-29 00:38:44', '', NULL, NULL),
       (0, 'OTA升级', '3', 'iot_function_type', NULL, 'warning', 'N', '0', 'admin', '2023-03-29 00:39:08', '', NULL, NULL),
       (0, '读写', '0', 'iot_data_read_write', NULL, 'primary', 'N', '0', 'admin', '2023-04-09 02:12:05', '', NULL, NULL),
       (0, '只读', '1', 'iot_data_read_write', NULL, 'info', 'N', '0', 'admin', '2023-04-09 02:12:19', '', NULL, NULL),
       (0, '全部设备', '1', 'oat_update_limit', NULL, 'default', 'N', '0', 'admin', '2023-04-09 23:57:06', 'admin', '2023-04-11 11:53:57', NULL),
       (1, '指定设备', '2', 'oat_update_limit', NULL, 'default', 'N', '0', 'admin', '2023-04-11 11:53:28', 'admin', '2023-04-11 11:53:52', NULL),
       (4, 'GB28181', 'GB28181', 'iot_transport_type', NULL, 'primary', 'N', '0', 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL),
       (1, '02(读离散量输入)', '2', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-07-03 10:16:48', 'admin', '2023-07-03 10:17:35', NULL),
       (3, '04(读输入寄存器)', '4', 'iot_modbus_status_code', NULL, 'default', 'N', '0', 'admin', '2023-07-03 10:17:18', 'admin', '2023-07-03 10:17:58', NULL),
       (4, '微信开放平台网站应用', 'wechat_open_web', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2023-08-23 11:28:15', '', NULL, NULL),
       (5, '微信开放平台移动应用', 'wechat_open_mobile', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2023-08-23 11:29:14', '', NULL, NULL),
       (6, '微信开放平台小程序', 'wechat_open_mini_program', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2023-08-23 11:38:12', '', NULL, NULL),
       (7, '微信开放平台网站应用个人中心绑定', 'wechat_open_web_bind', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2023-10-09 11:28:15', '', NULL, NULL),
       (0, '16位 无符号', 'ushort', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:11:54', '', NULL, NULL),
       (1, '16位 有符号', 'short', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:12:26', '', NULL, NULL),
       (2, '32位 有符号(ABCD)', 'long-ABCD', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:12:53', '', NULL, NULL),
       (3, '32位 有符号(CDAB)', 'long-CDAB', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:13:21', '', NULL, NULL),
       (4, '32位 无符号(ABCD)', 'ulong-ABCD', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:13:42', '', NULL, NULL),
       (5, '32位 无符号(CDAB)', 'ulong-CDAB', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:14:06', '', NULL, NULL),
       (6, '32位 浮点数(ABCD)', 'float-ABCD', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:14:28', '', NULL, NULL),
       (7, '32位 浮点数(CDAB)', 'float-CDAB', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:14:50', '', NULL, NULL),
       (8, '位', 'bit', 'iot_modbus_data_type', NULL, 'default', 'N', '0', 'admin', '2023-09-04 14:15:13', '', NULL, NULL),
       (1, 'Java脚本引擎', 'java', 'rule_script_language', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:51:09', 'admin', '2023-11-04 01:51:42', NULL),
       (2, 'JavaScript脚本引擎', 'js', 'rule_script_language', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:51:36', 'admin', '2023-11-05 10:28:30', NULL),
       (1, '普通脚本节点', 'script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:53:11', '', NULL, NULL),
       (2, '选择脚本节点 (switch)', 'switch_script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:53:32', 'admin', '2023-11-04 01:56:44', NULL),
       (3, '条件脚本节点 (if)', 'if_script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:53:48', 'admin', '2023-11-04 01:56:55', NULL),
       (4, '数量循环节点 (for)', 'for_script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:54:11', 'admin', '2023-11-04 01:57:07', NULL),
       (5, '条件循环节点 (while)', 'while_script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:54:30', 'admin', '2023-11-04 01:57:16', NULL),
       (6, '退出循环节点 (break)', 'break_script', 'rule_script_type', NULL, 'default', 'N', '0', 'admin', '2023-11-04 01:54:50', 'admin', '2023-11-04 01:57:29', NULL),
       (3, 'groovy脚本引擎', 'groovy', 'rule_script_language', NULL, 'default', 'N', '0', 'admin', '2023-11-05 10:29:14', '', NULL, NULL),
       (1, '短信', 'sms', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-12-21 10:31:54', NULL),
       (2, '微信', 'wechat', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:24:15', NULL),
       (5, '钉钉', 'dingtalk', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-10 15:28:06', NULL),
       (3, '语音', 'voice', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:24:22', NULL),
       (1, '阿里云', 'alibaba', 'notify_channel_sms_provider', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:49:10', ''),
       (1, 'QQ', 'qq', 'notify_channel_email_provider', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-12-22 10:33:48', '{\"host\":\"\",\"port\":\"\",\"sender\":\"\",\"password\":\"\"}'),
       (4, '邮箱', 'email', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-12-11 23:43:14', 'admin', '2024-01-05 16:24:32', NULL),
       (1, '设备告警', 'alert', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2023-12-18 14:58:40', 'admin', '2024-01-25 14:35:25', NULL),
       (2, '验证码', 'captcha', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2023-12-18 14:59:59', 'admin', '2024-01-25 14:36:07', NULL),
       (1, '设备上报', '1', 'rule_script_event', NULL, 'primary', 'N', '0', 'admin', '2023-12-19 11:40:34', 'admin', '2023-12-20 02:23:43', NULL),
       (2, '平台下发', '2', 'rule_script_event', NULL, 'warning', 'N', '0', 'admin', '2023-12-19 11:40:46', 'admin', '2023-12-20 02:23:51', NULL),
       (3, '设备上线', '3', 'rule_script_event', NULL, 'success', 'N', '0', 'admin', '2023-12-19 11:40:58', 'admin', '2023-12-20 02:24:00', NULL),
       (4, '设备离线', '4', 'rule_script_event', NULL, 'info', 'N', '0', 'admin', '2023-12-19 11:41:09', 'admin', '2023-12-20 02:24:08', NULL),
       (1, '数据流', '1', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:39', '', NULL, NULL),
       (2, '触发器', '2', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:48', '', NULL, NULL),
       (3, '执行动作', '3', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:59', '', NULL, NULL),
       (1, '消息重发', '1', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:42:26', '', NULL, NULL),
       (2, '消息通知', '2', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:18', 'admin', '2023-12-19 11:43:38', NULL),
       (3, 'Http推送', '3', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:33', '', NULL, NULL),
       (4, 'Mqtt桥接', '4', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:54', '', NULL, NULL),
       (5, '数据库存储', '5', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:44:08', 'admin', '2023-12-20 17:08:35', NULL),
       (2, '腾讯云', 'tencent', 'notify_channel_sms_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-21 10:11:25', 'admin', '2024-01-10 14:34:30', ''),
       (3, '天翼云', 'ctyun', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:11:51', 'admin', '2024-01-05 16:50:31', ''),
       (3, '华为云', 'huawei', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:07', 'admin', '2024-01-05 16:50:35', NULL),
       (3, '云片', 'yunpian', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:29', 'admin', '2024-01-05 16:50:39', NULL),
       (3, '亿美软通', 'emay', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:56', 'admin', '2024-01-05 16:49:52', NULL),
       (3, '容连云', 'cloopen', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:16', 'admin', '2024-01-05 16:49:58', NULL),
       (3, '京东云', 'jdcloud', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:37', 'admin', '2024-01-05 16:50:07', NULL),
       (3, '网易云', 'netease', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:57', 'admin', '2024-01-05 16:50:13', NULL),
       (1, '微信小程序（订阅消息）', 'mini_program', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-21 10:41:04', 'admin', '2024-01-22 15:49:10', ''),
       (1, '163', '163', 'notify_channel_email_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-21 10:41:52', 'admin', '2023-12-22 10:33:58', '{\"host\":\"\",\"port\":\"\",\"sender\":\"\",\"password\":\"\"}'),
       (1, '阿里云', 'alibaba', 'notify_channel_voice_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-27 14:58:54', 'admin', '2024-01-11 09:59:44', ''),
       (1, '工作通知', 'work', 'notify_channel_dingtalk_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-10 15:32:15', 'admin', '2024-02-02 17:19:11', NULL),
       (2, '群机器人', 'group_robot', 'notify_channel_dingtalk_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-10 15:32:57', 'admin', '2024-01-22 15:47:06', NULL),
       (3, '营销通知', 'marketing', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2024-01-11 09:56:07', 'admin', '2024-01-25 14:37:40', NULL),
       (2, '腾讯云', 'tencent', 'notify_channel_voice_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-11 09:59:33', '', NULL, NULL),
       (1, '文本', 'text', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:42:51', '', NULL, NULL),
       (2, 'markdown类型', 'markdown', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:43:44', '', NULL, NULL),
       (2, '链接消息', 'link', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:44:04', '', NULL, NULL),
       (2, '企业微信应用消息', 'wecom_apply', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:40:11', 'admin', '2024-01-22 17:06:03', NULL),
       (3, '企业微信群机器人', 'wecom_robot', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:40:33', 'admin', '2024-01-22 15:40:38', NULL),
       (1, '文本', 'text', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:51:48', '', NULL, NULL),
       (2, 'markdown', 'markdown', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:52:04', '', NULL, NULL),
       (4, '图文', 'news', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:52:29', '', NULL, NULL),
       (8, '微信开放平台公众号', 'wechat_open_public_account', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2024-03-08 17:56:56', '', NULL, '感谢您关注蜂信物联！'),
       (4, '微信公众号', 'public_account', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-03-09 11:11:57', '', NULL, NULL);

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 157 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('用户性别', 'sys_user_sex', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '用户性别列表'),
       ('菜单状态', 'sys_show_hide', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '菜单状态列表'),
       ('系统开关', 'sys_normal_disable', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统开关列表'),
       ('任务状态', 'sys_job_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '任务状态列表'),
       ('任务分组', 'sys_job_group', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '任务分组列表'),
       ('系统是否', 'sys_yes_no', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '系统是否列表'),
       ('通知类型', 'sys_notice_type', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知类型列表'),
       ('通知状态', 'sys_notice_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '通知状态列表'),
       ('操作类型', 'sys_oper_type', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '操作类型列表'),
       ('系统状态', 'sys_common_status', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '登录状态列表'),
       ('物模型类别', 'iot_things_type', '0', 'admin', '2021-12-12 16:39:47', 'admin', '2021-12-15 22:49:19', '属性、动作、事件'),
       ('数据类型', 'iot_data_type', '0', 'admin', '2021-12-12 20:16:48', 'admin', '2021-12-12 20:17:54', 'integer、decimal、bool、string、enum'),
       ('是否', 'iot_yes_no', '0', 'admin', '2021-12-12 20:24:51', 'admin', '2021-12-19 15:12:35', '是、否'),
       ('产品状态', 'iot_product_status', '0', 'admin', '2021-12-19 15:00:13', '', NULL, '未发布、已发布（不能修改）'),
       ('设备类型', 'iot_device_type', '0', 'admin', '2021-12-19 15:03:06', '', NULL, '直连设备、网关子设备、网关设备'),
       ('联网方式', 'iot_network_method', '0', 'admin', '2021-12-19 15:07:12', 'admin', '2022-01-14 02:11:58', 'wifi、蜂窝(2G/3G/4G/5G)、以太网、其他'),
       ('认证方式', 'iot_vertificate_method', '0', 'admin', '2021-12-19 15:11:48', 'admin', '2022-06-05 12:57:02', '1=简单认证、2=加密认证、3=简单+加密'),
       ('设备芯片', 'iot_device_chip', '0', 'admin', '2021-12-24 15:53:27', 'admin', '2022-01-22 00:14:23', 'ESP8266、ESP32、树莓派'),
       ('设备状态', 'iot_device_status', '0', 'admin', '2021-12-27 22:19:55', 'admin', '2021-12-27 22:20:13', '未激活、禁用、在线、离线'),
       ('是否启用', 'iot_is_enable', '0', 'admin', '2022-01-12 23:24:01', 'admin', '2022-01-12 23:24:15', '启用、禁用'),
       ('告警类型', 'iot_alert_level', '0', 'admin', '2022-01-13 14:56:44', 'admin', '2022-01-13 15:04:46', '1=提醒通知，2=轻微问题，3=严重警告'),
       ('处理状态', 'iot_process_status', '0', 'admin', '2022-01-13 15:04:06', 'admin', '2022-01-13 15:06:39', '1=不需要处理,2=未处理,3=已处理'),
       ('设备日志类型', 'iot_device_log_type', '0', 'admin', '2022-01-13 15:09:49', 'admin', '2022-03-13 00:22:43', '1=属性上报，2=调用功能,3=事件上报，4=设备升级，5=设备上线，6=设备离线'),
       ('Oauth开放平台', 'oauth_platform', '0', 'admin', '2022-02-07 20:27:48', 'admin', '2022-05-21 13:44:50', '1=小度，2=天猫精灵，3=小爱，4=其他'),
       ('第三方登录平台', 'iot_social_platform', '0', 'admin', '2022-04-12 15:28:13', 'admin', '2022-04-12 15:37:48', 'Wechat、QQ、'),
       ('第三方登录平台状态', 'iot_social_platform_status', '0', 'admin', '2022-04-20 17:02:13', 'admin', '2022-04-20 17:02:23', '第三方登录平台状态'),
       ('设备定位方式', 'iot_location_way', '0', 'admin', '2022-05-21 13:45:16', 'admin', '2022-05-21 13:46:06', '1=IP自动定位，2=设备定位，3=自定义'),
       ('授权码状态', 'iot_auth_status', '0', 'admin', '2022-06-07 17:38:56', '', NULL, '1=未分配，2=使用中'),
       ('SipID状态', 'sip_gen_status', '0', 'admin', '2023-02-19 15:43:36', 'admin', '2023-02-19 15:45:54', '1=未使用，2=使用中'),
       ('监控设备类型', 'video_type', '0', 'admin', '2023-02-22 01:06:38', '', NULL, NULL),
       ('通道类型', 'channel_type', '0', 'admin', '2023-02-22 01:11:51', '', NULL, NULL),
       ('轮询方式', 'data_collect_type', '0', 'admin', '2023-02-28 13:55:45', '', NULL, NULL),
       ('批量采集时间', 'iot_modbus_poll_time', '0', 'admin', '2023-02-28 14:38:21', '', NULL, NULL),
       ('寄存器功能码', 'iot_modbus_status_code', '0', 'admin', '2023-02-28 15:19:02', '', NULL, NULL),
       ('传输协议类型', 'iot_transport_type', '0', 'admin', '2023-02-28 16:35:20', '', NULL, NULL),
       ('设备事件类型', 'iot_event_type', '0', 'admin', '2023-03-29 00:24:51', '', NULL, NULL),
       ('指令下发类型', 'iot_function_type', '0', 'admin', '2023-03-29 00:37:51', '', NULL, NULL),
       ('读写类型', 'iot_data_read_write', '0', 'admin', '2023-04-09 02:11:14', '', NULL, NULL),
       ('升级范围', 'oat_update_limit', '0', 'admin', '2023-04-09 23:51:45', '', NULL, NULL),
       ('云存储平台类型', 'oss_platform_type', '0', 'admin', '2023-04-12 00:26:09', '', NULL, NULL),
       ('modbus数据类型', 'iot_modbus_data_type', '0', 'admin', '2023-09-04 13:54:17', '', NULL, NULL),
       ('规则脚本类型', 'rule_script_type', '0', 'admin', '2023-11-04 01:48:50', 'admin', '2023-11-04 01:50:16', NULL),
       ('规则脚本语言', 'rule_script_language', '0', 'admin', '2023-11-04 01:50:06', '', NULL, NULL),
       ('通知渠道类型', 'notify_channel_type', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '通知渠道'),
       ('通知短信服务商', 'notify_channel_sms_provider', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '短信服务商'),
       ('通知邮箱服务商', 'notify_channel_email_provider', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '邮箱服务商'),
       ('通知业务编码', 'notify_service_code', '0', 'admin', '2023-12-18 14:56:57', '', NULL, NULL),
       ('规则脚本事件', 'rule_script_event', '0', 'admin', '2023-12-19 11:33:48', '', NULL, '1=设备上报，2=平台下发，3=设备上线，4=设备离线'),
       ('规则脚本用途', 'rule_script_purpose', '0', 'admin', '2023-12-19 11:38:18', '', NULL, '1=数据流，2=触发器，3=执行动作'),
       ('规则脚本动作', 'rule_script_action', '0', 'admin', '2023-12-19 11:39:58', '', NULL, '1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储'),
       ('通知微信服务商', 'notify_channel_wechat_provider', '0', 'admin', '2023-12-21 10:37:25', '', NULL, NULL),
       ('通知语音服务商', 'notify_channel_voice_provider', '0', 'admin', '2023-12-27 14:58:23', '', NULL, NULL),
       ('通知钉钉服务商', 'notify_channel_dingtalk_provider', '0', 'admin', '2024-01-10 15:27:28', '', NULL, NULL),
       ('通知钉钉消息类型', 'dingtalk_msg_type', '0', 'admin', '2024-01-22 11:41:26', 'admin', '2024-01-22 11:44:20', NULL),
       ('通知企业微信消息类型', 'wecom_msg_type', '0', 'admin', '2024-01-22 11:49:03', 'admin', '2024-01-22 11:51:27', NULL);

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2021-12-15 21:36:18', '', NULL, '');
INSERT INTO `sys_job` VALUES (4, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', '0 0/1 * * * ? ', '1', '0', '0', 'admin', '2023-02-28 17:28:03', 'admin', '2023-04-08 22:07:42', '');
INSERT INTO `sys_job` VALUES (5, '设备定时同步', 'SYSTEM', 'syncDeviceStatusJob.syncDeviceStatus', '0 0/3 * * * ? ', '1', '1', '0', 'admin', '2023-03-24 10:57:48', 'admin', '2024-04-11 11:39:02', '');
INSERT INTO `sys_job` VALUES (6, '监控在线状态更新', 'SYSTEM', 'deviceJob.updateSipDeviceOnlineStatus(90)', '0 0/1 * * * ?', '1', '0', '1', 'admin', '2023-04-14 16:18:54', '', '2024-01-28 16:20:21', '');

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (1, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：3毫秒', '0', '', '2024-02-07 15:32:00');
INSERT INTO `sys_job_log` VALUES (2, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：44毫秒', '0', '', '2024-02-07 15:32:00');
INSERT INTO `sys_job_log` VALUES (3, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：32毫秒', '0', '', '2024-02-07 15:32:00');
INSERT INTO `sys_job_log` VALUES (4, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：15毫秒', '0', '', '2024-02-07 15:32:10');
INSERT INTO `sys_job_log` VALUES (5, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：3毫秒', '0', '', '2024-02-07 15:33:00');
INSERT INTO `sys_job_log` VALUES (6, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：44毫秒', '0', '', '2024-02-07 15:33:00');
INSERT INTO `sys_job_log` VALUES (7, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：32毫秒', '0', '', '2024-02-07 15:33:00');
INSERT INTO `sys_job_log` VALUES (8, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：14毫秒', '0', '', '2024-02-07 15:33:10');
INSERT INTO `sys_job_log` VALUES (9, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：3毫秒', '0', '', '2024-02-07 15:34:00');
INSERT INTO `sys_job_log` VALUES (10, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：44毫秒', '0', '', '2024-02-07 15:34:00');
INSERT INTO `sys_job_log` VALUES (11, 'modbus云端轮询', 'SYSTEM', 'propGetServiceImpl.fetchProperty', 'modbus云端轮询 总共耗时：31毫秒', '0', '', '2024-02-07 15:34:00');

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
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3179 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 5, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-12-15 21:36:18', 'admin', '2023-11-13 10:47:10', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 6, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-12-15 21:36:18', 'admin', '2023-11-13 10:47:17', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 7, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2021-12-15 21:36:18', 'admin', '2023-11-13 10:47:23', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '蜂信物联', 0, 20, 'http://fastbee.cn', NULL, '', 0, 0, 'M', '0', '0', '', 'guide', 'admin', '2021-12-15 21:36:18', 'admin', '2023-11-13 10:48:29', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-12-15 21:36:18', 'admin', '2023-12-07 11:11:06', '用户管理菜单');
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
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2021-12-15 21:36:18', 'admin', '2023-12-07 11:11:49', '');
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
INSERT INTO `sys_menu` VALUES (2000, '设备管理', 0, 0, 'iot', NULL, NULL, 1, 0, 'M', '0', '0', '', 'device', 'admin', '2021-12-15 23:57:06', 'admin', '2023-12-29 17:19:46', '');
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
INSERT INTO `sys_menu` VALUES (2067, '告警记录', 3051, 7, 'alertLog', 'iot/alert/log', NULL, 1, 0, 'C', '0', '0', 'iot:alert:list', 'log', 'admin', '2022-01-13 17:16:15', 'admin', '2024-02-01 02:36:27', '设备告警菜单');
INSERT INTO `sys_menu` VALUES (2068, '设备告警查询', 2067, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:query', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:10', '');
INSERT INTO `sys_menu` VALUES (2069, '设备告警新增', 2067, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:add', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:20', '');
INSERT INTO `sys_menu` VALUES (2070, '设备告警修改', 2067, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:edit', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:29', '');
INSERT INTO `sys_menu` VALUES (2071, '设备告警删除', 2067, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:remove', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:38', '');
INSERT INTO `sys_menu` VALUES (2072, '设备告警导出', 2067, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:alert:export', '#', 'admin', '2022-01-13 17:16:15', 'admin', '2022-06-11 00:38:46', '');
INSERT INTO `sys_menu` VALUES (2085, '场景联动', 3051, 5, 'scene', 'iot/scene/index', NULL, 1, 0, 'C', '0', '0', 'iot:scene:list', 'scene', 'admin', '2022-01-13 17:16:45', 'admin', '2023-11-12 10:53:07', '场景联动菜单');
INSERT INTO `sys_menu` VALUES (2086, '场景联动查询', 2085, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:query', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2087, '场景联动新增', 2085, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:add', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2088, '场景联动修改', 2085, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:edit', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2089, '场景联动删除', 2085, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:remove', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2090, '场景联动导出', 2085, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:scene:export', '#', 'admin', '2022-01-13 17:16:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2104, 'EMQ管理', 0, 11, 'http://81.71.97.58:18083/', NULL, NULL, 0, 0, 'M', '0', '0', '', 'mq', 'admin', '2022-02-26 00:42:12', 'admin', '2023-11-13 10:48:16', '');
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
INSERT INTO `sys_menu` VALUES (2141, '三方登录', 1, 12, 'platform', 'iot/platform/index', NULL, 1, 1, 'C', '0', '0', 'iot:platform:list', 'cloud', 'admin', '2022-04-11 18:55:34', 'admin', '2023-08-23 11:26:52', '');
INSERT INTO `sys_menu` VALUES (2142, '平台查询', 2142, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:query', '#', 'admin', '2022-04-11 19:10:28', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2147, '设备用户列表', 2007, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:list', '#', 'admin', '2022-06-10 01:08:40', 'admin', '2022-06-10 01:10:46', '');
INSERT INTO `sys_menu` VALUES (2148, '设备定时', 2007, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:list', '#', 'admin', '2022-06-10 01:10:30', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2149, '大屏展示', 3047, 5, 'https://iot.fastbee.cn/bigScreen', NULL, NULL, 0, 0, 'M', '0', '0', '', 'monitor-a', 'admin', '2022-08-13 22:32:11', 'admin', '2023-08-23 23:11:11', '');
INSERT INTO `sys_menu` VALUES (2167, '可视化平台', 3047, 6, 'https://iot.fastbee.cn/view/#/project/items', NULL, NULL, 0, 0, 'C', '0', '0', '', 'eye-open', 'admin', '2022-11-06 21:44:50', 'admin', '2023-09-16 16:42:33', '');
INSERT INTO `sys_menu` VALUES (2168, '通道管理', 3044, 2, 'sip', 'iot/sip/index', NULL, 1, 0, 'C', '0', '0', 'iot:video:list', 'swagger', 'admin', '2023-02-21 00:21:39', 'admin', '2023-09-22 11:45:38', '');
INSERT INTO `sys_menu` VALUES (2169, '视频配置查询', 2168, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:query', '#', 'admin', '2023-02-22 07:55:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2170, '视频配置新增', 2168, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', ' iot:video:add', '#', 'admin', '2023-02-22 07:56:41', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '视频配置修改', 2168, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:edit', '#', 'admin', '2023-02-22 07:57:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '视频配置删除', 2168, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:remove', '#', 'admin', '2023-02-22 07:58:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2173, '固件任务', 3000, 1, 'task', '', NULL, 1, 0, 'F', '0', '0', 'iot:task:list', '#', 'admin', '2023-02-28 01:17:55', '', NULL, '【请填写功能名称】菜单');
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
INSERT INTO `sys_menu` VALUES (3031, 'Netty管理', 0, 10, 'netty', NULL, NULL, 1, 0, 'M', '0', '0', '', 'mq', 'admin', '2022-02-26 00:42:12', 'admin', '2023-12-10 17:55:33', '');
INSERT INTO `sys_menu` VALUES (3032, '客户端', 3031, 1, 'client', 'iot/netty/clients', NULL, 1, 0, 'C', '0', '0', 'monitor:server:list', 'client', 'admin', '2022-02-26 00:45:39', 'admin', '2023-08-23 23:38:08', '');
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
INSERT INTO `sys_menu` VALUES (3044, '视频中心', 0, 2, 'video', NULL, NULL, 1, 0, 'M', '0', '0', '', 'live', 'admin', '2023-05-16 22:05:11', 'admin', '2023-08-24 17:20:01', '');
INSERT INTO `sys_menu` VALUES (3045, '分屏显示', 3044, 0, 'splitview', 'iot/sip/splitview', NULL, 1, 0, 'C', '0', '0', '', 'build', 'admin', '2023-05-16 22:08:33', 'admin', '2023-05-20 15:31:06', '');
INSERT INTO `sys_menu` VALUES (3046, '配置管理', 3044, 3, 'mediaServer', 'iot/sip/mediaServer', NULL, 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2023-05-16 22:09:10', 'admin', '2023-09-22 11:45:43', '');
INSERT INTO `sys_menu` VALUES (3047, '数据可视化', 0, 9, 'screen', NULL, NULL, 1, 0, 'M', '0', '0', '', 'monitor-a', 'admin', '2023-08-23 23:09:28', 'admin', '2023-11-13 10:47:39', '');
INSERT INTO `sys_menu` VALUES (3048, 'Mqtt统计', 3031, 2, 'mqtt', 'iot/netty/mqtt', NULL, 1, 0, 'C', '0', '0', 'monitor:server:list', 'monitor', 'admin', '2023-08-23 23:40:28', 'admin', '2023-08-23 23:40:38', '');
INSERT INTO `sys_menu` VALUES (3049, '录像管理', 3044, 1, 'record', 'iot/record/record', NULL, 1, 0, 'C', '0', '0', '', 'video', 'admin', '2023-05-16 22:09:34', 'admin', '2023-09-22 11:45:26', '');
INSERT INTO `sys_menu` VALUES (3051, '规则引擎', 0, 4, 'ruleengine', NULL, NULL, 1, 0, 'M', '0', '0', '', 'channel', 'admin', '2023-07-03 21:22:19', 'admin', '2023-11-13 10:46:00', '');
INSERT INTO `sys_menu` VALUES (3055, '规则脚本', 3051, 2, 'script', 'iot/scene/script', NULL, 1, 0, 'C', '0', '0', 'iot:script:list', 'code', 'admin', '2023-07-06 21:03:14', 'admin', '2024-03-22 12:01:55', '');
INSERT INTO `sys_menu` VALUES (3147, '告警配置', 3051, 6, 'alert', 'iot/alert/index', NULL, 1, 0, 'C', '0', '0', 'iot:alert:list', 'alert', 'admin', '2023-11-12 10:58:01', 'admin', '2024-02-01 02:36:17', '');
INSERT INTO `sys_menu` VALUES (3157, '通知渠道', 3000, 7, 'channel', 'notify/channel/index', NULL, 1, 0, 'C', '0', '0', 'notify:channel:list', 'notify_channel', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-19 17:10:46', '通知渠道菜单');
INSERT INTO `sys_menu` VALUES (3158, '通知渠道查询', 3157, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:query', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:24', '');
INSERT INTO `sys_menu` VALUES (3159, '通知渠道新增', 3157, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:add', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:31', '');
INSERT INTO `sys_menu` VALUES (3160, '通知渠道修改', 3157, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:edit', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:36', '');
INSERT INTO `sys_menu` VALUES (3161, '通知渠道删除', 3157, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:remove', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:41', '');
INSERT INTO `sys_menu` VALUES (3162, '通知渠道导出', 3157, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:export', '#', 'admin', '2023-12-01 10:18:41', 'admin', '2024-01-03 11:01:52', '');
INSERT INTO `sys_menu` VALUES (3166, '通知模板', 3000, 7, 'notifyTemplate', 'notify/template/index', NULL, 1, 0, 'C', '0', '0', 'notify:template:list', 'template', 'admin', '2023-12-22 15:19:44', 'admin', '2024-01-19 17:10:57', '');
INSERT INTO `sys_menu` VALUES (3167, '通知日志', 3000, 7, 'notifylog', 'notify/log/index', NULL, 1, 0, 'C', '0', '0', 'notify:log:list', 'notify_log', 'admin', '2023-12-28 10:10:52', 'admin', '2024-01-03 11:05:11', '');
INSERT INTO `sys_menu` VALUES (3168, '通知模板查询', 3166, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:query', '#', 'admin', '2024-01-03 11:02:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3169, '通知模板新增', 3166, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:add', '#', 'admin', '2024-01-03 11:03:23', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3170, '通知模板修改', 3166, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:edit', '#', 'admin', '2024-01-03 11:03:40', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3171, '通知模板删除', 3166, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:remove', '#', 'admin', '2024-01-03 11:03:55', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3172, '通知模板导出', 3166, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:export', '#', 'admin', '2024-01-03 11:04:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3173, '通知日志导出', 3167, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:export', '#', 'admin', '2024-01-03 11:06:11', 'admin', '2024-03-19 10:46:28', '');
INSERT INTO `sys_menu` VALUES (3174, '通知日志删除', 3167, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:remove', '#', 'admin', '2024-01-03 11:06:31', 'admin', '2024-03-19 10:46:32', '');
INSERT INTO `sys_menu` VALUES (3175, '通知模板测试', 3166, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:send', '#', 'admin', '2024-01-03 11:07:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3176, '安全生产企业节点', 3047, 7, 'https://iot.fastbee.cn/bigScreen', NULL, NULL, 0, 0, 'C', '0', '0', '', 'bug', 'admin', '2024-01-09 15:07:43', 'admin', '2024-03-19 10:26:37', '');
INSERT INTO `sys_menu` VALUES (3178, '服务下发', 2007, 9, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:service:invoke', '#', 'admin', '2021-12-16 00:40:12', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3179, '规则脚本导出', 3055, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:script:export', '#', 'admin', '2024-02-18 16:29:02', 'admin', '2024-02-18 16:38:04', '');
INSERT INTO `sys_menu` VALUES (3180, '规则脚本查询', 3055, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:script:query', '#', 'admin', '2024-02-18 16:29:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3181, '规则脚本新增', 3055, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:script:add', '#', 'admin', '2024-02-18 16:29:41', 'admin', '2024-02-18 16:37:42', '');
INSERT INTO `sys_menu` VALUES (3182, '规则脚本修改', 3055, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:script:edit', '#', 'admin', '2024-02-18 16:30:03', 'admin', '2024-02-18 16:37:50', '');
INSERT INTO `sys_menu` VALUES (3183, '规则脚本删除', 3055, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:script:remove', '#', 'admin', '2024-02-18 16:30:20', 'admin', '2024-02-18 16:37:59', '');
INSERT INTO `sys_menu` VALUES (3184, '告警配置导出', 3147, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:alert:export', '#', 'admin', '2024-02-18 16:35:43', 'admin', '2024-02-18 16:38:29', '');
INSERT INTO `sys_menu` VALUES (3185, '告警配置查询', 3147, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:alert:query', '#', 'admin', '2024-02-18 16:36:07', 'admin', '2024-02-18 16:38:22', '');
INSERT INTO `sys_menu` VALUES (3186, '告警配置新增', 3147, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:alert:add', '#', 'admin', '2024-02-18 16:36:35', 'admin', '2024-02-18 16:38:35', '');
INSERT INTO `sys_menu` VALUES (3187, '告警配置修改', 3147, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:alert:edit', '#', 'admin', '2024-02-18 16:37:00', 'admin', '2024-02-18 16:38:40', '');
INSERT INTO `sys_menu` VALUES (3188, '告警配置删除', 3147, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:alert:remove', '#', 'admin', '2024-02-18 16:37:22', 'admin', '2024-02-18 16:38:46', '');
INSERT INTO `sys_menu` VALUES (3189, '云云对接', 0, 3, 'speaker', NULL, NULL, 1, 0, 'M', '0', '0', '', 'cloud', 'admin', '2023-10-17 09:14:05', 'admin', '2023-11-13 10:45:51', '');
INSERT INTO `sys_menu` VALUES (3190, '音箱配置', 3189, 1, 'clientDetails', 'speaker/clientDetails/index', NULL, 1, 0, 'C', '0', '0', 'iot:clientDetails:list', 'speaker', 'admin', '2022-02-07 22:08:58', 'admin', '2023-12-11 15:55:43', '云云对接菜单');
INSERT INTO `sys_menu` VALUES (3191, '云云对接查询', 3190, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:query', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3192, '云云对接新增', 3190, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:add', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3193, '云云对接修改', 3190, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:edit', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3194, '云云对接删除', 3190, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:remove', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3195, '云云对接导出', 3190, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:clientDetails:export', '#', 'admin', '2022-02-07 22:08:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3196, '小度音箱', 3189, 2, 'dueros', 'speaker/dueros/index', NULL, 1, 0, 'C', '0', '0', 'dueros:relatedProduct:list', 'dueros', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-07 11:13:45', '小度音箱');
INSERT INTO `sys_menu` VALUES (3197, '关联产品查询', 3196, 0, '', NULL, NULL, 1, 0, 'F', '0', '0', 'dueros:relatedProduct:query', '#', 'admin', '2023-12-06 11:10:41', 'admin', '2023-12-06 15:13:56', '');
INSERT INTO `sys_menu` VALUES (3198, '关联物模查询', 3196, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'dueros:relatedThingsModel:query', '#', 'admin', '2023-12-06 11:14:27', 'admin', '2023-12-06 15:15:28', '');
INSERT INTO `sys_menu` VALUES (3199, '关联产品新增', 3196, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'dueros:relatedProduct:add', '#', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-06 15:14:55', '');
INSERT INTO `sys_menu` VALUES (3200, '关联产品删除', 3196, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'dueros:relatedProduct:delete', '#', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-06 15:15:16', '');
INSERT INTO `sys_menu` VALUES (3201, '关联产品编辑', 3196, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'dueros:relatedProduct:edit', '#', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-06 15:15:08', '');
INSERT INTO `sys_menu` VALUES (3202, '关联物模编辑', 3196, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'dueros:relatedThingsModel:edit', '#', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-06 15:17:28', '');
INSERT INTO `sys_menu` VALUES (3203, '关联物模删除', 3196, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'dueros:relatedThingsModel:delete', '#', 'admin', '2023-10-19 17:03:10', 'admin', '2023-12-06 15:17:35', '');
INSERT INTO `sys_menu` VALUES (3204, '组态管理', 0, 8, 'scada', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'monitor-a', 'admin', '2023-11-10 09:56:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3205, '组态中心', 3204, 1, 'center', 'scada/center/index', NULL, 1, 0, 'C', '0', '0', 'scada:center:list', 'product', 'admin', '2024-03-15 14:32:37', 'admin', '2024-03-19 09:49:13', '组态中心菜单');
INSERT INTO `sys_menu` VALUES (3206, '组态中心查询', 3205, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:center:query', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3207, '组态中心新增', 3205, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:center:add', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3208, '组态中心修改', 3205, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:center:edit', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3209, '组态中心删除', 3205, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:center:remove', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3210, '组态中心导出', 3205, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:center:export', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3211, '图表管理', 3204, 2, 'echart', 'scada/echart/index', NULL, 1, 0, 'C', '0', '0', 'scada:echart:list', 'chart', 'admin', '2024-03-15 14:32:37', 'admin', '2024-03-19 09:49:41', '图表管理菜单');
INSERT INTO `sys_menu` VALUES (3212, '图表管理查询', 3211, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:echart:query', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3213, '图表管理新增', 3211, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:echart:add', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3214, '图表管理修改', 3211, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:echart:edit', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3215, '图表管理删除', 3211, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:echart:remove', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3216, '图表管理导出', 3211, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:echart:export', '#', 'admin', '2024-03-15 14:32:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3217, '图库管理', 3204, 3, 'gallery', 'scada/gallery/index', NULL, 1, 0, 'C', '0', '0', 'scada:gallery:list', 'picture', 'admin', '2024-03-15 14:32:37', 'admin', '2024-03-19 09:49:51', '图库管理菜单');
INSERT INTO `sys_menu` VALUES (3218, '图库管理查询', 3217, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:gallery:query', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3219, '图库管理新增', 3217, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:gallery:add', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3220, '图库管理修改', 3217, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:gallery:edit', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3221, '图库管理删除', 3217, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:gallery:remove', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3222, '图库管理导出', 3217, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:gallery:export', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3223, '模型管理', 3204, 4, 'model', 'scada/model/index', NULL, 1, 0, 'C', '0', '0', 'scada:model:list', 'model', 'admin', '2024-03-15 14:32:38', 'admin', '2024-03-19 09:50:19', '模型管理菜单');
INSERT INTO `sys_menu` VALUES (3224, '模型管理查询', 3223, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:model:query', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3225, '模型管理新增', 3223, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:model:add', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3226, '模型管理修改', 3223, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:model:edit', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3227, '模型管理删除', 3223, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:model:remove', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3228, '模型管理导出', 3223, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:model:export', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3229, '组件管理', 3204, 5, 'component', 'scada/component/index', NULL, 1, 0, 'C', '0', '0', 'scada:component:list', 'redis', 'admin', '2024-03-15 14:32:38', 'admin', '2024-03-19 09:50:30', '组件管理菜单');
INSERT INTO `sys_menu` VALUES (3230, '组件管理查询', 3229, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:component:query', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3231, '组件管理新增', 3229, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:component:add', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3232, '组件管理修改', 3229, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:component:edit', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3233, '组件管理删除', 3229, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:component:remove', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3234, '组件管理导出', 3229, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scada:component:export', '#', 'admin', '2024-03-15 14:32:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3235, '通知日志详情', 3167, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:query', '#', 'admin', '2024-03-19 10:36:03', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3236, '组态详情预览', 3205, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scada:center:preview', '#', 'admin', '2024-04-11 14:19:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3237, '产品模型列表', 2043, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:list', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3238, '产品模型详情', 2043, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:query', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3239, '产品模型导入', 2043, 12, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:import', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3240, '产品模型新增', 2043, 13, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:add', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3241, '产品模型修改', 2043, 14, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:edit', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3242, '产品模型删除', 2043, 15, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:remove', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` VALUES (3243, '设备定时详情', 2148, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3244, '设备定时新增', 2148, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:add', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3245, '设备定时修改', 2148, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3246, '设备定时执行', 2148, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:execute', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3247, '设备定时导出', 2148, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:export', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3248, '设备定时删除', 2148, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3249, '设备用户详情', 2147, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3250, '分享设备', 2147, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:share', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3251, '设备用户修改', 2147, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3252, '取消分享', 2147, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3253, '录像管理列表', 3049, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:list', '#', 'admin', '2024-03-19 11:18:22', 'admin', '2024-04-12 17:06:13', '');
INSERT INTO `sys_menu` VALUES (3254, '查看录像', 3049, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:query', '#', 'admin', '2024-03-19 11:18:22', 'admin', '2024-04-12 17:06:19', '');
INSERT INTO `sys_menu` VALUES (3255, '录像下载', 3049, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:download', '#', 'admin', '2024-03-19 11:18:22', 'admin', '2024-04-12 17:06:25', '');
INSERT INTO `sys_menu` VALUES (3256, '场景联动执行', 2085, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:scene:run', '#', 'admin', '2024-03-19 16:15:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3257, '字典刷新', 105, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:dict:refresh', '#', 'admin', '2024-03-19 16:38:13', 'admin', '2024-03-19 16:38:35', '');
INSERT INTO `sys_menu` VALUES (3258, '参数刷新', 106, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:config:refresh', '#', 'admin', '2024-03-19 16:40:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3259, '三方登录详情', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3260, '三方登录新增', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:add', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3261, '三方登录修改', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3262, '三方登录导出', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:export', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3263, '三方登录删除', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3264, '任务执行', 110, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:job:run', '#', 'admin', '2024-03-19 17:05:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3265, '缓存列表', 124, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:cache:list', '#', 'admin', '2024-03-19 17:09:49', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3266, '缓存删除', 124, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2024-03-19 17:10:15', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3267, '客户端列表', 3032, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:client:list', '#', 'admin', '2024-03-19 17:19:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3268, '客户端踢出', 3032, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:client:remove', '#', 'admin', '2024-03-19 17:20:00', '', NULL, '');




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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, 'FastBeeV1.2版本发布', '2', 0x3C703EE8BF99E698AFE6B58BE8AF95E58685E5AEB9EFBC8CE696B0E78988E69CACE58A9FE883BDEFBC9A3C2F703E3C6F6C3E3C6C693EE694AFE68C81E5A49AE7A79FE688B73C2F6C693E3C6C693EE694AFE68C81E8AEBEE5A487E58886E4BAAB3C2F6C693E3C6C693EE694AFE68C81E697B6E5BA8FE695B0E68DAEE5BA933C2F6C693E3C6C693EE7AE80E58D95E8AEA4E8AF81E5928CE58AA0E5AF86E8AEA4E8AF81E7BB9FE4B8803C2F6C693E3C2F6F6C3E, '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-09-26 21:21:30', '管理员');
INSERT INTO `sys_notice` VALUES (2, 'FastBee sdk支持树莓派', '1', 0x3C703EE8BF99E698AFE6B58BE8AF95E58685E5AEB9EFBC8CE79BAEE5898D73646BE694AFE68C81E79A84E78988E69CAC3A3C2F703E3C703E3C62723E3C2F703E3C703E3C62723E3C2F703E3C6F6C3E3C6C693E41726475696E6F20657370383236363C2F6C693E3C6C693E41726475696E6F2065737033323C2F6C693E3C6C693E6573702D6964663C2F6C693E3C6C693E72617370626572727920E6A091E88E93E6B4BE3C2F6C693E3C2F6F6C3E, '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-09-26 21:21:41', '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作日志ID',
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
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;


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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = DYNAMIC;

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2021-12-15 21:36:18', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '设备租户', 'tenant', 2, '5', 1, 1, '0', '0', 'admin', '2021-12-16 16:41:30', 'admin', '2023-04-12 19:53:34', '管理产品和设备');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'general', 3, '5', 1, 1, '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2024-02-07 15:29:09', '设备的最终用户，只能管理设备和分组');
INSERT INTO `sys_role` VALUES (4, '游客', 'visitor', 4, '1', 1, 1, '0', '0', 'admin', '2021-12-16 16:44:30', 'admin', '2023-12-07 12:03:51', '只能查询和新增系统数据');
INSERT INTO `sys_role` VALUES (5, '管理员', 'manager', 5, '1', 1, 1, '0', '0', 'admin', '2022-06-10 13:54:29', 'admin', '2023-04-12 19:50:29', '普通管理员');
INSERT INTO `sys_role` VALUES (6, '景区运维员', ' @PreAuthorize( @ss.hasRole(admin\'))', 5, '1', 1, 1, '0', '2', '18926529123', '2023-11-10 12:32:22', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `sys_role_menu` VALUES (3, 3044);
INSERT INTO `sys_role_menu` VALUES (3, 3051);
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
INSERT INTO `sys_role_menu` VALUES (4, 2123);
INSERT INTO `sys_role_menu` VALUES (4, 2124);
INSERT INTO `sys_role_menu` VALUES (4, 2125);
INSERT INTO `sys_role_menu` VALUES (4, 2129);
INSERT INTO `sys_role_menu` VALUES (4, 2130);
INSERT INTO `sys_role_menu` VALUES (4, 2131);
INSERT INTO `sys_role_menu` VALUES (4, 2136);
INSERT INTO `sys_role_menu` VALUES (4, 2137);
INSERT INTO `sys_role_menu` VALUES (4, 2141);
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
INSERT INTO `sys_role_menu` VALUES (4, 3044);
INSERT INTO `sys_role_menu` VALUES (4, 3047);
INSERT INTO `sys_role_menu` VALUES (4, 3051);
INSERT INTO `sys_role_menu` VALUES (4, 3077);
INSERT INTO `sys_role_menu` VALUES (4, 3078);
INSERT INTO `sys_role_menu` VALUES (4, 3080);
INSERT INTO `sys_role_menu` VALUES (4, 3082);
INSERT INTO `sys_role_menu` VALUES (4, 3085);
INSERT INTO `sys_role_menu` VALUES (4, 3163);
INSERT INTO `sys_role_menu` VALUES (4, 3164);
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
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '蜂信管理员', '00', '164770707@qq.com', '15888888888', '0', '/profile/avatar/2024/01/30/blob_20240130102830A001.png', '$2a$10$QAow7ybs74fkSWJDJkVTNeogF7mhnihF7STErt78PxDhHiNno4IUu', '0', '0', '120.230.254.43', '2024-02-07 14:06:00', 'admin', '2021-12-15 21:36:18', '', '2024-02-07 14:06:00', '管理员');
INSERT INTO `sys_user` VALUES (2, 100, 'fastbee-t1', '蜂信租户壹', '00', '', '15888888880', '0', '', '$2a$10$BAWId9C2Nrcwklzl1Ikoau4iqL8XRGvfRjq6Wl.PXWpzwAw0sXMdK', '0', '0', '61.145.97.26', '2023-08-29 14:52:27', 'admin', '2022-04-15 16:21:25', 'admin', '2023-08-29 14:52:26', NULL);
INSERT INTO `sys_user` VALUES (3, 100, 'fastbee-t2', '蜂信租户贰', '00', '', '15888888881', '0', '', '$2a$10$1zMlbW7hGpzA59gpzWGO/ObeASziQ296evjMjHrYdZnxKBLU4WUum', '0', '0', '127.0.0.1', '2022-06-12 00:54:28', 'admin', '2022-04-15 16:22:08', 'admin', '2022-06-12 00:54:30', NULL);
INSERT INTO `sys_user` VALUES (4, 100, 'fastbee-u1', '蜂信用户壹', '00', '', '15888888882', '0', '', '$2a$10$691RJMXZ9HM4sgNTExLPfO5Nw6J6cWgCvcoF9V.jKMnPk5o/8c9VS', '0', '0', '127.0.0.1', '2024-01-23 10:42:28', 'admin', '2022-04-15 16:22:37', 'admin', '2024-01-23 10:42:27', NULL);
INSERT INTO `sys_user` VALUES (5, 100, 'fastbee-u2', '蜂信用户贰', '00', '', '15888888883', '0', '', '$2a$10$x3rM39rewwbi7ayvriGMEOKUHoPCqcL2CYXPLTJRCWYPVvykFIYJq', '0', '0', '127.0.0.1', '2022-06-12 00:55:45', 'admin', '2022-04-15 16:23:13', 'admin', '2023-10-25 10:15:02', NULL);
INSERT INTO `sys_user` VALUES (6, 100, 'fastbee', '游客账号', '00', '221112@qq.com', '15888888884', '0', '', '$2a$10$VJgxhCwmqjO69RXPtQPbxu8YIJ3rdA89004FVJf3Z9tKJxRGjQ4Nu', '0', '0', '219.134.168.219', '2024-01-30 11:46:58', 'admin', '2022-03-09 16:49:19', 'admin', '2024-01-30 11:46:57', NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 2);
INSERT INTO `sys_user_role` VALUES (4, 3);
INSERT INTO `sys_user_role` VALUES (5, 3);
INSERT INTO `sys_user_role` VALUES (6, 4);
INSERT INTO `sys_user_role` VALUES (13, 3);

SET FOREIGN_KEY_CHECKS = 1;
