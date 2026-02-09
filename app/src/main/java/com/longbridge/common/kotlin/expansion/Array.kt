package com.longbridge.common.kotlin.expansion

import java.lang.StringBuilder

/**
 * @author Kyle
 * on 2020/9/19
 */
fun Array<*>.toDataString(): String {
    val sb = StringBuilder()
    this.forEach {
        sb.append("${it.toString()},")
    }
    if (sb.isNotEmpty()) {
        return sb.deleteCharAt(sb.length - 1).toString()
    }
    return ""
}

fun List<*>.toDataString(): String {
    val sb = StringBuilder()
    this.forEach {
        sb.append("${it.toString()},")
    }
    if (sb.isNotEmpty()) {
        return sb.deleteCharAt(sb.length - 1).toString()
    }
    return ""
}

fun <T> List<T>?.toDataString(prefix: (Array<String>?) = arrayOf("[", "]"), middlefix: String = "，", call: ((Int, T) -> String)? = null): String {
    if (isNullOrEmpty()) return ""
    return "${prefix.getSafe(0)}${
        run {
            val sb = StringBuilder()
            forEachIndexed { index, it ->
                sb.append(call?.invoke(index, it) ?: it.toString())
                sb.append(middlefix)
            }
            if (sb.length > 1 && middlefix.isNotEmpty())
                sb.deleteCharAt(sb.length - 1)
            sb.toString()
        }
    }${prefix.getSafe(1)}"
}

fun <P> List<P>?.getLastData(): P? {
    if (this != null && this.isNotEmpty())
        return this[this.size - 1]
    return null
}

fun <P> List<P>?.getFirstData(): P? {
    if (this != null && this.isNotEmpty())
        return this[0]
    return null
}

fun <T> List<T>?.getSafe(index: Int?): T? {
    if (this == null || index == null)
        return null
    if (index < 0 || index >= size)
        return null
    return get(index)
}

fun FloatArray?.getSafe(index: Int, defaultValue: Float = 0F) =
    if (this == null || index >= size || index < 0) defaultValue else get(index)

fun <T> List<T>?.getSafe(index: Int, defaultValue: T? = null): T? {
    if (isNullOrEmpty() || index >= this.size || index < 0) return defaultValue
    return get(index)
}

fun FloatArray?.safeFirst(defaultValue: Float = 0F): Float {
    if (this == null || this.isEmpty()) return defaultValue
    return first()
}

fun FloatArray?.safeLast(defaultValue: Float = 0F): Float {
    if (this == null || this.isEmpty()) return defaultValue
    return last()
}

fun DoubleArray?.getSafe(index: Int, defaultValue: Double = 0.0): Double {
    if (this == null || index >= size || index < 0) return defaultValue
    return this[index]
}

fun FloatArray?.reset(value: Float = 0F) {
    if (this == null || this.isEmpty()) return
    for (i in 0 until this.size)
        this[i] = value
}

fun IntArray?.getSafe(index: Int, defaultValue: Int = 0): Int {
    if (this == null || index >= size || index < 0) return defaultValue
    return this[index]
}

fun LongArray?.getSafe(index: Int, defaultValue: Long = 0L): Long {
    if (this == null || index >= size || index < 0) return defaultValue
    return this[index]
}

fun <T> Array<T>?.getSafe(index: Int, defaultValue: T? = null): T? {
    if (isNullOrEmpty() || index >= this.size || index < 0) return defaultValue
    return get(index)
}

fun <T> MutableList<T>?.putSafe(index: Int, t: T) {
    if (this == null)
        return
    if (index < 0 || index >= size)
        return
    this.removeAt(index)
    this.add(index, t)
}

fun Array<String>?.getSafe(index: Int): String {
    if (this.isNullOrEmpty()) return ""
    if (index >= this.size) return ""
    return this[index]
}