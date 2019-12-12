package com.zws.concurrent.thread.base.pool;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;
import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * @author zhengws
 * @date 2019-12-11 18:24
 */
public class RejectThreadPoolDemo {
    private static class Task implements Runnable {
        public void run() {
            System.out.println(DateUtils.getNow() + "Thread Id: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName());
            ThreadUtils.sleep(1000);
            throw new RuntimeException("error.");
        }
    }

    /**
     * 堆栈包装，用户打印详细的堆栈信息
     * @param task
     * @param exStack
     * @return
     */
    private static Runnable wrap(final Runnable task, final Exception exStack){
        return new Runnable() {
            @SneakyThrows
            public void run() {
                try {
                    task.run();
                }catch (Exception e){
                    exStack.printStackTrace();
                    throw e;
                }
            }
        };
        /**
         * 可打印调用方式谁发起调用的。
         * java.lang.Exception: client execption
         *
         * 	at com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo.main(RejectThreadPoolDemo.java:69)
         *  Exception in thread "pool-1-thread-4" java.lang.RuntimeException: error.
         * 	at com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task.run(RejectThreadPoolDemo.java:18)
         * 	at com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$1.run(RejectThreadPoolDemo.java:27)
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         *
         * 	默认异常打印如下，并没有打印是谁发起调用信息, 因此问题很难排查：
         * 	Exception in thread "pool-1-thread-7" java.lang.RuntimeException: error.
         * 	at com.zws.concurrent.thread.base.pool.RejectThreadPoolDemo$Task.run(RejectThreadPoolDemo.java:18)
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         * 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         * 	at java.lang.Thread.run(Thread.java:748)
         * java.lang.RuntimeException: error.
         */
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
                }) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println(DateUtils.getNow() + "beforeExecute running.");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                System.out.println(DateUtils.getNow() + "afterExecute");
            }

            @Override
            protected void terminated() {
                super.terminated();
                System.out.println(DateUtils.getNow() + "thread pools terminated.");
            }
        };

        for (int i = 0; i < 20; i++) {
            /**
             * 会打印异常信息.
             */
            service.execute(task);
            /**
             * 打印详细堆栈信息.
             */
//            service.execute(wrap(task, new Exception("client execption")));
            /**
             * 采用submit的方式，出现异常，则不会打印任何异常信息。
             */
//            service.submit(task);
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
