package com.longbridge.common.kotlin.expansion

import android.text.TextUtils
import android.util.TypedValue
import com.longbridge.common.kotlin.expansion.toSafeFloat
import com.longbridge.core.comm.FApp
import com.longbridge.core.uitls.ArithUtils
import com.longbridge.core.uitls.DimenUtils
import java.math.BigDecimal

/**
 * @author 郭生生
 * on 2020/6/9
 */
val Float.dp
    get() = DimenUtils.dp2px(this).toFloat()

val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this, FApp.get().resources.displayMetrics
    )

val Int.dp: Float
    get() = DimenUtils.dp2px(this.toFloat()).toFloat()

val Float.dpf: Float
    get() = DimenUtils.dp2px(this).toFloat()

val Int.sp
    get() = DimenUtils.sp2px(this.toFloat()).toFloat()
private val ONE_DECIMAL = BigDecimal("1")
fun String.round(length: Int): String {
    if (TextUtils.isEmpty(this)) {
        return this
    }
    if (this.toSafeFloat() == 0f) {
        return "0.00"
    }
    val b = BigDecimal(this)
    val one = ONE_DECIMAL
    return b.divide(one, kotlin.math.min(length, this.length), BigDecimal.ROUND_HALF_UP).toString()
}

fun String.roundFormate(length: Int): String {
    val b = BigDecimal(this)
    val one = ONE_DECIMAL
    val value = b.divide(one, length, BigDecimal.ROUND_HALF_UP).toString()
    return if (value.toSafeFloat() >= 0) "+$value" else value
}

fun Float.roundFormate(length: Int): String {
    val b = BigDecimal(this.toString())
    val one = ONE_DECIMAL
    val value = b.divide(one, length, BigDecimal.ROUND_HALF_UP).toString()
    return if (value.toSafeFloat() >= 0) "+$value" else value
}

fun Float.round(length: Int, errorValue: Float? = null, defaultValue: String = ""): String {
    if (this.isNaN() || (errorValue != null && errorValue == this)) return defaultValue
    val b = BigDecimal(this.toString())
    val one = ONE_DECIMAL
    return b.divide(one, length, BigDecimal.ROUND_HALF_UP).toString()
}

fun Double.round(length: Int, defaultValue: String = ""): String {
    if (this.isNaN()) return defaultValue
    val b = BigDecimal(this)
    val one = ONE_DECIMAL
    return b.divide(one, length, BigDecimal.ROUND_HALF_UP).toString()
}

fun Float.safeGet(): Float {
    if (this.isNaN()) {
        return 0F
    }
    return this
}

fun Float.formatSymbol(): String {
    return if (this > 0F) "+" else ""
}

fun Float.safeDivider(data: Float): Float {
    if (data == 0F) {
        return 0F
    }
    return this / data
}

fun Double.safeDivider(data: Double): Double {
    if (data == 0.0) {
        return 0.0
    }
    return this / data
}

fun Float.dividerRound(data: Float, length: Int): String {
    if (data == 0F) return "--"
    return (this / data).roundFormate(length)
}

fun String?.sub(value: String?, length: Int = 2): String {
    if (this.isNullOrEmpty()) return "--"
    if (value.isNullOrEmpty()) return this
    return ArithUtils.sub(this, value).run {
        round(length)
    }
}

fun String?.add(value: String?, length: Int = 2): String {
    if (this.isNullOrEmpty()) return "--"
    if (value.isNullOrEmpty()) return this
    return ArithUtils.add(this, value).run {
        round(length)
    }
}

fun Float?.sub(value: Float?, length: Int = 2): String {
    if (this == null) return "--"
    if (value == null) return this.toString()
    return ArithUtils.sub(this.toString(), value.toString()).run {
        round(length)
    }
}

fun Float?.add(value: Float?, length: Int = 2): String {
    if (this == null) return "--"
    if (value == null) return this.toString()
    return ArithUtils.add(this.toString(), value.toString()).run {
        round(length)
    }
}

fun multiMin(vararg value: Float): Float {
    return value.minOrNull() ?: value.getSafe(0)
}

fun multiMin(vararg value: Int): Int {
    return value.minOrNull() ?: value.getSafe(0)
}

fun multiMin(vararg value: Long): Long {
    return value.minOrNull() ?: value.getSafe(0)
}

fun multiMin(vararg value: Double): Double {
    return value.minOrNull() ?: value.getSafe(0)
}

fun multiMax(vararg value: Float): Float {
    return value.maxOrNull() ?: value.getSafe(0)
}

fun multiMax(vararg value: Int): Int {
    return value.maxOrNull() ?: value.getSafe(0)
}

fun multiMax(vararg value: Long): Long {
    return value.maxOrNull() ?: value.getSafe(0)
}

fun multiMax(vararg value: Double): Double {
    return value.maxOrNull() ?: value.getSafe(0)
}
