package com.zhongpin.mvvm_android.ui.order.add.history

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListHistoryOrderItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHistoryOrderSearchItemBinding
import com.zhilianshidai.pindan.app.databinding.ListSearchOrderItemBinding
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.HistoryOrderItem
import com.zhongpin.mvvm_android.bean.SelectHistoryOrderItem
import com.zhongpin.mvvm_android.ui.common.BoxConfigData

class HistoryOrderSearchListAdapter(val mActivity: AppCompatActivity, data: MutableList<SelectHistoryOrderItem>)
    : BaseQuickAdapter<SelectHistoryOrderItem, HistoryOrderSearchListAdapter.VH>(data) {

    var  selectPosition = -1;

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: SelectHistoryOrderItem?
    ) {
        item?.let {
            holder.binding.apply {
                name.text = it.platCode ?: ""
                fluteText.text = "${it.lenType}瓦"
                if (TextUtils.isEmpty(it.size)) {
                    paperSize.text =  "${it.length} x ${it.width}"
                } else {
                    paperSize.text =  it.size
                }
                if (BoxConfigData.noneLineDesc.equals(BoxConfigData.lines["${it.line}"])) {
                    lineDesc.text = BoxConfigData.noneLineDesc;
                } else {
                    lineDesc.text = "${BoxConfigData.hasLineDesc} ${it.touch}";
                }
            }

            if (selectPosition == position) {
                holder.binding.chooseHistoryOrderContainer.setBackgroundResource(R.drawable.bg_choose_history_order_item_selected)
            } else {
                holder.binding.chooseHistoryOrderContainer.setBackgroundResource(R.drawable.bg_choose_history_order_item)
            }

            holder.binding.root.setOnClickListener {
                //can choose able
                selectPosition = position;
                notifyDataSetChanged();
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
        val binding: ListHistoryOrderSearchItemBinding = ListHistoryOrderSearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

}