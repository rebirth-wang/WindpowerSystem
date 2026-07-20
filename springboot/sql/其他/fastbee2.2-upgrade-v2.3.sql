-- ----------------------------
-- 注意注意：以下部分sql重构了表结构，会清空部分表的数据，需要根据自己的系统进行调整
-- 更新前建议备份数据，数据丢失蜂信物联概不负责
-- ----------------------------
-- 机构表新加字段

ALTER TABLE `sys_dept`
ADD COLUMN `dept_user_id` bigint(20) NULL DEFAULT NULL COMMENT '机构系统账号ID',
ADD COLUMN `dept_type` tinyint(11) NULL DEFAULT NULL COMMENT '机构类型';



-- 删除 iot_device表  user_id 、user_name

ALTER TABLE `iot_device`
DROP COLUMN `user_id`,
DROP COLUMN `user_name`;



-- 新建设备分享表

CREATE TABLE `iot_device_share`  (
  `device_id` bigint NOT NULL COMMENT '设备id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `phonenumber` varchar(20) NULL COMMENT '手机',
  `perms` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户物模型权限，多个以英文逗号分隔',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`device_id`, `user_id`),
  INDEX `PRIMARK_DEVICE_USER`(`device_id`, `user_id`) USING BTREE COMMENT '设备与用户索引'
) COMMENT = '设备分享表';



--  设备用户表删除字段

ALTER TABLE `iot_device_user`
DROP COLUMN `device_name`,
DROP COLUMN `user_name`,
DROP COLUMN `perms`,

DROP COLUMN `tenant_id`,
DROP COLUMN `tenant_name`,
DROP COLUMN `is_owner`;




