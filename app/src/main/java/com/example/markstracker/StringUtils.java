package com.example.markstracker;

import java.text.DecimalFormat;

public class StringUtils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static String decimalFormat(float mark) {
        return decimalFormat.format(mark);
    }
}
