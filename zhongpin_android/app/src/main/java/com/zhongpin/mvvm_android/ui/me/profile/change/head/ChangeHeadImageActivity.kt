package com.zhongpin.mvvm_android.ui.me.profile.change.head

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.AppUtils
import com.gyf.immersionbar.ImmersionBar
import com.sum.glide.setHeaderImage
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.databinding.ActivityAboutBinding
import com.zhilianshidai.pindan.app.databinding.ActivityChangeHeadImageBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goWebActivity
import com.zhongpin.mvvm_android.ui.common.previewHeaderImageActivity
import com.zhongpin.mvvm_android.ui.common.showImagePicker
import com.zhongpin.mvvm_android.ui.common.showImagePickerWithCrop
import com.zhongpin.mvvm_android.ui.debug.DebugActivity


class ChangeHeadImageActivity : BaseVMActivity<ChangeHeadImageViewModel>() {


    private lateinit var mBinding: ActivityChangeHeadImageBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityChangeHeadImageBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        mBinding.headerImage.setHeaderImage(
            UserInfoUtil.userInfo?.headImage
        )

        mBinding.headerImage.setOnClickListener {
            previewHeaderImageActivity(UserInfoUtil.userInfo?.headImage)
        }

        mBinding.btnSubmit.setOnClickListener {
            showImagePickerWithCrop {
                    if (it.isEmpty()) {
                        return@showImagePickerWithCrop
                    }
                    changeHeadImage(it[0])
            }
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

    private fun changeHeadImage(filePath: String) {
        showLoadingDialogV2()
        mViewModel.uploadImage(filePath).observe(this) {

            if (it.success) {
                updateUserInfo(it.data ?: "")
            } else {
                dismissLoadingDialogV2()
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    private fun updateUserInfo(url: String) {
        val parameter:HashMap<String,Any> = hashMapOf()
        UserInfoUtil.userInfo?.let {
            parameter["id"] = it.id.toString()
            parameter["headImage"] = url
        }
        mViewModel.updateUserInfo(parameter).observe(this) {
            dismissLoadingDialogV2()
            if (it.success) {
                UserInfoUtil.userInfo?.headImage = url
                mBinding.headerImage.setHeaderImage(url)
                EventBusUtils.postEvent(UserInfoChangeEvent(true))
                Toast.makeText(this, "头像上传成功", Toast.LENGTH_SHORT).show()
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

}