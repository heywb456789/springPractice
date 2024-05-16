package org.minjae.calculator;

public class DivisionOperator implements NewArithmaticOperator{
    @Override
    public boolean supports(String operator) {
        return "/".equals(operator);
    }

    @Override
    public int calculate(PositiveNumber operand1, PositiveNumber operand2) {
//        if(operand2.toInt() == 0 ){
//            throw new IllegalArgumentException("Division by zero");
//        } //PositiveNumber 로 작성하였기 때문에 더이상 Validation 할 필요가 없다.
        return operand1.toInt() / operand2.toInt();
    }
}
