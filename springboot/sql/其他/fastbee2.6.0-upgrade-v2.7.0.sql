-- 数据库版本升级脚本
-- 适用于fastbee2.6.0版本到fastbee2.6.1版本的数据库升级
-- 注意：请在备份好数据库后再进行升级操作

-- 数据权限相关sql更改
update `app_language`
set create_by = 'admin',
    create_time = now();

update bridge s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_alert s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

ALTER TABLE `iot_alert_log`
    ADD COLUMN `scene_id` bigint(20) NULL COMMENT '场景ID';

update `iot_alert_log`
set scene_id = create_by;

UPDATE iot_alert_log al
    JOIN sys_user u ON al.user_id = u.user_id
    SET al.create_by = u.user_name;

update iot_category s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_device s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_firmware s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_goview_project s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_group s
    JOIN sys_user u ON s.user_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.user_name = d.dept_name,
        s.create_by = u.user_name;

update iot_group s
    JOIN sys_user u ON s.user_id = u.user_id
    set s.create_by = u.user_name
    where u.dept_id is null;

update iot_product s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_product_modbus_job pm
  join iot_product p on pm.product_id = p.product_id
   set pm.create_by = p.create_by;

update iot_scene
set create_by = user_name
where create_by is null || create_by = '';

update iot_scene s
    JOIN sys_user u ON s.user_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.user_name = d.dept_name;

update iot_script
set create_by = user_name
where create_by is null || create_by = '';

update iot_script s
    JOIN sys_user u ON s.user_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.user_name = d.dept_name;

update iot_things_model s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update iot_things_model_template s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update media_server s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update notify_channel s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update notify_template s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update notify_log s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

update order_control o
join sys_dept d on o.tenant_id = d.dept_id
join sys_user u on d.dept_user_id = u.user_id
set o.create_by = u.user_name,
    o.tenant_id = d.dept_user_id;

update oss_config s
    JOIN sys_user u ON s.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    set s.tenant_name = d.dept_name,
        s.create_by = u.user_name;

UPDATE oss_detail o
    JOIN sys_user u ON o.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    SET o.tenant_name = d.dept_name,
        o.create_by = u.user_name;

update sip_device_channel
set create_by = tenant_name
where create_by is null || create_by = '';

update sip_device_channel s
    join sys_dept d on s.tenant_id = d.dept_id
    join sys_user u on d.dept_user_id = u.user_id
    set s.tenant_id = d.dept_user_id,
        s.tenant_name = d.dept_name, s.create_by = u.user_name;

ALTER TABLE scene_model_data
    MODIFY COLUMN variable_type tinyint(4) NULL DEFAULT NULL COMMENT '来源类型(0设备 1录入型 2运算型)';

ALTER TABLE `oauth_client_details`
    ADD COLUMN `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人' AFTER `tenant_name`;

update oauth_client_details o
    JOIN sys_user u ON o.tenant_id = u.user_id
    JOIN sys_dept d ON u.dept_id = d.dept_id
    SET o.tenant_name = d.dept_name,
        o.create_by = u.user_name;

ALTER TABLE `sys_role`
    MODIFY COLUMN `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）';

update sys_role
    set data_scope = 3;

update sys_role
   set role_name = '终端注册用户角色',
       role_key = 'terminalRegister',
       data_scope = 5
   where role_id = 3;

-- web端注册用户角色搬到第一个机构
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('web端注册用户角色', 'webRegister', 8, '5', 1, 1, 0, '0', 'admin', '2025-04-10 14:30:42', '', NULL, NULL);

