package com.zws.concurrent.thread.base.pool;

import com.zws.concurrent.utils.DateUtils;
import com.zws.concurrent.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhengws
 * @date 2019-12-12 10:49
 */
public class ForkJoinPoolTask {

    private static class CountTask extends RecursiveTask<Long> {

        /**
         * 每次最大处理条数
         */
        private static final int THRESHOLD = 10000;
        /**
         * 起始数
         */
        private long start;
        /**
         * 终止数
         */
        private long end;

        public CountTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        protected Long compute() {
            if (end - start > THRESHOLD) {
                //进行拆分
                return splitTask(start, end);
            }
            //直接计算.
            return count(start, end);
        }

        /**
         * 拆分任务
         *
         * @param start
         * @param end
         */
        private Long splitTask(long start, long end) {
            AtomicLong sum = new AtomicLong();
            long splitNum = (end - start) / THRESHOLD + 1;
            long lastOne, firstOne;

            List<CountTask> tasks = new ArrayList<>();
            CountTask c;
            for (int i = 1; i <= splitNum; i++) {
                lastOne = start + i * THRESHOLD - 1;
                firstOne = start + (i - 1) * THRESHOLD;
                if (lastOne > end) {
                    lastOne = end;
                }
                c = new CountTask(firstOne, lastOne);
                tasks.add(c);
                // 注意添加完列表后，就立即进行fork(), 如果放到了join() 之前，则没有效果，原因待定
                c.fork();
            }
            tasks.forEach(task -> {
                sum.addAndGet(task.join());
            });
            return sum.get();
        }

        /**
         * 计算值.
         *
         * @param start
         * @param end
         * @return
         */
        private Long count(long start, long end) {
            ThreadUtils.sleep(1000);
            System.out.println(DateUtils.getNow() + Thread.currentThread().getName());
            long sum = 0;
            for (long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pools = new ForkJoinPool(2);
        CountTask task = new CountTask(1, 140000L);
        ForkJoinTask<Long> result = pools.submit(task);
        try {
            System.out.println("Sum = " + result.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 输出结果： 2-> 4-> 7 ..趋近于指数.
         * 2019-12-12 13:47:26 ForkJoinPool-1-worker-0
         * 2019-12-12 13:47:26 ForkJoinPool-1-worker-2
         *
         * 2019-12-12 13:47:27 ForkJoinPool-1-worker-3
         * 2019-12-12 13:47:27 ForkJoinPool-1-worker-0
         * 2019-12-12 13:47:27 ForkJoinPool-1-worker-2
         * 2019-12-12 13:47:27 ForkJoinPool-1-worker-4
         *
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-6
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-3
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-0
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-2
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-4
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-7
         * 2019-12-12 13:47:28 ForkJoinPool-1-worker-5
         *
         * 2019-12-12 13:47:29 ForkJoinPool-1-worker-6
         * Sum = 9800070000
         */
    }
}
