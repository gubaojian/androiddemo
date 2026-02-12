package com.zhongpin.mvvm_android.ui.mine.company.profile

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.sum.glide.setHeaderImage
import com.zhilianshidai.pindan.app.databinding.ActivityCompanyProfileBinding
import com.zhongpin.lib_base.utils.EventBusRegister
import com.zhongpin.lib_base.view.LoadingDialog
import com.zhongpin.mvvm_android.base.ext.ToastExt
import com.zhongpin.mvvm_android.base.view.BaseVMActivity
import com.zhongpin.mvvm_android.bean.CompanyInfoChangeEvent
import com.zhongpin.mvvm_android.bean.CompanyListItemResponse
import com.zhongpin.mvvm_android.bean.UserInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.common.utils.Constant
import com.zhongpin.mvvm_android.common.utils.StatusBarUtil
import com.zhongpin.mvvm_android.ui.common.goChangeCompanyContractActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@EventBusRegister
class CompanyProfileActivity : BaseVMActivity<CompanyProfileViewModel>() {


    private lateinit var mBinding: ActivityCompanyProfileBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).fullScreen(false).init()
        super.onCreate(savedInstanceState)
    }


    override fun createContentViewByBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = ActivityCompanyProfileBinding.inflate(layoutInflater, container, false)
        val view = mBinding.root
        return view
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setMargin(this, mBinding.content)

        mBinding.ivBack.setOnClickListener { finish() }

        showCompanyInfo(UserInfoUtil.companyInfo)

        mBinding.editCompanyContract.setOnClickListener {
            goChangeCompanyContractActivity()
        }

        registerDefaultLoad(mBinding.loadContainer, Constant.COMMON_KEY)

    }

    override fun initDataObserver() {
        super.initDataObserver()
        mViewModel.mCompanyLiveData.observe(this) {
            if (it.success) {
                showCompanyInfo(it.data)
            } else {
                ToastExt.throttleToast(it.msg, {
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getCompanyInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCompanyInfoChangeEvent(event : CompanyInfoChangeEvent){
        if (event.isChange) {
            showCompanyInfo(UserInfoUtil.companyInfo)
            mViewModel.getCompanyInfo()
        }
    }



    fun showCompanyInfo(userInfo: CompanyListItemResponse?) {
        userInfo?.let {
           mBinding.companyName.text = it.companyName
           mBinding.registerAddress.text = it.registerAddress
           mBinding.companyFaRen.text = it.legal

           mBinding.companyContractName.text = it.contactPeople
           mBinding.companyContractPhone.text = it.contactTel

           mBinding.areaManagerName.text = it.regionManager
           mBinding.areaManagerPhone.text = it.regionMobile

        }
    }

}