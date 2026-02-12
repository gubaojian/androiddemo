package com.zhongpin.mvvm_android.ui.pay.chargeinput

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest
import com.zhongpin.mvvm_android.network.showLoadingState

class ChargeInputViewModel  : BaseViewModel<ChargeInputRepository>() {

    var mUserAuthInfoData: MutableLiveData<BaseResponse<UserInfoAuthResponse>> = MutableLiveData()

    fun getUserAuthInfoData() {
        initiateRequest({
            mUserAuthInfoData.value = mRepository.getUserAuthInfo()
        }, loadState)
    }


    fun deleteEntInfoAuth(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteEntInfoAuth(id);
        }, loadState)
        return mLiveData
    }

    fun addChargeInput(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<PayItem>> {
        val mLiveData: MutableLiveData<BaseResponse<PayItem>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.addChargeInput(parameters);
        }, loadState)
        return mLiveData
    }

    var mWaitPayData: MutableLiveData<BaseResponse<List<PayItem>>> = MutableLiveData()
    fun getWaitPayList() {
        initiateRequest({
            mWaitPayData.value = mRepository.getWaitPayList(0, hashMapOf()).showLoadingState(loadState)
        }, loadState)
    }
}