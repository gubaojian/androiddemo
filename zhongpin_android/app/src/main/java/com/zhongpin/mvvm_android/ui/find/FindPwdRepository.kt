package com.zhongpin.mvvm_android.ui.find

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert

class FindPwdRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun sendVerifyCode(mobile:String): BaseResponse<Boolean> {
        return apiService.sendVerifyCo(hashMapOf(
            "mobile" to mobile,
            "bizType" to 2
        ))
    }

    suspend fun setPassword(mobile:String, password:String, code:String): BaseResponse<Boolean> {
        return apiService.resetPassword(hashMapOf(
            "mobile" to mobile,
            "password" to password,
            "code" to code
        ))
    }

}