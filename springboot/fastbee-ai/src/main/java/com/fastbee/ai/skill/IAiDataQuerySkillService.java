package com.fastbee.ai.skill;

import java.util.List;

import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;

/**
 * AI 问数技能服务接口。
 */
public interface IAiDataQuerySkillService {

    /**
     * 统计告警处理情况。
     *
     * @param dataCenterParam 查询参数
     * @return 统计结果
     */
    List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam);

    /**
     * 统计告警级别分布。
     *
     * @param dataCenterParam 查询参数
     * @return 统计结果
     */
    List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam);

    /**
     * 统计设备物模型指令下发数量。
     *
     * @param dataCenterParam 查询参数
     * @return 统计结果
     */
    List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam);
}
