package com.zhongpin.mvvm_android.ui.login

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.LoginResponse
import com.zhongpin.mvvm_android.network.BaseResponse

class LoginRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


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
            "bizType" to 1
        ))
    }


}