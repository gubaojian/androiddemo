package com.zhongpin.mvvm_android.ui.pay

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListPayRecordItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.invisible
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.PayItem

class PayItemListAdapter(val mActivity: PayAccountDetailActivity, data: MutableList<PayItem>)
    : BaseQuickAdapter<PayItem, PayItemListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: PayItemListAdapter.VH,
        position: Int,
        item: PayItem?
    ) {
        val itemCount = getItemCount()
        item?.let {
            holder.binding.apply {
                if (itemCount == 1) {
                    payRecordItem.setBackgroundResource(R.drawable.bg_pay_record_item_all_corner)
                    bottomLine.invisible()
                } else if (position == 0) {
                    payRecordItem.setBackgroundResource(R.drawable.bg_pay_record_item_top_corner)
                    bottomLine.visible()
                } else if (position == (itemCount - 1)) {
                    payRecordItem.setBackgroundResource(R.drawable.bg_pay_record_item_bottom_corner)
                    bottomLine.invisible()
                } else {
                    payRecordItem.setBackgroundResource(R.drawable.bg_pay_record_item_middle)
                    bottomLine.visible()
                }

                payDesc.text = item.describe ?: "收支明细"

                if (item.receiveType == 0) {
                    payAmount.text = "+${item.amount}";
                } else if(item.receiveType == 1) {
                    payAmount.text = "-${item.amount}";
                } else {
                    payAmount.text = "${item.amount}";
                }

                if (item.status == 0) {
                    statusText.text = "待支付"
                    statusText.setTextColor("#ffa826".toColorInt())
                } else if (item.status == 1) {
                    statusText.text = "支付超时"
                    statusText.setTextColor("#D83333".toColorInt())
                } else if (item.status == 2) {
                    statusText.text = "取消支付"
                    statusText.setTextColor("#D83333".toColorInt())
                } else if (item.status == 3) {
                    statusText.text = item.payStatusDesc()
                    statusText.setTextColor("#333333".toColorInt())
                } else {
                    statusText.text = "支付状态"
                    statusText.setTextColor("#333333".toColorInt())
                }

                recordDate.text = item.createTime ?: ""

            }

        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListPayRecordItemBinding = ListPayRecordItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}