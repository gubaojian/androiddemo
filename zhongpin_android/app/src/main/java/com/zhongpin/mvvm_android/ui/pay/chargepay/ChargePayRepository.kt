package com.zhongpin.mvvm_android.ui.pay.chargepay

import androidx.lifecycle.MutableLiveData
import com.zhongpin.mvvm_android.base.repository.BaseRepository
import com.zhongpin.mvvm_android.base.viewstate.State
import com.zhongpin.mvvm_android.bean.PayItem
import com.zhongpin.mvvm_android.bean.PayOrderStatus
import com.zhongpin.mvvm_android.bean.PayUrlItem
import com.zhongpin.mvvm_android.bean.UserInfoAuthResponse
import com.zhongpin.mvvm_android.network.BaseResponse
import com.zhongpin.mvvm_android.network.requireLogin
import com.zhongpin.mvvm_android.network.showLoadingState


class ChargePayRepository(private val loadState: MutableLiveData<State>): BaseRepository() {

    suspend fun getUserAuthInfo(): BaseResponse<UserInfoAuthResponse> {
        return apiService.getUserAuthInfo().requireLogin().showLoadingState(loadState)
    }

    suspend fun cancelPayItem(id: Long): BaseResponse<Boolean> {
        return apiService.cancelPayItem(id)
    }

    suspend fun getPayUrl(parameters:HashMap<String,Any>): BaseResponse<PayUrlItem> {
        return apiService.getPayUrl(parameters).requireLogin()
    }


    suspend fun getPayStatus(id: Long): BaseResponse<PayOrderStatus> {
        return apiService.getPayStatus(id)
    }

}