package org.minjae;

import java.util.List;

/**
 * packageName       : org.minjae
 * fileName         : Courses
 * author           : MinjaeKim
 * date             : 2024-06-21
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-06-21        MinjaeKim       최초 생성
 */
public class Courses {
    private final List<Course> courses;

    public Courses(List<Course> courses) {
        this.courses = courses;
    }


    public double multiplyCreditAndCourseGrade() {

        return courses.stream()
                .mapToDouble(Course::multiplyCreditAndCourseGrade)
                .sum();

//        double mutipliedCrediAndCourseGrade = 0.0;
//        for(Course course : courses) {
////            mutipliedCrediAndCourseGrade += course.getCredit() * course.getGradeToNumber();
//            mutipliedCrediAndCourseGrade += course.multiplyCreditAndCourseGrade();
//        }
//        return mutipliedCrediAndCourseGrade;
    }

    public int calculateTotalCompletedCredit() {
        return courses.stream()
                .mapToInt(Course::getCredit)
                .sum();
    }
}
