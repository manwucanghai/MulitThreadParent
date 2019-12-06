package com.itheima;

import com.itheima.master.Master;
import com.itheima.task.Task;
import com.itheima.worker.TestWorker;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhengws
 * @create 2019/4/3 21:37
 */
public class Test {
    public static void main(String[] args) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("^^^^^^^^^^^^^^^^");
        Master<Task, Integer> master = new Master<Task, Integer>(new TestWorker(), Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 800 ; i++) {
            Task task = new Task(i, "task-"+i);
            master.submit(task);
        }
        master.execute();
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap<String, Integer> result = master.getResult();
        System.out.println("###### execute time "+ (System.currentTimeMillis() - startTime));
        System.out.println(result);
//        for (Map.Entry<String, Integer> entry : result.entrySet()){
//            System.out.println(entry.getValue());
//        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
