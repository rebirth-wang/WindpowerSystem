package com.fastbee.coap.handler;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.fastbee.coap.model.*;
import com.fastbee.coap.model.options.ContentFormat;
import com.fastbee.coap.service.ICoapSessionService;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.service.IToolService;

@Slf4j
public class ConnectionHandler extends AbstractResourceHandler {
    private final String path;

    private static IToolService toolService = SpringUtils.getBean(IToolService.class);

    private static ICoapSessionService coapSessionService = SpringUtils.getBean(ICoapSessionService.class);

    public ConnectionHandler(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public CoapResponse handle(CoapMessage request) {
        if (request.getMessageCode() == MessageCode.POST) {
            CoapResponse coapResponse = new CoapResponse(request.getMessageType(),
                    MessageCode.CONTENT_205);

            String query = ((CoapRequest) request).getUriQuery();
            Map<String, String> queryParams = this.parseQueryParams(query);
            // 现在你可以使用 queryParams 这个 Map
            log.info("Query parameters: {}", queryParams);
            try {
                String clientid = queryParams.get("clientid");
                Boolean ret = toolService.deviceAuth(clientid, queryParams.get("username"), queryParams.get("password"));
                if (ret) {
                    // 认证成功 创建会话
                    String token = toolService.getStringRandom(15);
                    CoapSession session = new CoapSession(clientid, queryParams.get("username"), token);
                    String[] clientArray = clientid.split("-");
                    if (clientArray.length != 4 || clientArray[0].isEmpty() || clientArray[1].isEmpty() || clientArray[2].isEmpty() || clientArray[3].isEmpty()) {
                        log.warn("设备客户端Id格式为：认证类型 - 设备编号 - 产品ID - 用户ID");
                        coapResponse = new CoapResponse(request.getMessageType(),
                                MessageCode.UNAUTHORIZED_401);
                        coapResponse.setContent("客户端Id格式格式不正确".getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
                    } else {
                        session.setProductId(clientArray[2]);
                        session.setSerialNumber(clientArray[1]);
                        coapSessionService.storeSession(token, session);
                        coapResponse.setContent(token.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
                    }
                } else {
                    coapResponse = new CoapResponse(request.getMessageType(),
                            MessageCode.UNAUTHORIZED_401);
                    coapResponse.setContent("认证失败".getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
                }
            } catch (Exception e) {
                log.error("设备认证失败", e);
            }
            coapResponse.setSender(request.getSender());
            coapResponse.setMessageID(request.getMessageID());
            coapResponse.setToken(request.getToken());
            return coapResponse;
        } else if (request.getMessageCode() == MessageCode.DELETE) {
            String query = ((CoapRequest) request).getUriQuery();
            Map<String, String> queryParams = this.parseQueryParams(query);
            coapSessionService.cleanSession(queryParams.get("token"));
            //
            CoapResponse coapResponse = new CoapResponse(request.getMessageType(),
                    MessageCode.CONTENT_205);
            coapResponse.setSender(request.getSender());
            coapResponse.setMessageID(request.getMessageID());
            coapResponse.setToken(request.getToken());
            return coapResponse;
        } else {
            CoapResponse coapResponse = new CoapResponse(request.getMessageType(),
                    MessageCode.METHOD_NOT_ALLOWED_405);
            String message = "Service does not allow " + request.getMessageCodeName() + " requests.";
            coapResponse.setContent(message.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
            coapResponse.setSender(request.getSender());
            coapResponse.setMessageID(request.getMessageID());
            coapResponse.setToken(request.getToken());
            return coapResponse;

        }
    }
}
