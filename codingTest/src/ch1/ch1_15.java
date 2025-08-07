package ch1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_15 {
    public static void main(String[] args) {
        List<String> words = List.of("a", "bb", "ccc","dd","e");

        Map<Integer, List<String>> result = words.stream().collect(Collectors.groupingBy(
                word-> word.length()
        ));
        System.out.println(result);
    }
}
