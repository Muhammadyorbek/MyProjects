package com.calculator.game;

import java.math.BigInteger;

public class Calculator  {
    final CalculatorArray calculatorArray = new CalculatorArray();
    public double sum(double d1, double d2) {return d1 + d2;}
    public double multiply(double d1, double d2) {return d1 * d2;}
    public double divide(double d1, double d2) {return d1 / d2;}
    public double subtract(double d1, double d2) {return d1 - d2;}
    public BigInteger factorial(int i1) {
        if (i1 >= 10000) return BigInteger.valueOf(0);
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= i1; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
    public double absoluteValue(double d1) {
        if (d1 >= 0) return d1;
        else return (-1) * d1;
    }
    public double minusValue(double d1) {return (-1.0) * d1;}
    public double power(double d1, double d2) {return Math.pow(d1, d2);}
    public String operation(String input) {
        if (input.charAt(0) == '0' && input.charAt(1) == '0') return "0";
        if (input == null) return "";
        String operation = "";
        int operationIndex = -1;
        for (int i = 1; i < input.length(); i++) {
             if (String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[7]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[11]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[15]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[19]) || String.valueOf(input.charAt(i)).equals("^")) {
                operation = String.valueOf(input.charAt(i));
                operationIndex = i;
                break;
            }
        }
        if (operationIndex == -1) return input;
        String number1 = input.substring(0, operationIndex);
        String number2 = input.substring(operationIndex + 1);
        if (number1.isEmpty() || number2.isEmpty()) return input;
        double d1, d2;
        double resultNumber = 0.0;
        d1 = Double.parseDouble(number1);
        d2 = Double.parseDouble(number2);
        switch (operation) {
            case "+":
                resultNumber = sum(d1, d2);
                break;
            case "-":
                resultNumber = subtract(d1, d2);
                break;
            case "X":
                resultNumber = multiply(d1, d2);
                break;
            case "/":
                if (d2 == 0.0) return "0";
                resultNumber = divide(d1, d2);
                break;
            case "^":
                resultNumber = power(d1, d2);
                break;
        }
        if (resultNumber * 10 == (int)(resultNumber) * 10) {
            int integer = (int)(resultNumber);
            return String.valueOf(integer);
        }
        else {
            return String.valueOf(resultNumber);
        }
    }
//    public String operation(String input) {
//        if (input.charAt(0) == '0' && input.charAt(1) == '0') return "0";
//        if (input == null) return "";
//        ArrayList<String> operations = new ArrayList<>();
//        boolean hasOperation = false;
//        ArrayList<Integer> operationIndex = new ArrayList<>();
//        for (int i = 1; i < input.length(); i++) {
//             if (String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[7]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[11]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[15]) || String.valueOf(input.charAt(i)).equals(calculatorArray.atIndex[19]) || String.valueOf(input.charAt(i)).equals("^")) {
//                operations.add(String.valueOf(input.charAt(i)));
//                operationIndex.add(i);
//                hasOperation = true;
//                break;
//            }
//        }
//        if (!hasOperation) return input;
//        ArrayList<String> numbers = new ArrayList<>();
//        int a = 0, b;
//        for (int i = 0; i < operationIndex.size(); i++) {
//            b = operationIndex.get(i);
//            numbers.add(input.substring(a, b));
//            a = b + 1;
//        }
//        double [] d = new double[numbers.size()];
//        double result = 0.0;
//        for (int i = 0; i < d.length; i++) {
//            d[i] = Double.parseDouble(numbers.get(i));
//        }
//        String resultat;
//        for (int i = 0; i < )
//    }
    public String minusValueOperation(String input) {
        double resultNumber = minusValue(Double.parseDouble(input));
        if (resultNumber * 10 == (int)(resultNumber) * 10) {
            int i = (int)(resultNumber);
            return String.valueOf(i);
        }
        else return String.valueOf(resultNumber);
    }
    public String factorialOperation(String input) {
        double d = Double.parseDouble(input);
        if (d * 10 != (int)(d) * 10) {
            return "0";
        }
        else {
            int i = (int)(d);
            return String.valueOf(factorial(i));
        }
    }
    public String absValueOperation(String input) {
        double d = absoluteValue(Double.parseDouble(input));
        if (d * 10 == (int)(d) * 10) {
            int i = (int)(d);
            return String.valueOf(i);
        }
        else return String.valueOf(d);
    }
}
