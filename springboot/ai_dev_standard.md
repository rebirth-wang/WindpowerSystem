1.新建实体类对象必须遵从swagger注释；
2.新建接口、方法时必须按规范注释；
3.数据库实体类有对应的VO类，通过convert目录下有转换类，使用mapstruct技术实现；
4.尽量把业务写到服务层service类里面；
4.编辑代码时，一些引用包记得引入；
5.接口或方法里返回给前端或者报错的结果需要实现国际化，国际化参考springboot/fastbee-admin/src/main/resources/i18n/目录下的中英文翻译文件；
6.新建表结构时，最后必须加上这几个字段：  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
`create_by` varchar(64) DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_by` varchar(64) DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`tenant_id` bigint(20) NOT NULL COMMENT '租户id',
`tenant_name` varchar(30) NOT NULL DEFAULT '' COMMENT '租户名称';
7.修改实体类字段时，对应的VO类、mapper.xml文件里的字段都需要同步修改；
8.写完代码必须检查是否有报错；
9.某个类修改代码后多余引用的包需要删除；