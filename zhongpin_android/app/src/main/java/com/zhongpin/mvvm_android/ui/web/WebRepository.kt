package com.zhongpin.mvvm_android.ui.web

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.dataConvert

class WebRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun loadBannerCo(): List<UserInfoResponse>? {
        return apiService.getUserInfoCo().dataConvert(loadState)
    }

    suspend fun sendVerifyCode(mobile:String): BaseResponse<Boolean> {
        return apiService.sendVerifyCo(hashMapOf(
            "mobile" to mobile,
            "bizType" to 2
        ))
    }

}