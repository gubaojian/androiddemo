package com.zhongpin.mvvm_android.ui.me.profile.change.nick

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.databinding.ActivityChangeUserNickBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.showImagePickerWithCrop
import com.zhongpin.mvvm_android.ui.view.ext.setAutoInputTips


class ChangeNickActivity : BaseVMActivity<ChangeNickViewModel>() {


    private lateinit var mBinding: ActivityChangeUserNickBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChangeUserNickBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.userNick.setAutoInputTips(mBinding.userNickInputTip)

        UserInfoUtil.userInfo?.let {
            mBinding.userNick.text.clear()
            mBinding.userNick.text.append(it.nickName ?: "")
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
        if (TextUtils.isEmpty(mBinding.userNick.text.trim().toString())) {
            Toast.makeText(applicationContext,"请输入昵称", Toast.LENGTH_LONG).show()
            return
        }
        submitForm();
    }


    private fun submitForm() {
        val parameter:HashMap<String,Any> = hashMapOf()
        val nickName = mBinding.userNick.text.trim().toString()
        UserInfoUtil.userInfo?.let {
            parameter["id"] = it.id.toString()
            parameter["nickName"] = nickName
        }
        showLoadingDialogV2()
        mViewModel.updateUserInfo(parameter).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                UserInfoUtil.userInfo?.nickName = nickName
                EventBusUtils.postEvent(UserInfoChangeEvent(true))
                Toast.makeText(this, "昵称更新成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

}