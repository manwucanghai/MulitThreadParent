/**
 * @author zhengws
 * @date 2019-12-16 17:29
 */
public class IndexTest {
    public static void main(String[] args) {
        int size = 32;

        long start = System.currentTimeMillis();
        int last = 0;
        int num;
        for (int i = 0; i < 100000000; i++) {
            last += i;
//            System.out.println("%%: " + ((last + 1) % size));
//            System.out.println("&&: " + ((last + 1) & (size-1)));
//            System.out.println("##############");
            num = (last + 1) & size;
//            num = (last + 1) % size;
        }
        System.out.println("cost " + (System.currentTimeMillis() - start)+" ms");
    }
}
