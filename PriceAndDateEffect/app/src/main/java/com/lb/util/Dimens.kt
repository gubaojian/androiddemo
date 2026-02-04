package com.lb.util

import android.content.res.Resources
import kotlin.math.roundToInt

/**
 * 简化 dp 转 px 的重复代码，统一用属性访问：
 *  - 4.dp -> px(Int)
 *  - 8f.dp -> px(Int)
 */
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()


