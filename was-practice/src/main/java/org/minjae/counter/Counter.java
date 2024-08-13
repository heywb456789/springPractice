package org.minjae.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * packageName       : org.minjae.counter
 * fileName         : Counter
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class Counter implements Runnable {

    private Logger logger = LoggerFactory.getLogger(Counter.class);

    private int count = 0;

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public int getValue() {
        return count;
    }

    @Override
    public void run() {
        //동기화 처리 하지 않으면 상태를 공유하게 설계하면 Thread Safety 하지 않는다.
        synchronized (this) {
            this.increment();
            logger.info("Value for Thread After Increment : {} :: {}", Thread.currentThread().getName(), this.getValue());

            this.decrement();
            logger.info("Value for Thread After Decrement : {} :: {}", Thread.currentThread().getName(), this.getValue());
        }
    }
}
