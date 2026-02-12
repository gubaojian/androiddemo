package com.zhongpin.mvvm_android.ui.main

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson2.JSON
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.mvvm_android.base.vm.BaseViewModel
import com.zhongpin.mvvm_android.bean.AppUpdateInfo
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.bean.LoginResponse
import com.zhongpin.mvvm_android.bean.MenuPermissionItem
import com.zhongpin.mvvm_android.bean.OrderItemInfoChangeEvent
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.biz.utils.UserInfoUtil
import com.zhongpin.mvvm_android.network.initiateRequest

class MainViewModel  : BaseViewModel<MainRepository>() {


    fun login(mobile:String, password:String, code:String) : MutableLiveData<BaseResponse<LoginResponse>> {
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


    fun loginOut(token:String) : MutableLiveData<BaseResponse<Boolean>> {
        val mLoginData: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.loginOut(token)
        }, dialogLoadState)
        return mLoginData
    }


    fun getAppUpdateInfo() : MutableLiveData<BaseResponse<AppUpdateInfo>> {
        val mLoginData: MutableLiveData<BaseResponse<AppUpdateInfo>> = MutableLiveData()
        initiateRequest({
            mLoginData.value = mRepository.getAppUpdateInfo()
        }, dialogLoadState)
        return mLoginData
    }

    var mUserInfoData: MutableLiveData<BaseResponse<UserInfoResponse>> = MutableLiveData()
    fun getUserInfoWhenNoPermission(): MutableLiveData<BaseResponse<UserInfoResponse>> {
        initiateRequest({
            var lastPermissionJson:String? = JSON.toJSONString(UserInfoUtil.userInfo?.menuList ?: emptyList<MenuPermissionItem>())
            val response = mRepository.getUserInfo()
            if (response.success) {
                UserInfoUtil.userInfo = response.data
                val updatePermissionJSON = JSON.toJSONString(UserInfoUtil.userInfo?.menuList ?: emptyList<MenuPermissionItem>())
                if (!TextUtils.equals(updatePermissionJSON, lastPermissionJson)) {
                    EventBusUtils.postEvent(OrderItemInfoChangeEvent(true))
                }
            }
            mUserInfoData.value = response;
        }, dialogLoadState, mUserInfoData)
        return  mUserInfoData
    }

}