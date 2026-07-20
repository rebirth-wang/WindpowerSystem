DROP TABLE IF EXISTS `onvif_platform`;
CREATE TABLE `onvif_platform`  (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
     `channel_id` bigint(20) NOT NULL COMMENT '通道id',
     `platform_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '平台id',
     `catalog_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '目录id',
     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = 'onvif平台' ROW_FORMAT = DYNAMIC;
