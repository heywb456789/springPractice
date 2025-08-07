package ch1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_19 {
    public static void main(String[] args) {
        List<String> list = List.of("a", "bb", "cat", "dog", "zebra");

        Map<Integer, List<String>> map = list.stream()
//                .filter(w-> w.length() > 2)
                .collect(Collectors.groupingBy(
                        l->l.length(),
                        Collectors.filtering(l->l.length() > 2, Collectors.toList())));

        System.out.println(map);
    }
}
