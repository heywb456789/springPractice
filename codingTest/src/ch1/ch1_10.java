package ch1;

import java.util.*;

public class ch1_10 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");

        Map<String, List<String>> map = wordsGroup(input);
        System.out.println(map);
    }

    private static Map<String, List<String>> wordsGroup(String[] input) {
        Map<String, List<String>> map = new HashMap<>();

        for (String word : input) {
            map.computeIfAbsent(String.valueOf(word.charAt(0)), k -> new ArrayList<>()).add(word);
        }
        return map;
    }
}
