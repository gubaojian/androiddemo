package com.zhongpin.mvvm_android.ui.pay.chargeinput

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityPayChargeInputBinding
import com.zhongpin.lib_base.ktx.limit2Decimal
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.PayItemInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChargePayActivity
import com.zhongpin.mvvm_android.ui.utils.PingDanAppUtils


class ChargeInputActivity : BaseVMActivity<ChargeInputViewModel>() {


    private lateinit var mBinding: ActivityPayChargeInputBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true)
            .fullScreen(false).keyboardEnable(true).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityPayChargeInputBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        mViewModel.loadState.observe(this, {
            dismissLoadingDialog()
        })
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.moneyAmount.limit2Decimal()

        mBinding.ivBack.setOnClickListener { finish() }

        val checkLoginButtonStatus = {
            if (mBinding.moneyAmount.text.trim().isNotEmpty()
                && mBinding.moneyAmount.text.trim().isNotEmpty()) {
                mBinding.btnSubmit.isEnabled = true;
            } else {
                mBinding.btnSubmit.isEnabled = false;
            }
        };

        mBinding.moneyAmount.addTextChangedListener {
            checkLoginButtonStatus();
        }

        //mBinding.moneyAmount.setMoneyInputFormat()

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit();
        }


        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mWaitPayData.observe(this) {
            if (it.success) {
                showSuccess(Constant.COMMON_KEY)
                val waitPayList = it.data ?: emptyList();
                if (waitPayList.isNotEmpty()) {
                    goChargePayActivity(waitPayList[0]);
                    finish()
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getWaitPayList()
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

    fun  checkAndSubmit() {
        if (TextUtils.isEmpty(mBinding.moneyAmount.text.toString())) {
            Toast.makeText(applicationContext,"请输入充值金额", Toast.LENGTH_LONG).show()
            return
        }
        if (!PingDanAppUtils.priceValid(mBinding.moneyAmount.text.toString())) {
            Toast.makeText(applicationContext,"充值金额最小为分", Toast.LENGTH_LONG).show()
            return
        }
        val amount = mBinding.moneyAmount.text.toString().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(applicationContext,"请输入合法充值金额", Toast.LENGTH_LONG).show()
            return
        }

        val maxAmount = 99999999.99
        if ( amount > maxAmount) {
            Toast.makeText(applicationContext,"充值金额不能超过99999999.99", Toast.LENGTH_LONG).show()
            return
        }

        //if (amount > 5*10000) {
            //Toast.makeText(applicationContext,"单笔充值最多5万", Toast.LENGTH_LONG).show()
            //return
        //}

        submitFormInfo()
    }

    fun submitFormInfo() {
        showLoadingDialogV2()
        val parameter:HashMap<String,Any> = hashMapOf()
        parameter["amount"] = mBinding.moneyAmount.text.trim().toString()
        mViewModel.addChargeInput(parameter).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                EventBusUtils.postEvent(PayItemInfoChangeEvent(true))
                it?.data?.let {
                    goChargePayActivity(it);
                }
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                })
            }
        }
    }

}