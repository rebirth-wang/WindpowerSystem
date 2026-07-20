-- 数据库版本升级脚本
-- 适用于fastbee2.7.0版本到fastbee2.7.1版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作
CREATE TABLE `iot_things_model_tag` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                        `identifier` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '物模型标识',
                                        `alias` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '别名，如 A',
                                        `model_id` bigint(20) NOT NULL COMMENT '关联的模型id',
                                        `operation` int(2) NOT NULL COMMENT '统计方式 ，用字典定义，暂时是”原值“',
                                        `source_model_id` bigint(20) DEFAULT NULL COMMENT '数据源模型id',
                                        `del_flag` char(1) CHARACTER SET utf8 DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                        `create_by` varchar(64) CHARACTER SET utf8 DEFAULT '' COMMENT '创建者',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_by` varchar(64) CHARACTER SET utf8 DEFAULT '' COMMENT '更新者',
                                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='物模型计算表';


INSERT INTO `sys_menu` VALUES (3385, '数据调试', 2008, 1, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:dataDebug', '#', 'admin', '2025-07-17 16:31:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3386, '组态应用', 2008, 2, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:scadaApply', '#', 'admin', '2025-07-17 16:32:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3387, '实时监测', 2008, 3, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:monitor', '#', 'admin', '2025-07-17 16:33:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3388, '监测统计', 2008, 4, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:monitorCount', '#', 'admin', '2025-07-17 16:34:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3389, '设备定时', 2008, 5, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:timing', '#', 'admin', '2025-07-17 16:35:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3390, '事件日志', 2008, 6, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:eventLog', '#', 'admin', '2025-07-17 16:36:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3391, '指令日志', 2008, 7, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:functionLog', '#', 'admin', '2025-07-17 16:37:15', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3392, '告警用户', 2008, 8, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:alertUser', '#', 'admin', '2025-07-17 16:37:53', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3393, '视频监控', 2008, 9, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:videoMonitor', '#', 'admin', '2025-07-17 16:38:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3394, '设备告警', 2008, 10, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:deviceAlert', '#', 'admin', '2025-07-17 16:40:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3395, '设备分享', 2008, 11, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:device:deviceShare', '#', 'admin', '2025-07-17 16:40:55', '', NULL, '');

INSERT INTO `sys_menu_translate` VALUES (3385, '数据调试', 'Data Debugger');
INSERT INTO `sys_menu_translate` VALUES (3386, '组态应用', 'Scada apply');
INSERT INTO `sys_menu_translate` VALUES (3387, '实时监测', 'Timing monitor');
INSERT INTO `sys_menu_translate` VALUES (3388, '监测统计', 'Monitor count');
INSERT INTO `sys_menu_translate` VALUES (3389, '设备定时', 'Device timing');
INSERT INTO `sys_menu_translate` VALUES (3390, '事件日志', 'Event log');
INSERT INTO `sys_menu_translate` VALUES (3391, '指令日志', 'Function log');
INSERT INTO `sys_menu_translate` VALUES (3392, '告警用户', 'Alert user');
INSERT INTO `sys_menu_translate` VALUES (3393, '视频监控', 'Video monitor');
INSERT INTO `sys_menu_translate` VALUES (3394, '设备告警', 'Device alert');
INSERT INTO `sys_menu_translate` VALUES (3395, '设备分享', 'Device share');

-- 菜单 SQL
insert into sys_menu values(3396, '报表管理', 3343, '1', 'report/list', 'dataCenter/report/index', '', 1, 0, 'C', '0', '0', 'dataCenter:report:list', '#', 'admin', sysdate(), '', null, '报表管理菜单');

-- 按钮 SQL
insert into sys_menu values(3397, '报表查询', 3396, '1',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:report:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3398, '报表新增', 3396, '2',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:report:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3399, '报表修改', 3396, '3',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:report:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3400, '报表删除', 3396, '4',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:report:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3401, '报表导出', 3396, '5',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:report:export',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3402, '报表记录', 3396, '1', '#', '', '', 1, 0, 'C', '0', '0', 'dataCenter:reportRecords:list', '#', 'admin', sysdate(), '', null, '报表记录菜单');

insert into sys_menu values(3403, '报表记录删除', 3396, '4',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:records:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values(3404, '报表记录下载', 3396, '5',  '#', '', '', 1, 0, 'F', '0', '0', 'dataCenter:records:download',       '#', 'admin', sysdate(), '', null, '');

INSERT INTO `sys_menu_translate` VALUES (3396, '报表管理', 'Report Manager');
INSERT INTO `sys_menu_translate` VALUES (3397, '报表查询', 'Report Query');
INSERT INTO `sys_menu_translate` VALUES (3398, '报表新增', 'Report addition');
INSERT INTO `sys_menu_translate` VALUES (3399, '报表修改', 'Report modification');
INSERT INTO `sys_menu_translate` VALUES (3400, '报表删除', 'Report deletion');
INSERT INTO `sys_menu_translate` VALUES (3401, '报表导出', 'Report export');
INSERT INTO `sys_menu_translate` VALUES (3402, '报表记录', 'Report Record');
INSERT INTO `sys_menu_translate` VALUES (3403, '报表记录删除', 'Delete report records');
INSERT INTO `sys_menu_translate` VALUES (3404, '报表记录下载', 'Download report records');

INSERT INTO `sys_menu` VALUES (3405, '场景逻辑删除列表', 3374, 2, '', NULL, '', 1, 0, 'F', '0', 0, 'iot:recovery:sceneModel', '#', 'admin', '2025-07-24 16:06:09', '', NULL, '');
INSERT INTO `sys_menu_translate` VALUES (3405, '场景逻辑删除列表', 'SceneModel delete list');

INSERT INTO `sys_dict_type` VALUES (168, '规则引擎状态', 'rule_engine_status', 0, 'admin', '2025-05-13 11:53:02', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (169, '大屏图表类型', 'databoard_card_type', 0, 'admin', '2025-03-07 10:49:21', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (170, '移动端页面大小', 'mobile_page_size', 0, 'admin', '2025-07-10 10:13:45', 'admin', '2025-07-10 11:59:10', NULL);
INSERT INTO `sys_dict_type` VALUES (171, 'pc端页面大小', 'pc_page_size', 0, 'admin', '2025-07-10 14:13:23', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (172, 'mqtt版本', 'mqtt_version', 0, 'admin', '2025-07-16 14:46:19', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (173, '报表变量统计方式', 'report_rule_data_operation', 0, 'admin', '2025-07-30 11:06:03', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (168, '规则引擎状态', 'Rule engine status');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (169, '大屏图表类型', 'Large screen chart type');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (170, '移动端页面大小', 'Mobile page size');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (171, 'pc端页面大小', 'PC page size');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (172, 'mqtt版本', 'mqtt version');
INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (173, '报表变量统计方式', 'Statistics of report variables');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (743, 1, '启用', '1', 'rule_engine_status', NULL, 'primary', 'N', 0, 'admin', '2025-05-13 16:35:44', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (744, 2, '禁止', '2', 'rule_engine_status', NULL, 'danger', 'N', 0, 'admin', '2025-05-13 16:36:19', '', NULL, NULL);

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (745, 0, '轮播列表', '1', 'databoard_card_type', NULL, 'default', 'N', 0, 'admin', '2025-06-12 14:51:45', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (746, 0, '滚动排名列表', '2', 'databoard_card_type', NULL, 'default', 'N', 0, 'admin', '2025-06-12 14:52:12', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (747, 0, '折线图/柱状图/饼状图-非环形（单个属性）', '3', 'databoard_card_type', NULL, 'default', 'N', 0, 'admin', '2025-06-12 14:52:34', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (748, 0, '折线图/柱状图（两个属性）', '4', 'databoard_card_type', NULL, 'default', 'N', 0, 'admin', '2025-06-12 14:52:56', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)

VALUES (749, 0, 'iPhone 6/7/8  (375x667)', '375x667', 'mobile_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 11:59:36', 'admin', '2025-07-10 14:33:36', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (750, 0, 'iPhone XR/11/12/13   (414×896)', '414x896', 'mobile_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:01:48', 'admin', '2025-07-10 14:33:45', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (751, 0, 'iPhone 14/15 (393×852)', '393x852', 'mobile_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:02:57', 'admin', '2025-07-11 17:45:16', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (752, 0, '8英寸 (800x1280)', '800x1280', 'mobile_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:05:49', 'admin', '2025-07-10 14:34:37', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (753, 0, '12英寸 (1200×1920)', '1200x1920', 'mobile_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:07:21', 'admin', '2025-07-10 14:34:16', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)

VALUES (754, 0, '16:10 (1920x1200)', '1920x1200', 'pc_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:14:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (755, 0, '16:9 (1920x1080)', '1920x1080', 'pc_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:14:40', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (756, 0, '16:9 (1600x900)', '1600x900', 'pc_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:15:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (757, 0, '4:3 (1600x1200)', '1600x1200', 'pc_page_size', NULL, 'default', 'N', 0, 'admin', '2025-07-10 14:15:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)

VALUES (758, 0, '0', '0', 'mqtt_version', NULL, 'default', 'N', 0, 'admin', '2025-07-16 14:47:53', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (759, 0, '3.1.3', '3', 'mqtt_version', NULL, 'default', 'N', 0, 'admin', '2025-07-16 14:48:25', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (760, 0, '3.1.1', '4', 'mqtt_version', NULL, 'default', 'N', 0, 'admin', '2025-07-16 14:48:43', 'admin', '2025-07-16 14:48:51', NULL);


INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (761, 0, '平均值', '1', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:07:08', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (762, 1, '最大值', '2', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:07:24', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (763, 2, '最小值', '3', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:07:36', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (764, 3, '累计值', '4', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:07:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (765, 4, '差值', '5', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:08:00', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (766, 5, '极差值', '6', 'report_rule_data_operation', NULL, 'default', 'N', 0, 'admin', '2025-07-30 11:08:12', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (743, '启用', 'Enable');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (744, '禁止', 'Disable');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (745, ' 轮播列表', 'Carousel list');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (746, '滚动排名列表', 'Scroll through the ranked list');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (747, '折线图/柱状图/饼状图-非环形（单个属性）', 'Line Chart/Column Chart/Pie Chart - Non-Ring (Single Attribute)');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (748, '折线图/柱状图（两个属性）', 'Line Chart/Column Chart (two attributes)');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (749, 'iPhone 6/7/8  (375x667)', 'iPhone 6/7/8');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (750, 'iPhone XR/11/12/13   (414×896‌)', 'iPhone XR/11/12/13');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (751, 'iPhone 14/15 (393×852)', 'iPhone 14/15');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (752, '8英寸 (800x1280)', '8 inches (800x1280)');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (753, '12英寸 (1200×1920)', '12 inches (1200×1920)');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (754, '16:10 (1920x1200)', '16:10 (1920x1200)');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (755, '16:9 (1920x1080)', '16:9 (1920x1080)');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (756, '16:9 (1600x900)', '16:9 (1600x900)');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (757, '4:3 (1600x1200)', '4:3 (1600x1200)');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (758, '0', '0');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (759, '3.1.3', '3.1.3');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (760, '3.1.1', '3.1.1');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (761, '平均值', 'AVERAGE');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (762, '最大值', 'MAX');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (763, '最小值', 'MIN');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (764, '累计值', 'CUMULATIVE');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (765, '差值', 'DIFFERENCE');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (766, '极差值', 'RANGE');

UPDATE sys_menu SET  path = 'operation' WHERE menu_id = 3000;
UPDATE sys_menu SET  path = 'sceneLinkage' WHERE menu_id = 2085;
UPDATE sys_menu SET  path = 'product/list' WHERE menu_id = 2043;
UPDATE sys_menu SET  path = 'device/list' WHERE menu_id = 2007;
UPDATE sys_menu SET  path = 'firmware/list' WHERE menu_id = 2013;
UPDATE sys_menu SET  path = 'echart/list' WHERE menu_id = 3166;
UPDATE sys_menu SET  path = 'component/list' WHERE menu_id = 3184;

ALTER TABLE `sys_dept`
    ADD COLUMN `invitation_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邀请码';

