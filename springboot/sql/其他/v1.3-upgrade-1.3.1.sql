-- ----------------------------
-- 创建流媒体配置表
-- ----------------------------
DROP TABLE IF EXISTS `media_server`;
CREATE TABLE `media_server`  (
  `id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '流媒体配置ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
  `enabled` tinyint(1) NULL DEFAULT NULL COMMENT '使能开关',
  `protocol` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '默认播放协议',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器ip',
  `domain` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务器域名',
  `secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流媒体密钥',
  `port_http` int(11) NOT NULL DEFAULT 0 COMMENT 'http端口',
  `port_ws` int(11) NOT NULL DEFAULT 0 COMMENT 'ws端口',
  `port_rtmp` int(11) NOT NULL DEFAULT 0 COMMENT 'rtmp端口',
  `port_rtsp` int(11) NOT NULL DEFAULT 0 COMMENT 'rtsp端口',
  `rtp_port_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'rtp端口范围',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流媒体服务器配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 新增流媒体配置记录
-- ----------------------------
INSERT INTO `media_server` VALUES (1, 1, 'admin', 1, 'https', '1.1.1.1', 'fastbee', '035c73f7-bb6b-4889-a715-d9eb2d192xxx', 8082, 8082, 1935, 554, '30000,30100', '0', '', '2021-12-29 13:12:42', '', '2023-02-26 22:35:09', 'admin');

-- ----------------------------
-- 新增SIP配置表
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
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'sip系统配置' ROW_FORMAT = Dynamic;


-- ----------------------------
-- 新增SIP设备表
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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备' ROW_FORMAT = Dynamic;


-- ----------------------------
-- 新增设备通道表
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
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备通道信息' ROW_FORMAT = Dynamic;


-- ----------------------------
-- 添加字典类型和数据
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (119, 'SipID状态', 'sip_gen_status', '0', 'admin', '2023-02-19 15:43:36', 'admin', '2023-02-19 15:45:54', '1=未使用，2=使用中');
INSERT INTO `sys_dict_type` VALUES (120, '监控设备类型', 'video_type', '0', 'admin', '2023-02-22 01:06:38', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (121, '通道类型', 'channel_type', '0', 'admin', '2023-02-22 01:11:51', '', NULL, NULL);

INSERT INTO `sys_dict_data` VALUES (165, 7, '对象', 'object', 'iot_data_type', NULL, 'default', 'N', '0', 'admin', '2023-02-09 16:20:57', 'admin', '2023-02-09 16:21:08', NULL);

-- 删除网关子设备，添加监控设备
delete FROM `sys_dict_data` where dict_type='iot_device_type' and dict_value=3;
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


-- ----------------------------
-- 视频配置菜单记录
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2168, '视频配置', 2000, 10, 'sip', 'iot/sip/index', NULL, 1, 0, 'C', '0', '0', 'iot:video:list', 'live', 'admin', '2023-02-21 00:21:39', 'admin', '2023-02-22 07:54:06', '');
INSERT INTO `sys_menu` VALUES (2169, '视频配置查询', 2168, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:query', '#', 'admin', '2023-02-22 07:55:16', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2170, '视频配置新增', 2168, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', ' iot:video:add', '#', 'admin', '2023-02-22 07:56:41', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '视频配置修改', 2168, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:edit', '#', 'admin', '2023-02-22 07:57:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '视频配置删除', 2168, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:video:remove', '#', 'admin', '2023-02-22 07:58:03', '', NULL, '');

-- ----------------------------
-- 设备日志的log_value 长度改为256
-- ----------------------------
alter table `iot_device_log` modify column log_value varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志值';

