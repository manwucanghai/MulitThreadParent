package com.itheima.disruptor.handler;

import com.itheima.disruptor.event.LongEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhengws
 * @create 2019/4/15 17:00
 */
public class LongEventHandler2 implements EventHandler<LongEvent>,WorkHandler<LongEvent> {


    @Override
    public void onEvent(LongEvent event, long l, boolean b) throws Exception {
        System.out.println("LongEventHandler2: "+l);
        System.out.println("LongEventHandler2: "+b);
        onEvent(event);
    }

    @Override
    public void onEvent(LongEvent event) throws Exception {
//        Thread.sleep(3000);
        System.out.println(Thread.currentThread().getName() + " LongEventHandler2 running : "+event.getValue());
    }
}
