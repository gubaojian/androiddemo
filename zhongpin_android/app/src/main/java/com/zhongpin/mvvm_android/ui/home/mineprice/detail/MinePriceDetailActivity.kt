package com.zhongpin.mvvm_android.ui.home.mineprice.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityMinePriceDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MaterialPriceDetailItem
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.RelateMaterialInfoItem
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.home.platform.list.PlatformListItemEntity
import com.zhongpin.mvvm_android.ui.home.platform.list.PlatformPriceListAdapter
import com.zhongpin.mvvm_android.ui.order.add.code.PlatformCodeListAdapter
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class MinePriceDetailActivity : BaseVMActivity<MinePriceDetailViewModel>() {


    private lateinit var mBinding: ActivityMinePriceDetailBinding;

    private var materialPriceItem: MaterialPriceItem? = null

    private var mRelateMaterialDatas:MutableList<RelateMaterialInfoItem> = mutableListOf()
    private lateinit var mRelateMaterialListAdapter: RelateMaterialListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            val materialPriceId = intent.getLongExtra("materialPriceId", 0)
            if (materialPriceId >= 0) {
                materialPriceItem = ShareParamDataUtils.materialPriceItem
                if (materialPriceItem != null) {
                    ShareParamDataUtils.materialPriceItem = null
                }
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityMinePriceDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialogV2()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }

        val item = materialPriceItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入专属报价详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        mRelateMaterialListAdapter = RelateMaterialListAdapter(this, mRelateMaterialDatas)
        mBinding.relateMaterialListView.layoutManager = LinearLayoutManager(this)
        mBinding.relateMaterialListView.adapter = mRelateMaterialListAdapter


        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)
    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mDetailItemData.observe(this) {
            if (it.success) {
                val detailItem = it.data;
                detailItem?.let {
                    showDetail(detailItem)
                }

            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMaterialPriceDetail(materialPriceItem?.id ?: 0);
    }

    private fun showDetail(priceItem: MaterialPriceDetailItem) {
        mBinding.apply {
            name.text = priceItem.platCode
            fluteText.text = "${priceItem.lenType}瓦"

            val specialPrice = priceItem.specialPrice?.toDoubleOrNull() ?: 0.0
            var itemPrice = priceItem.specialPrice;
            if (specialPrice <= 0) { //没有特价，
                itemPrice = priceItem.price
            }

            if (specialPrice <= 0) {
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

            purchaseTimes.text = "${priceItem.orderCount ?: 0}次"
            lastOrderTime.text = priceItem.orderTime
            updateDate.text = priceItem.updateTime ?: priceItem.createTime

            val relateItems = priceItem.materialInfoList ?: emptyList()
            if (relateItems.isEmpty()) {
                relateMaterialListContainer.gone()
            } else {
                relateMaterialListContainer.visible()
                var index = 0;
                relateItems.forEach { item ->
                    if (index % 2 == 1) {
                        item.cellItemCellColor = "#F5F6FA";
                    } else {
                        item.cellItemCellColor = "#FFFFFF";
                    }
                    index++
                    item.cellListShowIsLastItem = (item ==relateItems.last())
                    mRelateMaterialDatas.add(item)
                }
                mRelateMaterialListAdapter.notifyDataSetChanged()
            }
        }
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {

        }
    }




    override fun onDestroy() {
        super.onDestroy()
    }

}