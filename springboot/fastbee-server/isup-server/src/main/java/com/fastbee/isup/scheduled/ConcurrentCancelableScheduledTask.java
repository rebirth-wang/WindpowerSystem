package com.fastbee.isup.scheduled;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 支持多定时任务并发、可单独取消、执行完成后线程自动销毁的工具类
 */
@Profile("isup")
@Component
public class ConcurrentCancelableScheduledTask {
    // 线程池（支持多核心线程，核心线程空闲超时销毁）
    private ScheduledExecutorService executor;
    // 存储任务ID与对应Future的映射，用于单独取消指定任务
    private final Map<String, ScheduledFuture<?>> taskFutureMap = new ConcurrentHashMap<>();
    // 任务ID生成器（保证任务ID唯一）
    private final AtomicInteger taskIdGenerator = new AtomicInteger(0);

    /**
     * 初始化线程池
     */
    @PostConstruct
    public void init() {
        // 自定义线程工厂（给线程命名，便于排查问题）
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("scheduled-task-thread-" + threadNum.getAndIncrement());
                thread.setDaemon(false); // 非守护线程（保证任务执行完成）
                return thread;
            }
        };

        // 初始化调度线程池
        // 核心线程数（根据并发任务数设置）
        int corePoolSize = 10;
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
                corePoolSize,
                threadFactory
        );
        // 设置线程空闲超时时间
        executor.setKeepAliveTime(2, TimeUnit.MINUTES);
        // 核心线程允许超时销毁（关键：所有任务完成后线程自动回收）
        executor.allowCoreThreadTimeOut(true);
        // 拒绝策略：直接抛出异常（根据业务调整，如DiscardPolicy）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        this.executor = executor;
    }

    /**
     * 提交定时任务（返回唯一任务ID，用于取消任务）
     *
     * @param task     任务逻辑
     * @param delay    延迟执行时间
     * @param timeUnit 时间单位
     */
    public void submitScheduledTask(String taskId, Runnable task, long delay, TimeUnit timeUnit) {
        if (taskId == null || taskId.isEmpty() || task == null || timeUnit == null) {
            return;
        }
        this.cancelTask(taskId, true);
        // 调度任务并保存Future
        ScheduledFuture<?> future = executor.schedule(task, delay, timeUnit);
        taskFutureMap.put(taskId, future);
    }

    public String submitScheduledTask(Runnable task, long delay, TimeUnit timeUnit) {
        if (task == null || timeUnit == null) {
            throw new IllegalArgumentException("Task and timeUnit cannot be null");
        }
        // 生成唯一任务ID
        String taskId = "task-" + taskIdGenerator.incrementAndGet();
        this.submitScheduledTask(taskId, task, delay, timeUnit);
        return taskId;
    }

    /**
     * 单独取消指定任务（不影响其他任务）
     *
     * @param taskId                任务ID
     * @param mayInterruptIfRunning 是否中断正在执行的任务
     * @return 取消是否成功（false表示任务已完成/不存在）
     */
    public boolean cancelTask(String taskId, boolean mayInterruptIfRunning) {
        if (taskId == null || taskId.isEmpty()) return false;
        ScheduledFuture<?> future = taskFutureMap.get(taskId);
        if (future != null && !future.isDone()) {
            boolean result = future.cancel(mayInterruptIfRunning);
            // 成功取消或任务已完成时，从map中移除
            if (result || future.isDone()) {
                taskFutureMap.remove(taskId);
            }
            return result;
        }
        // 如果任务已完成，也从map中移除
        taskFutureMap.remove(taskId);
        return false;
    }

    /**
     * 优雅关闭线程池（所有已提交任务执行完成后销毁线程）
     */
    public void shutdownExecutorGracefully() {
        if (!executor.isShutdown()) {
            executor.shutdown(); // 拒绝新任务，等待已提交任务完成
            // 异步等待线程池终止，超时则强制关闭
            CompletableFuture.runAsync(() -> {
                try {
                    if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                        executor.shutdownNow(); // 超时强制关闭
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            });
        }
    }

    /**
     * 强制关闭线程池（立即中断所有任务）
     */
    public void shutdownExecutorImmediately() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    /**
     * 获取当前活跃任务数
     *
     * @return 未完成的定时任务数
     */
    public int getActiveTaskCount() {
        // 清理已完成的任务
        cleanupCompletedTasks();
        return taskFutureMap.size();
    }

    /**
     * 检查线程池是否已终止（线程全部销毁）
     *
     * @return true=线程已全部销毁
     */
    public boolean isExecutorTerminated() {
        return executor.isTerminated();
    }

    /**
     * 清理已完成的任务，防止内存泄漏
     */
    private void cleanupCompletedTasks() {
        taskFutureMap.entrySet().removeIf(entry -> entry.getValue().isDone());
    }

    /**
     * Spring容器销毁时自动调用，确保资源正确释放
     */
    @PreDestroy
    public void destroy() {
        shutdownExecutorGracefully();
    }
}
