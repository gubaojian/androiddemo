package com.zhongpin.mvvm_android.ui.order.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityOrderDetailBinding
import com.zhongpin.lib_base.ktx.gone
import com.zhongpin.lib_base.ktx.visible
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.OrderDetailItem
import com.zhongpin.mvvm_android.bean.OrderItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.autoFeedback
import com.zhongpin.mvvm_android.ui.common.autoGoFeedbackWhenDoneConfirmReceiveAction
import com.zhongpin.mvvm_android.ui.common.goBuyOrderAgainWithOrderDetail
import com.zhongpin.mvvm_android.ui.common.goConfirmReceiptOrderActivity
import com.zhongpin.mvvm_android.ui.common.goDeliveryProfListPage
import com.zhongpin.mvvm_android.ui.common.goOrderFeedbackList
import com.zhongpin.mvvm_android.ui.common.showConfirmCancelOrderDialog
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.view.ext.setCancelButtonShow
import com.zhongpin.mvvm_android.ui.view.ext.setConfirmOrderButtonShow
import com.zhongpin.mvvm_android.ui.view.ext.setFeedbackButtonShow
import com.zhongpin.mvvm_android.ui.view.ext.setLineTypeText
import com.zhongpin.mvvm_android.ui.view.ext.setPaperSizeText
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@EventBusRegister
class OrderDetailActivity : BaseVMActivity<OrderDetailViewModel>() {


    private lateinit var mBinding: ActivityOrderDetailBinding;

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
        mBinding = ActivityOrderDetailBinding.inflate(layoutInflater, container, false)
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

        val item = orderListItem;
        if (item == null) {
            Toast.makeText(applicationContext,"请传入订单详情信息", Toast.LENGTH_LONG).show()
            return;
        }


        mBinding.faHuoInfo.setOnClickListener {
            if (mBinding.seeFaHuoRecord.isVisible) {
                val orderDetailItem = mViewModel.mOrderDetailItemData.value?.data;
                orderDetailItem?.let {
                    goDeliveryProfListPage(it)
                }
            }
        }



        mBinding.seeFeedback.setOnClickListener {
            goOrderFeedbackList(orderListItem?.id ?: 0)
        }

        mBinding.copyOrderNum.setOnClickListener {
            ClipboardUtils.copyText(mBinding.orderNo.text)
            Toast.makeText(applicationContext,"复制成功", Toast.LENGTH_LONG).show()
        }

        mBinding.unitPriceContainer.setOnClickListener {
            if (mBinding.unitPriceDetail.isVisible)  {
                mBinding.unitPriceDetail.visibility = View.GONE
                mBinding.unitPriceDetailArrow.setImageResource(R.mipmap.order_detail_arrow_up)
            } else {
                mBinding.unitPriceDetailArrow.setImageResource(R.mipmap.order_detail_arrow_down)
                mBinding.unitPriceDetail.visibility = View.VISIBLE
            }
        }

        mBinding.totalPriceContainer.setOnClickListener {
            if (mBinding.totalPriceDetail.isVisible)  {
                mBinding.totalPriceDetail.visibility = View.GONE
                mBinding.totalPriceDetailArrow.setImageResource(R.mipmap.order_detail_arrow_up)
            } else {
                mBinding.totalPriceDetailArrow.setImageResource(R.mipmap.order_detail_arrow_down)
                mBinding.totalPriceDetail.visibility = View.VISIBLE
            }
        }

        mBinding.feedback.setOnClickListener {
            orderListItem?.let {
                autoFeedback(it, {
                    confirmReceiptOrder(it);
                })
            }
        }

        mBinding.buyAgain.setOnClickListener {
            mViewModel.mOrderDetailItemData.value?.data?.let {
                goBuyOrderAgainWithOrderDetail(it);
            }
        }

        mBinding.cancelOrder.setOnClickListener {
            showConfirmCancelOrderDialog {
                cancelOrder( orderListItem?.id ?: 0)
            }
        }

        mBinding.confirmOrder.setOnClickListener {
            orderListItem?.let { item ->
                goConfirmReceiptOrderActivity(item)
                /**
                showConfirmReceiptDialog(it, {
                    confirmReceiptOrder(it);
                })*/
            }
        }

