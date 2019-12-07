package com.zws.concurrent.thread.base.suspend;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

/**
 * 验证如果resume()操作意外地在suspend()前就执行了，那么被挂起的线程可能很难有机会被继续执行。
 *
 * @author zhengws
 * @date 2019-12-07 17:38
 */

public class BadThreadSuspend extends Thread {
    private static Object object = new Object();

    public BadThreadSuspend(String name) {
        super(name);
    }

    @Override
    public void run() {
        synchronized (object){
            //确保线程2先resume, 再进入suspend.
            ThreadUtils.sleep(500);
            System.out.println(DateUtils.getNow() + Thread.currentThread().getName() + " running ...");
            Thread.currentThread().suspend();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BadThreadSuspend t1 = new BadThreadSuspend(" first");
        BadThreadSuspend t2 = new BadThreadSuspend(" second");
        t1.start();

        ThreadUtils.sleep(2000);
        t2.start();

        t1.resume();
        System.out.println(DateUtils.getNow() + t1.getName() + "submit resume.");

        t2.resume();
        System.out.println(DateUtils.getNow() + t2.getName() + "submit resume.");
        t1.join();
        t2.join();

        /**
         * 输出：
         * 2019-12-07 18:09:35 first running ...
         * 2019-12-07 18:09:36 firstsubmit resume.
         * 2019-12-07 18:09:36 secondsubmit resume.
         * 2019-12-07 18:09:37 second running ...
         *
         * jstack 打印线程堆栈，如下：
         * " second" #12 prio=5 os_prio=31 tid=0x00007ff33f800800 nid=0x5903 runnable [0x000070000421f000]
         *    java.lang.Thread.State: RUNNABLE
         * 	at java.lang.Thread.suspend0(Native Method)
         * 	at java.lang.Thread.suspend(Thread.java:1032)
         * 	at com.zws.concurrent.thread.base.suspend.BadThreadSuspend.run(BadThreadSuspend.java:23)
         * 	- locked <0x000000076ac28d60> (a java.lang.Object)
         *
         * 	说明：
         * 	对于被挂起的线程，从它的线程状态上看，居然还是Runnable状态。
         */
    }
}
