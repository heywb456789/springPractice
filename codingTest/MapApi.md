✅ Step 1. Java 8 Map API 완전정복
📌 1-1. computeIfAbsent

```map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);```
* 없으면 value 생성, 있으면 가져와서 그대로 사용
* 대표적 그룹핑 패턴

📌 1-2. merge

```map.merge("apple", 1, Integer::sum);```
* 기존 값이 있으면 remappingFunction을 실행
* 없으면 새 값 저장

```map.merge("apple", 1, (oldVal, newVal) -> oldVal + newVal);```

📌 1-3. computeIfPresent

```map.computeIfPresent("apple", (k, v) -> v + 10);```

* key가 존재할 경우에만 값을 업데이트
* 없어도 예외 없음

📌 1-4. getOrDefault

```int count = map.getOrDefault("apple", 0);```
* key 없을 경우 디폴트 반환

✅ Step 2. 함수형 인터페이스 기초
함수형 인터페이스 = 메서드 1개만 가진 인터페이스 
→ 람다식이나 메서드 참조로 전달 가능

| 인터페이스                 | 용도                 | 예시                    |
| --------------------- | ------------------ | --------------------- |
| `Function<T, R>`      | 입력 T → 출력 R        | `x -> x.length()`     |
| `BiFunction<T, U, R>` | 입력 T, U → 출력 R     | `(a, b) -> a + b`     |
| `Predicate<T>`        | 조건 검사 (boolean 반환) | `s -> s.isEmpty()`    |
| `Consumer<T>`         | 소비, 리턴 없음          | `System.out::println` |
| `Supplier<T>`         | 공급자, 인자 없음 → T 반환  | `() -> "Hello"`       |

🔍 예제
```
Function<String, Integer> lengthFunc = s -> s.length();
System.out.println(lengthFunc.apply("hello")); // 5
```

```
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(10)); // true
```

✅ Step 3. Stream + Map 조합
📌 3-1. 그룹핑 + 카운팅

``` 
Map<String, Long> counts = list.stream()
.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
```
-> 값의 등장 횟수를 Map으로 집계 

📌 3-2. 그룹핑 + 리스트
```
Map<String, List<String>> grouped = list.stream()
    .collect(Collectors.groupingBy(s -> s.substring(0, 1))); // 첫 글자 기준 그룹화
```

📌 3-3. 그룹핑 + 맵핑 + 정렬

```
Map<String, Set<Integer>> group = list.stream()
    .collect(Collectors.groupingBy(
        s -> s.substring(0, 1),
        Collectors.mapping(String::length, Collectors.toSet())
));
```
