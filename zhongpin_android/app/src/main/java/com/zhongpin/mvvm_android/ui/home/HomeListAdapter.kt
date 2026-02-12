package com.zhongpin.mvvm_android.ui.home

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.HomeHeaderViewItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeCompanyNeedVerifyItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeCompanyVerifyingItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeFabuScanItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeMinePriceEmptyBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeMinePriceItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeMinePriceTitleItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeMinePriceUnLoginItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeNewsContentItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomeNewsTitleItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomePingtaiDataItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomePlatformPriceItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomePlatformPriceItemTitleItemBinding
import com.zhilianshidai.pindan.app.databinding.ListHomePlatformPriceTitleItemBinding
import com.zhongpin.lib_base.ktx.extSetDrawableColor
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgain
import com.zhongpin.mvvm_android.ui.common.goCompanyVerifyPage
import com.zhongpin.mvvm_android.ui.common.goMinePriceDetailActivity
import com.zhongpin.mvvm_android.ui.common.goMinePriceListActivity
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceDetailActivity
import com.zhongpin.mvvm_android.ui.common.goPlatformPriceListActivity
import com.zhongpin.mvvm_android.ui.debug.DebugActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow

class HomeListAdapter(val mFragment: HomeFragment, val mData: MutableList<HomeItemEntity>)
    : BaseMultiItemAdapter<HomeItemEntity>(mData) {

    class BuyScanVH(
        parent: ViewGroup,
        val binding: ListHomeFabuScanItemBinding = ListHomeFabuScanItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class PingTaiDataVH(
        parent: ViewGroup,
        val binding: ListHomePingtaiDataItemBinding = ListHomePingtaiDataItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class NewsTitleVH(
        parent: ViewGroup,
        val binding: ListHomeNewsTitleItemBinding = ListHomeNewsTitleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class NewsItemVH(
        parent: ViewGroup,
        val binding: ListHomeNewsContentItemBinding = ListHomeNewsContentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class BannerVH(
        parent: ViewGroup,
        val binding: HomeHeaderViewItemBinding = HomeHeaderViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


    class CompanyVerifyNeedVH(
        parent: ViewGroup,
        val binding: ListHomeCompanyNeedVerifyItemBinding = ListHomeCompanyNeedVerifyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class CompanyVerifyIngVH(
        parent: ViewGroup,
        val binding: ListHomeCompanyVerifyingItemBinding = ListHomeCompanyVerifyingItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class MinePriceTitleVH(
        parent: ViewGroup,
        val binding: ListHomeMinePriceTitleItemBinding = ListHomeMinePriceTitleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class MinePriceUnLoginVH(
        parent: ViewGroup,
        val binding: ListHomeMinePriceUnLoginItemBinding = ListHomeMinePriceUnLoginItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class MinePriceItemVH(
        parent: ViewGroup,
        val binding: ListHomeMinePriceItemBinding = ListHomeMinePriceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class MinePriceItemEmptyVH(
        parent: ViewGroup,
        val binding: ListHomeMinePriceEmptyBinding = ListHomeMinePriceEmptyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


    class PlatformPriceTitleVH(
        parent: ViewGroup,
        val binding: ListHomePlatformPriceTitleItemBinding = ListHomePlatformPriceTitleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class PlatformPriceItemTitleVH(
        parent: ViewGroup,
        val binding: ListHomePlatformPriceItemTitleItemBinding = ListHomePlatformPriceItemTitleItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    class PlatformPriceItemVH(
        parent: ViewGroup,
        val binding: ListHomePlatformPriceItemBinding = ListHomePlatformPriceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


    init {
        addItemType(BUY_SCAN_TYPE,  object : OnMultiItemAdapterListener<HomeItemEntity, BuyScanVH> {
                override fun onBind(holder: BuyScanVH, position: Int, item: HomeItemEntity?) {
                    holder.binding.scan.setOnClickListener {
                        mFragment.onClickScan();
                    }
                    holder.binding.fabu.setOnClickListener {
                        mFragment.onClickFaBu();
                    }
                }
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): BuyScanVH {
                   return BuyScanVH(parent)
                }

            }
        ).addItemType(PINGTAI_DATA_TYPE,  object : OnMultiItemAdapterListener<HomeItemEntity, PingTaiDataVH> {
                override fun onBind(holder: PingTaiDataVH, position: Int, item: HomeItemEntity?) {

                }

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): PingTaiDataVH {
                    return PingTaiDataVH(parent)
                }

          }
        ).addItemType(NEWS_TITLE_TYPE,  object : OnMultiItemAdapterListener<HomeItemEntity, NewsTitleVH> {
            override fun onBind(holder: NewsTitleVH, position: Int, item: HomeItemEntity?) {

            }

            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): NewsTitleVH {
                return NewsTitleVH(parent)
            }

        }
        ).addItemType(NEWS_ITEM_TYPE,  object : OnMultiItemAdapterListener<HomeItemEntity, NewsItemVH> {
            override fun onBind(holder: NewsItemVH, position: Int, item: HomeItemEntity?) {
                holder.binding.root.setOnClickListener {
                    val intent = Intent(mFragment.requireActivity(), DebugActivity::class.java)
                    mFragment.startActivity(intent)
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): NewsItemVH {
                return NewsItemVH(parent)
            }

        }
        ).addItemType(COMPANY_VERIFY_NEED,  object : OnMultiItemAdapterListener<HomeItemEntity, CompanyVerifyNeedVH> {
            override fun onBind(holder: CompanyVerifyNeedVH, position: Int, item: HomeItemEntity?) {
                holder.binding.companyVerifyNow.setOnClickListener {
                    mFragment.requireActivity().goCompanyVerifyPage(UserInfoUtil.companyInfo)
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): CompanyVerifyNeedVH {
                return CompanyVerifyNeedVH(parent)
            }

        }
        ).addItemType(COMPANY_VERIFY_ING,  object : OnMultiItemAdapterListener<HomeItemEntity, CompanyVerifyIngVH> {
            override fun onBind(holder: CompanyVerifyIngVH, position: Int, item: HomeItemEntity?) {
                var companyInfo = item?.data as CompanyListItemResponse?
                holder.binding.seeVerifyProgress.setOnClickListener {
                    mFragment.requireActivity().goCompanyVerifyPage(UserInfoUtil.companyInfo)
                }
                companyInfo?.let {
                    if (it.status == 3) {
                        holder.binding.companyStatusText.text = "企业认证：审核拒绝";
                    } else {
                        holder.binding.companyStatusText.text = "企业认证：认证中";
                    }
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): CompanyVerifyIngVH {
                return CompanyVerifyIngVH(parent)
            }

        }
        ).addItemType(MINE_PRICE_TITLE,  object : OnMultiItemAdapterListener<HomeItemEntity, MinePriceTitleVH> {
            override fun onBind(holder: MinePriceTitleVH, position: Int, item: HomeItemEntity?) {
                holder.binding.root.setOnClickListener {
                    mFragment.requireActivity().goMinePriceListActivity()
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): MinePriceTitleVH {
                return MinePriceTitleVH(parent)
            }

        }
        ).addItemType(MINE_PRICE_UN_LOGIN,  object : OnMultiItemAdapterListener<HomeItemEntity, MinePriceUnLoginVH> {
            override fun onBind(holder: MinePriceUnLoginVH, position: Int, item: HomeItemEntity?) {
                holder.binding.homeItemLoginNow.setOnClickListener {
                    LoginUtils.toLogin(mFragment.requireActivity())
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): MinePriceUnLoginVH {
                return MinePriceUnLoginVH(parent)
            }

        }
        ).addItemType(MINE_PRICE_ITEM,  object : OnMultiItemAdapterListener<HomeItemEntity, MinePriceItemVH> {
            override fun onBind(holder: MinePriceItemVH, position: Int, item: HomeItemEntity?) {
                val priceItem = item?.data as  MaterialPriceItem?
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

                        root.setOnClickListener {
                            mFragment.requireActivity().goMinePriceDetailActivity(priceItem)
                        }

                        buyPriceItem.setOnClickListener {
                            mFragment.requireActivity().goBuyOrderAgain(priceItem)
                        }
                    }
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): MinePriceItemVH {
                return MinePriceItemVH(parent)
            }

        }
        ).addItemType(MINE_PRICE_ITEM_EMPTY,  object : OnMultiItemAdapterListener<HomeItemEntity, MinePriceItemEmptyVH> {
            override fun onBind(holder: MinePriceItemEmptyVH, position: Int, item: HomeItemEntity?) {

            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): MinePriceItemEmptyVH {
                return MinePriceItemEmptyVH(parent)
            }

        }
        ).addItemType(PLATFORM_PRICE_TITLE,  object : OnMultiItemAdapterListener<HomeItemEntity, PlatformPriceTitleVH> {
            override fun onBind(holder: PlatformPriceTitleVH, position: Int, item: HomeItemEntity?) {
                holder.binding.root.setOnClickListener {
                    mFragment.requireActivity().goPlatformPriceListActivity()
                }
            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): PlatformPriceTitleVH {
                return PlatformPriceTitleVH(parent)
            }

        }
        ).addItemType(PLATFORM_PRICE_ITEM_TITLE,  object : OnMultiItemAdapterListener<HomeItemEntity, PlatformPriceItemTitleVH> {
            override fun onBind(holder: PlatformPriceItemTitleVH, position: Int, item: HomeItemEntity?) {

            }
            override fun onCreate(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): PlatformPriceItemTitleVH {
                return PlatformPriceItemTitleVH(parent)
            }

        }
        ).addItemType(PLATFORM_PRICE_ITEM_ITEM,  object : OnMultiItemAdapterListener<HomeItemEntity, PlatformPriceItemVH> {
            override fun onBind(holder: PlatformPriceItemVH, position: Int, item: HomeItemEntity?) {
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
                            mFragment.requireActivity().goPlatformPriceDetailActivity(platformItem)
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
        const val BUY_SCAN_TYPE = 0
        const val PINGTAI_DATA_TYPE = 1
        const val NEWS_TITLE_TYPE = 2
        const val NEWS_ITEM_TYPE = 3

        const val IMAGE_TYPE = 10


        const val COMPANY_VERIFY_NEED = 15
        const val COMPANY_VERIFY_ING = 16

        const val MINE_PRICE_TITLE = 20
        const val MINE_PRICE_UN_LOGIN = 22
        const val MINE_PRICE_ITEM = 25
        const val MINE_PRICE_ITEM_EMPTY = 26

        const val PLATFORM_PRICE_TITLE = 30
        const val PLATFORM_PRICE_ITEM_TITLE = 32
        const val PLATFORM_PRICE_ITEM_ITEM = 35

    }

}