package com.longbridge.core.uitls;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.LruCache;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;


import com.lb.price.one.BuildConfig;
import com.lb.price.one.R;
import com.longbridge.core.comm.FApp;
import com.longbridge.core.multilingual.LanguageUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 关于基本数据类型的强制转化。
 * 待补充 short byte 等数据类型的相互转化
 * Created by 李超军 on 2015/10/10.
 * fixed by louweijun on 2016-8-26.
 */
public class ConvertUtils {

    static final DecimalFormat DECIMAL_FORMAT0 = new DecimalFormat("0");
    static final DecimalFormat DECIMAL_FORMAT0_1 = new DecimalFormat("0.0");
    static final DecimalFormat DECIMAL_FORMAT0_2 = new DecimalFormat("0.00");
    static final DecimalFormat DECIMAL_FORMAT0_3 = new DecimalFormat("0.000");
    static final DecimalFormat DECIMAL_FORMAT0_4 = new DecimalFormat("0.0000");
    static final DecimalFormat DECIMAL_FORMAT0_5 = new DecimalFormat("0.00000");
    static final DecimalFormat DECIMAL_FORMAT0_6 = new DecimalFormat("0.000000");
    static final DecimalFormat DECIMAL_FORMAT0_7 = new DecimalFormat("0.0000000");
    static final DecimalFormat DECIMAL_FORMAT0_8 = new DecimalFormat("0.00000000");

    static BigDecimal b0 = new BigDecimal("0.1");//
    static BigDecimal b1 = new BigDecimal("1");//
    static BigDecimal b2 = new BigDecimal("1000");//
    static BigDecimal b3 = new BigDecimal("10000");//万
    static BigDecimal b4 = new BigDecimal("1000000");//百万
    static BigDecimal b5 = new BigDecimal("10000000");//千万（新增）
    static BigDecimal b6 = new BigDecimal("100000000");//亿
    static BigDecimal b7 = new BigDecimal("10000000000");//百亿
    static BigDecimal b8 = new BigDecimal("1000000000000");//万亿

    /**
     * 精确到小数点后两位
     *
     * @param value 值
     * @return float 值 xx.xx
     */
    public static String toFloatWithPoint(float value) {
        return toFloatWithPoint(value, 2);
    }

