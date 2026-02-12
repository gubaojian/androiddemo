package com.longbridge.common.uiLib.chart.minutes

import androidx.core.content.ContextCompat
import com.longbridge.market.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import com.longbridge.common.kotlin.expansion.dp
import com.longbridge.common.uiLib.drawableview.BaseCustomView

/**
 * @author Kyle
 * on 2020/7/24
 */
open class MinutesChart(context: Context, attrs: AttributeSet?) : BaseCustomView(context, attrs) {
    protected var mLineColor = 0
    protected var mCustomTitleHeight: Float
    protected var mStartMargin: Float
    private lateinit var mPaint: Paint
    private lateinit var mProxy: MinutesDrawProxy
    protected val mLinePadding: Float
    protected var mShowLine = true

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MinutesChart)
        mLineColor = ta.getResourceId(R.styleable.MinutesChart_minutes_line_color, R.color.common_color_normal)
        mCustomTitleHeight = ta.getDimension(R.styleable.MinutesChart_minutes_custom_title_height, 0.dp)
        mStartMargin = ta.getDimension(R.styleable.MinutesChart_minutes_line_startmargin, 0.dp)
        mLinePadding = ta.getDimension(R.styleable.MinutesChart_minutes_line_padding_hori, 0F)
        mShowLine = ta.getBoolean(R.styleable.MinutesChart_minutes_show_line, true)
        ta.recycle()
        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
//        mPaint.textLocales = LocaleList.getDefault()
        invalidate()
    }

    open class DefaultDataObserver(var chart: MinutesChart) : MinutesDrawProxy.DataChangeObserver {
        override fun refreshUi() {
            chart.invalidate()
        }

        override fun post(runnable: Runnable?) {
            chart.post(runnable)
        }

        override fun postDelay(runnable: Runnable?, delay: Long) {
            chart.postDelayed(runnable, delay)
        }

        override fun removeRunnable(runnable: Runnable?) {
            chart.removeCallbacks(runnable)
        }

        override fun onDataChange() {
            chart.post {
                doOnDataChange()
            }
        }

        public fun doOnDataChange() {
            chart.property.getDrawProperty(chart.getDrawKLineWidth(), chart.mStartMargin, chart.getTopY(), chart.getBottomY())
            chart.invalidate()
        }
    }

    fun setDrawProxy(proxy: MinutesDrawProxy) {
        mProxy = proxy
        mProxy.setTouchProxy(object : TouchProxy {
            override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                parent.requestDisallowInterceptTouchEvent(disallowIntercept)
            }
        })
        mProxy.setDataObserver(DefaultDataObserver(this), this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (::mProxy.isInitialized && mProxy.getGestureDetector(context) != null) {
            return mProxy.getGestureDetector(context).onTouchEvent(event)
        }
        return false //如果没有设置 GestureDetector ， 则不捕获事件，传递到上层处理
//        return super.onTouchEvent(event)
    }

    private lateinit var property: DrawProperty
    fun setDrawProperty(property: DrawProperty) {
        this.property = property
    }

    interface DrawProperty {
        fun getDrawProperty(drawWidth: Float, startMargin: Float, topY: Float, bottomY: Float)
    }

    override fun onDraw(canvas: Canvas) {
        drawLine(canvas)
        if (::mProxy.isInitialized) {
            mProxy.onDraw(canvas, getTopY(), getBottomY(), mCustomTitleHeight)
        }
    }

    private fun getDrawKLineWidth(): Float {
        return measuredWidth - mStartMargin
    }

    private fun drawLine(canvas: Canvas?) {
        if (!mShowLine) return
        mPaint.color = ContextCompat.getColor(context, mLineColor)
        if (::mProxy.isInitialized && mProxy.drawTopLine()) {
            canvas?.drawLine(mLinePadding, getTopY(), measuredWidth.toFloat() - mLinePadding, getTopY(), mPaint)
        }
        if (::mProxy.isInitialized && mProxy.drawBottomLine()) {
            canvas?.drawLine(mLinePadding, getBottomY(), measuredWidth.toFloat() - mLinePadding, getBottomY(), mPaint)
        }
    }

    open fun getTopY(): Float {
        return 2.dp
    }

    open fun getBottomY(): Float {
        return measuredHeight - 2.dp - mCustomTitleHeight
    }


}