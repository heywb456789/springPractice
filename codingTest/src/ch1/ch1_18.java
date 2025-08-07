package ch1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_18 {
    public static void main(String[] args) {
        List<String> list = List.of("aa", "bbb", "cccc");

        Map<String,Integer> map = list.stream()
                .collect(Collectors.toMap(l->l , l->l.length()));
        System.out.println(map);
    }
}
