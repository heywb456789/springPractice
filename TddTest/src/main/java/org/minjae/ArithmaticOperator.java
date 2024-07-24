package org.minjae;

import org.minjae.calculator.PositiveNumber;

import java.util.Arrays;

public enum ArithmaticOperator {
    ADDITION("+") {
        @Override
        public int calc(int a, int b) {
            return a+b;
        }
    },
    SUBSTRACTION("-") {
        @Override
        public int calc(int a, int b) {
            return a-b;
        }
    },
    MULTIPLE("*") {
        @Override
        public int calc(int a, int b) {
            return a*b;
        }
    },
    DIVISION("/") {
        @Override
        public int calc(int a, int b) {
            return a/b;
        }
    };

    private final String op;

    ArithmaticOperator(String op) {
        this.op = op;
    }

    //추상메서드
    protected abstract int calc(final int a, final int b);

    public static int calculator(PositiveNumber operand1, String operator, PositiveNumber operand2) {
        ArithmaticOperator arithmaticOperator = Arrays.stream(values())
                .filter(v-> v.op.equals(operator))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("Invalid operator: " + operator));

        return arithmaticOperator.calc(operand1.toInt(), operand2.toInt());
    }
}
