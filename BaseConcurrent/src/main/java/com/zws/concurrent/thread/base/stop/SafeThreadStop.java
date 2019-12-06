package com.zws.concurrent.thread.base.stop;

import com.zws.concurrent.annotation.Recommend;
import com.zws.concurrent.annotation.Safety;
import com.zws.concurrent.utils.ThreadUtils;

/**
 * 不安全关闭方式
 *
 * @author zhengws
 * @date 2019-12-04 15:17
 */
@Safety
@Recommend
public class SafeThreadStop {

    private static class User {
        public int id;
        public String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static User user = new User(1,"1");
    private static class ModifyObjectThread implements Runnable {

        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                synchronized (user){
                    int value = (int) (System.currentTimeMillis()/1000);
                    user.id = value;
                    ThreadUtils.sleep(100);
                    user.name = String.valueOf(value);
                }
                Thread.yield();
            }
        }
    }

    private static class WatchObjectThread implements Runnable {
        public void run() {
            while (true){
                synchronized (user){
                    if (user.id != Integer.parseInt(user.name)){
                        System.out.println(user.toString());
                    }
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(new WatchObjectThread());
        t1.start();

        Thread t2;
        while (true){
            t2 = new Thread(new ModifyObjectThread());
            t2.start();
            ThreadUtils.sleep(150);
            t2.interrupt();
        }


        /**
         * 无任何输出，说明线程安全关闭.
         */
    }
}
