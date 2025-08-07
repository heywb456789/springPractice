package ch1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_16 {
    public static void main(String[] args) {
        List<String> list = List.of("a", "bb", "ccc", "dd", "e");

        Map<Integer,Long> result = list.stream()
                .collect(Collectors.groupingBy(String::length, Collectors.counting()));

        System.out.println(result);
    }
}
