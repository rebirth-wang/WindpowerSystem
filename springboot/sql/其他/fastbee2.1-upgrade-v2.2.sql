-- ----------------------------
-- 注意注意：不要直接执行以下sql，部分有关联的sql，需要根据自己的系统进行调整
-- ----------------------------
-- ----------------------------
-- 通知渠道表
-- ----------------------------
DROP TABLE IF EXISTS `notify_channel`;
CREATE TABLE `notify_channel` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                                  `name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '通知名称',
                                  `channel_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道类型',
                                  `provider` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '服务商',
                                  `config_content` varchar(1024) COLLATE utf8_unicode_ci NOT NULL COMMENT '配置内容',
                                  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                                  `tenant_name` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                                  `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='通知渠道';

-- 通知模板表
DROP TABLE IF EXISTS `notify_template`;
CREATE TABLE `notify_template` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                                   `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道名称',
                                   `service_code` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '业务编码(唯一启用)',
                                   `channel_id` bigint(20) DEFAULT NULL COMMENT '通知渠道账号',
                                   `channel_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '渠道类型',
                                   `provider` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '服务商',
                                   `msg_params` text COLLATE utf8_unicode_ci COMMENT '模板配置参数',
                                   `status` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '是否启用 0-不启用 1-启用',
                                   `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                   `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                                   `tenant_name` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='通知模版';

-- 通知日志表
DROP TABLE IF EXISTS `notify_log`;
CREATE TABLE `notify_log` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                              `channel_id` bigint(20) NOT NULL COMMENT '渠道编号',
                              `notify_template_id` bigint(20) NOT NULL COMMENT '通知模版编号',
                              `msg_content` text COLLATE utf8_unicode_ci COMMENT '消息内容',
                              `send_account` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发送账号',
                              `send_status` tinyint(4) NOT NULL COMMENT '发送状态',
                              `result_content` text COLLATE utf8_unicode_ci COMMENT '返回内容',
                              `service_code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '业务编码(唯一启用)',
                              `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                              `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
                              `tenant_name` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '租户名称',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='通知日志';

