package com.zhongpin.mvvm_android.ui.home.platform.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ListPlatformListItemPriceItemBinding
import com.zhilianshidai.pindan.app.databinding.ListPlatformListItemTitleItemBinding
import com.zhongpin.lib_base.ktx.extSetDrawableColor
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceDetailActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow


class PlatformListItemEntity(
    val data:Any? = null,
    val type:Int = 0
) {

}

class PlatformPriceListAdapter(val mActivity: PlatformPriceListActivity, data: MutableList<PlatformListItemEntity>)
    : BaseMultiItemAdapter<PlatformListItemEntity>(data)  {

    class PlatformPriceItemTitleVH(
        parent: ViewGroup,
        val binding: ListPlatformListItemTitleItemBinding = ListPlatformListItemTitleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class PlatformPriceItemVH(
        parent: ViewGroup,
        val binding: ListPlatformListItemPriceItemBinding = ListPlatformListItemPriceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)



    init {
        addItemType(PLATFORM_PRICE_ITEM_TITLE,  object : OnMultiItemAdapterListener<PlatformListItemEntity, PlatformPriceItemTitleVH> {
            override fun onBind(holder: PlatformPriceItemTitleVH, position: Int, item: PlatformListItemEntity?) {

            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): PlatformPriceItemTitleVH {
                return PlatformPriceItemTitleVH(parent)
            }

        }
        ).addItemType(PLATFORM_PRICE_ITEM_ITEM,  object : OnMultiItemAdapterListener<PlatformListItemEntity, PlatformPriceItemVH> {
            override fun onBind(holder: PlatformPriceItemVH, position: Int, item: PlatformListItemEntity?) {
                val platformItem = item?.data as  PlatformMaterialItem?
                platformItem?.let {
                    holder.binding.apply {
                        if (platformItem.cellListShowIsLastItem) {
                            platformItemContainer.setBackgroundResource(R.drawable.bg_home_platform_price_bottom)
                        } else {
                            platformItemContainer.setBackgroundResource(R.drawable.bg_home_platform_price_middle)
                        }

                        val background =  platformItemContainer.background
                        background.extSetDrawableColor(platformItem.cellItemCellColor)

                        val typeName =  PingDanAppUtils.getAdaptTypeName(platformItem.typeName, platformItem.type)
                        paperMianZhi.show(typeName.contains("面纸"))
                        paperWaZhi.show(typeName.contains("瓦纸"))
                        paperZhongJia.show(typeName.contains("中夹"))
                        paperDiZhi.show(typeName.contains("底纸"))


                        name.text = platformItem.name
                        code.text = platformItem.platCode
                        weight.text = platformItem.weight?.toString()
                        price.text = platformItem.price

                        platformPriceArrow.setPlatformPriceArrow(platformItem.price, platformItem.prePrice)


                        root.setOnClickListener {
                            mActivity.goPlatformPriceDetailActivity(platformItem)
                        }
                    }
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): PlatformPriceItemVH {
                return PlatformPriceItemVH(parent)
            }

        }
        ).onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
            list[position].type
        }
    }

    companion object {
        const val PLATFORM_PRICE_ITEM_TITLE = 32
        const val PLATFORM_PRICE_ITEM_ITEM = 35
    }
}