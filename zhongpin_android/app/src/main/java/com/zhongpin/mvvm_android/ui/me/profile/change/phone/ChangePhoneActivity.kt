package com.zhongpin.mvvm_android.ui.me.profile.change.phone

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityChangeUserPhoneBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil


class ChangePhoneActivity : BaseVMActivity<ChangePhoneViewModel>() {


    private lateinit var mBinding: ActivityChangeUserPhoneBinding;

    private lateinit var countDownTimer: CountDownTimer



    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChangeUserPhoneBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        val checkLoginButtonStatus = {
            if (mBinding.newPhone.text.trim().isNotEmpty()
                && mBinding.smsCode.text.trim().isNotEmpty()) {
                mBinding.btnSubmit.isEnabled = true;
            } else {
                mBinding.btnSubmit.isEnabled = false;
            }
        };

        mBinding.newPhone.addTextChangedListener(
            afterTextChanged = {
                checkLoginButtonStatus()
            }
        )

        mBinding.smsCode.addTextChangedListener(
            afterTextChanged = {
                checkLoginButtonStatus()
            }
        )

        countDownTimer =  object: CountDownTimer(60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val leftSecond = millisUntilFinished/1000;
                if (leftSecond > 0) {
                    mBinding.smsGetVerifyCode.text = "" + (leftSecond) + "秒后重发"
                }
            }

            override fun onFinish() {
                mBinding.smsGetVerifyCode.setEnabled(true)
                mBinding.smsGetVerifyCode.text = "获取验证码"
            }
        }

        mBinding.smsGetVerifyCode.setOnClickListener {
            if (mBinding.newPhone.text.isNotEmpty()) {
                mViewModel.sendVerifyCode(mBinding.newPhone.text.trim().toString()).observe(this){
                    if (it.success) {
                        mBinding.smsGetVerifyCode.setEnabled(false)
                        countDownTimer.start()
                    } else {
                        Toast.makeText(applicationContext,it.msg, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext,"请输入手机号", Toast.LENGTH_LONG).show()
            }
        }

        mBinding.btnSubmit.setOnClickListener {
            checkAndSubmit();
        }

        //registerDefaultLoad(mBinding.loadContainer, ApiService.COMMON_KEY)

    }




    override fun initDataObserver() {
        super.initDataObserver()
    }

    override fun initData() {
        super.initData()
    }


    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }


    private fun checkAndSubmit() {
        if (TextUtils.isEmpty(mBinding.newPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入新手机号", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.smsCode.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入验证码", Toast.LENGTH_LONG).show()
            return
        }
        submitForm();
    }


    private fun submitForm() {
        val parameter:HashMap<String,Any> = hashMapOf()
        val mobile = mBinding.newPhone.text.trim().toString()
        val code = mBinding.smsCode.text.trim().toString()
        UserInfoUtil.userInfo?.let {
            parameter["id"] = it.id.toString()
            parameter["mobile"] = mobile
            parameter["code"] = code
        }
        showLoadingDialogV2()
        mViewModel.updateUserInfo(parameter).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                UserInfoUtil.userInfo?.mobile = mobile
                EventBusUtils.postEvent(UserInfoChangeEvent(true))
                Toast.makeText(this, "手机号更新成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

}