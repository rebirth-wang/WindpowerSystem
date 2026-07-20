package com.fastbee.iot.domain;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 告警场景对象 iot_alert_scene
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "AlertScene", description = "告警场景 iot_alert_scene")
@Data
@TableName("iot_alert_scene" )
public class AlertScene{
    /** 告警ID */
    @TableId(value = "alert_id", type = IdType.AUTO)
    @ApiModelProperty("告警ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_alert_scene.alert_id=iot_alert.alert_id"
    )
    private Long alertId;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_alert_scene.scene_id=iot_scene.scene_id"
    )
    private Long sceneId;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
