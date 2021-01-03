package xyz.zerxoi;

public class StaticField {
    static Object staticObject = new Object();
    Object instanceObject = new Object();

    public static void main(String[] args) {
        String s0 = "a";
        String s1 = "b";
        String s2 = "ab";
        String s3 = "a" + "b";
        String s4 = s0 + "b";
        String s5 = "a" + s1;
        String s6 = s0 + s1;
        String s7 = s6.intern();

        System.out.println(s2 == s3); // true

        System.out.println(s2 == s4); // false
        System.out.println(s2 == s5); // false
        System.out.println(s2 == s6); // false

        System.out.println(s4 == s5); // false
        System.out.println(s5 == s6); // false
        System.out.println(s4 == s6); // false

        System.out.println(s2 == s7); // true
        StringBuilder sb = new StringBuilder();
        sb.append(s0).append(s1);
    }
}
