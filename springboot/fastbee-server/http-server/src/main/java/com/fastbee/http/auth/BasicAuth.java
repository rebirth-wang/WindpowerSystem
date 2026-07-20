package com.fastbee.http.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.annotation.Resource;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.config.HttpAuthConfig;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.service.IToolService;

@Slf4j
@Component
public class BasicAuth {

    @Resource
    private HttpAuthConfig authConfig;

    private static IToolService toolService = SpringUtils.getBean(IToolService.class);

    private static final String REALM = "http-auth@fastbee.com";

    public boolean auth(ChannelHandlerContext ctx, String authHeader) throws Exception {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String encodedCredentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
            // clientid:username:password
            String[] parts = credentials.split(":", 3);
            if (parts.length == 3 && toolService.deviceAuth(parts[0], parts[1], parts[2])) {
                // 用户名和密码验证通过，继续处理请求
                return true;
            } else {
                // 发送401 Unauthorized响应
                sendUnauthorizedResponse(ctx);
            }
        } else {
            // 发送401 Unauthorized响应
            sendUnauthorizedResponse(ctx);
        }
        return false;
    }

    private boolean validateUser(String username, String password) {
        // 实现用户验证逻辑
        return username.equals(authConfig.getUsername()) && password.equals(authConfig.getPassword());
    }

    public void sendUnauthorizedResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED,
                Unpooled.copiedBuffer("Unauthorized\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
