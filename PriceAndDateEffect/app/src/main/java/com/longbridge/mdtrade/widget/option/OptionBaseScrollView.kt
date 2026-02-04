package com.longbridge.mdtrade.widget.option

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DefaultItemAnimator

/**
 * 小基类：承载通用的“选中回调”与防抖能力，不改变现有子类行为。
 *
 * 子类可直接使用：
 * - didSelectItemAtIndex 回调
 * - notifyIfIndexChanged(centerIdx) 进行对外通知（带防抖）
 * - configureRecyclerViewCommon(...) 统一初始化 RecyclerView 与滚动回调
 * - applySideInsetsForMinWidth(...) 统一左右 inset 计算
 */
open class OptionBaseScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var didSelectItemAtIndex: ((Int) -> Unit)? = null

    // 防抖：上一次已通知的中心下标（命名避免与子类字段冲突）
    protected var lastNotifiedCenterIndex: Int = RecyclerView.NO_POSITION

    protected fun notifyIfIndexChanged(centerIdx: Int) {
        if (centerIdx != RecyclerView.NO_POSITION && centerIdx != lastNotifiedCenterIndex) {
            lastNotifiedCenterIndex = centerIdx
            didSelectItemAtIndex?.invoke(centerIdx)
        }
    }

    /**
     * 滚动完成后的钩子。子类可按需覆写。
     */
    protected open fun onAfterScroll() {
        // default no-op
    }

    /**
     * 通用的 RecyclerView 初始化：关闭多余动画、绑定滚动监听、在 IDLE 时回调当前中心下标。
     */
    protected fun configureRecyclerViewCommon(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager,
        adapter: RecyclerView.Adapter<*>,
        onScrolled: (RecyclerView) -> Unit,
        onIdle: (centerIndex: Int) -> Unit
    ): RecyclerView {
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.isHorizontalScrollBarEnabled = false
        recyclerView.isVerticalScrollBarEnabled = false
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val animator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
        recyclerView.itemAnimator = animator
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                onScrolled(rv)
            }
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val center = OptionScrollUtils.findCenterChild(rv, width)
                    val idx = if (center != null) rv.getChildAdapterPosition(center) else RecyclerView.NO_POSITION
                    onIdle(idx)
                }
            }
        })
        return recyclerView
    }

    /**
     * 依据“最小 cell 宽度 + 左右 margin”计算左右 inset，使首尾最窄项也能居中。
     */
    protected fun applySideInsetsForMinWidth(
        recyclerView: RecyclerView,
        minCellWidthPx: Int,
        cellHorizontalMarginPx: Int
    ) {
        val minDecoratedWidth = minCellWidthPx + cellHorizontalMarginPx * 2
        val leftInset = ((width - minDecoratedWidth) / 2).coerceAtLeast(0)
        val rightInset = leftInset
        if (recyclerView.paddingLeft != leftInset || recyclerView.paddingRight != rightInset) {
            recyclerView.setPadding(leftInset, 0, rightInset, 0)
        }
        if (recyclerView.clipToPadding) {
            recyclerView.clipToPadding = false
        }
    }
}


