package com.lb.util;



import android.annotation.SuppressLint;
import android.os.Build;
import android.util.LruCache;

import androidx.annotation.RequiresApi;


import com.lb.price.one.R;
import com.longbridge.core.uitls.ArithUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * date相关工具类<br>
 * Author: 李超军<br>
 * Create 2014-5-28<br>
 * fixed by louweijun on 2016-8-26.<br>
 * Version 1.1.0
 */
public class DateTimeUtils {
    private static final int[] WEEKSID = {R.string.core_sunday, R.string.core_monday, R.string.core_tuesday,
            R.string.core_wednesday, R.string.core_thursday,
            R.string.core_friday, R.string.core_saturday};

    private static final String[] WEEKS2 = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static final String[] EN_MONTH = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] SUFFIXES = {"0th", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
            "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
            "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
            "30th", "31st"};

    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static String MMDDYYYY24HMMSS = "MM/dd/yyyy HH:mm:ss";

    public static boolean isFeatureYear(String year, TimeZone timeZone) {
        if (year == null || year.length() != 4) {
            return false;
        }

        // 将年份字符串转换为整数
        int yearInt = Integer.parseInt(year);

        // 获取当前年份
        Calendar calendar = Calendar.getInstance(timeZone);
        int currentYear = calendar.get(Calendar.YEAR);

        // 判断是否为未来年份
        return yearInt > currentYear;
    }

    /**
     * 把符合日期格式的字符串转换为日期类型
     *
     * @param dateStr    符合日期格式的字符串
     * @param formatType 日期格式 {@link DateTimeUtils}中常量
     * @return 日期类型
     */
    public static Date stringToDate(String dateStr, String formatType) {
        Date d;
        SimpleDateFormat format = new SimpleDateFormat(formatType, Locale.getDefault());
        try {
            format.setLenient(false);//严格控制日期格式
            d = format.parse(dateStr);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    public static Date stringToDateWithZone(String dateStr, String formatType, TimeZone zone) {
        Date d;
        SimpleDateFormat format = new SimpleDateFormat(formatType, Locale.getDefault());
        format.setTimeZone(zone);
        try {
            format.setLenient(false);//严格控制日期格式
            d = format.parse(dateStr);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    /**
     * 把日期转换为指定格式的字符串
     *
     * @param date       日期
     * @param formatType 指定格式
     * @return 指定格式的字符串
     */
    public static String dateToString(Date date, String formatType, Locale locale) {
        return dateToString(date, formatType, locale, null);
    }

    public static String dateToString(Date date, String formatType, Locale locale, TimeZone timeZone) {
        String result = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatType, locale);
            if (timeZone != null) {
                format.setTimeZone(timeZone);
            }
            result = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String dateToString(Date date, String formatType) {
        return dateToString(date, formatType, Locale.getDefault());
    }

    public static String dateToString(Date date, String formatType, TimeZone timeZone) {
        return dateToString(date, formatType, Locale.getDefault(), timeZone);
    }


    public static String chineseYearMonthDay(long dateLong) {
        return dateToString(new Date(dateLong), "yyyy年MM月dd日");
    }

    public static String chineseYearMonthDay2(long dateLong) {
        return dateToString(new Date(dateLong), "yyyy/MM/dd");
    }

    public static String MonthDay2(long dateLong) {
        return dateToString(new Date(dateLong), "MM/dd");
    }

    public static String toBirthDay(long dateLong) {
        return dateToString(new Date(dateLong), "yyyy-MM-dd");
    }

    public static String toMMdd(long dateLong) {
        return dateToString(new Date(dateLong), "MM-dd");
    }


    public static String toHourMinsTime(long dateLong) {
        return dateToString(new Date(dateLong), "HH:mm:ss");
    }

    public static String toHourMimuteTime(long dateLong) {
        return dateToString(new Date(dateLong), "HH:mm");
    }

    public static String YearMonthDayTime1(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd HH:mm");
    }

    public static String YearMonthDayTime3(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd HH:mm:ss");
    }

    public static String YearMonthDayTime4(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd HH:mm");
    }

    public static String YearMonthDayTime5(long dateLong) {
        return dateToString(new Date(dateLong), "yyyy/MM/dd HH:mm:ss");
    }

    public static String YearMonthDay2(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000;
        }
        return dateToString(new Date(dateLong), "yyyy-MM-dd");
    }

    public static String YearMonthDayTime6(long dateLong) {
        return dateToString(new Date(dateLong), "yyyy-MM-dd HH:mm:ss");
    }

    public static String YearMonthDayTime7(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd HH:mm");
    }

    public static String YearMonthDayTime8(long dateLong) {
        return dateToString(new Date(dateLong), "MM.dd HH:mm");
    }

    public static String YearMonthDay(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd");
    }

    public static String YearMonthDay(Date date) {
        return dateToString(date, "yyyy.MM.dd");
    }

    public static String Year(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy");
    }

    public static String YearMonth(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy.MM");
    }

    public static String MonthDay(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "MM.dd");
    }

    public static String MonthDay(long dateLong, TimeZone zone) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "MM/dd", zone);
    }


    public static String MonthDayTime3(long dateLong) {
        return dateToString(new Date(dateLong), "MM-dd HH:mm:ss");
    }

    public static String MonthDayTime5(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "MM.dd HH:mm");
    }

    public static String MonthDayTime6(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "MM.dd HH:mm:ss");
    }



    public static String HourMinute(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "HH:mm");
    }

    public static String HourMinute(Date date) {
        return dateToString(date, "HH:mm");
    }

    public static String HourMinuteA(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "hh:mm a");
    }

    public static String YearMonthDayHourMinuteA(long dateLong) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), "yyyy.MM.dd hh:mm a");
    }


    public static String EDTTime(long dateLong) {
        return dateToString(new Date(dateLong), "MM.dd HH:mm:ss");
    }

    /**
     * 把符合日期格式的字符串转换为long型时间格式
     *
     * @param dateStr    符合日期格式的字符串
     * @param formatType 日期格式 {@link DateTimeUtils}中常量
     * @return long型日期格式
     */
    public static long dateStrToLong(String dateStr, String formatType) {
        Date date = stringToDate(dateStr, formatType);
        if (null == date) {
            return 0L;
        }
        return date.getTime();
    }



    /**
     * 获取时间中的年份值
     *
     * @param date 时间
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取时间中的月份值
     *
     * @param date 时间
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取时间中的天份值
     *
     * @param date 时间
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }


    public static Date getTimeBeforeSomeDay(int day) {
        return getTimeBeforeSomeDay(new Date(), day);
    }

    /**
     * 获得某个时间几天以前的时间
     *
     * @param day  天数
     * @param date 参照时间
     */
    public static Date getTimeBeforeSomeDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * 获得某个时间几年以前的时间
     *
     * @param year 年数
     * @param date 参照时间
     */
    public static Date getBeforeYear(Date date, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, -year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }


    /**
     * 两个日期的年份差 注意：
     *
     * @param originalDate 小日期
     * @param compareDate  大日期
     * @return 只比较年份 2015-12-30和2016-01-01 返回也相差1年
     */
    public static int yearDiff(Date originalDate, Date compareDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(compareDate);
        int originalYear = aCalendar.get(Calendar.YEAR);
        aCalendar.setTime(originalDate);
        int compareYear = aCalendar.get(Calendar.YEAR);

        return compareYear - originalYear;
    }

    /**
     * 两个日期的年份差 注意：
     *
     * @param originalDate 小日期
     * @param compareDate  大日期
     * @return 只比较年份 2015-12-30和2016-01-01 返回也相差1年
     */
    public static int yearDiff(long originalDate, long compareDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTimeInMillis(compareDate);
        int originalYear = aCalendar.get(Calendar.YEAR);
        aCalendar.setTimeInMillis(originalDate);
        int compareYear = aCalendar.get(Calendar.YEAR);

        return compareYear - originalYear;
    }

    /**
     * 两个日期天数差
     *
     * @param originalDate 小日期
     * @param compareDate  大日期
     * @return 两个日期的天数差
     */
    public static int daysDiff(Date originalDate, Date compareDate) {
        if (null == originalDate || null == compareDate) {
            return 0;
        }
        return daysDiff(originalDate.getTime(), compareDate.getTime());
    }

    /**
     * 两个日期天数差,精确到小时
     *
     * @param originalDate 小日期
     * @param compareDate  大日期
     * @return 两个日期的天数差
     */
    public static Double daysHourDiff(String originalDate, String compareDate, String format, String zoneId) {
        if (null == originalDate || null == compareDate) {
            return 0.0;
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern(format);
            ZoneId zone = ZoneId.of(zoneId); // 指定时区

            // 2. 解析字符串为带时区的日期对象
            LocalDateTime date1 = LocalDateTime.parse(originalDate, formatter)
                    .atZone(zone).toLocalDateTime();
            LocalDateTime date2 = LocalDateTime.parse(compareDate, formatter)
                    .atZone(zone).toLocalDateTime();

            // 3. 计算时间差
            Duration duration = Duration.between(date1, date2);

            // 4. 转换为精确小时数
            return ArithUtils.div(duration.toHours(), 24.0);
        } else {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zoneId));
                Date date1 = simpleDateFormat.parse(originalDate);
                Date date2 = simpleDateFormat.parse(compareDate);
                return ArithUtils.div(hourDiff(date1, date2), 24);
            } catch (ParseException e) {
                return 0.0;
            }
        }
    }

    /**
     * 两个日期天数差
     *
     * @param originalDate 小日期
     * @param compareDate  大日期
     * @return 两个日期的天数差
     */
    public static int daysDiff(long originalDate, long compareDate) {
        return (int) ((getTimeInDay(compareDate) - getTimeInDay(originalDate)));
    }

    /**
     * 获取毫秒数，清零了时、分、秒字段上的数据
     * 可用于计算两个日期的天数差
     *
     * @param millionSeconds
     * @return
     */
    public static long getTimeInDay(long millionSeconds) {
        Calendar originalCal = Calendar.getInstance();
        originalCal.setTimeInMillis(millionSeconds);
        originalCal.set(Calendar.HOUR_OF_DAY, 0);
        originalCal.set(Calendar.MINUTE, 0);
        originalCal.set(Calendar.SECOND, 0);
        originalCal.set(Calendar.MILLISECOND, 0);
        int dayUnit = 1000 * 60 * 60 * 24;
        return originalCal.getTimeInMillis() / dayUnit;
    }

    public static int minuteDiff(Date originalDate, Date compareDate) {
        return (int) (compareDate.getTime() / 60000L - originalDate.getTime() / 60000L);
    }

    public static int hourDiff(Date originalDate, Date compareDate) {
        return (int) (compareDate.getTime() / (60000L * 60L) - originalDate.getTime() / (60000L * 60L));
    }


    /**
     * 判断一个日期和今天相差几天的绝对值
     * 用于判断当前日期前面的时间
     *
     * @param date 时间形
     * @return 相差的天数
     */

    public static int daysFromTodayAbs(Date date) {
        return Math.abs(daysDiff(new Date(), date));
    }




    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }


    /**
     * 获取当前时间季度
     *
     * @param time
     * @return
     */
    public static int getQuarterOfDay(long time) {
        if (isInSecondTime(time)) {
            time = time * 1000L;
        }
        int month = 0;
        try {
            Date date = new Date(time);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            month = c.get(Calendar.MONTH) + 1; //由于month从0开始，所有这里+1
        } catch (Exception e) {
            return 0;
        }
        return month % 3 == 0 ? month / 3 : month / 3 + 1;
    }

    /**
     * 上半年 H1，下半年 H2
     *
     * @param time
     * @return
     */
    public static int getHalfYear(long time) {
        if (isInSecondTime(time)) {
            time = time * 1000L;
        }
        int month = 0;
        try {
            Date date = new Date(time);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            month = c.get(Calendar.MONTH) + 1; //由于month从0开始，所有这里+1
        } catch (Exception e) {
            return 0;
        }
        return month > 6 ? 2 : 1;
    }

    /**
     * 返回周几
     *
     * @param time
     * @return
     */
    public static String getWeekByMillis(long time) {
        Calendar calendar = Calendar.getInstance();//获得一个日历
        calendar.setTimeInMillis(time);
        return WEEKS2[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 返回今年的第几周
     *
     * @param time
     * @return
     */
    public static int getWeekOfYear(long time) {
        if (isInSecondTime(time)) {
            time = time * 1000L;
        }
        Calendar calendar = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
        calendar.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
        calendar.setTime(new Date(time));
        int weeks = calendar.get(Calendar.WEEK_OF_YEAR);
        return weeks + 1;
    }


    /**
     * 返回周几
     *
     * @param date
     * @return
     */
    public static int getWeekResIdByMillis(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(timeZone);
        return WEEKSID[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }




    public static String stampToDate1(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(time));
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean isYesterday(long time) {
        boolean isYesterday = false;
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(sdf.format(new Date()));
            if (time < date.getTime() && time >= (date.getTime() - 24 * 60 * 60 * 1000)) {
                isYesterday = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isYesterday;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isInWeek(long time) {
        boolean isInWeek = false;
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(sdf.format(new Date()));
            if (time < date.getTime() && time >= (date.getTime() - 7 * 24 * 60 * 60 * 1000)) {
                isInWeek = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isInWeek;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isToday(long time) {
        boolean isToday = false;
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(sdf.format(new Date()));
            if (time >= (date.getTime())) {
                isToday = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isToday;
    }

    /**
     * 是否在 24 小时内
     */
    public static boolean isIn24Hour(long time, long nowTime) {
        return nowTime - time < 86400000L;
    }

    /**
     * 是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        }
        return false;
    }

    /**
     * 是否是同一天
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        }
        return false;
    }

    /**
     * 用于判断n个工作日(排除节假日、周六日包含节后补班数据)后的日期
     *
     * @param list        节假日数据源
     * @param weekDayList 节后补班数据源
     * @param today       计算开始时间
     * @param num         多少个工作日 根据需要自行安排
     * @return
     * @throws ParseException
     * @author ywh
     * @version 创建时间：2019年4月24日 上午11:24:58
     */
    @SuppressWarnings("deprecation")
    public static Date getScheduleActiveDate(List<String> list, List<String> weekDayList, Date today, int num) throws ParseException {
        String today1 = parseDateToString(today, "yyyy-MM-dd");
        Date tomorrow = null;
        int delay = 1;
        while (delay <= num) {
            tomorrow = getTomorrow(today);
            //当前日期+1即tomorrow,判断是否是节假日,同时要判断是否是周末,都不是则将scheduleActiveDate日期+1,直到循环num次即可------不是节假日不是周末并且不是补班
            if ((!isWeekend(sdfYear.format(tomorrow)) && !isHoliday(sdfYear.format(tomorrow), list)) || isWorkWeekDay(sdfYear.format(tomorrow), weekDayList)) {
                if (isWorkWeekDay(sdfYear.format(tomorrow), weekDayList)) {
                    System.out.println(sdfYear.format(tomorrow) + "::是节假日调休补班");
                } else {
                    System.out.println(sdfYear.format(tomorrow) + "::是正常工作日");
                }
                delay++;
                today = tomorrow;
            } else if (isHoliday(sdfYear.format(tomorrow), list)) {
//                tomorrow = getTomorrow(tomorrow);
                today = tomorrow;
                System.out.println(sdfYear.format(tomorrow) + "::是节假日");
            } else if (isWeekend(sdfYear.format(tomorrow))) {//是周六日并且不是节后补班
                if (!isWorkWeekDay(sdfYear.format(tomorrow), weekDayList)) {
                    today = tomorrow;
                    System.out.println(sdfYear.format(tomorrow) + "::是休息日");
                }

            }
        }
        System.out.println(today1 + "后" + num + "个工作日后,日期为::" + sdfYear.format(today));
        return today;
    }

    /**
     * 获取明天的日期
     *
     * @author ywh
     * @version 创建时间：2019年4月23日 下午5:18:44
     */
    public static Date getTomorrow(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 判断是否是weekend
     *
     * @author ywh
     * @version 创建时间：2019年4月23日 下午5:19:27
     */
    public static boolean isWeekend(String sdate) throws ParseException {
        Date date = sdfYear.parse(sdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    /**
     * 判断是否是holiday
     *
     * @author ywh
     * @version 创建时间：2019年4月23日 下午5:19:42
     */
    public static boolean isHoliday(String sdate, List<String> list) throws ParseException {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (sdate.equals(list.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是补班
     *
     * @author ywh
     * @version 创建时间：2019年4月23日 下午5:19:54
     */
    public static boolean isWorkWeekDay(String sdate, List<String> list) throws ParseException {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (sdate.equals(list.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 把日期格式化成字符串
     *
     * @param date
     * @param format 例: yyyy-MM-dd
     * @return
     */
    public static String parseDateToString(Date date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formater = new SimpleDateFormat(format);
        String dateString;
        dateString = formater.format(date);
        return dateString;
    }

    public static Date parseStringToDate(String time, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formater = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formater.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date parseStringToDateWithZone(String time, String format, TimeZone zone) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formater = new SimpleDateFormat(format);
        formater.setTimeZone(zone);
        Date date = null;
        try {
            date = formater.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int betweenMonthByTwoCalendar(Calendar startCalendar, Calendar endCalendar) {
        //判断日期大小
        if (startCalendar.after(endCalendar)) {
            Calendar temp = startCalendar;
            startCalendar = endCalendar;
            endCalendar = temp;
        }
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH);
        int endMonth = endCalendar.get(Calendar.MONTH);
        int monthNum = (endYear - startYear) * 12 + (endMonth - startMonth);
        return monthNum;
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean isInTimeRange(long nowTimeStamp, String sourceTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String nowTimeStr = format.format(new Date(nowTimeStamp));
        return isInTimeRange(nowTimeStr, sourceTime);
    }

    /**
     * 判断时间是否在某个时间段内
     *
     * @param nowTimeStr 需要判断的时间,形如23:30
     * @param sourceTime 20:00-08:00
     * @return boolean
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean isInTimeRange(String nowTimeStr, String sourceTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            return false;
        }
        if (nowTimeStr == null || !nowTimeStr.contains(":")) {
            return false;
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            long now = sdf.parse(nowTimeStr).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (now == start || now == end) {
                return true;
            }
            if (end < start) {
                return now < end || now >= start;
            } else {
                return now >= start && now < end;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 转化RFC3339格式的时间  2020-06-01T07:47:08-04:00
     *
     * @param time
     * @return
     */



    /**
     * 获取几个月前的时间
     *
     * @param monthNum
     * @return
     */
    public static long getMonthBefore(int monthNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int rangeData = 0;
        if (monthNum < 12) {
            rangeData = monthNum * 30;
        } else {
            rangeData = 365;
        }
        calendar.add(Calendar.DATE, -rangeData);
        return calendar.getTime().getTime();
    }

    /**
     * 获取某个时间几个月前的时间
     * < 12个月，30*N天
     * ==12个月，==365天
     *
     * @param monthNum
     * @return
     */
    public static long getMonthBefore(int monthNum, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int rangeData = 0;
        if (monthNum < 12) {
            rangeData = monthNum * 30;
        } else {
            rangeData = 365;
        }
        calendar.add(Calendar.DATE, -rangeData);
        return calendar.getTime().getTime();
    }

    /**
     * 获取某个时间几个月后的时间
     * < 12个月，30*N天
     * ==12个月，==365天
     *
     * @param monthNum
     * @return
     */
    public static long getMonthAfter(int monthNum, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int rangeData = 0;
        if (monthNum < 12) {
            rangeData = monthNum * 30;
        } else {
            rangeData = 365;
        }
        calendar.add(Calendar.DATE, rangeData);
        return calendar.getTime().getTime();
    }

    /**
     * 获取当天凌晨时间
     *
     * @param time
     * @return
     */
    public static long getEveningTimeLong(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 获取当天夜晚时间
     *
     * @param time
     * @return
     */
    public static long getEveningTimeLong2(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }

    public static String formatTimeToAssign(String dateStr, String originFormat, String targetFormat) {
        String tempDateStr = dateStr;
        try {
            Date date = DateTimeUtils.stringToDate(dateStr, originFormat);
            dateStr = DateTimeUtils.dateToString(date, targetFormat);
            return dateStr;
        } catch (Exception e) {
            return tempDateStr;
        }
    }

    public static Long addOneDay(TimeZone sourceZone, Long timestamp) {
        return addDay(sourceZone, timestamp, 1);
    }

    public static Long addDay(TimeZone sourceZone, Long timestamp, int day) {
        Calendar calendar = Calendar.getInstance(sourceZone);
        calendar.setTimeInMillis(timestamp);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定某一天的开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getDailyStartTime(Long timeStamp) {
        return getDailyStartTime(TimeZone.getDefault(), timeStamp);
    }

    /**
     * 获取指定某一天的开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getDailyStartTime(TimeZone timeZone, Long timeStamp) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定某一天的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getDailyEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定某一周的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getWeekEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        // setFirstDayOfWeek是不生效的，还是会以周日为第一天。所以下面要加一天
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(7);
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getMonthStartTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getMonthEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }


    /**
     * 获取当年的最后时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getYearEndTime(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTimeInMillis();
    }

    public static String getSecondTimeStamp(long timeMilSecond) {
        if (timeMilSecond > 0) {
            return String.valueOf(timeMilSecond / 1000);
        } else {
            return null;
        }
    }

    public static boolean isSameYear(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean isCurrentYear(long time) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(System.currentTimeMillis());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * 非精确判断时间戳是否是秒
     *
     * @param time
     * @return
     */
    public static boolean isInSecondTime(long time) {
        // 100000000000毫秒约在1973年
        return time < 100000000000L;
    }

    /**
     * 时间格式转换 20210608 转 2021.06.08
     *
     * @param str
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatTime(String str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date date = formatter.parse(str);
            return YearMonthDay(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatLongTimeToDateString(long timestamp, TimeZone zone) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            formatter.setTimeZone(zone);
            return formatter.format(new Date(timestamp));
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatTradingTime(String tradingTime) {
        try {
            return tradingTime.substring(0, 5);
        } catch (Exception e) {
            return tradingTime;
        }
    }

    public static long dateToTimeStamp(String time, TimeZone zone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        sdf.setTimeZone(zone);
        try {
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getSystemTimeZone() {
        String id = "";
        try {
            id = TimeZone.getDefault().getID();
        } catch (Throwable tr) {
        }
        return id;
    }

    public static String getTimeHHMMss(long timeInSeconds) {
        if (timeInSeconds <= 0) {
            return "";
        }
        int seconds = (int) (timeInSeconds % 60);
        int minutes = (int) (timeInSeconds % 3600 / 60);// 转为分钟
        int hour = (int) (timeInSeconds / 3600);//小时数
        return String.format("%02d:%02d:%02d", hour, minutes, seconds);
    }

    public static String getDurationLabel(long timeInSeconds, String hourStr, String minuteStr) {
        int minutes = (int) (timeInSeconds / 60);// 转为分钟
        if (minutes <= 0) {
            return "";
        }
        int hour = minutes / 60;
        int minute = minutes % 60;
        if (hour == 1 && hourStr.endsWith("s")) {
            hourStr = hourStr.substring(0, hourStr.length() - 1);
        }
        if (minute == 1 && minuteStr.endsWith("s")) {
            minuteStr = minuteStr.substring(0, minuteStr.length() - 1);
        }
        if (hour > 0) {
            if (minute > 0) {
                return String.format("%d %s %d %s", hour, hourStr, minute, minuteStr);
            } else {
                return String.format("%d %s", hour, hourStr);
            }
        } else {
            return String.format("%d %s", minute, minuteStr);
        }
    }

    public static TimeZone usaTimeZone = TimeZone.getTimeZone("America/New_York");

    public static Date convertNowToUSATime() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        simpleDateFormat.setTimeZone(usaTimeZone);
        String usaTime = simpleDateFormat.format(now);

        return DateTimeUtils.parseStringToDate(usaTime, "yyyy-MM-dd");
    }

    public static String convertNowToUSAStringTime() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setTimeZone(usaTimeZone);
        String usaTime = simpleDateFormat.format(now);
        return usaTime;
    }

    public static String convertNowToUSAStringYear() {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        simpleDateFormat.setTimeZone(usaTimeZone);
        String usaTime = simpleDateFormat.format(now);
        return usaTime;
    }

    public static String convertSpecTimestampToUSATime(long timestamp) {
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        simpleDateFormat.setTimeZone(usaTimeZone);
        return simpleDateFormat.format(date);
    }

    private static final int MAX_ENTRIES = 2000;
    private static final long MILLIS_PER_DAY = 86400000;
    private static final LruCache<String, Integer> dateToEasternResultCache = new LruCache<>(MAX_ENTRIES);

    public static int getDaysUntilSpecZoneFromCache(String targetDate, TimeZone timeZone) {
        if (targetDate == null || targetDate.isEmpty()) {
            return 0;
        }
        String zoneId = timeZone.getID();
        // 组合时区和日期作为缓存键
        String cacheKey = zoneId + "_" + targetDate;

        long currentMillis = System.currentTimeMillis();
        long midnightMillis = calculateMidnightMillisInTimeZone(currentMillis, timeZone);

        // 检查是否可以从缓存中获得结果
        if (currentMillis < midnightMillis && midnightMillis - currentMillis < MILLIS_PER_DAY) {
            Integer cachedResult = dateToEasternResultCache.get(cacheKey);
            if (cachedResult != null) {
                return cachedResult;  // 直接返回缓存结果
            }
        }

        // 计算日期距离并更新缓存
        int result = calculateDaysUntil(targetDate, timeZone);
        dateToEasternResultCache.put(cacheKey, result);
        return result;
    }

    private static long calculateMidnightMillisInTimeZone(long currentMillis, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(currentMillis);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static int calculateDaysUntil(String targetDate, TimeZone timeZone) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return daysUntilUsingJavaTime(targetDate, timeZone);
        } else {
            return daysUntilUsingCalendar(targetDate, timeZone);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int daysUntilUsingJavaTime(String targetDate, TimeZone timeZone) {
        ZoneId zoneId = timeZone.toZoneId();
        LocalDate today = LocalDate.now(zoneId);
        LocalDate futureDate = LocalDate.parse(targetDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return (int) ChronoUnit.DAYS.between(today, futureDate);
    }

    private static int daysUntilUsingCalendar(String targetDate, TimeZone timeZone) {
        Calendar today = Calendar.getInstance(timeZone);
        Calendar futureDate = Calendar.getInstance(timeZone);
        int year = Integer.parseInt(targetDate.substring(0, 4));
        int month = Integer.parseInt(targetDate.substring(4, 6)) - 1;
        int day = Integer.parseInt(targetDate.substring(6, 8));
        futureDate.set(year, month, day, 0, 0, 0);
        futureDate.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long diff = futureDate.getTimeInMillis() - today.getTimeInMillis();
        return (int) (diff / MILLIS_PER_DAY);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String format(long dateLong, String format) {
        if (isInSecondTime(dateLong)) {
            dateLong = dateLong * 1000L;
        }
        return dateToString(new Date(dateLong), format);
    }

    public static String getEnMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int month = calendar.get(Calendar.MONTH);
        return EN_MONTH[month];
    }
}