SELECT @parentId := LAST_INSERT_ID();

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 100);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 107);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 1001);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 1017);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 1036);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2000);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2001);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2002);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2003);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2004);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2005);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2006);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2007);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2008);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2009);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2010);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2011);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2012);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2013);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2014);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2015);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2016);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2017);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2018);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2019);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2020);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2021);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2022);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2023);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2024);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2043);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2044);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2045);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2046);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2047);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2048);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2049);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2050);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2051);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2052);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2053);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2054);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2067);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2068);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2069);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2070);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2071);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2072);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2085);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2086);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2087);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2088);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2089);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2090);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2099);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2100);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2101);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2102);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2136);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2137);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2138);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2139);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2140);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2147);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2148);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2149);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2167);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2168);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2169);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2170);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2173);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2174);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2175);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2176);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2177);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2178);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2179);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2180);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2181);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2182);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2183);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 2184);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3000);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3001);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3002);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3003);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3007);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3008);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3009);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3010);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3011);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3012);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3033);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3034);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3035);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3044);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3046);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3047);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3049);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3051);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3052);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3055);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3102);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3104);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3105);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3106);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3107);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3108);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3109);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3110);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3111);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3112);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3113);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3114);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3115);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3116);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3117);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3147);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3148);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3149);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3150);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3151);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3152);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3153);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3154);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3155);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3156);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3157);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3158);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3159);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3160);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3161);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3162);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3163);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3164);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3165);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3166);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3167);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3168);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3169);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3170);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3171);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3172);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3173);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3174);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3178);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3179);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3180);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3181);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3182);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3183);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3184);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3185);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3186);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3187);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3188);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3189);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3190);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3191);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3197);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3198);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3199);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3200);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3201);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3202);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3203);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3204);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3205);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3206);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3207);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3208);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3209);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3210);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3211);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3214);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3215);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3216);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3217);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3218);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3219);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3220);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3221);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3222);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3226);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3247);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3248);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3249);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3250);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3251);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3252);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3253);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3254);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3255);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3257);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3258);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3259);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3260);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3270);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3271);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3272);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3273);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3274);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3275);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3277);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3278);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3286);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3287);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3288);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3289);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3290);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3291);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3292);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3293);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3294);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3295);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3296);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3297);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3298);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3299);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3300);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3301);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3302);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3303);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3304);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3305);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3306);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3307);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3308);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3309);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3316);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3317);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3318);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3319);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3320);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3321);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3322);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3323);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3324);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3325);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3326);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3327);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3328);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3329);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3330);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3331);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3332);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3333);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3334);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3335);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3336);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3343);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3344);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3345);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3346);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3347);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3348);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3349);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3350);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3351);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3352);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3353);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3354);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3355);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3356);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3357);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3358);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3359);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3360);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3363);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3364);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3365);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3366);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3367);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3368);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3369);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3370);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3371);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3372);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (@parentId, 3373);

INSERT INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (@parentId, 100);


DELETE FROM sys_user_role
WHERE user_id IN (
    SELECT u.user_id
    FROM sys_user u
    WHERE u.dept_id = 101
      AND u.user_name != 'fastbee-web'
    );

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, @parentId
FROM sys_user u
WHERE u.dept_id = 101
  AND u.user_name != 'fastbee-web';

update sys_user
   set dept_id = 100
 where dept_id = 101
   and user_name != 'fastbee-web';

-- 遗漏菜单权限增加
INSERT INTO `sys_menu` VALUES (3373, '文件存储配置列表', 3261, 9, '', NULL, NULL, 1, 0, 'F', '0', 0, 'oss:config:list', '#', 'admin', '2024-07-29 17:32:41', '', NULL, '');
INSERT INTO `sys_menu_translate` VALUES (3373, '文件存储配置列表', 'A list of file storage configurations');

-- 产品表修改认证信息字段，兼容其他协议
ALTER TABLE `iot_product`
    CHANGE COLUMN `mqtt_account` `account` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '认证账号';

ALTER TABLE `iot_product`
    CHANGE COLUMN `mqtt_password` `auth_password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '认证密码';

ALTER TABLE `iot_product`
    CHANGE COLUMN `mqtt_secret` `secret` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品认证秘钥';

-- modbus配置从机字段名修改
ALTER TABLE `iot_modbus_params`
    CHANGE COLUMN `slave_id` `address` varchar(20) NULL DEFAULT NULL COMMENT '默认的子设备地址';

ALTER TABLE `iot_modbus_config`
    CHANGE COLUMN `slave` `address` varchar(20) NULL DEFAULT NULL COMMENT '子设备地址',
    CHANGE COLUMN `address` `register` int(10) NOT NULL COMMENT '寄存器地址';

ALTER TABLE `iot_product_sub_gateway`
    CHANGE COLUMN `slave_id` `address` varchar(20) NULL DEFAULT NULL COMMENT '从机地址';

-- 网关与子设备设备id字段改为设备编号，新增产品id字段
ALTER TABLE `iot_sub_gateway`
    CHANGE COLUMN `gw_device_id` `parent_client_id` varchar(255) NULL COMMENT '网关设备设备',
    CHANGE COLUMN `sub_device_id` `sub_client_id` varchar(64) NOT NULL COMMENT '子设备编号',
    CHANGE COLUMN `slave_id` `address` varchar(20) NULL DEFAULT NULL COMMENT '子设备地址';

ALTER TABLE `iot_sub_gateway`
    ADD COLUMN `parent_product_id` bigint(20) NULL COMMENT '网关产品id',
    ADD COLUMN `sub_product_id` bigint(20) NULL COMMENT '子设备产品id';

update
    iot_sub_gateway sg
    join iot_device d
on sg.parent_client_id = d.device_id
    set sg.parent_client_id = d.serial_number,
        sg.parent_product_id = d.product_id;

update
    iot_sub_gateway sg
    join iot_device d
on sg.sub_client_id = d.device_id
    set sg.sub_client_id = d.serial_number,
        sg.sub_product_id = d.product_id;

ALTER TABLE `iot_product_modbus_job`
    ADD COLUMN `address` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '从机地址';

update iot_product_modbus_job pm
    join iot_product_sub_gateway ps
on pm.product_id = ps.sub_product_id
    set pm.address = ps.address;

update iot_product_modbus_job pm
    join iot_modbus_params m
on pm.product_id = m.product_id
    set pm.address = m.address
where pm.address is null || pm.address = '';

ALTER TABLE `iot_modbus_job`
    ADD COLUMN `address` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '默认的子设备地址';

ALTER TABLE `iot_sub_gateway`
    MODIFY COLUMN `parent_client_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网关设备设备' AFTER `id`,
    MODIFY COLUMN `sub_client_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '子设备编号' AFTER `parent_client_id`;
ALTER TABLE `iot_modbus_job`
    MODIFY COLUMN `sub_serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '子设备编号' AFTER `sub_device_id`;

