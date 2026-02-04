package com.longbridge.core.uitls;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <pre>
 * company: 长桥科技
 * author : lcj168
 * time   : 2019/06/19
 * desc   :
 * version: 1.0.0
 * </pre>
 */
public class ArithUtils {

    public static final int DEF_DIV_SCALE = 6;
    private static final double EPSILON = 1e-10;
    private static final BigDecimal EPSILON_DECIMAL = new BigDecimal(EPSILON);

    public static boolean isZero(double value) {
        return Math.abs(value) < EPSILON;
    }

    public static Boolean isZero(@Nullable String value) {
        BigDecimal decimal = ConvertUtils.toBigDecimal(value);
        // |value| < EPSILON
        return decimal.abs().compareTo(EPSILON_DECIMAL) < 0;
    }

    public static Boolean isPositive(@Nullable String value) {
        return ConvertUtils.toBigDecimal(value).compareTo(EPSILON_DECIMAL) > 0;
    }

    public static Boolean isPositive(Double value) {
        return new BigDecimal(value).compareTo(EPSILON_DECIMAL) > 0;
    }

    /**
     * ----------------相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double add(String d1, String d2) {
        BigDecimal b1 = tempBigDecimal(d1);
        BigDecimal b2 = tempBigDecimal(d2);
        return b1.add(b2).doubleValue();

    }

    public static double add(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.add(b2).doubleValue();
    }

    public static String add(double d1, String d2, int scale) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = tempBigDecimal(d2);
        double value = b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        StringBuilder formatStr = new StringBuilder("0.");
        for (int i = 0; i < scale; i++) {
            formatStr.append("0");
        }
        return new DecimalFormat(formatStr.toString()).format(value);
    }

    /**
     * -----------------相减
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(String d1, String d2) {
        BigDecimal b1 = tempBigDecimal(d1);
        BigDecimal b2 = tempBigDecimal(d2);
        return b1.subtract(b2).doubleValue();
    }

    public static String sub(String d1, double d2, int scale) {
        BigDecimal b1 = tempBigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        double value = b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        StringBuilder formatStr = new StringBuilder("0.");
        for (int i = 0; i < scale; i++) {
            formatStr.append("0");
        }
        return new DecimalFormat(formatStr.toString()).format(value);
    }


    private static BigDecimal tempBigDecimal(String value) {
        if (!TextUtils.isEmpty(value)) {
            value = value.trim();
            return value.length() == 0 || value.equals("null") ? BigDecimal.ZERO : BigDecimal.valueOf(ConvertUtils.toDouble(value));
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * ----------------相乘
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(String d1, String d2) {
        BigDecimal b1 = tempBigDecimal(d1);
        BigDecimal b2 = tempBigDecimal(d2);
        return b1.multiply(b2).doubleValue();

    }

    public static double mul(double d1, double d2) {
        return mul(String.valueOf(d1), String.valueOf(d2));
    }

    public static double mul(double d1, String d2) {
        return mul(String.valueOf(d1), d2);
    }

    /**
     * ----------------相除
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double div(String d1, String d2) {
        return div(d1, d2, DEF_DIV_SCALE);
    }


    public static double div(double d1, double d2) {
        return div(String.valueOf(d1), String.valueOf(d2), DEF_DIV_SCALE);
    }

    public static double div(double d1, String d2) {
        return div(String.valueOf(d1), d2, DEF_DIV_SCALE);
    }


    public static double div(double d1, String d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(d1));
        BigDecimal b2 = tempBigDecimal(d2);
        try {
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (ArithmeticException e) {// 防止无限循环而报错  采用四舍五入保留3位有效数字
            return BigDecimal.ZERO.doubleValue();
        }
    }

    public static double div(String d1, String d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = tempBigDecimal(d1);
        BigDecimal b2 = tempBigDecimal(d2);
        try {
            return b1.divide(b2, scale + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (ArithmeticException e) {// 防止无限循环而报错  采用四舍五入保留3位有效数字
            return BigDecimal.ZERO.doubleValue();
        }
    }

    public static BigDecimal div(BigDecimal d1, BigDecimal d2, int scale, int roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        try {
            return d1.divide(d2, scale, roundingMode);
        } catch (ArithmeticException e) {// 防止无限循环而报错
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal div(BigDecimal d1, BigDecimal d2, int scale) {
        // 采用四舍五入保留3位有效数字
        return div(d1, d2, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static double div(double d1, double d2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        try {
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (ArithmeticException e) {// 防止无限循环而报错  采用四舍五入保留3位有效数字
            return BigDecimal.ZERO.doubleValue();
        }
    }

}
