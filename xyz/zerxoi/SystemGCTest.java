package xyz.zerxoi;

public class SystemGCTest {
    public static void main(String[] args) {
        new SystemGCTest();
        System.gc();
        System.runFinalization();
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("SysemGCTest finalize");
    }
    
}