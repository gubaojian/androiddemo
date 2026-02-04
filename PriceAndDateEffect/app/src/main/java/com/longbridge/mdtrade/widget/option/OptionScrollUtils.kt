package com.longbridge.mdtrade.widget.option

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 提取公共滚动计算逻辑，降低各 ScrollView 的重复实现。
 */
object OptionScrollUtils {

    /**
     * 在给定父容器宽度下，查找 RecyclerView 中最接近屏幕水平中心的 child。
     * 使用 decorated 边界，兼容 item margin。
     */
    fun findCenterChild(recyclerView: RecyclerView, parentWidth: Int): View? {
        val childCount = recyclerView.childCount
        if (childCount == 0) return null
        val lm = recyclerView.layoutManager ?: return null
        val screenCenterX = parentWidth / 2f
        var centerCell: View? = null
        var minDistance = Float.MAX_VALUE
        for (i in 0 until childCount) {
            val cell = recyclerView.getChildAt(i)
            val leftDecorated = (lm as RecyclerView.LayoutManager).getDecoratedLeft(cell)
            val rightDecorated = lm.getDecoratedRight(cell)
            val cellCenter = (leftDecorated + rightDecorated) / 2f
            val distance = abs(cellCenter - screenCenterX)
            if (distance < minDistance) {
                minDistance = distance
                centerCell = cell
            }
        }
        return centerCell
    }
}


