package com.zhongpin.mvvm_android.ui.mine.company.profile.change

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityChangeCompanyContractBinding
import com.zhilianshidai.pindan.app.databinding.ActivityChangeUserNickBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.utils.MobileUtil
import com.zhongpin.mvvm_android.ui.view.ext.setAutoInputTips
import com.zhongpin.mvvm_android.ui.view.ext.setAutoUpperCase


class ChangeCompanyContractActivity : BaseVMActivity<ChangeCompanyContractViewModel>() {


    private lateinit var mBinding: ActivityChangeCompanyContractBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChangeCompanyContractBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }


        mBinding.contractName.setAutoInputTips(mBinding.contractNameInputTip)
        mBinding.contractPhone.setAutoInputTips(mBinding.contractPhoneInputTip)

        UserInfoUtil.companyInfo?.let {
            mBinding.contractName.text.clear()
            mBinding.contractName.text.append(it.contactPeople ?: "")
            mBinding.contractPhone.text.clear()
            mBinding.contractPhone.text.append(it.contactTel ?: "")
        }

        mBinding.save.setOnClickListener {
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
        super.onDestroy()
    }


    private fun checkAndSubmit() {
        if (TextUtils.isEmpty(mBinding.contractName.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入姓名", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(mBinding.contractPhone.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入联系电话", Toast.LENGTH_LONG).show()
            return
        }
        val phone = mBinding.contractPhone.text.trim().toString();
        if (!MobileUtil.checkMobile(phone)) {
            Toast.makeText(applicationContext,"请输入合法手机号", Toast.LENGTH_LONG).show()
            return
        }
        submitForm();
    }


    private fun submitForm() {
        val parameter:HashMap<String,Any> = hashMapOf()
        val name = mBinding.contractName.text.trim().toString();
        val phone = mBinding.contractPhone.text.trim().toString();
        UserInfoUtil.companyInfo?.let {
            parameter["id"] = it.id.toString()
            parameter["contactPeople"] = name
            parameter["contactTel"] = phone
        }
        showLoadingDialogV2()
        mViewModel.updateEntContract(parameter).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                UserInfoUtil.companyInfo?.contactPeople = name
                UserInfoUtil.companyInfo?.contactTel = phone
                EventBusUtils.postEvent(CompanyInfoChangeEvent(true))
                Toast.makeText(this, "企业联系人更新成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

}