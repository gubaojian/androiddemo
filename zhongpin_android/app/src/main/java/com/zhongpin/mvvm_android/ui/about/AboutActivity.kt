package com.zhongpin.mvvm_android.ui.about

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.AppUtils
import com.gyf.immersionbar.ImmersionBar
import com.zhilianshidai.pindan.app.BuildConfig
import com.zhilianshidai.pindan.app.databinding.ActivityAboutBinding
import com.zhilianshidai.pindan.app.databinding.ActivityNotifySettingBinding
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.network.ApiService
import com.zhongpin.mvvm_android.ui.common.goWebActivity
import com.zhongpin.mvvm_android.ui.common.showContractKeFuDialog
import com.zhongpin.mvvm_android.ui.debug.DebugActivity


class AboutActivity : BaseVMActivity<AboutViewModel>() {


    private lateinit var mBinding: ActivityAboutBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityAboutBinding.inflate(layoutInflater, container, false)
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

        mBinding.versionName.text =  AppUtils.getAppVersionName()

        if (BuildConfig.DEBUG) {
            mBinding.versionName.setOnClickListener {
                val intent = Intent(this, DebugActivity::class.java)
                startActivity(intent)
            }
        }

        mBinding.serviceTerm.setOnClickListener {
            goWebActivity(
                title = "服务条款",
                url = Constant.SERVICE_TERM_URL
            )
        }

        mBinding.privateTerm.setOnClickListener {
            goWebActivity(
                title = "隐私政策",
                url = Constant.PRIVATE_TERM_URL
            )
        }

        mBinding.contract.text = "${Constant.CONTRACT_KEFU_PHONE_NUM}"
        mBinding.contractTerm.setOnClickListener {
            showContractKeFuDialog()
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

}