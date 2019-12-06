package com.zws.concurrent.thread.base.create;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author zhengws
 * @date 2019-12-04 15:03
 */
public class CreateThread {
    private static class UseExtendThead extends Thread{
        @Override
        public void run() {
            System.out.println("UseExtend Start.");
        }
    }

    private static class UseImplRunnable implements Runnable{

        public void run() {
            System.out.println("UseImplRunnable Start.");
        }
    }

    private static class UseImplCallable implements Callable<String>{

        public String call() throws Exception {
            System.out.println("UseImplCallable Start.");
            return "success";
        }
    }

    public static void main(String[] args) throws Exception{
        UseExtendThead t1 = new UseExtendThead();
        t1.start();

        Thread t2 = new Thread(new UseImplRunnable());
        t2.start();

        FutureTask<String> futureTask = new FutureTask<String>(new UseImplCallable());
        Thread t3 = new Thread(futureTask);
        t3.start();
        System.out.println(futureTask.get());
        /**
         * 输出：
         * UseExtend Start.
         * UseImplRunnable Start.
         * UseImplCallable Start.
         * success
         *
         * 结论：
         * 通过实现Callable，FutureTask调用线程，可获取线程执行结果。
         */
    }
}
