package com.zhongpin.mvvm_android.ui.notify

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R.*
import com.zhilianshidai.pindan.app.databinding.ListCompanyItemBinding
import com.zhilianshidai.pindan.app.databinding.ListNotifyItemBinding
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse

class NotifyListAdapter(val mFragment: NotifyListFragment, data: MutableList<CompanyListItemResponse>)
    : BaseQuickAdapter<CompanyListItemResponse, NotifyListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: NotifyListAdapter.VH,
        position: Int,
        item: CompanyListItemResponse?
    ) {
        item?.let {
            holder.binding.apply {
                /**
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
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): NotifyListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListNotifyItemBinding = ListNotifyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}