//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception.job;

public class TaskException extends Exception {
    private static final long U = 1L;
    private Code V;

    public TaskException(String msg, Code code) {
        this(msg, code, (Exception)null);
    }

    public TaskException(String msg, Code code, Exception nestedEx) {
        super(msg, nestedEx);
        this.V = code;
    }

    public Code getCode() {
        return this.V;
    }

    public static enum Code {
        TASK_EXISTS,
        NO_TASK_EXISTS,
        TASK_ALREADY_STARTED,
        UNKNOWN,
        CONFIG_ERROR,
        TASK_NODE_NOT_AVAILABLE;
    }
}
