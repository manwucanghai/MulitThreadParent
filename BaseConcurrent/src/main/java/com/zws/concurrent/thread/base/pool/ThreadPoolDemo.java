package com.zws.concurrent.thread.base.pool;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengws
 * @date 2019-12-11 16:20
 */
public class ThreadPoolDemo {

    private static class Task implements Runnable{
        public void run() {
            System.out.println(DateUtils.getNow() + "Thread Id: " + Thread.currentThread().getId() + ", Thread Name: " +Thread.currentThread().getName());
            ThreadUtils.sleep(1000);
        }
    }

    public static void main(String[] args) {
        Task task = new Task();
//        fixedThreadPoolTest(task);
//        cachedThreadPoolTest(task);
        scheduledThreadPoolTest(task);
    }

    private static void scheduledThreadPoolTest(Task task) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        System.out.println("start : "+ DateUtils.getNow());
        for (int i = 0; i < 2; i++) {
//            service.schedule(task, 1, TimeUnit.SECONDS);
            /**
             * start : 2019-12-11 16:45:34
             * 2019-12-11 16:45:35 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:45:35 Thread Id: 12, Thread Name: pool-1-thread-2
             * 说明: 只执行一次
             */
//            service.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
            /**
             * start : 2019-12-11 16:41:17
             * 2019-12-11 16:41:18 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:41:18 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:41:20 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:41:20 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:41:22 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:41:22 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:41:24 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:41:24 Thread Id: 12, Thread Name: pool-1-thread-2
             * 说明：
             * 首次执行延迟时间为initalDealy, 后面每次执行最小延迟时间为period，如果代码本身延迟时间已经超过period, 则代码执行完毕后就立即调起执行.
             */
            service.scheduleWithFixedDelay(task,1,2,TimeUnit.SECONDS);
            /**
             * 输出：
             * start : 2019-12-11 16:42:06
             * 2019-12-11 16:42:07 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:42:07 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:42:10 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:42:10 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:42:13 Thread Id: 11, Thread Name: pool-1-thread-1
             * 2019-12-11 16:42:13 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:42:16 Thread Id: 12, Thread Name: pool-1-thread-2
             * 2019-12-11 16:42:16 Thread Id: 11, Thread Name: pool-1-thread-1
             * 说明：
             * 首次执行延迟时间为initalDealy, 后面每次执行完后，都必须延迟时间为period 再调起执行.
             */
        }
    }

    private static void cachedThreadPoolTest(Task task) {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            service.execute(task);
        }
        service.shutdown();
        /**
         * 输出：
         * 2019-12-11 16:27:56 Thread Id: 19, Thread Name: pool-1-thread-9
         * 2019-12-11 16:27:56 Thread Id: 18, Thread Name: pool-1-thread-8
         * 2019-12-11 16:27:56 Thread Id: 15, Thread Name: pool-1-thread-5
         * 2019-12-11 16:27:56 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 16:27:56 Thread Id: 17, Thread Name: pool-1-thread-7
         * 2019-12-11 16:27:56 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 16:27:56 Thread Id: 14, Thread Name: pool-1-thread-4
         * 2019-12-11 16:27:56 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 16:27:56 Thread Id: 20, Thread Name: pool-1-thread-10
         * 2019-12-11 16:27:56 Thread Id: 16, Thread Name: pool-1-thread-6
         */
    }

    private static void fixedThreadPoolTest(Task task) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            service.execute(task);
        }
        service.shutdown();

        /**
         * 输出：
         * 2019-12-11 16:25:08 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 16:25:08 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 16:25:08 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 16:25:09 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 16:25:09 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 16:25:09 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 16:25:10 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 16:25:10 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 16:25:10 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 16:25:11 Thread Id: 12, Thread Name: pool-1-thread-2
         */}
}
