package com.zhongpin.mvvm_android.ui.order.preview.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPreviewOrderDetailBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PreviewPurchaseOrderItem
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.BoxConfigData
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.IntentUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PreviewOrderDetailActivity : BaseVMActivity<PreviewOrderDetailViewModel>() {


    private lateinit var mBinding: ActivityPreviewOrderDetailBinding;

    private var orderItem: PreviewPurchaseOrderItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            orderItem = IntentUtils.getSerializableExtra(intent, "orderItem", PreviewPurchaseOrderItem::class.java)
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPreviewOrderDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        mBinding.ivBack.setOnClickListener { finish() }

        val item = orderItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入预览订单详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        mBinding.material.text = item.platCode ?: ""
        mBinding.fluteText.text = item.lenType ?: ""
        mBinding.paperSize.text = item.size ?: ""
        mBinding.purchaseAmount.text = (item.num ?: "").toString()
        mBinding.areaAmount.text = (item.area ?: "").toString()

        if (item.line == 0) {
            mBinding.lineDescContainer.visibility = View.GONE
            mBinding.lineText.text = BoxConfigData.noneLineDesc;
        } else {
            mBinding.lineDesc.text = item.touch
            mBinding.lineText.text = BoxConfigData.hasLineDesc;
        }
        mBinding.demandDate.text = PingDanAppUtils.getDateDay(item.demandTime ?: "")


        SpanUtils.with(mBinding.unitPrice)
            .append("￥")
            .append( (item.areaPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.unitMaterialPrice)
            .append("￥")
            .append( (item.materialPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.unitCutPrice)
            .append("￥")
            .append( (item.cutPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.unitStepPrice)
            .append("￥")
            .append( (item.stepPrice ?: "0").toString())
            .create()



        SpanUtils.with(mBinding.totalPrice)
            .append("￥")
            .append( (item.totalPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.totalMaterialPrice)
            .append("￥")
            .append( (item.materialTotalPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.totalCutPrice)
            .append("￥")
            .append( (item.cutTotalPrice ?: "0").toString())
            .create()

        SpanUtils.with(mBinding.totalStepPrice)
            .append("￥")
            .append( (item.stepTotalPrice ?: "0").toString())
            .create()


        mBinding.unitPriceContainer.setOnClickListener {
            if (mBinding.unitPriceDetail.isVisible)  {
                mBinding.unitPriceDetail.visibility = View.GONE
                mBinding.unitPriceDetailArrow.setImageResource(R.mipmap.arrow_up)
            } else {
                mBinding.unitPriceDetailArrow.setImageResource(R.mipmap.arrow_down)
                mBinding.unitPriceDetail.visibility = View.VISIBLE
            }
        }

        mBinding.totalPriceContainer.setOnClickListener {
            if (mBinding.totalPriceDetail.isVisible)  {
                mBinding.totalPriceDetail.visibility = View.GONE
                mBinding.totalPriceDetailArrow.setImageResource(R.mipmap.arrow_up)
            } else {
                mBinding.totalPriceDetailArrow.setImageResource(R.mipmap.arrow_down)
                mBinding.totalPriceDetail.visibility = View.VISIBLE
            }
        }



    }

    override fun initDataObserver() {
        super.initDataObserver()

    }

    override fun initData() {
        super.initData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : CompanyInfoChangeEvent){
        if (infoEvent.isChange) {
            finish()
        }
    }

    private var mLoadingDialog: LoadingDialog? = null
    /**
     * show 加载中
     */
    fun showLoadingDialog() {
        dismissLoadingDialog()
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this, false)
        }
        mLoadingDialog?.showDialogV2(this)
    }

    /**
     * dismiss loading dialog
     */
    fun dismissLoadingDialog() {
        mLoadingDialog?.dismissDialogV2()
        mLoadingDialog = null
    }



}