package com.zws.concurrent.lock.readwrite;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zhengws
 * @date 2019-12-08 09:56
 */
public class ReadWriteLockDemo {
    private long value;

    private long handleRead(Lock lock) {
        try {
           lock.lock();
            ThreadUtils.sleep(1000);
            System.out.println(DateUtils.getNow() + Thread.currentThread().getName() + " -> "+ value + " read");
            return value;
        }finally {
            lock.unlock();
        }
    }

    private void handleWrite(Lock lock, long value) {
        try {
            lock.lock();
            ThreadUtils.sleep(1000);
            System.out.println(DateUtils.getNow() + Thread.currentThread().getName() + " write");
            this.value = value;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final Lock readLock = readWriteLock.readLock();
        final Lock writeLock = readWriteLock.writeLock();

        final ReadWriteLockDemo demo = new ReadWriteLockDemo();

        for (int i = 0; i < 2 ; i++) {
            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    demo.handleWrite(writeLock, Thread.currentThread().getId());
                }
            });
            t2.start();
        }

        for (int i = 0; i < 10 ; i++) {
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    demo.handleRead(readLock);
                }
            });
            t1.start();
        }

        /**
         * 输出:
         * 2019-12-08 10:25:07 Thread-1 write
         * 2019-12-08 10:25:08 Thread-0 write
         * 2019-12-08 10:25:09 Thread-3 -> 11 read
         * 2019-12-08 10:25:09 Thread-10 -> 11 read
         * 2019-12-08 10:25:09 Thread-2 -> 11 read
         * 2019-12-08 10:25:09 Thread-9 -> 11 read
         * 2019-12-08 10:25:09 Thread-11 -> 11 read
         * 2019-12-08 10:25:09 Thread-4 -> 11 read
         * 2019-12-08 10:25:09 Thread-6 -> 11 read
         * 2019-12-08 10:25:09 Thread-8 -> 11 read
         * 2019-12-08 10:25:09 Thread-7 -> 11 read
         * 2019-12-08 10:25:09 Thread-5 -> 11 read
         */

    }
}
