package com.zhongpin.mvvm_android.ui.pay.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListPayBillOrderItemBinding
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.ui.view.ext.setLineTypeText
import com.zhongpin.mvvm_android.ui.view.ext.setPaperSizeText

class PayBillOrderListAdapter(val mActivity: PayBillDetailActivity, data: MutableList<OrderItem>)
    : BaseQuickAdapter<OrderItem, PayBillOrderListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: PayBillOrderListAdapter.VH,
        position: Int,
        item: OrderItem?
    ) {
        item?.let {
            holder.binding.apply {
                materialCode.text = item.platCode
                waCode.text = item.lenType
                SpanUtils.with( moneyAmount)
                    .append("￥")
                    .setFontSize(14, true)
                    .append(item.totalPrice ?: "")
                    .setFontSize(16, true)
                    .create()
                buyAmount.text = (item.num ?: 0).toString()
                paperSize.setPaperSizeText(item)
                lineDesc.setLineTypeText(item)
            }

        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): PayBillOrderListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListPayBillOrderItemBinding = ListPayBillOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}