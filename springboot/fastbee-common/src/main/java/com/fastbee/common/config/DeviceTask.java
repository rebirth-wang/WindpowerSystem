//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@ConfigurationProperties(
        prefix = "spring.task.execution.pool"
)
public class DeviceTask {
    private int coreSize;
    private int maxSize;
    private int queueCapacity;
    private int keepAlive;

    @Bean({"deviceStatusTask"})
    public Executor deviceStatusTaskExecutor() {
        return this.builder("deviceStatusTask");
    }

    @Bean({"deviceFetchPropTask"})
    public Executor deviceFetchTaskExecutor() {
        return this.builder("deviceFetchPropTask");
    }

    @Bean({"deviceReplyMessageTask"})
    public Executor deviceReplyTaskExecutor() {
        return this.builder("deviceReplyMessageTask");
    }

    @Bean({"deviceUpMessageTask"})
    public Executor deviceUpMessageTaskExecutor() {
        return this.builder("deviceUpMessageTask");
    }

    @Bean({"functionInvokeTask"})
    public Executor functionInvokeTaskExecutor() {
        return this.builder("functionInvokeTask");
    }

    @Bean({"messageConsumeTask"})
    public Executor messageConsumeTaskExecutor() {
        return this.builder("messageConsumeTask");
    }

    @Bean({"messageConsumeTaskPub"})
    public Executor messageConsumePubTaskExecutor() {
        return this.builder("messageConsumeTaskPub");
    }

    @Bean({"messageConsumeTaskFetch"})
    public Executor messageConsumeFetchTaskExecutor() {
        return this.builder("messageConsumeTaskFetch");
    }

    @Bean({"delayUpgradeTask"})
    public Executor delayedTaskExecutor() {
        return this.builder("delayUpgradeTask");
    }

    @Bean({"deviceOtherMsgTask"})
    public Executor deviceOtherTaskExecutor() {
        return this.builder("deviceOtherMsgTask");
    }

    @Bean({"deviceTestMsgTask"})
    public Executor deviceTestTaskExecutor() {
        return this.builder("deviceTestMsgTask");
    }

    private ThreadPoolTaskExecutor builder(String threadNamePrefix) {
        ThreadPoolTaskExecutor var2 = new ThreadPoolTaskExecutor();
        var2.setCorePoolSize(this.coreSize);
        var2.setMaxPoolSize(this.maxSize);
        var2.setKeepAliveSeconds(this.keepAlive);
        var2.setQueueCapacity(this.queueCapacity);
        var2.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        var2.setThreadNamePrefix(threadNamePrefix);
        var2.initialize();
        return var2;
    }

    public int getCoreSize() {
        return this.coreSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public int getKeepAlive() {
        return this.keepAlive;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DeviceTask)) {
            return false;
        } else {
            DeviceTask var2 = (DeviceTask)o;
            if (!var2.canEqual(this)) {
                return false;
            } else if (this.getCoreSize() != var2.getCoreSize()) {
                return false;
            } else if (this.getMaxSize() != var2.getMaxSize()) {
                return false;
            } else if (this.getQueueCapacity() != var2.getQueueCapacity()) {
                return false;
            } else {
                return this.getKeepAlive() == var2.getKeepAlive();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DeviceTask;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        var2 = var2 * 59 + this.getCoreSize();
        var2 = var2 * 59 + this.getMaxSize();
        var2 = var2 * 59 + this.getQueueCapacity();
        var2 = var2 * 59 + this.getKeepAlive();
        return var2;
    }

    public String toString() {
        return "DeviceTask(coreSize=" + this.getCoreSize() + ", maxSize=" + this.getMaxSize() + ", queueCapacity=" + this.getQueueCapacity() + ", keepAlive=" + this.getKeepAlive() + ")";
    }
}
