package com.fastbee.icc.demo.event.eventSubscribe;

import com.dahuatech.hutool.json.JSONObject;
import com.dahuatech.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.icc.model.event.eventSubcribe.ReceiveMsgVO;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-12 11:24
 * @Description:
 */
@Slf4j
@RestController
public class SubscribeCallBackController {

    /**
     * 接收订阅回调消息
     * @param data 接收消息参数
     * @return 处理结果
     */
    @PostMapping("/receiveMsg")
    public JSONObject receiveMsg(@RequestBody JSONObject data){
        //打印原始返回数据
        log.info("SubscribeCallBackController,receiveMsg,data:{}", data);
        //TODO: 对收到的消息进行处理
        //对数据进行类型转换, 注意这里需要根据实际返回数据进行类型转换
        ReceiveMsgVO receiveMsgVO = JSONUtil.toBean(data, ReceiveMsgVO.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","0");
        jsonObject.put("message","成功");
        return jsonObject;
    }

}
