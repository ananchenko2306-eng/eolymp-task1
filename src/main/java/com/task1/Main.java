package com.task1;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        if (in.hasNext()) {
            String input = in.next();

            int plusIndex = input.indexOf('+');

            String part1 = input.substring(0, plusIndex);
            String part2 = input.substring(plusIndex + 1);

            int number1 = romanToInt(part1);
            int number2 = romanToInt(part2);

            int sum = number1 + number2;

            System.out.println(intToRoman(sum));
        }

        in.close();
    }

    private static int getRomanValue(char c) {
        switch (c) {
            case 'M': return 1000;
            case 'D': return 500;
            case 'C': return 100;
            case 'L': return 50;
            case 'X': return 10;
            case 'V': return 5;
            case 'I': return 1;
            default: return 0;
        }
    }

    private static int romanToInt(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            int current = getRomanValue(s.charAt(i));

            if (i + 1 < s.length()) {
                int next = getRomanValue(s.charAt(i + 1));

                if (current < next) {
                    result -= current;
                } else {
                    result += current;
                }
            } else {
                result += current;
            }
        }
        return result;
    }

    private static String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();

        while (num >= 1000) {
            sb.append("M");
            num -= 1000;
        }
        if (num >= 900) {
            sb.append("CM");
            num -= 900;
        }
        while (num >= 500) {
            sb.append("D");
            num -= 500;
        }
        if (num >= 400) {
            sb.append("CD");
            num -= 400;
        }
        while (num >= 100) {
            sb.append("C");
            num -= 100;
        }
        if (num >= 90) {
            sb.append("XC");
            num -= 90;
        }
        while (num >= 50) {
            sb.append("L");
            num -= 50;
        }
        if (num >= 40) {
            sb.append("XL");
            num -= 40;
        }
        while (num >= 10) {
            sb.append("X");
            num -= 10;
        }
        if (num >= 9) {
            sb.append("IX");
            num -= 9;
        }
        while (num >= 5) {
            sb.append("V");
            num -= 5;
        }
        if (num >= 4) {
            sb.append("IV");
            num -= 4;
        }
        while (num >= 1) {
            sb.append("I");
            num -= 1;
        }

        return sb.toString();
    }
}