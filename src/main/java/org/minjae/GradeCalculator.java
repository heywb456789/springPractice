package org.minjae;

import java.util.List;

public class GradeCalculator {

    private final List<Course> courses;

    public GradeCalculator(List<Course> courses) {
        this.courses = courses;
    }

    public double calculateGrade() {
        double mutipliedCrediAndCourseGrade = 0.0;
        for(Course course : courses) {
            mutipliedCrediAndCourseGrade += course.getCredit() * course.getGradeToNumber();
        }

        //수강신청 총 학점 수
        int totalCompletedCredits = courses.stream()
                .mapToInt(Course::getCredit)
                .sum();

        return mutipliedCrediAndCourseGrade / totalCompletedCredits;
    }
}
