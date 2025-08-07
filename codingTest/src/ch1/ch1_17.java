package ch1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ✅ groupingBy와 mapping 개념 차이
 * 🔹 Collectors.groupingBy
 * 스트림 요소를 "어떤 기준(key)"으로 그룹핑하여 Map으로 반환합니다.
 *
 * Collectors.mapping
 * groupingBy가 만든 그룹 내에서 "값(value)"을 변환해서 수집할 때 사용합니다.
 *
 */
public class ch1_17 {
    public static void main(String[] args) {
        List<String> list = List.of("Alice", "Amy", "Bob", "alex", "Brandon");

        Map<String,List<String>> map = list.stream()
                .collect(Collectors
                        .groupingBy(l-> l.substring(0,1).toLowerCase(),
                                Collectors.mapping(l->l.toLowerCase(),
                                        Collectors.toList())));

        System.out.println(map);
    }
}
