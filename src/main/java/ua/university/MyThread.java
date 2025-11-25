package ua.university;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.lang.Thread.sleep;

public class MyThread extends Thread {

//    public MyThread(Runnable target){
//        super(target);
//    }

    public void run() {
        try {
            sleep(Duration.of(3, ChronoUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Hello from " + Thread.currentThread().getName() + " in class MyThread");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        MyThread2 mth2 = new MyThread2();
        Thread mth = new Thread(mth2);
        mth.run();
        mth.start();
//        mth.join();
        mth.run();
//        mth.start();
        System.out.println("end");
    }

}

class MyThread2 implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello from " + Thread.currentThread().getName() + " in class MyThread2");
    }
}