        mBinding.ivTitle.text = item.orderStatusName ?: mBinding.ivTitle.text
        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)


    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mOrderDetailItemData.observe(this) {
            if (it.success && it.data != null) {
                showSuccess(Constant.COMMON_KEY)
                it.data?.let {
                    showOrderDetail(it)
                }
            } else {
                showError(it.msg, Constant.COMMON_KEY)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getOrderDetail(orderListItem?.id ?: 0)
    }

    private fun showOrderDetail(item: OrderDetailItem) {
        val appealCount = item.appealCount ?: 0;
        if (appealCount > 0) {
            mBinding.seeFeedback.visible()
        } else {
            mBinding.seeFeedback.gone()
        }


        mBinding.ivTitle.text = item.orderStatusName ?: mBinding.ivTitle.text
        mBinding.orderNo.text = item.orderNo

        mBinding.orderShouHuoPersonName.text = item.receiveName ?: ""
        mBinding.orderShouHuoPhone.text = item.receiveMobile ?: ""
        mBinding.orderShouHuoAddress.text =item.address ?: ""


        if (TextUtils.isEmpty(item.preCode)) {
            mBinding.faHuoPlatformMaterialCode.text = item.platCode ?: ""
        } else {
            mBinding.faHuoPlatformMaterialCode.text = "${item.platCode ?: ""}（下单材质：${item.preCode}）"
        }

        mBinding.faHuoMaterialCode.text = item.entCode ?: ""
        mBinding.faHuoCompany.text = item.providerName ?: ""
        val num = item.num ?: 0;
        val signNum = item.signNum ?: 0
        mBinding.signAmount.text = signNum.toString()
        mBinding.stockAmount.text = item.receiptNum ?: "0"


        val deliverCount = item.deliverCount ?: 0;
        mBinding.deliveryAmount.text =  deliverCount.toString()
        if (deliverCount > 0) {
            mBinding.faHuoInfo.visible()
            mBinding.seeFaHuoRecord.visible()
        } else {
            mBinding.faHuoInfo.gone()
            mBinding.seeFaHuoRecord.gone()
        }


        if (!TextUtils.isEmpty(item.signTime)) {
            mBinding.allFaHuoSuccessContainer.visible()
            mBinding.allFaHuoSuccessDate.text =  item.signTime ?: ""
            mBinding.signInfo.visible()
        } else {
            mBinding.allFaHuoSuccessContainer.gone()
            mBinding.signInfo.gone()
        }

        if (TextUtils.isEmpty(item.preCode)) {
            mBinding.orderMaterialCode.text = item.platCode ?: ""
        } else {
            mBinding.orderMaterialCode.text = "${item.platCode ?: ""}（下单材质：${item.preCode}）"
        }

        mBinding.waCode.text = item.lenType ?: ""
        mBinding.paperSize.setPaperSizeText(item)
        mBinding.lineDesc.setLineTypeText(item)
        mBinding.orderAmount.text = num.toString()
        mBinding.jiaoHuoDate.text =  PingDanAppUtils.getDateDayHHMM(item.demandTime ?: "")


        SpanUtils.with( mBinding.unitPrice)
            .append("￥")
            .append(item.areaPrice ?: "0.0")
            .create()

        SpanUtils.with( mBinding.unitMaterialAreaPrice)
            .append("￥")
            .append(item.materialPrice ?: "0.0")
            .create()

        SpanUtils.with( mBinding.unitCutPrice)
            .append("￥")
            .append(item.cutPrice ?: "0.0")
            .create()

        SpanUtils.with( mBinding.unitStepPrice)
            .append("￥")
            .append(item.stepPrice ?: "0.0")
            .create()

        SpanUtils.with( mBinding.totalMaterialPrice)
            .append("￥")
            .append(item.materialTotalPrice ?: "0.0")
            .create()

        SpanUtils.with( mBinding.totalCutPrice)
            .append("￥")
            .append(item.cutTotalPrice?: "0.0")
            .create()

        SpanUtils.with( mBinding.totalStepPrice)
            .append("￥")
            .append(item.stepTotalPrice ?: "0.0")
            .create()


        SpanUtils.with( mBinding.totalPrice)
            .append("￥")
            .append(item.totalPrice ?: "0.0")
            .create()


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



        mBinding.buyDate.text = item.orderTime ?: ""
        mBinding.buyPersonName.text = item.purName ?: ""
        mBinding.buyPersonPhone.text = item.purMobile ?: ""





        mBinding.payDesc.text = "预付款扣款"
        mBinding.payTradeNo.text = item.tradeNo


        mBinding.cancelOrder.setCancelButtonShow(item)
        mBinding.feedback.setFeedbackButtonShow(item)
        mBinding.confirmOrder.setConfirmOrderButtonShow(item)

        if(TextUtils.isEmpty(item.remark)) {
            mBinding.remarkInfo.visible()
            mBinding.remark.text = "无"
        } else {
            mBinding.remarkInfo.visible()
            mBinding.remark.text = item.remark
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshUser(infoEvent : OrderItemInfoChangeEvent){
        if (infoEvent.change) {
            mViewModel.getOrderDetail(orderListItem?.id ?: 0)
        }
    }


    /**
     * https://space-64stfp.w.eolink.com/home/api-studio/inside/p346wIBec8b27b4efe594eed716ffa6a4764f833feb25fd/api/3240503/detail/55987259?spaceKey=space-64stfp
     * */
    private fun confirmReceiptOrder(item: OrderItem){
        showLoadingDialogV2()
        val id  = item.id ?: 0
        mViewModel.confirmOrderReceiveDone(id).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                autoGoFeedbackWhenDoneConfirmReceiveAction?.invoke();
            } else {
                autoGoFeedbackWhenDoneConfirmReceiveAction = null;
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

    fun cancelOrder(id: Long) {
        showLoadingDialogV2()
        mViewModel.cancelOrder(id).observe(this) {
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



    override fun onDestroy() {
        super.onDestroy()
    }


}