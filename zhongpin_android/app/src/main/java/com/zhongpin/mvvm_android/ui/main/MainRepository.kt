package com.zhongpin.mvvm_android.ui.main

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.AppUpdateInfo
import com.zhongpin.mvvm_android.bean.LoginResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin

class MainRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun login(mobile:String, password:String, code:String): BaseResponse<LoginResponse> {
        return apiService.loginCo(hashMapOf(
            "mobile" to mobile,
            "password" to password,
            "code" to code,
            "type" to 2
        ))
    }

    suspend fun sendVerifyCode(mobile:String): BaseResponse<Boolean> {
        return apiService.sendVerifyCo(hashMapOf(
            "mobile" to mobile,
            "bizType" to 3
        ))
    }

    suspend fun loginOut(token:String): BaseResponse<Boolean> {
        return apiService.loginOut(token)
    }


    suspend fun getAppUpdateInfo(): BaseResponse<AppUpdateInfo> {
        return apiService.getAppUpdateInfo("0")
    }

    suspend fun getUserInfo(): BaseResponse<UserInfoResponse> {
        return apiService.getUserInfo().requireLogin()
    }


}