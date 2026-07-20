-- ----------------------------
-- 组态sysFlag字段升级脚本
-- 从v2.8.0升级到v2.x.x
-- ----------------------------

-- scada_echart 表添加 sys_flag 字段
ALTER TABLE `scada_echart` ADD COLUMN `sys_flag` int(1) DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）' AFTER `del_flag`;
UPDATE `scada_echart` SET `sys_flag` = 1 WHERE `tenant_id` = 1;

-- scada_component 表添加 sys_flag 字段，去除 is_share 字段
ALTER TABLE `scada_component` ADD COLUMN `sys_flag` int(1) DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）' AFTER `del_flag`;
UPDATE `scada_component` SET `sys_flag` = 1 WHERE `tenant_id` = 1;
ALTER TABLE `scada_component` DROP COLUMN `is_share`;

-- scada_model 表添加 sys_flag 字段
ALTER TABLE `scada_model` ADD COLUMN `sys_flag` int(1) DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）' AFTER `del_flag`;
UPDATE `scada_model` SET `sys_flag` = 1 WHERE `tenant_id` = 1;
