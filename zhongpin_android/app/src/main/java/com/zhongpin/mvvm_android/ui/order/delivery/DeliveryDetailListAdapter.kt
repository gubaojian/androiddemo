package com.zhongpin.mvvm_android.ui.order.delivery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.github.vipulasri.timelineview.TimelineView
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListOrderDeliveryProfItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.setBackgroundDrawableColor
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofItem
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

class DeliveryDetailListAdapter(val mActivity: AppCompatActivity, data: MutableList<OrderDeliveryProofItem>)
    : BaseQuickAdapter<OrderDeliveryProofItem, DeliveryDetailListAdapter.VH>(data) {


    class VH(
        parent: ViewGroup,
        private val viewType: Int,
        val binding: ListOrderDeliveryProfItemBinding = ListOrderDeliveryProfItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.timeline.initLine(viewType)
        }
    }

    override fun getItemViewType(position: Int, list: List<OrderDeliveryProofItem>): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }


    override fun onBindViewHolder(
        holder: DeliveryDetailListAdapter.VH,
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

                var itemTime =  item.createTime ?: item.updateTime ?: "";
                if (item.bizType == 2) {
                    deliveryStatusText.text = "${item.creatorName} 确认收货"
                    itemTime =  item.signTime ?: item.createTime ?: item.updateTime ?: "";
                    signStatus.gone()
                    stockStatus.gone()
                } else {
                    if (item.type == 1) {
                        deliveryStatusText.text = item.typeName ?: "部分送达"
                    } else {
                        deliveryStatusText.text = item.typeName ?: "全部送达"
                    }
                    itemTime =  item.createTime ?: item.updateTime ?: "";
                    signStatus.visible()
                    stockStatus.visible()
                }


                deliveryDate.text  = itemTime
                deliveryAmount.text = (item.num ?: 0).toString()
                deliveryPhotos.setImageUrls(item.imageList ?: emptyList());
                /**
                if (item.type == 1) {
                    deliveryPartStatus.visible()
                    deliveryDoneStatus.gone()
                } else {
                    deliveryPartStatus.gone()
                    deliveryDoneStatus.visible()
                }
                deliveryDate.text  = item.createTime ?: item.updateTime ?: ""
                deliveryAmount.text = (item.num ?: 0).toString()
                feedbackPhotos.setImageUrls(item.imageList ?: emptyList());
                */
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): DeliveryDetailListAdapter.VH {
        return VH(parent, viewType = viewType)
    }



}