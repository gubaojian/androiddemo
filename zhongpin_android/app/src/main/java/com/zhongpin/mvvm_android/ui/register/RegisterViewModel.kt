package com.zhongpin.mvvm_android.ui.register

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.LoginResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class RegisterViewModel  : BaseViewModel<RegisterRepository>() {

    var mBannerData: MutableLiveData<List<UserInfoResponse>> = MutableLiveData()

    fun loadBannerCo() {
        initiateRequest({
            mBannerData.value = mRepository.loadBannerCo()
        }, loadState)
    }

    fun login(mobile:String, password:String, code: String) : MutableLiveData<BaseResponse<LoginResponse>> {
        val mLoginData: MutableLiveData<BaseResponse<LoginResponse>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.login(mobile, password, code);
        }, loadState)
        return mLoginData
    }

    fun sendVerifyCode(mobile:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.sendVerifyCode(mobile);
        }, loadState)
        return mLoginData
    }

    fun register(mobile:String, password:String, code:String, nick:String, companyName:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.register(mobile, password, code, nick, companyName);
        }, loadState)
        return mLoginData
    }


}