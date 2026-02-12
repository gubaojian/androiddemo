package com.zhongpin.mvvm_android.ui.home.mineprice

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListMinePriceListMinePriceItemBinding
import com.zhilianshidai.pindan.app.databinding.ListPayRecordItemBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.invisible
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgain
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow

class MinePriceListAdapter(val mActivity: MinePriceListActivity, data: MutableList<MaterialPriceItem>)
    : BaseQuickAdapter<MaterialPriceItem, MinePriceListAdapter.VH>(data) {


    override fun onBindViewHolder(
        holder: MinePriceListAdapter.VH,
        position: Int,
        item: MaterialPriceItem?
    ) {
        val priceItem = item;
        priceItem?.let {
            holder.binding.apply {
                name.text = priceItem.platCode
                fluteText.text = "${priceItem.lenType}瓦"

                var itemPrice = priceItem.specialPrice;

                val specialPrice = priceItem.specialPrice?.toDoubleOrNull() ?: 0.0
                if (specialPrice <= 0.0) { //没有特价，
                    itemPrice = priceItem.price
                }

                if (specialPrice <= 0.0) {
                    tagSpecialPrice.gone()
                } else {
                    tagSpecialPrice.visible()
                }

                if (specialPrice <= 0.0) {
                    originPrice.gone()
                } else {
                    originPrice.visible()
                    SpanUtils.with(originPrice)
                        .append("￥")
                        .append(priceItem.price ?: "")
                        .setStrikethrough()
                        .create()
                }

                SpanUtils.with(price)
                    .append("￥")
                    .append(itemPrice ?: "")
                    .setFontSize(14, true)
                    .create()

                priceArrow.setPlatformPriceArrow(priceItem.price,priceItem.prePrice)

                var priceUpdateTime  = priceItem.priceUpdateTime ?: priceItem.updateTime ?: priceItem.createTime;
                priceUpdateTime = PingDanAppUtils.getDateDay(priceUpdateTime ?: "")
                holder.binding.priceUpdateTime.text = "${priceUpdateTime} 更新"

                var orderTime = PingDanAppUtils.getDateDay(priceItem.orderTime ?: priceItem.updateTime  ?: "")
                updateDate.text = "订购${priceItem.orderCount}次，上次订购 ${orderTime}"

                buyPriceItem.setOnClickListener {
                    mActivity.goBuyOrderAgain(priceItem)
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
        val binding: ListMinePriceListMinePriceItemBinding = ListMinePriceListMinePriceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


}