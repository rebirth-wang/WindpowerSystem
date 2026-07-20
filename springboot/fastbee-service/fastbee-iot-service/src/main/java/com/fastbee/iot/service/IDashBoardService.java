package com.fastbee.iot.service;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.model.dashBoard.DeviceMatchBo;

/**
 * 大屏接口
 * @author bill
 */
public interface IDashBoardService {


    public AjaxResult dashCombine(DeviceMatchBo bo);
}
