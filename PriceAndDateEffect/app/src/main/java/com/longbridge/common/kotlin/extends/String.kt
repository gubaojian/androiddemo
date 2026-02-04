package com.longbridge.common.kotlin.extends

import android.text.TextUtils
import com.longbridge.core.uitls.ConvertUtils
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author 郭生生
 * on 2020/6/16
 */
private const val placeholder = "--"
private val REGEX0 = "[+-]?\\d+(\\.\\d+)?".toRegex()
private val REGEX1 = "[+-]?\\d+(\\.\\d+)?%?$".toRegex()
fun String?.toPlaceHolderString(suffix: String = ""): String {
    if (this.isNullOrEmpty()) return placeholder
    return "$this$suffix"
}

fun Long?.toPlaceHolderString(): String {
    if (this == null) return placeholder
    return this.toString()
}

fun String?.toSafeInt(): Int {
    if (this.isNullOrEmpty()) return 0
    return try {
        this.toInt()
    } catch (e: Exception) {
        0
    }
}

fun String?.toSafeFormatNum(): String {
    if (this.isNullOrEmpty()) return placeholder
    return try {
        ConvertUtils.formatNum(this)
    } catch (e: Exception) {
        placeholder
    }
}

fun String?.empty() = this.isNullOrEmpty()

fun String?.notEmpty() = !this.isNullOrEmpty()

fun String?.safeEmpty(defaultValue: () -> String): String {
    return this?.ifEmpty {
        defaultValue.invoke()
    } ?: defaultValue.invoke()
}

fun Char.toSafeInt(): Int {
    return try {
        this.toInt()
    } catch (e: Exception) {
        0
    }
}

fun String?.toSafeDouble(defaultValue: Double = 0.0): Double {
    if (this.isNullOrEmpty()) return defaultValue
    if (this == "0" || this == "0.0") return 0.0
    return try {
        this.toDouble()
    } catch (e: Exception) {
        defaultValue
    }
}

fun String?.toSafeBoolean(): Boolean {
    if (this.isNullOrEmpty()) return false
    return try {
        this.toBoolean()
    } catch (e: Exception) {
        false
    }
}

fun String?.toSafeLong(): Long {
    if (this.isNullOrEmpty()) return 0L
    return try {
        this.toLong()
    } catch (e: Exception) {
        0L
    }
}

fun String?.toSafeFloat(): Float {
//    if (this.isNullOrEmpty()) return 0F
//    return try {
//        this.toFloat()
//    } catch (e: Exception) {
//        0F
//    }
    return ConvertUtils.toFloat(this)
}

fun CharSequence?.toSafeDouble(): Double {
    if (this.isNullOrEmpty()) return 0.0
    return try {
        this.toString().toDouble()
    } catch (e: Exception) {
        0.0
    }
}

fun CharSequence?.toSafeInt(): Int {
    if (this.isNullOrEmpty()) return 0
    return try {
        this.toString().toInt()
    } catch (e: Exception) {
        0
    }
}

fun CharSequence?.toSafeFloat(): Float {
    if (this.isNullOrEmpty()) return 0F
    return try {
        this.toString().toFloat()
    } catch (e: Exception) {
        0F
    }
}

fun String?.toSafeBigDecimal(): BigDecimal {
    if (this.isNullOrEmpty()) return BigDecimal.ZERO
    return try {
        this.toBigDecimal()
    } catch (e: Exception) {
        BigDecimal.ZERO
    }
}

fun String.toSafePercentFloat(): Float { //转成百分比后还要保留两位小数
    if (TextUtils.isEmpty(this)) return 0F
    return try {
        (this.toDouble() * 100).toFloat() //这里先转double再 * 100为了防止直接转float * 100精度丢失问题
    } catch (e: Exception) {
        0F
    }
}

fun String.toSafeFloatStr(): String { //转成百分比后还要保留两位小数
    if (TextUtils.isEmpty(this)) return "0.00"
    return try {
        val re = this.toSafeFloat()
        return DecimalFormat("0.00").format(re)
    } catch (e: Exception) {
        "0.00"
    }
}

fun String.toSafePercentStr(): String { //转成百分比后还要保留两位小数
    if (TextUtils.isEmpty(this)) return "0.00"
    return try {
        val re = (this.toDouble() * 100).toFloat() //这里先转double再 * 100为了防止直接转float * 100精度丢失问题
        return DecimalFormat("0.00").format(re)
    } catch (e: Exception) {
        "0.00"
    }
}

/**
 * 4.->4.0
 */
fun String?.toSafePrice(): String {
    if (this.isNullOrEmpty()) return ""
    return if (this.endsWith(".")) {
        this + "0"
    } else {
        this
    }
}

/**
 * 判断String是否为数字
 */
fun String.isNumeric() = REGEX0.matches(this)

/**
 * 判断String是否为数字或%
 */
fun String.isNumericOrPercent() = REGEX1.matches(this)

fun String.handleCharNum(num: Int): String {
    if (TextUtils.isEmpty(this)) {
        return this
    }
    var count = 0
    var endIndex = 0
    var endZn = false
    var endCount = 0
    for (i in this.indices) {
        val item = this[i]
        count += if (item < 128.toChar()) {
            1
        } else {
            2
        }
        /**
         * 主要为了记录最后一个下标是中文还是英文
         * 如果是中文并且字符已经超过10，那么在截取的时候就不在+1
         * 这里考虑第九个字符是英文，后面一个Char是中文，直接变成了11，那么要把这个中文干掉，变回9
         * */
        if (num == count || (item >= 128.toChar() && num + 1 == count)) {
            endIndex = i
            endZn = item >= 128.toChar()
            endCount = count
        } else if (count == (num + 1)) {
            endIndex = i - 1
            endCount = count
        }
    }
    return if (count <= num) {
        this
    } else {
        this.substring(0, endIndex + if (endZn && endCount != 10) 0 else 1) + "..."
    }
}