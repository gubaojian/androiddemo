package com.zhongpin.mvvm_android.ui.test

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.UserInfoResponse
import com.zhongpin.mvvm_android.network.dataConvert

class SettingRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun loadBannerCo(): List<UserInfoResponse>? {
        return apiService.getUserInfoCo().dataConvert(loadState)
    }


}