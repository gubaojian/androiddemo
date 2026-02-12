package com.zhongpin.mvvm_android.base.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.internal.ViewUtils


object HomeNavBarConfig {
    var bottomMargin:Int = 0;
}

fun Activity.setHomeNavBarMarin(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = insets.bottom
        }
        HomeNavBarConfig.bottomMargin = insets.bottom
        WindowInsetsCompat.CONSUMED
    }
}

fun View.forceSetNavBarMarin() {
    val view = this;
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
        val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = insets.bottom
        }
        windowInsets
    }
}


/**
 *  参考的 BottomNavigationView实现代码
 * */
@SuppressLint("RestrictedApi")
fun View.setBottomNavigationViewInsets() {
    val view: View = this;
    ViewUtils.doOnApplyWindowInsets(
        view,
        object : ViewUtils.OnApplyWindowInsetsListener {
            override fun onApplyWindowInsets(
                view: View,
                insets: WindowInsetsCompat,
                initialPadding: ViewUtils.RelativePadding
            ): WindowInsetsCompat {
                // Apply the bottom, start, and end padding for a BottomNavigationView
                // to dodge the system navigation bar
                initialPadding.bottom += insets.getSystemWindowInsetBottom()

                val isRtl = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL
                val systemWindowInsetLeft = insets.getSystemWindowInsetLeft()
                val systemWindowInsetRight = insets.getSystemWindowInsetRight()
                initialPadding.start += if (isRtl) systemWindowInsetRight else systemWindowInsetLeft
                initialPadding.end += if (isRtl) systemWindowInsetLeft else systemWindowInsetRight
                initialPadding.applyToView(view)
                return insets
            }
        })
}

/**
 *
 *
 *
 *  private void applyWindowInsets() {
 *     ViewUtils.doOnApplyWindowInsets(
 *         this,
 *         new ViewUtils.OnApplyWindowInsetsListener() {
 *           @NonNull
 *           @Override
 *           public WindowInsetsCompat onApplyWindowInsets(
 *               View view,
 *               @NonNull WindowInsetsCompat insets,
 *               @NonNull RelativePadding initialPadding) {
 *             // Apply the bottom, start, and end padding for a BottomNavigationView
 *             // to dodge the system navigation bar
 *             initialPadding.bottom += insets.getSystemWindowInsetBottom();
 *
 *             boolean isRtl = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
 *             int systemWindowInsetLeft = insets.getSystemWindowInsetLeft();
 *             int systemWindowInsetRight = insets.getSystemWindowInsetRight();
 *             initialPadding.start += isRtl ? systemWindowInsetRight : systemWindowInsetLeft;
 *             initialPadding.end += isRtl ? systemWindowInsetLeft : systemWindowInsetRight;
 *             initialPadding.applyToView(view);
 *             return insets;
 *           }
 *         });
 *   }
 *
 *
 * */