package com.longbridge.market.mvp.ui.widget.option.strategy.guide.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.lb.ConvertDateLine
import com.lb.util.LogUtils
import com.longbridge.common.global.entity.OptionChainDataListV4
import com.longbridge.mdtrade.widget.option.OptionScrollUtils
import com.longbridge.mdtrade.widget.option.OptionBaseScrollView
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 *  日期滚动view 
 */
open class OptionDateScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : OptionBaseScrollView(context, attrs, defStyleAttr) {

    companion object {
        const val kCellHeight = 80.0f
        const val kCellWidthNormal = 100.0f
        const val kCellWidthSameYear = 80.0f
        private const val TAG = "TradeOptionDate_"
    }

    val convertDateLine = ConvertDateLine()

    var dataSource: List<ConvertDateLine.OptionDate> = mutableListOf()
        set(value) {

            field = value
            adapter.updateDataSource(value)
            post {
                LogUtils.d(TAG, "post scrollToInitialPosition()")
                scrollToInitialPosition()
            }
        }



    var initialScrollToIndex: Int = 0

    val collectionView: RecyclerView by lazy { createCollectionView() }

    private val overlayMaskView: OptionGenericMaskView by lazy { createMaskView() }
    private val flowLayout: DateLineLinearLayoutManager by lazy { DateLineLinearLayoutManager(context)
    }
    private val adapter: OptionDateLineAdapter by lazy { createDateLineAdapter() }

    private var selectedIndex: Int = -1
    private var lastFeedbackIndex: Int = -1

    // 是否在执行自动贴边后等待一次“完成震动”
    private var pendingHapticAfterSnap: Boolean = false

    // 延迟到下一帧再执行的“选中项”更新，避免在滚动回调/布局期间触发 adapter 通知
    private var pendingSelectionIndex: Int? = null
    private var pendingSelectionRunnable: Runnable? = null

    // 与 Adapter 中设置的左右 margin 保持一致（用于计算首尾 inset）
    private val cellHorizontalMarginPx: Int get() = 4.dpToPx()

    init {
        addView(collectionView)
        addView(overlayMaskView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        LogUtils.d(TAG, "onLayout(changed=$changed,l=$left,t=$top,r=$right,b=$bottom)")
        super.onLayout(changed, left, top, right, bottom)
        val collectionHeight = kCellHeight.dpToPx()
        val collectionY = (height - collectionHeight) / 2
        collectionView.layout(0, collectionY, width, collectionY + collectionHeight)
        // 统一 inset 计算
        val minWidthPx = minOf(kCellWidthSameYear, kCellWidthNormal).dpToPx()
        applySideInsetsForMinWidth(collectionView, minWidthPx, cellHorizontalMarginPx)
        overlayMaskView.layout(0, 0, width, height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        LogUtils.d(
            TAG, "onMeasure(wSpec=${MeasureSpec.toString(widthMeasureSpec)}, hSpec=${
                MeasureSpec.toString(heightMeasureSpec)
            })"
        )
        // 先让子 View 完成测量，避免 child 尺寸为 0
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // wrap_content 的期望高度 = 单行日期条高度（cell 高度）
        val collectionH = kCellHeight.dpToPx()
        val desiredHeight = collectionH

        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        val finalHeight = when (hMode) {
            MeasureSpec.EXACTLY -> hSize
            MeasureSpec.AT_MOST -> desiredHeight.coerceAtMost(hSize)
            else -> desiredHeight
        }
        setMeasuredDimension(measuredWidth, finalHeight)
        LogUtils.d(TAG, "onMeasure done measuredWidth=$measuredWidth measuredHeight=$measuredHeight desired=$desiredHeight")
    }

