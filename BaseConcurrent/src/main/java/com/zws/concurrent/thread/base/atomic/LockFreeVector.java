package com.zws.concurrent.thread.base.atomic;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 补充基本知识：
 * >>> 无论最高位符号位是0，还是1，空缺位补0
 * >> 被位移的二进制最高位如果为1，则右移后空缺位补1，如果最高位为0，则右移后空缺位补0.
 * ^ 异或，相同为0，不同为1
 * 0x80000000 为Integer.MAX_VALUE + 1 也就是最高位为1，其他31位全补0.
 * Integer.numberOfLeadingZeros(num) 获取高位补0个数。 例如10，则二进制为1010，其补零数为 32 - 4 = 28.
 *
 * @author zhengws
 * @date 2019-12-16 11:12
 */
public class LockFreeVector<E> {

    private static final Integer N_BUCKET = 30;
    private static final Integer FIRST_BUCKET_SIZE = 8;
    private final AtomicReferenceArray<AtomicReferenceArray<E>> buckets;
    private final AtomicReference<Descriptor<E>> descriptor;
    private final int zeroNumFirst = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE);

    public LockFreeVector() {
        this.buckets = new AtomicReferenceArray<>(N_BUCKET);
        this.buckets.set(0, new AtomicReferenceArray<>(FIRST_BUCKET_SIZE));
        descriptor = new AtomicReference<>(new Descriptor<>(0, null));
    }

    private static class WriteDescriptor<E> {
        public E oldV;
        public E newV;
        public AtomicReferenceArray<E> addr;
        public int addr_ind;

        public WriteDescriptor(AtomicReferenceArray<E> addr, int addr_ind, E oldV, E newV) {
            this.oldV = oldV;
            this.newV = newV;
            this.addr = addr;
            this.addr_ind = addr_ind;
        }

        public void doIt() {
            addr.compareAndSet(addr_ind, oldV, newV);
        }
    }

    private static class Descriptor<E> {
        public int size;
        public volatile WriteDescriptor<E> writeDescriptor;

        public Descriptor(int size, WriteDescriptor<E> writeDescriptor) {
            this.size = size;
            this.writeDescriptor = writeDescriptor;
        }

        public void completeWrite() {
            WriteDescriptor<E> tmpWriteDescriptor = writeDescriptor;
            if (tmpWriteDescriptor != null) {
                tmpWriteDescriptor.doIt();
                writeDescriptor = null;
            }
        }
    }

    /**
     * 核心思想：
     * 以 descriptor 所引用的对象来确保一致性，如果不一致则进行
     * @param e
     */
    public void add(E e) {
        Descriptor<E> desc;
        Descriptor<E> newDesc;
        do {
            /**
             * 1.记录最后一次的引用.
             */
            desc = descriptor.get();
            /**
             * 2.获取当前的位置信息，并计算应该放到第几个数组。
             * 巧妙设计：
             *   1. zeroNumFirst为FIRST_BUCKET_SIZE的高位补码数
             *   2. 求解pos的补码，因为数组扩展个数是基于上一个数组长度再扩容一倍，因此借用补码差来计算所处的第几个数组。
             */
            int pos = desc.size + FIRST_BUCKET_SIZE;
            int zeroNumPos = Integer.numberOfLeadingZeros(pos);
            int bucketInd = zeroNumFirst - zeroNumPos;
            // 自动扩容.
            if (buckets.get(bucketInd) == null) {
                int newLen = buckets.get(bucketInd - 1).length() << 1;
                buckets.compareAndSet(bucketInd, null, new AtomicReferenceArray<>(newLen));
            }
            /**
             * 3.计算内层数组的位置。
             */
            int idx = (0x80000000 >>> zeroNumPos) ^ pos;
            System.out.println("pos: " + pos + ", zeroNumPos: " + zeroNumPos + ", bucketInd: " + bucketInd + ", idx: " + idx);
            newDesc = new Descriptor<>(desc.size + 1, new WriteDescriptor<>(buckets.get(bucketInd), idx, null, e));

        } while (!descriptor.compareAndSet(desc, newDesc));

        descriptor.get().completeWrite();
    }

    public E get(int index) {
        int pos = index + FIRST_BUCKET_SIZE;
        int zeroNumPos = Integer.numberOfLeadingZeros(pos);
        int bucketInd = zeroNumFirst - zeroNumPos;
        int idx = (0x80000000 >>> zeroNumPos) ^ pos;
        return buckets.get(bucketInd).get(idx);
    }

    public static void main(String[] args) {
        LockFreeVector<String> vector = new LockFreeVector<>();
        for (int i = 0; i < 57; i++) {
            vector.add("addd" + i);
        }
        System.out.println("######################");
        for (int i = 0; i < 57; i++) {
            System.out.println(vector.get(i));
        }

        /**
         * 输出：
         * pos: 8, zeroNumPos: 28, bucketInd: 0, idx: 0
         * pos: 9, zeroNumPos: 28, bucketInd: 0, idx: 1
         * pos: 10, zeroNumPos: 28, bucketInd: 0, idx: 2
         * pos: 11, zeroNumPos: 28, bucketInd: 0, idx: 3
         * pos: 12, zeroNumPos: 28, bucketInd: 0, idx: 4
         * pos: 13, zeroNumPos: 28, bucketInd: 0, idx: 5
         * pos: 14, zeroNumPos: 28, bucketInd: 0, idx: 6
         * pos: 15, zeroNumPos: 28, bucketInd: 0, idx: 7
         * pos: 16, zeroNumPos: 27, bucketInd: 1, idx: 0
         * pos: 17, zeroNumPos: 27, bucketInd: 1, idx: 1
         * pos: 18, zeroNumPos: 27, bucketInd: 1, idx: 2
         * pos: 19, zeroNumPos: 27, bucketInd: 1, idx: 3
         * pos: 20, zeroNumPos: 27, bucketInd: 1, idx: 4
         * pos: 21, zeroNumPos: 27, bucketInd: 1, idx: 5
         * pos: 22, zeroNumPos: 27, bucketInd: 1, idx: 6
         * pos: 23, zeroNumPos: 27, bucketInd: 1, idx: 7
         * pos: 24, zeroNumPos: 27, bucketInd: 1, idx: 8
         * pos: 25, zeroNumPos: 27, bucketInd: 1, idx: 9
         * pos: 26, zeroNumPos: 27, bucketInd: 1, idx: 10
         * pos: 27, zeroNumPos: 27, bucketInd: 1, idx: 11
         * pos: 28, zeroNumPos: 27, bucketInd: 1, idx: 12
         * pos: 29, zeroNumPos: 27, bucketInd: 1, idx: 13
         * pos: 30, zeroNumPos: 27, bucketInd: 1, idx: 14
         * pos: 31, zeroNumPos: 27, bucketInd: 1, idx: 15
         * pos: 32, zeroNumPos: 26, bucketInd: 2, idx: 0
         * pos: 33, zeroNumPos: 26, bucketInd: 2, idx: 1
         * pos: 34, zeroNumPos: 26, bucketInd: 2, idx: 2
         * pos: 35, zeroNumPos: 26, bucketInd: 2, idx: 3
         * pos: 36, zeroNumPos: 26, bucketInd: 2, idx: 4
         * pos: 37, zeroNumPos: 26, bucketInd: 2, idx: 5
         * pos: 38, zeroNumPos: 26, bucketInd: 2, idx: 6
         * pos: 39, zeroNumPos: 26, bucketInd: 2, idx: 7
         * pos: 40, zeroNumPos: 26, bucketInd: 2, idx: 8
         * pos: 41, zeroNumPos: 26, bucketInd: 2, idx: 9
         * pos: 42, zeroNumPos: 26, bucketInd: 2, idx: 10
         * pos: 43, zeroNumPos: 26, bucketInd: 2, idx: 11
         * pos: 44, zeroNumPos: 26, bucketInd: 2, idx: 12
         * pos: 45, zeroNumPos: 26, bucketInd: 2, idx: 13
         * pos: 46, zeroNumPos: 26, bucketInd: 2, idx: 14
         * pos: 47, zeroNumPos: 26, bucketInd: 2, idx: 15
         * pos: 48, zeroNumPos: 26, bucketInd: 2, idx: 16
         * pos: 49, zeroNumPos: 26, bucketInd: 2, idx: 17
         * pos: 50, zeroNumPos: 26, bucketInd: 2, idx: 18
         * pos: 51, zeroNumPos: 26, bucketInd: 2, idx: 19
         * pos: 52, zeroNumPos: 26, bucketInd: 2, idx: 20
         * pos: 53, zeroNumPos: 26, bucketInd: 2, idx: 21
         * pos: 54, zeroNumPos: 26, bucketInd: 2, idx: 22
         * pos: 55, zeroNumPos: 26, bucketInd: 2, idx: 23
         * pos: 56, zeroNumPos: 26, bucketInd: 2, idx: 24
         * pos: 57, zeroNumPos: 26, bucketInd: 2, idx: 25
         * pos: 58, zeroNumPos: 26, bucketInd: 2, idx: 26
         * pos: 59, zeroNumPos: 26, bucketInd: 2, idx: 27
         * pos: 60, zeroNumPos: 26, bucketInd: 2, idx: 28
         * pos: 61, zeroNumPos: 26, bucketInd: 2, idx: 29
         * pos: 62, zeroNumPos: 26, bucketInd: 2, idx: 30
         * pos: 63, zeroNumPos: 26, bucketInd: 2, idx: 31
         * pos: 64, zeroNumPos: 25, bucketInd: 3, idx: 0
         * ######################
         * addd0
         * addd1
         * addd2
         * addd3
         * addd4
         * addd5
         * addd6
         * addd7
         * addd8
         * addd9
         * addd10
         * addd11
         * addd12
         * addd13
         * addd14
         * addd15
         * addd16
         * addd17
         * addd18
         * addd19
         * addd20
         * addd21
         * addd22
         * addd23
         * addd24
         * addd25
         * addd26
         * addd27
         * addd28
         * addd29
         * addd30
         * addd31
         * addd32
         * addd33
         * addd34
         * addd35
         * addd36
         * addd37
         * addd38
         * addd39
         * addd40
         * addd41
         * addd42
         * addd43
         * addd44
         * addd45
         * addd46
         * addd47
         * addd48
         * addd49
         * addd50
         * addd51
         * addd52
         * addd53
         * addd54
         * addd55
         * addd56
         */
    }
}
