package ch1;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.function.Consumer;

public class ch1_13 {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Consumer<Integer> printer = n -> System.out.println("number: " + n);

        list.forEach(printer);
    }
}
