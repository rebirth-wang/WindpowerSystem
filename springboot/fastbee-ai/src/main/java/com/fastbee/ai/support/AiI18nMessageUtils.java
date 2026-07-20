package com.fastbee.ai.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.i18n.LocaleContextHolder;

import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 模块国际化消息辅助工具。
 */
public final class AiI18nMessageUtils {

    private static final String BUNDLE_BASE_NAME = "i18n/messages";

    private static final Properties EMPTY_PROPERTIES = new Properties();

    private static final ConcurrentMap<String, Properties> LOCAL_MESSAGE_CACHE = new ConcurrentHashMap<>();

    private AiI18nMessageUtils() {
    }

    /**
     * 获取国际化消息，并兼容项目中已有的 {@code {}} 占位符格式。
     *
     * @param code 消息编码
     * @param args 占位参数
     * @return 国际化消息
     */
    public static String message(String code, Object... args) {
        String message = resolveMessage(code);
        if (args == null || args.length == 0) {
            return message;
        }
        return StringUtils.format(message, args);
    }

    private static String resolveMessage(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        try {
            return MessageUtils.message(code);
        } catch (Exception ex) {
            return resolveFallbackMessage(code);
        }
    }

    private static String resolveFallbackMessage(String code) {
        for (Locale locale : fallbackLocales()) {
            String message = resolveFromResourceBundle(code, locale);
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
            message = resolveFromLocalProperties(code, locale);
            if (StringUtils.isNotBlank(message)) {
                return message;
            }
        }
        return code;
    }

    private static Set<Locale> fallbackLocales() {
        Set<Locale> locales = new LinkedHashSet<>();
        locales.add(Locale.SIMPLIFIED_CHINESE);
        locales.add(Locale.ROOT);
        Locale currentLocale = LocaleContextHolder.getLocale();
        if (currentLocale != null) {
            locales.add(currentLocale);
        }
        locales.add(Locale.US);
        return locales;
    }

    private static String resolveFromResourceBundle(String code, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    BUNDLE_BASE_NAME,
                    locale == null ? Locale.ROOT : locale,
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
            );
            return bundle.containsKey(code) ? bundle.getString(code) : null;
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    private static String resolveFromLocalProperties(String code, Locale locale) {
        Properties properties = loadLocalMessageProperties(fileName(locale));
        return properties.containsKey(code) ? properties.getProperty(code) : null;
    }

    private static String fileName(Locale locale) {
        if (locale == null || Locale.ROOT.equals(locale)) {
            return "messages.properties";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if ("zh".equalsIgnoreCase(language)) {
            return "messages_zh_CN.properties";
        }
        if ("en".equalsIgnoreCase(language)) {
            return "messages_en_US.properties";
        }
        return StringUtils.isBlank(country)
                ? "messages_" + language + ".properties"
                : "messages_" + language + "_" + country + ".properties";
    }

    private static Properties loadLocalMessageProperties(String fileName) {
        return LOCAL_MESSAGE_CACHE.computeIfAbsent(fileName, AiI18nMessageUtils::loadLocalMessageProperties0);
    }

    private static Properties loadLocalMessageProperties0(String fileName) {
        for (Path path : candidateMessagePaths(fileName)) {
            if (!Files.isRegularFile(path)) {
                continue;
            }
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
                return properties;
            } catch (IOException ignored) {
                return EMPTY_PROPERTIES;
            }
        }
        return EMPTY_PROPERTIES;
    }

    private static Set<Path> candidateMessagePaths(String fileName) {
        Set<Path> paths = new LinkedHashSet<>();
        Path current = Paths.get("").toAbsolutePath().normalize();
        for (Path base = current; base != null; base = base.getParent()) {
            paths.add(base.resolve("springboot").resolve("fastbee-admin")
                    .resolve("src").resolve("main").resolve("resources").resolve("i18n").resolve(fileName));
            paths.add(base.resolve("fastbee-admin")
                    .resolve("src").resolve("main").resolve("resources").resolve("i18n").resolve(fileName));
            paths.add(base.resolve("src").resolve("main").resolve("resources").resolve("i18n").resolve(fileName));
        }
        return paths;
    }
}
