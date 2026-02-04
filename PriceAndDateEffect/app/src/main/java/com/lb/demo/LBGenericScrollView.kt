package com.lb.demo

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.AttributeSet
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.lb.LBGenericMaskView
import com.lb.LBGenericScrollViewCellModel
import com.lb.demo.LBGenericAdapter
import com.lb.price.one.R
import kotlin.math.abs
import kotlin.math.roundToInt



/**
 * 通用滚动视图（iOS -> Android）
 */
open class LBGenericScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object{
         const val kCellHeight = 80.0f
         const val kCellWidthNormal = 100.0f
         const val kCellWidthSameYear = 80.0f
         private const val TAG = "LBGenericScrollView_"

    }

    var dataSource: List<LBGenericScrollViewCellModel> = emptyList()
        set(value) {
            Log.d(TAG, "dataSource set(size=${value.size}) caller=\n${Log.getStackTraceString(Throwable())}")
            field = value
            flowLayout.dataSource = value
            adapter.updateDataSource(value)
            post { 
                Log.d(TAG, "post scrollToInitialPosition()")
                scrollToInitialPosition() 
            }
        }

    var initialScrollToIndex: Int = 0
    var didSelectItemAtIndex: ((Int) -> Unit)? = null

    val collectionView: RecyclerView by lazy { createCollectionView() }
    private val overlayMaskView: LBGenericMaskView by lazy { createMaskView() }
    private val flowLayout: LBGenericFlowLayout by lazy { LBGenericFlowLayout(context) }
    private val adapter: LBGenericAdapter by lazy { createAdapter() }

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
        setBackgroundColor(context.getColor(R.color.front_bg_color_1))
       // setBackgroundColor(Color.BLUE)
        addView(collectionView)
        addView(overlayMaskView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d(TAG, "onLayout(changed=$changed,l=$left,t=$top,r=$right,b=$bottom)")
        super.onLayout(changed, left, top, right, bottom)
        val collectionHeight = kCellHeight.dpToPx()
        val collectionY = (height - collectionHeight) / 2
        collectionView.layout(0, collectionY, width, collectionY + collectionHeight)
        // 计算左右 inset：让首尾 item 也能滑到屏幕正中（蒙层抠空处）
        // 方案A：统一使用“最小宽度 80dp”的装饰后宽度计算左右 inset，确保最窄项在首尾也能居中
        val minWidthPx = minOf(kCellWidthSameYear, kCellWidthNormal).dpToPx()
        val minDecoratedWidth = minWidthPx + cellHorizontalMarginPx * 2
        val leftInset = ((width - minDecoratedWidth) / 2).coerceAtLeast(0)
        val rightInset = leftInset
        if (collectionView.paddingLeft != leftInset || collectionView.paddingRight != rightInset) {
            Log.d(TAG, "RV setPadding L=${collectionView.paddingLeft}->${leftInset}, R=${collectionView.paddingRight}->${rightInset}\ncaller=\n${Log.getStackTraceString(Throwable())}")
            collectionView.setPadding(leftInset, 0, rightInset, 0)
        }
        if (collectionView.clipToPadding) {
            Log.d(TAG, "RV clipToPadding=false (was true)")
            collectionView.clipToPadding = false
        } else {
            Log.d(TAG, "RV clipToPadding already false")
        }
        overlayMaskView.layout(0, 0, width, height)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure(wSpec=${MeasureSpec.toString(widthMeasureSpec)}, hSpec=${MeasureSpec.toString(heightMeasureSpec)})")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure done measuredWidth=$measuredWidth measuredHeight=$measuredHeight")
    }
    override fun requestLayout() {
        Log.d(TAG, "requestLayout() called\ncaller=\n${Log.getStackTraceString(Throwable())}")
        super.requestLayout()
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d(TAG, "onSizeChanged(w=$w,h=$h,oldw=$oldw,oldh=$oldh)")
        super.onSizeChanged(w, h, oldw, oldh)
    }
    override fun onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow()")
        super.onAttachedToWindow()
    }
    override fun onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow()")
        super.onDetachedFromWindow()
    }

    private fun createCollectionView(): RecyclerView {
        return RecyclerView(context).apply {
            layoutManager = flowLayout
            adapter = this@LBGenericScrollView.adapter
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            overScrollMode = OVER_SCROLL_NEVER
            // 使用硬件加速，避免快速滚动时文本出现“叠影/拖影”
            //  setLayerType(android.view.View.LAYER_TYPE_NONE, null)
            // 关闭 change 动画，防止 notifyItemChanged 造成旧/新 ViewHolder 交叠的视觉效果
            val animator = DefaultItemAnimator().apply {
                supportsChangeAnimations = false
            }
            itemAnimator = animator
            Log.d(TAG, "RecyclerView created: hardwareLayer=ON, supportsChangeAnimations=false")
            addOnLayoutChangeListener { _, l, t, r, b, ol, ot, orr, ob ->
                Log.d(TAG, "RecyclerView onLayoutChange new=[$l,$t,$r,$b] old=[$ol,$ot,$orr,$ob]")
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    Log.d(TAG, "onScrolled dx=$dx, dy=$dy computingLayout=${recyclerView.isComputingLayout}")
                    super.onScrolled(recyclerView, dx, dy)
                    scrollViewDidScroll(recyclerView)
                }
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    Log.d(TAG, "onScrollStateChanged state=$newState computingLayout=${recyclerView.isComputingLayout}")
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            Log.d(TAG, "SCROLL_STATE_IDLE -> snap & update")
                            scrollToNearestOption()
                            updateMaskSizeForCenterCell()
                            onAfterScroll()
                            // 在真正停止时触发一次震动：
                            // 1) 如果刚刚发起了贴边，则在贴边完成后的这个 IDLE 震动一次
                            // 2) 如果未贴边，但最终选中项与上次震动的不一致，也震动一次
                            val center = findCenterCell()
                            val centerIdx = if (center != null) collectionView.getChildAdapterPosition(center) else -1
                            if (centerIdx >= 0) {
                                if (pendingHapticAfterSnap) {
                                    if (lastFeedbackIndex != centerIdx) {
                                        Log.d(TAG, "HAPTIC(afterSnap) centerIdx=$centerIdx")
                                        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        lastFeedbackIndex = centerIdx
                                    }
                                    pendingHapticAfterSnap = false
                                } else if (lastFeedbackIndex != centerIdx) {
                                    Log.d(TAG, "HAPTIC(center change on idle) centerIdx=$centerIdx")
                                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    lastFeedbackIndex = centerIdx
                                }
                            } else {
                                pendingHapticAfterSnap = false
                            }
                        }
                    }
                }
            })
        }
    }

    private fun createMaskView(): LBGenericMaskView {
        return LBGenericMaskView(context).apply {
            clearRectSize = Size(kCellWidthNormal.dpToPx(), kCellHeight.dpToPx())
        }
    }

    private fun createAdapter(): LBGenericAdapter {
        return LBGenericAdapter { position ->
            Log.d(TAG, "onItemClick -> smoothScrollToPosition($position)")
            collectionView.smoothScrollToPosition(position)
            didSelectItemAtIndex?.invoke(position)
        }
    }

    private fun scrollToInitialPosition() {
        Log.d(TAG, "scrollToInitialPosition()")
        var targetIndex = initialScrollToIndex
        if (targetIndex < 0) targetIndex = 0
        else if (targetIndex >= dataSource.size) targetIndex = dataSource.size - 1
        if (targetIndex in 0 until dataSource.size) {
            val smoothScroller = CenterSmoothScroller(context)
            smoothScroller.targetPosition = targetIndex
            Log.d(TAG, "startSmoothScroll to index=$targetIndex")
            flowLayout.startSmoothScroll(smoothScroller)
            post {
                Log.d(TAG, "post updateSelectedState + updateMaskSizeForCenterCell + onAfterScroll")
                updateSelectedState()
                updateMaskSizeForCenterCell()
                onAfterScroll()
            }
        }
    }

    fun findCenterCell(): View? {
        val screenCenterX = width / 2f
        val childCount = collectionView.childCount
        if (childCount == 0) return null
        val lm = collectionView.layoutManager ?: return null
        var centerCell: View? = null
        var minDistance = Float.MAX_VALUE
        for (i in 0 until childCount) {
            val cell = collectionView.getChildAt(i)
            // 使用“decorated”边界（含 margin）计算中心，避免加了 item margin 后产生偏移
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
        Log.d(TAG, "scrollViewDidScroll()")
        updateSelectedState()
    }

    protected open fun onAfterScroll() {
        // 子类覆写
    }

    private fun updateSelectedState() {
        val centerCell = findCenterCell() ?: return
        val centerIndexPath = collectionView.getChildAdapterPosition(centerCell)
        if (centerIndexPath < 0 || selectedIndex == centerIndexPath) return

        val inScrollOrLayout = collectionView.isComputingLayout ||
                collectionView.scrollState != RecyclerView.SCROLL_STATE_IDLE

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
                    Log.d(TAG, "deferred updateSelectedState: $selectedIndex -> $idx (notify adapter)")
                    selectedIndex = idx
                    adapter.updateSelectedPosition(idx)
                    // 将震动交给 IDLE 时机统一处理，避免滚动中多次触发
                } else {
                    Log.d(TAG, "deferred updateSelectedState: skip (idx=$idx, selected=$selectedIndex)")
                }
            }
            pendingSelectionRunnable = runnable
            collectionView.post(runnable)
            Log.d(
                TAG,
                "updateSelectedState deferred (inScrollOrLayout=$inScrollOrLayout) pending ${prevPending} -> $centerIndexPath"
            )
        } else {
            Log.d(TAG, "updateSelectedState: $selectedIndex -> $centerIndexPath (notify adapter now)")
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
        override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }
    }
}

private class LBGenericFlowLayout(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {
    companion object{
        private const val kCellHeight = 80.0f

    }
    var dataSource: List<LBGenericScrollViewCellModel> = emptyList()

    private val density: Float = context.resources.displayMetrics.density
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        Log.d("LB_FLOW", "generateDefaultLayoutParams()")
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            kCellHeight.dpToPx()
        )
    }
    override fun canScrollHorizontally(): Boolean = true
    override fun canScrollVertically(): Boolean = false
    private fun Float.dpToPx(): Int = (this * density).roundToInt()
}




