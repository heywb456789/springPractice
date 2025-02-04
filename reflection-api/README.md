# Reflection API

Reflection API는 Java에서 제공하는 기능으로, 프로그램 실행 중에 클래스, 메서드, 필드 등의 정보를 동적으로 조회하고 조작할 수 있는 메커니즘. 
이를 통해 개발자는 객체의 메타데이터에 접근하고, 런타임에 객체를 생성하거나 메서드를 호출하는 등의 작업을 수행할 수 있습니다.

## 주요 기능

1. **클래스 정보 조회**: 클래스의 이름, 메서드, 필드, 생성자 등의 정보를 조회할 수 있습니다.
   ```java
   Class<?> clazz = Class.forName("com.example.MyClass");
   ```

2. **객체 생성**: 클래스 정보를 사용하여 새로운 객체를 생성할 수 있습니다.
   ```java
   Object obj = clazz.getDeclaredConstructor().newInstance();
   ```

3. **메서드 호출**: 특정 메서드를 동적으로 호출할 수 있습니다.
   ```java
   Method method = clazz.getMethod("myMethod", String.class);
   method.invoke(obj, "parameter");
   ```

4. **필드 접근**: 클래스의 필드에 접근하고 값을 읽거나 수정할 수 있습니다.
   ```java
   Field field = clazz.getDeclaredField("myField");
   field.setAccessible(true); // private 필드 접근 허용
   field.set(obj, "newValue");
   ```

5. **어노테이션 처리**: 클래스, 메서드, 필드에 적용된 어노테이션을 조회할 수 있습니다.
   ```java
   Annotation[] annotations = clazz.getAnnotations();
   ```

## 사용 예시

Reflection API는 주로 다음과 같은 경우에 사용됩니다:
- **프레임워크 및 라이브러리**: Spring, Hibernate와 같은 프레임워크는 Reflection을 사용하여 객체를 생성하고, 의존성을 주입하며, 메서드를 호출합니다.
- **테스트**: JUnit과 같은 테스트 프레임워크는 Reflection을 사용하여 테스트 메서드를 동적으로 호출합니다.
- **동적 프로그래밍**: 런타임에 클래스와 메서드를 동적으로 조작해야 하는 경우에 유용합니다.

## 단점

- **성능 저하**: Reflection은 일반적인 메서드 호출보다 느리며, 성능에 영향을 줄 수 있습니다.
- **타입 안전성**: 컴파일 타임에 타입 검사를 수행하지 않기 때문에, 런타임 오류가 발생할 수 있습니다.
- **보안 문제**: private 필드나 메서드에 접근할 수 있기 때문에, 보안상의 문제가 발생할 수 있습니다.

## 결론

Reflection API는 Java에서 매우 강력한 기능으로, 동적 프로그래밍을 가능하게 하지만, 사용 시 성능과 보안에 주의해야 합니다.