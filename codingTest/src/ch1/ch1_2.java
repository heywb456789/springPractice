package ch1;

import java.util.Scanner;
import java.util.TreeSet;
/**
 * ✅ 문제: 문자열에서 중복된 문자를 제거하고 사전순으로 정렬하여 반환하라.
 *
 * ✅ 핵심 개념 정리:
 * - TreeSet은 자동 정렬되는 Set 자료구조 (Red-Black Tree 기반)
 * - 중복 문자를 자동 제거하며, 삽입 시마다 정렬을 유지
 * - Set을 순회하면 정렬된 상태로 출력됨
 *
 * ✅ 시간복잡도:
 * - O(n log n): TreeSet 삽입 시 log n → 전체 n개의 문자 삽입
 *
 * ✅ 공간복잡도:
 * - O(n): TreeSet 및 결과 문자열 저장용 StringBuilder 사용
 *
 * ✅ 확장 포인트:
 * 1. 내림차순 정렬: new TreeSet<>(Collections.reverseOrder())
 * 2. 사용자 정의 정렬: Comparator 이용
 * 3. 한글/숫자/기호 등도 정렬 가능 (유니코드 기준)
 * 4. 실시간 정렬이 필요한 경우에도 유용 (예: 랭킹 시스템)
 *
 * ✅ 실무 응용 예시:
 * - 자동 태그 정렬 (중복 제거 + 가나다순)
 * - 사용자 입력에서 유니크 키워드 추출 + 정렬
 * - 등급, 점수 등 실시간 정렬 보장하는 데이터 처리
 */
public class ch1_2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();

        System.out.println(treeSet(line));
    }

    private static String treeSet(String line) {
        TreeSet<Character> treeSet = new TreeSet<>();

        for (char c : line.toCharArray()) {
            treeSet.add(c); // TreeSet이 중복 제거 + 자동 정렬 수행
        }

        StringBuilder sb = new StringBuilder();
        for (char c : treeSet) {
            sb.append(c);
        }

        return sb.toString();
    }
}
