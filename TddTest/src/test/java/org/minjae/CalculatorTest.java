package org.minjae;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.minjae.calculator.PositiveNumber;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CalculatorTest {

    @DisplayName("덧셈 ")
    @ParameterizedTest
    @MethodSource("param")
    void additionTest(PositiveNumber op1, String operator, PositiveNumber op2, int expected) {
        int calculate = Calculator.calculate(op1, operator, op2); //Enum 방식 refactor


        assertThat(calculate).isEqualTo(expected);
    }

    private static Stream<Arguments> param() {
        return Stream.of(
                arguments(1, "+", 2, 3),
                arguments(1, "-", 2, -1),
                arguments(1, "*", 2, 2),
                arguments(4, "/", 2, 2)
        );

    }

    @DisplayName("뺄셈")
    @Test
    void exceptionTest() {
        assertThatCode(()->Calculator.calculate(new PositiveNumber(10), "/", new PositiveNumber(0)))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
