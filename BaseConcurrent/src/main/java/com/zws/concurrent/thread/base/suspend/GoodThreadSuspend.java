package com.zws.concurrent.thread.base.suspend;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

/**
 * 可靠的挂起线程，与重新启用线程方式.
 * @author zhengws
 * @date 2019-12-07 19:20
 */
public class GoodThreadSuspend {
    public static Object o = new Object();

    private static class ChangeThread extends Thread {
        private volatile boolean isSuspend = false;

        public ChangeThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                ThreadUtils.sleep(1000);
                if (isSuspend) {
                    synchronized (this) {
                        try {
                            System.out.println(DateUtils.getNow() + getName() + " start suspend.");
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                synchronized (o) {
                    System.out.println(DateUtils.getNow() + getName() + " running.");
                }
                Thread.yield();
            }
        }

        public void startSuspend() {
            this.isSuspend = true;
        }

        public void startResume() {
            synchronized (this) {
                notifyAll();
            }
            this.isSuspend = false;
        }
    }

    private static class ReadThread extends Thread {
        public ReadThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true){
                ThreadUtils.sleep(1000);
                synchronized (o) {
                    System.out.println(DateUtils.getNow() + getName() + " running ...");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ChangeThread t1 = new ChangeThread("change-thread");
        ReadThread t2 = new ReadThread("read-thread");

        t1.start();
        t2.start();
        ThreadUtils.sleep(2000);
        t1.startSuspend();

        ThreadUtils.sleep(10000);
        System.out.println(DateUtils.getNow() + t1.getName() + " start resume >>>>>>>");
        t1.startResume();

        t1.join();
        t2.join();

        /**
         * 输出：
         * 2019-12-07 19:37:01 change-thread running.
         * 2019-12-07 19:37:01 read-thread running ...
         * 2019-12-07 19:37:02 read-thread running ...
         * 2019-12-07 19:37:02 change-thread start suspend.
         * 2019-12-07 19:37:03 read-thread running ...
         * 2019-12-07 19:37:04 read-thread running ...
         * 2019-12-07 19:37:05 read-thread running ...
         * 2019-12-07 19:37:06 read-thread running ...
         * 2019-12-07 19:37:07 read-thread running ...
         * 2019-12-07 19:37:08 read-thread running ...
         * 2019-12-07 19:37:09 read-thread running ...
         * 2019-12-07 19:37:10 read-thread running ...
         * 2019-12-07 19:37:11 read-thread running ...
         * 2019-12-07 19:37:12 change-thread start resume >>>>>>>
         * 2019-12-07 19:37:12 change-thread running.
         * 2019-12-07 19:37:12 read-thread running ...
         * 2019-12-07 19:37:13 change-thread running.
         * 2019-12-07 19:37:13 read-thread running ...
         * 2019-12-07 19:37:14 change-thread running.
         * 2019-12-07 19:37:14 read-thread running ...
         */

    }
}
