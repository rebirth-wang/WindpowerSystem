package com.fastbee.iot.model.dashBoard;

import java.util.List;

import lombok.Data;

/**
 * @author gsb
 * @date 2023/11/14 11:54
 */
@Data
public class DataCard {

    public static String PRODUCT = "product";
    public static String DATA1 = "data1";
    public static String DATA2 = "data2";

    /**
     * 数据项
     */
    private List<String> dimensions;
    /**
     * 数据源
     */
    private List<Object> source;

}
