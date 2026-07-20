-- ----------------------------
-- 组态sysFlag字段升级脚本
-- 从v2.8.0升级到v2.x.x
-- ----------------------------

-- scada_echart 表添加 sys_flag 字段
ALTER TABLE [dbo].[scada_echart] ADD [sys_flag] [int] DEFAULT 0;
EXEC sp_addextendedproperty 'MS_Description', '是否系统通用（0-否，1-是）', 'SCHEMA', [dbo], 'TABLE', [scada_echart], 'COLUMN', [sys_flag];
UPDATE [dbo].[scada_echart] SET [sys_flag] = 1 WHERE [tenant_id] = 1;

-- scada_component 表添加 sys_flag 字段，去除 is_share 字段
ALTER TABLE [dbo].[scada_component] ADD [sys_flag] [int] DEFAULT 0;
EXEC sp_addextendedproperty 'MS_Description', '是否系统通用（0-否，1-是）', 'SCHEMA', [dbo], 'TABLE', [scada_component], 'COLUMN', [sys_flag];
UPDATE [dbo].[scada_component] SET [sys_flag] = 1 WHERE [tenant_id] = 1;
ALTER TABLE [dbo].[scada_component] DROP COLUMN [is_share];

-- scada_model 表添加 sys_flag 字段
ALTER TABLE [dbo].[scada_model] ADD [sys_flag] [int] DEFAULT 0;
EXEC sp_addextendedproperty 'MS_Description', '是否系统通用（0-否，1-是）', 'SCHEMA', [dbo], 'TABLE', [scada_model], 'COLUMN', [sys_flag];
UPDATE [dbo].[scada_model] SET [sys_flag] = 1 WHERE [tenant_id] = 1;
