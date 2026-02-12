package com.zhongpin.mvvm_android.ui.setpwd

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class FindSetPwdViewModel  : BaseViewModel<FindSetPwdRepository>() {

    var mBannerData: MutableLiveData<List<UserInfoResponse>> = MutableLiveData()

    fun loadBannerCo() {
        initiateRequest({
            mBannerData.value = mRepository.loadBannerCo()
        }, loadState)
    }


    fun setPassword(mobile:String, password:String, code:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.setPassword(mobile, password, code);
        }, loadState)
        return mLoginData
    }


}