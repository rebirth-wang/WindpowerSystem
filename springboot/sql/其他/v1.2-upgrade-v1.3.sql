-- ----------------------------
-- 创建告警表
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
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建告警日志表
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_log`;
CREATE TABLE `iot_alert_log`  (
  `alert_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '告警ID',
  `alert_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警名称',
  `alert_level` tinyint(11) NOT NULL COMMENT '告警级别（1=提醒通知，2=轻微问题，3=严重警告）',
  `status` tinyint(11) NOT NULL COMMENT '处理状态(1=不需要处理,2=未处理,3=已处理)',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `detail` json NULL COMMENT '告警详情（对应物模型）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`alert_log_id`) USING BTREE,
  INDEX `iot_alert_log_index_serial_number`(`serial_number`) USING BTREE,
  INDEX `iot_alert_log_index_product_id`(`product_id`) USING BTREE,
  INDEX `iot_alert_log_index_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 149279 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建告警触发器表
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
) ENGINE = InnoDB AUTO_INCREMENT = 193 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '告警触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建场景联动表
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
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场景联动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建场景联动触发器表
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
) ENGINE = InnoDB AUTO_INCREMENT = 213 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '场景联动触发器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建视频设备表
-- ----------------------------
DROP TABLE IF EXISTS `sip_device`;
CREATE TABLE `sip_device`(
     `device_id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
     `device_sip_id` varchar(64) NOT NULL  COMMENT '设备sip_id',
     `device_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '设备名称',
     `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品ID',
     `product_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品名称',
     `manufacturer` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '厂商名称',
     `model`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品型号',
     `firmware`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '固件版本',
     `transport`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'UDP' COMMENT '传输模式',
     `streamMode`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'UDP' COMMENT '流模式',
     `online`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '在线状态',
     `registerTimeMillis`	bigint(20) NOT NULL COMMENT '注册时间',
     `ip`	varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备入网IP',
     `port`	bigint(10) NULL DEFAULT NULL COMMENT '设备接入端口号',
     `hostAddress`	varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备地址',
     PRIMARY KEY(`device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建视频设备通道
-- ----------------------------
DROP TABLE IF EXISTS `sip_device_channel`;
CREATE TABLE `sip_device_channel` (
    `channel_id`	bigint(64) NOT NULL AUTO_INCREMENT COMMENT '通道ID',
    `channel_sip_id`	varchar(64) NOT NULL  COMMENT '通道SipID',
    `device_sip_id`	varchar(64) NOT NULL COMMENT '设备sip_id',
    `channel_name`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道名称',
    `manufacture`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '厂商名称',
    `model`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '产品型号',
    `owner`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '设备归属',
    `civilCode`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '行政区域',
    `block`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '警区',
    `address`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '安装地址',
    `parentId`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '父级id',
    `ipAddress`	varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL DEFAULT '' COMMENT '设备入网IP',
    `port`	bigint(10) NULL DEFAULT 0 COMMENT '设备接入端口号',
    `password`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码',
    `PTZType`	bigint(20) NOT NULL DEFAULT 0 COMMENT 'PTZ类型',
    `PTZTypeText`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'PTZ类型描述字符串',
    `status`	tinyint(1) NOT NULL DEFAULT 1 COMMENT '设备状态（1-未激活，2-禁用，3-在线，4-离线）',
    `longitude`	double(11, 6) NULL DEFAULT NULL COMMENT '设备经度',
    `latitude`	double(11, 6) NULL DEFAULT NULL COMMENT '设备纬度',
    `streamId`	varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体ID',
    `subCount`	bigint(20) NOT NULL DEFAULT 0 COMMENT '子设备数',
    `parental`	tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有子设备（1-有, 0-没有）',
    `hasAudio`	tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否含有音频（1-有, 0-没有）',
    PRIMARY KEY(`channel_id`,`device_sip_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备通道信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建可视化项目表
-- ----------------------------
DROP TABLE IF EXISTS `iot_goview_project`;
CREATE TABLE `iot_goview_project` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `project_name` varchar(255) NOT NULL COMMENT '项目名称',
  `state` int(1) NOT NULL DEFAULT '0' COMMENT '项目状态[0未发布,1发布]',
  `index_image` varchar(255) DEFAULT NULL COMMENT '首页图片',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '删除状态[1删除,-1未删除]',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '项目介绍',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表';

-- ----------------------------
-- 创建可视化项目数据表
-- ----------------------------
DROP TABLE IF EXISTS `iot_goview_project_data`;
CREATE TABLE `iot_goview_project_data` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `project_id` varchar(50) NOT NULL COMMENT '项目id',
  `content` longblob COMMENT '存储数据',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目数据关联表';

-- ----------------------------
-- 更新设备定时任务表结构
-- ----------------------------
alter table `iot_device_job` modify column device_id bigint(20) NULL DEFAULT NULL COMMENT '设备ID';
alter table `iot_device_job` modify column product_id bigint(20) NULL DEFAULT NULL COMMENT '产品ID';
alter table `iot_device_job` modify column product_name varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名称';
alter table `iot_device_job` add `alert_trigger` json NULL COMMENT '定时告警触发器';

-- ----------------------------
-- 更新设备日志表结构
-- ----------------------------
alter table `iot_device_log` modify column device_id bigint(20) NULL DEFAULT NULL COMMENT '设备ID';
alter table `iot_device_log` modify column device_name varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备名称';
alter table `iot_device_log` modify column user_id bigint(20) NULL DEFAULT NULL COMMENT '用户ID';
alter table `iot_device_log` modify column user_name varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户昵称';
alter table `iot_device_log` modify column tenant_id bigint(20) NULL DEFAULT NULL COMMENT '租户ID';
alter table `iot_device_log` modify column tenant_name varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '租户名称';

-- ----------------------------
-- 更新通用物模型和产品模型表结构并设置默认值
-- ----------------------------
alter table `iot_things_model` add `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前';
alter table `iot_things_model` add `is_readonly` tinyint(1) NULL DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)';
alter table `iot_things_model_template` add `model_order` int(10) NULL DEFAULT 0 COMMENT '排序，值越大，排序越靠前';
alter table `iot_things_model_template` add `is_readonly` tinyint(1) NULL DEFAULT 0 COMMENT '是否只读数据(0-否，1-是)';
update `iot_things_model_template` set is_readonly=1 , model_order=1;
update `iot_things_model` set is_readonly=1 , model_order=1;

-- ----------------------------
-- 新增视频监控菜单
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2150, '监控设备', 2000, 10, 'iot/sip/index', 'iot/sip/index', NULL, 1, 0, 'C', '0', '0', 'sip:device:list', 'channel', 'admin', '2022-10-24 16:36:07', 'admin', '2022-10-24 16:38:07', '');
INSERT INTO `sys_menu` VALUES (2151, '设备通道', 2000, 11, 'iot/sip/channel', 'iot/sip/channel', '', 1, 0, 'C', '0', '0', 'sip:channel:list', 'live', 'admin', '2022-10-24 16:39:05', 'admin', '2022-10-24 16:39:48', '');

-- ----------------------------
-- 创建外键索引
-- ----------------------------
alter table `iot_category` add INDEX `iot_category_index_tenant_id`(`tenant_id`) USING BTREE;
alter table `iot_category` add INDEX `iot_category_index_parent_id`(`parent_id`) USING BTREE;

alter  table `iot_device` add INDEX `iot_device_index_product_id`(`product_id`) USING BTREE;
alter  table `iot_device` add INDEX `iot_device_index_tanant_id`(`tenant_id`) USING BTREE;
alter  table `iot_device` add INDEX `iot_device_index_user_id`(`user_id`) USING BTREE;
alter  table `iot_device` add INDEX `iot_device_index_create_time`(`create_time`) USING BTREE;
alter  table `iot_device` add UNIQUE INDEX `iot_device_index_serial_number`(`serial_number`) USING BTREE;

alter  table `iot_device_job` add INDEX `iot_device_job_index_device_id`(`device_id`) USING BTREE;
alter  table `iot_device_job` add INDEX `iot_device_job_index_product_id`(`product_id`) USING BTREE;
alter  table `iot_device_job` add INDEX `iot_device_job_index_scene_id`(`scene_id`) USING BTREE;
alter  table `iot_device_job` add INDEX `iot_device_job_index_alert_id`(`alert_id`) USING BTREE;
alter  table `iot_device_job` add INDEX `iot_device_job_index_serial_number`(`serial_number`) USING BTREE;

alter  table `iot_device_log` add INDEX `iot_device_log_index_serial_number`(`serial_number`) USING BTREE;
alter  table `iot_device_log` add INDEX `iot_device_log_index_tenant_id`(`tenant_id`) USING BTREE;
alter  table `iot_device_log` add INDEX `iot_device_log_index_user_id`(`user_id`) USING BTREE;
alter  table `iot_device_log` add INDEX `iot_device_log_index_device_id`(`device_id`) USING BTREE;

alter  table `iot_device_user` add INDEX `iot_device_user_index_user_id`(`user_id`) USING BTREE;
alter  table `iot_device_user` add INDEX `iot_device_user_index_tenant_id`(`tenant_id`) USING BTREE;

alter  table `iot_firmware` add INDEX `iot_firmware_index_product_id`(`product_id`) USING BTREE;
alter  table `iot_firmware` add INDEX `iot_firmware_index_tenant_id`(`tenant_id`) USING BTREE;

alter  table `iot_product` add INDEX `iot_product_index_category_id`(`category_id`) USING BTREE;
alter  table `iot_product` add INDEX `iot_product_index_tenant_id`(`tenant_id`) USING BTREE;

alter  table `iot_product_authorize` add INDEX `iot_product_authorize_index_product_id`(`product_id`) USING BTREE;
alter  table `iot_product_authorize` add INDEX `iot_product_authorize_index_device_id`(`device_id`) USING BTREE;
alter  table `iot_product_authorize` add INDEX `iot_product_authorize_index_serial_number`(`serial_number`) USING BTREE;
alter  table `iot_product_authorize` add INDEX `iot_product_authorize_index_user_id`(`user_id`) USING BTREE;

alter  table `iot_things_model` add INDEX `iot_things_model_index_product_id`(`product_id`) USING BTREE;
alter  table `iot_things_model` add INDEX `iot_things_model_index_tenant_id`(`tenant_id`) USING BTREE;
alter  table `iot_things_model` add INDEX `iot_things_model_index_model_order`(`model_order`) USING BTREE;

alter  table `iot_things_model_template` add INDEX `iot_things_model_template_index_tenant_id`(`tenant_id`) USING BTREE;
alter  table `iot_things_model_template` add INDEX `iot_things_model_template_index_model_order`(`model_order`) USING BTREE;

alter  table `news` add INDEX `news_index_category_id`(`category_id`) USING BTREE;
alter  table `iot_group` add INDEX `iot_group_index_user_id`(`user_id`) USING BTREE;

