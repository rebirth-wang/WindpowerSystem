package com.fastbee.rule.config;

import java.util.concurrent.*;

import com.yomahub.liteflow.thread.ExecutorBuilder;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class MainExecutorBuilder implements ExecutorBuilder {
    private ThreadFactory springThreadFactory = new CustomizableThreadFactory("liteflow-main-");
    @Override
    public ExecutorService buildExecutor() {
        return new ThreadPoolExecutor(
                10,
                30,
                5,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(1000),
                springThreadFactory);
    }
}
