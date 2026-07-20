-- 数据库版本升级脚本
-- 适用于fastbee2.5.2版本到fastbee2.6.0版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作

-- 租户新增logo设置
ALTER TABLE `sys_dept`
ADD COLUMN `dept_logo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '机构logo',
ADD COLUMN `logo_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo名称';

-- 新增MQTT通知
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (166, '通知MQTT服务商', 'notify_channel_mqtt_provider', 0, 'admin', '2024-12-30 14:13:11', '', NULL, NULL);

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (737, 6, 'MQTT', 'mqtt', 'notify_channel_type', NULL, 'default', 'N', 0, 'admin', '2024-12-30 14:11:33', 'admin', '2024-12-30 15:28:32', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (738, 0, '网页通知', 'web', 'notify_channel_mqtt_provider', NULL, 'default', 'N', 0, 'admin', '2024-12-30 14:14:23', '', NULL, NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (166, '通知MQTT服务商', 'Notify the MQTT service provider');

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (737, 'MQTT', 'MQTT');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (738, '网页通知', 'Web Notifications');

INSERT INTO `notify_channel` (`name`, `channel_type`, `provider`, `config_content`, `tenant_id`, `tenant_name`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES ('mqtt推送', 'mqtt', 'web', '{}', 1, 'admin', NULL, '2024-12-30 14:15:17', NULL, '2024-12-30 14:15:17', 0);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `notify_template` (`name`, `service_code`, `channel_id`, `channel_type`, `provider`, `msg_params`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `tenant_id`, `tenant_name`)
VALUES ('MQTT网页通知', 'alert', @parentId, 'mqtt', 'web', '{\"content\":\"设备编号：${0}，设备地址：${1}，当前值：${2}，报警限值：${3}，报警时间：${4}\"}', 1, NULL, '2024-12-30 14:35:32', NULL, '2024-12-30 14:53:30', 0, 1, 'admin');

-- 兼容逻辑删除与唯一索引字段
ALTER TABLE `iot_device`
DROP INDEX `iot_device_index_serial_number`,
ADD UNIQUE INDEX `iot_device_index_serial_number`(`serial_number`, `del_flag`) USING BTREE;

ALTER TABLE `iot_protocol`
DROP INDEX `UNIQUE_CODE`,
ADD UNIQUE INDEX `UNIQUE_CODE`(`protocol_code`, `del_flag`) USING BTREE;

ALTER TABLE `iot_social_platform`
DROP INDEX `iot_social_platform_platform_uindex`,
ADD UNIQUE INDEX `iot_social_platform_platform_uindex`(`platform`, `del_flag`) USING BTREE;

ALTER TABLE `iot_social_user`
DROP INDEX `iot_social_user_pk`,
ADD UNIQUE INDEX `iot_social_user_pk`(`social_user_id`, `del_flag`) USING BTREE;

ALTER TABLE `iot_protocol`
MODIFY COLUMN `del_flag` int(11) NULL DEFAULT 0 COMMENT '0:正常 1:删除' AFTER `protocol_status`;

ALTER TABLE `iot_social_platform`
MODIFY COLUMN `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记位(0代表存在，1代表删除)' AFTER `redirect_uri`;

ALTER TABLE `iot_social_user`
MODIFY COLUMN `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记位(0代表存在,2代表删除)';

update iot_device set del_flag = null where del_flag = '1';

update iot_protocol set del_flag = null where del_flag = 1;

update iot_social_platform set del_flag = null where del_flag = '1';

update iot_social_user set del_flag = null where del_flag = '1';

ALTER TABLE bridge
ADD COLUMN tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
ADD COLUMN tenant_name VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '租户名称';

update bridge
set tenant_id = 1,
    tenant_name = 'admin';

INSERT INTO `sys_dict_data` VALUES (739, 5, 'HTTP', 'HTTP', 'iot_transport_type', NULL, 'default', 'N', 0, 'admin', '2023-05-12 14:25:39', 'admin', '2023-05-12 14:26:09', NULL);
INSERT INTO `sys_dict_data_translate` VALUES (739, 'HTTP', 'HTTP');

ALTER TABLE `order_control`
    MODIFY COLUMN `img_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '图片路径';

ALTER TABLE `iot_device`
DROP COLUMN `wireless_version`;

ALTER TABLE `iot_product`
    ADD COLUMN `firmware_type` tinyint(2) NULL DEFAULT NULL COMMENT '1,二进制包升级2.http升级';

update iot_product
set firmware_type = 2 where firmware_type is null;

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
    VALUES (167, '固件类型', 'iot_firmware_type', 0, 'admin', '2025-02-13 16:11:41', 'admin', '2025-02-13 16:12:49', NULL);

INSERT INTO `sys_dict_type_translate` (`id`, `zh_cn`, `en_us`) VALUES (167, '固件类型', 'Firmware Type');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (740, 1, '分包拉取', '1', 'iot_firmware_type', NULL, 'default', 'N', 0, 'admin', '2025-02-13 16:14:55', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (741, 2, 'HTTP', '2', 'iot_firmware_type', NULL, 'default', 'N', 0, 'admin', '2025-02-13 16:15:22', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (740, '分包拉取', 'Subcontract Drawing');
INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (741, 'HTTP', 'HTTP');

ALTER TABLE `scene_model`
    CHANGE COLUMN `desc` `scene_desc` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场景描述';

ALTER TABLE gen_table_column
    MODIFY COLUMN table_id bigint(20) NULL DEFAULT NULL COMMENT '归属表编号';

ALTER TABLE `iot_device_user`
DROP PRIMARY KEY;

ALTER TABLE `iot_device_share`
DROP PRIMARY KEY;


