package com.zhongpin.mvvm_android.ui.home.mineprice.detail

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListMinePriceDetailReplateMaterialItemBinding
import com.zhongpin.lib_base.ktx.extSetDrawableColor
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.lib_base.utils.convertTo
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.bean.RelateMaterialInfoItem
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceDetailActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow

class RelateMaterialListAdapter(val mActivity: Activity, data: MutableList<RelateMaterialInfoItem>)
    : BaseQuickAdapter<RelateMaterialInfoItem, RelateMaterialListAdapter.VH>(data) {


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): RelateMaterialListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListMinePriceDetailReplateMaterialItemBinding = ListMinePriceDetailReplateMaterialItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: RelateMaterialListAdapter.VH,
        position: Int,
        item: RelateMaterialInfoItem?
    ) {
        item?.let {
            val platformItem = item;
            holder.binding.apply {
                if (platformItem.cellListShowIsLastItem) {
                    platformItemContainer.setBackgroundResource(R.drawable.bg_home_platform_price_bottom)
                } else {
                    platformItemContainer.setBackgroundResource(R.drawable.bg_home_platform_price_middle)
                }

                val background =  platformItemContainer.background
                background.extSetDrawableColor(platformItem.cellItemCellColor)

                val typeName = PingDanAppUtils.getAdaptTypeName(platformItem.typeName, platformItem.type)
                paperMianZhi.show(typeName.contains("面纸"))
                paperWaZhi.show(typeName.contains("瓦纸"))
                paperZhongJia.show(typeName.contains("中夹"))
                paperDiZhi.show(typeName.contains("底纸"))

                name.text = platformItem.name
                code.text = platformItem.platCode
                weight.text = platformItem.weight
                price.text = platformItem.price

                platformPriceArrow.setPlatformPriceArrow(platformItem.price, platformItem.prePrice)


                root.setOnClickListener {
                    platformItem.convertTo(PlatformMaterialItem::class.java)?.let {
                        mActivity.goPlatformPriceDetailActivity(it)
                    }
                }
            }



        }
    }


}