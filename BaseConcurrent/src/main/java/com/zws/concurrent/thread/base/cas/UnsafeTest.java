package com.zws.concurrent.thread.base.cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zhengws
 * @date 2019-12-12 14:19
 */
public class UnsafeTest {
    private final static Unsafe unsafe;
    private static final long itemOffset;
    private static final long nextOffset;
    private String item;
    private String next;

    static {
        try {
//            unsafe = Unsafe.getUnsafe();
            unsafe = initUnsafe();
            itemOffset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("item"));
            nextOffset = unsafe.objectFieldOffset(UnsafeTest.class.getDeclaredField("next"));
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
        /**
         * 解释为何field.get(null) 传入的是null.
         * java.lang.reflect.Field public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException
         * Returns the value of the field represented by this Field, on the specified object. The value is automatically wrapped in an object if it has a primitive type.
         * The underlying field's value is obtained as follows:
         * If the underlying field is a static field, the obj argument is ignored; it may be null.
         */
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        unsafeField.setAccessible(accessible);
        return unsafe;
    }

    public static void main(String[] args) {
        System.out.println(UnsafeTest.nextOffset);
        System.out.println(UnsafeTest.itemOffset);
        System.out.println(UnsafeTest.unsafe);

        /**
         * 一、运行，抛如下异常：
         * Exception in thread "main" java.lang.Error: java.lang.SecurityException: Unsafe
         * 	at com.zws.concurrent.thread.base.cas.UnsafeTest.<clinit>(UnsafeTest.java:22)
         * Caused by: java.lang.SecurityException: Unsafe
         * 	at sun.misc.Unsafe.getUnsafe(Unsafe.java:90)
         * 	at com.zws.concurrent.thread.base.cas.UnsafeTest.<clinit>(UnsafeTest.java:18)
         *
         * 	从以下Unsafe 源码可以看到, Unsafe类是一个单例类, 通过静态的getUnsafe()方法获取实例.
         * 	getUnsafe()方法中有一个权限检查的逻辑, 即:
         * 	如果不是系统域下的类(即，类的加载器不是启动类加载器), 调用getUnsafe()方法将抛出SecurityException异常.
         *
         *     @CallerSensitive
         *     public static Unsafe getUnsafe() {
         *         Class var0 = Reflection.getCallerClass();
         *         if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
         *             throw new SecurityException("Unsafe");
         *         } else {
         *             return theUnsafe;
         *         }
         *     }
         *
         *     public static boolean isSystemDomainLoader(ClassLoader var0) {
         *         return var0 == null;
         *     }
         *
         *  二、如何获取unsafe
         *  通过阅读源码，可以发现Unsafe 类，theUnsafe存放的是单例的实例，因此可以通过反射的方式进行获取。
         */
    }
}
