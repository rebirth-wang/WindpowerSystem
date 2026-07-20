package com.fastbee.iot.model;

import java.util.Date;

import com.fastbee.common.utils.DateUtils;

public class HistoryBo {
    private String identify;

    private String value;


    private Date time;

    public String getTime() {
        return DateUtils.dateTimeYY(time);
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identity) {
        this.identify = identity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTimeField() {
        return this.time;
    }

}
