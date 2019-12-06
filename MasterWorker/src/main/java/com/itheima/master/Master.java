package com.itheima.master;

import com.itheima.worker.Worker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhengws
 * @create 2019/4/3 21:22
 */
public class Master<T, E> {
    private ConcurrentLinkedQueue<T> workQueue = new ConcurrentLinkedQueue<T>();

    private HashMap<String, Thread> workers = new HashMap<String, Thread>();

    private ConcurrentHashMap<String, E> resultMap = new ConcurrentHashMap<String, E>();

    private Worker worker;

    public Master(Worker worker , int workerCount){
        this.worker = worker;
        worker.setWorkQueue(this.workQueue);
        worker.setResultMap(this.resultMap);

        for(int i = 0; i < workerCount; i ++){
            this.workers.put("Thread-"+i, new Thread(worker));
        }
    }

    public void submit(T task){
        this.workQueue.add(task);
    }

    public void execute(){
        worker.setCountDownLatch(new CountDownLatch(workQueue.size()));
        for(Map.Entry<String, Thread> me : workers.entrySet()){
            me.getValue().start();
        }
    }


    public ConcurrentHashMap<String, E> getResult(){
        try {
            worker.getCountDownLatch().await();
            return this.resultMap;
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
