package ch1;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 1. merge로 빈도수 Map 생성
 * 2. map.entrySet() 순회하면서
 *    - count > maxCount → result 갱신
 *    - count == maxCount && num < result → result 갱신
 * 3. 최종 result 반환
 */
public class ch1_9 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] inputs = sc.nextLine().split(" ");
        int[] arr = new int[inputs.length];
        for(int i=0;i<inputs.length;i++){
            arr[i] = Integer.parseInt(inputs[i]);
        }
        Map<Integer, Integer> map = checkMinNum(arr);
        int minFrequent = findMinFrequent(map);
        System.out.println(minFrequent);
    }

    private static int findMinFrequent(Map<Integer, Integer> map) {
        int maxCount = 0;
        int result = Integer.MAX_VALUE;

        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            int num = entry.getKey();
            int freq = entry.getValue();

            if(freq > maxCount){
                maxCount = freq;
                result = num;
            }else if(freq == maxCount && num < result){
                result = num;
            }
        }
        return result;
    }

    private static Map<Integer, Integer> checkMinNum(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();
        for(int i : arr){
            map.merge(i, 1, Integer::sum);
        }
        return map;
    }
}