    /**
     * 精确到小数点后n位
     *
     * @param value 值
     * @param scale 保留的位数
     * @return 保留n位小数的值
     */
    public static String toFloatWithPoint(float value, int scale) {
        //        String formatValue = String.valueOf(value);
        //        BigDecimal b = new BigDecimal(formatValue);
        //        return b.setScale(point, BigDecimal.ROUND_HALF_UP).floatValue();
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return DECIMAL_FORMAT0.format(value);
        }
        if (scale == 1) {
            return DECIMAL_FORMAT0_1.format(value);
        }
        if (scale == 2) {
            return DECIMAL_FORMAT0_2.format(value);
        }
        if (scale == 3) {
            return DECIMAL_FORMAT0_3.format(value);
        }
        if (scale == 4) {
            return DECIMAL_FORMAT0_4.format(value);
        }
        if (scale == 5) {
            return DECIMAL_FORMAT0_5.format(value);
        }
        if (scale == 6) {
            return DECIMAL_FORMAT0_6.format(value);
        }
        if (scale == 7) {
            return DECIMAL_FORMAT0_7.format(value);
        }
        if (scale == 8) {
            return DECIMAL_FORMAT0_8.format(value);
        }
        StringBuilder formatStr = new StringBuilder("0.");
        for (int i = 0; i < scale; i++) {
            formatStr.append("0");
        }
        return new DecimalFormat(formatStr.toString()).format(value);
    }

    /**
     * 精确到小数点后n位(末尾的0去掉)
     * 如： 3.1 保留2位小数，也返回3.1
     *
     * @param value 值
     * @param scale 保留的位数
     * @return 保留n位小数的值
     */
    public static String toFloatWithPointNoEndZero(double value, int scale) {
        return toFloatWithPointNoEndZero(value, scale, RoundingMode.HALF_UP);
    }

    public static String toFloatWithPointNoEndZero(double value, int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(value);
        }
        String pattern = "#.##";
        switch (scale) {
            case 0:
                pattern = "#";
                break;
            case 1:
                pattern = "#.#";
                break;
            case 2:
                pattern = "#.##";
                break;
            case 3:
                pattern = "#.###";
                break;
            case 4:
                pattern = "#.####";
                break;
            case 5:
                pattern = "#.#####";
                break;
            case 6:
                pattern = "#.######";
                break;
        }
        DecimalFormat df = new DecimalFormat(pattern);
        df.setMaximumFractionDigits(scale);
        df.setRoundingMode(roundingMode);
        return df.format(value);
    }

    /**
     * 精确到小数点后n位(末尾的0不去掉)
     * 如： 3.1 保留2位小数，也返回3.1
     *
     * @param value 值
     * @param scale 保留的位数
     * @return 保留n位小数的值
     */
    public static String toFloatWithPoint(double value, int scale) {
        return toFloatWithPoint(value, scale, RoundingMode.HALF_UP);
    }

    /**
     * 截断
     * 1.119 -> 1.11
     * 1.111 -> 1.11
     * -1.119 -> -1.11
     * -1.111 -> -1.11
     *
     * @param value
     * @param scale
     * @return
     */
    public static String toFloatWithPointModeDown(double value, int scale) {
        return toFloatWithPoint(value, scale, RoundingMode.DOWN);
    }

    public static String toFloatWithPoint(double value, int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(value);
        }
        String pattern = "#.##";
        switch (scale) {
            case 0:
                pattern = "0";
                break;
            case 1:
                pattern = "0.0";
                break;
            case 2:
                pattern = "0.00";
                break;
            case 3:
                pattern = "0.000";
                break;
            case 4:
                pattern = "0.0000";
                break;
            case 5:
                pattern = "0.00000";
                break;
            case 6:
                pattern = "0.000000";
                break;
        }
        DecimalFormat df = new DecimalFormat(pattern);
        df.setMaximumFractionDigits(scale);
        df.setRoundingMode(roundingMode);
        return df.format(value);
    }

    /**
     * 处理服务器返回值中需要强制转化的int型<br>
     * 可以处理95.0或者95%此类数据
     *
     * @param string 值
     * @return 考虑四舍五入
     */
    public static int toIntForServer(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }

        try {
            //服务端有可能会返回100.0这种整数类型，直接转会报错，需要截取掉.后面的数据
            //服务器数据有时会带上%
            if (string.endsWith("%")) {
                string = string.substring(0, string.length() - 1);
            }
            float aFloat = Float.parseFloat(string);
            if (aFloat > 0) {
                return (int) (aFloat + 0.5);//需要考虑四舍五入
            } else {
                return (int) (aFloat - 0.5);//需要考虑四舍五入
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return 0;
        }
    }

    /**
     * string 值转化为 long
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static long toLong(String string) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string) || "null".equalsIgnoreCase(string)) {
            return 0L;
        }

        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return 0L;
        }

    }

    /**
     * string 值转化为 int
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static int toInt(String string) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string)) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    //差不多占用 48 kb + 12 kb = 60 kb 内存  * 5  大概占用 300 kb 内存
    private static LruCache<String, Float> convertCache = new LruCache<String, Float>(5000);

    /**
     * string 值转化为 float
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static float toFloat(String string) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string)) {
            return 0.00f;
        }

//        if (convertCache.get(string) != null) return convertCache.get(string);
        try {
            Float value = convertCache.get(string);
            if (null == value) {
                Float result = Float.parseFloat(string);
                convertCache.put(string, result);
                return result.floatValue();
            } else {
                return value.floatValue();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return 0.00f;
        }
    }

    public static float toFloatSafety(String string) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string))
            return Float.NaN;

        try {
            Float value = convertCache.get(string);
            if (null == value) {
                Float result = Float.parseFloat(string);
                convertCache.put(string, result);
                return result.floatValue();
            } else {
                return value.floatValue();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return Float.NaN;
        }
    }

    /**
     * string 值转化为 float
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static float toFloat(String string, float defaultFloat) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string)) {
            return defaultFloat;
        }
        try {
            return Float.parseFloat(string);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return defaultFloat;
        }
    }

    /**
     * string 值转化为 float
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static float toFloatPrecision(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0.00f;
        }
        try {
            int count = string.length() - string.indexOf(".") - 1;
            double aa = Math.pow(10, count);
            float cc = Float.parseFloat(string);
            return (float) (Math.round(cc * aa) / aa);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return 0.00f;
        }
    }

    public static BigDecimal toBigDecimal(String string) {
        if (TextUtils.isEmpty(string)) {
            return BigDecimal.ZERO;
        }

        try {
            string = string.replace(",", "");
            return new BigDecimal(string);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return BigDecimal.ZERO;
        }
    }

    /**
     * string 值转化为 double
     *
     * @param string 值
     * @return 如果string 值错误 返回0
     */
    public static double toDouble(String string) {
        if (TextUtils.isEmpty(string) || "--".equalsIgnoreCase(string) || TextUtils.equals("NaN", string)) {
            return 0.00d;
        }

        try {
            return Double.parseDouble(string.replace(",", ""));
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            ;
            return 0.00d;
        }
    }

    /**
     * string 值是否能正确转成double类型
     * 如输入.、-、-.这些数字类型，则返回空字符串
     */
    public static Boolean canBeToDouble(String string) {
        if (TextUtils.isEmpty(string)) {
            return true;
        }

        try {
            String result = String.valueOf(Double.parseDouble(string));
            return true;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 转换成double可以控制精度
     * 3.156 -> 3.15(true)：3.16(false)
     *
     * @param obj   字符串
     * @param scale 小数点后保留位数
     * @param isCut 是否截取，否则4舍五入
     * @return 精确的double值
     */
    public static double toDouble(String obj, int scale, boolean isCut) {
        try {
            double result = toDouble(obj);

            if (result == 0) {
                return 0.00d;
            }

            BigDecimal bd = BigDecimal.valueOf(toDouble(obj));
            bd = bd.setScale(scale, isCut ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_HALF_UP);
            //        double d = bd.doubleValue();
            //        bd = null;
            return bd.doubleValue();
        } catch (NumberFormatException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return 0.00d;
        }
    }

    /**
     * 转换成double可以控制精度
     * 0.003156 -> 3.15%(true)：3.16%(false)
     *
     * @param obj   字符串
     * @param scale 小数点后保留位数
     * @param isCut 是否截取，否则4舍五入
     * @return 精确的double值
     */
    public static String toDoubleAddPercent(String obj, int scale, boolean isCut) {
        try {
            double result = toDouble(obj) * 100;

            if (result == 0) {
                return "0.00%";
            }

            BigDecimal bd = BigDecimal.valueOf(result);
            bd = bd.setScale(scale, isCut ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_HALF_UP);
            //        double d = bd.doubleValue();
            //        bd = null;
            return bd.doubleValue() + "%";
        } catch (NumberFormatException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return "0.00%";
        }
    }

    /**
     * 取整  4.45--->4
     */
    public static int toInt(String value, int roundingMode) {
        //        if (TextUtils.isEmpty(value)) {
        //            return 0;
        //        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, roundingMode);
        //        double d = bd.doubleValue();
        //        bd = null;
        return bd.intValue();
    }


    /**
     * 两个string 转化为 百分比
     *
     * @param str  被除数
     * @param str2 除数
     * @return 考虑四舍五入 数字错位返回 0
     */
    public static int toPercent(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        if (TextUtils.isEmpty(str2) || "0".equals(str2)) {
            return 0;
        }
        try {
            float f1 = Float.parseFloat(str);
            float f2 = Float.parseFloat(str2);
            return toPercent(f1, f2);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 两数相除  转化为 整数百分比 加上%
     *
     * @param f1 被除数
     * @param f2 除数
     * @return 考虑四舍五入 数字错位返回 0
     */
    public static int toPercent(float f1, float f2) {
        return (int) (f1 / f2 * 100 + 0.5);
    }

    /**
     * 两数相除  转化为 百分比 加上%
     *
     * @param str  被除数
     * @param str2 除数
     * @return 考虑四舍五入 数字错位返回 0
     */
    public static String toPercentStr(String str, String str2) {
        return toPercent(str, str2) + "%";
    }

    /**
     * 两数相除  转化为 百分比 加上%
     *
     * @param f1 被除数
     * @param f2 除数
     * @return 考虑四舍五入 数字错位返回 0
     */
    public static String toPercentStr(float f1, float f2) {
        return toPercent(f1, f2) + "%";
    }

    /**
     * 半角转化为全角的方法
     *
     * @param input 半角字符串
     * @return 全角
     */
    public static String toSBC(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        // 半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i] < 127 && c[i] > 32) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        //横线转减号（负号）
        returnString = returnString.replaceAll("—", "-");
        //过滤空格
        returnString = returnString.replaceAll(" ", "");
        //过滤搜狗减号
        returnString = returnString.replaceAll("﹣", "-");

        return returnString;
    }

    /**
     * 得到数据中的中文
     *
     * @return 中文文字
     */
    public static String getChinese(String data) {
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FFF]+");
        Matcher matcher = pattern.matcher(data);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }


    /**
     * 判断数据中是否有中文
     *
     * @return
     */
    public static boolean isChinese(String data) {
        String sb = getChinese(data);
        return sb.length() > 0;
    }

    /**
     * 判断数据中是否有中文
     *
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static int getColor(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }

        try {
            return Color.parseColor(str);
        } catch (Exception e) {
            return -1;
        }
    }


    public static String formatNum(String num) {
        return num;
    }




    public static String trailingZeros(BigDecimal bigDecimal) {
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    public static String trailingZeros(double value) {
        return trailingZeros(ConvertUtils.toBigDecimal(String.valueOf(value)));
    }


    /**
     * 格式化百分比。不做 * 100 处理
     *
     * @param priceChangeRate
     * @param timeRes         「倍」的单位 string ID
     */
    public static String toPercentFormatWithout100(double priceChangeRate, @StringRes int timeRes) {
        double abs = Math.abs(priceChangeRate);
        if (abs < 100) {
            return toFloatWithPointNoEndZero(priceChangeRate, 2) + "%";
        } else if (abs < 1000) {
            return toFloatWithPointNoEndZero(priceChangeRate, 1) + "%";
        } else if (abs < 10000) {
            return toIntForServer(String.valueOf(priceChangeRate)) + "%";
        } else {
            return FApp.get().getString(timeRes, Math.round(priceChangeRate / 100));
        }
    }

    public static String toPercentFormatWithout100EndZero(double priceChangeRate, @StringRes int timeRes) {
        double abs = Math.abs(priceChangeRate);
        if (abs < 100) {
            return toFloatWithPoint(priceChangeRate, 2) + "%";
        } else if (abs < 1000) {
            return toFloatWithPoint(priceChangeRate, 1) + "%";
        } else if (abs < 10000) {
            return toIntForServer(String.valueOf(priceChangeRate)) + "%";
        } else {
            return FApp.get().getString(timeRes, Math.round(priceChangeRate / 100));
        }
    }

    /**
     * double类型如果小数点后为零显示整数否则保留 返回String
     *
     * @param num
     * @return
     */
    public static String floatToNoZero(float num) {
        //只保留小数点后2位
        String number1 = String.format(Locale.getDefault(), "%.2f", num);
        //類型轉換
        float number2 = toFloat(number1);
        if (Math.round(number2) - number2 == 0) {
            return String.valueOf((long) number2);
        }
        return String.valueOf(number2);
    }

    /**
     * 保留2位小数去掉小数点后面多余的0
     *
     * @return
     */
    public static String subZeroAndDot(String value) {
        if (TextUtils.isEmpty(value) || "0".equals(value) || "0.0".equals(value)) {
            return "0.00";
        }
        float aFloat = toFloat(value);
        value = toFloatWithPoint(aFloat, 2);
        if (value.indexOf(".") > 0) {
            value = value.replaceAll("0+?$", "");//去掉多余的0
            value = value.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return value;
    }

    /*
     *
     * */
    public static String subZeroAndDot3(String value) {
        if (TextUtils.isEmpty(value) || "0".equals(value) || "0.0".equals(value)) {
            return "0.00";
        }
        float aFloat = toFloat(value);
        value = toFloatWithPoint(aFloat, 3);
        if (value.indexOf(".") > 0) {
            value = value.replaceAll("0+?$", "");//去掉多余的0
            value = value.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return value;
    }



    /**
     * 支持自定义小数位数的数字处理
     *
     * @param num
     * @param places
     * @return
     */


    /**
     * 将0.1235转为+12.35%
     *
     * @param rate
     * @return
     */
    public static String incomeRateString(BigDecimal rate) {
        BigDecimal one = new BigDecimal("1");
        BigDecimal roundvalue = rate.multiply(new BigDecimal(100)).divide(one, 2, BigDecimal.ROUND_HALF_UP);
        double value = roundvalue.doubleValue();
        if (value > 0) {
            return "+" + roundvalue.toString() + "%";
        } else {
            return roundvalue.toString() + "%";
        }

    }

    public static String incomeRateString(String rate) {
        BigDecimal rateBigDecimal = BigDecimal.ZERO;
        try {
            rateBigDecimal = new BigDecimal(rate);
        } catch (Exception e) {
        }
        return incomeRateString(rateBigDecimal);
    }

    /**
     * 避免出现-0.00的情况
     *
     * @param value
     * @param scale
     * @return
     */
    public static double formatNumNegative(double value, int scale) {
        if (scale < 1) {
            return value;
        }
        double pattern = -0.01;
        switch (scale) {
            case 1:
                pattern = -0.1;
                break;
            case 2:
                pattern = -0.01;
                break;
            case 3:
                pattern = -0.001;
                break;
            case 4:
                pattern = -0.0001;
                break;
            case 5:
                pattern = -0.00001;
                break;
            case 6:
                pattern = -0.000001;
                break;
        }
        if (value > pattern && value < 0) {
            return 0;
        }
        return value;
    }

    public static double roundBasePlaces(double value, int places) {
        if (places < 0) {
            places = 0;
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String floatToString(float number) {
        if (number == 0) {
            return "0";
        }
        DecimalFormat b2 = new DecimalFormat("0.000");//
        return b2.format(number);
    }

    public static int getPrecision(@Nullable String value) {
        return getPrecision(value, 0);
    }

    public static int getPrecision(@Nullable String value, int expect) {
        if (TextUtils.isEmpty(value) || value.endsWith(".") || !value.contains(".")) {
            return expect;
        }
        return value.length() - value.indexOf(".") - 1;
    }

    /**
     * 对double类型的数值进行四舍五入到指定的小数位数。
     * 如果指定的小数位数小于0，则自动调整为0。
     *
     * @param value  要四舍五入的数值
     * @param places 小数位数
     * @return 四舍五入后的结果
     */
    public static double round(double value, int places) {
        // 如果小数位数小于0，则将其设置为0
        if (places < 0) {
            places = 0;
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static final DecimalFormat decimalFormat00 = new DecimalFormat("0.00");

    public static String decimalFormat00(double value) {
        try {
            BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
            decimalFormat00.setRoundingMode(RoundingMode.HALF_UP);
            return decimalFormat00.format(bigDecimal);
        } catch (Exception e) {
            return BigDecimal.ZERO.toPlainString();
        }
    }

    /**
     * 都是2位小数，小于1 补齐，截断成2位小数
     * ------
     * 1        --> 1.00
     * 1.0      --> 1.00
     * 1.1      --> 1.10
     * 1.00     --> 1.00
     * 1.12345  --> 1.12
     * 88888.12345 --> 88,888.12
     */
    public static String formatCrashAmount(String numberStr) {
        try {
            BigDecimal bd = new BigDecimal(numberStr);
            BigDecimal displayBd = bd.setScale(2, RoundingMode.DOWN);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return df.format(displayBd);
        } catch (Exception e) {
            return "--";
        }
    }
}