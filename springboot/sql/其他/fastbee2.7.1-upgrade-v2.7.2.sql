-- 数据库版本升级脚本
-- 适用于fastbee2.7.1版本到fastbee2.7.2版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (174, '组态模型类型', 'scada_model_type', 0, 'admin', '2025-08-11 16:10:33', 'admin', '2025-08-14 14:56:59', NULL);
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (174, '组态模型类型', 'Configuration model type');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (767, 1, 'GLB', 'glb', 'scada_model_type', NULL, 'default', 'N', 0, 'admin', '2025-08-11 16:11:22', 'admin', '2025-08-11 16:23:35', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (768, 1, 'FBX', 'fbx', 'scada_model_type', NULL, 'primary', 'N', 0, 'admin', '2025-08-14 17:08:02', 'admin', '2025-08-14 17:10:30', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (769, 2, 'GLTF', 'gltf', 'scada_model_type', NULL, 'default', 'N', 0, 'admin', '2025-08-11 16:11:48', 'admin', '2025-08-11 16:23:54', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (770, 3, 'OBJ', 'obj', 'scada_model_type', NULL, 'default', 'N', 0, 'admin', '2025-08-11 16:12:10', 'admin', '2025-08-11 16:24:23', NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (767, 'GLB', 'GLB');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (768, 'FBX', 'FBX');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (769, 'GLTF', 'GLTF');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (770, 'OBJ', 'OBJ');

-- 工单管理
CREATE TABLE `iot_work_order` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                  `name` varchar(200) NOT NULL COMMENT '工单名称',
                                  `details` text NOT NULL COMMENT '工单详细信息',
                                  `status` tinyint(2) NOT NULL COMMENT '工单状态',
                                  `type` tinyint(2) NOT NULL COMMENT '工单类型',
                                  `number` varchar(50) NOT NULL COMMENT '工单编号',
                                  `user_id` bigint(20) DEFAULT NULL COMMENT '联系人',
                                  `device_id` bigint(20) NOT NULL COMMENT '设备id',
                                  `end_time` datetime DEFAULT NULL COMMENT '截止时间',
                                  `result` text COMMENT '处理结果',
                                  `tenant_id` bigint(20) NOT NULL COMMENT '所属租户id',
                                  `tenant_name` varchar(30) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '租户名称',
                                  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
                                  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='工单管理';

CREATE TABLE `object_operation_log` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                        `object_id` bigint(20) NOT NULL COMMENT '对象主键id',
                                        `type` tinyint(2) NOT NULL COMMENT '类型',
                                        `content` text NOT NULL COMMENT '变更内容',
                                        `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='对象操作记录表';

ALTER TABLE `iot_alert`
    ADD COLUMN `generate_work_order` tinyint(1) NULL DEFAULT 0 COMMENT '生成工单（1-是，0-否）';

-- 菜单 SQL
INSERT INTO `sys_menu` VALUES (3406, '工单管理', 3000, 1, 'workOrder', 'iot/workOrder/index', NULL, 1, 0, 'C', '0', 0, 'iot:workOrder:list', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '工单管理菜单');
INSERT INTO `sys_menu` VALUES (3407, '工单管理查询', 3406, 1, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:query', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3408, '工单管理新增', 3406, 2, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:add', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3409, '工单管理修改', 3406, 3, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:edit', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3410, '工单管理删除', 3406, 4, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:remove', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3411, '工单管理导出', 3406, 5, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:export', '#', 'admin', '2025-08-18 11:32:58', '', NULL, '');

INSERT INTO `sys_menu_translate` VALUES (3406, '工单管理', 'Work order');
INSERT INTO `sys_menu_translate` VALUES (3407, '工单管理查询', 'Work order query');
INSERT INTO `sys_menu_translate` VALUES (3408, '工单管理新增', 'Work order add');
INSERT INTO `sys_menu_translate` VALUES (3409, '工单管理修改', 'Work order edit');
INSERT INTO `sys_menu_translate` VALUES (3410, '工单管理删除', 'Work order deleted');
INSERT INTO `sys_menu_translate` VALUES (3411, '工单管理导出', 'Work order export');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (3418, '我的工单', 3000, 2, 'myWorkOrder', 'iot/workOrder/myWorkOrder', NULL, 1, 0, 'C', '0', 0, 'iot:workOrder:listMyself', '#', 'admin', '2025-08-21 09:38:32', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (3419, '终端用户新增', 3418, 1, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:endUserAdd', '#', 'admin', '2025-08-21 09:48:37', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (3420, '终端用户查询', 3418, 2, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:workOrder:endUserQuery', '#', 'admin', '2025-08-29 15:10:55', '', NULL, '');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3418, '我的工单', 'My ticket');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3419, '终端用户新增', 'End users are added');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3420, '终端用户查询', 'End-user queries');

INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (7, '工单定时强制结单', 'DEFAULT', 'workOrderServiceImpl.timingMandatoryBilling', '0 0 0 * * ?', '1', '1', 0, 'admin', NULL, 'admin', '2025-08-26 17:17:15', '');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (175, '工单类型', 'work_order_type', 0, 'admin', '2025-08-18 15:06:18', '', NULL, NULL);
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (176, '工单状态', 'work_order_status', 0, 'admin', '2025-08-18 15:09:27', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (175, '工单类型', 'Ticket Type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (176, '工单状态', 'Ticket Status');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (771, 1, '常规维护', '1', 'work_order_type', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:06:57', 'admin', '2025-09-04 16:33:53', '{\n\"description\":\"\",\n\"image\":[]\n}');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (772, 2, '设备故障', '2', 'work_order_type', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:07:20', 'admin', '2025-09-04 16:33:59', '{\n\"description\":\"\",\n\"image\":[]\n}');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (773, 3, '设备安装', '3', 'work_order_type', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:07:43', 'admin', '2025-09-04 16:34:18', '{\n\"location\":\"\",\n\"description\":\"\",\n\"image\":[]\n}');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (774, 4, '设备告警', '4', 'work_order_type', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:08:10', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (775, 5, '用户反馈', '5', 'work_order_type', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:08:35', '', NULL, '{\n\"description\":\"\",\n\"image\":[]\n}');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (776, 1, '已结单', '1', 'work_order_status', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:09:50', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (777, 2, '待处理', '2', 'work_order_status', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:14:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (778, 3, '已派单', '3', 'work_order_status', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:15:38', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (779, 4, '已接单', '4', 'work_order_status', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:16:04', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (780, 5, '强制结单', '5', 'work_order_status', NULL, 'default', 'N', 0, 'admin', '2025-08-18 15:16:34', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (771, '常规维护', 'Routine maintenance');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (772, '设备故障', 'Equipment failure');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (773, '设备安装', 'Equipment installation');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (774, '设备告警', 'Device Alarm');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (775, '用户反馈', 'user feedback');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (776, '已结单', 'Closed Order');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (777, '待处理', 'Pending');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (778, '已派单', 'Order dispatched');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (779, '已接单', 'Order received');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (780, '强制结单', 'Mandatory billing');

INSERT INTO `sys_menu` VALUES (3412, 'APP版本', 1, 1, 'version', 'system/appVersion/index', NULL, 1, 0, 'C', '0', 0, 'iot:version:list', '#', 'admin', '2025-08-18 16:06:46', '', NULL, 'APP版本菜单');
INSERT INTO `sys_menu` VALUES (3413, 'APP版本查询', 3412, 1, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:version:query', '#', 'admin', '2025-08-18 16:06:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3414, 'APP版本新增', 3412, 2, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:version:add', '#', 'admin', '2025-08-18 16:06:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3415, 'APP版本修改', 3412, 3, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:version:edit', '#', 'admin', '2025-08-18 16:06:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3416, 'APP版本删除', 3412, 4, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:version:remove', '#', 'admin', '2025-08-18 16:06:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3417, 'APP版本导出', 3412, 5, '#', '', NULL, 1, 0, 'F', '0', 0, 'iot:version:export', '#', 'admin', '2025-08-18 16:06:46', '', NULL, '');

INSERT INTO `sys_menu_translate` VALUES (3412, 'APP版本', 'APP version');
INSERT INTO `sys_menu_translate` VALUES (3413, 'APP版本查询', 'APP version query');
INSERT INTO `sys_menu_translate` VALUES (3414, 'APP版本新增', 'APP version add');
INSERT INTO `sys_menu_translate` VALUES (3415, 'APP版本修改', 'APP version edit');
INSERT INTO `sys_menu_translate` VALUES (3416, 'APP版本删除', 'APP version deleted');
INSERT INTO `sys_menu_translate` VALUES (3417, 'APP版本导出', 'APP version export');

ALTER TABLE `iot_scene_script`
    ADD COLUMN `notify_count` int(11) NULL DEFAULT NULL COMMENT '告警通知次数';

update iot_scene_script set notify_count = 1 where source = 4;

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (781, 4, '工单通知', 'workOrder', 'notify_service_code', NULL, 'default', 'N', 0, 'admin', '2025-09-10 17:07:02', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (781, '工单通知', 'Work Order Notification');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (782, 5, '报表通知', 'report', 'notify_service_code', NULL, 'default', 'N', 0, 'admin', '2025-09-17 17:07:02', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (782, '报表通知', 'Report Notification');

ALTER TABLE `report`
    ADD COLUMN `notify_users` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通知用户';

INSERT INTO `notify_template` (`name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`)
VALUES ('工单QQ邮箱', 'workOrder', 5, 'email', 'qq', '{\"sendAccount\":\"\",\"title\":\"派发工单\",\"attachment\":\"\",\"content\":\"<p>派单人：#{createBy}，工单名称：#{name}，工单编号：#{number}，工单类型：#{workOrderType}，设备名称：#{deviceName}，设备编号：#{serialnumber}</p>\"}', 1, 'admin', '2025-09-11 10:51:41', 'admin', '2025-09-12 15:44:51', 0, 1, '蜂信物联');
INSERT INTO `notify_template` (`name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`)
VALUES ('报表QQ邮箱', 'report', 5, 'email', 'qq', '{\"sendAccount\":\"\",\"title\":\"报表\",\"attachment\":\"\",\"content\":\"<p>报表名称：#{name}， 状态：#{statusDesc}，下载地址：https://localhost/prod-api#{uploadPath}</p>\"}', 1, 'admin', '2025-09-18 17:04:05', 'admin', '2025-09-19 10:22:10', 0, 1, '蜂信物联');


ALTER TABLE `sip_device_channel`
    ADD COLUMN `play_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '通道播放地址',
    ADD COLUMN `proxy_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '流代理地址';

ALTER TABLE `sip_device_channel` DROP COLUMN `tenant_name`;
ALTER TABLE `sip_device_channel` DROP COLUMN `user_id`;
ALTER TABLE `sip_device_channel` DROP COLUMN `block`;
ALTER TABLE `sip_device_channel` DROP COLUMN `address`;
ALTER TABLE `sip_device_channel` DROP COLUMN `password`;
ALTER TABLE `sip_device_channel` DROP COLUMN `sub_count`;
ALTER TABLE `sip_device_channel` DROP COLUMN `parental`;


UPDATE `sys_dict_data` SET dict_value = '2', dict_sort = 2 WHERE dict_type = 'rule_engine_status' and dict_value = '0';


CREATE TABLE `app_version` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `version` varchar(255) DEFAULT NULL COMMENT '版本号',
                               `version_name` varchar(255) DEFAULT NULL COMMENT '版本名称',
                               `is_live_update` char(1) DEFAULT NULL COMMENT '是否热更新',
                               `apk` longtext COMMENT 'apk链接',
                               `wgt` longtext COMMENT 'wgt链接',
                               `update_content` varchar(1000) DEFAULT NULL COMMENT '更新内容',
                               `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                               `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='APP版本';

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (783, 12, 'URL拉流', '600', 'video_type', NULL, 'default', 'N', 0, 'admin', '2025-08-11 16:11:22', 'admin', '2025-08-11 16:23:35', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (784, 10, 'URL拉流', '600', 'channel_type', NULL, 'default', 'N', 0, 'admin', '2025-08-14 17:08:02', 'admin', '2025-08-14 17:10:30', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (785, 11, 'URL代理拉流', '601', 'channel_type', NULL, 'default', 'N', 0, 'admin', '2025-08-11 16:11:48', 'admin', '2025-08-11 16:23:54', NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (783, 'URL拉流', 'URL streaming');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (784, 'URL拉流', 'URL streaming');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (785, 'URL代理拉流', 'URL proxy streaming');


ALTER TABLE iot_alert_log ADD INDEX `iot_alert_log_index_scene_id` (`scene_id`) USING BTREE;
