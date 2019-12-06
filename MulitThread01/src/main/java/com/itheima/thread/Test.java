package com.itheima.thread;

/**
 * @author zhengws
 * @create 2019/3/24 10:56
 */
public class Test{
    public static void main(String[] args) throws InterruptedException {
        Thread t1=new Thread(){
            @Override
            public void run(){
                while(true){
                    System.out.println(Thread.currentThread().isInterrupted());
                    if (Thread.currentThread().isInterrupted()){
                        System.out.println("%%%%%%%%%%%%");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Interruted When Sleep");
                        //设置中断状态
                        Thread.currentThread().interrupt();
                    }
                    Thread.yield();
                }
            }
        };
        t1.start();
        Thread.sleep(2000);
        t1.interrupt();
    }
}
