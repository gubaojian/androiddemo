package com.zhongpin.mvvm_android.ui.order.confirmreceipt

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.github.vipulasri.timelineview.TimelineView
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListConfirmReceiptOrderDeliveryProfItemBinding
import com.zhilianshidai.pindan.app.databinding.ListConfirmReceiptOrderDeliveryProfItemTimelineBinding
import com.zhilianshidai.pindan.app.databinding.ListPreviewOrderPurchaseItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.setBackgroundDrawableColor
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofItem
import com.zhongpin.mvvm_android.bean.PreviewPurchaseOrderItem
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor

class ConfirmReceiptItemListTimelineAdapter(val mActivity: ConfirmReceiptOrderActivity, data: MutableList<OrderDeliveryProofItem>)
    : BaseQuickAdapter<OrderDeliveryProofItem, ConfirmReceiptItemListTimelineAdapter.VH>(data) {

    class VH(
        parent: ViewGroup,
        private val viewType: Int,
        val binding: ListConfirmReceiptOrderDeliveryProfItemTimelineBinding = ListConfirmReceiptOrderDeliveryProfItemTimelineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.timeline.initLine(viewType)
        }
    }


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ConfirmReceiptItemListTimelineAdapter.VH {
        return VH(parent, viewType = viewType)
    }

    override fun getItemViewType(position: Int, list: List<OrderDeliveryProofItem>): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }


    override fun onBindViewHolder(
        holder: ConfirmReceiptItemListTimelineAdapter.VH,
        position: Int,
        item: OrderDeliveryProofItem?
    ) {
        item?.let {
            holder.binding.apply {
                if (item.bizType == 2) {
                    deliveryInfo.gone()
                    timeline.setMarker(ResourcesCompat.getDrawable(context.resources, R.mipmap.delivery_prof_sign, null))
                } else {
                    timeline.setMarker(ResourcesCompat.getDrawable(context.resources, R.mipmap.delivery_prof_car, null))
                    deliveryInfo.visible()
                }
                var itemTime =  item.createTime ?: item.updateTime ?: "";
                if (item.bizType == 2) {
                    deliveryStatusText.text = "${item.creatorName} 确认收货"
                    itemTime =  item.signTime ?: item.createTime ?: item.updateTime ?: "";
                } else {
                    if (item.type == 1) {
                        deliveryStatusText.text = item.typeName ?: "部分送达"
                    } else {
                        deliveryStatusText.text = item.typeName ?: "全部送达"
                    }
                    itemTime =  item.createTime ?: item.updateTime ?: "";
                }


                if (item.signStatus == 1) {
                    signStatus.text = item.signStatusName ?: "已签收"
                    signStatus.setBackgroundDrawableColor("#57C248")
                } else {
                    signStatus.text = item.signStatusName ?: "未签收"
                    signStatus.setBackgroundDrawableColor("#FFA826")
                }

                if (item.stockStatus == 1) {
                    stockStatus.text = item.stockStatusName ?: "已入库"
                    stockStatus.setBackgroundDrawableColor("#57C248")
                } else {
                    stockStatus.text = item.stockStatusName ?: "未入库"
                    stockStatus.setBackgroundDrawableColor("#FFA826")
                }


                deliveryDate.text  = itemTime
                deliveryAmount.text = (item.num ?: 0).toString()
                deliveryPhotos.setImageUrls(item.imageList ?: emptyList());

                 /**
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
                purchaseAmount.text = "${item.num}张" */
            }
        }
    }

}