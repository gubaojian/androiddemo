package com.longbridge.common.uiLib.edit

import android.text.SpannableStringBuilder
import android.text.method.ReplacementTransformationMethod
import android.view.View
import java.text.DecimalFormat
import java.text.NumberFormat

class ThousandSeparatorTransformation : ReplacementTransformationMethod() {

    private val numberFormat: NumberFormat = DecimalFormat("#,###.##")

    // 原始字符（不替换）
    override fun getOriginal(): CharArray = charArrayOf()
    // 替换后字符（不替换）
    override fun getReplacement(): CharArray = charArrayOf()

    // 核心：转换显示文本
    override fun getTransformation(source: CharSequence, v: View): CharSequence {
        if (source.isNullOrEmpty()) return ""

        val rawText = source.toString()
        return try {
            // 原始文本转数字 → 格式化显示
            val number = rawText.toDouble()
            val formatted = numberFormat.format(number)
            // 返回格式化后的显示文本
            SpannableStringBuilder(formatted)
        } catch (e: Exception) {
            // 非法内容直接显示原始
            rawText
        }
    }
}