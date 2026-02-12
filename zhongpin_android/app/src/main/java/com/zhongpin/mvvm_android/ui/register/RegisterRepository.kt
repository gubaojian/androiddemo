package com.zhongpin.mvvm_android.ui.register

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.LoginResponse
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert

class RegisterRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun loadBannerCo(): List<UserInfoResponse>? {
        return apiService.getUserInfoCo().dataConvert(loadState)
    }

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
            "bizType" to 0
        ))
    }

    suspend fun register(mobile:String, password:String, code:String, nickName:String, companyName:String): BaseResponse<Boolean> {
        return apiService.registerCo(hashMapOf(
            "mobile" to mobile,
            "code" to code,
            "nickName" to nickName,
            "password" to password,
            "headImage" to "",
            "companyName" to companyName
        ))
    }


}