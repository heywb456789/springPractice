package ch1;

import java.util.*;

/**
 * ✅ 문제: 애너그램끼리 문자열 배열을 그룹으로 묶어라.
 *
 * ✅ 핵심 개념 정리:
 * - 애너그램 → 정렬하면 동일한 문자열이 됨 (예: tea → aet)
 * - 정렬된 문자열을 키로 HashMap에 그룹핑
 * - 최종적으로 Map의 value만 리스트로 추출
 *
 * ✅ 시간복잡도:
 * - O(n * m log m): n개의 문자열, 각 문자열 길이 m만큼 정렬
 *
 * ✅ 공간복잡도:
 * - O(n * m): Map에 모든 문자열 저장
 *
 * ✅ 확장 포인트:
 * 1. 정렬 대신 알파벳 빈도 기반 키 사용 가능 (a~z → 26자리 key)
 * 2. 대소문자 무시, 공백 제거 등 전처리 추가 가능
 * 3. 그룹의 개수 제한, 길이 제한 등 조건 추가 가능
 *
 * ✅ 실무 응용 예시:
 * - 해시 기반 키워드 클러스터링
 * - 단어 유사도 분석 / 사전 자동 정렬
 * - 로그, 에러 메시지 유사 템플릿 자동 분류
 */

public class ch1_7 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] input = sc.nextLine().split(" ");

        List<List<String>> groups = groupAnagrams(input);
        for(List<String> list : groups){
            System.out.println(list);
        }
    }

    /**
     *| 메서드                        | 설명                 |
     * | -------------------------- | ------------------ |
     * | `computeIfAbsent(k, f)`    | key 없으면 f로 생성      |
     * | `computeIfPresent(k, f)`   | key 있으면 f로 재계산     |
     * | `compute(k, f)`            | key 있든 없든 f로 계산    |
     * | `merge(k, v, f)`           | 기존 값과 새 값 병합       |
     * | `getOrDefault(k, default)` | key 없으면 default 리턴 |
     * | `forEach((k, v) -> ...)`   | 전체 반복 (람다식)        |
     */
    private static List<List<String>> groupAnagrams(String[] input) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : input) {
            char[] arr = s.toCharArray();
            Arrays.sort(arr);
            String key = new String(arr);

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }

}
