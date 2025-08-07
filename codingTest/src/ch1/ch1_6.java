package ch1;

import java.util.Scanner;
/**
 * ✅ 문제: 길이 k인 구간 중 평균이 가장 큰 값을 소수점 둘째 자리까지 구하라.
 *
 * ✅ 핵심 개념 정리:
 * - 슬라이딩 윈도우로 O(n) 시간에 k-길이 구간 합을 계산
 * - 평균은 (double)로 캐스팅 후 계산
 * - 소수점 반올림은 String.format("%.2f") 사용
 *
 * ✅ 시간복잡도: O(n)
 * ✅ 공간복잡도: O(1)
 *
 * ✅ 확장 포인트:
 * 1. 평균이 아닌 중앙값 → PriorityQueue + 윈도우 조정
 * 2. 구간이 가변적일 경우 → 투포인터 또는 DP 활용
 * 3. 소수점 비교가 많은 경우 → BigDecimal 사용 가능
 *
 * ✅ 실무 응용 예시:
 * - 실시간 사용자 수 평균 (시간 창 기준)
 * - CPU, 네트워크 평균 사용률 분석
 * - 주식 평균 등락률 계산
 */
public class ch1_6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input =  sc.nextLine().split(" ");
        int k = sc.nextInt();
        int[] arr = new int[input.length];
        for(int i =0 ; i<input.length; i++){
            arr[i] = Integer.parseInt(input[i]);
        }

        System.out.printf("%.2f%n",MaxAverage(arr, k));
    }

    private static double MaxAverage(int[] arr, int k) {
        int windowSum = 0;
        for(int i=0; i<k; i++){
            windowSum += arr[i];
        }
        int maxSum = windowSum;
        for(int i=k; i<arr.length; i++){
            windowSum += arr[i] - arr[i-k];
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum / (double)k;
    }
}
