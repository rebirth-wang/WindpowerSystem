//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastbee.common.utils.spring.SpringUtils;

public class MessageUtils {
    public static String message(String code, Object... args) {
        MessageSource var2 = (MessageSource)SpringUtils.getBean(MessageSource.class);
        return var2.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