update iot_modbus_job mj
    join iot_sub_gateway sg on mj.sub_serial_number = sg.sub_client_id
    join iot_device d on sg.parent_client_id = d.serial_number
    set mj.address = sg.address,
        mj.sub_device_id = d.device_id,
        mj.sub_serial_number = d.serial_number;

update iot_modbus_job mj
    join iot_device d
on mj.sub_device_id = d.device_id
    join iot_modbus_params m
    on d.product_id = m.product_id
    set mj.address = m.address
where mj.address is null || mj.address = '';

ALTER TABLE `iot_modbus_job`
DROP COLUMN `device_type`,
CHANGE COLUMN `sub_device_id` `device_id` bigint(20) NOT NULL COMMENT '设备id',
CHANGE COLUMN `sub_serial_number` `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '设备编号';

INSERT INTO iot_modbus_params (product_id, poll_type, create_by)
SELECT
    p.product_id,
    0 AS poll_type,
    'admin' AS create_by
FROM
    iot_product p
        LEFT JOIN
    iot_modbus_params m ON p.product_id = m.product_id
WHERE
    p.del_flag = 0
  AND p.device_type = 2
  AND (p.protocol_code = 'MODBUS-RTU' OR p.protocol_code = 'MODBUS-TCP')
  AND m.product_id IS NULL;

update
    iot_product p
    join iot_modbus_params mp
on p.product_id = mp.product_id
    join iot_modbus_config mc
    on mp.product_id = mc.product_id
    set mc.address = mp.address
where p.device_type = 1
  and p.del_flag = 0
  and mp.address is not null;

-- 新增拖拽应用配置
ALTER TABLE `iot_product`
    ADD COLUMN `panel_enable` tinyint(1) NULL DEFAULT NULL COMMENT '面板启用（0-未启用，1-启用）';

ALTER TABLE `iot_product`
    ADD COLUMN `panel_models_json` json NULL DEFAULT NULL COMMENT '拖拽面板json数据';

-- ModbusTcp
update iot_product
set protocol_code = 'MODBUS-TCP-OVER-RTU'
where protocol_code = 'MODBUS-TCP';

ALTER TABLE `iot_device`
    ADD COLUMN `device_port` int(10) NULL DEFAULT NULL COMMENT '端口';

ALTER TABLE `iot_product_modbus_job`
    MODIFY COLUMN `command` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '指令' ;

ALTER TABLE `iot_device`
    ADD COLUMN `device_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '设备主机ip';

ALTER TABLE `iot_device`
DROP COLUMN `is_simulate`;

ALTER TABLE `iot_modbus_job`
    MODIFY COLUMN `job_id` bigint(20) NULL COMMENT '任务id';

ALTER TABLE `iot_modbus_job`
    MODIFY COLUMN `command` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '指令' ;

