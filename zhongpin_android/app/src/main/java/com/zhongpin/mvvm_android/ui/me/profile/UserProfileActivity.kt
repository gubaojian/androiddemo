package com.zhongpin.mvvm_android.ui.me.profile

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.sum.glide.setHeaderImage
import com.zhilianshidai.pindan.app.databinding.ActivityUserProfileBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.login.LoginUtils
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChangeHeadImageActivity
import com.zhongpin.mvvm_android.ui.common.goChangeUserNickActivity
import com.zhongpin.mvvm_android.ui.common.goChangeUserPhoneTipActivity
import com.zhongpin.mvvm_android.ui.common.loginOut
import com.zhongpin.mvvm_android.ui.common.loginOutManual
import com.zhongpin.mvvm_android.ui.common.showConfirmLoginOutDialog
import com.zhongpin.mvvm_android.ui.common.showConfirmUpdatePwdDialog
import com.zhongpin.mvvm_android.ui.common.throttleToast
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@EventBusRegister
class UserProfileActivity : BaseVMActivity<UserProfileViewModel>() {


    private lateinit var mBinding: ActivityUserProfileBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityUserProfileBinding.inflate(layoutInflater, container, false)
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

        mBinding.headerContainer.setOnClickListener {
            goChangeHeadImageActivity()
        }

        mBinding.userNickContainer.setOnClickListener {
            goChangeUserNickActivity()
        }

        mBinding.phoneContainer.setOnClickListener {
            goChangeUserPhoneTipActivity()
        }

        mBinding.resetPassword.setOnClickListener {
            showConfirmUpdatePwdDialog();
        }

        mBinding.loginOut.setOnClickListener {
            showConfirmLoginOutDialog({
                doLoginOutInUserProfile();
            })
        }

        showUserInfo(UserInfoUtil.userInfo)

        //registerDefaultLoad(mBinding.loadContainer, ApiService.COMMON_KEY)

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mUserInfoData.observe(this) {
            if (it.success) {
                showUserInfo(it.data)
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getUserInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserInfoChangeEvent(event : UserInfoChangeEvent){
        if (event.isChange) {
            showUserInfo(UserInfoUtil.userInfo)
            mViewModel.getUserInfo()
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

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showUserInfo(userInfo: UserInfoResponse?) {
        userInfo?.let {
            if (!TextUtils.isEmpty(it.nickName)) {
                mBinding.userNick.text = it.nickName
                mBinding.userNick.visibility = View.VISIBLE;
            } else {
                mBinding.userNick.visibility = View.GONE;
            }
            mBinding.phone.text = UserInfoUtil.maskPhone(it.mobile)
            mBinding.headerImage.setHeaderImage(it.headImage)
        }
    }

    fun doLoginOutInUserProfile() {
        val token = LoginUtils.token();
        if (token.isNotEmpty()) {
            mViewModel.loginOut(token).observe(this) {
                if (it.success) {
                    loginOutManual()
                } else {
                    throttleToast(it.msg)
                }
            }
        } else {
           loginOut()
        }
    }

}