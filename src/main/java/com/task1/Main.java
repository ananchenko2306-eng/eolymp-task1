package com.task1;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        if (in.hasNext()) {
            String line = in.next();

            String[] parts = line.split("\\+");

            int num1 = romanToInt(parts[0]);
            int num2 = romanToInt(parts[1]);

            int sum = num1 + num2;

            System.out.println(intToRoman(sum));
        }

        in.close();
    }

    private static int getVal(char c) {
        switch (c) {
            case 'M':
                return 1000;
            case 'D':
                return 500;
            case 'C':
                return 100;
            case 'L':
                return 50;
            case 'X':
                return 10;
            case 'V':
                return 5;
            case 'I':
                return 1;
            default:
                return 0;
        }
    }

    private static int romanToInt(String s) {
        int sum = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            int currentVal = getVal(s.charAt(i));

            if (i + 1 < n) {
                int nextVal = getVal(s.charAt(i + 1));
                if (currentVal < nextVal) {
                    sum -= currentVal;
                } else {
                    sum += currentVal;
                }
            } else {
                sum += currentVal;
            }
        }
        return sum;
    }

    private static String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                result.append(symbols[i]);
            }
        }
        return result.toString();
    }
}