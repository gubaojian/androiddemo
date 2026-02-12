package com.zhongpin.mvvm_android.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class ClickRecyclerView  @JvmOverloads constructor (context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr)  {

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_UP) {
            val isInItem = isPointInAnyItem(e.x, e.y)
            if (!isInItem) {
                performClick()
                return false
            }
        }
        return super.onTouchEvent(e)
    }
    private fun isPointInAnyItem(x: Float, y: Float): Boolean {
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val rect = Rect()
            child.getHitRect(rect)
            if (rect.contains(x.toInt(), y.toInt())) {
                return true
            }
        }
        return false
    }
}