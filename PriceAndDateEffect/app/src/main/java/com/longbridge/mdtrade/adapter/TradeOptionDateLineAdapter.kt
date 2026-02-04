package com.longbridge.mdtrade.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import click
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lb.ConvertDateLine
import com.lb.price.one.databinding.ItemTradeOptionDateLineBinding
import com.lb.util.LogUtils
import com.longbridge.common.global.entity.OptionChainDataListV4
import kotlin.math.roundToInt

/**
 * 期权-日期-适配器
 */
class TradeOptionDateLineAdapter(
    private val datas: MutableList<ConvertDateLine.OptionDate>, private val listener: OnExpireDateClickListener
) : BaseQuickAdapter<ConvertDateLine.OptionDate, TradeOptionDateLineAdapter.OptionDateLineViewHolder>(0, datas) {

    private var convertDateLine: ConvertDateLine = ConvertDateLine()

    companion object {
        private const val kCellHeight = 80.0f
        private const val kCellWidthNormal = 100.0f
        private const val kCellWidthSameYear = 80.0f
        private const val TAG = "OptionDateLineAdapter_"

    }

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionDateLineViewHolder {
        val binding = ItemTradeOptionDateLineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        val width = if (viewType == 1) kCellWidthSameYear.dpToPx() else kCellWidthNormal.dpToPx()
        val height = kCellHeight.dpToPx()
        val holder = OptionDateLineViewHolder(binding)
        val lp = RecyclerView.LayoutParams(width, height).apply {
            leftMargin = 4.dpToPx()
            rightMargin = 4.dpToPx()
        }
        holder.itemView.layoutParams = lp
        return holder
    }

    override fun convert(holder:OptionDateLineViewHolder, item: ConvertDateLine.OptionDate) {
        with(holder.binding){
            if (item.expireDate != null) {
                tvOptionDiffDay.text = item.expireDiffDate
                tvOptionDate.text = item.expireDateContent
                tvWeekLabel.text = getDateType(item.expireDate!!)
                val isSelect =
                    convertDateLine.getFetchChainDate() == item.expireDate!!.expire_date
                getRoot().isSelected = isSelect
                tvOptionDate.isSelected = isSelect
                tvOptionDiffDay.isSelected = isSelect
            }
            getRoot().click {
                item.expireDate?.let {
                    listener.onExpireDateClick(holder.bindingAdapterPosition, it)
                }
            }
        }
    }

   /*
   override fun getItemViewType(position: Int): Int {
        val type = if (position < datas.size) {
            if (datas[position].isSameYear) 1 else 0
        } else 0
        return type
    }
    */

    fun updateDataSource(newData: List<ConvertDateLine.OptionDate>) {
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

    private fun getDateType(expireDiffDate: OptionChainDataListV4.ExpireDate): String {
        return when {
            expireDiffDate.type.equals("w", ignoreCase = true) -> "W"
            expireDiffDate.type.equals("q", ignoreCase = true) -> "Q"
            else -> ""
        }
    }

    private fun Float.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    interface OnExpireDateClickListener {
        fun onExpireDateClick(position: Int,expireDate: OptionChainDataListV4.ExpireDate)
    }

    inner class OptionDateLineViewHolder(val binding: ItemTradeOptionDateLineBinding) :
        BaseViewHolder(binding.root)
}