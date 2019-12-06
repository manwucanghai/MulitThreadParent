package com.zws.concurrent.thread.base.atomic;

import com.zws.concurrent.ConcurrentRunable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhengws
 * @date 2019-12-05 10:13
 */
public class AtomicExample1 extends ConcurrentRunable {
    private AtomicInteger count = new AtomicInteger(0);
    public void execute() {
        int value = count.getAndIncrement();
        System.out.println(Thread.currentThread().getName()+" : " + value + " ----- " + System.currentTimeMillis() + " --jvm--> " + System.nanoTime());
    }

    public void result() {
        System.out.println("count is: " + count.get());
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicExample1 example = new AtomicExample1();
        example.start();
    }
}
