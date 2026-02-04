package com.lb.demo

import android.content.res.Resources
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lb.demo.LBGenericScrollViewCell
import com.lb.LBGenericScrollViewCellModel
import kotlin.math.roundToInt

/**
 * 现价适配器
 */
class LBGenericAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<LBGenericScrollViewCell>() {

    companion object {
        private const val kCellHeight = 80.0f
        private const val kCellWidthNormal = 100.0f
        private const val kCellWidthSameYear = 80.0f

    }

    private var dataSource: List<LBGenericScrollViewCellModel> = emptyList()
    private var selectedPosition: Int = -1

    fun updateDataSource(newData: List<LBGenericScrollViewCellModel>) {
        Log.d(
            "LB_ADAPTER",
            "updateDataSource: size=${newData.size} caller=\n${Log.getStackTraceString(Throwable())}"
        )
        dataSource = newData
        notifyDataSetChanged()
    }

    fun updateSelectedPosition(position: Int) {
        val old = selectedPosition
//        Log.d("LB_ADAPTER", "updateSelectedPosition: $old -> $position (notifyItemChanged old/new)\ncaller=\n${Log.getStackTraceString(Throwable())}")
        selectedPosition = position
        if (old >= 0) notifyItemChanged(old)
        if (position >= 0) notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LBGenericScrollViewCell {
        Log.d("LB_ADAPTER", "onCreateViewHolder viewType=$viewType")
        val width = if (viewType == 1) kCellWidthSameYear.dpToPx() else kCellWidthNormal.dpToPx()
        val height = kCellHeight.dpToPx()
        val holder = LBGenericScrollViewCell(parent.context)
        val lp = RecyclerView.LayoutParams(width, height).apply {
            leftMargin = 4.dpToPx()
            rightMargin = 4.dpToPx()
        }
        holder.itemView.layoutParams = lp
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) onItemClick(position)
        }
        return holder
    }

    override fun onBindViewHolder(holder: LBGenericScrollViewCell, position: Int) {
        if (position < dataSource.size) {
            val m = dataSource[position]
            holder.model = m
            Log.d(
                "LB_ADAPTER",
                "bind position=$position selectedPos=$selectedPosition model.normal=${m.normalAttributedText} model.selected=${m.selectedAttributedText} price=${m.price}"
            )
            holder.setSelected(position == selectedPosition)
        }
    }

    override fun getItemCount(): Int {
        val size = dataSource.size
        Log.d("LB_ADAPTER", "getItemCount: $size")
        return size
    }

    override fun getItemViewType(position: Int): Int {
        val type = if (position < dataSource.size) {
            if (dataSource[position].isSameYear) 1 else 0
        } else 0
        Log.d("LB_ADAPTER", "getItemViewType: position=$position -> $type")
        return type
    }

    override fun onViewAttachedToWindow(holder: LBGenericScrollViewCell) {
        super.onViewAttachedToWindow(holder)
        Log.d("LB_ADAPTER", "onViewAttachedToWindow pos=${holder.adapterPosition}")
    }

    override fun onViewDetachedFromWindow(holder: LBGenericScrollViewCell) {
        super.onViewDetachedFromWindow(holder)
        Log.d("LB_ADAPTER", "onViewDetachedFromWindow pos=${holder.adapterPosition}")
    }

    override fun onViewRecycled(holder: LBGenericScrollViewCell) {
        super.onViewRecycled(holder)
        Log.d("LB_ADAPTER", "onViewRecycled")
    }

    override fun onFailedToRecycleView(holder: LBGenericScrollViewCell): Boolean {
        Log.d("LB_ADAPTER", "onFailedToRecycleView")
        return super.onFailedToRecycleView(holder)
    }

    private fun Float.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }
}