package com.fastbee.icc.util;

import java.util.Collection;

public class CollUtil {

    /**
     * 判断集合是否为空
     *
     * @param collection 需要判断的集合
     * @return 如果集合为空则返回true，否则返回false
     */
    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }
}
