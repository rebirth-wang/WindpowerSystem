package com.fastbee.http.auth;

import static com.fastbee.http.utils.DigestAuthUtil.toHexString;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import jakarta.annotation.Resource;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.config.HttpAuthConfig;
import com.fastbee.http.utils.DigestAuthUtil;
import com.fastbee.iot.model.AuthenticateInputModel;
import com.fastbee.iot.model.ProductAuthenticateModel;
import com.fastbee.iot.service.IDeviceService;

@Slf4j
@Component
public class DigestAuth {

    private static final String REALM = "http-auth@fastbee.com";
    @Resource
    private HttpAuthConfig authConfig;
    @Resource
    @Lazy
    private IDeviceService deviceService;

    public boolean auth(ChannelHandlerContext ctx, FullHttpRequest request) {
        boolean ret = false;
        try {
            // 提取账号密码
            DigestAuthUtil util = new DigestAuthUtil();
            String clientid = util.getClientId(request);
            if (Objects.equals(clientid, "")) {
                log.error("clientId is empty,");
                return false;
            }
            String[] clientArray = clientid.split("-");
            if (clientArray.length != 4 || clientArray[0].isEmpty() || clientArray[1].isEmpty() || clientArray[2].isEmpty() || clientArray[3].isEmpty()) {
                log.warn("设备客户端Id格式为：认证类型 - 设备编号 - 产品ID - 用户ID");
                return false;
            }

            ProductAuthenticateModel model = deviceService.selectProductAuthenticate(new AuthenticateInputModel(clientArray[1], Long.valueOf(clientArray[2])));
            if (util.doAuthenticatePlainTextPassword(request, model.getAuthPassword())) {
                ret = true;
            } else {
                // 发送401 Unauthorized响应
                sendUnauthorizedResponse(ctx);
            }
        } catch (Exception e) {
            log.error("DigestAuth.auth error", e);
        }
        return ret;
    }

    public void sendUnauthorizedResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED,
                Unpooled.copiedBuffer("Unauthorized\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.WWW_AUTHENTICATE, "Digest realm=\"" + REALM + "\", nonce=\"" + this.generateNonce()
                + "\", opaque=\"\", stale=\"FALSE\", qop=\"auth\", algorithm=\"MD5\"");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String generateNonce() {
        // Get the time of day and run MD5 over it.
        Date date = new Date();
        long time = date.getTime();
        Random rand = new Random();
        long pad = rand.nextLong();
        String nonceString = (new Long(time))
                + (new Long(pad)).toString();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] mdbytes = messageDigest.digest(nonceString.getBytes());
            return toHexString(mdbytes);
        } catch (Exception e) {
            log.error("DigestAuth.auth error", e);
        }
        return "";
    }
}
