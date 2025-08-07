📌 1. 배열과 문자열의 구조 및 차이점

| 항목       | 배열                | 문자열                           |
| -------- | ----------------- | ----------------------------- |
| 구조       | 고정 길이 / 동일 자료형    | 문자(char) 배열로 표현됨              |
| 수정 가능 여부 | 가능 (mutable)      | 불가능 (Java의 String은 immutable) |
| 속도 측면    | 인덱스 접근 O(1)       | O(1) 접근 가능하지만 수정 시 새로 생성됨     |
| 사용 예시    | 정수 리스트, 인덱스 기반 탐색 | 이름, 태그, 메세지 등                 |

✅ 실전 팁: 문자열 수정이 많은 경우 → StringBuilder 사용 추천 (append, delete 등 O(1))

📌 2. 자주 쓰이는 자료구조

✅ HashMap, HashSet
* Key-Value 저장 구조
* O(1)에 가까운 조회/삽입/삭제 시간복잡도

Map<Character, Integer> map = new HashMap<>();
Set<String> unique = new HashSet<>();

활용 예시:
중복 문자 제거 / 등장 횟수 세기 / 쌍(pair) 존재 여부 판단

✅ ArrayList / LinkedList / Arrays

| 자료구조       | 특징                            |
| ---------- | ----------------------------- |
| ArrayList  | 동적 배열, 랜덤 접근 O(1), 삽입/삭제 O(n) |
| LinkedList | 연결 리스트, 삽입/삭제 O(1), 접근 O(n)   |
| Arrays     | 정렬, 이진탐색 등 유틸 제공              |

📌 3. 문자열 관련 유틸
```
String s = "abc";
s.charAt(i);         // 인덱스로 문자 접근
s.substring(i, j);   // 문자열 슬라이싱
s.equals("abc");     // 문자열 비교
s.toCharArray();     // 문자 배열 변환
s.split(" ");        // 공백 기준 나누기
```

📌 4. StringBuilder
* String은 불변 객체 → 잦은 수정 시 효율 낮음
* StringBuilder는 가변 객체
```
  StringBuilder sb = new StringBuilder();
  sb.append("a");
  sb.insert(1, "b");
  sb.reverse();
  sb.toString();
```

📌 5. 정렬
* Arrays.sort(arr) → 오름차순
* Arrays.sort(arr, Collections.reverseOrder()) → 내림차순
* 사용자 정의 정렬
```
Arrays.sort(arr, (a, b) -> a.length() - b.length());
```
📌 6. 투포인터 (Two Pointers)
정렬된 배열에서 특정 합 찾기, 중복 제거, 슬라이딩 윈도우 등에서 활용
```
int left = 0, right = arr.length - 1;
while (left < right) {
    int sum = arr[left] + arr[right];
    if (sum == target) ...
    else if (sum < target) left++;
    else right--;
}
```
📌 7. 슬라이딩 윈도우 (Sliding Window)
길이가 고정된 구간 합 / 최대값 찾기 등
```
int sum = 0, max = 0;
for (int i = 0; i < k; i++) sum += arr[i]; // 초기 윈도우
max = sum;

for (int i = k; i < arr.length; i++) {
    sum += arr[i] - arr[i-k]; // 한 칸 이동
    max = Math.max(max, sum);
}
```
📌 8. 빈도수 세기
```
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.put(c, freq.getOrDefault(c, 0) + 1);
}
```
📌 9. 예외 처리

| 예외 상황   | 처리 전략             |
| ------- | ----------------- |
| 인덱스 초과  | `arr.length` 체크   |
| Null 입력 | `if (s == null)`  |
| 대소문자 혼용 | `s.toLowerCase()` |


📌 10. 시간 복잡도 계산 팁

| 연산       | 복잡도        |
| -------- | ---------- |
| 배열 순회    | O(n)       |
| 정렬       | O(n log n) |
| 해시 삽입/조회 | O(1)       |
| 이중 반복문   | O(n²)      |
