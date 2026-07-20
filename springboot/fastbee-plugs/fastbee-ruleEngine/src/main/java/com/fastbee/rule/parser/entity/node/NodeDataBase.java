package com.fastbee.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataBase {
    //表达式id
    private String id;
    //组件标签
    private String tag;
    //组件参数名称
    private String dataName;
    //组件参数值
    private String data;
    //最大等待时间
    private Integer maxWaitSeconds;
    //重试次数
    private Integer retryCount;
    //指定异常的重试
    private String[] retryExceptions;
    //脚本语言
    private String language;
    //脚本代码\动态类代码
    private String code;
    //类路径
    private String clazz;
    //构建节点【0：普通类，1：BEAN，2：动态类，3：脚本】
    private Integer classType;

}
