package com.zws.concurrent.utils;

/**
 * @author zhengws
 * @date 2019-12-04 14:45
 */
public class ThreadUtils {
    /**
     * 封装sleep.
     * @param millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
