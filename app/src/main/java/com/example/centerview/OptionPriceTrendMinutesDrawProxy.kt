package com.example.centerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.longbridge.common.kotlin.expansion.dp
import com.longbridge.common.uiLib.chart.minutes.MinutesDrawProxy
import com.longbridge.core.comm.FApp
import com.longbridge.core.uitls.DimenUtils
import java.util.Locale

class OptionPriceTrendMinutesDrawProxy(val context: Context) : MinutesDrawProxy() {

    //K线画笔
    private val mLinePaint = Paint().apply {
        strokeWidth = 1.5F.dp
        color = ContextCompat.getColor(context, R.color.dash_line_color)
        style = Paint.Style.STROKE
    };

    //虚线画笔
    private val mDashPaint = Paint().apply{
        style = Paint.Style.STROKE
        strokeWidth = 1.0F.dp
        color = ContextCompat.getColor(context, R.color.dash_line_color)
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0F)
    }

    private val mGradientPaint = Paint().apply{
        style = Paint.Style.FILL
    }

    private val mPriceTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_color_1)
        textSize = 12.dp
    }

    private val mYLabelTextPaint = Paint().apply{
        color = ContextCompat.getColor(context, R.color.text_color_2)
        textSize = 12.dp
    }

    private val mRectTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_color_1)
        textSize = 12.dp
    }


    private val mBottomLinePaint = Paint().apply {
        strokeWidth = 1.0F.dp
        color = ContextCompat.getColor(context, R.color.option_bottom_line_color)
        style = Paint.Style.STROKE
    }





    var points: MutableList<KLinePoint> = mutableListOf();

    var yStartLabel = "09/05"
    var yEndLabel = "09/31"

    var yRectEndLabel = "09/31"

    override fun onDraw(
        canvas: Canvas,
        topY: Float,
        bottomY: Float,
        customTitleHeight: Float
    ) {
        drawDashLine(canvas, topY, bottomY)
        drawBottomLineLabel(canvas, topY, bottomY)
        drawRightPath(canvas, topY, bottomY)
        drawLinePath(canvas, topY, bottomY)
        drawYLabel(canvas, topY, bottomY)
        drawRectEndLabel(canvas, topY, bottomY)
    }

    fun drawDashLine(
        canvas: Canvas,
        topY: Float,
        bottomY: Float) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp;
        val topYInner = topY + 20.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp
        val middleY = topYInner + (bottomYInner - topYInner)/2.0f
        canvas.drawLine(16.dp, middleY,
            drawWidth - 16.dp,middleY, mDashPaint)

    }

    fun drawBottomLineLabel(
        canvas: Canvas,
        topY: Float,
        bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp;
        val topYInner = topY + 20.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp
        canvas.drawLine( 0.dp, bottomYInner, drawWidth, bottomYInner, mBottomLinePaint)
    }
    fun drawYLabel(
        canvas: Canvas,
        topY: Float,
        bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp;
        val topYInner = topY + 20.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp


        canvas.drawText(yStartLabel, startOffsetX, bottomYInner + 16.dp, mYLabelTextPaint)

        val yEndLabelWidth = mPriceTextPaint.measureText(yEndLabel)
        canvas.drawText(yEndLabel,
            endOffsetX - yEndLabelWidth - 4.dp ,
            bottomYInner + 16.dp, mYLabelTextPaint)


    }

    fun drawRectEndLabel(
        canvas: Canvas,
        topY: Float,
        bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp;
        val topYInner = topY + 20.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp
        val yRectEndLabelWidth = mRectTextPaint.measureText(yRectEndLabel)
        canvas.drawText(yRectEndLabel,
            drawWidth - 16.dp - yRectEndLabelWidth - 4.dp ,
            bottomYInner + 16.dp, mRectTextPaint)
    }

    fun drawLinePath(
        canvas: Canvas,
        topY: Float,
        bottomY: Float
    ) {
        var minPrice = Float.MAX_VALUE
        var maxPrice = Float.MIN_VALUE
        points.forEach {
            val price = it.price.toFloatOrNull() ?: 0.0f
            if (price < minPrice) {
                minPrice = price
            }
            if (price > maxPrice) {
                maxPrice = price
            }
        }

        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp;
        val topYInner = topY + 40.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp - 20.dp
        var minX = 0.0f;
        var minY = 0.0f;
        var maxX = 0.0f;
        var maxY = 0.0f;
        var maxPriceLabel:String = ""
        var minPriceLabel:String = ""
        mLinePath.reset()
        points.forEachIndexed {
            index, point ->
            val price = point.price.toFloatOrNull() ?: 0.0f
            val x = startOffsetX + (endOffsetX - startOffsetX)*index/(points.size.toFloat() - 1.0f);
            val y = bottomYInner - (bottomYInner - topYInner)*((price-minPrice)/(maxPrice-minPrice))
            if (index == 0) {
                mLinePath.moveTo(x, y)
            } else {
                mLinePath.lineTo(x, y)
            }
            if (price == maxPrice) {
                if (maxX <= 0.0f) {
                    maxX = x
                    maxY = y
                    maxPriceLabel = String.format(Locale.getDefault(), "%.2f", price)
                }
            }
            if (price == minPrice) {
                if (minX <= 0.0f) {
                    minX = x
                    minY = y
                    minPriceLabel = String.format(Locale.getDefault(), "%.2f", price)
                }
            }
        }
        canvas.drawPath(mLinePath, mLinePaint)

        val maxPriceLabelWidth = mPriceTextPaint.measureText(maxPriceLabel)
        if (maxX +  4.dp  + maxPriceLabelWidth > (drawWidth - 16.dp)) {
            canvas.drawText(maxPriceLabel, maxX -maxPriceLabelWidth -  4.dp, maxY, mPriceTextPaint)
        } else {
            canvas.drawText(maxPriceLabel, maxX +  4.dp, maxY, mPriceTextPaint)
        }

        val minPriceLabelWidth = mPriceTextPaint.measureText(minPriceLabel)
        canvas.drawText(minPriceLabel, minX - minPriceLabelWidth - 4.dp, minY, mPriceTextPaint)


    }
    fun drawRightPath(
        canvas: Canvas,
        topY: Float,
        bottomY: Float
    ) {
        val topYInner = topY + 20.dp;
        val bottomYInner = 248.dp - 2.dp - 40.dp
        mRightPath.reset()
        mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner)
        mRightPath.lineTo(drawWidth - 100.dp - 16.dp, bottomYInner)
        mRightPath.lineTo(drawWidth- 16.dp, bottomYInner)
        mRightPath.lineTo(drawWidth- 16.dp, topYInner)
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            0.0f, 0.0f, drawWidth, 0.0f,
            0xFF00ADA2.toInt(), 0xFFFFFF.toInt(),
            Shader.TileMode.REPEAT
        )
        canvas.drawPath(mRightPath, mGradientPaint)
    }

    override fun initPath(
        width: Float,
        startMargin: Float,
        topY: Float,
        bottomY: Float
    ) {
        this.drawWidth = if (width == 0F) (DimenUtils.getScreenWidth(FApp.get()).toFloat()) else width
    }

    fun initData(points: MutableList<KLinePoint>) {
        this.points = points
        notifyDataChange()
    }

    fun notifyDataChange() {
        this.mObserver?.onDataChange()
    }

    private var mRightPath = Path()

    private var mLinePath = Path()
    private var drawWidth = 0F

    override fun drawTopLine(): Boolean {
        return false
    }

    override fun drawBottomLine(): Boolean {
        return false
    }
}