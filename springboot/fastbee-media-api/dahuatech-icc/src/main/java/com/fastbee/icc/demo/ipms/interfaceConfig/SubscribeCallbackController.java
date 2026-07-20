package com.fastbee.icc.demo.ipms.interfaceConfig;

import java.net.URLDecoder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.dahuatech.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.icc.util.IPMSUtils;

/**
 * @className InterfaceConfigDemo
 * @Author 355079
 * @Date 2024/12/12
 * @Description 数据推送接收接口类
 */
@Slf4j
@RestController
public class SubscribeCallbackController {

    /**
     * 接收加密消息并进行解密处理
     *
     * @param encryptStr 加密消息
     * @param request    请求对象
     * @param response   响应对象
     * @return 处理结果
     * @throws Exception 异常
     */
    @PostMapping("/receiveEncryptMsg")
    public JSONObject receiveEncryptMsg(@RequestBody String encryptStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        encryptStr = URLDecoder.decode(encryptStr, "utf-8");
        String userAgent = request.getHeader("User-Agent");
        /*
        解密
         */
        String decryptStr = IPMSUtils.decrypt(userAgent,encryptStr);
        System.out.println(decryptStr);
        //返回成功
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resCode", "000000");
        jsonObject.put("errMsg", "success");
        jsonObject.put("success", true);
        jsonObject.put("data", new JSONObject());
        return jsonObject;

    }

}
