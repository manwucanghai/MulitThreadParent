package com.zws.concurrent.thread.base.containce.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengws
 * @date 2019-12-08 07:19
 */
public class NotSafeHashMap extends Thread{
    public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    private final static int MAXNUM = 100000;
    private int start;

    public NotSafeHashMap(int start) {
        this.start = start;
    }

    @Override
    public void run() {
        for (int i = start; i < MAXNUM; i+=2) {
            map.put(i,i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new NotSafeHashMap(1);
        Thread t2 = new NotSafeHashMap(2);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("map size is: " + map.size());
    }
}
