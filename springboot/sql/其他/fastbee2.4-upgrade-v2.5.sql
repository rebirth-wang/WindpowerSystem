-- 数据库版本升级脚本
-- 适用于fastbee2.4版本到fastbee2.5版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作

-- modbus产品改造
CREATE TABLE `iot_product_modbus_job` (
                                          `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
                                          `job_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '任务名称',
                                          `product_id` bigint(20) NOT NULL COMMENT '产品id',
                                          `command` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '指令',
                                          `status` char(1) COLLATE utf8_unicode_ci NOT NULL COMMENT '状态（0正常 1暂停）',
                                          `create_by` varchar(64) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '创建者',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `remark` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '备注信息',
                                          PRIMARY KEY (`task_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='产品轮训任务列表';

CREATE TABLE `iot_product_sub_gateway` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
                                           `gw_product_id` bigint(20) NOT NULL COMMENT '网关产品id',
                                           `sub_product_id` bigint(20) NOT NULL COMMENT '子产品id',
                                           `slave_id` int(6) DEFAULT NULL COMMENT '从机地址',
                                           `create_by` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建者',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_by` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '更新者',
                                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `remark` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备注',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='网关与子产品关联表';

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3363, '产品modbus关联子产品', 2043, 16, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:gateway:list', '#', 'admin', '2024-09-11 14:44:04', 'admin', '2024-09-11 14:48:00', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3364, '新增产品modbus关联子产品', 3363, 0, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:gateway:add', '#', 'admin', '2024-09-11 14:45:27', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3365, '修改产品modbus关联子产品', 3363, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:gateway:edit', '#', 'admin', '2024-09-11 14:45:57', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3366, '删除产品modbus关联子产品', 3363, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:gateway:remove', '#', 'admin', '2024-09-11 14:46:20', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3367, '产品modbus轮询任务', 2043, 17, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:job:list', '#', 'admin', '2024-09-11 14:47:40', 'admin', '2024-09-11 14:48:08', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3368, '新增产品modbus轮询任务', 3367, 0, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:job:add', '#', 'admin', '2024-09-11 14:48:35', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3369, '修改产品modbus轮询任务', 3367, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:job:edit', '#', 'admin', '2024-09-11 14:48:54', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3370, '删除产品modbus轮询任务', 3367, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'productModbus:job:remove', '#', 'admin', '2024-09-11 14:54:59', '', NULL, '');

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3363, '产品modbus关联子产品', 'Product Modbus related sub products');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3364, '新增产品modbus关联子产品', 'New product Modbus associated sub product');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3365, '修改产品modbus关联子产品', 'Modify product Modbus associated sub products');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3366, '删除产品modbus关联子产品', 'Delete product Modbus associated sub products');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3367, '产品modbus轮询任务', 'Product Modbus polling task');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3368, '新增产品modbus轮询任务', 'New product Modbus polling task');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3369, '修改产品modbus轮询任务', 'Modify product Modbus polling task');
INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3370, '删除产品modbus轮询任务', 'Delete product Modbus polling task');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3371, 'modbus配置导入', 3298, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'modbus:config:import', '#', 'admin', '2024-09-11 16:10:35', '', NULL, '');

INSERT INTO `sys_menu_translate` (`id`, `zh_cn`, `en_us`) VALUES (3371, 'modbus配置导入', 'Modbus configuration import');

-- 协议管理
ALTER TABLE `iot_protocol`
    ADD COLUMN `display` tinyint(2) NOT NULL DEFAULT 1 COMMENT '显示，1-显示；0-不显示';

ALTER TABLE `iot_protocol`
    ADD COLUMN `data_format` text NULL COMMENT '协议数据格式';


ALTER TABLE `iot_device`
    add COLUMN `wireless_version` float(11) null COMMENT '分包拉取固件版本';

ALTER TABLE `bridge`
    MODIFY COLUMN `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

