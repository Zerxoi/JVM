public class Test {
    public static void main(String args[]) {
        A[] as = new A[10];
    }
}

class A {
    public static int x = 100;
    static {
        System.out.println("静态初始化A");
    }
}
