package com.itheima.thread;

/**
 * @author zhengws
 * @create 2019/3/24 12:43
 */
public class ThreadGroupName implements Runnable {
    @Override
    public void run() {
        String groupAndName=Thread.currentThread().getThreadGroup().getName() + "-" + Thread.currentThread().getName();
        while (true){
            System.out.println("I am " + groupAndName);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup tg = new ThreadGroup("printGroup");
        Thread t1 = new Thread(tg, new ThreadGroupName(), "t1");
        t1.setDaemon(true);
        //如果用户线程全部结束，这也意味着这个程序实际上无事可做了。守护线程要守护的对象已经不存在了，那么整个应用程序就自然应该结束
//        Thread t2 = new Thread(tg, new ThreadGroupName(), "t2");
        t1.start();
//        t2.start();
        Thread.sleep(5000);
        System.out.println(tg.activeCount());
        tg.list();
    }
}