-- 报表相关
CREATE TABLE `report` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                          `name` varchar(200) NOT NULL COMMENT '报表名称',
                          `data_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '数据类型(1-历史数据 2-聚合数据)',
                          `cycle_type` tinyint(2) NOT NULL DEFAULT '3' COMMENT '时间周期方式 1-周期计算 3-固定时间',
                          `cycle` json DEFAULT NULL COMMENT '时间周期内容',
                          `aggregate_units` tinyint(2) DEFAULT NULL COMMENT '聚合单位 1-分钟 2-小时 3-天 4-月',
                          `data_dimension` tinyint(2) NOT NULL DEFAULT '1' COMMENT '数据维度 1-设备 2-场景',
                          `export_format` tinyint(2) DEFAULT NULL COMMENT '导出格式 1-单变量 2-多变量',
                          `tenant_id` bigint(20) NOT NULL COMMENT '所属租户id',
                          `tenant_name` varchar(30) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '租户名称',
                          `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
                          `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='报表';

CREATE TABLE `report_records` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                  `report_id` bigint(20) NOT NULL COMMENT '报表id',
                                  `report_file_path` varchar(255) NOT NULL COMMENT '报表文件下载路径',
                                  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态',
                                  `time_cycle` varchar(255) NOT NULL COMMENT '时间周期',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='报表记录';

CREATE TABLE `report_rule` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `report_id` bigint(20) NOT NULL COMMENT '报表id',
                               `cus_source_id` bigint(20) NOT NULL COMMENT '场景或设备id',
                               `scene_model_device_id` bigint(20) DEFAULT NULL COMMENT '场景关联设备id',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='报表规则';

CREATE TABLE `report_rule_data` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                    `report_id` bigint(20) NOT NULL COMMENT '报表id',
                                    `report_rule_id` bigint(20) NOT NULL COMMENT '报表规则id',
                                    `cus_data_id` bigint(20) NOT NULL COMMENT '场景或设备变量id',
                                    `operation` tinyint(2) NOT NULL COMMENT '统计方式',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='报表规则变量';

ALTER TABLE sip_device_channel DROP INDEX idx_channel;

ALTER TABLE sip_device_channel ADD UNIQUE INDEX idx_channel (device_sip_id, channel_sip_id, del_flag);

ALTER TABLE sip_device_channel
    modify COLUMN `del_flag` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 其他值代表删除）';

