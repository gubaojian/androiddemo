package com.zhongpin.mvvm_android.ui.pay.chargepay

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayOrderStatus
import com.zhongpin.mvvm_android.bean.PayUrlItem
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class ChargePayViewModel  : BaseViewModel<ChargePayRepository>() {

    var mUserAuthInfoData: MutableLiveData<BaseResponse<UserInfoAuthResponse>> = MutableLiveData()

    fun getUserAuthInfoData() {
        initiateRequest({
            mUserAuthInfoData.value = mRepository.getUserAuthInfo()
        }, loadState)
    }


    fun cancelPayItem(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.cancelPayItem(id);
        }, loadState)
        return mLiveData
    }

    fun getPayUrl(parameters:HashMap<String,Any>) : MutableLiveData<BaseResponse<PayUrlItem>> {
        val mLiveData: MutableLiveData<BaseResponse<PayUrlItem>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.getPayUrl(parameters);
        }, dialogLoadState)
        return mLiveData
    }

    fun getPayStatus(id: Long) : MutableLiveData<BaseResponse<PayOrderStatus>> {
        val mLiveData: MutableLiveData<BaseResponse<PayOrderStatus>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.getPayStatus(id);
        }, dialogLoadState)
        return mLiveData
    }
}