
DROP TABLE IF EXISTS `rule_cmp`;
CREATE TABLE `rule_cmp` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
   `component_id` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `component_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `script` text COLLATE utf8mb4_general_ci,
   `language` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `clazz` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `el` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_pre` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_finally_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_tag` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_max_wait_seconds` int DEFAULT NULL,
   `el_format` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_default_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_to` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_true_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_false_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_parallel` tinyint(1) DEFAULT NULL,
   `cmp_do_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_break_opt` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `cmp_data` text COLLATE utf8mb4_general_ci,
   `cmp_data_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
   `fallback_id` bigint DEFAULT NULL,
   `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '规则组件表' ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `rule_executor`;
CREATE TABLE `rule_executor` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
    `executor_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器ID',
    `executor_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器名称',
    `ivy_config_id` bigint DEFAULT NULL COMMENT '执行器配置IvyConfig',
    `executor_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器类型【execute2Resp:execute2Resp,execute2Future:execute2Future】',
    `context_beans` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上下文bean',
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '规则执行器表' ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `rule_chain`;
CREATE TABLE `rule_chain` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
   `application_name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
   `chain_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '链路ID',
   `chain_name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
   `el_data` varchar(1024) COLLATE utf8mb4_general_ci NOT NULL,
   `enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否生效（0-不生效，1-生效）',
   `context_beans` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上下文bean',
   `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
   `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '规则链表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ivy_lf_el_table
-- ----------------------------

-- ----------------------------
-- Table structure for table ivy_lf_script_node_table
-- ----------------------------
DROP TABLE IF EXISTS `rule_script_node`;
CREATE TABLE `rule_script_node` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
    `application_name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
    `script_node_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
    `script_node_name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
    `script_node_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
    `script_node_data` varchar(1024) COLLATE utf8mb4_general_ci NOT NULL,
    `script_language` varchar(1024) COLLATE utf8mb4_general_ci NOT NULL,
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT = '规则脚本节点表' ROW_FORMAT=DYNAMIC;
