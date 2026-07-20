-- 数据库版本升级脚本
-- 适用于fastbee2.3版本到fastbee2.4版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作

DROP TABLE IF EXISTS `bridge`;
CREATE TABLE `bridge` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id唯一标识',
   `config_json` json NULL COMMENT '桥接配置信息',
   `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '连接器名称',
   `enable` char(1) NOT NULL DEFAULT 1 COMMENT '是否生效（0-不生效，1-生效）',
   `status` char(1) NULL DEFAULT 0 COMMENT '状态（0-未连接，1-连接中）',
   `type` tinyint(1) NOT NULL COMMENT '桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)',
   `direction` tinyint(1) NOT NULL COMMENT '桥接方向(1=输入，2=输出)',
   `route` varchar(255) COLLATE utf8_unicode_ci NULL DEFAULT '' COMMENT '转发路由（mqtt topic，http url）',
   `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
   `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
   `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
   `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
   `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '数据桥接表'
  ROW_FORMAT = Dynamic;

INSERT INTO `bridge` VALUES (1, '{\"name\": \"萤石云平台获取acessToken\", \"method\": \"POST\", \"hostUrl\": \"https://open.ys7.com/api/lapp/token/get\", \"hostUrlbody\": \"open.ys7.com/api/lapp/token/get\", \"requestQuerys\": \"{\\\"appKey\\\":\\\"appKey\\\",\\\"appSecret\\\":\\\"appSecret\\\"}\", \"requestHeaders\": \"{\\\"Content-Type\\\":\\\"application/x-www-form-urlencoded\\\"}\"}', '萤石云平台获取acessToken', '1', '1', 3, 2, '', '0', '', NULL, '', NULL, NULL);
INSERT INTO `bridge` VALUES (2, '{\"name\": \"萤石云平台获取直播URL\", \"method\": \"POST\", \"hostUrl\": \"https://open.ys7.com/api/lapp/v2/live/address/get\", \"hostUrlbody\": \"open.ys7.com/api/lapp/v2/live/address/get\", \"requestQuerys\": \"{\\\"accessToken\\\":\\\"必填：授权access_token\\\",\\\"deviceSerial\\\":\\\"必填：设备序列号例如427734222\\\",\\\"channelNo\\\":\\\"可选：通道号\\\",\\\"protocol\\\":\\\"可选：流播放协议，1-ezopen、2-hls、3-rtmp、4-flv，默认为1\\\",\\\"code\\\":\\\"可选：ezopen协议地址的设备的视频加密密码\\\",\\\"expireTime\\\":\\\"可选：过期时长，单位秒；针对hls/rtmp/flv设置有效期，相对时间；30秒-720天\\\",\\\"type\\\":\\\"可选：地址的类型，1-预览，2-本地录像回放，3-云存储录像回放\\\",\\\"quality\\\":\\\"可选：视频清晰度，1-高清（主码流）、2-流畅（子码流）\\\",\\\"startTime\\\":\\\"可选：本地录像/云存储录像回放开始时间\\\",\\\"stopTime\\\":\\\"可选：本地录像/云存储录像回放结束时间\\\",\\\"gbchannel\\\":\\\"可选：国标设备的通道编号，视频通道编号ID\\\"}\", \"requestHeaders\": null}', '萤石云平台获取直播URL', '1', '1', 3, 2, '', '0', '', NULL, '', NULL, NULL);
INSERT INTO `bridge` VALUES (3, '{\"name\": \"海康综合安防平台分页获取监控点资源\", \"method\": \"POST\", \"hostUrl\": \"https://127.0.0.1：443/artemis/api/resource/v1/cameras\", \"hostUrlbody\": \"127.0.0.1：443/artemis/api/resource/v1/cameras\", \"requestConfig\": \"{\\\"appKey\\\":\\\"appKey\\\",\\\"appSecret\\\":\\\"appSecret\\\"}\", \"requestQuerys\": \"{\\\"pageNo\\\":\\\"pageNo\\\",\\\"pageSize\\\":\\\"pageSize\\\"}\", \"requestHeaders\": null}', '海康综合安防平台分页获取监控点资源', '1', '0', 3, 2, '', '0', '', NULL, '', NULL, NULL);
INSERT INTO `bridge` VALUES (4, '{\"name\": \"海康综合安防平台获取监控点预览取流URLv2\", \"method\": \"POST\", \"hostUrl\": \"https://127.0.0.1:443/artemis/api/video/v2/cameras/previewURLs\", \"hostUrlbody\": \"127.0.0.1:443/artemis/api/video/v2/cameras/previewURLs\", \"requestConfig\": \"{\\\"appKey\\\":\\\"appKey\\\",\\\"appSecret\\\":\\\"appSecret\\\"}\", \"requestQuerys\": \"{\\\"cameraIndexCode\\\":\\\"必填：监控点唯一标识，分页获取监控点资源接口获取返回参数cameraIndexCode\\\",\\\"streamType\\\":\\\"可选：码流类型，0:主码流 1:子码流 2:第三码流 参数不填，默认为主码流\\\",\\\"protocol\\\":\\\"可选：取流协议，\\\\\\\"hik\\\\\\\",\\\\\\\"rtsp\\\\\\\",\\\\\\\"rtmp\\\\\\\",\\\\\\\"hls\\\\\\\",\\\\\\\"ws\\\\\\\"\\\",\\\"transmode\\\":\\\"可选：传输协议，0:UDP 1:TCP 默认是TCP\\\",\\\"streamform\\\":\\\"可选：输出码流转封装格式，“ps”:PS封装格式、“rtp”:RTP封装协议\\\"}\", \"requestHeaders\": null}', '海康综合安防平台获取监控点预览取流URLv2', '1', '0', 3, 2, '', '0', '', NULL, '', NULL, NULL);
INSERT INTO `bridge` VALUES (5, '{\"name\": \"海康综合安防平台获取监控点回放取流URLv2\", \"method\": \"POST\", \"hostUrl\": \"https://127.0.0.1:443/artemis/api/video/v2/cameras/playbackURLs\", \"hostUrlbody\": \"127.0.0.1:443/artemis/api/video/v2/cameras/playbackURLs\", \"requestConfig\": \"{\\\"appKey\\\":\\\"appKey\\\",\\\"appSecret\\\":\\\"appSecret\\\"}\", \"requestQuerys\": \"{\\\"cameraIndexCode\\\":\\\"必填：监控点唯一标识，分页获取监控点资源接口获取返回参数cameraIndexCode\\\",\\\"recordLocation\\\":\\\"可选：存储类型,0：中心存储 1：设备存储 默认为中心存储\\\",\\\"protocol\\\":\\\"可选：取流协议，“hik”，“rtsp”，“ws”，“hls”，“rtmp”，\\\",\\\"transmode\\\":\\\"可选：0:UDP 1:TCP\\\",\\\"beginTime\\\":\\\"必填：开始时间\\\",\\\"endTime\\\":\\\"必填：结束时间\\\",\\\"streamform\\\":\\\"可选：输出码流转封装格式，“ps”:PS封装格式、“rtp”:RTP封装协议。\\\",\\\"lockType\\\":\\\"可选：录像锁定类型，0-查询全部录像；1-查询未锁定录像；2-查询已锁定录像，不传默认值为0。\\\"}\", \"requestHeaders\": null}', '海康综合安防平台获取监控点回放取流URLv2', '1', '0', 3, 2, '', '0', '', NULL, '', NULL, NULL);




insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接', '3051', '1', 'bridge', 'iot/bridge/index', 1, 0, 'C', '0', '0', 'iot:bridge:list', 'mq', 'admin', sysdate(), '', null, '数据桥接菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:bridge:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:bridge:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:bridge:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'iot:bridge:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('数据桥接导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'iot:bridge:export',       '#', 'admin', sysdate(), '', null, '');

-- 场景表新增字段
ALTER TABLE `iot_scene`
    ADD COLUMN `check_delay` int(10) NULL DEFAULT 0 COMMENT '延时匹配（秒钟）',
    ADD COLUMN `recover_id`  bigint(20) NULL DEFAULT 0 COMMENT '恢复告警场景ID';

