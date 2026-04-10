package com.example.testai

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class KLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── 颜色 ──────────────────────────────────────────────────────────────────
    private val colorBg = Color.parseColor("#0D1117")
    private val colorUp = Color.parseColor("#EF5350")        // 涨：红
    private val colorDown = Color.parseColor("#26A69A")      // 跌：绿
    private val colorGrid = Color.parseColor("#1E2D3D")
    private val colorText = Color.parseColor("#8B9DB0")
    private val colorCross = Color.parseColor("#FFFFFF")
    private val colorVolUp = Color.parseColor("#993D3D")
    private val colorVolDown = Color.parseColor("#1A6B63")

    // ── 尺寸常量 ──────────────────────────────────────────────────────────────
    private val priceAxisWidth = 80f        // 右侧价格轴宽度
    private val timeAxisHeight = 32f        // 底部时间轴高度
    private val volRatio = 0.25f            // 成交量区域占总高度比例
    private val dividerHeight = 2f          // 主图/量图分隔线高度
    private val gridRows = 4               // 主图横格数
    private val gridCols = 4               // 纵格数
    private val minCandleWidth = 4f
    private val maxCandleWidth = 40f
    private val candleSpacingRatio = 0.3f  // 间距 / 蜡烛宽度

    // ── 画笔 ──────────────────────────────────────────────────────────────────
    private val bgPaint = Paint().apply { color = colorBg }
    private val gridPaint = Paint().apply {
        color = colorGrid; strokeWidth = 1f; isAntiAlias = true
    }
    private val upPaint = Paint().apply {
        color = colorUp; isAntiAlias = true
    }
    private val downPaint = Paint().apply {
        color = colorDown; isAntiAlias = true
    }
    private val volUpPaint = Paint().apply { color = colorVolUp; isAntiAlias = true }
    private val volDownPaint = Paint().apply { color = colorVolDown; isAntiAlias = true }
    private val textPaint = Paint().apply {
        color = colorText; textSize = 24f; isAntiAlias = true
    }
    private val crossPaint = Paint().apply {
        color = colorCross; strokeWidth = 1f; isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private val highlightPaint = Paint().apply {
        color = Color.parseColor("#1AFFFFFF")
        style = Paint.Style.FILL
    }

    // ── 数据 ──────────────────────────────────────────────────────────────────
    private var dataList: List<KLineData> = emptyList()

    // ── 视图状态 ──────────────────────────────────────────────────────────────
    /** 每根蜡烛（含间距）占用的像素宽度 */
    private var candleUnitWidth = 16f
    /** 当前滚动偏移（向左为正，表示已滚动的蜡烛像素数） */
    private var scrollOffset = 0f
    /** 十字线所在数据索引，-1 表示不显示 */
    private var crossIndex = -1

    // ── 手势 ──────────────────────────────────────────────────────────────────
    private var lastScaleSpan = 0f
    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            scrollOffset = clampScroll(scrollOffset + distanceX)
            crossIndex = -1
            invalidate()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val idx = xToDataIndex(e.x)
            if (idx in dataList.indices) {
                crossIndex = idx
                invalidate()
            }
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            crossIndex = -1
            invalidate()
            return true
        }
    })

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val factor = detector.scaleFactor
                val newWidth = (candleUnitWidth * factor).coerceIn(minCandleWidth + 1f, maxCandleWidth)
                // 以捏合中心为锚点保持位置不变
                val focusX = detector.focusX
                val idxAtFocus = (scrollOffset + focusX) / candleUnitWidth
                candleUnitWidth = newWidth
                scrollOffset = clampScroll(idxAtFocus * candleUnitWidth - focusX)
                invalidate()
                return true
            }
        })

    // ── 布局区域（onSizeChanged 中赋值） ─────────────────────────────────────
    private var mainRect = RectF()     // 主K线区域
    private var volRect = RectF()      // 成交量区域
    private var timeAxisY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val chartW = w - priceAxisWidth
        val chartH = h - timeAxisHeight
        val volH = chartH * volRatio
        val mainH = chartH - volH - dividerHeight

        mainRect = RectF(0f, 0f, chartW, mainH)
        volRect = RectF(0f, mainH + dividerHeight, chartW, mainH + dividerHeight + volH)
        timeAxisY = h - timeAxisHeight
        // 初始让最后一根蜡烛贴右边
        scrollOffset = clampScroll(Float.MAX_VALUE)
    }

    // ── 公开 API ──────────────────────────────────────────────────────────────
    fun setData(data: List<KLineData>) {
        dataList = data
        scrollOffset = clampScroll(Float.MAX_VALUE)
        crossIndex = -1
        invalidate()
    }

    // ── 触摸事件 ──────────────────────────────────────────────────────────────
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        if (!scaleDetector.isInProgress) {
            gestureDetector.onTouchEvent(event)
        }
        return true
    }

    // ── 绘制 ──────────────────────────────────────────────────────────────────
    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        if (dataList.isEmpty()) {
            drawEmpty(canvas)
            return
        }

        val visibleRange = calcVisibleRange()
        val (priceMin, priceMax) = calcPriceRange(visibleRange)
        val (volMax) = calcVolRange(visibleRange)

        drawGridLines(canvas, priceMin, priceMax)
        drawCandles(canvas, visibleRange, priceMin, priceMax)
        drawVolume(canvas, visibleRange, volMax)
        drawPriceAxis(canvas, priceMin, priceMax)
        drawTimeAxis(canvas, visibleRange)
        drawDivider(canvas)

        if (crossIndex in dataList.indices) {
            drawCrossHair(canvas, crossIndex, priceMin, priceMax, volMax)
        }
    }

    // ── 辅助：空数据提示 ─────────────────────────────────────────────────────
    private fun drawEmpty(canvas: Canvas) {
        val msg = "暂无数据"
        val tw = textPaint.measureText(msg)
        canvas.drawText(msg, (width - tw) / 2f, height / 2f, textPaint)
    }

    // ── 辅助：可见数据范围 ────────────────────────────────────────────────────
    private fun calcVisibleRange(): IntRange {
        val firstIdx = (scrollOffset / candleUnitWidth).toInt().coerceAtLeast(0)
        val count = (mainRect.width() / candleUnitWidth).toInt() + 2
        val lastIdx = (firstIdx + count).coerceAtMost(dataList.size - 1)
        return firstIdx..lastIdx
    }

    // ── 辅助：价格范围（带 5% 上下边距） ─────────────────────────────────────
    private fun calcPriceRange(range: IntRange): Pair<Float, Float> {
        var lo = Float.MAX_VALUE
        var hi = Float.MIN_VALUE
        for (i in range) {
            val d = dataList[i]
            lo = min(lo, d.low)
            hi = max(hi, d.high)
        }
        if (lo == Float.MAX_VALUE) return 0f to 1f
        val margin = (hi - lo) * 0.05f
        return (lo - margin) to (hi + margin)
    }

    // ── 辅助：成交量最大值 ────────────────────────────────────────────────────
    private fun calcVolRange(range: IntRange): FloatArray {
        var maxVol = 0f
        for (i in range) maxVol = max(maxVol, dataList[i].volume)
        return floatArrayOf(if (maxVol == 0f) 1f else maxVol)
    }

    // ── 绘制主图网格 ──────────────────────────────────────────────────────────
    private fun drawGridLines(canvas: Canvas, priceMin: Float, priceMax: Float) {
        // 横线（价格）
        for (i in 0..gridRows) {
            val y = mainRect.top + mainRect.height() * i / gridRows
            canvas.drawLine(mainRect.left, y, mainRect.right, y, gridPaint)
        }
        // 纵线（时间）
        for (i in 0..gridCols) {
            val x = mainRect.left + mainRect.width() * i / gridCols
            canvas.drawLine(x, mainRect.top, x, mainRect.bottom, gridPaint)
            canvas.drawLine(x, volRect.top, x, volRect.bottom, gridPaint)
        }
        // 成交量区上下边界
        canvas.drawLine(volRect.left, volRect.top, volRect.right, volRect.top, gridPaint)
        canvas.drawLine(volRect.left, volRect.bottom, volRect.right, volRect.bottom, gridPaint)
    }

    // ── 绘制蜡烛 ──────────────────────────────────────────────────────────────
    private fun drawCandles(canvas: Canvas, range: IntRange, priceMin: Float, priceMax: Float) {
        val candleW = candleUnitWidth * (1f - candleSpacingRatio)
        for (i in range) {
            val d = dataList[i]
            val cx = dataIndexToX(i)
            val paint = if (d.isUp) upPaint else downPaint

            val highY = priceToY(d.high, priceMin, priceMax)
            val lowY = priceToY(d.low, priceMin, priceMax)
            val openY = priceToY(d.open, priceMin, priceMax)
            val closeY = priceToY(d.close, priceMin, priceMax)

            // 上下影线
            canvas.drawLine(cx, highY, cx, lowY, paint)

            // 实体
            val bodyTop = min(openY, closeY)
            val bodyBottom = max(openY, closeY)
            val halfW = candleW / 2f
            if (bodyBottom - bodyTop < 2f) {
                // 十字星：画横线
                canvas.drawLine(cx - halfW, bodyTop, cx + halfW, bodyTop, paint)
            } else {
                canvas.drawRect(cx - halfW, bodyTop, cx + halfW, bodyBottom, paint)
            }
        }
    }

    // ── 绘制成交量 ────────────────────────────────────────────────────────────
    private fun drawVolume(canvas: Canvas, range: IntRange, volMax: Float) {
        val barW = candleUnitWidth * (1f - candleSpacingRatio)
        for (i in range) {
            val d = dataList[i]
            val cx = dataIndexToX(i)
            val barTop = volRect.bottom - (d.volume / volMax) * volRect.height()
            val paint = if (d.isUp) volUpPaint else volDownPaint
            canvas.drawRect(cx - barW / 2f, barTop, cx + barW / 2f, volRect.bottom, paint)
        }
    }

    // ── 绘制价格轴 ────────────────────────────────────────────────────────────
    private fun drawPriceAxis(canvas: Canvas, priceMin: Float, priceMax: Float) {
        val axisX = mainRect.right + 4f
        for (i in 0..gridRows) {
            val frac = i.toFloat() / gridRows
            val price = priceMax - (priceMax - priceMin) * frac
            val y = mainRect.top + mainRect.height() * frac
            val label = formatPrice(price)
            canvas.drawText(label, axisX, y + textPaint.textSize / 3f, textPaint)
        }
    }

    // ── 绘制时间轴 ────────────────────────────────────────────────────────────
    private fun drawTimeAxis(canvas: Canvas, range: IntRange) {
        val count = range.last - range.first + 1
        if (count <= 0) return
        val step = max(1, (count / gridCols.toFloat()).roundToInt())
        val fmt = SimpleDateFormat("MM/dd", Locale.getDefault())
        var drawn = 0
        for (i in range step step) {
            if (drawn >= gridCols) break
            val x = dataIndexToX(i)
            if (x < 0f || x > mainRect.right) continue
            val label = fmt.format(Date(dataList[i].timestamp))
            val tw = textPaint.measureText(label)
            canvas.drawText(label, x - tw / 2f, timeAxisY + textPaint.textSize, textPaint)
            drawn++
        }
    }

    // ── 绘制分隔线 ────────────────────────────────────────────────────────────
    private fun drawDivider(canvas: Canvas) {
        val dividerPaint = Paint().apply { color = colorGrid; strokeWidth = dividerHeight }
        canvas.drawLine(mainRect.left, mainRect.bottom, mainRect.right, mainRect.bottom, dividerPaint)
    }

    // ── 绘制十字线和信息 ──────────────────────────────────────────────────────
    private fun drawCrossHair(canvas: Canvas, idx: Int, priceMin: Float, priceMax: Float, volMax: Float) {
        val d = dataList[idx]
        val cx = dataIndexToX(idx)
        val closeY = priceToY(d.close, priceMin, priceMax)

        // 竖线（贯通主图+量图）
        canvas.drawLine(cx, mainRect.top, cx, volRect.bottom, crossPaint)
        // 横线（主图）
        canvas.drawLine(mainRect.left, closeY, mainRect.right, closeY, crossPaint)

        // 右侧价格标签
        val priceLabel = formatPrice(d.close)
        val labelW = textPaint.measureText(priceLabel) + 12f
        val labelH = textPaint.textSize + 8f
        val labelY = closeY - labelH / 2f
        val lp = if (d.isUp) upPaint else downPaint
        canvas.drawRect(mainRect.right, labelY, mainRect.right + labelW, labelY + labelH, lp)
        canvas.drawText(priceLabel, mainRect.right + 6f, closeY + textPaint.textSize / 3f,
            Paint().apply { color = Color.WHITE; textSize = 24f; isAntiAlias = true })

        // 顶部信息栏
        drawInfoBar(canvas, d)
    }

    private fun drawInfoBar(canvas: Canvas, d: KLineData) {
        val fmt = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val items = listOf(
            "日期:${fmt.format(Date(d.timestamp))}",
            "开:${formatPrice(d.open)}",
            "高:${formatPrice(d.high)}",
            "低:${formatPrice(d.low)}",
            "收:${formatPrice(d.close)}",
            "量:${formatVol(d.volume)}"
        )
        val barH = textPaint.textSize + 12f
        canvas.drawRect(0f, 0f, mainRect.right, barH, highlightPaint)

        val paint = Paint().apply {
            textSize = 22f
            isAntiAlias = true
            color = if (d.isUp) colorUp else colorDown
        }
        var x = 6f
        for (item in items) {
            canvas.drawText(item, x, barH - 6f, paint)
            x += paint.measureText(item) + 14f
            if (x > mainRect.right) break
        }
    }

    // ── 坐标转换 ──────────────────────────────────────────────────────────────
    /** 数据索引 → 屏幕 x（蜡烛中心） */
    private fun dataIndexToX(idx: Int): Float =
        idx * candleUnitWidth - scrollOffset + candleUnitWidth / 2f

    /** 屏幕 x → 最近数据索引 */
    private fun xToDataIndex(x: Float): Int =
        ((x + scrollOffset - candleUnitWidth / 2f) / candleUnitWidth).roundToInt()
            .coerceIn(0, dataList.size - 1)

    /** 价格 → 屏幕 y（在 mainRect 内） */
    private fun priceToY(price: Float, min: Float, max: Float): Float {
        if (max == min) return mainRect.centerY()
        return mainRect.bottom - (price - min) / (max - min) * mainRect.height()
    }

    /** 限制滚动范围 */
    private fun clampScroll(offset: Float): Float {
        if (dataList.isEmpty() || mainRect.width() == 0f) return 0f
        val totalW = dataList.size * candleUnitWidth
        val maxScroll = max(0f, totalW - mainRect.width())
        return offset.coerceIn(0f, maxScroll)
    }

    // ── 格式化工具 ────────────────────────────────────────────────────────────
    private fun formatPrice(price: Float): String {
        return if (price >= 1000f) "%.2f".format(price)
        else "%.3f".format(price)
    }

    private fun formatVol(vol: Float): String {
        return when {
            vol >= 1_0000_0000f -> "%.1f亿".format(vol / 1_0000_0000f)
            vol >= 1_0000f -> "%.1f万".format(vol / 1_0000f)
            else -> vol.roundToInt().toString()
        }
    }
}
