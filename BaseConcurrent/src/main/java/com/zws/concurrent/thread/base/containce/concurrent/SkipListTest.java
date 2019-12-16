package com.zws.concurrent.thread.base.containce.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author zhengws
 * @date 2019-12-12 16:53
 */
public class SkipListTest {
    public static void main(String[] args) {
        Map<Integer, Integer> skipMap = new ConcurrentSkipListMap<>();
        for (int i = 5; i > 0 ; i--) {
            skipMap.put(i, i+100);
        }

        for (Map.Entry<Integer, Integer> entry : skipMap.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        /**
         * SikpListMap 会自动进行排序
         * 输出：
         * 1 : 101
         * 2 : 102
         * 3 : 103
         * 4 : 104
         * 5 : 105
         *
         */
    }
}
