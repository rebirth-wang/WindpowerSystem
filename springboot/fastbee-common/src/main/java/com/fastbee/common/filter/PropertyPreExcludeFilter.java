//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

public class PropertyPreExcludeFilter extends SimplePropertyPreFilter {
    public PropertyPreExcludeFilter() {
        super(new String[0]);
    }

    public PropertyPreExcludeFilter addExcludes(String... filters) {
        for(int var2 = 0; var2 < filters.length; ++var2) {
            this.getExcludes().add(filters[var2]);
        }

        return this;
    }
}
