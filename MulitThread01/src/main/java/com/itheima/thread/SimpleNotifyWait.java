package com.itheima.thread;

/**
 * @author zhengws
 * @create 2019/3/24 12:02
 */
public class SimpleNotifyWait {
    private final static Object obj = new Object();
    public static class Thread1 implements Runnable{

        @Override
        public void run() {
            synchronized (obj){
                System.out.println(System.currentTimeMillis()+":T1 start! ");
                try {
                    System.out.println(System.currentTimeMillis()+":T1 wait for obj! ");
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+":T1 wait end ");
            }
        }
    }

    public static class Thread2 implements Runnable{

        @Override
        public void run() {
            synchronized (obj){
                System.out.println(System.currentTimeMillis()+":T2 start! ");
                try {
                    obj.notify();
                    System.out.println(System.currentTimeMillis()+":T2 notify for obj! ");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis()+":T2 end ");
            }
        }
    }

    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        Thread2 thread2 = new Thread2();
        new Thread(thread1).start();
        new Thread(thread2).start();
        //在T2通知T1继续执行后，T1并不能立即继续执行，而是要等待T2释放object的锁，并重新成功获得锁后，才能继续执行
        /**
         *
         1553400524724:T1 start!
         1553400524724:T1 wait for obj!
         1553400524724:T2 start!
         1553400524725:T2 notify for obj!
         1553400526728:T2 wait end
         1553400526728:T1 wait end
         */
    }
}
