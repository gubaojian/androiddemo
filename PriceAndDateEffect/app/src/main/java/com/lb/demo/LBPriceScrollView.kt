package com.lb.demo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.lb.price.one.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 价格滚动视图（继承通用滚动视图）
 */
class LBPriceScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LBGenericScrollView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "LBPriceScrollView_"
    }

    // 标签高度自适应：优先用容器的实测高度；未测量时退回默认 26dp
    private val defaultLabelHeightPx: Int get() = 26.dpToPx()
    private val labelHeightPx: Int
        get() = currentPriceContainer.measuredHeight.takeIf { it > 0 } ?: defaultLabelHeightPx
    // 竖线底部额外延伸高度
    private val extraLineBottomPx: Int get() = 10.dpToPx()
    // 整体气泡（容器+竖线）相对 collectionView 顶部的下移偏移量（正数=向下）
    private val bubbleYOffsetPx: Int get() = 8.dpToPx()

    var currentPrice: Float = 0.0f
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
            cornerRadius = 8.dpToPx().toFloat()
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
        Log.d(TAG, "init: clipToPadding=$clipToPadding clipChildren=$clipChildren")
        addView(verticalLine)
        addView(currentPriceContainer)
        currentPriceContainer.setOnClickListener { scrollToNearestPriceOption() }
        // 在滚动过程中实时更新竖线与悬浮块位置，确保贴边后继续滑动时 X 也能跟随
        collectionView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d(TAG, "onScrolled dx=$dx dy=$dy -> updateCurrentPricePosition()")
                updateCurrentPricePosition()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                Log.d(TAG, "onScrollStateChanged state=$newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    post { updateCurrentPricePosition() }
                }
            }
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d(TAG, "onLayout(changed=$changed,l=$left,t=$top,r=$right,b=$bottom)")
        super.onLayout(changed, left, top, right, bottom)
        post { updateCurrentPricePosition() }
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d(TAG, "onSizeChanged(w=$w,h=$h,oldw=$oldw,oldh=$oldh)")
        super.onSizeChanged(w, h, oldw, oldh)
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
    override fun onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow()")
        super.onAttachedToWindow()
    }
    override fun onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow()")
        super.onDetachedFromWindow()
    }

    override fun onAfterScroll() {
        // 仅在滚动状态改变后更新，避免在 RecyclerView 布局过程中频繁 requestLayout 造成闪烁
        post { updateCurrentPricePosition() }
    }

    private fun updateCurrentPriceUI() {
        Log.d(TAG, "updateCurrentPriceUI()")
        currentPriceLabel.text = String.format("现价 %.2f", currentPrice)
        updateCurrentPricePosition()
    }

    private fun findNearestPriceIndex(): Int {
        Log.d(TAG, "findNearestPriceIndex()")
        if (dataSource.isEmpty()) return 0
        var minDiff = Float.MAX_VALUE
        var nearestIndex = 0
        for (i in dataSource.indices) {
            val diff = abs(dataSource[i].price - currentPrice)
            if (diff < minDiff) {
                minDiff = diff
                nearestIndex = i
            }
        }
        Log.d(TAG, "findNearestPriceIndex result=$nearestIndex")
        return nearestIndex
    }

    private fun isIndexVisible(index: Int): Boolean {
        Log.d(TAG, "isIndexVisible(index=$index)")
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
        Log.d(TAG, "isIndexVisible: left=$left right=$right visibleStart=$visibleStart visibleEnd=$visibleEnd -> $visible")
        return visible
    }

    private fun updateCurrentPricePosition() {
        Log.d(TAG, "updateCurrentPricePosition()")
        var lowerIndex = -1
        var upperIndex = -1
        for (i in dataSource.indices) {
            val price = dataSource[i].price
            if (price <= currentPrice) lowerIndex = i
            if (price >= currentPrice && upperIndex == -1) {
                upperIndex = i
                break
            }
        }
        if (lowerIndex == -1) lowerIndex = 0
        if (upperIndex == -1) upperIndex = dataSource.size - 1
        Log.d(TAG, "updateCurrentPricePosition: lower=$lowerIndex upper=$upperIndex")

        val lowerVisible = isIndexVisible(lowerIndex)
        val upperVisible = isIndexVisible(upperIndex)
        Log.d(TAG, "updateCurrentPricePosition: lowerVisible=$lowerVisible upperVisible=$upperVisible")
        if (lowerVisible && upperVisible) {
            layoutFloatingLabel(lowerIndex, upperIndex)
        } else {
            layoutBoundaryLabel()
        }
    }

    private fun layoutFloatingLabel(lowerIndex: Int, upperIndex: Int) {
        Log.d(TAG, "layoutFloatingLabel(lower=$lowerIndex, upper=$upperIndex)")
        val lm = collectionView.layoutManager
        val lowerView = lm?.findViewByPosition(lowerIndex)
        val upperView = lm?.findViewByPosition(upperIndex)
        if (lowerView == null || upperView == null) return

        // 使用相邻两个 item 的边界（lower 的右边缘与 upper 的左边缘的中点）
        val boundaryX = (lowerView.right + upperView.left) / 2f
        val floatingX = boundaryX
        Log.d(TAG, "layoutFloatingLabel: boundaryX=$boundaryX")

        ensureContainerHeight()
        var labelY = collectionView.top - labelHeightPx - 10.dpToPx() + bubbleYOffsetPx
        // 防止悬浮块顶部被父容器裁剪：向下限位一个最小安全边距
        labelY = max(2.dpToPx(), labelY)

        val text = String.format("现价 %.2f", currentPrice)
        val textWidth = currentPriceLabel.paint.measureText(text)
        val desiredWidth = (textWidth + 10.dpToPx() + 10.dpToPx()).roundToInt()
        val containerWidth = desiredWidth
        setContainerWidth(containerWidth)

        var containerX = floatingX - containerWidth / 2f
        val minX = 0f
        val maxX = width - containerWidth.toFloat()
        containerX = containerX.coerceIn(minX, maxX)
        Log.d(TAG, "layoutFloatingLabel: containerWidth=$containerWidth containerX=$containerX labelY=$labelY")

        currentPriceContainer.visibility = VISIBLE
        verticalLine.visibility = VISIBLE
        ensureVerticalLineLayout()
        leftArrowView.visibility = GONE
        rightArrowView.visibility = GONE
        // 浮动模式下文本左右各10dp内边距，避免与边界贴合
        val padL = 10.dpToPx()
        val padR = 10.dpToPx()
        Log.d(TAG, "layoutFloatingLabel: setTextPadding L=$padL T=0 R=$padR B=0 (old=${currentPriceLabel.paddingLeft},${currentPriceLabel.paddingTop},${currentPriceLabel.paddingRight},${currentPriceLabel.paddingBottom})")
        currentPriceLabel.setPadding(padL, 0, padR, 0)
        // 浮动模式：左右两侧都使用圆角
        updateContainerCorner(roundLeft = true, roundRight = true)

        // 更新文本
        currentPriceLabel.text = String.format("现价 %.2f", currentPrice)

        // 使用平移避免频繁 requestLayout
        Log.d(TAG, "layoutFloatingLabel: container translate from x=${currentPriceContainer.translationX}, y=${currentPriceContainer.translationY} -> x=$containerX y=$labelY")
        currentPriceContainer.translationX = containerX
        currentPriceContainer.translationY = labelY.toFloat()

        val lineStartY = labelY + labelHeightPx
        // 竖线高度为 cell 高度 + 额外 6dp（向下延伸）
        val cellHeight = lowerView.height.takeIf { it > 0 } ?: labelHeightPx
        val desiredHeight = cellHeight + extraLineBottomPx
        val params = verticalLine.layoutParams ?: LayoutParams(2.dpToPx(), desiredHeight)
        Log.d(TAG, "layoutFloatingLabel: ensure verticalLine params oldW=${params.width} oldH=${params.height} desiredH=$desiredHeight")
        if (params.height != desiredHeight) {
            params.height = desiredHeight
            verticalLine.layoutParams = params
            Log.d(TAG, "layoutFloatingLabel: verticalLine setLayoutParams(h=$desiredHeight)")
        } else {
            Log.d(TAG, "layoutFloatingLabel: verticalLine height unchanged -> skip setLayoutParams")
        }
        verticalLine.translationX = (floatingX - (params.width / 2f)).toFloat()
        verticalLine.translationY = lineStartY.toFloat()
        Log.d(TAG, "layoutFloatingLabel: verticalLine x=${verticalLine.translationX} y=${verticalLine.translationY} h=${params.height}")
    }

    private fun layoutBoundaryLabel() {
        Log.d(TAG, "layoutBoundaryLabel()")
        val centerView = findCenterCell() ?: return
        val centerPos = collectionView.getChildAdapterPosition(centerView)
        if (centerPos !in dataSource.indices) return
        val centerPrice = dataSource[centerPos].price

        ensureContainerHeight()
        var labelY = collectionView.top - labelHeightPx - 10.dpToPx() + bubbleYOffsetPx
        // 防止悬浮块顶部被父容器裁剪：向下限位一个最小安全边距
        labelY = max(2.dpToPx(), labelY)

        verticalLine.visibility = GONE
        currentPriceContainer.visibility = VISIBLE

        currentPriceLabel.text = String.format("现价 %.2f", currentPrice)

        if (currentPrice < centerPrice) {
            // 显示在左边界，左箭头（按设计稿）
            leftArrowView.visibility = VISIBLE
            rightArrowView.visibility = GONE
            currentPriceContainer.translationX = 0f
            currentPriceContainer.translationY = labelY.toFloat()
            // 左贴边：左侧直角、右侧圆角
            updateContainerCorner(roundLeft = false, roundRight = true)
            // 计算容器宽度（文本 + 端帽宽度=高度，用于水平居中的箭头区域）
            val textWidth = currentPriceLabel.paint.measureText(currentPriceLabel.text.toString())
            val endCapWidth = labelHeightPx
            val desiredWidth = (textWidth + endCapWidth + 10.dpToPx()).roundToInt()
            val containerWidth = desiredWidth
            setContainerWidth(containerWidth)
            // 左箭头尺寸与位置由布局控制（RelativeLayout 规则），此处不再动态修改 LayoutParams，避免类型不匹配
            // 文本留出左端帽宽度
            val padLeft = endCapWidth
            val padRight = 10.dpToPx()
            Log.d(TAG, "layoutBoundaryLabel LEFT: setTextPadding L=$padLeft T=0 R=$padRight B=0 (old=${currentPriceLabel.paddingLeft},${currentPriceLabel.paddingTop},${currentPriceLabel.paddingRight},${currentPriceLabel.paddingBottom})")
            currentPriceLabel.setPadding(padLeft, 0, padRight, 0)
            Log.d(TAG, "layoutBoundaryLabel: LEFT edge containerWidth=$containerWidth labelY=$labelY")
        } else {
            // 显示在右边界，右箭头（按设计稿）
            rightArrowView.visibility = VISIBLE
            leftArrowView.visibility = GONE
            // 右贴边：右侧直角、左侧圆角
            updateContainerCorner(roundLeft = true, roundRight = false)
            // 计算容器宽度（文本 + 端帽宽度=高度，用于水平居中的箭头区域）
            val textWidth = currentPriceLabel.paint.measureText(currentPriceLabel.text.toString())
            val endCapWidth = labelHeightPx
            val desiredWidth = (textWidth + 10.dpToPx() + endCapWidth).roundToInt()
            val containerWidth = desiredWidth
            setContainerWidth(containerWidth)
            Log.d(TAG, "layoutBoundaryLabel RIGHT: container translate from x=${currentPriceContainer.translationX}, y=${currentPriceContainer.translationY} -> x=${(width - containerWidth)} y=$labelY")
            currentPriceContainer.translationX = (width - containerWidth).toFloat()
            currentPriceContainer.translationY = labelY.toFloat()
            // 右箭头尺寸与位置由布局控制（RelativeLayout 规则），此处不再动态修改 LayoutParams，避免类型不匹配
            // 文本右侧留出端帽宽度
            val padLeft = 4.dpToPx()
            val padRight = endCapWidth
            Log.d(TAG, "layoutBoundaryLabel RIGHT: setTextPadding L=$padLeft T=0 R=$padRight B=0 (old=${currentPriceLabel.paddingLeft},${currentPriceLabel.paddingTop},${currentPriceLabel.paddingRight},${currentPriceLabel.paddingBottom})")
            currentPriceLabel.setPadding(padLeft, 0, padRight, 0)
            Log.d(TAG, "layoutBoundaryLabel: RIGHT edge containerWidth=$containerWidth labelY=$labelY")
        }
    }

    private fun ensureContainerHeight() {
        Log.d(TAG, "ensureContainerHeight()")
        val lp = currentPriceContainer.layoutParams
        if (lp == null) {
            Log.d(TAG, "ensureContainerHeight: set WRAP_CONTENT (new lp)")
            currentPriceContainer.layoutParams = LayoutParams(1, LayoutParams.WRAP_CONTENT)
        } else if (lp.height != LayoutParams.WRAP_CONTENT) {
            lp.height = LayoutParams.WRAP_CONTENT
            currentPriceContainer.layoutParams = lp
            Log.d(TAG, "ensureContainerHeight: set WRAP_CONTENT (mutate)")
        } else Log.d(TAG, "ensureContainerHeight: already WRAP_CONTENT")
    }

    private fun setContainerWidth(widthPx: Int) {
        Log.d(TAG, "setContainerWidth($widthPx) old=${currentPriceContainer.layoutParams?.width}")
        val lp = currentPriceContainer.layoutParams
        if (lp == null) {
            Log.d(TAG, "setContainerWidth: apply new LayoutParams width=$widthPx height=WRAP_CONTENT")
            currentPriceContainer.layoutParams = LayoutParams(widthPx, LayoutParams.WRAP_CONTENT)
        } else if (lp.width != widthPx) {
            lp.width = widthPx
            Log.d(TAG, "setContainerWidth: mutate existing lp.width=$widthPx")
            currentPriceContainer.layoutParams = lp
        } else {
            Log.d(TAG, "setContainerWidth: width unchanged -> no setLayoutParams")
        }
        Log.d(TAG, "setContainerWidth done new=${currentPriceContainer.layoutParams?.width}\ncaller=\n${Log.getStackTraceString(Throwable())}")
    }

    private fun ensureVerticalLineLayout() {
        Log.d(TAG, "ensureVerticalLineLayout()")
        val lp = verticalLine.layoutParams
        if (lp == null || lp.width != 2.dpToPx()) {
            Log.d(TAG, "ensureVerticalLineLayout: apply/mutate width=2dp oldW=${lp?.width} oldH=${lp?.height}")
            verticalLine.layoutParams = LayoutParams(2.dpToPx(), lp?.height ?: 0)
        } else if (lp.width != 2.dpToPx()) {
            lp.width = 2.dpToPx()
            Log.d(TAG, "ensureVerticalLineLayout: mutate existing lp.width=2dp")
            verticalLine.layoutParams = lp
        } else {
            Log.d(TAG, "ensureVerticalLineLayout: width unchanged -> no setLayoutParams")
        }
        Log.d(TAG, "ensureVerticalLineLayout done w=${verticalLine.layoutParams?.width} h=${verticalLine.layoutParams?.height}")
    }

    private fun updateContainerCorner(roundLeft: Boolean, roundRight: Boolean) {
        Log.d(TAG, "updateContainerCorner(roundLeft=$roundLeft, roundRight=$roundRight)")
        val r = 8.dpToPx().toFloat()
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
        Log.d(TAG, "updateContainerCorner applied (old=${old?.joinToString()} new=${radii.joinToString()})")
    }

    private fun scrollToNearestPriceOption() {
        Log.d(TAG, "scrollToNearestPriceOption()")
        val nearest = findNearestPriceIndex()
        if (nearest in dataSource.indices) {
            val scroller = object : LinearSmoothScroller(context) {
                override fun getHorizontalSnapPreference(): Int = SNAP_TO_START
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
            scroller.targetPosition = nearest
            collectionView.layoutManager?.startSmoothScroll(scroller)
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            Log.d(TAG, "scrollToNearestPriceOption smoothScroll to $nearest + haptic")
        }
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()

    // 自定义 ArrowView 已移除，改为使用 PNG 图片的 ImageView
}