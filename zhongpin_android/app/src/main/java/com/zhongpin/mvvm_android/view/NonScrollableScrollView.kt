package com.zhongpin.mvvm_android.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class NonScrollableScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 禁用触摸事件
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // 禁用拦截触摸事件
        return false
    }
}