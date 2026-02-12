package com.sum.framework.manager

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

object ActivityManager {

    /**
     * Activity是否销毁
     * @param context
     */
    fun isActivityDestroy(context: Context): Boolean {
        val activity = findActivity(context)
        return if (activity != null) {
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
                activity.isDestroyed || activity.isFinishing
            } else activity.isFinishing
        } else true
    }

    /**
     * ContextWrapper是context的包装类，AppcompatActivity，service，application实际上都是ContextWrapper的子类
     * AppcompatXXX类的context都会被包装成TintContextWrapper
     * @param context
     */
    private fun findActivity(context: Context): Activity? {
        // 怎么判断context是不是Activity
        if (context is Activity) { // 这种方法不够严谨
            return context
        } else if (context is ContextWrapper) {
            return findActivity(context.baseContext)
        }
        return null
    }
}