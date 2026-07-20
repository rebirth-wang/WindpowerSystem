package com.fastbee.iot.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

import com.fastbee.common.utils.StringUtils;

/**
 * 物模型通用布尔状态枚举
 * 对应所有是/否类型字段：0-否，1-是
 */
@Getter
public enum ThingsModelBoolEnum {
    YES(1, "是", Arrays.asList("是", "1", "Y", "y", "YES", "yes")),
    NO(0, "否", Arrays.asList("否", "0", "N", "n", "NO", "no", "可写")), // 可写映射为否（对应isReadonly）
    READONLY(1, "是", Arrays.asList("只读"));

    /** 数据库存储的数字编码 */
    private final Integer code;
    /** 导出显示的标准文字 */
    private final String text;
    /** 导入支持的文字别名（匹配任意一个即可） */
    private final List<String> aliases;

    ThingsModelBoolEnum(Integer code, String text, List<String> aliases) {
        this.code = code;
        this.text = text;
        this.aliases = aliases;
    }

    /**
     * 根据导入的文字匹配编码（忽略大小写，支持别名）
     * @param text 模板中的文字/数字文字
     * @return 对应的数字编码
     * @throws IllegalArgumentException 匹配不到时抛出异常
     */
    public static Integer getCodeByText(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String matchText = text.trim();
        return Arrays.stream(values())
                .filter(enumObj -> enumObj.getAliases().contains(matchText))
                .findFirst()
                .map(ThingsModelBoolEnum::getCode)
                .orElseThrow(() -> new IllegalArgumentException("状态值不合法：" + text + "，支持值：是/否/1/0"));
    }

    /**
     * 根据编码匹配标准文字（用于导出）
     * @param code 数据库数字编码
     * @return 导出显示的文字（是/否）
     */
    public static String getTextByCode(Integer code) {
        if (code == null) {
            return "";
        }
        return Arrays.stream(values())
                .filter(enumObj -> enumObj.getCode().equals(code))
                .findFirst()
                .map(ThingsModelBoolEnum::getText)
                .orElse("");
    }
}
