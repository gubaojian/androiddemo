package com.zhongpin.mvvm_android.ui.order.purchaselist.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPayPurchaseOrderDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.convertTo
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhongpin.mvvm_android.bean.PreviewPurchaseOrderItem
import com.zhongpin.mvvm_android.bean.PurchaseOrderDetail
import com.zhongpin.mvvm_android.bean.SubmitBuyOrderDoneEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChargeInputActivity
import com.zhongpin.mvvm_android.ui.common.goSubmitOrderResultActivity
import com.zhongpin.mvvm_android.ui.common.showConfirmPayPurchaseBottomSheet
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.order.preview.detail.PreviewOrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.preview.view.PriceDiscountDetailPopupView
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.view.dialog.ConfirmImageDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PayPurchaseOrderDetailActivity : BaseVMActivity<PayPurchaseOrderDetailViewModel>() {


    private lateinit var mBinding: ActivityPayPurchaseOrderDetailBinding;


    private var mDatas: MutableList<OrderItem> = mutableListOf()
    private lateinit var mListAdapter: PayPurchaseOrderDetailItemListAdapter
    private var mPurchaseOrderResponse: PurchaseOrderDetail? = null

    private var purchaseOrderId:Long? = 0;

    private var title:String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        if (intent != null) {
            purchaseOrderId = intent.getLongExtra("purchaseOrderId", 0)
            title = intent.getStringExtra("title")
        }
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPayPurchaseOrderDetailBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)
        registerDefaultLoad(mBinding.body, Constant.COMMON_KEY)

        if (!TextUtils.isEmpty(title)) {
            mBinding.ivTitle.text = title
        }

        mBinding.ivBack.setOnClickListener {
            finish()
        }



        mListAdapter = PayPurchaseOrderDetailItemListAdapter(this, mDatas)
        mListAdapter.setOnItemClickListener { _,view, position ->
            val item = mDatas[position];
            val intent = Intent(this@PayPurchaseOrderDetailActivity, PreviewOrderDetailActivity::class.java)
            intent.putExtra("orderItem", item.convertTo(PreviewPurchaseOrderItem::class.java))
            startActivity(intent)
        }

        mBinding.orderListRecyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.orderListRecyclerView.adapter = mListAdapter


        UserInfoUtil.userInfo?.let {
            mBinding.userNick.text = it.nickName ?: ""
            mBinding.userPhone.text = it.mobile
        }

        mBinding.seePriceDetail.setOnClickListener {
            val close = findViewById<View?>(R.id.closePopup)
            if (close != null) {
                return@setOnClickListener
            }
            XPopup.Builder(this@PayPurchaseOrderDetailActivity)
                .isDestroyOnDismiss(true)
                .atView(mBinding.bottomButtonsContainer)
                .isViewMode(true)
                .isClickThrough(true)
                .popupPosition(PopupPosition.Top)
                .asCustom(
                    PriceDiscountDetailPopupView(
                        this@PayPurchaseOrderDetailActivity,
                        mPurchaseOrderResponse?.convertTo(PreviewOrderResponse::class.java)
                    )
                )
                .show()
        }

        mBinding.cancelPurchaseOrder.setOnClickListener {
            onCancelPay();
        }

        mBinding.btnSubmit.setOnClickListener {
            manualShowConfirmPayPurchaseBottomSheet()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPurchaseOrderDetailData.observe(this) { outerIt ->
            if (outerIt.success) {
                showSuccess(Constant.COMMON_KEY)
                mPurchaseOrderResponse = outerIt.data
                showPageData();
            } else {
                showError(outerIt.msg, Constant.COMMON_KEY)
            }
        }
    }


    override fun initData() {
        super.initData()
        getPurchaseOrderDetail()
    }


    override fun onDestroy() {
        mBinding.waitForPayText.stop()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubmitOrderDoneEvent(event : SubmitBuyOrderDoneEvent) {
         finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderItemInfoChangeEvent(event : OrderItemInfoChangeEvent) {
        if (event.change) {
            getPurchaseOrderDetail()
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

    private fun getPurchaseOrderDetail() {
        purchaseOrderId?.let {
            mViewModel.getPurchaseOrderDetail(purchaseOrderId ?: 0)
        }
    }


    fun showPageData() {
        showTotalPrice((mPurchaseOrderResponse?.totalAmount ?: "").toString())
        showAddressItem(mPurchaseOrderResponse)
        showBuyPersonInfo(mPurchaseOrderResponse)
        val orderList = mPurchaseOrderResponse?.orders ?: emptyList()
        mDatas.clear()
        mDatas.addAll(orderList)
        mListAdapter.notifyDataSetChanged()

        if (mPurchaseOrderResponse?.status == 0) {
            mBinding.btnSubmit.visible()
            mBinding.cancelPurchaseOrder.visible()
            val remainTime = PingDanAppUtils.getPurTimeRemain(mPurchaseOrderResponse?.purTime)
            if (remainTime > 0) {
                mBinding.waitForPayContainer.visible()
                mBinding.waitForPayText.setSuffix("后取消订单，请及时支付")
                mBinding.waitForPayText.setRemainTime(remainTime, {
                    EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                })
            } else {
                mBinding.waitForPayContainer.gone()
            }
            autoShowConfirmPayPurchaseBottomSheet()
        } else {
            mBinding.btnSubmit.gone()
            mBinding.cancelPurchaseOrder.gone()
            mBinding.waitForPayContainer.gone()
        }
        mBinding.ivTitle.text = mPurchaseOrderResponse?.statusDesc()


    }

    private fun showAddressItem(item: PurchaseOrderDetail?) {
        mBinding.addressItemContainer.visible()
        mBinding.detailAddress.text = item?.address
        mBinding.name.text = item?.receiveName ?: ""
        mBinding.contractPhone.text = item?.receiveMobile ?: ""
    }

    private fun showBuyPersonInfo(item: PurchaseOrderDetail?) {
        mBinding.userNick.text = item?.purName
        mBinding.userPhone.text = item?.purMobile ?: ""
    }

    private fun showTotalPrice(price: String?) {
        SpanUtils.with(mBinding.totalPrice)
            .append("￥")
            .setFontSize(14, true)
            .append(price ?: "")
            .setFontSize(18, true)
            .create()
    }

    private fun showConfirmOrder() {
        val confirmDialog = ConfirmDialog(
            mContext = this@PayPurchaseOrderDetailActivity,
            title = "确认下单",
            message = "即将提交订单\n" +
                    "确认提交后将从企业账户中支付${mPurchaseOrderResponse?.totalAmount ?: ""}元\n" +
                    "请再次核对订单详细信息",
            confirmText = "确认订单",
            onConfirm = {
                submitConfirmOrder()
            }
        );
        confirmDialog.show()
    }

    private fun showAmountNotEnough() {
        val confirmDialog = ConfirmDialog(
            mContext = this@PayPurchaseOrderDetailActivity,
            title = "支付失败",
            message = "账户预付款余额不足，请充值后继续支付\n",
            confirmText = "去充值",
            onConfirm = {
                goChargeInputActivity();
            }
        );
        confirmDialog.show()
    }

    var hasAutoShowConfirmPayPurchaseBottomSheet = false
    private fun autoShowConfirmPayPurchaseBottomSheet() {
        if (!hasAutoShowConfirmPayPurchaseBottomSheet) {
           hasAutoShowConfirmPayPurchaseBottomSheet = true
           manualShowConfirmPayPurchaseBottomSheet()
        }
    }

    private fun manualShowConfirmPayPurchaseBottomSheet() {
        showConfirmPayPurchaseBottomSheet(
            confirmAction = {
                doPay()
            },
            inFullScreenActivity = false,
            purchaseOrderDetail = mPurchaseOrderResponse
        );
    }

    private fun showAmountNotEnoughWithErrorImage() {
        val confirmDialog = ConfirmImageDialog(
            mContext = this@PayPurchaseOrderDetailActivity,
            title = "支付失败",
            message = "账户预付款余额不足，请充值后继续支付\n",
            confirmText = "去充值",
            onConfirm = {
                goChargeInputActivity();
            }
        );
        confirmDialog.show()
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

    private fun onCancelPay() {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "取消订购",
            message = HtmlCompat.fromHtml("<br/>确定取消订购吗？<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            cancelText = "确定取消",
            confirmText = "继续支付",
            onCancel = {
                doCancelPay();
            },
            onConfirm = {
                doPay()
            }
        );
        dialog.showDialog(this)
    }

    fun doCancelPay() {
        showLoadingDialogV2()
        mViewModel.cancelPurchaseOrder(mPurchaseOrderResponse?.id ?: 0).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

    fun doPay() {
        showLoadingDialogV2()
        mViewModel.payPurchaseOrder(mPurchaseOrderResponse?.id ?: 0).observe(this) {
            dismissLoadingDialogV2()
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