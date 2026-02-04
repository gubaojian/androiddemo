package com.lb.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {

    private final static String DEFAULT_TAG = "longbridge";
    private static boolean USE_LOG = isDebugBuild();// true有log输出

    private static boolean isDebugBuild() {
        try {
            Class<?> cls = Class.forName("com.lb.price.one.BuildConfig");
            java.lang.reflect.Field field = cls.getField("DEBUG");
            return field.getBoolean(null);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    private static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (USE_LOG && !TextUtils.isEmpty(msg)) {
            if (!isDebugBuild()) {
                Log.println(Log.VERBOSE, tag, msg);
                return;
            }
            Log.v(tag, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (USE_LOG) {
            Log.v(tag, msg, tr);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (USE_LOG && !TextUtils.isEmpty(msg)) {
            if (!isDebugBuild()) {
                Log.println(Log.DEBUG, tag, msg);
                return;
            }
            Log.d(tag, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (USE_LOG) {
            if (!isDebugBuild()) {
                Log.println(Log.DEBUG, tag, msg + " " + Log.getStackTraceString(tr));
                return;
            }
            Log.d(tag, msg, tr);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void printStackTrace(String tag) {
        if (isDebugBuild()) {
            if (TextUtils.isEmpty(tag)) {
                tag = DEFAULT_TAG;
            }
            LogUtils.d(tag, "CallerStackTrace " + Log.getStackTraceString(new Exception(tag)));
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (USE_LOG && !TextUtils.isEmpty(msg)) {
            if (!isDebugBuild()) {
                Log.println(Log.INFO, tag, msg);
                return;
            }
            Log.i(tag, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (USE_LOG) {
            if (!isDebugBuild()) {
                Log.println(Log.INFO, tag, msg + " " + Log.getStackTraceString(tr));
                return;
            }
            Log.i(tag, msg, tr);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (USE_LOG && !TextUtils.isEmpty(msg)) {
            Log.w(tag, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (USE_LOG) {
            if (!isDebugBuild()) {
                Log.println(Log.WARN, tag, msg + " " + Log.getStackTraceString(tr));
                return;
            }
            Log.w(tag, msg, tr);
        }
    }

    /*
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     *
     * @param tr An exception to log
     */
    public static void w(String tag, Throwable tr) {
        if (USE_LOG) {
            Log.w(tag, tr);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (USE_LOG && !TextUtils.isEmpty(msg)) {
            if (!isDebugBuild()) {
                Log.println(Log.ERROR, tag, msg);
                return;
            }
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, Throwable tr) {
        if (USE_LOG) {
            Log.e(tag, getStackTraceString(tr));
        }
    }

    /***
     * this log will always print on release apk, just for fix crash, if you want some business log, pls use VLog
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * */
    public static void onlineE(String tag, String msg) {
        Log.println(Log.ERROR, tag, msg);
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (USE_LOG) {
            if (!isDebugBuild()) {
                Log.println(Log.ERROR, tag, msg  + " " + Log.getStackTraceString(tr));
                return;
            }
            Log.e(tag, msg, tr);
        }
    }

    public static void v(Object msg) {
        if (USE_LOG && msg != null) {
            v(DEFAULT_TAG, msg.toString());
        }
    }

    public static void d(Object msg) {
        if (USE_LOG && msg != null) {
            d(DEFAULT_TAG, msg.toString());
        }
    }

    public static void i(Object msg) {
        if (USE_LOG && msg != null) {
            i(DEFAULT_TAG, msg.toString());
        }
    }

    public static void w(Object msg) {
        if (USE_LOG && msg != null) {
            w(DEFAULT_TAG, msg.toString());
        }
    }

    public static void e(Object msg) {
        if (USE_LOG && msg != null) {
            e(DEFAULT_TAG, msg.toString());
        }
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void vv(String format, Object... args) {
        vv(DEFAULT_TAG, format, args);
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void dd(String format, Object... args) {
        dd(DEFAULT_TAG, format, args);
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ii(String format, Object... args) {
        ii(DEFAULT_TAG, format, args);
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ww(String format, Object... args) {
        ww(DEFAULT_TAG, format, args);
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ee(String format, Object... args) {
        ee(DEFAULT_TAG, format, args);
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void vv(String tag, String format, Object... args) {
        if (USE_LOG && !TextUtils.isEmpty(format)) {
            v(tag, String.format(format, args));
        }
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void dd(String tag, String format, Object... args) {
        if (USE_LOG && !TextUtils.isEmpty(format)) {
            d(tag, String.format(format, args));
        }
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ii(String tag, String format, Object... args) {
        if (USE_LOG && !TextUtils.isEmpty(format)) {
            i(tag, String.format(format, args));
        }
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ww(String tag, String format, Object... args) {
        if (USE_LOG && !TextUtils.isEmpty(format)) {
            w(tag, String.format(format, args));
        }
    }

    /**
     * java 使用，kotlin 调用可能异常
     *
     * @param format
     * @param args
     */
    public static void ee(String tag, String format, Object... args) {
        if (USE_LOG && !TextUtils.isEmpty(format)) {
            e(tag, String.format(format, args));
        }
    }

    public static void d(String msg, Throwable tr) {
        d(DEFAULT_TAG, msg, tr);
    }

    public static void d(String tag, String key, String value) {
        if (USE_LOG) {
            d(tag, key + " -- " + value);
        }
    }

    public static void setDebug(boolean debug) {
        USE_LOG = debug;
    }

    /**
     * 获取异常的完整堆栈跟踪信息
     */
    public static String logThrowable(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
