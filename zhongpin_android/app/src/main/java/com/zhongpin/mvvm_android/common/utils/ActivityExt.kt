package com.zhongpin.mvvm_android.common.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.zhilianshidai.pindan.app.R

/**
 * 沉浸式状态栏
 */
fun FragmentActivity.immerseStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
    }
}

/**
 * 获取状态栏高度
 */
fun FragmentActivity.getStatusBarHeight(): Int {
    var statusBarHeight = 0
    try {
        var clz = Class.forName("com.android.internal.R\$dimen")
        var obj = clz!!.newInstance()
        var field = clz.getField("status_bar_height")
        var status_bar_height = Integer.parseInt(field!!.get(obj).toString())
        statusBarHeight = resources.getDimensionPixelSize(status_bar_height)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return statusBarHeight
}

/**
 * 获取屏幕宽度
 */
fun FragmentActivity.getScreenWidth(): Int {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}
/**
 * 获取屏幕高度
 */
fun FragmentActivity.getScreenHeight(): Int {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun Activity.startActivitySlideUp(target: Intent) {
    val options = ActivityOptions.makeCustomAnimation(
        this,
        R.anim.slide_up,
        R.anim.slide_down
    )
    startActivity(target, options.toBundle())
}

fun Activity.makeSlideUpDownAnimation(): ActivityOptionsCompat {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.slide_up,
        R.anim.slide_down
    )
    return options
}

fun Activity.hideKeyboard() {
    val target = currentFocus
    target?.clearFocus()
    if (target != null) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(target.windowToken, 0)
    }
}