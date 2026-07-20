package com.fastbee.base.core.hanler;

import java.lang.reflect.Method;

import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.protocol.Message;

/**
 * 同步处理报文
 * @author bill
 */
public class SyncHandler extends BaseHandler{

    public SyncHandler(Object target, Method targetMethod, String desc,boolean async) {
        super(target, targetMethod, desc, async);
    }

    @Override
    public <T extends Message> T invoke(T request, Session session) throws Exception {
        return super.invoke(request, session);
    }
}
