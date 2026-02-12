package com.zhongpin.mvvm_android.ui.setpwd

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert

class FindSetPwdRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun loadBannerCo(): List<UserInfoResponse>? {
        return apiService.getUserInfoCo().dataConvert(loadState)
    }


    suspend fun setPassword(mobile:String, password:String, code:String): BaseResponse<Boolean> {
        return apiService.resetPassword(hashMapOf(
            "mobile" to mobile,
            "password" to password,
            "code" to code
        ))
    }


}