//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.xss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.fastbee.common.utils.StringUtils;

public class XssValidator implements ConstraintValidator<Xss, String> {
    private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />";

    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(value)) {
            return true;
        } else {
            return !containsHtml(value);
        }
    }

    public static boolean containsHtml(String value) {
        Pattern pattern = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
