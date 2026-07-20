package com.fastbee.rule.parser.entity.line;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fastbee.rule.parser.enums.RuleEnums;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LineData {

    // 路径类型 Enums.PATH_ENUM
    private String type;
    //表达式ID
    private String id;
    //表达式标签
    private String tag;

    public boolean isCommonPath(){
        return RuleEnums.PATH_ENUM.common_path.value().equals(type);
    }

    //是否特殊路径
    public boolean isSpecialPath(){
        return !isCommonPath();
    }

    public boolean isPath(RuleEnums.PATH_ENUM pathEnum){
        return pathEnum.value().equals(type);
    }

}
