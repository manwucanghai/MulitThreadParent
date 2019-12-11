package com.zws.concurrent.thread.base.cyclic;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author zhengws
 * @date 2019-12-10 09:17
 */
public class CyclicDemo {
    private static class Soldier extends Thread {
        private String name;
        private CyclicBarrier cyclicBarrier;

        public Soldier(String name, CyclicBarrier cyclicBarrier) {
            this.name = name;
            this.cyclicBarrier = cyclicBarrier;
        }

        public void run() {
            try {
                //等待所有士兵到齐
                cyclicBarrier.await();
                doWork();
                //等待所有士兵完成任务
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        private void doWork() {
            ThreadUtils.sleep(1000);
            System.out.println(DateUtils.getNow() + name + ": 执行完任务.");
        }
    }

    private static class BarrierRun implements Runnable {
        private boolean isFinish = false;
        private int soldierNum;

        public BarrierRun(int soldierNum) {
            this.soldierNum = soldierNum;
        }

        public void run() {
            if (!isFinish) {
                System.out.println(DateUtils.getNow() + soldierNum + " 个士兵开始执行任务.");
                //将标识置位true.
                isFinish = true;
            } else {
                System.out.println(DateUtils.getNow() + soldierNum + " 个士兵执行任务完毕.");
            }
        }
    }

    public static void main(String[] args) {
        int num = 10;
        Soldier[] soldiers = new Soldier[num];
        BarrierRun barrierRun = new BarrierRun(num);
        CyclicBarrier barrier = new CyclicBarrier(num, barrierRun);
        for (int i = 0; i < soldiers.length; i++) {
            soldiers[i] = new Soldier("A"+i, barrier);
            soldiers[i].start();
        }

        /**
         * 输出：
         * 2019-12-11 15:04:04 10 个士兵开始执行任务.
         * 2019-12-11 15:04:05 A9: 执行完任务.
         * 2019-12-11 15:04:05 A0: 执行完任务.
         * 2019-12-11 15:04:05 A2: 执行完任务.
         * 2019-12-11 15:04:05 A5: 执行完任务.
         * 2019-12-11 15:04:05 A8: 执行完任务.
         * 2019-12-11 15:04:05 A6: 执行完任务.
         * 2019-12-11 15:04:05 A4: 执行完任务.
         * 2019-12-11 15:04:05 A1: 执行完任务.
         * 2019-12-11 15:04:05 A3: 执行完任务.
         * 2019-12-11 15:04:05 A7: 执行完任务.
         * 2019-12-11 15:04:05 10 个士兵执行任务完毕.
         */
    }
}
