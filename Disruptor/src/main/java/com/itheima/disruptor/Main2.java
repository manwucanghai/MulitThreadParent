package com.itheima.disruptor;

import com.itheima.disruptor.event.LongEvent;
import com.itheima.disruptor.event.LongEventFactory;
import com.itheima.disruptor.handler.Consumer;
import com.itheima.disruptor.producer.Producer;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhengws
 * @create 2019/4/15 18:14
 */
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        LongEventFactory factory = new LongEventFactory();
        int ringBufferSize = 1024 * 1024;

        RingBuffer<LongEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE, factory, ringBufferSize, new YieldingWaitStrategy());
        
        SequenceBarrier barriers = ringBuffer.newBarrier();

        CountDownLatch latch = new CountDownLatch(10000);
        Consumer[] consumers = new Consumer[3];
        for(int i = 0; i < consumers.length; i++){
            consumers[i] = new Consumer("consumer - " + i, latch);
        }

        WorkerPool<LongEvent> workerPool = new WorkerPool<LongEvent>(ringBuffer, barriers, new IgnoreExceptionHandler(), consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        workerPool.start(service);

//        final CountDownLatch latch = new CountDownLatch(1);
//        for (int i = 0; i < 100; i++) {
//            final Producer p = new Producer(ringBuffer);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        latch.await();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    for(int j = 0; j < 100; j ++){
//                        p.onData(j * 100L);
//                    }
//                }
//            }).start();
//        }

        Producer p = new Producer(ringBuffer);
        for (int i = 1; i < 10001 ; i++) {
            p.onData(i*1L);
//            latch.countDown();
        }
//        Thread.sleep(2000);
//        System.out.println("---------------开始生产-----------------");
//        latch.countDown();
//        Thread.sleep(5000);
        latch.await();
//        Thread.sleep(1000);


        for (Consumer consumer : consumers){
            System.out.println("总数:" + consumer.getCount() +", value: "+ consumer.getaLong());
        }
        workerPool.halt();
        service.shutdown();
    }
}