-- 新增脚本输入桥接关联表
DROP TABLE IF EXISTS `iot_script_bridge`;
CREATE TABLE `iot_script_bridge` (
     `script_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本ID',
     `bridge_id` bigint(20) NOT NULL COMMENT '桥接配置id',
     PRIMARY KEY (`script_id`,`bridge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='脚本输入桥接关联表';

UPDATE iot_script
SET script_type = 'boolean_script'
WHERE script_type = 'if_script';

-- zzy
-- 新增用户密码修改权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('密码修改', 5, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'systerm:user:updatePwd', '#', 'admin', '2024-07-12 15:25:46', 'admin', '2024-07-12 15:26:36', '');

-- 新增设备记录表
DROP TABLE IF EXISTS `iot_device_record`;
CREATE TABLE `iot_device_record` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `operate_dept_id` bigint(20) DEFAULT NULL COMMENT '操作者机构id',
                                     `target_dept_id` bigint(20) DEFAULT NULL COMMENT '目标机构id',
                                     `product_id` bigint(20) DEFAULT NULL COMMENT '产品id',
                                     `device_id` bigint(20) DEFAULT NULL COMMENT '设备id',
                                     `serial_number` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '设备编号',
                                     `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
                                     `type` tinyint(2) NOT NULL COMMENT '设备记录类型（1-导入记录；2-回收记录；3-分配记录；4-分配详细记录）',
                                     `distribute_type` tinyint(2) DEFAULT NULL COMMENT '分配类型（1-选择分配；2-导入分配）',
                                     `total` int(20) DEFAULT NULL COMMENT '总数',
                                     `success_quantity` int(20) DEFAULT NULL COMMENT '成功数量',
                                     `fail_quantity` int(20) DEFAULT NULL COMMENT '失败数量',
                                     `status` tinyint(2) DEFAULT NULL COMMENT '状态（0-失败；1-成功）',
                                     `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                                     `tenant_name` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                                     `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='设备记录';

-- 新增设备记录相关权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备编号批量生成', 2007, 14, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:batchGenerator', '#', 'admin', '2024-07-19 15:28:56', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备记录导出', 2007, 13, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:record:export', '#', 'admin', '2024-07-19 15:28:25', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备记录', 2007, 12, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:record:list', '#', 'admin', '2024-07-19 15:28:01', '', NULL, '');


ALTER TABLE sip_device_channel
    MODIFY COLUMN tenant_id bigint(20) DEFAULT 1;

DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client` (
  `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id唯一标识',
  `client_key`     varchar(32) default null COMMENT '客户端key',
  `client_secret`  varchar(255) default null COMMENT '客户端秘钥',
  `token`          varchar(255) default null COMMENT '客户端token',
  `grant_type`     varchar(255) default null COMMENT '授权类型',
  `device_type`    varchar(32)  default null COMMENT '设备类型',
  `timeout`       int(11) default 604800 COMMENT 'token固定超时',
  `enable`        char(1) default '1'    COMMENT '是否生效（0-不生效，1-生效）',
  `del_flag`    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '系统授权表'
  ROW_FORMAT = Dynamic;

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权', '3', '1', 'sysclient', 'system/sysclient/index', 1, 0, 'C', '0', '0', 'system:sysclient:list', 'authenticate', 'admin', sysdate(), '', null, '系统授权菜单');
-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();
-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:sysclient:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:sysclient:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:sysclient:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:sysclient:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('系统授权导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:sysclient:export',       '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- Table structure for command_preferences
-- ----------------------------
DROP TABLE IF EXISTS `command_preferences`;
CREATE TABLE `command_preferences`  (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '指令id',
                                        `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '指令名称',
                                        `command` json NOT NULL COMMENT '指令',
                                        `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '设备编号',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '指令偏好设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of command_preferences
-- ----------------------------
INSERT INTO `command_preferences` VALUES (1, '读线圈', '{\"command\": \"010100000001fdca\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (2, '读保持寄存器', '{\"command\": \"010300000001840a\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (3, '写单个线圈', '{\"command\": \"0105000000010c0a\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (4, '写多个线圈', '{\"command\": \"010f00040000010a0e90\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (5, '写单个寄存器', '{\"command\": \"010600000002080b\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (6, '写多个寄存器', '{\"command\": \"01100003000006000100000002ea9b\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (7, '111', '{\"command\": \"010100000001fdca\"}', 'D1G9O8WFJ6J4');
INSERT INTO `command_preferences` VALUES (8, '上报测试', '{\"command\": \"01030000000045ca\"}', 'D1G9O8WFJ6J4');


-- ----------------------------
-- Table structure for iot_modbus_config
-- ----------------------------
DROP TABLE IF EXISTS `iot_modbus_config`;
CREATE TABLE `iot_modbus_config`  (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
                                      `product_id` bigint(20) NOT NULL COMMENT '所属产品id',
                                      `identifier` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联属性',
                                      `slave` int(10) NULL DEFAULT NULL COMMENT '从机地址',
                                      `address` int(10) NOT NULL COMMENT '寄存器地址',
                                      `is_readonly` tinyint(1) NOT NULL COMMENT '是否只读(0-否，1-是)',
                                      `data_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'modbus数据类型',
                                      `quantity` int(2) NULL DEFAULT NULL COMMENT '读取个数',
                                      `type` tinyint(1) NOT NULL COMMENT '寄存器类型 1-IO寄存器 2-数据寄存器',
                                      `bit_order` int(2) NULL DEFAULT NULL COMMENT 'bit位排序',
                                      `sort` int(6) NULL DEFAULT NULL COMMENT '排序',
                                      `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                      `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                      `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 318 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'modbus配置表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_modbus_job
-- ----------------------------
DROP TABLE IF EXISTS `iot_modbus_job`;
CREATE TABLE `iot_modbus_job`  (
                                   `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
                                   `job_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '任务名称',
                                   `sub_device_id` bigint(20) NOT NULL COMMENT '子设备id',
                                   `sub_serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '子设备编号',
                                   `device_type` tinyint(1) NULL DEFAULT NULL COMMENT '设备类型',
                                   `command` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '指令',
                                   `job_id` bigint(20) NOT NULL COMMENT '任务id',
                                   `status` char(1) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '状态（0正常 1暂停）',
                                   `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
                                   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                   `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
                                   PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '轮训任务列表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_modbus_params
-- ----------------------------
DROP TABLE IF EXISTS `iot_modbus_params`;
CREATE TABLE `iot_modbus_params`  (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
                                      `product_id` bigint(20) NOT NULL COMMENT '产品id',
                                      `poll_type` tinyint(1) NOT NULL COMMENT '是否启动云端轮训(1-云端轮训,2-边缘采集)',
                                      `slave_id` int(3) NULL DEFAULT NULL COMMENT '默认的子设备地址',
                                      `status_deter` tinyint(1) NULL DEFAULT NULL COMMENT '子设备状态判断方式 1-设备数据 2- 网关',
                                      `deter_timer` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '设备数据来判断子设备状态的时长(s)',
                                      `poll_length` int(3) NULL DEFAULT NULL COMMENT '批量读取的个数',
                                      `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                      `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                      `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '产品modbus配置参数' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数', 2043, 1, 'params', '', NULL, 1, 0, 'M', '0', '0', 'modbus:params:list', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '产品modbus配置参数菜单');
-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:params:query', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:params:add', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:params:edit', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:params:remove', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('产品modbus配置参数导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:params:export', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务', 2007, 1, 'modbusjob', '', NULL, 1, 0, 'M', '0', '0', 'modbus:job:list', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '轮训任务列菜单');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:job:query', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:job:add', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:job:edit', '#', 'admin', '2024-07-22 14:51:08', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:job:remove', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('轮训任务导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:job:export', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置', 2043, 1, 'config', '', NULL, 1, 0, 'M', '0', '0', 'modbus:config:list', '#', 'admin', '2024-07-22 14:51:09', '', NULL, 'modbus配置菜单');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:config:query', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:config:add', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:config:edit', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:config:remove', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('modbus配置导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'modbus:config:export', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');


-- ----------------------------
-- Table structure for iot_sip_relation
-- ----------------------------
DROP TABLE IF EXISTS `iot_sip_relation`;
CREATE TABLE `iot_sip_relation`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
                                     `channel_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '监控设备编号',
                                     `re_device_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的设备id',
                                     `re_scene_model_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的场景id',
                                     `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                     `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                     `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                     `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                     `remark` varchar(800) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '监控设备关联表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_sub_gateway
-- ----------------------------
DROP TABLE IF EXISTS `iot_sub_gateway`;
CREATE TABLE `iot_sub_gateway`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
                                    `gw_device_id` bigint(20) NOT NULL COMMENT '网关设备id',
                                    `sub_device_id` bigint(20) NOT NULL COMMENT '子设备id',
                                    `slave_id` int(6) NULL DEFAULT NULL COMMENT '从机地址',
                                    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '网关与子设备关联表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_things_model_template_translate
-- ----------------------------
DROP TABLE IF EXISTS `iot_things_model_template_translate`;
CREATE TABLE `iot_things_model_template_translate`  (
                                                        `id` bigint(20) NOT NULL COMMENT 'ID',
                                                        `zh_cn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zh_CN',
                                                        `en_us` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'en_US',
                                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型模板翻译表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for iot_things_model_translate
-- ----------------------------
DROP TABLE IF EXISTS `iot_things_model_translate`;
CREATE TABLE `iot_things_model_translate`  (
                                               `id` bigint(20) NOT NULL COMMENT 'ID',
                                               `zh_cn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zh_CN',
                                               `en_us` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'en_US',
                                               `product_id` bigint(20) DEFAULT NULL COMMENT '产品ID',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '物模型翻译表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_control
-- ----------------------------
DROP TABLE IF EXISTS `order_control`;
CREATE TABLE `order_control`  (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                  `tenant_id` bigint(20) NOT NULL COMMENT '租户id',
                                  `select_order` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '逗号分隔',
                                  `status` tinyint(1) NULL DEFAULT NULL COMMENT '是否生效 0-否 1-是',
                                  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '被限制的用户id',
                                  `device_id` bigint(20) NULL DEFAULT NULL COMMENT '设备id',
                                  `count` int(10) NULL DEFAULT NULL COMMENT '可操作次数',
                                  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
                                  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `file_path` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
                                  `img_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '图片路径',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '指令权限控制' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置', 2007, 1, 'preferences', '', NULL, 1, 0, 'M', '0', '0', 'order:preferences:list', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '指令偏好设置菜单');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:preferences:query', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:preferences:add', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:preferences:edit', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:preferences:remove', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令偏好设置导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:preferences:export', '#', 'admin', '2024-07-22 14:51:09', '', NULL, '');

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限', 3000, 1, 'control', 'iot/order/index', NULL, 1, 0, 'C', '0', '0', 'order:control:list', 'switch', 'admin', '2024-07-15 22:57:16', 'admin', '2024-07-18 13:56:56', '指令权限控制菜单');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限控制查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:control:query', '#', 'admin', '2024-07-15 22:57:16', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限控制新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:control:add', '#', 'admin', '2024-07-15 22:57:16', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限控制修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:control:edit', '#', 'admin', '2024-07-15 22:57:16', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限控制删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:control:remove', '#', 'admin', '2024-07-15 22:57:16', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('指令权限控制导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'order:control:export', '#', 'admin', '2024-07-15 22:57:16', '', NULL, '');


-- ----------------------------
-- Table structure for scene_model
-- ----------------------------
DROP TABLE IF EXISTS `scene_model`;
CREATE TABLE `scene_model`  (
                                `scene_model_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '场景管理id',
                                `tenant_id` bigint(20) NOT NULL COMMENT '所属租户id',
                                `scene_model_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '场景管理名称',
                                `status` int(1) NOT NULL DEFAULT 1 COMMENT '场景状态 0-停用 1-启用',
                                `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联的组态id',
                                `desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场景描述',
                                `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
                                PRIMARY KEY (`scene_model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '场景管理' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for scene_model_data
-- ----------------------------
DROP TABLE IF EXISTS `scene_model_data`;
CREATE TABLE `scene_model_data`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `scene_model_id` bigint(20) NULL DEFAULT NULL COMMENT '场景管理id',
                                     `scene_model_device_id` bigint(20) NULL DEFAULT NULL COMMENT '场景关联数据来源id',
                                     `variable_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '来源类型(0设备 1录入型 2运算型)',
                                     `datasource_id` bigint(20) NULL DEFAULT NULL COMMENT '物模型或变量id',
                                     `enable` tinyint(2) NOT NULL DEFAULT 1 COMMENT '启用（0未启用 1启用）',
                                     `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                     `source_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '物模型或变量名称',
                                     `identifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标识符',
                                     `type` tinyint(1) NULL DEFAULT NULL COMMENT '模型类别（1-属性，2-功能，3-事件）',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for scene_model_device
-- ----------------------------
DROP TABLE IF EXISTS `scene_model_device`;
CREATE TABLE `scene_model_device`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                       `scene_model_id` bigint(20) NOT NULL COMMENT '场景id',
                                       `cus_device_id` bigint(20) NULL DEFAULT NULL COMMENT '关联设备id',
                                       `sort` int(3) NULL DEFAULT NULL COMMENT '排序',
                                       `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                       `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                       `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                       `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                       `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                       `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                       `variable_type` tinyint(4) NOT NULL COMMENT '类型（0设备 1录入型 2运算型）',
                                       `all_enable` tinyint(2) NULL DEFAULT NULL COMMENT '全部启用（0否 1是）',
                                       `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '名称',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '场景管理关联设备' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for scene_model_tag
-- ----------------------------
DROP TABLE IF EXISTS `scene_model_tag`;
CREATE TABLE `scene_model_tag`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                    `scene_model_id` bigint(20) NOT NULL COMMENT '场景id',
                                    `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '录入型变量名',
                                    `unit` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '单位',
                                    `data_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '数据类型',
                                    `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '默认值',
                                    `is_readonly` int(1) NOT NULL DEFAULT 0 COMMENT '是否只读 0-否 1-是，默认0',
                                    `storage` int(1) NOT NULL DEFAULT 1 COMMENT '存储方式 0-不存储 1-存储',
                                    `variable_type` int(1) NULL DEFAULT NULL COMMENT '变量类型 2-录入型变量 3-运算型变量',
                                    `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                    `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    `cycle_executed` tinyint(4) NULL DEFAULT 0 COMMENT '周期执行（0未执行 1执行）',
                                    `formule` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '计算公式 ${id} + ${id}',
                                    `alias_formule` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '显示的计算公式  A+B',
                                    `cycle_type` int(2) NULL DEFAULT NULL COMMENT '时间周期方式 1-周期计算 2-自定义时间段',
                                    `cycle` json NULL COMMENT '时间周期内容',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '场景录入型变量' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for scene_tag_points
-- ----------------------------
DROP TABLE IF EXISTS `scene_tag_points`;
CREATE TABLE `scene_tag_points`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '运算型变量点id',
                                     `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '变量点名称',
                                     `alias` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '点别名，如 A',
                                     `tag_id` bigint(20) NOT NULL COMMENT '关联的变量id',
                                     `operation` int(2) NOT NULL COMMENT '统计方式 ，用字典定义，暂时是”原值“',
                                     `variable_type` int(2) NULL DEFAULT NULL COMMENT '数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量',
                                     `scene_model_data_id` bigint(20) NULL DEFAULT NULL COMMENT '数据源id,对应scene_model_data表id',
                                     `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                     `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                     `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '运算型变量点表' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景管理', 0, 1, 'scene', NULL, NULL, 1, 0, 'M', '0', '0', '', 'scene', 'admin', '2024-05-17 17:28:11', 'admin', '2024-05-23 11:43:31', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景列表', @parentId, 1, 'list/index', 'scene/list/index', '', 1, 0, 'C', '0', '0', 'scene:model:list', 'product', 'admin', '2024-05-17 17:33:46', 'admin', '2024-05-23 11:37:49', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('变量列表', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelData:list', '#', 'admin', '2024-07-29 14:40:54', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量启用', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelData:editEnable', '#', 'admin', '2024-07-29 10:28:14', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景列表查询', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'scene:model:query', '#', 'admin', '2024-05-20 00:54:36', 'admin', '2024-05-23 11:37:58', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景列表新增', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'scene:model:add', '#', 'admin', '2024-05-20 00:54:36', 'admin', '2024-05-23 11:38:05', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景列表修改', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'scene:model:edit', '#', 'admin', '2024-05-20 00:54:36', 'admin', '2024-05-23 11:38:10', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景列表删除', @parentId, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'scene:model:remove', '#', 'admin', '2024-05-20 00:54:37', 'admin', '2024-05-23 11:38:20', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景组态设计', @parentId, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:model:scada:design', '#', 'admin', '2024-05-23 16:08:28', 'admin', '2024-05-23 16:09:48', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景组态运行', @parentId, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:model:scada:run', '#', 'admin', '2024-05-23 16:10:15', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('视频监控', @parentId, 9, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:SipRelation:list', '#', 'admin', '2024-07-29 10:22:40', '', NULL, '');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备配置列表', 3316, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelDevice:list', '#', 'admin', '2024-07-29 10:06:31', 'admin', '2024-07-29 10:17:33', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备配置新增', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelDevice:add', '#', 'admin', '2024-07-29 10:07:00', 'admin', '2024-07-29 10:12:22', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备配置修改', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelDevice:edit', '#', 'admin', '2024-07-29 10:07:49', 'admin', '2024-07-29 10:12:29', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备配置删除', @parentId, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelDevice:remove', '#', 'admin', '2024-07-29 10:08:17', 'admin', '2024-07-29 10:12:32', '');

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备变量列表', 3316, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelDeviceData:list', '#', 'admin', '2024-07-29 10:12:17', '', NULL, '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量列表', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelTag:list', '#', 'admin', '2024-07-29 10:17:27', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量查看', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelTag:query', '#', 'admin', '2024-07-29 10:25:05', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量新增', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelTag:add', '#', 'admin', '2024-07-29 10:25:29', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量修改', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelTag:edit', '#', 'admin', '2024-07-29 10:26:47', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景变量删除', @parentId, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scene:modelTag:remove', '#', 'admin', '2024-07-29 10:27:11', '', NULL, '');


-- ----------------------------
-- Table structure for app_language
-- ----------------------------
DROP TABLE IF EXISTS `app_language`;
CREATE TABLE `app_language`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `language` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '语言',
                                 `country` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '国家',
                                 `time_zone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '时区',
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                 `lang_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '语言名称',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = 'app语言' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_language
-- ----------------------------
INSERT INTO `app_language` VALUES (2, 'zh-CN', '中国', 'UTF+8', 'UTC+8', NULL, '简体中文');
INSERT INTO `app_language` VALUES (3, 'en-US', '欧美', 'UTC', 'UTC', NULL, 'English');
INSERT INTO `app_language` VALUES (4, 'jp', '日本', 'utf-9', NULL, '2024-05-31 14:25:30', '日语');



-- ----------------------------
-- Table structure for app_preferences
-- ----------------------------
DROP TABLE IF EXISTS `app_preferences`;
CREATE TABLE `app_preferences`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                    `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户',
                                    `language` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '语言',
                                    `time_zone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '时区',
                                    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `PRIAMRK_USER_LANG`(`user_id`, `language`) USING BTREE COMMENT '用户语言唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = 'APP用户偏好设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_preferences
-- ----------------------------
INSERT INTO `app_preferences` VALUES (1, 1, 'zh-CN', NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置', 1, 2, 'appLang', 'system/app/lang', NULL, 1, 0, 'C', '0', '0', 'app:language:list', 'international', 'sunrain', '2024-05-20 14:15:35', 'sunrain', '2024-05-22 17:33:56', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置新增', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'app:language:add', '#', 'sunrain', '2024-05-20 14:16:11', 'sunrain', '2024-05-20 14:27:09', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置查询', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'app:language:query', '#', 'sunrain', '2024-05-20 14:16:40', 'sunrain', '2024-05-20 14:27:32', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置修改', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'app:language:edit', '#', 'sunrain', '2024-05-20 14:17:54', 'sunrain', '2024-05-20 14:27:21', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置删除', @parentId, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'app:language:remove', '#', 'sunrain', '2024-05-20 14:19:19', 'sunrain', '2024-05-20 14:27:41', '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国际化配置导出', @parentId, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'app:language:export', '#', 'sunrain', '2024-05-20 14:19:53', 'sunrain', '2024-05-20 14:27:48', '');


-- 数据中心菜单
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('数据中心', 0, 4, 'dataCenter', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'column-chart', 'admin', '2024-05-24 14:42:04', '', NULL, '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('数据分析', @parentId, 2, 'analysis', 'dataCenter/analysis', NULL, 1, 0, 'C', '0', '0', 'dataCenter:analysis:list', 'curve-chart', 'admin', '2024-06-11 15:46:19', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('历史记录', @parentId, 1, 'history', 'dataCenter/history', NULL, 1, 0, 'C', '0', '0', 'dataCenter:history:list', 'excel', 'admin', '2024-05-24 14:44:58', 'admin', '2024-05-24 14:46:07', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('历史记录查询', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'dataCenter:history:query', '#', 'admin', '2024-05-24 14:46:27', 'admin', '2024-05-24 14:47:26', '');

-- 组态菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('模板组态', 3159, 1, 'center/temp', 'scada/center/tempScada', NULL, 1, 0, 'C', '0', '0', 'scada:center:list', 'template', 'admin', '2024-03-01 11:38:48', 'admin', '2024-06-01 17:18:09', '组态中心菜单');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('场景组态', 3159, 1, 'center/scene', 'scada/center/sceneScada', NULL, 1, 0, 'C', '0', '0', 'scada:center:list', 'scene', 'admin', '2024-05-16 18:32:47', 'admin', '2024-07-24 15:43:38', '');

-- 修改组态中心菜单
update sys_menu
set menu_name = '独立组态',
    component = 'scada/center/indieScada'
where menu_id = 3160;

-- 移动组态中心下面的按钮权限
update sys_menu
set parent_id = 3159,
    order_num = 6
where parent_id = 3160;

-- 产品表sql更改
ALTER TABLE `iot_product`
    ADD COLUMN `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品关联的组态id';

-- 删除modbus定时
DELETE FROM sys_job WHERE job_id = 4;

-- 删除采集点模板相关
DELETE FROM sys_menu WHERE menu_id in (3013,3014,3015,3016,3017,3018,3019,3020,3021,3022,3023,3024,3025,3026,3027,3028,3029,3030);

-- 删除设备模拟
DELETE FROM sys_menu WHERE menu_id in (3039,3040,3041,3042,3043);

-- 定时表新增数据源id字段
ALTER TABLE `iot_device_job`
    ADD COLUMN `datasource_id` bigint(20) NULL COMMENT '执行id,可共用，通过jobType区分';

-- iot_things_model
ALTER TABLE `iot_things_model`
DROP COLUMN `temp_slave_id`,
DROP COLUMN `reverse_formula`,
DROP COLUMN `reg_addr`,
DROP COLUMN `bit_option`,
DROP COLUMN `value_type`,
DROP COLUMN `is_params`,
DROP COLUMN `quantity`,
DROP COLUMN `code`,
DROP COLUMN `parse_type`,
MODIFY COLUMN `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式';

-- iot_things_model_template
ALTER TABLE `iot_things_model_template`
DROP COLUMN `reverse_formula`,
DROP COLUMN `reg_addr`,
DROP COLUMN `bit_option`,
DROP COLUMN `value_type`,
DROP COLUMN `is_params`,
DROP COLUMN `quantity`,
DROP COLUMN `code`,
DROP COLUMN `old_identifier`,
DROP COLUMN `old_temp_slave_id`,
DROP COLUMN `parse_type`,
MODIFY COLUMN `formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计算公式';

-- iot_things_model
ALTER TABLE `iot_things_model`
ADD COLUMN `is_app` tinyint(1) NULL COMMENT '是否在APP显示(0-否，1-是)';

-- iot_things_model_template
ALTER TABLE `iot_things_model_template`
DROP COLUMN `temp_slave_id`,
ADD COLUMN `is_app` tinyint(1) NULL DEFAULT NULL COMMENT '是否在APP显示(0-否，1-是)';

-- 字典类型 ！！包含原2.4增加的
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('网关子设备通讯方式', 'sub_gateway_type', '0', 'admin', '2024-05-28 11:31:00', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('变量统计方式', 'variable_operation_type', '0', 'admin', '2024-05-29 14:39:14', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('时间周期间隔', 'variable_operation_interval', '0', 'admin', '2024-05-29 14:58:33', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('时间周期时间', 'variable_operation_time', '0', 'admin', '2024-05-29 15:23:45', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('时间周期周', 'variable_operation_week', '0', 'admin', '2024-05-29 15:40:33', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('时间周期日', 'variable_operation_day', '0', 'admin', '2024-05-29 15:49:03', '', NULL, NULL);
INSERT INTO `sys_dict_type`(`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备状态判断方式', 'device_status_deter', '0', 'admin', '2024-05-30 20:11:43', '', NULL, '用于网关子设备的状态判断');

-- 字典类型值--！！包含原2.4增加的
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '网关子设备', '4', 'iot_device_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 01:37:54', 'admin', '2024-05-28 10:55:33', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, 'WIFI', '1', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:31:19', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '以太网', '2', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:31:34', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'RS485', '3', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:31:46', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'RS232', '4', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:32:07', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'LoRa', '5', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:32:25', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'ZigBee', '6', 'sub_gateway_type', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:32:42', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'NB-IoT', '5', 'iot_network_method', NULL, 'default', 'N', '0', 'admin', '2024-05-28 11:35:14', 'admin', '2024-05-28 11:35:26', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '原值', '1', 'variable_operation_type', NULL, 'default', 'N', '0', 'admin', '2024-05-29 14:40:15', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '累计值', '2', 'variable_operation_type', NULL, 'default', 'N', '0', 'admin', '2024-05-29 14:40:50', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '平均值', '3', 'variable_operation_type', NULL, 'default', 'N', '0', 'admin', '2024-05-29 14:41:06', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '最大值', '4', 'variable_operation_type', NULL, 'default', 'N', '0', 'admin', '2024-05-29 14:41:18', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '最小值', '5', 'variable_operation_type', NULL, 'default', 'N', '0', 'admin', '2024-05-29 14:41:32', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '1分钟', '60', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:00:20', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '3分钟', '180', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:00:59', 'admin', '2024-05-29 15:01:52', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '5分钟', '300', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:01:16', 'admin', '2024-05-29 15:01:44', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '10分钟', '600', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:02:22', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '15分钟', '900', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:02:47', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '20分钟', '1200', 'variable_operation_interval', '', 'default', 'N', '0', 'admin', '2024-05-29 15:03:11', 'admin', '2024-05-29 15:03:30', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '30分钟', '1800', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:03:56', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '5小时', '18000', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:04:56', 'admin', '2024-05-29 15:05:08', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '时', 'hour', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:06:31', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, '日', 'day', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:07:54', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '周', 'week', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:08:19', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, '月', 'month', 'variable_operation_interval', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:08:32', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '00时', '00', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:24:20', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '01时', '01', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:24:31', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '02时', '02', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:24:58', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '03时', '03', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:28:14', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '04时', '04', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:28:33', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '05时', '05', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:28:48', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '06时', '06', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:29:10', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '07时', '07', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:29:24', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '08时', '08', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:30:50', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, '09时', '09', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:31:03', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '10时', '10', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:31:20', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, '11时', '11', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:33:42', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, '12时', '12', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:34:00', 'admin', '2024-05-29 15:34:27', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, '13时', '13', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:34:12', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, '14时', '14', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:34:39', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, '15时', '15', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:34:52', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, '16时', '16', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:35:04', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, '17时', '17', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:35:36', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, '18时', '18', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:35:51', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, '19时', '19', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:36:03', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20, '20时', '20', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:36:18', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, '21时', '21', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:36:33', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, '22时', '22', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:36:44', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, '23时', '23', 'variable_operation_time', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:36:54', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '周一', '1', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:41:27', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '周二', '2', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:41:35', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '周三', '3', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:41:47', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '周四', '4', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:42:01', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '周五', '5', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:42:14', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '周六', '6', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:42:25', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '周日', '7', 'variable_operation_week', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:42:41', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '1日', '1', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:49:28', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '2日', '2', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:49:38', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '3日', '3', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:51:48', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '4日', '4', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:52:18', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '5日', '5', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:52:28', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '6日', '6', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:52:40', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '7日', '7', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:52:52', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '8日', '8', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:53:01', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '9日', '9', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:53:17', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, '10日', '10', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:53:34', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '11日', '11', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:53:48', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, '12日', '12', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:54:02', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, '13日', '13', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:54:11', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, '14日', '14', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:54:23', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, '15日', '15', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:54:39', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, '16日', '16', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:54:53', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, '17日', '17', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:55:22', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, '18日', '18', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:55:35', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, '19日', '19', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:55:52', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, '20日', '20', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:56:10', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20, '21日', '21', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:56:23', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, '22日', '22', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:56:40', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, '23日', '23', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:56:57', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, '24日', '24', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:57:17', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '25日', '25', 'variable_operation_day', '24', 'default', 'N', '0', 'admin', '2024-05-29 15:57:28', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, '26日', '26', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:57:41', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (24, '25日', '25', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:57:59', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, '27日', '27', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:58:16', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, '28日', '28', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:58:39', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (28, '29日', '29', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:58:53', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, '30日', '30', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:59:10', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (30, '31日', '31', 'variable_operation_day', NULL, 'default', 'N', '0', 'admin', '2024-05-29 15:59:37', '', NULL, NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (0, '设备数据', '1', 'device_status_deter', NULL, 'default', 'N', '0', 'admin', '2024-05-30 20:12:04', 'admin', '2024-06-27 00:44:49', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '网关', '2', 'device_status_deter', NULL, 'default', 'N', '0', 'admin', '2024-05-30 20:12:17', 'admin', '2024-06-27 00:44:53', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '10分钟', '600', 'iot_modbus_poll_time', NULL, 'default', 'N', '0', 'admin', '2024-05-30 20:20:56', 'admin', '2024-05-30 20:21:07', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'http接入', '5', 'rule_script_event', NULL, 'primary', 'N', '0', 'admin', '2024-07-22 11:56:41', 'admin', '2024-07-22 11:57:20', NULL);
INSERT INTO `sys_dict_data`(`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'mqtt接入', '6', 'rule_script_event', NULL, 'primary', 'N', '0', 'admin', '2024-07-22 11:57:36', '', NULL, NULL);

-- 新增公共状态字典
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('公共状态', 'common_status_type', '0', 'admin', '2024-07-18 17:48:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (0, '失败', 'o', 'common_status_type', NULL, 'default', 'N', '0', 'admin', '2024-07-18 17:48:57', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (1, '成功', '1', 'common_status_type', NULL, 'default', 'N', '0', 'admin', '2024-07-18 17:49:07', '', NULL, NULL);
-- ----------------------------
-- Table structure for sys_dict_data_translate
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data_translate`;
CREATE TABLE `sys_dict_data_translate`  (
                                            `id` bigint(20) NOT NULL COMMENT 'ID',
                                            `zh_cn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zh_CN',
                                            `en_us` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'en_US',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典数据翻译表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data_translate
-- ----------------------------
INSERT INTO `sys_dict_data_translate` VALUES (1, '男', 'man');
INSERT INTO `sys_dict_data_translate` VALUES (2, '女', 'woman');
INSERT INTO `sys_dict_data_translate` VALUES (3, '未知', 'Unknown');
INSERT INTO `sys_dict_data_translate` VALUES (4, '显示', 'display');
INSERT INTO `sys_dict_data_translate` VALUES (5, '隐藏', 'hide');
INSERT INTO `sys_dict_data_translate` VALUES (6, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` VALUES (7, '停用', 'Deactivated');
INSERT INTO `sys_dict_data_translate` VALUES (8, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` VALUES (9, '暂停', 'Time out');
INSERT INTO `sys_dict_data_translate` VALUES (10, '默认', 'default');
INSERT INTO `sys_dict_data_translate` VALUES (11, '系统', 'system');
INSERT INTO `sys_dict_data_translate` VALUES (12, '是', 'be');
INSERT INTO `sys_dict_data_translate` VALUES (13, '否', 'not');
INSERT INTO `sys_dict_data_translate` VALUES (14, '通知', 'notice');
INSERT INTO `sys_dict_data_translate` VALUES (15, '公告', 'announcement');
INSERT INTO `sys_dict_data_translate` VALUES (16, '正常', 'normal');
INSERT INTO `sys_dict_data_translate` VALUES (17, '关闭', 'Shut down');
INSERT INTO `sys_dict_data_translate` VALUES (18, '新增', 'New');
INSERT INTO `sys_dict_data_translate` VALUES (19, '修改', 'revise');
INSERT INTO `sys_dict_data_translate` VALUES (20, '删除', 'Delete');
INSERT INTO `sys_dict_data_translate` VALUES (21, '授权', 'authorization');
INSERT INTO `sys_dict_data_translate` VALUES (22, '导出', 'Export');
INSERT INTO `sys_dict_data_translate` VALUES (23, '导入', 'Import');
INSERT INTO `sys_dict_data_translate` VALUES (24, '强退', 'Forced retreat');
INSERT INTO `sys_dict_data_translate` VALUES (25, '生成代码', 'Generate code');
INSERT INTO `sys_dict_data_translate` VALUES (26, '清空数据', 'Clear the data');
INSERT INTO `sys_dict_data_translate` VALUES (27, '成功', 'succeed');
INSERT INTO `sys_dict_data_translate` VALUES (28, '失败', 'fail');
INSERT INTO `sys_dict_data_translate` VALUES (100, '属性', 'attribute');
INSERT INTO `sys_dict_data_translate` VALUES (101, '功能', 'function');
INSERT INTO `sys_dict_data_translate` VALUES (102, '事件', 'event');
INSERT INTO `sys_dict_data_translate` VALUES (103, '整数', 'integer');
INSERT INTO `sys_dict_data_translate` VALUES (104, '小数', 'decimal fraction');
INSERT INTO `sys_dict_data_translate` VALUES (105, '布尔', 'Boolean');
INSERT INTO `sys_dict_data_translate` VALUES (106, '枚举', 'enumerate');
INSERT INTO `sys_dict_data_translate` VALUES (107, '字符串', 'string');
INSERT INTO `sys_dict_data_translate` VALUES (108, '是', 'be');
INSERT INTO `sys_dict_data_translate` VALUES (109, '否', 'not');
INSERT INTO `sys_dict_data_translate` VALUES (110, '数组', 'array');
INSERT INTO `sys_dict_data_translate` VALUES (111, '未发布', 'Unpublished');
INSERT INTO `sys_dict_data_translate` VALUES (112, '已发布', 'Published');
INSERT INTO `sys_dict_data_translate` VALUES (113, '直连设备', 'Directly connected devices');
INSERT INTO `sys_dict_data_translate` VALUES (114, '网关设备', 'Gateway devices');
INSERT INTO `sys_dict_data_translate` VALUES (116, 'WIFI', 'WIFI');
INSERT INTO `sys_dict_data_translate` VALUES (117, '蜂窝(2G/3G/4G/5G)', 'Cellular (2G/3G/4G/5G)');
INSERT INTO `sys_dict_data_translate` VALUES (118, '以太网', 'Ethernet');
INSERT INTO `sys_dict_data_translate` VALUES (119, '简单认证', 'Simple authentication');
INSERT INTO `sys_dict_data_translate` VALUES (120, '加密认证', 'Cryptographic authentication');
INSERT INTO `sys_dict_data_translate` VALUES (122, 'ESP8266/Arduino', 'ESP8266/Arduino');
INSERT INTO `sys_dict_data_translate` VALUES (123, 'ESP32/Arduino', 'ESP32/Arduino');
INSERT INTO `sys_dict_data_translate` VALUES (124, 'ESP8266/RTOS', 'ESP8266/RTOS');
INSERT INTO `sys_dict_data_translate` VALUES (127, 'ESP32/ESP-IDF', 'ESP32/ESP-IDF');
INSERT INTO `sys_dict_data_translate` VALUES (128, '树莓派/Python', 'Raspberry Pi/Python');
INSERT INTO `sys_dict_data_translate` VALUES (129, '未激活', 'Not activated');
INSERT INTO `sys_dict_data_translate` VALUES (130, '禁用', 'disable');
INSERT INTO `sys_dict_data_translate` VALUES (131, '在线', 'online');
INSERT INTO `sys_dict_data_translate` VALUES (132, '离线', 'offline');
INSERT INTO `sys_dict_data_translate` VALUES (133, '启用', 'enable');
INSERT INTO `sys_dict_data_translate` VALUES (134, '禁用', 'disable');
INSERT INTO `sys_dict_data_translate` VALUES (135, '提醒通知', 'Reminder notifications');
INSERT INTO `sys_dict_data_translate` VALUES (136, '轻微问题', 'Minor issues');
INSERT INTO `sys_dict_data_translate` VALUES (137, '严重警告', 'Serious warning');
INSERT INTO `sys_dict_data_translate` VALUES (138, '不需要处理', 'No processing is required');
INSERT INTO `sys_dict_data_translate` VALUES (139, '未处理', 'Not processed');
INSERT INTO `sys_dict_data_translate` VALUES (140, '已处理', 'Processed');
INSERT INTO `sys_dict_data_translate` VALUES (141, '属性上报', 'Attribute escalation');
INSERT INTO `sys_dict_data_translate` VALUES (142, '事件上报', 'Event escalation');
INSERT INTO `sys_dict_data_translate` VALUES (143, '功能调用', 'Function calls');
INSERT INTO `sys_dict_data_translate` VALUES (144, '设备升级', 'Equipment upgrades');
INSERT INTO `sys_dict_data_translate` VALUES (145, '设备上线', 'The device goes online');
INSERT INTO `sys_dict_data_translate` VALUES (146, '设备离线', 'The device is offline');
INSERT INTO `sys_dict_data_translate` VALUES (147, '其他', 'other');
INSERT INTO `sys_dict_data_translate` VALUES (148, '安卓/Android', 'Android/Android');
INSERT INTO `sys_dict_data_translate` VALUES (149, '其他', 'other');
INSERT INTO `sys_dict_data_translate` VALUES (150, '小度平台', 'Small platform');
INSERT INTO `sys_dict_data_translate` VALUES (151, '天猫精灵', 'Tmall Genie');
INSERT INTO `sys_dict_data_translate` VALUES (152, '小米小爱', 'Xiaomi Xiaoai');
INSERT INTO `sys_dict_data_translate` VALUES (153, '其他平台', 'Other platforms');
INSERT INTO `sys_dict_data_translate` VALUES (154, '微信登录', 'WeChat login');
INSERT INTO `sys_dict_data_translate` VALUES (155, 'QQ登录', 'QQ login');
INSERT INTO `sys_dict_data_translate` VALUES (156, '启用', 'enable');
INSERT INTO `sys_dict_data_translate` VALUES (157, '未启用', 'Not enabled');
INSERT INTO `sys_dict_data_translate` VALUES (158, '支付宝', 'Alipay');
INSERT INTO `sys_dict_data_translate` VALUES (159, '自动定位', 'Auto-target');
INSERT INTO `sys_dict_data_translate` VALUES (160, '设备定位', 'Device positioning');
INSERT INTO `sys_dict_data_translate` VALUES (161, '自定义位置', 'Custom location');
INSERT INTO `sys_dict_data_translate` VALUES (162, '简单+加密', 'Simple + Encrypted');
INSERT INTO `sys_dict_data_translate` VALUES (163, '未使用', 'Unused');
INSERT INTO `sys_dict_data_translate` VALUES (164, '已使用', 'Used');
INSERT INTO `sys_dict_data_translate` VALUES (165, '对象', 'object');
INSERT INTO `sys_dict_data_translate` VALUES (166, '监控设备', 'Monitor the device');
INSERT INTO `sys_dict_data_translate` VALUES (167, '未使用', 'Unused');
INSERT INTO `sys_dict_data_translate` VALUES (168, '在线', 'online');
INSERT INTO `sys_dict_data_translate` VALUES (169, 'DVR', 'DVR');
INSERT INTO `sys_dict_data_translate` VALUES (170, 'NVR', 'NVR');
INSERT INTO `sys_dict_data_translate` VALUES (171, '报警控制器', 'Alarm controller');
INSERT INTO `sys_dict_data_translate` VALUES (172, '摄像机', 'camera');
INSERT INTO `sys_dict_data_translate` VALUES (173, 'IPC', 'IPC');
INSERT INTO `sys_dict_data_translate` VALUES (174, '显示器', 'display');
INSERT INTO `sys_dict_data_translate` VALUES (175, '报警输入设备', 'Alarm input device');
INSERT INTO `sys_dict_data_translate` VALUES (176, '报警输出设备', 'Alarm output device');
INSERT INTO `sys_dict_data_translate` VALUES (177, '语音输入设备', 'Voice input devices');
INSERT INTO `sys_dict_data_translate` VALUES (178, '语音输出设备', 'Voice output devices');
INSERT INTO `sys_dict_data_translate` VALUES (179, '移动传输设备', 'Mobile Transmission Devices');
INSERT INTO `sys_dict_data_translate` VALUES (180, '报警控制器', 'Alarm controller');
INSERT INTO `sys_dict_data_translate` VALUES (181, '摄像机', 'camera');
INSERT INTO `sys_dict_data_translate` VALUES (182, 'IPC', 'IPC');
INSERT INTO `sys_dict_data_translate` VALUES (183, '显示器', 'display');
INSERT INTO `sys_dict_data_translate` VALUES (184, '报警输入设备', 'Alarm input device');
INSERT INTO `sys_dict_data_translate` VALUES (185, '报警输出设备', 'Alarm output device');
INSERT INTO `sys_dict_data_translate` VALUES (186, '语音输入设备', 'Voice input devices');
INSERT INTO `sys_dict_data_translate` VALUES (187, '语音输出设备', 'Voice output devices');
INSERT INTO `sys_dict_data_translate` VALUES (188, '移动传输设备', 'Mobile Transmission Devices');
INSERT INTO `sys_dict_data_translate` VALUES (189, '离线', 'offline');
INSERT INTO `sys_dict_data_translate` VALUES (190, '禁用', 'disable');
INSERT INTO `sys_dict_data_translate` VALUES (191, '云端轮询', 'Cloud polling');
INSERT INTO `sys_dict_data_translate` VALUES (192, '边缘采集', 'Edge acquisition');
INSERT INTO `sys_dict_data_translate` VALUES (193, '1分钟', '1 minute');
INSERT INTO `sys_dict_data_translate` VALUES (194, '2分钟', '2 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (195, '5分钟', '5 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (196, '03(读保持寄存器)', '03 (Read Hold Register)');
INSERT INTO `sys_dict_data_translate` VALUES (197, '01(读线圈)', '01 (Reading coil)');
INSERT INTO `sys_dict_data_translate` VALUES (198, 'MQTT', 'MQTT');
INSERT INTO `sys_dict_data_translate` VALUES (199, 'TCP', 'TCP');
INSERT INTO `sys_dict_data_translate` VALUES (200, 'COAP', 'COAP');
INSERT INTO `sys_dict_data_translate` VALUES (201, 'UDP', 'UDP');
INSERT INTO `sys_dict_data_translate` VALUES (230, '其他', 'other');
INSERT INTO `sys_dict_data_translate` VALUES (231, '事件上报', 'Event escalation');
INSERT INTO `sys_dict_data_translate` VALUES (232, '设备上线', 'The device goes online');
INSERT INTO `sys_dict_data_translate` VALUES (233, '设备离线', 'The device is offline');
INSERT INTO `sys_dict_data_translate` VALUES (234, '服务下发', 'Service delivery');
INSERT INTO `sys_dict_data_translate` VALUES (235, '属性获取', 'Attribute acquisition');
INSERT INTO `sys_dict_data_translate` VALUES (236, 'OTA升级', 'OTA upgrades');
INSERT INTO `sys_dict_data_translate` VALUES (237, '读写', 'Read and write');
INSERT INTO `sys_dict_data_translate` VALUES (238, '只读', 'read only');
INSERT INTO `sys_dict_data_translate` VALUES (239, '全部设备', 'All equipment');
INSERT INTO `sys_dict_data_translate` VALUES (240, '指定设备', 'Specify the device');
INSERT INTO `sys_dict_data_translate` VALUES (241, 'GB28181', 'GB28181');
INSERT INTO `sys_dict_data_translate` VALUES (242, '02(读离散量输入)', '02 (Read Discrete Input)');
INSERT INTO `sys_dict_data_translate` VALUES (243, '04(读输入寄存器)', '04 (Read Input Register)');
INSERT INTO `sys_dict_data_translate` VALUES (244, '微信开放平台网站应用', 'WeChat open platform website application');
INSERT INTO `sys_dict_data_translate` VALUES (245, '微信开放平台移动应用', 'WeChat open platform mobile application');
INSERT INTO `sys_dict_data_translate` VALUES (246, '微信开放平台小程序', 'WeChat Open Platform Mini Program');
INSERT INTO `sys_dict_data_translate` VALUES (247, '微信开放平台网站应用个人中心绑定', 'WeChat open platform website application personal center binding');
INSERT INTO `sys_dict_data_translate` VALUES (248, '16位 无符号', '16-bit unsigned');
INSERT INTO `sys_dict_data_translate` VALUES (249, '16位 有符号', '16-bit signed');
INSERT INTO `sys_dict_data_translate` VALUES (250, '32位 有符号(ABCD)', '32-bit Signed (ABCD)');
INSERT INTO `sys_dict_data_translate` VALUES (251, '32位 有符号(CDAB)', '32-bit Signed (CDAB)');
INSERT INTO `sys_dict_data_translate` VALUES (252, '32位 无符号(ABCD)', '32-bit Unsigned (ABCD)');
INSERT INTO `sys_dict_data_translate` VALUES (253, '32位 无符号(CDAB)', '32-bit Unsigned (CDAB)');
INSERT INTO `sys_dict_data_translate` VALUES (254, '32位 浮点数(ABCD)', '32-bit floating-point (ABCD)');
INSERT INTO `sys_dict_data_translate` VALUES (255, '32位 浮点数(CDAB)', '32-bit Floating-Point (CDAB)');
INSERT INTO `sys_dict_data_translate` VALUES (257, 'Java脚本引擎', 'Java Scripting Engine');
INSERT INTO `sys_dict_data_translate` VALUES (258, 'JavaScript脚本引擎', 'JavaScript scripting engine');
INSERT INTO `sys_dict_data_translate` VALUES (259, '普通脚本节点', 'Normal script nodes');
INSERT INTO `sys_dict_data_translate` VALUES (260, '选择脚本节点 (switch)', 'Selecting a Script Node (switch)');
INSERT INTO `sys_dict_data_translate` VALUES (261, '条件脚本节点 (if)', 'Conditional Script Node (if)');
INSERT INTO `sys_dict_data_translate` VALUES (262, '数量循环节点 (for)', 'Quantity Cycle Node (for)');
INSERT INTO `sys_dict_data_translate` VALUES (263, '条件循环节点 (while)', 'Conditional Loop Node (while)');
INSERT INTO `sys_dict_data_translate` VALUES (264, '退出循环节点 (break)', 'Exit Loop Node (Break)');
INSERT INTO `sys_dict_data_translate` VALUES (265, 'groovy脚本引擎', 'Groovy scripting engine');
INSERT INTO `sys_dict_data_translate` VALUES (266, '其他', 'other');
INSERT INTO `sys_dict_data_translate` VALUES (267, '电灯', 'lamp');
INSERT INTO `sys_dict_data_translate` VALUES (268, '空调', 'air conditioning');
INSERT INTO `sys_dict_data_translate` VALUES (269, '窗帘', 'curtain');
INSERT INTO `sys_dict_data_translate` VALUES (270, '窗纱', 'Screens');
INSERT INTO `sys_dict_data_translate` VALUES (271, '插座', 'receptacle');
INSERT INTO `sys_dict_data_translate` VALUES (272, '开关', 'switch');
INSERT INTO `sys_dict_data_translate` VALUES (273, '冰箱', 'Refrigerator');
INSERT INTO `sys_dict_data_translate` VALUES (274, '净水器', 'Water purifiers');
INSERT INTO `sys_dict_data_translate` VALUES (275, '加湿器', 'Humidifier');
INSERT INTO `sys_dict_data_translate` VALUES (276, '除湿器', 'Dehumidifiers');
INSERT INTO `sys_dict_data_translate` VALUES (277, '电磁炉', 'Induction');
INSERT INTO `sys_dict_data_translate` VALUES (278, '空气净化器', 'air purifier');
INSERT INTO `sys_dict_data_translate` VALUES (279, '洗衣机', 'washer');
INSERT INTO `sys_dict_data_translate` VALUES (280, '热水器', 'water heater');
INSERT INTO `sys_dict_data_translate` VALUES (281, '燃气灶', 'Gas stoves');
INSERT INTO `sys_dict_data_translate` VALUES (282, '电视机', 'television set');
INSERT INTO `sys_dict_data_translate` VALUES (283, '网络盒子', 'Cyberbox');
INSERT INTO `sys_dict_data_translate` VALUES (284, '油烟机', 'Range hood');
INSERT INTO `sys_dict_data_translate` VALUES (285, '电风扇', 'fan');
INSERT INTO `sys_dict_data_translate` VALUES (286, '投影仪', 'Projector');
INSERT INTO `sys_dict_data_translate` VALUES (287, '扫地机器人', 'Robot vacuums');
INSERT INTO `sys_dict_data_translate` VALUES (288, '热水壶', 'Kettle');
INSERT INTO `sys_dict_data_translate` VALUES (289, '微波炉', 'microwave oven');
INSERT INTO `sys_dict_data_translate` VALUES (290, '压力锅', 'pressure cooker');
INSERT INTO `sys_dict_data_translate` VALUES (291, '电饭煲', 'rice cooker');
INSERT INTO `sys_dict_data_translate` VALUES (292, '破壁机', 'Wall breaker');
INSERT INTO `sys_dict_data_translate` VALUES (293, '新风机', 'Fresh air fan');
INSERT INTO `sys_dict_data_translate` VALUES (294, '晾衣架', 'Drying racks');
INSERT INTO `sys_dict_data_translate` VALUES (295, '烤箱设备', 'Oven equipment');
INSERT INTO `sys_dict_data_translate` VALUES (296, '蒸烤箱', 'Steam oven');
INSERT INTO `sys_dict_data_translate` VALUES (297, '蒸箱', 'Steamer');
INSERT INTO `sys_dict_data_translate` VALUES (298, '电暖器', 'Electric heaters');
INSERT INTO `sys_dict_data_translate` VALUES (299, '开窗器', 'Window opener');
INSERT INTO `sys_dict_data_translate` VALUES (300, '摄像头', 'Camera');
INSERT INTO `sys_dict_data_translate` VALUES (301, '相机', 'camera');
INSERT INTO `sys_dict_data_translate` VALUES (302, '机器人', 'robot');
INSERT INTO `sys_dict_data_translate` VALUES (303, '打印机', 'printer');
INSERT INTO `sys_dict_data_translate` VALUES (304, '饮水机', 'Water dispenser');
INSERT INTO `sys_dict_data_translate` VALUES (305, '鱼缸', 'fish tank');
INSERT INTO `sys_dict_data_translate` VALUES (306, '浇花器', 'Flower waterer');
INSERT INTO `sys_dict_data_translate` VALUES (307, '机顶盒', 'Stb');
INSERT INTO `sys_dict_data_translate` VALUES (308, '香薰机', 'Aroma diffuser');
INSERT INTO `sys_dict_data_translate` VALUES (309, 'DVD', 'DVD');
INSERT INTO `sys_dict_data_translate` VALUES (310, '鞋柜', 'Shoe cabinet');
INSERT INTO `sys_dict_data_translate` VALUES (311, '走步机', 'Walking machine');
INSERT INTO `sys_dict_data_translate` VALUES (312, '跑步机', 'Treadmill');
INSERT INTO `sys_dict_data_translate` VALUES (313, '床', 'bed');
INSERT INTO `sys_dict_data_translate` VALUES (314, '浴霸', 'Bath bombs');
INSERT INTO `sys_dict_data_translate` VALUES (315, '花洒', 'Showerhead');
INSERT INTO `sys_dict_data_translate` VALUES (316, '浴缸', 'bathtub');
INSERT INTO `sys_dict_data_translate` VALUES (317, '消毒柜', 'Sterilizer');
INSERT INTO `sys_dict_data_translate` VALUES (318, '洗碗机', 'dishwasher');
INSERT INTO `sys_dict_data_translate` VALUES (319, '沙发品类', 'Sofa category');
INSERT INTO `sys_dict_data_translate` VALUES (320, '门铃', 'doorbell');
INSERT INTO `sys_dict_data_translate` VALUES (321, '电梯', 'lift');
INSERT INTO `sys_dict_data_translate` VALUES (322, '体重秤', 'Scales');
INSERT INTO `sys_dict_data_translate` VALUES (323, '体脂秤', 'Body fat scales');
INSERT INTO `sys_dict_data_translate` VALUES (324, '壁挂炉', 'Wall-hung boiler');
INSERT INTO `sys_dict_data_translate` VALUES (325, '特定设备的组合场景', 'A combination of device-specific scenarios');
INSERT INTO `sys_dict_data_translate` VALUES (326, '特定设备的组合场景', 'A combination of device-specific scenarios');
INSERT INTO `sys_dict_data_translate` VALUES (327, '打开', 'Open it');
INSERT INTO `sys_dict_data_translate` VALUES (328, '关闭', 'Shut down');
INSERT INTO `sys_dict_data_translate` VALUES (329, '定时打开', 'Turn on at a timer');
INSERT INTO `sys_dict_data_translate` VALUES (330, '定时关闭', 'Timed shutdown');
INSERT INTO `sys_dict_data_translate` VALUES (331, '查询开关状态', 'Query the switching status');
INSERT INTO `sys_dict_data_translate` VALUES (332, '设备启动', 'The device boots up');
INSERT INTO `sys_dict_data_translate` VALUES (333, '设置灯光亮度', 'Set the brightness of the light');
INSERT INTO `sys_dict_data_translate` VALUES (334, '调亮灯光', 'Turn the lights on');
INSERT INTO `sys_dict_data_translate` VALUES (335, '调暗灯光', 'Dim the lights');
INSERT INTO `sys_dict_data_translate` VALUES (336, '升高温度', 'Increase the temperature');
INSERT INTO `sys_dict_data_translate` VALUES (337, '降低温度', 'Reduce the temperature');
INSERT INTO `sys_dict_data_translate` VALUES (338, '设置温度', 'Set the temperature');
INSERT INTO `sys_dict_data_translate` VALUES (339, '查询温度（当前温度和目标温度）', 'Query temperature (current temperature and target temperature)');
INSERT INTO `sys_dict_data_translate` VALUES (340, '查询当前温度', 'Query the current temperature');
INSERT INTO `sys_dict_data_translate` VALUES (341, '查询目标温度', 'Query the target temperature');
INSERT INTO `sys_dict_data_translate` VALUES (342, '设置湿度模式', 'Set the humidity mode');
INSERT INTO `sys_dict_data_translate` VALUES (343, '增大湿度', 'Increase humidity');
INSERT INTO `sys_dict_data_translate` VALUES (344, '降低湿度', 'Reduce humidity');
INSERT INTO `sys_dict_data_translate` VALUES (345, '查询湿度', 'Check the humidity');
INSERT INTO `sys_dict_data_translate` VALUES (346, '查询目标湿度', 'Query the target humidity');
INSERT INTO `sys_dict_data_translate` VALUES (347, '查询二氧化碳含量', 'Check the carbon dioxide content');
INSERT INTO `sys_dict_data_translate` VALUES (348, '暂停', 'Time out');
INSERT INTO `sys_dict_data_translate` VALUES (349, '继续', 'Go on');
INSERT INTO `sys_dict_data_translate` VALUES (350, '调高音量', 'Turn up the volume');
INSERT INTO `sys_dict_data_translate` VALUES (351, '调低音量', 'Turn down the volume');
INSERT INTO `sys_dict_data_translate` VALUES (352, '设置音量', 'Set the volume');
INSERT INTO `sys_dict_data_translate` VALUES (353, '设置静音状态', 'Set the mute status');
INSERT INTO `sys_dict_data_translate` VALUES (354, '查询设备所在位置', 'Query the location of the device');
INSERT INTO `sys_dict_data_translate` VALUES (355, '设置清扫位置', 'Set the sweeping position');
INSERT INTO `sys_dict_data_translate` VALUES (356, '上锁/解锁', 'Locked/unlocked');
INSERT INTO `sys_dict_data_translate` VALUES (357, '设置颜色', 'Set the color');
INSERT INTO `sys_dict_data_translate` VALUES (358, '查询电量', 'Query the battery level');
INSERT INTO `sys_dict_data_translate` VALUES (359, '开始充电', 'Start charging');
INSERT INTO `sys_dict_data_translate` VALUES (360, '停止充电', 'Stop charging');
INSERT INTO `sys_dict_data_translate` VALUES (361, '设置灯光色温', 'Set the color temperature of the light');
INSERT INTO `sys_dict_data_translate` VALUES (362, '增高灯光色温', 'Increase the color temperature of the light');
INSERT INTO `sys_dict_data_translate` VALUES (363, '降低灯光色温', 'Reduce the color temperature of the light');
INSERT INTO `sys_dict_data_translate` VALUES (364, '查询风速', 'Query the wind speed');
INSERT INTO `sys_dict_data_translate` VALUES (365, '设置风速', 'Set the wind speed');
INSERT INTO `sys_dict_data_translate` VALUES (366, '增加风速', 'Increase wind speed');
INSERT INTO `sys_dict_data_translate` VALUES (367, '减小风速', 'Reduce wind speed');
INSERT INTO `sys_dict_data_translate` VALUES (368, '查询空气质量', 'Check the air quality');
INSERT INTO `sys_dict_data_translate` VALUES (369, '设置吸力', 'Set the suction');
INSERT INTO `sys_dict_data_translate` VALUES (370, '设置水量', 'Set the amount of water');
INSERT INTO `sys_dict_data_translate` VALUES (371, '上一个频道', 'Previous channel');
INSERT INTO `sys_dict_data_translate` VALUES (372, '下一个频道', 'Next channel');
INSERT INTO `sys_dict_data_translate` VALUES (373, '设置频道', 'Set up a channel');
INSERT INTO `sys_dict_data_translate` VALUES (374, '返回上个频道', 'Go back to the previous channel');
INSERT INTO `sys_dict_data_translate` VALUES (375, '设置移动方向', 'Set the direction of movement');
INSERT INTO `sys_dict_data_translate` VALUES (376, '设置模式', 'Set the mode');
INSERT INTO `sys_dict_data_translate` VALUES (377, '取消设置的模式', 'Cancel the set mode');
INSERT INTO `sys_dict_data_translate` VALUES (378, '定时设置模式', 'Timed setting mode');
INSERT INTO `sys_dict_data_translate` VALUES (379, '定时取消设置的模式', 'The mode of scheduled unsetting');
INSERT INTO `sys_dict_data_translate` VALUES (380, '查询水质', 'Check the water quality');
INSERT INTO `sys_dict_data_translate` VALUES (381, '设置电梯楼层', 'Set up elevator floors');
INSERT INTO `sys_dict_data_translate` VALUES (382, '电梯按下', 'The elevator presses');
INSERT INTO `sys_dict_data_translate` VALUES (383, '电梯按上', 'The elevator presses on');
INSERT INTO `sys_dict_data_translate` VALUES (384, '升高高度', 'Raise height');
INSERT INTO `sys_dict_data_translate` VALUES (385, '降低高度', 'Lower the height');
INSERT INTO `sys_dict_data_translate` VALUES (386, '查询运行时间', 'Query the run time');
INSERT INTO `sys_dict_data_translate` VALUES (387, '查询剩余时间', 'Query the remaining time');
INSERT INTO `sys_dict_data_translate` VALUES (388, '查询运行状态', 'Query the running status');
INSERT INTO `sys_dict_data_translate` VALUES (389, '增加速度', 'Increase speed');
INSERT INTO `sys_dict_data_translate` VALUES (390, '降低速度', 'Reduce the speed');
INSERT INTO `sys_dict_data_translate` VALUES (391, '设置速度', 'Set the speed');
INSERT INTO `sys_dict_data_translate` VALUES (392, '获取速度', 'Get speed');
INSERT INTO `sys_dict_data_translate` VALUES (393, '设置档位', 'Set the gear');
INSERT INTO `sys_dict_data_translate` VALUES (394, '查询设备所有状态', 'Query all device statuses');
INSERT INTO `sys_dict_data_translate` VALUES (395, '查询PM2.5', 'Check PM2. 5');
INSERT INTO `sys_dict_data_translate` VALUES (396, '查询PM10', 'Query PM10');
INSERT INTO `sys_dict_data_translate` VALUES (397, '获取跑步信息', 'Get running information');
INSERT INTO `sys_dict_data_translate` VALUES (398, '设置水流', 'Set the water flow');
INSERT INTO `sys_dict_data_translate` VALUES (399, '执行自定义复杂动作', 'Perform custom complex actions');
INSERT INTO `sys_dict_data_translate` VALUES (400, '设备定时', 'Device timing');
INSERT INTO `sys_dict_data_translate` VALUES (401, '取消设备定时', 'Cancel the device schedule');
INSERT INTO `sys_dict_data_translate` VALUES (402, '设备复位', 'The device resets');
INSERT INTO `sys_dict_data_translate` VALUES (403, '设置功率', 'Set the power');
INSERT INTO `sys_dict_data_translate` VALUES (404, '增大功率', 'Increase the power');
INSERT INTO `sys_dict_data_translate` VALUES (405, '减小功率', 'Reduce power');
INSERT INTO `sys_dict_data_translate` VALUES (406, '查询油量', 'Check the oil level');
INSERT INTO `sys_dict_data_translate` VALUES (407, '打印', 'print');
INSERT INTO `sys_dict_data_translate` VALUES (408, '设置摆风角度', 'Set the swing angle');
INSERT INTO `sys_dict_data_translate` VALUES (409, '增大雾量', 'Increase the amount of fog');
INSERT INTO `sys_dict_data_translate` VALUES (410, '见效雾量', 'Effective fog volume');
INSERT INTO `sys_dict_data_translate` VALUES (411, '设置雾量', 'Set the amount of fog');
INSERT INTO `sys_dict_data_translate` VALUES (412, '打开灶眼', 'Open the hearth');
INSERT INTO `sys_dict_data_translate` VALUES (413, '关闭灶眼', 'Close the hearth');
INSERT INTO `sys_dict_data_translate` VALUES (414, '定时打开灶眼', 'Open the stove eye regularly');
INSERT INTO `sys_dict_data_translate` VALUES (415, '定时关闭灶眼', 'Close the burner regularly');
INSERT INTO `sys_dict_data_translate` VALUES (416, '设备的开关状态属性', 'The on/off status properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (417, '设备的亮度属性', 'The brightness attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (418, '设备对应的温度属性', 'The temperature attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (419, '湿度属性', 'Humidity attributes');
INSERT INTO `sys_dict_data_translate` VALUES (420, '空气中CO2的浓度', 'The concentration of CO2 in the air');
INSERT INTO `sys_dict_data_translate` VALUES (421, '设备的暂停属性', 'The pause properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (422, '设备的音量属性', 'The volume attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (423, '发声设备当前的静音属性', 'The current mute properties of the sounding device');
INSERT INTO `sys_dict_data_translate` VALUES (424, '设备的位置属性', 'The location properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (425, '锁的状态属性', 'The state attribute of the lock');
INSERT INTO `sys_dict_data_translate` VALUES (426, '设备的颜色', 'The color of the device');
INSERT INTO `sys_dict_data_translate` VALUES (427, '设备电池的电量属性', 'The battery level attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (428, '自定义充电状态属性', 'Customize state-of-charge attributes');
INSERT INTO `sys_dict_data_translate` VALUES (429, '设备的色温属性', 'The color temperature properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (430, '设备风速值属性', 'The wind speed value attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (431, '空气质量的属性', 'Attributes of air quality');
INSERT INTO `sys_dict_data_translate` VALUES (432, '设备的吸力属性', 'Suction properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (433, '设备的水量属性', 'The water volume attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (434, '电视频道属性', 'TV channel attributes');
INSERT INTO `sys_dict_data_translate` VALUES (435, '自定义方向属性', 'Custom orientation attributes');
INSERT INTO `sys_dict_data_translate` VALUES (436, '设备控制模式属性', 'Device control mode properties');
INSERT INTO `sys_dict_data_translate` VALUES (437, '自定义水质属性', 'Customize water quality attributes');
INSERT INTO `sys_dict_data_translate` VALUES (438, '自定义楼梯属性', 'Customize stair properties');
INSERT INTO `sys_dict_data_translate` VALUES (439, '自定义高度属性', 'Custom height attributes');
INSERT INTO `sys_dict_data_translate` VALUES (440, '自定义运行时间属性', 'Customize runtime properties');
INSERT INTO `sys_dict_data_translate` VALUES (441, '自定义剩余时间属性', 'Customize the time remaining attributes');
INSERT INTO `sys_dict_data_translate` VALUES (442, '自定义运行状态属性', 'Customize the running status properties');
INSERT INTO `sys_dict_data_translate` VALUES (443, '设备速度值属性', 'Device speed value attributes');
INSERT INTO `sys_dict_data_translate` VALUES (444, '自定义档位属性', 'Customize gear properties');
INSERT INTO `sys_dict_data_translate` VALUES (445, '设备的状态属性', 'The status properties of the device');
INSERT INTO `sys_dict_data_translate` VALUES (446, '空气中PM2.5的含量', 'PM2 in the air. 5 content');
INSERT INTO `sys_dict_data_translate` VALUES (447, '空气中PM10的含量', 'The amount of PM10 in the air');
INSERT INTO `sys_dict_data_translate` VALUES (448, '运动信息属性', 'Motion information attributes');
INSERT INTO `sys_dict_data_translate` VALUES (449, '自定义水流属性', 'Customize water flow attributes');
INSERT INTO `sys_dict_data_translate` VALUES (450, '自定义复杂动作属性', 'Customize complex action attributes');
INSERT INTO `sys_dict_data_translate` VALUES (451, '自定义定时动作属性', 'Customize timed action properties');
INSERT INTO `sys_dict_data_translate` VALUES (452, '自定义复位动作属性', 'Customize the properties of the reset action');
INSERT INTO `sys_dict_data_translate` VALUES (453, '设备油箱的油量属性', 'The fuel level attribute of the device\'s fuel tank');
INSERT INTO `sys_dict_data_translate` VALUES (454, '设备的工作状态属性', 'The work status attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (455, '设备是否可达属性', 'Whether the device is reachable or not');
INSERT INTO `sys_dict_data_translate` VALUES (456, '设备通电状态的属性', 'The properties of the power-on status of the device');
INSERT INTO `sys_dict_data_translate` VALUES (457, '设备的功率属性', 'The power attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (458, '空气中总挥发性有机化合物的浓度', 'The concentration of total volatile organic compounds in the air');
INSERT INTO `sys_dict_data_translate` VALUES (459, '空气中甲醛的浓度', 'The concentration of formaldehyde in the air');
INSERT INTO `sys_dict_data_translate` VALUES (460, '百分比属性', 'Percentage attributes');
INSERT INTO `sys_dict_data_translate` VALUES (461, '日期和时间属性', 'Date and time attributes');
INSERT INTO `sys_dict_data_translate` VALUES (462, '设备可行驶距离属性', 'The distance that can be traveled by the device attributes');
INSERT INTO `sys_dict_data_translate` VALUES (463, '设备的名称属性', 'Name attribute of the device');
INSERT INTO `sys_dict_data_translate` VALUES (464, '折线图', 'Line chart');
INSERT INTO `sys_dict_data_translate` VALUES (465, '柱状图', 'histogram');
INSERT INTO `sys_dict_data_translate` VALUES (466, '饼图', 'Pie charts');
INSERT INTO `sys_dict_data_translate` VALUES (467, '散点图', 'Scatter plot');
INSERT INTO `sys_dict_data_translate` VALUES (468, 'K线图', 'Candlestick chart');
INSERT INTO `sys_dict_data_translate` VALUES (469, '雷达图', 'Radar chart');
INSERT INTO `sys_dict_data_translate` VALUES (470, '盒须图', 'Box whisker diagram');
INSERT INTO `sys_dict_data_translate` VALUES (471, '热力图', 'Heatmap');
INSERT INTO `sys_dict_data_translate` VALUES (472, '关系图', 'Diagrams');
INSERT INTO `sys_dict_data_translate` VALUES (473, '水球图', 'Water polo diagram');
INSERT INTO `sys_dict_data_translate` VALUES (474, '树图', 'Tree diagram');
INSERT INTO `sys_dict_data_translate` VALUES (475, '矩形树图', 'Rectangular tree diagram');
INSERT INTO `sys_dict_data_translate` VALUES (476, '旭日图', 'Diagram of the Rising Sun');
INSERT INTO `sys_dict_data_translate` VALUES (477, '平行坐标系', 'Parallel coordinate system');
INSERT INTO `sys_dict_data_translate` VALUES (478, '桑葚图', 'Mulberry diagram');
INSERT INTO `sys_dict_data_translate` VALUES (479, '漏斗图', 'Funnel charts');
INSERT INTO `sys_dict_data_translate` VALUES (480, '仪表图', 'Gauge diagram');
INSERT INTO `sys_dict_data_translate` VALUES (481, '象形柱图', 'Pictogram bar chart');
INSERT INTO `sys_dict_data_translate` VALUES (482, '主题河流', 'Theme river');
INSERT INTO `sys_dict_data_translate` VALUES (483, '路径图', 'Road map');
INSERT INTO `sys_dict_data_translate` VALUES (484, '3D柱图', '3D bar chart');
INSERT INTO `sys_dict_data_translate` VALUES (485, '3D地图', '3D maps');
INSERT INTO `sys_dict_data_translate` VALUES (486, '词云图', 'Word cloud diagrams');
INSERT INTO `sys_dict_data_translate` VALUES (487, '其他图表', 'Other charts');
INSERT INTO `sys_dict_data_translate` VALUES (488, '按钮', 'button');
INSERT INTO `sys_dict_data_translate` VALUES (489, '指示灯', 'Light');
INSERT INTO `sys_dict_data_translate` VALUES (490, '工业泵', 'Industrial pumps');
INSERT INTO `sys_dict_data_translate` VALUES (491, '储蓄罐', 'Piggy bank');
INSERT INTO `sys_dict_data_translate` VALUES (492, '电机', 'Motor');
INSERT INTO `sys_dict_data_translate` VALUES (493, '电力符号', 'Electricity symbol');
INSERT INTO `sys_dict_data_translate` VALUES (494, '阀门', 'valve');
INSERT INTO `sys_dict_data_translate` VALUES (495, '反应器', 'reactor');
INSERT INTO `sys_dict_data_translate` VALUES (496, '风机', 'Fans');
INSERT INTO `sys_dict_data_translate` VALUES (497, '管道', 'pipeline');
INSERT INTO `sys_dict_data_translate` VALUES (498, '建筑物', 'building');
INSERT INTO `sys_dict_data_translate` VALUES (499, '锅炉', 'boiler');
INSERT INTO `sys_dict_data_translate` VALUES (500, '换热站', 'Heat exchange station');
INSERT INTO `sys_dict_data_translate` VALUES (501, '机械设备', 'Machinery and equipment');
INSERT INTO `sys_dict_data_translate` VALUES (502, '交通', 'traffic');
INSERT INTO `sys_dict_data_translate` VALUES (503, '警示与标志', 'Warnings & Signs');
INSERT INTO `sys_dict_data_translate` VALUES (504, '卡片', 'card');
INSERT INTO `sys_dict_data_translate` VALUES (505, '空调系统', 'Air conditioning system');
INSERT INTO `sys_dict_data_translate` VALUES (506, '流体符号', 'Fluid symbols');
INSERT INTO `sys_dict_data_translate` VALUES (507, '背景模版', 'Background templates');
INSERT INTO `sys_dict_data_translate` VALUES (508, '人物', 'figure');
INSERT INTO `sys_dict_data_translate` VALUES (509, '天气', 'Weather');
INSERT INTO `sys_dict_data_translate` VALUES (510, '物料运输', 'Material transport');
INSERT INTO `sys_dict_data_translate` VALUES (511, '仪表', 'appearance');
INSERT INTO `sys_dict_data_translate` VALUES (512, '装饰', 'ornament');
INSERT INTO `sys_dict_data_translate` VALUES (513, '自然', 'nature');
INSERT INTO `sys_dict_data_translate` VALUES (514, '全景', 'panorama');
INSERT INTO `sys_dict_data_translate` VALUES (515, '3D图画', '3D drawings');
INSERT INTO `sys_dict_data_translate` VALUES (516, '960x600', '960x600');
INSERT INTO `sys_dict_data_translate` VALUES (517, '1024x768', '1024x768');
INSERT INTO `sys_dict_data_translate` VALUES (518, '1280x1024', '1280x1024');
INSERT INTO `sys_dict_data_translate` VALUES (519, '1366x768', '1366x768');
INSERT INTO `sys_dict_data_translate` VALUES (520, '1440x900', '1440x900');
INSERT INTO `sys_dict_data_translate` VALUES (521, '1920x960', '1920x960');
INSERT INTO `sys_dict_data_translate` VALUES (522, '1920x1080', '1920x1080');
INSERT INTO `sys_dict_data_translate` VALUES (523, '2048x858', '2048x858');
INSERT INTO `sys_dict_data_translate` VALUES (524, '2048x1080', '2048x1080');
INSERT INTO `sys_dict_data_translate` VALUES (525, '3840x2160', '3840x2160');
INSERT INTO `sys_dict_data_translate` VALUES (526, '3656x2664', '3656x2664');
INSERT INTO `sys_dict_data_translate` VALUES (527, '4096x3112', '4096x3112');
INSERT INTO `sys_dict_data_translate` VALUES (528, '授权码模式', 'Authorization code mode');
INSERT INTO `sys_dict_data_translate` VALUES (529, '客户端模式', 'Client-side mode');
INSERT INTO `sys_dict_data_translate` VALUES (530, '密码模式', 'Password mode');
INSERT INTO `sys_dict_data_translate` VALUES (531, '简化模式', 'Simplified mode');
INSERT INTO `sys_dict_data_translate` VALUES (532, '刷新Token', 'Refresh the token');
INSERT INTO `sys_dict_data_translate` VALUES (533, '短信', 'Sms');
INSERT INTO `sys_dict_data_translate` VALUES (535, '微信', 'Wechat');
INSERT INTO `sys_dict_data_translate` VALUES (536, '钉钉', 'DingTalk');
INSERT INTO `sys_dict_data_translate` VALUES (537, '语音', 'Voice');
INSERT INTO `sys_dict_data_translate` VALUES (539, '阿里云', 'Alibaba Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (540, 'QQ', 'QQ');
INSERT INTO `sys_dict_data_translate` VALUES (543, '邮箱', 'mailbox');
INSERT INTO `sys_dict_data_translate` VALUES (554, '设备告警', 'Device alarms');
INSERT INTO `sys_dict_data_translate` VALUES (558, '验证码', 'Captcha');
INSERT INTO `sys_dict_data_translate` VALUES (560, '设备上报', 'The device is escalated');
INSERT INTO `sys_dict_data_translate` VALUES (561, '平台下发', 'Distributed by the platform');
INSERT INTO `sys_dict_data_translate` VALUES (562, '设备上线', 'The device goes online');
INSERT INTO `sys_dict_data_translate` VALUES (563, '设备离线', 'The device is offline');
INSERT INTO `sys_dict_data_translate` VALUES (564, '数据流', 'data stream');
INSERT INTO `sys_dict_data_translate` VALUES (565, '触发器', 'trigger');
INSERT INTO `sys_dict_data_translate` VALUES (566, '执行动作', 'Perform the action');
INSERT INTO `sys_dict_data_translate` VALUES (567, '消息重发', 'The message is retransmitted');
INSERT INTO `sys_dict_data_translate` VALUES (568, '消息通知', 'Message notifications');
INSERT INTO `sys_dict_data_translate` VALUES (569, 'Http推送', 'HTTP push');
INSERT INTO `sys_dict_data_translate` VALUES (570, 'Mqtt桥接', 'MQTT bridging');
INSERT INTO `sys_dict_data_translate` VALUES (571, '数据库存储', 'Database storage');
INSERT INTO `sys_dict_data_translate` VALUES (572, '腾讯云', 'Tencent Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (573, '天翼云', 'Wing clouds');
INSERT INTO `sys_dict_data_translate` VALUES (574, '华为云', 'HUAWEI CLOUD');
INSERT INTO `sys_dict_data_translate` VALUES (575, '云片', 'Cloud sheets');
INSERT INTO `sys_dict_data_translate` VALUES (576, '亿美软通', 'Yimei SoftStone');
INSERT INTO `sys_dict_data_translate` VALUES (577, '容连云', 'Rong Lianyun');
INSERT INTO `sys_dict_data_translate` VALUES (578, '京东云', 'JD Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (579, '网易云', 'NetEase Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (580, '微信小程序（订阅消息）', 'WeChat Mini Program (Subscribe to Messages) ');
INSERT INTO `sys_dict_data_translate` VALUES (581, '163', '163');
INSERT INTO `sys_dict_data_translate` VALUES (582, '阿里云', 'Alibaba Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (583, '工作通知', 'Job Notices');
INSERT INTO `sys_dict_data_translate` VALUES (584, '群机器人', 'Swarm bots');
INSERT INTO `sys_dict_data_translate` VALUES (585, '营销通知', 'Marketing Notifications');
INSERT INTO `sys_dict_data_translate` VALUES (586, '腾讯云', 'Tencent Cloud');
INSERT INTO `sys_dict_data_translate` VALUES (587, '文本', 'text');
INSERT INTO `sys_dict_data_translate` VALUES (588, 'markdown类型', 'Markdown type');
INSERT INTO `sys_dict_data_translate` VALUES (589, '链接消息', 'Link messages');
INSERT INTO `sys_dict_data_translate` VALUES (590, '企业微信应用消息', 'WeCom app messages');
INSERT INTO `sys_dict_data_translate` VALUES (591, '企业微信群机器人', 'Enterprise WeChat group robot');
INSERT INTO `sys_dict_data_translate` VALUES (592, '文本', 'text');
INSERT INTO `sys_dict_data_translate` VALUES (593, 'markdown', 'markdown');
INSERT INTO `sys_dict_data_translate` VALUES (595, '图文', 'Graphic');
INSERT INTO `sys_dict_data_translate` VALUES (596, '厂商', 'Manufacturers');
INSERT INTO `sys_dict_data_translate` VALUES (597, '生产厂商', 'Manufacturer');
INSERT INTO `sys_dict_data_translate` VALUES (598, '经销商', 'dealer');
INSERT INTO `sys_dict_data_translate` VALUES (599, '服务商', 'Service');
INSERT INTO `sys_dict_data_translate` VALUES (600, '微信开放平台公众号', 'WeChat open platform public account');
INSERT INTO `sys_dict_data_translate` VALUES (601, '微信公众号', 'WeChat public account');
INSERT INTO `sys_dict_data_translate` VALUES (602, '网关子设备', 'Gateway sub-device');
INSERT INTO `sys_dict_data_translate` VALUES (603, 'WIFI', 'WIFI');
INSERT INTO `sys_dict_data_translate` VALUES (604, '以太网', 'Ethernet');
INSERT INTO `sys_dict_data_translate` VALUES (605, 'RS485', 'RS485');
INSERT INTO `sys_dict_data_translate` VALUES (606, 'RS232', 'RS232');
INSERT INTO `sys_dict_data_translate` VALUES (607, 'LoRa', 'LoRa');
INSERT INTO `sys_dict_data_translate` VALUES (608, 'ZigBee', 'ZigBee');
INSERT INTO `sys_dict_data_translate` VALUES (609, 'NB-IoT', 'NB-IoT');
INSERT INTO `sys_dict_data_translate` VALUES (610, '原值', 'Original value');
INSERT INTO `sys_dict_data_translate` VALUES (611, '累计值', 'Cumulative');
INSERT INTO `sys_dict_data_translate` VALUES (612, '平均值', 'average value');
INSERT INTO `sys_dict_data_translate` VALUES (613, '最大值', 'maximum');
INSERT INTO `sys_dict_data_translate` VALUES (614, '最小值', 'minimum');
INSERT INTO `sys_dict_data_translate` VALUES (615, '1分钟', '1 minute');
INSERT INTO `sys_dict_data_translate` VALUES (616, '3分钟', '3 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (617, '5分钟', '5 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (618, '10分钟', '10 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (619, '15分钟', '15 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (620, '20分钟', '20 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (621, '30分钟', '30 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (622, '5小时', '5 hours');
INSERT INTO `sys_dict_data_translate` VALUES (623, '时', 'time');
INSERT INTO `sys_dict_data_translate` VALUES (624, '日', 'day');
INSERT INTO `sys_dict_data_translate` VALUES (625, '周', 'week');
INSERT INTO `sys_dict_data_translate` VALUES (626, '月', 'month');
INSERT INTO `sys_dict_data_translate` VALUES (627, '00时', '00 hours');
INSERT INTO `sys_dict_data_translate` VALUES (628, '01时', '01 hour');
INSERT INTO `sys_dict_data_translate` VALUES (629, '02时', '02 hours');
INSERT INTO `sys_dict_data_translate` VALUES (630, '03时', '03 hours');
INSERT INTO `sys_dict_data_translate` VALUES (631, '04时', '04 hours');
INSERT INTO `sys_dict_data_translate` VALUES (632, '05时', '05 hours');
INSERT INTO `sys_dict_data_translate` VALUES (633, '06时', '06 hours');
INSERT INTO `sys_dict_data_translate` VALUES (634, '07时', '07 hours');
INSERT INTO `sys_dict_data_translate` VALUES (635, '08时', '08 hours');
INSERT INTO `sys_dict_data_translate` VALUES (636, '09时', '09 hours');
INSERT INTO `sys_dict_data_translate` VALUES (637, '10时', '10 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (638, '11时', '11 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (639, '12时', '12 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (640, '13时', '13 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (641, '14时', '14 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (642, '15时', '15 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (643, '16时', '16 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (644, '17时', '17 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (645, '18时', '18 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (646, '19时', '19 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (647, '20时', '20 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (648, '21时', '21 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (649, '22时', '22 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (650, '23时', '23 o\'clock');
INSERT INTO `sys_dict_data_translate` VALUES (651, '周一', 'Monday');
INSERT INTO `sys_dict_data_translate` VALUES (652, '周二', 'Tuesday');
INSERT INTO `sys_dict_data_translate` VALUES (653, '周三', 'Wednesday');
INSERT INTO `sys_dict_data_translate` VALUES (654, '周四', 'Thursday');
INSERT INTO `sys_dict_data_translate` VALUES (655, '周五', 'Friday');
INSERT INTO `sys_dict_data_translate` VALUES (656, '周六', 'Saturday');
INSERT INTO `sys_dict_data_translate` VALUES (657, '周日', 'Sunday');
INSERT INTO `sys_dict_data_translate` VALUES (658, '1日', '1 day');
INSERT INTO `sys_dict_data_translate` VALUES (659, '2日', '2nd');
INSERT INTO `sys_dict_data_translate` VALUES (660, '3日', '3rd');
INSERT INTO `sys_dict_data_translate` VALUES (661, '4日', '4th');
INSERT INTO `sys_dict_data_translate` VALUES (662, '5日', '5th');
INSERT INTO `sys_dict_data_translate` VALUES (663, '6日', '6th');
INSERT INTO `sys_dict_data_translate` VALUES (664, '7日', '7th');
INSERT INTO `sys_dict_data_translate` VALUES (665, '8日', '8th');
INSERT INTO `sys_dict_data_translate` VALUES (666, '9日', '9th');
INSERT INTO `sys_dict_data_translate` VALUES (667, '10日', '10th');
INSERT INTO `sys_dict_data_translate` VALUES (668, '11日', '11th');
INSERT INTO `sys_dict_data_translate` VALUES (669, '12日', '12th');
INSERT INTO `sys_dict_data_translate` VALUES (670, '13日', '13th');
INSERT INTO `sys_dict_data_translate` VALUES (671, '14日', '14th');
INSERT INTO `sys_dict_data_translate` VALUES (672, '15日', '15th');
INSERT INTO `sys_dict_data_translate` VALUES (673, '16日', '16th');
INSERT INTO `sys_dict_data_translate` VALUES (674, '17日', '17th');
INSERT INTO `sys_dict_data_translate` VALUES (675, '18日', '18th');
INSERT INTO `sys_dict_data_translate` VALUES (676, '19日', '19th');
INSERT INTO `sys_dict_data_translate` VALUES (677, '20日', '20th');
INSERT INTO `sys_dict_data_translate` VALUES (678, '21日', '21st');
INSERT INTO `sys_dict_data_translate` VALUES (679, '22日', '22nd');
INSERT INTO `sys_dict_data_translate` VALUES (680, '23日', '23rd');
INSERT INTO `sys_dict_data_translate` VALUES (681, '24日', '24th');
INSERT INTO `sys_dict_data_translate` VALUES (682, '25日', '25th');
INSERT INTO `sys_dict_data_translate` VALUES (683, '26日', '26th');
INSERT INTO `sys_dict_data_translate` VALUES (684, '25日', '25th');
INSERT INTO `sys_dict_data_translate` VALUES (685, '27日', '27th');
INSERT INTO `sys_dict_data_translate` VALUES (686, '28日', '28th');
INSERT INTO `sys_dict_data_translate` VALUES (687, '29日', '29th');
INSERT INTO `sys_dict_data_translate` VALUES (688, '30日', '30th');
INSERT INTO `sys_dict_data_translate` VALUES (689, '31日', '31st');
INSERT INTO `sys_dict_data_translate` VALUES (690, '设备数据', 'Device Data');
INSERT INTO `sys_dict_data_translate` VALUES (691, '网关', 'gateway');
INSERT INTO `sys_dict_data_translate` VALUES (692, '10分钟', '10 minutes');
INSERT INTO `sys_dict_data_translate` VALUES (693, 'http接入', 'HTTP access');
INSERT INTO `sys_dict_data_translate` VALUES (694, 'mqtt接入', 'MQTT access');
INSERT INTO `sys_dict_data_translate` VALUES (695, '失败', 'fail');
INSERT INTO `sys_dict_data_translate` VALUES (696, '成功', 'success');
INSERT INTO `sys_dict_data_translate` VALUES (697, '开关按钮', 'switch button');
INSERT INTO `sys_dict_data_translate` VALUES (698, '指示灯', 'indicator light');
INSERT INTO `sys_dict_data_translate` VALUES (699, '基本形状', 'Basic shape');
INSERT INTO `sys_dict_data_translate` VALUES (700, '箭头图标', 'Arrow icon');
INSERT INTO `sys_dict_data_translate` VALUES (701, '卡片图片', 'Card image');
INSERT INTO `sys_dict_data_translate` VALUES (702, '字母数字', 'Alphanumeric characters');
INSERT INTO `sys_dict_data_translate` VALUES (703, '背景图片', 'Background image');
INSERT INTO `sys_dict_data_translate` VALUES (704, '装饰框', 'Decorative frame');
INSERT INTO `sys_dict_data_translate` VALUES (705, '天气符号', 'Weather symbols');
INSERT INTO `sys_dict_data_translate` VALUES (706, '自然植物', 'Natural plants');
INSERT INTO `sys_dict_data_translate` VALUES (707, '交通标志', 'traffic sign');
INSERT INTO `sys_dict_data_translate` VALUES (708, '建筑物', 'building');
INSERT INTO `sys_dict_data_translate` VALUES (709, '阀门图标', 'Valve icon');
INSERT INTO `sys_dict_data_translate` VALUES (710, '圆形管道', 'Circular pipeline');
INSERT INTO `sys_dict_data_translate` VALUES (711, '方形管道', 'Square pipeline');
INSERT INTO `sys_dict_data_translate` VALUES (712, '软管图标', 'Hose icon');
INSERT INTO `sys_dict_data_translate` VALUES (713, '传送带', 'Conveyor belt');
INSERT INTO `sys_dict_data_translate` VALUES (714, '电子设备', 'Electronic devices');
INSERT INTO `sys_dict_data_translate` VALUES (715, '罐体箱体', 'Tank body box');
INSERT INTO `sys_dict_data_translate` VALUES (716, '化工罐塔', 'Chemical tank tower');
INSERT INTO `sys_dict_data_translate` VALUES (717, '工业泵', 'Industrial pump');
INSERT INTO `sys_dict_data_translate` VALUES (718, '水泵图片', 'Pump picture');
INSERT INTO `sys_dict_data_translate` VALUES (719, '风机图标', 'Fan icon');
INSERT INTO `sys_dict_data_translate` VALUES (720, '电机图标', 'Motor icon');
INSERT INTO `sys_dict_data_translate` VALUES (721, '电力设施', 'Power facilities');
INSERT INTO `sys_dict_data_translate` VALUES (722, '搅拌机', 'blender');
INSERT INTO `sys_dict_data_translate` VALUES (723, '反应器', 'Reactor');
INSERT INTO `sys_dict_data_translate` VALUES (724, '暖通空调', 'Heating, Ventilation, and Air Conditioning');
INSERT INTO `sys_dict_data_translate` VALUES (725, '工业冷却', 'Industrial cooling');
INSERT INTO `sys_dict_data_translate` VALUES (726, '工业加热', 'Industrial heating');
INSERT INTO `sys_dict_data_translate` VALUES (727, '水处理设备', 'Water treatment equipment');
INSERT INTO `sys_dict_data_translate` VALUES (728, '仪表设备', 'Instrument equipment');
INSERT INTO `sys_dict_data_translate` VALUES (729, '机械设备', 'mechanical equipment');
INSERT INTO `sys_dict_data_translate` VALUES (730, '控制柜', 'Control cabinet');
INSERT INTO `sys_dict_data_translate` VALUES (731, '度量尺', 'Measuring ruler');
INSERT INTO `sys_dict_data_translate` VALUES (732, '采矿图标', 'Mining icon');
INSERT INTO `sys_dict_data_translate` VALUES (733, 'ISA符号', 'ISA symbol');
INSERT INTO `sys_dict_data_translate` VALUES (734, '电气符号', 'Electrical symbols');
INSERT INTO `sys_dict_data_translate` VALUES (735, '供暖符号', 'Heating symbol');
INSERT INTO `sys_dict_data_translate` VALUES (736, '其他', 'other');

-- ----------------------------
-- Table structure for sys_dict_type_translate
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type_translate`;
CREATE TABLE `sys_dict_type_translate`  (
                                            `id` bigint(20) NOT NULL COMMENT 'ID',
                                            `zh_cn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zh_CN',
                                            `en_us` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'en_US',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典类型翻译表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type_translate
-- ----------------------------
INSERT INTO `sys_dict_type_translate` VALUES (1, '用户性别', 'User gender');
INSERT INTO `sys_dict_type_translate` VALUES (2, '菜单状态', 'Menu status');
INSERT INTO `sys_dict_type_translate` VALUES (3, '系统开关', 'System switches');
INSERT INTO `sys_dict_type_translate` VALUES (4, '任务状态', 'Task status');
INSERT INTO `sys_dict_type_translate` VALUES (5, '任务分组', 'Task grouping');
INSERT INTO `sys_dict_type_translate` VALUES (6, '系统是否', 'Whether the system is');
INSERT INTO `sys_dict_type_translate` VALUES (7, '通知类型', 'Notification type');
INSERT INTO `sys_dict_type_translate` VALUES (8, '通知状态', 'Notification status');
INSERT INTO `sys_dict_type_translate` VALUES (9, '操作类型', 'The type of operation');
INSERT INTO `sys_dict_type_translate` VALUES (10, '系统状态', 'System status');
INSERT INTO `sys_dict_type_translate` VALUES (100, '物模型类别', 'TSL model category');
INSERT INTO `sys_dict_type_translate` VALUES (101, '数据类型', 'data type');
INSERT INTO `sys_dict_type_translate` VALUES (102, '是否', 'Whether');
INSERT INTO `sys_dict_type_translate` VALUES (103, '产品状态', 'Product status');
INSERT INTO `sys_dict_type_translate` VALUES (104, '设备类型', 'Device type');
INSERT INTO `sys_dict_type_translate` VALUES (105, '联网方式', 'Networking');
INSERT INTO `sys_dict_type_translate` VALUES (106, '认证方式', 'Authentication method');
INSERT INTO `sys_dict_type_translate` VALUES (107, '设备芯片', 'Device chips');
INSERT INTO `sys_dict_type_translate` VALUES (109, '设备状态', 'Device status');
INSERT INTO `sys_dict_type_translate` VALUES (110, '是否启用', 'Whether it is enabled');
INSERT INTO `sys_dict_type_translate` VALUES (111, '告警类型', 'Alarm type');
INSERT INTO `sys_dict_type_translate` VALUES (112, '处理状态', 'Processing status');
INSERT INTO `sys_dict_type_translate` VALUES (113, '设备日志类型', 'Device log type');
INSERT INTO `sys_dict_type_translate` VALUES (114, 'Oauth开放平台', 'OAuth Open Platform');
INSERT INTO `sys_dict_type_translate` VALUES (115, '第三方登录平台', 'Third-party login platform');
INSERT INTO `sys_dict_type_translate` VALUES (116, '第三方登录平台状态', 'Third-party login platform status');
INSERT INTO `sys_dict_type_translate` VALUES (117, '设备定位方式', 'How the device is located');
INSERT INTO `sys_dict_type_translate` VALUES (118, '授权码状态', 'Authorization code status');
INSERT INTO `sys_dict_type_translate` VALUES (119, 'SipID状态', 'SipID status');
INSERT INTO `sys_dict_type_translate` VALUES (120, '监控设备类型', 'Monitor device types');
INSERT INTO `sys_dict_type_translate` VALUES (121, '通道类型', 'Channel type');
INSERT INTO `sys_dict_type_translate` VALUES (122, '轮询方式', 'Polling method');
INSERT INTO `sys_dict_type_translate` VALUES (123, '批量采集时间', 'Batch collection time');
INSERT INTO `sys_dict_type_translate` VALUES (124, '寄存器功能码', 'Register function code');
INSERT INTO `sys_dict_type_translate` VALUES (125, '传输协议类型', 'The type of transport protocol');
INSERT INTO `sys_dict_type_translate` VALUES (126, '设备事件类型', 'Device event type');
INSERT INTO `sys_dict_type_translate` VALUES (127, '指令下发类型', 'The type of command delivery');
INSERT INTO `sys_dict_type_translate` VALUES (128, '读写类型', 'Read and write type');
INSERT INTO `sys_dict_type_translate` VALUES (129, '升级范围', 'Scope of upgrade');
INSERT INTO `sys_dict_type_translate` VALUES (130, '云存储平台类型', 'The type of cloud storage platform');
INSERT INTO `sys_dict_type_translate` VALUES (131, 'modbus数据类型', 'Modbus data type');
INSERT INTO `sys_dict_type_translate` VALUES (132, '小度音箱关联设备', 'Small speakers are associated with devices');
INSERT INTO `sys_dict_type_translate` VALUES (133, '小度音箱操作类型', 'Small speaker operation type');
INSERT INTO `sys_dict_type_translate` VALUES (134, '小度音箱设备属性', 'Small speaker device attributes');
INSERT INTO `sys_dict_type_translate` VALUES (135, '组态图表类型', 'Configure the chart type');
INSERT INTO `sys_dict_type_translate` VALUES (136, '组态图库类型', 'Configure the library type');
INSERT INTO `sys_dict_type_translate` VALUES (140, '规则脚本类型', 'The type of rule script');
INSERT INTO `sys_dict_type_translate` VALUES (141, '规则脚本语言', 'Rule scripting language');
INSERT INTO `sys_dict_type_translate` VALUES (142, '页面大小', 'Page size');
INSERT INTO `sys_dict_type_translate` VALUES (143, 'Oauth授权模式', 'OAuth authorization model');
INSERT INTO `sys_dict_type_translate` VALUES (144, '通知渠道类型', 'The type of notification channel');
INSERT INTO `sys_dict_type_translate` VALUES (145, '通知短信服务商', 'Notify the SMS provider');
INSERT INTO `sys_dict_type_translate` VALUES (146, '通知邮箱服务商', 'Notify your email service provider');
INSERT INTO `sys_dict_type_translate` VALUES (148, '通知业务编码', 'Notification service code');
INSERT INTO `sys_dict_type_translate` VALUES (149, '规则脚本事件', 'Rule script events');
INSERT INTO `sys_dict_type_translate` VALUES (150, '规则脚本用途', 'Rule script usage');
INSERT INTO `sys_dict_type_translate` VALUES (151, '规则脚本动作', 'Rule script actions');
INSERT INTO `sys_dict_type_translate` VALUES (152, '通知微信服务商', 'Notify the WeChat service provider');
INSERT INTO `sys_dict_type_translate` VALUES (153, '通知语音服务商', 'Notify the voice service provider');
INSERT INTO `sys_dict_type_translate` VALUES (154, '通知钉钉服务商', 'Notify the DingTalk service provider');
INSERT INTO `sys_dict_type_translate` VALUES (155, '通知钉钉消息类型', 'Notification DingTalk message type');
INSERT INTO `sys_dict_type_translate` VALUES (156, '通知企业微信消息类型', 'Notify the WeCom message type');
INSERT INTO `sys_dict_type_translate` VALUES (157, '机构类型', 'Type of institution');
INSERT INTO `sys_dict_type_translate` VALUES (158, '网关子设备通讯方式', 'The communication mode of the gateway sub-device');
INSERT INTO `sys_dict_type_translate` VALUES (159, '变量统计方式', 'How variables are counted');
INSERT INTO `sys_dict_type_translate` VALUES (160, '时间周期间隔', 'Time period intervals');
INSERT INTO `sys_dict_type_translate` VALUES (161, '时间周期时间', 'Time period time');
INSERT INTO `sys_dict_type_translate` VALUES (162, '时间周期周', 'Time period week');
INSERT INTO `sys_dict_type_translate` VALUES (163, '时间周期日', 'Time period day');
INSERT INTO `sys_dict_type_translate` VALUES (164, '设备状态判断方式', 'How to determine the device status');
INSERT INTO `sys_dict_type_translate` VALUES (165, '公共状态', 'Public status');


-- ----------------------------
-- Table structure for sys_menu_translate
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_translate`;
CREATE TABLE `sys_menu_translate`  (
  `id` bigint(20) NOT NULL COMMENT '菜单ID',
  `zh_cn` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'zh_CN菜单名称',
  `en_us` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'en菜单名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单名称翻译表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_translate
-- ----------------------------
INSERT INTO `sys_menu_translate` VALUES (1, '系统管理', 'system management');
INSERT INTO `sys_menu_translate` VALUES (2, '系统监控', 'System monitoring');
INSERT INTO `sys_menu_translate` VALUES (3, '系统工具', 'System Tools');
INSERT INTO `sys_menu_translate` VALUES (4, '蜂信物联', 'Bee Trust IoT');
INSERT INTO `sys_menu_translate` VALUES (5, '企业管理', 'business management');
INSERT INTO `sys_menu_translate` VALUES (100, '用户管理', 'user management ');
INSERT INTO `sys_menu_translate` VALUES (101, '角色管理', 'Role management');
INSERT INTO `sys_menu_translate` VALUES (102, '菜单管理', 'Menu management');
INSERT INTO `sys_menu_translate` VALUES (103, '机构管理', 'organizational management');
INSERT INTO `sys_menu_translate` VALUES (104, '岗位管理', 'Job management');
INSERT INTO `sys_menu_translate` VALUES (105, '字典管理', 'Dictionary management');
INSERT INTO `sys_menu_translate` VALUES (106, '参数设置', 'Parameter settings');
INSERT INTO `sys_menu_translate` VALUES (107, '通知公告', 'Notice Announcement');
INSERT INTO `sys_menu_translate` VALUES (108, '日志管理', 'Log management');
INSERT INTO `sys_menu_translate` VALUES (109, '在线用户', 'Online users');
INSERT INTO `sys_menu_translate` VALUES (110, '定时任务', 'Scheduled tasks');
INSERT INTO `sys_menu_translate` VALUES (111, '数据监控', 'Data monitoring');
INSERT INTO `sys_menu_translate` VALUES (112, '服务监控', 'Service monitoring');
INSERT INTO `sys_menu_translate` VALUES (113, '缓存监控', 'Cache monitoring');
INSERT INTO `sys_menu_translate` VALUES (114, '表单构建', 'Form construction');
INSERT INTO `sys_menu_translate` VALUES (115, '代码生成', 'code generation');
INSERT INTO `sys_menu_translate` VALUES (116, '系统接口', 'system interface ');
INSERT INTO `sys_menu_translate` VALUES (124, '缓存列表', 'Cache List');
INSERT INTO `sys_menu_translate` VALUES (500, '操作日志', 'Operation log');
INSERT INTO `sys_menu_translate` VALUES (501, '登录日志', 'Login log');
INSERT INTO `sys_menu_translate` VALUES (1001, '用户查询', 'User query');
INSERT INTO `sys_menu_translate` VALUES (1002, '用户新增', 'User addition');
INSERT INTO `sys_menu_translate` VALUES (1003, '用户修改', 'User modification');
INSERT INTO `sys_menu_translate` VALUES (1004, '用户删除', 'User deletion');
INSERT INTO `sys_menu_translate` VALUES (1005, '用户导出', 'User export');
INSERT INTO `sys_menu_translate` VALUES (1006, '用户导入', 'User import');
INSERT INTO `sys_menu_translate` VALUES (1007, '重置密码', 'reset password ');
INSERT INTO `sys_menu_translate` VALUES (1008, '角色查询', 'Role query');
INSERT INTO `sys_menu_translate` VALUES (1009, '角色新增', 'New role addition');
INSERT INTO `sys_menu_translate` VALUES (1010, '角色修改', 'Role modification');
INSERT INTO `sys_menu_translate` VALUES (1011, '角色删除', 'Role deletion');
INSERT INTO `sys_menu_translate` VALUES (1012, '角色导出', 'Character export');
INSERT INTO `sys_menu_translate` VALUES (1013, '菜单查询', 'Menu query');
INSERT INTO `sys_menu_translate` VALUES (1014, '菜单新增', 'Menu addition');
INSERT INTO `sys_menu_translate` VALUES (1015, '菜单修改', 'Menu modification');
INSERT INTO `sys_menu_translate` VALUES (1016, '菜单删除', 'Menu deletion');
INSERT INTO `sys_menu_translate` VALUES (1017, '部门查询', 'Departmental inquiry');
INSERT INTO `sys_menu_translate` VALUES (1018, '部门新增', 'Department addition');
INSERT INTO `sys_menu_translate` VALUES (1019, '部门修改', 'Department modification');
INSERT INTO `sys_menu_translate` VALUES (1020, '部门删除', 'Department deletion');
INSERT INTO `sys_menu_translate` VALUES (1021, '岗位查询', 'Job Search');
INSERT INTO `sys_menu_translate` VALUES (1022, '岗位新增', 'Job addition');
INSERT INTO `sys_menu_translate` VALUES (1023, '岗位修改', 'Position modification');
INSERT INTO `sys_menu_translate` VALUES (1024, '岗位删除', 'Position deletion');
INSERT INTO `sys_menu_translate` VALUES (1025, '岗位导出', 'Job export');
INSERT INTO `sys_menu_translate` VALUES (1026, '字典查询', 'Dictionary query');
INSERT INTO `sys_menu_translate` VALUES (1027, '字典新增', 'Dictionary addition');
INSERT INTO `sys_menu_translate` VALUES (1028, '字典修改', 'Dictionary modification');
INSERT INTO `sys_menu_translate` VALUES (1029, '字典删除', 'Dictionary deletion');
INSERT INTO `sys_menu_translate` VALUES (1030, '字典导出', 'Dictionary export');
INSERT INTO `sys_menu_translate` VALUES (1031, '参数查询', 'Parameter query');
INSERT INTO `sys_menu_translate` VALUES (1032, '参数新增', 'Parameter addition');
INSERT INTO `sys_menu_translate` VALUES (1033, '参数修改', 'Parameter modification');
INSERT INTO `sys_menu_translate` VALUES (1034, '参数删除', 'Parameter deletion');
INSERT INTO `sys_menu_translate` VALUES (1035, '参数导出', 'Parameter export');
INSERT INTO `sys_menu_translate` VALUES (1036, '公告查询', 'Announcement inquiry');
INSERT INTO `sys_menu_translate` VALUES (1037, '公告新增', 'Announcement added');
INSERT INTO `sys_menu_translate` VALUES (1038, '公告修改', 'Announcement modification');
INSERT INTO `sys_menu_translate` VALUES (1039, '公告删除', 'Announcement deletion');
INSERT INTO `sys_menu_translate` VALUES (1040, '操作查询', 'Operation query');
INSERT INTO `sys_menu_translate` VALUES (1041, '操作删除', 'Operation deletion');
INSERT INTO `sys_menu_translate` VALUES (1042, '日志导出', 'Log export');
INSERT INTO `sys_menu_translate` VALUES (1043, '登录查询', 'Login query');
INSERT INTO `sys_menu_translate` VALUES (1044, '登录删除', 'Login Delete');
INSERT INTO `sys_menu_translate` VALUES (1045, '日志导出', 'Log export');
INSERT INTO `sys_menu_translate` VALUES (1046, '在线查询', 'Online inquiry');
INSERT INTO `sys_menu_translate` VALUES (1047, '批量强退', 'Batch forced refunds');
INSERT INTO `sys_menu_translate` VALUES (1048, '单条强退', 'Single strong refund');
INSERT INTO `sys_menu_translate` VALUES (1049, '任务查询', 'Task query');
INSERT INTO `sys_menu_translate` VALUES (1050, '任务新增', 'Task addition');
INSERT INTO `sys_menu_translate` VALUES (1051, '任务修改', 'Task modification');
INSERT INTO `sys_menu_translate` VALUES (1052, '任务删除', 'Task deletion');
INSERT INTO `sys_menu_translate` VALUES (1053, '状态修改', 'Status modification');
INSERT INTO `sys_menu_translate` VALUES (1054, '任务导出', 'Task export');
INSERT INTO `sys_menu_translate` VALUES (1055, '生成查询', 'Generate query');
INSERT INTO `sys_menu_translate` VALUES (1056, '生成修改', 'Generate modifications');
INSERT INTO `sys_menu_translate` VALUES (1057, '生成删除', 'Generate Delete');
INSERT INTO `sys_menu_translate` VALUES (1058, '导入代码', 'Import code');
INSERT INTO `sys_menu_translate` VALUES (1059, '预览代码', 'Preview code');
INSERT INTO `sys_menu_translate` VALUES (1060, '生成代码', 'Generate code');
INSERT INTO `sys_menu_translate` VALUES (1065, '账户解锁', 'Account unlocking');
INSERT INTO `sys_menu_translate` VALUES (2000, '设备管理', 'device management');
INSERT INTO `sys_menu_translate` VALUES (2001, '产品分类', 'Product classification');
INSERT INTO `sys_menu_translate` VALUES (2002, '产品分类查询', 'Product classification query');
INSERT INTO `sys_menu_translate` VALUES (2003, '产品分类新增', 'Product classification added');
INSERT INTO `sys_menu_translate` VALUES (2004, '产品分类修改', 'Product classification modification');
INSERT INTO `sys_menu_translate` VALUES (2005, '产品分类删除', 'Delete product category');
INSERT INTO `sys_menu_translate` VALUES (2006, '产品分类导出', 'Product classification export');
INSERT INTO `sys_menu_translate` VALUES (2007, '设备管理', 'device management');
INSERT INTO `sys_menu_translate` VALUES (2008, '设备查询', 'Equipment inquiry');
INSERT INTO `sys_menu_translate` VALUES (2009, '设备新增', 'Equipment addition');
INSERT INTO `sys_menu_translate` VALUES (2010, '设备修改', 'Equipment modification');
INSERT INTO `sys_menu_translate` VALUES (2011, '设备删除', 'Device deletion');
INSERT INTO `sys_menu_translate` VALUES (2012, '设备导出', 'Device export');
INSERT INTO `sys_menu_translate` VALUES (2013, '产品固件', 'Product firmware');
INSERT INTO `sys_menu_translate` VALUES (2014, '产品固件查询', 'Product firmware query');
INSERT INTO `sys_menu_translate` VALUES (2015, '产品固件新增', 'Product firmware addition');
INSERT INTO `sys_menu_translate` VALUES (2016, '产品固件修改', 'Product firmware modification');
INSERT INTO `sys_menu_translate` VALUES (2017, '产品固件删除', 'Product firmware deletion');
INSERT INTO `sys_menu_translate` VALUES (2018, '产品固件导出', 'Product firmware export');
INSERT INTO `sys_menu_translate` VALUES (2019, '设备分组', 'Equipment grouping');
INSERT INTO `sys_menu_translate` VALUES (2020, '设备分组查询', 'Equipment grouping query');
INSERT INTO `sys_menu_translate` VALUES (2021, '设备分组新增', 'New equipment grouping');
INSERT INTO `sys_menu_translate` VALUES (2022, '设备分组修改', 'Equipment grouping modification');
INSERT INTO `sys_menu_translate` VALUES (2023, '设备分组删除', 'Device group deletion');
INSERT INTO `sys_menu_translate` VALUES (2024, '设备分组导出', 'Equipment group export');
INSERT INTO `sys_menu_translate` VALUES (2043, '产品管理', 'product management ');
INSERT INTO `sys_menu_translate` VALUES (2044, '产品查询', 'Product inquiry');
INSERT INTO `sys_menu_translate` VALUES (2045, '产品新增', 'Product addition');
INSERT INTO `sys_menu_translate` VALUES (2046, '产品修改', 'Product modification');
INSERT INTO `sys_menu_translate` VALUES (2047, '产品删除', 'Product deletion');
INSERT INTO `sys_menu_translate` VALUES (2048, '产品导出', 'Product export');
INSERT INTO `sys_menu_translate` VALUES (2049, '通用物模型', 'Universal object model');
INSERT INTO `sys_menu_translate` VALUES (2050, '通用物模型查询', 'General object model query');
INSERT INTO `sys_menu_translate` VALUES (2051, '通用物模型新增', 'Addition of Universal Object Model');
INSERT INTO `sys_menu_translate` VALUES (2052, '通用物模型修改', 'Modification of Universal Object Model');
INSERT INTO `sys_menu_translate` VALUES (2053, '通用物模型删除', 'Universal object model deletion');
INSERT INTO `sys_menu_translate` VALUES (2054, '通用物模型导出', 'Export of Universal Object Model');
INSERT INTO `sys_menu_translate` VALUES (2067, '告警记录', 'Alarm Log ');
INSERT INTO `sys_menu_translate` VALUES (2068, '设备告警查询', 'Equipment alarm query');
INSERT INTO `sys_menu_translate` VALUES (2069, '设备告警新增', 'Equipment alarm addition');
INSERT INTO `sys_menu_translate` VALUES (2070, '设备告警修改', 'Equipment alarm modification');
INSERT INTO `sys_menu_translate` VALUES (2071, '设备告警删除', 'Device alarm deletion');
INSERT INTO `sys_menu_translate` VALUES (2072, '设备告警导出', 'Equipment alarm export');
INSERT INTO `sys_menu_translate` VALUES (2085, '场景联动', 'Scene linkage');
INSERT INTO `sys_menu_translate` VALUES (2086, '场景联动查询', 'Scene linkage query');
INSERT INTO `sys_menu_translate` VALUES (2087, '场景联动新增', 'New scene linkage');
INSERT INTO `sys_menu_translate` VALUES (2088, '场景联动修改', 'Scene linkage modification');
INSERT INTO `sys_menu_translate` VALUES (2089, '场景联动删除', 'Scene linkage deletion');
INSERT INTO `sys_menu_translate` VALUES (2090, '场景联动导出', 'Scene linkage export');
INSERT INTO `sys_menu_translate` VALUES (2099, '云云对接查询', 'Cloud to cloud docking query');
INSERT INTO `sys_menu_translate` VALUES (2100, '云云对接新增', 'Cloud to Cloud Connection Added');
INSERT INTO `sys_menu_translate` VALUES (2101, '云云对接修改', 'Yunyun docking modification');
INSERT INTO `sys_menu_translate` VALUES (2102, '云云对接删除', 'Cloud docking deletion');
INSERT INTO `sys_menu_translate` VALUES (2103, '云云对接导出', 'Cloud docking export');
INSERT INTO `sys_menu_translate` VALUES (2104, 'EMQ管理', 'EMQ Management');
INSERT INTO `sys_menu_translate` VALUES (2123, '新闻分类', 'News classification');
INSERT INTO `sys_menu_translate` VALUES (2124, '新闻分类查询', 'News classification query');
INSERT INTO `sys_menu_translate` VALUES (2125, '新闻分类新增', 'New news category added');
INSERT INTO `sys_menu_translate` VALUES (2126, '新闻分类修改', 'News classification modification');
INSERT INTO `sys_menu_translate` VALUES (2127, '新闻分类删除', 'Delete news category');
INSERT INTO `sys_menu_translate` VALUES (2128, '新闻分类导出', 'Export news categories');
INSERT INTO `sys_menu_translate` VALUES (2129, '新闻资讯', 'News and Information');
INSERT INTO `sys_menu_translate` VALUES (2130, '新闻资讯查询', 'News and Information Inquiry');
INSERT INTO `sys_menu_translate` VALUES (2131, '新闻资讯新增', 'New news and information added');
INSERT INTO `sys_menu_translate` VALUES (2132, '新闻资讯修改', 'News and Information Modification');
INSERT INTO `sys_menu_translate` VALUES (2133, '新闻资讯删除', 'Delete news and information');
INSERT INTO `sys_menu_translate` VALUES (2134, '新闻资讯导出', 'Export news and information');
INSERT INTO `sys_menu_translate` VALUES (2136, '产品授权码查询', 'Product authorization code query');
INSERT INTO `sys_menu_translate` VALUES (2137, '产品授权码新增', 'Product authorization code added');
INSERT INTO `sys_menu_translate` VALUES (2138, '产品授权码修改', 'Product authorization code modification');
INSERT INTO `sys_menu_translate` VALUES (2139, '产品授权码删除', 'Product authorization code deletion');
INSERT INTO `sys_menu_translate` VALUES (2140, '产品授权码导出', 'Export of Product Authorization Code');
INSERT INTO `sys_menu_translate` VALUES (2141, '三方登录', 'Three party login');
INSERT INTO `sys_menu_translate` VALUES (2142, '平台查询', 'Platform query');
INSERT INTO `sys_menu_translate` VALUES (2147, '设备用户列表', 'List of device users');
INSERT INTO `sys_menu_translate` VALUES (2148, '设备定时', 'Equipment timing');
INSERT INTO `sys_menu_translate` VALUES (2149, '固定大屏', 'Fixed large screen');
INSERT INTO `sys_menu_translate` VALUES (2167, '可视化管理', 'Visual management');
INSERT INTO `sys_menu_translate` VALUES (2168, '通道管理', 'Channel management');
INSERT INTO `sys_menu_translate` VALUES (2169, '视频配置查询', 'Video configuration query');
INSERT INTO `sys_menu_translate` VALUES (2170, '视频配置新增', 'Video configuration added');
INSERT INTO `sys_menu_translate` VALUES (2171, '视频配置修改', 'Video configuration modification');
INSERT INTO `sys_menu_translate` VALUES (2172, '视频配置删除', 'Video configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (2173, '固件任务', 'Firmware task');
INSERT INTO `sys_menu_translate` VALUES (2174, '固件任务查询', 'Firmware task query');
INSERT INTO `sys_menu_translate` VALUES (2175, '固件任务新增', 'New firmware task added');
INSERT INTO `sys_menu_translate` VALUES (2176, '固件任务修改', 'Firmware task modification');
INSERT INTO `sys_menu_translate` VALUES (2177, '固件任务删除', 'Firmware task deletion');
INSERT INTO `sys_menu_translate` VALUES (2178, '固件任务导出', 'Firmware task export');
INSERT INTO `sys_menu_translate` VALUES (2179, '固件详情', 'Firmware Details');
INSERT INTO `sys_menu_translate` VALUES (2180, '固件详情查询', 'Firmware Details Query');
INSERT INTO `sys_menu_translate` VALUES (2181, '固件详情新增', 'Firmware details added');
INSERT INTO `sys_menu_translate` VALUES (2182, '固件详情修改', 'Firmware details modification');
INSERT INTO `sys_menu_translate` VALUES (2183, '固件详情删除', 'Firmware details deleted');
INSERT INTO `sys_menu_translate` VALUES (2184, '固件详情导出', 'Firmware details export');
INSERT INTO `sys_menu_translate` VALUES (3000, '运维管理', 'Mocha ITOM ');
INSERT INTO `sys_menu_translate` VALUES (3001, '设备服务下发日志', 'Device service issuance log');
INSERT INTO `sys_menu_translate` VALUES (3002, '设备服务下发日志查询', 'Device service issuance log query');
INSERT INTO `sys_menu_translate` VALUES (3003, '设备服务下发日志新增', 'Add new logs for device service issuance');
INSERT INTO `sys_menu_translate` VALUES (3004, '设备服务下发日志修改', 'Device service issue log modification');
INSERT INTO `sys_menu_translate` VALUES (3005, '设备服务下发日志删除', 'Device service issues log deletion');
INSERT INTO `sys_menu_translate` VALUES (3006, '设备服务下发日志导出', 'Export logs issued by device services');
INSERT INTO `sys_menu_translate` VALUES (3007, '协议管理', 'Protocol management');
INSERT INTO `sys_menu_translate` VALUES (3008, '协议查询', 'Protocol inquiry');
INSERT INTO `sys_menu_translate` VALUES (3009, '协议新增', 'Protocol addition');
INSERT INTO `sys_menu_translate` VALUES (3010, '协议修改', 'Protocol modification');
INSERT INTO `sys_menu_translate` VALUES (3011, '协议删除', 'Protocol deletion');
INSERT INTO `sys_menu_translate` VALUES (3012, '协议导出', 'Protocol export');
INSERT INTO `sys_menu_translate` VALUES (3031, 'Netty管理', 'Netty Management');
INSERT INTO `sys_menu_translate` VALUES (3032, '客户端', 'client');
INSERT INTO `sys_menu_translate` VALUES (3033, '事件日志', 'Event log');
INSERT INTO `sys_menu_translate` VALUES (3034, '事件日志查询', 'Event log query');
INSERT INTO `sys_menu_translate` VALUES (3035, '事件日志新增', 'Event log addition');
INSERT INTO `sys_menu_translate` VALUES (3036, '事件日志修改', 'Event log modification');
INSERT INTO `sys_menu_translate` VALUES (3037, '事件日志删除', 'Event log deletion');
INSERT INTO `sys_menu_translate` VALUES (3038, '事件日志导出', 'Event log export');
INSERT INTO `sys_menu_translate` VALUES (3044, '服务下发', 'Service Issuance');
INSERT INTO `sys_menu_translate` VALUES (3046, '视频中心', 'Video Center');
INSERT INTO `sys_menu_translate` VALUES (3047, '分屏显示', 'Split screen display');
INSERT INTO `sys_menu_translate` VALUES (3048, '视频配置', 'Video configuration');
INSERT INTO `sys_menu_translate` VALUES (3049, '数据可视化', 'Data visualization');
INSERT INTO `sys_menu_translate` VALUES (3051, '规则引擎', 'Rule engine');
INSERT INTO `sys_menu_translate` VALUES (3052, '可视化大屏', 'Visual large screen');
INSERT INTO `sys_menu_translate` VALUES (3055, '规则脚本', 'Rule Script');
INSERT INTO `sys_menu_translate` VALUES (3099, '录像管理', 'Video management');
INSERT INTO `sys_menu_translate` VALUES (3100, 'Mqtt统计', 'Mqtt statistics');
INSERT INTO `sys_menu_translate` VALUES (3102, '通知渠道', 'Notification channels');
INSERT INTO `sys_menu_translate` VALUES (3103, '通知渠道查询', 'Notification channel inquiry');
INSERT INTO `sys_menu_translate` VALUES (3104, '通知渠道新增', 'Notification channel added');
INSERT INTO `sys_menu_translate` VALUES (3105, '通知渠道修改', 'Notification channel modification');
INSERT INTO `sys_menu_translate` VALUES (3106, '通知渠道删除', 'Notification channel deletion');
INSERT INTO `sys_menu_translate` VALUES (3107, '通知渠道导出', 'Notification channel export');
INSERT INTO `sys_menu_translate` VALUES (3108, '通知模板', 'Notification Template');
INSERT INTO `sys_menu_translate` VALUES (3109, '通知模板查询', 'Notification template query');
INSERT INTO `sys_menu_translate` VALUES (3110, '通知模板新增', 'Notification Template Added');
INSERT INTO `sys_menu_translate` VALUES (3111, '通知模板修改', 'Notification Template Modification');
INSERT INTO `sys_menu_translate` VALUES (3112, '通知模板删除', 'Notification template deletion');
INSERT INTO `sys_menu_translate` VALUES (3113, '通知模板导出', 'Notification Template Export');
INSERT INTO `sys_menu_translate` VALUES (3114, '通知模板测试', 'Notification Template Testing');
INSERT INTO `sys_menu_translate` VALUES (3115, '通知日志', 'Notification log');
INSERT INTO `sys_menu_translate` VALUES (3116, '通知日志导出', 'Notification log export');
INSERT INTO `sys_menu_translate` VALUES (3117, '通知日志删除', 'Notification log deletion');
INSERT INTO `sys_menu_translate` VALUES (3147, '告警配置', 'Alarm configuration');
INSERT INTO `sys_menu_translate` VALUES (3148, '规则脚本导出', 'Rule script export');
INSERT INTO `sys_menu_translate` VALUES (3149, '规则脚本查询', 'Rule script query');
INSERT INTO `sys_menu_translate` VALUES (3150, '规则脚本新增', 'New rule script added');
INSERT INTO `sys_menu_translate` VALUES (3151, '规则脚本修改', 'Rule script modification');
INSERT INTO `sys_menu_translate` VALUES (3152, '规则脚本删除', 'Rule script deletion');
INSERT INTO `sys_menu_translate` VALUES (3153, '告警配置导出', 'Alarm configuration export');
INSERT INTO `sys_menu_translate` VALUES (3154, '告警配置查询', 'Alarm configuration query');
INSERT INTO `sys_menu_translate` VALUES (3155, '告警配置新增', 'Alarm configuration added');
INSERT INTO `sys_menu_translate` VALUES (3156, '告警配置修改', 'Alarm configuration modification');
INSERT INTO `sys_menu_translate` VALUES (3157, '告警配置删除', 'Alarm configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (3158, '通知日志详情', 'Notification log details');
INSERT INTO `sys_menu_translate` VALUES (3159, '组态管理', 'Configuration management');
INSERT INTO `sys_menu_translate` VALUES (3160, '独立组态', 'Independent configuration');
INSERT INTO `sys_menu_translate` VALUES (3161, '组态中心查询', 'Configuration Center Query');
INSERT INTO `sys_menu_translate` VALUES (3162, '组态中心新增', 'New configuration center added');
INSERT INTO `sys_menu_translate` VALUES (3163, '组态中心修改', 'Configuration Center Modification');
INSERT INTO `sys_menu_translate` VALUES (3164, '组态中心删除', 'Delete configuration center');
INSERT INTO `sys_menu_translate` VALUES (3165, '组态中心导出', 'Export from Configuration Center');
INSERT INTO `sys_menu_translate` VALUES (3166, '图表管理', 'Chart management');
INSERT INTO `sys_menu_translate` VALUES (3167, '图表管理查询', 'Chart management query');
INSERT INTO `sys_menu_translate` VALUES (3168, '图表管理新增', 'Chart Management Added');
INSERT INTO `sys_menu_translate` VALUES (3169, '图表管理修改', 'Chart management modification');
INSERT INTO `sys_menu_translate` VALUES (3170, '图表管理删除', 'Chart management deletion');
INSERT INTO `sys_menu_translate` VALUES (3171, '图表管理导出', 'Chart management export');
INSERT INTO `sys_menu_translate` VALUES (3172, '图库管理', 'Library Management');
INSERT INTO `sys_menu_translate` VALUES (3173, '图库管理查询', 'Library management query');
INSERT INTO `sys_menu_translate` VALUES (3174, '图库管理新增', 'Library Management Added');
INSERT INTO `sys_menu_translate` VALUES (3175, '图库管理修改', 'Library management modification');
INSERT INTO `sys_menu_translate` VALUES (3176, '图库管理删除', 'Library Management Delete');
INSERT INTO `sys_menu_translate` VALUES (3177, '图库管理导出', 'Library Management Export');
INSERT INTO `sys_menu_translate` VALUES (3178, '模型管理', 'Model management');
INSERT INTO `sys_menu_translate` VALUES (3179, '模型管理查询', 'Model management query');
INSERT INTO `sys_menu_translate` VALUES (3180, '模型管理新增', 'New Model Management');
INSERT INTO `sys_menu_translate` VALUES (3181, '模型管理修改', 'Model management modification');
INSERT INTO `sys_menu_translate` VALUES (3182, '模型管理删除', 'Model management deletion');
INSERT INTO `sys_menu_translate` VALUES (3183, '模型管理导出', 'Model management export');
INSERT INTO `sys_menu_translate` VALUES (3184, '组件管理', 'Component Management');
INSERT INTO `sys_menu_translate` VALUES (3185, '组件管理查询', 'Component management query');
INSERT INTO `sys_menu_translate` VALUES (3186, '组件管理新增', 'New Component Management');
INSERT INTO `sys_menu_translate` VALUES (3187, '组件管理修改', 'Component management modification');
INSERT INTO `sys_menu_translate` VALUES (3188, '组件管理删除', 'Component management deletion');
INSERT INTO `sys_menu_translate` VALUES (3189, '组件管理导出', 'Component management export');
INSERT INTO `sys_menu_translate` VALUES (3190, '云云对接', 'Cloud to Cloud Connection');
INSERT INTO `sys_menu_translate` VALUES (3191, '音箱配置', 'Speaker configuration');
INSERT INTO `sys_menu_translate` VALUES (3197, '小度音箱', 'Xiaodu speaker');
INSERT INTO `sys_menu_translate` VALUES (3198, '关联产品查询', 'Related product query');
INSERT INTO `sys_menu_translate` VALUES (3199, '关联物模查询', 'Related Model Query');
INSERT INTO `sys_menu_translate` VALUES (3200, '关联产品新增', 'Add related products');
INSERT INTO `sys_menu_translate` VALUES (3201, '关联产品删除', 'Related product deletion');
INSERT INTO `sys_menu_translate` VALUES (3202, '关联产品编辑', 'Related product editing');
INSERT INTO `sys_menu_translate` VALUES (3203, '关联物模编辑', 'Related Model Editing');
INSERT INTO `sys_menu_translate` VALUES (3204, '关联物模删除', 'Related object model deletion');
INSERT INTO `sys_menu_translate` VALUES (3205, '组态详情预览', 'Preview of configuration details');
INSERT INTO `sys_menu_translate` VALUES (3206, '产品模型列表', 'Product Model List');
INSERT INTO `sys_menu_translate` VALUES (3207, '产品模型详情', 'Product Model Details');
INSERT INTO `sys_menu_translate` VALUES (3208, '产品模型导入', 'Product model import');
INSERT INTO `sys_menu_translate` VALUES (3209, '产品模型新增', 'Product Model Addition');
INSERT INTO `sys_menu_translate` VALUES (3210, '产品模型修改', 'Product model modification');
INSERT INTO `sys_menu_translate` VALUES (3211, '产品模型删除', 'Product model deletion');
INSERT INTO `sys_menu_translate` VALUES (3214, '设备定时详情', 'Equipment timing details');
INSERT INTO `sys_menu_translate` VALUES (3215, '设备定时新增', 'Equipment scheduled addition');
INSERT INTO `sys_menu_translate` VALUES (3216, '设备定时修改', 'Equipment scheduled modification');
INSERT INTO `sys_menu_translate` VALUES (3217, '设备定时执行', 'Equipment scheduled execution');
INSERT INTO `sys_menu_translate` VALUES (3218, '设备定时删除', 'Equipment scheduled deletion');
INSERT INTO `sys_menu_translate` VALUES (3219, '设备用户详情', 'Device User Details');
INSERT INTO `sys_menu_translate` VALUES (3220, '分享设备', 'Share devices');
INSERT INTO `sys_menu_translate` VALUES (3221, '设备用户修改', 'Device user modification');
INSERT INTO `sys_menu_translate` VALUES (3222, '取消分享', 'Cancel sharing');
INSERT INTO `sys_menu_translate` VALUES (3223, '录像管理列表', 'Video management list');
INSERT INTO `sys_menu_translate` VALUES (3224, '查看录像', 'View video recording');
INSERT INTO `sys_menu_translate` VALUES (3225, '录像下载', 'Video download');
INSERT INTO `sys_menu_translate` VALUES (3226, '场景联动执行', 'Scene linkage execution');
INSERT INTO `sys_menu_translate` VALUES (3227, '字典刷新', 'Dictionary refresh');
INSERT INTO `sys_menu_translate` VALUES (3228, '参数刷新', 'Parameter refresh');
INSERT INTO `sys_menu_translate` VALUES (3229, '三方登录详情', 'Three party login details');
INSERT INTO `sys_menu_translate` VALUES (3230, '三方登录新增', 'Three party login added');
INSERT INTO `sys_menu_translate` VALUES (3231, '三方登录修改', 'Three party login modification');
INSERT INTO `sys_menu_translate` VALUES (3232, '三方登录导出', 'Three party login export');
INSERT INTO `sys_menu_translate` VALUES (3233, '三方登录删除', 'Three party login deletion');
INSERT INTO `sys_menu_translate` VALUES (3234, '任务执行', 'Task Execution');
INSERT INTO `sys_menu_translate` VALUES (3235, '缓存列表', 'Cache List');
INSERT INTO `sys_menu_translate` VALUES (3236, '缓存删除', 'Cache deletion');
INSERT INTO `sys_menu_translate` VALUES (3237, '客户端列表', 'Client List');
INSERT INTO `sys_menu_translate` VALUES (3238, '客户端踢出', 'Client kicked out');
INSERT INTO `sys_menu_translate` VALUES (3239, '设备分配', 'Equipment allocation');
INSERT INTO `sys_menu_translate` VALUES (3240, '设备回收', 'Equipment recycling');
INSERT INTO `sys_menu_translate` VALUES (3247, '设备分享列表', 'Equipment Sharing List');
INSERT INTO `sys_menu_translate` VALUES (3248, '设备分享用户', 'Device Sharing Users');
INSERT INTO `sys_menu_translate` VALUES (3249, '设备分享详情', 'Device Sharing Details');
INSERT INTO `sys_menu_translate` VALUES (3250, '设备分享新增', 'New device sharing');
INSERT INTO `sys_menu_translate` VALUES (3251, '设备分享修改', 'Device sharing and modification');
INSERT INTO `sys_menu_translate` VALUES (3252, '设备分享删除', 'Device sharing and deletion');
INSERT INTO `sys_menu_translate` VALUES (3253, '设备分享导出', 'Device sharing export');
INSERT INTO `sys_menu_translate` VALUES (3254, '设备定时导出', 'Equipment timed export');
INSERT INTO `sys_menu_translate` VALUES (3255, '设备联动', 'Equipment linkage');
INSERT INTO `sys_menu_translate` VALUES (3256, '终端用户', 'end user');
INSERT INTO `sys_menu_translate` VALUES (3257, '告警用户', 'Alert user');
INSERT INTO `sys_menu_translate` VALUES (3258, '告警用户查询', 'Alarm user query');
INSERT INTO `sys_menu_translate` VALUES (3259, '告警用户新增', 'Alarm user addition');
INSERT INTO `sys_menu_translate` VALUES (3260, '告警用户删除', 'Alarm user to delete');
INSERT INTO `sys_menu_translate` VALUES (3261, '文件记录', 'files recording');
INSERT INTO `sys_menu_translate` VALUES (3262, '文件记录查询', 'File record query');
INSERT INTO `sys_menu_translate` VALUES (3263, '文件记录新增', 'File record addition');
INSERT INTO `sys_menu_translate` VALUES (3264, '文件记录修改', 'Document record modification');
INSERT INTO `sys_menu_translate` VALUES (3265, '文件记录删除', 'File record deletion');
INSERT INTO `sys_menu_translate` VALUES (3266, '文件记录导出', 'Export file records');
INSERT INTO `sys_menu_translate` VALUES (3267, '文件存储配置新增', 'New file storage configuration added');
INSERT INTO `sys_menu_translate` VALUES (3268, '文件存储配置修改', 'File storage configuration modification');
INSERT INTO `sys_menu_translate` VALUES (3269, '文件存储配置删除', 'File storage configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (3270, '数据桥接', 'Data bridging');
INSERT INTO `sys_menu_translate` VALUES (3271, '数据桥接查询', 'Data bridging query');
INSERT INTO `sys_menu_translate` VALUES (3272, '数据桥接新增', 'New addition of data bridging');
INSERT INTO `sys_menu_translate` VALUES (3273, '数据桥接修改', 'Data bridging modification');
INSERT INTO `sys_menu_translate` VALUES (3274, '数据桥接删除', 'Data bridging deletion');
INSERT INTO `sys_menu_translate` VALUES (3275, '数据桥接导出', 'Data Bridge Export');
INSERT INTO `sys_menu_translate` VALUES (3276, '密码修改', 'Password modification');
INSERT INTO `sys_menu_translate` VALUES (3277, '设备编号批量生成', 'Batch generation of device numbers');
INSERT INTO `sys_menu_translate` VALUES (3278, '设备记录导出', 'Export device records');
INSERT INTO `sys_menu_translate` VALUES (3279, '设备记录', 'Equipment records');
INSERT INTO `sys_menu_translate` VALUES (3280, '系统授权', 'System authorization');
INSERT INTO `sys_menu_translate` VALUES (3281, '系统授权查询', 'System authorization query');
INSERT INTO `sys_menu_translate` VALUES (3282, '系统授权新增', 'System authorization added');
INSERT INTO `sys_menu_translate` VALUES (3283, '系统授权修改', 'System authorization modification');
INSERT INTO `sys_menu_translate` VALUES (3284, '系统授权删除', 'System authorization deletion');
INSERT INTO `sys_menu_translate` VALUES (3285, '系统授权导出', 'System authorization export');
INSERT INTO `sys_menu_translate` VALUES (3286, '产品modbus配置参数', 'Product Modbus configuration parameters');
INSERT INTO `sys_menu_translate` VALUES (3287, '产品modbus配置参数查询', 'Product Modbus configuration parameter query');
INSERT INTO `sys_menu_translate` VALUES (3288, '产品modbus配置参数新增', 'New configuration parameters for product Modbus');
INSERT INTO `sys_menu_translate` VALUES (3289, '产品modbus配置参数修改', 'Product Modbus configuration parameter modification');
INSERT INTO `sys_menu_translate` VALUES (3290, '产品modbus配置参数删除', 'Product Modbus configuration parameter deletion');
INSERT INTO `sys_menu_translate` VALUES (3291, '产品modbus配置参数导出', 'Product Modbus configuration parameter export');
INSERT INTO `sys_menu_translate` VALUES (3292, '轮训任务', 'Rotation training task');
INSERT INTO `sys_menu_translate` VALUES (3293, '轮训任务查询', 'Rotation training task query');
INSERT INTO `sys_menu_translate` VALUES (3294, '轮训任务新增', 'New rotation training tasks added');
INSERT INTO `sys_menu_translate` VALUES (3295, '轮训任务修改', 'Modification of rotation training tasks');
INSERT INTO `sys_menu_translate` VALUES (3296, '轮训任务删除', 'Delete rotation training task');
INSERT INTO `sys_menu_translate` VALUES (3297, '轮训任务导出', 'Export rotation training tasks');
INSERT INTO `sys_menu_translate` VALUES (3298, 'modbus配置', 'Modbus configuration');
INSERT INTO `sys_menu_translate` VALUES (3299, 'modbus配置查询', 'Modbus configuration query');
INSERT INTO `sys_menu_translate` VALUES (3300, 'modbus配置新增', 'Modbus configuration added');
INSERT INTO `sys_menu_translate` VALUES (3301, 'modbus配置修改', 'Modbus configuration modification');
INSERT INTO `sys_menu_translate` VALUES (3302, 'modbus配置删除', 'Modbus configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (3303, 'modbus配置导出', 'Modbus configuration export');
INSERT INTO `sys_menu_translate` VALUES (3304, '指令偏好设置', 'Instruction preference settings');
INSERT INTO `sys_menu_translate` VALUES (3305, '指令偏好设置查询', 'Instruction preference setting query');
INSERT INTO `sys_menu_translate` VALUES (3306, '指令偏好设置新增', 'New instruction preference settings added');
INSERT INTO `sys_menu_translate` VALUES (3307, '指令偏好设置修改', 'Command preference setting modification');
INSERT INTO `sys_menu_translate` VALUES (3308, '指令偏好设置删除', 'Delete command preference settings');
INSERT INTO `sys_menu_translate` VALUES (3309, '指令偏好设置导出', 'Export command preference settings');
INSERT INTO `sys_menu_translate` VALUES (3310, '指令权限', 'Command Authority');
INSERT INTO `sys_menu_translate` VALUES (3311, '指令权限控制查询', 'Command permission control query');
INSERT INTO `sys_menu_translate` VALUES (3312, '指令权限控制新增', 'Instruction permission control added');
INSERT INTO `sys_menu_translate` VALUES (3313, '指令权限控制修改', 'Command permission control modification');
INSERT INTO `sys_menu_translate` VALUES (3314, '指令权限控制删除', 'Command permission control deletion');
INSERT INTO `sys_menu_translate` VALUES (3315, '指令权限控制导出', 'Command permission control export');
INSERT INTO `sys_menu_translate` VALUES (3316, '场景管理', 'Scene management');
INSERT INTO `sys_menu_translate` VALUES (3317, '场景列表', 'Scene List');
INSERT INTO `sys_menu_translate` VALUES (3318, '变量列表', 'Variable List');
INSERT INTO `sys_menu_translate` VALUES (3319, '场景变量启用', 'Enable scene variables');
INSERT INTO `sys_menu_translate` VALUES (3320, '场景列表查询', 'Scene List Query');
INSERT INTO `sys_menu_translate` VALUES (3321, '场景列表新增', 'New scene list added');
INSERT INTO `sys_menu_translate` VALUES (3322, '场景列表修改', 'Scene list modification');
INSERT INTO `sys_menu_translate` VALUES (3323, '场景列表删除', 'Scene list deletion');
INSERT INTO `sys_menu_translate` VALUES (3324, '场景组态设计', 'Scene configuration design');
INSERT INTO `sys_menu_translate` VALUES (3325, '场景组态运行', 'Scenario configuration operation');
INSERT INTO `sys_menu_translate` VALUES (3326, '视频监控', 'Video surveillance');
INSERT INTO `sys_menu_translate` VALUES (3327, '设备配置列表', 'Equipment configuration list');
INSERT INTO `sys_menu_translate` VALUES (3328, '设备配置新增', 'New device configuration');
INSERT INTO `sys_menu_translate` VALUES (3329, '设备配置修改', 'Equipment configuration modification');
INSERT INTO `sys_menu_translate` VALUES (3330, '设备配置删除', 'Device configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (3331, '设备变量列表', 'List of device variables');
INSERT INTO `sys_menu_translate` VALUES (3332, '场景变量列表', 'List of scene variables');
INSERT INTO `sys_menu_translate` VALUES (3333, '场景变量查看', 'View scene variables');
INSERT INTO `sys_menu_translate` VALUES (3334, '场景变量新增', 'New scene variables added');
INSERT INTO `sys_menu_translate` VALUES (3335, '场景变量修改', 'Scene variable modification');
INSERT INTO `sys_menu_translate` VALUES (3336, '场景变量删除', 'Scene variable deletion');
INSERT INTO `sys_menu_translate` VALUES (3337, '国际化配置', 'International configuration');
INSERT INTO `sys_menu_translate` VALUES (3338, '国际化配置新增', 'International configuration added');
INSERT INTO `sys_menu_translate` VALUES (3339, '国际化配置查询', 'Internationalization configuration query');
INSERT INTO `sys_menu_translate` VALUES (3340, '国际化配置修改', 'Internationalization configuration modification');
INSERT INTO `sys_menu_translate` VALUES (3341, '国际化配置删除', 'Internationalization configuration deletion');
INSERT INTO `sys_menu_translate` VALUES (3342, '国际化配置导出', 'International configuration export');
INSERT INTO `sys_menu_translate` VALUES (3343, '数据中心', 'Data Center');
INSERT INTO `sys_menu_translate` VALUES (3344, '数据分析', 'Data analysis');
INSERT INTO `sys_menu_translate` VALUES (3345, '历史记录', 'Historical records');
INSERT INTO `sys_menu_translate` VALUES (3346, '历史记录查询', 'Historical record query');
INSERT INTO `sys_menu_translate` VALUES (3347, '模板组态', 'Template configuration');
INSERT INTO `sys_menu_translate` VALUES (3348, '场景组态', 'Scene configuration');
INSERT INTO `sys_menu_translate` VALUES (3349, '平台下发指令', 'Platform issues instructions');
INSERT INTO `sys_menu_translate` VALUES (3350, '指令编码', 'Instruction encoding');
INSERT INTO `sys_menu_translate` VALUES (3351, '指令解码', 'Instruction decoding');
INSERT INTO `sys_menu_translate` VALUES (3352, '子设备', 'Sub devices');
INSERT INTO `sys_menu_translate` VALUES (3353, '子设备详情', 'Details of sub devices');
INSERT INTO `sys_menu_translate` VALUES (3354, '子设备新增', 'Addition of sub devices');
INSERT INTO `sys_menu_translate` VALUES (3355, '子设备修改', 'Sub device modification');
INSERT INTO `sys_menu_translate` VALUES (3356, '子设备删除', 'Delete sub devices');
INSERT INTO `sys_menu_translate` VALUES (3357, '组态中心分享', 'Configuration Center Sharing');
INSERT INTO `sys_menu_translate` VALUES (3358, '设备绑定', 'Device binding');
INSERT INTO `sys_menu_translate` VALUES (3359, '监控设备关联列表', 'Watch device association list');
INSERT INTO `sys_menu_translate` VALUES (3360, '监控设备关联详细', 'Watch device association detail');
INSERT INTO `sys_menu_translate` VALUES (3361, '监控设备关联修改', 'Watch device association edit');
INSERT INTO `sys_menu_translate` VALUES (3362, '监控设备关联删除', 'Watch device association remove');
                                              '
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3349, '平台下发指令', 2007, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:message:post', '#', 'admin', '2024-08-02 09:59:50', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3350, '指令编码', 2007, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:message:encode', '#', 'admin', '2024-08-02 10:00:10', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3351, '指令解码', 2007, 12, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:message:decode', '#', 'admin', '2024-08-02 10:00:25', '', NULL, '');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3352, '子设备', 2007, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'sub:gateway:list', '#', 'admin', '2024-08-06 14:26:36', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3353, '子设备详情', 3352, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'sub:gateway:query', '#', 'admin', '2024-08-06 14:27:26', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3354, '子设备新增', 3352, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'sub:gateway:add', '#', 'admin', '2024-08-06 14:27:37', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3355, '子设备修改', 3352, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'sub:gateway:edit', '#', 'admin', '2024-08-06 14:28:08', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3356, '子设备删除', 3352, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'sub:gateway:remove', '#', 'admin', '2024-08-06 14:28:27', '', NULL, '');

INSERT INTO `sys_menu` VALUES (3357, '组态中心分享', 3159, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'scada:center:share', '#', 'admin', '2024-08-19 17:31:42', 'admin', '2024-08-19 17:32:00', '');
INSERT INTO `sys_menu` VALUES (3358, '设备绑定', 2168, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:relation:add', '#', 'admin', '2024-08-06 14:28:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3359, '监控设备关联列表', 2168, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:relation:list', '#', 'admin', '2024-09-26 15:42:23', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3360, '监控设备关联详细', 2168, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:relation:query', '#', 'admin', '2024-09-26 15:43:06', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3361, '监控设备关联修改', 2168, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:relation:edit', '#', 'admin', '2024-09-26 15:44:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3362, '监控设备关联删除', 2168, 9, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:relation:remove', '#', 'admin', '2024-09-26 15:45:10', '', NULL, '');

-- 新增组态分享用户角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('组态分享', 'scadaShare', 7, '1', 0, 1, '0', '0', 'admin', '2024-08-27 09:36:58', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (@parentId, 100);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2007);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2008);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2067);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2168);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3044);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3046);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3159);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3161);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3173);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3185);

INSERT INTO `sys_user` (`dept_id`, `user_name`, `nick_name`, `status`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (100, 'fastbee_scada', '组态分享者', '0', '00', '', '15888888889', '0', '', '$2a$10$u82ntKWR11ELMWlKadzJ9ubbal6ymBzdXJndpi8CdfWWPwCpuwi9O', '0', '', NULL, 'admin', '2024-08-27 09:42:45', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_user_role` (`user_id`, `role_id`) SELECT @parentId, `role_id` from sys_role where role_key = 'scadaShare';
