package com.zws.concurrent.thread.base.containce.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengws
 * @date 2019-12-07 20:49
 */
public class NotSafeArraryList {
    private static List<Integer> list = new ArrayList<Integer>(10);
    private static int MAXVALUE = 20000;

    private static class AddThread extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < MAXVALUE; i++) {
                list.add(i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new AddThread();
        Thread t2 = new AddThread();

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("result list size is: " + list.size());

        /**
         * 第一种情况:
         * Exception in thread "Thread-0" java.lang.ArrayIndexOutOfBoundsException: 109
         * 	at java.util.ArrayList.add(ArrayList.java:463)
         * 	at com.zws.concurrent.thread.base.containce.sync.NotSafeArraryList$AddThread.run(NotSafeArraryList.java:18)
         * result list size is: 20097
         *
         * 第二种情况：
         * 无异常，但结果不足40000.
         *
         * 思路分析：
         * 1、抛异常
         *    因为ArrayList在扩容过程中，内部一致性被破坏，但由于没有锁的保护，另外一个线程访问到了不一致的内部状态，导致出现越界问题。
         * 2、结果小于预期值
         *    由于多线程访问冲突，两个线程同时对ArrayList中的同一个位置进行赋值导致的
         */
    }
}
