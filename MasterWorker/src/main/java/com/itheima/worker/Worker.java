package com.itheima.worker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhengws
 * @create 2019/4/3 21:22
 */
public class Worker<T, E> implements Runnable{
    private ConcurrentLinkedQueue<T> workQueue;
    private ConcurrentHashMap<String, E> resultMap;
    private CountDownLatch countDownLatch;

    public void setWorkQueue(ConcurrentLinkedQueue<T> workQueue) {
        this.workQueue = workQueue;
    }

    public void setResultMap(ConcurrentHashMap<String, E> resultMap) {
        this.resultMap = resultMap;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    public CountDownLatch getCountDownLatch(){
        return this.countDownLatch;
    }

    @Override
    public void run() {
        while(true){
            T input = this.workQueue.poll();
            if(input == null){
                break;
            }

            E output = handle(input);
            this.resultMap.put(Thread.currentThread().getName()+":"+input.hashCode(), output);
            countDownLatch.countDown();
        }
    }

    protected E handle(T input){
        return null;
    }

}
