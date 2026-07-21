//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.handler;

import org.redisson.api.NameMapper;

import com.fastbee.common.utils.StringUtils;

public class KeyPrefixHandler implements NameMapper {
    private final String keyPrefix;

    public KeyPrefixHandler(String keyPrefix) {
        this.keyPrefix = StringUtils.isBlank(keyPrefix) ? "" : keyPrefix + ":";
    }

    public String map(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        } else {
            return StringUtils.isNotBlank(this.keyPrefix) && !name.startsWith(this.keyPrefix) ? this.keyPrefix + name : name;
        }
    }

    public String unmap(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        } else {
            return StringUtils.isNotBlank(this.keyPrefix) && name.startsWith(this.keyPrefix) ? name.substring(this.keyPrefix.length()) : name;
        }
    }
}
