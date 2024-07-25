package org.minjae.calculator;

import java.util.List;

public class Calculator {

    private static final  List<NewArithmaticOperator>NEW_ARITHMATIC_OPERATOR_LIST
            = List.of(new AdditionOperator(), new SubtractionOperator(), new DivisionOperator(), new MultipleOperator());

    public static int calculate(PositiveNumber operand1, String operator, PositiveNumber operand2 ) {
//        return ArithmaticOperator.calculator(operand1, operator, operand2);//enum
        return NEW_ARITHMATIC_OPERATOR_LIST.stream()
                .filter(operatorList -> operatorList.supports(operator))
                .map(operatorList ->operatorList.calculate(operand1, operand2))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("No such operator: " + operator));
    }
}
