package com.zhongpin.mvvm_android.ui.order.purchaselist.pay

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListPreviewOrderPurchaseItemBinding
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.PreviewPurchaseOrderItem
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor

class PayPurchaseOrderDetailItemListAdapter(val mActivity: PayPurchaseOrderDetailActivity, data: MutableList<OrderItem>)
    : BaseQuickAdapter<OrderItem, PayPurchaseOrderDetailItemListAdapter.VH>(data) {


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListPreviewOrderPurchaseItemBinding = ListPreviewOrderPurchaseItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: OrderItem?
    ) {
        item?.let {
            holder.binding.apply {
                waBg.setWaBg(item.platCode)
                name.setWaTextColor(item.platCode)
                fluteText.setWaTextColor(item.platCode)

                name.setWaText(item.platCode)

                fluteText.text = "${item.lenType}瓦"
                if (item.totalPrice != null) {
                    SpanUtils.with(price)
                        .append("￥")
                        .setFontSize(12, true)
                        .append(item.totalPrice.toString())
                        .setFontSize(16, true)
                        .create()
                } else {
                    SpanUtils.with(price)
                        .append("￥")
                        .setFontSize(12, true)
                        .append("0")
                        .setFontSize(16, true)
                        .create()
                }
                paperSize.text = PingDanAppUtils.toPaperSizeWithUnitMM(item.size ?: "")
                purchaseAmount.text = "${item.num}张"
            }
        }
    }

}