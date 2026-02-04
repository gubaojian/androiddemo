package com.longbridge.mdtrade.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import click
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lb.price.one.databinding.ItemTradeOptionPriceBinding
import com.longbridge.common.global.entity.StrikePriceInfo
import com.longbridge.mdtrade.adapter.TradeOptionPriceAdapter.PriceViewHolder
import kotlin.math.roundToInt

/**
 *
 * 期权-现价-适配器
 */
class TradeOptionPriceAdapter(
    private val datas: MutableList<StrikePriceInfo>,
    private val listener: OnExpirePriceClickListener
) : BaseQuickAdapter<StrikePriceInfo, PriceViewHolder>(0, datas) {

    companion object {
        private const val kCellHeight = 80.0f
        private const val kCellWidthNormal = 100.0f
        private const val kCellWidthSameYear = 80.0f
        private const val TAG = "OptionPriceAdapter_"
    }

    private var selectedPosition: Int = -1

    fun updateDataSource(newData: List<StrikePriceInfo>) {
        datas.clear()
        datas.addAll(newData)
        notifyDataSetChanged()
    }

    fun updateSelectedPosition(position: Int) {
        val old = selectedPosition
//        Log.d("LB_ADAPTER", "updateSelectedPosition: $old -> $position (notifyItemChanged old/new)\ncaller=\n${Log.getStackTraceString(Throwable())}")
        selectedPosition = position
        if (old >= 0) notifyItemChanged(old)
        if (position >= 0) notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceViewHolder {
        Log.d(TAG, "onCreateViewHolder viewType=$viewType")

        val binding =
            ItemTradeOptionPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val width = if (viewType == 1) kCellWidthSameYear.dpToPx() else kCellWidthNormal.dpToPx()
        val height = kCellHeight.dpToPx()

        val holder = PriceViewHolder(binding)

        val lp = RecyclerView.LayoutParams(width, height).apply {
            leftMargin = 4.dpToPx()
            rightMargin = 4.dpToPx()
        }
        holder.itemView.layoutParams = lp
        return holder
    }

    override fun convert(holder: PriceViewHolder, item: StrikePriceInfo) {
        with(holder.binding) {
            tvPriceLabel.text = item.price
            getRoot().click {
                listener.onExpirePriceClick(holder.bindingAdapterPosition, item)
            }
        }
    }

    override fun getItemCount(): Int {
        if (datas.isNullOrEmpty()) {
            return 0
        }
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        val type = if (position < datas.size) {
            if (datas[position].isSameYear) 1 else 0
        } else 0
        return type
    }

    private fun Float.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }


    interface OnExpirePriceClickListener {
        fun onExpirePriceClick(position: Int, priceInfo: StrikePriceInfo)
    }

    inner class PriceViewHolder(val binding: ItemTradeOptionPriceBinding) :
        BaseViewHolder(binding.root)
}