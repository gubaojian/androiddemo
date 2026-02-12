package com.zhongpin.mvvm_android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import kotlin.collections.set

class MultiStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr : Int = 0,
    defStyleRes : Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes)  {

    val stateViewsMap: MutableMap<Int, View> = mutableMapOf()

    fun addStateView(layoutResId: Int) {
        if (stateViewsMap.contains(layoutResId)) {
            return
        }
        val stateView = LayoutInflater.from(context).inflate(layoutResId, this, false)
        addView(stateView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ))
        stateViewsMap[layoutResId] = stateView
        if (stateViewsMap.size == 1) {
            stateView.visible()
        } else {
            stateView.gone()
        }
        requestLayout()
    }

    fun showStateView(layoutResId: Int) {
        stateViewsMap.keys.forEach {  keyIt ->
            stateViewsMap[keyIt]?.gone()
        }
        stateViewsMap[layoutResId]?.visible()
        requestLayout()
    }

}