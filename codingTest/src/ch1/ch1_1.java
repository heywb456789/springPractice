package ch1;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ch1_1 {
    /**
     * ✅ 문제: 문자열에서 중복된 문자를 제거하고 처음 등장한 순서대로 반환하라.
     *
     * ✅ 핵심 개념 정리:
     * - HashSet: 중복 여부를 O(1)에 판별하기 위한 자료구조
     * - StringBuilder: String은 immutable(불변)하므로, 문자열 누적 시 성능을 위해 StringBuilder 사용
     * - char 순회: 문자열을 문자 단위로 순회할 때는 toCharArray() 사용
     * - 순서 보존: 입력 문자열의 등장 순서를 유지하기 위해 탐색 순서를 바꾸지 않음
     *
     * ✅ 시간복잡도:
     * - O(n): 문자열의 길이만큼 1회 순회
     *
     * ✅ 공간복잡도:
     * - O(n): 최대 모든 문자가 유일한 경우 HashSet 및 StringBuilder에 n개 저장
     *
     * ✅ 확장 포인트:
     * 1. 대소문자 구분 없이 처리하고 싶을 경우: 입력 문자열을 toLowerCase() 또는 toUpperCase()로 통일
     * 2. 결과를 사전순으로 정렬하고 싶을 경우: HashSet → TreeSet으로 교체하여 자동 정렬
     * 3. 한글/이모지 등 유니코드 복합 문자 대응: char 단위가 아닌 codePoint 기반으로 처리
     * 4. 중복 제거가 아닌 "중복 제거 + 정렬된 순서로 출력" → HashSet + 정렬 리스트 조합 필요
     *
     * ✅ 실무 응용 예시:
     * - 사용자 태그 입력 시 중복 키워드 제거
     * - 자동완성/추천 기능에서 중복 입력 제거
     * - 회원가입 시 닉네임의 중복 문자 자동 필터링
     */

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        System.out.println(removeDuplicates(str));
    }

    private static String removeDuplicates(String str) {
        Set<Character> set = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for(char c : str.toCharArray()){
            if(!set.contains(c)){
                set.add(c);
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
