package com.zhongpin.mvvm_android.ui.photo.preview

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.network.BaseResponse

class PhonePreviewerRepository(private val loadState: MutableLiveData<State>): BaseRepository() {


    suspend fun sendVerifyCode(mobile:String): BaseResponse<Boolean> {
        return apiService.sendVerifyCo(hashMapOf(
            "mobile" to mobile,
            "bizType" to 2
        ))
    }

}