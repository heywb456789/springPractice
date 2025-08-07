package ch1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ch1_21 {
    public static void main(String[] args) {
        List<Person> people = List.of(
                new Person("John", "M", 25),
                new Person("Alice", "F", 22),
                new Person("Bob", "M", 38),
                new Person("Jane", "F", 47),
                new Person("Tom", "M", 61)
        );

        Map<String, Map<String, List<String>>> list = people.stream()
                .collect(Collectors.groupingBy(
                        p->p.gender,
                        Collectors.groupingBy(
                                p -> {
                                    if(p.age >= 60) return "60대 이상";
                                    else return (p.age/10) * 10 + "대";
                                },
                                Collectors.mapping(p->p.name,Collectors.toList())
                        )
                ));
        System.out.println(list);
    }

    public static class Person{

        String name;
        String gender;
        int age;

        public Person(String name, String gender, int age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }
    }
}
