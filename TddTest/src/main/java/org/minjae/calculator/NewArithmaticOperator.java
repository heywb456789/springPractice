package org.minjae.calculator;

public interface NewArithmaticOperator {

    boolean supports(String operator);

    int calculate(PositiveNumber operand1, PositiveNumber operand2);


}
