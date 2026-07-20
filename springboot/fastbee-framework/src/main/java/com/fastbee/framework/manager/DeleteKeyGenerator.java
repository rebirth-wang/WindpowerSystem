//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class DeleteKeyGenerator implements KeyGenerator {
    public Object generate(Object target, Method method, Object... params) {
        if (params.length > 0) {
            Object ids = params[0];
            Collection<?> collection = Collections.singletonList(ids);
            if (collection.stream().allMatch((item) -> item instanceof Long)) {
                String[] keys = (String[])collection.stream().map(Object::toString).toArray((x$0) -> new String[x$0]);
                List<Object> keysList = new ArrayList(keys.length);
                keysList.addAll(Arrays.asList(keys));
                return keysList;
            }
        }

        return new String[0];
    }
}
