//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.page;

import com.fastbee.common.core.text.Convert;
import com.fastbee.common.utils.ServletUtils;

public class TableSupport {
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY_COLUMN = "orderByColumn";
    public static final String IS_ASC = "isAsc";
    public static final String REASONABLE = "reasonable";

    public static PageDomain getPageDomain() {
        PageDomain var0 = new PageDomain();
        var0.setPageNum(Convert.toInt(ServletUtils.getParameter("pageNum"), 1));
        var0.setPageSize(Convert.toInt(ServletUtils.getParameter("pageSize"), 10));
        var0.setOrderByColumn(ServletUtils.getParameter("orderByColumn"));
        var0.setIsAsc(ServletUtils.getParameter("isAsc"));
        var0.setReasonable(ServletUtils.getParameterToBool("reasonable"));
        return var0;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
