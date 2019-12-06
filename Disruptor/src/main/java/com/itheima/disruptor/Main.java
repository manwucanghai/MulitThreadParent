package com.itheima.disruptor;

import com.itheima.disruptor.event.LongEvent;
import com.itheima.disruptor.event.LongEventFactory;
import com.itheima.disruptor.handler.Consumer;
import com.itheima.disruptor.handler.LongEventHandler;
import com.itheima.disruptor.handler.LongEventHandler2;
import com.itheima.disruptor.producer.LongEventProducer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author zhengws
 * @create 2019/4/15 17:15
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //创建线程池
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        //创建工厂
        LongEventFactory factory = new LongEventFactory();

        //创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024 * 1024;

        //创建disruptor
        Disruptor<LongEvent> disruptor =
                new Disruptor<LongEvent>(factory, ringBufferSize, threadFactory, ProducerType.SINGLE, new YieldingWaitStrategy());

        LongEventHandler handler1 = new LongEventHandler();
        LongEventHandler2 handler2 = new LongEventHandler2();
        // 连接消费事件方法
        disruptor.handleEventsWith(handler1, handler2);

        Thread.sleep(20000);
        disruptor.start();

        //发布事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        for(long l = 0; l<1000000; l++){
            byteBuffer.putLong(0, l);
            producer.onData(byteBuffer);
        }

//        Thread.sleep(5000);
        System.out.println("################");

//        while (true){
//
////            Thread.sleep(300);
//            long cursor = disruptor.getCursor();
//            if (disruptor.getSequenceValueFor(handler1) == cursor && disruptor.getSequenceValueFor(handler2) == cursor){
//                System.out.println(disruptor.getSequenceValueFor(handler1));
//                System.out.println(disruptor.getSequenceValueFor(handler2));
//                System.out.println("*************");
//                disruptor.halt();
//                disruptor.shutdown();
//                break;
//            }
//        }
//        long cursor1 = ringBuffer.getCursor();
//        System.out.println(cursor1);
//        while (true){
//            if (handler1.getCount().intValue()==2 && handler2.getCount().intValue() == 2){
//                System.out.println("");
//                break;
//            }
//        }
//        disruptor.shutdown();
    }
}
