package com.longbridge.market.mvp.ui.widget.option.strategy.guide.proxy

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.example.centerview.R
import com.longbridge.common.kotlin.expansion.dp
import com.longbridge.common.uiLib.chart.minutes.MinutesDrawProxy
import com.longbridge.core.comm.FApp
import com.longbridge.core.uitls.DimenUtils
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.toColorInt

class OptionPriceDrawProxy(val context: Context) : MinutesDrawProxy() {

    //K线画笔
    val mLinePaint = Paint().apply {
        strokeWidth = 1.5F.dp
        color = ContextCompat.getColor(context, R.color.market_option_dash_line_color)
        style = Paint.Style.STROKE
    }

    //虚线画笔
    val mDashPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.0F.dp
        color = ContextCompat.getColor(context, R.color.market_option_dash_line_color)
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0F)
    }

    val mGradientPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    val mPriceTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_color_1)
        textSize = 12.dp
    }

    val mTrendPriceTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.common_color_main_reverse)
        textSize = 12.dp
    }

    val mYLabelTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_color_2)
        textSize = 12.dp
    }

    val mRectTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_color_1)
        textSize = 12.dp
    }

    val mBottomLinePaint = Paint().apply {
        strokeWidth = 1.0F.dp
        color = ContextCompat.getColor(context, R.color.market_option_option_bottom_line_color)
        style = Paint.Style.STROKE
    }

    val bgTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.common_color_new_brand1)
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    var dateRectStartColor: Int =
        ContextCompat.getColor(context, R.color.market_option_date_linear_rect_start_color)
    var dateRectEndColor: Int =
        ContextCompat.getColor(context, R.color.market_option_date_linear_rect_end_color)

    var priceTrendRectStartColor: Int =
        ContextCompat.getColor(context, R.color.market_option_price_trend_linear_rect_start_color)
    var priceTrendRectEndColor: Int =
        ContextCompat.getColor(context, R.color.market_option_price_trend_linear_rect_end_color)

    var points: MutableList<KLinePoint> = mutableListOf()

    var yStartLabel = "09/05"
    var yEndLabel = "09/31"
    var yRectEndLabel = "09/31"
    var yRectEndTopLabel = "in 28 days"

    var targetTrendPrice = "120.00"
    var targetPrice: Float = 120.0f

    var targetTrendPrice2 = "120.00"
    var targetPrice2: Float = 120.0f

    var drawScene: String = "choose_date"

    override fun onDraw(
        canvas: Canvas, topY: Float, bottomY: Float, customTitleHeight: Float
    ) {
        if (points.size <= 2) {
            return
        }
        drawBottomLineLabel(canvas, topY, bottomY)
        if (drawScene == "choose_date") {
            drawChooseDateRightPath(canvas, topY, bottomY)
        } else if (drawScene == "single_price_trend") {
            drawSinglePriceTrendPath(canvas, topY, bottomY)
        } else if (drawScene == "two_price_move_at_least") {
            drawTwoPriceTrendPathForMoveAtLeastTop(canvas, topY, bottomY)
            drawTwoPriceTrendPathForMoveAtLeastBottom(canvas, topY, bottomY)
        } else if (drawScene == "two_price_move_with_in") {
            drawTwoPriceTrendPathForMoveWithIn(canvas, topY, bottomY)
        } else if (drawScene == "connect_options_premium_not_exceed") {
            drawSinglePriceTrendPathNotExceed(canvas, topY, bottomY)
        } else if (drawScene == "connect_options_premium_not_fall_below") {
            drawSinglePriceTrendPathNotFallBelow(canvas, topY, bottomY)
        }
        drawLinePath(canvas, topY, bottomY)
        drawYLabel(canvas, topY, bottomY)
    }

    fun drawDashLine(
        canvas: Canvas, topY: Float, bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp

        val point = points.lastOrNull()
        point?.let {
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
            val index = points.size - 1
            val price = point.price.toFloatOrNull() ?: 0.0f
            val x =
                startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
            val y =
                bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))

            canvas.drawLine(
                16.dp, y, drawWidth - 16.dp, y, mDashPaint
            )
        }
    }

    fun drawBottomLineLabel(
        canvas: Canvas, topY: Float, bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 0.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp
        canvas.drawLine(startOffsetX, topYInner, drawWidth - 16.dp, topYInner, mBottomLinePaint)
        canvas.drawLine(0.dp, bottomYInner, drawWidth, bottomYInner, mBottomLinePaint)
    }

    fun drawYLabel(
        canvas: Canvas, topY: Float, bottomY: Float
    ) {
        var startOffsetX = 16.dp + 2.dp
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 0.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp

        canvas.drawText(yStartLabel, startOffsetX, bottomYInner + 16.dp, mYLabelTextPaint)

        val yEndLabelWidth = mPriceTextPaint.measureText(yEndLabel)
        canvas.drawText(
            yEndLabel, endOffsetX - yEndLabelWidth - 4.dp, bottomYInner + 16.dp, mYLabelTextPaint
        )

        val yRectEndTopLabelWidth = mPriceTextPaint.measureText(yRectEndTopLabel)
        canvas.drawText(
            yRectEndTopLabel,
            drawWidth - 16.dp - yRectEndTopLabelWidth - 4.dp,
            bottomYInner - 4.dp,
            mYLabelTextPaint
        )
        val yRectEndLabelWidth = mRectTextPaint.measureText(yRectEndLabel)
        canvas.drawText(
            yRectEndLabel,
            drawWidth - 16.dp - yRectEndLabelWidth - 4.dp,
            bottomYInner + 16.dp,
            mRectTextPaint
        )
    }

    fun drawLinePath(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        var minX = 0.0f
        var minY = 0.0f
        var maxX = 0.0f
        var maxY = 0.0f
        var maxPriceLabel: String = ""
        var minPriceLabel: String = ""
        mLinePath.reset()
        points.forEachIndexed { index, point ->
            val price = point.price.toFloatOrNull() ?: 0.0f
            val x =
                startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
            val y =
                bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
            if (index == 0) {
                mLinePath.moveTo(x, y)
            } else {
                mLinePath.lineTo(x, y)
            }
            if (price == maxPrice) {
                if (maxX <= 0.0f) {
                    maxX = x
                    maxY = y
                    maxPriceLabel = String.Companion.format(Locale.getDefault(), "%.2f", price)
                }
            }
            if (price == minPrice) {
                if (minX <= 0.0f) {
                    minX = x
                    minY = y
                    minPriceLabel = String.Companion.format(Locale.getDefault(), "%.2f", price)
                }
            }
            if (index == points.size - 1) {
                canvas.drawLine(
                    16.dp, y, drawWidth - 16.dp, y, mDashPaint
                )
                val lastPrice = String.format(Locale.getDefault(), "%s%.2f", "$", price)
                val lastPriceWidth = mPriceTextPaint.measureText(lastPrice)
                val offsetX = (100.dp - lastPriceWidth) / 2
                canvas.drawText(lastPrice, x + offsetX, y + 16.dp, mPriceTextPaint)
            }
        }
        canvas.drawPath(mLinePath, mLinePaint)

        val maxPriceLabelWidth = mPriceTextPaint.measureText(maxPriceLabel)
        if (maxX + 4.dp + maxPriceLabelWidth > (drawWidth - 16.dp)) {
            canvas.drawText(maxPriceLabel, maxX - maxPriceLabelWidth - 4.dp, maxY, mPriceTextPaint)
        } else {
            canvas.drawText(maxPriceLabel, maxX + 4.dp, maxY, mPriceTextPaint)
        }

        val minPriceLabelWidth = mPriceTextPaint.measureText(minPriceLabel)
        canvas.drawText(minPriceLabel, minX - minPriceLabelWidth - 4.dp, minY, mPriceTextPaint)
    }

    fun drawChooseDateRightPath(
        canvas: Canvas, topY: Float, bottomY: Float
    ) {
        val topYInner = topY + 0.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp
        mRightPath.reset()
        mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner)
        mRightPath.lineTo(drawWidth - 100.dp - 16.dp, bottomYInner)
        mRightPath.lineTo(drawWidth - 16.dp, bottomYInner)
        mRightPath.lineTo(drawWidth - 16.dp, topYInner)
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            0.0f, 0.0f, drawWidth, 0.0f, dateRectStartColor, dateRectEndColor, Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
    }

    fun drawSinglePriceTrendPath(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val dx = targetX - lastX
        val dy = targetY - lastY

        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY
        val control2X = midX
        val control2Y = targetY

        mRightPath.reset()
        var linearGradient: LinearGradient
        if (targetPrice >= price) { //上涨
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner - 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, topYInner - 20.dp)
            linearGradient = LinearGradient(
                endOffsetX,
                0.0f,
                drawWidth,
                bottomYInner,
                "#00FFFFFF".toColorInt(),
                "#6600B8B8".toColorInt(),
                Shader.TileMode.CLAMP
            )
        } else {
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, bottomYInner + 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, bottomYInner + 20.dp)
            linearGradient = LinearGradient(
                endOffsetX,
                bottomYInner,
                drawWidth,
                0.0f,
                "#00FFFFFF".toColorInt(),
                "#6600B8B8".toColorInt(),
                Shader.TileMode.CLAMP
            )
        }
        mRightPath.close()
        mGradientPaint.shader = linearGradient
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)

        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )

        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice, targetX, targetY - 10.dp
        )
    }

    fun drawTwoPriceTrendPathForMoveAtLeastTop(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice2 - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY
        val control2X = midX
        val control2Y = targetY

        mRightPath.reset()
        if (targetPrice2 >= price) { //上涨
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner - 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, topYInner - 20.dp)
        } else {
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, bottomYInner + 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, bottomYInner + 20.dp)
        }
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            0.0f,
            drawWidth,
            bottomYInner,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )
        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice2, targetX, targetY - 10.dp
        )
    }

    fun drawTwoPriceTrendPathForMoveAtLeastBottom(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY
        val control2X = midX
        val control2Y = targetY

        mRightPath.reset()
        if (targetPrice >= price) { //上涨
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner - 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, topYInner - 20.dp)
        } else {
            mRightPath.moveTo(drawWidth - 100.dp - 16.dp, bottomYInner + 20.dp)
            mRightPath.lineTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, bottomYInner + 20.dp)
        }
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            bottomYInner,
            drawWidth,
            0.0f,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )
        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice, targetX, targetY - 10.dp
        )
    }

    fun drawTwoPriceTrendPathForMoveWithIn(
        canvas: Canvas, topY: Float, bottomY: Float
    ) {
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f
        if (targetPrice > price && targetPrice2 > price) {
            drawTwoPriceTrendPathForMoveWithInAllOneSide(canvas, topY, bottomY)
        } else if (targetPrice < price && targetPrice2 < price) {
            drawTwoPriceTrendPathForMoveWithInAllOneSide(canvas, topY, bottomY)
        } else {
            drawTwoPriceTrendPathForMoveWithInTop(canvas, topY, bottomY)
            drawTwoPriceTrendPathForMoveWithInBottom(canvas, topY, bottomY)
        }
    }

    fun drawTwoPriceTrendPathForMoveWithInTop(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice2 - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val dx = targetX - lastX
        val dy = targetY - lastY
        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY // 向下偏移减小，让控制点更往上
        val control2X = midX
        val control2Y = targetY // 向上偏移减小，让控制点更偏下
        mRightPath.reset()
        if (targetPrice2 >= price) { //上涨
            mRightPath.moveTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, lastY)
        } else {
            mRightPath.moveTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, bottomYInner)
            mRightPath.lineTo(targetX, lastY)
        }
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            0.0f,
            drawWidth,
            bottomYInner,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )
        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice2, targetX, targetY - 10.dp
        )
    }

    fun drawTwoPriceTrendPathForMoveWithInBottom(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)
        val dx = targetX - lastX
        val dy = targetY - lastY
        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY // 向下偏移减小，让控制点更往上
        val control2X = midX
        val control2Y = targetY // 向上偏移减小，让控制点更偏下
        mRightPath.reset()
        if (targetPrice >= price) { //上涨
            mRightPath.moveTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, lastY)
        } else {
            mRightPath.moveTo(lastX, lastY)
            mRightPath.cubicTo(
                control1X, control1Y, control2X, control2Y, targetX, targetY
            )
            mRightPath.lineTo(targetX, bottomYInner)
            mRightPath.lineTo(targetX, lastY)
        }
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            0.0f,
            drawWidth,
            bottomYInner,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )

        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )
        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice, targetX, targetY - 10.dp
        )
    }

    fun drawTwoPriceTrendPathForMoveWithInAllOneSide(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY1 =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))
        val targetPriceY2 =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice2 - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX2 = drawWidth - 16.dp
        var targetY2 = max(topYInner, targetPriceY2)
        targetY2 = min(targetY2, bottomYInner)

        val targetX1 = drawWidth - 16.dp
        var targetY1 = max(topYInner, targetPriceY1)
        targetY1 = min(targetY1, bottomYInner)

        val midX1 = (x + targetX1) / 2.0f
        val midX2 = (x + targetX2) / 2.0f

        val dy1 = targetY1 - lastY
        val dx = targetX2 - lastX
        val dy2 = targetY2 - lastY

        val control1X1 = lastX + dx * 0.25f
        val control1Y1 = lastY + dy1 * 0.25f + abs(dy1) * 0.25f // 向下偏移减小，让控制点更往上
        val control2X1 = lastX + dx * 0.75f
        val control2Y1 = lastY + dy1 * 0.75f - abs(dy1) * 0.15f // 向上偏移减小，让控制点更偏下

        val control1X2 = lastX + dx * 0.25f
        val control1Y2 = lastY + dy2 * 0.25f + abs(dy2) * 0.25f // 向下偏移减小，让控制点更往上
        val control2X2 = lastX + dx * 0.75f
        val control2Y2 = lastY + dy2 * 0.75f - abs(dy2) * 0.15f // 向上偏移减小，让控制点更偏下

        mRightPath.reset()
        mRightPath.moveTo(lastX, lastY)
        mRightPath.cubicTo(
            control1X2, control1Y2, control2X2, control2Y2, targetX2, targetY2
        )
        mRightPath.lineTo(targetX1, targetY1)
        mRightPath.cubicTo(
            control2X1, control2Y1, control1X1, control1Y1, lastX, lastY
        )
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            0.0f,
            drawWidth,
            bottomYInner,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )

        canvas.drawPath(mRightPath, mGradientPaint)
        canvas.drawLine(
            16.dp, targetY1, drawWidth - 16.dp, targetY1, mDashPaint
        )
        canvas.drawLine(
            16.dp, targetY2, drawWidth - 16.dp, targetY2, mDashPaint
        )
        targetTrendPrice2.let {
            val dashPath = Path()
            dashPath.moveTo(lastX, lastY)
            dashPath.cubicTo(
                control1X2, control1Y2, control2X2, control2Y2, targetX2, targetY2
            )
            canvas.drawPath(dashPath, mDashPaint)

            drawRoundBgTextAtPoint(
                canvas, mTrendPriceTextPaint, targetTrendPrice2, targetX2, targetY2 - 10.dp
            )
        }

        targetTrendPrice.let {
            val dashPath = Path()
            dashPath.moveTo(lastX, lastY)
            dashPath.cubicTo(
                control1X1, control1Y1, control2X1, control2Y1, targetX1, targetY1
            )
            canvas.drawPath(dashPath, mDashPaint)
            drawRoundBgTextAtPoint(
                canvas, mTrendPriceTextPaint, targetTrendPrice, targetX1, targetY1 - 10.dp
            )
        }
    }

    fun drawSinglePriceTrendPathNotExceed(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val dx = targetX - lastX
        val dy = targetY - lastY

        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY
        val control2X = midX
        val control2Y = targetY

        mRightPath.reset()
        mRightPath.moveTo(drawWidth - 100.dp - 16.dp, bottomYInner + 20.dp)
        mRightPath.lineTo(lastX, lastY)
        mRightPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        mRightPath.lineTo(targetX, bottomYInner + 20.dp)
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX,
            0.0f,
            drawWidth,
            bottomYInner,
            "#6600B8B8".toColorInt(),
            "#00FFFFFF".toColorInt(),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )

        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice, targetX, targetY - 10.dp
        )
    }

    fun drawSinglePriceTrendPathNotFallBelow(
        canvas: Canvas, topY: Float, bottomY: Float
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
        val endOffsetX = drawWidth - 100.dp - 16.dp
        val topYInner = topY + 20.dp
        val bottomYInner = 288.dp - 2.dp - 40.dp - 20.dp
        val index = points.size - 1.0f
        val point = points.last()
        val price = point.price.toFloatOrNull() ?: 0.0f

        val x = startOffsetX + (endOffsetX - startOffsetX) * index / (points.size.toFloat() - 1.0f)
        val y =
            bottomYInner - (bottomYInner - topYInner) * ((price - minPrice) / (maxPrice - minPrice))
        val targetPriceY =
            bottomYInner - (bottomYInner - topYInner) * ((targetPrice - minPrice) / (maxPrice - minPrice))

        val lastX = x
        val lastY = y
        val targetX = drawWidth - 16.dp
        var targetY = max(topYInner, targetPriceY)
        targetY = min(targetY, bottomYInner)

        val dx = targetX - lastX
        val dy = targetY - lastY

        val midX = (x + targetX) / 2.0f
        val control1X = midX
        val control1Y = lastY
        val control2X = midX
        val control2Y = targetY

        mRightPath.reset()
        mRightPath.moveTo(drawWidth - 100.dp - 16.dp, topYInner - 20.dp)
        mRightPath.lineTo(lastX, lastY)
        mRightPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        mRightPath.lineTo(targetX, topYInner - 20.dp)
        mRightPath.close()
        mGradientPaint.shader = LinearGradient(
            endOffsetX + 100.dp,
            0.0f,
            endOffsetX,
            bottomYInner,
            "#00FFFFFF".toColorInt(),
            "#6600B8B8".toColorInt(),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(mRightPath, mGradientPaint)
        val dashPath = Path()
        dashPath.moveTo(lastX, lastY)
        dashPath.cubicTo(
            control1X, control1Y, control2X, control2Y, targetX, targetY
        )
        canvas.drawPath(dashPath, mDashPaint)
        canvas.drawLine(
            16.dp, targetY, drawWidth - 16.dp, targetY, mDashPaint
        )

        drawRoundBgTextAtPoint(
            canvas, mTrendPriceTextPaint, targetTrendPrice, targetX, targetY - 10.dp
        )
    }

    fun drawRoundBgTextAtPoint(
        canvas: Canvas, paint: Paint, text: String, x: Float, y: Float
    ) {
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val textWidth = textBounds.width().toFloat()
        val textHeight = textBounds.height().toFloat()
        val centerX = x - (textWidth + 8.dp) / 2 + 16.dp
        val centerY = y
        val rectLeft = centerX - (textWidth + 8.dp) / 2
        val rectTop = centerY - 20.dp / 2
        val rectRight = centerX + (textWidth + 8.dp) / 2
        val rectBottom = centerY + 20.dp / 2
        val bgRectF = RectF()
        bgRectF.set(rectLeft, rectTop, rectRight, rectBottom)
        canvas.drawRoundRect(bgRectF, 6.dp, 6.dp, bgTextPaint)

        val bgRect2 = RectF()
        bgRect2.set(rectLeft + 20.dp, rectTop, rectRight, rectBottom)
        canvas.drawRect(bgRect2, bgTextPaint)

        val fontMetrics = paint.fontMetrics
        val textBaseLineY = centerY - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(text, centerX - textWidth / 2, textBaseLineY, paint)
    }

    override fun initPath(
        width: Float, startMargin: Float, topY: Float, bottomY: Float
    ) {
        this.drawWidth =
            if (width == 0F) (DimenUtils.getScreenWidth(FApp.get()).toFloat()) else width
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