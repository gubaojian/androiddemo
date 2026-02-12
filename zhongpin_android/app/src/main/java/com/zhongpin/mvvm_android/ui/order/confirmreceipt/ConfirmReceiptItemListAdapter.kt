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

class ConfirmReceiptItemListAdapter(val mActivity: ConfirmReceiptOrderActivity, data: MutableList<OrderDeliveryProofItem>)
    : BaseQuickAdapter<OrderDeliveryProofItem, ConfirmReceiptItemListAdapter.VH>(data) {

    class VH(
        parent: ViewGroup,
        val binding: ListConfirmReceiptOrderDeliveryProfItemBinding = ListConfirmReceiptOrderDeliveryProfItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ConfirmReceiptItemListAdapter.VH {
        return VH(parent)
    }


    override fun onBindViewHolder(
        holder: ConfirmReceiptItemListAdapter.VH,
        position: Int,
        item: OrderDeliveryProofItem?
    ) {
        item?.let {
            holder.binding.apply {

                deliveryDoneStatus.gone()
                deliveryDoneStatus.gone()
                deliveryDate.text  = item.createTime ?: item.updateTime ?: ""
                deliveryAmount.text = (item.num ?: 0).toString()
                feedbackPhotos.setImageUrls(item.imageList ?: emptyList());
            }
        }
    }

}