--  设备分享权限

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享', 2007, 1, 'share', 'iot/share/index', NULL, 1, 0, 'C', '0', '0', 'iot:share:list', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '设备分享菜单');

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享用户', @parentId, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:share:user', '#', 'admin', '2024-05-09 09:38:07', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:share:query', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:share:add', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:share:edit', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:share:remove', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '');
INSERT INTO `sys_menu`(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('设备分享导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'iot:share:export', '#', 'admin', '2024-04-17 14:20:33', '', NULL, '');

 -- 产品表增加是否是私有产品
ALTER TABLE `iot_product`
ADD COLUMN `is_owner` tinyint(255) NULL DEFAULT NULL COMMENT '是否是私有产品(0-否，1-是)';



-- 告警表增加租户字段

ALTER TABLE iot_alert ADD COLUMN tenant_id bigint(20) NULL DEFAULT NULL COMMENT '租户id', ADD COLUMN tenant_name varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称';




-- 菜单表新增权限

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES ('设备分配', 2007, 10, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:assignment', '#', 'admin', '2024-04-10 09:40:59', '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES ('设备回收', 2007, 11, '', NULL, NULL, 1, 0, 'F', '0', '0', 'iot:device:recovery', '#', 'admin', '2024-04-10 09:41:16', '', NULL, '');




-- 云云对接表增加租户字段

ALTER TABLE oauth_client_details ADD COLUMN tenant_id bigint(20) NOT NULL COMMENT '租户id',

ADD COLUMN tenant_name varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称';




-- 场景表增加区分终端用户表

ALTER TABLE `iot_scene`  ADD COLUMN `terminal_user` tinyint(1) NULL DEFAULT 0 COMMENT '是否终端用户（1-是，0-不是)';



-- 大屏加租户字段

ALTER TABLE iot_goview_project ADD COLUMN tenant_id bigint(20) NULL DEFAULT NULL COMMENT '租户id', ADD COLUMN tenant_name varchar(30) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '租户名称';



-- 机构类型枚举

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) VALUES ('机构类型', 'department_type', '0', 'admin', '2024-02-29 11:38:14', '', NULL, NULL);

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES (1, '厂商', '1', 'department_type', NULL, 'success', 'N', '0', 'admin', '2024-02-29 14:07:54', 'admin', '2024-03-20 11:55:36', '2,3,4');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES (2, '生产厂商', '2', 'department_type', NULL, 'primary', 'N', '0', 'admin', '2024-02-29 14:09:06', 'admin', '2024-03-20 11:55:45', '3,4');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES (2, '经销商', '3', 'department_type', NULL, 'warning', 'N', '0', 'admin', '2024-02-29 14:09:25', 'admin', '2024-03-20 11:55:52', '3,4');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES (4, '服务商', '4', 'department_type', NULL, 'info', 'N', '0', 'admin', '2024-02-29 14:09:34', 'admin', '2024-03-20 11:55:57', '4');

-- 新增企业管理菜单

INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES ('企业管理', 0, 5, 'enterprise', NULL, '', 1, 0, 'M', '0', '0', '', 'authenticate', 'admin', '2021-12-15 21:36:18', 'admin', '2024-03-20 15:57:01', '企业管理目录');
SELECT @parentId := LAST_INSERT_ID();

update sys_menu
set parent_id = @parentId
where menu_name in ('用户管理','角色管理','菜单管理','部门管理');

update sys_menu
set menu_name = '机构管理'
where menu_name = '部门管理';



-- 刷新部门表数据

truncate table sys_dept;

INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `dept_user_id`, `dept_type`) VALUES (100, 0, '0', '蜂信物联', 0, 'FastBee', '15888888888', '164770707@qq.com', '0', '0', 'admin', '2021-12-15 21:36:18', 'admin', '2023-02-26 23:06:24', 1, 1);

update sys_user
set dept_id = 100
where user_id = 1;

-- 刷新角色部门表数据

truncate table sys_role_dept;

INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (1, 100);
INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (2, 100);
INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (3, 100);
INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (4, 100);
INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (5, 100);
INSERT INTO `fastbee`.`sys_role_dept` (`role_id`, `dept_id`) VALUES (6, 100);

-- 刷新角色菜单数据
delete from sys_role_menu
where role_id in (3,4);

-- 更新普通用户、游客角色权限
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
INSERT INTO `sys_role_menu` VALUES (3, 2043);
INSERT INTO `sys_role_menu` VALUES (3, 2067);
INSERT INTO `sys_role_menu` VALUES (3, 2068);
INSERT INTO `sys_role_menu` VALUES (3, 2070);
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
INSERT INTO `sys_role_menu` VALUES (3, 3001);
INSERT INTO `sys_role_menu` VALUES (3, 3002);
INSERT INTO `sys_role_menu` VALUES (3, 3003);
INSERT INTO `sys_role_menu` VALUES (3, 3004);
INSERT INTO `sys_role_menu` VALUES (3, 3005);
INSERT INTO `sys_role_menu` VALUES (3, 3006);
INSERT INTO `sys_role_menu` VALUES (3, 3031);
INSERT INTO `sys_role_menu` VALUES (3, 3032);
INSERT INTO `sys_role_menu` VALUES (3, 3033);
INSERT INTO `sys_role_menu` VALUES (3, 3034);
INSERT INTO `sys_role_menu` VALUES (3, 3035);
INSERT INTO `sys_role_menu` VALUES (3, 3036);
INSERT INTO `sys_role_menu` VALUES (3, 3037);
INSERT INTO `sys_role_menu` VALUES (3, 3038);
INSERT INTO `sys_role_menu` VALUES (3, 3044);
INSERT INTO `sys_role_menu` VALUES (3, 3046);
INSERT INTO `sys_role_menu` VALUES (3, 3051);
INSERT INTO `sys_role_menu` VALUES (3, 3159);
INSERT INTO `sys_role_menu` VALUES (3, 3178);
INSERT INTO `sys_role_menu` VALUES (3, 3206);
INSERT INTO `sys_role_menu` VALUES (3, 3207);
INSERT INTO `sys_role_menu` VALUES (3, 3226);
INSERT INTO `sys_role_menu` VALUES (3, 3238);
INSERT INTO `sys_role_menu` VALUES (3, 3247);
INSERT INTO `sys_role_menu` VALUES (3, 3249);
INSERT INTO `sys_role_menu` VALUES (3, 3250);
INSERT INTO `sys_role_menu` VALUES (3, 3251);
INSERT INTO `sys_role_menu` VALUES (3, 3252);
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
INSERT INTO `sys_role_menu` VALUES (4, 124);
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
INSERT INTO `sys_role_menu` VALUES (4, 3045);
INSERT INTO `sys_role_menu` VALUES (4, 3046);
INSERT INTO `sys_role_menu` VALUES (4, 3047);
INSERT INTO `sys_role_menu` VALUES (4, 3048);
INSERT INTO `sys_role_menu` VALUES (4, 3049);
INSERT INTO `sys_role_menu` VALUES (4, 3051);
INSERT INTO `sys_role_menu` VALUES (4, 3055);
INSERT INTO `sys_role_menu` VALUES (4, 3147);
INSERT INTO `sys_role_menu` VALUES (4, 3157);
INSERT INTO `sys_role_menu` VALUES (4, 3159);
INSERT INTO `sys_role_menu` VALUES (4, 3166);
INSERT INTO `sys_role_menu` VALUES (4, 3167);
INSERT INTO `sys_role_menu` VALUES (4, 3168);
INSERT INTO `sys_role_menu` VALUES (4, 3169);
INSERT INTO `sys_role_menu` VALUES (4, 3172);
INSERT INTO `sys_role_menu` VALUES (4, 3173);
INSERT INTO `sys_role_menu` VALUES (4, 3174);
INSERT INTO `sys_role_menu` VALUES (4, 3175);
INSERT INTO `sys_role_menu` VALUES (4, 3176);
INSERT INTO `sys_role_menu` VALUES (4, 3178);
INSERT INTO `sys_role_menu` VALUES (4, 3179);
INSERT INTO `sys_role_menu` VALUES (4, 3180);
INSERT INTO `sys_role_menu` VALUES (4, 3181);
INSERT INTO `sys_role_menu` VALUES (4, 3184);
INSERT INTO `sys_role_menu` VALUES (4, 3185);
INSERT INTO `sys_role_menu` VALUES (4, 3186);
INSERT INTO `sys_role_menu` VALUES (4, 3187);
INSERT INTO `sys_role_menu` VALUES (4, 3189);
INSERT INTO `sys_role_menu` VALUES (4, 3190);
INSERT INTO `sys_role_menu` VALUES (4, 3191);
INSERT INTO `sys_role_menu` VALUES (4, 3192);
INSERT INTO `sys_role_menu` VALUES (4, 3196);
INSERT INTO `sys_role_menu` VALUES (4, 3197);
INSERT INTO `sys_role_menu` VALUES (4, 3198);
INSERT INTO `sys_role_menu` VALUES (4, 3199);
INSERT INTO `sys_role_menu` VALUES (4, 3200);
INSERT INTO `sys_role_menu` VALUES (4, 3202);
INSERT INTO `sys_role_menu` VALUES (4, 3203);
INSERT INTO `sys_role_menu` VALUES (4, 3204);
INSERT INTO `sys_role_menu` VALUES (4, 3205);
INSERT INTO `sys_role_menu` VALUES (4, 3206);
INSERT INTO `sys_role_menu` VALUES (4, 3207);
INSERT INTO `sys_role_menu` VALUES (4, 3208);
INSERT INTO `sys_role_menu` VALUES (4, 3210);
INSERT INTO `sys_role_menu` VALUES (4, 3211);
INSERT INTO `sys_role_menu` VALUES (4, 3212);
INSERT INTO `sys_role_menu` VALUES (4, 3213);
INSERT INTO `sys_role_menu` VALUES (4, 3214);
INSERT INTO `sys_role_menu` VALUES (4, 3217);
INSERT INTO `sys_role_menu` VALUES (4, 3218);
INSERT INTO `sys_role_menu` VALUES (4, 3219);
INSERT INTO `sys_role_menu` VALUES (4, 3220);
INSERT INTO `sys_role_menu` VALUES (4, 3223);
INSERT INTO `sys_role_menu` VALUES (4, 3224);
INSERT INTO `sys_role_menu` VALUES (4, 3225);
INSERT INTO `sys_role_menu` VALUES (4, 3226);
INSERT INTO `sys_role_menu` VALUES (4, 3229);
INSERT INTO `sys_role_menu` VALUES (4, 3230);
INSERT INTO `sys_role_menu` VALUES (4, 3231);
INSERT INTO `sys_role_menu` VALUES (4, 3232);
INSERT INTO `sys_role_menu` VALUES (4, 3235);
INSERT INTO `sys_role_menu` VALUES (4, 3236);
INSERT INTO `sys_role_menu` VALUES (4, 3237);
INSERT INTO `sys_role_menu` VALUES (4, 3238);
INSERT INTO `sys_role_menu` VALUES (4, 3239);
INSERT INTO `sys_role_menu` VALUES (4, 3240);
INSERT INTO `sys_role_menu` VALUES (4, 3243);
INSERT INTO `sys_role_menu` VALUES (4, 3244);
INSERT INTO `sys_role_menu` VALUES (4, 3249);
INSERT INTO `sys_role_menu` VALUES (4, 3250);
INSERT INTO `sys_role_menu` VALUES (4, 3251);
INSERT INTO `sys_role_menu` VALUES (4, 3252);
INSERT INTO `sys_role_menu` VALUES (4, 3253);
INSERT INTO `sys_role_menu` VALUES (4, 3256);
INSERT INTO `sys_role_menu` VALUES (4, 3269);
INSERT INTO `sys_role_menu` VALUES (4, 3270);
INSERT INTO `sys_role_menu` VALUES (4, 3271);
INSERT INTO `sys_role_menu` VALUES (4, 3277);



-- 删除视频配置表字段

ALTER TABLE `sip_config`
DROP COLUMN `tenant_id`,
DROP COLUMN `tenant_name`;

-- 更新老数据绑定到管理员账户
update iot_goview_project
set tenant_id = 1,
	  tenant_name = 'admin';

update iot_scene
set user_id = 1,
	  user_name = 'admin';

update iot_script
set user_id = 1,
	  user_name = 'admin';

update iot_alert
set tenant_id = 1,
	  tenant_name = 'admin';

update iot_alert_log
set user_id = 1;

update oauth_client_details
set tenant_id = 1,
	  tenant_name = 'admin';

update sys_user
set dept_id = 100;

-- 此机构为web端注册用户绑定机构，其中dept_id必须为101，后端写死的
INSERT INTO `sys_dept` (`dept_id`, `dept_user_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `email`, `dept_type`)
VALUES (101, 0, 100, '0,100', 'web端注册用户归属机构', 0, 'fastbee-web', '15888888885', '0', '0', 'admin', '2024-05-08 16:35:48', 'admin', '2024-05-08 16:36:15', NULL, 2);

-- 新增web端注册用户绑定机构
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('管理员', 'manager', 1, '1', 0, 0, '0', '0', '', '2024-05-08 16:35:49', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_role_menu` VALUES (@parentId, 3);
INSERT INTO `sys_role_menu` VALUES (@parentId, 4);
INSERT INTO `sys_role_menu` VALUES (@parentId, 5);
INSERT INTO `sys_role_menu` VALUES (@parentId, 100);
INSERT INTO `sys_role_menu` VALUES (@parentId, 101);
INSERT INTO `sys_role_menu` VALUES (@parentId, 103);
INSERT INTO `sys_role_menu` VALUES (@parentId, 114);
INSERT INTO `sys_role_menu` VALUES (@parentId, 115);
INSERT INTO `sys_role_menu` VALUES (@parentId, 116);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1001);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1002);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1003);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1004);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1005);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1006);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1007);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1008);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1009);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1010);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1011);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1012);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1017);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1018);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1019);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1020);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1055);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1056);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1057);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1058);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1059);
INSERT INTO `sys_role_menu` VALUES (@parentId, 1060);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2000);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2001);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2002);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2003);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2006);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2007);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2008);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2009);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2010);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2011);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2012);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2013);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2014);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2015);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2016);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2017);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2018);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2019);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2020);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2021);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2022);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2023);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2024);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2043);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2044);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2045);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2046);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2047);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2048);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2049);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2050);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2051);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2054);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2067);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2068);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2069);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2070);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2071);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2072);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2085);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2086);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2087);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2088);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2089);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2090);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2099);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2100);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2101);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2102);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2103);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2104);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2105);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2106);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2107);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2108);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2109);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2111);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2112);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2136);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2137);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2138);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2139);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2140);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2143);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2144);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2145);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2146);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2147);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2148);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2149);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2167);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2168);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2169);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2170);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2171);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2172);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2173);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2174);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2175);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2176);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2177);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2178);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2179);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2180);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2181);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2182);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2183);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2184);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3000);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3001);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3002);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3003);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3004);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3005);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3006);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3007);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3008);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3009);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3010);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3011);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3012);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3013);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3014);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3015);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3016);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3017);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3018);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3019);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3020);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3021);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3022);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3023);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3024);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3025);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3026);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3027);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3028);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3029);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3030);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3031);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3032);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3033);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3034);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3035);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3036);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3037);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3038);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3039);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3040);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3041);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3042);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3043);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3044);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3046);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3047);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3048);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3049);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3051);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3052);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3055);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3099);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3100);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3102);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3103);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3104);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3105);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3106);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3107);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3108);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3109);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3110);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3111);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3112);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3113);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3114);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3115);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3116);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3117);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3147);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3148);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3149);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3150);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3151);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3152);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3153);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3154);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3155);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3156);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3157);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3158);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3159);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3160);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3161);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3162);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3163);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3164);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3165);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3166);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3167);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3168);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3169);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3170);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3171);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3172);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3173);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3174);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3175);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3176);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3177);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3178);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3179);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3180);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3181);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3182);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3183);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3184);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3185);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3186);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3187);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3188);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3189);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3190);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3191);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3197);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3198);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3199);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3200);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3201);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3202);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3203);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3204);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3205);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3206);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3207);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3208);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3209);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3210);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3211);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3214);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3215);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3216);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3217);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3218);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3219);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3220);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3221);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3222);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3223);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3224);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3225);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3226);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3237);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3238);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3239);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3240);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3247);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3248);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3249);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3250);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3251);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3252);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3253);

