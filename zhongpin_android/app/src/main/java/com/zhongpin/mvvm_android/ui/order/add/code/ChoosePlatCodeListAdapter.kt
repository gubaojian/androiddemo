package com.zhongpin.mvvm_android.ui.order.add.code

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListAddOrderPurchaseItemBinding
import com.zhilianshidai.pindan.app.databinding.ListChoosePricePlatCodeItemBinding
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.MaterialPriceItem

class ChoosePlatCodeListAdapter(val mActivity: ChoosePlatCodeActivity, data: MutableList<MaterialPriceItem>)
    : BaseQuickAdapter<MaterialPriceItem, ChoosePlatCodeListAdapter.VH>(data) {

    var  selectPosition = -1;

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ChoosePlatCodeListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListChoosePricePlatCodeItemBinding = ListChoosePricePlatCodeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: ChoosePlatCodeListAdapter.VH,
        position: Int,
        item: MaterialPriceItem?
    ) {
        item?.let {
            holder.binding.apply {
                name.text = item.platCode
                fluteText.text = "${item.lenType}瓦"
                val specialPriceDouble = item.specialPrice?.toDoubleOrNull() ?: 0.0;
                if (!TextUtils.isEmpty(item.price) && specialPriceDouble > 0.0) {
                    originPrice.visibility = View.VISIBLE
                    SpanUtils.with(originPrice)
                        .append("${item.price}元/㎡")
                        .setStrikethrough()
                        .create()
                    SpanUtils.with(price)
                        .append(item.specialPrice ?: "")
                        .setFontSize(16, true)
                        .append("元/㎡")
                        .setFontSize(12, true)
                        .create()
                } else {
                    originPrice.visibility = View.GONE
                    var priceText = item.specialPrice;
                    if (TextUtils.isEmpty(priceText) || specialPriceDouble < 0.01) {
                        priceText = item.price
                    }
                    SpanUtils.with(price)
                        .append(priceText ?: "")
                        .setFontSize(16, true)
                        .append("元/㎡")
                        .setFontSize(12, true)
                        .create()
                }
                paperDetail.text = item.detail
            }
            if (selectPosition == position) {
                holder.binding.choosePlatCodeContainer.setBackgroundResource(R.drawable.bg_choose_plat_code_item_selected)
            } else {
                holder.binding.choosePlatCodeContainer.setBackgroundResource(R.drawable.bg_choose_plat_code_item)
            }

            holder.binding.root.setOnClickListener {
                //can choose able
                selectPosition = position;
                notifyDataSetChanged();
            }
        }
    }


}