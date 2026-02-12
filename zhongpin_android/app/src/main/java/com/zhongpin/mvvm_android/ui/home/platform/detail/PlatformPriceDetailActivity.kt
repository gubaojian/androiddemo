package com.zhongpin.mvvm_android.ui.home.platform.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPlatformPriceDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.show
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.MaterialPriceItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PlatformMaterialItem
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setPlatformPriceArrow
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PlatformPriceDetailActivity : BaseVMActivity<PlatformPriceDetailViewModel>() {


    private lateinit var mBinding: ActivityPlatformPriceDetailBinding;

    private var materialPriceItem: PlatformMaterialItem? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            val materialPriceId = intent.getLongExtra("materialPriceId", 0)
            if (materialPriceId >= 0) {
                materialPriceItem = ShareParamDataUtils.platformMaterialPriceItem
                if (materialPriceItem != null) {
                    ShareParamDataUtils.platformMaterialPriceItem = null
                }
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPlatformPriceDetailBinding.inflate(layoutInflater, container, false)
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
            Toast.makeText(applicationContext,"请传入平台报价详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        materialPriceItem?.let {
            showDetail(it)
        }

        //registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)
    }

    override fun initDataObserver() {
        super.initDataObserver()

    }

    private fun showDetail(platformItem: PlatformMaterialItem) {
        mBinding.apply {
            val typeName = PingDanAppUtils.getAdaptTypeName(platformItem.typeName, platformItem.type)
            paperMianZhi.show(typeName.contains("面纸"))
            paperWaZhi.show(typeName.contains("瓦纸"))
            paperZhongJia.show(typeName.contains("中夹"))
            paperDiZhi.show(typeName.contains("底纸"))

            name.text = platformItem.name
            code.text =  "材质代码(平台)：${platformItem.platCode}"
            weight.text = platformItem.weightWithUnit()
            types.text = typeName
            paperPoPress.text = platformItem.crush
            paperCirclePress.text = platformItem.pressure
            price.text = platformItem.price

            platformPriceArrow.setPlatformPriceArrow(platformItem.price, platformItem.prePrice)

            lastUpdateTime.text = platformItem.updateTime ?: platformItem.createTime
        }
    }

    override fun initData() {
        super.initData()
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