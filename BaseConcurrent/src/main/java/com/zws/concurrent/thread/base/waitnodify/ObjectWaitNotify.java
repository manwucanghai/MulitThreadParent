package com.zws.concurrent.thread.base.waitnodify;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

/**
 * @author zhengws
 * @date 2019-12-04 14:44
 */
public class ObjectWaitNotify {
    public static Object object = new Object();

    public static class ThreadWait extends Thread{
        @Override
        public void run() {
            synchronized (object){
                System.out.println(DateUtils.getNow() + " : ThreadWait start!");
                try {
                    System.out.println(DateUtils.getNow() + " : ThreadWait wait ...");
                    object.wait();
                    System.out.println(DateUtils.getNow() + " : ThreadWait end!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ThreadNotify extends Thread{
        @Override
        public void run() {
            synchronized (object){
                System.out.println(DateUtils.getNow() + " : ThreadNotify start!");
                object.notify();
                ThreadUtils.sleep(2000);
                System.out.println(DateUtils.getNow() + " : ThreadNotify end!");
            }
        }
    }

    public static void main(String[] args) {
        ThreadWait wait = new ThreadWait();
        ThreadNotify notify = new ThreadNotify();
        wait.start();
        notify.start();

        /**
         * 输出结果：
         * 2019-12-04 14:55:45 : ThreadWait start!
         * 2019-12-04 14:55:45 : ThreadWait wait ...
         * 2019-12-04 14:55:45 : ThreadNotify start!
         * 2019-12-04 14:55:47 : ThreadNotify end!
         * 2019-12-04 14:55:47 : ThreadWait end!
         *
         * 结论：
         * 1、wait() 方法会释放目标对象的锁
         * 2、notify() 方法并不会释放当前锁，而是需要等待当前同步代码块执行完毕后，才释放锁。
         * 3、在ThreadNotify通知ThreadWait继续执行后，ThreadWait并不能立即继续执行，而是要等待ThreadNotify释放object的锁，
         * 并重新成功获得锁后，才能继续执行
         */
    }
}
