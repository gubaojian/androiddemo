package com.longbridge.mdtrade.widget.option

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.lb.LBGenericMaskView
import com.lb.ConvertDateLine
import com.lb.price.one.R
import com.lb.util.LogUtils
import com.longbridge.common.global.entity.StrikePriceInfo
import com.longbridge.mdtrade.adapter.TradeOptionPriceAdapter
import com.longbridge.mdtrade.adapter.TradeOptionPriceAdapter.OnExpirePriceClickListener
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * option-价格滚动View
 */
class TradeOptionRealPriceScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : OptionBaseScrollView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = " TradeOptionRealPriceView_"
        const val kCellHeight = 80.0f
        const val kCellWidthNormal = 100.0f
        const val kCellWidthSameYear = 80.0f
    }
    val convertDateLine = ConvertDateLine()

    var dataSource: List<StrikePriceInfo> = mutableListOf()
        set(value) {
            field = value
            adapter.updateDataSource(value)
            post { scrollToInitialPosition() }
        }
    var initialScrollToIndex: Int = 0

    val collectionView: RecyclerView by lazy { createCollectionView() }
    private val overlayMaskView: LBGenericMaskView by lazy { createMaskView() }
    private val flowLayout: RealOptionPriceLinearLayoutManager by lazy { RealOptionPriceLinearLayoutManager(context) }
    private val adapter: TradeOptionPriceAdapter by lazy { createAdapter() }

    // 震动相关状态
    private var lastFeedbackIndex: Int = -1
    private var pendingHapticAfterSnap: Boolean = false
    // 标签高度自适应：优先用容器的实测高度；未测量时退回默认 26dp
    private val defaultLabelHeightPx: Int get() = 26.dpToPx()
    private val labelHeightPx: Int
        get() = currentPriceContainer.measuredHeight.takeIf { it > 0 } ?: defaultLabelHeightPx
    // 竖线底部额外延伸高度
    private val extraLineBottomPx: Int get() = 10.dpToPx()
    // 整体气泡（容器+竖线）相对 collectionView 顶部的下移偏移量（正数=向下）
    private val bubbleYOffsetPx: Int get() = 8.dpToPx()
    // 细调：为避免与蒙层边框重叠，将气泡整体再向上微调 4dp（负值表示向上）
    private val bubbleUpAdjustPx: Int get() = (-4).dpToPx()
    // 期望气泡底边与蒙层上边界的最小可视间距
    private val containerMaskGapPx: Int get() = 2.dpToPx()

    var currentPrice: Double = 0.0
        set(value) {
            if (field == value) return
            field = value
            updateCurrentPriceUI()
        }

    private val verticalLine: View by lazy { View(context).apply { setBackgroundColor(Color.BLACK) } }
    // 复用的圆角背景，便于动态控制左右圆角
    private val containerBg: GradientDrawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(context.getColor(R.color.text_color_1))
            cornerRadius = 4.dpToPx().toFloat()
        }
    }


    private val currentPriceContainer: FrameLayout by lazy {
        FrameLayout(context).apply {
            // 宽度稍后按内容计算，高度先让其自适应（WRAP_CONTENT）
            layoutParams = LayoutParams(1, LayoutParams.WRAP_CONTENT)
            LayoutInflater.from(context).inflate(R.layout.trade_view_curr_price_container, this, true)
            // 黑色圆角背景，后续根据贴边动态调整左右圆角
            background = containerBg
        }
    }

    private val currentPriceLabel: TextView by lazy { currentPriceContainer.findViewById(R.id.tv_current_price_label) }
    private val leftArrowView: ImageView by lazy { currentPriceContainer.findViewById(R.id.iv_curr_price_left) }
    private val rightArrowView: ImageView by lazy { currentPriceContainer.findViewById(R.id.iv_curr_price_right) }

    init {
        // 允许子视图超出自身范围绘制，避免顶部圆角被裁剪
        clipToPadding = false
        clipChildren = false
        LogUtils.d(TAG, "init: clipToPadding=$clipToPadding clipChildren=$clipChildren")
        // 默认使用透明背景，避免未绑定数据时出现整块深色底
        setBackgroundColor(Color.TRANSPARENT)
        // 先添加价格列表与蒙层
        addView(collectionView)
        addView(overlayMaskView)
        // 再添加悬浮的竖线与价格气泡
        addView(verticalLine)
        addView(currentPriceContainer)
        // 初始不显示气泡与竖线；待有数据时再显式展示
        verticalLine.visibility = GONE
        currentPriceContainer.visibility = GONE
        currentPriceContainer.setOnClickListener { scrollToNearestPriceOption() }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        LogUtils.d(TAG, "onLayout(changed=$changed,l=$left,t=$top,r=$right,b=$bottom)")
        super.onLayout(changed, left, top, right, bottom)
        val collectionHeight = kCellHeight.dpToPx()
        val collectionY = (height - collectionHeight) / 2
        collectionView.layout(0, collectionY, width, collectionY + collectionHeight)
        val minWidthPx = minOf(kCellWidthSameYear, kCellWidthNormal).dpToPx()
        applySideInsetsForMinWidth(collectionView, minWidthPx, 4.dpToPx())
        overlayMaskView.layout(0, 0, width, height)
        post { updateCurrentPricePosition() }
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        LogUtils.d(TAG, "onSizeChanged(w=$w,h=$h,oldw=$oldw,oldh=$oldh)")
        super.onSizeChanged(w, h, oldw, oldh)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        LogUtils.d(TAG, "onMeasure(wSpec=${MeasureSpec.toString(widthMeasureSpec)}, hSpec=${MeasureSpec.toString(heightMeasureSpec)})")
        // 先让父类测量子 View，确保 child 拿到尺寸（否则我们手动 setMeasuredDimension 会导致 child 尺寸为 0）
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // wrap_content 时的期望总高度：价格条高度 + 标签高度 + 顶部间隙(10dp) + 竖线额外延伸 + 下移偏移
        val collectionH = kCellHeight.dpToPx()
        val desiredHeight = collectionH +
                (currentPriceContainer.measuredHeight.takeIf { it > 0 } ?: defaultLabelHeightPx) +
                10.dpToPx() +
                extraLineBottomPx +
                bubbleYOffsetPx +
                // 额外为气泡与蒙层之间预留的头部空间（两侧平均，提升 collectionView.top）
                containerMaskGapPx * 2

        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        val measuredW = measuredWidth
        val measuredH = measuredHeight

        val finalHeight = when (hMode) {
            MeasureSpec.EXACTLY -> hSize                       // match_parent / 固定值
            MeasureSpec.AT_MOST -> desiredHeight
                .coerceAtMost(hSize)                           // 不超过父约束
            else -> desiredHeight                               // UNSPECIFIED -> 使用期望高度
        }
        setMeasuredDimension(measuredW, finalHeight)
        LogUtils.d(TAG, "onMeasure done measuredWidth=$measuredWidth measuredHeight=$measuredHeight desired=$desiredHeight")
    }
    override fun requestLayout() {

        super.requestLayout()
    }
    override fun onAttachedToWindow() {
        LogUtils.d(TAG, "onAttachedToWindow()")
        super.onAttachedToWindow()
    }
    override fun onDetachedFromWindow() {
        LogUtils.d(TAG, "onDetachedFromWindow()")
        super.onDetachedFromWindow()
    }

    override fun onAfterScroll() {
        // 仅在滚动状态改变后更新，避免在 RecyclerView 布局过程中频繁 requestLayout 造成闪烁
        post { updateCurrentPricePosition() }
    }

    private fun updateCurrentPriceUI() {
        LogUtils.d(TAG, "updateCurrentPriceUI()")
        setCurrentPriceLabelText()
        updateCurrentPricePosition()
    }

    private fun findNearestPriceIndex(): Int {
        LogUtils.d(TAG, "findNearestPriceIndex()")
        if (dataSource.isEmpty()) return 0
        var minDiff = Double.MAX_VALUE
        var nearestIndex = 0
        for (i in dataSource.indices) {
            // todo 需要处理toDouble 异常
            val diff = abs(dataSource[i].price.toDouble() - currentPrice)
            if (diff < minDiff) {
                minDiff = diff
                nearestIndex = i
            }
        }
        LogUtils.d(TAG, "findNearestPriceIndex result=$nearestIndex")
        return nearestIndex
    }

    private fun isIndexVisible(index: Int): Boolean {
        LogUtils.d(TAG, "isIndexVisible(index=$index)")
        if (index !in dataSource.indices) return false
        val lm = collectionView.layoutManager as? LinearLayoutManager ?: return false
        // 改为基于实际 child 位置与“扩展视口（包含左右 padding）”的相交判断，
        // 避免因为我们给 RecyclerView 设置了左右 inset 而过早判定为不可见
        val child = lm.findViewByPosition(index) ?: return false
        val left = child.left
        val right = child.right
        val visibleStart = -collectionView.paddingLeft
        val visibleEnd = collectionView.width + collectionView.paddingRight
        val visible = right > visibleStart && left < visibleEnd
        LogUtils.d(TAG, "isIndexVisible: left=$left right=$right visibleStart=$visibleStart visibleEnd=$visibleEnd -> $visible")
        return visible
    }

    private fun updateCurrentPricePosition() {
        LogUtils.d(TAG, "updateCurrentPricePosition()")
        // 无数据时隐藏前景元素并返回，避免显示大块背景
        if (dataSource.isEmpty()) {
            verticalLine.visibility = GONE
            currentPriceContainer.visibility = GONE
            return
        }
        var lowerIndex = -1
        var upperIndex = -1
        for (i in dataSource.indices) {
            // todo 需要处理toDouble 异常
            val price = dataSource[i].price.toDouble()
            if (price <= currentPrice) lowerIndex = i
            if (price >= currentPrice && upperIndex == -1) {
                upperIndex = i
                break
            }
        }
        if (lowerIndex == -1) lowerIndex = 0
        if (upperIndex == -1) upperIndex = dataSource.size - 1
        // 若 currentPrice 恰好等于列表某一项，为了始终显示在两项之间，偏向“右侧边界”
        // 例如：current=53.0，列表包含 53.0 -> 选择 [index, index+1] 的中点
        val eps = 1e-6
        if (upperIndex == lowerIndex) {
            val k = lowerIndex
            val priceK = dataSource[k].price.toDouble()
            if (k < dataSource.size - 1 && kotlin.math.abs(priceK - currentPrice) < eps) {
                // 使用右侧相邻项作为上界
                upperIndex = k + 1
            } else if (k > 0 && kotlin.math.abs(priceK - currentPrice) < eps) {
                // 已经是末尾，退一步，选择 [k-1, k]
                lowerIndex = k - 1
            }
        }
        LogUtils.d(TAG, "updateCurrentPricePosition: lower=$lowerIndex upper=$upperIndex")

        val lowerVisible = isIndexVisible(lowerIndex)
        val upperVisible = isIndexVisible(upperIndex)
        LogUtils.d(TAG, "updateCurrentPricePosition: lowerVisible=$lowerVisible upperVisible=$upperVisible")
        if (lowerVisible && upperVisible) {
            layoutFloatingLabel(lowerIndex, upperIndex)
        } else {
            layoutBoundaryLabel()
        }
    }

    private fun layoutFloatingLabel(lowerIndex: Int, upperIndex: Int) {
        LogUtils.d(TAG, "layoutFloatingLabel(lower=$lowerIndex, upper=$upperIndex)")
        val lm = collectionView.layoutManager
        val lowerView = lm?.findViewByPosition(lowerIndex)
        val upperView = lm?.findViewByPosition(upperIndex)
        if (lowerView == null || upperView == null) return

        // 使用相邻两个 item 的边界（lower 的右边缘与 upper 的左边缘的中点）
        val boundaryX = (lowerView.right + upperView.left) / 2f
        val floatingX = boundaryX
        LogUtils.d(TAG, "layoutFloatingLabel: boundaryX=$boundaryX")

        ensureContainerHeight()
        var labelY = collectionView.top - labelHeightPx - 10.dpToPx() + bubbleYOffsetPx + bubbleUpAdjustPx
        // 防止悬浮块顶部被父容器裁剪：向下限位一个最小安全边距
        labelY = max(2.dpToPx(), labelY)

        // 更新文本，随后按 wrap_content 重新测量容器宽度（使用 XML 的 padding/margin）
        setCurrentPriceLabelText()
        val measuredContainerWidth = measureContainerWidth()
        var containerX = floatingX - measuredContainerWidth / 2f
        val minX = 0f
        val maxX = width - measuredContainerWidth.toFloat()
        containerX = containerX.coerceIn(minX, maxX)
        LogUtils.d(TAG, "layoutFloatingLabel: containerWidth=$measuredContainerWidth containerX=$containerX labelY=$labelY")

        currentPriceContainer.visibility = VISIBLE
        verticalLine.visibility = VISIBLE
        ensureVerticalLineLayout()
        leftArrowView.visibility = GONE
        rightArrowView.visibility = GONE
        // 使用 XML 中的 padding/margin，不再在代码里改动
        // 浮动模式：左右两侧都使用圆角
        updateContainerCorner(roundLeft = true, roundRight = true)

        // 使用平移避免频繁 requestLayout
        LogUtils.d(TAG, "layoutFloatingLabel: container translate from x=${currentPriceContainer.translationX}, y=${currentPriceContainer.translationY} -> x=$containerX y=$labelY")
        currentPriceContainer.translationX = containerX
        currentPriceContainer.translationY = labelY.toFloat()

        val lineStartY = labelY + labelHeightPx
        // 竖线高度为 cell 高度 + 额外 6dp（向下延伸）
        val cellHeight = lowerView.height.takeIf { it > 0 } ?: labelHeightPx
        val desiredHeight = cellHeight + extraLineBottomPx
        val params = verticalLine.layoutParams ?: LayoutParams(2.dpToPx(), desiredHeight)
        LogUtils.d(TAG, "layoutFloatingLabel: ensure verticalLine params oldW=${params.width} oldH=${params.height} desiredH=$desiredHeight")
        if (params.height != desiredHeight) {
            params.height = desiredHeight
            verticalLine.layoutParams = params
            LogUtils.d(TAG, "layoutFloatingLabel: verticalLine setLayoutParams(h=$desiredHeight)")
        } else {
            LogUtils.d(TAG, "layoutFloatingLabel: verticalLine height unchanged -> skip setLayoutParams")
        }
        verticalLine.translationX = (floatingX - (params.width / 2f)).toFloat()
        verticalLine.translationY = lineStartY.toFloat()
        LogUtils.d(TAG, "layoutFloatingLabel: verticalLine x=${verticalLine.translationX} y=${verticalLine.translationY} h=${params.height}")
    }

    private fun layoutBoundaryLabel() {
        LogUtils.d(TAG, "layoutBoundaryLabel()")
        val centerView = findCenterCell() ?: return
        val centerPos = collectionView.getChildAdapterPosition(centerView)
        if (centerPos !in dataSource.indices) return
        // todo 需要处理toDouble 异常
        val centerPrice = dataSource[centerPos].price.toDouble()

        ensureContainerHeight()
        var labelY = collectionView.top - labelHeightPx - 10.dpToPx() + bubbleYOffsetPx + bubbleUpAdjustPx
        // 防止悬浮块顶部被父容器裁剪：向下限位一个最小安全边距
        labelY = max(2.dpToPx(), labelY)

        verticalLine.visibility = GONE
        currentPriceContainer.visibility = VISIBLE

        setCurrentPriceLabelText()

        if (currentPrice < centerPrice) {
            // 显示在左边界，左箭头（按设计稿）
            leftArrowView.visibility = VISIBLE
            rightArrowView.visibility = GONE
            currentPriceContainer.translationX = 0f
            currentPriceContainer.translationY = labelY.toFloat()
            // 左贴边：左侧直角、右侧圆角
            updateContainerCorner(roundLeft = false, roundRight = true)
            // 容器宽度直接采用 XML 布局（包含内部 padding/margin 所需占位）
            val containerWidth = measureContainerWidth()
            LogUtils.d(TAG, "layoutBoundaryLabel: LEFT edge containerWidth=$containerWidth labelY=$labelY")
        } else {
            // 显示在右边界，右箭头（按设计稿）
            rightArrowView.visibility = VISIBLE
            leftArrowView.visibility = GONE
            // 右贴边：右侧直角、左侧圆角
            updateContainerCorner(roundLeft = true, roundRight = false)
            // 容器宽度直接采用 XML 布局（包含内部 padding/margin 所需占位）
            val containerWidth = measureContainerWidth()
            LogUtils.d(TAG, "layoutBoundaryLabel RIGHT: container translate from x=${currentPriceContainer.translationX}, y=${currentPriceContainer.translationY} -> x=${(width - containerWidth)} y=$labelY")
            currentPriceContainer.translationX = (width - containerWidth).toFloat()
            currentPriceContainer.translationY = labelY.toFloat()
            LogUtils.d(TAG, "layoutBoundaryLabel: RIGHT edge containerWidth=$containerWidth labelY=$labelY")
        }
    }

    private fun ensureContainerHeight() {
        LogUtils.d(TAG, "ensureContainerHeight()")
        val lp = currentPriceContainer.layoutParams
        if (lp == null) {
            LogUtils.d(TAG, "ensureContainerHeight: set WRAP_CONTENT (new lp)")
            currentPriceContainer.layoutParams = LayoutParams(1, LayoutParams.WRAP_CONTENT)
        } else if (lp.height != LayoutParams.WRAP_CONTENT) {
            lp.height = LayoutParams.WRAP_CONTENT
            currentPriceContainer.layoutParams = lp
            LogUtils.d(TAG, "ensureContainerHeight: set WRAP_CONTENT (mutate)")
        } else LogUtils.d(TAG, "ensureContainerHeight: already WRAP_CONTENT")
    }

    private fun setContainerWidth(widthPx: Int) {
        LogUtils.d(TAG, "setContainerWidth($widthPx) old=${currentPriceContainer.layoutParams?.width}")
        val lp = currentPriceContainer.layoutParams
        // 改为尊重 XML 的 wrap_content 宽度，这里不再修改 width
        if (lp == null) {
            currentPriceContainer.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        } else if (lp.width != LayoutParams.WRAP_CONTENT) {
            lp.width = LayoutParams.WRAP_CONTENT
            currentPriceContainer.layoutParams = lp
        }

    }

    private fun ensureVerticalLineLayout() {
        LogUtils.d(TAG, "ensureVerticalLineLayout()")
        val lp = verticalLine.layoutParams
        if (lp == null || lp.width != 2.dpToPx()) {
            LogUtils.d(TAG, "ensureVerticalLineLayout: apply/mutate width=2dp oldW=${lp?.width} oldH=${lp?.height}")
            verticalLine.layoutParams = LayoutParams(2.dpToPx(), lp?.height ?: 0)
        } else if (lp.width != 2.dpToPx()) {
            lp.width = 2.dpToPx()
            LogUtils.d(TAG, "ensureVerticalLineLayout: mutate existing lp.width=2dp")
            verticalLine.layoutParams = lp
        } else {
            LogUtils.d(TAG, "ensureVerticalLineLayout: width unchanged -> no setLayoutParams")
        }
        LogUtils.d(TAG, "ensureVerticalLineLayout done w=${verticalLine.layoutParams?.width} h=${verticalLine.layoutParams?.height}")
    }

    private fun updateContainerCorner(roundLeft: Boolean, roundRight: Boolean) {
        LogUtils.d(TAG, "updateContainerCorner(roundLeft=$roundLeft, roundRight=$roundRight)")
        val r = 4.dpToPx().toFloat()
        val zero = 0f
        // 顺序：top-left, top-right, bottom-right, bottom-left（每个角 x,y 成对）
        val radii = floatArrayOf(
            if (roundLeft) r else zero, if (roundLeft) r else zero,            // top-left
            if (roundRight) r else zero, if (roundRight) r else zero,          // top-right
            if (roundRight) r else zero, if (roundRight) r else zero,          // bottom-right
            if (roundLeft) r else zero, if (roundLeft) r else zero             // bottom-left
        )
        val old = containerBg.cornerRadii
        containerBg.cornerRadii = radii
        // 重新设置背景（不改变对象引用，仅为刷新）
        currentPriceContainer.background = containerBg
        LogUtils.d(TAG, "updateContainerCorner applied (old=${old?.joinToString()} new=${radii.joinToString()})")
    }

    private fun measureContainerWidth(): Int {
        // 以父宽度为上限 AT_MOST 重新测量，尽量尊重 XML 的 padding/margin
        // 同时强制将容器和其首个子 View 的宽度改为 WRAP_CONTENT，避免 XML 使用了 match_parent 导致占满
        currentPriceContainer.layoutParams?.let { lp ->
            if (lp.width != LayoutParams.WRAP_CONTENT) {
                lp.width = LayoutParams.WRAP_CONTENT
                currentPriceContainer.layoutParams = lp
            }
        }
        val child0 = currentPriceContainer.getChildAt(0)
        child0?.layoutParams?.let { clp ->
            if (clp.width != LayoutParams.WRAP_CONTENT) {
                clp.width = LayoutParams.WRAP_CONTENT
                child0.layoutParams = clp
            }
        }
        val parentLimit = width.coerceAtLeast(0)
        val wSpec = if (parentLimit > 0)
            MeasureSpec.makeMeasureSpec(parentLimit, MeasureSpec.AT_MOST)
        else
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val hSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        currentPriceContainer.measure(wSpec, hSpec)
        val mw = currentPriceContainer.measuredWidth
        val mlp = currentPriceContainer.layoutParams
        val marginExtra = (mlp as? ViewGroup.MarginLayoutParams)?.let { it.leftMargin + it.rightMargin } ?: 0
        return mw + marginExtra
    }

    private fun scrollToNearestPriceOption() {
        LogUtils.d(TAG, "scrollToNearestPriceOption()")
        val nearest = findNearestPriceIndex()
        if (nearest in dataSource.indices) {
            val scroller = object : LinearSmoothScroller(context) {
                override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
            scroller.targetPosition = nearest
            collectionView.layoutManager?.startSmoothScroll(scroller)
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            LogUtils.d(TAG, "scrollToNearestPriceOption smoothScroll to $nearest + haptic")
        }
    }

    private fun createCollectionView(): RecyclerView {
        val rv = RecyclerView(context)
        return configureRecyclerViewCommon(
            recyclerView = rv,
            layoutManager = flowLayout,
            adapter = this@TradeOptionRealPriceScrollView.adapter,
            onScrolled = { updateCurrentPricePosition() },
            onIdle = { centerIdx ->
                scrollToNearestOption()
                updateMaskSizeForCenterCell()
                onAfterScroll()
                if (centerIdx != RecyclerView.NO_POSITION) {
                    // 震动：若刚刚触发了贴边，则在贴边完成后的这次 IDLE 震动；否则在 index 变化时震动
                    if (pendingHapticAfterSnap) {
                        if (lastFeedbackIndex != centerIdx) {
                            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            lastFeedbackIndex = centerIdx
                        }
                        pendingHapticAfterSnap = false
                    } else if (lastFeedbackIndex != centerIdx) {
                        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        lastFeedbackIndex = centerIdx
                    }
                    notifyIfIndexChanged(centerIdx)
                } else {
                    pendingHapticAfterSnap = false
                }
            }
        )
    }

    private fun createMaskView(): com.lb.LBGenericMaskView {
        return com.lb.LBGenericMaskView(context).apply {
            clearRectSize = android.util.Size(kCellWidthNormal.dpToPx(), kCellHeight.dpToPx())
        }
    }

    private fun createAdapter(): TradeOptionPriceAdapter {
        return TradeOptionPriceAdapter(
            dataSource as MutableList<StrikePriceInfo>,
            object : OnExpirePriceClickListener {
                override fun onExpirePriceClick(position: Int, priceInfo: StrikePriceInfo) {
                    collectionView.smoothScrollToPosition(position)
                    didSelectItemAtIndex?.invoke(position)
                }
            }
        )
    }

    private fun scrollToInitialPosition() {
        var targetIndex = initialScrollToIndex
        if (targetIndex < 0) targetIndex = 0
        else if (targetIndex >= dataSource.size) targetIndex = dataSource.size - 1
        if (targetIndex in 0 until dataSource.size) {
            val smoothScroller = CenterSmoothScroller(context)
            smoothScroller.targetPosition = targetIndex
            flowLayout.startSmoothScroll(smoothScroller)
            post { updateCurrentPricePosition() }
        }
    }

    private fun findCenterCell(): View? =
        OptionScrollUtils.findCenterChild(collectionView, width)

    private fun scrollToNearestOption() {
        val centerCell = findCenterCell() ?: return
        val centerIndexPath = collectionView.getChildAdapterPosition(centerCell)
        if (centerIndexPath >= 0) {
            val screenCenterX = width / 2f
            val lm = collectionView.layoutManager
            val cellCenter = if (lm != null) {
                (lm.getDecoratedLeft(centerCell) + lm.getDecoratedRight(centerCell)) / 2f
            } else {
                centerCell.left + centerCell.width / 2f
            }
            val delta = screenCenterX - cellCenter
            val smoothScroller = CenterSmoothScroller(context)
            smoothScroller.targetPosition = centerIndexPath
            if (kotlin.math.abs(delta) > 0.5f) {
                pendingHapticAfterSnap = true
                flowLayout.startSmoothScroll(smoothScroller)
            } else {
                pendingHapticAfterSnap = false
            }
        }
    }

    private fun updateMaskSizeForCenterCell() {
        val centerCell = findCenterCell() ?: return
        val measuredWidth = centerCell.width.takeIf { it > 0 } ?: return
        val screenCenterX = width / 2f
        overlayMaskView.clearRectCenterX = screenCenterX
        if (overlayMaskView.clearRectSize.width != measuredWidth) {
            overlayMaskView.clearRectSize =
                android.util.Size(measuredWidth, kCellHeight.dpToPx())
        } else {
            overlayMaskView.invalidate()
        }
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()
    private fun Float.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()

    // 自定义 ArrowView 已移除，改为使用 PNG 图片的 ImageView

    private inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            // 使目标项中心对齐到容器中心
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }
    }

    private fun setCurrentPriceLabelText() {
        // todo 多语言
        currentPriceLabel.text = String.format("现价 %.2f", currentPrice)

    }
}
 
private class RealOptionPriceLinearLayoutManager(context: Context) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    companion object {
        private const val kCellHeight = 80.0f
    }
    private val density: Float = context.resources.displayMetrics.density
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            (kCellHeight * density).roundToInt()
        )
    }
    override fun canScrollHorizontally(): Boolean = true
    override fun canScrollVertically(): Boolean = false
}