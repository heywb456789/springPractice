package ch1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ch1_12 {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Predicate<Integer> isEven = i -> i % 2 == 0;

        List<Integer>result = new ArrayList<>();

        for(Integer i : list){
            if(isEven.test(i)){
                result.add(i);
            }
        }
        System.out.println(result);
    }
}
