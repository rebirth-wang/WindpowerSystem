//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Threads {
    private static final Logger aM = LoggerFactory.getLogger(Threads.class);

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException var3) {
        }
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();

            try {
                if (!pool.awaitTermination(120L, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(120L, TimeUnit.SECONDS)) {
                        aM.info("Pool did not terminate");
                    }
                }
            } catch (InterruptedException var2) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

    }

    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future) {
            try {
                Future var2 = (Future)r;
                if (var2.isDone()) {
                    var2.get();
                }
            } catch (CancellationException var3) {
                t = var3;
            } catch (ExecutionException var4) {
                t = var4.getCause();
            } catch (InterruptedException var5) {
                Thread.currentThread().interrupt();
            }
        }

        if (t != null) {
            aM.error(t.getMessage(), t);
        }

    }
}
