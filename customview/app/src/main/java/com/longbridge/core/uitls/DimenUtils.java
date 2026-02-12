package com.longbridge.core.uitls;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.longbridge.core.comm.FApp;

public class DimenUtils {
    private static int mScreenWidth = -1;                           //手机屏幕的宽度
    private static int mScreenHeight = -1;                           //手机屏幕的高度
    private static float mScreenDensity = -1;                            //手机屏幕dpi
    private static int mRealWidth = -1;
    private static int mRealHeight = -1;
    private static final int sStatusbarHeight = -1;


    /**
     * 得到视图测量后的宽度
     *
     * @param rootView
     * @return
     */
    public static int getMeasuredWidth(View rootView) {
        if (rootView == null) {
            return 0;
        }
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return rootView.getMeasuredWidth();    //实际想在父view中得到的视图宽度
    }

    /**
     * 得到视图测量后的高度
     *
     * @param rootView
     * @return
     */
    public static int getMeasuredHeight(View rootView) {
        if (rootView == null) {
            return 0;
        }
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return rootView.getMeasuredHeight(); //实际想在父view中得到的视图高度
    }

    /**
     * 标题栏的高度
     *
     * @param window
     * @return
     */
    public static int getTitleBarHeight(Window window) {
        return window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * dip转pix
     *
     * @param dp
     * @return
     */
    public static int dp2px(float dp) {
        final float scale = FApp.get().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float dp2pxFloat(float dp) {
        final float scale = FApp.get().getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    // 将px值转换为dip或dp值
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * px转dp
     *
     * @param px
     * @return
     */
    public static int px2dp(float px) {
        final float scale = FApp.get().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, FApp.get().getResources().getDisplayMetrics());
    }


    // 将px值转换为sp值
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 获取屏幕的宽度和高度
     *
     * @param context
     * @return
     */
    public static int[] getScreenWidthAndHeight(Context context) {
        //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int[] screenDimen = new int[2];
        // mScreenWidth = wm.getDefaultDisplay().getWidth();
        //  mScreenHeight = wm.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        screenDimen[0] = mScreenWidth;
        screenDimen[1] = mScreenHeight;
        mScreenDensity = displayMetrics.density;

        return screenDimen;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
//        if (mScreenWidth == -1) {
        getScreenWidthAndHeight(context);
//        }
        return mScreenWidth;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
//        if (mScreenHeight == -1 || DeviceUtils.isPad(context)) {
        getScreenWidthAndHeight(context);
//        }
        return mScreenHeight;
    }

    public static float getScreenDensity(Context context) {
//        if (mScreenDensity == -1 || DeviceUtils.isPad(context)) {
        getScreenWidthAndHeight(context);
//        }
        return mScreenDensity;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


//    public static boolean isNavigationBarShow(Activity context){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            Display display = context.getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            Point realSize = new Point();
//            display.getSize(size);
//            display.getRealSize(realSize);
//            return realSize.y!=size.y;
//        }else {
//            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
//            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//            return !menu && !back;
//        }
//    }

    public static int getScreenHeight1(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return point.y;
    }

    /**
     * 获取实际屏幕宽高  包括状态栏
     *
     * @param context
     */
    public static void getRealWidthAndHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metric);
        mRealWidth = metric.widthPixels; // 宽度（PX）
        mRealHeight = metric.heightPixels;
    }

    public static int getmRealWidth(Context context) {
//        if (mRealWidth == -1) {
        getRealWidthAndHeight(context);
//        }
        return mRealWidth;
    }

    public static int getmRealHeight(Context context) {
//        if (mRealHeight == -1) {
        getRealWidthAndHeight(context);
//        }
        return mRealHeight;
    }

    public static boolean isPortrait() {
        return FApp.get().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int getRealTop(View view) {
        View v = view;
        int top = 0;
        while (v != null && v.getParent() != null) {
            top += v.getTop();
            if (v.getParent() == ((Activity) view.getContext()).findViewById(android.R.id.content)) {
                break;
            }
            v = (View) v.getParent();
        }
        return top;
    }

    public static int getWindowWidth() {
        WindowManager wm = (WindowManager) FApp.get().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    public static int getWindowHeight() {
        WindowManager wm = (WindowManager) FApp.get().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}
