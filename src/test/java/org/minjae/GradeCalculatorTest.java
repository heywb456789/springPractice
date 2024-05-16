package org.minjae;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 학점계산기, 코스
 * 평균학점 계산 요청 ---> '학점계산기' ---> (학점수×교과목 평점)의 합계 ---> '코스'
 * ---> 수강신청 총학점 수
 * 일급 컬렉션 사용
 */
public class GradeCalculatorTest {
    //학점계산기 도메인 : 이수한 과목, 학점 계산기
    //도메인 : 이수한 과목, 학정
    //이수한 과목 : 객체지향 프로그래밍 , 자료구조론 , 중국어 회화
    // -> 동적인 객체 정적인 타입 추상화 -> 과목(코스) 클래스로 구현하기
    //이수한 과목을 전달하여 평균학점 계산 요청 -> 학점 계산기 -> (학점수 x 교과목 평점) -> 수강신청 총 학점 수


    @Test
    void calculateGradeTest() {
        List<Course> courses = List.of(new Course("oop", 3 , "A+")
        , new Course("자료구조", 3 , "A+"));

        GradeCalculator gradeCalculator = new GradeCalculator(courses);
        double gradeResult = gradeCalculator.calculateGrade();

        assertThat(gradeResult).isEqualTo(4.5);
    }
}
