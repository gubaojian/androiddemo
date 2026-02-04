package com.longbridge.core.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;


import com.lb.price.one.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Application Holder
 * Created by lichaojun on 2017/1/19.
 */
public class FApp {
    @SuppressLint("StaticFieldLeak")
    private static Application app;
    private static WeakReference<Activity> curActivity;
    private static boolean isProduct = true;

    public static void init(Application application, String flavorEnv, boolean isDebug) {
        app = application;
        isProduct = !(isDebug || "ts".equalsIgnoreCase(flavorEnv));

    }

    public static Application get() {
        if (app == null) {
            throw new NullPointerException("Application must init.");
        }
        return app;
    }

    public static Resources getResources() {
        return app.getResources();
    }

    public static void setCurActivity(Activity activity) {
        if (curActivity != null) {
            curActivity = null;
        }
        curActivity = new WeakReference<>(activity);
    }

    public static Activity curActivity() {
        return curActivity != null ? curActivity.get() : null;
    }

    public static void setCurActivityToNull(Activity activity) {
        if (curActivity != null && curActivity.get() == activity) {
            curActivity = null;
        }
    }

    /**
     * 获取 Activity 堆栈中所有 Activity
     * @return 所有 Activity
     */
    public static List<Activity> getAllActivitys(){
        List<Activity> list = new ArrayList<>();
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            //获取主线程对象
            Object activityThreadObject = currentActivityThread.invoke(null);
            Field mActivitiesField = activityThread.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Map<Object,Object> mActivities = (Map<Object,Object>) mActivitiesField.get(activityThreadObject);
            for (Map.Entry<Object,Object> entry : mActivities.entrySet()){
                Object value = entry.getValue();
                Class<?> activityClientRecordClass = value.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Object o = activityField.get(value);
                list.add((Activity) o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getAppName(){
        return get().getString(R.string.app_lb_name);
    }

    public static boolean isProduct() {
        return isProduct;
    }
}
