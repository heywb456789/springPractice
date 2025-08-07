package ch1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ch1_11 {
    public static void main(String[] args) {
        String[] words = {"apple", "hi", "banana"};

        Function<String, Integer> strLength = s-> s.length();

        List<Integer> lengths = new ArrayList<>();

        for(String word : words){
            lengths.add(strLength.apply(word));
        }

        System.out.println(lengths);
    }
}
