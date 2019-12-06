package com.itheima.disruptor;

import com.itheima.disruptor.event.LongEvent;
import com.itheima.disruptor.event.LongEventFactory;
import com.itheima.disruptor.handler.LongEventHandler;
import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhengws
 * @create 2019/4/15 17:48
 */
public class MuliWorkerMain {
    public static void main(String[] args) throws InterruptedException {
        int BUFFER_SIZE=1024;
        int THREAD_NUMBERS=4;

        LongEventFactory factory = new LongEventFactory();

        RingBuffer<LongEvent> ringBuffer = RingBuffer.createSingleProducer(factory, BUFFER_SIZE);

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBERS);

        WorkHandler<LongEvent> handler = new LongEventHandler();

        WorkerPool<LongEvent> workerPool = new WorkerPool<LongEvent>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), handler);

        workerPool.start(executor);

        //下面这个生产8个数据
        for(int i=0;i<8;i++){
            long seq=ringBuffer.next();
            ringBuffer.get(seq).setValue(9999*i);
            ringBuffer.publish(seq);
        }

        Thread.sleep(1000);
        workerPool.halt();
        executor.shutdown();
    }
}
