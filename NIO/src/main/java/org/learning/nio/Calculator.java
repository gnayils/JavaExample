package org.learning.nio;

public class Calculator {

    public static char[] operatorChars = {'+', '-', '*', '/'};

    public static String cal(String expression) {
        for (char operator : operatorChars) {
            if (expression.indexOf(operator) > -1) {
                String[] operands = expression.split("[" + operator + "]");
                switch (operator) {
                    case '+':
                        return String.valueOf(Double.valueOf(operands[0]) + Double.valueOf(operands[1]));
                    case '-':
                        return String.valueOf(Double.valueOf(operands[0]) - Double.valueOf(operands[1]));
                    case '*':
                        return String.valueOf(Double.valueOf(operands[0]) * Double.valueOf(operands[1]));
                    case '/':
                        return String.valueOf(Double.valueOf(operands[0]) / Double.valueOf(operands[1]));
                }
            }
        }
        return "";
    }

    public static String generateRandom() {
        return String.format("%d%s%d", (int) (Math.random() * 1000), operatorChars[(int) (Math.random() * 4)], (int) (Math.random() * 1000));
    }

    public static void main(String[] args) {
        for(int i=0; i<10; i++) {
            System.out.println(generateRandom());
        }
    }
}
