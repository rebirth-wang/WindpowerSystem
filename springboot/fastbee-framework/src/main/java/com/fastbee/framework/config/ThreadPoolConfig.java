//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fastbee.common.utils.Threads;

@Configuration
public class ThreadPoolConfig {
    private int corePoolSize = 50;
    private int maxPoolSize = 200;
    private int queueCapacity = 1000;
    private int keepAliveSeconds = 300;

    @Bean(
            name = {"threadPoolTaskExecutor"}
    )
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setCorePoolSize(this.corePoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Bean(
            name = {"scheduledExecutorService"}
    )
    protected ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(this.corePoolSize, (new BasicThreadFactory.Builder()).namingPattern("schedule-pool-%d").daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy()) {
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }
}
