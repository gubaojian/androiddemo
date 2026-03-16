package com.longbridge.common.uiLib.edit

import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher

class ThousandSeparatorWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null) return

        // 每次文本变动时，先清除旧的逗号 Span
        val existingSpans = s.getSpans(0, s.length, CommaSpan::class.java)
        for (span in existingSpans) {
            s.removeSpan(span)
        }

        val text = s.toString()
        if (text.isEmpty()) return

        // 处理小数情况，如果有小数点，只对整数部分加逗号
        var decimalIndex = text.indexOf('.')
        if (decimalIndex == -1) {
            decimalIndex = text.length
        }
        // 处理负数情况：如果以 "-" 开头，逗号不能插在负号和数字之间
        val startIndex = if (text.startsWith("-")) 1 else 0

        // 从小数点（或末尾）往回推，每 3 个数字插一个 Span
        for (i in decimalIndex - 3 downTo startIndex + 1 step 3) {
            val spanStart = i - 1
            val spanEnd = i
            // 给对应的字符加上我们自定义绘制的 CommaSpan
            s.setSpan(CommaSpan(), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}