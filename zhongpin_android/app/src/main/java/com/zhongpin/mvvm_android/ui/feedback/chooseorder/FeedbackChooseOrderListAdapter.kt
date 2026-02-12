package com.zhongpin.mvvm_android.ui.feedback.chooseorder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R.*
import com.zhilianshidai.pindan.app.databinding.ListChooseOrderItemBinding
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse

class FeedbackChooseOrderListAdapter(val mActivity: AppCompatActivity, data: MutableList<CompanyListItemResponse>)
    : BaseQuickAdapter<CompanyListItemResponse, FeedbackChooseOrderListAdapter.VH>(data) {

    var  selectPostion = -1;

    override fun onBindViewHolder(
        holder: FeedbackChooseOrderListAdapter.VH,
        position: Int,
        item: CompanyListItemResponse?
    ) {
        item?.let {
            holder.binding.apply {
                /**、
                name.text = it?.name ?: ""
                address.text = it?.address ?: ""
                legal.text = it?.legal ?: ""
                if (it.status == 0) {
                    statusText.text = "待审核"
                    statusText.setTextColor(Color.parseColor("#FFA826"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_wait)
                } else if (it.status == 1) {
                    statusText.text = "已认证"
                    statusText.setTextColor(Color.parseColor("#57C248"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_ok)
                } else if (it.status == 2) {
                    statusText.text = "认证失败"
                    statusText.setTextColor(Color.parseColor("#D34545"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_failed)
                } else {
                    statusText.text = "已认证"
                    statusText.setTextColor(Color.parseColor("#57C248"))
                    statusText.setBackgroundResource(drawable.bg_company_verify_status_ok)
                }*/
            }
            if (selectPostion == position) {
                holder.binding.chooseOrderItemContainer.setBackgroundResource(drawable.bg_choose_order_item_select)
            } else {
                holder.binding.chooseOrderItemContainer.setBackgroundResource(drawable.bg_choose_order_item_normal)
            }
            /**
            holder.binding.chooseOrderItemContainer.foreground = context.resources.getDrawable(
                drawable.fg_choose_order_item_disable,
                context.theme
            )*/
            holder.binding.root.setOnClickListener {
                //can choose able
                selectPostion = position;
                notifyDataSetChanged();
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FeedbackChooseOrderListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListChooseOrderItemBinding = ListChooseOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}