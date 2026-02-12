package com.zhongpin.mvvm_android.ui.me.profile

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.initiateRequest

class UserProfileViewModel  : BaseViewModel<UserProfileRepository>() {

    var mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()
    fun getUserInfo(): MutableLiveData<BaseResponse<UserInfoResponse>> {
        initiateRequest({
            val response = mRepository.getUserInfo();
            if (response.success) {
                UserInfoUtil.userInfo = response.data
            }
            mUserInfoData.value = response;
        }, loadState)
        return  mUserInfoData
    }


    fun deleteEntInfoAuth(id: Long) : MutableLiveData<BaseResponse<Boolean>> {
        val mLiveData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLiveData.value = mRepository.deleteEntInfoAuth(id);
        }, loadState)
        return mLiveData
    }

    fun loginOut(token:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.loginOut(token)
        }, dialogLoadState)
        return mLoginData
    }
}