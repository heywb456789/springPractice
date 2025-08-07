package ch1;

import java.util.Scanner;
/**
 * ✅ 문제: 길이 k인 연속 부분 배열 중 최대 합을 구하라.
 *
 * ✅ 핵심 개념 정리:
 * - 슬라이딩 윈도우: 고정된 구간을 하나씩 이동하며 합 계산
 * - 매번 전체 구간을 새로 합산하지 않고, 이전 합에서 한 항 빼고 한 항 더함
 *
 * ✅ 시간복잡도: O(n)
 * ✅ 공간복잡도: O(1)
 *
 * ✅ 확장 포인트:
 * 1. 최대 합이 아니라 최대 평균 → sum / k 로 응용 가능
 * 2. 구간이 가변적일 경우 → 투포인터 or DP 응용
 * 3. 구간 합 저장이 필요한 경우 → prefix sum 배열 활용
 *
 * ✅ 실무 응용 예시:
 * - 실시간 로그 데이터 평균 트래픽 계산
 * - 주식 가격 k일 평균 등락률 계산
 * - 메모리/네트워크 트렌드 분석에서 시간 창 기반 집계
 */
public class ch1_5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        int k = sc.nextInt();
        int[] arr = new int[input.length];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(input[i]);
        }

        System.out.println(maxSum(arr, k));
    }

    private static int maxSum(int[] arr, int k) {
        if (arr.length < k) return 0; // 예외 처리

        int windowSum = 0;
        // 초기 윈도우 구간 합
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }

        int maxSum = windowSum;

        // 슬라이딩 윈도우 이동
        for (int i = k; i < arr.length; i++) {
            windowSum += arr[i] - arr[i - k]; // 앞 하나 빼고, 뒤 하나 추가
            maxSum = Math.max(maxSum, windowSum);
        }

        return maxSum;
    }
}
