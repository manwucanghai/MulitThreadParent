package com.zws.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author zhengws
 * @date 2019-12-05 09:31
 */

public abstract class ConcurrentRunable {

    private final int clientTotal = 5000;

    private final int threadTotal = 10;

    private final Semaphore semaphore = new Semaphore(threadTotal);

    private final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

    private ExecutorService service = Executors.newCachedThreadPool();

    public void start() throws InterruptedException {
        for (int i = 0; i < clientTotal; i++) {
            service.execute(new Runnable() {
                public void run() {
                    try {
                        semaphore.acquire();
                        execute();
                        countDownLatch.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        }
        countDownLatch.await();
        result();
        this.service.shutdown();
    }

    public abstract void execute();

    public abstract void result();
}
