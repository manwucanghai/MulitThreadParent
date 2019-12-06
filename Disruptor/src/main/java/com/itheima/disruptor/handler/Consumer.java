package com.itheima.disruptor.handler;

import com.itheima.disruptor.event.LongEvent;
import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhengws
 * @create 2019/4/15 18:06
 */
public class Consumer implements WorkHandler<LongEvent>{
    private String consumerId;
    private AtomicInteger count = new AtomicInteger(0);
    private AtomicLong aLong = new AtomicLong(0);
    private CountDownLatch latch;
    public Consumer(String consumerId, CountDownLatch latch) {
        this.consumerId = consumerId;
        this.latch = latch;
    }

    @Override
    public void onEvent(LongEvent event) throws Exception {
        System.out.println("当前消费者: " + consumerId + "，消费信息：" + event.getValue());
        aLong.addAndGet(event.getValue());
        count.incrementAndGet();
        latch.countDown();
    }

    public AtomicInteger getCount() {
        return count;
    }

    public AtomicLong getaLong() {
        return aLong;
    }
}
