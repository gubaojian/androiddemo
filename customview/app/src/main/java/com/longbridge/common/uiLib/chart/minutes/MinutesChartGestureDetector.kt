package com.longbridge.common.uiLib.chart.minutes

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * desc:
 * @author:k2
 * @Date:2025/8/22
 */
class MinutesChartGestureDetector(context: Context, val listener: OnGestureListener) : GestureDetector(context, listener) {
    private lateinit var lastMotionEvent: MotionEvent
    private var eX = 0F
    private var eY = 0F
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionEvent = ev
                eX = lastMotionEvent.x
                eY = lastMotionEvent.y
            }

            MotionEvent.ACTION_MOVE -> {
                listener.onScroll(lastMotionEvent, ev, ev.x - eX, ev.y - eY)
                return true
            }

            MotionEvent.ACTION_UP -> {
                listener.onSingleTapUp(ev)
            }
        }
        return super.onTouchEvent(ev)
    }
}