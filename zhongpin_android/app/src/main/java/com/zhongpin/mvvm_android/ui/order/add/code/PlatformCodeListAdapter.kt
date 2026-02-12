package com.zhongpin.mvvm_android.ui.order.add.code

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListAddOrderPurchaseItemBinding
import com.zhilianshidai.pindan.app.databinding.ListPlatformCodeItemBinding
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.mvvm_android.bean.AddOrderPurchaseItem
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceDetailActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils

class PlatformCodeListAdapter(val mActivity: ChoosePlatCodeActivity, data: MutableList<PlatformMaterialItem>)
    : BaseQuickAdapter<PlatformMaterialItem, PlatformCodeListAdapter.VH>(data) {


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): PlatformCodeListAdapter.VH {
        return VH(parent)
    }

    class VH(
        parent: ViewGroup,
        val binding: ListPlatformCodeItemBinding = ListPlatformCodeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(
        holder: PlatformCodeListAdapter.VH,
        position: Int,
        item: PlatformMaterialItem?
    ) {
        item?.let {
            val platformItem = item;
            if (position % 2 == 0) {
                holder.binding.platformCodeContainer.setBackgroundResource(R.drawable.bg_platform_code_item_white)
            } else {
                holder.binding.platformCodeContainer.setBackgroundResource(R.drawable.bg_platform_code_item_gray)
            }

            holder.binding.apply {
                val typeName = PingDanAppUtils.getAdaptTypeName(platformItem.typeName, platformItem.type)
                paperMianZhi.show(typeName.contains("面纸"))
                paperWaZhi.show(typeName.contains("瓦纸"))
                paperZhongJia.show(typeName.contains("中夹"))
                paperDiZhi.show(typeName.contains("底纸"))

                name.text = item.name
                code.text = item.platCode
                weight.text = item.weight?.toString()
                price.text = item.price

                root.setOnClickListener {
                    mActivity.goPlatformPriceDetailActivity(item)
                }
            }



        }
    }


}