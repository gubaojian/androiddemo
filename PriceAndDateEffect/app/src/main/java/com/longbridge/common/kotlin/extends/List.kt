package com.longbridge.common.kotlin.extends

import android.os.Build
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author Kyle
 * on 2021/9/2
 */
/**
 * 改用CopyOnWriteArrayList，若再进Collections.sort(copyOnWriteArrayList, myComparator)时，会报异常：
 * java.lang.UnsupportedOperationException
 * 因为CopyOnWriteArrayList不支持set()操作。
 * 不会发生在 Android 8 及更高版本上。它只发生在 5.0 - 7.1
 */
fun <T> MutableList<T>?.safeSortWith(comparator: Comparator<in T>) {
    if (this.isNullOrEmpty() || this.size == 1) return
    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && this is CopyOnWriteArrayList) {
            val temp: ArrayList<T> = ArrayList<T>(this)
            temp.sortWith(comparator)
            this.clear()
            this.addAll(temp)
        } else {
            this.sortWith(comparator)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> ArrayList<T>.updateList(data: List<T>?, refresh: Boolean = true) {
    if (refresh) {
        clear()
    }
    if (data.isNullOrEmpty()) return
    addAll(data)
}

inline fun <T, R> Iterable<T>.filterMapTo(predicate: (T) -> Boolean, transform: (T) -> R): ArrayList<R> {
    val destination = ArrayList<R>()
    for (item in this) {
        if (predicate(item)) {
            destination.add(transform(item))
        }
    }
    return destination
}

fun <T> CopyOnWriteArrayList<T>.updateList(data: List<T>?, refresh: Boolean = true) {
    if (refresh) {
        clear()
    }
    if (data.isNullOrEmpty()) return
    addAll(data)
}

fun <T> ArrayList<T>.updateList(data: Array<T>?, refresh: Boolean = true) {
    if (refresh) {
        clear()
    }
    if (data.isNullOrEmpty()) return
    addAll(data)
}

fun <T> List<T>.upsert(value: T, finder: (T) -> Boolean) = indexOfFirst(finder).let { index ->
    if (index >= 0) copy(index, value) else this + value
}

fun <T> List<T>.copy(i: Int, value: T): List<T> = toMutableList().apply { set(i, value) }

fun <T> List<T>.add(value: T): List<T> = toMutableList().apply { add(value) }

fun <T> List<T>.add(value: List<T>): List<T> = toMutableList().apply { addAll(value) }

fun <T> List<T>.insert(i: Int, value: List<T>): List<T> = toMutableList().apply { addAll(i, value) }

fun <T> List<T>.insert(i: Int, value: T): List<T> = toMutableList().apply { add(i, value) }

fun <T> List<T>.replace(i: Int, value: T): List<T> = toMutableList().apply {
    if (i >= size) return@apply
    removeAt(i)
    add(i, value)
}

inline fun <T> List<T>.delete(filter: (T) -> Boolean): List<T> = toMutableList().apply {
    val index = indexOfFirst(filter)
    if (index != -1) removeAt(index)
}

inline fun <T> List<T>.delete(i: Int): List<T> = toMutableList().apply { removeAt(i) }

inline fun <T> List<T>.delete(value: T): List<T> = toMutableList().apply { remove(value) }