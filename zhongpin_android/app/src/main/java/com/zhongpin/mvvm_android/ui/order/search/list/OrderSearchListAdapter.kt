package com.zhongpin.mvvm_android.ui.order.search.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListOrderSearchItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.ui.common.autoFeedback
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgain
import com.zhongpin.mvvm_android.ui.common.goConfirmReceiptOrderActivity
import com.zhongpin.mvvm_android.ui.common.showConfirmReceiptDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setConfirmOrderButtonShow
import com.zhongpin.mvvm_android.ui.view.ext.setFeedbackButtonShow
import com.zhongpin.mvvm_android.ui.view.ext.setLineTypeText
import com.zhongpin.mvvm_android.ui.view.ext.setOrderStatusText
import com.zhongpin.mvvm_android.ui.view.ext.setPaperSizeText
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor

class OrderSearchListAdapter(val mActivity: OrderSearchListActivity, data: MutableList<OrderItem>)
    : BaseQuickAdapter<OrderItem, OrderSearchListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: OrderSearchListAdapter.VH,
        position: Int,
        item: OrderItem?
    ) {
        item?.let {
            holder.binding.apply {
                holder.binding.apply {
                    waBg.setWaBg(item.platCode)
                    name.setWaTextColor(item.platCode)
                    fluteText.setWaTextColor(item.platCode)

                    name.setWaText(item.platCode)
                    fluteText.text = "${item.lenType}瓦"
                    SpanUtils.with(price)
                        .append("￥")
                        .setFontSize(12, true)
                        .append(item.totalPrice ?: "0.0")
                        .setFontSize(16, true)
                        .create()
                    paperSize.text = PingDanAppUtils.toPaperSizeWithUnitMM(item.size ?: "")
                    purchaseAmount.text = "${item.num}张"

                    buyDate.text = item.orderTime ?: ""
                    statusText.setOrderStatusText(item)

                    val waitSignNo = item.unSignNum ?: 0L
                    if (waitSignNo <= 0) {
                        waitSignContainer.gone()
                    } else {
                        waitSignContainer.visible()
                        waitSignNumText.text = "${item.unSignNum}件待签收";
                    }

                    if (item.orderType == 2) {
                        orderTypeIcon.setImageResource(R.mipmap.icon_buhuozhong)
                        orderTypeText.text = "补发订单"
                        orderTypeText.setTextColor("#57C248".toColorInt())
                    } else {
                        orderTypeIcon.setImageResource(R.mipmap.icon_zhibancaigou)
                        orderTypeText.text = "纸板订单"
                        orderTypeText.setTextColor("#557EF7".toColorInt())
                    }


                    buyAgain.setOnClickListener {
                        mActivity.goBuyOrderAgain(item)
                    }

                    feedback.setFeedbackButtonShow(item)
                    feedback.setOnClickListener {

                        mActivity.autoFeedback(item, {
                            mActivity.confirmReceiptOrder(item)
                        })
                    }
                    confirmOrder.setConfirmOrderButtonShow(item)
                    confirmOrder.setOnClickListener {
                        mActivity.goConfirmReceiptOrderActivity(item)
                    }
                }
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
        val binding: ListOrderSearchItemBinding = ListOrderSearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}