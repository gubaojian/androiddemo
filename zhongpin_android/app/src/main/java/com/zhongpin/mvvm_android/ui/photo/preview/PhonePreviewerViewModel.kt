package com.zhongpin.mvvm_android.ui.photo.preview

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class PhonePreviewerViewModel  : BaseViewModel<PhonePreviewerRepository>() {


    fun sendVerifyCode(mobile:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.sendVerifyCode(mobile);
        }, loadState)
        return mLoginData
    }

}