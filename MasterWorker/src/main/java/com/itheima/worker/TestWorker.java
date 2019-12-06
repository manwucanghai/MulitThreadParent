package com.itheima.worker;

import com.itheima.task.Task;

/**
 * @author zhengws
 * @create 2019/4/3 21:35
 */
public class TestWorker extends Worker<Task, Integer> {
    @Override
    protected Integer handle(Task task) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return task.getId();
    }
}
