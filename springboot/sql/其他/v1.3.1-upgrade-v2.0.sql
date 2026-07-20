-- -------------------------------2.0版本表更改-----------------------------------------

-- iot_things_model 物模型表更改
ALTER TABLE `wumeismart`.`iot_things_model` 
ADD COLUMN `temp_slave_id` bigint NULL COMMENT '从机id' AFTER `remark`,
ADD COLUMN `formula` varchar(255) NULL COMMENT '计算公式' AFTER `temp_slave_id`,
ADD COLUMN `reverse_formula` varchar(255) NULL COMMENT '控制公式' AFTER `formula`,
ADD COLUMN `reg_addr` int(255) NULL COMMENT '寄存器地址值' AFTER `reverse_formula`,
ADD COLUMN `bit_option` varchar(255) NULL COMMENT '位定义选项' AFTER `reg_addr`,
ADD COLUMN `value_type` varchar(64) NULL COMMENT '解析类型 1.数值 2.选项' AFTER `bit_option`;
MODIFY COLUMN `specs` json NULL COMMENT '数据定义' AFTER `datatype`;

--  iot_things_model_template  物模型模板表更改
ALTER TABLE `wumeismart`.`iot_things_model_template` 
ADD COLUMN `temp_slave_id` bigint NULL COMMENT '从机id' AFTER `remark`,
ADD COLUMN `formula` varchar(255) NULL COMMENT '计算公式' AFTER `temp_slave_id`,
ADD COLUMN `reverse_formula` varchar(255) NULL COMMENT '控制公式' AFTER `formula`,
ADD COLUMN `reg_addr` int(255) NULL COMMENT '寄存器地址值' AFTER `reverse_formula`,
ADD COLUMN `bit_option` varchar(255) NULL COMMENT '位定义选项' AFTER `reg_addr`,
ADD COLUMN `value_type` varchar(64) NULL COMMENT '解析类型 1.数值 2.选项' AFTER `bit_option`;
ADD COLUMN `is_params` int(1) ZEROFILL NULL COMMENT '是否是计算参数,默认否 0=否，1=是' AFTER `remark`;
MODIFY COLUMN `specs` json NULL COMMENT '数据定义' AFTER `datatype`;


-- iot_device  设备表更改
ALTER TABLE `wumeismart`.`iot_device` 
ADD COLUMN `gw_dev_code` varchar(64) NULL COMMENT '子设备网关编号' AFTER `firmware_version`,

-- iot_product  产品表更改
ALTER TABLE `wumeismart`.`iot_product` 
ADD COLUMN `protocol_code` varchar(64) NULL COMMENT '协议编号' AFTER `category_id`,



-- -------------------------------2.0表新增-----------------------------------------

-- iot_firmware_task   OTA升级任务对象
CREATE TABLE `iot_firmware_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` varchar(60) NOT NULL DEFAULT '' COMMENT '任务名称',
  `firmware_id` bigint(20) unsigned NOT NULL COMMENT '关联固件ID',
  `upgrade_type` int(11) NOT NULL DEFAULT '1' COMMENT '1:指定设备 2:产品级别',
  `task_desc` varchar(255) NOT NULL DEFAULT '',
  `device_amount` int(11) NOT NULL DEFAULT '0' COMMENT '选中的设备总数',
  `del_flag` int(11) NOT NULL DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `book_time` timestamp NULL DEFAULT NULL COMMENT '预定时间升级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='OTA升级任务对象';

-- iot_firmware_task_detail  OTA升级任务详细表
CREATE TABLE `iot_firmware_task_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `serial_number` varchar(64) NOT NULL DEFAULT '' COMMENT '设备编码',
  `upgrade_status` int(11) NOT NULL DEFAULT '0' COMMENT '0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败',
  `detail_msg` varchar(100) NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message_id` varchar(100) DEFAULT '' COMMENT '消息ID',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='OTA升级任务详细表';

