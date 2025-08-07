package ch1;

import java.util.Scanner;

/**
 * ✅ 문제: 정렬된 배열에서 두 수의 합이 target이 되는 쌍을 찾아라.
 *
 * ✅ 핵심 개념 정리:
 * - 투포인터 알고리즘: 정렬된 배열에서 양끝에서 시작하여 합 비교
 * - 합이 작으면 왼쪽 포인터 증가, 크면 오른쪽 포인터 감소
 * - O(n) 시간에 모든 가능한 쌍 탐색 가능
 *
 * ✅ 시간복잡도: O(n)
 * ✅ 공간복잡도: O(1) (포인터 2개만 사용)
 *
 * ✅ 확장 포인트:
 * 1. target이 여러 쌍일 경우 → List<List<Integer>> 로 확장 가능
 * 2. 합이 아닌 곱, 차이 등 다른 연산 적용도 가능
 * 3. 정렬되지 않은 배열에서는 → HashMap 방식으로 대체 필요 (O(n))
 *
 * ✅ 실무 응용 예시:
 * - 가격 조합 중 특정 예산에 맞는 상품 찾기
 * - 양방향 스크롤 가능한 데이터 처리
 * - 추천 알고리즘에서 유사도 기준 쌍 매칭
 */
public class ch1_4 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");
        int target = sc.nextInt();

        int[] arr = new int[input.length];
        for(int i=0;i<input.length;i++){
            arr[i] = Integer.parseInt(input[i]);
        }

        int[] result = findTowpointer(arr, target);

        if(result == null){
            System.out.println("None");
        }else {
            System.out.printf("[%d, %d]",result[0],result[1]);
        }
    }

    private static int[] findTowpointer(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while(left < right){
            int sum = arr[left] + arr[right];
            if(sum == target){
                return new int[]{arr[left], arr[right]};
            }else if(sum < target){
                left++;
            }else{
                right--;
            }
        }
        return null;
    }
}
