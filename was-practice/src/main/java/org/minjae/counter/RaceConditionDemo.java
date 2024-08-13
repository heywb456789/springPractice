package org.minjae.counter;

/**
 * packageName       : org.minjae.counter
 * fileName         : RaceConditionDemo
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class RaceConditionDemo {
    public static void main(String[] args) {
        Counter counter = new Counter();
        Thread t1 = new Thread(counter, "Tread-1");
        Thread t2 = new Thread(counter, "Tread-2");
        Thread t3 = new Thread(counter, "Tread-3");

        t1.start();
        t2.start();
        t3.start();
    }
}
