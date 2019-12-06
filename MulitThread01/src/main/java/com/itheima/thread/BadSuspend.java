package com.itheima.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author zhengws
 * @create 2019/3/24 12:14
 */
public class BadSuspend {
    public static Object obj = new Object();

    public static class ChangeObjectThread extends Thread{
        public ChangeObjectThread(String name){
            super.setName(name);
        }

        @Override
        public void run() {
            synchronized (obj){
                System.out.println("in "+ getName());
                Thread.currentThread().suspend();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(getProcessID());
        ChangeObjectThread t1 = new ChangeObjectThread("change1");
        ChangeObjectThread t2 = new ChangeObjectThread("change2");
        t1.start();
        Thread.sleep(1000);
        t2.start();
        t1.resume();
        t2.resume();
        t1.join();
        t2.join();
    }

    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }
}