INSERT INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (@parentId, 101);

INSERT INTO `sys_user` (`dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (101, 'fastbee-web', 'fastbee-web', '00', '', '15888888885', '0', '', '$2a$10$nwso5Yvdl6pfntn/wuz0MO6BqKTybKXdtb3f326XR8XRVaoD5OTYm', '0', '0', '', NULL, '', '2024-05-08 16:35:49', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

update sys_dept
set dept_user_id = @parentId
where dept_id = 101;

INSERT INTO `sys_user_role`(`user_id`, `role_id`)
VALUES (@parentId, (select rd.role_id from sys_dept d left join sys_role_dept rd on d.dept_id = rd.dept_id where d.dept_id = 101));

-- web端注册用户绑定角色，其中role_key必须为general，后端代码里写死的
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('web端注册用户', 'general', 0, '1', 1, 1, '0', '0', 'fastbee-web', '2024-05-08 16:55:16', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_role_menu` VALUES (@parentId, 2000);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2007);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2008);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2009);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2010);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2011);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2012);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2013);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2014);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2015);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2016);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2017);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2018);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2019);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2020);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2021);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2043);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2044);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2147);
INSERT INTO `sys_role_menu` VALUES (@parentId, 2148);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3000);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3001);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3002);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3003);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3007);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3008);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3013);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3014);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3015);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3016);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3017);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3018);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3019);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3020);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3021);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3022);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3023);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3024);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3025);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3026);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3027);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3028);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3029);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3030);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3033);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3034);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3035);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3039);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3040);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3041);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3042);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3043);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3044);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3206);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3207);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3208);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3209);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3210);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3214);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3215);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3216);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3217);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3218);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3219);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3220);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3221);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3222);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3247);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3248);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3249);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3250);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3251);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3252);
INSERT INTO `sys_role_menu` VALUES (@parentId, 3253);

