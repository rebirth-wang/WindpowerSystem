package com.fastbee.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author fastbee
 * @date 2025-09-08 19:46
 * @description:
 */

public class Arith {
    private static final int al = 10;

    private Arith() {
    }

    public static double add(double v1, double v2) {
        BigDecimal var4 = new BigDecimal(Double.toString(v1));
        BigDecimal var5 = new BigDecimal(Double.toString(v2));
        return var4.add(var5).doubleValue();
    }

    public static double sub(double v1, double v2) {
        BigDecimal var4 = new BigDecimal(Double.toString(v1));
        BigDecimal var5 = new BigDecimal(Double.toString(v2));
        return var4.subtract(var5).doubleValue();
    }

    public static double mul(double v1, double v2) {
        BigDecimal var4 = new BigDecimal(Double.toString(v1));
        BigDecimal var5 = new BigDecimal(Double.toString(v2));
        return var4.multiply(var5).doubleValue();
    }

    public static double div(double v1, double v2) {
        return div(v1, v2, 10);
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal var5 = new BigDecimal(Double.toString(v1));
            BigDecimal var6 = new BigDecimal(Double.toString(v2));
            return var5.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.doubleValue() : var5.divide(var6, scale, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal var3 = new BigDecimal(Double.toString(v));
            BigDecimal var4 = BigDecimal.ONE;
            return var3.divide(var4, scale, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
