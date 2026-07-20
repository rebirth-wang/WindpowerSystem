//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.fastbee.common.utils.poi.ExcelHandlerAdapter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {
    int sort() default Integer.MAX_VALUE;

    String name() default "";

    String dateFormat() default "";

    String dictType() default "";

    String readConverterExp() default "";

    String separator() default ",";

    int scale() default -1;

    int roundingMode() default 6;

    double height() default (double)14.0F;

    double width() default (double)16.0F;

    String suffix() default "";

    String defaultValue() default "";

    String prompt() default "";

    String[] combo() default {};

    boolean needMerge() default false;

    boolean isExport() default true;

    String targetAttr() default "";

    boolean isStatistics() default false;

    ColumnType cellType() default Excel.ColumnType.STRING;

    IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT;

    IndexedColors headerColor() default IndexedColors.WHITE;

    IndexedColors backgroundColor() default IndexedColors.WHITE;

    IndexedColors color() default IndexedColors.BLACK;

    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    Class<?> handler() default ExcelHandlerAdapter.class;

    String[] args() default {};

    Type type() default Excel.Type.ALL;

    public static enum Type {
        ALL(0),
        EXPORT(1),
        IMPORT(2);

        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    public static enum ColumnType {
        NUMERIC(0),
        STRING(1),
        IMAGE(2);

        private final int value;

        private ColumnType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}
