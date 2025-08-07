package ch1;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ch1_3 {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        System.out.println(solution(line));
    }

    private static char solution(String line) {
        Map<Character, Integer> map = new HashMap<>();
        int maxCount = 0;
        char result = ' ';

        for(Character ch : line.toCharArray()){
            map.put(ch, map.getOrDefault(ch, 0)+1);

            int count = map.get(ch);

            if(count > maxCount){
                maxCount = count;
                result = ch;

            }else if(count == maxCount && ch < result){
                //문자열은 부등호로 비교 가능이므로
                result = ch;
            }
        }
        return result;
    }
}
