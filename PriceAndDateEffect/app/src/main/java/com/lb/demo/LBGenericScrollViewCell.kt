package com.lb.demo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lb.LBGenericScrollViewCellModel
import com.lb.price.one.R
import kotlin.math.roundToInt

/**
 * 通用滚动视图的 Cell
 */
class LBGenericScrollViewCell(context: Context) : RecyclerView.ViewHolder(
    LayoutInflater.from(context).inflate(R.layout.item_price_lable, null, false)) {

    // 直接使用布局根视图作为根容器
    private val rootContainer: RelativeLayout = itemView as RelativeLayout

    private val contentLabel: TextView = rootContainer.findViewById(R.id.tv_price_label)
    private var currentScale: Float = 1.0f
    private var isSelectedState: Boolean = false


    var model: LBGenericScrollViewCellModel? = null
        set(value) {
            field = value
            updateContent()
        }

    fun setSelected(selected: Boolean) {
        if (this.isSelectedState == selected) return
        this.isSelectedState = selected
        val m = this.model ?: return
        val display = if (selected) {
            (m.selectedAttributedText ?: m.normalAttributedText ?: m.price.toString()).toString()
        } else {
            (m.normalAttributedText ?: m.selectedAttributedText ?: m.price.toString()).toString()
        }
        setPriceContent(display)

        if (currentScale > 0 && currentScale != 1.0f) {
            updateScaleRatio(currentScale)
        }
    }

    fun updateScaleRatio(scale: Float) {
        currentScale = scale
        val m = this.model ?: return
        val display = if (isSelectedState) {
            (m.selectedAttributedText ?: m.normalAttributedText ?: m.price.toString()).toString()
        } else {
            (m.normalAttributedText ?: m.selectedAttributedText ?: m.price.toString()).toString()
        }

        setPriceContent(display)
    }

    companion object {
        private const val TAG = "LBGenericScrollViewCell_"

    }

    init {
        Log.d(TAG, "ViewHolder created")
        itemView.addOnLayoutChangeListener { _, l, t, r, b, ol, ot, orr, ob ->
            Log.d(TAG, "onLayoutChange new=[$l,$t,$r,$b] old=[$ol,$ot,$orr,$ob]")
        }
    }

    private fun setPriceContent(display: String) {
        Log.d(TAG, "setPriceContent: ")
        Log.i(TAG, "       display: $display")
        contentLabel.text = display
    }

    private fun updateContent() {
        val m = this.model ?: return
        val display = if (isSelectedState) {
            (m.selectedAttributedText ?: m.normalAttributedText ?: m.price.toString()).toString()
        } else {
            (m.normalAttributedText ?: m.selectedAttributedText ?: m.price.toString()).toString()
        }
        setPriceContent(display)


        if (currentScale > 0 && currentScale != 1.0f) {
            updateScaleRatio(currentScale)
        }
    }

    private fun Int.dpToPx(): Int =
        (this * rootContainer.context.resources.displayMetrics.density).roundToInt()
}