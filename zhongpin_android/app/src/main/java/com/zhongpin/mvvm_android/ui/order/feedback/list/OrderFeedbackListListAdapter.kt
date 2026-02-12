package com.zhongpin.mvvm_android.ui.order.feedback.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.databinding.ListOrderFeedbackItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.OrderFeedbackItem
import com.zhongpin.mvvm_android.ui.common.goEditFeedbackPage
import com.zhongpin.mvvm_android.ui.common.showCancelFeedbackDialog
import com.zhongpin.mvvm_android.ui.common.showContractKeFuDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

class OrderFeedbackListListAdapter(val mActivity: OrderFeedbackListActivity, data: MutableList<OrderFeedbackItem>)
    : BaseQuickAdapter<OrderFeedbackItem, OrderFeedbackListListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: OrderFeedbackListListAdapter.VH,
        position: Int,
        item: OrderFeedbackItem?
    ) {
        item?.let {
            holder.binding.apply {
                feedbackDate.text = item.createTime ?: ""
                if (item.appealStatus == 3) {
                    feedbackStatusCancel.visible()
                    feedbackStatusIng.gone()
                    feedbackStatusDone.gone()
                    cancelFeedback.gone()
                    editFeedback.gone()
                } else if (item.appealStatus == 2) {
                    feedbackStatusCancel.gone()
                    feedbackStatusIng.gone()
                    feedbackStatusDone.visible()
                    cancelFeedback.gone()
                    editFeedback.gone()
                } else {
                    feedbackStatusCancel.gone()
                    feedbackStatusDone.gone()
                    feedbackStatusIng.visible()

                    if (item.appealStatus == 0) {
                        feedbackStatusIng.text = "待处理"
                        cancelFeedback.visible()
                        editFeedback.visible()
                    } else {
                        feedbackStatusIng.text = "处理中"
                        cancelFeedback.gone()
                        editFeedback.gone()
                    }
                }
                feedbackNo.text = item.appealNo
                feedbackTypeText.text = item.appealTypeName
                solutionTypeText.text = item.handleTypeName
                val num = item.num ?: 0;
                if (num > 0 && item.totalPrice != null) {
                    feedbackAmount.text = "${num}(￥${item.totalPrice})"
                } else {
                    feedbackAmount.text = "${num}"
                }

                SpanUtils.with( feedbackMoneyAmount)
                    .append("￥")
                    .append( (item.price ?: "0"))
                    .create()

                feedbackDesc.text = item.description ?: ""

                feedbackPhotos.setImageUrls(item.imageList ?: emptyList());

                val deliverImageList = item.deliverImageList ?: emptyList()
                deliveryPhotosContainer.show(deliverImageList.isNotEmpty())
                deliveryPhotos.setImageUrls(deliverImageList)

                cancelFeedback.setOnClickListener {
                    mActivity.showCancelFeedbackDialog({
                        mActivity.cancelFeedback(item)
                    });
                }

                editFeedback.setOnClickListener {
                    mActivity.goEditFeedbackPage(item)
                }

                contractKeFu.setOnClickListener {
                    mActivity.showContractKeFuDialog()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): OrderFeedbackListListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListOrderFeedbackItemBinding = ListOrderFeedbackItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}