-- iot_device_template 设备采集点模板关联表
CREATE TABLE `iot_device_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `product_id` bigint(20) DEFAULT NULL COMMENT '产品id',
  `templateId` bigint(20) DEFAULT NULL COMMENT '采集点模板id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='设备采集点模板关联表';

-- iot_var_temp  设备采集变量模板表
CREATE TABLE `iot_var_temp` (
  `templateId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `templateName` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '模板名称',
  `type` tinyint(20) NOT NULL,
  `pollingMethod` tinyint(20) NOT NULL COMMENT '采集方式 1.云端轮询 2.云端边缘计算',
  `userId` int(11) DEFAULT NULL COMMENT '模板所属用户',
  `slaveTotal` bigint(20) DEFAULT NULL COMMENT '从机总数',
  `pointTotal` bigint(20) DEFAULT NULL COMMENT '总采集点数',
  `share` tinyint(20) DEFAULT NULL COMMENT '是否分享',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `createBy` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateBy` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新用户',
  PRIMARY KEY (`templateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='设备采集变量模板表';

-- iot_var_temp_salve  变量模板设备从机对象表
CREATE TABLE `iot_var_temp_salve` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceTempId` bigint(20) NOT NULL COMMENT '关联的模板id',
  `slaveAddr` int(20) NOT NULL COMMENT '从机编号',
  `slaveIndex` int(20) NOT NULL,
  `slaveIp` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '从机ip地址',
  `slaveName` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '从机名称',
  `slavePort` int(20) NOT NULL COMMENT '从机端口',
  `addrStart` bigint(20) NOT NULL COMMENT '寄存器起始地址(10进制)',
  `addrEnd` bigint(20) NOT NULL COMMENT '寄存器结束地址(10进制)',
  `packetLength` int(20) NOT NULL COMMENT '寄存器批量读取个数',
  `timer` bigint(20) NOT NULL COMMENT '批量获取轮询时间(默认5分钟)',
  `status` tinyint(20) NOT NULL COMMENT '状态 0-启动 1-失效',
  `code` int(20) DEFAULT NULL COMMENT '功能编码',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `createBy` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `updateBy` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新用户',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='变量模板设备从机对象表';

-- iot_protocol 设备通讯协议表
CREATE TABLE `iot_protocol` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `protocol_code` varchar(60) NOT NULL DEFAULT '' COMMENT '协议编码',
  `protocol_name` varchar(60) NOT NULL DEFAULT '' COMMENT '协议名称',
  `protocol_file_url` varchar(500) NOT NULL DEFAULT '' COMMENT '协议jar包,js包，c程序上传地址',
  `protocol_type` int(11) NOT NULL DEFAULT '0' COMMENT '协议类型 0:未知 1:jar，2.js,3.c',
  `jar_sign` varchar(100) NOT NULL DEFAULT '' COMMENT '协议文件摘要(文件的md5)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `protocol_status` int(11) NOT NULL DEFAULT '0' COMMENT '0:草稿 1:启用 2:停用',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '0:正常 1:删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UNIQUE_CODE` (`protocol_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备通讯协议表';


-- ----------------------------
-- iot_function_log 设备服务下发日志
-- ----------------------------
DROP TABLE IF EXISTS `iot_function_log`;
CREATE TABLE `iot_function_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `identify` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '标识符',
  `fun_type` int(2) NOT NULL COMMENT '1==服务下发，2=属性获取，3.OTA升级',
  `fun_value` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '日志值',
  `message_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '消息id',
  `device_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '设备名称',
  `serial_number` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '设备编号',
  `mode` int(2) DEFAULT NULL COMMENT '模式(1=影子模式，2=在线模式，3=其他)',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `result_msg` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '下发结果描述',
  `result_code` int(3) DEFAULT NULL COMMENT '下发结果代码',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `iot_function_log_id_uindex` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='设备服务下发日志';


-- ----------------------------
-- 系统相关升级sql
-- ----------------------------
-- 二级菜单
insert into sys_menu values('124',  '缓存列表', '2',   '6', 'cacheList',  'monitor/cache/list',       '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis-list',    'admin', sysdate(), '', null, '缓存列表菜单');
-- 三级菜单
insert into sys_menu values('1065', '账户解锁', '501', '4', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 'admin', sysdate(), '', null, '');
-- 角色和菜单关联，给管理员添加缓存列表菜单权限
insert into sys_role_menu values ('5', '124');
-- 字典数据表
insert into sys_dict_data values(230, 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '其他操作');
-- 13、参数配置表
insert into sys_config values(6, '账号自助-验证码开关',           'sys.account.captchaEnabled',    'true',          'Y', 'admin', sysdate(), '', null, '是否开启验证码功能（true开启，false关闭）');