-- 告警通知模板关联表
DROP TABLE IF EXISTS `iot_alert_notify_template`;
CREATE TABLE `iot_alert_notify_template` (
                                             `alert_id` bigint(20) NOT NULL COMMENT '告警id',
                                             `notify_template_id` bigint(20) NOT NULL COMMENT '通知模版id',
                                             PRIMARY KEY (`alert_id`,`notify_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='告警通知模版关联表';

-- 通知渠道菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( '通知渠道', 3000, 7, 'channel', 'notify/channel/index', NULL, 1, 0, 'C', '0', '0', 'notify:channel:list', 'notify_channel', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:00:37', '通知渠道菜单');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知渠道查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:query', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:24', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( '通知渠道新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:add', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:31', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知渠道修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:edit', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:36', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知渠道删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:remove', '#', 'admin', '2023-12-01 10:18:40', 'admin', '2024-01-03 11:01:41', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知渠道导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'notify:channel:export', '#', 'admin', '2023-12-01 10:18:41', 'admin', '2024-01-03 11:01:52', '');

-- 通知模板菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板', 3000, 7, 'notifyTemplate', 'notify/template/index', NULL, 1, 0, 'C', '0', '0', 'notify:template:list', 'template', 'admin', '2023-12-22 15:19:44', 'admin', '2024-01-03 11:02:12', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板查询', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:query', '#', 'admin', '2024-01-03 11:02:55', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( '通知模板新增', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:add', '#', 'admin', '2024-01-03 11:03:23', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板修改', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:edit', '#', 'admin', '2024-01-03 11:03:40', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板删除', @parentId, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:remove', '#', 'admin', '2024-01-03 11:03:55', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板导出', @parentId, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:export', '#', 'admin', '2024-01-03 11:04:16', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知模板测试', @parentId, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:template:send', '#', 'admin', '2024-01-03 11:07:19', '', NULL, '');

-- 通知日志菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ( '通知日志', 3000, 7, 'notifylog', 'notify/log/index', NULL, 1, 0, 'C', '0', '0', 'notify:log:list', 'notify_log', 'admin', '2023-12-28 10:10:52', 'admin', '2024-01-03 11:05:11', '');
SELECT @parentId := LAST_INSERT_ID();
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知日志导出', @parentId, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:export', '#', 'admin', '2024-01-03 11:06:11', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知日志删除', @parentId, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:remove', '#', 'admin', '2024-01-03 11:06:31', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知日志详情', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'notify:log:query', '#', 'admin', '2024-01-03 11:06:31', '', NULL, '');

-- 字典通知渠道类型
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知渠道类型', 'notify_channel_type', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '通知渠道');
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '短信', 'sms', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-12-21 10:31:54', NULL),
                                                                                                                                                                                                            (2, '语音', 'voice', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:24:22', NULL),
                                                                                                                                                                                                            (3, '微信', 'wechat', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:24:15', NULL),
                                                                                                                                                                                                            (4, '钉钉', 'dingtalk', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:24:38', NULL),
                                                                                                                                                                                                            (5, '邮箱', 'email', 'notify_channel_type', NULL, 'default', 'N', '0', 'admin', '2023-12-11 23:43:14', 'admin', '2024-01-05 16:24:32', NULL);
-- 字典通知短信服务商
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知短信服务商', 'notify_channel_sms_provider', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '短信服务商');
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '阿里云', 'alibaba', 'notify_channel_sms_provider', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2024-01-05 16:49:10', ''),
                                                                                                                                                                                                            (2, '腾讯云', 'tencent', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:11:25', 'admin', '2024-01-05 16:50:26', ''),
                                                                                                                                                                                                            (3, '天翼云', 'ctyun', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:11:51', 'admin', '2024-01-05 16:50:31', ''),
                                                                                                                                                                                                            (4, '华为云', 'huawei', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:07', 'admin', '2024-01-05 16:50:35', NULL),
                                                                                                                                                                                                            (5, '云片', 'yunpian', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:29', 'admin', '2024-01-05 16:50:39', NULL),
                                                                                                                                                                                                            (6, '亿美软通', 'emay', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:12:56', 'admin', '2024-01-05 16:49:52', NULL),
                                                                                                                                                                                                            (7, '容连云', 'cloopen', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:16', 'admin', '2024-01-05 16:49:58', NULL),
                                                                                                                                                                                                            (8, '京东云', 'jdcloud', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:37', 'admin', '2024-01-05 16:50:07', NULL),
                                                                                                                                                                                                            (9, '网易云', 'netease', 'notify_channel_sms_provider', NULL, 'default', 'N', '1', 'admin', '2023-12-21 10:13:57', 'admin', '2024-01-05 16:50:13', NULL);
-- 字典通知邮箱服务商
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知邮箱服务商', 'notify_channel_email_provider', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-11-30 09:45:15', '邮箱服务商');
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, 'QQ', 'qq', 'notify_channel_email_provider', NULL, 'default', 'N', '0', 'admin', '2023-11-30 09:45:15', 'admin', '2023-12-22 10:33:48', NULL),
                                                                                                                                                                                                            (2, '163', '163', 'notify_channel_email_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-21 10:41:52', 'admin', '2023-12-22 10:33:58', NULL);
-- 字典通知微信服务商
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知微信服务商', 'notify_channel_wechat_provider', '0', 'admin', '2023-12-21 10:37:25', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '微信小程序（订阅消息）', 'mini_program', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-21 10:41:04', 'admin', '2024-01-22 15:49:10', ''),
                                                                                                                                                                                                            (2, '企业微信应用消息', 'wecom_apply', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:40:11', 'admin', '2024-01-22 17:06:03', NULL),
                                                                                                                                                                                                            (3, '企业微信群机器人', 'wecom_robot', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:40:33', 'admin', '2024-01-22 15:40:38', NULL);
-- 字典通知语音服务商
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知语音服务商', 'notify_channel_voice_provider', '0', 'admin', '2023-12-27 14:58:23', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '阿里云', 'alibaba', 'notify_channel_voice_provider', NULL, 'default', 'N', '0', 'admin', '2023-12-27 14:58:54', 'admin', '2024-01-11 09:59:44', ''),
                                                                                                                                                                                                            (2, '腾讯云', 'tencent', 'notify_channel_voice_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-11 09:59:33', '', NULL, NULL);

-- 字典通知语音服务商
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知钉钉服务商', 'notify_channel_dingtalk_provider', '0', 'admin', '2024-01-10 15:27:28', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '工作通知', 'work', 'notify_channel_dingtalk_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-10 15:32:15', 'admin', '2024-01-18 17:43:10', NULL),
                                                                                                                                                                                                            (2, '群机器人', 'group_robot', 'notify_channel_dingtalk_provider', NULL, 'default', 'N', '0', 'admin', '2024-01-10 15:32:57', 'admin', '2024-01-22 15:47:06', NULL);

-- 通知钉钉消息类型
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知钉钉消息类型', 'dingtalk_msg_type', '0', 'admin', '2024-01-22 11:41:26', 'admin', '2024-01-22 11:44:20', NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '文本', 'text', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:42:51', '', NULL, NULL),
                                                                                                                                                                                                            (2, 'markdown类型', 'markdown', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:43:44', '', NULL, NULL),
                                                                                                                                                                                                            (3, '链接消息', 'link', 'dingtalk_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 11:44:04', '', NULL, NULL);
-- 通知企业微信消息类型
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知企业微信消息类型', 'wecom_msg_type', '0', 'admin', '2024-01-22 11:49:03', 'admin', '2024-01-22 11:51:27', NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                            (1, '文本', 'text', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:51:48', '', NULL, NULL),
                                                                                                                                                                                                            (2, 'markdown', 'markdown', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:52:04', '', NULL, NULL),
                                                                                                                                                                                                            (3, '图文', 'news', 'wecom_msg_type', NULL, 'default', 'N', '0', 'admin', '2024-01-22 15:52:29', '', NULL, NULL);

-- 通知业务编码
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('通知业务编码', 'notify_service_code', '0', 'admin', '2023-12-18 14:56:57', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '设备告警', 'alert', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2023-12-18 14:58:40', 'admin', '2024-01-25 14:35:25', NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '验证码', 'captcha', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2023-12-18 14:59:59', 'admin', '2024-01-25 14:36:07', NULL);
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '营销通知', 'marketing', 'notify_service_code', NULL, 'default', 'N', '0', 'admin', '2024-01-11 09:56:07', 'admin', '2024-01-25 14:37:40', NULL);

-- 通知渠道初始数据
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (1, '阿里云短信', 'sms', 'alibaba', '{\"accessKeyId\":\"\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:24:24', NULL, '2024-01-25 17:24:24', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (2, '阿里云语音', 'voice', 'alibaba', '{\"accessKeyId\":\"\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:24:47', NULL, '2024-01-31 11:33:16', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (3, '腾讯云短信', 'sms', 'tencent', '{\"accessKeyId\":\"\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:25:31', NULL, '2024-01-25 17:25:31', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (4, '腾讯云语音', 'voice', 'tencent', '{\"accessKeyId\":\"\",\"accessKeySecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:26:01', NULL, '2024-01-31 14:13:50', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (5, 'QQ邮箱', 'email', 'qq', '{\"smtpServer\":\"smtp.qq.com\",\"port\":\"465\",\"username\":\"\",\"password\":\"\",\"sslEnable\":true,\"authEnable\":true,\"retryInterval\":\"5\",\"maxRetries\":\"1\"}', 1, 'admin', NULL, '2024-01-25 17:27:34', NULL, '2024-01-25 17:27:34', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (6, '163邮箱', 'email', '163', '{\"smtpServer\":\"smtp.163.com\",\"port\":\"465\",\"username\":\"\",\"password\":\"\",\"sslEnable\":true,\"authEnable\":true,\"retryInterval\":\"5\",\"maxRetries\":\"1\"}', 1, 'admin', NULL, '2024-01-25 17:27:58', NULL, '2024-01-25 17:27:58', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (7, '微信小程序', 'wechat', 'mini_program', '{\"appId\":\"\",\"appSecret\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:28:24', NULL, '2024-01-31 14:32:39', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (8, '企业微信群机器人', 'wechat', 'wecom_robot', '{\"webHook\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:29:06', NULL, '2024-01-31 14:50:38', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (9, '企业微信应用消息', 'wechat', 'wecom_apply', '{\"corpId\":\"\",\"corpSecret\":\"\",\"agentId\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:30:47', NULL, '2024-01-31 15:06:03', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (10, '钉钉消息通知', 'dingtalk', 'work', '{\"appKey\":\"\",\"appSecret\":\"\",\"agentId\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:35:53', NULL, '2024-01-31 15:19:30', 0);
INSERT INTO `notify_channel` (`id`, `name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (11, '钉钉群机器人', 'dingtalk', 'group_robot', '{\"webHook\":\"\"}', 1, 'admin', NULL, '2024-01-25 17:38:58', NULL, '2024-01-31 15:26:39', 0);

-- 通知模版初始数据
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (1, '告警阿里云短信', 'alert', 1, 'sms', 'alibaba', '{\"sendAccount\":\"\",\"templateId\":\"\",\"signature\":\"\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\"}', 0, NULL, '2024-01-26 09:21:57', NULL, '2024-02-05 09:36:16', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (2, '验证码阿里云短信', 'captcha', 1, 'sms', 'alibaba', '{\"templateId\":\"\",\"signature\":\"\",\"content\":\"验证码${code}，有效期5分钟\"}', 0, NULL, '2024-01-26 09:23:55', NULL, '2024-01-30 13:59:47', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (3, '验证码腾讯云短信', 'captcha', 3, 'sms', 'tencent', '{\"templateId\":\"\",\"signature\":\"\",\"sdkAppId\":\"\",\"content\":\"验证码{1}，有效期5分钟\"}', 0, NULL, '2024-01-26 09:28:50', NULL, '2024-01-26 09:28:52', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (4, '告警腾讯云短信', 'alert', 3, 'sms', 'tencent', '{\"sendAccount\":\"\",\"templateId\":\"\",\"signature\":\"\",\"sdkAppId\":\"\",\"content\":\"您的设备:{1},设备编号:{2},在{3}发生{4}告警\"}', 0, NULL, '2024-01-26 09:37:18', NULL, '2024-02-01 09:30:49', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (5, '告警QQ邮箱', 'alert', 5, 'email', 'qq', '{\"sendAccount\":\"\",\"title\":\"设备告警\",\"attachment\":\"\",\"content\":\"<p>您的设备:#{name},设备编号:#{serialnumber},在#{address}发生#{alert}告警</p>\"}', 0, NULL, '2024-01-26 09:43:26', NULL, '2024-02-05 09:36:16', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (6, '告警163邮箱', 'alert', 6, 'email', '163', '{\"title\":\"设备告警\",\"attachment\":\"\",\"content\":\"<p>您的设备:#{name},设备编号:#{serialnumber},在#{address}发生#{alert}告警</p>\"}', 0, NULL, '2024-01-26 09:44:05', NULL, '2024-01-26 15:08:29', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (7, '告警阿里云语音', 'alert', 2, 'voice', 'alibaba', '{\"sendAccount\":\"\",\"templateId\":\"\",\"content\":\"您的设备:${name}，在${address}发生告警，请尽快处理\",\"playTimes\":\"1\",\"volume\":\"50\",\"speed\":\"0\"}', 0, NULL, '2024-01-26 09:49:23', NULL, '2024-02-05 09:36:17', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (8, '告警腾讯云语音', 'alert', 4, 'voice', 'tencent', '{\"sendAccount\":\"\",\"sdkAppId\":\"\",\"templateId\":\"\",\"content\":\"您的设备:{1},设备编号:{2},在{3}发生{4}告警\"}', 0, NULL, '2024-01-26 09:53:07', NULL, '2024-02-01 11:31:35', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (9, '告警微信小程序', 'alert', 7, 'wechat', 'mini_program', '{\"sendAccount\":\"\",\"templateId\":\"\",\"redirectUrl\":\"/pages/tabBar/alert/index\",\"content\":\"报警设备 {{thing1.DATA}}  设备编号 {{character_string7.DATA}}\"}', 0, NULL, '2024-01-26 10:22:42', NULL, '2024-02-05 09:36:17', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (10, '告警企业微信应用消息', 'alert', 9, 'wechat', 'wecom_apply', '{\"sendAccount\":\"\",\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"url\":\"https://iot.fastbee.cn/\",\"picUrl\":\"\",\"msgType\":\"news\"}', 0, NULL, '2024-01-26 10:25:58', NULL, '2024-02-05 09:36:18', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (11, '告警企业微信群机器人', 'alert', 8, 'wechat', 'wecom_robot', '{\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"url\":\"https://iot.fastbee.cn/\",\"picUrl\":\"\",\"msgType\":\"news\"}', 0, NULL, '2024-01-26 10:26:57', NULL, '2024-02-05 09:36:18', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (12, '告警钉钉消息通知', 'alert', 10, 'dingtalk', 'work', '{\"deptId\":\"\",\"sendAllEnable\":false,\"sendAccount\":\"\",\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"messageUrl\":\" https://iot.fastbee.cn/  \",\"picUrl\":\"\",\"msgType\":\"link\"}', 0, NULL, '2024-01-26 10:27:50', NULL, '2024-02-05 09:36:19', 0, 1, 'admin');
INSERT INTO `notify_template` (`id`, `name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`) VALUES (13, '告警钉钉机器人', 'alert', 11, 'dingtalk', 'group_robot', '{\"title\":\"设备告警\",\"content\":\"您的设备:${name},设备编号:${serialnumber},在${address}发生${alert}告警\",\"messageUrl\":\"https://iot.fastbee.cn\",\"picUrl\":\"\",\"msgType\":\"link\"}', 0, NULL, '2024-01-26 10:28:34', NULL, '2024-02-05 09:36:19', 0, 1, 'admin');


-- ----------------------------
-- 告警表
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备告警' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 告警关联场景表
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_scene`;
CREATE TABLE `iot_alert_scene`  (
                                    `alert_id` bigint(20) NOT NULL COMMENT '告警ID',
                                    `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
                                    PRIMARY KEY (`alert_id`, `scene_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '告警场景表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- 场景表
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
-- 场景设备表
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '场景设备表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 场景脚本表
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
-- 规则脚本表
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
-- 删除旧版本告警和场景的触发器表
-- ----------------------------
DROP TABLE IF EXISTS `iot_alert_trigger`;
DROP TABLE IF EXISTS `iot_scene_trigger`;

-- ----------------------------
-- 新增规则引擎相关字典
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (140, '规则脚本类型', 'rule_script_type', '0', 'admin', '2023-11-04 01:48:50', 'admin', '2023-11-04 01:50:16', NULL);
INSERT INTO `sys_dict_type` VALUES (141, '规则脚本语言', 'rule_script_language', '0', 'admin', '2023-11-04 01:50:06', '', NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (149, '规则脚本事件', 'rule_script_event', '0', 'admin', '2023-12-19 11:33:48', '', NULL, '1=设备上报，2=平台下发，3=设备上线，4=设备离线');
INSERT INTO `sys_dict_type` VALUES (150, '规则脚本用途', 'rule_script_purpose', '0', 'admin', '2023-12-19 11:38:18', '', NULL, '1=数据流，2=触发器，3=执行动作');
INSERT INTO `sys_dict_type` VALUES (151, '规则脚本动作', 'rule_script_action', '0', 'admin', '2023-12-19 11:39:58', '', NULL, '1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储');

-- ----------------------------
-- 新增规则引擎相关字典数据
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (560, 1, '设备上报', '1', 'rule_script_event', NULL, 'primary', 'N', '0', 'admin', '2023-12-19 11:40:34', 'admin', '2023-12-20 02:23:43', NULL);
INSERT INTO `sys_dict_data` VALUES (561, 2, '平台下发', '2', 'rule_script_event', NULL, 'warning', 'N', '0', 'admin', '2023-12-19 11:40:46', 'admin', '2023-12-20 02:23:51', NULL);
INSERT INTO `sys_dict_data` VALUES (562, 3, '设备上线', '3', 'rule_script_event', NULL, 'success', 'N', '0', 'admin', '2023-12-19 11:40:58', 'admin', '2023-12-20 02:24:00', NULL);
INSERT INTO `sys_dict_data` VALUES (563, 4, '设备离线', '4', 'rule_script_event', NULL, 'info', 'N', '0', 'admin', '2023-12-19 11:41:09', 'admin', '2023-12-20 02:24:08', NULL);
INSERT INTO `sys_dict_data` VALUES (564, 1, '数据流', '1', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:39', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (565, 2, '触发器', '2', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:48', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (566, 3, '执行动作', '3', 'rule_script_purpose', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:41:59', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (567, 1, '消息重发', '1', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:42:26', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (568, 2, '消息通知', '2', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:18', 'admin', '2023-12-19 11:43:38', NULL);
INSERT INTO `sys_dict_data` VALUES (569, 3, 'Http推送', '3', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:33', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (570, 4, 'Mqtt桥接', '4', 'rule_script_action', NULL, 'default', 'N', '0', 'admin', '2023-12-19 11:43:54', '', NULL, NULL);


-- ----------------------------
-- 规则引擎相关菜单
-- ----------------------------
INSERT INTO `sys_menu` VALUES (3051, '规则引擎', 0, 4, 'ruleengine', NULL, NULL, 1, 0, 'M', '0', '0', '', 'channel', 'admin', '2023-07-03 21:22:19', 'admin', '2023-11-13 10:46:00', '');
INSERT INTO `sys_menu` VALUES (3055, '规则脚本', 3051, 2, 'script', 'iot/scene/script', NULL, 1, 0, 'C', '0', '0', 'iot/scene/script', 'code', 'admin', '2023-07-06 21:03:14', 'admin', '2024-01-19 00:24:37', '');

UPDATE `sys_menu`  SET parent_id=3051,menu_name='场景联动'  WHERE menu_id=2085;
INSERT INTO `sys_menu` VALUES (3147, '告警配置', 3051, 6, 'alert', 'iot/alert/index', NULL, 1, 0, 'C', '0', '0', 'iot:alert:list', 'alert', 'admin', '2023-11-12 10:58:01', 'admin', '2024-02-01 02:36:17', '');
UPDATE `sys_menu`  SET parent_id=3051,menu_name='告警记录',component='iot/alert/log',perms='iot:alert:list',icon='log'  WHERE menu_id=2067;

ALTER TABLE `iot_product`
    ADD COLUMN `location_way` tinyint(1) NULL DEFAULT 1 COMMENT '定位方式(1=ip自动定位，2=设备定位，3=自定义)' AFTER `transport`;



-- ----------------------------
-- 权限整理修改，**注意注意**：不要直接执行以下sql，菜单的parent_id不一样，请先整理一下
-- ----------------------------
-- 产品模型

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型列表', 2043, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:list', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型详情', 2043, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:query', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型导入', 2043, 12, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:import', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型新增', 2043, 13, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:add', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型修改', 2043, 14, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:edit', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('产品模型删除', 2043, 15, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:model:remove', '#', 'admin', '2024-03-18 17:01:05', 'admin', '2024-03-18 17:01:17', '');



-- 删除设备详情按钮

delete
from sys_menu
where perms = 'iot:device:detail';



-- 设备定时

update sys_menu
set perms = 'iot:device:timer:list'
where perms = 'iot:device:timer';

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                                                              ('设备定时详情', 2148, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('设备定时新增', 2148, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:add', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('设备定时修改', 2148, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('设备定时执行', 2148, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:execute', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),

                                                                                                                                                                                                                                              ('设备定时导出', 2148, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:export', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),

                                                                                                                                                                                                                                              ('设备定时删除', 2148, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:timer:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');



-- 设备用户

update sys_menu
set perms = 'iot:device:user:list',
    menu_name = '设备用户列表'
where perms = 'iot:device:share';

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                                                              ('设备用户详情', 2147, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('分享设备', 2147, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:share', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('设备用户修改', 2147, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('取消分享', 2147, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:user:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');



-- 录像管理

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                                                              ('录像管理列表', 3099, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:list', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('查看录像', 3099, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('录像下载', 3099, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:sip:record:download', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');



-- 场景联动

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('场景联动执行', 2085, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:scene:run', '#', 'admin', '2024-03-19 16:15:22', '', NULL, '');



-- 字典管理

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('字典刷新', 105, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:dict:refresh', '#', 'admin', '2024-03-19 16:38:13', 'admin', '2024-03-19 16:38:35', '');



-- 参数设置

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('参数刷新', 106, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'system:config:refresh', '#', 'admin', '2024-03-19 16:40:31', '', NULL, '');



-- 三方登录

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
                                                                                                                                                                                                                                              ('三方登录详情', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:query', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('三方登录新增', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:add', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('三方登录修改', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:edit', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('三方登录导出', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:export', '#', 'admin', '2024-03-19 11:18:22', '', NULL, ''),
                                                                                                                                                                                                                                              ('三方登录删除', 2141, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:platform:remove', '#', 'admin', '2024-03-19 11:18:22', '', NULL, '');

-- 定时任务

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('任务执行', 110, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:job:run', '#', 'admin', '2024-03-19 17:05:29', '', NULL, '');

-- 缓存列表

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('缓存列表', 124, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:cache:list', '#', 'admin', '2024-03-19 17:09:49', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('缓存删除', 124, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:cache:remove', '#', 'admin', '2024-03-19 17:10:15', '', NULL, '');

-- 客户端

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('客户端列表', 3032, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:client:list', '#', 'admin', '2024-03-19 17:19:32', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('客户端踢出', 3032, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:emqx:client:remove', '#', 'admin', '2024-03-19 17:20:00', '', NULL, '');



-- 固件任务

update sys_menu
set parent_id = 3000
where menu_name = '固件任务'


-- 微信公众号字典
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '微信开放平台公众号', 'wechat_open_public_account', 'iot_social_platform', NULL, 'default', 'N', '0', 'admin', '2024-03-08 17:56:56', '', NULL, '感谢您关注蜂信物联！');
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '微信公众号', 'public_account', 'notify_channel_wechat_provider', NULL, 'default', 'N', '0', 'admin', '2024-03-09 11:11:57', '', NULL, NULL);


