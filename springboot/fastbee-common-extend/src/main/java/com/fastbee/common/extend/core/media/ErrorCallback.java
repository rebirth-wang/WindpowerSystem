package com.fastbee.common.extend.core.media;

public interface ErrorCallback<T> {

    void run(int code, String msg, T data);
}
