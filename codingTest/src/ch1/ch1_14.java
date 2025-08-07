package ch1;

import java.util.Random;
import java.util.function.Supplier;

public class ch1_14 {
    public static void main(String[] args) {
        Supplier<Integer> random = () -> new Random().nextInt();

        for (int i = 0; i < 10; i++) {
            System.out.println(random.get());
        }
    }
}
