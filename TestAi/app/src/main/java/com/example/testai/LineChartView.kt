package com.example.testai

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ── 配置 ──────────────────────────────────────────────────────────────────
    var lineColor: Int = Color.parseColor("#4FC3F7")
    var lineWidth: Float = 3f
    var pointRadius: Float = 5f
    var showPoints: Boolean = true
    var smoothLine: Boolean = true         // true = 贝塞尔曲线，false = 折线
    var showFill: Boolean = true           // 线下渐变填充
    var gridRows: Int = 4
    var gridCols: Int = 5

    // ── 内边距 ────────────────────────────────────────────────────────────────
    private val paddingLeft = 56f
    private val paddingRight = 20f
    private val paddingTop = 20f
    private val paddingBottom = 40f

    // ── 数据 ──────────────────────────────────────────────────────────────────
    private var points: List<Float> = emptyList()

    // ── 画笔 ──────────────────────────────────────────────────────────────────
    private val bgPaint = Paint().apply { color = Color.parseColor("#0D1117") }
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#1E2D3D"); strokeWidth = 1f; isAntiAlias = true
    }
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE; isAntiAlias = true; strokeJoin = Paint.Join.ROUND
    }
    private val pointPaint = Paint().apply { isAntiAlias = true }
    private val textPaint = Paint().apply {
        color = Color.parseColor("#8B9DB0"); textSize = 24f; isAntiAlias = true
    }

    // ── 公开 API ──────────────────────────────────────────────────────────────
    fun setPoints(data: List<Float>) {
        points = data
        invalidate()
    }

    // ── 绘制 ──────────────────────────────────────────────────────────────────
    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        if (points.size < 2) {
            drawEmpty(canvas)
            return
        }

        val chartRect = RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
        val yMin = points.min()
        val yMax = points.max()
        val yRange = if (yMax == yMin) 1f else yMax - yMin
        val margin = yRange * 0.1f
        val lo = yMin - margin
        val hi = yMax + margin

        drawGrid(canvas, chartRect, lo, hi)
        drawFill(canvas, chartRect, lo, hi)
        drawLine(canvas, chartRect, lo, hi)
        if (showPoints) drawPoints(canvas, chartRect, lo, hi)
        drawAxes(canvas, chartRect, lo, hi)
    }

    // ── 网格 ──────────────────────────────────────────────────────────────────
    private fun drawGrid(canvas: Canvas, rect: RectF, lo: Float, hi: Float) {
        for (i in 0..gridRows) {
            val y = rect.top + rect.height() * i / gridRows
            canvas.drawLine(rect.left, y, rect.right, y, gridPaint)
        }
        for (i in 0..gridCols) {
            val x = rect.left + rect.width() * i / gridCols
            canvas.drawLine(x, rect.top, x, rect.bottom, gridPaint)
        }
    }

    // ── 线下渐变填充 ──────────────────────────────────────────────────────────
    private fun drawFill(canvas: Canvas, rect: RectF, lo: Float, hi: Float) {
        if (!showFill) return
        val path = buildPath(rect, lo, hi)
        path.lineTo(xOf(points.size - 1, rect), rect.bottom)
        path.lineTo(xOf(0, rect), rect.bottom)
        path.close()
        val fillPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            shader = LinearGradient(
                0f, rect.top, 0f, rect.bottom,
                intArrayOf(lineColor and 0x00FFFFFF or 0x66000000 or (lineColor and 0x00FFFFFF),
                    lineColor and 0x00FFFFFF),
                null, Shader.TileMode.CLAMP
            )
            // 简单透明度渐变
            shader = LinearGradient(
                0f, rect.top, 0f, rect.bottom,
                Color.argb(80, Color.red(lineColor), Color.green(lineColor), Color.blue(lineColor)),
                Color.argb(0, Color.red(lineColor), Color.green(lineColor), Color.blue(lineColor)),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(path, fillPaint)
    }

    // ── 折线/曲线 ─────────────────────────────────────────────────────────────
    private fun drawLine(canvas: Canvas, rect: RectF, lo: Float, hi: Float) {
        linePaint.color = lineColor
        linePaint.strokeWidth = lineWidth
        canvas.drawPath(buildPath(rect, lo, hi), linePaint)
    }

    // ── 数据点圆点 ────────────────────────────────────────────────────────────
    private fun drawPoints(canvas: Canvas, rect: RectF, lo: Float, hi: Float) {
        pointPaint.color = lineColor
        val outlinePaint = Paint().apply {
            color = Color.parseColor("#0D1117"); isAntiAlias = true
        }
        points.forEachIndexed { i, v ->
            val x = xOf(i, rect)
            val y = yOf(v, rect, lo, hi)
            canvas.drawCircle(x, y, pointRadius + 2f, outlinePaint)
            canvas.drawCircle(x, y, pointRadius, pointPaint)
        }
    }

    // ── 坐标轴标签 ────────────────────────────────────────────────────────────
    private fun drawAxes(canvas: Canvas, rect: RectF, lo: Float, hi: Float) {
        // Y 轴（左侧）
        for (i in 0..gridRows) {
            val frac = i.toFloat() / gridRows
            val value = hi - (hi - lo) * frac
            val y = rect.top + rect.height() * frac
            val label = formatValue(value)
            val tw = textPaint.measureText(label)
            canvas.drawText(label, rect.left - tw - 6f, y + textPaint.textSize / 3f, textPaint)
        }
        // X 轴（底部索引）
        val step = maxOf(1, points.size / gridCols)
        var drawn = 0
        for (i in points.indices step step) {
            if (drawn >= gridCols) break
            val x = xOf(i, rect)
            val label = i.toString()
            val tw = textPaint.measureText(label)
            canvas.drawText(label, x - tw / 2f, rect.bottom + textPaint.textSize + 4f, textPaint)
            drawn++
        }
    }

    // ── 空数据提示 ────────────────────────────────────────────────────────────
    private fun drawEmpty(canvas: Canvas) {
        val msg = if (points.isEmpty()) "暂无数据" else "至少需要2个点"
        val tw = textPaint.measureText(msg)
        canvas.drawText(msg, (width - tw) / 2f, height / 2f, textPaint)
    }

    // ── 路径构建（支持贝塞尔平滑） ────────────────────────────────────────────
    private fun buildPath(rect: RectF, lo: Float, hi: Float): Path {
        val path = Path()
        if (points.isEmpty()) return path

        val xs = FloatArray(points.size) { xOf(it, rect) }
        val ys = FloatArray(points.size) { yOf(points[it], rect, lo, hi) }

        path.moveTo(xs[0], ys[0])

        if (!smoothLine || points.size < 3) {
            for (i in 1 until xs.size) path.lineTo(xs[i], ys[i])
        } else {
            // Catmull-Rom → 贝塞尔控制点
            for (i in 1 until xs.size) {
                val cp1x = xs[i - 1] + (xs[i] - (if (i > 1) xs[i - 2] else xs[i - 1])) / 6f
                val cp1y = ys[i - 1] + (ys[i] - (if (i > 1) ys[i - 2] else ys[i - 1])) / 6f
                val cp2x = xs[i] - (xs[minOf(i + 1, xs.size - 1)] - xs[i - 1]) / 6f
                val cp2y = ys[i] - (ys[minOf(i + 1, ys.size - 1)] - ys[i - 1]) / 6f
                path.cubicTo(cp1x, cp1y, cp2x, cp2y, xs[i], ys[i])
            }
        }
        return path
    }

    // ── 坐标映射 ──────────────────────────────────────────────────────────────
    private fun xOf(idx: Int, rect: RectF): Float {
        if (points.size == 1) return rect.centerX()
        return rect.left + rect.width() * idx / (points.size - 1)
    }

    private fun yOf(value: Float, rect: RectF, lo: Float, hi: Float): Float {
        if (hi == lo) return rect.centerY()
        return rect.bottom - (value - lo) / (hi - lo) * rect.height()
    }

    private fun formatValue(v: Float): String = when {
        v >= 10000f || v <= -10000f -> "%.0f".format(v)
        v >= 100f || v <= -100f -> "%.1f".format(v)
        else -> "%.2f".format(v)
    }
}
