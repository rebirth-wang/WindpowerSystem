package com.fastbee.rule.context;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.extend.core.domin.thingsModel.SceneThingsModelItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

@Data
@EqualsAndHashCode(callSuper = true)
public class RuleContext extends BaseContext {
    /**
     * 上报信息的设备信息类型 1=属性， 2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线
     */
    private int type;

    private Long sceneId;

    /**
     * 上报的物模型集合
     */
    private List<ThingsModelSimpleItem> thingsModelSimpleItems;

    /**
     * 触发成功的物模型集合,保留给告警记录
     */
    private List<SceneThingsModelItem> sceneThingsModelItems;
}
