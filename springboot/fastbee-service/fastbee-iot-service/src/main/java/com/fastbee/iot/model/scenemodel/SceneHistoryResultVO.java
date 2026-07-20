package com.fastbee.iot.model.scenemodel;

import java.util.List;

import lombok.Data;

import com.fastbee.iot.model.HistoryModel;

/**
 * @author zzy
 * @description: TODO
 * @date 2025-07-18 11:33
 */
@Data
public class SceneHistoryResultVO {

    private List<HistoryModel> historyModelList;

    private List<String> identifierList;
}
