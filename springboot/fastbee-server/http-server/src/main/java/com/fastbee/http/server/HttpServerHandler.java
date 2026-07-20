package com.fastbee.http.server;

import java.util.Set;

import jakarta.servlet.http.HttpSession;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fastbee.http.auth.BasicAuth;
import com.fastbee.http.auth.DigestAuth;
import com.fastbee.http.manager.HttpSessionManager;

@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private HttpSessionManager sessionManager;

    @Autowired
    private HttpListener httpListener;

    @Autowired
    private BasicAuth basicAuth;

    @Autowired
    private DigestAuth digestAuth;

    @Value("${server.http.auth.type}")
    private String authtype;

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;
        String sessionId = null;
        HttpSession session;
        // 获取cookie头
        String cookieHeader = req.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null) {
            // 使用ServerCookieDecoder解码Cookie字符串
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
            // 遍历Cookies
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.name())) {
                    sessionId = cookie.value();
                }
            }
            // sessionId为空
            if (sessionId != null) {
                log.info("客户端已认证!");
                // 已经认证
                session = sessionManager.getSession(sessionId);
                if(session == null) {
                    log.error("session已经过期!");
                    sendUnauthorizedResponse(ctx);
                }
                log.info("session:{}!", session);
                // http 路由处理函数
                httpListener.processRequest(req, session);
                FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                ctx.writeAndFlush(res);
            } else {
                log.error("sessionId为空!");
                sendUnauthorizedResponse(ctx);
            }
        } else {
            log.info("cookie头为空!");
            // 获取认证头
            String authHeader = req.headers().get(HttpHeaderNames.AUTHORIZATION);
            boolean check = false;
            if (authHeader != null) {
                if (authHeader.startsWith("Basic ")) {
                    if (basicAuth.auth(ctx, authHeader)) {
                        check = true;
                    } else {
                        log.error("客户端认证未通过，请检查账号密码是否正确!");
                    }
                } else if (authHeader.startsWith("Digest ")) {
                    if (digestAuth.auth(ctx, req)) {
                        check = true;
                    } else {
                        log.error("客户端认证未通过，请检查账号密码是否正确!");
                    }
                }
                if (check) {
                    log.info("客户端认证通过，设置Cookie!");
                    sessionId = sessionManager.createSession();
                    session = sessionManager.getSession(sessionId);
                    session.setAttribute("user", "Fastbee");
                    // 创建一个Cookie
                    Cookie sessionCookie = new DefaultCookie("JSESSIONID", sessionId);
                    // 设置一些属性，比如路径和最大年龄
                    sessionCookie.setPath("/");
                    // 30分钟
                    sessionCookie.setMaxAge(Long.MIN_VALUE);
                    // 编码Cookie并添加到响应头中
                    FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    res.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(sessionCookie));
                    ctx.writeAndFlush(res);
                } else {
                    log.info("客户端无认证头，回复401!");
                    sendUnauthorizedResponse(ctx);
                }
            } else {
                log.info("客户端无认证头，回复401!");
                sendUnauthorizedResponse(ctx);
            }
        }
    }

    private void sendUnauthorizedResponse(ChannelHandlerContext ctx){
        if ("Basic".equals(authtype)) {
            basicAuth.sendUnauthorizedResponse(ctx);
        } else {
            digestAuth.sendUnauthorizedResponse(ctx);
        }
    }
}
