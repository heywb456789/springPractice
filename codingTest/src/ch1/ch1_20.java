package ch1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_20 {
    public static void main(String[] args) {
        List<String> list=List.of("apple", "apricot", "banana", "blueberry", "avocado");

        Map<String,String> map=list
                .stream()
                .collect(Collectors.toMap(
                        w-> w.substring(0,1).toLowerCase(),
                        w->w.toLowerCase(),
                        (v1,v2)->v1+","+v2
                ));
        System.out.println(map);
    }
}
