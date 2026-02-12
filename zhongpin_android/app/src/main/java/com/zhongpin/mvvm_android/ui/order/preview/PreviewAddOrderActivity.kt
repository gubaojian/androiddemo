package com.zhongpin.mvvm_android.ui.order.preview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPreviewAddOrderBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.AddressListItemResponse
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PreviewPurchaseOrderItem
import com.zhongpin.mvvm_android.bean.PreviewOrderResponse
import com.zhongpin.mvvm_android.bean.SubmitBuyOrderDoneEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChargeInputActivity
import com.zhongpin.mvvm_android.ui.common.goPayPurchaseOrderActivity
import com.zhongpin.mvvm_android.ui.common.goSubmitOrderResultActivity
import com.zhongpin.mvvm_android.ui.order.add.AddOrderActivity
import com.zhongpin.mvvm_android.ui.order.preview.detail.PreviewOrderDetailActivity
import com.zhongpin.mvvm_android.ui.order.preview.view.PriceDiscountDetailPopupView
import com.zhongpin.mvvm_android.view.dialog.ConfirmImageDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class PreviewAddOrderActivity : BaseVMActivity<PreviewAddOrderViewModel>() {


    private lateinit var mBinding: ActivityPreviewAddOrderBinding;

    private var mAddressItem: AddressListItemResponse? = null;


    private var mDatas: MutableList<PreviewPurchaseOrderItem> = mutableListOf()
    private lateinit var mListAdapter: PreviewOrderPurchaseItemListAdapter
    private var mPreviewOrderResponse: PreviewOrderResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPreviewAddOrderBinding.inflate(layoutInflater, container, false)
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

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mAddressItem = AddOrderActivity.shareAddressItem;
        showAddressItem(mAddressItem)


        mListAdapter = PreviewOrderPurchaseItemListAdapter(this, mDatas)
        mListAdapter.setOnItemClickListener { _,view, position ->
            val item = mDatas[position];
            val intent = Intent(this@PreviewAddOrderActivity, PreviewOrderDetailActivity::class.java)
            intent.putExtra("orderItem", item)
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
            XPopup.Builder(this@PreviewAddOrderActivity)
                .isDestroyOnDismiss(true)
                .atView(mBinding.bottomButtonsContainer)
                .isViewMode(true)
                .isClickThrough(true)
                .popupPosition(PopupPosition.Top)
                .asCustom(
                    PriceDiscountDetailPopupView(
                        this@PreviewAddOrderActivity,
                        mPreviewOrderResponse
                    )
                )
                .show()
        }

        mBinding.btnSubmit.setOnClickListener {
            showConfirmOrder()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mPreviewData.observe(this) { outerIt ->
            if (outerIt.success) {
                mPreviewOrderResponse = outerIt.data
                showSuccess(Constant.COMMON_KEY)
                showTotalPrice((mPreviewOrderResponse?.totalAmount ?: "").toString())
                val orderList = outerIt.data?.orders ?: emptyList()
                mDatas.clear()
                mDatas.addAll(orderList)
                mListAdapter.notifyDataSetChanged()
            } else {
                showError(outerIt.msg, Constant.COMMON_KEY)
            }
        }
    }


    override fun initData() {
        super.initData()
        sendPreviewOrder()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubmitOrderDoneEvent(event : SubmitBuyOrderDoneEvent) {
         finish()
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

    private fun sendPreviewOrder() {
        val parameter = AddOrderActivity.getOrderParameter();
        mViewModel.orderPreview(parameter)
    }


    private fun showAddressItem(item: AddressListItemResponse?) {
        mBinding.addressItemContainer.visibility = View.VISIBLE
        mBinding.detailAddress.text = item?.toShouHuoAddress()
        mBinding.name.text = item?.name ?: ""
        mBinding.contractPhone.text = item?.mobile ?: ""
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
        submitConfirmOrder() //无需确认框，直接提交
        /**
        val confirmDialog = ConfirmDialog(
            mContext = this@PreviewAddOrderActivity,
            title = "确认下单",
            message = "即将提交订单\n" +
                    "确认提交后将从企业账户中支付${mPreviewOrderResponse?.totalAmount ?: ""}元\n" +
                    "请再次核对订单详细信息",
            confirmText = "确认订单",
            onConfirm = {
                submitConfirmOrder()
            }
        );
        confirmDialog.show() */
    }

    private fun showAmountNotEnough() {
        val confirmDialog = ConfirmDialog(
            mContext = this@PreviewAddOrderActivity,
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
            mContext = this@PreviewAddOrderActivity,
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
        mViewModel.createOrderV6(parameter).observe(this) {
            dismissLoadingDialog()
            if (it.success) {
                //goSubmitOrderResultActivity();
                EventBusUtils.postEvent(SubmitBuyOrderDoneEvent(true))
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                goPayPurchaseOrderActivity(it.data?.id ?: 0)
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