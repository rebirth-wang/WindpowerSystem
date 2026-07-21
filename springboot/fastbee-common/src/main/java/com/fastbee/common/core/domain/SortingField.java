//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;

public class SortingField implements Serializable {
    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";
    private String field;
    private String order;

    public SortingField() {
    }

    public SortingField(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return this.field;
    }

    public SortingField setField(String field) {
        this.field = field;
        return this;
    }

    public String getOrder() {
        return this.order;
    }

    public SortingField setOrder(String order) {
        this.order = order;
        return this;
    }
}
