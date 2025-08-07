package ch1;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**

 ✅ 문제: 정수 배열에서 각 수의 등장 횟수를 Map으로 세기

 ✅ 전략:

 HashMap<Integer, Integer>을 사용하여 카운팅

 getOrDefault()를 활용해 초기값을 0으로 대체

 ✅ 시간복잡도: O(n)

 ✅ 공간복잡도: O(n)

 ✅ 확장:

 Java 8 merge()로도 구현 가능

 문자, 문자열 등 다른 타입에도 그대로 적용 가능

 가장 많이 등장한 값 찾기 → 정렬 or PriorityQueue 추가
 */
public class ch1_8 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        int[] arr = new int[input.length];
        for(int i=0;i<input.length;i++){
            arr[i]  = Integer.parseInt(input[i]);
        }
        Map<Integer, Integer> map = solution(arr);

        System.out.println(map);
    }

    private static Map<Integer, Integer> solution(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();

//        for(Integer i:arr){
//
//            map.put(i, map.getOrDefault(i, 0) + 1);
//        }

        for(int i : arr){
            map.merge(i, 1, Integer::sum);
        }
        return map;
    }
}
