package com.zws.concurrent.thread.base.group;

import com.zws.concurrent.utils.ThreadUtils;

/**
 * @author zhengws
 * @date 2019-12-07 20:14
 */
public class ThreadGroupName extends Thread{
    @Override
    public void run() {
        String message = Thread.currentThread().getThreadGroup().getName() + " - " + Thread.currentThread().getName();
        while (true){
            ThreadUtils.sleep(1000);
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("testThreadGroup");

        Thread t1 = new Thread(group, new ThreadGroupName(), "T1");
        Thread t2 = new Thread(group, new ThreadGroupName(), "T2");

        t1.start();
        t2.start();

        /**
         * activeCount()可以获得活动线程的总数，但由于线程是动态的，因此这个值只是一个估计值，无法确定精确
         */
        System.out.println(group.getName() + " count is : " + group.activeCount());

        group.list();


        /**
         * 输出：
         * testThreadGroup count is : 2
         * java.lang.ThreadGroup[name=testThreadGroup,maxpri=10]
         *     Thread[T1,5,testThreadGroup]
         *     Thread[T2,5,testThreadGroup]
         * testThreadGroup - T2
         * testThreadGroup - T1
         * testThreadGroup - T2
         * testThreadGroup - T1
         * testThreadGroup - T2
         * testThreadGroup - T1
         */
    }
}
