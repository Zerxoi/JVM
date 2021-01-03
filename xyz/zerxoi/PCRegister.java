package xyz.zerxoi;

public class PCRegister {
    static final int MAX_COUNT = 10_000_000;
    static final String[] arr = new String[MAX_COUNT];

    public static void main(String[] args) {
        Integer[] data = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MAX_COUNT; i++) {
            // arr[i] = String.valueOf(data[i % data.length]);
            arr[i] = String.valueOf(data[i % data.length]).intern();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}
