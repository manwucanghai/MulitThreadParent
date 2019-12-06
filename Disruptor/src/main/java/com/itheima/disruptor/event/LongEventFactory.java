package com.itheima.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zhengws
 * @create 2019/4/15 16:58
 */
public class LongEventFactory implements EventFactory<LongEvent> {
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