INSERT INTO `sys_menu` VALUES (3374, '回收管理', 3000, 1, 'recovery', 'iot/recovery/index', NULL, 1, 0, 'C', '0', 0, NULL, 'job', 'admin', '2025-05-14 15:25:14', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3375, '设备列表', 3374, 1, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:recovery:device', '#', 'admin', '2025-05-15 16:05:41', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3376, '产品列表', 3374, 2, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:recovery:product', '#', 'admin', '2025-05-15 16:06:09', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3377, '还原', 3374, 3, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:recovery:restore', '#', 'admin', '2025-05-15 16:08:13', 'admin', '2025-05-15 16:13:05', '');
INSERT INTO `sys_menu` VALUES (3378, '删除', 3374, 4, '', NULL, NULL, 1, 0, 'F', '0', 0, 'iot:recovery:delete', '#', 'admin', '2025-05-15 16:08:53', 'admin', '2025-05-15 16:13:17', '');

INSERT INTO `sys_menu_translate` VALUES (3374, '回收管理', 'Recycling management');
INSERT INTO `sys_menu_translate` VALUES (3375, '设备列表', 'Device deleted list');
INSERT INTO `sys_menu_translate` VALUES (3376, '产品列表', 'Product deleted list');
INSERT INTO `sys_menu_translate` VALUES (3377, '还原', 'Restore');
INSERT INTO `sys_menu_translate` VALUES (3378, '删除', 'Delete');

UPDATE iot_things_model SET model_order = model_id;

ALTER TABLE `iot_product_modbus_job`
    ADD COLUMN `command_type` tinyint(2) NULL COMMENT '1轮询指令 2下发指令';

ALTER TABLE `iot_modbus_job`
    ADD COLUMN `command_type` tinyint(2) NULL COMMENT '1轮询指令 2下发指令';

update iot_product_modbus_job
set command_type = 1;

update iot_modbus_job
set command_type = 1;

DROP TABLE IF EXISTS `rule_el`;
CREATE TABLE `rule_el` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
   `el_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'EL表达式ID',
   `el_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'EL表达式名称',
   `el` text COLLATE utf8mb4_general_ci COMMENT 'EL表达式',
   `flow_json` text COLLATE utf8mb4_general_ci COMMENT '流程数据',
   `source_json` text COLLATE utf8mb4_general_ci COMMENT '原始数据',
   `executor_id` bigint DEFAULT NULL COMMENT 'Executor执行器ID',
   `scene_id` bigint(20) NULL COMMENT '场景ID',
   `enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否生效（0-不生效，1-生效）',
   `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
   `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT = 1 DEFAULT CHARSET  =utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '规则el表' ROW_FORMAT=DYNAMIC;

INSERT INTO `sys_menu` VALUES (3379, '规则列表', 3051, 6, 'list', 'ruleengine/list/index', '', 1, 0, 'C', '0', 0, 'rule:el:list', 'ruleengine', 'admin', '2025-07-23 11:55:09', 'admin', '2025-07-23 11:56:46', '');
INSERT INTO `sys_menu` VALUES (3380, '规则列表查询', 3379, 1, '', NULL, '', 1, 0, 'F', '0', 0, 'rule:el:query', '#', 'admin', '2025-07-23 11:58:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3381, '规则列表新增', 3379, 2, '', NULL, '', 1, 0, 'F', '0', 0, 'rule:el:add', '#', 'admin', '2025-07-23 11:58:40', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3382, '规则列表修改', 3379, 3, '', NULL, '', 1, 0, 'F', '0', 0, 'rule:el:edit', '#', 'admin', '2025-07-23 11:59:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3383, '规则列表删除', 3379, 4, '', NULL, '', 1, 0, 'F', '0', 0, 'rule:el:remove', '#', 'admin', '2025-07-23 11:59:25', '', NULL, '');

INSERT INTO sys_menu_translate VALUES (3379, '规则列表', 'Rule list');
INSERT INTO sys_menu_translate VALUES (3380, '规则列表查询', 'Rule list query');
INSERT INTO sys_menu_translate VALUES (3381, '规则列表新增', 'Rule list add');
INSERT INTO sys_menu_translate VALUES (3382, '规则列表编辑', 'Rule list edit');
INSERT INTO sys_menu_translate VALUES (3383, '规则列表删除', 'Rule list delete');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (742, 9, '16进制', 'hex', 'iot_modbus_data_type', NULL, 'default', 'N', 0, 'admin', '2025-06-11 17:33:52', '', NULL, NULL);

INSERT INTO `sys_dict_data_translate` (`id`, `zh_cn`, `en_us`) VALUES (742, '16进制', 'iot_modbus_data_type');


INSERT INTO `sys_menu` VALUES (3384, '大屏参数配置', 3049, 4, 'bashBoard', 'iot/view/setting', NULL, 1, 0, 'C', '0', 0, NULL, '#', 'admin', '2025-06-12 15:20:13', '', NULL, '');
INSERT INTO `sys_menu_translate` VALUES (3384, '大屏参数配置', 'BashBoard param config');

ALTER TABLE `sip_device`
    CHANGE COLUMN `online` `online_status` varchar(64) NOT NULL DEFAULT '' COMMENT '在线状态';

ALTER TABLE sip_device_channel ADD UNIQUE INDEX idx_channel (device_sip_id, channel_sip_id, del_flag);
