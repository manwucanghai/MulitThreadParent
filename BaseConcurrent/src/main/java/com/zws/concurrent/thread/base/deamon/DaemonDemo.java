package com.zws.concurrent.thread.base.deamon;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

/**
 * @author zhengws
 * @date 2019-12-07 20:27
 */
public class DaemonDemo extends Thread {
    @Override
    public void run() {
        while (true){
            ThreadUtils.sleep(500);
            System.out.println(DateUtils.getNow() + getName() + " running ...");
        }
    }

    public static void main(String[] args) {
        Thread t1 = new DaemonDemo();
        t1.start();
        t1.setDaemon(true);
        System.out.println(Thread.currentThread().getName() + " sleep 3s");
        ThreadUtils.sleep(3000);

        /**
         * 输出:
         * main sleep 3s
         * 2019-12-07 20:30:10 Thread-0 running ...
         * 2019-12-07 20:30:10 Thread-0 running ...
         * 2019-12-07 20:30:11 Thread-0 running ...
         * 2019-12-07 20:30:11 Thread-0 running ...
         * 2019-12-07 20:30:12 Thread-0 running ...
         *
         * 说明： 当一个Java应用内，只有守护线程时，Java虚拟机就会自然退出。
         */
    }
}
