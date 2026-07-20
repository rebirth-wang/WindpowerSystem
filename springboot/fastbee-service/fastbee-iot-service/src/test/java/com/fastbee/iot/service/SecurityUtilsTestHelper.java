package com.fastbee.iot.service;

import java.util.Locale;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.redis.RedisCache;

/**
 * Ensures SecurityUtils static initialization can run in plain Mockito tests.
 */
public final class SecurityUtilsTestHelper {

    private SecurityUtilsTestHelper() {
    }

    public static void prepareSecurityUtils() {
        ConfigurableListableBeanFactory beanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);
        RedisCache redisCache = Mockito.mock(RedisCache.class);
        MessageSource messageSource = mockMessageSource();
        Mockito.lenient().when(beanFactory.getBean(RedisCache.class)).thenReturn(redisCache);
        Mockito.lenient().when(beanFactory.getBean(MessageSource.class)).thenReturn(messageSource);
        Mockito.lenient().when(beanFactory.getBean(Mockito.any(Class.class)))
                .thenAnswer(defaultBeanAnswer(redisCache, messageSource));
        ReflectionTestUtils.setField(getSpringUtilsClass(), "beanFactory", beanFactory);
    }

    private static MessageSource mockMessageSource() {
        MessageSource messageSource = Mockito.mock(MessageSource.class);
        Mockito.lenient().when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any(Locale.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.lenient().when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.any(Locale.class)))
                .thenAnswer(invocation -> invocation.getArgument(2));
        return messageSource;
    }

    private static Answer<Object> defaultBeanAnswer(RedisCache redisCache, MessageSource messageSource) {
        return invocation -> {
            Class<?> type = invocation.getArgument(0);
            if (RedisCache.class.equals(type)) {
                return redisCache;
            }
            if (MessageSource.class.equals(type)) {
                return messageSource;
            }
            return Mockito.mock(type);
        };
    }

    private static Class<?> getSpringUtilsClass() {
        try {
            return Class.forName("com.fastbee.common.utils.spring.SpringUtils");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find SpringUtils on test classpath", e);
        }
    }
}
