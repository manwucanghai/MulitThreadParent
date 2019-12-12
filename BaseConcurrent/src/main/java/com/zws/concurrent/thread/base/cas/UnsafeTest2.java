package com.zws.concurrent.thread.base.cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zhengws
 * @date 2019-12-12 14:19
 */
public class UnsafeTest2 {
    private final static Unsafe unsafe;
    private static final long itemOffset;
    private static final long nextOffset;
    private String item;
    private String next;

    static {
        try {
            unsafe = initUnsafe();
            itemOffset = unsafe.objectFieldOffset(UnsafeTest2.class.getDeclaredField("item"));
            nextOffset = unsafe.objectFieldOffset(UnsafeTest2.class.getDeclaredField("next"));
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    /**
     * 通过反射获取unsafe.
     *
     * @return
     * @throws Throwable
     */
    private static Unsafe initUnsafe() throws Throwable{
        Class<?> clazz = Class.forName("sun.misc.Unsafe");
        Field unsafeField = clazz.getDeclaredField("theUnsafe");
        boolean accessible = unsafeField.isAccessible();
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        unsafeField.setAccessible(accessible);
        return unsafe;
    }

    private boolean compareAndSet(String ex, String val){
        return unsafe.compareAndSwapObject(this, itemOffset, ex, val);
    }

    public static void main(String[] args) {
        UnsafeTest2 test2 = new UnsafeTest2();
        boolean isSuccess = test2.compareAndSet(null, "ITEM");
        System.out.println(isSuccess);
        System.out.println(test2.item);
        System.out.println("#########");
        isSuccess = test2.compareAndSet(null, "ITEM2");
        System.out.println(isSuccess);
        System.out.println(test2.item);

        /**
         * 输出：
         * true
         * ITEM
         * #########
         * false
         * ITEM
         */
    }
}