INSERT INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (@parentId, 101);

-- 新增设备联动、终端用户菜单，需自行修改parent_id
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('设备联动', 2000, 7, 'linkage', 'iot/device/device-linkage', NULL, 1, 0, 'C', '0', '0', NULL, 'tree-table', 'admin', '2024-05-10 17:54:12', '', NULL, '');
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('终端用户', 5, 5, 'terminal', 'system/terminalUser/index', NULL, 1, 0, 'C', '0', '0', NULL, 'build', 'admin', '2024-05-10 17:55:00', '', NULL, '');

-- 修复小程序头像修改字段超长问题
ALTER TABLE `sys_user`
    MODIFY COLUMN `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址';

-- 设备新增告警表
-- 新增设备告警用户配置表
DROP TABLE IF EXISTS `iot_device_alert_user`;
CREATE TABLE `iot_device_alert_user` (
                                         `device_id` bigint(20) NOT NULL COMMENT '设备id',
                                         `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                         PRIMARY KEY (`device_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='设备告警用户表';


-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('告警用户', '2007', '1', 'user', 'iot/device/alert/user', 1, 0, 'C', '0', '0', 'iot:device:alert:user:list', '#', 'admin', sysdate(), '', null, '设备告警用户菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('告警用户查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'iot:device:alert:user:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('告警用户新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'iot:device:alert:user:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('告警用户删除', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'iot:device:alert:user:remove',       '#', 'admin', sysdate(), '', null, '');

-- 设备告警记录更改一下菜单权限标识，menu_id请自行修改，或者直接在菜单改就行了
update sys_menu
set perms = 'iot:alertLog:list'
where menu_id = 2067;
update sys_menu
set perms = 'iot:alertLog:query'
where menu_id = 2068;
update sys_menu
set perms = 'iot:alertLog:add'
where menu_id = 2069;
update sys_menu
set perms = 'iot:alertLog:edit'
where menu_id = 2070;
update sys_menu
set perms = 'iot:alertLog:remove'
where menu_id = 2071;
update sys_menu
set perms = 'iot:alertLog:export'
where menu_id = 2072;

-- ----------------------------
-- Table structure for oss_config
-- ----------------------------
DROP TABLE IF EXISTS `oss_config`;
CREATE TABLE `oss_config`
(
    `id`              int(10)       UNSIGNED                                   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `tenant_id`       bigint(20) NOT NULL DEFAULT 1 COMMENT '租户ID',
    `tenant_name`     varchar(30)   CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
    `config_key`      varchar(20)   CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '配置key',
    `access_key`      varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'accessKey',
    `secret_key`      varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '秘钥',
    `bucket_name`     varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '桶名称',
    `prefix`          varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT'前缀',
    `endpoint`        varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '访问站点',
    `domain`          varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '自定义域名',
    `is_https`        char(1)       CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
    `region`          varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '域',
    `access_policy`   char(1)       CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '桶权限类型(0=private 1=public 2=custom)',
    `status`          char(1)       CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否默认（0=是,1=否）',
    `ext1`            varchar(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段',
    `del_flag`        char(1)       CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`       varchar(64)   CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime(0)                                              NOT NULL COMMENT '创建时间',
    `update_by`       varchar(64)   CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT '' COMMENT '更新者',
    `update_time`     datetime(0)                                              NULL     DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500)  CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '对象存储配置表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oss_config
