package com.zhongpin.mvvm_android.ui.pay.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityAboutBinding
import com.zhilianshidai.pindan.app.databinding.ActivityPayBillDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goOrderDetailActivity
import com.zhongpin.mvvm_android.ui.common.goWebActivity
import com.zhongpin.mvvm_android.ui.common.throttleToast
import com.zhongpin.mvvm_android.ui.debug.DebugActivity
import com.zhongpin.mvvm_android.ui.order.orderstatus.OrderStatusListAdapter
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils


class PayBillDetailActivity : BaseVMActivity<PayBillDetailViewModel>() {

    private var mDatas:MutableList<OrderItem> = mutableListOf()
    private lateinit var listAdapter: PayBillOrderListAdapter
    private lateinit var mBinding: ActivityPayBillDetailBinding;

    private var payId:Long = 0;
    var payItem:PayItem? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            payId = intent.getLongExtra("payId", 0);
            if (payId > 0) {
                payItem =  ShareParamDataUtils.payItem;
                ShareParamDataUtils.payItem = null;
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPayBillDetailBinding.inflate(layoutInflater, container, false)
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


        if (payItem == null) {
            Toast.makeText(applicationContext,"请传入收支明细详情信息", Toast.LENGTH_LONG).show()
            return;
        }


        payItem?.let {
            showPayItem(it)
        }


        listAdapter = PayBillOrderListAdapter(this, mDatas)
        listAdapter.setOnItemClickListener {
                adapter, view, position ->
            val orderListItem = mDatas.get(position)
            goOrderDetailActivity(orderListItem)
        }
        listAdapter.isStateViewEnable = true;
        listAdapter.setStateViewLayout(this, R.layout.empty_view_status_order_empty)

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = listAdapter


        //registerDefaultLoad(mBinding.loadContainer, ApiService.COMMON_KEY)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPurchaseOrderDetailData.observe(this) { responseIt ->
            if (responseIt.success) {
                val orders = responseIt.data?.orders ?: emptyList();
                val cancelOrderItem = orders.find { it.id == payItem?.userOrderId }
                if (cancelOrderItem != null) {
                    mBinding.orderInfo.visible()
                    mDatas.clear()
                    mDatas.addAll(listOf(cancelOrderItem))
                    listAdapter.notifyDataSetChanged()
                } else {
                    if (orders.isEmpty()) {
                        mBinding.orderInfo.gone()
                    } else {
                        mBinding.orderInfo.visible()
                        mDatas.clear()
                        mDatas.addAll(orders)
                        listAdapter.notifyDataSetChanged()
                    }
                }

            } else {
                mBinding.orderInfo.gone()
                throttleToast(responseIt.msg)
            }
        }
    }

    override fun initData() {
        super.initData()
        payItem?.purId?.let {
            if (it <= 0) {
                return@let
            }
            mViewModel.getPurchaseOrderDetail(it)
        }
    }



    private fun showPayItem(item: PayItem) {
        mBinding.payDesc.text = item.describe ?: "收支明细";
        if (item.receiveType == 0) {
            SpanUtils.with( mBinding.payAmount)
                .append("+")
                .setFontSize(24, true)
                .setVerticalAlign(SpanUtils.ALIGN_CENTER)
                .append(item.amount ?: "0")
                .setFontSize(36, true)
                .create()
        } else if(item.receiveType == 1) {
            SpanUtils.with( mBinding.payAmount)
                .append("-")
                .setFontSize(24, true)
                .setVerticalAlign(SpanUtils.ALIGN_CENTER)
                .append(item.amount ?: "0")
                .setFontSize(36, true)
                .create()
        } else {
            SpanUtils.with( mBinding.payAmount)
                .append(item.amount ?: "0")
                .setFontSize(36, true)
                .create()
        }

        mBinding.payResultDesc.text = item.payStatusDesc()

        mBinding.payMethodDesc.text = item.payTypeDesc();
        if (item.payTypeDesc().equals("支付宝")) {
            mBinding.payMethodIcon.visible();
            mBinding.payMethodIcon.setImageResource(R.mipmap.pay_alipay_ic)
        } else if (item.payTypeDesc().equals("微信")) {
            mBinding.payMethodIcon.visible();
            mBinding.payMethodIcon.setImageResource(R.mipmap.pay_wechat_ic)
        } else {
            mBinding.payMethodIcon.gone()
        }

        mBinding.payTime.text = item.createTime
        mBinding.payOrderNo.text = item.orderId
        if (TextUtils.isEmpty(item.waterId)) {
            mBinding.waterNoContainer.gone()
        } else {
            mBinding.waterNoContainer.visible()
            mBinding.transNo.text = item.waterId
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

    override fun onDestroy() {
        super.onDestroy()
    }

}