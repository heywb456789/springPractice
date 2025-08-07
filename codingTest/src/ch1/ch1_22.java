package ch1;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ch1_22 {

    public static void main(String[] args) {

        List<Student> students = List.of(
                new Student("Kim", 1, List.of("Math", "English")),
                new Student("Lee", 1, List.of("Science")),
                new Student("Park", 2, List.of("Math", "Art")),
                new Student("Choi", 2, List.of("English", "Art"))
        );

        Map<Integer, Set<String>> result = students.stream()
            .collect(Collectors.groupingBy(
                    p-> p.grade,
                    Collectors.flatMapping(
                            student -> student.subjects.stream(),
                            Collectors.toSet()
                    )
            ))        ;
        System.out.println(result);
    }

    public static class Student {
        String name;
        int grade;
        List<String> subjects;

        public Student(String name, int grade, List<String> subjects) {
            this.name = name;
            this.grade = grade;
            this.subjects = subjects;
        }
    }
}
