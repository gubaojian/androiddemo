package com.zhongpin.mvvm_android.ui.order.purchaselist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListPurchaseOrderListItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgain
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgainFromPurchaseOrder
import com.zhongpin.mvvm_android.ui.common.goPayPurchaseOrderActivity
import com.zhongpin.mvvm_android.ui.order.purchaselist.view.HorizontalWaItemListViewer
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

class PurchaseOrderListAdapter(val mActivity: PurchaseOrderListActivity, data: MutableList<PurchaseOrderDetail>)
    : BaseQuickAdapter<PurchaseOrderDetail, PurchaseOrderListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: PurchaseOrderListAdapter.VH,
        position: Int,
        item: PurchaseOrderDetail?
    ) {
        val itemCount = getItemCount()
        item?.let {
            holder.binding.apply {

                purchaseOrderItemRecyclerView.setOrderItems(item.orders ?: emptyList())

                purchaseOrderItemRecyclerView.setContentOnClickListener {
                    mActivity.goPayPurchaseOrderActivity(item.id)
                }
                statusText.text = item.statusDesc()
                val remainTime = PingDanAppUtils.getPurTimeRemain(item.purTime)
                if (item.status == 0 && remainTime > 0) {
                    statusText.gone()
                    waitPayStatusText.visible()
                    waitPayStatusText.setPrefix("待支付（")
                    waitPayStatusText.setRemainTime(remainTime, {
                        EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                    })
                    waitPayStatusText.setSuffix("）")
                } else {
                    statusText.visible()
                    waitPayStatusText.gone()
                }

                buyDate.text = item.purTime

                itemTotalOrderAmount.text = "共${item.orders?.size ?: 0}条"

                if (item.status == 0) {
                    cancelOrder.visible()
                    payOrder.visible()
                } else {
                    cancelOrder.gone()
                    payOrder.gone()
                }

                cancelOrder.setOnClickListener {
                    mActivity.onCancelPay(item)
                }

                buyAgain.setOnClickListener {
                    mActivity.goBuyOrderAgainFromPurchaseOrder(item)
                }

                payOrder.setOnClickListener {
                    mActivity.onConfirmPay(item)
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
        val binding: ListPurchaseOrderListItemBinding = ListPurchaseOrderListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}

