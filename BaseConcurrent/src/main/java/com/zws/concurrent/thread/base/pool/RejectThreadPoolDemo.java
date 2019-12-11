package com.zws.concurrent.thread.base.pool;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

import java.util.concurrent.*;

/**
 * @author zhengws
 * @date 2019-12-11 18:24
 */
public class RejectThreadPoolDemo {
    private static class Task implements Runnable{
        public void run() {
            System.out.println(DateUtils.getNow() + "Thread Id: " + Thread.currentThread().getId() + ", Thread Name: " +Thread.currentThread().getName());
            ThreadUtils.sleep(1000);
        }
    }

    public static void main(String[] args) {
        Task task = new Task();
        ExecutorService service = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println(r.toString() + "rejected...");
            }
        }){
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
//                System.out.println(DateUtils.getNow() + "beforeExecute running.");
            }
        };

        for (int i = 0; i < 20; i++) {
            service.execute(task);
        }
        service.shutdown();

        /**
         * 输出：
         * com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task@66d3c617rejected...
         * com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task@66d3c617rejected...
         * com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task@66d3c617rejected...
         * com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task@66d3c617rejected...
         * com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task@66d3c617rejected...
         * 2019-12-11 18:29:44 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 18:29:44 Thread Id: 14, Thread Name: pool-1-thread-4
         * 2019-12-11 18:29:44 Thread Id: 15, Thread Name: pool-1-thread-5
         * 2019-12-11 18:29:44 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 18:29:44 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 18:29:45 Thread Id: 15, Thread Name: pool-1-thread-5
         * 2019-12-11 18:29:45 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 18:29:45 Thread Id: 14, Thread Name: pool-1-thread-4
         * 2019-12-11 18:29:45 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 18:29:45 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 18:29:46 Thread Id: 11, Thread Name: pool-1-thread-1
         * 2019-12-11 18:29:46 Thread Id: 15, Thread Name: pool-1-thread-5
         * 2019-12-11 18:29:46 Thread Id: 12, Thread Name: pool-1-thread-2
         * 2019-12-11 18:29:46 Thread Id: 13, Thread Name: pool-1-thread-3
         * 2019-12-11 18:29:46 Thread Id: 14, Thread Name: pool-1-thread-4
         * 解析：
         *  20个任务，当前执行线程拿走5个任务，队列中可存放10个任务，因此剩余5个任务被拒绝。
         */
    }
}
