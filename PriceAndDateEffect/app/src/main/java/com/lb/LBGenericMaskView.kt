package com.lb

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.lb.price.one.R
import kotlin.math.roundToInt

/**
 * 中间抠空的蒙层视图
 */
class LBGenericMaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val TAG = "LBGenericMaskView_"
    }

    /** 抠空区域的大小（默认 80x80） */
    var clearRectSize: Size = Size(200.dpToPx(), 80.dpToPx())
        set(value) {
            val oldSize = field
            field = value
            android.util.Log.d(TAG, "clearRectSize set: ${oldSize.width}x${oldSize.height} -> ${value.width}x${value.height}")
            animatePathChange(oldSize, value)
        }

    /** 抠空区域中心点的 X（不设置则默认使用视图中心 width/2f） */
    var clearRectCenterX: Float? = null
        set(value) {
            field = value
            android.util.Log.d(TAG, "clearRectCenterX set: $value (will updateMaskPath)")
            updateMaskPath(currentAnimatedSize)
        }

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb((0.4f * 255).toInt(), 255, 255, 255)
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // todo  use local color
        color = context.getColor(R.color.green_line)
        style = Paint.Style.STROKE
        strokeWidth = 1.dpToPx().toFloat()
    }
    private var maskPath = Path()
    private var borderPath = Path()
    private var currentAnimatedSize = clearRectSize

    init {
        setBackgroundColor(Color.TRANSPARENT)
        isClickable = false
        isFocusable = false
        android.util.Log.d(TAG, "init")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        android.util.Log.d(TAG, "onLayout(changed=$changed,l=$left,t=$top,r=$right,b=$bottom)")
        super.onLayout(changed, left, top, right, bottom)
        updateMaskPath(currentAnimatedSize)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        android.util.Log.d(TAG, "onMeasure(wSpec=${android.view.View.MeasureSpec.toString(widthMeasureSpec)}, hSpec=${android.view.View.MeasureSpec.toString(heightMeasureSpec)})")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        android.util.Log.d(TAG, "onMeasure done measuredWidth=$measuredWidth measuredHeight=$measuredHeight")
    }
    override fun requestLayout() {
       // android.util.Log.d(TAG, "requestLayout() called\ncaller=\n${android.util.Log.getStackTraceString(Throwable())}")
        super.requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(maskPath, maskPaint)
        canvas.drawPath(borderPath, borderPaint)
    }

    private fun updateMaskPath(size: Size) {
        android.util.Log.d(TAG, "updateMaskPath size=${size.width}x${size.height} centerX=$clearRectCenterX viewW=$width viewH=$height")
        val bounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val centerX = clearRectCenterX ?: (width / 2f)
        val centerY = height / 2f
        val clearRect = RectF(
            centerX - size.width / 2f,
            centerY - size.height / 2f,
            centerX + size.width / 2f,
            centerY + size.height / 2f
        )

        maskPath.reset()
        maskPath.fillType = Path.FillType.EVEN_ODD
        maskPath.addRect(bounds, Path.Direction.CW)
        maskPath.addRoundRect(clearRect, 8.dpToPx().toFloat(), 8.dpToPx().toFloat(), Path.Direction.CW)

        borderPath.reset()
        borderPath.addRoundRect(clearRect, 8.dpToPx().toFloat(), 8.dpToPx().toFloat(), Path.Direction.CW)
        android.util.Log.d(TAG, "invalidate()")
        invalidate()
    }

    private fun animatePathChange(oldSize: Size, newSize: Size) {
        android.util.Log.d(TAG, "animatePathChange from ${oldSize.width}x${oldSize.height} to ${newSize.width}x${newSize.height}")
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val animatedWidth = oldSize.width + (newSize.width - oldSize.width) * progress
                val animatedHeight = oldSize.height + (newSize.height - oldSize.height) * progress
                currentAnimatedSize = Size(animatedWidth.toInt(), animatedHeight.toInt())
                updateMaskPath(currentAnimatedSize)
            }
            doOnEnd {
                currentAnimatedSize = newSize
                android.util.Log.d(TAG, "animatePathChange end -> apply final size ${newSize.width}x${newSize.height}")
                updateMaskPath(currentAnimatedSize)
            }
        }
        animator.start()
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()
    private fun Float.dpToPx(): Float = this * context.resources.displayMetrics.density
}


