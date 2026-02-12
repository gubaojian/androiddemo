package com.zhongpin.mvvm_android.ui.pay.chargepay

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.alipay.sdk.app.EnvUtils
import com.blankj.utilcode.util.SpanUtils
import com.gyf.immersionbar.ImmersionBar
import com.king.pay.alipay.AliPay
import com.king.pay.wxpay.WXPay
import com.tencent.mm.opensdk.modelpay.PayReq
import com.zhilianshidai.pindan.app.R
import com.zhilianshidai.pindan.app.databinding.ActivityPayChargePayBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.convertTo
import com.zhongpin.lib_base.view.ConfirmDialog
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.PayUrlItem
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.throttleToast
import com.zhongpin.mvvm_android.ui.utils.ShareParamDataUtils
import com.zhongpin.mvvm_android.ui.utils.WXAPIUtil


class ChargePayActivity : BaseVMActivity<ChargePayViewModel>() {


    private lateinit var mBinding: ActivityPayChargePayBinding;

    private val payViaAlipay = "payViaAlipay";
    private val payViaWeChat = "payViaWeChat";

    private var payMethod = payViaAlipay

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
        mBinding = ActivityPayChargePayBinding.inflate(layoutInflater, container, false)
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
            Toast.makeText(applicationContext,"请传入支付详情信息", Toast.LENGTH_LONG).show()
            return;
        }

        payItem?.let {
            mBinding.waitForTime.setPrefix("待付款剩余时间：")
            mBinding.waitForTime.setRemainTime(it.expireTime ?: 0, {
                EventBusUtils.postEvent(PayItemInfoChangeEvent(true));
                finish()
            })
            SpanUtils.with( mBinding.payAmount)
                .append("￥")
                .setFontSize(16, true)
                .append(it.amount ?: "0")
                .setFontSize(36, true)
                .create()
        }

        mBinding.payViaWeChatContainer.setOnClickListener {
            mBinding.checkboxAlipay.setImageResource(R.mipmap.pay_item_un_checked)
            mBinding.checkboxWeChat.setImageResource(R.mipmap.pay_item_checked)
            payMethod = payViaWeChat
        }



        mBinding.payViaAlipayContainer.setOnClickListener {
            mBinding.checkboxAlipay.setImageResource(R.mipmap.pay_item_checked)
            mBinding.checkboxWeChat.setImageResource(R.mipmap.pay_item_un_checked)
            payMethod = payViaAlipay
        }

        mBinding.cancelPay.setOnClickListener {
            onCancelPay();
        }

        mBinding.btnSubmit.setOnClickListener {
            doCheckAndPay()
        }


        //registerDefaultLoad(mBinding.loadContainer, ApiService.COMMON_KEY)

    }



    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
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


    private fun onCancelPay() {
        val dialog = ConfirmDialog(
            mContext = this,
            title = "取消支付",
            message = HtmlCompat.fromHtml("<br/>确定取消支付吗？<br/><br/>", HtmlCompat.FROM_HTML_MODE_LEGACY),
            cancelText = "确定取消",
            confirmText = "继续支付",
            onCancel = {
                doCancelPay();
            },
            onConfirm = {
                doCheckAndPay()
            }
        );
        dialog.showDialog(this)
    }

    fun doCancelPay() {
        showLoadingDialogV2()
        mViewModel.cancelPayItem(payId).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(PayItemInfoChangeEvent(true))
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

    fun doCheckAndPay() {
        val supportMethod = payViaWeChat.equals(payMethod) || payViaAlipay.equals(payMethod);
        if (!supportMethod) {
            Toast.makeText(applicationContext, "暂时不支持$payMethod", Toast.LENGTH_LONG).show()
            return
        }

        showLoadingDialogV2()
        val params = hashMapOf<String, Any>()
        params["applicationType"] = 1;
        params["deviceType"] = 1
        params["id"] = payId; //移动应用

        params["payType"] = 0;
        if (payViaWeChat.equals(payMethod)) {
            params["payType"] = 1;
        }
        mViewModel.getPayUrl(params).observe(this) {
            dismissLoadingDialogV2()
            if (it.success && it.data != null) {
                if (params["payType"] == 0) {
                    //支付宝用payUrl字段
                    if (TextUtils.isEmpty(it.data?.payUrl)) {
                        throttleToast("支付链接异常")
                        return@observe
                    }
                    it.data?.let { payUrl ->
                        payViaAlipay(payUrl)
                    }
                } else if (params["payType"] == 1) {
                    it.data?.let { payUrl ->
                        payViaWeChat(payUrl)
                    }
                }

            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

    fun payViaAlipay(item: PayUrlItem) {
        val aliPay = AliPay(this);
        aliPay.setOnPayListener {
            if (it.isSuccess) {
                throttleToast(it.memo ?: "支付异常")
                checkPayStatus()
            } else {
                checkPayStatus()
                throttleToast(it.memo ?: "支付异常")
            }
        }
        val orderInfo = item.payUrl;
        orderInfo?.let {
            aliPay.sendReq(orderInfo)
        }
    }

    fun payViaWeChat(result: PayUrlItem) {
        var item: PayUrlItem? = result
        if (TextUtils.isEmpty(item?.appId)) {
            item = result.payUrl?.convertTo(PayUrlItem::class.java)
        }

        if(item == null || TextUtils.isEmpty(item?.appId)) {
            throttleToast("数据格式错误")
            return
        }

        val wxPay = WXPay(this);
        wxPay.setOnPayListener {
            if (it.isSuccess) {
                checkPayStatus()
            }else {
                checkPayStatus()
                throttleToast(it.message ?: "支付异常")
            }
        }
        val req = PayReq();
        req.appId = item.appId
        req.partnerId = item.partnerId
        req.prepayId = item.prepayId
        req.packageValue = item.packageValue
        req.nonceStr = item.nonceStr
        req.timeStamp = item.timeStamp
        req.sign = item.sign
        wxPay.sendReq(req)
    }

    fun checkPayStatus() {
        showLoadingDialogV2()
        mViewModel.getPayStatus(payId).observe(this) {
            dismissLoadingDialogV2()
            EventBusUtils.postEvent(PayItemInfoChangeEvent(true))
            if (it.success) {
                if(it.data?.status != 0) { //不是待支付
                    finish()
                }
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }
}