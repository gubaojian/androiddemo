package com.zhongpin.mvvm_android.ui.order.confirmreceipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityConfirmReceiptOrderBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderDeliveryProofItem
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.SubmitBuyOrderDoneEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.autoShowConfirmReceiptBottomSheet
import com.zhongpin.mvvm_android.ui.common.goChargeInputActivity
import com.zhongpin.mvvm_android.ui.common.goConfirmReceiptOrderPayResultActivity
import com.zhongpin.mvvm_android.ui.common.goSubmitOrderResultActivity
import com.zhongpin.mvvm_android.ui.common.throttleToast
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setWaBg
import com.zhongpin.mvvm_android.ui.view.ext.setWaText
import com.zhongpin.mvvm_android.ui.view.ext.setWaTextColor
import com.zhongpin.mvvm_android.view.dialog.ConfirmImageDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class ConfirmReceiptOrderActivity : BaseVMActivity<ConfirmReceiptOrderViewModel>() {


    private lateinit var mBinding: ActivityConfirmReceiptOrderBinding;


    private var mDatas: MutableList<OrderDeliveryProofItem> = mutableListOf()
    private lateinit var mListAdapter: ConfirmReceiptItemListAdapter

    private var orderListItem: OrderItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            val orderId = intent.getLongExtra("orderId", 0)
            if (orderId >= 0) {
                orderListItem = ShareParamDataUtils.orderItem
                if (orderListItem != null) {
                    ShareParamDataUtils.orderItem = null
                }
            }
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityConfirmReceiptOrderBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener {
            finish()
        }


        val item = orderListItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入订单详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        mListAdapter = ConfirmReceiptItemListAdapter(this, mDatas)


        mBinding.orderListRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.orderListRecyclerView.adapter = mListAdapter




        mBinding.btnSubmit.setOnClickListener {
            showConfirmReceiptOrder()
        }
        registerDefaultLoad(mBinding.body, Constant.COMMON_KEY)

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPageData.observe(this) { outerIt ->
            if (outerIt.success) {
                showSuccess(Constant.COMMON_KEY)
                showPageData()
            } else {
                showError(outerIt.msg, Constant.COMMON_KEY)
            }
        }
    }


    override fun initData() {
        super.initData()
        getPageData()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(event : OrderItemInfoChangeEvent) {
          getPageData()
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

    private fun getPageData() {
        mViewModel.getPageData(orderListItem?.id ?: 0)
    }




    fun showPageData() {
        val orderDetailItem = mViewModel.mOrderItemData.value?.data;
        orderDetailItem?.let { item ->
            mBinding.apply {
                waBg.setWaBg(item.platCode)
                name.setWaTextColor(item.platCode)
                fluteText.setWaTextColor(item.platCode)

                name.setWaText(item.platCode)
                fluteText.text = "${item.lenType}瓦"
                SpanUtils.with(price)
                    .append("￥")
                    .setFontSize(12, true)
                    .append(item.totalPrice ?: "0.0")
                    .setFontSize(16, true)
                    .create()
                paperSize.text = PingDanAppUtils.toPaperSizeWithUnitMM(item.size ?: "")
                purchaseAmount.text = "${item.num}张"
            }
        }

        orderDetailItem?.let { item ->
            mBinding.apply {
                hasSignAmount.text = "${item.signNum}张"
                hasStoreAmount.text = "${item.receiptNum ?: 0}张"
                unSignNum.text = "${item.unSignNum ?: 0}张"
            }

            val extraNum = item.extraNum ?: 0L;
            if (extraNum > 0) {
                mBinding.extraPriceInfo.visible()
                mBinding.extraAmount.text = "${item.extraNum}"
                SpanUtils.with( mBinding.extraPayPrice)
                    .append("￥")
                    .append(item.extraPrice ?: "0.0")
                    .create()
            } else {
                mBinding.extraPriceInfo.gone()
            }
        }



        val deliveryList = mViewModel.mDeliveryPageData.value?.data?.records ?: emptyList()
        mDatas.clear()
        mDatas.addAll(deliveryList)
        mListAdapter.notifyDataSetChanged()
    }

    fun showConfirmReceiptOrder() {
        val orderDetailItem = mViewModel.mOrderItemData.value?.data;
        orderDetailItem?.let { item ->
            val unSignNum = item.unSignNum ?: 0L
            if (unSignNum <= 0L) {
                throttleToast("没有待签收的记录")
                return@let
            }
            autoShowConfirmReceiptBottomSheet(
                confirmAction =  {
                    confirmReceiptOrder(item)
                },
                inFullScreenActivity = false,
                orderDetailItem = item
            )
        }

    }


    private fun showAmountNotEnough() {
        val confirmDialog = ConfirmDialog(
            mContext = this@ConfirmReceiptOrderActivity,
            title = "支付失败",
            message = "账户预付款余额不足，请充值后继续支付\n",
            confirmText = "去充值",
            onConfirm = {
                goChargeInputActivity();
            }
        );
        confirmDialog.show()
    }

    private fun showAmountNotEnoughWithErrorImage() {
        val confirmDialog = ConfirmImageDialog(
            mContext = this@ConfirmReceiptOrderActivity,
            title = "支付失败",
            message = "账户预付款余额不足，请充值后继续支付\n",
            confirmText = "去充值",
            onConfirm = {
                goChargeInputActivity();
            }
        );
        confirmDialog.show()
    }


    fun confirmReceiptOrder(item: OrderDetailItem){
        showLoadingDialogV2()
        val id  = item.id ?: 0
        val hasPay = ((item.extraPrice?.toDoubleOrNull() ?: 0.0) > 0);
        mViewModel.confirmOrderReceiveDone(id).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                if (hasPay) {
                    goConfirmReceiptOrderPayResultActivity();
                } else {
                    throttleToast("")
                }
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                finish()
            } else {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                if (5002 == it.code) { //账户余额不足
                    showAmountNotEnoughWithErrorImage();
                    return@observe
                }
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun submitConfirmOrder() {
        showLoadingDialog()
        val parameter = AddOrderActivity.getOrderParameter();
        mViewModel.createOrder(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                goSubmitOrderResultActivity();
                EventBusUtils.postEvent(SubmitBuyOrderDoneEvent(true))
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
            } else {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                if (5002 == it.code) { //账户余额不足
                    showAmountNotEnoughWithErrorImage();
                    return@observe
                }
                Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
            }
        }

    }


}