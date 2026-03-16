package com.longbridge.common.uiLib.edit

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class CommaSpan : ReplacementSpan() {

    // 告诉系统：这个字符的宽度 = 原本字符的宽度 + 一个逗号的宽度
    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        val charWidth = paint.measureText(text, start, end)
        val commaWidth = paint.measureText(",")
        return (charWidth + commaWidth).toInt()
    }

    // 重写绘制逻辑：画出原字符，并在旁边补上逗号
    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        // 1. 画出原本的数字字符
        canvas.drawText(text, start, end, x, y.toFloat(), paint)

        // 2. 在字符右边画出逗号
        val charWidth = paint.measureText(text, start, end)
        canvas.drawText(",", x + charWidth, y.toFloat(), paint)
    }
}