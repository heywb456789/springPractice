package org.minjae;

import java.util.List;

public class GradeCalculator {

    //1급 콜렉션 : 리스트 형태로 된  코스들의 정보만을 가지며 처리하는 기능을가진 클래스
    private final Courses courses;

//    private final List<Course> courses; // 1급 컬렉션 안으로 이동

    public GradeCalculator(List<Course> courses) {
        this.courses = new Courses(courses);
    }

    public double calculateGrade() {
            //1급 클래스로 이동
//        double mutipliedCrediAndCourseGrade = 0.0;
//        for(Course course : courses) {
////            mutipliedCrediAndCourseGrade += course.getCredit() * course.getGradeToNumber();
//            mutipliedCrediAndCourseGrade += course.multiplyCreditAndCourseGrade();
//        }
        double mutipliedCrediAndCourseGrade =  courses.multiplyCreditAndCourseGrade();


        //수강신청 총 학점 수
        int totalCompletedCredits = courses.calculateTotalCompletedCredit();
//        int totalCompletedCredits = courses.stream()
//                .mapToInt(Course::getCredit)
//                .sum();

        return mutipliedCrediAndCourseGrade / totalCompletedCredits;
    }
}
