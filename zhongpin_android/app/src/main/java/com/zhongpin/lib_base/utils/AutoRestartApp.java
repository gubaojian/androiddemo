package com.zhongpin.lib_base.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.zhongpin.mvvm_android.ui.main.MainActivity;

public class AutoRestartApp implements Thread.UncaughtExceptionHandler {
    private Context context;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    public AutoRestartApp(Context context) {
        this.context = context;
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
    private static boolean handleException = false;
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (handleException) {
            return;
        }
        handleException = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (uncaughtExceptionHandler != null) {
                    uncaughtExceptionHandler.uncaughtException(t, e);
                }
            }
        }).start();
        if (Looper.getMainLooper().getThread() == t) {
            restartApp();
        } else {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restartApp();
                }
            }, 500);
        }
    }

    private void restartApp() {
        ActivityStackManager.INSTANCE.popAllActivity();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
