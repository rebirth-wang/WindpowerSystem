package com.fastbee.controller.dashBoard;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.iot.model.dashBoard.DashMqttMetrics;
import com.fastbee.iot.model.dashBoard.DashMqttStat;
import com.fastbee.iot.model.dashBoard.DeviceMatchBo;
import com.fastbee.iot.service.IDashBoardService;
import com.fastbee.mqtt.manager.ClientManager;
import com.fastbee.mqtt.manager.RetainMsgManager;

/**
 * 首页面板和大屏数据
 *
 * @author gsb
 * @date 2023/3/27 17:00
 */
@Api(tags = "首页面板和大屏数据")
@RestController
@RequestMapping("/bashBoard")
public class DashBoardController {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private IDashBoardService dashBoardService;

    @GetMapping("/stats")
    @ApiOperation("mqtt状态数据")
    public AjaxResult stats() {
        DashMqttStat stat = DashMqttStat.builder()
                .connection_count(sessionStore.getSessionMap().size())
                .connection_total(getTotal(FastBeeConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .subscription_count(ClientManager.topicMap.size())
                .subscription_total(getTotal(FastBeeConstant.REDIS.MESSAGE_SUBSCRIBE_TOTAL))
                .retain_count(RetainMsgManager.getSize())
                .retain_total(getTotal(FastBeeConstant.REDIS.MESSAGE_RETAIN_TOTAL))
                .session_count(sessionStore.getSessionMap().size())
                .session_total(getTotal(FastBeeConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .build();
        return AjaxResult.success(stat);

    }


    @GetMapping("/metrics")
    @ApiOperation("mqtt统计")
    public AjaxResult metrics() {
        DashMqttMetrics metrics = DashMqttMetrics.builder()
                .send_total(getTotal(FastBeeConstant.REDIS.MESSAGE_SEND_TOTAL))
                .receive_total(getTotal(FastBeeConstant.REDIS.MESSAGE_RECEIVE_TOTAL))
                .auth_total(getTotal(FastBeeConstant.REDIS.MESSAGE_AUTH_TOTAL))
                .connect_total(getTotal(FastBeeConstant.REDIS.MESSAGE_CONNECT_TOTAL))
                .subscribe_total(getTotal(FastBeeConstant.REDIS.MESSAGE_SUBSCRIBE_TOTAL))
                .today_received(getTotal(FastBeeConstant.REDIS.MESSAGE_RECEIVE_TODAY))
                .today_send(getTotal(FastBeeConstant.REDIS.MESSAGE_SEND_TODAY))
                .build();
        return AjaxResult.success(metrics);
    }

    @GetMapping("/matchsData")
    @ApiOperation("大屏匹配数据使用")
    public AjaxResult matchData(DeviceMatchBo bo){
        return dashBoardService.dashCombine(bo);
    }


    public Integer getTotal(String key) {
        return redisCache.getCacheObject(key) == null ? 0 : redisCache.getCacheObject(key);
    }
}
