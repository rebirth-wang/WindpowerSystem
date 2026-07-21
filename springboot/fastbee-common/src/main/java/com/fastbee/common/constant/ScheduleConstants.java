//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.constant;

public class ScheduleConstants {
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";
    public static final String MISFIRE_DEFAULT = "0";
    public static final String MISFIRE_IGNORE_MISFIRES = "1";
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";
    public static final String MISFIRE_DO_NOTHING = "3";

    public static enum Status {
        NORMAL(0),
        PAUSE(1);

        private Integer value;

        private Status(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return this.value;
        }
    }
}