-- ----------------------------
insert into oss_config values (1, 1, 'admin', 'aliyun', 'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'fastbee','', 'oss-cn-beijing.aliyuncs.com',   '','N', '','0' ,'0', '', '0', '', '2023-02-25 23:15:57', '', NULL, NULL);
insert into oss_config values (2, 1, 'admin', 'qiniu',  'XXXXXXXXXXXXXXX',  'XXXXXXXXXXXXXXX', 'fastbee','', 's3-cn-north-1.qiniucs.com',     '','N', '','1' ,'1', '', '0', '', '2023-02-25 23:15:57', '', NULL, NULL);

-- ----------------------------
-- Table structure for oss_detail
-- ----------------------------
DROP TABLE IF EXISTS `oss_detail`;
CREATE TABLE `oss_detail`
(
    `id`                int(10) UNSIGNED                                        NOT NULL AUTO_INCREMENT COMMENT '文件id',
    `tenant_id`         bigint(20) NOT NULL DEFAULT 1 COMMENT '租户ID',
    `tenant_name`       varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '租户名称',
    `file_name`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
    `original_name`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '原名',
    `file_suffix`       varchar(10)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '文件后缀名',
    `url`               varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'URL地址',
    `service`           varchar(20)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'aliyun'   COMMENT '服务商',
    `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime(0) NOT NULL COMMENT '创建时间',
    `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '文件记录表'
  ROW_FORMAT = Dynamic;

-- 文件记录菜单SQL
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录', 3046, 4, 'detail', 'system/oss/index', NULL, 1, 0, 'C', '0', '0', 'oss:detail:list', 'documentation', 'admin', sysdate(), '', null, '文件记录菜单');
-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();
-- 文件记录按钮SQL
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录查询', @parentId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'oss:detail:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录新增', @parentId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'oss:detail:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录修改', @parentId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'oss:detail:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录删除', @parentId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'oss:detail:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件记录导出', @parentId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'oss:detail:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件存储配置新增', @parentId, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'oss:config:add', '#', 'admin', sysdate(), 'admin', null, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件存储配置修改', @parentId, 7, '', NULL, NULL, 1, 0, 'F', '0', '0', 'oss:config:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('文件存储配置删除', @parentId, 8, '', NULL, NULL, 1, 0, 'F', '0', '0', 'oss:config:remove', '#', 'admin', sysdate(), '', NULL, '');


