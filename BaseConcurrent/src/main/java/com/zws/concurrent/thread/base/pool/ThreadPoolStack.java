package com.zws.concurrent.thread.base.pool;

import com.zws.concurrent.utils.ThreadUtils;
import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * @author zhengws
 * @date 2019-12-12 10:04
 */
public class ThreadPoolStack {
    private static class Task implements Runnable{

        private int a;
        private int b;

        public Task(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public void run() {
            int c = a / b;
            ThreadUtils.sleep(100);
            System.out.println("Result: " + c);

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
    }

    public static void main(String[] args) {
        ExecutorService services = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(10), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Test-Thread");
            }
        }, new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println(r.toString() + " rejected.");
            }
        });


        for (int i = 0; i < 5; i++) {
            Task task = new Task(100, i);
//            services.submit(task);
//            services.execute(task);
            services.execute(wrap(task, new Exception("client exception")));
        }
        services.shutdown();

        /**
         * 1、调用 services.submit(task)
         * 执行结果少了一个，但是没有任何异常信息输出
         * 输出：
         * Result: 100
         * Result: 50
         * Result: 33
         * Result: 25
         *
         *
         * 2、调用 services.execute(task);
         * 有打印部分异常信息，但是调用方并没有打印出来。
         * Exception in thread "Test-Thread" java.lang.ArithmeticException: / by zero
         * 	at com.zws.concurrent.thread.base.pool.ThreadPoolStack$Task.run(ThreadPoolStack.java:25)
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         * 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         * 	at java.lang.Thread.run(Thread.java:748)
         * Result: 100
         * Result: 50
         * Result: 33
         * Result: 25
         *
         *
         * 3、采用自定义包装方法，打印详细的堆栈信息
         * java.lang.Exception: client exception
         * 	at com.zws.concurrent.thread.base.pool.ThreadPoolStack.main(ThreadPoolStack.java:49)
         * Exception in thread "Test-Thread" java.lang.ArithmeticException: / by zero
         * 	at com.zws.concurrent.thread.base.pool.ThreadPoolStack$Task.run(ThreadPoolStack.java:26)
         * 	at com.zws.concurrent.thread.base.pool.ThreadPoolStack$3.run(ThreadPoolStack.java:88)
         * 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         * 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         * 	at java.lang.Thread.run(Thread.java:748)
         * Result: 100
         * Result: 50
         * Result: 33
         * Result: 25
         */
    }
}