    override fun requestLayout() {
        super.requestLayout()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        LogUtils.d(TAG, "onSizeChanged(w=$w,h=$h,oldw=$oldw,oldh=$oldh)")
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onAttachedToWindow() {
        LogUtils.d(TAG, "onAttachedToWindow()")
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        LogUtils.d(TAG, "onDetachedFromWindow()")
        super.onDetachedFromWindow()
    }

    private fun createCollectionView(): RecyclerView {
        val rv = RecyclerView(context)
        return configureRecyclerViewCommon(
            recyclerView = rv,
            layoutManager = flowLayout,
            adapter = this@OptionDateScrollView.adapter,
            onScrolled = { scrollViewDidScroll(it) },
            onIdle = { centerIdx ->
                LogUtils.d(TAG, "SCROLL_STATE_IDLE -> snap & update")
                scrollToNearestOption()
                updateMaskSizeForCenterCell()
                onAfterScroll()
                if (centerIdx >= 0) {
                    if (pendingHapticAfterSnap) {
                        if (lastFeedbackIndex != centerIdx) {
                            LogUtils.d(TAG, "HAPTIC(afterSnap) centerIdx=$centerIdx")
                            //performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            lastFeedbackIndex = centerIdx
                        }
                        pendingHapticAfterSnap = false
                    } else if (lastFeedbackIndex != centerIdx) {
                        LogUtils.d(TAG, "HAPTIC(center change on idle) centerIdx=$centerIdx")
                        //performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        lastFeedbackIndex = centerIdx
                    }
                    notifyIfIndexChanged(centerIdx)
                } else {
                    pendingHapticAfterSnap = false
                }
            }
        )
    }

    private fun createMaskView(): OptionGenericMaskView {
        return OptionGenericMaskView(context).apply {
            clearRectSize = Size(kCellWidthNormal.dpToPx(), kCellHeight.dpToPx())
        }
    }

    var preSmoothPosition = -1
    private fun createDateLineAdapter(): OptionDateLineAdapter {
        val adapter = OptionDateLineAdapter(
            dataSource as MutableList<ConvertDateLine.OptionDate>,
            object : OptionDateLineAdapter.OnExpireDateClickListener {
                override fun onExpireDateClick(position: Int, expireDate: OptionChainDataListV4.ExpireDate) {
                    LogUtils.d(TAG, "onExpireDateClick: ")
                    LogUtils.i(TAG, "       position: $position")
                    didSelectItemAtIndex?.invoke(position)
                    if (preSmoothPosition != position) {
                        collectionView.smoothScrollToPosition(position)
                        preSmoothPosition = position
                    }
                }
            })
        return adapter
    }

    private fun scrollToInitialPosition() {
        LogUtils.d(TAG, "scrollToInitialPosition()")
        var targetIndex = initialScrollToIndex
        if (targetIndex < 0) targetIndex = 0
        else if (targetIndex >= dataSource.size) targetIndex = dataSource.size - 1
        if (targetIndex in 0 until dataSource.size) {
            val smoothScroller = CenterSmoothScroller(context)
            smoothScroller.targetPosition = targetIndex
            LogUtils.d(TAG, "startSmoothScroll to index=$targetIndex")
            flowLayout.startSmoothScroll(smoothScroller)
            post {
                LogUtils.d(TAG, "post updateSelectedState + updateMaskSizeForCenterCell + onAfterScroll")
                updateSelectedState()
                updateMaskSizeForCenterCell()
                onAfterScroll()
            }
        }
    }

    fun findCenterCell(): View? = OptionScrollUtils.findCenterChild(collectionView, width)

    fun scrollToNearestOption() {
        val centerCell = findCenterCell() ?: return
        val centerIndexPath = collectionView.getChildAdapterPosition(centerCell)
        if (centerIndexPath >= 0) {
            // 估算与屏幕中心的偏差，只有存在偏差时才发起贴边并在完成后震动
            val screenCenterX = width / 2f
            val lm = collectionView.layoutManager
            val cellCenter = if (lm != null) {
                (lm.getDecoratedLeft(centerCell) + lm.getDecoratedRight(centerCell)) / 2f
            } else {
                centerCell.left + centerCell.width / 2f
            }
            val delta = screenCenterX - cellCenter
            Log.d(TAG, "scrollToNearestOption centerIdx=$centerIndexPath delta=$delta")
            val smoothScroller = CenterSmoothScroller(context)
            smoothScroller.targetPosition = centerIndexPath
            if (abs(delta) > 0.5f) {
                pendingHapticAfterSnap = true
                Log.d(TAG, "startSmoothScroll (snap) to $centerIndexPath")
                flowLayout.startSmoothScroll(smoothScroller)
            } else {
                pendingHapticAfterSnap = false
                Log.d(TAG, "no snap needed")
            }
        }
    }

    fun scrollViewDidScroll(@Suppress("UNUSED_PARAMETER") scrollView: RecyclerView) {
        LogUtils.d(TAG, "scrollViewDidScroll()")
        updateSelectedState()
    }

    /**
     * 对外暴露：获取当前被蒙层居中的 item 下标。
     * 若列表为空或无法定位，返回 RecyclerView.NO_POSITION (-1)。
     */
    fun getCenteredIndex(): Int {
        val center = findCenterCell() ?: return RecyclerView.NO_POSITION
        return collectionView.getChildAdapterPosition(center)
    }

    protected override fun onAfterScroll() {
        // 子类覆写
    }

    private fun updateSelectedState() {
        val centerCell = findCenterCell() ?: return
        val centerIndexPath = collectionView.getChildAdapterPosition(centerCell)
        if (centerIndexPath < 0 || selectedIndex == centerIndexPath) return

        val inScrollOrLayout =
            collectionView.isComputingLayout || collectionView.scrollState != RecyclerView.SCROLL_STATE_IDLE

        if (inScrollOrLayout) {
            // 在滚动/布局过程中，不直接触发 adapter 通知；合并成一次延迟更新
            val prevPending = pendingSelectionIndex
            pendingSelectionIndex = centerIndexPath
            pendingSelectionRunnable?.let { collectionView.removeCallbacks(it) }
            val runnable = Runnable {
                val idx = pendingSelectionIndex
                pendingSelectionRunnable = null
                pendingSelectionIndex = null
                if (idx != null && idx != selectedIndex) {
                    LogUtils.d(
                        TAG, "deferred updateSelectedState: $selectedIndex -> $idx (notify adapter)"
                    )
                    selectedIndex = idx
                    adapter.updateSelectedPosition(idx)
                    // 将震动交给 IDLE 时机统一处理，避免滚动中多次触发
                } else {
                    LogUtils.d(
                        TAG,
                        "deferred updateSelectedState: skip (idx=$idx, selected=$selectedIndex)"
                    )
                }
            }
            pendingSelectionRunnable = runnable
            collectionView.post(runnable)
            LogUtils.d(
                TAG,
                "updateSelectedState deferred (inScrollOrLayout=$inScrollOrLayout) pending ${prevPending} -> $centerIndexPath"
            )
        } else {
            Log.d(
                TAG, "updateSelectedState: $selectedIndex -> $centerIndexPath (notify adapter now)"
            )
            selectedIndex = centerIndexPath
            adapter.updateSelectedPosition(centerIndexPath)
            // 将震动交给 IDLE 时机统一处理，避免滚动中多次触发
        }
    }

    private fun updateMaskSizeForCenterCell() {
        Log.d(TAG, "updateMaskSizeForCenterCell()")
        val centerCell = findCenterCell() ?: return
        // 使用当前居中 cell 的实测“内容宽度”和实际中心点，确保蒙层抠空与 cell 完全重合
        val measuredWidth = centerCell.width.takeIf { it > 0 } ?: return
        // 方案F：蒙层水平中心固定在屏幕中线，仅根据居中项宽度切换（80/100）
        val screenCenterX = width / 2f
        Log.d(TAG, "mask centerX(screenCenter)=$screenCenterX measuredWidth=$measuredWidth")
        overlayMaskView.clearRectCenterX = screenCenterX
        if (overlayMaskView.clearRectSize.width != measuredWidth) {
            Log.d(TAG, "mask size change ${overlayMaskView.clearRectSize.width} -> $measuredWidth")
            overlayMaskView.clearRectSize = Size(measuredWidth, kCellHeight.dpToPx())
        } else {
            // 尺寸未变更也要刷新路径以应用 centerX 的更新
            Log.d(TAG, "mask size same -> invalidate()")
            overlayMaskView.invalidate()
        }
    }

    private fun Float.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()
    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()

    private inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int
        ): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }
    }
}

private class DateLineLinearLayoutManager(context: Context) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    companion object {
        private const val kCellHeight = 80.0f

    }

    private val density: Float = context.resources.displayMetrics.density
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        Log.d("LB_FLOW", "generateDefaultLayoutParams()")
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT, kCellHeight.dpToPx()
        )
    }

    override fun canScrollHorizontally(): Boolean = true
    override fun canScrollVertically(): Boolean = false
    private fun Float.dpToPx(): Int = (this * density).roundToInt()
}




